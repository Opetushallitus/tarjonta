package fi.vm.sade.tarjonta.service.search;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.dao.impl.HakukohdeDAOImpl;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.business.IndexService;
import fi.vm.sade.tarjonta.service.business.impl.IndexServiceImpl;
import fi.vm.sade.tarjonta.service.search.resolver.OppilaitostyyppiResolver;
import fi.vm.sade.tarjonta.shared.KoodiService;
import fi.vm.sade.tarjonta.shared.KoodistoProactiveCaching;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.*;

public class IndexerResourceTest {

    private IndexerResource indexer;
    private SolrServer hakukohteetServer;

    @Before
    public void setup() {
        hakukohteetServer = Mockito.mock(SolrServer.class);
        SolrServerFactory factory = Mockito.mock(SolrServerFactory.class);
        stub(factory.getSolrServer("hakukohteet"))
                .toReturn(hakukohteetServer);
        indexer = new IndexerResource();
        indexer.setSolrServerFactory(factory);
        HakukohdeToSolrDocument hakukohdeToSolr = new HakukohdeToSolrDocument();

        OrganisaatioService organisaatioService = Mockito.mock(OrganisaatioService.class);
        stub(organisaatioService.findByUsingOrganisaatioCache(anySet())).toReturn(Lists.newArrayList(getOrg("o-oid-12345")));
        stub(organisaatioService.findByUsingOrganisaatioCache(anySet())).toReturn(Lists.newArrayList(getOrg("o-oid-12345")));
        ReflectionTestUtils.setField(hakukohdeToSolr, "organisaatioService", organisaatioService);
        ReflectionTestUtils.setField(indexer, "organisaatioService", organisaatioService);

        OppilaitostyyppiResolver oppilaitostyyppiResolver = Mockito.mock(OppilaitostyyppiResolver.class);
        stub(oppilaitostyyppiResolver.resolve(any(OrganisaatioPerustieto.class))).toReturn("oppilaitostyyppi_41");
        ReflectionTestUtils.setField(hakukohdeToSolr, "oppilaitostyyppiResolver", oppilaitostyyppiResolver);

        TarjontaKoodistoHelper tarjontaKoodistoHelper = new TarjontaKoodistoHelper();
        Whitebox.setInternalState(tarjontaKoodistoHelper, "koodiService", mock(KoodiService.class));
        Whitebox.setInternalState(tarjontaKoodistoHelper, "koodistoProactiveCaching", mock(KoodistoProactiveCaching.class));
        Whitebox.setInternalState(hakukohdeToSolr, "koodistoHelper", tarjontaKoodistoHelper);

        IndexerDAO indexerDao = Mockito.mock(IndexerDAO.class);
        ReflectionTestUtils.setField(indexer, "indexerDao", indexerDao);

        HakukohdeDAOImpl hakukohdeDAO = Mockito.mock(HakukohdeDAOImpl.class);
        Whitebox.setInternalState(hakukohdeToSolr, "hakukohdeDAO", hakukohdeDAO);
        Mockito.stub(hakukohdeDAO.findBy("id", 12345L)).toReturn(Arrays.asList(getHakukohde(12345L)));
        Mockito.stub(hakukohdeDAO.findBy("id", 1234555L)).toReturn(Arrays.asList(getHakukohde(1234555L)));
        Mockito.stub(hakukohdeDAO.findBy("id", 1234556L)).toReturn(Arrays.asList(getHakukohde(1234556L)));

        IndexService indexService = new IndexServiceImpl(null, null, hakukohdeToSolr, indexerDao, factory);
        ReflectionTestUtils.setField(indexer, "indexService", indexService);
    }

    private OrganisaatioPerustieto getOrg(String oid) {
        OrganisaatioPerustieto organisaatio = new OrganisaatioPerustieto();
        organisaatio.setOid(oid);
        organisaatio.setParentOidPath("010101");
        organisaatio.setNimi("fi", "nimi for " + oid);
        organisaatio.setOppilaitostyyppi("oppilaitostyyppi");
        organisaatio.setKotipaikkaUri("kotipaikka");
        return organisaatio;
    }

    @Test
    public void indexing() throws SolrServerException, IOException {
        List<Long> hakukohteet = Lists.newArrayList();
        hakukohteet.add(getHakukohde(1234555L).getId());
        hakukohteet.add(getHakukohde(1234556L).getId());
        indexer.indexHakukohteet(hakukohteet);
        verify(hakukohteetServer, times(2)).add(any(SolrInputDocument.class));
        verify(hakukohteetServer, times(1)).commit(true, true, false);
    }

    private Hakukohde getHakukohde(Long withId) {

        Haku haku = new Haku();
        haku.setOid("oid");
        haku.setKohdejoukkoUri("kohdejoukko");
        haku.setHakutapaUri("hakutapa");

        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setHakukohdeNimi("xxx");
        hakukohde.setId(withId);
        hakukohde.setHaku(haku);

        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setTarjoaja("o-oid-12345");
        komoto.setToteutustyyppi(ToteutustyyppiEnum.LUKIOKOULUTUS);

        Koulutusmoduuli koulutusmoduuli = new Koulutusmoduuli();
        koulutusmoduuli.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);
        komoto.setKoulutusmoduuli(koulutusmoduuli);

        hakukohde.setKoulutusmoduuliToteutuses(Sets.newHashSet(komoto));
        return hakukohde;
    }

}
