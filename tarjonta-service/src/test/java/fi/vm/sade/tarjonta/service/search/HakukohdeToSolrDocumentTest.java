package fi.vm.sade.tarjonta.service.search;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.helpers.KoodistoHelper;
import fi.vm.sade.tarjonta.matchers.KoodistoCriteriaMatcher;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.search.resolver.OppilaitostyyppiResolver;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.solr.common.SolrInputDocument;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HakukohdeToSolrDocumentTest {

    private KoodistoHelper koodistoHelper = new KoodistoHelper();

    @Mock
    private OrganisaatioSearchService organisaatioSearchService;

    @Mock
    private KoodiService koodiService;

    @Mock
    private HakukohdeDAO hakukohdeDAO;

    @Mock
    private OppilaitostyyppiResolver oppilaitostyyppiResolver;

    @InjectMocks
    private HakukohdeToSolrDocument converter;

    private Hakukohde hakukohde;

    @Before
    public void init() {
        hakukohde = createHakukohde();
        List<OrganisaatioPerustieto> organisaatioPerustiedot = createOrganisaatioPerustiedot();

        when(hakukohdeDAO.findBy("id", 1L)).thenReturn(Arrays.asList(new Hakukohde[]{hakukohde}));
        when(organisaatioSearchService.findByOidSet(new HashSet<String>(Arrays.asList("1.2.3")))).thenReturn(organisaatioPerustiedot);
        when(oppilaitostyyppiResolver.resolve(organisaatioPerustiedot.get(0))).thenReturn("oppilaitostyyppi_41");
    }

    @Test
    public void thatOneDocumentIsConverted() {
        List<SolrInputDocument> docs = converter.apply(hakukohde.getId());
        assertTrue(docs.size() == 1);
    }

    @Test
    public void thatEmptyListIsReturnedWhenNoOrganisationFound() {
        when(organisaatioSearchService.findByOidSet(new HashSet<String>(Arrays.asList("1.2.3")))).thenReturn(new ArrayList<OrganisaatioPerustieto>());
        List<SolrInputDocument> docs = converter.apply(hakukohde.getId());
        assertTrue(docs.isEmpty());
    }

    @Test
    public void thatSimpleFieldsAreConverted() {
        SolrInputDocument doc = convert();

        assertEquals("1.0.0", doc.getFieldValue(OID));
        assertEquals(2014, doc.getFieldValue(VUOSI_KOODI));
        assertEquals(TarjontaTila.JULKAISTU, doc.getFieldValue(TILA));
        assertEquals("2.0.0", doc.getFieldValue(HAUN_OID));
        assertEquals("hakutyyppi", doc.getFieldValue(HAKUTYYPPI_URI));
        assertEquals("kohdejoukko", doc.getFieldValue(KOHDEJOUKKO_URI));
        assertTrue(doc.getFieldValues(ORGANISAATIORYHMAOID).contains("5.5.5"));
        assertTrue(doc.getFieldValues(ORGANISAATIORYHMAOID).contains("6.6.6"));
        assertTrue(doc.getFieldValues(KOULUTUS_OIDS).contains("4.5.6"));
        assertEquals(100, doc.getFieldValue(ALOITUSPAIKAT));
    }

    @Test
    public void thatKausikooditiedotAreConverted() {
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kausi_k"))).thenReturn(koodistoHelper.getKoodiTypes("kausi_k"));

        SolrInputDocument doc = convert();

        assertEquals("kausi_k-nimi-FI", doc.getFieldValue(KAUSI_FI));
        assertEquals("kausi_k-nimi-SV", doc.getFieldValue(KAUSI_SV));
        assertEquals("kausi_k-nimi-EN", doc.getFieldValue(KAUSI_EN));
        assertEquals("kausi_k#0", doc.getFieldValue(KAUSI_URI));
    }

    @Test
    public void thatHakutapatiedotAreConverted() {
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("hakutapa"))).thenReturn(koodistoHelper.getKoodiTypes("yhteishaku"));

        SolrInputDocument doc = convert();

        assertEquals("yhteishaku-nimi-FI", doc.getFieldValue(HAKUTAPA_FI));
        assertEquals("yhteishaku-nimi-SV", doc.getFieldValue(HAKUTAPA_SV));
        assertEquals("yhteishaku-nimi-EN", doc.getFieldValue(HAKUTAPA_EN));
        assertEquals("hakutapa", doc.getFieldValue(HAKUTAPA_URI));
    }

    @Test
    public void thatAloituspaikkakuvauksetAreConverted() {
        MonikielinenTeksti aloituspaikkakuvaus = new MonikielinenTeksti();
        aloituspaikkakuvaus.addTekstiKaannos("kieli_fi", "suomi");
        aloituspaikkakuvaus.addTekstiKaannos("kieli_sv", "ruotsi");
        aloituspaikkakuvaus.addTekstiKaannos("kieli_en", "englanti");
        hakukohde.setAloituspaikatKuvaus(aloituspaikkakuvaus);

        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_fi"))).thenReturn(koodistoHelper.getKoodiTypes("kieli_fi"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_sv"))).thenReturn(koodistoHelper.getKoodiTypes("kieli_sv"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_en"))).thenReturn(koodistoHelper.getKoodiTypes("kieli_en"));

        SolrInputDocument doc = convert();

        assertTrue(doc.getFieldValues(ALOITUSPAIKAT_KUVAUKSET).contains("suomi"));
        assertTrue(doc.getFieldValues(ALOITUSPAIKAT_KUVAUKSET).contains("ruotsi"));
        assertTrue(doc.getFieldValues(ALOITUSPAIKAT_KUVAUKSET).contains("englanti"));

        assertTrue(doc.getFieldValues(ALOITUSPAIKAT_KIELET).contains("kieli_fi"));
        assertTrue(doc.getFieldValues(ALOITUSPAIKAT_KIELET).contains("kieli_sv"));
        assertTrue(doc.getFieldValues(ALOITUSPAIKAT_KIELET).contains("kieli_en"));

        assertTrue(doc.getFieldValues(TEKSTIHAKU).contains("suomi"));
        assertTrue(doc.getFieldValues(TEKSTIHAKU).contains("ruotsi"));
        assertTrue(doc.getFieldValues(TEKSTIHAKU).contains("englanti"));
    }

    @Test
    public void thatNimiIsConverted() {
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("hakukohteet_000"))).thenReturn(koodistoHelper.getKoodiTypes("lukio"));

        SolrInputDocument doc = convert();

        assertEquals("lukio-nimi-FI", doc.getFieldValue(HAKUKOHTEEN_NIMI_FI));
        assertEquals("lukio-nimi-SV", doc.getFieldValue(HAKUKOHTEEN_NIMI_SV));
        assertEquals("lukio-nimi-EN", doc.getFieldValue(HAKUKOHTEEN_NIMI_EN));
        assertEquals("hakukohteet_000", doc.getFieldValue(HAKUKOHTEEN_NIMI_URI));
    }

    @Test
    public void thatVapaanSivistystyonNimiIsConverted() {
        hakukohde.setHakukohdeNimi("Vapaan sivistystyön nimi");
        hakukohde.getFirstKoulutus().setToteutustyyppi(ToteutustyyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS);
        hakukohde.getFirstKoulutus().getKoulutusmoduuli().setKoulutustyyppiEnum(ModuulityyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS);

        SolrInputDocument doc = convert();

        assertEquals("Vapaan sivistystyön nimi", doc.getFieldValue(HAKUKOHTEEN_NIMI_FI));
        assertEquals("Vapaan sivistystyön nimi", doc.getFieldValue(HAKUKOHTEEN_NIMI_SV));
        assertEquals("Vapaan sivistystyön nimi", doc.getFieldValue(HAKUKOHTEEN_NIMI_EN));
        assertEquals("Vapaan sivistystyön nimi", doc.getFieldValue(HAKUKOHTEEN_NIMI_URI));
    }

    @Test
    public void thatMonikielinenNimiIsConverted() {
        MonikielinenTeksti nimi = new MonikielinenTeksti();
        nimi.addTekstiKaannos("kieli_fi", "suomi");
        nimi.addTekstiKaannos("kieli_sv", "ruotsi");
        nimi.addTekstiKaannos("kieli_en", "englanti");
        hakukohde.setHakukohdeMonikielinenNimi(nimi);
        hakukohde.setHakukohdeNimi(null);

        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_fi"))).thenReturn(koodistoHelper.getKoodiTypes("kieli_fi"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_sv"))).thenReturn(koodistoHelper.getKoodiTypes("kieli_sv"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_en"))).thenReturn(koodistoHelper.getKoodiTypes("kieli_en"));

        SolrInputDocument doc = convert();

        assertTrue(doc.getFieldValues(NIMET).contains("suomi"));
        assertTrue(doc.getFieldValues(NIMET).contains("ruotsi"));
        assertTrue(doc.getFieldValues(NIMET).contains("englanti"));

        assertTrue(doc.getFieldValues(NIMIEN_KIELET).contains("kieli_fi"));
        assertTrue(doc.getFieldValues(NIMIEN_KIELET).contains("kieli_sv"));
        assertTrue(doc.getFieldValues(NIMIEN_KIELET).contains("kieli_en"));

        assertTrue(doc.getFieldValues(TEKSTIHAKU).contains("suomi"));
        assertTrue(doc.getFieldValues(TEKSTIHAKU).contains("ruotsi"));
        assertTrue(doc.getFieldValues(TEKSTIHAKU).contains("englanti"));
    }

    @Test
    public void thatStartAndEndDateAreConverted() {
        DateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        SolrInputDocument doc = convert();

        Date alkamisPvm = new DateTime().withYear(2014).withMonthOfYear(1).toDate();
        Date paattymisPvm = new DateTime().withYear(2014).withMonthOfYear(6).toDate();

        assertEquals(dateFormat.format(alkamisPvm), doc.getFieldValue(HAUN_ALKAMISPVM));
        assertEquals(dateFormat.format(paattymisPvm), doc.getFieldValue(HAUN_PAATTYMISPVM));
    }

    @Test
    public void thatKoulutuslajitAreConverted() {
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("koulutuslaji_n"))).thenReturn(koodistoHelper.getKoodiTypes("nuorten koulutus"));

        SolrInputDocument doc = convert();

        assertEquals("nuorten koulutus-nimi-FI", doc.getFieldValue(KOULUTUSLAJI_FI));
        assertEquals("nuorten koulutus-nimi-SV", doc.getFieldValue(KOULUTUSLAJI_SV));
        assertEquals("nuorten koulutus-nimi-EN", doc.getFieldValue(KOULUTUSLAJI_EN));
        assertTrue(doc.getFieldValues(KOULUTUSLAJI_URIS).contains("koulutuslaji_n"));
    }

    @Test
    public void thatKoulutustyypitIsConverted() {
        SolrInputDocument doc = convert();

        assertEquals(ModuulityyppiEnum.LUKIOKOULUTUS.getKoulutusasteTyyppi().value(), doc.getFieldValue(KOULUTUSASTETYYPPI));
        assertEquals("koulutustyyppi_2", doc.getFieldValue(KOULUTUSTYYPPI_URI));
        assertEquals(ToteutustyyppiEnum.LUKIOKOULUTUS, doc.getFieldValue(TOTEUTUSTYYPPI_ENUM));
        assertEquals(KoulutusmoduuliTyyppi.TUTKINTO, doc.getFieldValue(KOULUTUSMODUULITYYPPI_ENUM));
    }

    @Test
    public void thatPohjakoulutusvaatimuksetAreConverted() {
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("peruskoulu"))).thenReturn(koodistoHelper.getKoodiTypes("peruskoulu"));

        SolrInputDocument doc = convert();

        assertEquals("peruskoulu-lyhyt-nimi-FI", doc.getFieldValue(POHJAKOULUTUSVAATIMUS_FI));
        assertEquals("peruskoulu-lyhyt-nimi-SV", doc.getFieldValue(POHJAKOULUTUSVAATIMUS_SV));
        assertEquals("peruskoulu-lyhyt-nimi-EN", doc.getFieldValue(POHJAKOULUTUSVAATIMUS_EN));
        assertEquals("peruskoulu#0", doc.getFieldValue(POHJAKOULUTUSVAATIMUS_URI));
    }

    @Test
    public void thatOppilaitostyypitAreConverted() {
        SolrInputDocument doc = convert();

        assertTrue(doc.getFieldValues(OPPILAITOSTYYPPI_URIS).size() == 1);
        assertTrue(doc.getFieldValues(OPPILAITOSTYYPPI_URIS).contains("oppilaitostyyppi_41"));
    }

    @Test
    public void thatKunnatAreConverted() {
        SolrInputDocument doc = convert();

        assertTrue(doc.getFieldValues(KUNTA_URIS).contains("kotipaikka"));
    }

    @Test
    public void thatOpetuskieletAreConverted() {
        SolrInputDocument doc = convert();

        assertTrue(doc.getFieldValues(OPETUSKIELI_URIS).contains("kieli_fi"));
        assertTrue(doc.getFieldValues(OPETUSKIELI_URIS).contains("kieli_sv"));
    }

    @Test
    public void thatTeksihakuIsConverted() {
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("hakukohteet_000"))).thenReturn(koodistoHelper.getKoodiTypes("lukio"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kausi_k"))).thenReturn(koodistoHelper.getKoodiTypes("kausi_k"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("hakutapa"))).thenReturn(koodistoHelper.getKoodiTypes("yhteishaku"));

        SolrInputDocument doc = convert();

        Collection<Object> tekstihakuValues = doc.getFieldValues(TEKSTIHAKU);

        assertTrue(tekstihakuValues.contains("lukio-nimi-FI"));
        assertTrue(tekstihakuValues.contains("lukio-nimi-SV"));
        assertTrue(tekstihakuValues.contains("lukio-nimi-EN"));
        assertTrue(tekstihakuValues.contains("kausi_k-nimi-FI"));
        assertTrue(tekstihakuValues.contains("kausi_k-nimi-SV"));
        assertTrue(tekstihakuValues.contains("kausi_k-nimi-EN"));
        assertTrue(tekstihakuValues.contains(2014));
        assertTrue(tekstihakuValues.contains("yhteishaku-nimi-FI"));
        assertTrue(tekstihakuValues.contains("yhteishaku-nimi-SV"));
        assertTrue(tekstihakuValues.contains("yhteishaku-nimi-EN"));
    }

    private SolrInputDocument convert() {
        return converter.apply(hakukohde.getId()).get(0);
    }

    private SearchKoodisCriteriaType createKoodistoCriteriaMatcher(String uri) {
        return Matchers.argThat(new KoodistoCriteriaMatcher(uri));
    }

    private Hakukohde createHakukohde() {
        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setId(1L);
        hakukohde.setOid("1.0.0");
        hakukohde.setHakukohdeNimi("hakukohteet_000");
        hakukohde.setTila(TarjontaTila.JULKAISTU);
        hakukohde.setOrganisaatioRyhmaOids(new String[]{"5.5.5", "6.6.6"});
        hakukohde.setAloituspaikatLkm(100);

        KoulutusmoduuliToteutus koulutusmoduuliToteutus = new KoulutusmoduuliToteutus();
        koulutusmoduuliToteutus.setOid("4.5.6");
        koulutusmoduuliToteutus.setAlkamisVuosi(2014);
        koulutusmoduuliToteutus.setAlkamiskausiUri("kausi_k");
        koulutusmoduuliToteutus.addKoulutuslaji("koulutuslaji_n");
        koulutusmoduuliToteutus.setToteutustyyppi(ToteutustyyppiEnum.LUKIOKOULUTUS);
        koulutusmoduuliToteutus.setPohjakoulutusvaatimusUri("peruskoulu");
        koulutusmoduuliToteutus.addOpetuskieli(new KoodistoUri("kieli_fi"));
        koulutusmoduuliToteutus.addOpetuskieli(new KoodistoUri("kieli_sv"));

        Koulutusmoduuli koulutusmoduuli = new Koulutusmoduuli();
        koulutusmoduuli.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);
        koulutusmoduuli.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        koulutusmoduuliToteutus.setKoulutusmoduuli(koulutusmoduuli);

        KoulutusOwner koulutusOwner = new KoulutusOwner();
        koulutusOwner.setOwnerOid("1.2.3");
        koulutusOwner.setOwnerType(KoulutusOwner.TARJOAJA);
        koulutusmoduuliToteutus.getOwners().add(koulutusOwner);

        hakukohde.addKoulutusmoduuliToteutus(koulutusmoduuliToteutus);

        KoulutusmoduuliToteutusTarjoajatiedot koulutusmoduuliToteutusTarjoajatiedot = new KoulutusmoduuliToteutusTarjoajatiedot();
        koulutusmoduuliToteutusTarjoajatiedot.getTarjoajaOids().add("1.2.3");
        hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().put("4.5.6", koulutusmoduuliToteutusTarjoajatiedot);

        Haku haku = new Haku();
        haku.setOid("2.0.0");
        haku.setHakutapaUri("hakutapa");
        haku.setHakutyyppiUri("hakutyyppi");
        haku.setKohdejoukkoUri("kohdejoukko");

        Hakuaika firstHakuaika = new Hakuaika();
        firstHakuaika.setAlkamisPvm(new DateTime().withYear(2014).withMonthOfYear(1).toDate());
        firstHakuaika.setPaattymisPvm(new DateTime().withYear(2014).withMonthOfYear(3).toDate());
        haku.addHakuaika(firstHakuaika);

        Hakuaika secondHakuaika = new Hakuaika();
        secondHakuaika.setAlkamisPvm(new DateTime().withYear(2014).withMonthOfYear(4).toDate());
        secondHakuaika.setPaattymisPvm(new DateTime().withYear(2014).withMonthOfYear(6).toDate());
        haku.addHakuaika(secondHakuaika);

        hakukohde.setHaku(haku);

        return hakukohde;
    }

    private List<OrganisaatioPerustieto> createOrganisaatioPerustiedot() {
        OrganisaatioPerustieto organisaatioPerustieto = new OrganisaatioPerustieto();
        organisaatioPerustieto.setOid("1.2.3");
        organisaatioPerustieto.setNimi("fi", "Organisaatio");
        organisaatioPerustieto.setKotipaikkaUri("kotipaikka");
        organisaatioPerustieto.setOppilaitostyyppi("oppilaitostyyppi_41");
        organisaatioPerustieto.setParentOidPath("123/456");

        return Arrays.asList(new OrganisaatioPerustieto[]{organisaatioPerustieto});
    }
}
