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

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Jukka Raanamo
 */
@SuppressWarnings("serial")
public abstract class KoulutusmoduuliToteutusDTO implements Serializable {

    private KoulutusmoduuliTila tila;

    private String oid;

    private String nimi;

    private String toteutettavaKoulutusmoduuliOID;

    private KoulutusmoduuliPerustiedotDTO perustiedot;

    

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public void setPerustiedot(KoulutusmoduuliPerustiedotDTO perustiedot) {
        this.perustiedot = perustiedot;
    }

    public KoulutusmoduuliPerustiedotDTO getPerustiedot() {
        return perustiedot;
    }

    public String getToteutettavaKoulutusmoduuliOID() {
        return toteutettavaKoulutusmoduuliOID;
    }

    public void setToteutettavaKoulutusmoduuliOID(String toteutettavaKoulutusmoduuliOID) {
        this.toteutettavaKoulutusmoduuliOID = toteutettavaKoulutusmoduuliOID;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("oid", oid).
            append("nimi", nimi).
            append("tila", getTila()).
            append("toteutettavaKoulutusmoduuliOID", toteutettavaKoulutusmoduuliOID).
            append(perustiedot).toString();
    }

    /**
     * @return the tila
     */
    public KoulutusmoduuliTila getTila() {
        return tila;
    }

    /**
     * @param tila the tila to set
     */
    public void setTila(KoulutusmoduuliTila tila) {
        this.tila = tila;
    }

}

