package fi.vm.sade.tarjonta.service.impl.resources.v1;

import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateTime;
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
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.SecurityAwareTestBase;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.impl.KoulutusSisaltyvyysDAOImpl;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.types.HenkiloTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("embedded-solr")
@Transactional()
public class KoulutusResourceImplV1APITest extends SecurityAwareTestBase {
    
    private static final Integer VUOSI = 2013;
    private static final String OPETUSAIKAS = "opetusaikas";
    private static final String OPETUSPAIKKAS = "opetuspaikkas";
    private static final String KAUSI_KOODI_URI = "kausi_k";
    private static final String LAAJUUSARVO = "laajuusarvo";
    private static final String LAAJUUSYKSIKKO = "laajuusyksikko";
    private static final String KOULUTUSOHJELMA = "koulutusohjelma";
    private static final String URI_KIELI_FI = "kieli_fi";
    private static final String LOCALE_FI = "FI";
    private static final String KOULUTUSKOODI = "koulutuskoodi";
    private static final String KOULUTUSASTE = "koulutusaste";
    private static final String KOULUTUSALA = "koulutusala";
    private static final String OPINTOALA = "opintoala";
    private static final String TUTKINTO = "tutkinto";
    private static final String TUTKINTONIMIKE = "tutkintonimike";
    private static final String AIHEES = "aihees";
    private static final String OPETUSKIELI = "opetuskieli";
    private static final String POHJAKOULUTUS = "pohjakoulutus";
    private static final String OPETUMUOTO = "opetusmuoto";
    private static final String AMMATTINIMIKE = "ammattinimike";
    private static final String EQF = "EQF";
    private static final String KOMO_OID = "komo_oid";
    private static final String KOMOTO_OID = "komoto_oid";
    private static final String ORGANISAATIO_OID = "1.2.3.4.5";
    private static final String ORGANISAATIO_NIMI = "organisaatio_nimi";
    private static final String TUNNISTE = "tunniste_txt";
    private static final String SUUNNITELTU_KESTO_VALUE = "10";
    private static final String SUUNNITELTU_KESTO = "suunniteltu_kesto";
    private static final String[] PERSON = {"henkilo_oid", "firstanames",
        "lastname", "Mr.", "oph@oph.fi", "12345678"};
    private final DateTime DATE = new DateTime(VUOSI, 1, 1, 1, 1);
    
    private static String toKoodiUriStr(final String type) {
        return type + "_uri";
    }
    
    private static KoodiV1RDTO toKoodiUri(final String type) {
        return new KoodiV1RDTO(type + "_uri", 1, null);
    }
    
    private static KoodiV1RDTO toMetaValue(final String value, String lang) {
        return new KoodiV1RDTO(lang, 1, value);
    }
    
    private static String toNimiValue(final String value, String lang) {
        return value + "_" + lang;
    }
    
    @Autowired
    TarjontaFixtures tarjontaFixtures;
    
    @Autowired
    KoulutusmoduuliDAO koulutusmoduuliDAO;
    
    @Autowired
    KoulutusSisaltyvyysDAOImpl koulutusSisaltyvyysDao;
    
    @Autowired
    KoulutusV1Resource koulutusResource;
    
    @Autowired
    OrganisaatioService organisaatioService;
    
    @Autowired
    KoodiService koodiService;
    
    @Autowired
    OidService oidService;
    
    @Before
    public void setup() throws OIDCreationException {
        KoodistoURI.KOODISTO_KIELI_URI = "kieli";
        
        Mockito.stub(organisaatioService.findByOid(ORGANISAATIO_OID)).toReturn(
                getOrganisaatio(ORGANISAATIO_OID));
        stubKoodi(koodiService, "kieli_fi", "FI");
        stubKoodi(koodiService, "koulutuskoodi_uri", "FI");
        stubKoodi(koodiService, "tutkinto_uri", "FI");
        stubKoodi(koodiService, "laajuusarvo_uri", "FI");
        stubKoodi(koodiService, "laajuusyksikko_uri", "FI");
        stubKoodi(koodiService, "koulutusaste_uri", "FI");
        stubKoodi(koodiService, "koulutusala_uri", "FI");
        stubKoodi(koodiService, "opintoala_uri", "FI");
        stubKoodi(koodiService, "tutkintonimike_uri", "FI");
        stubKoodi(koodiService, "aihees_uri", "FI");
        stubKoodi(koodiService, "opetusaikas_uri", "FI");
        stubKoodi(koodiService, "opetuspaikkas_uri", "FI");
        stubKoodi(koodiService, "opetuskieli_uri", "FI");
        stubKoodi(koodiService, "opetusmuoto_uri", "FI");
        stubKoodi(koodiService, "pohjakoulutus_uri", "FI");
        stubKoodi(koodiService, "suunniteltu_kesto_uri", "FI");
        stubKoodi(koodiService, "ammattinimike_uri", "FI");
        stubKoodi(koodiService, "EQF_uri", "FI");
        Mockito.stub(oidService.get(TarjontaOidType.KOMO)).toReturn("komo-oid");
        Mockito.stub(oidService.get(TarjontaOidType.KOMOTO)).toReturn("komoto-oid");
    }
    
