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
package fi.vm.sade.tarjonta.service.impl.resources.v1.komo.validation;

import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class KomoValidator {

    private static final Logger LOG = LoggerFactory.getLogger(KomoValidator.class);
    private static final int DEFAULT_MIN_NONE = 0;

    /**
     * Required data validation for koulutus -type of objects.
     *
     * @param dto
     * @param result
     * @return validation flag: full stop == true
     */
    public static boolean validateBaseData(KomoV1RDTO dto, ResultV1RDTO result) {
        if (dto == null) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_INPUT_OBJECT_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_INPUT_OBJECT_MISSING.lower()));
            return true;
        }

        if (dto.getKoulutusmoduuliTyyppi() == null) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_MODUULI_TYYPPI_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_MODUULI_TYYPPI_MISSING.lower()));
        }

        if (dto.getKoulutusasteTyyppi() == null) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_KOULUTUSASTETYYPPI_ENUM_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_KOULUTUSASTETYYPPI_ENUM_MISSING.lower()));
        }

        if (dto.getTila() == null) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_TILA_ENUM_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_TILA_ENUM_MISSING.lower()));
        }

        return false;
    }

    public static ResultV1RDTO validateModuleKorkeakoulu(KomoV1RDTO dto, ResultV1RDTO result) {
        common(dto, result);

        if (dto.getOrganisaatio() == null || dto.getOrganisaatio().getOid() == null || dto.getOrganisaatio().getOid().isEmpty()) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING.lower()));
        }

        return result;
    }

    public static ResultV1RDTO validateModuleLukioAndAmm(KomoV1RDTO dto, ResultV1RDTO result) {
        switch (dto.getKoulutusmoduuliTyyppi()) {
            case TUTKINTO_OHJELMA:
                validateKoodi(result, dto.getKoulutusohjelma(), KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_INVALID);
                break;
        }

        common(dto, result);
        return result;
    }

    public static ResultV1RDTO validateModuleGeneric(KomoV1RDTO dto, ResultV1RDTO result) {
        common(dto, result);
        return result;
    }

    private static void common(KomoV1RDTO dto, ResultV1RDTO result) {
        validateKoodiUris(result, dto.getTutkintonimikes(), KoulutusValidationMessages.KOULUTUS_OPPILAITOSTYYPPI_MISSING, KoulutusValidationMessages.KOULUTUS_OPPILAITOSTYYPPI_INVALID, DEFAULT_MIN_NONE);
        validateTunniste(dto, result);
        validateKoodistoRelations(dto, result);
    }

    /**
     * Tarkista merkkijonon max pituus
     */
    private static void validateStringMaxLength(ResultV1RDTO result, String tunniste, int maxLength,
            KoulutusValidationMessages koulutusTunnusteLength) {
        if (tunniste.length() > maxLength) {
            result.addError(ErrorV1RDTO.createValidationError(koulutusTunnusteLength.getFieldName(), koulutusTunnusteLength.lower()));
        }
    }

    public static void validateTunniste(KomoV1RDTO dto, ResultV1RDTO result) {
        if (dto.getTunniste() != null) {
            validateStringMaxLength(result, dto.getTunniste(), 35, KoulutusValidationMessages.KOULUTUS_TUNNISTE_LENGTH);
        }
    }

    private static void validateKoodistoRelations(KomoV1RDTO dto, ResultV1RDTO result) {
        validateKoodiUris(result, dto.getTutkintonimikes(), KoulutusValidationMessages.KOULUTUS_OPPILAITOSTYYPPI_MISSING, KoulutusValidationMessages.KOULUTUS_OPPILAITOSTYYPPI_INVALID, DEFAULT_MIN_NONE);

        if (dto.getEqf() != null) {
            validateKoodi(result, dto.getEqf(), KoulutusValidationMessages.KOULUTUS_EQF_MISSING, KoulutusValidationMessages.KOULUTUS_EQF_INVALID);
        }

        if (dto.getNqf() != null) {
            validateKoodi(result, dto.getNqf(), KoulutusValidationMessages.KOULUTUS_NQF_MISSING, KoulutusValidationMessages.KOULUTUS_NQF_INVALID);
        }

        if (dto.getKoulutusala() != null) {
            validateKoodi(result, dto.getKoulutusala(), KoulutusValidationMessages.KOULUTUS_KOULUTUSALA_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSALA_INVALID);
        }
        if (dto.getKoulutusaste() != null) {
            validateKoodi(result, dto.getKoulutusaste(), KoulutusValidationMessages.KOULUTUS_KOULUTUSASTE_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSASTE_INVALID);
        }

        if (dto.getOpintoala() != null) {
            validateKoodi(result, dto.getOpintoala(), KoulutusValidationMessages.KOULUTUS_OPINTOALA_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOALA_INVALID);
        }

        if (dto.getOpintojenLaajuusarvo() != null) {
            validateKoodi(result, dto.getOpintojenLaajuusarvo(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_INVALID);
        }

        if (dto.getOpintojenLaajuusyksikko() != null) {
            validateKoodi(result, dto.getOpintojenLaajuusyksikko(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_INVALID);
        }

        if (dto.getTutkinto() != null) {
            validateKoodi(result, dto.getTutkinto(), KoulutusValidationMessages.KOULUTUS_TUTKINTO_MISSING, KoulutusValidationMessages.KOULUTUS_TUTKINTO_INVALID);
        }

        if (dto.getKoulutustyyppi() != null) {
            validateKoodi(result, dto.getKoulutustyyppi(), KoulutusValidationMessages.KOULUTUS_KOULUTUSTYYPPI_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSTYYPPI_INVALID);
        }

        if (dto.getTutkintonimikes() != null) {
            validateKoodiUris(result, dto.getTutkintonimikes(), KoulutusValidationMessages.KOULUTUS_TUTKINTONIMIKE_MISSING, KoulutusValidationMessages.KOULUTUS_TUTKINTONIMIKE_INVALID, DEFAULT_MIN_NONE);
        }
    }

    /**
     * True when valid string.
     */
    public static boolean notNullStrOrEmpty(final String str) {
        return str != null && !(str.trim().isEmpty());
    }

    /**
     * True when valid integer.
     */
    private static boolean isPositiveInteger(final Integer val) {
        return val != null && val > 0;
    }

    /**
     * True when valid uri and version. TODO: koodi uri pattern validation.
     */
    public static boolean isValidKoodiUriWithVersion(final KoodiV1RDTO koodi) {
        if (koodi == null) {
            return false;
        }

        return notNullStrOrEmpty(koodi.getUri()) && isPositiveInteger(koodi.getVersio());
    }

    /**
     * True when valid uri and version. TODO: koodi uri pattern validation.
     *
     * min : null no limit
     */
    private static boolean isValidKoodiUrisWithVersion(Map<String, Integer> map, Integer min) {
        if (map == null) {
            return false;
        } else if (min != null && min > map.size()) {
            return false;
        }

        for (Entry<String, Integer> e : map.entrySet()) {
            if (!notNullStrOrEmpty(e.getKey())) {
                return false;
            }

            if (!isPositiveInteger(e.getValue())) {
                return false;
            }
        }

        return true;
    }

    private static boolean validateKoodi(ResultV1RDTO result, KoodiV1RDTO dto, KoulutusValidationMessages missing, KoulutusValidationMessages invalid) {
        if (!isValidKoodiUriWithVersion(dto)) {
            result.addError(ErrorV1RDTO.createValidationError(missing.getFieldName(), missing.lower()));
            return false;
        }

        return true;
    }

    public static boolean validateKoodiUris(ResultV1RDTO result, KoodiUrisV1RDTO dto, KoulutusValidationMessages missing, KoulutusValidationMessages invalid, Integer min) {
        if (dto == null || dto.getUris() == null) {
            result.addError(ErrorV1RDTO.createValidationError(missing.getFieldName(), missing.lower()));
            return false;
        }

        if (!isValidKoodiUrisWithVersion(dto.getUris(), min)) {
            result.addError(ErrorV1RDTO.createValidationError(missing.getFieldName(), missing.lower()));
            return false;
        }

        return true;
    }

    /* REQUIRED FIELDS:
     "meta" : {
     "kieli_sv" : {
     "koodi" : {
     "uri" : "kieli_sv",
     "versio" : "1",
     "arvo" : "koulutusohjelma xxx"
     },
     }
     */
    private static void validateTextOneOrMany(ResultV1RDTO result, NimiV1RDTO dto,
            KoulutusValidationMessages missing,
            KoulutusValidationMessages invalid,
            KoulutusValidationMessages invalidTextValue) {
        Set<KoulutusValidationMessages> tempError = Sets.<KoulutusValidationMessages>newHashSet();

        if (dto.getTekstis().isEmpty()) {
            //no items
            result.addError(ErrorV1RDTO.createValidationError(invalid.getFieldName(), invalid.lower()));
        } else {
            for (Entry<String, String> e : dto.getTekstis().entrySet()) {
                if (notNullStrOrEmpty(e.getValue())) {
                    //validation was success
                    //at least one text items was available and correct;
                    return;
                } else {
                    //validation will fail
                    tempError.add(missing);
                }
            }

            //all set items are empty or null values;
            for (KoulutusValidationMessages e : tempError) {
                result.addError(ErrorV1RDTO.createValidationError(e.getFieldName(), e.lower()));
            }
        }
    }

    public static void validateKieliUri(final String kieliUri, final String errorInObjectfieldname, ResultV1RDTO result) {
        if (kieliUri == null || kieliUri.isEmpty()) {
            result.addError(ErrorV1RDTO.createValidationError(errorInObjectfieldname, "error_missing_uri"));
        } else if (!KoodistoURI.isValidKieliUri(kieliUri)) {
            result.addError(ErrorV1RDTO.createValidationError(errorInObjectfieldname, "error_invalid_uri"));
        }
    }

    public static void validateModuleUpdate(final Koulutusmoduuli komo, ResultV1RDTO dto) {
        if (komo == null) {
            dto.addError(ErrorV1RDTO.createValidationError("oid", KoulutusValidationMessages.KOULUTUS_KOMO_MISSING.lower()));
            dto.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
        } else {
            //check is deleted
            checkIsDeleted(komo, dto);
        }
    }

    private static void checkIsDeleted(final Koulutusmoduuli komo, ResultV1RDTO dto) {
        Preconditions.checkNotNull(komo, "Koulutusmoduulin toteutus cannot be null!");
        Preconditions.checkNotNull(komo.getTila(), "Status cannot be null!");
        if (komo.getTila().equals(TarjontaTila.POISTETTU)) {
            dto.addError(ErrorV1RDTO.createValidationError("komoto.tila", KoulutusValidationMessages.KOULUTUS_DELETED.lower(), komo.getOid()));
        }
    }

}
