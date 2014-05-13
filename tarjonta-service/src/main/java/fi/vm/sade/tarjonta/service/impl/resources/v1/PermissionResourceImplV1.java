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
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

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
    private KoulutusmoduuliToteutusDAO komoDao;

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
    
    
    @Override
    @Secured({ROLE_READ, ROLE_UPDATE, ROLE_CRUD})
    public Map<String, Boolean> getPermissions(String type, String key) {
        Map<String, Boolean> result = new HashMap<String, Boolean>();

        result.put(PERMISSION_CREATE, Boolean.FALSE);
        result.put(PERMISSION_UPDATE, Boolean.FALSE);
        result.put(PERMISSION_UPDATE_LIMITED, Boolean.FALSE);
        result.put(PERMISSION_REMOVE, Boolean.FALSE);
        result.put(PERMISSION_COPY, Boolean.FALSE);
        
        if ("haku".equalsIgnoreCase(type)) {
            Haku haku = processHakuPermissions(key, result);
            if (haku != null) {
            }
        }
        
        if ("hakukohde".equalsIgnoreCase(type)) {
            Hakukohde hakukohde = processHakukohdePermissions(key, result);
            if (hakukohde != null) {
            }            
        }

        if ("koulutus".equalsIgnoreCase(type)) {
            KoulutusmoduuliToteutus komoto = processKomotoPermissions(key, result);
            if (komoto != null) {
            }
        }
        
        if ("organisaatio".equalsIgnoreCase(type)) {
            
        }

        LOG.info("getPermissions({}, {}) -> {}", new Object[] {type, key, result});
        
        return result;
    }
    
    private Haku processHakuPermissions(String key, Map<String, Boolean> result) {
        Haku h = hakuDao.findByOid(key);
        if (h != null) {
            updateStateTransferInformation(result, h.getTila());
            
            //
            // Check "real" permissions
            //
            
            // TODO how to check if user can create haku? We need target orgs...
            LOG.warn("  how to check the 'create' permission here? Returing false just to be sure...");
            result.put("create", Boolean.FALSE);

            try {
                permissionChecker.checkRemoveHakuWithOrgs(h.getTarjoajaOids());
                result.put(PERMISSION_REMOVE, Boolean.TRUE);
            } catch (Exception ex) {
                result.put(PERMISSION_REMOVE, Boolean.FALSE);
            }

            try {
                permissionChecker.checkHakuUpdateWithOrgs(h.getTarjoajaOids());
                LOG.info("  (permissions) can edit, check params still");
                
                // OK - by permissions we can edit, how about parameters?
                boolean paramsCanEdit = parameterServices.parameterCanEditHakukohde(key);
                boolean paramsCanEditLimited = parameterServices.parameterCanEditHakukohdeLimited(key);
                
                // Now - in UI it's easer to just check if we can modify AT ALL and limit the fields if limited mode is on...
                result.put(PERMISSION_UPDATE, paramsCanEdit || paramsCanEditLimited);
                result.put(PERMISSION_UPDATE_LIMITED, paramsCanEditLimited);
            } catch (Exception ex) {
                LOG.info("  (permissions) cannot edit");
                result.put(PERMISSION_UPDATE, Boolean.FALSE);
                result.put(PERMISSION_UPDATE_LIMITED, Boolean.FALSE);
            }
        }        
        return h;
    }

    private Hakukohde processHakukohdePermissions(String key, Map<String, Boolean> result) {
        Hakukohde hk = hakukohdeDao.findHakukohdeByOid(key);
        if (hk != null) {
            updateStateTransferInformation(result, hk.getTila());
        }
        return hk;
    }

    private KoulutusmoduuliToteutus processKomotoPermissions(String key, Map<String, Boolean> result) {
        KoulutusmoduuliToteutus komoto = komoDao.findKomotoByOid(key);
        if (komoto != null) {
            updateStateTransferInformation(result, komoto.getTila());
        }
        return komoto;
    }

    private void updateStateTransferInformation(Map<String, Boolean> result, TarjontaTila fromTila) {
        if (result == null || fromTila == null) {
            return;
        }
        
        for (TarjontaTila targetTila : TarjontaTila.values()) {
            result.put("to_" + targetTila.name(), fromTila.acceptsTransitionTo(targetTila));
        }
    }
    
}
