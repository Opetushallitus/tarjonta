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
public class KoulutusLukioV1RDTO extends Koulutus2AsteV1RDTO {

    @ApiModelProperty(value = "Lukiodiplomit", required = true)
    private KoodiUrisV1RDTO lukiodiplomit;

    public KoulutusLukioV1RDTO() {
        super(ToteutustyyppiEnum.LUKIOKOULUTUS, ModuulityyppiEnum.LUKIOKOULUTUS);
    }

    protected KoulutusLukioV1RDTO(ToteutustyyppiEnum koulutustyyppiUri) {
        super(koulutustyyppiUri, ModuulityyppiEnum.LUKIOKOULUTUS);
    }

    /**
     * @return the lukiodiplomit
     */
    public KoodiUrisV1RDTO getLukiodiplomit() {
        if (lukiodiplomit == null) {
            lukiodiplomit = new KoodiUrisV1RDTO();
        }
        return lukiodiplomit;
    }

    /**
     * @param lukiodiplomit the lukiodiplomit to set
     */
    public void setLukiodiplomit(KoodiUrisV1RDTO lukiodiplomit) {
        this.lukiodiplomit = lukiodiplomit;
    }

}
