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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.enums.KoulutustyyppiEnum;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusCommonConverter;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.ImageMimeValidator;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class KoulutusValidator {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusValidator.class);
    private static final int DEFAULT_MIN = 1;

    /**
     * Required data validation for koulutus -type of objects.
     *
     * @param koulutus
     * @param result
     * @return validation flag: full stop == true
     */
    public static boolean validateBaseKoulutusData(KoulutusV1RDTO koulutus, ResultV1RDTO result) {
        if (koulutus == null) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_INPUT_OBJECT_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_INPUT_OBJECT_MISSING.lower()));
            return true;
        }

        if (koulutus.getKoulutusasteTyyppi() == null) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_KOULUTUSASTETYYPPI_ENUM_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_KOULUTUSASTETYYPPI_ENUM_MISSING.lower()));
        }

        if (koulutus.getTila() == null) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_TILA_ENUM_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_TILA_ENUM_MISSING.lower()));
        }

        if (koulutus.getOrganisaatio() == null || koulutus.getOrganisaatio().getOid() == null || koulutus.getOrganisaatio().getOid().isEmpty()) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING.lower()));
        }

        return false;
    }

    public static ResultV1RDTO validateKoulutusKorkeakoulu(KoulutusKorkeakouluV1RDTO dto, ResultV1RDTO result) {
        if (validateBaseKoulutusData(dto, result)) {
            //a major validation error, validation must stop now!
            return result;
        }

        validateKoodistoRelationsKorkeakoulu(dto, result);
        validateTunniste(dto, result);
        validateKoodiUris(result, dto.getOpetusmuodos(), KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_INVALID, DEFAULT_MIN);
        validateKoodiUris(result, dto.getAihees(), KoulutusValidationMessages.KOULUTUS_TEEMAT_AIHEET_MISSING, KoulutusValidationMessages.KOULUTUS_TEEMAT_AIHEET_INVALID, DEFAULT_MIN);
        validateKoodiUris(result, dto.getOpetusAikas(), KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_INVALID, DEFAULT_MIN);
        validateKoodiUris(result, dto.getOpetusPaikkas(), KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_INVALID, DEFAULT_MIN);
        validateKoodiUris(result, dto.getOpetuskielis(), KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_INVALID, DEFAULT_MIN);

        validateNameKoulutusohjelma(result, dto);
        validateAlkamisPvms(result, dto);
        validateSuuniteltukesto(result, dto);

        return result;
    }

    public static ResultV1RDTO validateKoulutusLukio(KoulutusLukioV1RDTO dto, ResultV1RDTO result) {
        if (validateBaseKoulutusData(dto, result)) {
            //a major validation error, validation must stop now!
            return result;
        }

        validateKoodistoRelationsLukio(dto, result);
        validateKoodiUris(result, dto.getOpetusmuodos(), KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_INVALID, DEFAULT_MIN);
        validateKoodiUris(result, dto.getOpetusAikas(), KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_INVALID, DEFAULT_MIN);
        validateKoodiUris(result, dto.getOpetusPaikkas(), KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_INVALID, DEFAULT_MIN);
        validateKoodiUris(result, dto.getOpetuskielis(), KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_INVALID, DEFAULT_MIN);

        validateAlkamisPvms(result, dto);
        validateSuuniteltukesto(result, dto);
        return result;
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

    public static void validateTunniste(KoulutusKorkeakouluV1RDTO dto, ResultV1RDTO result) {
        if (dto.getTunniste() != null) {
            validateStringMaxLength(result, dto.getTunniste(), 35, KoulutusValidationMessages.KOULUTUS_TUNNISTE_LENGTH);
        }
    }

    private static void validateNameKoulutusohjelma(ResultV1RDTO result, KoulutusKorkeakouluV1RDTO dto) {
        validateTextOneOrMany(result, dto.getKoulutusohjelma(),
                KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_NAME_MISSING,
                KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_INVALID,
                KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_INVALID_VALUE);
    }

    private static void validateKoodistoRelationsKorkeakoulu(KoulutusKorkeakouluV1RDTO dto, ResultV1RDTO result) {
        validateKoodi(result, dto.getEqf(), KoulutusValidationMessages.KOULUTUS_EQF_MISSING, KoulutusValidationMessages.KOULUTUS_EQF_INVALID);
        validateKoodi(result, dto.getKoulutusala(), KoulutusValidationMessages.KOULUTUS_KOULUTUSALA_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSALA_INVALID);
        validateKoodi(result, dto.getKoulutusaste(), KoulutusValidationMessages.KOULUTUS_KOULUTUSASTE_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSASTE_INVALID);
        validateKoodi(result, dto.getKoulutuskoodi(), KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_INVALID);
        validateKoodi(result, dto.getOpintoala(), KoulutusValidationMessages.KOULUTUS_OPINTOALA_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOALA_INVALID);
        validateKoodi(result, dto.getOpintojenLaajuusarvo(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_INVALID);
        validateKoodi(result, dto.getOpintojenLaajuusyksikko(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_INVALID);

        validateKoodi(result, dto.getTutkinto(), KoulutusValidationMessages.KOULUTUS_TUTKINTO_MISSING, KoulutusValidationMessages.KOULUTUS_TUTKINTO_INVALID);
        validateKoodiUris(result, dto.getTutkintonimikes(), KoulutusValidationMessages.KOULUTUS_TUTKINTONIMIKE_MISSING, KoulutusValidationMessages.KOULUTUS_TUTKINTONIMIKE_INVALID, DEFAULT_MIN);
    }

    private static void validateKoodistoRelationsLukio(KoulutusLukioV1RDTO dto, ResultV1RDTO result) {
        // TODO:  kun relaatiot on tehty koodistoon
        // alidateKoodi(result, dto.getNqf(), KoulutusValidationMessages.KOULUTUS_NQF_MISSING, KoulutusValidationMessages.KOULUTUS_NQF_INVALID);
        // validateKoodi(result, dto.getEqf(), KoulutusValidationMessages.KOULUTUS_EQF_MISSING, KoulutusValidationMessages.KOULUTUS_EQF_INVALID);
        validateKoodi(result, dto.getKoulutusohjelma(), KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_INVALID);
        validateKoodi(result, dto.getKoulutusala(), KoulutusValidationMessages.KOULUTUS_KOULUTUSALA_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSALA_INVALID);
        validateKoodi(result, dto.getKoulutuskoodi(), KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_INVALID);
        validateKoodi(result, dto.getOpintojenLaajuusarvo(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_INVALID);
        validateKoodi(result, dto.getOpintojenLaajuusyksikko(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_INVALID);
        validateKoodi(result, dto.getKoulutuslaji(), KoulutusValidationMessages.KOULUTUS_KOULUTUSLAJI_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSLAJI_INVALID);
        validateKoodi(result, dto.getTutkintonimike(), KoulutusValidationMessages.KOULUTUS_TUTKINTONIMIKE_MISSING, KoulutusValidationMessages.KOULUTUS_TUTKINTONIMIKE_INVALID);
        validateKoodi(result, dto.getPohjakoulutusvaatimus(), KoulutusValidationMessages.KOULUTUS_POHJAKOULUTUSVAATIMUS_MISSING, KoulutusValidationMessages.KOULUTUS_POHJAKOULUTUSVAATIMUS_INVALID);
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
        /*
         else if(xxx){
         //TODO: validate uri pattern / search koodi by koodi uri 
         result.addError(ErrorV1RDTO.createValidationError(invalid.getFieldName(), invalid.lower()));
         }
         */

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
        /*
         else if(xxx){
         //TODO: validate uri pattern / search koodi by koodi uri 
         result.addError(ErrorV1RDTO.createValidationError(invalid.getFieldName(), invalid.lower()));
         }
         */

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

    private static void checkKausiVuosi(ResultV1RDTO result, KoodiV1RDTO kausi, Integer year) {
        if (kausi == null || isEmpty(kausi.getUri())) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_MISSING.lower()));

        } else if (!KoodistoURI.isValidKausiUri(kausi.getUri())) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_INVALID.getFieldName(), KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_INVALID.lower()));
        }

        if (year == null) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_MISSING.lower()));

        } else if (year < 2000) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_INVALID.getFieldName(), KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_INVALID.lower()));
        }
    }

    private static void validateAlkamisPvms(ResultV1RDTO result, KoulutusV1RDTO dto) {
        if (dto.getKoulutuksenAlkamisPvms() == null) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_MISSING.lower()));

        } else if (!dto.getKoulutuksenAlkamisPvms().isEmpty()) {

            final Set<Date> koulutuksenAlkamisPvms = dto.getKoulutuksenAlkamisPvms();
            KoulutusValidationMessages validateDates = KoulutusCommonConverter.validateDates(
                    koulutuksenAlkamisPvms.iterator().next(),
                    dto.getKoulutuksenAlkamisPvms());

            if (!validateDates.equals(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_SUCCESS)) {
                result.addError(ErrorV1RDTO.createValidationError(validateDates.getFieldName(), validateDates.lower()));
            }
        } else {
            checkKausiVuosi(result, dto.getKoulutuksenAlkamiskausi(), dto.getKoulutuksenAlkamisvuosi());
        }
    }

    private static void validateSuuniteltukesto(ResultV1RDTO result, KoulutusV1RDTO dto) {
        if (!notNullStrOrEmpty(dto.getSuunniteltuKestoArvo())) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_VALUE_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_VALUE_MISSING.lower()));
        }

        validateKoodi(result, dto.getSuunniteltuKestoTyyppi(), KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_TYPE_MISSING, KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_TYPE_INVALID);
    }

    public static void validateKoulutusKuva(KuvaV1RDTO kuva, ResultV1RDTO result) {
        validateKoulutusKuva(kuva, null, result);
    }

    public static void validateKoulutusKuva(final KuvaV1RDTO kuva, final String kieliUri, ResultV1RDTO result) {
        if (validateKieliUri(kuva.getKieliUri() != null ? kuva.getKieliUri() : kieliUri, "kieliUri", result)) {
            //invalid kieli uri, fail fast.
            return;
        }

        String raw = kuva.getBase64data();
        if (raw == null || raw.isEmpty()) {
            result.addError(ErrorV1RDTO.createValidationError("base64data", "error_missing_base64_data"));
        } else {
            /*
             * Data validation check
             */
            if (getValidBase64Image(raw) == null) {
                result.addError(ErrorV1RDTO.createValidationError("base64data", "error_invalid_base64_data"));
            }
        }

        validateMimeType(kuva.getMimeType(), "mimeType", result);

    }

    public static void validateMimeType(String mimeType, final String errorInObjectfieldname, ResultV1RDTO result) {
        if (mimeType == null || mimeType.isEmpty()) {
            result.addError(ErrorV1RDTO.createValidationError(errorInObjectfieldname, "error_missing_mime_type"));
        } else if (ImageMimeValidator.validate(mimeType)) {
            result.addError(ErrorV1RDTO.createValidationError(errorInObjectfieldname, "error_unrecognized_mime_type"));
        }
    }

    public static boolean validateKieliUri(final String kieliUri, final String errorInObjectfieldname, ResultV1RDTO result) {
        if (kieliUri == null || kieliUri.isEmpty()) {
            result.addError(ErrorV1RDTO.createValidationError(errorInObjectfieldname, "error_missing_uri"));
            return false;
        } else if (!KoodistoURI.isValidKieliUri(kieliUri)) {
            result.addError(ErrorV1RDTO.createValidationError(errorInObjectfieldname, "error_invalid_uri"));
            return false;
        }

        return true;
    }

    public static String getValidBase64Image(final String rawbase64) {
        Preconditions.checkNotNull(rawbase64, "Image string cannot be null!");
        String modifiedBase64 = rawbase64;
        final boolean isBase64 = Base64.isBase64(rawbase64);
        if (!isBase64) {
            LOG.debug("Not valid base64 - try to clean received raw data. Data : '{}'", rawbase64);
            modifiedBase64 = rawbase64.replaceFirst("^data:image/[^;]*;base64,?", "");

            if (Base64.isBase64(modifiedBase64)) {
                return modifiedBase64;
            }
        } else {
            return rawbase64;
        }

        return null;
    }

    public static void validateKoulutusUpdate(final KoulutusmoduuliToteutus komoto, ResultV1RDTO dto) {
        if (komoto == null) {
            dto.addError(ErrorV1RDTO.createValidationError("oid", KoulutusValidationMessages.KOULUTUS_KOMOTO_MISSING.lower()));
            dto.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
        } else if (komoto.getKoulutusmoduuli() == null) {
            dto.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            dto.addError(ErrorV1RDTO.createValidationError("unknown", KoulutusValidationMessages.KOULUTUS_KOMO_MISSING.lower()));
        } else {
            //check is deleted
            checkIsDeleted(komoto, dto);
        }
    }

    private static final Set<KoulutustyyppiEnum> allowDeletingPublishedKomoForTypes = new ImmutableSet.Builder<KoulutustyyppiEnum>().add(KoulutustyyppiEnum.KORKEAKOULUTUS, KoulutustyyppiEnum.AMMATTIKORKEAKOULUTUS, KoulutustyyppiEnum.YLIOPISTOKOULUTUS).build();

    public static void validateKoulutusDelete(final KoulutusmoduuliToteutus komoto, final List<KoulutusmoduuliToteutus> relatedKomotos, final List<String> children, final List<String> parent, Map<String, Integer> hkKoulutusMap, ResultV1RDTO dto) {
        Preconditions.checkNotNull(komoto, "KOMOTO object cannot be null.");
        Preconditions.checkNotNull(relatedKomotos, "List of related KOMOTO objects cannot be null.");
        Preconditions.checkNotNull(children, "List of child links cannot be null.");
        Preconditions.checkNotNull(parent, "List of parent links cannot be null.");
        Preconditions.checkNotNull(hkKoulutusMap, "Map of hakukohde objects cannot be null.");
        Preconditions.checkNotNull(dto, "Result RDTO cannot be null.");

        LOG.debug("related komotos size : {}", relatedKomotos.size());

        if (relatedKomotos.size() == 1) {
            //Removed (safe delete) items must be excluded from the list!    
            //if the last komoto, then we will need to check if the komo is allowed to be removed (safe delete)
//            Set<String> komotoOids = Sets.<String>newHashSet();
//            for (KoulutusmoduuliToteutus t : relatedKomotos) {
//                komotoOids.add(t.getOid());
//            }
//
//            dto.addError(ErrorV1RDTO.createValidationError("komo.komotos", KoulutusValidationMessages.KOULUTUS_RELATION_KOMO_REMOVE_KOMOTO.lower(), komotoOids.toArray(new String[komotoOids.size()])));

            if (!children.isEmpty()) {
                dto.addError(ErrorV1RDTO.createValidationError("komo.link.childs", KoulutusValidationMessages.KOULUTUS_RELATION_KOMO_CHILD_REMOVE_LINK.lower(), children.toArray(new String[children.size()])));
            }

            if (!parent.isEmpty()) {
                dto.addError(ErrorV1RDTO.createValidationError("komo.link.parents", KoulutusValidationMessages.KOULUTUS_RELATION_KOMO_PARENT_REMOVE_LINK.lower(), parent.toArray(new String[parent.size()])));
            }

            if (!allowDeletingPublishedKomoForTypes.contains(komoto.getKoulutusmoduuli().getKoulutustyyppiEnum()) && !komoto.getKoulutusmoduuli().getTila().isRemovable()) {
                dto.addError(ErrorV1RDTO.createValidationError("komo.invalid.transition", KoulutusValidationMessages.KOULUTUS_INVALID_TRANSITION.lower(), parent.toArray(new String[parent.size()])));
            }
        }

        /*
         * Ei haukohteita (tai kaikki poistettu) == OK
         * Jos hakukohde ja hakukohteessa on jokin muu koulutus kiinni == OK
         */
        if (hkKoulutusMap.size() >= 0) {

            Set<String> hakukohdeOids = Sets.<String>newHashSet();

            for (Entry<String, Integer> hkKoulutusCount : hkKoulutusMap.entrySet()) {
                if (hkKoulutusCount.getValue() == 1) {
                    hakukohdeOids.add(hkKoulutusCount.getKey());
                }
            }

            if (hakukohdeOids.size() > 0) {
                dto.addError(ErrorV1RDTO.createValidationError("komoto.hakukohdes", KoulutusValidationMessages.KOULUTUS_RELATION_KOMOTO_HAKUKOHDE_REMOVE_LINK.lower(), hakukohdeOids.toArray(new String[hakukohdeOids.size()])));
            }
        }
    }

    private static void checkIsDeleted(final KoulutusmoduuliToteutus komoto, ResultV1RDTO dto) {
        Preconditions.checkNotNull(komoto, "Koulutusmoduulin toteutus cannot be null!");
        Preconditions.checkNotNull(komoto.getTila(), "Status cannot be null!");
        if (komoto.getTila().equals(TarjontaTila.POISTETTU)) {
            dto.addError(ErrorV1RDTO.createValidationError("komoto.tila", KoulutusValidationMessages.KOULUTUS_DELETED.lower(), komoto.getOid()));
        }
    }

    /**
     * @param v
     * @return true if null or empty (when trimmed) string
     */
    private static boolean isEmpty(String v) {
        return (v == null) || (v.trim().isEmpty());
    }
}
