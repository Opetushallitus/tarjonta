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

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class SearchWordUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SearchWordUtil.class);
    private static final int MAX_SIZE_CHARACTERS = 255;
    private static final String SUFFIX = ", ";

    public static MonikielinenTekstiTyyppi createSearchKeywords(final List<KoodiMetadataType> koulutuskoodi, List<KoodiMetadataType> koulutusohjelma, TarjontaKoodistoHelper tarjontaKoodistoHelper) {
        //add all multilanguage strings as search keywords
        if (koulutuskoodi == null) {
            throw new RuntimeException("koulutuskoodi list object cannot be null.");
        }

        if (koulutusohjelma == null) {
            throw new RuntimeException("koulutusohjelma list object cannot be null.");
        }

        Map<KieliType, StringBuilder> langKeywords = new EnumMap<KieliType, StringBuilder>(KieliType.class);
        appendTyyppi(langKeywords, koulutuskoodi);
        appendTyyppi(langKeywords, koulutusohjelma);

        MonikielinenTekstiTyyppi monikielinenTekstiTyyppi = new MonikielinenTekstiTyyppi();

        for (Entry<KieliType, StringBuilder> e : langKeywords.entrySet()) {
            String str = e.getValue().toString();
            if (str.length() > MAX_SIZE_CHARACTERS) {
                //max size of the database column field
                str = str.substring(0, MAX_SIZE_CHARACTERS);
            }

            Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
            teksti.setKieliKoodi(tarjontaKoodistoHelper.convertKielikoodiToKieliUri(e.getKey().value().toLowerCase()));
            teksti.setValue(str);
            monikielinenTekstiTyyppi.getTeksti().add(teksti);
        }

        if (monikielinenTekstiTyyppi.getTeksti().isEmpty()) {
            LOG.warn("No name text created.");
        }

        return monikielinenTekstiTyyppi;
    }

    /**
     * Append all name fields to StringBuilder object.
     *
     * @param keywords
     * @param tyyppit
     */
    private static void appendTyyppi(Map<KieliType, StringBuilder> map, final List<KoodiMetadataType> tyyppit) {
        if (tyyppit != null) {

            for (KoodiMetadataType type : tyyppit) {
                if (type != null && type.getNimi() != null) {
                    appendLangStringBuffer(map, type.getKieli(), type.getNimi());
                }
            }
        }
    }

    private static void appendLangStringBuffer(Map<KieliType, StringBuilder> langKeywords, final KieliType key, final String value) {
        if (langKeywords.containsKey(key)) {
            langKeywords.get(key).append(SUFFIX).append(value);
        } else {
            StringBuilder keywords = new StringBuilder();
            keywords.append(value);
            langKeywords.put(key, keywords);
        }
    }
}
