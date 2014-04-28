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
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;

import java.util.Calendar;
import java.util.Locale;
import org.apache.commons.lang.time.DateUtils;
import static org.easymock.EasyMock.*;
import org.junit.Test;

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
public class KoulutusResourceImplV1LukioTest extends KoulutusBase {

    @Before
    public void setUp() throws OIDCreationException {
        reload();
        createJoinedParentAndChildKomos(KoulutusasteTyyppi.LUKIOKOULUTUS);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreateAndLoadToteutus() throws ExceptionMessage {
        //EXPECT
        expect(organisaatioServiceMock.findByOid(ORGANISATION_OID)).andReturn(organisaatioDTO).times(2);
        //the calls of the OidServices must be in correct order!

        permissionChecker.checkCreateKoulutus(ORGANISATION_OID);
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(ORGANISATION_OID);

        /* 
         * KOODISTO DATA CALLS IN CORRECT CALL ORDER
         * 1th round, convert to entity 
         */
        expectKoodis();

        /* REPLAY */
        replay(organisaatioServiceMock);
        replay(tarjontaKoodistoHelperMock);
        /*
         * INSERT LUKIO TO DB
         */
        ResultV1RDTO<KoulutusV1RDTO> v = instance.postKoulutus(createDTO());
        assertEquals("Validation errors", true, v.getErrors() != null ? v.getErrors().isEmpty() : true);

        verify(organisaatioServiceMock);
        verify(tarjontaKoodistoHelperMock);

        /*
         * LOAD LUKIO DTO FROM DB
         */
        reset(organisaatioServiceMock);
        reset(tarjontaKoodistoHelperMock);

        /* 2nd round, convert to dto */
        expectKoodis();

        expect(organisaatioServiceMock.findByOid(ORGANISATION_OID)).andReturn(organisaatioDTO).times(1);

        replay(organisaatioServiceMock);
        replay(tarjontaKoodistoHelperMock);

        final ResultV1RDTO result = instance.findByOid(KOMOTO_OID, true, "FI");
        KoulutusLukioV1RDTO result1 = (KoulutusLukioV1RDTO) result.getResult();
        assertLoadData(result1);

        verify(organisaatioServiceMock);
        verify(tarjontaKoodistoHelperMock);
    }

    private void expectKoodis() {
        expectKausiLukio();
        expectMetaUri(KOULUTUSOHJELMA);
        expectMetaUri(POHJAKOULUTUSVAATIMUS);
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
        expectMetaMapUris(MAP_OPETUSKIELI);
        expectMetaMapUris(MAP_OPETUMUOTO);
        expectMetaMapUris(MAP_OPETUSAIKAS);
        expectMetaMapUris(MAP_OPETUSPAIKKAS);
        expectMetaUri(SUUNNITELTU_KESTO);
        expectMetaUri(TUTKINTO);
    }

    private void assertLoadData(final KoulutusLukioV1RDTO result) {
        assertNotNull(result);

        assertEquals(KOMOTO_OID, result.getOid());
        assertEquals(KOMO_CHILD_OID, result.getKomoOid());
        assertEquals(ORGANISATION_OID, result.getOrganisaatio().getOid());
        assertEquals(ORGANISAATIO_NIMI, result.getOrganisaatio().getNimi());

        assertEquals(KoulutusasteTyyppi.LUKIOKOULUTUS, result.getKoulutusasteTyyppi());

        final String key = URI_KIELI_FI + "_uri";

        assertNotNull(null, result.getKoulutusohjelma().getTekstis().isEmpty());
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
        assertEqualMetaDto(MAP_OPETUMUOTO, result.getOpetusmuodos());

        assertEquals(SUUNNITELTU_KESTO_VALUE, result.getSuunniteltuKestoArvo());
        assertEquals(SUUNNITELTU_KESTO + "_uri", result.getSuunniteltuKestoTyyppi().getUri());
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

    private void expectKausiLukio() {
        KoodiType kausiKoodiType = createKoodiType(KAUSI_KOODI_URI, "x" + KAUSI_KOODI_URI);
        expect(tarjontaKoodistoHelperMock.getKoodiByUri(KAUSI_KOODI_URI + "#1")).andReturn(kausiKoodiType).times(1);
        expect(tarjontaKoodistoHelperMock.getKoodi(KAUSI_KOODI_URI + "_uri", 1)).andReturn(kausiKoodiType).times(1);
        expect(tarjontaKoodistoHelperMock.getKoodiNimi(kausiKoodiType, new Locale(LOCALE_FI))).andReturn(KAUSI_KOODI_URI).times(1);

        KoodiType koodiLanguageFi = createKoodiType(URI_KIELI_FI, "fi");
        expect(tarjontaKoodistoHelperMock.convertKielikoodiToKoodiType(LOCALE_FI)).andReturn(koodiLanguageFi).times(1);
    }

    private KoulutusLukioV1RDTO createDTO() {
        /*
         * KOMO data fields:
         */

        KoulutusLukioV1RDTO dto = new KoulutusLukioV1RDTO();

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

        dto.setPohjakoulutusvaatimus(toKoodiUri(POHJAKOULUTUSVAATIMUS));

        dto.setKoulutuskoodi(toKoodiUri(KOULUTUSKOODI));

        dto.setKoulutusohjelma(toNimiKoodiUri(KOULUTUSOHJELMA));

        dto.getKoulutuksenAlkamisPvms().add(DATE.toDate());

        koodiUrisMap(dto.getOpetusAikas(), URI_KIELI_FI, MAP_OPETUSAIKAS);

        koodiUrisMap(dto.getOpetusPaikkas(), URI_KIELI_FI, MAP_OPETUSPAIKKAS);

        koodiUrisMap(dto.getOpetuskielis(), URI_KIELI_FI, MAP_OPETUSKIELI);

        koodiUrisMap(dto.getOpetusmuodos(), URI_KIELI_FI, MAP_OPETUMUOTO);

        dto.setSuunniteltuKestoTyyppi(toKoodiUri(SUUNNITELTU_KESTO));

        dto.setSuunniteltuKestoArvo(SUUNNITELTU_KESTO_VALUE);

        dto.getYhteyshenkilos().add(new YhteyshenkiloTyyppi(PERSON[0], PERSON[1], PERSON[2], PERSON[3], PERSON[4], PERSON[5], null, HenkiloTyyppi.YHTEYSHENKILO));

        dto.setOpintojenLaajuusarvo(toKoodiUri(LAAJUUSARVO));

        dto.setOpintojenLaajuusyksikko(toKoodiUri(LAAJUUSYKSIKKO));

        dto.setKoulutuslaji(toKoodiUri(KOULUTUSLAJI));

        dto.setTutkintonimike(toKoodiUri(TUTKINTONIMIKE));

        return dto;
    }

}
