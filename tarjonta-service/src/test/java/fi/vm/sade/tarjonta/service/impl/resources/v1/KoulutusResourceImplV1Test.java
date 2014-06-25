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
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;

import java.util.Calendar;
import org.apache.commons.lang.time.DateUtils;

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
public class KoulutusResourceImplV1Test extends KoulutusBase {

    @Before
    public void setUp() throws OIDCreationException {
        reload();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreateAndLoadToteutus() throws ExceptionMessage {
        KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();
        /*
         * KOMO data fields:
         */
        teksti(dto.getKoulutusohjelma(), KOULUTUSOHJELMA, URI_KIELI_FI);
        dto.getKoulutusohjelma().getTekstis().put(URI_KIELI_FI, toNimiValue("koulutusohjelma", URI_KIELI_FI));
        dto.getOrganisaatio().setOid(ORGANISATION_OID);
        dto.setKoulutusaste(toKoodiUri(KOULUTUSASTE));
        dto.setKoulutusala(toKoodiUri(KOULUTUSALA));
        dto.setOpintoala(toKoodiUri(OPINTOALA));
        dto.setTutkinto(toKoodiUri(TUTKINTO));
        dto.setKoulutustyyppi(toKoodiUri(KOULUTUSTYYPPI));

        dto.setEqf(toKoodiUri(EQF));
        dto.setTila(TarjontaTila.JULKAISTU);
        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO);
        dto.setTunniste(TUNNISTE);
        dto.setHinta(1.11);
        dto.setOpintojenMaksullisuus(Boolean.TRUE);
        dto.setKoulutuskoodi(toKoodiUri(KOULUTUSKOODI));
        dto.getKoulutuksenAlkamisPvms().add(DATE.toDate());

        koodiUrisMap(dto.getTutkintonimikes(), URI_KIELI_FI, MAP_TUTKINTONIMIKE);
        koodiUrisMap(dto.getOpetusAikas(), URI_KIELI_FI, MAP_OPETUSAIKAS);
        koodiUrisMap(dto.getOpetusPaikkas(), URI_KIELI_FI, MAP_OPETUSPAIKKAS);
        koodiUrisMap(dto.getAihees(), URI_KIELI_FI, MAP_OPETUSAIHEES);
        koodiUrisMap(dto.getOpetuskielis(), URI_KIELI_FI, MAP_OPETUSKIELI);
        koodiUrisMap(dto.getOpetusmuodos(), URI_KIELI_FI, MAP_OPETUMUOTO);
        koodiUrisMap(dto.getAmmattinimikkeet(), URI_KIELI_FI, (MAP_AMMATTINIMIKE));
        koodiUrisMap(dto.getPohjakoulutusvaatimukset(), URI_KIELI_FI, MAP_POHJAKOULUTUS);

        dto.setSuunniteltuKestoTyyppi(toKoodiUri(SUUNNITELTU_KESTO_TYYPPI));
        dto.setSuunniteltuKestoArvo(SUUNNITELTU_KESTO_VALUE);

        dto.getYhteyshenkilos().add(new YhteyshenkiloTyyppi(PERSON[0], PERSON[1], PERSON[2], PERSON[3], PERSON[4], PERSON[5], null, HenkiloTyyppi.YHTEYSHENKILO));
        dto.setOpintojenLaajuusarvo(toKoodiUri(LAAJUUSARVO));
        dto.setOpintojenLaajuusyksikko(toKoodiUri(LAAJUUSYKSIKKO));

        //EXPECT
        expect(organisaatioServiceMock.findByOid(ORGANISATION_OID)).andReturn(organisaatioDTO).times(3);
        //the calls of the OidServices must be in correct order!

        permissionChecker.checkCreateKoulutus(ORGANISATION_OID);
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(ORGANISATION_OID);

        /* 
         * KOODISTO DATA CALLS IN CORRECT CALL ORDER
         * 1th round, convert to entity 
         */
        expectKausi();
        expectMetaUri(KOULUTUSKOODI);
        expectMetaUri(TUTKINTO);
        expectMetaUri(LAAJUUSARVO);
        expectMetaUri(LAAJUUSYKSIKKO);
        expectMetaUri(KOULUTUSASTE);
        expectMetaUri(KOULUTUSALA);
        expectMetaUri(OPINTOALA);
        expectMetaMapUris(MAP_TUTKINTONIMIKE);
        expectMetaUri(EQF);
        expectMetaUri(KOULUTUSTYYPPI);

        expectMetaMapUris(MAP_OPETUSPAIKKAS);
        expectMetaMapUris(MAP_OPETUSAIKAS);
        expectMetaMapUris(MAP_OPETUSAIHEES);
        expectMetaMapUris(MAP_OPETUSKIELI);
        expectMetaMapUris(MAP_OPETUMUOTO);
        expectMetaMapUris(MAP_POHJAKOULUTUS);
        expectMetaUri(SUUNNITELTU_KESTO_TYYPPI);
        expectMetaMapUris(MAP_AMMATTINIMIKE);

        /* 2nd round, convert to dto */
        expectKausi();
        expectMetaUri(KOULUTUSKOODI);
        expectMetaUri(TUTKINTO);
        expectMetaUri(LAAJUUSARVO);
        expectMetaUri(LAAJUUSYKSIKKO);
        expectMetaUri(KOULUTUSASTE);
        expectMetaUri(KOULUTUSALA);
        expectMetaUri(OPINTOALA);
        expectMetaMapUris(MAP_TUTKINTONIMIKE);
        expectMetaUri(EQF);
        expectMetaUri(KOULUTUSTYYPPI);

        expectMetaMapUris(MAP_OPETUSPAIKKAS);
        expectMetaMapUris(MAP_OPETUSAIKAS);
        expectMetaMapUris(MAP_OPETUSAIHEES);
        expectMetaMapUris(MAP_OPETUSKIELI);
        expectMetaMapUris(MAP_OPETUMUOTO);
        expectMetaMapUris(MAP_POHJAKOULUTUS);
        expectMetaUri(SUUNNITELTU_KESTO_TYYPPI);
        expectMetaMapUris(MAP_AMMATTINIMIKE);

        //  expectKoulutusohjelmaUris(KOULUTUSOHELMA);

        /* REPLAY */
        replay(organisaatioServiceMock);
        replay(tarjontaKoodistoHelperMock);
        /*
         * INSERT KORKEAKOULU TO DB
         */
        ResultV1RDTO<KoulutusV1RDTO> v = instance.postKoulutus(dto);
        assertEquals("Validation errors", true, v.getErrors() != null ? v.getErrors().isEmpty() : true);

        /*
         * LOAD KORKEAKOULU DTO FROM DB
         */
        final ResultV1RDTO result = instance.findByOid(KOMOTO_OID, true, false, "FI");
        KoulutusKorkeakouluV1RDTO result1 = (KoulutusKorkeakouluV1RDTO) result.getResult();
        assertLoadData(result1);

        verify(organisaatioServiceMock);
    }

