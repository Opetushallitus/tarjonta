package fi.vm.sade.tarjonta.service.search;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.index.HakukohdeIndexEntity;
import fi.vm.sade.tarjonta.model.index.KoulutusIndexEntity;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class IndexerResourceTest {

    private IndexerResource indexer;
    private SolrServer hakukohteetServer;
    private KoodiService koodiService;
    private OrganisaatioSearchService organisaatioSearchService;

    @org.junit.Before
    public void setup() {
        hakukohteetServer = Mockito.mock(SolrServer.class);
        SolrServerFactory factory = Mockito.mock(SolrServerFactory.class);
        stub(factory.getSolrServer("hakukohteet"))
                .toReturn(hakukohteetServer);
        indexer = new IndexerResource();
        indexer.setSolrServerFactory(factory);
        HakukohdeIndexEntityToSolrDocument hakukohdeToSolr = new HakukohdeIndexEntityToSolrDocument();
        ReflectionTestUtils.setField(indexer, "hakukohdeConverter", hakukohdeToSolr);

        organisaatioSearchService = Mockito.mock(OrganisaatioSearchService.class);
        stub(organisaatioSearchService.findByOidSet(anySet())).toReturn(Lists.newArrayList(getOrg("o-oid-12345")));
        ReflectionTestUtils.setField(hakukohdeToSolr, "organisaatioSearchService", organisaatioSearchService);

        koodiService = Mockito.mock(KoodiService.class);
        ReflectionTestUtils.setField(hakukohdeToSolr, "koodiService", koodiService);

        IndexerDAO indexerDao = Mockito.mock(IndexerDAO.class);
        ReflectionTestUtils.setField(indexer, "indexerDao", indexerDao);
        ReflectionTestUtils.setField(hakukohdeToSolr, "indexerDao", indexerDao);

        stub(indexerDao.findHakukohdeById(1l)).toReturn(getHakukohdeIndexEntity(1l));
        stub(indexerDao.findKoulutusmoduuliToteutusesByHakukohdeId(1l)).toReturn(getHakukohdeKoulutukset(1l));
    }

    private List<KoulutusIndexEntity> getHakukohdeKoulutukset(long id) {
        List<KoulutusIndexEntity> hakukohteenKoulutukset = Lists.newArrayList();
        KoulutusIndexEntity koulutus = new KoulutusIndexEntity("koulutus-oid", "o-oid-12345", "koulutusaste", "pohjakoulutusvaatimus", ModuulityyppiEnum.KORKEAKOULUTUS, ToteutustyyppiEnum.KORKEAKOULUTUS, "12345678#100");
        hakukohteenKoulutukset.add(koulutus);
        return hakukohteenKoulutukset;
    }

    private HakukohdeIndexEntity getHakukohdeIndexEntity(long id) {
        HakukohdeIndexEntity hie = new HakukohdeIndexEntity(id, "oid", "hakukohdenimi", "hakukausiUri", Integer.valueOf(2013), TarjontaTila.JULKAISTU, "hakutapaUri", Integer.valueOf(5), 2l, "hakuoid", "hakutyyppiUri");
        return hie;
    }

    private OrganisaatioPerustieto getOrg(String oid) {
        OrganisaatioPerustieto organisaatio = new OrganisaatioPerustieto();
        organisaatio.setOid(oid);
        organisaatio.setParentOidPath("010101");
        organisaatio.setNimi("fi", "nimi for " + oid);
        return organisaatio;
    }

    @Test
    public void indexing() throws SolrServerException, IOException {
        List<Long> hakukohteet = Lists.newArrayList();
        hakukohteet.add(getHakukohde().getId());
        indexer.indexHakukohteet(hakukohteet);
        verify(hakukohteetServer, times(2)).commit(true, true, false);
        verify(hakukohteetServer, times(1)).add(any(Collection.class));
        verify(koodiService, times(5)).searchKoodis(any(SearchKoodisCriteriaType.class));
        verify(organisaatioSearchService, times(1)).findByOidSet(anySet());
    }

    private Hakukohde getHakukohde() {

        Haku haku = new Haku();
        haku.setOid("oid");
        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setHakukohdeNimi("xxx");
        hakukohde.setId(1l);
        hakukohde.setHaku(haku);
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setTarjoaja("o-oid-12345");
        hakukohde.setKoulutusmoduuliToteutuses(Sets.newHashSet(komoto));
        return hakukohde;
    }

}
