package fi.vm.sade.tarjonta.ui.presenter;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.easymock.Capture;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.google.common.collect.Lists;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.service.types.WebLinkkiTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.helper.OidCreationException;
import fi.vm.sade.tarjonta.ui.helper.OidHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.helper.conversion.ConversionUtils;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusKoodistoConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusLukioConverter;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.LukiolinjaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.YhteyshenkiloModel;
import fi.vm.sade.tarjonta.ui.model.org.OrganisationOidNamePair;
import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusKuvailevatTiedotFormView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusKuvailevatTiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusView;

/**
 *
 * @author jani
 */
public class TarjontaLukioPresenterTest extends BaseTarjontaTest {

    private TarjontaLukioPresenter instance;
    private TarjontaAdminService tarjontaAdminServiceMock;
    private TarjontaPublicService tarjontaPublicServiceMock;
    private OrganisaatioSearchService organisaatioSearchServiceMock;
    private KoulutusKoodistoConverter koulutusKoodisto;
    private KoulutusLukioConverter koulutusLukioConverter;
    private EditLukioKoulutusKuvailevatTiedotView kuvailevatTiedotView;
    private EditLukioKoulutusView editLukioKoulutusView;
    private TabSheet.Tab kuvailevatTiedotTab;
    private KoulutusLukioPerustiedotViewModel perustiedot;
    private KoulutusLukioKuvailevatTiedotViewModel kuvailevatTiedot;
    private OrganisaatioDTO orgDto;
    private OrganisaatioPerustieto orgPerus;
    private TarjontaUIHelper tarjontaUiHelperMock;
    private YhteyshenkiloModel yhteyshenkiloModel;
    private TarjontaPresenter tarjontaPresenter;
    private TarjontaKoodistoHelper tarjontaKoodistoHelperMock;
    private TarjontaSearchService tarjontaSearchServiceMock;

