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
package fi.vm.sade.tarjonta.service.business.impl;

import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.tarjonta.dao.KoulutusDAO;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.LearningOpportunityObject;
import fi.vm.sade.tarjonta.service.business.KoulutusBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Service
@Transactional
public class KoulutusBusinessServiceImpl implements KoulutusBusinessService {

    @Autowired
    private KoulutusDAO koulutusDAO;

    @Autowired
    private KoulutusSisaltyvyysDAO sisaltyvyysDAO;

    @Override
    public Koulutusmoduuli create(Koulutusmoduuli koulutusmoduuli, String parentOid, boolean optional) {

        final Koulutusmoduuli newModuuli = create(koulutusmoduuli);

        Koulutusmoduuli parent = koulutusDAO.findByOid(Koulutusmoduuli.class, parentOid);
        sisaltyvyysDAO.insert(new KoulutusSisaltyvyys(parent, newModuuli, optional));

        return newModuuli;

    }

    @Override
    public Koulutusmoduuli create(Koulutusmoduuli moduuli) {

        return (Koulutusmoduuli) koulutusDAO.insert(moduuli);

    }

    @Override
    public KoulutusmoduuliToteutus create(KoulutusmoduuliToteutus toteutus, String koulutusmoduuliOid) {

        return create(toteutus, koulutusDAO.findByOid(Koulutusmoduuli.class, koulutusmoduuliOid));

    }

    @Override
    public KoulutusmoduuliToteutus create(KoulutusmoduuliToteutus toteutus, Koulutusmoduuli moduuli) {

        final Koulutusmoduuli m = isNew(moduuli) ? create(moduuli) : moduuli;
        toteutus.setLearningOpportunitySpecification(moduuli);

        return (KoulutusmoduuliToteutus) koulutusDAO.insert(toteutus);

    }

    @Override
    public LearningOpportunityObject findByOid(String oid) {

        return koulutusDAO.findByOid(LearningOpportunityObject.class, oid);

    }

    private boolean isNew(BaseEntity e) {
        // no good
        return (e.getId() == null);
    }

}

