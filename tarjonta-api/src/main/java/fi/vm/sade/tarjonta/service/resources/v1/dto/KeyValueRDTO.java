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
package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple DTO for simple usage.
 *
 * This is ment to be used in "dynamic" situations, when the data can be pretty much anything.
 *
 * MAYBE NOT A GOOD IDEA?
 *
 * @author mlyly
 */
public class KeyValueRDTO extends HashMap<String, Serializable> {

    public KeyValueRDTO() {
    }

    public KeyValueRDTO(String key, Serializable value) {
        this();
        put(key, value);
    }
}
