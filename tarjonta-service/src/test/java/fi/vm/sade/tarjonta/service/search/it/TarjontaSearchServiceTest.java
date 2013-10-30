package fi.vm.sade.tarjonta.service.search.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.SecurityAwareTestBase;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliToteutusDAOImpl;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.resources.KoulutusResource;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.KorkeakouluDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.OrgDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.OidResultDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.SuunniteltuKestoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.UiDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeRDTO;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutuksetKysely;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.OrganisaatioHakukohdeGroup;
import fi.vm.sade.tarjonta.service.search.SolrServerFactory;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("embedded-solr")
public class TarjontaSearchServiceTest extends SecurityAwareTestBase {

    private OrganisaatioPerustieto getOrganisaatio(String orgOid) {
        OrganisaatioPerustieto perus = new OrganisaatioPerustieto();
        perus.setOid(orgOid);
        perus.setParentOidPath(Joiner.on("/").join(ophOid, orgOid));
        perus.setNimi("fi", "org nimi fi for oid:" + orgOid);
        perus.setNimi("sv", "org nimi sv for oid:" + orgOid);
        perus.setNimi("en", "org nimi en for oid:" + orgOid);
        return perus;
    }

    @Autowired
    private TarjontaSearchService tarjontaSearchService;
    @Autowired
    private OrganisaatioSearchService organisaatioSearchService;
    @Autowired
    private OrganisaatioService organisaatioService;
    @Autowired
    private SolrServerFactory solrServerFactory;
    @Autowired
    private TarjontaAdminService adminService;
    @Autowired
    private TarjontaPublicService publicService;
    @Autowired
    private KoodiService koodiService;

    @Autowired
    private HakukohdeResource hakukohdeResource;

    @Autowired
    private KoulutusResource koulutusResource;

    @Autowired
    private KoulutusmoduuliToteutusDAOImpl koulutusmoduuliToteutusDAO;

    @Autowired
    TarjontaFixtures tarjontaFixtures;
    private Hakukohde hakukohde;

