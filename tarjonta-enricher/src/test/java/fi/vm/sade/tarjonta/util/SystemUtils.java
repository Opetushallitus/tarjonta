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
package fi.vm.sade.tarjonta.util;

/**
 * Convenience class for reading system properties.
 *
 * @author Jukka Raanamo
 */
public class SystemUtils {

    public static boolean isTrue(String systemProperty, boolean defaultValue) {
        return Boolean.parseBoolean(System.getProperty(systemProperty, defaultValue ? "true" : "false"));
    }

    public static boolean isTrue(String systemProperty) {
        return isTrue(systemProperty, false);
    }

    public static boolean isFalse(String systemProperty, boolean defaultValue) {
        return !isTrue(systemProperty, defaultValue);
    }

    /**
     * Print message to output stream if given system property is "true".
     *
     * @param msg
     * @param systemProperty
     */
    public static void printOutIf(String msg, String systemProperty) {
        if (isTrue(systemProperty)) {
            System.out.println(msg);
        }
    }

}

