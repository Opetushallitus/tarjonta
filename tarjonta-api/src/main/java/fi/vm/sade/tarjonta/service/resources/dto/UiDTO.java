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

import java.io.Serializable;

/**
 *
 * @author Jani Wilén
 */
public class UiDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String teksti;
    private KoodiUriDTO koodi;
    private String arvo;

    /**
     * @return the teksti
     */
    public String getTeksti() {
        return teksti;
    }

    /**
     * @param teksti the teksti to set
     */
    public void setTeksti(String teksti) {
        this.teksti = teksti;
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
     * @return the koodi
     */
    public KoodiUriDTO getKoodi() {
        return koodi;
    }

    /**
     * @param koodi the koodi to set
     */
    public void setKoodi(KoodiUriDTO koodi) {
        this.koodi = koodi;
    }
}
