package fi.vm.sade.tarjonta.service.search;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import fi.vm.sade.tarjonta.matchers.KoodistoCriteriaMatcher;
import fi.vm.sade.tarjonta.helpers.KoodistoHelper;
import org.apache.commons.lang.time.DateUtils;
import org.apache.solr.common.SolrInputDocument;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KoulutusToSolrDocumentTest {

    private KoodistoHelper koodistoHelper = new KoodistoHelper();

    @Mock
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Mock
    private OrganisaatioSearchService organisaatioSearchService;

    @Mock
    private KoodiService koodiService;

    @InjectMocks
    private KoulutusToSolrDocument converter;

    private KoulutusmoduuliToteutus koulutusmoduuliToteutus;

    @Before
    public void init() {
        koulutusmoduuliToteutus = createKoulutusmoduuliToteutus();
        List<OrganisaatioPerustieto> organisaatioPerustiedot = createOrganisaatioPerustiedot();

        when(koulutusmoduuliToteutusDAO.findBy("id", 1L)).thenReturn(Arrays.asList(new KoulutusmoduuliToteutus[]{koulutusmoduuliToteutus}));
        when(organisaatioSearchService.findByOidSet(new HashSet<String>(Arrays.asList("1.2.3", "4.5.6")))).thenReturn(organisaatioPerustiedot);
    }

    @Test
    public void thatOneDocumentIsConverted() {
        List<SolrInputDocument> docs = converter.apply(koulutusmoduuliToteutus.getId());
        assertTrue(docs.size() == 1);
    }

    @Test
    public void thatEmptyListIsReturnedWhenNoOrganisationFound() {
        when(organisaatioSearchService.findByOidSet(new HashSet<String>(Arrays.asList("1.2.3", "4.5.6")))).thenReturn(new ArrayList<OrganisaatioPerustieto>());
        List<SolrInputDocument> docs = converter.apply(koulutusmoduuliToteutus.getId());
        assertTrue(docs.isEmpty());
    }

    @Test
    public void thatSimpleFieldsAreConverted() {
        SolrInputDocument doc = convert();

        assertEquals("1", doc.getFieldValue(OID));
        assertEquals(ToteutustyyppiEnum.LUKIOKOULUTUS, doc.getFieldValue(TOTEUTUSTYYPPI_ENUM));
        assertEquals("koulutustyyppi_2", doc.getFieldValue(KOULUTUSTYYPPI_URI));
        assertEquals(TarjontaTila.JULKAISTU, doc.getFieldValue(TILA));
        assertEquals("1.2.3.4", doc.getFieldValue(KOULUTUSMODUULI_OID));
        assertEquals("Lukiokoulutus", doc.getFieldValue(KOULUTUSASTETYYPPI_ENUM));
        assertTrue("2", doc.getFieldValues(HAKUKOHDE_OIDS).contains("2"));
    }

    @Test
    public void thatFirstOwnerIsAdded() {
        SolrInputDocument doc = convert();

        assertTrue(doc.getFieldValues(ORG_OID).size() == 2);
        assertEquals("1.2.3", doc.getFieldValues(ORG_OID).iterator().next());
    }

    @Test
    public void thatOrganisationPathsAreConverted() {
        SolrInputDocument doc = convert();

        assertTrue(doc.getFieldValues(ORG_PATH).size() == 4);
        assertTrue(doc.getFieldValues(ORG_PATH).contains("123"));
        assertTrue(doc.getFieldValues(ORG_PATH).contains("456"));
        assertTrue(doc.getFieldValues(ORG_PATH).contains("987"));
        assertTrue(doc.getFieldValues(ORG_PATH).contains("654"));
    }

    @Test
    public void thatLukioDataIsConverted() {
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("lukiolinja"))).thenReturn(koodistoHelper.getKoodiTypes("lukiolinja"));

        SolrInputDocument doc = convert();

        assertEquals("lukiolinja-nimi-FI", doc.getFieldValue(KOULUTUSOHJELMA_FI));
        assertEquals("lukiolinja-nimi-SV", doc.getFieldValue(KOULUTUSOHJELMA_SV));
        assertEquals("lukiolinja-nimi-EN", doc.getFieldValue(KOULUTUSOHJELMA_EN));
        assertEquals("lukiolinja", doc.getFieldValue(KOULUTUSOHJELMA_URI));
    }

    @Test
    public void thatKorkeakouluDataIsConverted() {
        koulutusmoduuliToteutus.setToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS);
        koulutusmoduuliToteutus.getKoulutusmoduuli().setKoulutustyyppiEnum(ModuulityyppiEnum.KORKEAKOULUTUS);

        MonikielinenTeksti nimi = new MonikielinenTeksti();
        nimi.addTekstiKaannos("kieli_fi", "suomi");
        nimi.addTekstiKaannos("kieli_sv", "ruotsi");
        nimi.addTekstiKaannos("kieli_en", "englanti");
        koulutusmoduuliToteutus.setNimi(nimi);

        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_fi"))).thenReturn(koodistoHelper.getKoodiTypes("kieli_fi"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_sv"))).thenReturn(koodistoHelper.getKoodiTypes("kieli_sv"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_en"))).thenReturn(koodistoHelper.getKoodiTypes("kieli_en"));

        SolrInputDocument doc = convert();

        assertTrue(doc.getFieldValues(NIMET).size() == 3);
        assertTrue(doc.getFieldValues(NIMET).contains("suomi"));
        assertTrue(doc.getFieldValues(NIMET).contains("ruotsi"));
        assertTrue(doc.getFieldValues(NIMET).contains("englanti"));

        assertTrue(doc.getFieldValues(NIMIEN_KIELET).size() == 3);
        assertTrue(doc.getFieldValues(NIMIEN_KIELET).contains("kieli_fi"));
        assertTrue(doc.getFieldValues(NIMIEN_KIELET).contains("kieli_sv"));
        assertTrue(doc.getFieldValues(NIMIEN_KIELET).contains("kieli_en"));
    }

    @Test
    public void thatVapaanSivistystyonDataIsConverted() {
        koulutusmoduuliToteutus.setToteutustyyppi(ToteutustyyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS);
        koulutusmoduuliToteutus.getKoulutusmoduuli().setKoulutustyyppiEnum(ModuulityyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS);
        koulutusmoduuliToteutus.getKoulutusmoduuli().setKoulutusohjelmaUri("vapaasivistystyo");

        MonikielinenTeksti nimi = new MonikielinenTeksti();
        nimi.addTekstiKaannos("kieli_fi", "vapaan sivistystyön koulutus");
        koulutusmoduuliToteutus.setNimi(nimi);

        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("vapaasivistystyo"))).thenReturn(koodistoHelper.getKoodiTypes("vapaasivistystyo"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_fi"))).thenReturn(koodistoHelper.getKoodiTypes("kieli_fi"));

        SolrInputDocument doc = convert();

        assertEquals("vapaasivistystyo-nimi-FI", doc.getFieldValue(KOULUTUSOHJELMA_FI));
        assertEquals("vapaasivistystyo-nimi-SV", doc.getFieldValue(KOULUTUSOHJELMA_SV));
        assertEquals("vapaasivistystyo-nimi-EN", doc.getFieldValue(KOULUTUSOHJELMA_EN));
        assertEquals("vapaasivistystyo", doc.getFieldValue(KOULUTUSOHJELMA_URI));

        assertTrue(doc.getFieldValues(NIMET).size() == 1);
        assertTrue(doc.getFieldValues(NIMET).contains("vapaan sivistystyön koulutus"));

        assertTrue(doc.getFieldValues(NIMIEN_KIELET).size() == 1);
        assertTrue(doc.getFieldValues(NIMIEN_KIELET).contains("kieli_fi"));
    }

    @Test
    public void thatValmentavaJaKuntouttavaOpetusJaOhjausDataIsConverted() {
        koulutusmoduuliToteutus.setToteutustyyppi(ToteutustyyppiEnum.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS);
        koulutusmoduuliToteutus.getKoulutusmoduuli().setKoulutustyyppiEnum(ModuulityyppiEnum.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS);
        koulutusmoduuliToteutus.getKoulutusmoduuli().setKoulutusohjelmaUri("valmentavakuntouttava");

        MonikielinenTeksti nimi = new MonikielinenTeksti();
        nimi.addTekstiKaannos("kieli_fi", "valmentavan ja kuntouttavan opetuksen koulutus");
        koulutusmoduuliToteutus.setNimi(nimi);

        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("valmentavakuntouttava"))).thenReturn(koodistoHelper.getKoodiTypes("valmentavakuntouttava"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_fi"))).thenReturn(koodistoHelper.getKoodiTypes("kieli_fi"));

        SolrInputDocument doc = convert();

        assertEquals("valmentavakuntouttava-nimi-FI", doc.getFieldValue(KOULUTUSOHJELMA_FI));
        assertEquals("valmentavakuntouttava-nimi-SV", doc.getFieldValue(KOULUTUSOHJELMA_SV));
        assertEquals("valmentavakuntouttava-nimi-EN", doc.getFieldValue(KOULUTUSOHJELMA_EN));
        assertEquals("valmentavakuntouttava", doc.getFieldValue(KOULUTUSOHJELMA_URI));

        assertTrue(doc.getFieldValues(NIMET).size() == 1);
        assertTrue(doc.getFieldValues(NIMET).contains("valmentavan ja kuntouttavan opetuksen koulutus"));

        assertTrue(doc.getFieldValues(NIMIEN_KIELET).size() == 1);
        assertTrue(doc.getFieldValues(NIMIEN_KIELET).contains("kieli_fi"));
    }

    @Test
    public void thatAmmatillinenPerustutkintoDataIsConverted() {
        koulutusmoduuliToteutus.setToteutustyyppi(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        koulutusmoduuliToteutus.getKoulutusmoduuli().setKoulutustyyppiEnum(ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
        koulutusmoduuliToteutus.getKoulutusmoduuli().setKoulutusohjelmaUri("ammatillinenperustutkinto");

        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("ammatillinenperustutkinto"))).thenReturn(koodistoHelper.getKoodiTypes("ammatillinenperustutkinto"));

        SolrInputDocument doc = convert();

        assertEquals("ammatillinenperustutkinto-nimi-FI", doc.getFieldValue(KOULUTUSOHJELMA_FI));
        assertEquals("ammatillinenperustutkinto-nimi-SV", doc.getFieldValue(KOULUTUSOHJELMA_SV));
        assertEquals("ammatillinenperustutkinto-nimi-EN", doc.getFieldValue(KOULUTUSOHJELMA_EN));
        assertEquals("ammatillinenperustutkinto", doc.getFieldValue(KOULUTUSOHJELMA_URI));
    }

    @Test
    public void thatOsaamisalaUriIsConverted() {
        koulutusmoduuliToteutus.setToteutustyyppi(ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO);
        koulutusmoduuliToteutus.getKoulutusmoduuli().setKoulutustyyppiEnum(ModuulityyppiEnum.ERIKOISAMMATTITUTKINTO);
        koulutusmoduuliToteutus.setOsaamisalaUri("kotitalousopetus");

        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kotitalousopetus"))).thenReturn(koodistoHelper.getKoodiTypes("kotitalousopetus"));

        SolrInputDocument doc = convert();

        assertEquals("kotitalousopetus-nimi-FI", doc.getFieldValue(KOULUTUSOHJELMA_FI));
        assertEquals("kotitalousopetus-nimi-SV", doc.getFieldValue(KOULUTUSOHJELMA_SV));
        assertEquals("kotitalousopetus-nimi-EN", doc.getFieldValue(KOULUTUSOHJELMA_EN));
        assertEquals("kotitalousopetus", doc.getFieldValue(KOULUTUSOHJELMA_URI));
    }

    @Test
    public void thatKoulutusohjelmaUriIsConverted() {
        koulutusmoduuliToteutus.setToteutustyyppi(ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO);
        koulutusmoduuliToteutus.getKoulutusmoduuli().setKoulutustyyppiEnum(ModuulityyppiEnum.ERIKOISAMMATTITUTKINTO);
        koulutusmoduuliToteutus.getKoulutusmoduuli().setKoulutusohjelmaUri("köksä");
        koulutusmoduuliToteutus.setOsaamisalaUri(null);

        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("köksä"))).thenReturn(koodistoHelper.getKoodiTypes("köksä"));

        SolrInputDocument doc = convert();

        assertEquals("köksä-nimi-FI", doc.getFieldValue(KOULUTUSOHJELMA_FI));
        assertEquals("köksä-nimi-SV", doc.getFieldValue(KOULUTUSOHJELMA_SV));
        assertEquals("köksä-nimi-EN", doc.getFieldValue(KOULUTUSOHJELMA_EN));
        assertEquals("köksä", doc.getFieldValue(KOULUTUSOHJELMA_URI));
    }

    @Test
    public void thatKoulutuskoodiIsConverted() {
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("lukio"))).thenReturn(koodistoHelper.getKoodiTypes("lukio"));

        SolrInputDocument doc = convert();

        assertEquals("lukio-nimi-FI", doc.getFieldValue(KOULUTUSKOODI_FI));
        assertEquals("lukio-nimi-SV", doc.getFieldValue(KOULUTUSKOODI_SV));
        assertEquals("lukio-nimi-EN", doc.getFieldValue(KOULUTUSKOODI_EN));
        assertEquals("lukio", doc.getFieldValue(KOULUTUSKOODI_URI));
    }

    @Test
    public void thatVuosikoodiIsConvertedWhenAlkamisPvmIsSet() {
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kausi_k"))).thenReturn(koodistoHelper.getKoodiTypes("kevat"));

        SolrInputDocument doc = convert();

        assertEquals("kevat-nimi-FI", doc.getFieldValue(KAUSI_FI));
        assertEquals("kevat-nimi-SV", doc.getFieldValue(KAUSI_SV));
        assertEquals("kevat-nimi-EN", doc.getFieldValue(KAUSI_EN));
        assertEquals("kevat#0", doc.getFieldValue(KAUSI_URI));
        assertEquals("2014", doc.getFieldValue(VUOSI_KOODI));
    }

    @Test
    public void thatVuosikoodiIsConvertedWhenAlkamisPvmIsNotSet() {
        koulutusmoduuliToteutus.getKoulutuksenAlkamisPvms().clear();
        koulutusmoduuliToteutus.setAlkamiskausiUri("syksyuri");
        koulutusmoduuliToteutus.setAlkamisVuosi(2014);

        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("syksyuri"))).thenReturn(koodistoHelper.getKoodiTypes("syksy"));

        SolrInputDocument doc = convert();

        assertEquals("syksy-nimi-FI", doc.getFieldValue(KAUSI_FI));
        assertEquals("syksy-nimi-SV", doc.getFieldValue(KAUSI_SV));
        assertEquals("syksy-nimi-EN", doc.getFieldValue(KAUSI_EN));
        assertEquals("syksy#0", doc.getFieldValue(KAUSI_URI));
        assertEquals(2014, doc.getFieldValue(VUOSI_KOODI));
    }

    @Test
    public void thatPohjakoulutusvaatimusIsConverted() {
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("peruskoulu"))).thenReturn(koodistoHelper.getKoodiTypes("peruskoulu"));

        SolrInputDocument doc = convert();

        assertEquals("peruskoulu-lyhyt-nimi-FI", doc.getFieldValue(POHJAKOULUTUSVAATIMUS_FI));
        assertEquals("peruskoulu-lyhyt-nimi-SV", doc.getFieldValue(POHJAKOULUTUSVAATIMUS_SV));
        assertEquals("peruskoulu-lyhyt-nimi-EN", doc.getFieldValue(POHJAKOULUTUSVAATIMUS_EN));
        assertEquals("peruskoulu#0", doc.getFieldValue(POHJAKOULUTUSVAATIMUS_URI));
    }

    @Test
    public void thatKoulutuslajisAreConverted() {
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("koulutuslaji_n"))).thenReturn(koodistoHelper.getKoodiTypes("koulutuslaji_n"));

        SolrInputDocument doc = convert();

        assertEquals("koulutuslaji_n-nimi-FI", doc.getFieldValue(KOULUTUSLAJI_FI));
        assertEquals("koulutuslaji_n-nimi-SV", doc.getFieldValue(KOULUTUSLAJI_SV));
        assertEquals("koulutuslaji_n-nimi-EN", doc.getFieldValue(KOULUTUSLAJI_EN));
        assertTrue(doc.getFieldValues(KOULUTUSLAJI_URIS).contains("koulutuslaji_n"));
    }

    @Test
    public void thatMinMaxAlkamisPvmAreConverted() {
        DateTime minDateTime = new DateTime().withYear(2014).withMonthOfYear(1).withDayOfMonth(1);
        DateTime maxDateTime = new DateTime().withYear(2014).withMonthOfYear(1).withDayOfMonth(2);

        koulutusmoduuliToteutus.getKoulutuksenAlkamisPvms().clear();
        koulutusmoduuliToteutus.addKoulutuksenAlkamisPvms(minDateTime.toDate());
        koulutusmoduuliToteutus.addKoulutuksenAlkamisPvms(maxDateTime.toDate());

        SolrInputDocument doc = convert();

        assertEquals(DateUtils.truncate(minDateTime.toDate(), Calendar.DATE), doc.getFieldValue(KOULUTUALKAMISPVM_MIN));
        assertEquals(DateUtils.truncate(maxDateTime.toDate(), Calendar.DATE), doc.getFieldValue(KOULUTUALKAMISPVM_MAX));
    }

    @Test
    public void thatOppilaitostyyppiUrisAreConverted() {
        SolrInputDocument doc = convert();

        assertTrue(doc.getFieldValues(OPPILAITOSTYYPPI_URIS).size() == 1);
        assertTrue(doc.getFieldValues(OPPILAITOSTYYPPI_URIS).contains("oppilaitostyyppi"));
    }

    @Test
    public void thatKuntaUrisAreConverted() {
        SolrInputDocument doc = convert();

        assertTrue(doc.getFieldValues(KUNTA_URIS).size() == 1);
        assertTrue(doc.getFieldValues(KUNTA_URIS).contains("kotipaikka"));
    }

    @Test
    public void thatOpetuskieliUrisAreConverted() {
        SolrInputDocument doc = convert();

        assertTrue(doc.getFieldValues(OPETUSKIELI_URIS).size() == 2);
        assertTrue(doc.getFieldValues(OPETUSKIELI_URIS).contains("kieli_fi"));
        assertTrue(doc.getFieldValues(OPETUSKIELI_URIS).contains("kieli_sv"));
    }

    @Test
    public void thatHakutapasAreConverted() {
        SolrInputDocument doc = convert();

        assertTrue(doc.getFieldValues(HAKUTAPA_URIS).size() == 1);
        assertTrue(doc.getFieldValues(HAKUTAPA_URIS).contains("hakutapa"));
    }

    @Test
    public void thatHakutyyppisAreConverted() {
        SolrInputDocument doc = convert();

        assertTrue(doc.getFieldValues(HAKUTYYPPI_URIS).size() == 1);
        assertTrue(doc.getFieldValues(HAKUTYYPPI_URIS).contains("hakutyyppi"));
    }

    @Test
    public void thatKohdejoukkosAreConverted() {
        SolrInputDocument doc = convert();

        assertTrue(doc.getFieldValues(KOHDEJOUKKO_URIS).size() == 1);
        assertTrue(doc.getFieldValues(KOHDEJOUKKO_URIS).contains("kohdejoukko"));
    }

    @Test
    public void thatTeksihakuIsConverted() {
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("lukio"))).thenReturn(koodistoHelper.getKoodiTypes("lukio"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kausi_k"))).thenReturn(koodistoHelper.getKoodiTypes("kevat"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("lukiolinja"))).thenReturn(koodistoHelper.getKoodiTypes("lukiolinja"));

        SolrInputDocument doc = convert();

        Collection<Object> tekstihakuValues = doc.getFieldValues(TEKSTIHAKU);

        assertTrue(tekstihakuValues.contains("lukio-nimi-FI"));
        assertTrue(tekstihakuValues.contains("lukio-nimi-SV"));
        assertTrue(tekstihakuValues.contains("lukio-nimi-EN"));
        assertTrue(tekstihakuValues.contains("kevat#0"));
        assertTrue(tekstihakuValues.contains("2014"));
        assertTrue(tekstihakuValues.contains("lukiolinja-nimi-FI"));
        assertTrue(tekstihakuValues.contains("lukiolinja-nimi-SV"));
        assertTrue(tekstihakuValues.contains("lukiolinja-nimi-EN"));
    }

    @Test
    public void thatMonikielinenNimiIsAddedToTeksihaku() {
        koulutusmoduuliToteutus.setToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS);
        koulutusmoduuliToteutus.getKoulutusmoduuli().setKoulutustyyppiEnum(ModuulityyppiEnum.KORKEAKOULUTUS);
        MonikielinenTeksti nimi = new MonikielinenTeksti();
        nimi.addTekstiKaannos("kieli_fi", "suomi");
        nimi.addTekstiKaannos("kieli_sv", "ruotsi");
        nimi.addTekstiKaannos("kieli_en", "englanti");
        koulutusmoduuliToteutus.setNimi(nimi);

        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_fi"))).thenReturn(koodistoHelper.getKoodiTypes("kieli_fi"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_sv"))).thenReturn(koodistoHelper.getKoodiTypes("kieli_sv"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_en"))).thenReturn(koodistoHelper.getKoodiTypes("kieli_en"));

        SolrInputDocument doc = convert();

        Collection<Object> tekstihakuValues = doc.getFieldValues(TEKSTIHAKU);

        assertTrue(tekstihakuValues.contains("suomi"));
        assertTrue(tekstihakuValues.contains("ruotsi"));
        assertTrue(tekstihakuValues.contains("englanti"));
    }

    private SolrInputDocument convert() {
        return converter.apply(koulutusmoduuliToteutus.getId()).get(0);
    }

    private SearchKoodisCriteriaType createKoodistoCriteriaMatcher(String uri) {
        return Matchers.argThat(new KoodistoCriteriaMatcher(uri));
    }

    private List<OrganisaatioPerustieto> createOrganisaatioPerustiedot() {
        OrganisaatioPerustieto firstOrganisaatioPerustieto = new OrganisaatioPerustieto();
        firstOrganisaatioPerustieto.setOid("1.2.3");
        firstOrganisaatioPerustieto.setNimi("fi", "Organisaatio");
        firstOrganisaatioPerustieto.setKotipaikkaUri("kotipaikka");
        firstOrganisaatioPerustieto.setOppilaitostyyppi("oppilaitostyyppi");
        firstOrganisaatioPerustieto.setParentOidPath("123/456");

        OrganisaatioPerustieto secondOrganisaatioPerustieto = new OrganisaatioPerustieto();
        secondOrganisaatioPerustieto.setOid("4.5.6");
        secondOrganisaatioPerustieto.setNimi("fi", "Organisaatio");
        secondOrganisaatioPerustieto.setKotipaikkaUri("kotipaikka");
        secondOrganisaatioPerustieto.setOppilaitostyyppi("oppilaitostyyppi");
        secondOrganisaatioPerustieto.setParentOidPath("987/654");

        return Arrays.asList(new OrganisaatioPerustieto[]{firstOrganisaatioPerustieto, secondOrganisaatioPerustieto});
    }

    private KoulutusmoduuliToteutus createKoulutusmoduuliToteutus() {
        KoulutusmoduuliToteutus koulutusmoduuliToteutus = new KoulutusmoduuliToteutus();
        koulutusmoduuliToteutus.setId(1L);
        koulutusmoduuliToteutus.setOid("1");
        koulutusmoduuliToteutus.setTarjoaja("1.2.3");
        koulutusmoduuliToteutus.setToteutustyyppi(ToteutustyyppiEnum.LUKIOKOULUTUS);
        koulutusmoduuliToteutus.setLukiolinjaUri("lukiolinja");
        koulutusmoduuliToteutus.setTila(TarjontaTila.JULKAISTU);
        koulutusmoduuliToteutus.setKoulutusUri("lukio");
        koulutusmoduuliToteutus.setPohjakoulutusvaatimusUri("peruskoulu");
        koulutusmoduuliToteutus.addKoulutuslaji("koulutuslaji_n");

        KoodistoUri opetusKieliFi = new KoodistoUri("kieli_fi");
        KoodistoUri opetusKieliSv = new KoodistoUri("kieli_sv");
        koulutusmoduuliToteutus.addOpetuskieli(opetusKieliFi);
        koulutusmoduuliToteutus.addOpetuskieli(opetusKieliSv);

        DateTime alkamisPvm = new DateTime().withYear(2014).withMonthOfYear(1).withDayOfMonth(1);
        koulutusmoduuliToteutus.setKoulutuksenAlkamisPvm(alkamisPvm.toDate());

        KoulutusOwner koulutusOwner = new KoulutusOwner();
        koulutusOwner.setOwnerOid("1.2.3");
        koulutusOwner.setOwnerType(KoulutusOwner.TARJOAJA);
        koulutusmoduuliToteutus.getOwners().add(koulutusOwner);

        koulutusOwner = new KoulutusOwner();
        koulutusOwner.setOwnerOid("4.5.6");
        koulutusOwner.setOwnerType(KoulutusOwner.TARJOAJA);
        koulutusmoduuliToteutus.getOwners().add(koulutusOwner);

        Koulutusmoduuli koulutusmoduuli = new Koulutusmoduuli();
        koulutusmoduuli.setOid("1.2.3.4");
        koulutusmoduuli.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);
        koulutusmoduuli.setKoulutusUri("lukio");

        koulutusmoduuliToteutus.setKoulutusmoduuli(koulutusmoduuli);

        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setOid("2");
        koulutusmoduuliToteutus.addHakukohde(hakukohde);

        Haku haku = new Haku();
        haku.setHakutapaUri("hakutapa");
        haku.setHakutyyppiUri("hakutyyppi");
        haku.setKohdejoukkoUri("kohdejoukko");
        hakukohde.setHaku(haku);

        return koulutusmoduuliToteutus;
    }

}
