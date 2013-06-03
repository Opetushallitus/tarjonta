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
package fi.vm.sade.tarjonta.shared;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisVersioSelectionType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Koodisto related helpers used from UI and Servier side.
 *
 * @author mlyly
 */
@Component
public class TarjontaKoodistoHelper {

    public static final String KOODI_URI_AND_VERSION_SEPARATOR = "#";
    public static final String LANGUAGE_SEPARATOR = ", ";
    private static final Logger LOG = LoggerFactory.getLogger(TarjontaKoodistoHelper.class);
    @Autowired
    private KoodistoService _koodistoService;
    @Autowired
    private KoodiService _koodiService;
    @Value("${koodisto.language.fi.uri:kieli_fi}")
    private String langKoodiUriFi;
    @Value("${koodisto.language.en.uri:kieli_en}")
    private String langKoodiUriEn;
    @Value("${koodisto.language.sv.uri:kieli_sv}")
    private String langKoodiUriSv;

    public TarjontaKoodistoHelper() {
        LOG.info("*** TarjontaKoodistoHelper ***");
    }

    /**
     * Try to convert any language related value to different "kieli" URI.
     *
     * <pre>
     * kieliX --> kieliX
     * EN,en -> kieli_en#1
     * SV,sv -> kieli_sv#1
     * FI,fi -> kieli_fi#1
     *
     * Search "kieli" koodisto for suitable value
     *
     * Finally, give up and return the provided value
     * </pre>
     *
     * @param kieli
     * @return
     */
    public String convertKielikoodiToKieliUri(String kieli) {
        String result = kieli;

        if (kieli == null) {
            return result;
        }

        String tmp = kieli.toLowerCase();

        if (tmp.startsWith("kieli")) {
            // All "kieli" kodiisto values
            result = kieli;
        } else if (tmp.startsWith("fi")) {
            // "fi", "FI", "fi_fi", ...
            result = langKoodiUriFi;
        } else if (tmp.startsWith("en")) {
            // "EN", "en", "en_US", ...
            result = langKoodiUriEn;
        } else if (tmp.startsWith("sv")) {
            result = langKoodiUriSv;
        } else {
            SearchKoodisCriteriaType skct = new SearchKoodisCriteriaType();
            skct.setKoodiArvo(kieli);
            skct.setKoodiVersioSelection(SearchKoodisVersioSelectionType.LATEST);

            List<KoodiType> koodis = _koodiService.searchKoodis(skct);
            for (KoodiType koodiType : koodis) {
                if (koodiType.getKoodisto().getKoodistoUri().equals("kieli")) {
                    result = koodiType.getKoodiUri();
                    break;
                }
            }
        }

        LOG.info("convertKielikoodiToKieliUri({}) --> '{}'", kieli, result);

        return result;
    }


    /**
     * Construct versioned koodi uri (adds #version to the end of uri).
     *
     * @param koodiType
     * @return
     */
    public static String createKoodiUriWithVersion(KoodiType koodiType) {
        return koodiType.getKoodiUri() + KOODI_URI_AND_VERSION_SEPARATOR + koodiType.getVersio();
    }

    /**
     * Split uri from "#" and extract uri without version number.
     *
     * @param koodiUriWithVersion
     * @return koodiUri without version information
     */
    public static String getKoodiURIFromVersionedUri(String koodiUriWithVersion) {
        return splitKoodiURIWithVersion(koodiUriWithVersion)[0];
    }

    /**
     * Get koodi version from versioned URI. -1 means no version could be extracted.
     *
     * @param koodiUriWithVersion
     * @return
     */
    public static int getKoodiVersionFromVersionedUri(String koodiUriWithVersion) {
        return Integer.parseInt(splitKoodiURIWithVersion(koodiUriWithVersion)[1]);
    }

    /**
     * Split koodi URI with (possible) version number to array of "uri" and "version". If no version can be extracted return "-1" as version.
     *
     * @param koodiUriWithVersion
     * @return
     */
    public static String[] splitKoodiURIWithVersion(final String koodiUriWithVersion) {
        if (koodiUriWithVersion == null) {
            throw new IllegalArgumentException("Koodi uri with version string object cannot be null.");
        }

        String[] result = new String[2];

        int index = koodiUriWithVersion.lastIndexOf(KOODI_URI_AND_VERSION_SEPARATOR);
        if (index > 0) {
            result[0] = koodiUriWithVersion.substring(0, index);
            result[1] = koodiUriWithVersion.substring(index + KOODI_URI_AND_VERSION_SEPARATOR.length());
        } else {
            LOG.warn("splitKoodiURIWithVersion - URI '{}' cannot be parsed to URI and Version array.", koodiUriWithVersion);
            result[0] = koodiUriWithVersion;
            result[1] = "-1";
        }

        return result;
    }

    /**
     * Creates koodi uri for storage, appends: koodi URI + "#" + version number.
     *
     * @param uri
     * @param version
     * @return
     */
    public static String createVersionedKoodiUri(String uri, int version) {
        return new StringBuilder(uri)
                .append(KOODI_URI_AND_VERSION_SEPARATOR)
                .append(version).toString();
    }

