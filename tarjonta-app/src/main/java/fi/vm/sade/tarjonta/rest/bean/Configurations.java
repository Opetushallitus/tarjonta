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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 *
 * @author Jani Wilén
 */
@Configuration
@PropertySource({
        "classpath:tarjonta-app.properties",
        "classpath:tarjonta-rest.properties",
        "file:///${user.home:''}/oph-configuration/common.properties",
        "file:///${user.home:''}/oph-configuration/tarjonta-app.properties",
        "file:///${user.home:''}/oph-configuration/override.properties"
})
public class Configurations {

    @Autowired
    private Environment env;

    public Environment getEnv() {
        return env;
    }
}
