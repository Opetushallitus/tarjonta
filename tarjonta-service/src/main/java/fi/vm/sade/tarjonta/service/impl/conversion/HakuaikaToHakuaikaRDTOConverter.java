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
package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.tarjonta.model.Hakuaika;
import fi.vm.sade.tarjonta.service.resources.dto.HakuaikaRDTO;

/**
 * Conversion from domain Hakuaika to REST API DTO.
 *
 * @author mlyly
 */
public class HakuaikaToHakuaikaRDTOConverter extends BaseRDTOConverter<Hakuaika, HakuaikaRDTO> {

    @Override
    public HakuaikaRDTO convert(Hakuaika s) {

        if (s == null) {
            return null;
        }

        HakuaikaRDTO t = new HakuaikaRDTO();

        t.setAlkuPvm(s.getAlkamisPvm());
        t.setLoppuPvm(s.getPaattymisPvm());
        t.setNimi(s.getSisaisenHakuajanNimi());

        t.setOid("" + s.getId());
        t.setVersion(s.getVersion() != null ? s.getVersion().intValue() : 0);

        return t;
    }

}
