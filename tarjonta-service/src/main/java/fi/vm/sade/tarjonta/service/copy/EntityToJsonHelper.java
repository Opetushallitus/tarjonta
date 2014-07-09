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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import java.io.StringWriter;
import java.io.Writer;

/**
 *
 * @author jani
 */
public class EntityToJsonHelper {

    private static final Gson GSON = JsonFieldFilter.createPreConfiguredGsonBuilder().create();
    private static final JsonFieldFilter GSON_HAKUKOHDE;
    private static final JsonFieldFilter GSON_KOMOTO;
    private static final String[] FILTER_FIELDS_KOULUTUSMODUULI_TOTEUTUS = new String[]{"koulutusmoduuli", "hakukohdes"};
    private static final String[] FILTER_FIELDS_HAKUKOHDE = new String[]{};

    static {
        GSON_HAKUKOHDE = new JsonFieldFilter(Hakukohde.class, FILTER_FIELDS_HAKUKOHDE);
        GSON_KOMOTO = new JsonFieldFilter(KoulutusmoduuliToteutus.class, FILTER_FIELDS_KOULUTUSMODUULI_TOTEUTUS);
    }

    public static String convertToJson(Object entity) {
        if (entity instanceof Hakukohde) {
            return convertToJson((Hakukohde) entity);
        } else if (entity instanceof KoulutusmoduuliToteutus) {
            return convertToJson((KoulutusmoduuliToteutus) entity);
        } else {
            //no filter specified
            return GSON.toJson(entity);
        }
    }

    public static String convertToJson(Hakukohde entity) {
        return GSON_HAKUKOHDE.getJson(entity);
    }

    public static String convertToJson(KoulutusmoduuliToteutus entity) {
        return GSON_KOMOTO.getJson(entity);
    }

    public static Object convertToEntity(String jsonEntity, Class clazz) {
        return GSON.fromJson(jsonEntity, clazz);
    }

    public static String convertToFullJson(Object obj) {
        return GSON.toJson(obj);
    }
}