    @Before
    @Override
    public void before() {
        try {
            clearIndex(solrServerFactory.getOrganisaatioSolrServer());
            clearIndex(solrServerFactory.getSolrServer("hakukohteet"));
        } catch (SolrServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Mockito.stub(
                organisaatioSearchService.findByOidSet(Sets
                        .newHashSet("1.2.3.4.555"))).toReturn(
                Lists.newArrayList(getOrganisaatio("1.2.3.4.555")));
        Mockito.stub(
                organisaatioSearchService.findByOidSet(Sets
                        .newHashSet("1.2.3.4.556"))).toReturn(
                Lists.newArrayList(getOrganisaatio("1.2.3.4.556")));
        Mockito.stub(
                organisaatioSearchService.findByOidSet(Sets
                        .newHashSet("1.2.3.4.557"))).toReturn(
                Lists.newArrayList(getOrganisaatio("1.2.3.4.557")));
        Mockito.stub(
                organisaatioSearchService.findByOidSet(Sets.newHashSet(
                        "1.2.3.4.555", "1.2.3.4.556", "1.2.3.4.557")))
                .toReturn(
                        Lists.newArrayList(getOrganisaatio("1.2.3.4.555"),
                                getOrganisaatio("1.2.3.4.556"),
                                getOrganisaatio("1.2.3.4.557")));
        Mockito.stub(
                organisaatioSearchService.findByOidSet(Sets.newHashSet(
                        "1.2.3.4.555", "1.2.3.4.556"))).toReturn(
                Lists.newArrayList(getOrganisaatio("1.2.3.4.555"),
                        getOrganisaatio("1.2.3.4.556")));
        Mockito.stub(organisaatioService.findByOid("1.2.3.4.555")).toReturn(
                getOrgDTO("1.2.3.4.555"));

        stubKoodi(koodiService, "kieli_fi", "FI");

        super.before();
    }

    private OrganisaatioDTO getOrgDTO(String string) {
        OrganisaatioDTO organisaatioDTO = new OrganisaatioDTO();
        organisaatioDTO.setOid(string);
        organisaatioDTO.setNimi(new MonikielinenTekstiTyyppi());
        return organisaatioDTO;
    }

    private void clearIndex(SolrServer solrServer) throws SolrServerException,
            IOException {
        solrServer.deleteByQuery("*:*");
        solrServer.commit();
    }

    void createTestDataInTransaction() {

        executeInTransaction(new Runnable() {
            @Override
            public void run() {
                hakukohde = tarjontaFixtures
                        .createPersistedHakukohdeWithKoulutus("1.2.3.4.555");
                LueHakukohdeVastausTyyppi hakukohdeVastaus = publicService
                        .lueHakukohde(new LueHakukohdeKyselyTyyppi(hakukohde
                                .getOid()));
                adminService.paivitaHakukohde(hakukohdeVastaus.getHakukohde());

                hakukohde = tarjontaFixtures
                        .createPersistedHakukohdeWithKoulutus("1.2.3.4.556");
                hakukohdeVastaus = publicService
                        .lueHakukohde(new LueHakukohdeKyselyTyyppi(hakukohde
                                .getOid()));
                adminService.paivitaHakukohde(hakukohdeVastaus.getHakukohde());

                hakukohde = tarjontaFixtures
                        .createPersistedHakukohdeWithKoulutus("1.2.3.4.557");
                hakukohdeVastaus = publicService
                        .lueHakukohde(new LueHakukohdeKyselyTyyppi(hakukohde
                                .getOid()));
                adminService.paivitaHakukohde(hakukohdeVastaus.getHakukohde());
            }

        });
    }

    @Autowired
    PlatformTransactionManager tm;

    @Test
    public void testEtsiKoulutukset() throws SolrServerException {
        createTestDataInTransaction();

        KoulutuksetKysely kysely = new KoulutuksetKysely();
        KoulutuksetVastaus vastaus = tarjontaSearchService
                .haeKoulutukset(kysely);

        assertNotNull(vastaus);

        assertEquals(3, vastaus.getKoulutukset().size());

        kysely.setNimi("foo");
        vastaus = tarjontaSearchService.haeKoulutukset(kysely);

        assertNotNull(vastaus);

        assertEquals(0, vastaus.getKoulutukset().size());

    }

    @Test
    public void testEtsiHakukohteet() throws SolrServerException {
        createTestDataInTransaction();

        HakukohteetKysely kysely = new HakukohteetKysely();
        HakukohteetVastaus vastaus = tarjontaSearchService
                .haeHakukohteet(kysely);

        assertNotNull(vastaus);

        assertEquals(3, vastaus.getHakukohteet().size());

        kysely.setNimi("foo");
        vastaus = tarjontaSearchService.haeHakukohteet(kysely);

        assertNotNull(vastaus);

        assertEquals(0, vastaus.getHakukohteet().size());

    }

    @Test
    public void testEtsiHakukohteetByHakuOid() throws SolrServerException {
        createTestDataInTransaction();

        HakukohteetKysely kysely = new HakukohteetKysely();
        kysely.setHakuOid(hakukohde.getHaku().getOid());
        HakukohteetVastaus vastaus = tarjontaSearchService
                .haeHakukohteet(kysely);

        assertNotNull(vastaus);

        assertEquals(1, vastaus.getHakukohteet().size());
    }

    @Test
    public void testEtsiHakukohteetByTarjoajaOid() throws SolrServerException {
        createTestDataInTransaction();

        HakukohteetKysely kysely = new HakukohteetKysely();
        kysely.getTarjoajaOids().add("1.2.3.4.555");
        HakukohteetVastaus vastaus = tarjontaSearchService
                .haeHakukohteet(kysely);
        assertNotNull(vastaus);
        assertEquals(1, vastaus.getHakukohteet().size());
        assertEquals("1.2.3.4.555", vastaus.getHakukohteet().get(0)
                .getTarjoajaOid());

        kysely.getTarjoajaOids().add("1.2.3.4.556");
        vastaus = tarjontaSearchService.haeHakukohteet(kysely);
        assertNotNull(vastaus);
        assertEquals(2, vastaus.getHakukohteet().size());
        assertEquals("1.2.3.4.555", vastaus.getHakukohteet().get(0)
                .getTarjoajaOid());
        assertEquals("1.2.3.4.556", vastaus.getHakukohteet().get(1)
                .getTarjoajaOid());
    }

    @Test
    public void testEtsiHakukohteitaGrouped() {
        createTestDataInTransaction();

        HakukohteetKysely kysely = new HakukohteetKysely();
        List<OrganisaatioHakukohdeGroup> vastaus = tarjontaSearchService
                .haeHakukohteetGroupedByOrganisaatio(new Locale("fi"), kysely);
        assertEquals(3, vastaus.size());
        for (OrganisaatioHakukohdeGroup group : vastaus) {
            Assert.assertTrue(group.getOrganisaatioNimi().contains("fi"));
        }

        vastaus = tarjontaSearchService.haeHakukohteetGroupedByOrganisaatio(
                new Locale("sv"), kysely);
        assertEquals(3, vastaus.size());
        for (OrganisaatioHakukohdeGroup group : vastaus) {
            Assert.assertTrue(group.getOrganisaatioNimi().contains("sv"));
        }
    }

    @Test
    public void testKKKoulutus() throws SolrServerException {

        // tee kk koulutus
        executeInTransaction(new Runnable() {
            @Override
            public void run() {

                KorkeakouluDTO kk = getKKKoulutus();
                koulutusResource.createToteutus(kk);

                // HakukohdeRDTO hakukohde = getHakukohde();
                // hakukohdeResource.insertHakukohde(hakukohde);
            }

        });

        KoulutuksetKysely kysely = new KoulutuksetKysely();
        kysely.setNimi("otsikko");
        KoulutuksetVastaus vastaus = tarjontaSearchService
                .haeKoulutukset(kysely);
        assertNotNull(vastaus);
        assertEquals(1, vastaus.getKoulutukset().size());
    }

    @Test
    public void testKKHakukohde() throws SolrServerException {
        createTestDataInTransaction();
        
        // tee kk koulutus ja hakukohde
        executeInTransaction(new Runnable() {
            @Override
            public void run() {

                KorkeakouluDTO kk = getKKKoulutus();
                OidResultDTO result = koulutusResource.createToteutus(kk);

                HakukohdeRDTO hakukohde = getHakukohde(result.getOid());
                hakukohdeResource.insertHakukohde(hakukohde);
            }

        });
        
        HakukohteetKysely kysely = new HakukohteetKysely();
        kysely.setNimi("kkhakukohdenimi");
        HakukohteetVastaus vastaus = tarjontaSearchService
                .haeHakukohteet(kysely);
        assertNotNull(vastaus);
        assertEquals(1, vastaus.getHakukohteet().size());
    }

    private KorkeakouluDTO getKKKoulutus() {
        
        KorkeakouluDTO kk = new KorkeakouluDTO();
        kk.getKoulutusohjelma()
                .getMeta()
                .put("kieli_fi",
                        new UiDTO(null, "kieli_fi", "1", "Otsikko suomeksi"));

        kk.setKoulutusasteTyyppi(KoulutusasteTyyppi.YLIOPISTOKOULUTUS);
        kk.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        kk.setTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.VALMIS);
        kk.setOrganisaatio(new OrgDTO("1.2.3.4.555", null));
        kk.setTutkinto(new UiDTO(null, "tutkinto-uri", "1", null));
        kk.setOpintojenLaajuus(new UiDTO(null, "laajuus-uri", "1", null));
        kk.setKoulutusaste(new UiDTO(null, "koulutusaste-uri", "1", null));
        kk.setKoulutusala(new UiDTO(null, "koulutusala-uri", "1", null));
        kk.setOpintoala(new UiDTO(null, "opintoala-uri", "1", null));
        kk.setTutkintonimike(new UiDTO(null, "tutkintonimike-uri", "1", null));
        kk.setEqf(new UiDTO(null, "EQF-uri", "1", null));
        kk.setKoulutuskoodi(new UiDTO(null, "koulutus-uri", "1", null));
        kk.setOpintojenMaksullisuus(Boolean.FALSE);
        kk.setSuunniteltuKesto(new SuunniteltuKestoDTO(null,
                "suunniteltu-kesto-uri", "1", null));
        return kk;
    }