    private OrganisaatioDTO getOrganisaatio(String organisaatioOid) {
        OrganisaatioDTO dto = new OrganisaatioDTO();
        dto.setOid(organisaatioOid);
        MonikielinenTekstiTyyppi mkt = new MonikielinenTekstiTyyppi();
        mkt.getTeksti().add(
                new MonikielinenTekstiTyyppi.Teksti(ORGANISAATIO_NIMI, "fi"));
        dto.setNimi(mkt);
        return dto;
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
        kt.setVersio(1);
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
    
    @Test
    public void testAPI() {
        super.printCurrentUser();
        
        KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();
        /*
         * KOMO data fields:
         */
        
        meta(dto.getKoulutusohjelma(), URI_KIELI_FI, toMetaValue("koulutusohjelma", URI_KIELI_FI));
        dto.getKoulutusohjelma()
                .getTekstis()
                .put(URI_KIELI_FI, toNimiValue("koulutusohjelma", URI_KIELI_FI));
        dto.getOrganisaatio().setOid(ORGANISAATIO_OID);
        dto.setKoulutusaste(toKoodiUri(KOULUTUSASTE));
        dto.setKoulutusala(toKoodiUri(KOULUTUSALA));
        dto.setOpintoala(toKoodiUri(OPINTOALA));
        dto.setTutkinto(toKoodiUri(TUTKINTO));
        dto.setEqf(toKoodiUri(EQF));
        dto.setTila(TarjontaTila.JULKAISTU);
        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO);
        dto.setTunniste(TUNNISTE);
        dto.setHinta(1.11);
        dto.setOpintojenMaksullisuus(Boolean.TRUE);
        dto.setKoulutuskoodi(toKoodiUri(KOULUTUSKOODI));
        dto.getKoulutuksenAlkamisPvms().add(DATE.toDate());
        
        koodiUrisMap(dto.getOpetusAikas(), URI_KIELI_FI, OPETUSAIKAS);
        koodiUrisMap(dto.getOpetusPaikkas(), URI_KIELI_FI, OPETUSPAIKKAS);
        
        koodiUrisMap(dto.getTutkintonimikes(), URI_KIELI_FI, TUTKINTONIMIKE);
        koodiUrisMap(dto.getAihees(), URI_KIELI_FI, AIHEES);
        koodiUrisMap(dto.getOpetuskielis(), URI_KIELI_FI, OPETUSKIELI);
        koodiUrisMap(dto.getOpetusmuodos(), URI_KIELI_FI, OPETUMUOTO);
        koodiUrisMap(dto.getAmmattinimikkeet(), URI_KIELI_FI, AMMATTINIMIKE);
        koodiUrisMap(dto.getPohjakoulutusvaatimukset(), URI_KIELI_FI, POHJAKOULUTUS);
        
        dto.setSuunniteltuKestoTyyppi(toKoodiUri(SUUNNITELTU_KESTO));
        dto.setSuunniteltuKestoArvo(SUUNNITELTU_KESTO_VALUE);
        
        dto.getYhteyshenkilos().add(
                new YhteyshenkiloTyyppi(PERSON[0], PERSON[1], PERSON[2],
                        PERSON[3], PERSON[4], PERSON[5], null,
                        HenkiloTyyppi.YHTEYSHENKILO));
        dto.setOpintojenLaajuusarvo(toKoodiUri(LAAJUUSARVO));
        dto.setOpintojenLaajuusyksikko(toKoodiUri(LAAJUUSYKSIKKO));
        
        ResultV1RDTO<KoulutusV1RDTO> v = koulutusResource
                .postKoulutus(dto);
        
        KoulutusKorkeakouluV1RDTO result = (KoulutusKorkeakouluV1RDTO) v.getResult();
        String oid = result.getKomotoOid();
        
        String strErrorKeys = "";
        if (v.getErrors() != null && !v.getErrors().isEmpty()) {
            
            for (ErrorV1RDTO d : v.getErrors()) {
                strErrorKeys += d.getErrorMessageKey() + " ";
            }
        }
        
        assertEquals("Validation errors keys : " + strErrorKeys, true, v.getErrors() != null ? v.getErrors().isEmpty() : true);
        
        assertNotNull("missing komoto oid", oid);
        
        ResultV1RDTO v1 = koulutusResource.findByOid(oid, false, false, null);
        result = (KoulutusKorkeakouluV1RDTO) v1.getResult();
        Assert.assertEquals(1, result.getYhteyshenkilos().size());

        // poista yht henkilö
        result.getYhteyshenkilos().clear();
        ResultV1RDTO<KoulutusV1RDTO> postKoulutus = koulutusResource.postKoulutus(result);
        Assert.assertEquals("koulutus update failed", false, postKoulutus.hasErrors());
        
        v1 = koulutusResource.findByOid(oid, false, false, null);
        result = (KoulutusKorkeakouluV1RDTO) v1.getResult();
        Assert.assertEquals(0, result.getYhteyshenkilos().size());
        
    }
    
    private static KoodiV1RDTO meta(final KoodiV1RDTO dto, final String kieli, final KoodiV1RDTO metaValue) {
        dto.setMeta(Maps.<String, KoodiV1RDTO>newHashMap());
        return dto.getMeta().put(kieli, metaValue);
    }
    
    private void koodiUrisMap(final KoodiUrisV1RDTO dto, final String kieliUri, final String fieldName) {
        meta(dto, URI_KIELI_FI, toKoodiUri(fieldName));
        
        if (dto.getUris() == null) {
            dto.setUris(Maps.<String, Integer>newHashMap());
        }
        
        dto.getUris().put(toKoodiUriStr(fieldName), 1);
    }
}
