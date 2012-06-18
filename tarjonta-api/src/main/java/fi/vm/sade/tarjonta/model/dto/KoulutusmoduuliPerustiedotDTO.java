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
package fi.vm.sade.tarjonta.model.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliPerustiedotDTO implements Serializable {

    private static final long serialVersionUID = 4328770061817006214L;

    private String koulutusKoodiUri;

    private List<String> opetusmuotos = new ArrayList<String>();

    private List<String> opetuskielis = new ArrayList<String>();
    
    private Collection<String> asiasanoituses = new ArrayList<String>();

    /*
     * Uri to Koodisto.
     */
    private String suunniteltuKestoUri;

    public String getKoulutusKoodiUri() {
        return koulutusKoodiUri;
    }

    public void setKoulutusKoodiUri(String koulutusKoodiUri) {
        this.koulutusKoodiUri = koulutusKoodiUri;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("koulutusKoodiUri", koulutusKoodiUri).
            toString();
    }

    /**
     *
     * @return the list of opetusmuotos
     */
    public List<String> getOpetusmuotos() {
        return opetusmuotos;
    }

    /**
     *
     * @param opetusmuotos the list of opetusmuotos to set
     */
    public void setOpetusmuotos(List<String> opetusmuotos) {
        this.opetusmuotos = opetusmuotos;
    }

    /**
     *
     * @return the opetuskielis
     */
    public List<String> getOpetuskielis() {
        return opetuskielis;
    }

    /**
     *
     * @param opetuskielis the opetuskielis to set
     */
    public void setOpetuskielis(List<String> opetuskielis) {
        this.opetuskielis = opetuskielis;
    }

    /**
     *
     * @return the suunniteltuKesto
     */
    public String getSuunniteltuKestoUri() {
        return suunniteltuKestoUri;
    }

    /**
     *
     * @param suunniteltuKesto the suunniteltuKestoUri to set
     */
    public void setSuunniteltuKestoUri(String suunniteltuKestoUri) {
        this.suunniteltuKestoUri = suunniteltuKestoUri;
    }

    /**
     * @return the asiasanoituses
     */
    public Collection<String> getAsiasanoituses() {
        return asiasanoituses;
    }

    /**
     * @param asiasanoituses the asiasanoituses to set
     */
    public void setAsiasanoituses(Collection<String> asiasanoituses) {
        this.asiasanoituses = asiasanoituses;
    }

}

