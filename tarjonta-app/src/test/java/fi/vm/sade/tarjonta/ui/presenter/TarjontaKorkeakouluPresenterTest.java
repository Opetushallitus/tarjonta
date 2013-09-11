package fi.vm.sade.tarjonta.ui.presenter;

import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.Capture;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.google.common.collect.Lists;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HenkiloTyyppi;
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
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.helper.conversion.ConversionUtils;
import fi.vm.sade.tarjonta.ui.helper.conversion.KorkeakouluConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusKoodistoConverter;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KoulutuskoodiRowModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.TutkintoohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.YhteyshenkiloModel;
import fi.vm.sade.tarjonta.ui.model.org.OrganisationOidNamePair;
import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.EditKorkeakouluKuvailevatTiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.EditKorkeakouluView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusKuvailevatTiedotView;

/**
 *
 * @author jani
 */
public class TarjontaKorkeakouluPresenterTest extends BaseTarjontaTest {

    protected static final String TUTKINTOOHJELMA = "tutkintoohjelma";
    private TarjontaKorkeakouluPresenter instance;
    private TarjontaAdminService tarjontaAdminServiceMock;
    private TarjontaPublicService tarjontaPublicServiceMock;
    private OIDService oidServiceMock;
    private OrganisaatioSearchService organisaatioSearchServiceMock;
    private KoulutusKoodistoConverter koulutusKoodisto;
    private KorkeakouluConverter korkeakouluConverter;
    private EditKorkeakouluKuvailevatTiedotView kuvailevatTiedotView;
    private EditKorkeakouluView editKoulutusView;
    private TabSheet.Tab kuvailevatTiedotTab;
    private KorkeakouluPerustiedotViewModel perustiedot;
    private KorkeakouluKuvailevatTiedotViewModel kuvailevatTiedot;
    private OrganisaatioPerustieto orgPerus;
    private List<OrganisaatioPerustieto> orgs;
    private TarjontaUIHelper tarjontaUiHelper;
    private YhteyshenkiloModel yhteyshenkilo1, yhteyshenkilo2;
    private TarjontaPresenter tarjontaPresenter;
    private static final String KOMO_ULKOINEN_TUNNISTE = "654321";
    private static final String KOMO_TUTKINTOOHJELMA_NAME = "tutkinto-ohjelman nimi";
    private static final Long VERSION_ID = 123l;
    private TarjontaKoodistoHelper tarjontaKoodistoHelperMock;
    private KoodistoURI helpper;

    public TarjontaKorkeakouluPresenterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        helpper = new KoodistoURI();
        helpper.setKoodistoTutkintoUri(createKoodistoUri(KOULUTUSKOODI));
        helpper.setKoulutusalaUri(createKoodistoUri(KOULUTUSALA));
        helpper.setKoodistoasteUri(createKoodistoUri(KOULUTUSASTE));
        helpper.setOpintoalaUri(createKoodistoUri(OPINTOALA));
        helpper.setTutkintonimikeUri(createKoodistoUri(TUTKINTONIMIKE));
        helpper.setOpintojenLaajuusyksikkoUri(createKoodistoUri(LAAJUUS_YKSIKKO));

        orgs = Lists.<OrganisaatioPerustieto>newArrayList();

        orgPerus = new OrganisaatioPerustieto();
        orgPerus.setOid(ORGANISAATIO_OID);
        orgPerus.setNimiFi(ORGANISATION_NAME);
        orgPerus.setOid(ORGANISAATIO_OID);
        fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi omtt = new fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi();
        fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi.Teksti teksti = new fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi.Teksti();
        teksti.setKieliKoodi(LANGUAGE_FI);
        teksti.setValue(ORGANISATION_NAME);
        orgs.add(orgPerus);

        tarjontaPresenter = new TarjontaPresenter();
        tarjontaPresenter.getNavigationOrganisation().setOrganisationOid(ORGANISAATIO_OID);
        tarjontaPresenter.setRootView(new TarjontaRootView());
        instance = new TarjontaKorkeakouluPresenter();

        korkeakouluConverter = new KorkeakouluConverter();
        kuvailevatTiedotView = new EditKorkeakouluKuvailevatTiedotView("text");
        editKoulutusView = new EditKorkeakouluView(KOMOTO_OID);
        instance.setKuvailevatTiedotView(kuvailevatTiedotView);

