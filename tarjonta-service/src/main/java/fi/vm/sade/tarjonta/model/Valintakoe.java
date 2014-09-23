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
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonManagedReference;

/**
 *
 */
@Entity
@Table(name = "valintakoe")
@JsonIgnoreProperties({"id","version"})
public class Valintakoe extends TarjontaBaseEntity {

    private static final long serialVersionUID = 7092585555234995829L;

    //@Column(name = "hakukohde_id", insertable = false, updatable = false)
    //private long hakukohdeId;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hakukohde_id", nullable = false)
    private Hakukohde hakukohde;

    @Column(name = "valintakoe_nimi")
    private String valintakoeNimi;

    /**
     * Valintakokeen tyyppi. Koodisto uri.
     */
    @Column(name = "tyyppiuri")
    private String tyyppiUri;

    @Column(name = "kieli")
    private String kieli;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "valintakoe")
    private Set<ValintakoeAjankohta> ajankohtas = new HashSet<ValintakoeAjankohta>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER/*, optional=false*/)
    @JoinColumn(name = "kuvaus_monikielinenteksti_id"/*, nullable=false*/)
    private MonikielinenTeksti kuvaus;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "lisanaytot_monikielinenteksti_id")
    private MonikielinenTeksti lisanaytot;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "valintakoe")
    private Set<Pisteraja> pisterajat = new HashSet<Pisteraja>();

    @Column(name = "viimPaivitysPvm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateDate;

    @Column(name = "viimPaivittajaOid")
    private String lastUpdatedByOid;

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

    public Hakukohde getHakukohde() {
        return hakukohde;
    }

    public void setHakukohde(Hakukohde hakukohde) {
        this.hakukohde = hakukohde;
    }

    /* *
     * @return the hakukohdeId
     */
    /*public long getHakukohdeId() {
     return hakukohdeId;
     }*/

    /* *
     * @param hakukohdeId the hakukohdeId to set
     */
    /*public void setHakukohdeId(long hakukohdeId) {
     this.hakukohdeId = hakukohdeId;
     }*/
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
        this.pisterajat.clear();
        this.pisterajat = pisterajat;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getLastUpdatedByOid() {
        return lastUpdatedByOid;
    }

    public void setLastUpdatedByOid(String lastUpdatedByOid) {
        this.lastUpdatedByOid = lastUpdatedByOid;
    }

    public String getKieli() {
        return kieli;
    }

    public void setKieli(String kieli) {
        this.kieli = kieli;
    }

    public String getValintakoeNimi() {
        return valintakoeNimi;
    }

    public void setValintakoeNimi(String valintakoeNimi) {
        this.valintakoeNimi = valintakoeNimi;
    }

    public boolean hasPisterajas() {
        return !pisterajat.isEmpty();
    }
}