    public TarjontaLukioPresenterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws OidCreationException {
        orgDto = new OrganisaatioDTO();
        orgDto.setOid(ORGANISAATIO_OID);
        orgPerus = new OrganisaatioPerustieto();
        orgPerus.setOid(ORGANISAATIO_OID);
        orgPerus.setNimi("fi", ORGANISATION_NAME);
        fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi omtt = new fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi();
        fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi.Teksti teksti = new fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi.Teksti();
        teksti.setKieliKoodi(LANGUAGE_FI);
        teksti.setValue(ORGANISATION_NAME);
        omtt.getTeksti().add(teksti);
        orgDto.setNimi(omtt);

        tarjontaPresenter = new TarjontaPresenter();
        tarjontaPresenter.getNavigationOrganisation().setOrganisationOid(ORGANISAATIO_OID);
        TarjontaRootView tarjontaRootView = new TarjontaRootView();
        Whitebox.setInternalState(tarjontaRootView, "_presenter", tarjontaPresenter);
        tarjontaPresenter.setRootView(tarjontaRootView);

        instance = new TarjontaLukioPresenter();

        koulutusLukioConverter = new KoulutusLukioConverter();
        kuvailevatTiedotView = new EditLukioKoulutusKuvailevatTiedotView("text");
        editLukioKoulutusView = new EditLukioKoulutusView(KOMOTO_OID);
        instance.setKuvailevatTiedotView(kuvailevatTiedotView);

        TabSheet tabSheet = UiBuilder.tabSheet(new VerticalLayout());
        EditLukioKoulutusKuvailevatTiedotView lisatiedotView = new EditLukioKoulutusKuvailevatTiedotView(KOMOTO_OID);
        kuvailevatTiedotTab = tabSheet.addTab(lisatiedotView, "kuvailevattiedot");

        tarjontaAdminServiceMock = createMock(TarjontaAdminService.class);
        organisaatioSearchServiceMock = createMock(OrganisaatioSearchService.class);
        koulutusKoodisto = new KoulutusKoodistoConverter();//createMock(KoulutusKoodistoConverter.class);
        tarjontaUiHelperMock = createMock(TarjontaUIHelper.class);

        tarjontaPublicServiceMock = createMock(TarjontaPublicService.class);
        tarjontaKoodistoHelperMock = createMock(TarjontaKoodistoHelper.class);
        tarjontaSearchServiceMock = createMock(TarjontaSearchService.class);
        OidHelper oidHelper = createMock(OidHelper.class);
        expect(oidHelper.getOid(TarjontaOidType.KOMOTO)).andReturn(KOMOTO_OID);
        replay(oidHelper);
        Whitebox.setInternalState(koulutusLukioConverter, "oidHelper", oidHelper);

        Whitebox.setInternalState(koulutusKoodisto, "tarjontaKoodistoHelper", tarjontaKoodistoHelperMock);
        Whitebox.setInternalState(kuvailevatTiedotView, "formView", new EditLukioKoulutusKuvailevatTiedotFormView());
        Whitebox.setInternalState(editLukioKoulutusView, "kuvailevatTiedot", kuvailevatTiedotTab);
        Whitebox.setInternalState(koulutusLukioConverter, "organisaatioSearchService", organisaatioSearchServiceMock);
        Whitebox.setInternalState(koulutusLukioConverter, "koulutusKoodisto", koulutusKoodisto);
        Whitebox.setInternalState(instance, "editLukioKoulutusView", editLukioKoulutusView);
        Whitebox.setInternalState(instance, "lukioKoulutusConverter", koulutusLukioConverter);
        Whitebox.setInternalState(instance, "tarjontaAdminService", tarjontaAdminServiceMock);
        Whitebox.setInternalState(tarjontaPresenter, "tarjontaPublicService", tarjontaPublicServiceMock);
        Whitebox.setInternalState(instance, "tarjontaPublicService", tarjontaPublicServiceMock);
        Whitebox.setInternalState(instance, "presenter", tarjontaPresenter);
        Whitebox.setInternalState(koulutusKoodisto, "tarjontaUiHelper", tarjontaUiHelperMock);

        tarjontaPresenter.getTarjoaja().setSelectedOrganisation(new OrganisationOidNamePair(ORGANISAATIO_OID, "org name"));
        tarjontaPresenter.setTarjontaSearchService(tarjontaSearchServiceMock);


        /*
         * Perustiedot tab data
         */
        perustiedot = instance.getPerustiedotModel();
        perustiedot.setKomotoOid(KOMOTO_OID);
        perustiedot.setKoulutusmoduuliOid("1.2.3.170");
        perustiedot.setJatkoopintomahdollisuudet(createMonikielinenTeksti(JATKOOPINTOMAHDOLLISUUDET));
        perustiedot.setKoulutuksenAlkamisPvm(DATE);
        perustiedot.setKoulutuksenRakenne(createMonikielinenTeksti(KOULUTUKSEN_RAKENNE));
        perustiedot.setTavoitteet(createMonikielinenTeksti(TUTKINNON_TAVOITTEET));
        perustiedot.setKoulutusala(createKoodiModel(KOULUTUSALA));
        perustiedot.setKoulutusaste(createKoodiModel(KOULUTUSASTE));
        perustiedot.setKoulutuslaji(createKoodiModel(KOULUTUSLAJI));
        perustiedot.setOpetuskieli("kieli");

        Set<String> opetusmuodos = new HashSet<String>();
        opetusmuodos.add("opetumuoto1");
        opetusmuodos.add("opetumuoto2");

        perustiedot.setOpetusmuoto(opetusmuodos);
        perustiedot.setOpintojenLaajuus(createKoodiModel(LAAJUUS_ARVO));
        perustiedot.setOpintojenLaajuusyksikko(createKoodiModel(LAAJUUS_YKSIKKO));
        perustiedot.setOpsuLinkki(WEB_LINK);
        perustiedot.setPohjakoulutusvaatimus(createKoodiModel("pohjakoulutusvaatimus"));
        perustiedot.setSuunniteltuKesto("kesto");
        perustiedot.setSuunniteltuKestoTyyppi("kesto_tyyppi");
        perustiedot.setTutkinto(createKoodiModel(KOULUTUSKOODI));
        perustiedot.setTutkintonimike(createKoodiModel(TUTKINTONIMIKE));

        yhteyshenkiloModel = new YhteyshenkiloModel();
        yhteyshenkiloModel.setYhtHenkEmail("email");
        yhteyshenkiloModel.setYhtHenkKokoNimi("full name");
        yhteyshenkiloModel.setYhtHenkPuhelin("12345678910");
        yhteyshenkiloModel.setYhtHenkTitteli("Mr.");
        yhteyshenkiloModel.setYhtHenkiloOid("12.1232402.8320948.2843");

        perustiedot.setYhteyshenkilo(yhteyshenkiloModel);

        //form logic
        perustiedot.setLukiolinja(createLukiolinja(LUKIOLINJA));
        perustiedot.setKoulutuskoodiModel(createKoulutuskoodiModel(KOULUTUSKOODI));

        /*
         * Kuvailevat tiedot tab data
         */
        kuvailevatTiedot = instance.getKuvailevatTiedotModel();
        kuvailevatTiedot.setDiplomit(createList(1, "diplomi"));
        kuvailevatTiedot.setKieletMuu(createList(2, "kieliMuu"));
        kuvailevatTiedot.setKieliA(createList(3, "kieliA"));
        kuvailevatTiedot.setKieliB1(createList(4, "kieliB1"));
        kuvailevatTiedot.setKieliB2(createList(5, "kieliB2"));
        kuvailevatTiedot.setKieliB3(createList(6, "kieliB3"));

        Map<String, KoulutusLisatietoModel> map = new HashMap<String, KoulutusLisatietoModel>();
        KoulutusLisatietoModel koulutusLisatietoModel = new KoulutusLisatietoModel();
        koulutusLisatietoModel.setKansainvalistyminen("Kansainvalistyminen");
        koulutusLisatietoModel.setYhteistyoMuidenToimijoidenKanssa("YhteistyoMuidenToimijoidenKanssa");
        koulutusLisatietoModel.setSisalto("Sisalto");
        map.put("fi", koulutusLisatietoModel);

        kuvailevatTiedot.setLisatiedot(map);
        kuvailevatTiedot.setTila(TarjontaTila.VALMIS);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of saveKoulutus method, of class TarjontaLukioPresenter.
     */
    @Test
    public void testInsertSaveKoulutus() throws Exception {
        /*
         * initilaize
         */
        perustiedot.setKomotoOid(null); //null == insert new perustiedot

        HaeKoulutusmoduulitVastausTyyppi vastaus = new HaeKoulutusmoduulitVastausTyyppi();
        KoulutusmoduuliTulos koulutusmoduuliTulos = new KoulutusmoduuliTulos();
        KoulutusmoduuliKoosteTyyppi t = new KoulutusmoduuliKoosteTyyppi();
        t.setOid(KOMO_OID);
        koulutusmoduuliTulos.setKoulutusmoduuli(t);
        vastaus.getKoulutusmoduuliTulos().add(koulutusmoduuliTulos);
        LueKoulutusVastausTyyppi lueKoulutusVastaus = new LueKoulutusVastausTyyppi();
        lueKoulutusVastaus.setTarjoaja(ORGANISAATIO_OID);
        lueKoulutusVastaus.setKoulutusmoduuli(t);
        lueKoulutusVastaus.setKoulutusKoodi(createKoodistoKoodiTyyppi(KOULUTUSKOODI));
        lueKoulutusVastaus.setLukiolinjaKoodi(createKoodistoKoodiTyyppi(LUKIOLINJA));
        Capture<LisaaKoulutusTyyppi> localeCapture = new Capture<LisaaKoulutusTyyppi>();

        /*
         * Expect
         */
        expect(tarjontaAdminServiceMock.lisaaKoulutus(capture(localeCapture))).andReturn(new LisaaKoulutusVastausTyyppi());
        expect(organisaatioSearchServiceMock.findByOidSet(anyObject(Set.class))).andReturn(Lists.newArrayList(orgPerus));
        expect(tarjontaPublicServiceMock.haeKoulutusmoduulit(isA(HaeKoulutusmoduulitKyselyTyyppi.class))).andReturn(vastaus);
        expect(tarjontaPublicServiceMock.lueKoulutus(isA(LueKoulutusKyselyTyyppi.class))).andReturn(lueKoulutusVastaus);
        expect(organisaatioSearchServiceMock.findByOidSet(anyObject(Set.class))).andReturn(Lists.newArrayList(orgPerus));

        expectKoodis();
        /*
         * replay
         */
        replay(tarjontaAdminServiceMock);
        //      replay(organisaatioServiceMock);
        replay(tarjontaPublicServiceMock);
        replay(tarjontaUiHelperMock);
        replay(organisaatioSearchServiceMock);

        /*
         * Presenter method call
         */
        instance.saveKoulutus(SaveButtonState.SAVE_AS_DRAFT, KoulutusActiveTab.PERUSTIEDOT);

        /*
         * verify
         */
        verify(tarjontaAdminServiceMock);
        verify(organisaatioSearchServiceMock);
        verify(tarjontaPublicServiceMock);
        LisaaKoulutusTyyppi koulutus = localeCapture.getValue();

        assertNotNull(koulutus);
        assertEquals(KOMOTO_OID, koulutus.getOid());
        assertKuvailevatTiedot(koulutus);

        assertEquals(1, koulutus.getOpetuskieli().size());
        assertEquals(2, koulutus.getOpetusmuoto().size());

        assertNotNull("Koulutuksen kesto tyyppi", koulutus.getKesto());
        assertEquals("kesto", koulutus.getKesto().getArvo());
        assertEquals("kesto_tyyppi", koulutus.getKesto().getYksikko());
        assertEquals(DATE, koulutus.getKoulutuksenAlkamisPaiva());
        assertEquals(createUri(KOULUTUSKOODI), koulutus.getKoulutusKoodi().getUri());
        //       assertEquals(createUri(KOULUTUSASTE), koulutus.getKoulutusaste().getUri());
        assertEquals(createUri(KOULUTUSLAJI), koulutus.getKoulutuslaji().get(0).getUri()); //only one needed
        assertEquals(KoulutusasteTyyppi.LUKIOKOULUTUS, koulutus.getKoulutustyyppi());
        assertEquals(WEB_LINK, koulutus.getLinkki().get(0).getUri());
        assertEquals(createUri(LUKIOLINJA), koulutus.getLukiolinjaKoodi().getUri());

        //assertEquals(createUri("pohjakoulutusvaatimus"), koulutus.getPohjakoulutusvaatimus().getUri());
        assertEquals(ORGANISAATIO_OID, koulutus.getTarjoaja());
        assertEquals(TarjontaTila.LUONNOS, koulutus.getTila());

        assertNotNull(koulutus.getYhteyshenkiloTyyppi());
        YhteyshenkiloTyyppi yhteyshenkilo = koulutus.getYhteyshenkiloTyyppi().get(0);
        assertYhteyshenkilo(yhteyshenkilo);

        assertEquals("Kansainvalistyminen", ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.KANSAINVALISTYMINEN).getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.KANSAINVALISTYMINEN).getTeksti().get(0).getKieliKoodi());