        TabSheet tabSheet = UiBuilder.tabSheet(new VerticalLayout());
        EditLukioKoulutusKuvailevatTiedotView lisatiedotView = new EditLukioKoulutusKuvailevatTiedotView(KOMOTO_OID);
        kuvailevatTiedotTab = tabSheet.addTab(lisatiedotView, "kuvailevattiedot");

        tarjontaKoodistoHelperMock = createMock(TarjontaKoodistoHelper.class);
        oidServiceMock = createMock(OIDService.class);
        tarjontaAdminServiceMock = createMock(TarjontaAdminService.class);
        organisaatioSearchServiceMock = createMock(OrganisaatioSearchService.class);
        koulutusKoodisto = new KoulutusKoodistoConverter();//createMock(KoulutusKoodistoConverter.class);
        tarjontaUiHelper = createMock(TarjontaUIHelper.class);
        tarjontaPublicServiceMock = createMock(TarjontaPublicService.class);

        //Whitebox.setInternalState(kuvailevatTiedotView, "formView", new EditKorkeakouluKuvailevatTiedotView(null));
        Whitebox.setInternalState(editKoulutusView, "kuvailevatTiedot", kuvailevatTiedotTab);
        Whitebox.setInternalState(korkeakouluConverter, "helper", tarjontaKoodistoHelperMock);
        Whitebox.setInternalState(korkeakouluConverter, "oidService", oidServiceMock);
        Whitebox.setInternalState(korkeakouluConverter, "organisaatioSearchService", organisaatioSearchServiceMock);
        Whitebox.setInternalState(korkeakouluConverter, "koulutusKoodisto", koulutusKoodisto);
        Whitebox.setInternalState(instance, "editKoulutusView", editKoulutusView);
        Whitebox.setInternalState(instance, "korkeakouluConverter", korkeakouluConverter);
        Whitebox.setInternalState(instance, "tarjontaAdminService", tarjontaAdminServiceMock);
        Whitebox.setInternalState(tarjontaPresenter, "tarjontaPublicService", tarjontaPublicServiceMock);
        Whitebox.setInternalState(instance, "tarjontaPublicService", tarjontaPublicServiceMock);
        Whitebox.setInternalState(instance, "presenter", tarjontaPresenter);
        Whitebox.setInternalState(koulutusKoodisto, "tarjontaUiHelper", tarjontaUiHelper);
        Whitebox.setInternalState(koulutusKoodisto, "tarjontaKoodistoHelper", tarjontaKoodistoHelperMock);

        tarjontaPresenter.getTarjoaja().setSelectedOrganisation(new OrganisationOidNamePair(ORGANISAATIO_OID, "org name"));

        /*
         * Perustiedot tab data
         */
        perustiedot = instance.getPerustiedotModel();

        /*
         * Create new koulutuskoodi model. (populated by dialog)
         */
        KoulutuskoodiRowModel koulutuskoodiRowModel = new KoulutuskoodiRowModel(createKoodiModel(KOULUTUSKOODI));
        koulutuskoodiRowModel.setKoodi(KOMO_ULKOINEN_TUNNISTE);
        koulutuskoodiRowModel.setNimi("koulutuskoodi name");
        instance.getValitseKoulutusModel().setKoulutuskoodiRow(koulutuskoodiRowModel);

