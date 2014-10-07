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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * @author Jani Wilén
 */
@Configuration
@PropertySource({
        "classpath:tarjonta-app.properties",
        "classpath:tarjonta-rest.properties",
        "file:///${user.home:''}/oph-configuration/common.properties",
        "file:///${user.home:''}/oph-configuration/tarjonta-app.properties"
})
public class Configurations {

    private static final Logger LOG = LoggerFactory.getLogger(Configurations.class);

    @Autowired
    private ConfigurableEnvironment env;

    public ConfigurableEnvironment getEnv() {
        return env;
    }

    @PostConstruct
    public void init() {
        try {
            tryToLoadOverrideProperties();
        } catch (IOException e) {
            LOG.info("override.properties not loaded.");
        }
    }

    private void tryToLoadOverrideProperties() throws IOException {
        MutablePropertySources propertySources = env.getPropertySources();
        String path = "file:///" + env.getProperty("user.home") + "/oph-configuration/override.properties";
        ResourcePropertySource overrideProperties = new ResourcePropertySource(path);
        propertySources.addFirst(overrideProperties);
    }
}
