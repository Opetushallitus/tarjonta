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

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.auth.NotAuthorizedException;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusCommonConverter;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.valmistava.ValmistavaV1RDTO;
import fi.vm.sade.tarjonta.shared.ImageMimeValidator;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO.createValidationError;

@Service
public class KoulutusValidator {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusValidator.class);
    private static final int DEFAULT_MIN = 1;
    private static final boolean REQUIRE_KOMO_VALIDATION = true;
    private static final boolean NO_KOMO_VALIDATION = false;
    private static final Date firstOfJanuary2000 = new Date(946738364556L);
    private static final Date firstOfAugust2018 =  new Date(1533081600000L);

    public static final String KOULUTUSOHJELMA = "koulutusohjelma";
    public static final String KOULUTUSMODUULITYYPPI = "koulutusmoduuliTyyppi";
    public static final String KOULUTUKSEN_ALKAMISPVMS = "koulutuksenAlkamisPvms";
    public static final String OPINTOJEN_LAAJUUS_PISTETTA = "opintojenLaajuusPistetta";
    public static final String OPETUSKIELIS = "opetuskielis";
    public static final String AIHEES = "aihees";
    public static final String SISALTYY_KOULUTUKSIIN = "sisaltyyKoulutuksiin";
    public static final String TOTEUTUSTYYPPI = "toteutustyyppi";
    public static final String OPETUS_JARJESTAJAT = "opetusJarjestajat";

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private PermissionChecker permissionChecker;

    @Autowired
    private OrganisaatioService organisaatioService;

    /**
     * Required data validation for koulutus -type of objects.
     *
     * @param koulutus
     * @param result
     * @return validation flag: full stop == true
     */
    public static boolean validateBaseKoulutusData(KoulutusV1RDTO koulutus, Koulutusmoduuli komo, ResultV1RDTO result, boolean komoRequired) {
        if (komoRequired && komo == null) {
            result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_MODULE_NOT_FOUND.getFieldName(), KoulutusValidationMessages.KOULUTUS_MODULE_NOT_FOUND.lower()));
            return true;
        }

        if (koulutus == null) {
            result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_INPUT_OBJECT_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_INPUT_OBJECT_MISSING.lower()));
            return true;
        }

        if (koulutus.getToteutustyyppi() == null) {
            result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_TOTEUTUSTYYPPI_ENUM_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_TOTEUTUSTYYPPI_ENUM_MISSING.lower()));
        }

        if (koulutus.getModuulityyppi() == null) {
            result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_MODUULITYYPPI_ENUM_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_MODUULITYYPPI_ENUM_MISSING.lower()));
        }

        if (koulutus.getTila() == null) {
            result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_TILA_ENUM_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_TILA_ENUM_MISSING.lower()));
        }

        if (koulutus.getOrganisaatio() == null || koulutus.getOrganisaatio().getOid() == null || koulutus.getOrganisaatio().getOid().isEmpty()) {
            result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING.lower()));
        }

        return false;
    }

    public void validateTutkintoonjohtamaton(TutkintoonJohtamatonKoulutusV1RDTO dto, ResultV1RDTO result) {
        validateBaseKoulutusData(dto, null, result, false);

        validateKoulutusohjelma(dto, result);

        if (dto.getKoulutusmoduuliTyyppi() == null) {
            result.addError(createValidationError(KOULUTUSMODUULITYYPPI, KOULUTUSMODUULITYYPPI + " missing, should be OPINTOKOKONAISUUS/OPINTOJAKSO"));
        }

        validateAlkamisPvms(result, dto);

        if (!TarjontaTila.PUUTTEELLINEN.equals(dto.getTila())) {
            validateTutkintoonjohtamatonAdditionalRequiredFields(dto, result);
        }

        validateSisaltyyKoulutuksiin(dto, result);
        validateKoulutuksenJarjestajat(dto, result);
    }

    private void validateKoulutuksenJarjestajat(TutkintoonJohtamatonKoulutusV1RDTO dto, ResultV1RDTO result) {
        if (dto.getOpetusJarjestajat() == null) return;
        for (String orgOid : dto.getOpetusJarjestajat()) {
            if (!isValidOrganisation(orgOid)) {
                result.addError(createValidationError(OPETUS_JARJESTAJAT, "organisation with oid '" + orgOid + "' not found!"));
            }
        }
    }

    // Extra validation of fields that are required in order to show the learning opportunity in opintopolku.fi
    private static void validateTutkintoonjohtamatonAdditionalRequiredFields(TutkintoonJohtamatonKoulutusV1RDTO dto, ResultV1RDTO result) {
        if (StringUtils.isBlank(dto.getOpintojenLaajuusPistetta())) {
            result.addError(createValidationError(OPINTOJEN_LAAJUUS_PISTETTA, OPINTOJEN_LAAJUUS_PISTETTA + " is required"));
        }
        if (isBlank(dto.getOpetuskielis())) {
            result.addError(createValidationError(OPETUSKIELIS, OPETUSKIELIS + ".uris cannot be empty!"));
        }
        if (isBlank(dto.getAihees())) {
            result.addError(createValidationError(AIHEES, AIHEES + ".uris cannot be empty!"));
        }
    }

    private void validateSisaltyyKoulutuksiin(KoulutusV1RDTO dto, ResultV1RDTO result) {
        if (dto.getSisaltyyKoulutuksiin() == null) {
            return;
        }

        for (KoulutusIdentification id : dto.getSisaltyyKoulutuksiin()) {
            KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findKomotoByKoulutusId(id);
            String tunniste = StringUtils.isNotBlank(id.getOid())
                    ? "oid: " + id.getOid()
                    : "uniqueExternalId:" + id.getUniqueExternalId();
            if (komoto == null || TarjontaTila.POISTETTU.equals(komoto.getTila())) {
                result.addError(createValidationError(SISALTYY_KOULUTUKSIIN, "koulutus with " + tunniste + " does not exist!"));
            } else {
                try {
                    permissionChecker.checkUpdateKoulutusByTarjoajaOid(komoto.getTarjoaja());
                } catch (NotAuthorizedException e) {
                    result.addError(createValidationError(SISALTYY_KOULUTUKSIIN, "permission denied for koulutus with " + tunniste));
                }
            }
        }
    }

    private static boolean isBlank(KoodiUrisV1RDTO koodis) {
        try {
            Iterables.find(koodis.getUris().keySet(), new Predicate<String>() {
                @Override
                public boolean apply(String input) {
                    return StringUtils.isNotBlank(input);
                }
            });
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private static boolean hasStartingDateMissing(KoulutusV1RDTO dto) {
        return (dto.getKoulutuksenAlkamisPvms() == null || dto.getKoulutuksenAlkamisPvms().isEmpty())
                && (dto.getKoulutuksenAlkamisvuosi() == null
                    || dto.getKoulutuksenAlkamiskausi() == null || dto.getKoulutuksenAlkamiskausi().getUri() == null);
    }

    public static ResultV1RDTO validateKoulutusKorkeakoulu(KoulutusKorkeakouluV1RDTO dto, ResultV1RDTO result) {
        validateBaseKoulutusData(dto, null, result, NO_KOMO_VALIDATION);
        validateKoulutusohjelma(dto, result);
        validateAlkamisPvms(result, dto);
        validateKoodi(result, dto.getKoulutuskoodi(), KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_INVALID);
        validateTunniste(dto, result);

        if (!TarjontaTila.PUUTTEELLINEN.equals(dto.getTila())) {
            validateKoodi(result, dto.getOpintojenLaajuusarvo(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_INVALID);
            validateKoodi(result, dto.getOpintojenLaajuusyksikko(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_INVALID);
            validateKoodiUris(result, dto.getOpetusmuodos(), KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_INVALID, DEFAULT_MIN);
            validateKoodiUris(result, dto.getAihees(), KoulutusValidationMessages.KOULUTUS_TEEMAT_AIHEET_MISSING, KoulutusValidationMessages.KOULUTUS_TEEMAT_AIHEET_INVALID, DEFAULT_MIN);
            validateKoodiUris(result, dto.getOpetusAikas(), KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_INVALID, DEFAULT_MIN);
            validateKoodiUris(result, dto.getOpetusPaikkas(), KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_INVALID, DEFAULT_MIN);
            validateKoodiUris(result, dto.getOpetuskielis(), KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_INVALID, DEFAULT_MIN);
            validateSuuniteltukesto(result, dto);
        }

        return result;
    }

    public static ResultV1RDTO validateKoulutusGeneric(KoulutusGenericV1RDTO dto, Koulutusmoduuli komo, ResultV1RDTO result) {
        if (validateBaseKoulutusData(dto, komo, result, REQUIRE_KOMO_VALIDATION)) {
            //a major validation error, validation must stop now!
            return result;
        }
        boolean validatePohjakoulutus = true;
        if(dto.getToteutustyyppi() == ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_ALK_2018){
            validatePohjakoulutus = false;
        }
        validateKoodistoRelationsGeneric(dto, result, validatePohjakoulutus);
        validateAlkamisPvms(result, dto);

        if (TarjontaTila.PUUTTEELLINEN.equals(dto.getTila())) {
            return result;
        }

        validateSuuniteltukesto(result, dto);

        if (dto instanceof ValmistavaKoulutusV1RDTO) {
            ValmistavaKoulutusV1RDTO valmistavaKoulutusV1RDTO = (ValmistavaKoulutusV1RDTO) dto;

            if (!notNullStrOrEmpty(valmistavaKoulutusV1RDTO.getOpintojenLaajuusarvoKannassa())) {
                result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_INVALID.lower()));
            }

            Boolean invalidNimi = false;
            Map<String, String> koulutusohjelmanNimiKannassa = valmistavaKoulutusV1RDTO.getKoulutusohjelmanNimiKannassa();
            if ( koulutusohjelmanNimiKannassa != null ) {
                for (Object key : koulutusohjelmanNimiKannassa.keySet()) {
                    if (!notNullStrOrEmpty(koulutusohjelmanNimiKannassa.get(key))) {
                        invalidNimi = true;
                        break;
                    }
                }
            }

            if(invalidNimi) {
                result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_NIMI_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_NIMI_MISSING.lower()));
            }
        } else if (mustValidateLaajuus(dto)) {
            validateKoodi(result, dto.getOpintojenLaajuusarvo(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSARVO_INVALID);
        }

        if (dto instanceof Koulutus2AsteV1RDTO && !(dto instanceof KoulutusAikuistenPerusopetusV1RDTO)) {
            validateKoodi(result, ((Koulutus2AsteV1RDTO) dto).getTutkintonimike(), KoulutusValidationMessages.KOULUTUS_TUTKINTONIMIKE_MISSING, KoulutusValidationMessages.KOULUTUS_TUTKINTONIMIKE_INVALID);
        }

        return result;
    }

    public ResultV1RDTO validateKoulutusNayttotutkinto(NayttotutkintoV1RDTO dto, Koulutusmoduuli komo, ResultV1RDTO result) {
        if (validateBaseKoulutusData(dto, komo, result, true)) {
            //a major validation error, validation must stop now!
            return result;
        }

        validateKoulutusWithOrWithoutKoulutusohjelma(dto, komo.getModuuliTyyppi(), result);

        if (!TarjontaTila.PUUTTEELLINEN.equals(dto.getTila())) {
            validateKoodiUris(result, dto.getOpetuskielis(), KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_INVALID, DEFAULT_MIN);

            if (dto.getJarjestavaOrganisaatio() == null || dto.getJarjestavaOrganisaatio().getOid() == null || dto.getJarjestavaOrganisaatio().getOid().isEmpty()) {
                result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_JARJESTAJA_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_JARJESTAJA_MISSING.lower()));
            } else {
                validateOrganisation(
                        dto.getJarjestavaOrganisaatio(),
                        result,
                        KoulutusValidationMessages.KOULUTUS_JARJESTAJA_MISSING,
                        KoulutusValidationMessages.KOULUTUS_JARJESTAJA_INVALID
                );
            }
        }

        if (dto.getValmistavaKoulutus() != null) {
            final ValmistavaV1RDTO valmistavaKoulutus = dto.getValmistavaKoulutus();
            if (!notNullStrOrEmpty(valmistavaKoulutus.getSuunniteltuKestoArvo())) {
                result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_VALUE_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_VALUE_MISSING.lower()));
            }

            validateKoodiUris(result, valmistavaKoulutus.getOpetusmuodos(), KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_INVALID, DEFAULT_MIN);
            validateKoodiUris(result, valmistavaKoulutus.getOpetusAikas(), KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_INVALID, DEFAULT_MIN);
            validateKoodiUris(result, valmistavaKoulutus.getOpetusPaikkas(), KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_INVALID, DEFAULT_MIN);
            validateKoodi(result, valmistavaKoulutus.getSuunniteltuKestoTyyppi(), KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_TYPE_MISSING, KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_TYPE_INVALID);
        }

        validateAlkamisPvms(result, dto);

        return result;
    }

    public boolean isValidOrganisation(String orgOid) {
        try {
            final OrganisaatioRDTO org = organisaatioService.findByOid(orgOid);
            if (org == null || org.getOid() == null || org.getOid().isEmpty()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            LOG.error("Organisation service call failed", e);
            return false;
        }
    }

    public boolean validateOrganisation(OrganisaatioV1RDTO dto, ResultV1RDTO result, final KoulutusValidationMessages kvmMissing, final KoulutusValidationMessages kvmInvalid) {
        if (dto == null || dto.getOid() == null || dto.getOid().isEmpty()) {
            result.addError(createValidationError(kvmMissing.getFieldName(), kvmMissing.lower()));
        } else {
            if (isValidOrganisation(dto.getOid())) {
                return true;
            } else {
                result.addError(createValidationError(kvmInvalid.getFieldName(), kvmInvalid.lower()));
            }
        }

        return false;
    }

    /**
     * Tarkista merkkijonon max pituus
     */
    private static void validateStringMaxLength(ResultV1RDTO result, String tunniste, int maxLength,
            KoulutusValidationMessages koulutusTunnusteLength) {
        if (tunniste.length() > maxLength) {
            result.addError(createValidationError(koulutusTunnusteLength.getFieldName(), koulutusTunnusteLength.lower()));
        }
    }

    public static void validateTunniste(KoulutusKorkeakouluV1RDTO dto, ResultV1RDTO result) {
        if (dto.getTunniste() != null) {
            validateStringMaxLength(result, dto.getTunniste(), 35, KoulutusValidationMessages.KOULUTUS_TUNNISTE_LENGTH);
        }
    }

    private static void validateKoulutusohjelma(KoulutusV1RDTO dto, ResultV1RDTO result) {
        try {
            Iterables.find(dto.getKoulutusohjelma().getTekstis().values(), new Predicate<String>() {
                @Override
                public boolean apply(String input) {
                    return StringUtils.isNotBlank(input);
                }
            });
        } catch (Exception e) {
            result.addError(createValidationError(KOULUTUSOHJELMA, KOULUTUSOHJELMA + ".tekstis cannot be empty!"));
        }
    }

    private static void validateKoodistoRelationsGeneric(KoulutusGenericV1RDTO dto, ResultV1RDTO result, boolean validatePohjakoulutus) {
        if (!(dto instanceof KoulutusAmmatillinenPerustutkintoV1RDTO
                || dto instanceof KoulutusAmmatilliseenPeruskoulutukseenValmentavaV1RDTO
                || dto instanceof KoulutusAmmatilliseenPeruskoulutukseenValmentavaERV1RDTO
                || dto instanceof KoulutusValmentavaJaKuntouttavaV1RDTO
                || dto instanceof KoulutusAmmatillinenPerustutkintoAlk2018V1RDTO)) {
            validateKoodi(
                result,
                dto.getKoulutusohjelma(),
                KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_MISSING,
                KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_INVALID
            );
        }

        validateKoodi(result, dto.getKoulutuskoodi(), KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_INVALID);

        if (!TarjontaTila.PUUTTEELLINEN.equals(dto.getTila())) {
            validateKoodi(result, dto.getKoulutusala(), KoulutusValidationMessages.KOULUTUS_KOULUTUSALA_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSALA_INVALID);
            if (mustValidateLaajuus(dto)) {
                validateKoodi(result, dto.getOpintojenLaajuusyksikko(), KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOJENLAAJUUSYKSIKKO_INVALID);
            }
            if (mustValidateKoulutuslaji(dto)) {
                validateKoodi(result, dto.getKoulutuslaji(), KoulutusValidationMessages.KOULUTUS_KOULUTUSLAJI_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSLAJI_INVALID);
            }
            if (validatePohjakoulutus) {
                validateKoodi(result, dto.getPohjakoulutusvaatimus(), KoulutusValidationMessages.KOULUTUS_POHJAKOULUTUSVAATIMUS_MISSING, KoulutusValidationMessages.KOULUTUS_POHJAKOULUTUSVAATIMUS_INVALID);
            }
            validateKoodiUris(result, dto.getOpetusmuodos(), KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSMUOTO_INVALID, DEFAULT_MIN);
            validateKoodiUris(result, dto.getOpetusAikas(), KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSAIKA_INVALID, DEFAULT_MIN);
            validateKoodiUris(result, dto.getOpetusPaikkas(), KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSPAIKKA_INVALID, DEFAULT_MIN);
            validateKoodiUris(result, dto.getOpetuskielis(), KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_MISSING, KoulutusValidationMessages.KOULUTUS_OPETUSKIELI_INVALID, DEFAULT_MIN);
        }
   }

    private static void validateKoulutusWithOrWithoutKoulutusohjelma(NayttotutkintoV1RDTO dto, KoulutusmoduuliTyyppi koulutusmoduuliTyyppi, ResultV1RDTO result) {
        switch (koulutusmoduuliTyyppi) {
            case TUTKINTO_OHJELMA:
                validateKoodi(result, dto.getKoulutusohjelma(), KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSOHJELMA_INVALID);
                break;
            default:
                //no validation
                break;
        }
    }

    private static void validateKoodistoRelationsNaytto(NayttotutkintoV1RDTO dto, ResultV1RDTO result) {
        // TODO:  kun relaatiot on tehty koodistoon;
        validateKoodi(result, dto.getOpintoala(), KoulutusValidationMessages.KOULUTUS_OPINTOALA_MISSING, KoulutusValidationMessages.KOULUTUS_OPINTOALA_INVALID);
        validateKoodi(result, dto.getKoulutusala(), KoulutusValidationMessages.KOULUTUS_KOULUTUSALA_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSALA_INVALID);
        validateKoodi(result, dto.getKoulutuskoodi(), KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_INVALID);
        validateKoodi(result, dto.getTutkintonimike(), KoulutusValidationMessages.KOULUTUS_TUTKINTONIMIKE_MISSING, KoulutusValidationMessages.KOULUTUS_TUTKINTONIMIKE_INVALID);
        validateKoodi(result, dto.getKoulutuslaji(), KoulutusValidationMessages.KOULUTUS_KOULUTUSLAJI_MISSING, KoulutusValidationMessages.KOULUTUS_KOULUTUSLAJI_INVALID);
    }

    private static boolean mustValidateLaajuus(KoulutusV1RDTO dto) {
        Set<ToteutustyyppiEnum> toteutustyypitWithoutLaajuusValidation = Sets.newHashSet(
                ToteutustyyppiEnum.EB_RP_ISH,
                ToteutustyyppiEnum.LUKIOKOULUTUS
        );
        return !toteutustyypitWithoutLaajuusValidation.contains(dto.getToteutustyyppi());
    }

    private static boolean mustValidateKoulutuslaji(KoulutusV1RDTO dto) {
        Set<ToteutustyyppiEnum> toteutustyypitWithoutLaajuusValidation = Sets.newHashSet(
                ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_ALK_2018
        );
        return !toteutustyypitWithoutLaajuusValidation.contains(dto.getToteutustyyppi());
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
     *
     * @param koodi
     * @return
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
            result.addError(createValidationError(missing.getFieldName(), missing.lower()));
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
            result.addError(createValidationError(missing.getFieldName(), missing.lower()));
            return false;
        }

        if (!isValidKoodiUrisWithVersion(dto.getUris(), min)) {
            result.addError(createValidationError(missing.getFieldName(), missing.lower()));
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

    private static void checkKausiVuosi(ResultV1RDTO result, KoodiV1RDTO kausi, Integer year) {
        if (kausi == null || isEmpty(kausi.getUri())) {
            result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_MISSING.lower()));

        } else if (!KoodistoURI.isValidKausiUri(kausi.getUri())) {
            result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_INVALID.getFieldName(), KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_INVALID.lower()));
        }

        if (year == null) {
            result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_MISSING.lower()));
        } else if (year < 2000) {
            result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_INVALID.getFieldName(), KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_INVALID.lower()));
        }
    }

    private static void validateAlkamisPvms(ResultV1RDTO result, KoulutusV1RDTO dto) {
        if (hasStartingDateMissing(dto)) {
            result.addError(createValidationError(KOULUTUKSEN_ALKAMISPVMS, KOULUTUKSEN_ALKAMISPVMS + " is required" +
                    " (or alternatively koulutuksenAlkamiskausi and koulutuksenAlkamisvuosi can be provided)"));
        } else if (!dto.getKoulutuksenAlkamisPvms().isEmpty()) {
            final Set<Date> koulutuksenAlkamisPvms = dto.getKoulutuksenAlkamisPvms();
            KoulutusValidationMessages validateDates = KoulutusCommonConverter.validateDates(
                    koulutuksenAlkamisPvms.iterator().next(),
                    dto.getKoulutuksenAlkamisPvms());

            if (!validateDates.equals(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_SUCCESS)) {
                result.addError(createValidationError(KOULUTUKSEN_ALKAMISPVMS, KOULUTUKSEN_ALKAMISPVMS + " contains invalid starting dates"));
            }
            validateAfterFirstOfAugust2018(result, dto);
        } else {
            checkKausiVuosi(result, dto.getKoulutuksenAlkamiskausi(), dto.getKoulutuksenAlkamisvuosi());
        }
    }

    private static void validateAfterFirstOfAugust2018(ResultV1RDTO result, KoulutusV1RDTO dto) {
        if(dto.getToteutustyyppi() == ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO || dto.getToteutustyyppi() == ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA){
            for (Date startDate : dto.getKoulutuksenAlkamisPvms()) {
                if(startDate.after(firstOfAugust2018)){
                    result.addError(createValidationError(KOULUTUKSEN_ALKAMISPVMS, KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_WRONGTYPEAFTER182018.getFieldName(), KOULUTUKSEN_ALKAMISPVMS + " cannot be this type after 1.8.2018"));
                }
            }
        }
    }

    private static void validateSuuniteltukesto(ResultV1RDTO result, KoulutusV1RDTO dto) {
        if (StringUtils.isBlank(dto.getSuunniteltuKestoArvo())) {
            result.addError(createValidationError(KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_VALUE_MISSING.getFieldName(), KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_VALUE_MISSING.lower()));
        }

        validateKoodi(result, dto.getSuunniteltuKestoTyyppi(), KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_TYPE_MISSING, KoulutusValidationMessages.KOULUTUS_SUUNNITELTU_KESTO_TYPE_INVALID);
    }

    public static void validateKoulutusKuva(KuvaV1RDTO kuva, ResultV1RDTO result) {
        validateKoulutusKuva(kuva, null, result);
    }

    public static void validateKoulutusKuva(final KuvaV1RDTO kuva, final String kieliUri, ResultV1RDTO result) {
        if (!validateKieliUri(kuva.getKieliUri() != null ? kuva.getKieliUri() : kieliUri, "kieliUri", result)) {
            //invalid kieli uri, fail fast.
            return;
        }

        final String rawBase64 = kuva.getBase64data();
        if (rawBase64 == null || rawBase64.isEmpty()) {
            result.addError(createValidationError("base64data", "error_missing_base64_data"));
        } else {
            /*
             * Data validation check
             */
            if (getValidBase64Image(rawBase64) == null) {
                result.addError(createValidationError("base64data", "error_invalid_base64_data"));
            }
        }

        if (kuva.getFilename() == null || kuva.getFilename().isEmpty()) {
            result.addError(createValidationError("filename", "error_missing_filename"));
        }

        validateMimeType(kuva.getMimeType(), "mimeType", result);

    }

    public static void validateMimeType(String mimeType, final String errorInObjectfieldname, ResultV1RDTO result) {
        if (mimeType == null || mimeType.isEmpty()) {
            result.addError(createValidationError(errorInObjectfieldname, "error_missing_mime_type"));
        } else if (!ImageMimeValidator.isValid(mimeType)) {
            result.addError(createValidationError(errorInObjectfieldname, "error_unrecognized_mime_type"));
        }
    }

    /**
     * Return true when correct kieli uri format.
     */
    public static boolean validateKieliUri(final String kieliUri, final String errorInObjectfieldname, ResultV1RDTO result) {
        if (kieliUri == null || kieliUri.isEmpty()) {
            result.addError(createValidationError(errorInObjectfieldname, "error_missing_uri"));
            return false;
        } else if (!KoodistoURI.isValidKieliUri(kieliUri)) {
            result.addError(createValidationError(errorInObjectfieldname, "error_invalid_uri"));
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
            dto.addError(createValidationError("oid", KoulutusValidationMessages.KOULUTUS_KOMOTO_MISSING.lower()));
            dto.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
        } else if (komoto.getKoulutusmoduuli() == null) {
            dto.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            dto.addError(createValidationError("unknown", KoulutusValidationMessages.KOULUTUS_KOMO_MISSING.lower()));
        } else {
            //check is deleted
            checkIsDeleted(komoto, dto);
        }
    }

    private static final Set<ModuulityyppiEnum> allowDeletingPublishedKomoForTypes = new ImmutableSet.Builder<ModuulityyppiEnum>().add(ModuulityyppiEnum.KORKEAKOULUTUS).build();

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
                dto.addError(createValidationError("komo.link.childs", KoulutusValidationMessages.KOULUTUS_RELATION_KOMO_CHILD_REMOVE_LINK.lower(), children.toArray(new String[children.size()])));
            }

            if (!parent.isEmpty()) {
                dto.addError(createValidationError("komo.link.parents", KoulutusValidationMessages.KOULUTUS_RELATION_KOMO_PARENT_REMOVE_LINK.lower(), parent.toArray(new String[parent.size()])));
            }

            if (!allowDeletingPublishedKomoForTypes.contains(komoto.getKoulutusmoduuli().getKoulutustyyppiEnum()) && !komoto.getKoulutusmoduuli().getTila().isRemovable()) {
                dto.addError(createValidationError("komo.invalid.transition", KoulutusValidationMessages.KOULUTUS_INVALID_TRANSITION.lower(), parent.toArray(new String[parent.size()])));
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
                dto.addError(createValidationError("komoto.hakukohdes", KoulutusValidationMessages.KOULUTUS_RELATION_KOMOTO_HAKUKOHDE_REMOVE_LINK.lower(), hakukohdeOids.toArray(new String[hakukohdeOids.size()])));
            }
        }
    }

    private static void checkIsDeleted(final KoulutusmoduuliToteutus komoto, ResultV1RDTO dto) {
        Preconditions.checkNotNull(komoto, "Koulutusmoduulin toteutus cannot be null!");
        Preconditions.checkNotNull(komoto.getTila(), "Status cannot be null!");
        if (komoto.getTila().equals(TarjontaTila.POISTETTU)) {
            dto.addError(createValidationError("komoto.tila", KoulutusValidationMessages.KOULUTUS_DELETED.lower(), komoto.getOid()));
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
