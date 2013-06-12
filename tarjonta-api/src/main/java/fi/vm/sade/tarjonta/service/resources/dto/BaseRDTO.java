/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.resources.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * "Basic" info for "all" REST DTO's.
 *
 * @author mlyly
 */
public class BaseRDTO implements Serializable {

    private Date created;
    private String createdBy;
    private Date modified;
    private String modifiedBy;
    private String oid;
    private int version;

    private int _API_VERSION = 4;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getAPI_VERSION() {
        return _API_VERSION;
    }

    public void setAPI_VERSION(int API_VERSION) {
        if (API_VERSION != _API_VERSION) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "WARNING - REMOTE API VERSION DIFFERENT FROM CLIENT API!");
        }
    }


}
