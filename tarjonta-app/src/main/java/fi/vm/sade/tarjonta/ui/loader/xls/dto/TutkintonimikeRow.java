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
package fi.vm.sade.tarjonta.ui.loader.xls.dto;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * @author Jani Wilén
 */
public class TutkintonimikeRow {

    private String relaatioKoodiarvo;
    private String tutkintonimikeKoodiarvo;

    /**
     * @return the relaatioKoodiarvo
     */
    public String getRelaatioKoodiarvo() {
        return relaatioKoodiarvo;
    }

    /**
     * @param relaatioKoodiarvo the relaatioKoodiarvo to set
     */
    public void setRelaatioKoodiarvo(String relaatioKoodiarvo) {
        this.relaatioKoodiarvo = relaatioKoodiarvo;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    /**
     * @return the tutkintonimikeKoodiarvo
     */
    public String getTutkintonimikeKoodiarvo() {
        return tutkintonimikeKoodiarvo;
    }

    /**
     * @param tutkintonimikeKoodiarvo the tutkintonimikeKoodiarvo to set
     */
    public void setTutkintonimikeKoodiarvo(String tutkintonimikeKoodiarvo) {
        this.tutkintonimikeKoodiarvo = tutkintonimikeKoodiarvo;
    }
}
