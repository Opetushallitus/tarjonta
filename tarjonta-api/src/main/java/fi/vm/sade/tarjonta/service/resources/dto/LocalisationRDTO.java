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
package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * A localisation object for a given language / languages.
 *
 * Example of a data structure.
 *
 * <pre>
 * [
 *   {
 *     key : "tarjonta.organisaationHaku.hae"
 *     locale: "fi",
 *     value: "Tee haku",
 *     values: {
 *       "fi" : "Tee haku",
 *       "en" : "Perform the search",
 *       "sv" : "Sök - sök!"
 *     }
 *   },
 *   ...
 * ]
 * </pre>
 *
 * @author mlyly
 */
public class LocalisationRDTO extends BaseRDTO {

    private String key;
    private String locale;
    private String value;
    private Map<String, String> _values = null;

    public LocalisationRDTO() {
        super();
    }

    public LocalisationRDTO(String key, String locale, String value) {
        this();

        this.key = key;
        this.locale = locale;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, String> getValues() {
        return _values;
    }

    public void setValues(Map<String, String> _values) {
        this._values = _values;
    }
}
