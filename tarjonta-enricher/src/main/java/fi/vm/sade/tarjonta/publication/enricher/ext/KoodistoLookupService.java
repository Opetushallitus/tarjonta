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
package fi.vm.sade.tarjonta.publication.enricher.ext;

import java.util.Map;

/**
 * Simplified data contract used by content enrichment handlers to lookup
 * Koodisto data to inject into XML. This allows the actual data to come e.g.
 * from live Koodisto link or from pre-fetched data.
 *
 * @author Jukka Raanamo
 */
public interface KoodistoLookupService {

    /**
     * Lookup Koodi by uri and version. Returns null if not found.
     *
     * @param uri
     * @param version
     * @return
     */
    public KoodiValue lookupKoodi(String uri, Integer version);

    /**
     *
     */
    public interface KoodiValue {

        public static final String LANG_FI = "fi";
        public static final String LANG_EN = "en";
        public static final String LANG_SV = "sv";

        /**
         * Returns the uri that was used to look up this koodi.
         *
         * @return
         */
        public String getUri();

        /**
         * Returns koodi's value (arvo).
         *
         * @return
         */
        public String getValue();

        /**
         * Returns koodi's name from it's metadata in asked language. Note that
         * supported languages are usually fi, en, sv.
         *
         * @param lang
         * @return
         */
        public String getMetaName(String lang);
    }

    public static class SimpleKoodiValue implements KoodiValue {

        private String nameFI;
        private String nameEN;
        private String nameSV;
        private String uri;
        private String value;

        public SimpleKoodiValue(String uri, String value, String nameFI, String nameEN, String nameSV) {
            this.nameFI = nameFI;
            this.nameEN = nameEN;
            this.nameSV = nameSV;
            this.uri = uri;
            this.value = value;
        }

        @Override
        public String getMetaName(String lang) {

            // koodisto is likely not to support more than 3 languages, hence hard coded
            // fields instead of a e.g. Map

            if (LANG_FI.endsWith(lang)) {
                return nameFI;
            } else if (LANG_SV.equals(lang)) {
                return nameSV;
            } else if (LANG_EN.equals(lang)) {
                return nameEN;
            }

            return null;

        }

        @Override
        public String getUri() {
            return uri;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    public KoodiValue searchKoodiRelation(final String koodiUri);

    /**
     * Get cached ISO language codes in a map. Koodisto URI is map key and
     * the ISO language code is map value.
     *
     * @return Map<koodiUri, isoLangCode>
     */
    public Map<String, String> getCachedKoodistoLanguageCodes();
    
    public String getLanguageCodeByKoodiUri(final String koodiUri);
}