    /**
     * Get koodi from Koodisto with given URI.
     *
     * @param koodiUriWithPossibleVersionInformation
     * @return
     */
    public KoodiType getKoodiByUri(String koodiUriWithPossibleVersionInformation) {
        LOG.info("getKoodiByUri({})", koodiUriWithPossibleVersionInformation);

        KoodiType result = null;

        if (koodiUriWithPossibleVersionInformation == null) {
            return result;
        }

        try {
            String uri = getKoodiURIFromVersionedUri(koodiUriWithPossibleVersionInformation);
            int version = getKoodiVersionFromVersionedUri(koodiUriWithPossibleVersionInformation);

            // Search for the give koodi (and version)
            SearchKoodisCriteriaType searchCriteria;

            if (version < 0) {
                searchCriteria = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(uri);
            } else {
                searchCriteria = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(uri, version);
            }

            List<KoodiType> queryResult = _koodiService.searchKoodis(searchCriteria);
            if (queryResult != null && queryResult.size() == 1) {
                result = queryResult.get(0);
            } else {
                result = null;
            }

            LOG.info("  --> result = {}", result);
        } catch (Exception ex) {
            LOG.error("Failed to get KoodiType for koodi: " + koodiUriWithPossibleVersionInformation, ex);
            result = null;
        }

        return result;
    }

    /**
     * Get koodi nimi (name) with given locale.
     *
     * @param koodiUriWithPossibleVersionInformation
     * @param locale
     * @return
     */
    public String getKoodiNimi(String koodiUriWithPossibleVersionInformation, Locale locale) {
        LOG.info("getKoodiNimi({}, {})", koodiUriWithPossibleVersionInformation, locale);

        String result = null;

        if (locale == null) {
            locale = I18N.getLocale();
        }

        KoodiType koodi = getKoodiByUri(koodiUriWithPossibleVersionInformation);
        if (koodi == null) {
            result = null;
        } else {

            // Get metadata
            KoodiMetadataType kmdt = getKoodiMetadataForLanguage(koodi, locale);

            if (kmdt != null) {
                result = kmdt.getNimi();
            }
        }

        LOG.info("  --> result = {}", result);

        return result;
    }

    /**
     * Get the localized name.
     *
     * @param koodiType
     * @param locale
     * @return
     */
    public String getKoodiNimi(KoodiType koodiType, Locale locale) {
        if (koodiType == null) {
            return null;
        }
        return getKoodiNimi(createKoodiUriWithVersion(koodiType), locale);
    }

    /**
     * Get kode names as comma separated list for a given language.
     *
     * @param koodiUrisWithPossibleVersion
     * @param locale
     * @return comma separated string of names
     */
    public String getKoodiNimi(Collection<String> koodiUrisWithPossibleVersion, Locale locale) {
        StringBuilder result = new StringBuilder();

        if (koodiUrisWithPossibleVersion != null) {
            for (String koodiUri : koodiUrisWithPossibleVersion) {
                result.append(LANGUAGE_SEPARATOR).append(getKoodiNimi(koodiUri, locale));
            }
        }

        // Strip first comma if any
        final String str = result.toString();
        return str.length() == 0 ? str : str.substring(LANGUAGE_SEPARATOR.length());
    }

    /**
     * Get koodi metadata by locale with language fallback to FI
     *
     * @param koodiType
     * @param locale
     * @return
     */
    public KoodiMetadataType getKoodiMetadataForLanguage(KoodiType koodiType, Locale locale) {
        KoodiMetadataType kmdt = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KoodistoHelper.getKieliForLocale(locale));
        if (kmdt == null || (kmdt.getNimi() == null || kmdt.getNimi().length() == 0)) {
            // Try finnish if current locale is not found
            kmdt = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KieliType.FI);
        }

        return kmdt;
    }

    /**
     * Convert Tarjonta koodi uri and version string to KoodiUriAndVersioType object.
     *
     * @param koodiUriWithVersion
     * @return
     */
    public static KoodiUriAndVersioType getKoodiUriAndVersioTypeByKoodiUriAndVersion(final String koodiUriWithVersion) {
        KoodiUriAndVersioType type = new KoodiUriAndVersioType();
        type.setKoodiUri(getKoodiURIFromVersionedUri(koodiUriWithVersion));
        type.setVersio(getKoodiVersionFromVersionedUri(koodiUriWithVersion));
        return type;
    }

    /**
     * Get koodisto relationa for given koodi.
     *
     * @param koodiUri
     * @param targetKoodistoName
     * @param suhteenTyyppiType
     * @param alasuhde
     * @return
     */
    public Collection<KoodiType> getKoodistoRelations(String koodiUri, String targetKoodistoName, SuhteenTyyppiType suhteenTyyppiType, boolean alasuhde) {
        LOG.info("getKoodistoRelations(kuri={}, tkn={}, stt={}, as={})", new Object[] {koodiUri, targetKoodistoName, suhteenTyyppiType, alasuhde});

        Collection<KoodiType> result = new HashSet<KoodiType>();

        // Get koodi
        KoodiType sourceKoodiType = getKoodiByUri(koodiUri);

        // Create uri + version
        KoodiUriAndVersioType koodi = getKoodiUriAndVersioTypeByKoodiUriAndVersion(createKoodiUriWithVersion(sourceKoodiType));

        // Get relations
        List<KoodiType> relatedKoodis = _koodiService.listKoodiByRelation(koodi, alasuhde, suhteenTyyppiType);
        if (targetKoodistoName == null || targetKoodistoName.trim().isEmpty()) {
            result.addAll(relatedKoodis);
        } else {
            // Select relations to target koodis
            for (KoodiType relatedKoodi : relatedKoodis) {
                LOG.info("  considering: {}", relatedKoodi.getKoodiUri());
                if (targetKoodistoName.equals(relatedKoodi.getKoodisto().getKoodistoUri()))  {
                    LOG.info("    -- OK!");
                    result.add(relatedKoodi);
                }
            }
        }

        LOG.info("  --> result, koodi type {} relations: {}", koodiUri, result);
        for (KoodiType koodiType : result) {
            LOG.info("    {}", koodiType.getKoodiUri());
        }

        return result;

    }

}
