/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */
package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.types.HenkiloTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.KoulutusBase.KAUSI_KOODI_URI;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusValmistavaV1RDTO;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.time.DateUtils;
import static org.easymock.EasyMock.*;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author jani
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("embedded-solr")
@Transactional()
public class KoulutusResourceImplV1NayttoTest extends KoulutusBase {

    private static final String ORGANISATION_JARJESTAJA_OID = "organisaatio_jarjestaja_oid";
    private static final String VALMENTAVA_KOMOTO_OID = "valmentava_komoto_oid";

    private OrganisaatioDTO jarjestajaDTO = new OrganisaatioDTO();

    @Before
    public void setUp() throws OIDCreationException {
        reload();
        createJoinedParentAndChildKomos(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        jarjestajaDTO.setOid(ORGANISATION_JARJESTAJA_OID);
    }

    @After
    public void tearDown() {
    }

    private void printResultErrors(ResultV1RDTO r) {
        if (r != null && r.getErrors() != null) {
            List<ErrorV1RDTO> errors = r.getErrors();

            for (ErrorV1RDTO e : errors) {
                System.out.println(e.getErrorMessageKey());
            }
        }
    }

    @Test
    public void testCreateAndLoadToteutus() throws ExceptionMessage, OIDCreationException {
        //EXPECT
        expect(organisaatioServiceMock.findByOid(ORGANISATION_OID)).andReturn(organisaatioDTO).times(2);
        expect(organisaatioServiceMock.findByOid(ORGANISATION_JARJESTAJA_OID)).andReturn(jarjestajaDTO).times(2);
        //the calls of the OidServices must be in correct order!

        permissionChecker.checkCreateKoulutus(ORGANISATION_OID);

        expectNayttoKoodis();  /* 1th round koodisto calls, convert result to dto */

        /*
         * INSERT NAYTTO TO DB
         */
        replayAll();
        ResultV1RDTO<KoulutusV1RDTO> v = instance.postKoulutus(createDTO());
        printResultErrors(v);
        assertEquals("Validation errors", true, v.getErrors() != null ? v.getErrors().isEmpty() : true);
        verifyAll();

        /*
         * LOAD NAYTTO DTO FROM DB
         */
        resetAll();
        expectNayttoKoodis(); /* 2th round koodisto calls, convert result to dto */

        expect(organisaatioServiceMock.findByOid(ORGANISATION_OID)).andReturn(organisaatioDTO).times(1);
        expect(organisaatioServiceMock.findByOid(ORGANISATION_JARJESTAJA_OID)).andReturn(jarjestajaDTO).times(1);
        replayAll();
        final ResultV1RDTO result = instance.findByOid(KOMOTO_OID, true, false, "FI");
        KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO withoutValmentava = (KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO) result.getResult();
        assertNayttoData(withoutValmentava);
        verifyAll();
        /*
         * LOAD NAYTTO DTO FROM DB
         */
        resetAll();
        expectNayttoKoodis(); /* 3rd round koodisto calls, convert result base komoto to dto */

        expectValmistavaKoodis(); /* 4rd round koodisto calls, convert result valmentava to dto */

        Mockito.stub(oidService.get(TarjontaOidType.KOMOTO)).toReturn(VALMENTAVA_KOMOTO_OID);
        permissionChecker.checkCreateKoulutus(ORGANISATION_OID);
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(ORGANISATION_OID);
        expect(publicationDataService.isValidStatusChange(isA(fi.vm.sade.tarjonta.publication.Tila.class))).andReturn(true);
        expect(organisaatioServiceMock.findByOid(ORGANISATION_OID)).andReturn(organisaatioDTO).times(3);
        expect(organisaatioServiceMock.findByOid(ORGANISATION_JARJESTAJA_OID)).andReturn(jarjestajaDTO).times(2);
        replayAll();

        /*
         * UPDATE NAYTTO AND ADD VALMENTAVA TO DB
         */
        withoutValmentava.setValmistavaKoulutus(createValmentavaDTO());
        v = instance.postKoulutus(withoutValmentava);

        printResultErrors(v);
        assertEquals("Validation errors", true, v.getErrors() != null ? v.getErrors().isEmpty() : true);

        KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO withValmentava = (KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO) v.getResult();
        assertNayttoData(withValmentava);
        assertValmentavaData(withValmentava.getValmistavaKoulutus());

        verify(organisaatioServiceMock);
        verify(tarjontaKoodistoHelperMock);
    }

    private void expectValmistavaKoodis() {
        expectKausiNaytto();
        expectMetaUri(SUUNNITELTU_KESTO_TYYPPI);
        expectMetaMapUris(MAP_OPETUSKIELI);
        expectMetaMapUris(MAP_OPETUMUOTO);
        expectMetaMapUris(MAP_OPETUSAIKAS);
        expectMetaMapUris(MAP_OPETUSPAIKKAS);
    }

    private void expectNayttoKoodis() {
        expectKausiNaytto();
        expectMetaUri(KOULUTUSOHJELMA);
        expectMetaUri(KOULUTUSKOODI);
        expectMetaUri(LAAJUUSARVO);
        expectMetaUri(LAAJUUSYKSIKKO);
        expectMetaUri(KOULUTUSASTE);
        expectMetaUri(KOULUTUSALA);
        expectMetaUri(OPINTOALA);
        expectMetaUri(TUTKINTONIMIKE);
        expectMetaUri(KOULUTUSLAJI);
        expectMetaUri(EQF);
        expectMetaUri(NQF);
        expectMetaUri(TUTKINTO);
        expectMetaMapUris(MAP_OPETUSKIELI);
    }

    private void assertValmentavaData(final KoulutusValmistavaV1RDTO result) {
        assertNotNull(result);

        assertEquals(VALMENTAVA_KOMOTO_OID, result.getOid());
        assertEquals(KOMO_CHILD_OID, result.getKomoOid());
        assertEquals(ORGANISATION_OID, result.getOrganisaatio().getOid());
        assertEquals(ORGANISAATIO_NIMI, result.getOrganisaatio().getNimi());

        assertEquals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS, result.getKoulutusasteTyyppi());
        assertEquals(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA_VALMISTAVA, result.getToteutustyyppi());
        assertEquals(ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS, result.getModuulityyppi());

        final String key = URI_KIELI_FI + "_uri";

        Assert.assertTrue(result.getKoulutusohjelma().getTekstis().isEmpty());
        Assert.assertNull(result.getKoulutusohjelma().getNimi());
        Assert.assertNull(result.getKoulutusohjelma().getUri());
        Assert.assertNull(result.getKoulutusaste());
        Assert.assertNull(result.getKoulutusala());
        Assert.assertNull(result.getOpintoala());
        Assert.assertNull(result.getEqf());
        Assert.assertNull(result.getNqf());
        Assert.assertNull(result.getKoulutuskoodi());
        Assert.assertNull(result.getOpintojenLaajuusarvo());
        Assert.assertNull(result.getOpintojenLaajuusyksikko());
        Assert.assertNull(result.getTutkinto());

        assertEquals(null, result.getTila()); // no status for valmentava
        assertEquals(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, result.getKoulutusmoduuliTyyppi());
        assertEquals(null, result.getTunniste());
        assertEquals((DateUtils.truncate(DATE.toDate(), Calendar.DATE)), result.getKoulutuksenAlkamisPvms().iterator().next());
        assertEqualDtoKoodi(KAUSI_KOODI_URI, result.getKoulutuksenAlkamiskausi(), true);
        assertEquals(VUOSI, result.getKoulutuksenAlkamisvuosi());

        assertEqualMetaDto(MAP_OPETUSKIELI, result.getOpetuskielis());
        assertEqualMetaDto(MAP_OPETUMUOTO, result.getOpetusmuodos());
        assertEqualMetaDto(MAP_OPETUSPAIKKAS, result.getOpetusPaikkas());
        assertEqualMetaDto(MAP_OPETUSAIKAS, result.getOpetusAikas());

        assertEquals(SUUNNITELTU_KESTO_VALUE, result.getSuunniteltuKestoArvo());
        assertEquals(SUUNNITELTU_KESTO_TYYPPI + "_uri", result.getSuunniteltuKestoTyyppi().getUri());
        assertEquals(new Integer(1), result.getSuunniteltuKestoTyyppi().getVersio());
        YhteyshenkiloTyyppi next = result.getYhteyshenkilos().iterator().next();
        assertEquals(PERSON[0], next.getHenkiloOid());
        assertEquals(PERSON[1], next.getEtunimet());
        assertEquals(PERSON[2], next.getSukunimi());
        assertEquals(PERSON[3], next.getTitteli());
        assertEquals(PERSON[4], next.getSahkoposti());
        assertEquals(PERSON[5], next.getPuhelin());
        assertEquals(HenkiloTyyppi.YHTEYSHENKILO, next.getHenkiloTyyppi());
        assertEquals(USER_OID, result.getModifiedBy());
    }

