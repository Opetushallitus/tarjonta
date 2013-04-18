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
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.*;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.*;
import static fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi.AMMATTIKORKEAKOULUTUS;
import fi.vm.sade.tarjonta.ui.enums.BasicLanguage;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;

import java.text.SimpleDateFormat;
import java.util.*;
import net.sf.ehcache.CacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
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
@EnableScheduling
public class TarjontaUIHelper {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaUIHelper.class);

    public static final String KOODI_URI_AND_VERSION_SEPARATOR = "#";

    private static final String LANGUAGE_SEPARATOR = ", ";

    @Autowired
    private KoodiService _koodiService;

    @Autowired(required = true)
    private TarjontaPublicService _tarjontaPublicService;

    private transient I18NHelper _i18n = new I18NHelper(TarjontaUIHelper.class);

    @Autowired
    private CacheManager _cacheManager;

    @Value("${koodisto.language.fi.uri:kieli_fi}")
    private String langKoodiUriFi;

    @Value("${koodisto.language.en.uri:kieli_en}")
    private String langKoodiUriEn;

    @Value("${koodisto.language.sv.uri:kieli_sv}")
    private String langKoodiUriSv;

    @Scheduled(cron = "0 */5 * * * ?")
    public void printCacheStats() {
        LOG.debug("---------- printCacheStats(): " + this);
        for (String cacheName : _cacheManager.getCacheNames()) {
            LOG.debug("  {}", _cacheManager.getCache(cacheName).getStatistics());
        }
    }

    /**
     * Splits koodiUri to URI and Version.
     * Default version for those uris without version information is "-1".
     *
     * @param koodiUriWithVersion
     * @return String array with [koodiUri, koodiVersion]
     */
    private static String[] splitKoodiURIWithVersion(String koodiUriWithVersion) {
        return splitKoodiURI(koodiUriWithVersion);
    }

    /**
     * Extract version number from uri.
     *
     * @param koodiUriWithVersion
     * @return version number, -1 means no version available
     */
    public static int getKoodiVersion(String koodiUriWithVersion) {
        return Integer.parseInt(splitKoodiURIWithVersion(koodiUriWithVersion)[1]);
    }

    /**
     * Get related hakukohde koodi URIs for given {komoto.koulutus.[lukiolinjakoodi, koulutusohjelmakoodi]}* list.
     *
     * @param komotoOids
     * @return collection of codes from KoodistoURIHelper.KOODISTO_HAKUKOHDE_URI koodisto.
     */
    public Collection<KoodiType> getRelatedHakukohdeKoodisByKomotoOids(List<String> komotoOids) {
        HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
        kysely.getKoulutusOids().addAll(komotoOids);
        HaeKoulutuksetVastausTyyppi vastaus = _tarjontaPublicService.haeKoulutukset(kysely);

        List<String> sourceKoodiUris = new ArrayList<String>();
        for (HaeKoulutuksetVastausTyyppi.KoulutusTulos koulutusTulos : vastaus.getKoulutusTulos()) {
            switch (koulutusTulos.getKoulutus().getKoulutustyyppi()) {
                case AMMATILLINEN_PERUSKOULUTUS:
                    sourceKoodiUris.add(koulutusTulos.getKoulutus().getKoulutusohjelmakoodi());
                    break;
                case LUKIOKOULUTUS:
                    sourceKoodiUris.add(koulutusTulos.getKoulutus().getLukiolinjakoodi());
                    break;

                case AMMATTIKORKEAKOULUTUS:
                case PERUSOPETUKSEN_LISAOPETUS:
                case YLIOPISTOKOULUTUS:
                default:
                    LOG.error("UNKNOWN KOULUTUSTYYPPI, CANNOT GET RELATED KOODIS FOR: {}", koulutusTulos.getKoulutus());
                    LOG.error("  koulutustyyppi == {}", koulutusTulos.getKoulutus().getKoulutustyyppi());
                    break;
            }
        }

        return getKoodistoRelationsForUris(sourceKoodiUris, KoodistoURIHelper.KOODISTO_HAKUKOHDE_URI);

//        List<KoodiUriAndVersioType> koodiVersios = new ArrayList<KoodiUriAndVersioType>();
//        for (HaeKoulutuksetVastausTyyppi.KoulutusTulos koulutusTulos : vastaus.getKoulutusTulos()) {
//            KoodiUriAndVersioType koodiUriAndVersioType = new KoodiUriAndVersioType();
//            if (koulutusTulos.getKoulutus().getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
//                koodiUriAndVersioType.setKoodiUri(getKoodiURI(koulutusTulos.getKoulutus().getLukiolinjakoodi()));
//                koodiUriAndVersioType.setVersio(getKoodiVersion(koulutusTulos.getKoulutus().getLukiolinjakoodi()));
//            } else {
//                koodiUriAndVersioType.setKoodiUri(getKoodiURI(koulutusTulos.getKoulutus().getKoulutusohjelmakoodi()));
//                koodiUriAndVersioType.setVersio(getKoodiVersion(koulutusTulos.getKoulutus().getKoulutusohjelmakoodi()));
//            }
//            koodiVersios.add(koodiUriAndVersioType);
//        }
//
//        return getRelatedParentKoodis(koodiVersios, SuhteenTyyppiType.SISALTYY);
    }

