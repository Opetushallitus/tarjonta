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
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY) // auto-detect all member fields
                .setVisibility(JsonMethod.GETTER, JsonAutoDetect.Visibility.NONE) // but only public getters
                .setVisibility(JsonMethod.IS_GETTER, JsonAutoDetect.Visibility.NONE); // and none of "is-setters"
    }

//    private static final Gson GSON = JsonFieldFilter.createPreConfiguredGsonBuilder().create();
//    private static final JsonFieldFilter GSON_HAKUKOHDE;
//    private static final JsonFieldFilter GSON_KOMOTO;
//    private static final String[] FILTER_FIELDS_KOULUTUSMODUULI_TOTEUTUS = new String[]{"koulutusmoduuli", "hakukohdes"};
//    private static final String[] FILTER_FIELDS_HAKUKOHDE = new String[]{"koulutusmoduuliToteutuses", "haku"};
//
//    static {
//        GSON_HAKUKOHDE = new JsonFieldFilter(Hakukohde.class, FILTER_FIELDS_HAKUKOHDE);
//        GSON_KOMOTO = new JsonFieldFilter(KoulutusmoduuliToteutus.class, FILTER_FIELDS_KOULUTUSMODUULI_TOTEUTUS);
//    }
//
//    public static String convertToJson(Object entity) {
//        if (entity instanceof Hakukohde) {
//            return convertToJson((Hakukohde) entity);
//        } else if (entity instanceof KoulutusmoduuliToteutus) {
//            return convertToJson((KoulutusmoduuliToteutus) entity);
//        } else {
//            //no filter specified
//            return GSON.toJson(entity);
//        }
//    }
//
//    public static String convertToJson(Hakukohde entity) {
//        return GSON_HAKUKOHDE.getJson(entity);
//    }
//
//    public static String convertToJson(KoulutusmoduuliToteutus entity) {
//        return GSON_KOMOTO.getJson(entity);
//    }
//
//    public static Object convertToEntity(String jsonEntity, Class clazz) {
//        return GSON.fromJson(jsonEntity, clazz);
//    }
//
//    public static String convertToFullJson(Object obj) {
//        return GSON.toJson(obj);
//    }
    public static String convertToJson(Object entity) {
        Writer strWriter = new StringWriter();
        String json = null;
        try {
            mapper.writeValue(strWriter, entity);
            json = strWriter.toString();
        } catch (IOException ex) {
            LOG.error("Convert object to JSON failed", ex);
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
