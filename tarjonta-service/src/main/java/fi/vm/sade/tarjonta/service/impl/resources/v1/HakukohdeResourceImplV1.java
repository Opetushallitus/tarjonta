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

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.service.resources.v1.HakukohdeV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeLiiteV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoeV1RDTO;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mlyly
 */
public class HakukohdeResourceImplV1 implements HakukohdeV1Resource {

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
    public ResultV1RDTO<List<OidV1RDTO>> search() {
        LOG.error("search()");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultV1RDTO<HakukohdeV1RDTO> findByOid(String oid) {
        LOG.error("findByOid({})", oid);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultV1RDTO<HakukohdeV1RDTO> createHaku(HakukohdeV1RDTO hakukohde) {
        LOG.error("createHaku({})", hakukohde);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultV1RDTO<HakukohdeV1RDTO> updateHaku(HakukohdeV1RDTO hakukohde) {
        LOG.error("updateHaku({})", hakukohde);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultV1RDTO<Boolean> deleteHaku(String oid) {
        LOG.error("deleteHaku({})", oid);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultV1RDTO<List<ValintakoeV1RDTO>> findHakukohdeValintakoes(String hakukohdeOid) {

        ResultV1RDTO<List<ValintakoeV1RDTO>> resultRDTO = new ResultV1RDTO<List<ValintakoeV1RDTO>>();

        if (hakukohdeOid == null) {
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
            errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
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
        resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);

        } catch (Exception exp) {
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
            exp.printStackTrace();
            errorRDTO.setErrorTechnicalInformation(exp.toString());
            errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
            resultRDTO.addError(errorRDTO);
        }
        return resultRDTO;

        }

    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<ValintakoeV1RDTO> insertValintakoe(String hakukohdeOid, ValintakoeV1RDTO valintakoeV1RDTO) {
        try {

            Valintakoe valintakoe = converter.toValintakoe(valintakoeV1RDTO);
            if (hakukohdeOid != null && valintakoe != null) {
                LOG.debug("INSERTING VALINTAKOE : {} with kieli : {}" , valintakoe.getValintakoeNimi(), valintakoe.getKieli() );
                List<Valintakoe> valintakoes = hakukohdeDao.findValintakoeByHakukohdeOid(hakukohdeOid);
                valintakoes.add(valintakoe);
                hakukohdeDao.updateValintakoe(valintakoes,hakukohdeOid);
                ResultV1RDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultV1RDTO<ValintakoeV1RDTO>();
                ValintakoeV1RDTO result = converter.fromValintakoe(valintakoe);
                rdtoResultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
                rdtoResultRDTO.setResult(result);
                return rdtoResultRDTO;
            }else {
                ResultV1RDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultV1RDTO<ValintakoeV1RDTO>();
                rdtoResultRDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
                ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
                errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
                errorRDTO.setErrorTechnicalInformation("Hakukohde cannot be null when inserting valintakoe");
                rdtoResultRDTO.addError(errorRDTO);
                return rdtoResultRDTO;

            }

        } catch (Exception exp) {
           ResultV1RDTO<ValintakoeV1RDTO> rdtoResultRDTO = new ResultV1RDTO<ValintakoeV1RDTO>();
           rdtoResultRDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);
           ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
           exp.printStackTrace();
           errorRDTO.setErrorTechnicalInformation(exp.toString());
           errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
           rdtoResultRDTO.addError(errorRDTO);

           return rdtoResultRDTO;
        }

    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<ValintakoeV1RDTO> updateValintakoe(String hakukohdeOid, ValintakoeV1RDTO valintakoeV1RDTO) {
        try {

            Valintakoe valintakoe = converter.toValintakoe(valintakoeV1RDTO);

            LOG.debug("UPDATEVALINTAKOE SIZE: {} ", valintakoe.getAjankohtas().size());

            hakukohdeDao.updateSingleValintakoe(valintakoe,hakukohdeOid);
            LOG.debug("UPDATED VALINTAKOE");
            ResultV1RDTO<ValintakoeV1RDTO> valintakoeResult = new ResultV1RDTO<ValintakoeV1RDTO>();
            valintakoeResult.setStatus(ResultV1RDTO.ResultStatus.OK);
            valintakoeResult.setResult(valintakoeV1RDTO);
            return valintakoeResult;

        } catch (Exception exp) {
           ResultV1RDTO<ValintakoeV1RDTO> errorResult = new ResultV1RDTO<ValintakoeV1RDTO>();

            errorResult.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
            errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
            exp.printStackTrace();
            errorRDTO.setErrorTechnicalInformation(exp.toString());
            errorResult.addError(errorRDTO);

           return errorResult;
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<Boolean> removeValintakoe(String hakukohdeOid, String valintakoeOid) {
        try {

            LOG.debug("REMOVEVALINTAKOE: {}", valintakoeOid);
            Valintakoe valintakoe =  hakukohdeDao.findValintaKoeById(valintakoeOid);
            hakukohdeDao.removeValintakoe(valintakoe);

            ResultV1RDTO<Boolean> resultRDTO = new ResultV1RDTO<Boolean>();
            resultRDTO.setResult(true);
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            return resultRDTO;


        } catch (Exception exp) {
            ResultV1RDTO<Boolean> resultRDTO = new ResultV1RDTO<Boolean>();
            resultRDTO.setResult(false);
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);

            ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
            errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
            exp.printStackTrace();
            errorRDTO.setErrorTechnicalInformation(exp.toString());

            resultRDTO.addError(errorRDTO);
            return resultRDTO;

        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> findHakukohdeLiites(String hakukohdeOid) {
        try {

            ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> listResultRDTO = new ResultV1RDTO<List<HakukohdeLiiteV1RDTO>>();

            List<HakukohdeLiite> liites = hakukohdeDao.findHakukohdeLiitesByHakukohdeOid(hakukohdeOid);
            List<HakukohdeLiiteV1RDTO> liiteV1RDTOs = new ArrayList<HakukohdeLiiteV1RDTO>();
            if (liites != null) {
             LOG.debug("LIITES SIZE : {} ",liites.size());
             for (HakukohdeLiite liite : liites) {
                 liiteV1RDTOs.add(converter.fromHakukohdeLiite(liite));
             }
            }

            listResultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            listResultRDTO.setResult(liiteV1RDTOs);
            return listResultRDTO;

        } catch (Exception exp) {
            ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> errorResult = new ResultV1RDTO<List<HakukohdeLiiteV1RDTO>>();
            errorResult.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            exp.printStackTrace();
            ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
            errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
            errorRDTO.setErrorTechnicalInformation(exp.toString());
            errorResult.addError(errorRDTO);
            return errorResult;
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<HakukohdeLiiteV1RDTO> insertHakukohdeLiite(String hakukohdeOid, HakukohdeLiiteV1RDTO liiteV1RDTO) {

         try {

             ResultV1RDTO<HakukohdeLiiteV1RDTO> resultRDTO = new ResultV1RDTO<HakukohdeLiiteV1RDTO>();
             HakukohdeLiite hakukohdeLiite = converter.toHakukohdeLiite(liiteV1RDTO);
             List<HakukohdeLiite> liites = hakukohdeDao.findHakukohdeLiitesByHakukohdeOid(hakukohdeOid);
             liites.add(hakukohdeLiite);
             hakukohdeDao.insertLiittees(liites, hakukohdeOid);

             resultRDTO.setResult(converter.fromHakukohdeLiite(liites.get(0)));
             resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
             return resultRDTO;

         } catch (Exception exp) {
             ResultV1RDTO<HakukohdeLiiteV1RDTO> errorResult = new ResultV1RDTO<HakukohdeLiiteV1RDTO>();
             errorResult.setStatus(ResultV1RDTO.ResultStatus.ERROR);
             ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
             errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
             exp.printStackTrace();
             errorRDTO.setErrorTechnicalInformation(exp.toString());
             errorResult.addError(errorRDTO);
             return errorResult;

         }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<HakukohdeLiiteV1RDTO> updateHakukohdeLiite(String hakukohdeOid, HakukohdeLiiteV1RDTO liiteV1RDTO) {

        try {

            ResultV1RDTO<HakukohdeLiiteV1RDTO> resultRDTO = new ResultV1RDTO<HakukohdeLiiteV1RDTO>();

            HakukohdeLiite hakukohdeLiite = converter.toHakukohdeLiite(liiteV1RDTO);



            hakukohdeDao.updateLiite(hakukohdeLiite,hakukohdeOid);

            resultRDTO.setResult(converter.fromHakukohdeLiite(hakukohdeLiite));
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);

            return resultRDTO;

        } catch (Exception exp) {


           ResultV1RDTO<HakukohdeLiiteV1RDTO> errorResultDto = new ResultV1RDTO<HakukohdeLiiteV1RDTO>();
           errorResultDto.setStatus(ResultV1RDTO.ResultStatus.OK);
           errorResultDto.addError(ErrorV1RDTO.createSystemError(exp,"system.error",hakukohdeOid));
           return errorResultDto;

        }

    }

    @Override
    @Transactional(rollbackFor = Throwable.class, readOnly = false)
    public ResultV1RDTO<Boolean> deleteHakukohdeLiite(String hakukohdeOid, String liiteId) {

        try {

            HakukohdeLiite hakukohdeLiite = hakukohdeDao.findHakuKohdeLiiteById(liiteId);



            if (hakukohdeLiite != null && hakukohdeLiite.getId() != null) {

               hakukohdeDao.removeHakukohdeLiite(hakukohdeLiite);

                ResultV1RDTO<Boolean> booleanResultRDTO = new ResultV1RDTO<Boolean>();
                booleanResultRDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
                booleanResultRDTO.setResult(true);
                return booleanResultRDTO;
            }  else {
                ResultV1RDTO<Boolean> booleanResultRDTO = new ResultV1RDTO<Boolean>();
                booleanResultRDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
                booleanResultRDTO.setResult(false);
                return booleanResultRDTO;
            }



        } catch (Exception exp) {
            ResultV1RDTO<Boolean> errorResult = new ResultV1RDTO<Boolean>();
            errorResult.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            errorResult.setResult(false);
            errorResult.addError(ErrorV1RDTO.createSystemError(exp, "system.error", hakukohdeOid));
            return errorResult;

        }

    }
}