    private HakukohdeRDTO getHakukohde(String koulutusOid) {
        HakukohdeRDTO hakukohde = new HakukohdeRDTO();
        hakukohde.setHakuOid(TarjontaSearchServiceTest.this.hakukohde.getHaku()
                .getOid());
        TekstiRDTO nimi = new TekstiRDTO();
        nimi.setUri("kieli_fi");
        nimi.setTeksti("kkhakukohdenimi");
        ArrayList<TekstiRDTO> nimet = new ArrayList<TekstiRDTO>();
        nimet.add(nimi);

        hakukohde.setHakukohteenNimet(nimet);
        hakukohde.setTila(TarjontaTila.JULKAISTU.toString());

        ArrayList<String> koulutusOidit = new ArrayList();
        koulutusOidit.add(koulutusOid);
        // oidit
        hakukohde.setHakukohdeKoulutusOids(koulutusOidit);
        OsoiteRDTO osoite = new OsoiteRDTO();
        osoite.setCreated(new Date());
        osoite.setModified(new Date());
        hakukohde.setLiitteidenToimitusOsoite(osoite);
        return hakukohde;
    }

    /**
     * Tee asioita transaktiossa, välttämätöntä koska esim indeksointi on
     * hookattu nyt transaktion onnistumiseen.
     * 
     * @param runnable
     */
    private void executeInTransaction(final Runnable runnable) {

        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.execute(new TransactionCallback<Object>() {

            @Override
            public Object doInTransaction(TransactionStatus status) {
                runnable.run();
                return null;
            }
        });

    }

