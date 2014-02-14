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
package fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusKorkeakouluDTOConverterToEntity;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class KoulutusValidator {

    public static List<ErrorV1RDTO> validateKoulutus(KoulutusKorkeakouluV1RDTO dto) {
        Set<KoulutusValidationMessages> validationMessages = Sets.<KoulutusValidationMessages>newHashSet();
        validateKoodistoRelations(dto, validationMessages);

        validateKoodiUris(validationMessages, dto.getOpetusmuodos(), KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_INVALID);
        validateKoodiUris(validationMessages, dto.getAihees(), KoulutusValidationMessages.KOULUTUS_TEEMAT_AIHEET_MISSING, KoulutusValidationMessages.KOULUTUS_TEEMAT_AIHEET_INVALID);
        validateKoodiUris(validationMessages, dto.getOpetusAikas(), KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_INVALID);
        validateKoodiUris(validationMessages, dto.getOpetusPaikkas(), KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_INVALID);
        validateKoodiUris(validationMessages, dto.getOpetuskielis(), KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_INVALID);

        validateNameKoulutusohjelma(validationMessages, dto);
        validateAlkamisPvms(validationMessages, dto);
        validateSuuniteltukesto(validationMessages, dto);
        ArrayList<ErrorV1RDTO> newArrayList = Lists.<ErrorV1RDTO>newArrayList();

        for (KoulutusValidationMessages e : validationMessages) {
            ErrorV1RDTO errorV1RDTO = new ErrorV1RDTO();
            errorV1RDTO.setErrorMessageKey(e.name().toLowerCase());
            newArrayList.add(errorV1RDTO);
        }

        return newArrayList;
    }

    private static void validateNameKoulutusohjelma(Set<KoulutusValidationMessages> validationMessages, KoulutusKorkeakouluV1RDTO dto) {
        validateTextOneOrMany(validationMessages, dto.getKoulutusohjelma(),
                KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_NAME_MISSING,
                KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_INVALID,
                KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_INVALID_VALUE);
    }

    private static void validateKoodistoRelations(KoulutusKorkeakouluV1RDTO dto, Set<KoulutusValidationMessages> validationMessages) {
        validateKoodi(validationMessages, dto.getEqf(), KoulutusValidationMessages.KOULUTUS_EQF_MISSING, KoulutusValidationMessages.KOULUTUS_EQF_INVALID);
        validateKoodi(validationMessages, dto.getKoulutusala(), KoulutusValidationMessages.KOULUTUS_KOULUTUSALA_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSALA_INVALID);
        validateKoodi(validationMessages, dto.getKoulutusaste(), KoulutusValidationMessages.KOULUTUS_KOULUTUSASTE_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSASTE_INVALID);
        validateKoodi(validationMessages, dto.getKoulutuskoodi(), KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_INVALID);
        validateKoodi(validationMessages, dto.getOpintoala(), KoulutusValidationMessages.KOULUTUS_OPINTOALA_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOALA_INVALID);
        validateKoodi(validationMessages, dto.getOpintojenLaajuusarvo(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_INVALID);
        validateKoodi(validationMessages, dto.getOpintojenLaajuusyksikko(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_INVALID);
        
        validateKoodi(validationMessages, dto.getTutkinto(), KoulutusValidationMessages.KOULUTUS_TUTKINTO_MISSING, KoulutusValidationMessages.KOULUTUS_TUTKINTO_INVALID);
        validateKoodiUris(validationMessages, dto.getTutkintonimikes(), KoulutusValidationMessages.KOULUTUS_TUTKINTONIMIKE_MISSING, KoulutusValidationMessages.KOULUTUS_TUTKINTONIMIKE_INVALID);
    }

    private static boolean notNullOrEmpty(final List list) {
        return notNull(list) && !list.isEmpty();
    }

    private static boolean notNull(final List list) {
        return list != null;
    }

    private static boolean notNullStr(final String str) {
        return str != null;
    }

    private static boolean notNullStrOrEmpty(final String str) {
        return notNullStr(str) && !str.isEmpty();
    }

    private static boolean isInteger(final Integer val) {
        return val == null;
    }

    private static boolean isDouble(final Double val) {
        return val == null;
    }

    private static boolean requireKoodiUriWithVersion(final KoodiV1RDTO koodi) {
        return !notNullStrOrEmpty(koodi.getUri());
    }

    private static boolean requireKoodiUrisWithVersion(Map<String, Integer> map) {
        if (map == null) {
            return false;
        }

        for (Entry<String, Integer> e : map.entrySet()) {
            if (!notNullStrOrEmpty(e.getKey())) {
                return false;
            }

            if (!isInteger(e.getValue())) {
                return false;
            }
        }
        return true;
    }

    private static boolean validateKoodi(Set<KoulutusValidationMessages> validationMessages, KoodiV1RDTO dto, KoulutusValidationMessages missing, KoulutusValidationMessages invalid) {
        if (dto == null) {
            validationMessages.add(missing);
            return false;
        }
        if (requireKoodiUriWithVersion(dto)) {
            validationMessages.add(missing);
            return false;
        }

        return true;
    }

    private static boolean validateKoodiUris(Set<KoulutusValidationMessages> validationMessages, KoodiUrisV1RDTO dto, KoulutusValidationMessages missing, KoulutusValidationMessages invalid) {
        if (dto == null) {
            validationMessages.add(missing);
            return false;
        }

        if (requireKoodiUrisWithVersion(dto.getUris())) {
            validationMessages.add(missing);
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
    private static void validateTextOneOrMany(Set<KoulutusValidationMessages> validationMessages, NimiV1RDTO dto,
            KoulutusValidationMessages missing,
            KoulutusValidationMessages invalidKoodi,
            KoulutusValidationMessages invalidValue) {
        if (dto.getTekstis().isEmpty()) {
            validationMessages.add(missing);
        } else {
            for (Entry<String, String> e : dto.getTekstis().entrySet()) {

                if (!notNullStrOrEmpty(e.getValue())) {
                    validationMessages.add(invalidValue);
                }
            }
        }
    }

    private static void checkKausiVuosi(Set<KoulutusValidationMessages> validationMessages, KoodiV1RDTO kausi, Integer year) {
        if (kausi == null) {
            validationMessages.add(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_MISSING);
        } else if (requireKoodiUriWithVersion(kausi)) {
            validationMessages.add(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_INVALID);
        }

        if (year == null) {
            validationMessages.add(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_MISSING);
        } else if (year < 2000) {
            validationMessages.add(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_INVALID);
        }

    }

    private static void validateAlkamisPvms(Set<KoulutusValidationMessages> validationMessages, KoulutusKorkeakouluV1RDTO dto) {
        if (dto.getKoulutuksenAlkamisPvms() == null) {
            validationMessages.add(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_MISSING);
        } else if (!dto.getKoulutuksenAlkamisPvms().isEmpty()) {
            final Set<Date> koulutuksenAlkamisPvms = dto.getKoulutuksenAlkamisPvms();
            KoulutusValidationMessages validateDates = KoulutusKorkeakouluDTOConverterToEntity.validateDates(
                    koulutuksenAlkamisPvms.iterator().next(),
                    dto.getKoulutuksenAlkamisPvms());

            if (!validateDates.equals(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_SUCCESS)) {
                validationMessages.add(validateDates);
            }
        } else {
            checkKausiVuosi(validationMessages, dto.getKoulutuksenAlkamiskausi(), dto.getKoulutuksenAlkamisvuosi());
        }
    }

    private static void validateSuuniteltukesto(Set<KoulutusValidationMessages> validationMessages, KoulutusKorkeakouluV1RDTO dto) {
        if (!notNullStrOrEmpty(dto.getSuunniteltuKestoArvo())) {
            validationMessages.add(KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_VALUE_MISSING);
        }

        validateKoodi(validationMessages, dto.getSuunniteltuKestoTyyppi(), KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_TYPE_MISSING, KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_TYPE_INVALID);
    }
}
