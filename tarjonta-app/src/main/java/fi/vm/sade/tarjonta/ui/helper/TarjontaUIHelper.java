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
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import java.text.SimpleDateFormat;
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
@Configurable(preConstruction=false)
public class TarjontaUIHelper {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaUIHelper.class);

    @Autowired
    private KoodiService _koodiService;

    private I18NHelper _i18n = new I18NHelper(TarjontaUIHelper.class);

    /**
     * Get koodi's name in given locale.
     *
     * @param koodiUri
     * @param locale if null, then I18N.getLocale() used
     * @return empty string or koodi metadatas localized name, in error cases also error text is given
     */
    public String getKoodiNimi(String koodiUri, Locale locale) {
        String result = "";

        try {
            if (koodiUri != null) {
                if (locale == null) {
                    locale = I18N.getLocale();
                }

                SearchKoodisCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.latestValidAcceptedKoodiByUri(koodiUri);
                List<KoodiType> queryResult = _koodiService.searchKoodis(searchCriteria);

                if (queryResult.size() == 1) {
                    result = KoodistoHelper.getKoodiMetadataForLanguage(queryResult.get(0), KoodistoHelper.getKieliForLocale(locale)).getNimi();
                }
            }
        } catch (Throwable ex) {
            LOG.error("Failed to read koodi from koodisto: koodi uri == " + koodiUri, ex);
            result = _i18n.getMessage("/koodiError", koodiUri);
        }

        LOG.info("getKoodiNimi({}, {}) --> {}", new Object[] {koodiUri, locale, result});

        return result;
    }


    /**
     * Get koodis localized nimi for set of uris.
     *
     * @param koodiUris
     * @param locale
     * @return comma separated string of names
     */
    public String getKoodiNimi(Set<String> koodiUris, Locale locale) {
        String result = "";

        if (koodiUris != null) {
            for (String koodiUri : koodiUris) {
                result += ", " + getKoodiNimi(koodiUri, locale);
            }
        }

        // Strip first comma
        return result.length() == 0 ? result : result.substring(2);
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


}
