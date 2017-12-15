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

import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Jani Wilén
 */
public class SearchWordUtil {

    private static final int MAX_SIZE_CHARACTERS = 255;
    private static final String SUFFIX = ", ";

    public static Map<String, String> createSearchKeywords(final KoulutusTyyppi koulutus) {
        //add all multilanguage strings as search keywords
        if (koulutus == null) {
            throw new RuntimeException("KoulutusTyyppi cannot be null.");
        }

        Map<String, StringBuilder> langKeywords = new HashMap<String, StringBuilder>();
        appendTyyppi(langKeywords, koulutus.getKoulutusKoodi());
        appendTyyppi(langKeywords, koulutus.getKoulutusohjelmaKoodi());
        //appendTyyppi(langKeywords, koulutus.getKoulutusaste());

        Map<String, String> outputLangKeywords = new HashMap<String, String>();

        langKeywords.forEach((key, value) -> {
            String str = value.toString();
            if (str.length() > MAX_SIZE_CHARACTERS) {
                //max size of the database column field
                str = str.substring(0, MAX_SIZE_CHARACTERS);
            }
            outputLangKeywords.put(key, str);
        });

        return outputLangKeywords;
    }

    /**
     * Append all name fields to StringBuilder object.
     *
     * @param keywords
     * @param tyyppi
     */
    public static void appendTyyppi(Map<String, StringBuilder> map, final KoodistoKoodiTyyppi tyyppi) {
        if (tyyppi != null && tyyppi.getNimi() != null) {
            for (KoodistoKoodiTyyppi.Nimi nimi : tyyppi.getNimi()) {
                appendLangStringBuffer(map, nimi.getKieli(), nimi.getValue());

                if (tyyppi.getArvo() != null && tyyppi.getNimi() != null && tyyppi.getNimi().isEmpty()) {
                    appendLangStringBuffer(map, nimi.getKieli(), tyyppi.getArvo());
                }
            }
        }
    }

    private static void appendLangStringBuffer(Map<String, StringBuilder> langKeywords, final String key, final String value) {
        if (langKeywords.containsKey(key)) {
            langKeywords.get(key).append(SUFFIX).append(value);
        } else {
            StringBuilder keywords = new StringBuilder();
            keywords.append(value);
            langKeywords.put(key, keywords);
        }
    }
}
