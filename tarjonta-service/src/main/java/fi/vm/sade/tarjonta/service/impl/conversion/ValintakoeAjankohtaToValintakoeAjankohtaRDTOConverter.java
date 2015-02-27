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

import fi.vm.sade.tarjonta.model.ValintakoeAjankohta;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST API conversion from ValintakoeAjankohta to API dto.
 *
 * @author mlyly
 */
public class ValintakoeAjankohtaToValintakoeAjankohtaRDTOConverter extends BaseRDTOConverter<ValintakoeAjankohta, ValintakoeAjankohtaRDTO> {

    private static final Logger LOG = LoggerFactory.getLogger(ValintakoeAjankohtaToValintakoeAjankohtaRDTOConverter.class);

    @Override
    public ValintakoeAjankohtaRDTO convert(ValintakoeAjankohta s) {
        LOG.debug("convert({})", s);

        if (s == null) {
            return null;
        }

        ValintakoeAjankohtaRDTO t = new ValintakoeAjankohtaRDTO();

        t.setOsoite(getConversionService().convert(s.getAjankohdanOsoite(), OsoiteRDTO.class));
        t.setAlkaa(s.getAlkamisaika());
        t.setLisatiedot(s.getLisatietoja());
        t.setLoppuu(s.getPaattymisaika());
        t.setKellonaikaKaytossa(s.isKellonaikaKaytossa());

        t.setOid("" + s.getId());
        t.setVersion(s.getVersion() != null ? s.getVersion().intValue() : 0);

        return t;
    }

}
