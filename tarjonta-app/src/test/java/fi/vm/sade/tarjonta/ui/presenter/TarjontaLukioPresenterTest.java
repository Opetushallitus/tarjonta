package fi.vm.sade.tarjonta.ui.presenter;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoItemType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.service.types.WebLinkkiTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusKoodistoConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusLukioConverter;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.org.OrganisationOidNamePair;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.LukiolinjaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.YhteyshenkiloModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusKuvailevatTiedotFormView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusKuvailevatTiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.easymock.Capture;
import static org.easymock.EasyMock.*;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

/**
 *
 * @author jani
 */
public class TarjontaLukioPresenterTest {

    private static final String ORGANISATION_NAME = "organisation name";
    private static final String WEB_LINK = "http://localhost:8080/";
    private static final String LAAJUUS_ARVO = "laajuus_arvo";
    private static final String LAAJUUS_YKSIKKO = "laajuus_tyyppi";
    private static final String KOULUTUSALA = "koulutusala";
    private static final String KOULUTUSKOODI = "koulutuskoodi";
    private static final String KOULUTUSASTE = "koulutusaste";
    private static final String TUTKINTONIMIKE = "tutkintonimike";
    private static final String KOULUTUSLAJI = "koulutuslaji";
    private static final String LUKIOLINJA = "lukiolinja";
    private static final String OPINTOALA = "opintoala";
    private static final String TUTKINNON_TAVOITTEET = "tutkinnon_tavoitteet";
    private static final String JATKOOPINTOMAHDOLLISUUDET = "jatko-opintomahdollisuudet";
    private static final String KOULUTUKSEN_RAKENNE = "koulutuksen_rakenne";
    private static final int VERSION = 1;
    private static final String KOMO_OID = "komo.234234.234.2.342.34";
    private static final String KOMOTO_OID = "komoto.1321321.321.321";
    private static final String LANGUAGE_FI = "fi";
    private static final String TEXT = "text";
    private static final String ORGANISAATIO_OID = "organisaatio.1.2.3.4.5.6.7";
    private static final Date DATE = new DateTime(2013, 1, 1, 10, 12).toDate();
    private TarjontaLukioPresenter instance;
    private TarjontaAdminService tarjontaAdminServiceMock;
    private TarjontaPublicService tarjontaPublicServiceMock;
    private OIDService oidServiceMock;
    private OrganisaatioService organisaatioServiceMock;
    private KoulutusKoodistoConverter koulutusKoodisto;
    private KoulutusLukioConverter koulutusLukioConverter;
    private EditLukioKoulutusKuvailevatTiedotView kuvailevatTiedotView;
    private EditLukioKoulutusView editLukioKoulutusView;
    private TabSheet.Tab kuvailevatTiedotTab;
    private KoulutusLukioPerustiedotViewModel perustiedot;
    private KoulutusLukioKuvailevatTiedotViewModel kuvailevatTiedot;
    private OrganisaatioDTO orgDto;
    private TarjontaUIHelper tarjontaUiHelper;
    private YhteyshenkiloModel yhteyshenkiloModel;
    private TarjontaPresenter tarjontaPresenter;

    public TarjontaLukioPresenterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        orgDto = new OrganisaatioDTO();
        orgDto.setOid(ORGANISAATIO_OID);
        fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi omtt = new fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi();
        fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi.Teksti teksti = new fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi.Teksti();
        teksti.setKieliKoodi(LANGUAGE_FI);
        teksti.setValue(ORGANISATION_NAME);
        omtt.getTeksti().add(teksti);
        orgDto.setNimi(omtt);

        tarjontaPresenter = new TarjontaPresenter();
        tarjontaPresenter.getNavigationOrganisation().setOrganisationOid(ORGANISAATIO_OID);
        tarjontaPresenter.setRootView(new TarjontaRootView(false));
        instance = new TarjontaLukioPresenter();

        koulutusLukioConverter = new KoulutusLukioConverter();
        kuvailevatTiedotView = new EditLukioKoulutusKuvailevatTiedotView("text");
        editLukioKoulutusView = new EditLukioKoulutusView(KOMOTO_OID);
        instance.setKuvailevatTiedotView(kuvailevatTiedotView);

