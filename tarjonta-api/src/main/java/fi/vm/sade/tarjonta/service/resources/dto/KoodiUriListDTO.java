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
package fi.vm.sade.tarjonta.service.resources.dto;

import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jani Wilén
 */
public class KoodiUriListDTO extends MonikielinenTekstiTyyppi {

    private static final long serialVersionUID = 1L;
    private String uri;
    private String version;
    private String arvo;
    private List<MonikielinenTekstiTyyppi.Teksti> availableLanguages;

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the availableLanguages
     */
    public List<MonikielinenTekstiTyyppi.Teksti> getAvailableLanguages() {
        if (availableLanguages  == null) {
            availableLanguages = new ArrayList<MonikielinenTekstiTyyppi.Teksti>();
        }

        return availableLanguages;
    }

    /**
     * @param availableLanguages the availableLanguages to set
     */
    public void setAvailableLanguages(List<MonikielinenTekstiTyyppi.Teksti> availableLanguages) {
        this.availableLanguages = availableLanguages;
    }

    /**
     * @return the arvo
     */
    public String getArvo() {
        return arvo;
    }

    /**
     * @param arvo the arvo to set
     */
    public void setArvo(String arvo) {
        this.arvo = arvo;
    }
}
