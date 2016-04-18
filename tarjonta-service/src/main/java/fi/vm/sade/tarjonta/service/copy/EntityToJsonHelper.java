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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jani
 */
public class EntityToJsonHelper {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(EntityToJsonHelper.class);
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY) // auto-detect all member fields
                .setVisibility(JsonMethod.GETTER, JsonAutoDetect.Visibility.NONE) // but only public getters
                .setVisibility(JsonMethod.IS_GETTER, JsonAutoDetect.Visibility.NONE); // and none of "is-setters"
    }

    public static String convertToJson(Object entity) {
        Writer strWriter = new StringWriter();
        String json = null;
        try {
            mapper.writeValue(strWriter, entity);
            json = strWriter.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Convert object to JSON failed", ex);
        } finally {
            try {
                strWriter.close();  // close the writer
            } catch (IOException e) {
                LOG.error("StringWriter close failed");
            }
        }
        return json;
    }

    public static Object convertToEntity(String jsonEntity, Class clazz) {
        try {
            return mapper.readValue(jsonEntity, clazz);
        } catch (IOException ex) {
            LOG.error("Convert JSON to object failed. JSON : '{}'", jsonEntity, ex);
        }
        return null;
    }
}
