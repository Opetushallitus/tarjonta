package fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation;

import static fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages.KOULUTUS_JARJESTAJA_MISSING;
import static fi.vm.sade.tarjonta.shared.types.TarjontaTila.JULKAISTU;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.impl.resources.v1.KoulutusImplicitDataPopulator;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.LokalisointiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.AmmattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ErikoisammattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPerustutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NayttotutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KoulutusValidatorTest {

    private static final Koulutusmoduuli KOULUTUS_OHJELMA = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
    private final OrganisaatioV1RDTO jarjestavaOrganisaatio = new OrganisaatioV1RDTO(
        "organisaatioOid",
        "Näyttötutkinnonjärjestäjät Ry",
        Collections.singletonList(new LokalisointiV1RDTO("fi", "kieli_fi", "Näyttötutkinnonjärjestäjät Ry")));
    private final OrganisaatioRDTO jarjestavaOrganisaatioRdto = new OrganisaatioRDTO();

    private KoulutusImplicitDataPopulator dataPopulator = new KoulutusImplicitDataPopulator();
    private OrganisaatioService mockOrganisaatioService = Mockito.mock(OrganisaatioService.class);
    private KoulutusValidator validator = new KoulutusValidator(Mockito.mock(KoulutusmoduuliToteutusDAO.class),
        Mockito.mock(PermissionChecker.class),
        mockOrganisaatioService);
    private final Koulutusmoduuli tutkinto = new Koulutusmoduuli();

    @Before
    public void populateTestData() {
        jarjestavaOrganisaatioRdto.setOid(jarjestavaOrganisaatio.getOid());
        jarjestavaOrganisaatioRdto.setNimi(ImmutableMap.of("fi", jarjestavaOrganisaatio.getNimi()));

        tutkinto.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
    }

    @Test
    public void testTunniste() {

        KoulutusKorkeakouluV1RDTO kk = new KoulutusKorkeakouluV1RDTO();
        kk.setTunniste("123456789012345678901234567890123456");

        ResultV1RDTO<KoulutusV1RDTO> r = new ResultV1RDTO<>();
        KoulutusValidator.validateTunniste(kk, r);

        final KoulutusValidationMessages tunnisteError = KoulutusValidationMessages.KOULUTUS_TUNNISTE_LENGTH;

        assertTrue(tunnisteError.lower(), r.hasErrors());
        assertErrorExist(r.getErrors(), tunnisteError);

        kk.setTunniste("12345678901234567890123456789012345");
        r = new ResultV1RDTO<>();
        KoulutusValidator.validateTunniste(kk, r);
        assertFalse(tunnisteError.lower(), r.hasErrors());

        kk.setTunniste("");
        r = new ResultV1RDTO<>();
        KoulutusValidator.validateTunniste(kk, r);
        assertFalse(tunnisteError.lower(), r.hasErrors());

        kk.setTunniste(null);
        r = new ResultV1RDTO<>();
        KoulutusValidator.validateTunniste(kk, r);
        assertFalse(tunnisteError.lower(), r.hasErrors());
    }

    @Test
    public void testValidationLukioNullObject() {
        ResultV1RDTO<KoulutusV1RDTO> result = new ResultV1RDTO<>();
        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(null, KOULUTUS_OHJELMA, result);
        assertTrue("errors", v.hasErrors());
        assertEquals("errors count", 1, v.getErrors().size());
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

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO<>());
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

        v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO<>());
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

        v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO<>());
        assertFalse("not success?", v.hasErrors());
    }

    @Test
    public void testStartingDateFirstOfFebruary2018Fails(){
        KoulutusAmmatillinenPerustutkintoV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto = (KoulutusAmmatillinenPerustutkintoV1RDTO) dataPopulator.defaultValuesForDto(dto);
        prepStartingDate2018TestDTO(dto);

        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date(1517436000000L))); // 1.8.2018 fail

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO<>());
        assertTrue("has errors", v.hasErrors());
        assertEquals("errors count", v.getErrors().size(), 1);

        assertErrorExist(v.getErrors(), KoulutusValidator.KOULUTUKSEN_ALKAMISPVMS);
    }

    @Test
    public void testStartingDateBeforeFirstOfFebruary2018Succeeds(){
        KoulutusAmmatillinenPerustutkintoV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto = (KoulutusAmmatillinenPerustutkintoV1RDTO) dataPopulator.defaultValuesForDto(dto);
        prepStartingDate2018TestDTO(dto);

        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date(946738364556L))); // 1.1.2000 ok
        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date(1517414400000L))); // 31.1.2018 ok

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO<>());
        assertFalse("no errors", v.hasErrors());
    }

    @Test
    public void testAlkamisKausiFromKevät2018Fails(){
        KoulutusAmmatillinenPerustutkintoV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto = (KoulutusAmmatillinenPerustutkintoV1RDTO) dataPopulator.defaultValuesForDto(dto);
        prepStartingDate2018TestDTO(dto);

        // ei alkamispäivämäärää

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO<>());
        assertTrue("has errors", v.hasErrors());
        assertEquals("errors count", v.getErrors().size(), 1);

        assertErrorExist(v.getErrors(), KoulutusValidator.KOULUTUKSEN_ALKAMISPVMS);
        String invalidTypeForDateMessage = "toteutustyyppi ei voi olla tätä tyyppiä alkaen 1.2.2018. Jos koulutus alkaa samalla kaudella, käytä tarkkaa päivämäärää kauden sijaan.";
        assertNotNull(Iterables.find(v.getErrors(), candidate -> candidate.getErrorMessageParameters().contains(invalidTypeForDateMessage)));
    }

    @Test
    public void testAlkamisKausiFromSyksy2018Fails(){
        KoulutusAmmatillinenPerustutkintoV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto = (KoulutusAmmatillinenPerustutkintoV1RDTO) dataPopulator.defaultValuesForDto(dto);
        prepStartingDate2018TestDTO(dto);
        dto.setKoulutuksenAlkamiskausi(new KoodiV1RDTO("kausi_s", 1, null));

        // ei alkamispäivämäärää

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO<>());
        assertTrue("has errors", v.hasErrors());
        assertEquals("errors count", v.getErrors().size(), 1);

        assertErrorExist(v.getErrors(), KoulutusValidator.KOULUTUKSEN_ALKAMISPVMS);
    }

    @Test
    public void testAlkamisKausiFrom2018SucceedsForOtherTypes(){
        KoulutusAmmatillinenPerustutkintoV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto = (KoulutusAmmatillinenPerustutkintoV1RDTO) dataPopulator.defaultValuesForDto(dto);
        prepStartingDate2018TestDTO(dto);
        dto.setKoulutuksenAlkamiskausi(new KoodiV1RDTO("kausi_s", 1, null));

        // ei alkamispäivämäärää

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO<>());
        assertTrue("has errors", v.hasErrors());
        assertEquals("errors count", v.getErrors().size(), 1);

        assertErrorExist(v.getErrors(), KoulutusValidator.KOULUTUKSEN_ALKAMISPVMS);
    }

    @Test
    public void testNullAlkamisVuosiFailsWithCorrectMessage(){
        KoulutusAmmatillinenPerustutkintoV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto = (KoulutusAmmatillinenPerustutkintoV1RDTO) dataPopulator.defaultValuesForDto(dto);
        prepStartingDate2018TestDTO(dto);
        dto.setKoulutuksenAlkamisvuosi(null);
        dto.setKoulutuksenAlkamiskausi(null);

        ResultV1RDTO<KoulutusV1RDTO> v = KoulutusValidator.validateKoulutusGeneric(dto, KOULUTUS_OHJELMA, new ResultV1RDTO<>());
        assertTrue("has errors", v.hasErrors());
        assertEquals("errors count", v.getErrors().size(), 1);

        String missingVuosiMessage = "koulutuksenAlkamisPvms is required (or alternatively koulutuksenAlkamiskausi and koulutuksenAlkamisvuosi can be provided)";
        assertNotNull(Iterables.find(v.getErrors(), candidate -> missingVuosiMessage.equals(candidate.getErrorMessageKey())));
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
        assertFalse(KoulutusValidator.isValidKoodiUriWithVersion(null));
        assertFalse(KoulutusValidator.isValidKoodiUriWithVersion(new KoodiV1RDTO()));
        assertFalse(KoulutusValidator.isValidKoodiUriWithVersion(new KoodiV1RDTO("", -1, null)));
        assertFalse(KoulutusValidator.isValidKoodiUriWithVersion(new KoodiV1RDTO("", 1, null)));
        assertFalse(KoulutusValidator.isValidKoodiUriWithVersion(new KoodiV1RDTO(null, 1, null)));
        assertFalse(KoulutusValidator.isValidKoodiUriWithVersion(new KoodiV1RDTO("1", 0, null)));
        assertTrue(KoulutusValidator.isValidKoodiUriWithVersion(new KoodiV1RDTO("1", 1, null)));
    }

    @Test
    public void notNullStrOrEmpty() {
        assertFalse(KoulutusValidator.notNullStrOrEmpty(null));
        assertFalse(KoulutusValidator.notNullStrOrEmpty(""));
        assertFalse(KoulutusValidator.notNullStrOrEmpty(" "));
        assertFalse(KoulutusValidator.notNullStrOrEmpty("          "));
        assertTrue(KoulutusValidator.notNullStrOrEmpty(" 1 "));
        assertTrue(KoulutusValidator.notNullStrOrEmpty("1"));
    }

    @Test
    public void validateKoodiUris() {
        //use any error message, this test do not care the error message types.
        final KoulutusValidationMessages missing = KoulutusValidationMessages.KOULUTUS_NQF_MISSING;
        final KoulutusValidationMessages invalid = KoulutusValidationMessages.KOULUTUS_NQF_INVALID;
        assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO<>(), null, missing, invalid, null));

        KoodiUrisV1RDTO dto = new KoodiUrisV1RDTO();
        assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO<>(), dto, missing, invalid, null));

        //set empty map (size of zero)
        Map<String, Integer> uris = Maps.newHashMap();
        dto.setUris(uris);

        //no limit
        assertTrue(KoulutusValidator.validateKoodiUris(new ResultV1RDTO<>(), dto, missing, invalid, null));
        //limit == uris.size
        assertTrue(KoulutusValidator.validateKoodiUris(new ResultV1RDTO<>(), dto, missing, invalid, 0));
        //limit > uris.size
        assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO<>(), dto, missing, invalid, 1));

        uris.put(null, null);
        dto.setUris(uris);
        assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO<>(), dto, missing, invalid, null));

        uris.put(null, -1);
        dto.setUris(uris);
        assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO<>(), dto, missing, invalid, null));

        uris = Maps.newHashMap();
        uris.put("", 0);
        dto.setUris(uris);
        assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO<>(), dto, missing, invalid, null));

        uris = Maps.newHashMap();
        uris.put("", 1);
        dto.setUris(uris);
        assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO<>(), dto, missing, invalid, null));

        uris = Maps.newHashMap();
        uris.put("", 0);
        dto.setUris(uris);
        assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO<>(), dto, missing, invalid, null));

        uris = Maps.newHashMap();
        uris.put("1", 1);
        uris.put("", -1);
        dto.setUris(uris);
        assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO<>(), dto, missing, invalid, null));

        uris = Maps.newHashMap();
        uris.put("1", 1);
        uris.put("2", 2);
        dto.setUris(uris);
        assertTrue(KoulutusValidator.validateKoodiUris(new ResultV1RDTO<>(), dto, missing, invalid, 1));
        assertTrue(KoulutusValidator.validateKoodiUris(new ResultV1RDTO<>(), dto, missing, invalid, 2));
        assertFalse(KoulutusValidator.validateKoodiUris(new ResultV1RDTO<>(), dto, missing, invalid, 3));

    }

    @Test
    public void nayttokoulutuksenJarjestavaOrganisaatioOnPakollinenAmmattitutkinnoilleErikoisammattitutkinnoilleJaAmmatillisillePerustutkinnoilleNayttotutkintonaEnnenReformia() {
        when(mockOrganisaatioService.findByOid(jarjestavaOrganisaatio.getOid())).thenReturn(jarjestavaOrganisaatioRdto);

        NayttotutkintoV1RDTO poistuvaAmmatillinenPerustutkintoNayttotutkintonaV1RDTO = populate(new KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO());
        NayttotutkintoV1RDTO vanhaAmmattitutkintoV1RDTO = populate(new AmmattitutkintoV1RDTO());
        NayttotutkintoV1RDTO vanhaErikoisammattitutkintoV1RDTO = populate(new ErikoisammattitutkintoV1RDTO());

        ResultV1RDTO<KoulutusV1RDTO> result = new ResultV1RDTO<>(poistuvaAmmatillinenPerustutkintoNayttotutkintonaV1RDTO);
        validator.validateKoulutusNayttotutkinto(poistuvaAmmatillinenPerustutkintoNayttotutkintonaV1RDTO, tutkinto, result);
        assertEquals(null, result.getErrors());

        result.setResult(vanhaAmmattitutkintoV1RDTO);
        validator.validateKoulutusNayttotutkinto(vanhaAmmattitutkintoV1RDTO, tutkinto, result);
        assertEquals(null, result.getErrors());

        result.setResult(vanhaErikoisammattitutkintoV1RDTO);
        validator.validateKoulutusNayttotutkinto(vanhaErikoisammattitutkintoV1RDTO, tutkinto, result);
        assertEquals(null, result.getErrors());

        Arrays.asList(poistuvaAmmatillinenPerustutkintoNayttotutkintonaV1RDTO, vanhaAmmattitutkintoV1RDTO, vanhaErikoisammattitutkintoV1RDTO)
            .forEach(k -> k.setJarjestavaOrganisaatio(null));

        verify(mockOrganisaatioService, times(3)).findByOid(jarjestavaOrganisaatio.getOid());
        verifyNoMoreInteractions(mockOrganisaatioService);
    }

    private void checkMissingErrors(ResultV1RDTO<KoulutusV1RDTO> v, int eCount) {
        assertTrue("errors", v.hasErrors());
        assertEquals("errors count", v.getErrors().size(), eCount);
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
        List<String> actualErrorFields = errors.stream().map(ErrorV1RDTO::getErrorField).collect(Collectors.toList());
        String message = "Only got error fields " + actualErrorFields + " , expected '" + errorField + "'";
        try {
            assertNotNull(message, Iterables.find(errors, candidate -> errorField.equals(candidate.getErrorField())));
        } catch (Exception e) {
            System.err.println(message);
            throw e;
        }
    }

    private void assertErrorExist(List<ErrorV1RDTO> errors, KoulutusValidationMessages em) {
        for (ErrorV1RDTO error : errors) {
            if (Objects.equal(em.lower(), error.getErrorMessageKey())) {
                return;
            }
        }
        Assert.fail("Could not find error code '" + em + "' in errors:" + errors);
    }

    private <T extends NayttotutkintoV1RDTO> T populate(T dto) {
        dto.setOrganisaatio(new OrganisaatioV1RDTO("tarjoajaOid"));
        dto.setKoulutuksenAlkamisPvms(Collections.singleton(new LocalDate(2015, 1, 1).toDate()));
        dto.setOpetuskielis(new KoodiUrisV1RDTO(ImmutableMap.of("fi", 1)));
        dto.setJarjestavaOrganisaatio(jarjestavaOrganisaatio);
        dto.setTila(JULKAISTU);
        return dto;
    }
}
