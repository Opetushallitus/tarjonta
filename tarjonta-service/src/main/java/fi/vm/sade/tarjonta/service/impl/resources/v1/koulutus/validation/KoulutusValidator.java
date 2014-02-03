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
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class KoulutusValidator {

    public static List<KoulutusValidationMessages> validateKoulutus(KoulutusKorkeakouluV1RDTO dto) {
        Set<KoulutusValidationMessages> validationMessages = Sets.<KoulutusValidationMessages>newHashSet();
        validateKoodistoRelations(dto, validationMessages);
        validateKoodiUris(validationMessages, dto.getAihees(), KoulutusValidationMessages.KOULUTUS_INVALID_KOODI_TEEMAT_AIHEET);
        validateKoodiUris(validationMessages, dto.getOpetusAikas(), KoulutusValidationMessages.KOULUTUS_INVALID_KOODI_OPETUSAIKA);
        validateKoodiUris(validationMessages, dto.getOpetusPaikkas(), KoulutusValidationMessages.KOULUTUS_INVALID_KOODI_OPETUSPAIKKA);
        validateKoodiUris(validationMessages, dto.getOpetuskielis(), KoulutusValidationMessages.KOULUTUS_INVALID_KOODI_OPETUSKIELI);

        validateNameKoulutusohjelma(dto, validationMessages);
        
        return Lists.<KoulutusValidationMessages>newArrayList(validationMessages);
    }

    private static void validateNameKoulutusohjelma(KoulutusKorkeakouluV1RDTO dto, Set<KoulutusValidationMessages> validationMessages) {
        validateTextOneOrMany(validationMessages, dto.getKoulutusohjelma(),
                KoulutusValidationMessages.KOULUTUS_MISSING_KOULUTUSOHJELMA,
                KoulutusValidationMessages.KOULUTUS_INVALID_KOULUTUSOHJELMA_KOODI,
                KoulutusValidationMessages.KOULUTUS_INVALID_KOULUTUSOHJELMA_NAME);
    }

    private static void validateKoodistoRelations(KoulutusmoduuliRelationV1RDTO dto, Set<KoulutusValidationMessages> validationMessages) {
        validateKoodi(validationMessages, dto.getEqf(), KoulutusValidationMessages.KOULUTUS_INVALID_KOODI_EQF);
        validateKoodi(validationMessages, dto.getKoulutusala(), KoulutusValidationMessages.KOULUTUS_INVALID_KOODI_KOULUTUSALA);
        validateKoodi(validationMessages, dto.getKoulutusaste(), KoulutusValidationMessages.KOULUTUS_INVALID_KOODI_KOULUTUSASTE);
        validateKoodi(validationMessages, dto.getKoulutuskoodi(), KoulutusValidationMessages.KOULUTUS_INVALID_KOODI_KOULUTUSKOODI);
        validateKoodi(validationMessages, dto.getOpintoala(), KoulutusValidationMessages.KOULUTUS_INVALID_KOODI_OPINTOALA);
        validateKoodi(validationMessages, dto.getOpintojenLaajuus(), KoulutusValidationMessages.KOULUTUS_INVALID_KOODI_OPINTOJENLAAJUUS);
        validateKoodi(validationMessages, dto.getTutkinto(), KoulutusValidationMessages.KOULUTUS_INVALID_KOODI_TUTKINTO);

        validateKoodiUris(validationMessages, dto.getTutkintonimikes(), KoulutusValidationMessages.KOULUTUS_INVALID_KOODI_TUTKINTONIMIKE);
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

    private static boolean validateKoodi(Set<KoulutusValidationMessages> validationMessages, KoodiV1RDTO dto, KoulutusValidationMessages msg) {
        if (requireKoodiUriWithVersion(dto)) {
            validationMessages.add(msg);
            return false;
        }

        return true;
    }

    private static boolean validateKoodiUris(Set<KoulutusValidationMessages> validationMessages, KoodiUrisV1RDTO dto, KoulutusValidationMessages msg) {
        if (requireKoodiUrisWithVersion(dto.getUris())) {
            validationMessages.add(msg);
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
            KoulutusValidationMessages invalidName) {
        if (dto.getTekstis().isEmpty()) {
            validationMessages.add(missing);
        } else {
            for (Entry<String, String> e : dto.getTekstis().entrySet()) {
                if (!notNullStrOrEmpty(e.getValue())) {
                    validationMessages.add(invalidName);
                }
            }
        }
    }
}
