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

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "V1 Hakukohde's painotettava oppiaine REST-api model")
public class PainotettavaOppiaineV1RDTO extends BaseV1RDTO {

    @ApiModelProperty(value = "Oppiainee's name uri", required = true)
    private String oppiaineUri;

    @ApiModelProperty(value = "Oppiainee's weight", required = true)
    private BigDecimal painokerroin;

    public String getOppiaineUri() {
        return oppiaineUri;
    }

    public void setOppiaineUri(String oppiaineUri) {
        this.oppiaineUri = oppiaineUri;
    }

    public BigDecimal getPainokerroin() {
        return painokerroin;
    }

    public void setPainokerroin(BigDecimal painokerroin) {
        this.painokerroin = painokerroin;
    }
}