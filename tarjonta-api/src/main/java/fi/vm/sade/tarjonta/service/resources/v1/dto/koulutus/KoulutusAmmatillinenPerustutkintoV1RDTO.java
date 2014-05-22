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
 * @author mlyly
 */
public class KoulutusAmmatillinenPerustutkintoV1RDTO extends KoulutusV1RDTO {

    @ApiModelProperty(value = "Tutkintonimike", required = true)
    private KoodiV1RDTO tutkintonimike;

    @ApiModelProperty(value = "HTTP-linkki opetussuunnitelmaan", required = false)
    private String linkkiOpetussuunnitelmaan;

    public KoulutusAmmatillinenPerustutkintoV1RDTO() {
        super(KoulutustyyppiUri.AMMATILLINEN_PERUSTUTKINTO);
    }

    protected KoulutusAmmatillinenPerustutkintoV1RDTO(KoulutustyyppiUri koulutustyyppiUri) {
        super(koulutustyyppiUri);
    }

    /**
     * @return the linkkiOpetussuunnitelmaan
     */
    public String getLinkkiOpetussuunnitelmaan() {
        return linkkiOpetussuunnitelmaan;
    }

    /**
     * @param linkkiOpetussuunnitelmaan the linkkiOpetussuunnitelmaan to set
     */
    public void setLinkkiOpetussuunnitelmaan(String linkkiOpetussuunnitelmaan) {
        this.linkkiOpetussuunnitelmaan = linkkiOpetussuunnitelmaan;
    }

    /**
     * @return the tutkintonimike
     */
    public KoodiV1RDTO getTutkintonimike() {
        return tutkintonimike;
    }

    /**
     * @param tutkintonimike the tutkintonimike to set
     */
    public void setTutkintonimike(KoodiV1RDTO tutkintonimike) {
        this.tutkintonimike = tutkintonimike;
    }
}