    private void stubKoodi(KoodiService koodiService, String uri, String arvo) {
        List<KoodiType> vastaus = Lists.newArrayList(getKoodiType(uri, arvo));
        Mockito.stub(
                koodiService.searchKoodis(Matchers
                        .argThat(new KoodistoCriteriaMatcher(uri)))).toReturn(
                vastaus);
    }

    private KoodiType getKoodiType(String uri, String arvo) {
        KoodiType kt = new KoodiType();
        kt.setKoodiArvo(arvo);
        kt.setKoodiUri(uri);
        kt.getMetadata().add(getKoodiMeta(arvo, KieliType.FI));
        kt.getMetadata().add(getKoodiMeta(arvo, KieliType.SV));
        kt.getMetadata().add(getKoodiMeta(arvo, KieliType.EN));
        return kt;
    }

    private KoodiMetadataType getKoodiMeta(String arvo, KieliType kieli) {
        KoodiMetadataType type = new KoodiMetadataType();
        type.setKieli(kieli);
        type.setNimi(arvo + "-nimi-" + kieli.toString());
        return type;
    }

    private static class KoodistoCriteriaMatcher implements
            Matcher<SearchKoodisCriteriaType> {

        private String uri;

        public KoodistoCriteriaMatcher(String uri) {
            this.uri = uri;
        }

        @Override
        public boolean matches(Object arg0) {
            SearchKoodisCriteriaType type = (SearchKoodisCriteriaType) arg0;
            return type != null && type.getKoodiUris().contains(uri);
        }

        @Override
        public void describeTo(Description arg0) {
        }

        @Override
        public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
        }
    };

}
