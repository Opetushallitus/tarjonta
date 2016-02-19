package fi.vm.sade.tarjonta.service.search.it;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.SecurityAwareTestBase;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusDTOConverterToEntityTest;
import fi.vm.sade.tarjonta.service.impl.resources.v1.KoulutusImplicitDataPopulator;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutuksetKysely;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeVastausTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@ActiveProfiles("embedded-solr")
public class TarjontaSearchServiceTest extends SecurityAwareTestBase {

    private OrganisaatioPerustieto getOrganisaatio(String orgOid) {
        OrganisaatioPerustieto perus = new OrganisaatioPerustieto();
        perus.setOid(orgOid);
        perus.setParentOidPath(Joiner.on("/").join(ophOid, orgOid));
        perus.setOppilaitostyyppi("oppilaitostyyppi");
        perus.setKotipaikkaUri("kunta");
        perus.setNimi("fi", "org nimi fi for oid:" + orgOid);
        perus.setNimi("sv", "org nimi sv for oid:" + orgOid);
        perus.setNimi("en", "org nimi en for oid:" + orgOid);
        return perus;
    }

    private Hakukohde hakukohde;

    @Autowired
    private KoulutusImplicitDataPopulator dataPopulator;

    @Before
    @Override
    public void before() {

        try {
            Mockito.stub(oidService.get(TarjontaOidType.KOMO)).toReturn("oid-komo");
            Mockito.stub(oidService.get(TarjontaOidType.KOMOTO)).toReturn("oid-komoto");
            Mockito.stub(oidService.get(TarjontaOidType.HAKUKOHDE)).toReturn("oid-hakukohde");
        } catch (OIDCreationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        KoodistoURI.KOODISTO_KIELI_URI = "kieli";
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
        stubKoodi(koodiService, "tutkinto-uri", "FI");
        stubKoodi(koodiService, "laajuus-uri", "FI");
        stubKoodi(koodiService, "laajuusyksikko-uri", "FI");
        stubKoodi(koodiService, "koulutusaste-uri", "FI");
        stubKoodi(koodiService, "koulutusala-uri", "FI");
        stubKoodi(koodiService, "opintoala-uri", "FI");
        stubKoodi(koodiService, "tutkintonimike-uri", "FI");
        stubKoodi(koodiService, "kausi_k", "FI");
        stubKoodi(koodiService, "EQF-uri", "FI");
        stubKoodi(koodiService, "suunniteltu-kesto-uri", "FI");

        KoulutusDTOConverterToEntityTest.mockKoodiService(koodiService);

        super.before();
    }

    @Override
    @After
    public void after() {
        super.after();
        executeInTransaction(new Runnable() {
            @Override
            public void run() {
                tarjontaFixtures.deleteAll();
            }
        });
    }

    private OrganisaatioDTO getOrgDTO(String string) {
        OrganisaatioDTO organisaatioDTO = new OrganisaatioDTO();
        organisaatioDTO.setOid(string);
        organisaatioDTO.setNimi(new MonikielinenTekstiTyyppi());

        organisaatioDTO.getNimi().getTeksti().add(new MonikielinenTekstiTyyppi.Teksti("organisaation nimi", "FI"));
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
        KoulutuksetVastaus vastaus = koulutusSearchService.haeKoulutukset(kysely);

        assertNotNull(vastaus);

        assertEquals(3, vastaus.getKoulutukset().size());

        kysely.setNimi("foo");
        vastaus = koulutusSearchService.haeKoulutukset(kysely);

        assertNotNull(vastaus);

        assertEquals(0, vastaus.getKoulutukset().size());

    }

    @Test
    public void testEtsiHakukohteet() throws SolrServerException {
        createTestDataInTransaction();

        HakukohteetKysely kysely = new HakukohteetKysely();
        HakukohteetVastaus vastaus = hakukohdeSearchService.haeHakukohteet(kysely);

        assertNotNull(vastaus);

        assertEquals(3, vastaus.getHakukohteet().size());

        kysely.setNimi("foo");
        vastaus = hakukohdeSearchService.haeHakukohteet(kysely);

        assertNotNull(vastaus);

        assertEquals(0, vastaus.getHakukohteet().size());
    }

    @Test
    public void testEtsiHakukohteetByHakuOid() throws SolrServerException {
        createTestDataInTransaction();

        HakukohteetKysely kysely = new HakukohteetKysely();
        kysely.setHakuOid(hakukohde.getHaku().getOid());
        HakukohteetVastaus vastaus = hakukohdeSearchService.haeHakukohteet(kysely);

        assertNotNull(vastaus);

        assertEquals(1, vastaus.getHakukohteet().size());
    }

    @Test
    public void testEtsiHakukohteetByTarjoajaOid() throws SolrServerException {
        createTestDataInTransaction();

        HakukohteetKysely kysely = new HakukohteetKysely();
        kysely.getTarjoajaOids().add("1.2.3.4.555");
        HakukohteetVastaus vastaus = hakukohdeSearchService.haeHakukohteet(kysely);
        assertNotNull(vastaus);
        assertEquals(1, vastaus.getHakukohteet().size());
        assertEquals("1.2.3.4.555", vastaus.getHakukohteet().get(0)
                .getTarjoajaOid());

        kysely.getTarjoajaOids().add("1.2.3.4.556");
        vastaus = hakukohdeSearchService.haeHakukohteet(kysely);
        assertNotNull(vastaus);
        assertEquals(2, vastaus.getHakukohteet().size());
        assertEquals("1.2.3.4.555", vastaus.getHakukohteet().get(0)
                .getTarjoajaOid());
        assertEquals("1.2.3.4.556", vastaus.getHakukohteet().get(1)
                .getTarjoajaOid());
    }

    @Test
    public void testKKKoulutus() throws SolrServerException {
        Mockito.stub(organisaatioSearchService.findByOidSet(Mockito.anySet())).toReturn(Arrays.asList(getOrganisaatio("1.2.3.4.5.6.7.8.9")));

        // tee kk koulutus
        executeInTransaction(new Runnable() {
            @Override
            public void run() {
                KoulutusKorkeakouluV1RDTO kkKoulutus = getKKKoulutus();
                ResultV1RDTO<KoulutusV1RDTO> result = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResource.postKoulutus(kkKoulutus).getEntity();
                assertEquals("errors in koulutus insert", false, result.hasErrors());
            }
        });

        KoulutuksetKysely kysely = new KoulutuksetKysely();
        kysely.setNimi("otsikko");
        kysely.getTotetustyyppi().add(ToteutustyyppiEnum.KORKEAKOULUTUS);
        KoulutuksetVastaus vastaus = koulutusSearchService.haeKoulutukset(kysely);

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
                KoulutusKorkeakouluV1RDTO kk = getKKKoulutus();
                ResultV1RDTO<KoulutusV1RDTO> postKorkeakouluKoulutus = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResource.postKoulutus(kk).getEntity();
                HakukohdeV1RDTO hakukohde = getKKHakukohde(postKorkeakouluKoulutus.getResult().getOid());
                ResultV1RDTO<HakukohdeV1RDTO> response = (ResultV1RDTO<HakukohdeV1RDTO>) hakukohdeResource.postHakukohde(hakukohde).getEntity();
                assertEquals("errors in koulutus insert", false, response.hasErrors());
            }
        });

        HakukohteetKysely kysely = new HakukohteetKysely();
        kysely.getTotetustyyppi().add(ToteutustyyppiEnum.KORKEAKOULUTUS);
        kysely.setNimi("kkhakukohdenimi");

        HakukohteetVastaus vastaus = hakukohdeSearchService.haeHakukohteet(kysely);
        assertNotNull(vastaus);
        assertEquals(1, vastaus.getHakukohteet().size());

        assertEquals(Integer.valueOf(2014), TarjontaSearchServiceTest.this.hakukohde.getHaku().getKoulutuksenAlkamisVuosi());

        assertEquals(Integer.valueOf(2011), vastaus.getHakukohteet().get(0).getKoulutuksenAlkamisvuosi());
        assertEquals("kausi_k#0", vastaus.getHakukohteet().get(0).getKoulutuksenAlkamiskausi().getUri());
        assertEquals("kkhakukohdenimi", vastaus.getHakukohteet().get(0).getNimi("fi"));
    }


    private KoulutusKorkeakouluV1RDTO getKKKoulutus() {

        KoulutusKorkeakouluV1RDTO kk = new KoulutusKorkeakouluV1RDTO();

        kk = (KoulutusKorkeakouluV1RDTO) dataPopulator.defaultValuesForDto(kk);

        kk.getKoulutusohjelma().getTekstis().put("kieli_fi", "Otsikko suomeksi");
        kk.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        kk.setTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.VALMIS);
        kk.setOrganisaatio(new OrganisaatioV1RDTO("1.2.3.4.555", null, null));
        kk.setTutkinto(new KoodiV1RDTO("tutkinto-uri", 1, null));
        kk.setOpintojenLaajuusarvo(new KoodiV1RDTO("laajuus-uri", 1, null));
        kk.setOpintojenLaajuusyksikko(new KoodiV1RDTO("laajuusyksikko-uri", 1, null));
        kk.setKoulutusaste(new KoodiV1RDTO("koulutusaste-uri", 1, null));
        kk.setKoulutusala(new KoodiV1RDTO("koulutusala-uri", 1, null));
        kk.setOpintoala(new KoodiV1RDTO("opintoala-uri", 1, null));

        kk.setEqf(new KoodiV1RDTO("EQF-uri", 1, null));
        kk.setKoulutuskoodi(new KoodiV1RDTO("koulutus-uri", 1, null));
        kk.setOpintojenMaksullisuus(Boolean.FALSE);
        kk.setSuunniteltuKestoTyyppi(new KoodiV1RDTO("suunniteltu-kesto-uri", 1, null));
        kk.getKoulutuksenAlkamisPvms().add(new DateTime(2011, 1, 1, 1, 1).toDate());
        kk.setKoulutuksenAlkamiskausi(new KoodiV1RDTO("uri_kausi", 1, null));
        kk.setSuunniteltuKestoArvo("1");

        Map<String, Integer> tutkintoNimikes = Maps.newHashMap();
        tutkintoNimikes.put("tutkintonimike-uri", 1);
        kk.getTutkintonimikes().setUris(tutkintoNimikes);

        Map<String, Integer> opetuskieli = Maps.newHashMap();
        opetuskieli.put("opetuskieli-uri", 1);
        kk.getOpetuskielis().setUris(opetuskieli);

        Map<String, Integer> opetusaika = Maps.newHashMap();
        opetusaika.put("opetusaika-uri", 1);
        kk.getOpetusAikas().setUris(opetusaika);

        Map<String, Integer> opetuspaikka = Maps.newHashMap();
        opetuspaikka.put("opetuspaikka-uri", 1);
        kk.getOpetusPaikkas().setUris(opetuspaikka);

        Map<String, Integer> opetusmuoto = Maps.newHashMap();
        opetusmuoto.put("opetusmuoto-uri", 1);
        kk.getOpetusmuodos().setUris(opetusmuoto);

        Map<String, Integer> teema = Maps.newHashMap();
        teema.put("teema-uri", 1);
        kk.getAihees().setUris(teema);

        return kk;
    }

    private HakukohdeV1RDTO getKKHakukohde(String koulutusOid) {
        HakukohdeV1RDTO hakukohde = new HakukohdeV1RDTO();
        hakukohde.setHakuOid(TarjontaSearchServiceTest.this.hakukohde.getHaku()
                .getOid());

        // TekstiRDTO nimi = new TekstiRDTO();
        HashMap<String, String> nimet = new HashMap<String, String>();
        nimet.put("kieli_fi", "kkhakukohdenimi");
        // nimi.setUri("kieli_fi");
        // nimi.setTeksti("kkhakukohdenimi");
        // ArrayList<TekstiRDTO> nimet = new ArrayList<TekstiRDTO>();
        // nimet.add(nimi);

        hakukohde.setHakukohteenNimet(nimet);
        hakukohde.setTila(TarjontaTila.VALMIS.toString());

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

    public static void stubKoodi(KoodiService koodiService, String uri, String arvo) {
        List<KoodiType> vastaus = Lists.newArrayList(getKoodiType(uri, arvo));
        Mockito.stub(
                koodiService.searchKoodis(Matchers
                        .argThat(new KoodistoCriteriaMatcher(uri)))).toReturn(
                vastaus);
    }

    public static KoodiType getKoodiType(String uri, String arvo) {
        KoodiType kt = new KoodiType();
        kt.setKoodiArvo(arvo);
        kt.setKoodiUri(uri);
        kt.getMetadata().add(getKoodiMeta(arvo, KieliType.FI));
        kt.getMetadata().add(getKoodiMeta(arvo, KieliType.SV));
        kt.getMetadata().add(getKoodiMeta(arvo, KieliType.EN));
        return kt;
    }

    public static KoodiMetadataType getKoodiMeta(String arvo, KieliType kieli) {
        KoodiMetadataType type = new KoodiMetadataType();
        type.setKieli(kieli);
        type.setNimi(arvo + "-nimi-" + kieli.toString());
        return type;
    }

    public static class KoodistoCriteriaMatcher implements
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
    }

    ;

}
