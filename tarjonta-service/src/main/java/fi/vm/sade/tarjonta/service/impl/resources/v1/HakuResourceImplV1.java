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
import fi.vm.sade.tarjonta.service.resources.v1.HakuV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.GenericSearchParamsV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mlyly
 */
@Transactional(readOnly = false)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class HakuResourceImplV1 implements HakuV1Resource {

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
    public ResultV1RDTO<List<OidV1RDTO>> search(GenericSearchParamsV1RDTO params) {
        LOG.info("search({})", params);

        ResultV1RDTO<List<OidV1RDTO>>  result = new ResultV1RDTO<List<OidV1RDTO>>();
        result.setStatus(ResultV1RDTO.ResultStatus.OK);

        // TODO implement the search!

        return result;
    }

    @Override
    public ResultV1RDTO<HakuV1RDTO> findByOid(String oid) {
        LOG.info("findByOid({})", oid);

        ResultV1RDTO<HakuV1RDTO> result = new ResultV1RDTO<HakuV1RDTO>();

        try {
            result.setResult(_converter.fromHakuToHakuRDTO(oid));
            if (result.getResult() == null) {
                result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            } else {
                result.setStatus(ResultV1RDTO.ResultStatus.OK);
            }
        } catch (Exception ex) {
            result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            result.addError(ErrorV1RDTO.createSystemError(ex, "system.error", oid));
        }

        return result;
    }

    @Override
    public ResultV1RDTO<HakuV1RDTO> createHaku(HakuV1RDTO haku) {
        LOG.info("createHaku({})", haku);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResultV1RDTO<HakuV1RDTO> updateHaku(HakuV1RDTO haku) {
        LOG.info("updateHaku({})", haku);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResultV1RDTO<Boolean> deleteHaku(String oid) {
        LOG.info("deleteHaku({})", oid);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResultV1RDTO<List<OidV1RDTO>> getHakukohdesForHaku(String oid, GenericSearchParamsV1RDTO params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultV1RDTO<String> getHakuState(String oid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultV1RDTO<String> setHakuState(String oid, String state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
