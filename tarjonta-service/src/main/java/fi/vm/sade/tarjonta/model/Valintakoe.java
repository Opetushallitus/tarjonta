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

import fi.vm.sade.generic.model.BaseEntity;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "valintakoe")
public class Valintakoe extends BaseEntity {

    private static final long serialVersionUID = 7092585555234995829L;

    /**
     * Collection of times when this Valintakoe is to be held. This collection is loaded
     * eagerly since the number of items and amount of data will be very small.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "valintakoe_id")
    private Set<ValintakoeAjankohta> ajankohtas = new HashSet<ValintakoeAjankohta>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "kuvaus_monikielinenteksti_id")
    private MonikielinenTeksti kuvaus;

    /**
     * Valintakokeen tyyppi. Koodisto uri.
     */
    private String tyyppiUri;

    /**
     * Returns an immutable set of Ajankohtas.
     *
     * @return
     */
    public Set<ValintakoeAjankohta> getAjankohtas() {
        return Collections.unmodifiableSet(ajankohtas);
    }

    public void removeAjankohta(ValintakoeAjankohta ajankohta) {
        ajankohtas.remove(ajankohta);
    }

    public void addAjankohta(ValintakoeAjankohta ajankohta) {
        ajankohtas.add(ajankohta);
    }

    /**
     * @return the kuvaus
     */
    public MonikielinenTeksti getKuvaus() {
        return kuvaus;
    }

    /**
     * @param kuvaus the kuvaus to set
     */
    public void setKuvaus(MonikielinenTeksti kuvaus) {
        this.kuvaus = kuvaus;
    }

    /**
     * Kertaoo valintakokeen tyypin, esim. sovelutuvuuskoe (arvoltaan koodisto uri).
     *
     * @return
     */
    public String getTyyppiUri() {
        return tyyppiUri;
    }

    /**
     * Tyyppi, koodisto uri.
     *
     * @param tyyppiUri
     */
    public void setTyyppiUri(String tyyppiUri) {
        this.tyyppiUri = tyyppiUri;
    }

}