        assertEquals("YhteistyoMuidenToimijoidenKanssa", ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTeksti().get(0).getKieliKoodi());

        assertEquals("Sisalto", ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.SISALTO).getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.SISALTO).getTeksti().get(0).getKieliKoodi());

        assertEquals(null, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.PAINOTUS));
        assertEquals(null, koulutus.getNimi()); ///????
        assertEquals(null, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN));
        assertEquals(null, koulutus.getKoulutusohjelmaKoodi());
        assertEquals(null, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.KOULUTUSOHJELMAN_VALINTA));
        assertEquals(null, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.KUVAILEVAT_TIEDOT));
    }
//

    @Test
    public void testUpdateSaveKoulutus() throws Exception {
        /*
         * initilaize
         */
        HaeKoulutusmoduulitVastausTyyppi vastaus = new HaeKoulutusmoduulitVastausTyyppi();
        KoulutusmoduuliTulos koulutusmoduuliTulos = new KoulutusmoduuliTulos();
        KoulutusmoduuliKoosteTyyppi t = new KoulutusmoduuliKoosteTyyppi();
        t.setOid(KOMO_OID);
        koulutusmoduuliTulos.setKoulutusmoduuli(t);
        vastaus.getKoulutusmoduuliTulos().add(koulutusmoduuliTulos);

        tarjontaPresenter.getModel().getTarjoajaModel().setSelectedOrganisation(new OrganisationOidNamePair(ORGANISAATIO_OID, ORGANISATION_NAME));

        LueKoulutusVastausTyyppi lueKoulutusVastaus = new LueKoulutusVastausTyyppi();
        lueKoulutusVastaus.setTarjoaja(ORGANISAATIO_OID);
        lueKoulutusVastaus.setKoulutusmoduuli(t);
        lueKoulutusVastaus.setKoulutusKoodi(createKoodistoKoodiTyyppi(KOULUTUSKOODI));
        lueKoulutusVastaus.setLukiolinjaKoodi(createKoodistoKoodiTyyppi(LUKIOLINJA));

        Capture<PaivitaKoulutusTyyppi> localeCapture = new Capture<PaivitaKoulutusTyyppi>();

        /*
         * Expect
         */
        expect(tarjontaAdminServiceMock.paivitaKoulutus(capture(localeCapture))).andReturn(new PaivitaKoulutusVastausTyyppi());
        expect(organisaatioSearchServiceMock.findByOidSet(anyObject(Set.class))).andReturn(Lists.newArrayList(orgPerus));
        expect(tarjontaPublicServiceMock.lueKoulutus(isA(LueKoulutusKyselyTyyppi.class))).andReturn(lueKoulutusVastaus);
        expect(organisaatioSearchServiceMock.findByOidSet(anyObject(Set.class))).andReturn(Lists.newArrayList(orgPerus));

        expectKoodis();
        /*
         * replay
         */
        replay(tarjontaAdminServiceMock);
        replay(tarjontaPublicServiceMock);
        replay(organisaatioSearchServiceMock);
        replay(tarjontaUiHelperMock);

        /*
         * Presenter method call
         */
        instance.saveKoulutus(SaveButtonState.SAVE_AS_DRAFT, KoulutusActiveTab.PERUSTIEDOT);

        /*
         * verify
         */
        verify(tarjontaAdminServiceMock);
        verify(organisaatioSearchServiceMock);

        PaivitaKoulutusTyyppi koulutus = localeCapture.getValue();

        assertNotNull(koulutus);
        assertEquals(KOMOTO_OID, koulutus.getOid());
        assertKuvailevatTiedot(koulutus);

        assertEquals(1, koulutus.getOpetuskieli().size());
        assertEquals(2, koulutus.getOpetusmuoto().size());

        assertNotNull("Koulutuksen kesto tyyppi", koulutus.getKesto());
        assertEquals("kesto", koulutus.getKesto().getArvo());
        assertEquals("kesto_tyyppi", koulutus.getKesto().getYksikko());
        assertEquals(DATE, koulutus.getKoulutuksenAlkamisPaiva());
        assertEquals(createUri(KOULUTUSKOODI), koulutus.getKoulutusKoodi().getUri());
        //       assertEquals(createUri(KOULUTUSASTE), koulutus.getKoulutusaste().getUri());
        assertEquals(createUri(KOULUTUSLAJI), koulutus.getKoulutuslaji().get(0).getUri()); //only one needed
        assertEquals(KoulutusasteTyyppi.LUKIOKOULUTUS, koulutus.getKoulutustyyppi());
        assertEquals(WEB_LINK, koulutus.getLinkki().get(0).getUri());
        assertEquals(createUri(LUKIOLINJA), koulutus.getLukiolinjaKoodi().getUri());

        //assertEquals(createUri("pohjakoulutusvaatimus"), koulutus.getPohjakoulutusvaatimus().getUri());
        assertEquals(ORGANISAATIO_OID, koulutus.getTarjoaja());
        assertEquals(TarjontaTila.LUONNOS, koulutus.getTila());

        assertNotNull(koulutus.getYhteyshenkiloTyyppi());
        assertNotNull("getYhteyshenkiloTyyppi not null", koulutus.getYhteyshenkiloTyyppi()); //not needed

        YhteyshenkiloTyyppi yhteyshenkilo = koulutus.getYhteyshenkiloTyyppi().get(0);
        assertYhteyshenkilo(yhteyshenkilo);

        assertEquals("Kansainvalistyminen", ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.KANSAINVALISTYMINEN).getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.KANSAINVALISTYMINEN).getTeksti().get(0).getKieliKoodi());

        assertEquals("YhteistyoMuidenToimijoidenKanssa", ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTeksti().get(0).getKieliKoodi());

        assertEquals("Sisalto", ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.SISALTO).getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.SISALTO).getTeksti().get(0).getKieliKoodi());

        assertEquals(null, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.PAINOTUS));
        assertEquals(null, koulutus.getNimi()); ///????
        assertEquals(null, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN));
        assertEquals(null, koulutus.getKoulutusohjelmaKoodi());
        assertEquals(null, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.KOULUTUSOHJELMAN_VALINTA));
        assertEquals(null, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.KUVAILEVAT_TIEDOT));
    }

    @Test
    public void testLoad() throws Exception {


        /*
         * initilaize
         */
        perustiedot.setKomotoOid(null); //null == insert new perustiedot

        LueKoulutusVastausTyyppi vastaus = new LueKoulutusVastausTyyppi();
        setTeksti(vastaus.getTekstit(), KomotoTeksti.KANSAINVALISTYMINEN, LANGUAGE_FI, "Kansainvalistyminen");
        KoulutuksenKestoTyyppi koulutuksenKestoTyyppi = new KoulutuksenKestoTyyppi();
        koulutuksenKestoTyyppi.setArvo("kesto");
        koulutuksenKestoTyyppi.setYksikko("yksikko");
        vastaus.setKesto(koulutuksenKestoTyyppi);

        vastaus.setKoulutusKoodi(createKoodistoKoodiTyyppi(KOULUTUSKOODI));
        //     vastaus.setKoulutusaste(createKoodistoKoodiTyyppi(KOULUTUSASTE));
        vastaus.setKoulutusmoduuli(new KoulutusmoduuliKoosteTyyppi());

        setTeksti(vastaus.getTekstit(), KomotoTeksti.KOULUTUSOHJELMAN_VALINTA, LANGUAGE_FI, "KoulutusohjelmanValinta");
        vastaus.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        setTeksti(vastaus.getTekstit(), KomotoTeksti.KUVAILEVAT_TIEDOT, LANGUAGE_FI, "KuvailevatTiedot");
        vastaus.setLukiolinjaKoodi(createKoodistoKoodiTyyppi(LUKIOLINJA));
        vastaus.setNimi(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, "nimi"));
        vastaus.setOid(KOMOTO_OID);

        setTeksti(vastaus.getTekstit(), KomotoTeksti.SISALTO, LANGUAGE_FI, "sisalto");
        vastaus.setTarjoaja(ORGANISAATIO_OID);
        vastaus.setTila(TarjontaTila.VALMIS);
        setTeksti(vastaus.getTekstit(), KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA, LANGUAGE_FI, "setYhteistyoMuidenToimijoidenKanssa");
        vastaus.getA1A2Kieli().add(createKoodistoKoodiTyyppi("A1"));
        vastaus.getAmmattinimikkeet().add(createKoodistoKoodiTyyppi("ammattinimikkeet"));
        vastaus.getB1Kieli().add(createKoodistoKoodiTyyppi("b1"));
        vastaus.getB2Kieli().add(createKoodistoKoodiTyyppi("b2"));
        vastaus.getB3Kieli().add(createKoodistoKoodiTyyppi("b3"));
        clearTeksti(vastaus.getTekstit(), KomotoTeksti.PAINOTUS);
        vastaus.setKoulutuksenAlkamisPaiva(null);
        vastaus.setKoulutusohjelmaKoodi(null);
        vastaus.setPohjakoulutusvaatimus(null);
        clearTeksti(vastaus.getTekstit(), KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN);

        WebLinkkiTyyppi web = new WebLinkkiTyyppi();
        web.setUri(WEB_LINK);
        vastaus.getLinkki().add(web);

        vastaus.getOpetuskieli().add(createKoodistoKoodiTyyppi("opetuskieli"));

        YhteyshenkiloTyyppi y = new YhteyshenkiloTyyppi();
        y.setSukunimi("suku");
        y.setEtunimet(yhteyshenkiloModel.getYhtHenkKokoNimi());
        y.setHenkiloOid(yhteyshenkiloModel.getYhtHenkiloOid());
        y.setPuhelin(yhteyshenkiloModel.getYhtHenkPuhelin());
        y.setSahkoposti(yhteyshenkiloModel.getYhtHenkEmail());
        y.setTitteli(yhteyshenkiloModel.getYhtHenkTitteli());
        vastaus.getYhteyshenkiloTyyppi().add(y);

        KoulutusmoduuliKoosteTyyppi t = new KoulutusmoduuliKoosteTyyppi();
        t.setOid(KOMO_OID);
        t.setKoulutusalaUri(createUri(KOULUTUSALA));
        setTeksti(t.getTekstit(), KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET, LANGUAGE_FI, JATKOOPINTOMAHDOLLISUUDET);
        setTeksti(t.getTekstit(), KomoTeksti.KOULUTUKSEN_RAKENNE, LANGUAGE_FI, KOULUTUKSEN_RAKENNE);
        t.setKoulutusasteUri(createUri(KOULUTUSASTE));
        t.setKoulutuskoodiUri(createUri(KOULUTUSKOODI));
        t.setLaajuusarvoUri(createUri(LAAJUUS_ARVO));
        t.setLaajuusyksikkoUri(createUri(LAAJUUS_YKSIKKO));
        t.setLukiolinjakoodiUri(createUri(LUKIOLINJA));
        t.setTutkintonimikeUri(createUri(TUTKINTONIMIKE));
        t.setOpintoalaUri(createUri(OPINTOALA));
        t.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        t.setKoulutusmoduulinNimi(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, "nimi"));
        t.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        t.setTutkinnonTavoitteet(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, TUTKINNON_TAVOITTEET));
        t.setUlkoinenTunniste(null);
        t.setParentOid(null);
        clearTeksti(t.getTekstit(), KomoTeksti.TAVOITTEET);
        t.setKoulutusohjelmakoodiUri(null);

        vastaus.setKoulutusmoduuli(t);
        /*
         * Expect
         */
        expect(organisaatioSearchServiceMock.findByOidSet(anyObject(Set.class))).andReturn(Lists.newArrayList(orgPerus));
        expect(tarjontaPublicServiceMock.lueKoulutus(isA(LueKoulutusKyselyTyyppi.class))).andReturn(vastaus);

        expectKoodis();