//    private Set<KoodiType> getRelatedParentKoodis(List<KoodiUriAndVersioType> parentKoodis, SuhteenTyyppiType suhdeTyyppi) {
//        return getRelatedParentKoodis(parentKoodis, false, suhdeTyyppi);
//    }
//
//    private Set<KoodiType> getRelatedParentKoodis(List<KoodiUriAndVersioType> parentKoodis, boolean alaKoodi, SuhteenTyyppiType suhdeTyyppi) {
//        LOG.debug("getRelatedParentKoodis(..)");
//
//        Set<KoodiType> koodiTypes = new HashSet<KoodiType>();
//
//        for (KoodiUriAndVersioType koodiUriAndVersioType : parentKoodis) {
//            List<KoodiType> koodis = _koodiService.listKoodiByRelation(koodiUriAndVersioType, alaKoodi, suhdeTyyppi);//SuhteenTyyppiType.SISALTYY);
//            koodiTypes.addAll(koodis);
//        }
//
//        return koodiTypes;
//    }

    /**
     * Construct versioned koodi uri (adds #version to the end of uri).
     *
     * @param koodiType
     * @return
     */
    private String createUriWithVersion(KoodiType koodiType) {
        return koodiType.getKoodiUri() + KOODI_URI_AND_VERSION_SEPARATOR + koodiType.getVersio();
    }

    /**
     * Split uri from "#" and extract uri without version number.
     *
     * @param koodiUriWithVersion
     * @return koodiUri without version information
     */
    protected String getKoodiURI(String koodiUriWithVersion) {
        if (koodiUriWithVersion == null) {
            throw new IllegalArgumentException("Koodi uri with version string object cannot be null.");
        }

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

    public String getHakukohdeHakukentta(String hakuOid, Locale locale, String hakukohdeNimi) {
        if (hakukohdeNimi == null) {
            throw new IllegalArgumentException("Hakukohde nimi koodi uri with version string object cannot be null.");
        }

        StringBuilder result = new StringBuilder();
        result.append(getConcatenatedNamesForGivenKoodiURI(hakukohdeNimi));
        result.append(", ");
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

    /**
     * Load koodi with metadata, returns all languages concatenated.
     *
     * @param hakukohdeUriVersioned
     * @return concatenated name from koodi's metadata getNimi()'s
     */
    private String getConcatenatedNamesForGivenKoodiURI(String hakukohdeUriVersioned) {
        LOG.debug("getConcatenatedNamesForGivenKoodiURI({})", hakukohdeUriVersioned);

        StringBuilder nimet = new StringBuilder();
        try {
            List<KoodiType> koodit = _koodiService.searchKoodis(KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(getKoodiURI(hakukohdeUriVersioned)));
            for (KoodiType koodi : koodit) {
                List<KoodiMetadataType> metas = koodi.getMetadata();
                for (KoodiMetadataType meta : metas) {
                    nimet.append(meta.getNimi());
                    nimet.append(" ");
                }
            }
        } catch (Exception e) {
            LOG.error("Koodi service not responding.", e);
            nimet.append(hakukohdeUriVersioned);
        }

        return nimet.toString();
    }

//    /**
//     * Search koodis by koodisto uri, the uri can be with or without koodisto
//     * version information.
//     *
//     * @param uri
//     * @return
//     */
//    public List<KoodiType> getKoodisByKoodisto(String uri) {
//        LOG.debug("getKoodisByKoodisto({})", uri);
//        SearchKoodisByKoodistoCriteriaType criteriUri = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(getKoodiURI(uri));
//        return _koodiService.searchKoodisByKoodisto(criteriUri);
//    }

    /**
     * Search koodis by koodi uri, the uri can be with or without koodi version
     * information.
     *
     * @param uri
     * @return
     */
    public List<KoodiType> getKoodis(String uri) {
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
    private List<KoodiType> gethKoodis(String uri, Integer version) {
        LOG.debug("getKoodis({}, {})", uri, version);
        SearchKoodisCriteriaType criteria;

        if (version == null) {
            criteria = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUri(uri);
        } else {
            criteria = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(uri, version);
        }

        return _koodiService.searchKoodis(criteria);
    }

//    /**
//     * Gets the list of KoulutuskoodiModel objects that match the list of
//     * oppilaitostyyppis given as parameter
//     *
//     * @param olTyyppiUris - the oppilaitostyyppi uris to match
//     * @param unfilteredKoulutuskoodis - the list of all KoulutuskoodiModel
//     * objects in the system
//     * @return the list of KoulutuskoodiModel objects that match the
//     * oppilaitostyyppis.
//     */
//    public List<KoulutuskoodiModel> getKoulutusFilteredkooditRelatedToOlTyypit(
//            List<String> olTyyppiUris,
//            List<KoulutuskoodiModel> unfilteredKoulutuskoodis) {
//        LOG.debug("getKoulutusFilteredKooditRelatedToOlTyypit");
//
//        List<KoulutuskoodiModel> filteredKoodit = new ArrayList<KoulutuskoodiModel>();
//        for (KoodiType curKoulutusKoodi : getOlRelatedKoulutuskoodit(olTyyppiUris)) {
//
//            KoulutuskoodiModel matchingKoulutus = getKoulutusByKoodi(curKoulutusKoodi, unfilteredKoulutuskoodis);
//            if (matchingKoulutus != null
//                    && !filteredKoodit.contains(matchingKoulutus)) {
//                filteredKoodit.add(matchingKoulutus);
//                LOG.debug("Match : {}", matchingKoulutus.getKoodistoUriVersio());
//            }
//        }
//
//        return filteredKoodit;
//    }

    /**
     * Returns the koulutuskoodit related to one or more of the olTyyppiUris
     * given as parameter.
     *
     * <pre>
     *   OppilaitosTyyppi -> KoulutusAsteKoodi -> KoulutusKoodi
     * </pre>
     *
     * As en exampple:
     * <pre>
     *   Ammattikoulu -> ammattikoulutus -> Hevostalouden perustutkinto
     *   Ammattikoulu -> ammattikoulutus -> Sirkusalan perustutkinto
     *   Lukio -> lukiokoulutus -> Hevostalous
     *   Lukio -> lukiokoulutus -> Ylioppilas
     * </pre>
     *
     * @param olTyyppiUris - the oppilaitostyyppi uris
     * @return list of koodis related from Oppilaitostyyppi to KoulutusAsteKoodi
     * to KoulutusKoodi
     */
    public Collection<KoodiType> getOlRelatedKoulutuskoodit(List<String> olTyyppiUris) {
        LOG.debug("getOlRelatedKoulutuskoodit({})", olTyyppiUris);

        return getKoodistoRelationsForUris(olTyyppiUris, KoodistoURIHelper.KOODISTO_KOULUTUSASTE_URI, KoodistoURIHelper.KOODISTO_TUTKINTO_URI);

//        //First the list of koulutusastekoodis that are related to the oppilaitostyyppis is fetched
//        List<KoodiType> koulutusasteKoodit = new ArrayList<KoodiType>();
//        for (String curUri : olTyyppiUris) {
//            LOG.debug("olTyyppiUri: {}", curUri);
//            SearchKoodisCriteriaType criteria = null;
//            if (curUri.split(TarjontaUIHelper.KOODI_URI_AND_VERSION_SEPARATOR).length > 1) {
//                KoodiUriAndVersioType uriAndVersio = getKoodiUriAndVersioTypeByKoodiUriAndVersion(curUri);
//                criteria = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(uriAndVersio.getKoodiUri(), uriAndVersio.getVersio());
//            } else {
//                criteria = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(curUri);
//            }
//            List<KoodiType> olTyyppiKoodit = _koodiService.searchKoodis(criteria);
//            LOG.debug("olTyyppiKoodit: {}", olTyyppiKoodit.size());
//            if (olTyyppiKoodit != null && !olTyyppiKoodit.isEmpty()) {
//                koulutusasteKoodit.addAll(getRelatedKoodit(olTyyppiKoodit, KoodistoURIHelper.KOODISTO_KOULUTUSASTE_URI, SuhteenTyyppiType.SISALTYY));
//            }
//        }
//
//        LOG.debug("koulutusasteKoodit: {}", koulutusasteKoodit.size());
//
//        //then the koulutuskoodi objects that are related to the koulutusastekoodis are fetced and returned.
//        return getRelatedKoodit(koulutusasteKoodit, null, true, SuhteenTyyppiType.SISALTYY);
    }


//    /*
//     * Returns the KoulutuskoodiModel objects that  matches the koulutusKoodi given as parameter.
//     */
//    private KoulutuskoodiModel getKoulutusByKoodi(KoodiType koulutusKoodi, List<KoulutuskoodiModel> unfilteredKoulutuskoodis) {
//        //LOG.debug("getKoulutusByKoodi: {}, {}", koulutusKoodi.getKoodiUri(), koulutusKoodi.getVersio());
//        for (KoulutuskoodiModel curKoulutus : unfilteredKoulutuskoodis) {
//            LOG.debug("getKoulutusByKoodi: {}, {}", curKoulutus.getKoodistoUri(), koulutusKoodi.getKoodiUri());
//            if (curKoulutus.getKoodistoUri().equals(koulutusKoodi.getKoodiUri())) {
//                LOG.debug("MATCH");
//                return curKoulutus;
//            }
//        }
//        return null;
//    }

//    /*
//     * Gets the koodis that are related to one or more of the koodit given as parameters with the suhdeTyyppi
//     * given as parameters.
//     */
//    private List<KoodiType> getRelatedKoodit(List<KoodiType> koodit, String koodistoUri, SuhteenTyyppiType suhdeTyyppi) {
//        return getRelatedKoodit(koodit, koodistoUri, false, suhdeTyyppi);
//    }

//    /*
//     * Gets the koodis that are related to one or more of the koodit given as parameters with the suhdeTyyppi
//     * given as parameters.
//     */
//    private List<KoodiType> getRelatedKoodit(List<KoodiType> koodit, String koodistoUri, boolean alaKoodi, SuhteenTyyppiType suhdeTyyppi) {
//        LOG.debug("getRelatedKoodit()");
//        List<KoodiType> relatedKoodit = new ArrayList<KoodiType>();
//        List<KoodiUriAndVersioType> koodiVersios = new ArrayList<KoodiUriAndVersioType>();
//        for (KoodiType curOlTyyppiKoodi : koodit) {
//            KoodiUriAndVersioType koodiUriAndVersioType = new KoodiUriAndVersioType();
//            koodiUriAndVersioType.setKoodiUri(curOlTyyppiKoodi.getKoodiUri());
//            koodiUriAndVersioType.setVersio(curOlTyyppiKoodi.getVersio());
//            LOG.debug("KoodiURI and versio: {}, {}", koodiUriAndVersioType.getKoodiUri(), koodiUriAndVersioType.getVersio());
//            koodiVersios.add(koodiUriAndVersioType);
//        }
//
//        for (KoodiType curKoodi : this.getRelatedParentKoodis(koodiVersios, alaKoodi, suhdeTyyppi)) {
//            if (koodistoUri == null || koodistoUri.equals(curKoodi.getKoodisto().getKoodistoUri())) {
//                LOG.debug("RelatedParentKoodi URI: {}", curKoodi.getKoodiUri());
//                relatedKoodit.add(curKoodi);
//            }
//        }
//        LOG.debug("relatedKoodit size: {}", relatedKoodit.size());
//
//        return relatedKoodit;
//    }

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
        // LOG.debug("getKoodiNimi('{}', {}) ...", new Object[]{koodiUriWithPossibleVersionInformation, locale});

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
                    searchCriteria = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(uri);
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

        //LOG.debug("getKoodiNimi('{}', {}) --> {}", new Object[]{koodiUriWithPossibleVersionInformation, locale, result});

        return result;
    }

    /**
     * Get koodis localized nimi for set of uris in given language.
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

        return kmdt == null ? koodiType.getKoodiArvo() : kmdt.getNimi();
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

    /**
     * Creates koodi uro for storage, appends: koodi URI + "#" + version number.
     *
     * @param uri
     * @param version
     * @return
     */
    public static String createVersionUri(String uri, int version) {
        return new StringBuilder(uri)
                .append(KOODI_URI_AND_VERSION_SEPARATOR)
                .append(version).toString();

    }

    /**
     * Extract components from the versioned koodi uri.
     *
     * @param koodiUriWithVersion
     * @return
     */
    public static String[] splitKoodiURI(final String koodiUriWithVersion) {
        if (koodiUriWithVersion == null) {
            throw new IllegalArgumentException("Koodi uri with version string object cannot be null.");
        }

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

    /**
     * Extract koodi uri and version from mayve versioned koodi URI.
     *
     * @param koodiUriWithVersion
     * @return
     */
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

    /**
     * Get koodi metadata by locale with language fallback to FI
     *
     * @param koodiType
     * @param locale
     * @return
     */
    public static KoodiMetadataType getKoodiMetadataForLanguage(KoodiType koodiType, Locale locale) {
        KoodiMetadataType kmdt = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KoodistoHelper.getKieliForLocale(locale));
        if (kmdt == null || (kmdt.getNimi() == null || kmdt.getNimi().length() == 0)) {
            // Try finnish if current locale is not found
            kmdt = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KieliType.FI);
        }

        return kmdt;
    }

    /**
     * Get text for "closest" match for a given language.
     * Actully this means exact match, if not found use FI.
     *
     * @param locale
     * @param monikielinenTeksti
     * @return
     */
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


    /**
     * Get closet Haku name for given language, fallback order is [fi, se, en].
     *
     * @param locale
     * @param haku
     * @return
     */
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


    private static String getAvailableHakuName(HakuViewModel haku) {
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

    /**
     * Select Teksti for a given locale.
     *
     * @param tekstis
     * @param locale
     * @return
     */
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

    /**
     * Convert Tarjonta koodi uri and version string to KoodiUriAndVersioType
     * object.
     *
     * @param koodiUriWithVersion
     * @return
     */
    public static KoodiUriAndVersioType getKoodiUriAndVersioTypeByKoodiUriAndVersion(final String koodiUriWithVersion) {
        final String[] splitUri = TarjontaUIHelper.splitKoodiURI(koodiUriWithVersion);
        KoodiUriAndVersioType type = new KoodiUriAndVersioType();
        type.setKoodiUri(splitUri[0]);
        type.setVersio(Integer.parseInt(splitUri[1]));

        LOG.debug("getKoodiUriAndVersioTypeByKoodiUriAndVersion : '{}' --> {}", splitUri, type);

        return type;
    }

    /**
     * Convert basic(fi,en,sv) language koodi URI to language enum. It also
     * converts 2 char language code to BasicLanguage enum. If no match, then it
     * will return BasicLanguage.FI.
     *
     * @param langCode
     * @return
     */
    public BasicLanguage toLanguageEnum(final String langCode) {
        if (langCode == null) {
            return BasicLanguage.FI;
        }

        final String trimmedLangCode = langCode.trim();

        if (trimmedLangCode.startsWith(langKoodiUriFi)) {
            return BasicLanguage.FI;
        } else if (trimmedLangCode.startsWith(langKoodiUriEn)) {
            return BasicLanguage.EN;
        } else if (trimmedLangCode.startsWith(langKoodiUriSv)) {
            return BasicLanguage.SV;
        } else {
            //final check before fallback to Finnish language:
            //it's possible that the language code string is real 'en' etc. language code.
            //if no match, it return BasicLanguage.FI
            return BasicLanguage.toLanguageEnum(trimmedLangCode);
        }
    }



    /**
     * Traverse koodisto relations. Returned related koodis are filtered in every step with given koodisto.
     *
     * An (pseudokode) example:
     * <pre>
     *   oppilaitosTyyppiUri --> [koulutusAlaKoodiUris] --> [koulutusKoodiUris]
     *
     *   getKoodistoRelations("ammattikoulu#1", ["koulutusAlaKoodistoUri", "koulutusKoodiKoodistoUri"]);
     * </pre>
     *
     * @param koodiUris the koodiUris to start from
     * @param koodistoUris the "path" to follow
     * @return
     */
    public Collection<KoodiType> getKoodistoRelationsForUris(Collection<String> koodiUris, String... koodistoUris) {
        Set<KoodiType> result = new HashSet<KoodiType>();

        // Loop over avery koodi and collect results
        for (String koodiUri : koodiUris) {
            result.addAll(getKoodistoRelations(koodiUri, koodistoUris));
        }

        return result;
    }


    /**
     * Extract relations for given KoodiType's and koodisto path.
     * @param koodis
     * @param koodistoUris
     * @return
     */
    public Collection<KoodiType> getKoodistoRelationsForKoodiTypes(Collection<KoodiType> koodis, String... koodistoUris) {
        Set<KoodiType> result = new HashSet<KoodiType>();

        for (KoodiType koodiType : koodis) {
            result.addAll(getKoodistoRelations(koodiType, koodistoUris));
        }

        return result;
    }

    /**
     * Extract relations for given koodisto path and KoodiType.
     *
     * @param koodi
     * @param koodistoUris
     * @return
     */
    public Collection<KoodiType> getKoodistoRelations(KoodiType koodi, String... koodistoUris) {
        String koodiUri = createUriWithVersion(koodi);
        return getKoodistoRelations(koodiUri, koodistoUris);
    }


    /**
     * Extract transitive koodisto relations with a path and koodi given.
     *
     * <nl>
     * <li>If koodisto uris == null or epty -> empty result</li>
     * <li>Get relations for given koodi in the first koodisto uri</li>
     * <li>If koodisto uris == single uri -> return the result in previous step</li>
     * <li>If multiple koodistos, loop over koodis and create recursive calls for each koodi + koodistos minus current koodisto</li>
     * </nl>
     *
     * @param koodiUri
     * @param koodistoUris
     * @return the end results for given koodisto relation "path"
     */
    public Collection<KoodiType> getKoodistoRelations(String koodiUri, String... koodistoUris) {
        LOG.info("getKoodistoRelations({}, {})", koodiUri, koodistoUris);

        Collection<KoodiType> result = new HashSet<KoodiType>();

        if (koodistoUris == null || koodistoUris.length == 0) {
            LOG.warn("empty target koodisto? return empty result.");
            return result;
        }

        // Current target koodisto (relations filtered with this one)
        String koodistoUri = koodistoUris[0];

        // Fetch relations to target koodisto
        // TODO getKoodistoRelations - alakoodi false? sisätyy?
        Collection<KoodiType> nextStepResult = getKoodistoRelations(koodiUri, koodistoUri, false, SuhteenTyyppiType.SISALTYY);

        // Final step, return the actual results
        if (koodistoUris.length == 1) {
            result = nextStepResult;
        }

        // Nonfinal step, use intermediate results to loop and fetch next step recursively
        if (koodistoUris.length > 1) {
            // Extract next koodistoUris (skip/exclude the current)
            String[] nextKoodistoUris = Arrays.copyOfRange(koodistoUris, 1, koodistoUris.length);

            Collection<KoodiType> tmp = new HashSet<KoodiType>();

            // Get the results for next step for each koodi
            for (KoodiType koodiType : nextStepResult) {
                tmp.addAll(getKoodistoRelations(koodiType, nextKoodistoUris));
            }

            return tmp;
        }

        LOG.info("  result ==> {}", result);
        return result;
    }


    /**
     * Get koodisto koodi relations.
     *
     * @param koodiUri koodi to search relatios for
     * @param koodistoUri only these will be returned, if null all relations returned
     * @param alaKoodi if true the relation is reversed?
     * @param suhdeTyyppi default SuhteenTyyppiType.SISALTYY
     * @return
     */
    public Collection<KoodiType> getKoodistoRelations(String koodiUri, String koodistoUri, boolean alaKoodi, SuhteenTyyppiType suhdeTyyppi) {
        LOG.info("getKoodistoRelations({}, {}, {}, {})", new Object[] {koodiUri, koodistoUri, alaKoodi, suhdeTyyppi});

        Set<KoodiType> result = new HashSet<KoodiType>();

        if (suhdeTyyppi == null) {
            suhdeTyyppi = SuhteenTyyppiType.SISALTYY;
        }

        // Convert Koodi URI to API type
        KoodiUriAndVersioType koodiUriAndVersioType = getKoodiUriAndVersioByKoodiUri(koodiUri);

        // Get relations and filter only wanted koodisto koodis
        List<KoodiType> resultKoodis = _koodiService.listKoodiByRelation(koodiUriAndVersioType, alaKoodi, suhdeTyyppi); //SuhteenTyyppiType.SISALTYY);
        for (KoodiType koodiType : resultKoodis) {
            if (koodistoUri == null || koodiType.getKoodisto().getKoodistoUri().equals(koodistoUri)) {
                result.add(koodiType);
            }
        }

        LOG.info(" --> {}", result);

        return result;
    }



    /**
     * Return koodi with uri and version.
     *
     * If koodi uri contains version it is constructed without service calls, otherwise latest koodi version will be queried from the KoodiService.
     *
     * @param koodiUri
     * @return
     */
    private KoodiUriAndVersioType getKoodiUriAndVersioByKoodiUri(String koodiUri) {
        // Does uri contain version information?
        if (koodiUri.indexOf(KOODI_URI_AND_VERSION_SEPARATOR) >= 0) {
           // yes, it does, construct the KoodiUriAndVersionType
            return getKoodiUriAndVersioTypeByKoodiUriAndVersion(koodiUri);
        } else {
            // Nope, search for the given koodi uri, query the latest from the service
            SearchKoodisCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(koodiUri);

            List<KoodiType> koodis = _koodiService.searchKoodis(searchCriteria);
            if (koodis == null || koodis.isEmpty()) {
                return null;
            }

            if (koodis.size() != 1) {
                LOG.warn("Koodi with uri: {} -- returns {} KoodiType's ... using first one.", koodiUri, koodis.size());
            }

            KoodiType kt = koodis.get(0);

            KoodiUriAndVersioType result = new KoodiUriAndVersioType();
            result.setKoodiUri(kt.getKoodiUri());
            result.setVersio(kt.getVersio());

            return result;
        }
    }
}
