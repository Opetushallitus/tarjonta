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
package fi.vm.sade.tarjonta.ui.model;

import java.io.Serializable;

/**
 *
 * @author Jani Wilén
 */
public class KoulutusohjelmaModel implements Serializable {
    private String koodiUri;
    private String id;
    private String name;
    private String fullName;

    public KoulutusohjelmaModel(String koodiUri, String id, String name) {
        this.koodiUri = koodiUri;
        this.id = id;
        this.name = name;
        this.fullName = name + " " + id;
    }

    /**
     * @return the koodiUri
     */
    public String getKoodiUri() {
        return koodiUri;
    }

    /**
     * @param koodiUri the koodiUri to set
     */
    public void setKoodiUri(String koodiUri) {
        this.koodiUri = koodiUri;
    }

    /**
     * @return the koodi
     */
    public String getId() {
        return id;
    }

    /**
     * @param koodi the koodi to set
     */
    public void setId(String koodi) {
        this.id = koodi;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
