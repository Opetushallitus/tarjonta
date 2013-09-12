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

import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.io.Serializable;

/**
 * Yhteinen abstrakti perusluokka (ei entiteetti) Koulutusmoduuli:lle seka Koulutusmoduulintoteutukselle.
 */
@MappedSuperclass
public abstract class BaseKoulutusmoduuli extends TarjontaBaseEntity implements Serializable {

    public static final String OID_COLUMN_NAME = "oid";

    public static final String TILA_COLUMN_NAME = "tila";

    private static final long serialVersionUID = -8023508784857174305L;

    @Column(name = OID_COLUMN_NAME, nullable = false, insertable = true, updatable = false, unique = true)
    private String oid;

    @Column(name = TILA_COLUMN_NAME)
    @Enumerated(EnumType.STRING)
    private TarjontaTila tila = TarjontaTila.LUONNOS;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="viimPaivitysPvm")
    private Date updated;

    @PreUpdate
    protected void beforeUpdate() {
        updated = new Date();
    }

    @PrePersist
    protected void beforePersist() {
        updated = new Date();
    }

    /**
     * OID of this Koulutus. On database level, this does not uniquely identify Koulutus. For that version needs to be specified.
     *
     * @return the koulutusOid
     */
    public String getOid() {
        return oid;
    }

    /**
     * Assing the OID. Once this entity is persisted - OID cannot be changed.
     *
     * @param koulutusOid the koulutusOid to set
     */
    public void setOid(String koulutusOid) {
        this.oid = koulutusOid;
    }

    /**
     * Returns timestamp when this Koulutusmoduuli was updated or null if has never been persisted.
     *
     * @return
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * Returns uri to koodisto representing current state of this Koulutus.
     *
     * @return
     */
    public TarjontaTila getTila() {
        return tila;
    }

    /**
     * Set uri to koodisto representing the current state of this Koulutus.
     *
     * @param tila
     */
    public void setTila(TarjontaTila tila) {
        // todo: since states come from koodisto, can we do any state lifecycle validation??
        this.tila = tila;
    }


}

