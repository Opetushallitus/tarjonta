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
import java.util.Date;
import javax.persistence.*;

/**
 *
 */
@Entity
@Table(name = "hakukohdeliite")
public class HakukohdeLiite extends BaseEntity {

    private static final long serialVersionUID = 6186622208433509334L;

    @ManyToOne
    private Hakukohde hakukohde;

    @Column(name = "liitetyyppi", nullable = false)
    private String liitetyyppi;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "kuvaus_teksti_id")
    private MonikielinenTeksti kuvaus;

    @Embedded
    private Osoite toimitusosoite;

    @Temporal(TemporalType.TIMESTAMP)
    private Date erapaiva;

    private String sahkoinenToimitusosoite;

    private String liitteenTyyppiKoodistoNimi;

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

    public String getLiitteenTyyppiKoodistoNimi() {
        return liitteenTyyppiKoodistoNimi;
    }

    public void setLiitteenTyyppiKoodistoNimi(String liitteenTyyppiKoodistoNimi) {
        this.liitteenTyyppiKoodistoNimi = liitteenTyyppiKoodistoNimi;
    }
}