        TabSheet tabSheet = UiBuilder.tabSheet(new VerticalLayout());
        EditLukioKoulutusKuvailevatTiedotView lisatiedotView = new EditLukioKoulutusKuvailevatTiedotView(KOMOTO_OID);
        kuvailevatTiedotTab = tabSheet.addTab(lisatiedotView, "kuvailevattiedot");

        oidServiceMock = createMock(OIDService.class);
        tarjontaAdminServiceMock = createMock(TarjontaAdminService.class);
        organisaatioServiceMock = createMock(OrganisaatioService.class);
        koulutusKoodisto = new KoulutusKoodistoConverter();//createMock(KoulutusKoodistoConverter.class);
        tarjontaUiHelper = createMock(TarjontaUIHelper.class);

        tarjontaPublicServiceMock = createMock(TarjontaPublicService.class);


        Whitebox.setInternalState(kuvailevatTiedotView, "formView", new EditLukioKoulutusKuvailevatTiedotFormView());
        Whitebox.setInternalState(editLukioKoulutusView, "kuvailevatTiedot", kuvailevatTiedotTab);
        Whitebox.setInternalState(koulutusLukioConverter, "oidService", oidServiceMock);
        Whitebox.setInternalState(koulutusLukioConverter, "organisaatioService", organisaatioServiceMock);
        Whitebox.setInternalState(koulutusLukioConverter, "koulutusKoodisto", koulutusKoodisto);
        Whitebox.setInternalState(instance, "editLukioKoulutusView", editLukioKoulutusView);
        Whitebox.setInternalState(instance, "lukioKoulutusConverter", koulutusLukioConverter);
        Whitebox.setInternalState(instance, "tarjontaAdminService", tarjontaAdminServiceMock);
        Whitebox.setInternalState(tarjontaPresenter, "tarjontaPublicService", tarjontaPublicServiceMock);
        Whitebox.setInternalState(instance, "tarjontaPublicService", tarjontaPublicServiceMock);
        Whitebox.setInternalState(instance, "presenter", tarjontaPresenter);
        Whitebox.setInternalState(koulutusKoodisto, "tarjontaUiHelper", tarjontaUiHelper);

        tarjontaPresenter.getTarjoaja().setSelectedOrganisation(new OrganisationOidNamePair(ORGANISAATIO_OID, "org name"));


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
        perustiedot.setOpintojenLaajuus(LAAJUUS_ARVO);
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

        Capture<LisaaKoulutusTyyppi> localeCapture = new Capture<LisaaKoulutusTyyppi>();

        /*
         * Expect
         */
        expect(tarjontaAdminServiceMock.lisaaKoulutus(capture(localeCapture))).andReturn(new LisaaKoulutusVastausTyyppi());
        expect(organisaatioServiceMock.findByOid(and(isA(String.class), eq(ORGANISAATIO_OID)))).andReturn(orgDto);
        expect(oidServiceMock.newOid(and(isA(NodeClassCode.class), eq(NodeClassCode.TEKN_5)))).andReturn(KOMOTO_OID).anyTimes();
        expect(tarjontaPublicServiceMock.haeKoulutusmoduulit(isA(HaeKoulutusmoduulitKyselyTyyppi.class))).andReturn(vastaus);

        /*
         * replay
         */
        replay(oidServiceMock);
        replay(tarjontaAdminServiceMock);
        replay(organisaatioServiceMock);
        replay(tarjontaPublicServiceMock);

        /*
         * Presenter method call
         */
        instance.saveKoulutus(SaveButtonState.SAVE_AS_DRAFT);

        /*
         * verify
         */
        verify(oidServiceMock);
        verify(tarjontaAdminServiceMock);
        verify(organisaatioServiceMock);
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
        assertEquals(createUri(KOULUTUSASTE), koulutus.getKoulutusaste().getUri());
        assertEquals(createUri(KOULUTUSLAJI), koulutus.getKoulutuslaji().get(0).getUri()); //only one needed
        assertEquals(KoulutusasteTyyppi.LUKIOKOULUTUS, koulutus.getKoulutustyyppi());
        assertEquals(WEB_LINK, koulutus.getLinkki().get(0).getUri());
        assertEquals(createUri(LUKIOLINJA), koulutus.getLukiolinjaKoodi().getUri());

