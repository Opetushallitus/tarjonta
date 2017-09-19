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

import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.auditlog.tarjonta.LogMessage;
import fi.vm.sade.auditlog.tarjonta.TarjontaOperation;
import fi.vm.sade.auditlog.tarjonta.TarjontaResource;
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
import fi.vm.sade.tarjonta.service.resources.v1.dto.AtaruLomakeHakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.AtaruLomakkeetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.GenericSearchParamsV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuSearchParamsV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeNimiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeTulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus;
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.HakukohdeSearchService;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static fi.vm.sade.tarjonta.service.AuditHelper.AUDIT;
import static fi.vm.sade.tarjonta.service.AuditHelper.builder;
import static fi.vm.sade.tarjonta.service.AuditHelper.getHakuDelta;
import static fi.vm.sade.tarjonta.service.AuditHelper.getUsernameFromSession;

/**
 * REST API V1 implementation for Haku.
 *
 * @author mlyly
 */
@Transactional(readOnly = false)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class HakuResourceImplV1 implements HakuV1Resource {
    // generic field names of different type of specific virkailijas, also used by UI
    public static final String KORKEAKOULUVIRKAILIJA = "kkUser";
    public static final String TOISEN_ASTEEN_VIRKAILIJA = "toinenAsteUser";

    private static final Logger LOG = LoggerFactory.getLogger(HakuResourceImplV1.class);

    /**
     * List of kohdejoukko id:s that match {@link HakuResourceImplV1#KORKEAKOULUVIRKAILIJA}
     */
    private final static String KK_VIRKAILIJAN_KOHDEJOUKOT = "haunkohdejoukko_12";
    /**
     * List of kohdejoukko id:s that match {@link HakuResourceImplV1#TOISEN_ASTEEN_VIRKAILIJAN_KOHDEJOUKOT}
     */
    private final static String TOISEN_ASTEEN_VIRKAILIJAN_KOHDEJOUKOT = "haunkohdejoukko_11,haunkohdejoukko_17,haunkohdejoukko_20";
    /**
     * /haku/find API specific override for maximum amount of results to return
     */
    private static final int FIND_MAX_RESULTS = 10_000;

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

    /**
     * Cache for search results, keyed by virkailijaTyyppi. This is not the optimal solution at the time of writing but it is the fastest one to implement.
     */
    private final Cache<String, ResultV1RDTO<List<HakuV1RDTO>>> hakuCache = CacheBuilder
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
    public ResultV1RDTO<List<HakuV1RDTO>> find(final HakuSearchParamsV1RDTO params, UriInfo uriInfo) {
        final List<HakuSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.addAll(getCriteriaListFromUri(uriInfo, null));
        criteriaList.addAll(getCriteriaListFromParams(params, uriInfo));

        params.setCount(FIND_MAX_RESULTS);

        if (criteriaList.isEmpty()) {
            return findAllHakus(params);
        } else {
            if (params.cache) {
                String cacheKey = resolveComplexCacheKey(criteriaList, params);
                try {
                    return hakuCache.get(cacheKey, new Callable<ResultV1RDTO<List<HakuV1RDTO>>>() {
                        @Override
                        public ResultV1RDTO<List<HakuV1RDTO>> call() throws Exception {
                            return findHakuResultByCriteriaOrAllIfNull(params, criteriaList);
                        }
                    });
                } catch (ExecutionException e) {
                    LOG.error("Failed to cache result for key '" + FIND_ALL_CACHE_KEY + "', fetching all hakus as is", e);
                    ResultV1RDTO<List<HakuV1RDTO>> resultV1RDTO = new ResultV1RDTO<>();
                    createSystemErrorFromException(e, resultV1RDTO);
                    return resultV1RDTO;
                }
            }

            return findHakuResultByCriteriaOrAllIfNull(params, criteriaList);
        }
    }


    @Override
    public ResultV1RDTO<List<HakuV1RDTO>> findAllHakus() {
        HakuSearchParamsV1RDTO params = new HakuSearchParamsV1RDTO();
        params.addHakukohdes = true;
        return findAllHakus(params);
    }

    private ResultV1RDTO<List<HakuV1RDTO>> findAllHakus(final HakuSearchParamsV1RDTO params) {
        try {
            return hakuCache.get(FIND_ALL_CACHE_KEY, new Callable<ResultV1RDTO<List<HakuV1RDTO>>>() {
                @Override
                public ResultV1RDTO<List<HakuV1RDTO>> call() {
                    return findHakuResultByCriteriaOrAllIfNull(params, null);

                }
            });
        } catch (ExecutionException e) {
            LOG.error("Failed to cache result for key '" + FIND_ALL_CACHE_KEY + "', fetching all hakus as is", e);
            ResultV1RDTO<List<HakuV1RDTO>> resultV1RDTO = new ResultV1RDTO<>();
            createSystemErrorFromException(e, resultV1RDTO);
            return resultV1RDTO;
        }
    }

    private ResultV1RDTO<List<HakuV1RDTO>> findHakuResultByCriteriaOrAllIfNull(HakuSearchParamsV1RDTO params, List<HakuSearchCriteria> criteriaList) {
        Stopwatch timer = Stopwatch.createStarted();
        List<Haku> hakus;
        if(criteriaList == null) {
            LOG.info("Loading all hakus from DB.");
            hakus = hakuDAO.findAll();
        } else {
            LOG.debug("Loading hakus from DB for criteria {}.", resolveComplexCacheKey(criteriaList, params));
            hakus = hakuDAO.findHakuByCriteria(params.getCount(), params.getStartIndex(), criteriaList);
        }
        LOG.debug("Got {} hakus.", hakus.size());

        ResultV1RDTO<List<HakuV1RDTO>> resultV1RDTO = new ResultV1RDTO<>();
        resultV1RDTO.setStatus(ResultStatus.OK);
        resultV1RDTO.setResult(hakusToHakuRDTO(hakus, params));

        timer.stop();
        if(timer.elapsed(TimeUnit.SECONDS) > 10) LOG.info("Fetched hakus in {}", timer);
        return resultV1RDTO;
    }



    private String resolveComplexCacheKey(List<HakuSearchCriteria> criteriaList, HakuSearchParamsV1RDTO params) {
        List<HakuSearchCriteria> normalized = new ArrayList<>(criteriaList);
        Collections.sort(normalized, new Comparator<HakuSearchCriteria>() {
            @Override
            public int compare(HakuSearchCriteria left, HakuSearchCriteria right) {
                return left.getField().compareTo(right.getField());
            }
        });

        StringBuilder cacheKey = new StringBuilder()
                .append("addHakukohdes=").append(params.addHakukohdes)
                .append(", startIndex=").append(params.getStartIndex())
                .append(", count=").append(params.getCount());

        for (HakuSearchCriteria c : normalized) {
            cacheKey.append(", ").append(c.getField()).append("=").append(c.getValue());
        }
        return cacheKey.toString();
    }

    private List<HakuV1RDTO> hakusToHakuRDTO(List<Haku> hakus, HakuSearchParamsV1RDTO params) {
        Map<String, List<String>> hakukohdeMap = Maps.newHashMap();
        if(hakus == null || hakus.isEmpty()) return Lists.newArrayList();

        List<HakuV1RDTO> hakuDtos = new ArrayList<>();
        if (params.addHakukohdes) {
            Stopwatch timer = Stopwatch.createStarted();
            hakukohdeMap = hakukohdeDAO.findAllHakuToHakukohde();
            timer.stop();
            LOG.info("Fetched haku to hakukohde map in {}", timer);
        }
        Stopwatch timer = Stopwatch.createStarted();
        Map<Long, List<String>> hakuToYOAHKMap = hakukohdeDAO.findAllHakuToHakukohdeWhereYlioppilastutkintoAntaaHakukelpoisuuden(hakus);

        timer.stop();
        LOG.info("Fetched findAllHakuToHakukohdeWhereYlioppilastutkintoAntaaHakukelpoisuuden for {} hakus in {}", hakuToYOAHKMap.size(), timer);

        for (Haku haku : hakus) {
            List<String> hakukohteet = hakukohdeMap.containsKey(haku.getOid()) ? hakukohdeMap.get(haku.getOid()) : Lists.<String>newArrayList();
            HakuV1RDTO hakuV1RDTO = converterV1.fromHakuToHakuRDTO(haku, params.addHakukohdes, hakukohteet, hakuToYOAHKMap);
            hakuDtos.add(hakuV1RDTO);
        }
        return hakuDtos;
    }

    @Override
    public ResultV1RDTO<HakuV1RDTO> findByOid(String oid) {
        LOG.debug("findByOid({})", oid);

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

            HakuV1RDTO hakuDtoBeforeUpdate = null;

            if (isNew) {
                permissionChecker.checkCreateHakuWithOrgs(hakuDto.getTarjoajaOids());

                hakuDto.setOid(oidService.get(TarjontaOidType.HAKU));
                LOG.debug("updateHakue() - NEW haku! - oid ==> {}", hakuDto.getOid());
            } else {
                LOG.debug("updateHaku() - OLD haku - find by oid");

                final String oid = hakuDto.getOid();

                hakuToUpdate = hakuDAO.findByOid(oid);

                hakuDtoBeforeUpdate = converterV1.fromHakuToHakuRDTO(hakuToUpdate, false);

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

            LOG.debug("updateHaku() - validate");

            if (!validateHaku(hakuDto, result)) {
                return result;
            }

            LOG.debug("updateHaku() - convert");

            hakuToUpdate = converterV1.convertHakuV1DRDTOToHaku(hakuDto, hakuToUpdate);

            HakuV1RDTO hakuDtoAfterUpdate;

            if (isNew) {
                LOG.debug("updateHaku() - insert");
                hakuDAO.insert(hakuToUpdate);

                hakuDtoAfterUpdate = converterV1.fromHakuToHakuRDTO(hakuToUpdate, false);

                AUDIT.log(builder()
                        .setOperation(TarjontaOperation.CREATE)
                        .setResource(TarjontaResource.HAKU)
                        .setDelta(getHakuDelta(hakuDtoAfterUpdate, null))
                        .setResourceOid(hakuToUpdate.getOid()).build());
            } else {
                LOG.debug("updateHaku() - update");
                hakuDAO.update(hakuToUpdate);
                indexerDao.setHakukohdeViimindeksointiPvmToNull(hakuToUpdate);

                hakuDtoAfterUpdate = converterV1.fromHakuToHakuRDTO(hakuToUpdate, false);

                AUDIT.log(builder()
                        .setOperation(TarjontaOperation.UPDATE)
                        .setResource(TarjontaResource.HAKU)
                        .setDelta(getHakuDelta(hakuDtoAfterUpdate, hakuDtoBeforeUpdate))
                        .setResourceOid(hakuToUpdate.getOid()).build());
            }

            LOG.debug("updateHaku() - make whopee!");

            hakuToUpdate = hakuDAO.findByOid(hakuDto.getOid());
            result.setResult(converterV1.fromHakuToHakuRDTO(hakuToUpdate, true));
            result.setStatus(ResultV1RDTO.ResultStatus.OK);

        } catch (Throwable ex) {
            createSystemErrorFromException(ex, result);
        }

        LOG.debug("RETURN RESULT: " + result);

        hakuCache.invalidateAll();

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
            hakuDAO.safeDelete(hakuToRemove.getOid(), userOid);
            result.setResult(true);
            result.setStatus(ResultV1RDTO.ResultStatus.OK);

            AUDIT.log(builder()
                    .setResource(TarjontaResource.HAKU)
                    .setResourceOid(hakuToRemove.getOid())
                    .setOperation(TarjontaOperation.DELETE).build());
        } else {
            result.setResult(false);
            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            result.addError(ErrorV1RDTO.createValidationError(null, "haku.delete.error.notFound"));
        }

        hakuCache.invalidateAll();

        return result;
    }

    // GET /haku/OID/hakukohde
    @Override
    public ResultV1RDTO<List<OidV1RDTO>> getHakukohdesForHaku(String oid, GenericSearchParamsV1RDTO params) {
        LOG.debug("getHakukohdesForHaku(): oid={}, params={}", oid, params);

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
        LOG.debug("getHakuState({})", oid);

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
        LOG.debug("setHakuState({}, {})", oid, tila);

        final Haku haku = hakuDAO.findByOid(oid);
        permissionChecker.checkUpdateHaku(haku.getTarjoajaOids());


        //julkaise vain haku
        if (onlyHaku) {
            if (haku.getTila().acceptsTransitionTo(tila)) {
                haku.setTila(tila);
                hakuDAO.update(haku);

                LogMessage.LogMessageBuilder builder = builder().setResource(TarjontaResource.HAKU).setResourceOid(haku.getOid());

                switch (tila) {
                    case JULKAISTU:
                        builder.setOperation(TarjontaOperation.PUBLISH);
                        break;
                    case PERUTTU:
                        builder.setOperation(TarjontaOperation.UNPUBLISH);
                        break;
                    default:
                        builder.setOperation(TarjontaOperation.CHANGE_STATE);
                        builder.add("newTila", tila.name());
                        break;
                }

                AUDIT.log(builder.build());
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

            LogMessage.LogMessageBuilder builder = builder().setResource(TarjontaResource.HAKU).setResourceOid(haku.getOid());
            builder.add("updateStatusForHakukohdeAndKomotosAlso", "true");

            switch (tila) {
                case JULKAISTU:
                    builder.setOperation(TarjontaOperation.PUBLISH);
                    break;
                case PERUTTU:
                    builder.setOperation(TarjontaOperation.UNPUBLISH);
                    break;
                default:
                    builder.setOperation(TarjontaOperation.CHANGE_STATE);
                    builder.add("newTila", tila.name());
                    break;
            }

            AUDIT.log(builder.build());

        } catch (IllegalArgumentException iae) {
            ResultV1RDTO<Tilamuutokset> r = new ResultV1RDTO<Tilamuutokset>();
            r.addError(ErrorV1RDTO.createValidationError(null, iae.getMessage()));
            return r;
        }

        hakuCache.invalidateAll();

        return new ResultV1RDTO<>(tm);
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

    private boolean isVarsinainenHaku(HakuV1RDTO haku) {
        return KoodistoURI.KOODI_VARSINAINEN_HAKU_URI.equals(haku.getHakutyyppiUri());
    }

    private boolean isErillishaku(HakuV1RDTO haku) {
        return KoodistoURI.KOODI_ERILLISHAKU_URI.equals(haku.getHakutapaUri());
    }

    private boolean isToisenAsteenHaku(HakuV1RDTO haku) {
        return (KoodistoURI.KOODI_KOHDEJOUKKO_AMMATILLINEN_LUKIO_URI.equals(haku.getKohdejoukkoUri()) ||
                KoodistoURI.KOODI_KOHDEJOUKKO_VALMISTAVA_URI.equals(haku.getKohdejoukkoUri()) ||
                KoodistoURI.KOODI_KOHDEJOUKKO_ERITYISOPETUKSENA_URI.equals(haku.getKohdejoukkoUri()));
    }

    /**
     * Simple validations for Haku.
     *
     * @param haku   Haku to validate
     * @param result Validation erros added here.
     * @return if false Haku has errors.
     */
    private boolean validateHaku(HakuV1RDTO haku, ResultV1RDTO<HakuV1RDTO> result) {
        LOG.debug("validateHaku() {}", haku);

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

        if (isEmpty(haku.getAtaruLomakeAvain()) &&
                haku.getCanSubmitMultipleApplications() != (!isVarsinainenHaku(haku) || (isToisenAsteenHaku(haku) && isErillishaku(haku)))) {
            result.addError(ErrorV1RDTO.createValidationError("canSubmitMultipleApplications", "haku.validation.canSubmitMultipleApplications.invalid"));
        }

        if (!isEmpty(haku.getAtaruLomakeAvain())) {
            try {
                UUID uuid = UUID.fromString(haku.getAtaruLomakeAvain());
            } catch (IllegalArgumentException ex) {
                result.addError(ErrorV1RDTO.createValidationError("ataruLomakeAvain", "haku.validation.ataruLomakeAvain.invalid"));
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
                LOG.warn("  ERROR: t={}, f={}, msg={}", err.getErrorTarget(), err.getErrorField(), err.getErrorMessageKey());
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

        LOG.debug("updateRightsInformation(): {}", result.getAccessRights());
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
        processV1RDTO.getParameters().put(MassCopyProcess.USER_OID, getUsernameFromSession());

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

        List<HakukohdePerustieto> tulosList = new ArrayList<>(tulokset);
        sortHakukohdeTulokset(tulosList);

        int size = tulosList.size();
        List<HakukohdeNimiV1RDTO> results = new ArrayList<HakukohdeNimiV1RDTO>();
        int index = 0;

        for (HakukohdePerustieto tulos : tulosList) {
            if (index >= startIndex + count) {
                break;
            }
            if (index >= startIndex) {
                createHakukohdeNimiV1RDTO(results, tulos);
            }
            ++index;
        }
        return new HakukohdeTulosV1RDTO(size, results);
    }

    private void createHakukohdeNimiV1RDTO(List<HakukohdeNimiV1RDTO> results, HakukohdePerustieto tulos) {
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

    private void sortHakukohdeTulokset(List<HakukohdePerustieto> tulokset) {
        Collections.sort(tulokset, new OrderByTarjoajaAndHakukohdeName());
    }

    @Override
    public ResultV1RDTO<Set<String>> getHakukohteidenOrganisaatioOids(String oid) {
        Set<String> oids = hakuDAO.findOrganisaatioOidsFromHakukohteetByHakuOid(oid);
        return new ResultV1RDTO<>(oids);
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
            criteriaList = new ArrayList<>();
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
                LOG.warn("Ignoring unknown parameter:" + key);
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

    protected static List<HakuSearchCriteria> getCriteriaListFromParams(HakuSearchParamsV1RDTO params, UriInfo uriInfo) {
        HakuSearchCriteria.Builder criteria = new HakuSearchCriteria.Builder();

        // limit results only if virkailijaTyyppi parameter is present at all
        if (uriInfo.getQueryParameters().containsKey("virkailijaTyyppi")) {
            String virkailijaTyyppi = (params.virkailijaTyyppi != null) ? params.virkailijaTyyppi : "";

            switch (virkailijaTyyppi) {
                case KORKEAKOULUVIRKAILIJA:
                    criteria.add(Field.KOHDEJOUKKO, KK_VIRKAILIJAN_KOHDEJOUKOT, Match.LIKE_OR);
                    break;
                case TOISEN_ASTEEN_VIRKAILIJA:
                    criteria.add(Field.KOHDEJOUKKO, TOISEN_ASTEEN_VIRKAILIJAN_KOHDEJOUKOT, Match.LIKE_OR);
                    break;
                default:
                    // show all by default
                    criteria.add(Field.KOHDEJOUKKO, TOISEN_ASTEEN_VIRKAILIJAN_KOHDEJOUKOT + "," + KK_VIRKAILIJAN_KOHDEJOUKOT, Match.LIKE_OR);
            }
        }


        return criteria.build();
    }

    @Override
    public ResultV1RDTO<Set<String>> findOidsToSyncTarjontaFor() {
        ResultV1RDTO result = new ResultV1RDTO<>();
        Date today = new Date();

        try {
            result.setResult(hakuDAO.findHakusToSync(today));
            result.setStatus(ResultStatus.OK);
        }
        catch (Exception ex) {
            createSystemErrorFromException(ex, result);
        }
        return result;
    }

    private static class OrderByTarjoajaAndHakukohdeName implements Comparator<HakukohdePerustieto> {
        @Override
        public int compare(HakukohdePerustieto h1, HakukohdePerustieto h2) {
            int i = nullSafeStringComparator(h1.getAnyTarjoajaNimi(), h2.getAnyTarjoajaNimi());
            return (i == 0) ? nullSafeStringComparator(h1.getAnyNimi(), h2.getAnyNimi()) : i;
        }

        private int nullSafeStringComparator(final String one, final String two) {
            if (one == null ^ two == null) {
                return (one == null) ? -1 : 1;
            }
            if (one == null && two == null) {
                return 0;
            }
            return one.compareToIgnoreCase(two);
        }
    }

    @Override
    public ResultV1RDTO<List<AtaruLomakkeetV1RDTO>> findAtaruFormUsage(List<String> organisationOids) {
        List<Haku> hakus = hakuDAO.findHakusWithAtaruFormKeys(organisationOids);
        Map<String, List<AtaruLomakeHakuV1RDTO>> grouped = new HashMap<>();
        List<AtaruLomakkeetV1RDTO> result = new ArrayList<>();

        for (Haku haku : hakus) {
            AtaruLomakeHakuV1RDTO dto = converterV1.fromHakuToAtaruLomakeHakuRDTO(haku);
            String key = haku.getAtaruLomakeAvain();
            if (!grouped.containsKey(key)) {
                grouped.put(key, new ArrayList<AtaruLomakeHakuV1RDTO>());
            }
            grouped.get(key).add(dto);
        }

        for (Map.Entry<String, List<AtaruLomakeHakuV1RDTO>> entry : grouped.entrySet()) {
            AtaruLomakkeetV1RDTO dto = new AtaruLomakkeetV1RDTO();
            dto.setAvain(entry.getKey());
            dto.setHaut(entry.getValue());
            result.add(dto);
        }

        ResultV1RDTO<List<AtaruLomakkeetV1RDTO>> resultV1RDTO = new ResultV1RDTO<>();
        resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.OK);
        resultV1RDTO.setResult(result);

        return resultV1RDTO;
    }
}
