/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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

import fi.vm.sade.tarjonta.service.auth.NotAuthorizedException;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.resources.v1.ExportResourceV1;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Export resource for tarjonta.
 * - KELA, requires OPH CRUD user.
 * 
 * @author mlyly
 */
@Transactional(readOnly = true)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class ExportResourceV1Impl implements ExportResourceV1 {
    
    private static final Logger LOG = LoggerFactory.getLogger(ExportResourceV1Impl.class);

    @Autowired
    private PermissionChecker permissionChecker;
    
    // GET /export/kela
    @Override
    public boolean exportKela() {
        LOG.info("exportKela()...");

        if (!permissionChecker.isOphCrud()) {
            throw new NotAuthorizedException("Not authorized to export KELA.");
        }
        
        if (true) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        LOG.info("exportKela()... done.");
        return false;
    }
    
}
