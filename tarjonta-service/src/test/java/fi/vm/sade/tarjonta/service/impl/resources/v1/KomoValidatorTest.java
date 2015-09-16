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

import fi.vm.sade.tarjonta.service.impl.resources.v1.komo.validation.KomoValidator;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author jani
 */
public class KomoValidatorTest extends KoulutusBase {

    private static final String KOULUTUSOHJELMA = "koulutus_koulutusohjelma_missing";
    private static final String OHJELMA = "koulutus_tutkinto_ohjelma_uri_required";

    @Test
    public void testValidateModuleLukioAndAmmTutkinto() {
        KomoV1RDTO tutkinto = createDTO(KoulutusmoduuliTyyppi.TUTKINTO);
        ResultV1RDTO result = new ResultV1RDTO();
        KomoValidator.validateModuleGeneric(tutkinto, result);
        assertEquals(null, result.getErrors());
    }

    @Test
    public void testValidateModuleLukioAndAmmTutkintoOhjelmaNull() {
        KomoV1RDTO tutkinto = createDTO(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        tutkinto.setKoulutusohjelma(null);
        tutkinto.setOsaamisala(null);
        tutkinto.setLukiolinja(null);
        ResultV1RDTO result = new ResultV1RDTO();
        KomoValidator.validateModuleGeneric(tutkinto, result);
        assertEquals(OHJELMA, ((ErrorV1RDTO) result.getErrors().get(0)).getErrorMessageKey());
    }

    @Test
    public void testValidateModuleLukioAndAmmTutkintoOhjelma() {
        KomoV1RDTO tutkinto = createDTO(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        ResultV1RDTO result = new ResultV1RDTO();
        KomoValidator.validateModuleGeneric(tutkinto, result);
        assertEquals(null, result.getErrors());

        tutkinto = createDTO(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        tutkinto.setOsaamisala(toKoodiUri(FieldNames.OSAAMISALA.toString()));
        result = new ResultV1RDTO();
        KomoValidator.validateModuleGeneric(tutkinto, result);
        assertEquals(null, result.getErrors());

        tutkinto = createDTO(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        tutkinto.setKoulutusohjelma(null);
        tutkinto.setOsaamisala(toKoodiUri(FieldNames.OSAAMISALA.toString()));
        result = new ResultV1RDTO();
        KomoValidator.validateModuleGeneric(tutkinto, result);
        assertEquals(null, result.getErrors());

        tutkinto = createDTO(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        NimiV1RDTO nimi = new NimiV1RDTO();
        nimi.setUri("uri");
        tutkinto.setKoulutusohjelma(nimi);
        tutkinto.setOsaamisala(null);
        result = new ResultV1RDTO();
        KomoValidator.validateModuleGeneric(tutkinto, result);
        assertEquals(KOULUTUSOHJELMA, ((ErrorV1RDTO) result.getErrors().get(0)).getErrorMessageKey());

        tutkinto = createDTO(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        nimi = new NimiV1RDTO();
        nimi.setVersio(1);
        tutkinto.setKoulutusohjelma(nimi);
        tutkinto.setOsaamisala(null);
        result = new ResultV1RDTO();
        KomoValidator.validateModuleGeneric(tutkinto, result);
        assertEquals(KOULUTUSOHJELMA, ((ErrorV1RDTO) result.getErrors().get(0)).getErrorMessageKey());

        tutkinto = createDTO(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        nimi = new NimiV1RDTO();
        nimi.setVersio(1);
        tutkinto.setKoulutusohjelma(nimi);
        tutkinto.setOsaamisala(toKoodiUri(FieldNames.OSAAMISALA.toString()));
        result = new ResultV1RDTO();
        KomoValidator.validateModuleGeneric(tutkinto, result);
        assertEquals(KOULUTUSOHJELMA, ((ErrorV1RDTO) result.getErrors().get(0)).getErrorMessageKey());
    }

    private KomoV1RDTO createDTO(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi tyyppi) {
        KomoV1RDTO dto = new KomoV1RDTO();
        dto.getOrganisaatio().setOid(ORGANISATION_OID);
        dto.setKoulutusaste(toKoodiUri(KOULUTUSASTE));
        dto.setKoulutusala(toKoodiUri(KOULUTUSALA));
        dto.setOpintoala(toKoodiUri(OPINTOALA));
        dto.setTutkinto(toKoodiUri(TUTKINTO));
        dto.setEqf(toKoodiUri(EQF));
        dto.setNqf(toKoodiUri(NQF));
        dto.setKoulutusmoduuliTyyppi(tyyppi);
        dto.setTunniste(TUNNISTE);
        dto.setKomoOid(KOMO_CHILD_OID);
        dto.setKoulutuskoodi(toKoodiUri(KOULUTUSKOODI));
        dto.setKoulutusohjelma(toNimiKoodiUri(KOULUTUSOHJELMA));
        dto.setSuunniteltuKestoTyyppi(toKoodiUri(SUUNNITELTU_KESTO_TYYPPI));
        dto.setSuunniteltuKestoArvo(SUUNNITELTU_KESTO_VALUE);
        dto.setOpintojenLaajuusarvo(toKoodiUri(LAAJUUSARVO));
        dto.setOpintojenLaajuusyksikko(toKoodiUri(LAAJUUSYKSIKKO));

        KoodiUrisV1RDTO oppilaitostyyppi = new KoodiUrisV1RDTO();
        Map mapTypes = new HashMap<String, Integer>();
        mapTypes.put(OPPILAITOSTYYPPI, 1);
        oppilaitostyyppi.setUris(mapTypes);
        dto.setOppilaitostyyppis(oppilaitostyyppi);

        KoodiUrisV1RDTO tutkintonimike = new KoodiUrisV1RDTO();
        Map mapNimikes = new HashMap<String, Integer>();
        mapNimikes.put(TUTKINTONIMIKE, 1);
        tutkintonimike.setUris(mapNimikes);
        dto.setTutkintonimikes(tutkintonimike);

        KoodiUrisV1RDTO koulutustyyppi = new KoodiUrisV1RDTO();
        Map mapKoulutusTypes = new HashMap<String, Integer>();
        mapKoulutusTypes.put(KOULUTUSTYYPPI, 1);
        koulutustyyppi.setUris(mapKoulutusTypes);
        dto.setKoulutustyyppis(koulutustyyppi);

        return dto;
    }
}
