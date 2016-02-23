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
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mlyly
 */
public class KoulutusGenericV1RDTO extends KoulutusV1RDTO {

    @ApiModelProperty(value = "Pohjakoulutusvaatimus-koodi", required = true)
    private KoodiV1RDTO pohjakoulutusvaatimus;

    @ApiModelProperty(value = "HTTP-linkki opetussuunnitelmaan")
    private String linkkiOpetussuunnitelmaan;

    public KoulutusGenericV1RDTO(ToteutustyyppiEnum toteutustyyppiEnum, ModuulityyppiEnum moduulityyppiEnum) {
        super(toteutustyyppiEnum, moduulityyppiEnum);
    }

    // Default constructor for JSON deserializing
    public KoulutusGenericV1RDTO() {
        super();
    }

    /**
     * @return the pohjakoulutusvaatimus
     */
    public KoodiV1RDTO getPohjakoulutusvaatimus() {
        return pohjakoulutusvaatimus;
    }

    /**
     * @param pohjakoulutusvaatimus the pohjakoulutusvaatimus to set
     */
    public void setPohjakoulutusvaatimus(KoodiV1RDTO pohjakoulutusvaatimus) {
        this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
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

}
