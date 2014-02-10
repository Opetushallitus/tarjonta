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

import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.resources.v1.HakuV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.GenericSearchParamsV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;

import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.tarjonta.service.types.SearchCriteriaType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
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
    private HakuDAO hakuDAO;

    @Autowired
    private ConverterV1 _converter;

    @Autowired
    private OIDService oidService;

    @Override
    public ResultV1RDTO<List<OidV1RDTO>> search(GenericSearchParamsV1RDTO params) {
        LOG.info("search({})", params);

        int count = (params != null) ? params.getCount() : 0;
        int startIndex = (params != null) ? params.getStartIndex() : 0;

        ResultV1RDTO<List<OidV1RDTO>>  result = new ResultV1RDTO<List<OidV1RDTO>>();
        result.setStatus(ResultV1RDTO.ResultStatus.OK);

        List<OidV1RDTO> tmp = new ArrayList<OidV1RDTO>();
        result.setResult(tmp);

        List<String> oidList = hakuDAO.findOIDsBy(null, count, startIndex, null, null);

        for (String oid : oidList) {
            OidV1RDTO dto = new OidV1RDTO();
            dto.setOid(oid);
            tmp.add(dto);
        }

        LOG.info(" --> result = {}", result);

        return result;
    }

    @Override
    public ResultV1RDTO<List<HakuV1RDTO>> findAllHakus() {

        SearchCriteriaType search = new SearchCriteriaType();
        search.setMeneillaan(true);
        search.setPaattyneet(true);
        search.setTulevat(true);
        List<Haku> hakus = hakuDAO.findAll(search);

        LOG.debug("FOUND  : {} hakus",hakus.size());
        List<HakuV1RDTO> hakuDtos = new ArrayList<HakuV1RDTO>();
        ResultV1RDTO<List<HakuV1RDTO>> resultV1RDTO = new ResultV1RDTO<List<HakuV1RDTO>>();
        if (hakus != null && hakus.size() > 0) {
            for (Haku haku:hakus) {

                HakuV1RDTO hakuV1RDTO = _converter.fromHakuToHakuRDTO(haku,false);
                hakuDtos.add(hakuV1RDTO);
            }

            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            resultV1RDTO.setResult(hakuDtos);
        } else {
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
        }




        return  resultV1RDTO;

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

    // POST /haku
    @Override
    public ResultV1RDTO<HakuV1RDTO> createHaku(HakuV1RDTO haku) {
        try {
          haku.setOid(oidService.newOid(NodeClassCode.TEKN_5));

          Haku hakuToInsert = _converter.convertHakuV1DRDTOToHaku(haku);

          Haku hakuResult = hakuDAO.insert(hakuToInsert);

          ResultV1RDTO<HakuV1RDTO> hakuResultDto = new ResultV1RDTO<HakuV1RDTO>();

            hakuResultDto.setStatus(ResultV1RDTO.ResultStatus.OK);

            hakuResultDto.setResult( _converter.fromHakuToHakuRDTO(hakuResult,false));

          return hakuResultDto;

        } catch (Exception exp) {
             exp.printStackTrace();
            LOG.warn("EXCEPTION createHaku : {}",exp.toString());
            ResultV1RDTO<HakuV1RDTO> errorResult = new ResultV1RDTO<HakuV1RDTO>();
            errorResult.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            errorResult.addError(ErrorV1RDTO.createSystemError(exp,"system.error"));
            return errorResult;

        }
    }

    // PUT /haku/OID
    @Override
    public ResultV1RDTO<HakuV1RDTO> updateHaku(HakuV1RDTO haku) {
        LOG.error("updateHaku({})", haku);

        Haku h = hakuDAO.findByOid(haku.getOid());

        ResultV1RDTO<HakuV1RDTO> result = new ResultV1RDTO<HakuV1RDTO>();

        result.setResult(haku);
        result.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
        result.addError(ErrorV1RDTO.createValidationError("alkuPvm", "foo.bar"));

        return result;
    }

    // DELETE /haku/OID
    @Override
    public ResultV1RDTO<Boolean> deleteHaku(String oid) {

        Haku hakuToRemove = hakuDAO.findByOid(oid);

        if (hakuToRemove != null) {

            hakuDAO.remove(hakuToRemove);

            ResultV1RDTO<Boolean> resultV1RDTO = new ResultV1RDTO<Boolean>();
            resultV1RDTO.setResult(true);
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            return resultV1RDTO;


        }  else {
            ResultV1RDTO<Boolean> resultV1RDTO = new ResultV1RDTO<Boolean>();
            resultV1RDTO.setResult(false);
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return resultV1RDTO;
        }

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
