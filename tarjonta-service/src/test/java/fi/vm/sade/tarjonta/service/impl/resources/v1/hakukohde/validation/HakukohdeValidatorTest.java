package fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation;

import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValitutKoulutuksetV1RDTO;
import fi.vm.sade.tarjonta.service.search.KoodistoKoodi;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author jani
 */
public class HakukohdeValidatorTest {

    public HakukohdeValidatorTest() {
    }

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

    /**
     * Test of isValidKomotoSelection method, of class HakukohdeValidator.
     */
    @Test
    public void testIsValidKomotoSelectionSuccess() {
        KoulutuksetVastaus kv = new KoulutuksetVastaus();

        /*
         * Common education:
         */
        List<KoulutusPerustieto> list = Lists.<KoulutusPerustieto>newArrayList();
        list.add(perustieto(KOMOTO_OID1, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_S_VERSIO));
        list.add(perustieto(KOMOTO_OID2, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_A_VERSIO, VUOSI_2014, KAUSI_S));
        list.add(perustieto(KOMOTO_OID3, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_S));
        kv.setKoulutukset(list);

        ResultV1RDTO<ValitutKoulutuksetV1RDTO> dto = HakukohdeValidator.getValidKomotoSelection(kv);
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
        list = Lists.<KoulutusPerustieto>newArrayList();
        list.add(perustieto(KOMOTO_OID1, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID2, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_B, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID3, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_C_VERSIO, VUOSI_2014, KAUSI_K));
        kv.setKoulutukset(list);

        dto = HakukohdeValidator.getValidKomotoSelection(kv);
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

        List<KoulutusPerustieto> list = Lists.<KoulutusPerustieto>newArrayList();
        list.add(perustieto(KOMOTO_OID1, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID2, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID3, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_B_VERSIO, VUOSI_2014, KAUSI_K));
        kv.setKoulutukset(list);

        ResultV1RDTO<ValitutKoulutuksetV1RDTO> dto = HakukohdeValidator.getValidKomotoSelection(kv);
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
    public void testIsValidKomotoSelectionFailKoulutustyyppi() {
        KoulutuksetVastaus kv = new KoulutuksetVastaus();

        List<KoulutusPerustieto> list = Lists.<KoulutusPerustieto>newArrayList();
        list.add(perustieto(KOMOTO_OID1, ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID2, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID3, ToteutustyyppiEnum.AMMATTITUTKINTO, KOULUTUS_A_VERSIO, VUOSI_2014, KAUSI_K));
        kv.setKoulutukset(list);

        ResultV1RDTO<ValitutKoulutuksetV1RDTO> dto = HakukohdeValidator.getValidKomotoSelection(kv);
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

        List<KoulutusPerustieto> list = Lists.<KoulutusPerustieto>newArrayList();
        list.add(perustieto(KOMOTO_OID1, ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID2, ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID3, ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO, KOULUTUS_A_VERSIO, VUOSI_2014, KAUSI_S));

        kv.setKoulutukset(list);

        ResultV1RDTO<ValitutKoulutuksetV1RDTO> dto = HakukohdeValidator.getValidKomotoSelection(kv);
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

        List<KoulutusPerustieto> list = Lists.<KoulutusPerustieto>newArrayList();
        list.add(perustieto(KOMOTO_OID1, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_A, VUOSI_2014, KAUSI_K));
        list.add(perustieto(KOMOTO_OID2, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_A, VUOSI_2015, KAUSI_K));
        list.add(perustieto(KOMOTO_OID3, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_A_VERSIO, VUOSI_2015, KAUSI_K));

        kv.setKoulutukset(list);

        ResultV1RDTO<ValitutKoulutuksetV1RDTO> dto = HakukohdeValidator.getValidKomotoSelection(kv);
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

        List<KoulutusPerustieto> list = Lists.<KoulutusPerustieto>newArrayList();
        list.add(koulutusperustieto(KOMOTO_OID1, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_A, VUOSI_2015, KAUSI_K, TarjontaTila.POISTETTU));
        list.add(perustieto(KOMOTO_OID2, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_A_VERSIO, VUOSI_2015, KAUSI_K));
        list.add(perustieto(KOMOTO_OID3, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_B_VERSIO, VUOSI_2015, KAUSI_K));

        kv.setKoulutukset(list);

        ResultV1RDTO<ValitutKoulutuksetV1RDTO> dto = HakukohdeValidator.getValidKomotoSelection(kv);
        Map<String, Set<String>> result = dto.getResult().getOidConflictingWithOids();

        assertEquals(ResultStatus.ERROR, dto.getStatus());
        assertEquals(1, dto.getErrors().size());
        assertEquals("hakukohde.luonti.virhe.tila", dto.getErrors().get(0).getErrorMessageKey());

        assertEquals(3, result.size());
        assertEquals(1, result.get(KOMOTO_OID1).size());
        assertEquals(0, result.get(KOMOTO_OID2).size());
        assertEquals(0, result.get(KOMOTO_OID3).size());

        list = Lists.<KoulutusPerustieto>newArrayList();
        list.add(koulutusperustieto(KOMOTO_OID1, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_A, VUOSI_2015, KAUSI_K, TarjontaTila.KOPIOITU));
        list.add(koulutusperustieto(KOMOTO_OID2, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_B, VUOSI_2015, KAUSI_K, TarjontaTila.LUONNOS));
        list.add(koulutusperustieto(KOMOTO_OID3, ToteutustyyppiEnum.KORKEAKOULUTUS, KOULUTUS_C_VERSIO, VUOSI_2015, KAUSI_K, TarjontaTila.VALMIS));

        kv.setKoulutukset(list);
        dto = HakukohdeValidator.getValidKomotoSelection(kv);

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
}
