package fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation;

import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.TestMockBase;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.impl.resources.v1.ConverterV1;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus;
import fi.vm.sade.tarjonta.service.search.KoodistoKoodi;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.service.search.Tarjoaja;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class HakukohdeValidatorTest extends TestMockBase {

    private static final int VUOSI_2014 = 2014;
    private static final int VUOSI_2015 = 2015;
    private static final String KOMOTO_OID1 = "A";
    private static final String KOMOTO_OID2 = "B";
    private static final String KOMOTO_OID3 = "C";

    private static final String KOULUTUS_A = "koulutus_123456";
    private static final String KOULUTUS_A_VERSIO = KOULUTUS_A + "#1";
    private static final String KOULUTUS_B = "koulutus_123457";
    private static final String KOULUTUS_B_VERSIO = KOULUTUS_B + "#1";
    private static final String KOULUTUS_C_VERSIO = "koulutus_987654#10";

    private static final String KAUSI_K = "kausi_k";
    private static final String KAUSI_S = "kausi_s";
    private static final String KAUSI_S_VERSIO = "kausi_s#1";

    @InjectMocks
    private HakukohdeValidator hakukohdeValidator;

    private ConverterV1 converterV1 = new ConverterV1();

    @Before
    public void before() {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        when(koulutusmoduuliToteutusDAO.findByOid("1.2.3.4.5")).thenReturn(komoto);
        when(hakukohdeDAO.findByTarjoajaHakuAndNimiUri(Matchers.anySet(), Matchers.anyString(), Matchers.anyString())).thenReturn(Lists.<Hakukohde>newArrayList());
    }

    @Test
    public void testIsValidKomotoSelectionSuccess() {
        KoulutuksetVastaus kv = new KoulutuksetVastaus();

        List<KoulutusPerustieto> list = Lists.newArrayList();
        list.add(perustieto(KOMOTO_OID1, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_S_VERSIO));
        list.add(perustieto(KOMOTO_OID2, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_A_VERSIO, VUOSI_2014, KAUSI_S));
        list.add(perustieto(KOMOTO_OID3, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_S));
        kv.setKoulutukset(list);

        ResultV1RDTO<ValitutKoulutuksetV1RDTO> dto = hakukohdeValidator.getValidKomotoSelection(kv);
        Map<String, Set<String>> result = dto.getResult().getOidConflictingWithOids();
        assertEquals(ResultStatus.OK, dto.getStatus());
        assertNotNull("result was null?", result.size());
        assertEquals(null, dto.getErrors());
        assertEquals(3, result.size());
        assertEquals(true, result.get(KOMOTO_OID1).isEmpty());
        assertEquals(true, result.get(KOMOTO_OID2).isEmpty());
        assertEquals(true, result.get(KOMOTO_OID3).isEmpty());

        /*
         * Korkeakoulutus:
         */
        list = Lists.newArrayList();
        list.add(perustieto(KOMOTO_OID1, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID2, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_B, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID3, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_C_VERSIO, VUOSI_2014, KAUSI_K));
        kv.setKoulutukset(list);

        dto = hakukohdeValidator.getValidKomotoSelection(kv);
        result = dto.getResult().getOidConflictingWithOids();
        assertEquals(ResultStatus.OK, dto.getStatus());
        assertNotNull("result was null?", result.size());
        assertEquals(null, dto.getErrors());
        assertEquals(3, result.size());
        assertEquals(true, result.get(KOMOTO_OID1).isEmpty());
        assertEquals(true, result.get(KOMOTO_OID2).isEmpty());
        assertEquals(true, result.get(KOMOTO_OID3).isEmpty());
    }

    @Test
    public void testIsValidKomotoSelectionFailKoulutus() {
        KoulutuksetVastaus kv = new KoulutuksetVastaus();

        List<KoulutusPerustieto> list = Lists.newArrayList();
        list.add(perustieto(KOMOTO_OID1, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID2, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID3, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_B_VERSIO, VUOSI_2014, KAUSI_K));
        kv.setKoulutukset(list);

        ResultV1RDTO<ValitutKoulutuksetV1RDTO> dto = hakukohdeValidator.getValidKomotoSelection(kv);
        Map<String, Set<String>> result = dto.getResult().getOidConflictingWithOids();

        assertEquals(ResultStatus.ERROR, dto.getStatus());
        assertEquals(1, dto.getErrors().size());
        assertEquals("hakukohde.luonti.virhe.koulutus", dto.getErrors().get(0).getErrorMessageKey());
        assertEquals(3, result.size());
        assertEquals(2, result.get(KOMOTO_OID3).size());
        assertEquals(1, result.get(KOMOTO_OID2).size());
        assertEquals(1, result.get(KOMOTO_OID1).size());

    }

    @Test
    public void testIsValidKomotoPohjakoulutusTarjoajaKoulutus() {
        KoulutuksetVastaus kv = new KoulutuksetVastaus();

        List<KoulutusPerustieto> list = Lists.newArrayList();
        Tarjoaja tarjoaja = new Tarjoaja();
        tarjoaja.setOid("t1");
        Tarjoaja tarjoaja2 = new Tarjoaja();
        tarjoaja2.setOid("t2");

        KoulutusPerustieto kp1 = perustieto(KOMOTO_OID1, ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_ALK_2018, KOULUTUS_A, VUOSI_2014, KAUSI_K);
        kp1.setPohjakoulutusvaatimus(null);
        kp1.setTarjoaja(tarjoaja);
        list.add(kp1);
        KoulutusPerustieto kp2 = perustieto(KOMOTO_OID2, ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_ALK_2018, KOULUTUS_A, VUOSI_2014, KAUSI_K);
        kp2.setPohjakoulutusvaatimus(null);
        kp2.setTarjoaja(tarjoaja);
        list.add(kp2);
        kv.setKoulutukset(list);

        ResultV1RDTO<ValitutKoulutuksetV1RDTO> dto = hakukohdeValidator.getValidKomotoSelection(kv);
        Map<String, Set<String>> result = dto.getResult().getOidConflictingWithOids();

        assertEquals(null, dto.getErrors());
        assertEquals(2, result.size());
        assertEquals(true, result.get(KOMOTO_OID1).isEmpty());
        assertEquals(true, result.get(KOMOTO_OID2).isEmpty());

        // another success
        list.clear();

        KoodistoKoodi kk1 = new KoodistoKoodi("yksi");
        kp1.setPohjakoulutusvaatimus(kk1);
        KoodistoKoodi kk2 = new KoodistoKoodi("yksi");
        kp2.setPohjakoulutusvaatimus(kk2);

        list.add(kp1);
        list.add(kp2);

        kv.setKoulutukset(list);
        dto = hakukohdeValidator.getValidKomotoSelection(kv);
        result = dto.getResult().getOidConflictingWithOids();

        assertEquals(null, dto.getErrors());
        assertEquals(2, result.size());
        assertEquals(true, result.get(KOMOTO_OID1).isEmpty());
        assertEquals(true, result.get(KOMOTO_OID2).isEmpty());

        // then error
        list.clear();

        kp1 = perustieto(KOMOTO_OID1, ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_K);
        kp1.setPohjakoulutusvaatimus(null);
        kp1.setTarjoaja(tarjoaja);
        list.add(kp1);
        kp2 = perustieto(KOMOTO_OID2, ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_K);
        kp2.setPohjakoulutusvaatimus(null);
        kp2.setTarjoaja(tarjoaja);
        list.add(kp2);

        kv.setKoulutukset(list);
        dto = hakukohdeValidator.getValidKomotoSelection(kv);
        result = dto.getResult().getOidConflictingWithOids();

        assertEquals(ResultStatus.ERROR, dto.getStatus());
        assertEquals(1, dto.getErrors().size());
        assertEquals("hakukohde.luonti.virhe.pohjakoulutusvaatimus", dto.getErrors().get(0).getErrorMessageKey());
        assertEquals(2, result.size());
        assertEquals(1, result.get(KOMOTO_OID2).size());
        assertEquals(1, result.get(KOMOTO_OID1).size());

        // then another error
        list.clear();

        kk1 = new KoodistoKoodi("yksi");
        kp1.setPohjakoulutusvaatimus(kk1);
        list.add(kp1);
        kk2 = new KoodistoKoodi("kaksi");
        kp2.setPohjakoulutusvaatimus(kk2);
        list.add(kp2);

        kv.setKoulutukset(list);
        dto = hakukohdeValidator.getValidKomotoSelection(kv);
        result = dto.getResult().getOidConflictingWithOids();

        assertEquals(ResultStatus.ERROR, dto.getStatus());
        assertEquals(1, dto.getErrors().size());
        assertEquals("hakukohde.luonti.virhe.pohjakoulutusvaatimus", dto.getErrors().get(0).getErrorMessageKey());
        assertEquals(2, result.size());
        assertEquals(1, result.get(KOMOTO_OID2).size());
        assertEquals(1, result.get(KOMOTO_OID1).size());

        // ----------- tarjoaja error
        list.clear();

        kk1 = new KoodistoKoodi("yksi");
        kp1.setPohjakoulutusvaatimus(kk1);
        kp1.setTarjoaja(tarjoaja);
        list.add(kp1);
        kk2 = new KoodistoKoodi("yksi");
        kp2.setPohjakoulutusvaatimus(kk2);
        kp2.setTarjoaja(tarjoaja2);
        list.add(kp2);

        kv.setKoulutukset(list);
        dto = hakukohdeValidator.getValidKomotoSelection(kv);
        result = dto.getResult().getOidConflictingWithOids();

        assertEquals(ResultStatus.ERROR, dto.getStatus());
        assertEquals(1, dto.getErrors().size());
        assertEquals("hakukohde.luonti.virhe.tarjoaja", dto.getErrors().get(0).getErrorMessageKey());
        assertEquals(2, result.size());
        assertEquals(1, result.get(KOMOTO_OID2).size());
        assertEquals(1, result.get(KOMOTO_OID1).size());

    }

    @Test
    public void testIsValidKomotoSelectionFailKoulutustyyppi() {
        KoulutuksetVastaus kv = new KoulutuksetVastaus();

        List<KoulutusPerustieto> list = Lists.newArrayList();
        list.add(perustieto(KOMOTO_OID1, ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID2, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID3, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_A_VERSIO, VUOSI_2014, KAUSI_K));
        kv.setKoulutukset(list);

        ResultV1RDTO<ValitutKoulutuksetV1RDTO> dto = hakukohdeValidator.getValidKomotoSelection(kv);
        Map<String, Set<String>> result = dto.getResult().getOidConflictingWithOids();

        assertEquals(ResultStatus.ERROR, dto.getStatus());
        assertEquals(1, dto.getErrors().size());
        assertEquals("hakukohde.luonti.virhe.tyyppi", dto.getErrors().get(0).getErrorMessageKey());
        assertEquals(3, result.size());
        assertEquals(2, result.get(KOMOTO_OID1).size());
        assertEquals(1, result.get(KOMOTO_OID2).size());
        assertEquals(1, result.get(KOMOTO_OID3).size());

    }

    @Test
    public void testIsValidKomotoSelectionFailKausi() {
        KoulutuksetVastaus kv = new KoulutuksetVastaus();

        List<KoulutusPerustieto> list = Lists.newArrayList();
        list.add(perustieto(KOMOTO_OID1, ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID2, ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID3, ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO, KOULUTUS_A_VERSIO, VUOSI_2014, KAUSI_S));

        kv.setKoulutukset(list);

        ResultV1RDTO<ValitutKoulutuksetV1RDTO> dto = hakukohdeValidator.getValidKomotoSelection(kv);
        Map<String, Set<String>> result = dto.getResult().getOidConflictingWithOids();

        assertEquals(ResultStatus.ERROR, dto.getStatus());
        assertEquals(1, dto.getErrors().size());
        assertEquals("hakukohde.luonti.virhe.kausi", dto.getErrors().get(0).getErrorMessageKey());
        assertEquals(3, result.size());
        assertEquals(1, result.get(KOMOTO_OID1).size());
        assertEquals(1, result.get(KOMOTO_OID2).size());
        assertEquals(2, result.get(KOMOTO_OID3).size());
    }

    @Test
    public void testIsValidKomotoSelectionFailVuosi() {
        KoulutuksetVastaus kv = new KoulutuksetVastaus();

        List<KoulutusPerustieto> list = Lists.newArrayList();
        list.add(perustieto(KOMOTO_OID1, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID2, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_A, VUOSI_2015, KAUSI_K));
        list.add(perustieto(KOMOTO_OID3, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_A_VERSIO, VUOSI_2015, KAUSI_K));

        kv.setKoulutukset(list);

        ResultV1RDTO<ValitutKoulutuksetV1RDTO> dto = hakukohdeValidator.getValidKomotoSelection(kv);
        Map<String, Set<String>> result = dto.getResult().getOidConflictingWithOids();

        assertEquals(ResultStatus.ERROR, dto.getStatus());
        assertEquals(1, dto.getErrors().size());
        assertEquals("hakukohde.luonti.virhe.vuosi", dto.getErrors().get(0).getErrorMessageKey());

        assertEquals(3, result.size());
        assertEquals(2, result.get(KOMOTO_OID1).size());
        assertEquals(1, result.get(KOMOTO_OID2).size());
        assertEquals(1, result.get(KOMOTO_OID3).size());
    }

    @Test
    public void testIsValidKomotoSelectionFailTila() {
        KoulutuksetVastaus kv = new KoulutuksetVastaus();

        List<KoulutusPerustieto> list = Lists.newArrayList();
        list.add(koulutusperustieto(KOMOTO_OID1, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_A, VUOSI_2015, KAUSI_K, TarjontaTila.POISTETTU));
        list.add(perustieto(KOMOTO_OID2, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_A_VERSIO, VUOSI_2015, KAUSI_K));
        list.add(perustieto(KOMOTO_OID3, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_B_VERSIO, VUOSI_2015, KAUSI_K));

        kv.setKoulutukset(list);

        ResultV1RDTO<ValitutKoulutuksetV1RDTO> dto = hakukohdeValidator.getValidKomotoSelection(kv);
        Map<String, Set<String>> result = dto.getResult().getOidConflictingWithOids();

        assertEquals(ResultStatus.ERROR, dto.getStatus());
        assertEquals(1, dto.getErrors().size());
        assertEquals("hakukohde.luonti.virhe.tila", dto.getErrors().get(0).getErrorMessageKey());

        assertEquals(3, result.size());
        assertEquals(1, result.get(KOMOTO_OID1).size());
        assertEquals(0, result.get(KOMOTO_OID2).size());
        assertEquals(0, result.get(KOMOTO_OID3).size());

        list = Lists.newArrayList();
        list.add(koulutusperustieto(KOMOTO_OID1, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_A, VUOSI_2015, KAUSI_K, TarjontaTila.KOPIOITU));
        list.add(koulutusperustieto(KOMOTO_OID2, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_B, VUOSI_2015, KAUSI_K, TarjontaTila.LUONNOS));
        list.add(koulutusperustieto(KOMOTO_OID3, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_C_VERSIO, VUOSI_2015, KAUSI_K, TarjontaTila.VALMIS));

        kv.setKoulutukset(list);
        dto = hakukohdeValidator.getValidKomotoSelection(kv);

        assertEquals("Found errors?", null, dto.getErrors());
        kv.setKoulutukset(list);
    }

    private static KoulutusPerustieto koulutusperustieto(
            String oid,
            ToteutustyyppiEnum toteutustyyppi,
            String koulutuskoodi,
            int vuosi,
            String kausiUri, TarjontaTila tila) {
        KoulutusPerustieto kp = new KoulutusPerustieto();
        kp.setKomotoOid(oid);
        kp.setToteutustyyppi(toteutustyyppi);
        kp.setKoulutusKoodi(new KoodistoKoodi(koulutuskoodi));
        kp.setKoulutuksenAlkamisVuosi(vuosi);
        kp.setKoulutuksenAlkamiskausiUri(new KoodistoKoodi(kausiUri));
        kp.setTila(tila);

        return kp;
    }

    private static KoulutusPerustieto perustieto(
            String oid,
            ToteutustyyppiEnum toteutustyyppi,
            String koulutuskoodi,
            int vuosi,
            String kausiUri) {
        KoulutusPerustieto kp = new KoulutusPerustieto();
        kp.setKomotoOid(oid);
        kp.setToteutustyyppi(toteutustyyppi);
        kp.setKoulutusKoodi(new KoodistoKoodi(koulutuskoodi));
        kp.setKoulutuksenAlkamisVuosi(vuosi);
        kp.setKoulutuksenAlkamiskausiUri(new KoodistoKoodi(kausiUri));
        kp.setTila(TarjontaTila.VALMIS);

        return kp;
    }

    @Test
    public void thatPisterajatPrecisionIsValidated() {
        ValintakoeV1RDTO valintakoe = getValintakoe();

        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertFalse(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_NOT_VALID_TYPE));

        getPaasykoepisterajat(valintakoe).setAlinPistemaara(new BigDecimal("4.555"));
        getPaasykoepisterajat(valintakoe).setYlinPistemaara(new BigDecimal("9.55"));
        getPaasykoepisterajat(valintakoe).setAlinHyvaksyttyPistemaara(null);
        messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_NOT_VALID_TYPE));

        getPaasykoepisterajat(valintakoe).setAlinPistemaara(new BigDecimal("4.55"));
        getPaasykoepisterajat(valintakoe).setYlinPistemaara(new BigDecimal("9.555"));
        getPaasykoepisterajat(valintakoe).setAlinHyvaksyttyPistemaara(null);
        messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_NOT_VALID_TYPE));

        getPaasykoepisterajat(valintakoe).setAlinPistemaara(new BigDecimal("4.55"));
        getPaasykoepisterajat(valintakoe).setYlinPistemaara(new BigDecimal("9.55"));
        getPaasykoepisterajat(valintakoe).setAlinHyvaksyttyPistemaara(new BigDecimal("4.555"));
        messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_NOT_VALID_TYPE));
    }

    private ArrayList<ValintakoeV1RDTO> getValintkoeAsList(ValintakoeV1RDTO valintakoe) {
        return new ArrayList<ValintakoeV1RDTO>(Arrays.asList(valintakoe));
    }

    @Test
    public void thatRestrictionsAreValidated() {
        ValintakoeV1RDTO valintakoe = getValintakoe();

        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertFalse(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_NOT_VALID));

        validateYlinPistemaaraOverTen();
        validateYlinPistemaaraSumOverTen();
        validateLisapisteetAlinHyvaksyttyPistemaaraLessThanAlinPistemaara();
        validateLisapisteetAlinPistemaaraGreaterThanYlinPistemaara();
        validatePaasykoeAlinHyvaksyttyPistemaaraLessThanAlinPistemaara();
        validatePaasykoeAlinPistemaaraGreaterThanYlinPistemaara();
        validateKokonaispisteetAlinHyvaksyttyLessThanAlimmatHyvaksytytPisterajat();
        validateKokonaispisteetAlinHyvaksyttyGreaterThanYlimmatPisterajat();
    }

    @Test
    public void thatPaasykoekuvauksetAreValidated() {
        ValintakoeV1RDTO valintakoe = getValintakoe();

        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertFalse(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_PAASYKOE_DATA_MISSING));

        valintakoe.getKuvaukset().put("kieli_fi", "");
        messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertFalse(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_PAASYKOE_DATA_MISSING));

        valintakoe.getKuvaukset().put("kieli_en", "");
        messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_PAASYKOE_DATA_MISSING));

        valintakoe.getKuvaukset().remove("kieli_fi");
        valintakoe.getKuvaukset().remove("kieli_en");

        messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_PAASYKOE_DATA_MISSING));
    }

    @Test
    public void thatLisanaytotAreValidated() {
        ValintakoeV1RDTO valintakoe = getValintakoe();

        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertFalse(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_LISANAYTOT_DATA_MISSING));

        valintakoe.getLisanaytot().put("kieli_fi", "");
        messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertFalse(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_LISANAYTOT_DATA_MISSING));

        valintakoe.getLisanaytot().put("kieli_en", "");
        messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_LISANAYTOT_DATA_MISSING));

        valintakoe.getLisanaytot().remove("kieli_fi");
        valintakoe.getLisanaytot().remove("kieli_en");

        messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_LISANAYTOT_DATA_MISSING));
    }


    @Test
    public void thatDuplicateHakukohdeCheckDoesNotBlockUpdatingHakukohde() {
        HakukohdeV1RDTO hakukohdeDTO = new HakukohdeV1RDTO();
        hakukohdeDTO = converterV1.setDefaultValues(hakukohdeDTO);
        hakukohdeDTO.setOid("hakukohdeOid");
        hakukohdeDTO.setToteutusTyyppi(ToteutustyyppiEnum.LUKIOKOULUTUS);
        hakukohdeDTO.setHakuOid("hakuOid");
        hakukohdeDTO.setHakukohteenNimiUri("hakukohdeNimi");
        hakukohdeDTO.getHakukohdeKoulutusOids().add("komotoOid");
        hakukohdeDTO.setTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.VALMIS);

        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setOid("komotoOid");

        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setOid("hakukohdeOid");
        hakukohde.setHakukohdeNimi("hakukohdeNimi");
        hakukohde.setTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.JULKAISTU);

        Haku haku = new Haku();
        haku.setOid("hakuOid");
        hakukohde.setHaku(haku);

        komoto.addHakukohde(hakukohde);

        when(koulutusmoduuliToteutusDAO.findByOid("komotoOid")).thenReturn(komoto);

        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateToisenAsteenHakukohde(hakukohdeDTO);

        assertFalse(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_DUPLIKAATTI));
    }

    @Test
    public void thatDuplicateHakukohdeCannotBeCreated() {
        HakukohdeV1RDTO hakukohdeDTO = new HakukohdeV1RDTO();
        hakukohdeDTO = converterV1.setDefaultValues(hakukohdeDTO);
        hakukohdeDTO.setToteutusTyyppi(ToteutustyyppiEnum.LUKIOKOULUTUS);
        hakukohdeDTO.setHakuOid("hakuOid");
        hakukohdeDTO.setHakukohteenNimiUri("hakukohdeNimi");
        hakukohdeDTO.getHakukohdeKoulutusOids().add("komotoOid");
        hakukohdeDTO.setTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.VALMIS);

        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setOid("komotoOid");

        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setOid("hakukohdeOid");
        hakukohde.setHakukohdeNimi("hakukohdeNimi");
        hakukohde.setTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.JULKAISTU);

        Haku haku = new Haku();
        haku.setOid("hakuOid");
        hakukohde.setHaku(haku);

        komoto.addHakukohde(hakukohde);

        when(koulutusmoduuliToteutusDAO.findByOid("komotoOid")).thenReturn(komoto);

        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateToisenAsteenHakukohde(hakukohdeDTO);

        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_DUPLIKAATTI));
    }

    @Test
    public void thatDuplicateHakukohdeCanBeCreatedWhenDuplicateIsPoistettu() {
        HakukohdeV1RDTO hakukohdeDTO = new HakukohdeV1RDTO();
        hakukohdeDTO = converterV1.setDefaultValues(hakukohdeDTO);
        hakukohdeDTO.setToteutusTyyppi(ToteutustyyppiEnum.LUKIOKOULUTUS);
        hakukohdeDTO.setHakuOid("hakuOid");
        hakukohdeDTO.setHakukohteenNimiUri("hakukohdeNimi");
        hakukohdeDTO.getHakukohdeKoulutusOids().add("komotoOid");
        hakukohdeDTO.setTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.VALMIS);

        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setOid("komotoOid");

        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setOid("hakukohdeOid");
        hakukohde.setHakukohdeNimi("hakukohdeNimi");
        hakukohde.setTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.POISTETTU);

        Haku haku = new Haku();
        haku.setOid("hakuOid");
        hakukohde.setHaku(haku);

        komoto.addHakukohde(hakukohde);

        when(koulutusmoduuliToteutusDAO.findByOid("komotoOid")).thenReturn(komoto);

        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateToisenAsteenHakukohde(hakukohdeDTO);

        assertFalse(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_DUPLIKAATTI));
    }

    @Test
    public void thatDuplicateHakukohdeCanBeCreatedWhenDuplicateIsPeruttu() {
        HakukohdeV1RDTO hakukohdeDTO = new HakukohdeV1RDTO();
        hakukohdeDTO = converterV1.setDefaultValues(hakukohdeDTO);
        hakukohdeDTO.setToteutusTyyppi(ToteutustyyppiEnum.LUKIOKOULUTUS);
        hakukohdeDTO.setHakuOid("hakuOid");
        hakukohdeDTO.setHakukohteenNimiUri("hakukohdeNimi");
        hakukohdeDTO.getHakukohdeKoulutusOids().add("komotoOid");
        hakukohdeDTO.setTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.VALMIS);

        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setOid("komotoOid");

        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setOid("hakukohdeOid");
        hakukohde.setHakukohdeNimi("hakukohdeNimi");
        hakukohde.setTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.PERUTTU);

        Haku haku = new Haku();
        haku.setOid("hakuOid");
        hakukohde.setHaku(haku);

        komoto.addHakukohde(hakukohde);

        when(koulutusmoduuliToteutusDAO.findByOid("komotoOid")).thenReturn(komoto);

        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateToisenAsteenHakukohde(hakukohdeDTO);

        assertFalse(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_DUPLIKAATTI));
    }

    private void validateKokonaispisteetAlinHyvaksyttyGreaterThanYlimmatPisterajat() {
        ValintakoeV1RDTO valintakoe = getValintakoe();
        getPaasykoepisterajat(valintakoe).setYlinPistemaara(new BigDecimal("2.00"));
        getLisapisteetPisterajat(valintakoe).setYlinPistemaara(new BigDecimal("2.00"));
        getKokonaispisterajat(valintakoe).setAlinHyvaksyttyPistemaara(new BigDecimal("5.00"));
        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_KOKONAISPISTEET_NOT_VALID));
    }

    private void validateKokonaispisteetAlinHyvaksyttyLessThanAlimmatHyvaksytytPisterajat() {
        ValintakoeV1RDTO valintakoe = getValintakoe();
        getPaasykoepisterajat(valintakoe).setAlinHyvaksyttyPistemaara(new BigDecimal("1.00"));
        getLisapisteetPisterajat(valintakoe).setAlinHyvaksyttyPistemaara(new BigDecimal("1.00"));
        getKokonaispisterajat(valintakoe).setAlinHyvaksyttyPistemaara(new BigDecimal("1.00"));
        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_KOKONAISPISTEET_NOT_VALID));
    }

    private void validatePaasykoeAlinPistemaaraGreaterThanYlinPistemaara() {
        ValintakoeV1RDTO valintakoe = getValintakoe();
        getPaasykoepisterajat(valintakoe).setAlinPistemaara(new BigDecimal("2.00"));
        getPaasykoepisterajat(valintakoe).setYlinPistemaara(new BigDecimal("1.00"));
        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_NOT_VALID));
    }

    private void validatePaasykoeAlinHyvaksyttyPistemaaraLessThanAlinPistemaara() {
        ValintakoeV1RDTO valintakoe = getValintakoe();
        getPaasykoepisterajat(valintakoe).setAlinPistemaara(new BigDecimal("2.00"));
        getPaasykoepisterajat(valintakoe).setAlinHyvaksyttyPistemaara(new BigDecimal("1.00"));
        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_NOT_VALID));
    }

    private void validateLisapisteetAlinPistemaaraGreaterThanYlinPistemaara() {
        ValintakoeV1RDTO valintakoe = getValintakoe();
        getLisapisteetPisterajat(valintakoe).setAlinPistemaara(new BigDecimal("2.00"));
        getLisapisteetPisterajat(valintakoe).setYlinPistemaara(new BigDecimal("1.00"));
        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_NOT_VALID));
    }

    private void validateLisapisteetAlinHyvaksyttyPistemaaraLessThanAlinPistemaara() {
        ValintakoeV1RDTO valintakoe = getValintakoe();
        getLisapisteetPisterajat(valintakoe).setAlinPistemaara(new BigDecimal("2.00"));
        getLisapisteetPisterajat(valintakoe).setAlinHyvaksyttyPistemaara(new BigDecimal("1.00"));
        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_NOT_VALID));
    }

    private void validateYlinPistemaaraOverTen() {
        ValintakoeV1RDTO valintakoe = getValintakoe();
        getPaasykoepisterajat(valintakoe).setYlinPistemaara(new BigDecimal("10.1"));
        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_NOT_VALID));
    }

    private void validateYlinPistemaaraSumOverTen() {
        ValintakoeV1RDTO valintakoe = getValintakoe();
        getPaasykoepisterajat(valintakoe).setYlinPistemaara(new BigDecimal("6.00"));
        getLisapisteetPisterajat(valintakoe).setYlinPistemaara(new BigDecimal("6.00"));
        List<HakukohdeValidationMessages> messages = hakukohdeValidator.validateValintakokees(getValintkoeAsList(valintakoe));
        assertTrue(messages.contains(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_NOT_VALID));
    }

    private ValintakoeV1RDTO getValintakoe() {
        ValintakoeV1RDTO valintakoe = new ValintakoeV1RDTO();
        List<ValintakoePisterajaV1RDTO> pisterajat = getValidPisterajat();
        valintakoe.setPisterajat(pisterajat);
        valintakoe.setValintakoeNimi("valintakoeNimi");
        valintakoe.setKuvaukset(getValidKuvaukset());
        valintakoe.setLisanaytot(getValidLisanaytot());
        valintakoe.setValintakoeAjankohtas(getValintakoeAjankohtas());
        return valintakoe;
    }

    private List<ValintakoeAjankohtaRDTO> getValintakoeAjankohtas() {
        ValintakoeAjankohtaRDTO ajankohta = new ValintakoeAjankohtaRDTO();
        ajankohta.setAlkaa(new Date());
        ajankohta.setLoppuu(new Date());
        ajankohta.setOsoite(new OsoiteRDTO());
        ajankohta.setLisatiedot("Lisatiedot");
        return Arrays.asList(ajankohta);
    }

    private Map<String, String> getValidLisanaytot() {
        Map<String, String> lisanaytot = new HashMap<String, String>();
        lisanaytot.put("kieli_fi", "lisanaytto");
        lisanaytot.put("kieli_en", "additional stuff");
        return lisanaytot;
    }

    private Map<String, String> getValidKuvaukset() {
        Map<String, String> kuvaukset = new HashMap<String, String>();
        kuvaukset.put("kieli_fi", "kuvaus");
        kuvaukset.put("kieli_en", "description");
        return kuvaukset;
    }

    private List<ValintakoePisterajaV1RDTO> getValidPisterajat() {
        ValintakoePisterajaV1RDTO paasykoePisterajat = new ValintakoePisterajaV1RDTO();
        paasykoePisterajat.setPisterajatyyppi(ValintakoePisterajaV1RDTO.PAASYKOE);
        paasykoePisterajat.setAlinPistemaara(new BigDecimal("0.00"));
        paasykoePisterajat.setYlinPistemaara(new BigDecimal("4.00"));
        paasykoePisterajat.setAlinHyvaksyttyPistemaara(new BigDecimal("2.00"));

        ValintakoePisterajaV1RDTO lisapisteetPisterajat = new ValintakoePisterajaV1RDTO();
        lisapisteetPisterajat.setPisterajatyyppi(ValintakoePisterajaV1RDTO.LISAPISTEET);
        lisapisteetPisterajat.setAlinPistemaara(new BigDecimal("0.00"));
        lisapisteetPisterajat.setYlinPistemaara(new BigDecimal("6.00"));
        lisapisteetPisterajat.setAlinHyvaksyttyPistemaara(new BigDecimal("2.00"));

        ValintakoePisterajaV1RDTO kokonaispisteetPisterajat = new ValintakoePisterajaV1RDTO();
        kokonaispisteetPisterajat.setPisterajatyyppi(ValintakoePisterajaV1RDTO.KOKONAISPISTEET);
        kokonaispisteetPisterajat.setAlinHyvaksyttyPistemaara(new BigDecimal("4.00"));

        return Arrays.asList(paasykoePisterajat, lisapisteetPisterajat, kokonaispisteetPisterajat);
    }

    private ValintakoePisterajaV1RDTO getPaasykoepisterajat(ValintakoeV1RDTO valintakoe) {
        return valintakoe.getPisterajat().get(0);
    }

    private ValintakoePisterajaV1RDTO getLisapisteetPisterajat(ValintakoeV1RDTO valintakoe) {
        return valintakoe.getPisterajat().get(1);
    }

    private ValintakoePisterajaV1RDTO getKokonaispisterajat(ValintakoeV1RDTO valintakoe) {
        return valintakoe.getPisterajat().get(2);
    }

    @Test
    public void thatPainokerroinIsValidated() {
        HakukohdeV1RDTO hakukohdeDTO = createhakukohde();

        hakukohdeDTO.getPainotettavatOppiaineet().add(createPainokerroin(null, "oppiaineUri"));
        hakukohdeDTO.getPainotettavatOppiaineet().add(createPainokerroin(new BigDecimal(0), "oppiaineUri"));
        hakukohdeDTO.getPainotettavatOppiaineet().add(createPainokerroin(new BigDecimal(21), "oppiaineUri"));
        hakukohdeDTO.getPainotettavatOppiaineet().add(createPainokerroin(new BigDecimal("5.555"), "oppiaineUri"));

        List<HakukohdeValidationMessages> validationMessages = hakukohdeValidator.validateToisenAsteenHakukohde(hakukohdeDTO);

        assertTrue(validationMessages.size() == 4);
        assertEquals(HakukohdeValidationMessages.HAKUKOHDE_PAINOTETTAVA_OPPIAINE_PAINOKERROIN_MISSING, validationMessages.get(0));
        assertEquals(HakukohdeValidationMessages.HAKUKOHDE_PAINOTETTAVA_OPPIAINE_PAINOKERROIN_RANGE, validationMessages.get(1));
        assertEquals(HakukohdeValidationMessages.HAKUKOHDE_PAINOTETTAVA_OPPIAINE_PAINOKERROIN_RANGE, validationMessages.get(2));
        assertEquals(HakukohdeValidationMessages.HAKUKOHDE_PAINOTETTAVA_OPPIAINE_PAINOKERROIN_RANGE, validationMessages.get(3));

        hakukohdeDTO.getPainotettavatOppiaineet().clear();
        hakukohdeDTO.getPainotettavatOppiaineet().add(createPainokerroin(new BigDecimal(1), "oppiaineUri"));
        hakukohdeDTO.getPainotettavatOppiaineet().add(createPainokerroin(new BigDecimal(20), "oppiaineUri"));

        validationMessages = hakukohdeValidator.validateToisenAsteenHakukohde(hakukohdeDTO);

        assertTrue(validationMessages.isEmpty());
    }

    private HakukohdeV1RDTO createhakukohde() {
        HakukohdeV1RDTO hakukohdeDTO = new HakukohdeV1RDTO();
        hakukohdeDTO = converterV1.setDefaultValues(hakukohdeDTO);
        hakukohdeDTO.setOid("3.2.1");
        hakukohdeDTO.setToteutusTyyppi(ToteutustyyppiEnum.LUKIOKOULUTUS);
        hakukohdeDTO.setTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.JULKAISTU);
        hakukohdeDTO.setHakuOid("1.2.3");
        hakukohdeDTO.setHakukohteenNimiUri("nimi_uri");
        hakukohdeDTO.setHakukohdeKoulutusOids(Arrays.asList("1.2.3.4.5"));
        return hakukohdeDTO;
    }

    @Test
    public void thatOppiaineUriIsValidated() {
        HakukohdeV1RDTO hakukohdeDTO = createhakukohde();

        hakukohdeDTO.getPainotettavatOppiaineet().add(createPainokerroin(new BigDecimal(1), ""));
        hakukohdeDTO.getPainotettavatOppiaineet().add(createPainokerroin(new BigDecimal(1), null));

        List<HakukohdeValidationMessages> validationMessages = hakukohdeValidator.validateToisenAsteenHakukohde(hakukohdeDTO);

        assertTrue(validationMessages.size() == 2);
        assertEquals(HakukohdeValidationMessages.HAKUKOHDE_PAINOTETTAVA_OPPIAINE_OPPIAINE_MISSING, validationMessages.get(0));
        assertEquals(HakukohdeValidationMessages.HAKUKOHDE_PAINOTETTAVA_OPPIAINE_OPPIAINE_MISSING, validationMessages.get(1));

        hakukohdeDTO.getPainotettavatOppiaineet().clear();
        hakukohdeDTO.getPainotettavatOppiaineet().add(createPainokerroin(new BigDecimal(1), "oppiaineUri"));

        validationMessages = hakukohdeValidator.validateToisenAsteenHakukohde(hakukohdeDTO);

        assertTrue(validationMessages.isEmpty());
    }

    private PainotettavaOppiaineV1RDTO createPainokerroin(BigDecimal value, String oppiaineUri) {
        PainotettavaOppiaineV1RDTO painotettavaOppiaineDTO = new PainotettavaOppiaineV1RDTO();
        painotettavaOppiaineDTO.setPainokerroin(value);
        painotettavaOppiaineDTO.setOppiaineUri(oppiaineUri);
        return painotettavaOppiaineDTO;
    }
}
