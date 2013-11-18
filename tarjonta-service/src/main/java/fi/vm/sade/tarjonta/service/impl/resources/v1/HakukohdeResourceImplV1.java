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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation.HakukohdeValidationMessages;
import fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation.HakukohdeValidator;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.HakukohdeV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeLiiteV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoeV1RDTO;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.service.search.KoulutuksetKysely;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;

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
    
    @Autowired(required=true)
    TarjontaSearchService tarjontaSearchService;

    @Autowired
    private IndexerResource solrIndexer;

    @Autowired
    private ConversionService conversionService;

    @Autowired(required = true)
    private PublicationDataService publication;


    @Autowired(required = true)
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private ConverterV1 converter;

    
    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> search(String searchTerms,
            List<String> organisationOids, List<String> hakukohdeTilas,
            String alkamisKausi, Integer alkamisVuosi) {

        organisationOids = organisationOids != null ? organisationOids
                : new ArrayList<String>();
        hakukohdeTilas = hakukohdeTilas != null ? hakukohdeTilas
                : new ArrayList<String>();

        HakukohteetKysely q = new HakukohteetKysely();
        q.setNimi(searchTerms);
        q.setKoulutuksenAlkamiskausi(alkamisKausi);
        q.setKoulutuksenAlkamisvuosi(alkamisVuosi);
        q.getTarjoajaOids().addAll(organisationOids);

        for (String s : hakukohdeTilas) {
            q.getTilat().add(
                    fi.vm.sade.tarjonta.shared.types.TarjontaTila.valueOf(s));
        }

        HakukohteetVastaus r = tarjontaSearchService.haeHakukohteet(q);

        return new ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>(converter.fromHakukohteetVastaus(r));
    }
    
    @Override
    public ResultV1RDTO<List<OidV1RDTO>> search() {
        List<Hakukohde> hakukohdeList =  hakukohdeDao.findAll();

        List<OidV1RDTO> oidList = new ArrayList<OidV1RDTO>();
        if (hakukohdeList != null && hakukohdeList.size() > 0) {

            for (Hakukohde hakukohde:hakukohdeList) {

                OidV1RDTO oidi = new OidV1RDTO();
                oidi.setOid(hakukohde.getOid());
                oidList.add(oidi);
            }
            ResultV1RDTO<List<OidV1RDTO>> result = new ResultV1RDTO<List<OidV1RDTO>>();
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            result.setResult(oidList);
            return result;
        } else {
            ResultV1RDTO<List<OidV1RDTO>> result = new ResultV1RDTO<List<OidV1RDTO>>();
            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return result;
        }


    }



    @Override
    @Transactional(readOnly = true)
    public ResultV1RDTO<HakukohdeV1RDTO> findByOid(String oid) {
        Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(oid);

        HakukohdeV1RDTO hakukohdeRDTO = conversionService.convert(hakukohde,HakukohdeV1RDTO.class);

        ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<HakukohdeV1RDTO>();
        result.setResult(hakukohdeRDTO);
        result.setStatus(ResultV1RDTO.ResultStatus.OK);

        return result;
    }



    @Override
    @Transactional
    public ResultV1RDTO<HakukohdeV1RDTO> createHakukohde(HakukohdeV1RDTO hakukohdeRDTO) {
        String hakuOid = hakukohdeRDTO.getHakuOid();
        List<HakukohdeValidationMessages> validationMessageses = HakukohdeValidator.validateHakukohde(hakukohdeRDTO);
        if (validationMessageses.size() > 0) {
            ResultV1RDTO<HakukohdeV1RDTO> errorResult = new ResultV1RDTO<HakukohdeV1RDTO>();
            errorResult.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
            for (HakukohdeValidationMessages message: validationMessageses) {
                errorResult.addError(ErrorV1RDTO.createValidationError(null,message.name(),null));
            }
            return errorResult;
        }
        hakukohdeRDTO.setOid(null);
        Hakukohde hakukohde = conversionService.convert(hakukohdeRDTO,Hakukohde.class);

        LOG.debug("INSERT HAKUKOHDE OID : ", hakukohde.getOid());

        Haku haku = hakuDao.findByOid(hakuOid);
        hakukohde.setHaku(haku);

        hakukohde = hakukohdeDao.insert(hakukohde);

        hakukohde.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(hakukohdeRDTO.getHakukohdeKoulutusOids(),hakukohde));

        //TODO, add valintakokees and liittees etc.

        hakukohdeDao.update(hakukohde);

        solrIndexer.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
        solrIndexer.indexKoulutukset(Lists.newArrayList(Iterators.transform(hakukohde.getKoulutusmoduuliToteutuses().iterator(), new Function<KoulutusmoduuliToteutus, Long>() {
            public Long apply(@Nullable KoulutusmoduuliToteutus arg0) {
                return arg0.getId();
            }
        })));
        publication.sendEvent(hakukohde.getTila(), hakukohde.getOid(), PublicationDataService.DATA_TYPE_HAKUKOHDE, PublicationDataService.ACTION_INSERT);

        hakukohdeRDTO.setOid(hakukohde.getOid());

        ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<HakukohdeV1RDTO>();
        result.setStatus(ResultV1RDTO.ResultStatus.OK);
        result.setResult(hakukohdeRDTO);
        return result;
    }

    @Override
    @Transactional
    public ResultV1RDTO<HakukohdeV1RDTO> updateHakukohde(String hakukohdeOid,HakukohdeV1RDTO hakukohdeRDTO) {
        String hakuOid = hakukohdeRDTO.getHakuOid();
        Preconditions.checkNotNull(hakuOid, "Haku OID (HakukohteenHakuOid) cannot be null.");
        Preconditions.checkNotNull(hakukohdeRDTO.getOid(),"Hakukohteen oid cannot be null");

        Hakukohde hakukohde = conversionService.convert(hakukohdeRDTO,Hakukohde.class);

        Hakukohde hakukohdeTemp = hakukohdeDao.findHakukohdeByOid(hakukohdeRDTO.getOid());

        hakukohde.setId(hakukohdeTemp.getId());
        hakukohde.setVersion(hakukohdeTemp.getVersion());

        Haku haku = hakuDao.findByOid(hakuOid);

        hakukohde.setHaku(haku);
        //TODO: add sisaiset hakuajat

        hakukohde.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(hakukohdeRDTO.getHakukohdeKoulutusOids(),hakukohde));
        //TODO: valintakoes and liites

        hakukohdeDao.update(hakukohde);
        solrIndexer.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
        solrIndexer.indexKoulutukset(Lists.newArrayList(Iterators.transform(hakukohde.getKoulutusmoduuliToteutuses().iterator(), new Function<KoulutusmoduuliToteutus, Long>() {
            public Long apply(@Nullable KoulutusmoduuliToteutus arg0) {
                return arg0.getId();
            }
        })));
        publication.sendEvent(hakukohde.getTila(), hakukohde.getOid(), PublicationDataService.DATA_TYPE_HAKUKOHDE, PublicationDataService.ACTION_INSERT);

        ResultV1RDTO<HakukohdeV1RDTO> result = new ResultV1RDTO<HakukohdeV1RDTO>();
        result.setStatus(ResultV1RDTO.ResultStatus.OK);
        result.setResult(hakukohdeRDTO);

        return result;
    }

    @Override
    @Transactional
    public ResultV1RDTO<Boolean> deleteHakukohde(String oid) {
        try {
            Hakukohde hakukohde = hakukohdeDao.findHakukohdeByOid(oid);
            if (hakukohde.getKoulutusmoduuliToteutuses() != null) {
                for (KoulutusmoduuliToteutus koulutus:hakukohde.getKoulutusmoduuliToteutuses()) {
                    koulutus.removeHakukohde(hakukohde);
                }
            }

            hakukohdeDao.remove(hakukohde);
            solrIndexer.deleteHakukohde(Lists.newArrayList(hakukohde.getOid()));
            ResultV1RDTO<Boolean> result = new ResultV1RDTO<Boolean>();
            result.setResult(true);
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
            return  result;

        } catch (Exception exp) {
            LOG.warn("Exception occured when removing hakukohde {}, exception : {}" , oid,exp.toString());
            ResultV1RDTO<Boolean> errorResult = new ResultV1RDTO<Boolean>();
            errorResult.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            errorResult.addError(ErrorV1RDTO.createSystemError(exp,null));
            return errorResult;

        }
    }

    @Override
    @Transactional
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

             resultRDTO.setResult(converter.fromHakukohdeLiite(hakukohdeLiite));
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

    private Set<KoulutusmoduuliToteutus> findKoulutusModuuliToteutus(List<String> komotoOids, Hakukohde hakukohde) {
        Set<KoulutusmoduuliToteutus> komotos = new HashSet<KoulutusmoduuliToteutus>();

        for (String komotoOid : komotoOids) {
            KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(komotoOid);
            komoto.addHakukohde(hakukohde);
            komotos.add(komoto);
        }

        return komotos;
    }
    
    @Override
    @Transactional(readOnly = false)
    public ResultV1RDTO<String> updateTila(String oid, TarjontaTila tila) {
        Hakukohde hk = hakukohdeDao.findHakukohdeByOid(oid);
        Preconditions.checkArgument(hk != null, "Hakukohdetta ei löytynyt: %s",
                oid);
        if (!hk.getTila().acceptsTransitionTo(tila)) {
            return new ResultV1RDTO<String>(hk.getTila().toString());
        }
        hk.setTila(tila);
        hakukohdeDao.update(hk);
        solrIndexer.indexHakukohteet(Collections.singletonList(hk.getId()));
        return new ResultV1RDTO<String>(tila.toString());
    }

    @Override
    public ResultV1RDTO<List<NimiJaOidRDTO>> getKoulutukset(String oid) {
        KoulutuksetKysely ks = new KoulutuksetKysely();
        ks.getHakukohdeOids().add(oid);

        KoulutuksetVastaus kv = tarjontaSearchService.haeKoulutukset(ks);
        List<NimiJaOidRDTO> ret = new ArrayList<NimiJaOidRDTO>();
        for (KoulutusPerustieto kp : kv.getKoulutukset()) {
            ret.add(new NimiJaOidRDTO(kp.getNimi(), kp.getKomotoOid()));
        }
        return new ResultV1RDTO<List<NimiJaOidRDTO>>(ret);
    }
}
