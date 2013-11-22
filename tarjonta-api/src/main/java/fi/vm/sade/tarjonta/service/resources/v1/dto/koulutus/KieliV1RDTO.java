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
package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import java.io.Serializable;

/**
 *
 * @author Jani Wilén
 */
public class KieliV1RDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String kieliUri;
    private String kieliVersio;
    private String kieliArvo;
    private String kieliKaannos;

    public KieliV1RDTO() {
    }

    /**
     * @return the kieliUri
     */
    public String getKieliUri() {
        return kieliUri;
    }

    /**
     * @param kieliUri the kieliUri to set
     */
    public void setKieliUri(String kieliUri) {
        this.kieliUri = kieliUri;
    }

    /**
     * @return the kieliVersio
     */
    public String getKieliVersio() {
        return kieliVersio;
    }

    /**
     * @param kieliVersio the kieliVersio to set
     */
    public void setKieliVersio(String kieliVersio) {
        this.kieliVersio = kieliVersio;
    }

    /**
     * @return the kieliArvo
     */
    public String getKieliArvo() {
        return kieliArvo;
    }

    /**
     * @param kieliArvo the kieliArvo to set
     */
    public void setKieliArvo(String kieliArvo) {
        this.kieliArvo = kieliArvo;
    }

    /**
     * @return the kieliKaannos
     */
    public String getKieliKaannos() {
        return kieliKaannos;
    }

    /**
     * @param kieliKaannos the kieliKaannos to set
     */
    public void setKieliKaannos(String kieliKaannos) {
        this.kieliKaannos = kieliKaannos;
    }

}
