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
package fi.vm.sade.tarjonta.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import fi.vm.sade.security.xssfilter.FilterXss;

/**
 *
 */
@Entity
@Table(name = "valintakoe_ajankohta")
public class ValintakoeAjankohta extends TarjontaBaseEntity {

    private static final long serialVersionUID = -2304365086611685405L;


    @Temporal(TemporalType.TIMESTAMP)
    private Date alkamisaika;

    @Temporal(TemporalType.TIMESTAMP)
    private Date paattymisaika;

    @FilterXss
    private String lisatietoja;

    private Osoite ajankohdanOsoite;

    public Date getAlkamisaika() {
        return alkamisaika;
    }

    public void setAlkamisaika(Date alkamisaika) {
        this.alkamisaika = alkamisaika;
    }

    public Date getPaattymisaika() {
        return paattymisaika;
    }

    public void setPaattymisaika(Date paattymisaika) {
        this.paattymisaika = paattymisaika;
    }

    public String getLisatietoja() {
        return lisatietoja;
    }

    public void setLisatietoja(String lisatietoja) {
        this.lisatietoja = lisatietoja;
    }


    public Osoite getAjankohdanOsoite() {
        return ajankohdanOsoite;
    }

    public void setAjankohdanOsoite(Osoite ajankohdanOsoite) {
        this.ajankohdanOsoite = ajankohdanOsoite;
    }
}