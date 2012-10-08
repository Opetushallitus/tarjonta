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

import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.LearningOpportunitySpecification;
import fi.vm.sade.tarjonta.model.TutkintoOhjelma;
import fi.vm.sade.tarjonta.model.TutkintoOhjelmaToteutus;
import fi.vm.sade.tarjonta.service.LearningUploadService;
import fi.vm.sade.tarjonta.service.business.KoulutusBusinessService;
import fi.vm.sade.tarjonta.service.types2.ExtendedStringType;
import fi.vm.sade.tarjonta.service.types2.LearningClassificationCodeType;
import fi.vm.sade.tarjonta.service.types2.LearningOpportunityDataType;
import fi.vm.sade.tarjonta.service.types2.LearningOpportunityInstanceType;
import fi.vm.sade.tarjonta.service.types2.LearningOpportunityProviderType;
import fi.vm.sade.tarjonta.service.types2.LearningOpportunitySpecificationType;
import fi.vm.sade.tarjonta.service.types2.LearningOpportunityTypeType;
import fi.vm.sade.tarjonta.service.types2.LearningUploadRequestType;
import fi.vm.sade.tarjonta.service.types2.LearningUploadResponseType;
import java.util.Date;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author mlyly
 */
public class LearningUploadServiceImpl implements LearningUploadService {

    private static final Logger LOG = LoggerFactory.getLogger(LearningUploadServiceImpl.class);
    @Autowired
    private KoulutusBusinessService koulutusBusinessService;

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
            importOrUpdateLOP(request, lop);
        }

        for (LearningOpportunitySpecificationType los : lod.getLearningOpportunitySpecification()) {
            Koulutusmoduuli komo = importOrUpdateLOS(request, los);
        }

        for (LearningOpportunityInstanceType loi : lod.getLearningOpportunityInstance()) {
            KoulutusmoduuliToteutus komoto = importOrUpdateLOI(request, loi);
        }

        return result;
    }

    private Koulutusmoduuli importOrUpdateLOS(LearningUploadRequestType request, LearningOpportunitySpecificationType los) {
        LOG.info("importOrUpdateLOS() los={}", los.getId());

        Koulutusmoduuli komo = null;

        if (los.getType().equals(LearningOpportunityTypeType.DEGREE_PROGRAMME)) {
            TutkintoOhjelma to = new TutkintoOhjelma();

            // TODO multilingual!
            to.setTutkintoOhjelmanNimi("-");
            for (ExtendedStringType extendedStringType : los.getName()) {
                to.setTutkintoOhjelmanNimi(extendedStringType.getValue());
            }

            // TODO koulutusNimi == Nimi! So where to store the "degree title"?
            // to.setKoulutusNimi(los.getDegreeTitle().getValue());

            // TODO where is this?
            String tilaskokeskusKoulutusKoodiUrl = "TK KOULUTUS KOODI URL";
            // los.getClassification().getClassificationCode().get(0).getScheme().HTTP_STAT_FI
            to.setKoulutusKoodi(tilaskokeskusKoulutusKoodiUrl);

            komo = to;
        } else if (los.getType().equals(LearningOpportunityTypeType.COURSE_UNIT)) {
            LOG.error("LOS == Opintojakso (course unit) - NOT IMPLEMENTED");
            return null;
        } else {
            LOG.error("Invalid LearningOpportunityTypeType: {} not handled for id: {}", los.getType(), los.getId());
            return null;
        }

        komo.setNimi("-");
        for (ExtendedStringType extendedStringType : los.getName()) {
            komo.setNimi(extendedStringType.getValue());
        }

        komo.setEqfLuokitus("EQF");
        komo.setKoulutusAla("KOULUTUSALA");
        komo.setKoulutusAste("KOULUTUSASTE");
        komo.setNimi("NIMI");
        komo.setNqfLuokitus("NQF");
        komo.setOid("OID");
        komo.setOwnerOrganisaatioOid("ORG OID");
        komo.setTila("KOODISTO TILA");
        komo.setVersion(Long.MIN_VALUE);

        koulutusBusinessService.create(komo);
        LOG.info("Saved: {}", komo);

        return komo;
    }

    private void importOrUpdateLOP(LearningUploadRequestType request, LearningOpportunityProviderType lop) {
        LOG.info("importOrUpdateLOP(): lop={}", lop.getId());
    }

    private KoulutusmoduuliToteutus importOrUpdateLOI(LearningUploadRequestType request, LearningOpportunityInstanceType loi) {
        LOG.info("importOrUpdateLOI() loi={}", loi.getId());

        // TODO actual type from los?
        KoulutusmoduuliToteutus komoto = new TutkintoOhjelmaToteutus();
        Koulutusmoduuli komo = null;

        LearningOpportunitySpecificationType los = (LearningOpportunitySpecificationType) loi.getSpecificationRef().getRef();
        LearningOpportunityProviderType lop = (LearningOpportunityProviderType) loi.getProviderRef().getRef();

        komoto.setKoulutuksenAlkamisPvm(new Date());
        komoto.setKoulutusLaji("KOULUTUSLAJI");
        komoto.setMaksullisuus("MAKSULLISUUS");
        komoto.setMaksullisuusUrl(null);
        komoto.setNimi("NIMI");
        komoto.setOpetussuunnitelmaUrl(null);
        komoto.setOppilaitosUrl(null);
        komoto.setSosiaalinenMediaUrl(null);
        komoto.setStipendimahdollisuusUrl(null);
        komoto.setSuunniteltuKesto("SUUNNITELTU KESTO");
        komoto.setTeemas(new HashSet<KoodistoUri>());
        komoto.setTila("KOODISTO TILA");

        komoto = koulutusBusinessService.create(komoto, komo);
        LOG.info("Saved: {}", komoto);

        return komoto;
    }
}
