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
package fi.vm.sade.tarjonta.rest.bean;

import com.google.common.base.Preconditions;
import fi.vm.sade.tarjonta.rest.helper.PropertyPlaceholder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.rest.dto.JsonConfigObject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Jani Wilén
 */
@Controller
@RequestMapping("env")
public class JsonConfiguration {

    @Value("${importAllKeys.startsWith}")
    private String keysStartWith;
    @Value("${importAllKeys.contains}")
    private String keysContains;
    private static String configurationJson;
    @Autowired
    ApplicationContext context;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String help() {
        return "<p>"
                + "Return configuration JSON object : <environment>/<a href='/tarjonta-app/kk/ext/env/configuration.json'>tarjonta-app/kk/ext/env/configuration.json</a>.<br/>"
                + "Return configuration JSON object in JavaScript variable : <environment>/<a href='/tarjonta-app/kk/ext/env/configuration.js'>tarjonta-app/kk/ext/env/configuration.js</a>."
                + "</p>";
    }

    @RequestMapping(value = "/configuration.json", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getJsonConfig() {
        if (configurationJson == null) {
            final Properties propertyes = PropertyPlaceholder.getPropertyes();
            configurationJson = createJsonConfiguration(propertyes);
        }

        return configurationJson;
    }

    @RequestMapping(value = "/configuration.js", method = RequestMethod.GET, produces = "text/javascript")
    @ResponseBody
    public String getJsFile() {
        if (configurationJson == null) {
            final Properties propertyes = PropertyPlaceholder.getPropertyes();
            configurationJson = createJsonConfiguration(propertyes);
        }

        return "var CONFIG = " + configurationJson + ";";
    }

    private String createJsonConfiguration(Properties propertyes) {
        Set<String> visibleKeys = searchAllRequiredPropertyKeys(propertyes);

        //create configuration object
        final Configurations conf = context.getBean(Configurations.class);

        List<JsonConfigObject> jsons = Lists.<JsonConfigObject>newLinkedList();
        for (final String key : visibleKeys) {
            jsons.add(new JsonConfigObject(key, conf.getEnv().getProperty(key)));
        }

        Collections.sort(jsons, new Comparator<JsonConfigObject>() {
            @Override
            public int compare(JsonConfigObject a, JsonConfigObject b) {
                int ret = a.getKey().compareTo(b.getKey());
                return ret != 0 ? ret : a.getKey().compareTo(b.getKey());
            }
        });

        StringBuilder outputJson = new StringBuilder();
        outputJson.append("{ \"properties\" : ");
        outputJson.append(jsons.toString());
        outputJson.append("}");
        return outputJson.toString();
    }

    private Set<String> searchAllRequiredPropertyKeys(Properties propertyes) {
        Preconditions.checkNotNull(propertyes, "System properties object cannot be null.");
        final String[] arrKeysStartWith = keysStartWith.split(",");
        final String[] arrKeysContains = keysContains.split(",");

        Set<String> keysNeeded = Sets.<String>newHashSet();

        if ((arrKeysStartWith == null && arrKeysStartWith == null) || (arrKeysStartWith.length == 0 && arrKeysContains.length == 0)) {
            return keysNeeded;
        }

        for (Object keyObj : propertyes.keySet()) {
            final String realKey = (String) keyObj;

            //start with
            for (String k : arrKeysStartWith) {
                final String filter = k.trim();
                if (filter != null && realKey.startsWith(filter)) {
                    keysNeeded.add(realKey);
                }
            }

            //contains
            for (String k : arrKeysContains) {
                final String filter = k.trim();
                if (filter != null && realKey.contains(filter)) {
                    keysNeeded.add(realKey);
                }
            }
        }

        return keysNeeded;
    }
}