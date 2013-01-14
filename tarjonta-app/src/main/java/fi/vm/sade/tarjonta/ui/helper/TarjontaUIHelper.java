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
package fi.vm.sade.tarjonta.ui.helper;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.GetKoodistoByUriAndVersionType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.ListHakuVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

/**
 * Common UI helpers, formatters and so forth.
 *
 * Koodisto related helpers to access koodisto data.
 *
 * @author mlyly
 */
@Component
@Configurable(preConstruction = false)
public class TarjontaUIHelper {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaUIHelper.class);
    public static final String KOODI_URI_AND_VERSION_SEPARATOR = "#";
    private static final String LANGUAGE_SEPARATOR = ", ";
    @Autowired
    private KoodiService _koodiService;
    @Autowired(required = true)
    private TarjontaPublicService _tarjontaPublicService;
    private transient I18NHelper _i18n = new I18NHelper(TarjontaUIHelper.class);

    /**
     * Default version for those uris without version information is "-1".
     *
     * @param koodiUriWithVersion
     * @return String array with [koodiUri, koodiVersion]
     */
    private String[] splitKoodiURIWithVersion(String koodiUriWithVersion) {
        return splitKoodiURI(koodiUriWithVersion);
    }

    /**
     * Extract version number from uri.
     *
     * @param koodiUriWithVersion
     * @return version number, -1 means no version available
     */
    public int getKoodiVersion(String koodiUriWithVersion) {
        return Integer.parseInt(splitKoodiURIWithVersion(koodiUriWithVersion)[1]);
    }

    /**
     * Split uri from "#" and extract uri without version number.
     *
     * @param koodiUriWithVersion
     * @return koodiUri without version information
     */
    public String getKoodiURI(String koodiUriWithVersion) {
        return splitKoodiURIWithVersion(koodiUriWithVersion)[0];
    }

    /**
     * Get koodi's localized name with current UI locale. Uses versioned koodi
     * data if given.
     *
     * @param koodiUriWithPossibleVersionInformation
     * @return
     */
    public String getKoodiNimi(String koodiUriWithPossibleVersionInformation) {
        return getKoodiNimi(koodiUriWithPossibleVersionInformation, null);
    }

    public String getHakukohdeHakukentta(String hakuOid, Locale locale, String hakuKohdeNimi) {
        StringBuilder result = new StringBuilder();
        result.append(getAllHakukohdeNimet(hakuKohdeNimi));
        result.append(" ");
        result.append(getHakuKausiJaVuosi(hakuOid, locale));
        return result.toString();
    }

    private String getHakuKausiJaVuosi(String hakuOid, Locale locale) {
        StringBuilder hakuTiedot = new StringBuilder();
        ListaaHakuTyyppi hakuCriteria = new ListaaHakuTyyppi();
        hakuCriteria.setHakuOid(hakuOid);
        ListHakuVastausTyyppi vastaus = _tarjontaPublicService.listHaku(hakuCriteria);
        for (HakuTyyppi haku : vastaus.getResponse()) {
            hakuTiedot.append(getKoodiNimi(haku.getHakukausiUri(), locale));
            hakuTiedot.append(" ");
            hakuTiedot.append(haku.getHakuVuosi());
        }

        return hakuTiedot.toString();
    }

    private String getAllHakukohdeNimet(String hakuKohdeNimi) {
        StringBuilder nimet = new StringBuilder();
        List<KoodiType> koodit = _koodiService.searchKoodis(KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(getKoodiURI(hakuKohdeNimi)));
        for (KoodiType koodi : koodit) {
            List<KoodiMetadataType> metas = koodi.getMetadata();
            for (KoodiMetadataType meta : metas) {
                nimet.append(meta.getNimi());
                nimet.append(" ");
            }
        }

        return nimet.toString();
    }

    /**
     * Search koodis by koodisto uri, the uri can be with or without koodisto
     * version information.
     *
     * @param uri
     * @return
     */
    public List<KoodiType> getKoodisByKoodisto(String uri) {
        SearchKoodisByKoodistoCriteriaType criteriUri = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(getKoodiURI(uri));
        return _koodiService.searchKoodisByKoodisto(criteriUri);
    }

    /**
     * Search koodis by koodi uri, the uri can be with or without koodi version
     * information.
     *
     * @param uri
     * @return
     */
    public List<KoodiType> gethKoodis(String uri) {
        final String[] spitByUriAndVersion = splitKoodiURIAllowNull(uri);
        final String version = spitByUriAndVersion[1];
        return gethKoodis(spitByUriAndVersion[0], version == null ? null : Integer.valueOf(version));
    }

    /**
     * Search koodis by koodi uri and version information.
     *
     * @param uri
     * @return
     */
    public List<KoodiType> gethKoodis(String uri, Integer version) {
        SearchKoodisCriteriaType criteria;

        if (version == null) {
            criteria = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUri(uri);
        } else {
            criteria = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(uri, version);
        }

        return _koodiService.searchKoodis(criteria);
    }

    /**
     * Get koodi's name in given locale. If nimi for given
     * <code>locale</locale> is not found, we try to return it with locale "FI".
     * Uses versioned koodi data if given.
     *
     * @param koodiUriWithPossibleVersionInformation
     * @param locale if null, then I18N.getLocale() used
     * @return empty string or koodi metadatas localized name, in error cases
     * also error text is given
     */
    public String getKoodiNimi(String koodiUriWithPossibleVersionInformation, Locale locale) {
        String result = "";

        try {
            if (koodiUriWithPossibleVersionInformation != null) {
                if (locale == null) {
                    locale = I18N.getLocale();
                }

                String uri = getKoodiURI(koodiUriWithPossibleVersionInformation);
                int version = getKoodiVersion(koodiUriWithPossibleVersionInformation);

                // Search for the give koodi (and version)
                SearchKoodisCriteriaType searchCriteria;

                if (version < 0) {
                    searchCriteria = KoodiServiceSearchCriteriaBuilder.latestValidAcceptedKoodiByUri(uri);
                } else {
                    searchCriteria = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(uri, version);
                }

                List<KoodiType> queryResult = _koodiService.searchKoodis(searchCriteria);

                if (queryResult != null && queryResult.size() >= 1) {
                    // Get metadata
                    KoodiMetadataType kmdt = getKoodiMetadataForLanguage(queryResult.get(0), locale);

                    if (kmdt != null) {
                        result = kmdt.getNimi();
                    } else {
                        result = _i18n.getMessage("_koodiMetadataError", koodiUriWithPossibleVersionInformation, locale);
                    }
                }
            }
        } catch (Throwable ex) {
            LOG.error("Failed to read koodi from koodisto: koodi uri == " + koodiUriWithPossibleVersionInformation, ex);
            result = _i18n.getMessage("_koodiError", koodiUriWithPossibleVersionInformation);
        }

        LOG.debug("getKoodiNimi('{}', {}) --> {}", new Object[]{koodiUriWithPossibleVersionInformation, locale, result});

        return result;
    }

    /**
     * Get koodis localized nimi for set of uris.
     *
     * @param koodiUris
     * @param locale
     * @return comma separated string of names
     */
    public String getKoodiNimi(Collection<String> koodiUris, Locale locale) {
        StringBuilder result = new StringBuilder();

        if (koodiUris != null) {
            for (String koodiUri : koodiUris) {
                result.append(LANGUAGE_SEPARATOR).append(getKoodiNimi(koodiUri, locale));
            }
        }

        // Strip first comma
        final String str = result.toString();
        return str.length() == 0 ? str : str.substring(2);
    }

    public String getKoodiNimi(KoodiType koodiType, Locale locale) {
        if (koodiType == null) {
            return null;
        }

        if (locale == null) {
            locale = I18N.getLocale();
        }

        KoodiMetadataType kmdt;
        kmdt = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KoodistoHelper.getKieliForLocale(locale));

        if (kmdt == null) {
            kmdt = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KieliType.FI);
        }

        return  kmdt == null ? koodiType.getKoodiArvo() : kmdt.getNimi();
    }


    /**
     * Format date to string with default "dd.MM.yyyy" formatting.
     *
     * @param date
     * @return
     */
    public String formatDate(Date date) {
        return formatDate(date, "dd.MM.yyyy");
    }

    /**
     * Format date to string with default "dd.MM.yyyy HH:mm" formatting.
     *
     * @param date
     * @return
     */
    public String formatDateTime(Date date) {
        return formatDate(date, "dd.MM.yyyy HH:mm");
    }

    /**
     * Format date to string with default "HH:mm" formatting.
     *
     * @param date
     * @return
     */
    public String formatTime(Date date) {
        return formatDate(date, "HH:mm");
    }

    /**
     * Format date to given format.
     *
     * @param date if null, returns empty string
     * @param format
     * @return date formatted as string
     */
    public String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String createVersionUri(String uri, int version) {
        return new StringBuilder(uri)
                .append(KOODI_URI_AND_VERSION_SEPARATOR)
                .append(version).toString();

    }

    public static String[] splitKoodiURI(final String koodiUriWithVersion) {
        String[] result = new String[2];

        int index = koodiUriWithVersion.lastIndexOf(KOODI_URI_AND_VERSION_SEPARATOR);
        if (index > 0) {
            result[0] = koodiUriWithVersion.substring(0, index);
            result[1] = koodiUriWithVersion.substring(index + KOODI_URI_AND_VERSION_SEPARATOR.length());
        } else {
            result[0] = koodiUriWithVersion;
            result[1] = "-1";
        }

        return result;
    }

    public static String[] splitKoodiURIAllowNull(final String koodiUriWithVersion) {
        String[] result = new String[2];
        int index = koodiUriWithVersion.lastIndexOf(KOODI_URI_AND_VERSION_SEPARATOR);
        if (index > 0) {
            result[0] = koodiUriWithVersion.substring(0, index);
            result[1] = koodiUriWithVersion.substring(index + KOODI_URI_AND_VERSION_SEPARATOR.length());
        } else {
            result[0] = koodiUriWithVersion;
            result[1] = null;
        }

        return result;
    }

    public static KoodiMetadataType getKoodiMetadataForLanguage(KoodiType koodiType, Locale locale) {
        KoodiMetadataType kmdt = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KoodistoHelper.getKieliForLocale(locale));
        if (kmdt == null || (kmdt.getNimi() == null || kmdt.getNimi().length() == 0)) {
            // Try finnish if current locale is not found
            kmdt = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KieliType.FI);
        }

        return kmdt;
    }

    public static MonikielinenTekstiTyyppi.Teksti getClosestMonikielinenTekstiTyyppiName(Locale locale, MonikielinenTekstiTyyppi monikielinenTeksti) { 
        MonikielinenTekstiTyyppi.Teksti teksti = null;
        if (locale != null) {
            teksti = searchTekstiTyyppiByLanguage(monikielinenTeksti.getTeksti(), locale);
        }

        if (teksti == null || teksti.getKieliKoodi() == null || teksti.getValue() == null) {
            //FI default fallback
            final Locale locale1 = new Locale("FI");
            teksti = searchTekstiTyyppiByLanguage(monikielinenTeksti.getTeksti(), locale1);

            if (teksti == null || teksti.getKieliKoodi() == null || teksti.getValue() == null) {
                LOG.error("An invalid data error -´MonikielinenTekstiTyyppi object was missing Finnish language data.");
            }
        }
        return teksti;
    }

    public static String getClosestHakuName(Locale locale, HakuViewModel haku) {
        String lang = locale != null && locale.getLanguage() != null ? locale.getLanguage().toLowerCase() : "";

        if ("fi".equals(lang) && haku.getNimiFi() != null) {
            return haku.getNimiFi();
        }

        if ("sv".equals(lang) && haku.getNimiSe() != null) {
            return haku.getNimiSe();
        }

        if ("en".equals(lang) && haku.getNimiEn() != null) {
            return haku.getNimiEn();
        }

        return getAvailableHakuName(haku);
    }

    public static String getAvailableHakuName(HakuViewModel haku) {
        if (haku.getNimiFi() != null) {
            return haku.getNimiFi();
        }
        if (haku.getNimiSe() != null) {
            return haku.getNimiSe();
        }

        if (haku.getNimiEn() != null) {
            return haku.getNimiEn();
        }

        return "";
    }

    public static MonikielinenTekstiTyyppi.Teksti searchTekstiTyyppiByLanguage(List<MonikielinenTekstiTyyppi.Teksti> tekstis, final Locale locale) {
        LOG.debug("locale : " + locale.getLanguage() + ", teksti : " + (tekstis != null ? tekstis.size() : tekstis));
        final String langCode = locale.getLanguage().toUpperCase();

        for (MonikielinenTekstiTyyppi.Teksti teksti : tekstis) {

            if (teksti.getKieliKoodi() != null
                    && teksti.getKieliKoodi().toUpperCase().equals(langCode)) {
                return teksti;
            } else if (teksti.getKieliKoodi() == null) {
                LOG.error("An unknown data bug : MonikielinenTekstiTyyppi.Teksti KieliKoodi was null?");
            }
        }

        LOG.warn("no text found by locale : " + locale.getLanguage());

        return null;
    }
}
