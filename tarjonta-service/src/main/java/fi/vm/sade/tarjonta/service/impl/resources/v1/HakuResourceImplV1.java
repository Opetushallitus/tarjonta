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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria.Field;
import fi.vm.sade.tarjonta.service.resources.v1.HakuV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.GenericSearchParamsV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private ConverterV1 _converter;

    @Autowired
    private OIDService oidService;


    @Override
    public ResultV1RDTO<List<OidV1RDTO>> search(GenericSearchParamsV1RDTO params, List<HakuSearchCriteria> criteriaList, UriInfo uriInfo) {
        LOG.info("search({})", params);

        MultivaluedMap<String, String> values = uriInfo.getQueryParameters(true);

        for(String key: values.keySet()) {
            LOG.info("processing parameter:" + key);


            if(!key.toUpperCase().equals(key)) continue;  //our fields are upper cased!
            
            try {
                Field field = Field.valueOf(key);
            } catch (Throwable t) {
                LOG.info("Ignoring unknown parameter:" + key);
                continue;
            }


            for(String sValue: values.get(key)) {
                Field field = Field.valueOf(key);
                Object value=null;
                boolean like=false;
                switch (field) {
                case HAKUVUOSI:
                case KOULUTUKSEN_ALKAMISVUOSI:
                    value = Integer.parseInt(sValue);
                    break;
                case TILA:
                    value = TarjontaTila.valueOf(sValue);
                    break;
                case HAKUSANA:
                    like=true; // %foo% haku
                    value = "%" + sValue + "%";
                    break;
                case HAKUKAUSI:
                case HAKUTAPA:
                case HAKUTYYPPI:
                case KOHDEJOUKKO:
                case KOULUTUKSEN_ALKAMISKAUSI:
                    value = sValue;
                    break;

                default:
                    throw new RuntimeException("unhandled parameter:" + key  + "=" + sValue);


                }
                if(like) {
                    criteriaList.addAll(new HakuSearchCriteria.Builder().like(field,  value).build());
                } else {
                    criteriaList.addAll(new HakuSearchCriteria.Builder().mustMatch(field,  value).build());
                }
                
            }
        }

        int count = (params != null) ? params.getCount() : 0;
        int startIndex = (params != null) ? params.getStartIndex() : 0;

        List<OidV1RDTO> tmp = new ArrayList<OidV1RDTO>();
        ResultV1RDTO<List<OidV1RDTO>>  result = new ResultV1RDTO<List<OidV1RDTO>>(tmp);
        result.setStatus(ResultV1RDTO.ResultStatus.OK);


        List<String> oidList = hakuDAO.findOIDByCriteria(count, startIndex, criteriaList);

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

        List<Haku> hakus = hakuDAO.findAll();

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
            createSystemErrorFromException(ex, result);
        }

        return result;
    }

    // POST /haku
    @Override
    public ResultV1RDTO<HakuV1RDTO> createHaku(HakuV1RDTO haku) {
        LOG.info("createHaku() - {}", haku);
        return updateHaku(haku);
    }

    // POST /haku/OID
    @Override
    public ResultV1RDTO<HakuV1RDTO> updateHaku(HakuV1RDTO haku) {
        LOG.info("updateHaku() - {}", haku);

        ResultV1RDTO<HakuV1RDTO> result = new ResultV1RDTO<HakuV1RDTO>();
        // result.setResult(haku);

        try {
            boolean isNew = isEmpty(haku.getOid());

            Haku hakuToUpdate = null;

            if (isNew) {
                // Generate new OID for haku to be created
                haku.setOid(oidService.newOid(NodeClassCode.TEKN_5));
                LOG.info("updateHakue() - NEW haku!");
            }


            if (!isNew) {
                LOG.info("updateHaku() - find by oid");

                // Check is haku exists
                hakuToUpdate = hakuDAO.findByOid(haku.getOid());
                if (hakuToUpdate == null) {
                    result.addError(ErrorV1RDTO.createValidationError("haku", "haku.not.exists", haku.getOid()));
                    result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
                    return result;
                }
            }

            LOG.info("updateHaku() - validate");

            // 1. Server side validate, returns false if validation fails
            if (!validateHaku(haku, result)) {
                return result;
            }

            LOG.info("updateHaku() - convert");

            hakuToUpdate = _converter.convertHakuV1DRDTOToHaku(haku, hakuToUpdate);

            if (isNew) {
                LOG.info("updateHaku() - insert");
                hakuDAO.insert(hakuToUpdate);
            } else {
                LOG.info("updateHaku() - update");
                hakuDAO.update(hakuToUpdate);
            }

            LOG.info("updateHaku() - make whopee!");

            // Convert to DTO - reload to get hakuaika id's for example
            hakuToUpdate = hakuDAO.findByOid(haku.getOid());
            result.setResult(_converter.fromHakuToHakuRDTO(hakuToUpdate, false));

            result.setStatus(ResultV1RDTO.ResultStatus.OK);
        } catch (Throwable ex) {
            createSystemErrorFromException(ex, result);
        }

        LOG.info("RETURN RESULT: " + result);

        return result;
    }

    // DELETE /haku/OID
    @Override
    public ResultV1RDTO<Boolean> deleteHaku(String oid) {
        LOG.info("deleteHaku() oid={}", oid);

        ResultV1RDTO<Boolean> result = new ResultV1RDTO<Boolean>();

        Haku hakuToRemove = hakuDAO.findByOid(oid);

        if (hakuToRemove != null) {
            hakuDAO.remove(hakuToRemove);
            result.setResult(true);
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
        }  else {
            result.setResult(false);
            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
        }

        return result;
    }

    // GET /haku/OID/hakukohde
    @Override
    public ResultV1RDTO<List<OidV1RDTO>> getHakukohdesForHaku(String oid, GenericSearchParamsV1RDTO params) {
        LOG.info("getHakukohdesForHaku(): oid={}, params={}", oid, params);

        ResultV1RDTO<List<OidV1RDTO>> result = new ResultV1RDTO<List<OidV1RDTO>>();
        result.setResult(new ArrayList<OidV1RDTO>());

        try {
            if (params == null) {
                params = new GenericSearchParamsV1RDTO();
            }
            result.setParams(params);

            for (String hakukohdeOid : hakukohdeDAO.findByHakuOid(oid, null, params.getCount(), params.getStartIndex(),
                    params.getModifiedBeforeAsDate(), params.getModifiedAfterAsDate())) {
                result.getResult().add(new OidV1RDTO(hakukohdeOid));
            }
        } catch (Throwable ex) {
            createSystemErrorFromException(ex, result);
        }

        return result;
    }

    @Override
    public ResultV1RDTO<String> getHakuState(String oid) {
        LOG.info("getHakuState({})", oid);

        ResultV1RDTO<String> result = new ResultV1RDTO<String>();

        try {
            Haku h = hakuDAO.findByOid(oid);
            result.setResult(h.getTila().name());
        } catch (Throwable ex) {
            createSystemErrorFromException(ex, result);
        }

        return result;
    }

    @Override
    public ResultV1RDTO<String> setHakuState(String oid, String state) {
        LOG.info("setHakuState({}, {})", oid, state);

        ResultV1RDTO<String> result = new ResultV1RDTO<String>();
        result.setResult(state);

        try {
            // Validate state
            TarjontaTila tila = TarjontaTila.valueOf(state);
            if (tila == null) {
                result.addError(ErrorV1RDTO.createValidationError(null, "haku.state.invalid", oid));
                result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
                return result;
            }

            // Get haku
            Haku h = hakuDAO.findByOid(oid);
            if (h == null) {
                result.addError(ErrorV1RDTO.createValidationError(null, "haku.not.exists", oid));
                result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
                return result;
            }

            hakuDAO.update(h);
        } catch (Throwable ex) {
            createSystemErrorFromException(ex, result);
        }

        return result;
    }

    /**
     * Create AND log "system.error" object.
     * Creates an ID for this exception which can be found in system logs for debugging.
     *
     * @param ex
     * @param result
     */
    private void createSystemErrorFromException(Throwable ex, ResultV1RDTO result) {
        long errorId = new Random().nextLong();
        result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
        result.addError(ErrorV1RDTO.createSystemError(ex, "system.error", "" + errorId, ex.toString()));

        LOG.error("Haku - operation failed! ERROR_ID=" + errorId, ex);
    }


    /**
     * Simple validations for Haku.
     *
     * @param haku Haku to validate
     * @param result Validation erros added here.
     * @return if false Haku has errors.
     */
    private boolean validateHaku(HakuV1RDTO haku, ResultV1RDTO<HakuV1RDTO> result) {
        LOG.info("vaidateHaku() {}", haku);

        if (haku == null) {
            result.addError(ErrorV1RDTO.createValidationError("", "haku.validation.null"));
            return false;
        }

        // TODO not valid if this is JATKUVA HAKU!
        if (haku.getHakuaikas() == null || haku.getHakuaikas().isEmpty()) {
            result.addError(ErrorV1RDTO.createValidationError("hakuaikas", "haku.validation.hakuaikas.empty"));
        }

        for (HakuaikaV1RDTO hakuaikaV1RDTO : haku.getHakuaikas()) {
            if (hakuaikaV1RDTO.getAlkuPvm() == null) {
                result.addError(ErrorV1RDTO.createValidationError("alkuPvm", "haku.validation.hakuaikas.alkuPvm.empty"));
            }
            if (hakuaikaV1RDTO.getLoppuPvm() == null) {
                result.addError(ErrorV1RDTO.createValidationError("loppuPvm", "haku.validation.hakuaikas.loppuPvm.empty"));
            }

            if (hakuaikaV1RDTO.getAlkuPvm() != null && hakuaikaV1RDTO.getLoppuPvm() != null && hakuaikaV1RDTO.getAlkuPvm().after(hakuaikaV1RDTO.getLoppuPvm())) {
                result.addError(ErrorV1RDTO.createValidationError("loppuPvm", "haku.validation.hakuaikas.invalidOrder"));
            }

            // TODO tarkista vuosi - haun alkamiskausi / vuosi?
            // TODO tarkista kausi - haun alkamiskausi / vuosi?
        }

        if (isEmpty(haku.getHakukausiUri())) {
            result.addError(ErrorV1RDTO.createValidationError("hakukausiUri", "haku.validation.hakukausiUri.invalid"));
        }
        if (isEmpty(haku.getHakutapaUri())) {
            result.addError(ErrorV1RDTO.createValidationError("hakutapaUri", "haku.validation.hakutapaUri.invalid"));
        }
        if (isEmpty(haku.getHakutyyppiUri())) {
            result.addError(ErrorV1RDTO.createValidationError("hakutyyppiUri", "haku.validation.hakutyyppiUri.invalid"));
        }
        if (isEmpty(haku.getHaunTunniste())) {
            result.addError(ErrorV1RDTO.createValidationError("haunTunniste", "haku.validation.haunTunniste.invalid"));
        }
        if (isEmpty(haku.getKohdejoukkoUri())) {
            result.addError(ErrorV1RDTO.createValidationError("kohdejoukkoUri", "haku.validation.kohdejoukkoUri.invalid"));
        }
        if (isEmpty(haku.getKoulutuksenAlkamiskausiUri())) {
            result.addError(ErrorV1RDTO.createValidationError("koulutuksenAlkamiskausiUri", "haku.validation.koulutuksenAlkamiskausiUri.invalid"));
        }

        // TODO Nimi validation - always one of fi, sv, en?
        if (haku.getNimi() == null || haku.getNimi().isEmpty()) {
            result.addError(ErrorV1RDTO.createValidationError("nimi", "haku.validation.nimi.empty"));
        }

        // Hakulomake URI  -OR- maxHakukohdes
        if (isEmpty(haku.getHakulomakeUri())) {
            // max hakuaikas cannot be empty
            if (haku.getMaxHakukohdes() <= 0) {
                result.addError(ErrorV1RDTO.createValidationError("maxHakukohdes", "haku.validation.maxHakukohdes.invalid"));
            }
        } else {
            // Cannot have this since we have hakulomakeUri
            if (haku.getMaxHakukohdes() > 0) {
                result.addError(ErrorV1RDTO.createValidationError("maxHakukohdes", "haku.validation.maxHakukohdes.invalid"));
            }

            try {
                URL url = new URL( haku.getHakulomakeUri() );
            } catch (MalformedURLException ex) {
                result.addError(ErrorV1RDTO.createValidationError("hakulomakeUri", "haku.validation.hakulomakeUri.invalid"));
            }
        }

        // TODO  haku.getHakukausiArvo() - what is this?
        // TODO haku.getHakukausiVuosi() - verrataanko hakukausi / vuosi arvoihin?
        // TODO haku.getKoulutuksenAlkamisVuosi() - verrataanko hakukausi / vuosi arvoihin?
        // TODO haku.getMaxHakukohdes()

        if (result.hasErrors()) {
            result.setStatus(ResultV1RDTO.ResultStatus.ERROR);

            for (ErrorV1RDTO err : result.getErrors()) {
                LOG.info("  ERROR: t={}, f={}, msg={}", err.getErrorTarget(), err.getErrorField(), err.getErrorMessageKey());
            }
        }

        return !result.hasErrors();
    }

    private boolean isEmpty(String s) {
        return (s == null || s.trim().isEmpty());
    }

}
