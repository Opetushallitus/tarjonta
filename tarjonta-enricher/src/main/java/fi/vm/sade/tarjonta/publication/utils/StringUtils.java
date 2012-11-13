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
package fi.vm.sade.tarjonta.publication.utils;

import java.util.Deque;
import java.util.Iterator;

/**
 *
 * @author Jukka Raanamo
 */
public class StringUtils {

    private static final String EMPTY_STRING = "";

    /**
     * Returns true if value is null or value is empty or contains only white space chars.
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        return (value == null || EMPTY_STRING.equals(value.trim()));
    }

    /**
     * @see #isEmpty(java.lang.String)
     */
    public static boolean notEmpty(String value) {
        return !isEmpty(value);
    }


    public static String join(Deque deque, String delimiter) {

        StringBuilder sb = new StringBuilder();
        Iterator i = deque.descendingIterator();
        while (i.hasNext()) {
            sb.append(delimiter);
            sb.append(i.next().toString());
        }
        return sb.toString();

    }

}

