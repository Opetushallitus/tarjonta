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
import javax.persistence.*;

import com.google.common.base.Preconditions;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonManagedReference;

/**
 *
 */
@Entity
@JsonIgnoreProperties({"id","version", "hibernateLazyInitializer", "handler", "hakukohde"})
@Table(name = "hakukohdeliite")
public class HakukohdeLiite extends TarjontaBaseEntity {

    private static final long serialVersionUID = 6186622208433509334L;

    @ManyToOne (fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="hakukohde_id", nullable=false)
    private Hakukohde hakukohde;
  
    @Column(name="hakukohde_liite_nimi", nullable=false)
    private String hakukohdeLiiteNimi;

    /**
     * Koodisto URI.
     */
    @Column(name = "liitetyyppi")
    private String liitetyyppi;

    /* *
     * Textual name for the koodisto URI, in Finnish.
     */
    //@Column(name = "liitteenTyyppiKoodistoNimi", nullable=false)
    //private String liitteenTyyppiKoodistoNimi;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval=true)
    @JoinColumn(name = "kuvaus_teksti_id")
    private MonikielinenTeksti kuvaus;

    @Embedded
    private Osoite toimitusosoite;

    @Column(name="kieli")
    private String kieli;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="erapaiva", nullable=false)
    private Date erapaiva;

    @Column(name="sahkoinenToimitusosoite")
    private String sahkoinenToimitusosoite;

    @Column(name="viimPaivitysPvm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateDate;
    
    @Column(name="viimPaivittajaOid")
    private String lastUpdatedByOid;


    @PrePersist
    public void checkConstraints() {
    	Preconditions.checkState(liitetyyppi!=null || kieli!=null, "Either liitetyyppiuri or kieli must be set");
    }
    
    public Date getErapaiva() {
        return erapaiva;
    }

    public void setErapaiva(Date erapaiva) {
        this.erapaiva = erapaiva;
    }

    public MonikielinenTeksti getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(MonikielinenTeksti kuvaus) {
        this.kuvaus = kuvaus;
    }

    public String getLiitetyyppi() {
        return liitetyyppi;
    }

    public void setLiitetyyppi(String liitetyyppi) {
        this.liitetyyppi = liitetyyppi;
    }

    public String getSahkoinenToimitusosoite() {
        return sahkoinenToimitusosoite;
    }

    public void setSahkoinenToimitusosoite(String sahkoinenToimitusosoite) {
        this.sahkoinenToimitusosoite = sahkoinenToimitusosoite;
    }

    public Osoite getToimitusosoite() {
        return toimitusosoite;
    }

    public void setToimitusosoite(Osoite toimitusosoite) {
        this.toimitusosoite = toimitusosoite;
    }

    public Hakukohde getHakukohde() {
        return hakukohde;
    }

    public void setHakukohde(Hakukohde hakukohde) {
        this.hakukohde = hakukohde;
    }

    @Deprecated
    public String getLiitteenTyyppiKoodistoNimi() {
        return hakukohdeLiiteNimi; //liitteenTyyppiKoodistoNimi;
    }

    @Deprecated
    public void setLiitteenTyyppiKoodistoNimi(String liitteenTyyppiKoodistoNimi) {
    	hakukohdeLiiteNimi = liitteenTyyppiKoodistoNimi;
    	//this.liitteenTyyppiKoodistoNimi = liitteenTyyppiKoodistoNimi;
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

    public String getHakukohdeLiiteNimi() {
        return hakukohdeLiiteNimi;
    }

    public void setHakukohdeLiiteNimi(String hakukohdeLiiteNimi) {

        this.hakukohdeLiiteNimi = hakukohdeLiiteNimi;
    }

}

