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
package fi.vm.sade.tarjonta.service.resources.v1.dto;

import com.wordnik.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * Basic class for V! RDTO's
 *
 * @author mlyly
 */
public class BaseV1RDTO implements Serializable {

    @ApiModelProperty(value = "Luontipäivä ja aika", required = true)
    private Date created;
    @ApiModelProperty(value = "Luonnin suorittajan nimi", required = true)
    private String createdBy;
    @ApiModelProperty(value = "Viimeinen muokkauspäivä ja aika", required = true)
    private Date modified;
    @ApiModelProperty(value = "Muokkauksen suorittajan nimi", required = true)
    private String modifiedBy;

    @ApiModelProperty(value = "Objektin yksilöivä tunniste", required = true)
    private String oid;

    @ApiModelProperty(value = "Objektin versio numero", required = true)
    private Long version;

    public BaseV1RDTO() {
    }

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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}
