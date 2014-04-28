/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.model.Kielivalikoima;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.WebLinkki;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiValikoimaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Convert entity objects to koulutus rest objects and vise versa.
 *
 * @author jani
 */
@Component
public class KoulutusCommonConverter {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusCommonConverter.class);

    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @Autowired
    private OrganisaatioService organisaatioService;

    private final KoodistoURI koodistoUri = new KoodistoURI();

    public NimiV1RDTO koulutusohjelmaUiMetaDTO(final MonikielinenTeksti mt, final Locale langCode, final FieldNames msg, final boolean showMeta) {
        NimiV1RDTO data = new NimiV1RDTO();
        for (TekstiKaannos tk : mt.getTekstis()) {
            final KoodiType koodiType = tarjontaKoodistoHelper.convertKielikoodiToKoodiType(tk.getKieliKoodi());

            if (koodiType == null) {
                LOG.error("No koodisto koodi URI found for kielikoodi : '{}'", tk.getKieliKoodi());
                continue;
            }

            final String koodiUri = koodiType.getKoodiUri();

            data.getTekstis().put(koodiUri, tk.getArvo());
            if (showMeta) {
                if (data.getMeta() == null) {
                    data.setMeta(Maps.<String, KoodiV1RDTO>newHashMap());
                }

                KoodiUriAndVersioType type = new KoodiUriAndVersioType();
                type.setKoodiUri(koodiType.getKoodiUri());
                type.setVersio(koodiType.getVersio());
                data.getMeta().put(koodiUri, convertToKoodiDTO(new KoodiV1RDTO(), type, langCode, msg, true));
            }
        }
        return data;
    }

    private void convertKoodistoMetaData(KoodiV1RDTO dto, KoodiUriAndVersioType type, final String arvo, Locale locale, boolean showSubMeta) {
        if (type != null && type.getKoodiUri() != null && !type.getKoodiUri().isEmpty()) {
            dto.setUri(type.getKoodiUri());
            dto.setVersio(type.getVersio());

            if (arvo != null) { //only when koodisto has thrown an exception
                dto.setArvo(arvo);

                final KoodiType koodiType = tarjontaKoodistoHelper.getKoodi(type.getKoodiUri(), type.getVersio());
                if (koodiType != null) {
                    dto.setNimi(tarjontaKoodistoHelper.getKoodiNimi(koodiType, locale));
                    if (showSubMeta) {
                        addOtherLanguages(dto, koodiType, locale, showSubMeta);
                    } else {
                        dto.setMeta(null);
                    }
                }
            }
        } else {
            //koodisto koodi missing
            dto.setUri("");
            dto.setVersio(-1);
            dto.setArvo("");
            dto.setNimi("");
            dto.setMeta(null);
        }
    }

    /*
     * Create JSON object:
     * 
     * "kieli_sv" : {
     *    "kieliUri" : "kieli_sv",
     *    "kieliVersio" : 1,
     *    "kieliArvo" : "SV",
     *    "nimi" : "Agrolog (YH)"
     * }
     */
    public void convertKoodistoKieliData(KoodiV1RDTO dto, final String koodiUri, final KoodiType koodiType, final String langNimi, final Locale locale) {
        if (koodiType != null && koodiType.getKoodiUri() != null && !koodiType.getKoodiUri().isEmpty()) {
            dto.setKieliUri(koodiType.getKoodiUri());
            dto.setKieliVersio(koodiType.getVersio());
            dto.setKieliArvo(koodiType.getKoodiArvo());
            dto.setNimi(langNimi);
        }
    }

