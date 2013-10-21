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
package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.service.resources.v1.HakuResource;
import fi.vm.sade.tarjonta.service.resources.v1.HakukohdeResource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultRDTO;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ws.rs.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author mlyly
 */
public class HakuResourceImplV1 implements HakuResource {

    private static final Logger LOG = LoggerFactory.getLogger(HakuResourceImplV1.class);

    @Autowired
    private HakuDAO _hakuDao;
    @Autowired
    private HakukohdeDAO _hakuHakuDAODao;

    private V1Converter _converter;

    @PostConstruct
    private void init() {
        LOG.info("init()");
        _converter = new V1Converter();
        _converter.setHakuDao(_hakuDao);
        _converter.setHakukohdeDao(_hakuHakuDAODao);
    }

    @Override
    public String hello() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResultRDTO<List<OidRDTO>> search() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResultRDTO<HakuRDTO> findByOid(String oid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResultRDTO<HakuRDTO> createHaku(HakuRDTO haku) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResultRDTO<HakuRDTO> updateHaku(HakuRDTO haku) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResultRDTO<Boolean> deleteHaku(String oid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
