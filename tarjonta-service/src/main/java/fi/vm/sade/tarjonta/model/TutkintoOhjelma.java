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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Concrete type of Koulutusmoduuli.
 */
@Entity
@DiscriminatorValue(LearningOpportunityObject.KoulutusTyyppit.TUTKINTO_OHJELMA)
public class TutkintoOhjelma extends Koulutusmoduuli implements Serializable {

    private static final long serialVersionUID = 5126887997800825478L;

    @NotNull
    @Column(name = "koulutus_luokitus_koodi")
    private String koulutusLuokitusKoodi;

    @NotNull
    @Column(name = "tutkinto_ohjelman_nimi")
    private String tutkintoOhjelmanNimi;

    public TutkintoOhjelma() {
        super();
    }

    /**
     *
     * @see #setKoulutusKoodi(java.lang.String)
     * @return
     */
    public String getKoulutusKoodi() {
        return koulutusLuokitusKoodi;
    }

    /**
     * Koulutus luokitus koodi assigned by Tilastokeskus.
     *
     * @see http://www.stat.fi/meta/luokitukset/koulutus/001-2010/index.html
     * @param koulutusKoodiUri
     */
    public void setKoulutusKoodi(String koulutusKoodiUri) {
        this.koulutusLuokitusKoodi = koulutusKoodiUri;
    }

    /**
     *
     * @return
     */
    public String getKoulutusNimi() {
        return getNimi();
    }

    /**
     * If the value comes from Tilastokeskus - should we group setting the code and name?
     *
     * @param koulutusNimi
     */
    public void setKoulutusNimi(String koulutusNimi) {
        setNimi(koulutusNimi);
    }

    /**
     * Finnish explanation: Pääaineen koulutusohjelman tai vastaavan nimi. This corresponds to: ects:DegreeProgrammeTitle.
     *
     * @return the tutkintoOhjelmanNimi
     */
    public String getTutkintoOhjelmanNimi() {
        return tutkintoOhjelmanNimi;
    }

    /**
     * @param tutkintoOhjelmanNimi the tutkintoOhjelmanNimi to set
     */
    public void setTutkintoOhjelmanNimi(String tutkintoOhjelmanNimi) {
        this.tutkintoOhjelmanNimi = tutkintoOhjelmanNimi;
    }

}

