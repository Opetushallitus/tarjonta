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
package fi.vm.sade.tarjonta.service.copy;

import com.google.common.collect.Lists;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.DateFormat;
import java.util.List;

/**
 *
 * @author jani
 */
public class JsonFieldFilter {

    private final Gson gson;

    public static GsonBuilder createPreConfiguredGsonBuilder() {
        GsonBuilder b = new GsonBuilder();
        b.setDateFormat(DateFormat.FULL, DateFormat.FULL);
        return b;
    }

    public static Gson build(final List<String> fieldExclusions, final List<Class<?>> classExclusions) {
        GsonBuilder b = createPreConfiguredGsonBuilder();
        b.addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return fieldExclusions == null ? false : fieldExclusions.contains(f.getName());
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return classExclusions == null ? false : classExclusions.contains(clazz);
            }
        });
        return b.create();
    }

    public JsonFieldFilter(Class clazz, String[] fieldExclusions) {
        gson = build(Lists.<String>newArrayList(fieldExclusions), null);
    }

    public String getJson(Object obj) {
        return gson.toJson(obj);
    }
}
