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
package fi.vm.sade.tarjonta.service.resources.dto.kk;

import java.io.Serializable;

/**
 *
 * @author Jani Wilén
 */
public class KoodiUriDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String uri;
    private String versio;
    private String arvo;
    private String kaannos;

    public KoodiUriDTO() {
    }

    public KoodiUriDTO(String uri, String versio, String arvo) {
        this.uri = uri;
        this.versio = versio;
        this.arvo = arvo;
    }
    
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
     * @return the versio
     */
    public String getVersio() {
        return versio;
    }

    /**
     * @param version the version to set
     */
    public void setVersio(String versio) {
        this.versio = versio;
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

    /**
     * @return the kaannos
     */
    public String getKaannos() {
        return kaannos;
    }

    /**
     * @param kaannos the kaannos to set
     */
    public void setKaannos(String kaannos) {
        this.kaannos = kaannos;
    }
}
