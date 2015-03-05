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

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

/**
 *
 * @author alexGofore
 */
public class KoulutusAmmatilliseenPeruskoulutukseenValmentavaV1RDTO extends ValmistavaKoulutusV1RDTO {

    public KoulutusAmmatilliseenPeruskoulutukseenValmentavaV1RDTO() {
        super(ToteutustyyppiEnum.AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA, ModuulityyppiEnum.TUNTEMATON);
    }

}