    private void assertLoadData(final KoulutusKorkeakouluV1RDTO result) {
        assertNotNull(result);

        assertEquals(KOMOTO_OID, result.getOid());
        assertEquals(ORGANISATION_OID, result.getOrganisaatio().getOid());
        assertEquals(ORGANISAATIO_NIMI, result.getOrganisaatio().getNimi());

        assertEquals(KoulutusasteTyyppi.KORKEAKOULUTUS, result.getKoulutusasteTyyppi());

        final String key = URI_KIELI_FI + "_uri";

        assertNotNull(KOULUTUSOHJELMA, result.getKoulutusohjelma().getTekstis().get(key));
        assertEqualDtoKoodi(KOULUTUSASTE, result.getKoulutusaste());
        assertEqualDtoKoodi(KOULUTUSALA, result.getKoulutusala());
        assertEqualDtoKoodi(OPINTOALA, result.getOpintoala());
        assertEqualDtoKoodi(EQF, result.getEqf());
        assertEqualDtoKoodi(KOULUTUSKOODI, result.getKoulutuskoodi());
        assertEqualDtoKoodi(LAAJUUSARVO, result.getOpintojenLaajuusarvo());
        assertEqualDtoKoodi(LAAJUUSYKSIKKO, result.getOpintojenLaajuusyksikko());
        assertEqualDtoKoodi(TUTKINTO, result.getTutkinto());

        assertEquals(TarjontaTila.JULKAISTU, result.getTila());
        assertEquals(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO, result.getKoulutusmoduuliTyyppi());
        assertEquals(KoulutusasteTyyppi.KORKEAKOULUTUS, result.getKoulutusasteTyyppi());
        assertEquals(TUNNISTE, result.getTunniste());
        assertEquals(new Double(1.11), result.getHinta());
        assertEquals(Boolean.TRUE, result.getOpintojenMaksullisuus());
        assertEquals((DateUtils.truncate(DATE.toDate(), Calendar.DATE)), result.getKoulutuksenAlkamisPvms().iterator().next());
        assertEqualDtoKoodi(KAUSI_KOODI_URI, result.getKoulutuksenAlkamiskausi(), true);
        assertEquals(VUOSI, result.getKoulutuksenAlkamisvuosi());

        assertEqualMetaDto(MAP_OPETUSAIHEES, result.getAihees());
        assertEqualMetaDto(MAP_OPETUSKIELI, result.getOpetuskielis());
        assertEqualMetaDto(MAP_OPETUMUOTO, result.getOpetusmuodos());
        assertEqualMetaDto(MAP_POHJAKOULUTUS, result.getPohjakoulutusvaatimukset());
        assertEqualMetaDto(MAP_AMMATTINIMIKE, result.getAmmattinimikkeet());
        assertEqualMetaDto(MAP_TUTKINTONIMIKE, result.getTutkintonimikes());

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
}
