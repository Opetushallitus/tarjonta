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
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.Roles.*;
import fi.vm.sade.tarjonta.service.resources.v1.PermissionV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.UserV1RDTO;
import fi.vm.sade.tarjonta.shared.ParameterServices;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.HashMap;
import java.util.Map;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jani
 */
@CrossOriginResourceSharing(allowAllOrigins = true)
public class PermissionResourceImplV1 implements PermissionV1Resource {

    private static final Logger LOG = LoggerFactory.getLogger(PermissionResourceImplV1.class);

    @Autowired
    private ContextDataService contextDataService;
    
    @Autowired
    private HakuDAO hakuDao;

    @Autowired
    private HakukohdeDAO hakukohdeDao;

    @Autowired
    private KoulutusmoduuliToteutusDAO komotoDao;

    @Autowired
    private PermissionChecker permissionChecker;
    
    @Autowired
    private ParameterServices parameterServices;
   
    @Secured({ROLE_READ, ROLE_UPDATE, ROLE_CRUD})
    @Override
    public ResultV1RDTO<String> authorize() {
        LOG.debug("authorize()");
        ResultV1RDTO dto = new ResultV1RDTO();
        dto.setResult(contextDataService.getCurrentUserOid());
        return dto;
    }

    @Secured({ROLE_READ, ROLE_UPDATE, ROLE_CRUD})
    @Override
    public ResultV1RDTO<UserV1RDTO> getUser() {
        LOG.debug("getUser()");
        ResultV1RDTO<UserV1RDTO> dto = new ResultV1RDTO<UserV1RDTO>();
        dto.setResult(new UserV1RDTO(contextDataService.getCurrentUserOid(), contextDataService.getCurrentUserLang()));
        return dto;
    }

    @Secured({ROLE_READ, ROLE_UPDATE, ROLE_CRUD})
    @Override
    public void recordUiStacktrace(String stacktrace) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map map = mapper.readValue(stacktrace, Map.class);