        /*
         * Create new tutkintoohjelma model (populated by user & autocomplete)
         */
        TutkintoohjelmaModel tutkintoohjelmaModel = new TutkintoohjelmaModel();
        tutkintoohjelmaModel.setNimi(KOMO_TUTKINTOOHJELMA_NAME);
        final Set<KielikaannosViewModel> kielikaannos = KoulutusConveter.convertToKielikaannosViewModel(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, KOMO_TUTKINTOOHJELMA_NAME));

        tutkintoohjelmaModel.setKielikaannos(LANGUAGE_FI, LANGUAGE_FI, kielikaannos);
        perustiedot.setTutkintoohjelma(tutkintoohjelmaModel);

        /*
         * set the base data
         */
        perustiedot.setKomotoOid(KOMOTO_OID);
        perustiedot.setKoulutuksenAlkamisPvm(DATE);
        perustiedot.setOpintojenLaajuus(LAAJUUS_ARVO);
        perustiedot.setOpintojenLaajuusyksikko(createKoodiModel(LAAJUUS_YKSIKKO));
        perustiedot.setSuunniteltuKesto("kesto");
        perustiedot.setSuunniteltuKestoTyyppi("kesto_tyyppi");
        perustiedot.setOpintojenMaksullisuus(Boolean.TRUE);

        perustiedot.setOpetuskielis(createSet(4, "opetuskielis"));
        perustiedot.setOpetusmuodos(createSet(3, "opetusmuodos"));
        perustiedot.setTeemas(createSet(2, "teemas"));
        perustiedot.setPohjakoulutusvaatimukset(createSet(1, "pohjakoulutusvaatimukset"));
        perustiedot.setTunniste(KOMO_ULKOINEN_TUNNISTE);
        perustiedot.setKoulutusmoduuliOid(KOMO_OID);

        /*
         * all unused fields
         */
        perustiedot.setPohjakoulutusvaatimus(null);
        perustiedot.setJatkoopintomahdollisuudet(null);
        perustiedot.setKoulutuksenRakenne(null);
        perustiedot.setTavoitteet(null);
        perustiedot.setKoulutusala(null);
        perustiedot.setKoulutusaste(null);
        perustiedot.setKoulutuslaji(null);
        perustiedot.setTutkintonimike(null);
        perustiedot.setTutkinto(null);

        yhteyshenkilo1 = new YhteyshenkiloModel();
        yhteyshenkilo1.setYhtHenkEmail("email");
        yhteyshenkilo1.setYhtHenkKokoNimi("full name");
        yhteyshenkilo1.setYhtHenkPuhelin("12345678910");
        yhteyshenkilo1.setYhtHenkTitteli("Mr.");
        yhteyshenkilo1.setYhtHenkiloOid("12.1232402.8320948.2843");
        yhteyshenkilo1.setHenkiloTyyppi(HenkiloTyyppi.YHTEYSHENKILO);
        perustiedot.setYhteyshenkilo(yhteyshenkilo1);

        yhteyshenkilo2 = new YhteyshenkiloModel();
        yhteyshenkilo2.setYhtHenkEmail("email");
        yhteyshenkilo2.setYhtHenkKokoNimi("full name");
        yhteyshenkilo2.setYhtHenkPuhelin("12345678910");
        yhteyshenkilo2.setYhtHenkTitteli("Mr.");
        yhteyshenkilo2.setYhtHenkiloOid("12.1232402.8320948.2843");
        yhteyshenkilo2.setHenkiloTyyppi(HenkiloTyyppi.ECTS_KOORDINAATTORI);
        perustiedot.setEctsKoordinaattori(yhteyshenkilo2);

        /*
         * Kuvailevat tiedot tab data
         */

        kuvailevatTiedot = instance.getKuvailevatTiedotModel();

        Map<String, KorkeakouluLisatietoModel> map = new HashMap<String, KorkeakouluLisatietoModel>();
        KorkeakouluLisatietoModel koulutusLisatietoModel = new KorkeakouluLisatietoModel();
        koulutusLisatietoModel.setKoulutusohjelmanAmmatillisetTavoitteet("koulutusohjelmanAmmatillisetTavoitteet");
        koulutusLisatietoModel.setPaaaineenValinta("paaaineenValinta");
        koulutusLisatietoModel.setKoulutuksenSisalto("koulutuksenSisalto");
        koulutusLisatietoModel.setKoulutuksenRakenne("koulutuksenRakenne");
        koulutusLisatietoModel.setKuvausKoulutuksenRakenteesta("kuvausKoulutuksenRakenteesta");
        koulutusLisatietoModel.setLisatietoaOpetuskielesta("lisatietoaOpetuskielesta");
        koulutusLisatietoModel.setLopputyonKuvaus("lopputyonKuvaus");
        koulutusLisatietoModel.setOpintojenMaksullisuus("lopputyonKuvaus");
        koulutusLisatietoModel.setSijoittautuminenTyoelamaan("opintojenMaksullisuus");
        koulutusLisatietoModel.setPatevyys("patevyys");
        koulutusLisatietoModel.setKansainvalistyminen("kansainvalistyminen");
        koulutusLisatietoModel.setYhteistyoMuidenToimijoidenKanssa("yhteistyoMuidenToimijoidenKanssa");
        koulutusLisatietoModel.setTutkimuksenPainopisteet("tutkimuksenPainopisteet");
        koulutusLisatietoModel.setJatkoOpintomahdollisuudet("jatkoOpintomahdollisuudet");
        map.put("fi", koulutusLisatietoModel);

        kuvailevatTiedot.setLisatiedot(map);
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
        LisaaKoulutusVastausTyyppi lisaaKoulutusVastausTyyppi = new LisaaKoulutusVastausTyyppi();
        lisaaKoulutusVastausTyyppi.setVersion(VERSION_ID + 1);
        lisaaKoulutusVastausTyyppi.setKomoOid("komooid_1234567");
        /*
         * Expect
         */
        expect(tarjontaAdminServiceMock.lisaaKoulutus(capture(localeCapture))).andReturn(lisaaKoulutusVastausTyyppi);
        expect(organisaatioSearchServiceMock.findByOidSet(anyObject(Set.class))).andReturn(orgs);
        expect(oidServiceMock.newOid(and(isA(NodeClassCode.class), eq(NodeClassCode.TEKN_5)))).andReturn(KOMOTO_OID).anyTimes();

        /*
         * replay
         */
        replay(oidServiceMock);
        replay(tarjontaAdminServiceMock);
        replay(organisaatioSearchServiceMock);

        /*
         * Presenter method call
         */
        instance.saveKoulutus(SaveButtonState.SAVE_AS_DRAFT);

        /*
         * verify
         */
        verify(oidServiceMock);
        verify(tarjontaAdminServiceMock);
        verify(organisaatioSearchServiceMock);

        LisaaKoulutusTyyppi koulutus = localeCapture.getValue();
        assertNotNull(koulutus);
        assertNull("version", koulutus.getVersion());

        /*
         * Null fields
         */
        assertOther(koulutus);
    }

    @Test
    public void testUpdateSaveKoulutus() throws Exception {
        /*
         * initialize UI model objects
         */

        perustiedot.setKomotoOid(KOMOTO_OID); //null == insert new perustiedot
        perustiedot.setVersion(VERSION_ID);

        /*
         * initialize other objects
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

        Capture<PaivitaKoulutusTyyppi> localeCapture = new Capture<PaivitaKoulutusTyyppi>();

        /*
         * expect
         */
        expect(tarjontaAdminServiceMock.paivitaKoulutus(capture(localeCapture))).andReturn(new PaivitaKoulutusVastausTyyppi());
        expect(organisaatioSearchServiceMock.findByOidSet(anyObject(Set.class))).andReturn(orgs);

        /*
         * replay
         */
        replay(tarjontaAdminServiceMock);
        replay(organisaatioSearchServiceMock);

        /*
         * Presenter method call
         */
        instance.saveKoulutus(SaveButtonState.SAVE_AS_DRAFT);

        /*
         * verify
         */
        verify(tarjontaAdminServiceMock);
        verify(organisaatioSearchServiceMock);

        PaivitaKoulutusTyyppi koulutus = localeCapture.getValue();
        assertNotNull(koulutus);
        assertOther(koulutus);
        assertEquals("KOMO OID", KOMOTO_OID, koulutus.getOid());
        assertEquals("version", VERSION_ID, koulutus.getVersion());
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

        setTeksti(vastaus.getTekstit(), KomotoTeksti.KOULUTUSOHJELMAN_VALINTA, LANGUAGE_FI, "KoulutusohjelmanValinta");
        vastaus.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        setTeksti(vastaus.getTekstit(), KomotoTeksti.KUVAILEVAT_TIEDOT, LANGUAGE_FI, "KuvailevatTiedot");

        vastaus.setOid(KOMOTO_OID);
        vastaus.setTarjoaja(ORGANISAATIO_OID);
        vastaus.setTila(TarjontaTila.VALMIS);
        vastaus.setVersion(VERSION_ID);

        vastaus.getAmmattinimikkeet().add(createKoodistoKoodiTyyppi("ammattinimikkeet"));
        clearTeksti(vastaus.getTekstit(), KomotoTeksti.PAINOTUS);
        vastaus.setKoulutuksenAlkamisPaiva(null);
        vastaus.setPohjakoulutusvaatimus(null);
        clearTeksti(vastaus.getTekstit(), KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN);

        vastaus.getPohjakoulutusvaatimusKorkeakoulu().add(createKoodistoKoodiTyyppi("pohjakoulutusvaatimus1"));
        vastaus.getPohjakoulutusvaatimusKorkeakoulu().add(createKoodistoKoodiTyyppi("pohjakoulutusvaatimus2"));

        vastaus.getOpetuskieli().add(createKoodistoKoodiTyyppi("opetuskieli1"));
        vastaus.getOpetuskieli().add(createKoodistoKoodiTyyppi("opetuskieli2"));
        vastaus.getOpetuskieli().add(createKoodistoKoodiTyyppi("opetuskieli3"));

        vastaus.getOpetusmuoto().add(createKoodistoKoodiTyyppi("opetusmuodos1"));
        vastaus.getOpetusmuoto().add(createKoodistoKoodiTyyppi("opetusmuodos2"));
        vastaus.getOpetusmuoto().add(createKoodistoKoodiTyyppi("opetusmuodos3"));
        vastaus.getOpetusmuoto().add(createKoodistoKoodiTyyppi("opetusmuodos4"));

        vastaus.getTeemat().add(createKoodistoKoodiTyyppi("teema1"));
        vastaus.getTeemat().add(createKoodistoKoodiTyyppi("teema2"));
        vastaus.getTeemat().add(createKoodistoKoodiTyyppi("teema3"));
        vastaus.getTeemat().add(createKoodistoKoodiTyyppi("teema4"));
        vastaus.getTeemat().add(createKoodistoKoodiTyyppi("teema5"));

        YhteyshenkiloTyyppi y = new YhteyshenkiloTyyppi();
        y.setSukunimi("suku");
        y.setEtunimet(yhteyshenkilo1.getYhtHenkKokoNimi());
        y.setHenkiloOid(yhteyshenkilo1.getYhtHenkiloOid());
        y.setPuhelin(yhteyshenkilo1.getYhtHenkPuhelin());
        y.setSahkoposti(yhteyshenkilo1.getYhtHenkEmail());
        y.setTitteli(yhteyshenkilo1.getYhtHenkTitteli());
        y.setHenkiloTyyppi(HenkiloTyyppi.YHTEYSHENKILO);
        vastaus.getYhteyshenkiloTyyppi().add(y);

        YhteyshenkiloTyyppi ects = new YhteyshenkiloTyyppi();
        ects.setSukunimi("suku");
        ects.setEtunimet(yhteyshenkilo1.getYhtHenkKokoNimi());
        ects.setHenkiloOid(yhteyshenkilo1.getYhtHenkiloOid());
        ects.setPuhelin(yhteyshenkilo1.getYhtHenkPuhelin());
        ects.setSahkoposti(yhteyshenkilo1.getYhtHenkEmail());
        ects.setTitteli(yhteyshenkilo1.getYhtHenkTitteli());
        ects.setHenkiloTyyppi(HenkiloTyyppi.ECTS_KOORDINAATTORI);
        vastaus.getYhteyshenkiloTyyppi().add(ects);

        KoulutusmoduuliKoosteTyyppi moduuli = new KoulutusmoduuliKoosteTyyppi();
        moduuli.setOid(KOMO_OID);
        moduuli.setKoulutusalaUri(createUri(KOULUTUSALA));
        setTeksti(moduuli.getTekstit(), KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET, LANGUAGE_FI, JATKOOPINTOMAHDOLLISUUDET);
        setTeksti(moduuli.getTekstit(), KomoTeksti.KOULUTUKSEN_RAKENNE, LANGUAGE_FI, KOULUTUKSEN_RAKENNE);
        moduuli.setKoulutusasteUri(createUri(KOULUTUSASTE));
        moduuli.setKoulutuskoodiUri(createUri(KOULUTUSKOODI));
        moduuli.setLaajuusarvoUri(createUri(LAAJUUS_ARVO));
        moduuli.setLaajuusyksikkoUri(createUri(LAAJUUS_YKSIKKO));
        moduuli.setLukiolinjakoodiUri(createUri(LUKIOLINJA));
        moduuli.setTutkintonimikeUri(createUri(TUTKINTONIMIKE));
        moduuli.setOpintoalaUri(createUri(OPINTOALA));
        moduuli.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        moduuli.setKoulutusmoduulinNimi(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, "nimi"));
        moduuli.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        moduuli.setTutkinnonTavoitteet(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, TUTKINNON_TAVOITTEET));
        moduuli.setUlkoinenTunniste(KOMO_ULKOINEN_TUNNISTE);
        moduuli.setNimi(convertToMonikielinenTekstiTyyppi(LANGUAGE_FI, KOMO_TUTKINTOOHJELMA_NAME));
        moduuli.setParentOid(null);
        clearTeksti(moduuli.getTekstit(), KomoTeksti.TAVOITTEET);
        moduuli.setKoulutusohjelmakoodiUri(null);

        vastaus.setKoulutusmoduuli(moduuli);
        /*
         * Expect
         */

        expect(organisaatioSearchServiceMock.findByOidSet(anyObject(Set.class))).andReturn(orgs);
        expect(oidServiceMock.newOid(and(isA(NodeClassCode.class), eq(NodeClassCode.TEKN_5)))).andReturn(KOMOTO_OID).anyTimes();
        expect(tarjontaPublicServiceMock.lueKoulutus(isA(LueKoulutusKyselyTyyppi.class))).andReturn(vastaus);
        expect(tarjontaKoodistoHelperMock.convertKielikoodiToKieliUri("fi")).andReturn("kieli_fi").anyTimes();


        final KoodiType koulutuskoodi = createKoodiType(KOULUTUSKOODI, helpper.KOODISTO_TUTKINTO_URI);
        final KoodiType koulutusala = createKoodiType(KOULUTUSALA, helpper.KOODISTO_KOULUTUSALA_URI);
        final KoodiType opintoala = createKoodiType(OPINTOALA, helpper.KOODISTO_OPINTOALA_URI);
        final KoodiType tutkintonimike = createKoodiType(TUTKINTONIMIKE, helpper.KOODISTO_TUTKINTONIMIKE_URI);
        final KoodiType laajuusyksikko = createKoodiType(LAAJUUS_YKSIKKO, helpper.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI);
        final KoodiType koulutusaste = createKoodiType(KOULUTUSASTE, helpper.KOODISTO_KOULUTUSASTE_URI);
        List<KoodiType> commonKomoData = Lists.<KoodiType>newArrayList();

        commonKomoData.add(koulutusala);
        commonKomoData.add(opintoala);
        commonKomoData.add(tutkintonimike);
        commonKomoData.add(laajuusyksikko);
        commonKomoData.add(koulutusaste);


        expect(tarjontaUiHelper.getKoodis(eq(koulutuskoodi.getKoodiUri() + "#1"))).andReturn(createKoodiTypeList(koulutuskoodi));
        expect(tarjontaUiHelper.getKoodis(eq(koulutusaste.getKoodiUri() + "#1"))).andReturn(createKoodiTypeList(koulutusaste));
        expect(tarjontaUiHelper.getKoodis(eq(opintoala.getKoodiUri() + "#1"))).andReturn(createKoodiTypeList(opintoala));
        expect(tarjontaUiHelper.getKoodis(eq(tutkintonimike.getKoodiUri() + "#1"))).andReturn(createKoodiTypeList(tutkintonimike));
        expect(tarjontaUiHelper.getKoodis(eq(laajuusyksikko.getKoodiUri() + "#1"))).andReturn(createKoodiTypeList(laajuusyksikko));
        expect(tarjontaUiHelper.getKoodis(eq(koulutusala.getKoodiUri() + "#1"))).andReturn(createKoodiTypeList(koulutusala));

        /*
         * replay
         */
        replay(tarjontaUiHelper);
        replay(oidServiceMock);
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
        verify(tarjontaKoodistoHelperMock);
        verify(tarjontaUiHelper);
        verify(oidServiceMock);
        verify(organisaatioSearchServiceMock);
        verify(tarjontaPublicServiceMock);

        KorkeakouluPerustiedotViewModel perustiedotModel = instance.getPerustiedotModel();
        assertEquals(KOMOTO_OID, perustiedotModel.getKomotoOid());
        assertEquals(null, perustiedotModel.getKoulutuksenAlkamisPvm());

        assertNotNull("koulutuskoodi model", perustiedotModel.getKoulutuskoodiModel());
        assertEquals(createUri(TUTKINTONIMIKE), perustiedotModel.getKoulutuskoodiModel().getTutkintonimike().getKoodistoUriVersio());
        assertEquals(createUri(KOULUTUSALA), perustiedotModel.getKoulutuskoodiModel().getKoulutusala().getKoodistoUriVersio());
        assertEquals(createUri(KOULUTUSASTE), perustiedotModel.getKoulutuskoodiModel().getKoulutusaste().getKoodistoUriVersio());
        assertEquals(createUri(KOULUTUSKOODI), perustiedotModel.getKoulutuskoodiModel().getKoodistoUriVersio());
        assertEquals(createUri(OPINTOALA), perustiedotModel.getKoulutuskoodiModel().getOpintoala().getKoodistoUriVersio());
        assertEquals(createUri(LAAJUUS_YKSIKKO), perustiedotModel.getKoulutuskoodiModel().getOpintojenLaajuusyksikko().getKoodistoUriVersio());
        assertEquals(createUri(LAAJUUS_ARVO), perustiedotModel.getOpintojenLaajuus());

        assertEquals(KOMO_OID, perustiedotModel.getKoulutusmoduuliOid());

        assertEquals("no names for tutkinto-ohjelma", 1, perustiedotModel.getTutkintoohjelma().getKielikaannos().size());
        assertEquals("654321", perustiedotModel.getTutkintoohjelma().getKoodi());
        assertEquals(KOMO_TUTKINTOOHJELMA_NAME, perustiedotModel.getTutkintoohjelma().getNimi());

        /*
         * values on bottom are loaded to the model only after combox event is fired
         */
        assertEquals("opetuskielis", 3, perustiedotModel.getOpetuskielis().size());
        assertEquals("opetusmuodos", 4, perustiedotModel.getOpetusmuodos().size());
        assertEquals("teemas", 5, perustiedotModel.getTeemas().size());
        assertEquals("pohjakoulutusvaatimukset", 2, perustiedotModel.getPohjakoulutusvaatimukset().size());

        assertEquals(ORGANISATION_NAME, tarjontaPresenter.getTarjoaja().getSelectedOrganisation().getOrganisationName());
        assertEquals(ORGANISAATIO_OID, tarjontaPresenter.getTarjoaja().getSelectedOrganisationOid());
        assertEquals("kesto", perustiedotModel.getSuunniteltuKesto());
        assertEquals("yksikko", perustiedotModel.getSuunniteltuKestoTyyppi());

        assertEquals(TarjontaTila.VALMIS, perustiedotModel.getTila());
        assertYhteyshenkilo(perustiedotModel.getYhteyshenkilo(), HenkiloTyyppi.YHTEYSHENKILO);
        assertYhteyshenkilo(perustiedotModel.getEctsKoordinaattori(), HenkiloTyyppi.ECTS_KOORDINAATTORI);

        assertNull("tutkinto", perustiedotModel.getTutkinto());
        assertNull("kuvaus tavoitteet", perustiedotModel.getTavoitteet());
        assertNull("kuvaus pohjakoulutusvaatimus", perustiedotModel.getPohjakoulutusvaatimus());
        assertNull("kuvaus koulutuksen rakenne", perustiedotModel.getKoulutuksenRakenne());

        assertNull(perustiedotModel.getTutkintonimike());
        assertNull(perustiedotModel.getKoulutusala());
        assertNull(perustiedotModel.getKoulutusaste());
        assertNull(perustiedotModel.getOpintoala());
        assertNull(perustiedotModel.getOpintojenLaajuusyksikko());
    }

    private void assertYhteyshenkilo(YhteyshenkiloModel cperson, HenkiloTyyppi tyyppi) {
        assertNotNull("YhteyshenkiloTyyppi", cperson);
        assertEquals(yhteyshenkilo1.getYhtHenkKokoNimi() + " suku", cperson.getYhtHenkKokoNimi());
        assertEquals(yhteyshenkilo1.getYhtHenkPuhelin(), cperson.getYhtHenkPuhelin());
        assertEquals(yhteyshenkilo1.getYhtHenkEmail(), cperson.getYhtHenkEmail());
        assertEquals(yhteyshenkilo1.getYhtHenkTitteli(), cperson.getYhtHenkTitteli());
        assertEquals(yhteyshenkilo1.getYhtHenkiloOid(), cperson.getYhtHenkiloOid());
        assertEquals(tyyppi, cperson.getHenkiloTyyppi());
    }

    private void assertYhteyshenkilo(YhteyshenkiloTyyppi yhteyshenkilo, HenkiloTyyppi tyyppi) {
        assertNotNull("YhteyshenkiloTyyppi", yhteyshenkilo);
        assertEquals(yhteyshenkilo2.getYhtHenkKokoNimi(), yhteyshenkilo.getEtunimet());
        assertEquals(yhteyshenkilo2.getYhtHenkiloOid(), yhteyshenkilo.getHenkiloOid());
        assertEquals(0, yhteyshenkilo.getKielet().size());
        assertEquals(yhteyshenkilo2.getYhtHenkPuhelin(), yhteyshenkilo.getPuhelin());
        assertEquals(yhteyshenkilo2.getYhtHenkEmail(), yhteyshenkilo.getSahkoposti());
        assertEquals(yhteyshenkilo2.getYhtHenkTitteli(), yhteyshenkilo.getTitteli());
        assertEquals(tyyppi, yhteyshenkilo.getHenkiloTyyppi());
    }

    protected KoodistoKoodiTyyppi createKoodistoKoodiTyyppiWithValue(final String fieldName, final String value) {
        KoodistoKoodiTyyppi createKoodi = KoulutusConveter.createKoodi(createUri(fieldName), false, fieldName);
        createKoodi.setArvo(value);
        return createKoodi;
    }

    private void assertHenkilo(List<YhteyshenkiloTyyppi> yhteyshenkiloTyyppis) {
        assertEquals("contact person", 2, yhteyshenkiloTyyppis.size()); //not needed
        boolean ects = false;
        boolean contactPerson = false;
        for (YhteyshenkiloTyyppi tyyppi : yhteyshenkiloTyyppis) {
            switch (tyyppi.getHenkiloTyyppi()) {
                case ECTS_KOORDINAATTORI:
                    ects = true;
                    break;
                case YHTEYSHENKILO:
                    contactPerson = true;
                    break;
            }
        }

        assertTrue(ects);
        assertTrue(contactPerson);
    }

    private void assertOther(KoulutusTyyppi koulutus) {
    	
        assertNotNull("getKansainvalistyminen", ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA));
        assertEquals("kansainvalistyminen", ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.KANSAINVALISTYMINEN).getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.KANSAINVALISTYMINEN).getTeksti().get(0).getKieliKoodi());

        assertNotNull("getYhteistyoMuidenToimijoidenKanssa", ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA));
        assertEquals("yhteistyoMuidenToimijoidenKanssa", ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA).getTeksti().get(0).getKieliKoodi());

        assertNotNull("getSisalto", ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.SISALTO));
        assertEquals("koulutuksenSisalto", ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.SISALTO).getTeksti().get(0).getValue());
        assertEquals(LANGUAGE_FI, ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.SISALTO).getTeksti().get(0).getKieliKoodi());

        assertEquals(KOMOTO_OID, koulutus.getOid());

        //koulutuskoodi
        assertNull("koulutuskoodi object", koulutus.getKoulutusKoodi());

        assertNotNull("Koulutuksen kesto tyyppi", koulutus.getKesto());
        assertEquals("kesto", koulutus.getKesto().getArvo());
        assertEquals("kesto_tyyppi", koulutus.getKesto().getYksikko());
        assertEquals(DATE, koulutus.getKoulutuksenAlkamisPaiva());

        assertEquals(KoulutusasteTyyppi.AMMATTIKORKEAKOULUTUS, koulutus.getKoulutustyyppi());
        assertEquals(KOMO_ULKOINEN_TUNNISTE, koulutus.getKoulutusmoduuli().getUlkoinenTunniste());
        assertEquals(ORGANISAATIO_OID, koulutus.getTarjoaja());
        assertEquals(TarjontaTila.LUONNOS, koulutus.getTila());

        assertEquals(1, koulutus.getPohjakoulutusvaatimusKorkeakoulu().size());
        assertEquals(4, koulutus.getOpetuskieli().size());
        assertEquals(3, koulutus.getOpetusmuoto().size());
        assertEquals(2, koulutus.getTeemat().size());
        assertHenkilo(koulutus.getYhteyshenkiloTyyppi());

        assertNull("pohjakoulutusvaatimus", koulutus.getPohjakoulutusvaatimus());
        assertNull("KoulutusohjelmaKoodi", koulutus.getKoulutusohjelmaKoodi());
        // assertNull("koulutusaste", koulutus.getKoulutusaste());
        assertNull("painotus", ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.PAINOTUS));
        assertNull("nimi", koulutus.getNimi()); //???

        /*
         * KOMO data
         */
        assertNotNull(koulutus.getKoulutusmoduuli());
        assertEquals(LAAJUUS_ARVO, koulutus.getKoulutusmoduuli().getLaajuusarvoUri());
        assertEquals(KOMO_OID, koulutus.getKoulutusmoduuli().getOid());
        assertEquals(createUri(KOULUTUSKOODI), koulutus.getKoulutusmoduuli().getKoulutuskoodiUri());

    }
}