//

        //expect(tarjontaUiHelperMock.getKoodis(isA(String.class))).andReturn(createKoodiTypes(TUTKINTONIMIKE));

        /*
         * replay
         */
        replay(tarjontaUiHelperMock);
        replay(organisaatioSearchServiceMock);
        replay(tarjontaPublicServiceMock);
        replay(tarjontaKoodistoHelperMock);

        /*
         * Presenter method call
         */
        instance.showEditKoulutusView(KOMOTO_OID, null);

        /*
         * verify
         */
        verify(tarjontaUiHelperMock);
        verify(organisaatioSearchServiceMock);
        verify(tarjontaPublicServiceMock);
        verify(tarjontaKoodistoHelperMock);

        KoulutusLukioPerustiedotViewModel perustiedotModel = instance.getPerustiedotModel();
        assertEquals(KOMOTO_OID, perustiedotModel.getKomotoOid());
        assertEquals(null, perustiedotModel.getKoulutuksenAlkamisPvm());
        assertEquals(KOULUTUKSEN_RAKENNE, perustiedotModel.getKoulutuksenRakenne().getNimi());

        assertNotNull("koulutuskoodi model", perustiedotModel.getKoulutuskoodiModel());
        assertEquals(createUri(TUTKINTONIMIKE), perustiedotModel.getKoulutuskoodiModel().getTutkintonimike().getKoodistoUriVersio());
        assertEquals(createUri(KOULUTUSALA), perustiedotModel.getKoulutuskoodiModel().getKoulutusala().getKoodistoUriVersio());
        assertEquals(createUri(KOULUTUSASTE), perustiedotModel.getKoulutuskoodiModel().getKoulutusaste().getKoodistoUriVersio());
        assertEquals(createUri(KOULUTUSKOODI), perustiedotModel.getKoulutuskoodiModel().getKoodistoUriVersio());
        assertEquals(createUri(OPINTOALA), perustiedotModel.getKoulutuskoodiModel().getOpintoala().getKoodistoUriVersio());
        // assertEquals(createUri(LAAJUUS_ARVO), perustiedotModel.getKoulutuskoodiModel().getOpintojenLaajuus());
        assertEquals(createUri(LAAJUUS_YKSIKKO), perustiedotModel.getKoulutuskoodiModel().getOpintojenLaajuusyksikko().getKoodistoUriVersio());
        assertEquals(createUri(LUKIOLINJA), perustiedotModel.getLukiolinja().getKoodistoUriVersio());

        //the same data as above, but in perustiedot object
        assertEquals(createUri(TUTKINTONIMIKE), perustiedotModel.getTutkintonimike().getKoodistoUriVersio());
        assertEquals(createUri(KOULUTUSALA), perustiedotModel.getKoulutusala().getKoodistoUriVersio());
        assertEquals(createUri(KOULUTUSASTE), perustiedotModel.getKoulutusaste().getKoodistoUriVersio());
        assertEquals(createUri(KOULUTUSKOODI), perustiedotModel.getKoulutuskoodiModel().getKoodistoUriVersio());
        assertEquals(createUri(OPINTOALA), perustiedotModel.getOpintoala().getKoodistoUriVersio());
        // assertEquals(createUri(LAAJUUS_ARVO), perustiedotModel.getOpintojenLaajuus());
        assertEquals(createUri(LAAJUUS_YKSIKKO), perustiedotModel.getOpintojenLaajuusyksikko().getKoodistoUriVersio());
        assertEquals(createUri(LUKIOLINJA), perustiedotModel.getLukiolinja().getKoodistoUriVersio());
        assertEquals(KOMO_OID, perustiedotModel.getKoulutusmoduuliOid());

        /*
         * values on bottom are loaded to the model only after combox event is fired
         */
        assertEquals(createUri("opetuskieli"), perustiedotModel.getOpetuskieli());
        assertEquals(0, perustiedotModel.getOpetusmuoto().size());
        assertEquals(WEB_LINK, perustiedotModel.getOpsuLinkki());
        assertEquals(ORGANISATION_NAME, tarjontaPresenter.getTarjoaja().getSelectedOrganisation().getOrganisationName());
        assertEquals(ORGANISAATIO_OID, tarjontaPresenter.getTarjoaja().getSelectedOrganisationOid());
        assertEquals("kesto", perustiedotModel.getSuunniteltuKesto());
        assertEquals("yksikko", perustiedotModel.getSuunniteltuKestoTyyppi());
        assertEquals(TUTKINNON_TAVOITTEET, perustiedotModel.getTavoitteet().getNimi());
        assertEquals(TarjontaTila.VALMIS, perustiedotModel.getTila());
        assertYhteyshenkilo(perustiedotModel.getYhteyshenkilo());
        assertEquals(null, perustiedotModel.getTutkinto());
        assertNotNull(null, perustiedotModel.getPohjakoulutusvaatimus());
    }

    protected void assertYhteyshenkilo(YhteyshenkiloModel cperson) {
        assertNotNull("YhteyshenkiloTyyppi", cperson);
        assertEquals(yhteyshenkiloModel.getYhtHenkKokoNimi() + " suku", cperson.getYhtHenkKokoNimi());
        assertEquals(yhteyshenkiloModel.getYhtHenkPuhelin(), cperson.getYhtHenkPuhelin());
        assertEquals(yhteyshenkiloModel.getYhtHenkEmail(), cperson.getYhtHenkEmail());
        assertEquals(yhteyshenkiloModel.getYhtHenkTitteli(), cperson.getYhtHenkTitteli());
        assertEquals(yhteyshenkiloModel.getYhtHenkiloOid(), cperson.getYhtHenkiloOid());
    }

    protected void assertYhteyshenkilo(YhteyshenkiloTyyppi yhteyshenkilo) {
        assertNotNull("YhteyshenkiloTyyppi", yhteyshenkilo);
        assertEquals(yhteyshenkiloModel.getYhtHenkKokoNimi(), yhteyshenkilo.getEtunimet());
        assertEquals(yhteyshenkiloModel.getYhtHenkiloOid(), yhteyshenkilo.getHenkiloOid());
        assertEquals(0, yhteyshenkilo.getKielet().size());
        assertEquals(yhteyshenkiloModel.getYhtHenkPuhelin(), yhteyshenkilo.getPuhelin());
        assertEquals(yhteyshenkiloModel.getYhtHenkEmail(), yhteyshenkilo.getSahkoposti());
        assertEquals(yhteyshenkiloModel.getYhtHenkTitteli(), yhteyshenkilo.getTitteli());
    }

    protected LukiolinjaModel createLukiolinja(String koodiUri) {
        LukiolinjaModel lukiolinja = new LukiolinjaModel();
        createKoodiModel(lukiolinja, koodiUri);
        return lukiolinja;
    }

    private void expectKoodis() {
        KoodistoURI.KOODISTO_KOULUTUSALA_URI = "koodisto_koulutusala_uri";
        KoodistoURI.KOODISTO_OPINTOALA_URI = "koodisto_opintoala_uri";
        KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI = "koodisto_tutkintonimike_uri";
        KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI = "koodisto_laajuus_tyyppi_uri";
        KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSARVO_URI = "koodisto_laajuus_arvo_uri";
        KoodistoURI.KOODISTO_KOULUTUSASTE_URI = "koodisto_koulutusaste_uri";

        expect(tarjontaUiHelperMock.getKoulutusRelations(and(isA(String.class), eq("koulutuskoodi_uri")))).andReturn(
                createKoodiTypes(KOULUTUSKOODI, KOULUTUSASTE, OPINTOALA, KOULUTUSALA)
        );

        expect(tarjontaUiHelperMock.getKoulutusRelations(and(isA(String.class), eq("lukiolinja_uri")))).andReturn(
                createKoodiTypes(TUTKINTONIMIKE, LAAJUUS_YKSIKKO, LAAJUUS_ARVO));
//
        expect(tarjontaUiHelperMock.getKoodis(eq(createUri(KOULUTUSKOODI)))).andReturn(createKoodiTypes(KOULUTUSKOODI));
        expect(tarjontaUiHelperMock.getKoodis(eq(createUri(LUKIOLINJA)))).andReturn(createKoodiTypes(LUKIOLINJA));

        expect(tarjontaUiHelperMock.getKoodis(eq(createUri(KOULUTUSASTE)))).andReturn(createKoodiTypes(KOULUTUSASTE));
        expect(tarjontaUiHelperMock.getKoodis(eq(createUri(OPINTOALA)))).andReturn(createKoodiTypes(OPINTOALA));
        expect(tarjontaUiHelperMock.getKoodis(eq(createUri(TUTKINTONIMIKE)))).andReturn(createKoodiTypes(TUTKINTONIMIKE));
        // expect(tarjontaUiHelperMock.getKoodis(eq(createUri(KOULUTUSLAJI)))).andReturn(createKoodiTypes(KOULUTUSLAJI));
        expect(tarjontaUiHelperMock.getKoodis(eq(createUri(LAAJUUS_YKSIKKO)))).andReturn(createKoodiTypes(LAAJUUS_YKSIKKO));
        expect(tarjontaUiHelperMock.getKoodis(eq(createUri(LAAJUUS_ARVO)))).andReturn(createKoodiTypes(LAAJUUS_ARVO));
        expect(tarjontaUiHelperMock.getKoodis(eq(createUri(KOULUTUSALA)))).andReturn(createKoodiTypes(KOULUTUSALA));
        expect(tarjontaKoodistoHelperMock.convertKielikoodiToKieliUri("fi")).andReturn("kieli_fi").anyTimes();
    }
}