            LOG.error("recordUiStacktrace");
            for (Object key : map.keySet()) {
                LOG.error("{} - {}", key, map.get(key));
            }
        } catch (Throwable ex) {
            LOG.error("recordUiStacktrace\n{}", stacktrace);
        }
    }

    
    private static final String PERMISSION_CREATE = "create";
    private static final String PERMISSION_UPDATE = "update";
    private static final String PERMISSION_UPDATE_LIMITED = "updateLimited";
    private static final String PERMISSION_REMOVE = "remove";
    private static final String PERMISSION_COPY = "copy";
    private static final String PERMISSION_HAKU_CAN_ADD_REMOVE_HAKUKOHDE = "addRemoveHakukohde";
    
    // http://localhost:8084/tarjonta-service/rest/v1/permission/permissions/haku/1.2.246.562.29.45401879304
    // http://localhost:8084/tarjonta-service/rest/v1/permission/permissions/hakukohde/1.2.246.562.20.46022392388
    @Override
    @Secured({ROLE_READ, ROLE_UPDATE, ROLE_CRUD})
    @Transactional(readOnly = true)
    public Map getPermissions(String type, String key) {
        Map result = new HashMap();

        if ("haku".equalsIgnoreCase(type)) {
            Haku haku = processHakuPermissions(key, result);
            if (haku != null) {
                // Possible additional checks
            }
        }
        
        if ("hakukohde".equalsIgnoreCase(type)) {
            Hakukohde hakukohde = processHakukohdePermissions(key, result);
            if (hakukohde != null) {
                // Possible additional checks
            }            
        }

        if ("koulutus".equalsIgnoreCase(type)) {
            KoulutusmoduuliToteutus komoto = processKomotoPermissions(key, result);
            if (komoto != null) {
            }
        }
        
        if ("organisaatio".equalsIgnoreCase(type)) {
            Map subResults = new HashMap();            
            result.put("organisaatio", subResults);            
            // TODO implement organisaatio checks
        }

        LOG.info("getPermissions({}, {}) -> {}", new Object[] {type, key, result});
        
        return result;
    }
    
    private Haku processHakuPermissions(String key, Map parentResult) {
        LOG.debug("processHakuPermissions({}, {})", key, parentResult);

        Map result = new HashMap();
        parentResult.put("haku", result);

        //
        // Most restrictive permissions by default
        //
        result.put(PERMISSION_COPY, false);
        result.put(PERMISSION_CREATE, false);
        result.put(PERMISSION_REMOVE, false);
        result.put(PERMISSION_UPDATE, false);
        result.put(PERMISSION_UPDATE_LIMITED, false);
        result.put(PERMISSION_HAKU_CAN_ADD_REMOVE_HAKUKOHDE, false);

        Haku h = hakuDao.findByOid(key);
        if (h != null) {
            updateStateTransferInformation(result, h.getTila());

            if (permissionChecker.isOphCrud()) {
                result.put(PERMISSION_COPY, true);
                result.put(PERMISSION_CREATE, true);
                result.put(PERMISSION_REMOVE, true);
                result.put(PERMISSION_UPDATE, true);
                result.put(PERMISSION_UPDATE_LIMITED, false);
                result.put(PERMISSION_HAKU_CAN_ADD_REMOVE_HAKUKOHDE, true);
                return h;
            }
            
            // Does the haku accept new hakukohdes / removals still?
            result.put(PERMISSION_HAKU_CAN_ADD_REMOVE_HAKUKOHDE, parameterServices.parameterCanAddHakukohdeToHaku(key));
            
            //
            // Check "real" permissions
            //

            result.put(PERMISSION_CREATE, true);

            try {
                permissionChecker.checkRemoveHakuWithOrgs(h.getTarjoajaOids());
                result.put(PERMISSION_REMOVE, true);
            } catch (Exception ex) {
                result.put(PERMISSION_REMOVE, false);
            }

            try {
                permissionChecker.checkHakuUpdateWithOrgs(h.getTarjoajaOids());
                result.put(PERMISSION_UPDATE, true);
                result.put(PERMISSION_UPDATE_LIMITED, true);
                result.put(PERMISSION_COPY, true);
            } catch (Exception ex) {
                result.put(PERMISSION_UPDATE, false);
                result.put(PERMISSION_UPDATE_LIMITED, false);
                result.put(PERMISSION_COPY, false);                
            }
        }        
        return h;
    }

    private Hakukohde processHakukohdePermissions(String key, Map parentResult) {
        LOG.debug("processHakukohdePermissions({}, {})", key, parentResult);

        Map result = new HashMap();
        parentResult.put("hakukohde", result);

        //
        // Most restrictive permissions by default
        //
        result.put(PERMISSION_COPY, false);
        // result.put(PERMISSION_CREATE, false); // Cannot check this? requires komoto oids!
        result.put(PERMISSION_REMOVE, false);
        result.put(PERMISSION_UPDATE, false);
        result.put(PERMISSION_UPDATE_LIMITED, true);


        Hakukohde hk = hakukohdeDao.findHakukohdeByOid(key);
        if (hk != null) {
            updateStateTransferInformation(result, hk.getTila());
            Haku h = hk.getHaku();
            if (h != null) {
                processHakuPermissions(h.getOid(), parentResult);
            }
            
            if (permissionChecker.isOphCrud()) {
                LOG.debug("Hakukohde permissions? YES SIR!");                
                result.put(PERMISSION_COPY, true);
                result.put(PERMISSION_CREATE, true);
                result.put(PERMISSION_REMOVE, true);
                result.put(PERMISSION_UPDATE, true);
                result.put(PERMISSION_UPDATE_LIMITED, false);
                return hk;
            }
            
            boolean permissionCanUpdateHakukohde;

            try {
                permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(key);
                permissionCanUpdateHakukohde = true;
                
                result.put(PERMISSION_UPDATE, true);
                result.put(PERMISSION_COPY, true); // If can update can copy?

                LOG.debug("  (permission) can update/copy hakukohde: {}", key);
            } catch (Exception ex) {
                permissionCanUpdateHakukohde = false;
                result.put(PERMISSION_UPDATE, false);
                result.put(PERMISSION_COPY, false);

                LOG.debug("  (permission) cannot update/copy hakukohde: {}", key);
            }
            
            try {
                permissionChecker.checkRemoveHakukohde(key);
                LOG.debug("  (permission) can remove hakukohde: {}", key);
                result.put(PERMISSION_REMOVE, true);
            } catch (Exception ex) {
                LOG.debug("  (permission) cannot remove hakukohde: {}", key);
                result.put(PERMISSION_REMOVE, false);                
            }
            
            if (h != null) {
                // Check if hakukohde can be edit now that it is attached to haku
                if (permissionCanUpdateHakukohde) {
                    boolean canEdit = parameterServices.parameterCanEditHakukohde(h.getOid());
                    boolean canEditLimited = parameterServices.parameterCanEditHakukohdeLimited(h.getOid());
                    
                    result.put(PERMISSION_UPDATE, canEdit || canEditLimited);
                    result.put(PERMISSION_UPDATE_LIMITED, !canEdit && canEditLimited);
                }
            }
        }
        return hk;
    }

    private KoulutusmoduuliToteutus processKomotoPermissions(String key, Map parentResult) {
        LOG.debug("processKomotoPermissions({}, {})", key, parentResult);

        Map result = new HashMap();
        parentResult.put("koulutus", result);

        result.put(PERMISSION_COPY, false);
        result.put(PERMISSION_CREATE, false);
        result.put(PERMISSION_REMOVE, false);
        result.put(PERMISSION_UPDATE, false);
        result.put(PERMISSION_UPDATE_LIMITED, false);

        KoulutusmoduuliToteutus komoto = komotoDao.findKomotoByOid(key);
        if (komoto != null) {
            updateStateTransferInformation(result, komoto.getTila());
            
            if (permissionChecker.isOphCrud()) {
                LOG.debug("Komoto permissions? YES SIR!");                
                result.put(PERMISSION_COPY, true);
                result.put(PERMISSION_CREATE, true);
                result.put(PERMISSION_REMOVE, true);
                result.put(PERMISSION_UPDATE, true);
                result.put(PERMISSION_UPDATE_LIMITED, false);
                return komoto;
            }

            try {
                permissionChecker.checkUpdateKoulutusByKoulutusOid(key);
                result.put(PERMISSION_COPY, true);
                result.put(PERMISSION_CREATE, true);
                result.put(PERMISSION_UPDATE, true);
            } catch (Exception ex) {
                result.put(PERMISSION_COPY, false);
                result.put(PERMISSION_CREATE, false);
                result.put(PERMISSION_UPDATE, false);
            }

            try {
                permissionChecker.checkRemoveKoulutus(key);
                result.put(PERMISSION_REMOVE, true);
            } catch (Exception ex) {                
                result.put(PERMISSION_REMOVE, false);
            }

            // TODO more checks here!
            LOG.warn("HOW TO check parameters edit / edit limited permissions since can belong to multiple hakukohdes and thus to multipel hakus?");            
        }
        
        return komoto;
    }

    private void updateStateTransferInformation(Map result, TarjontaTila fromTila) {
        if (result == null) {
            return;
        }
        
        Map<String, Object> states = new HashMap<String, Object>();
        result.put("states", states);

        // Assume state "LUONNOS" - this is good for new stuff
        if (fromTila == null) {
            fromTila = TarjontaTila.LUONNOS;
        }
        
        for (TarjontaTila targetTila : TarjontaTila.values()) {
            states.put(targetTila.name(), fromTila.acceptsTransitionTo(targetTila));
        }
    }
    
}
