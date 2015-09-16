package fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class KoulutusValidatorTest {

    private static final Koulutusmoduuli KOULUTUS_OHJELMA = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);

    @Test
    public void testTunniste() {

        KoulutusKorkeakouluV1RDTO kk = new KoulutusKorkeakouluV1RDTO();
        kk.setTunniste("123456789012345678901234567890123456");

        ResultV1RDTO r = new ResultV1RDTO();
        KoulutusValidator.validateTunniste(kk, r);

        final KoulutusValidationMessages tunnisteError = KoulutusValidationMessages.KOULUTUS_TUNNISTE_LENGTH;

        org.junit.Assert.assertTrue(tunnisteError.lower(), r.hasErrors());
        assertErrorExist(r.getErrors(), tunnisteError);

        kk.setTunniste("12345678901234567890123456789012345");
        r = new ResultV1RDTO();
        KoulutusValidator.validateTunniste(kk, r);
        org.junit.Assert.assertFalse(tunnisteError.lower(), r.hasErrors());

        kk.setTunniste("");
        r = new ResultV1RDTO();
        KoulutusValidator.validateTunniste(kk, r);
        org.junit.Assert.assertFalse(tunnisteError.lower(), r.hasErrors());

        kk.setTunniste(null);
        r = new ResultV1RDTO();
        KoulutusValidator.validateTunniste(kk, r);
        org.junit.Assert.assertFalse(tunnisteError.lower(), r.hasErrors());
    }

    @Test
    public void testValidationLukioNullObject() {
        ResultV1RDTO result = new ResultV1RDTO();
        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(null, KOULUTUS_OHJELMA, result);
        org.junit.Assert.assertTrue("errors", v.hasErrors());
        org.junit.Assert.assertEquals("errors count", 1, v.getErrors().size());
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_INPUT_OBJECT_MISSING);

        KoulutusLukioV1RDTO dto = new KoulutusLukioV1RDTO();
        dto.setTila(null);
        dto.setOrganisaatio(null);
        dto.setEqf(null);
        dto.setKoulutusala(null);
        dto.setKoulutuskoodi(null);
        dto.setOpintojenLaajuusarvo(null);
        dto.setOpintojenLaajuusyksikko(null);
        dto.setKoulutuslaji(null);
        dto.setTutkintonimike(null);
        dto.setPohjakoulutusvaatimus(null);
        dto.setKoulutuksenAlkamiskausi(null);
        dto.setKoulutuksenAlkamisvuosi(null);
        dto.setSuunniteltuKestoArvo(null);
        dto.setSuunniteltuKestoTyyppi(null);

        Map uris = Maps.<String, Integer>newHashMap();
        uris.put(null, null);
        dto.getOpetusmuodos().setUris(uris);
        dto.getOpetusAikas().setUris(uris);
        dto.getOpetusPaikkas().setUris(uris);
        dto.getOpetuskielis().setUris(uris);

        checkMissingErrors(KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, result), 19);
    }

    @Test
    public void testValidationLukioMissing() {
        KoulutusLukioV1RDTO dto = new KoulutusLukioV1RDTO();

        /* 
         * Next test empty object:
         * ************************
         */
        dto.setTila(null);
        dto.setOrganisaatio(new OrganisaatioV1RDTO("", null, null));
        dto.setEqf(new KoodiV1RDTO());
        dto.setKoulutusala(new KoodiV1RDTO());
        dto.setKoulutuskoodi(new KoodiV1RDTO());
        dto.setKoulutusohjelma(new NimiV1RDTO());
        dto.setOpintojenLaajuusarvo(new KoodiV1RDTO());
        dto.setOpintojenLaajuusyksikko(new KoodiV1RDTO());
        dto.setKoulutuslaji(new KoodiV1RDTO());
        dto.setTutkintonimike(new KoodiV1RDTO());
        dto.setPohjakoulutusvaatimus(new KoodiV1RDTO());
        dto.setKoulutuksenAlkamiskausi(new KoodiV1RDTO());
        dto.setKoulutuksenAlkamisvuosi(-1);
        dto.setSuunniteltuKestoArvo("");
        dto.setSuunniteltuKestoTyyppi(new KoodiV1RDTO());

        Map uris = Maps.<String, Integer>newHashMap();
        uris.put(null, null);
        dto.getOpetusmuodos().setUris(uris);
        dto.getOpetusAikas().setUris(uris);
        dto.getOpetusPaikkas().setUris(uris);
        dto.getOpetuskielis().setUris(uris);

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO());
        checkMissingErrors(v, 18);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_TILA_ENUM_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_INVALID);

        /* 
         * Next test an empty and an invalid version:
         * ******************************************
         */
        dto.setTila(TarjontaTila.VALMIS);
        dto.getOrganisaatio().setOid("1");
        dto.setEqf(new KoodiV1RDTO("", 0, null));
        dto.setKoulutusala(new KoodiV1RDTO("", 0, null));
        dto.setKoulutuskoodi(new KoodiV1RDTO("", 0, null));
        dto.getKoulutusohjelma().setUri("");
        dto.getKoulutusohjelma().setVersio(0);
        dto.setOpintojenLaajuusarvo(new KoodiV1RDTO("", 0, null));
        dto.setOpintojenLaajuusyksikko(new KoodiV1RDTO("", 0, null));
        dto.setKoulutuslaji(new KoodiV1RDTO("", 0, null));
        dto.setTutkintonimike(new KoodiV1RDTO("", 0, null));
        dto.setPohjakoulutusvaatimus(new KoodiV1RDTO("", 0, null));
        dto.setKoulutuksenAlkamiskausi(new KoodiV1RDTO("", 0, null));
        dto.setKoulutuksenAlkamisvuosi(1999);
        dto.setSuunniteltuKestoArvo("");
        dto.setSuunniteltuKestoTyyppi(new KoodiV1RDTO("1", -1, null));

        uris = Maps.<String, Integer>newHashMap();
        uris.put("", -1);
        dto.getOpetusmuodos().setUris(uris);
        dto.getOpetusAikas().setUris(uris);
        dto.getOpetusPaikkas().setUris(uris);
        dto.getOpetuskielis().setUris(uris);

        v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO());
        checkMissingErrors(v, 16);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_INVALID);

        /* 
         * Validation success
         * ******************************************
         */
        dto.setTila(TarjontaTila.VALMIS);
        dto.getOrganisaatio().setOid("1");
        dto.setEqf(new KoodiV1RDTO("1", 1, null));
        dto.setKoulutusala(new KoodiV1RDTO("1", 1, null));
        dto.setKoulutuskoodi(new KoodiV1RDTO("1", 1, null));
        dto.getKoulutusohjelma().setUri("1");
        dto.getKoulutusohjelma().setVersio(1);
        dto.setOpintojenLaajuusarvo(new KoodiV1RDTO("1", 1, null));
        dto.setOpintojenLaajuusyksikko(new KoodiV1RDTO("1", 1, null));
        dto.setKoulutuslaji(new KoodiV1RDTO("1", 1, null));
        dto.setTutkintonimike(new KoodiV1RDTO("1", 1, null));
        dto.setPohjakoulutusvaatimus(new KoodiV1RDTO("1", 1, null));
        dto.setKoulutuksenAlkamiskausi(new KoodiV1RDTO("kausi_s", 1, null)); //must have a real pattern validated koodi uri!!!!
        dto.setKoulutuksenAlkamisvuosi(2000);
        dto.setSuunniteltuKestoArvo("1");
        dto.setSuunniteltuKestoTyyppi(new KoodiV1RDTO("1", 1, null));

        uris = Maps.<String, Integer>newHashMap();
        uris.put("1", 1);
        dto.getOpetusmuodos().setUris(uris);
        dto.getOpetusAikas().setUris(uris);
        dto.getOpetusPaikkas().setUris(uris);
        dto.getOpetuskielis().setUris(uris);

        v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO());
        org.junit.Assert.assertFalse("not success?", v.hasErrors());
    }

    @Test
    public void requireKoodiUriWithVersion() {
        Assert.assertFalse(KoulutusValidator.isValidKoodiUriWithVersion(null));
        Assert.assertFalse(KoulutusValidator.isValidKoodiUriWithVersion(new KoodiV1RDTO()));
        Assert.assertFalse(KoulutusValidator.isValidKoodiUriWithVersion(new KoodiV1RDTO("", -1, null)));
        Assert.assertFalse(KoulutusValidator.isValidKoodiUriWithVersion(new KoodiV1RDTO("", 1, null)));
        Assert.assertFalse(KoulutusValidator.isValidKoodiUriWithVersion(new KoodiV1RDTO(null, 1, null)));
        Assert.assertFalse(KoulutusValidator.isValidKoodiUriWithVersion(new KoodiV1RDTO("1", 0, null)));
        Assert.assertTrue(KoulutusValidator.isValidKoodiUriWithVersion(new KoodiV1RDTO("1", 1, null)));
    }

    @Test
    public void notNullStrOrEmpty() {
        Assert.assertFalse(KoulutusValidator.notNullStrOrEmpty(null));
        Assert.assertFalse(KoulutusValidator.notNullStrOrEmpty(""));
        Assert.assertFalse(KoulutusValidator.notNullStrOrEmpty(" "));
        Assert.assertFalse(KoulutusValidator.notNullStrOrEmpty("          "));
        Assert.assertTrue(KoulutusValidator.notNullStrOrEmpty(" 1 "));
        Assert.assertTrue(KoulutusValidator.notNullStrOrEmpty("1"));
    }

    @Test
    public void validateKoodiUris() {
        //use any error message, this test do not care the error message types.
        final KoulutusValidationMessages missing = KoulutusValidationMessages.KOULUTUS_NQF_MISSING;
        final KoulutusValidationMessages invalid = KoulutusValidationMessages.KOULUTUS_NQF_INVALID;
        Assert.assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO(), null, missing, invalid, null));

        KoodiUrisV1RDTO dto = new KoodiUrisV1RDTO();
        Assert.assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO(), dto, missing, invalid, null));

        //set empty map (size of zero)
        Map<String, Integer> uris = Maps.newHashMap();
        dto.setUris(uris);

        //no limit
        Assert.assertTrue(KoulutusValidator.validateKoodiUris(new ResultV1RDTO(), dto, missing, invalid, null));
        //limit == uris.size
        Assert.assertTrue(KoulutusValidator.validateKoodiUris(new ResultV1RDTO(), dto, missing, invalid, 0));
        //limit > uris.size
        Assert.assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO(), dto, missing, invalid, 1));

        uris.put(null, null);
        dto.setUris(uris);
        Assert.assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO(), dto, missing, invalid, null));

        uris.put(null, -1);
        dto.setUris(uris);
        Assert.assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO(), dto, missing, invalid, null));

        uris = Maps.newHashMap();
        uris.put("", 0);
        dto.setUris(uris);
        Assert.assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO(), dto, missing, invalid, null));

        uris = Maps.newHashMap();
        uris.put("", 1);
        dto.setUris(uris);
        Assert.assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO(), dto, missing, invalid, null));

        uris = Maps.newHashMap();
        uris.put("", 0);
        dto.setUris(uris);
        Assert.assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO(), dto, missing, invalid, null));

        uris = Maps.newHashMap();
        uris.put("1", 1);
        uris.put("", -1);
        dto.setUris(uris);
        Assert.assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO(), dto, missing, invalid, null));

        uris = Maps.newHashMap();
        uris.put("1", 1);
        uris.put("2", 2);
        dto.setUris(uris);
        Assert.assertTrue(KoulutusValidator.validateKoodiUris(new ResultV1RDTO(), dto, missing, invalid, 1));
        Assert.assertTrue(KoulutusValidator.validateKoodiUris(new ResultV1RDTO(), dto, missing, invalid, 2));
        Assert.assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO(), dto, missing, invalid, 3));

    }

    private void checkMissingErrors(ResultV1RDTO<KoulutusV1RDTO> v, int eCount) {
        org.junit.Assert.assertTrue("errors", v.hasErrors());
//
//        for (ErrorV1RDTO e : v.getErrors()) {
//            System.out.println(e.getErrorMessageKey());
//        }
//        System.out.println("--------------- : " + eCount);
        org.junit.Assert.assertEquals("errors count", eCount, v.getErrors().size());
        //assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_EQF_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_KOULUTUSALA_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_KOULUTUSLAJI_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_TUTKINTONIMIKE_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_POHJAKOULUTUSVAATIMUS_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_TYPE_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_VALUE_MISSING);

        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_MISSING);
    }

    private void assertErrorExist(List<ErrorV1RDTO> errors, KoulutusValidationMessages em) {
        for (ErrorV1RDTO error : errors) {
            if (Objects.equal(em.lower(), error.getErrorMessageKey())) {
                return;
            }
        }
        Assert.fail("Could not find error code '" + em + "' in errors:" + errors);
    }
}