//    private KoodiV1RDTO koodiData(String koodistoKoodiUri, final Locale locale, final FieldNames fieldName, final boolean showMeta) {
//        if (koodistoKoodiUri != null && !koodistoKoodiUri.isEmpty()) {
//            return convertToKoodiDTO(koodistoKoodiUri, locale, fieldName, showMeta);
//        }
//        return new KoodiV1RDTO();
//    }
//
//    public KoodiV1RDTO convertKoodiAllowNull(String koodistoKoodiUri, final Locale locale, final FieldNames fieldName, final boolean showMeta) {
//        return koodiData(koodistoKoodiUri, locale, fieldName, showMeta);
//    }
//
//    public KoodiV1RDTO convertKoodiAllowNull(String fromKoodiUri, String fromOverrideKoodiUri, final Locale locale, final FieldNames fieldName, final boolean showMeta) {
//        return koodiData(fromOverrideKoodiUri != null ? fromOverrideKoodiUri : fromKoodiUri, locale, fieldName, showMeta);
//    }
    /**
     * Convert koodisto service URI with hashtag version to DTO.
     *
     * @param fromKoodiUri
     * @param locale
     * @param fieldName
     * @param allowNullKoodi
     * @param showMeta
     * @return
     */
    public KoodiV1RDTO convertToKoodiDTO(final String fromKoodiUri, final Locale locale, final FieldNames fieldName, final boolean allowNullKoodi, final boolean showMeta) {
        KoodiV1RDTO koodiUriDto = new KoodiV1RDTO();

        if (allowNullKoodi && (fromKoodiUri == null || fromKoodiUri.isEmpty())) {
            //use empty string arg to return empty data object
            convertKoodistoMetaData(koodiUriDto, null, "", locale, false);
        } else {
            convertKoodiUriToKoodiDTO(fromKoodiUri, koodiUriDto, locale, fieldName, allowNullKoodi, showMeta);
        }
        return koodiUriDto;
    }

    public KoodiV1RDTO convertToKoodiDTO(final String fromKoodiUri, final Locale locale, final FieldNames fieldName, final boolean showMeta) {
        return convertToKoodiDTO(fromKoodiUri, locale, fieldName, false, showMeta);
    }

    /**
     * Convert koodisto service URI with hashtag version to DTO. Optional
     * override URI.
     *
     * @param fromKoodiUri base URI
     * @param fromOverrideKoodiUri base URI override
     * @param locale
     * @param fieldName
     * @param showMeta
     * @return
     */
    public KoodiV1RDTO convertToKoodiDTO(final String fromKoodiUri, final String fromOverrideKoodiUri, final Locale locale, final FieldNames fieldName, final boolean showMeta) {
        return convertToKoodiDTO(fromOverrideKoodiUri != null ? fromOverrideKoodiUri : fromKoodiUri, locale, fieldName, false, showMeta);
    }

    public KoodiV1RDTO convertToKoodiDTO(final String fromKoodiUri, final String fromOverrideKoodiUri, final Locale locale, final FieldNames fieldName, final boolean allowNullKoodi, final boolean showMeta) {
        return convertToKoodiDTO(fromOverrideKoodiUri != null ? fromOverrideKoodiUri : fromKoodiUri, locale, fieldName, allowNullKoodi, showMeta);
    }

    public NimiV1RDTO convertToNimiDTO(final String fromKoodiUri, final Locale locale, final FieldNames fieldName, final boolean allowNullKoodi, final boolean showMeta) {
        NimiV1RDTO koodiUriDto = new NimiV1RDTO();
        convertKoodiUriToKoodiDTO(fromKoodiUri, koodiUriDto, locale, fieldName, allowNullKoodi, showMeta);
        return koodiUriDto;
    }

    public KoodiUrisV1RDTO convertToKoodiUrisDTO(final Set<KoodistoUri> fromKoodiUris, final Locale Locale, final FieldNames fieldName, final boolean showMeta) {
        KoodiUrisV1RDTO koodiMapDto = new KoodiUrisV1RDTO();
        if (koodiMapDto.getUris() == null) {
            koodiMapDto.setUris(Maps.<String, Integer>newHashMap());
        }

        for (KoodistoUri koodiUri : fromKoodiUris) {
            final KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(koodiUri.getKoodiUri());
            addToKoodiUrisMap(koodiMapDto, type, Locale, fieldName, showMeta);
        }

        return koodiMapDto;
    }

    public KoodiUrisV1RDTO convertToKoodiUrisDTO(final List<String> fromKoodiUris, final Locale Locale, final FieldNames fieldName, final boolean showMeta) {
        KoodiUrisV1RDTO koodiMapDto = new KoodiUrisV1RDTO();
        if (koodiMapDto.getUris() == null) {
            koodiMapDto.setUris(Maps.<String, Integer>newHashMap());
        }

        for (String koodiUri : fromKoodiUris) {
            final KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(koodiUri);
            addToKoodiUrisMap(koodiMapDto, type, Locale, fieldName, showMeta);
        }

        return koodiMapDto;
    }

    public KoodiV1RDTO convertToKoodiDTO(KoodiV1RDTO uiDto, final KoodiUriAndVersioType type, final Locale locale, final FieldNames fieldName, final boolean showSubMeta) {
        Preconditions.checkNotNull(type, "Koodi URI cannot be null in field : " + fieldName);
        Preconditions.checkNotNull(locale, "Locale object cannot be null. field in " + fieldName);
        KoodiType koodiType = null;
        try {
            koodiType = tarjontaKoodistoHelper.getKoodi(type.getKoodiUri(), type.getVersio());
        } catch (Exception e) {
            LOG.error("Koodisto service error.", e);
        }
        if (koodiType == null) {
            LOG.error("No koodisto service koodi URI found by '{}' and version {}.", type.getKoodiUri(), type.getVersio());
        }
        convertKoodistoMetaData(uiDto, type, koodiType != null ? koodiType.getKoodiArvo() : null, locale, showSubMeta);
        return uiDto;
    }

    public void convertKoodiUriToKoodiDTO(
            final String fromKoodiUri,
            final KoodiV1RDTO koodiDto,
            final Locale locale,
            final FieldNames fieldName,
            final boolean allowNullKoodisto, final boolean showMeta) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null in field : " + fieldName);
        Preconditions.checkNotNull(locale, "Locale object cannot be null in field in " + fieldName);
        Preconditions.checkNotNull(koodiDto, "KoodiV1RDTO object cannot be null in field " + fieldName);

        final KoodiType koodiType = tarjontaKoodistoHelper.getKoodiByUri(fromKoodiUri);

        if (koodiType == null && allowNullKoodisto) {
            //TODO: remove this code block when data is fixed
            toKoodiUriDTO(koodiDto, new KoodiUriAndVersioType(), new KoodiType(), locale);
        } else if (koodiType == null) {
            LOG.error("No koodisto service koodi URI found by '{}'.", fromKoodiUri);
            KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(fromKoodiUri);
            toKoodiUriDTO(koodiDto, type, koodiType, locale);
            addOtherLanguages(koodiDto, koodiType, locale, showMeta);
        } else {
            Preconditions.checkNotNull(koodiType, "No result found by koodisto koodi URI '" + fromKoodiUri + "' in field " + fieldName);
            KoodiUriAndVersioType type = new KoodiUriAndVersioType();
            type.setKoodiUri(koodiType.getKoodiUri());
            type.setVersio(koodiType.getVersio());
            toKoodiUriDTO(koodiDto, type, koodiType, locale);
            addOtherLanguages(koodiDto, koodiType, locale, showMeta);
        }
    }

    public KoodiV1RDTO toKoodiUriDTO(KoodiV1RDTO dto, final KoodiUriAndVersioType fromKoodiUri, final KoodiType koodiByUri, final Locale locale) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null.");
        Preconditions.checkNotNull(locale, "Locale object cannot be null.");

        if (dto == null) {
            dto = new KoodiV1RDTO();
        }
        convertKoodistoMetaData(dto, fromKoodiUri, koodiByUri != null ? koodiByUri.getKoodiArvo() : null, locale, false);
        return dto;
    }

    public void addOtherLanguages(final KoodiV1RDTO koodiUriDto, KoodiType koodiType, final Locale locale, final boolean showMeta) {
        Preconditions.checkNotNull(koodiUriDto, "KoodiUriDTO object cannot be null.");

        if (koodiType != null) {
            for (KoodiMetadataType meta : koodiType.getMetadata()) {
                //get a koodi for 'FI', 'SV' etc.
                final KoodiType langKoodiType = tarjontaKoodistoHelper.convertKielikoodiToKoodiType(meta.getKieli().value());

                KoodiV1RDTO dto = new KoodiV1RDTO();
                convertKoodistoKieliData(dto, koodiUriDto.getUri(), langKoodiType, meta.getNimi(), locale);
                if (showMeta && langKoodiType != null) {
                    if (koodiUriDto.getMeta() == null) {
                        koodiUriDto.setMeta(Maps.<String, KoodiV1RDTO>newHashMap());
                    }
                    koodiUriDto.getMeta().put(langKoodiType.getKoodiUri(), dto);
                }
            }
        } else {
            LOG.error("Unable to show koodisto koodi metadata.");
        }
    }

    public OrganisaatioV1RDTO searchOrganisaationNimi(String tarjoajaOid, Locale locale) {
        OrganisaatioV1RDTO organisaatioRDTO = new OrganisaatioV1RDTO();

        try {
            final OrganisaatioDTO organisaatioDto = organisaatioService.findByOid(tarjoajaOid);
            Preconditions.checkNotNull(organisaatioDto, "OrganisaatioDTO object cannot be null.");
            Preconditions.checkNotNull(organisaatioDto.getOid(), "OrganisaatioDTO OID cannot be null.");
            Preconditions.checkNotNull(organisaatioDto.getNimi(), "OrganisaatioDTO name object cannot be null.");
            organisaatioRDTO.setOid(organisaatioDto.getOid());
            final List<fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi.Teksti> tekstis = organisaatioDto.getNimi().getTeksti();

            String nimi = null;

            for (fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi.Teksti teksti : tekstis) {
                Preconditions.checkNotNull(teksti.getKieliKoodi(), "Locale language code cannot be null.");
                if (teksti.getKieliKoodi().toLowerCase().equals(locale.getLanguage())) {
                    nimi = teksti.getValue();
                    break;
                }
            }

            //fallback
            if (nimi == null && tekstis.size() > 0) {
                nimi = tekstis.get(0).getValue();
            } else if (nimi == null) {
                nimi = "No name";
            }

            organisaatioRDTO.setNimi(nimi);
        } catch (Exception e) {
            organisaatioRDTO.setOid(tarjoajaOid);
            LOG.error("Organisation service call failed!", e);
        }

        return organisaatioRDTO;
    }

    public void addToKoodiUrisMap(final KoodiUrisV1RDTO koodiUris, final KoodiUriAndVersioType type, final Locale locale, final FieldNames fieldName, final boolean showMeta) {
        koodiUris.getUris().put(type.getKoodiUri(), type.getVersio());
        //do not use hashtag uris as map key!
        if (koodiUris.getMeta() == null) {
            koodiUris.setMeta(Maps.<String, KoodiV1RDTO>newHashMap());
        }
        koodiUris.getMeta().put(type.getKoodiUri(), convertToKoodiDTO(new KoodiUrisV1RDTO(), type, locale, fieldName, showMeta));
    }

    /**
     * Logic for handling dates.
     *
     * @param komoto
     * @param dto
     */
    public void handleDates(KoulutusmoduuliToteutus komoto, KoulutusV1RDTO dto) {
        final Set<Date> koulutuksenAlkamisPvms = dto.getKoulutuksenAlkamisPvms();

        if (koulutuksenAlkamisPvms != null && !koulutuksenAlkamisPvms.isEmpty()) {
            //one or many dates   
            EntityUtils.keepSelectedDates(komoto.getKoulutuksenAlkamisPvms(), koulutuksenAlkamisPvms);
            final Date firstDate = koulutuksenAlkamisPvms.iterator().next();
            KoulutusValidationMessages checkDates = validateDates(firstDate, koulutuksenAlkamisPvms, komoto);
            Preconditions.checkArgument(checkDates.equals(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_SUCCESS), "Alkamisaika validation error - key : %s.", checkDates);

            komoto.setAlkamisVuosi(IndexDataUtils.parseYearInt(firstDate));
            komoto.setAlkamiskausiUri(IndexDataUtils.parseKausiKoodi(firstDate));
        } else {
            //allowed only one kausi and year
            Preconditions.checkNotNull(dto.getKoulutuksenAlkamiskausi(), "Alkamiskausi cannot be null!");
            Preconditions.checkArgument(!convertToUri(dto.getKoulutuksenAlkamiskausi(), FieldNames.ALKAMISKAUSI).isEmpty(), "Alkamiskausi cannot be empty string.");
            Preconditions.checkNotNull(dto.getKoulutuksenAlkamisvuosi(), "Alkamisvuosi cannot be null!");

            komoto.clearKoulutuksenAlkamisPvms();
            //only kausi + year, no date objects   
            komoto.setAlkamisVuosi(dto.getKoulutuksenAlkamisvuosi());
            komoto.setAlkamiskausiUri(convertToUri(dto.getKoulutuksenAlkamiskausi(), FieldNames.ALKAMISKAUSI));
        }
    }

    public static KoulutusValidationMessages validateDates(Date targetDate, Set<Date> dates, KoulutusmoduuliToteutus komoto) {
        final String baseKausi = IndexDataUtils.parseKausiKoodi(targetDate);
        final Integer baseVuosi = IndexDataUtils.parseYearInt(targetDate);

        if (baseKausi == null) {
            return KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_MISSING;
        }

        if (baseVuosi == null) {
            return KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_INVALID;
        }

        //pre-check if the dates are within same date range of kausi + vuosi
        for (Date pvm : dates) {
            if (!baseKausi.equals(IndexDataUtils.parseKausiKoodi(pvm))) {
                return KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_INVALID;
            }

            if (!baseVuosi.equals(IndexDataUtils.parseYearInt(pvm))) {
                return KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_INVALID;
            }

            if (komoto != null) {
                komoto.addKoulutuksenAlkamisPvms(pvm);
            }
        }

        return KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_SUCCESS;
    }

    public static KoulutusValidationMessages validateDates(Date targetDate, Set<Date> dates) {
        return validateDates(targetDate, dates, null);
    }

    public String convertToUri(final KoodiV1RDTO dto, final FieldNames msg) {
        return convertToUri(dto, msg, false);
    }

    /**
     * TODO: missing koodi uri validation check.
     */
    public String convertToUri(final KoodiV1RDTO dto, final FieldNames msg, boolean nullable) {
        if (nullable && (dto == null || dto.getUri() == null || dto.getUri().isEmpty())) {
            //nullable koodisto uri
            return null;
        }

        Preconditions.checkNotNull(dto, "KoodiV1RDTO object cannot be null! Error in field : %s.", msg);
        Preconditions.checkNotNull(dto.getUri(), "KoodiV1RDTO's koodisto koodi URI cannot be null! Error in field : %s.", msg);
        Preconditions.checkNotNull(dto.getVersio(), "KoodiV1RDTO's koodisto koodi version for koodi '%s' cannot be null! Error in field : %s.", dto.getUri(), msg);
        return convertToKoodiUri(dto.getUri(), dto.getVersio(), msg);
    }

    public String convertToKoodiUri(final String uri, final Integer version, final FieldNames msg) {
        //check data
        Integer checkVersion = version;
        if (checkVersion == null || checkVersion == -1) {
            //search latest koodi version for the koodi uri.
            final KoodiType koodi = tarjontaKoodistoHelper.getKoodiByUri(uri);
            Preconditions.checkNotNull(koodi, "Koodisto koodi not found! Error in field : %s by koodi URI : '%s' ", msg, uri);
            checkVersion = koodi.getVersio();
        }

        return new StringBuilder(uri)
                .append('#')
                .append(checkVersion).toString();
    }

    public Set<KoodistoUri> convertToUris(final KoodiUrisV1RDTO dto, Set<KoodistoUri> koodistoUris, final FieldNames msg) {
        Preconditions.checkNotNull(dto, "DTO object cannot be null! Error field : " + msg);

        Set<KoodistoUri> modifiedUris = Sets.<KoodistoUri>newHashSet(koodistoUris);
        if (koodistoUris == null) {
            modifiedUris = Sets.<KoodistoUri>newHashSet();
        }

        if (dto.getUris() != null) {
            for (Map.Entry<String, Integer> uriWithVersion : dto.getUris().entrySet()) {
                modifiedUris.add(new KoodistoUri(convertToKoodiUri(uriWithVersion.getKey(), uriWithVersion.getValue(), msg)));
            }
        }

        return modifiedUris;
    }

    public Set<KoodistoUri> convertToUris(final KoodiV1RDTO dto, Set<KoodistoUri> koodistoUris, final FieldNames msg) {
        Preconditions.checkNotNull(dto, "DTO object cannot be null! Error field : " + msg);

        Set<KoodistoUri> modifiedUris = Sets.<KoodistoUri>newHashSet(koodistoUris);
        if (koodistoUris == null) {
            modifiedUris = Sets.<KoodistoUri>newHashSet();
        }

        if (dto != null) {
            modifiedUris.add(new KoodistoUri(convertToKoodiUri(dto.getUri(), dto.getVersio(), msg)));
        }

        return modifiedUris;
    }

    public KoodiValikoimaV1RDTO convertToKielivalikoimaDTO(Map<String, Kielivalikoima> tarjotutKielet, Locale locale, boolean showMeta) {
        KoodiValikoimaV1RDTO result = new KoodiValikoimaV1RDTO();
        if (tarjotutKielet != null && !tarjotutKielet.isEmpty()) {
            for (String key : tarjotutKielet.keySet()) {
                final Kielivalikoima v = tarjotutKielet.get(key);
                if (v != null && v.getKielet() != null && !v.getKielet().isEmpty()) {
                    result.put(key, convertToKoodiUrisDTO(v.getKielet(), locale, FieldNames.KIELIVALIKOIMA, showMeta));
                }
            }
        }

        return result;
    }

    public void convertToKielivalikoima(KoodiValikoimaV1RDTO tarjotutKielet, KoulutusmoduuliToteutus komoto) {
        if (tarjotutKielet != null && !tarjotutKielet.isEmpty()) {
            for (Entry<String, KoodiUrisV1RDTO> e : tarjotutKielet.entrySet()) {
                if (e.getValue() != null && e.getValue().getUris() != null && !e.getValue().getUris().isEmpty()) {
                    List<String> uris = Lists.<String>newArrayList();
                    for (Entry<String, Integer> uriAndVersio : e.getValue().getUris().entrySet()) {
                        uris.add(new StringBuilder(uriAndVersio.getKey()).append('#').append(uriAndVersio.getValue()).toString());
                    }

                    komoto.setKieliValikoima(e.getKey(), uris);
                }
            }
        }
    }

    public Set<WebLinkki> convertToLinkkis(WebLinkki.LinkkiTyyppi type, final String uri, Set<WebLinkki> linkkis) {
        Set<WebLinkki> modifiedUris = Sets.<WebLinkki>newHashSet(linkkis);
        if (linkkis == null) {
            modifiedUris = Sets.<WebLinkki>newHashSet();
        }
        modifiedUris.add(new WebLinkki(type, null, uri));
        return modifiedUris;
    }

    public MonikielinenTeksti convertToTexts(final NimiV1RDTO dto, final FieldNames msg) {
        Preconditions.checkNotNull(dto, "Language map object cannot be null! Error field : " + msg);
        Preconditions.checkNotNull(dto.getTekstis(), "Language map objects cannot be null! Error in field : " + msg);

        MonikielinenTeksti mt = new MonikielinenTeksti();
        for (Map.Entry<String, String> kieliAndText : dto.getTekstis().entrySet()) {
            koodistoUri.validateKieliUri(kieliAndText.getKey());
            mt.addTekstiKaannos(kieliAndText.getKey(), kieliAndText.getValue());
        }

        return mt;
    }

}
