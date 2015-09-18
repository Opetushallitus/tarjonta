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
import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.publication.Tila;
import fi.vm.sade.tarjonta.publication.Tila.Tyyppi;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.impl.resources.v1.process.MassCopyProcess;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.KoodistoValidator;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria.Field;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria.Match;
import fi.vm.sade.tarjonta.service.resources.v1.HakuV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.ProcessResourceV1;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus;
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.HakukohdeSearchService;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * REST API V1 implementation for Haku.
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
    private ConverterV1 converterV1;

    @Autowired
    private OidService oidService;

    @Autowired
    private PermissionChecker permissionChecker;

    @Autowired
    private KoodistoValidator koodistoValidator;

    @Autowired
    private PublicationDataService publication;

    @Autowired
    private ContextDataService contextDataService;

    @Autowired
    private IndexerDAO indexerDao;

    @Autowired
    private ProcessResourceV1 processResource;

    @Autowired
    private HakukohdeSearchService hakukohdeSearchService;

    private final String FIND_ALL_CACHE_KEY = "findAll";

    private final Cache<String, List<Haku>> hakuCache = CacheBuilder
            .newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

    @Override
    public ResultV1RDTO<List<String>> search(GenericSearchParamsV1RDTO genericSearchParamsDTO, List<HakuSearchCriteria> criteriaList, UriInfo uriInfo) {
        LOG.debug("search({})", genericSearchParamsDTO);

        if (genericSearchParamsDTO == null) {
            genericSearchParamsDTO = new GenericSearchParamsV1RDTO();
        }

        criteriaList = getCriteriaListFromUri(uriInfo, criteriaList);

        List<String> oidList = hakuDAO.findOIDByCriteria(genericSearchParamsDTO.getCount(), genericSearchParamsDTO.getStartIndex(), criteriaList);
        ResultV1RDTO<List<String>> result = new ResultV1RDTO<List<String>>(oidList);
        result.setParams(genericSearchParamsDTO);

        LOG.debug(" --> result = {}", result);

        return result;
    }

    public ResultV1RDTO<List<HakuV1RDTO>> multiGet(List<String> oids) {
        if (oids.size() == 0) {
            return new ResultV1RDTO<List<HakuV1RDTO>>(Collections.EMPTY_LIST, ResultStatus.OK);
        }
        List<Haku> hakus = hakuDAO.findByOids(oids);

        List<HakuV1RDTO> hakuDtos = new ArrayList<HakuV1RDTO>();
        ResultV1RDTO<List<HakuV1RDTO>> resultV1RDTO = new ResultV1RDTO<List<HakuV1RDTO>>(hakuDtos);
        for (Haku haku : hakus) {
            HakuV1RDTO hakuV1RDTO = converterV1.fromHakuToHakuRDTO(haku, true);
            hakuDtos.add(hakuV1RDTO);
        }

        resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);

        updateRightsInformation(resultV1RDTO, null);

        return resultV1RDTO;
    }

    @Override
    public ResultV1RDTO<List<HakuV1RDTO>> find(HakuSearchParamsV1RDTO params, UriInfo uriInfo) {
        List<HakuSearchCriteria> criteriaList = getCriteriaListFromUri(uriInfo, null);

        if (criteriaList.isEmpty()) {
            return findAllHakus(params);
        } else {
            List<Haku> hakus = hakuDAO.findHakuByCriteria(params.getCount(), params.getStartIndex(), criteriaList);

            ResultV1RDTO<List<HakuV1RDTO>> resultV1RDTO = new ResultV1RDTO<List<HakuV1RDTO>>();
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            resultV1RDTO.setResult(hakusToHakuRDTO(hakus, params));

            return resultV1RDTO;
        }
    }

    @Override
    public ResultV1RDTO<List<HakuV1RDTO>> findAllHakus() {
        HakuSearchParamsV1RDTO params = new HakuSearchParamsV1RDTO();
        params.addHakukohdes = true;
        return findAllHakus(params);
    }

    private ResultV1RDTO<List<HakuV1RDTO>> findAllHakus(HakuSearchParamsV1RDTO params) {
        List<Haku> hakus;
        try {
            hakus = hakuCache.get(FIND_ALL_CACHE_KEY, new Callable<List<Haku>>() {
                @Override
                public List<Haku> call() {
                    List<Haku> all = hakuDAO.findAll();
                    hakuCache.put(FIND_ALL_CACHE_KEY, all);
                    return all;
                }
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
            hakus = hakuDAO.findAll();
        }


        LOG.debug("FOUND  : {} hakus", hakus.size());
        ResultV1RDTO<List<HakuV1RDTO>> resultV1RDTO = new ResultV1RDTO<List<HakuV1RDTO>>();
        if (hakus.size() > 0) {
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
            resultV1RDTO.setResult(hakusToHakuRDTO(hakus, params));
        } else {
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
        }
        return resultV1RDTO;
    }

    private List<HakuV1RDTO> hakusToHakuRDTO(List<Haku> hakus, HakuSearchParamsV1RDTO params) {
        Map<String, List<String>> hakukohdeMap = null;
        List<HakuV1RDTO> hakuDtos = new ArrayList<HakuV1RDTO>();
        if (params.addHakukohdes) {
            hakukohdeMap = hakukohdeDAO.findAllHakuToHakukohde();
        }
        for (Haku haku : hakus) {
            List<String> hakukohteet = null;
            if (params.addHakukohdes && hakukohdeMap != null) {
                hakukohteet = hakukohdeMap.get(haku.getOid());
                if (hakukohteet == null) {
                    hakukohteet = Collections.emptyList();
                }
            }
            HakuV1RDTO hakuV1RDTO = converterV1.fromHakuToHakuRDTO(haku, params.addHakukohdes, hakukohteet);
            hakuDtos.add(hakuV1RDTO);
        }
        return hakuDtos;
    }

    @Override
    public ResultV1RDTO<HakuV1RDTO> findByOid(String oid) {
        LOG.info("findByOid({})", oid);

        ResultV1RDTO<HakuV1RDTO> result = new ResultV1RDTO<HakuV1RDTO>();

        try {
            HakuV1RDTO hakuDTO = converterV1.fromHakuToHakuRDTO(oid);
            if (hakuDTO != null) {
                hakuDTO.setOrganisaatioryhmat(hakuDAO.findOrganisaatioryhmaOids(hakuDTO.getOid()));
            }

            result.setResult(hakuDTO);
            updateRightsInformation(result, result.getResult());

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
        if (haku.getOid() != null) {
            ResultV1RDTO<HakuV1RDTO> result = new ResultV1RDTO<HakuV1RDTO>();
            createSystemErrorFromException(new RuntimeException("cannot create with predefeined oid"), result);
            return result;
        }

        return updateHaku(haku);
    }

    // POST /haku/OID
    @Override
    public ResultV1RDTO<HakuV1RDTO> updateHaku(HakuV1RDTO hakuDto) {
        LOG.info("updateHaku() - {}", hakuDto);

        ResultV1RDTO<HakuV1RDTO> result = new ResultV1RDTO<HakuV1RDTO>();

        updateRightsInformation(result, hakuDto);

        try {
            boolean isNew = isEmpty(hakuDto.getOid());

            Haku hakuToUpdate = null;

            if (isNew) {
                permissionChecker.checkCreateHakuWithOrgs(hakuDto.getTarjoajaOids());

                hakuDto.setOid(oidService.get(TarjontaOidType.HAKU));
                LOG.info("updateHakue() - NEW haku! - oid ==> {}", hakuDto.getOid());
            } else {
                LOG.info("updateHaku() - OLD haku - find by oid");

                final String oid = hakuDto.getOid();

                hakuToUpdate = hakuDAO.findByOid(oid);

                final TarjontaTila toTila = TarjontaTila.valueOf(hakuDto.getTila());

                final Tila tila = new Tila(Tyyppi.HAKU, toTila, oid);
                if (!publication.isValidStatusChange(tila)) {
                    result.addError(ErrorV1RDTO.createValidationError("haku", "haku.tilamuutos.not.allowed", hakuDto.getOid()));
                    result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
                    return result;
                }

                if (hakuToUpdate == null) {
                    result.addError(ErrorV1RDTO.createValidationError("haku", "haku.not.exists", hakuDto.getOid()));
                    result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
                    return result;
                }
                permissionChecker.checkCreateHakuWithOrgs(hakuToUpdate.getTarjoajaOids());
            }

            LOG.info("updateHaku() - validate");

            if (!validateHaku(hakuDto, result)) {
                return result;
            }

            LOG.info("updateHaku() - convert");

            hakuToUpdate = converterV1.convertHakuV1DRDTOToHaku(hakuDto, hakuToUpdate);

            if (isNew) {
                LOG.info("updateHaku() - insert");
                hakuDAO.insert(hakuToUpdate);
            } else {
                LOG.info("updateHaku() - update");
                hakuDAO.update(hakuToUpdate);
                indexerDao.setHakukohdeViimindeksointiPvmToNull(hakuToUpdate);
            }

            LOG.info("updateHaku() - make whopee!");

            hakuToUpdate = hakuDAO.findByOid(hakuDto.getOid());
            result.setResult(converterV1.fromHakuToHakuRDTO(hakuToUpdate, true));
            result.setStatus(ResultV1RDTO.ResultStatus.OK);

        } catch (Throwable ex) {
            createSystemErrorFromException(ex, result);
        }

        LOG.info("RETURN RESULT: " + result);

        hakuCache.invalidate(FIND_ALL_CACHE_KEY);

        return result;
    }

    @Override
    public ResultV1RDTO<Boolean> deleteHaku(final String oid) {
        LOG.info("deleteHaku() oid={}", oid);

        final ResultV1RDTO<Boolean> result = new ResultV1RDTO<Boolean>();
        updateRightsInformation(result, null);

        final Haku hakuToRemove = hakuDAO.findByOid(oid);

        permissionChecker.checkRemoveHakuWithOrgs(hakuToRemove.getTarjoajaOids());

        if (hakuToRemove != null) {
            if (hakuToRemove.getHakukohdes().size() > 0) {

                //check existing ones are "deleted"
                for (Hakukohde hk : hakuToRemove.getHakukohdes()) {
                    if (hk.getTila() != TarjontaTila.POISTETTU) {
                        // Ei voida poistaa jos poistamattomia hakukohteita!
                        result.setResult(false);
                        result.setStatus(ResultStatus.ERROR);
                        result.addError(ErrorV1RDTO.createValidationError(null, "haku.delete.error.hasExistingHakukohdes"));
                        return result; //exit with error
                    }
                }
            }

            final String userOid = contextDataService.getCurrentUserOid();
            hakuDAO.safeDelete(hakuToRemove.getOid(), "userOid");
            result.setResult(true);
            result.setStatus(ResultV1RDTO.ResultStatus.OK);
        } else {
            result.setResult(false);
            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            result.addError(ErrorV1RDTO.createValidationError(null, "haku.delete.error.notFound"));
        }

        hakuCache.invalidate(FIND_ALL_CACHE_KEY);

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
    public ResultV1RDTO<Tilamuutokset> setHakuState(String oid, TarjontaTila tila, boolean onlyHaku) {
        LOG.info("setHakuState({}, {})", oid, tila);

        final Haku haku = hakuDAO.findByOid(oid);
        permissionChecker.checkUpdateHaku(haku.getTarjoajaOids());


        //julkaise vain haku
        if (onlyHaku) {
            if (haku.getTila().acceptsTransitionTo(tila)) {
                haku.setTila(tila);
                hakuDAO.update(haku);
            } else {
                //siirtymä ei mahdollinen
                return ResultV1RDTO.create(ResultStatus.ERROR, (Tilamuutokset) null, ErrorV1RDTO.createValidationError("tila", "tila", "transition.not.valid"));
            }
            return new ResultV1RDTO<Tilamuutokset>(new Tilamuutokset());
        }

        Tila tilamuutos = new Tila(Tyyppi.HAKU, tila, oid);
        Tilamuutokset tm = null;
        try {
            tm = publication.updatePublicationStatus(Lists.newArrayList(tilamuutos));
        } catch (IllegalArgumentException iae) {
            ResultV1RDTO<Tilamuutokset> r = new ResultV1RDTO<Tilamuutokset>();
            r.addError(ErrorV1RDTO.createValidationError(null, iae.getMessage()));
            return r;
        }

        hakuCache.invalidate(FIND_ALL_CACHE_KEY);

        return new ResultV1RDTO<Tilamuutokset>(tm);
    }

    /**
     * Create AND log "system.error" object. Creates an ID for this exception
     * which can be found in system logs for debugging.
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
     * @param haku   Haku to validate
     * @param result Validation erros added here.
     * @return if false Haku has errors.
     */
    private boolean validateHaku(HakuV1RDTO haku, ResultV1RDTO<HakuV1RDTO> result) {
        LOG.info("validateHaku() {}", haku);

        if (haku == null) {
            result.addError(ErrorV1RDTO.createValidationError("", "haku.validation.null"));
            return false;
        }

        // Hakukausi uri has to be valid and existing since this is not "jatkuva haku"
        koodistoValidator.validateKoodiUri(haku.getHakukausiUri(), !isJatkuvaHaku(haku), "hakukausiUri", result, "haku.validation");

        // Also koulutusken alkamiskausi koodisto uri has to be existing if not "jatkuva haku"
        koodistoValidator.validateKoodiUri(haku.getKoulutuksenAlkamiskausiUri(), !isJatkuvaHaku(haku), "koulutuksenAlkamiskausiUri", result, "haku.validation");

        // These uris are always required
        koodistoValidator.validateKoodiUri(haku.getHakutapaUri(), true, "hakutapaUri", result, "haku.validation");
        koodistoValidator.validateKoodiUri(haku.getHakutyyppiUri(), true, "hakutyyppiUri", result, "haku.validation");
        koodistoValidator.validateKoodiUri(haku.getKohdejoukkoUri(), true, "kohdejoukkoUri", result, "haku.validation");

        if (isEmpty(haku.getNimi())) {
            result.addError(ErrorV1RDTO.createValidationError("nimi", "haku.validation.nimi.empty"));
        }

        // If we are using systems application form - make sure the "maxHakukohdes" is set
        if (haku.isJarjestelmanHakulomake()) {
            if (haku.getMaxHakukohdes() <= 0) {
                result.addError(ErrorV1RDTO.createValidationError("maxHakukohdes", "haku.validation.maxHakukohdes.invalid"));
            }
        }

        // Own hakulomake url? Verify it.
        if (!isEmpty(haku.getHakulomakeUri())) {
            try {
                URL url = new URL(haku.getHakulomakeUri());
            } catch (MalformedURLException ex) {
                result.addError(ErrorV1RDTO.createValidationError("hakulomakeUri", "haku.validation.hakulomakeUri.invalid"));
            }
        }

        // Must have at least one hakuaika
        if (haku.getHakuaikas() == null || haku.getHakuaikas().isEmpty()) {
            result.addError(ErrorV1RDTO.createValidationError("hakuaikas", "haku.validation.hakuaikas.empty"));
        }

        for (HakuaikaV1RDTO hakuaikaV1RDTO : haku.getHakuaikas()) {
            // Start time is required
            if (hakuaikaV1RDTO.getAlkuPvm() == null) {
                result.addError(ErrorV1RDTO.createValidationError("alkuPvm", "haku.validation.hakuaikas.alkuPvm.empty"));
            }

            // End time is required IF this is not continous haku
            if (!isJatkuvaHaku(haku)) {
                if (hakuaikaV1RDTO.getLoppuPvm() == null) {
                    result.addError(ErrorV1RDTO.createValidationError("loppuPvm", "haku.validation.hakuaikas.loppuPvm.empty"));
                }

                if (hakuaikaV1RDTO.getAlkuPvm() != null && hakuaikaV1RDTO.getLoppuPvm() != null && hakuaikaV1RDTO.getAlkuPvm().after(hakuaikaV1RDTO.getLoppuPvm())) {
                    result.addError(ErrorV1RDTO.createValidationError("loppuPvm", "haku.validation.hakuaikas.invalidOrder"));
                }
            }
        }

        // TODO haku.getHakukausiVuosi() - verrataanko hakukausi / vuosi arvoihin?
        // TODO haku.getKoulutuksenAlkamisVuosi() - verrataanko hakukausi / vuosi arvoihin?
        // Sijoittelu + system application form -> priority = true
        if (haku.isSijoittelu() && haku.isJarjestelmanHakulomake()) {
            haku.setUsePriority(true);
        }

        if (isLisahaku(haku)) {
            if (haku.getParentHakuOid() == null) {
                result.addError(ErrorV1RDTO.createValidationError("parentHakuOid", "haku.validation.parentHakuOid.empty"));
            }
        }

        if (result.hasErrors()) {
            result.setStatus(ResultV1RDTO.ResultStatus.ERROR);

            for (ErrorV1RDTO err : result.getErrors()) {
                LOG.info("  ERROR: t={}, f={}, msg={}", err.getErrorTarget(), err.getErrorField(), err.getErrorMessageKey());
            }
        }

        return !result.hasErrors();
    }

    private boolean isLisahaku(HakuV1RDTO haku) {
        return StringUtils.contains(haku.getHakutyyppiUri(), "hakutyyppi_03");
    }

    /**
     * @param s
     * @return true if string s is empty or null
     */
    private boolean isEmpty(String s) {
        return (s == null || s.trim().isEmpty());
    }

    /**
     * @param map
     * @return true if map is totally empty OR all values in the map are empty
     */
    private boolean isEmpty(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return true;
        }

        boolean result = true;

        for (String nimiValue : map.values()) {
            result = result && isEmpty(nimiValue);
        }

        return result;
    }

    /**
     * Note, if Haku is "jatkuva" then it should not have any
     */
    @Value("${koodisto.hakutapa.jatkuvaHaku.uri}")
    private String _jatkuvaHakutapaUri;

    private boolean isJatkuvaHaku(HakuV1RDTO haku) {

        if (haku == null || isEmpty(haku.getHakutapaUri())) {
            return false;
        }

        boolean result = (haku.getHakutapaUri().equals(_jatkuvaHakutapaUri));

//        LOG.info("isJatkuvaHaku(), uri = '{}'", haku.getHakutapaUri());
//        LOG.info("        property uri = '{}'", _jatkuvaHakutapaUri);
//        LOG.info("        => result = {}", result);
        return result;
    }

    /**
     * Debug functionality - check access rights to given haku / org.
     *
     * @param result
     * @param hakuDto
     */
    private void updateRightsInformation(ResultV1RDTO result, HakuV1RDTO hakuDto) {
        try {
            permissionChecker.checkUpdateHaku(hakuDto.getOid());
            result.getAccessRights().put("update", true);
        } catch (Throwable ex) {
            result.getAccessRights().put("update", false);
        }

        try {
            permissionChecker.checkCreateHakuWithOrgs(hakuDto.getTarjoajaOids());
            result.getAccessRights().put("create", true);
        } catch (Throwable ex) {
            result.getAccessRights().put("create", false);
        }

        try {
            permissionChecker.checkRemoveHaku(hakuDto.getOid());
            result.getAccessRights().put("delete", true);
        } catch (Throwable ex) {
            result.getAccessRights().put("delete", false);
        }

        LOG.info("updateRightsInformation(): {}", result.getAccessRights());
    }

    @Override
    public ResultV1RDTO<Boolean> isStateChangePossible(String oid,
                                                       TarjontaTila tila) {
        Tila tilamuutos = new Tila(Tyyppi.HAKU, tila, oid);
        return new ResultV1RDTO<Boolean>(publication.isValidStatusChange(tilamuutos));
    }

    /*
     * Massakopioinnin metodi, tallentaa kopioitavan datan json-formaatissa valitauluun.
     */
    @Override
    public ResultV1RDTO<String> copyHaku(final String fromHakuOid, final String step) {
        LOG.info("copyHaku");

        Haku h = hakuDAO.findByOid(fromHakuOid);

        permissionChecker.checkUpdateHaku(h.getTarjoajaOids());
        ProcessV1RDTO processV1RDTO = MassCopyProcess.getDefinition(fromHakuOid, step);
        processV1RDTO.getParameters().put(MassCopyProcess.PROCESS_SKIP_STEP, step);

        ProcessV1RDTO result = processResource.start(processV1RDTO);
        return new ResultV1RDTO<String>(result.getId());
    }

    @Override
    public HakukohdeTulosV1RDTO getHakukohdeTulos(String oid,
                                                  String searchTerms,
                                                  int count,
                                                  int startIndex,
                                                  Date lastModifiedBefore,
                                                  Date lastModifiedSince,
                                                  String organisationOidsStr,
                                                  String hakukohdeTilasStr,
                                                  Integer alkamisVuosi,
                                                  String alkamisKausi) {
        final String kieliAvain = "fi";

        final String kieliAvain_fi = "fi";
        final String kieliAvain_sv = "sv";
        final String kieliAvain_en = "en";

        final String filtterointiTeksti = StringUtils.upperCase(StringUtils.trimToEmpty(searchTerms));

        List<String> organisationOids = splitToList(organisationOidsStr, ",");
        List<String> hakukohdeTilas = splitToList(hakukohdeTilasStr, ",");

        LOG.debug("  oids = {}", organisationOids);
        LOG.debug("  tilas = {}", hakukohdeTilas);

        count = getCountDefaultValue(count);

        HakukohteetKysely hakukohteetKysely = new HakukohteetKysely();
        hakukohteetKysely.setHakuOid(oid);
        hakukohteetKysely.getTarjoajaOids().addAll(organisationOids);
        hakukohteetKysely.setKoulutuksenAlkamiskausi(alkamisKausi);
        hakukohteetKysely.setKoulutuksenAlkamisvuosi(alkamisVuosi);

        if (hakukohdeTilas.size() > 0) {
            for (String tilaString : hakukohdeTilas) {
                TarjontaTila tila = TarjontaTila.valueOf(tilaString);
                if (tila != null) {
                    hakukohteetKysely.addTila(tila);
                } else {
                    LOG.error("  INVALID TarjontaTila in 'hakukohdeTila' : {}", hakukohdeTilas);
                }
            }
        }

        HakukohteetVastaus v = hakukohdeSearchService.haeHakukohteet(hakukohteetKysely);

        Collection<HakukohdePerustieto> tulokset = v.getHakukohteet();
        // filtteroi tarvittaessa tulokset joko tarjoaja- tai hakukohdenimen
        // mukaan!
        if (!filtterointiTeksti.isEmpty()) {
            tulokset = Collections2.filter(tulokset, new Predicate<HakukohdePerustieto>() {

                private String haeTekstiAvaimella(Map<String, String> tekstit) {

                    if (tekstit.containsKey(kieliAvain)) {
                        return StringUtils.upperCase(tekstit.get(kieliAvain));
                    }

                    if (tekstit.containsKey(kieliAvain_fi)) {
                        return StringUtils.upperCase(tekstit.get(kieliAvain_fi));
                    }

                    if (tekstit.containsKey(kieliAvain_sv)) {
                        return StringUtils.upperCase(tekstit.get(kieliAvain_sv));
                    }

                    if (tekstit.containsKey(kieliAvain_en)) {
                        return StringUtils.upperCase(tekstit.get(kieliAvain_en));
                    }

                    return StringUtils.EMPTY;
                }

                public boolean apply(@Nullable HakukohdePerustieto hakukohde) {
                    return haeTekstiAvaimella(hakukohde.getTarjoajaNimi())
                            .contains(filtterointiTeksti)
                            || haeTekstiAvaimella(hakukohde.getNimi()).contains(filtterointiTeksti);
                }
            });
        }

        Ordering<HakukohdePerustieto> ordering = Ordering.natural().nullsFirst().onResultOf(new Function<HakukohdePerustieto, Comparable>() {
            public Comparable apply(HakukohdePerustieto input) {
                String tarjoajaNimi = input.getTarjoajaNimi().get(kieliAvain);
                if (tarjoajaNimi == null) {
                    tarjoajaNimi = input.getTarjoajaNimi().get(kieliAvain_fi);
                    if (tarjoajaNimi == null) {
                        tarjoajaNimi = input.getTarjoajaNimi().get(kieliAvain_sv);
                        if (tarjoajaNimi == null) {
                            tarjoajaNimi = input.getTarjoajaNimi().get(kieliAvain_en);
                        }
                    }
                }
                return tarjoajaNimi;
            }
        });

        List<HakukohdePerustieto> sortattuLista = ordering.immutableSortedCopy(tulokset);

        int size = sortattuLista.size();
        List<HakukohdeNimiV1RDTO> results = new ArrayList<HakukohdeNimiV1RDTO>();
        int index = 0;

        for (HakukohdePerustieto tulos : sortattuLista) {
            if (index >= startIndex + count) {
                break;
            }
            if (index >= startIndex) {
                HakukohdePerustieto hakukohde = tulos;
                HakukohdeNimiV1RDTO rdto = new HakukohdeNimiV1RDTO();
                rdto.setTarjoajaOid(hakukohde.getTarjoajaOid());
                rdto.setHakukohdeNimi(hakukohde.getNimi());
                rdto.setTarjoajaNimi(hakukohde.getTarjoajaNimi());
                rdto.setHakukohdeOid(hakukohde.getOid());
                rdto.setHakukohdeTila(hakukohde.getTila() != null ? hakukohde.getTila().name() : null);
                rdto.setOpetuskielet(tulos.getOpetuskielet());
                results.add(rdto);
            }
            ++index;
        }
        return new HakukohdeTulosV1RDTO(size, results);
    }

    @Override
    public ResultV1RDTO<Set<String>> getHakukohteidenOrganisaatioOids(String oid) {
        Set<String> oids = hakuDAO.findOrganisaatioOidsFromHakukohteetByHakuOid(oid);
        return new ResultV1RDTO<Set<String>>(oids);
    }

    private List<String> splitToList(String input, String separator) {
        if (input == null || input.trim().isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        String[] params = input.split(separator);
        return Arrays.asList(params);
    }

    private int getCountDefaultValue(int count) {
        if (count < 0) {
            count = Integer.MAX_VALUE;
        }

        if (count == 0) {
            count = 100;
        }

        return count;
    }

    private List<HakuSearchCriteria> getCriteriaListFromUri(UriInfo uriInfo, List<HakuSearchCriteria> criteriaList) {
        if (criteriaList == null) {
            criteriaList = new ArrayList<HakuSearchCriteria>();
        }

        MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(true);

        for (String key : queryParameters.keySet()) {

            if (!key.toUpperCase().equals(key)) {
                continue;  //our fields are upper cased, see HakuSearchCriteria.Field.
            }

            Field field;
            try {
                field = Field.valueOf(key);
            } catch (Throwable t) {
                LOG.info("Ignoring unknown parameter:" + key);
                continue;
            }

            for (String sValue : queryParameters.get(key)) {
                Object value = null;
                Match match = Match.MUST_MATCH;  //default match type
                switch (field) {
                    case HAKUVUOSI:
                    case KOULUTUKSEN_ALKAMISVUOSI:
                        value = Integer.parseInt(sValue);
                        break;
                    case TILA:
                        if (sValue.equals("NOT_POISTETTU")) {
                            match = Match.MUST_NOT;
                            value = TarjontaTila.POISTETTU;
                        } else {
                            value = TarjontaTila.valueOf(sValue);
                        }
                        break;
                    case HAKUSANA:
                        match = Match.LIKE; // %foo% haku
                        value = "%" + sValue + "%";
                        break;
                    case TARJOAJAOID:
                        // OR-type LIKE '%foo%' search
                        // split string by comma "value1,value1" => "(field LIKE '%value1%' OR field LIKE '%value2%)'
                        match = Match.LIKE_OR;
                        value = sValue;
                        break;
                    case HAKUKAUSI:
                    case HAKUTAPA:
                    case HAKUTYYPPI:
                    case KOHDEJOUKKO:
                    case KOULUTUKSEN_ALKAMISKAUSI:
                        value = sValue;
                        break;

                    default:
                        throw new RuntimeException("unhandled parameter:" + key + "=" + sValue);
                }
                criteriaList.addAll(new HakuSearchCriteria.Builder().add(field, value, match).build());
            }
        }

        return criteriaList;
    }
}