    private void assertNayttoData(final KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO result) {
        assertNotNull(result);

        assertEquals(KOMOTO_OID, result.getOid());
        assertEquals(KOMO_CHILD_OID, result.getKomoOid());
        assertEquals(ORGANISATION_OID, result.getOrganisaatio().getOid());
        assertEquals(ORGANISAATIO_NIMI, result.getOrganisaatio().getNimi());
        assertEquals(ORGANISATION_JARJESTAJA_OID, result.getJarjestavaOrganisaatio().getOid());

        assertEquals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS, result.getKoulutusasteTyyppi());
        assertEquals(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA, result.getToteutustyyppi());
        assertEquals(ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS, result.getModuulityyppi());

        final String key = URI_KIELI_FI + "_uri";

        Assert.assertTrue(result.getKoulutusohjelma().getTekstis().isEmpty());
        assertNotNull(KOULUTUSOHJELMA, result.getKoulutusohjelma().getMeta().get(key));
        assertEqualDtoKoodi(KOULUTUSOHJELMA, result.getKoulutusohjelma());
        assertEqualDtoKoodi(KOULUTUSASTE, result.getKoulutusaste());
        assertEqualDtoKoodi(KOULUTUSALA, result.getKoulutusala());
        assertEqualDtoKoodi(OPINTOALA, result.getOpintoala());
        assertEqualDtoKoodi(EQF, result.getEqf());
        assertEqualDtoKoodi(NQF, result.getNqf());
        assertEqualDtoKoodi(KOULUTUSKOODI, result.getKoulutuskoodi());
        assertEqualDtoKoodi(LAAJUUSARVO, result.getOpintojenLaajuusarvo());
        assertEqualDtoKoodi(LAAJUUSYKSIKKO, result.getOpintojenLaajuusyksikko());
        assertEqualDtoKoodi(TUTKINTONIMIKE, result.getTutkintonimike());
        assertEqualDtoKoodi(KOULUTUSLAJI, result.getKoulutuslaji());
        assertEqualDtoKoodi(TUTKINTO, result.getTutkinto());

