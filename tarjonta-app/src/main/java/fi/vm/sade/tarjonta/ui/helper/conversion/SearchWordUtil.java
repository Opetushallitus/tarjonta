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
package fi.vm.sade.tarjonta.ui.helper.conversion;

import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
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

    public static MonikielinenTekstiTyyppi createSearchKeywords(final KoulutusToisenAsteenPerustiedotViewModel koulutus) {
        //add all multilanguage strings as search keywords
        if (koulutus == null) {
            throw new RuntimeException("KoulutusToisenAsteenPerustiedotViewModel cannot be null.");
        }

        Map<String, StringBuilder> langKeywords = new HashMap<String, StringBuilder>();
        appendTyyppi(langKeywords, koulutus.getKoulutuskoodiModel());
        appendTyyppi(langKeywords, koulutus.getKoulutusohjelmaModel());

        MonikielinenTekstiTyyppi monikielinenTekstiTyyppi = new MonikielinenTekstiTyyppi();

        for (Entry<String, StringBuilder> e : langKeywords.entrySet()) {
            String str = e.getValue().toString();
            if (str.length() > MAX_SIZE_CHARACTERS) {
                //max size of the database column field
                str = str.substring(0, MAX_SIZE_CHARACTERS);
            }

            Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
            teksti.setKieliKoodi(e.getKey());
            teksti.setValue(str);
            monikielinenTekstiTyyppi.getTeksti().add(teksti);
        }

        return monikielinenTekstiTyyppi;
    }

    /**
     * Append all name fields to StringBuilder object.
     *
     * @param keywords
     * @param model
     */
    private static void appendTyyppi(Map<String, StringBuilder> map, final MonikielinenTekstiModel model) {
        if (model != null && model.getNimi() != null) {
            for (KielikaannosViewModel nimi : model.getKielikaannos()) {
                MonikielinenTekstiTyyppi.Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
                appendLangStringBuffer(map, nimi.getKielikoodi(), nimi.getNimi());
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
