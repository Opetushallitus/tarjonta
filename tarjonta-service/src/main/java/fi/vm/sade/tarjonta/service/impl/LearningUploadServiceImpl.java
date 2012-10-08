/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.service.impl;

import fi.vm.sade.tarjonta.service.LearningUploadService;
import fi.vm.sade.tarjonta.service.types2.LearningOpportunityDataType;
import fi.vm.sade.tarjonta.service.types2.LearningOpportunityInstanceType;
import fi.vm.sade.tarjonta.service.types2.LearningOpportunityProviderType;
import fi.vm.sade.tarjonta.service.types2.LearningOpportunitySpecificationType;
import fi.vm.sade.tarjonta.service.types2.LearningUploadRequestType;
import fi.vm.sade.tarjonta.service.types2.LearningUploadResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mlyly
 */
public class LearningUploadServiceImpl implements LearningUploadService {

    private static final Logger LOG = LoggerFactory.getLogger(LearningUploadServiceImpl.class);

    @Override
    public LearningUploadResponseType upload(LearningUploadRequestType request) {
        LOG.info("upload({})", request);

        LearningUploadResponseType result = new LearningUploadResponseType();

        LearningOpportunityDataType lod = request.getLearningOpportunityData();
        if (lod == null) {
            LOG.error("LODT == NULL");
            return result;
        }

        for (LearningOpportunityProviderType lop : lod.getLearningOpportunityProvider()) {
            LOG.info("LOP: {}", lop.getId());
        }

        for (LearningOpportunitySpecificationType los : lod.getLearningOpportunitySpecification()) {
            LOG.info("LOS: {}", los.getId());
        }

        for (LearningOpportunityInstanceType loi : lod.getLearningOpportunityInstance()) {
            LOG.info("LOI: {}", loi.getId());
        }

        return result;
    }

}
