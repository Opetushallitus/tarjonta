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
package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.tarjonta.shared.types.KoulutustyyppiUri;

/**
 *
 * @author jani
 */
public class KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO extends KoulutusAmmatillinenPerustutkintoV1RDTO {

   

    @ApiModelProperty(value = "Koulutuksen hinta, on pakollinen jos koulutus on merkitty maksulliseksi", required = false)
    private Double hinta;

    public KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO() {
        super(KoulutustyyppiUri.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA);
    }

   

    /**
     * @return the hinta
     */
    public Double getHinta() {
        return hinta;
    }

    /**
     * @param hinta the hinta to set
     */
    public void setHinta(Double hinta) {
        this.hinta = hinta;
    }

}
