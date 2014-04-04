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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
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
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.shared.ImageMimeValidator;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class KoulutusValidator {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusValidator.class);

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
        List<ErrorV1RDTO> errors = Lists.<ErrorV1RDTO>newArrayList();

        for (KoulutusValidationMessages e : validationMessages) {
            ErrorV1RDTO errorV1RDTO = new ErrorV1RDTO();
            errorV1RDTO.setErrorMessageKey(e.name().toLowerCase());
            errors.add(errorV1RDTO);
        }

        return errors;
    }

    public static List<ErrorV1RDTO> validateKoulutus(KoulutusLukioV1RDTO dto) {
        Set<KoulutusValidationMessages> validationMessages = Sets.<KoulutusValidationMessages>newHashSet();
        //TODO: validateKoodistoRelations(dto, validationMessages);

        validateKoodiUris(validationMessages, dto.getOpetusmuodos(), KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_INVALID);
        validateKoodiUris(validationMessages, dto.getOpetusAikas(), KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_INVALID);
        validateKoodiUris(validationMessages, dto.getOpetusPaikkas(), KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_INVALID);
        validateKoodiUris(validationMessages, dto.getOpetuskielis(), KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_INVALID);

        validateAlkamisPvms(validationMessages, dto);
        validateSuuniteltukesto(validationMessages, dto);
        List<ErrorV1RDTO> erros = Lists.<ErrorV1RDTO>newArrayList();

        for (KoulutusValidationMessages e : validationMessages) {
            ErrorV1RDTO errorV1RDTO = new ErrorV1RDTO();
            errorV1RDTO.setErrorMessageKey(e.name().toLowerCase());
            erros.add(errorV1RDTO);
        }

        return erros;
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
            KoulutusValidationMessages invalid,
            KoulutusValidationMessages invalidTextValue) {
        Set<KoulutusValidationMessages> tempError = Sets.<KoulutusValidationMessages>newHashSet();

        if (dto.getTekstis().isEmpty()) {
            //no items
            validationMessages.add(invalid);
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
            validationMessages.addAll(tempError);
        }
    }

    private static void checkKausiVuosi(Set<KoulutusValidationMessages> validationMessages, KoodiV1RDTO kausi, Integer year) {

        if (kausi == null || isEmpty(kausi.getUri())) {
            validationMessages.add(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_MISSING);
        } else if (!KoodistoURI.isValidKausiUri(kausi.getUri())) {
            validationMessages.add(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_INVALID);
        }

        if (year == null) {
            validationMessages.add(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_MISSING);
        } else if (year < 2000) {
            validationMessages.add(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_INVALID);
        }
    }

    private static void validateAlkamisPvms(Set<KoulutusValidationMessages> validationMessages, KoulutusV1RDTO dto) {
        if (dto.getKoulutuksenAlkamisPvms() == null) {
            validationMessages.add(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_MISSING);
        } else if (!dto.getKoulutuksenAlkamisPvms().isEmpty()) {

            final Set<Date> koulutuksenAlkamisPvms = dto.getKoulutuksenAlkamisPvms();
            KoulutusValidationMessages validateDates = KoulutusCommonConverter.validateDates(
                    koulutuksenAlkamisPvms.iterator().next(),
                    dto.getKoulutuksenAlkamisPvms());

            if (!validateDates.equals(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_SUCCESS)) {
                validationMessages.add(validateDates);
            }
        } else {
            checkKausiVuosi(validationMessages, dto.getKoulutuksenAlkamiskausi(), dto.getKoulutuksenAlkamisvuosi());
        }
    }

    private static void validateSuuniteltukesto(Set<KoulutusValidationMessages> validationMessages, KoulutusV1RDTO dto) {
        if (!notNullStrOrEmpty(dto.getSuunniteltuKestoArvo())) {
            validationMessages.add(KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_VALUE_MISSING);
        }

        validateKoodi(validationMessages, dto.getSuunniteltuKestoTyyppi(), KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_TYPE_MISSING, KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_TYPE_INVALID);
    }

    public static void validateKoulutusKuva(KuvaV1RDTO kuva, ResultV1RDTO<KuvaV1RDTO> result) {
        validateKieliUri(kuva.getKieliUri(), "kieliUri", result);
        String raw = kuva.getBase64data();

        /*
         * Data validation check
         */
        if (getValidBase64Image(raw) == null) {
            result.addError(ErrorV1RDTO.createValidationError("base64data", "error_invalid_base64_data"));
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

    public static void validateKieliUri(final String kieliUri, final String errorInObjectfieldname, ResultV1RDTO result) {
        if (kieliUri == null || kieliUri.isEmpty()) {
            result.addError(ErrorV1RDTO.createValidationError(errorInObjectfieldname, "error_missing_uri"));
        } else if (!KoodistoURI.isValidKieliUri(kieliUri)) {
            result.addError(ErrorV1RDTO.createValidationError(errorInObjectfieldname, "error_invalid_uri"));
        }
    }

    public static String getValidBase64Image(final String rawbase64) {
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

    public static void validateKoulutusDelete(final KoulutusmoduuliToteutus komoto, final List<String> children, final List<String> parent, KoulutuksetVastaus kv, ResultV1RDTO dto) {
        final Koulutusmoduuli komo = komoto.getKoulutusmoduuli();

        if (komo.getKoulutusmoduuliToteutusList().size() > 1) {
            Set<String> komotoOids = Sets.<String>newHashSet();
            for (KoulutusmoduuliToteutus t : komo.getKoulutusmoduuliToteutusList()) {
                komotoOids.add(t.getOid());
            }

            dto.addError(ErrorV1RDTO.createValidationError("komo.komotos", KoulutusValidationMessages.KOULUTUS_RELATION_KOMO_REMOVE_KOMOTO.lower(), komotoOids.toArray(new String[komotoOids.size()])));
        }

        if (!children.isEmpty()) {
            dto.addError(ErrorV1RDTO.createValidationError("komo.link.childs", KoulutusValidationMessages.KOULUTUS_RELATION_KOMO_CHILD_REMOVE_LINK.lower(), children.toArray(new String[children.size()])));
        }

        if (!parent.isEmpty()) {
            dto.addError(ErrorV1RDTO.createValidationError("komo.link.parents", KoulutusValidationMessages.KOULUTUS_RELATION_KOMO_PARENT_REMOVE_LINK.lower(), parent.toArray(new String[parent.size()])));
        }

        if (!komo.getTila().isRemovable()) {
            dto.addError(ErrorV1RDTO.createValidationError("komo.invalid.transition", KoulutusValidationMessages.KOULUTUS_INVALID_TRANSITION.lower(), parent.toArray(new String[parent.size()])));
        }

        /*
         * Ei haukohteita == OK
         * Jos hakukohde ja hakukohteessa on jokin muu koulutus kiinni == OK
         */
        if (!komoto.getHakukohdes().isEmpty()) {
            final String komotoOid = komoto.getOid();

            int includedToHakukohde = 0;
            for (KoulutusPerustieto kp : kv.getKoulutukset()) {
                if (komotoOid.equals(kp.getKomotoOid())) {
                    includedToHakukohde = 1;
                    break;
                }
            }

            if ((komoto.getHakukohdes().size() - includedToHakukohde) < 1) {
                Set<String> hakukohdeOids = Sets.<String>newHashSet();
                for (Hakukohde hk : komoto.getHakukohdes()) {
                    hakukohdeOids.add(hk.getOid());
                }

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
