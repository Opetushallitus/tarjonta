package fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.impl.resources.v1.KoulutusImplicitDataPopulator;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import junit.framework.Assert;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class KoulutusValidatorTest {

    private static final Koulutusmoduuli KOULUTUS_OHJELMA = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
    private KoulutusImplicitDataPopulator dataPopulator = new KoulutusImplicitDataPopulator();

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
        dto = (KoulutusLukioV1RDTO) dataPopulator.defaultValuesForDto(dto);
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

        checkMissingErrors(KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, result), 16);
    }


    @Test
    public void testValidationLukioMissing() {
        KoulutusLukioV1RDTO dto = new KoulutusLukioV1RDTO();
        dto = (KoulutusLukioV1RDTO) dataPopulator.defaultValuesForDto(dto);

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

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO());
        checkMissingErrors(v, 15);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_TILA_ENUM_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidator.KOULUTUKSEN_ALKAMISPVMS);

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

        v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO());
        checkMissingErrors(v, 14);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_INVALID);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_MISSING);

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
        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date()));

        KoodiUrisV1RDTO koodiUris = new KoodiUrisV1RDTO();
        koodiUris.setUris(ImmutableMap.of("koodi_uri", 1));
        dto.setOpetuskielis(koodiUris);
        dto.setOpetusAikas(koodiUris);
        dto.setOpetusPaikkas(koodiUris);
        dto.setOpetusmuodos(koodiUris);

        dto.setSuunniteltuKestoArvo("1");
        dto.setSuunniteltuKestoTyyppi(new KoodiV1RDTO("1", 1, null));

        v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO());
        org.junit.Assert.assertFalse("not success?", v.hasErrors());
    }

    @Test
    public void testStartingDateFirstOfFebruary2018Fails(){
        KoulutusAmmatillinenPerustutkintoV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto = (KoulutusAmmatillinenPerustutkintoV1RDTO) dataPopulator.defaultValuesForDto(dto);
        prepStartingDate2018TestDTO(dto);

        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date(1517436000000L))); // 1.8.2018 fail

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO());
        org.junit.Assert.assertTrue("has errors", v.hasErrors());
        org.junit.Assert.assertEquals("errors count", v.getErrors().size(), 1);

        assertErrorExist(v.getErrors(), KoulutusValidator.KOULUTUKSEN_ALKAMISPVMS);
    }

    @Test
    public void testStartingDateBeforeFirstOfFebruary2018Succeeds(){
        KoulutusAmmatillinenPerustutkintoV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto = (KoulutusAmmatillinenPerustutkintoV1RDTO) dataPopulator.defaultValuesForDto(dto);
        prepStartingDate2018TestDTO(dto);

        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date(946738364556L))); // 1.1.2000 ok
        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date(1517414400000L))); // 31.1.2018 ok

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO());
        org.junit.Assert.assertFalse("no errors", v.hasErrors());
    }

    @Test
    public void testAlkamisKausiFromKevät2018Fails(){
        KoulutusAmmatillinenPerustutkintoV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto = (KoulutusAmmatillinenPerustutkintoV1RDTO) dataPopulator.defaultValuesForDto(dto);
        prepStartingDate2018TestDTO(dto);

        // ei alkamispäivämäärää

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO());
        org.junit.Assert.assertTrue("has errors", v.hasErrors());
        org.junit.Assert.assertEquals("errors count", v.getErrors().size(), 1);

        assertErrorExist(v.getErrors(), KoulutusValidator.KOULUTUKSEN_ALKAMISPVMS);
        String invalidTypeForDateMessage = "toteutustyyppi ei voi olla tätä tyyppiä alkaen 1.2.2018. Jos koulutus alkaa samalla kaudella, käytä tarkkaa päivämäärää kauden sijaan.";
        Assert.assertNotNull(Iterables.find(v.getErrors(), candidate -> candidate.getErrorMessageParameters().contains(invalidTypeForDateMessage)));
    }

    @Test
    public void testAlkamisKausiFromSyksy2018Fails(){
        KoulutusAmmatillinenPerustutkintoV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto = (KoulutusAmmatillinenPerustutkintoV1RDTO) dataPopulator.defaultValuesForDto(dto);
        prepStartingDate2018TestDTO(dto);
        dto.setKoulutuksenAlkamiskausi(new KoodiV1RDTO("kausi_s", 1, null));

        // ei alkamispäivämäärää

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO());
        org.junit.Assert.assertTrue("has errors", v.hasErrors());
        org.junit.Assert.assertEquals("errors count", v.getErrors().size(), 1);

        assertErrorExist(v.getErrors(), KoulutusValidator.KOULUTUKSEN_ALKAMISPVMS);
    }

    @Test
    public void testAlkamisKausiFrom2018SucceedsForOtherTypes(){
        KoulutusAmmatillinenPerustutkintoV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto = (KoulutusAmmatillinenPerustutkintoV1RDTO) dataPopulator.defaultValuesForDto(dto);
        prepStartingDate2018TestDTO(dto);
        dto.setKoulutuksenAlkamiskausi(new KoodiV1RDTO("kausi_s", 1, null));

        // ei alkamispäivämäärää

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO());
        org.junit.Assert.assertTrue("has errors", v.hasErrors());
        org.junit.Assert.assertEquals("errors count", v.getErrors().size(), 1);

        assertErrorExist(v.getErrors(), KoulutusValidator.KOULUTUKSEN_ALKAMISPVMS);
    }

    @Test
    public void testNullAlkamisVuosiFailsWithCorrectMessage(){
        KoulutusAmmatillinenPerustutkintoV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto = (KoulutusAmmatillinenPerustutkintoV1RDTO) dataPopulator.defaultValuesForDto(dto);
        prepStartingDate2018TestDTO(dto);
        dto.setKoulutuksenAlkamisvuosi(null);
        dto.setKoulutuksenAlkamiskausi(null);

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO());
        org.junit.Assert.assertTrue("has errors", v.hasErrors());
        org.junit.Assert.assertEquals("errors count", v.getErrors().size(), 1);

        String missingVuosiMessage = "koulutuksenAlkamisPvms is required (or alternatively koulutuksenAlkamiskausi and koulutuksenAlkamisvuosi can be provided)";
        Assert.assertNotNull(Iterables.find(v.getErrors(), candidate -> missingVuosiMessage.equals(candidate.getErrorMessageKey())));
    }

    private void prepStartingDate2018TestDTO(KoulutusAmmatillinenPerustutkintoV1RDTO dto) {
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
        dto.setKoulutuksenAlkamiskausi(new KoodiV1RDTO("kausi_k", 1, null)); //must have a real pattern validated koodi uri!!!!
        dto.setKoulutuksenAlkamisvuosi(2018);

        KoodiUrisV1RDTO koodiUris = new KoodiUrisV1RDTO();
        koodiUris.setUris(ImmutableMap.of("koodi_uri", 1));
        dto.setOpetuskielis(koodiUris);
        dto.setOpetusAikas(koodiUris);
        dto.setOpetusPaikkas(koodiUris);
        dto.setOpetusmuodos(koodiUris);

        dto.setSuunniteltuKestoArvo("1");
        dto.setSuunniteltuKestoTyyppi(new KoodiV1RDTO("1", 1, null));

        dto.setToteutustyyppi(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
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
        org.junit.Assert.assertEquals("errors count", v.getErrors().size(), eCount);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_KOULUTUSALA_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_KOULUTUSLAJI_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_TUTKINTONIMIKE_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_POHJAKOULUTUSVAATIMUS_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_TYPE_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_VALUE_MISSING);

        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_MISSING);
        assertErrorExist(v.getErrors(), KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_MISSING);
    }

    private void assertErrorExist(List<ErrorV1RDTO> errors, final String errorField) {
        Assert.assertNotNull(Iterables.find(errors, candidate -> errorField.equals(candidate.getErrorField())));
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
