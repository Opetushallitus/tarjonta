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

import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeLiiteDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;

/**
 *
 * @author mlyly
 */
public class HakukohdeLiiteToHakukohdeLiiteRDTOConverter extends BaseRDTOConverter<HakukohdeLiite, HakukohdeLiiteDTO> {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeLiiteToHakukohdeLiiteRDTOConverter.class);

    @Override
    public HakukohdeLiiteDTO convert(HakukohdeLiite s) {
        HakukohdeLiiteDTO t = new HakukohdeLiiteDTO();

        t.setErapaiva(s.getErapaiva());
        // t.setHakukohdeOid(s.getHakukohde());
        t.setKuvaus(convertMonikielinenTekstiToMap(s.getKuvaus()));
        t.setModified(s.getLastUpdateDate());
        t.setModifiedBy(s.getLastUpdatedByOid());
        t.setLiitteenTyyppiUri(s.getLiitetyyppi());
        t.setLiitteenTyyppiKoodistonNimi(s.getLiitteenTyyppiKoodistoNimi());
        t.setSahkoinenToimitusosoite(s.getSahkoinenToimitusosoite());
        t.setToimitusosoite(getConversionService().convert(s.getToimitusosoite(), OsoiteRDTO.class));

        return t;
    }

    @Autowired
    private ApplicationContext _applicationContext;

    // @Autowired -- cannot do this since this bean is created in the scope of ConversionSerices initalization...
    private ConversionService _conversionService;

    private ConversionService getConversionService() {
        if (_conversionService == null) {
            LOG.info("looking up ConversionService...");
            _conversionService = _applicationContext.getBean(ConversionService.class);
        }
        return _conversionService;
    }

}
