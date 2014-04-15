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

import fi.vm.sade.tarjonta.service.business.ContextDataService;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.Roles.*;
import fi.vm.sade.tarjonta.service.resources.v1.PermissionV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.UserV1RDTO;
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
}
