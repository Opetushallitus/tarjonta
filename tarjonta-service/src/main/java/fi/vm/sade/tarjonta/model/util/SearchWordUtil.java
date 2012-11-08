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
package fi.vm.sade.tarjonta.model.util;

import fi.vm.sade.tarjonta.service.types.tarjonta.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutusTyyppi;

/**
 *
 * @author Jani Wilén
 */
public class SearchWordUtil {

    private static final int MAX_SIZE_CHARACTERS = 255;
    private static final String SUFFIX = ", ";

    public static String createSearchKeywords(final KoulutusTyyppi koulutus) {
        //add all multilanguage strings as search keywords
        if (koulutus == null) {
            throw new RuntimeException("KoulutusTyyppi cannot be null.");
        }

        StringBuilder keywords = new StringBuilder();

        appendTyyppi(keywords, koulutus.getKoulutusKoodi());
        appendTyyppi(keywords, koulutus.getKoulutusohjelmaKoodi());
        appendTyyppi(keywords, koulutus.getKoulutusaste());

        final String str = keywords.toString();

        if (str.length() > MAX_SIZE_CHARACTERS) {
            //max size of the database column field
            return str.substring(0, MAX_SIZE_CHARACTERS);
        }

        return str;
    }

    /**
     * Append all name fields to StringBuilder object.
     *
     * @param keywords
     * @param tyyppi
     */
    public static void appendTyyppi(StringBuilder keywords, final KoodistoKoodiTyyppi tyyppi) {
        if (tyyppi != null) {
            if (tyyppi.getArvo() != null && tyyppi.getNimi() != null && tyyppi.getNimi().isEmpty()) {
                append(keywords, tyyppi.getArvo());
            }

            for (KoodistoKoodiTyyppi.Nimi nimi : tyyppi.getNimi()) {
                append(keywords, nimi.getValue());
            }
        }
    }

    private static void append(StringBuilder keywords, final String str) {
        keywords.append(str).append(SUFFIX);
    }
}
