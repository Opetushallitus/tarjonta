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
import fi.vm.sade.tarjonta.service.search.resolver.OppilaitostyyppiResolver;
import fi.vm.sade.tarjonta.shared.KoodiService;
import fi.vm.sade.tarjonta.shared.KoodistoProactiveCaching;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
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
        ReflectionTestUtils.setField(indexer, "hakukohdeConverter", hakukohdeToSolr);

        OrganisaatioService organisaatioService = Mockito.mock(OrganisaatioService.class);
        stub(organisaatioService.findByOidSet(anySet())).toReturn(Lists.newArrayList(getOrg("o-oid-12345")));
        ReflectionTestUtils.setField(hakukohdeToSolr, "organisaatioService", organisaatioService);

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
        Mockito.stub(hakukohdeDAO.findBy("id", 1L)).toReturn(Arrays.asList(getHakukohde()));
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
        hakukohteet.add(getHakukohde().getId());
        indexer.indexHakukohteet(hakukohteet);
        verify(hakukohteetServer, times(2)).commit(true, true, false);
        verify(hakukohteetServer, times(1)).add(any(Collection.class));
    }

    private Hakukohde getHakukohde() {

        Haku haku = new Haku();
        haku.setOid("oid");
        haku.setKohdejoukkoUri("kohdejoukko");
        haku.setHakutapaUri("hakutapa");

        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setHakukohdeNimi("xxx");
        hakukohde.setId(1l);
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
