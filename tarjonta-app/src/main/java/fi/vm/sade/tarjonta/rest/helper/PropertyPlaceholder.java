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
package fi.vm.sade.tarjonta.rest.helper;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Properties;

public class PropertyPlaceholder extends PropertyPlaceholderConfigurer {

    private static Properties properties = new Properties();
    private static final PropertyPlaceholderHelper propertyHelper = new PropertyPlaceholderHelper("${", "}");

    public static Properties getProperties() {
        return properties;
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory,
                                     Properties props) throws BeansException {
        super.processProperties(beanFactory, props);

        for (Object keyObject : props.keySet()) {
            String key = keyObject.toString();
            String value = propertyHelper.replacePlaceholders(props.getProperty(key), props);
            properties.put(key, value);
        }
    }

    public static String getProperty(String name) {
        Object o = properties.get(name);
        if (o != null) {
            return String.valueOf(o);
        }
        return null;
    }
}
