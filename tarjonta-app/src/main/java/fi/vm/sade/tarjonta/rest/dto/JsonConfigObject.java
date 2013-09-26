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
package fi.vm.sade.tarjonta.rest.dto;

/**
 *
 * @author Jani Wilén
 */
public class JsonConfigObject {

    private static final String SEPARATOR_QUOTE = "\"";
    private static final String SEPARATOR = "\":\"";
    private String key;
    private String value;

    public JsonConfigObject() {
    }

    public JsonConfigObject(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SEPARATOR_QUOTE);
        sb.append(getKey());
        sb.append(SEPARATOR);
        sb.append(getValue());
        sb.append(SEPARATOR_QUOTE);
        return sb.toString();
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
