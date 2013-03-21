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

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 */
@Entity
@Table(name = "valintakoe")
public class Valintakoe extends BaseEntity {

    private static final long serialVersionUID = 7092585555234995829L;
    @Column(name = "hakukohde_id", insertable = false, updatable = false)
    private long hakukohdeId;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "valintakoe_id")
    private Set<ValintakoeAjankohta> ajankohtas = new HashSet<ValintakoeAjankohta>();
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "kuvaus_monikielinenteksti_id")
    private MonikielinenTeksti kuvaus;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "lisanaytot_monikielinenteksti_id")
    private MonikielinenTeksti lisanaytot;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Pisteraja> pisterajat = new HashSet<Pisteraja>();
    /**
     * Valintakokeen tyyppi. Koodisto uri.
     */
    private String tyyppiUri;

    /**
     * Collection of times when this Valintakoe is to be held. This collection
     * is loaded eagerly since the number of items and amount of data will be
     * very small.
     */
    /**
     * Returns an immutable set of Ajankohtas.
     *
     * @return
     */
    public Set<ValintakoeAjankohta> getAjankohtas() {
        return ajankohtas;
    }

    public void removeAjankohta(ValintakoeAjankohta ajankohta) {
        getAjankohtas().remove(ajankohta);
    }

    public void addAjankohta(ValintakoeAjankohta ajankohta) {
        getAjankohtas().add(ajankohta);
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
     * Kertaoo valintakokeen tyypin, esim. sovelutuvuuskoe (arvoltaan koodisto
     * uri).
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

    public void setAjankohtas(Set<ValintakoeAjankohta> ajankohtas) {
        this.ajankohtas = ajankohtas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Valintakoe that = (Valintakoe) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }

    /**
     * @return the hakukohdeId
     */
    public long getHakukohdeId() {
        return hakukohdeId;
    }

    /**
     * @param hakukohdeId the hakukohdeId to set
     */
    public void setHakukohdeId(long hakukohdeId) {
        this.hakukohdeId = hakukohdeId;
    }

    /**
     * @return the lisanaytot
     */
    public MonikielinenTeksti getLisanaytot() {
        return lisanaytot;
    }

    /**
     * @param lisanaytot the lisanaytot to set
     */
    public void setLisanaytot(MonikielinenTeksti lisanaytot) {
        this.lisanaytot = lisanaytot;
    }

    /**
     * @return the pisterajat
     */
    public Set<Pisteraja> getPisterajat() {
        return pisterajat;
    }

    /**
     * @param pisterajat the pisterajat to set
     */
    public void setPisterajat(Set<Pisteraja> pisterajat) {
        this.pisterajat = pisterajat;
    }
}
