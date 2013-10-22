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
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.service.resources.dto.v1.ValintakoeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.HakukohdeResource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultRDTO;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author mlyly
 */
public class HakukohdeResourceImplV1 implements HakukohdeResource {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeResourceImplV1.class);

    @Autowired
    private HakuDAO hakuDao;
    @Autowired
    private HakukohdeDAO hakukohdeDao;

    @Autowired(required = true)
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;


    private V1Converter converter;

    @PostConstruct
    private void init() {
        LOG.info("init()");
        converter = new V1Converter();
        converter.setHakuDao(hakuDao);
        converter.setHakukohdeDao(hakukohdeDao);
        converter.setTarjontaKoodistoHelper(tarjontaKoodistoHelper);
    }

    @Override
    public String hello() {
        LOG.error("hello()");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<List<OidRDTO>> search() {
        LOG.error("search()");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<HakukohdeRDTO> findByOid(String oid) {
        LOG.error("findByOid({})", oid);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<HakukohdeRDTO> createHaku(HakukohdeRDTO hakukohde) {
        LOG.error("createHaku({})", hakukohde);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<HakukohdeRDTO> updateHaku(HakukohdeRDTO hakukohde) {
        LOG.error("updateHaku({})", hakukohde);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<Boolean> deleteHaku(String oid) {
        LOG.error("deleteHaku({})", oid);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<List<ValintakoeV1RDTO>> findHakukohdeValintakoes(String hakukohdeOid) {

        ResultRDTO<List<ValintakoeV1RDTO>> resultRDTO = new ResultRDTO<List<ValintakoeV1RDTO>>();

        if (hakukohdeOid == null) {
            resultRDTO.setStatus(ResultRDTO.ResultStatus.NOT_FOUND);
            ErrorRDTO errorRDTO = new ErrorRDTO();
            errorRDTO.setErrorCode(ErrorRDTO.ErrorCode.ERROR);
            errorRDTO.setErrorField("hakukohdeOid");
            errorRDTO.setErrorTechnicalInformation("Hakukohde oid cannot be null");
            resultRDTO.addError(errorRDTO);
            return resultRDTO;
        }  else {
        try {
        List<ValintakoeV1RDTO> valintakoeV1RDTOs = new ArrayList<ValintakoeV1RDTO>();
        List<Valintakoe> valintakokees = hakukohdeDao.findValintakoeByHakukohdeOid(hakukohdeOid);
        for (Valintakoe valintakoe:valintakokees) {
            ValintakoeV1RDTO valintakoeV1RDTO = converter.fromValintakoe(valintakoe);
            valintakoeV1RDTOs.add(valintakoeV1RDTO);
        }
        resultRDTO.setResult(valintakoeV1RDTOs);
        resultRDTO.setStatus(ResultRDTO.ResultStatus.OK);
        } catch (Exception exp) {
            resultRDTO.setStatus(ResultRDTO.ResultStatus.ERROR);
            ErrorRDTO errorRDTO = new ErrorRDTO();
            exp.printStackTrace();
            errorRDTO.setErrorTechnicalInformation(exp.toString());
            errorRDTO.setErrorCode(ErrorRDTO.ErrorCode.ERROR);
            resultRDTO.addError(errorRDTO);
        }
        return resultRDTO;

        }

    }

    @Override
    public ResultRDTO<ValintakoeV1RDTO> insertValintakoe(String hakukohdeOid, ValintakoeV1RDTO valintakoeV1RDTO) {
        try {

            Valintakoe valintakoe = converter.toValintakoe(valintakoeV1RDTO);
            if (hakukohdeOid != null && valintakoe != null) {
                List<Valintakoe> valintakoes = new ArrayList<Valintakoe>();
                valintakoes.add(valintakoe);
                hakukohdeDao.updateValintakoe(valintakoes,hakukohdeOid);
                ResultRDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultRDTO<ValintakoeV1RDTO>();
                ValintakoeV1RDTO result = converter.fromValintakoe(valintakoe);
                rdtoResultRDTO.setStatus(ResultRDTO.ResultStatus.OK);
                rdtoResultRDTO.setResult(result);
                return rdtoResultRDTO;
            }else {
                ResultRDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultRDTO<ValintakoeV1RDTO>();
                rdtoResultRDTO.setStatus(ResultRDTO.ResultStatus.NOT_FOUND);
                ErrorRDTO errorRDTO = new ErrorRDTO();
                errorRDTO.setErrorCode(ErrorRDTO.ErrorCode.ERROR);
                errorRDTO.setErrorTechnicalInformation("Hakukohde cannot be null when inserting valintakoe");
                rdtoResultRDTO.addError(errorRDTO);
                return rdtoResultRDTO;

            }

        } catch (Exception exp) {
           ResultRDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultRDTO<ValintakoeV1RDTO>();
           rdtoResultRDTO.setStatus(ResultRDTO.ResultStatus.ERROR);
           ErrorRDTO errorRDTO = new ErrorRDTO();
           exp.printStackTrace();
           errorRDTO.setErrorTechnicalInformation(exp.toString());
           errorRDTO.setErrorCode(ErrorRDTO.ErrorCode.ERROR);
           rdtoResultRDTO.addError(errorRDTO);

           return rdtoResultRDTO;
        }

    }

    @Override
    public ResultRDTO<ValintakoeV1RDTO> updateValintakoe(String hakukohdeOid, ValintakoeV1RDTO valintakoeV1RDTO) {
        try {

            Valintakoe valintakoe = converter.toValintakoe(valintakoeV1RDTO);
            valintakoe.setId(new Long(valintakoeV1RDTO.getOid()));

            List<Valintakoe> valintakoes = new ArrayList<Valintakoe>();
            valintakoes.add(valintakoe);

            hakukohdeDao.updateValintakoe(valintakoes,hakukohdeOid);

            ResultRDTO<ValintakoeV1RDTO> valintakoeResult = new ResultRDTO<ValintakoeV1RDTO>();
            valintakoeResult.setStatus(ResultRDTO.ResultStatus.OK);
            valintakoeResult.setResult(converter.fromValintakoe(valintakoe));
            return valintakoeResult;

        } catch (Exception exp) {
           ResultRDTO<ValintakoeV1RDTO> errorResult = new ResultRDTO<ValintakoeV1RDTO>();

            errorResult.setStatus(ResultRDTO.ResultStatus.ERROR);
            ErrorRDTO errorRDTO = new ErrorRDTO();
            errorRDTO.setErrorCode(ErrorRDTO.ErrorCode.ERROR);
            exp.printStackTrace();
            errorRDTO.setErrorTechnicalInformation(exp.toString());
            errorResult.addError(errorRDTO);

           return errorResult;
        }
    }

    @Override
    public ResultRDTO<Boolean> removeValintakoe(String hakukohdeOid, ValintakoeV1RDTO valintakoeV1RDTO) {
        try {

            Valintakoe valintakoe = new Valintakoe();
            valintakoe.setId(new Long(valintakoeV1RDTO.getOid()));
            hakukohdeDao.removeValintakoe(valintakoe);

            ResultRDTO<Boolean> resultRDTO = new ResultRDTO<Boolean>();
            resultRDTO.setResult(true);
            resultRDTO.setStatus(ResultRDTO.ResultStatus.OK);
            return resultRDTO;


        } catch (Exception exp) {
            ResultRDTO<Boolean> resultRDTO = new ResultRDTO<Boolean>();
            resultRDTO.setResult(false);
            resultRDTO.setStatus(ResultRDTO.ResultStatus.ERROR);

            ErrorRDTO errorRDTO = new ErrorRDTO();
            errorRDTO.setErrorCode(ErrorRDTO.ErrorCode.ERROR);
            exp.printStackTrace();
            errorRDTO.setErrorTechnicalInformation(exp.toString());

            resultRDTO.addError(errorRDTO);
            return resultRDTO;

        }
    }
}
