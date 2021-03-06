/*
 *
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
package fi.vm.sade.tarjonta.service.helper;

import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Tuomas Katva
 */
public class DateHelper {

    /**
     * Converts a String representing a {@link XMLGregorianCalendar} to a
     * {@link Date}.
     *
     * @param xgc
     *            String to convert from
     * @return Computed date
     */
    public static Date parseDate(String xgc) {

        return DatatypeConverter.parseDate(xgc).getTime();
    }

    /**
     * Converts a {@link Date} to a String representing a
     * {@link XMLGregorianCalendar}.
     *
     * @param date
     *            Date to convert from
     * @return String representation
     */
    public static String printDate(Date date) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);

        return DatatypeConverter.printDate(calendar);
    }

    public static Date parseDateTime(String xmlDateTime) {
        return DatatypeConverter.parseDateTime(xmlDateTime).getTime();
    }

    public static String printDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return DatatypeConverter.printDateTime(calendar);
    }

}

