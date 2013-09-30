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
package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.dto.KorkeakouluDTO;

/**
 *
 * @author Jani Wilén
 */
public class KorkeakouluDTOConverterToKomoto extends AbstractToDomainConverter<KorkeakouluDTO, KoulutusmoduuliToteutus> {

    @Override
    public KoulutusmoduuliToteutus convert(KorkeakouluDTO s) {
        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();
        if (s == null) {
            return t;
        }

        t.setOid(s.getOid());
    
        return t;
    }
}
