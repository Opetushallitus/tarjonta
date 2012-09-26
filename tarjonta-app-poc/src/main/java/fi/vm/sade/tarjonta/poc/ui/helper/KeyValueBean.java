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
package fi.vm.sade.tarjonta.poc.ui.helper;

import java.io.Serializable;

/**
 *
 * @author mlyly
 */
public class KeyValueBean implements Serializable {
    
    /**
     * Used to access and display "value" field in selects and lists.
     */
    public static String VALUE = "value";
    
    private String _key;
    private String _value;

    public KeyValueBean() {
    }
    
    public KeyValueBean(String k, String v) {
        _key = k;
        _value = v;
    }
    
    public void setKey(String key) {
        this._key = key;
    }
    
    public String getKey() {
        return _key;
    }
    
    public void setValue(String value) {
        this._value = value;
    }
    
    public String getValue() {
        return _value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("KeyValueBean[id=");
        sb.append(_key);
        sb.append(", value=");
        sb.append(_value);
        sb.append("]");
        return sb.toString();
    }
    
    
}