        assertEquals(TarjontaTila.JULKAISTU, result.getTila());
        assertEquals(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, result.getKoulutusmoduuliTyyppi());
        assertEquals(TUNNISTE, result.getTunniste());
        assertEquals((DateUtils.truncate(DATE.toDate(), Calendar.DATE)), result.getKoulutuksenAlkamisPvms().iterator().next());
        assertEqualDtoKoodi(KAUSI_KOODI_URI, result.getKoulutuksenAlkamiskausi(), true);
        assertEquals(VUOSI, result.getKoulutuksenAlkamisvuosi());

        assertEqualMetaDto(MAP_OPETUSKIELI, result.getOpetuskielis());
        Assert.assertTrue(result.getOpetusmuodos().getUris().isEmpty());
        Assert.assertTrue(result.getOpetusPaikkas().getUris().isEmpty());
        Assert.assertTrue(result.getOpetusAikas().getUris().isEmpty());

        assertEquals(null, result.getSuunniteltuKestoArvo());
        assertEquals(null, result.getSuunniteltuKestoTyyppi());

        YhteyshenkiloTyyppi next = result.getYhteyshenkilos().iterator().next();
        assertEquals(PERSON[0], next.getHenkiloOid());
        assertEquals(PERSON[1], next.getEtunimet());
        assertEquals(PERSON[2], next.getSukunimi());
        assertEquals(PERSON[3], next.getTitteli());
        assertEquals(PERSON[4], next.getSahkoposti());
        assertEquals(PERSON[5], next.getPuhelin());
        assertEquals(HenkiloTyyppi.YHTEYSHENKILO, next.getHenkiloTyyppi());
        assertEquals(USER_OID, result.getModifiedBy());
    }

    private void expectKausiNaytto() {
        KoodiType kausiKoodiType = createKoodiType(KAUSI_KOODI_URI, "x" + KAUSI_KOODI_URI);
        expect(tarjontaKoodistoHelperMock.getKoodiByUri(KAUSI_KOODI_URI + "#1")).andReturn(kausiKoodiType).times(1);
        expect(tarjontaKoodistoHelperMock.getKoodi(KAUSI_KOODI_URI + "_uri", 1)).andReturn(kausiKoodiType).times(1);
        expect(tarjontaKoodistoHelperMock.getKoodiNimi(kausiKoodiType, new Locale(LOCALE_FI))).andReturn(KAUSI_KOODI_URI).times(1);

        KoodiType koodiLanguageFi = createKoodiType(URI_KIELI_FI, "fi");
        expect(tarjontaKoodistoHelperMock.convertKielikoodiToKoodiType(LOCALE_FI)).andReturn(koodiLanguageFi).times(1);
    }

    /*
     * Set base DTO data fields:
     */
    private KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO createDTO() {

        KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO dto = new KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO();
        dto.getOrganisaatio().setOid(ORGANISATION_OID);
        dto.setKoulutusaste(toKoodiUri(KOULUTUSASTE));
        dto.setKoulutusala(toKoodiUri(KOULUTUSALA));
        dto.setOpintoala(toKoodiUri(OPINTOALA));
        dto.setTutkinto(toKoodiUri(TUTKINTO));
        dto.setEqf(toKoodiUri(EQF));
        dto.setNqf(toKoodiUri(NQF));
        dto.setTila(TarjontaTila.JULKAISTU);
        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO);
        dto.setTunniste(TUNNISTE);
        dto.setKomoOid(KOMO_CHILD_OID);
        dto.setKoulutuskoodi(toKoodiUri(KOULUTUSKOODI));
        dto.setKoulutusohjelma(toNimiKoodiUri(KOULUTUSOHJELMA));
        dto.getKoulutuksenAlkamisPvms().add(DATE.toDate());
        koodiUrisMap(dto.getOpetusAikas(), URI_KIELI_FI, MAP_OPETUSAIKAS);
        koodiUrisMap(dto.getOpetusPaikkas(), URI_KIELI_FI, MAP_OPETUSPAIKKAS);
        koodiUrisMap(dto.getOpetuskielis(), URI_KIELI_FI, MAP_OPETUSKIELI);
        koodiUrisMap(dto.getOpetusmuodos(), URI_KIELI_FI, MAP_OPETUMUOTO);
        dto.setSuunniteltuKestoTyyppi(toKoodiUri(SUUNNITELTU_KESTO_TYYPPI));
        dto.setSuunniteltuKestoArvo(SUUNNITELTU_KESTO_VALUE);
        dto.getYhteyshenkilos().add(new YhteyshenkiloTyyppi(PERSON[0], PERSON[1], PERSON[2], PERSON[3], PERSON[4], PERSON[5], null, HenkiloTyyppi.YHTEYSHENKILO));
        dto.setOpintojenLaajuusarvo(toKoodiUri(LAAJUUSARVO));
        dto.setOpintojenLaajuusyksikko(toKoodiUri(LAAJUUSYKSIKKO));
        dto.setKoulutuslaji(toKoodiUri(KOULUTUSLAJI));
        dto.setTutkintonimike(toKoodiUri(TUTKINTONIMIKE));
        dto.setJarjestavaOrganisaatio(new OrganisaatioV1RDTO(ORGANISATION_JARJESTAJA_OID, null, null));

        return dto;
    }

    /*
     * Set optional DTO data fields:
     */
    private KoulutusValmistavaV1RDTO createValmentavaDTO() {
        KoulutusValmistavaV1RDTO dto = new KoulutusValmistavaV1RDTO();
        dto.getOrganisaatio().setOid(ORGANISATION_OID);
        dto.setKoulutusaste(toKoodiUri(KOULUTUSASTE));
        dto.setKoulutusala(toKoodiUri(KOULUTUSALA));
        dto.setOpintoala(toKoodiUri(OPINTOALA));
        dto.setTutkinto(toKoodiUri(TUTKINTO));
        dto.setEqf(toKoodiUri(EQF));
        dto.setNqf(toKoodiUri(NQF));
        dto.setTila(TarjontaTila.JULKAISTU);
        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO);
        dto.setTunniste(TUNNISTE);
        dto.setKomoOid(KOMO_CHILD_OID);
        dto.setKoulutuskoodi(toKoodiUri(KOULUTUSKOODI));
        dto.setKoulutusohjelma(toNimiKoodiUri(KOULUTUSOHJELMA));
        dto.getKoulutuksenAlkamisPvms().add(DATE.toDate());
        koodiUrisMap(dto.getOpetusAikas(), URI_KIELI_FI, MAP_OPETUSAIKAS);
        koodiUrisMap(dto.getOpetusPaikkas(), URI_KIELI_FI, MAP_OPETUSPAIKKAS);
        koodiUrisMap(dto.getOpetuskielis(), URI_KIELI_FI, MAP_OPETUSKIELI);
        koodiUrisMap(dto.getOpetusmuodos(), URI_KIELI_FI, MAP_OPETUMUOTO);
        dto.setSuunniteltuKestoTyyppi(toKoodiUri(SUUNNITELTU_KESTO_TYYPPI));
        dto.setSuunniteltuKestoArvo(SUUNNITELTU_KESTO_VALUE);
        dto.getYhteyshenkilos().add(new YhteyshenkiloTyyppi(PERSON[0], PERSON[1], PERSON[2], PERSON[3], PERSON[4], PERSON[5], null, HenkiloTyyppi.YHTEYSHENKILO));
        dto.setOpintojenLaajuusarvo(toKoodiUri(LAAJUUSARVO));
        dto.setOpintojenLaajuusyksikko(toKoodiUri(LAAJUUSYKSIKKO));

        return dto;
    }

    private void replayAll() {
        replay(permissionChecker);
        replay(organisaatioServiceMock);
        replay(tarjontaKoodistoHelperMock);
        replay(publicationDataService);
    }

    private void resetAll() {
        reset(permissionChecker);
        reset(organisaatioServiceMock);
        reset(tarjontaKoodistoHelperMock);
        reset(publicationDataService);
    }

    private void verifyAll() {
        verify(permissionChecker);
        verify(organisaatioServiceMock);
        verify(tarjontaKoodistoHelperMock);
        verify(publicationDataService);
    }

}
