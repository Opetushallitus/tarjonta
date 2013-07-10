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
package fi.vm.sade.tarjonta.ui.view.common;

import com.vaadin.data.Property;
import com.vaadin.ui.DateField;
import java.util.Date;

/**
 *
 * @author Jani Wilén
 */
public class DateTimeField extends DateField {

    private static final long serialVersionUID = 1L;
    private static String DATE_FORMAT_NO_TIME = "^\\s*+\\d{1,2}.\\d{1,2}.\\d{4}\\s*+$";
    private static String TIME_FORMAT_NO_DATE = "^\\s*\\d{2}:\\d{2}\\s*+$";
    private String emptyErrorMessage;
    private String missingTimeMessage;
    private String missingDateMessage;

    @Override
    protected Date handleUnparsableDateString(String dateString) throws Property.ConversionException {
        return parseErrors(dateString);
    }

    public Date parseErrors(String text) {
        if (text == null) {
            throw new Property.ConversionException(getEmptyErrorMessage());
        } else if (text.trim().matches(TIME_FORMAT_NO_DATE)) {
            throw new Property.ConversionException(getMissingDateMessage());
        } else if (text.trim().matches(DATE_FORMAT_NO_TIME)) {
            throw new Property.ConversionException(getMissingTimeMessage());
        } else {
            throw new Property.ConversionException(getParseErrorMessage());
        }
    }

    /**
     * @return the emptyErrorMessage
     */
    public String getEmptyErrorMessage() {
        return emptyErrorMessage;
    }

    /**
     * @param emptyErrorMessage the emptyErrorMessage to set
     */
    public void setEmptyErrorMessage(String emptyErrorMessage) {
        this.emptyErrorMessage = emptyErrorMessage;
    }

    /**
     * @return the missingTimeMessage
     */
    public String getMissingTimeMessage() {
        return missingTimeMessage;
    }

    /**
     * @param missingTimeMessage the missingTimeMessage to set
     */
    public void setMissingTimeMessage(String missingTimeMessage) {
        this.missingTimeMessage = missingTimeMessage;
    }

    /**
     * @return the missingDateMessage
     */
    public String getMissingDateMessage() {
        return missingDateMessage;
    }

    /**
     * @param missingDateMessage the missingDateMessage to set
     */
    public void setMissingDateMessage(String missingDateMessage) {
        this.missingDateMessage = missingDateMessage;
    }
}