        assertEquals(createUri("pohjakoulutusvaatimus"), koulutus.getPohjakoulutusvaatimus().getUri());
        assertEquals(ORGANISAATIO_OID, koulutus.getTarjoaja());
        assertEquals(TarjontaTila.LUONNOS, koulutus.getTila());

        assertNotNull(koulutus.getYhteyshenkiloTyyppi());
        YhteyshenkiloTyyppi yhteyshenkilo = koulutus.getYhteyshenkiloTyyppi().get(0);
        assertYhteyshenkilo(yhteyshenkilo);
        assertTrue("contact person", koulutus.getYhteyshenkilo().isEmpty()); //not needed

        assertEquals("Kansainvalistyminen", koulutus.getKansainvalistyminen().getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, koulutus.getKansainvalistyminen().getTeksti().get(0).getKieliKoodi());

        assertEquals("YhteistyoMuidenToimijoidenKanssa", koulutus.getYhteistyoMuidenToimijoidenKanssa().getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, koulutus.getYhteistyoMuidenToimijoidenKanssa().getTeksti().get(0).getKieliKoodi());

        assertEquals("Sisalto", koulutus.getSisalto().getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, koulutus.getSisalto().getTeksti().get(0).getKieliKoodi());


        assertEquals(null, koulutus.getPainotus());
        assertEquals(null, koulutus.getNimi()); ///????
        assertEquals(null, koulutus.getSijoittuminenTyoelamaan());
        assertEquals(null, koulutus.getKoulutusohjelmaKoodi());
        assertEquals(null, koulutus.getKoulutusohjelmanValinta());
        assertEquals(null, koulutus.getKuvailevatTiedot());
    }

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

        Capture<PaivitaKoulutusTyyppi> localeCapture = new Capture<PaivitaKoulutusTyyppi>();

        /*
         * Expect
         */
        expect(tarjontaAdminServiceMock.paivitaKoulutus(capture(localeCapture))).andReturn(new PaivitaKoulutusVastausTyyppi());
        expect(organisaatioServiceMock.findByOid(and(isA(String.class), eq(ORGANISAATIO_OID)))).andReturn(orgDto);

        /*
         * replay
         */

        replay(tarjontaAdminServiceMock);
        replay(organisaatioServiceMock);

        /*
         * Presenter method call
         */
        instance.saveKoulutus(SaveButtonState.SAVE_AS_DRAFT);

        /*
         * verify
         */
        verify(tarjontaAdminServiceMock);
        verify(organisaatioServiceMock);

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
        assertEquals(createUri(KOULUTUSASTE), koulutus.getKoulutusaste().getUri());
        assertEquals(createUri(KOULUTUSLAJI), koulutus.getKoulutuslaji().get(0).getUri()); //only one needed
        assertEquals(KoulutusasteTyyppi.LUKIOKOULUTUS, koulutus.getKoulutustyyppi());
        assertEquals(WEB_LINK, koulutus.getLinkki().get(0).getUri());
        assertEquals(createUri(LUKIOLINJA), koulutus.getLukiolinjaKoodi().getUri());

        assertEquals(createUri("pohjakoulutusvaatimus"), koulutus.getPohjakoulutusvaatimus().getUri());
        assertEquals(ORGANISAATIO_OID, koulutus.getTarjoaja());
        assertEquals(TarjontaTila.LUONNOS, koulutus.getTila());

        assertNotNull(koulutus.getYhteyshenkiloTyyppi());
        assertNotNull("getYhteyshenkiloTyyppi not null", koulutus.getYhteyshenkiloTyyppi()); //not needed

        YhteyshenkiloTyyppi yhteyshenkilo = koulutus.getYhteyshenkiloTyyppi().get(0);
        assertYhteyshenkilo(yhteyshenkilo);

        assertEquals("Kansainvalistyminen", koulutus.getKansainvalistyminen().getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, koulutus.getKansainvalistyminen().getTeksti().get(0).getKieliKoodi());

        assertEquals("YhteistyoMuidenToimijoidenKanssa", koulutus.getYhteistyoMuidenToimijoidenKanssa().getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, koulutus.getYhteistyoMuidenToimijoidenKanssa().getTeksti().get(0).getKieliKoodi());

        assertEquals("Sisalto", koulutus.getSisalto().getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, koulutus.getSisalto().getTeksti().get(0).getKieliKoodi());

        assertEquals(null, koulutus.getPainotus());
        assertEquals(null, koulutus.getNimi()); ///????
        assertEquals(null, koulutus.getSijoittuminenTyoelamaan());
        assertEquals(null, koulutus.getKoulutusohjelmaKoodi());
        assertEquals(null, koulutus.getKoulutusohjelmanValinta());
        assertEquals(null, koulutus.getKuvailevatTiedot());
    }

    @Test
    public void testLoad() throws Exception {
        /*
         * initilaize
         */
        perustiedot.setKomotoOid(null); //null == insert new perustiedot

        LueKoulutusVastausTyyppi vastaus = new LueKoulutusVastausTyyppi();
        vastaus.setKansainvalistyminen(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, "Kansainvalistyminen"));
        KoulutuksenKestoTyyppi koulutuksenKestoTyyppi = new KoulutuksenKestoTyyppi();
        koulutuksenKestoTyyppi.setArvo("kesto");
        koulutuksenKestoTyyppi.setYksikko("yksikko");
        vastaus.setKesto(koulutuksenKestoTyyppi);

        vastaus.setKoulutusKoodi(createKoodistoKoodiTyyppi(KOULUTUSKOODI));
        vastaus.setKoulutusaste(createKoodistoKoodiTyyppi(KOULUTUSASTE));
        vastaus.setKoulutusmoduuli(new KoulutusmoduuliKoosteTyyppi());

        vastaus.setKoulutusohjelmanValinta(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, "KoulutusohjelmanValinta"));
        vastaus.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        vastaus.setKuvailevatTiedot(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, "KuvailevatTiedot"));
        vastaus.setLukiolinjaKoodi(createKoodistoKoodiTyyppi(LUKIOLINJA));
        vastaus.setNimi(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, "nimi"));
        vastaus.setOid(KOMOTO_OID);

        vastaus.setSisalto(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, "sisalto"));
        vastaus.setTarjoaja(ORGANISAATIO_OID);
        vastaus.setTila(TarjontaTila.VALMIS);
        vastaus.setYhteistyoMuidenToimijoidenKanssa(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, "setYhteistyoMuidenToimijoidenKanssa"));
        vastaus.getA1A2Kieli().add(createKoodistoKoodiTyyppi("A1"));
        vastaus.getAmmattinimikkeet().add(createKoodistoKoodiTyyppi("ammattinimikkeet"));
        vastaus.getB1Kieli().add(createKoodistoKoodiTyyppi("b1"));
        vastaus.getB2Kieli().add(createKoodistoKoodiTyyppi("b2"));
        vastaus.getB3Kieli().add(createKoodistoKoodiTyyppi("b3"));
        vastaus.setPainotus(null);
        vastaus.setKoulutuksenAlkamisPaiva(null);
        vastaus.setKoulutusohjelmaKoodi(null);
        vastaus.setPohjakoulutusvaatimus(null);
        vastaus.setSijoittuminenTyoelamaan(null);

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
        vastaus.getYhteyshenkilo().add(y);

        KoulutusmoduuliKoosteTyyppi t = new KoulutusmoduuliKoosteTyyppi();
        t.setOid(KOMO_OID);
        t.setKoulutusalaUri(createUri(KOULUTUSALA));
        t.setJatkoOpintoMahdollisuudet(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, JATKOOPINTOMAHDOLLISUUDET));
        t.setKoulutuksenRakenne(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, KOULUTUKSEN_RAKENNE));
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
        t.setTavoitteet(null);
        t.setKoulutusohjelmakoodiUri(null);

        vastaus.setKoulutusmoduuli(t);
        /*
         * Expect
         */
        expect(organisaatioServiceMock.findByOid(and(isA(String.class), eq(ORGANISAATIO_OID)))).andReturn(orgDto);
        expect(oidServiceMock.newOid(and(isA(NodeClassCode.class), eq(NodeClassCode.TEKN_5)))).andReturn(KOMOTO_OID).anyTimes();
        expect(tarjontaPublicServiceMock.lueKoulutus(isA(LueKoulutusKyselyTyyppi.class))).andReturn(vastaus);

        expect(tarjontaUiHelper.getKoodis(eq(createUri(KOULUTUSKOODI)))).andReturn(createKoodiType(KOULUTUSKOODI));
        expect(tarjontaUiHelper.getKoodis(eq(createUri(LUKIOLINJA)))).andReturn(createKoodiType(LUKIOLINJA));
        expect(tarjontaUiHelper.getKoodis(eq(createUri(KOULUTUSASTE)))).andReturn(createKoodiType(KOULUTUSASTE));
        expect(tarjontaUiHelper.getKoodis(eq(createUri(OPINTOALA)))).andReturn(createKoodiType(OPINTOALA));
        expect(tarjontaUiHelper.getKoodis(eq(createUri(TUTKINTONIMIKE)))).andReturn(createKoodiType(TUTKINTONIMIKE));
        //expect(tarjontaUiHelper.getKoodis(eq(createUri(KOULUTUSLAJI)))).andReturn(createKoodiType(KOULUTUSLAJI));
        expect(tarjontaUiHelper.getKoodis(eq(createUri(LAAJUUS_YKSIKKO)))).andReturn(createKoodiType(LAAJUUS_YKSIKKO));
        //expect(tarjontaUiHelper.getKoodis(eq(createUri(LAAJUUS_ARVO)))).andReturn(createKoodiType(LAAJUUS_ARVO));
        expect(tarjontaUiHelper.getKoodis(eq(createUri(KOULUTUSALA)))).andReturn(createKoodiType(KOULUTUSALA));

        /*
         * replay
         */
        replay(tarjontaUiHelper);
        replay(oidServiceMock);
        replay(organisaatioServiceMock);
        replay(tarjontaPublicServiceMock);

        /*
         * Presenter method call
         */

        instance.showEditKoulutusView(KOMOTO_OID, null);

        /*
         * verify
         */
        verify(tarjontaUiHelper);
        verify(oidServiceMock);
        verify(organisaatioServiceMock);
        verify(tarjontaPublicServiceMock);

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
        assertEquals(null, perustiedotModel.getPohjakoulutusvaatimus());
    }

    private void assertYhteyshenkilo(YhteyshenkiloModel cperson) {
        assertNotNull("YhteyshenkiloTyyppi", cperson);
        assertEquals(yhteyshenkiloModel.getYhtHenkKokoNimi() + " suku", cperson.getYhtHenkKokoNimi());
        assertEquals(yhteyshenkiloModel.getYhtHenkPuhelin(), cperson.getYhtHenkPuhelin());
        assertEquals(yhteyshenkiloModel.getYhtHenkEmail(), cperson.getYhtHenkEmail());
        assertEquals(yhteyshenkiloModel.getYhtHenkTitteli(), cperson.getYhtHenkTitteli());
        assertEquals(yhteyshenkiloModel.getYhtHenkiloOid(), cperson.getYhtHenkiloOid());
    }

    private void assertYhteyshenkilo(YhteyshenkiloTyyppi yhteyshenkilo) {
        assertNotNull("YhteyshenkiloTyyppi", yhteyshenkilo);
        assertEquals(yhteyshenkiloModel.getYhtHenkKokoNimi(), yhteyshenkilo.getEtunimet());
        assertEquals(yhteyshenkiloModel.getYhtHenkiloOid(), yhteyshenkilo.getHenkiloOid());
        assertEquals(0, yhteyshenkilo.getKielet().size());
        assertEquals(yhteyshenkiloModel.getYhtHenkPuhelin(), yhteyshenkilo.getPuhelin());
        assertEquals(yhteyshenkiloModel.getYhtHenkEmail(), yhteyshenkilo.getSahkoposti());
        assertEquals(yhteyshenkiloModel.getYhtHenkTitteli(), yhteyshenkilo.getTitteli());
    }

    private void assertKuvailevatTiedot(KoulutusTyyppi koulutus) {
        assertEquals(1, koulutus.getLukiodiplomit().size());
        assertEquals(2, koulutus.getMuutKielet().size());
        assertEquals("A1 A2", 3, koulutus.getA1A2Kieli().size());
        assertEquals(0, koulutus.getAmmattinimikkeet().size());
        assertEquals("B1", 4, koulutus.getB1Kieli().size());
        assertEquals("B2", 5, koulutus.getB2Kieli().size());
        assertEquals("B3", 6, koulutus.getB3Kieli().size());
    }

    /*
     * Helper methods
     */
    private MonikielinenTekstiModel createMonikielinenTeksti(String koodiUri) {
        MonikielinenTekstiModel mtm = new MonikielinenTekstiModel();
        mtm.getKielikaannos().add(new KielikaannosViewModel(LANGUAGE_FI, TEXT));
        createKoodiModel(mtm, koodiUri);

        return mtm;
    }

    private KoodiModel createKoodiModel(String koodiUri) {
        KoodiModel koodiModel = new KoodiModel();
        createKoodiModel(koodiModel, koodiUri);
        return koodiModel;
    }

    private LukiolinjaModel createLukiolinja(String koodiUri) {
        LukiolinjaModel lukiolinja = new LukiolinjaModel();
        createKoodiModel(lukiolinja, koodiUri);
        return lukiolinja;
    }

    private KoulutuskoodiModel createKoulutuskoodiModel(String koodiUri) {
        KoulutuskoodiModel km = new KoulutuskoodiModel();
        createKoodiModel(km, koodiUri);
        return km;
    }

    private KoulutusKoodistoModel createKoodiModel(KoulutusKoodistoModel kkm, String fieldName) {
        if (kkm == null) {
            kkm = new KoodiModel();
        }

        final String koodiUri = fieldName + "_uri";

        kkm.setKielikoodi(LANGUAGE_FI);
        kkm.setKoodi(fieldName);
        kkm.setKoodistoUri(koodiUri);
        kkm.setKoodistoUriVersio(createUri(fieldName, VERSION));
        kkm.setKoodistoVersio(1);
        kkm.setKuvaus(fieldName + " kuvaus");
        kkm.setNimi(fieldName + " nimi");

        return kkm;
    }

    private static String createUri(String fieldName) {
        return createUri(fieldName, VERSION);
    }

    private static String createUri(String fieldName, int version) {
        return fieldName + "_uri#" + version;
    }

    private List<String> createList(final int max, final String value) {
        List<String> list = new ArrayList<String>(max);

        for (int i = 0; i < max; i++) {
            list.add(value + "_" + i);
        }
        return list;
    }

    private MonikielinenTekstiTyyppi convertToMonikielinenTekstiTyyppi(String languageCode, String text) {
        MonikielinenTekstiTyyppi tyyppi = new MonikielinenTekstiTyyppi();
        tyyppi.getTeksti().add(KoulutusConveter.convertToMonikielinenTekstiTyyppiTeksti(languageCode, text));

        return tyyppi;
    }

    private KoodistoKoodiTyyppi createKoodistoKoodiTyyppi(String fieldName) {
        return KoulutusConveter.createKoodi(createUri(fieldName), false, fieldName);
    }

    private List<KoodiType> createKoodiType(String fieldName) {
        KoodiType koodiType = new KoodiType();
        koodiType.setKoodiUri(fieldName + "_uri");
        koodiType.setVersio(VERSION);
        koodiType.setKoodiArvo(fieldName);
        koodiType.setTila(TilaType.HYVAKSYTTY);

        KoodiMetadataType meta = new KoodiMetadataType();
        meta.setKuvaus(fieldName + " kuvaus");
        meta.setKieli(KieliType.FI);
        koodiType.getMetadata().add(meta);
        koodiType.setKoodisto(new KoodistoItemType());

        List<KoodiType> types = new ArrayList<KoodiType>();
        types.add(koodiType);
        return types;
    }
}