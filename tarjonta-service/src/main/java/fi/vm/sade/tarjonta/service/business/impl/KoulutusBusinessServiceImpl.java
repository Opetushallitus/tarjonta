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
import fi.vm.sade.tarjonta.dao.KoulutusRakenneDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.business.KoulutusBusinessService;
import java.util.List;
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
    private KoulutusRakenneDAO rakenneDAO;

    @Override
    public Koulutusmoduuli create(Koulutusmoduuli koulutusmoduuli, String parentOid, boolean optional) {

        final Koulutusmoduuli newModuuli = create(koulutusmoduuli);

        Koulutusmoduuli parent = koulutusDAO.findByOid(Koulutusmoduuli.class, parentOid);

        // todo: since we added more logic to KoulutusRakenne, plain boolean "optional" will not do anymore, refactor
        rakenneDAO.insert(new KoulutusRakenne(parent, newModuuli, optional
            ? KoulutusRakenne.SelectorType.SOME_OFF
            : KoulutusRakenne.SelectorType.ONE_OFF));

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

    @Override
    public Koulutusmoduuli update(Koulutusmoduuli moduuli) {

        koulutusDAO.update(moduuli);
        return moduuli;

    }

    @Override
    public KoulutusmoduuliToteutus update(KoulutusmoduuliToteutus toteutus) {

        koulutusDAO.update(toteutus);
        return toteutus;

    }

    @Override
    public void deleteByOid(String oid) {

        List<LearningOpportunityObject> list = koulutusDAO.findBy(LearningOpportunityObject.OID_COLUMN_NAME, oid);

        if (list.isEmpty()) {
            // nothing to delete
            return;
        } else if (list.size() > 1) {
            // todo: versioning issue needs to be resolved - oid should always be unique
            throw new IllegalStateException("multiple matches for oid: " + oid + ", refusing to delete");
        }

        final LearningOpportunityObject loo = list.get(0);
        final String tila = loo.getTila();

        // validate that state is non-published or ready
        if (!KoodistoContract.TarjontaTilat.SUUNNITTELUSSA.equals(tila)) {
            throw new IllegalStateException("refusing to delete LOO in state: " + tila);
        }

        // validate that we have no children
        if (!loo.getStructures().isEmpty()) {
            throw new IllegalStateException("refusing to delete LOO with children");
        }

        if (loo instanceof Koulutusmoduuli) {
            delete((Koulutusmoduuli) loo);
        } else if (loo instanceof KoulutusmoduuliToteutus) {
            delete((KoulutusmoduuliToteutus) loo);
        } else {
            throw new IllegalStateException("unknown type of LearningOpportunityObject: " + loo.getClass().getName());
        }

    }

    private void delete(Koulutusmoduuli koulutusmoduuli) {
        // todo: what are the conditions that allow to delete a Koulutusmoduuli
    }

    private void delete(KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        // todo: what are the conditions that allow to delete a KoulutusmoduuliToteutus
    }

    private boolean isNew(BaseEntity e) {
        // no good
        return (e.getId() == null);
    }

}

