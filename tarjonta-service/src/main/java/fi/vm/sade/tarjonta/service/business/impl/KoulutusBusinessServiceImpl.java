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
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.business.KoulutusBusinessService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jukka Raanamo
 */
@Service
@Transactional
public class KoulutusBusinessServiceImpl implements KoulutusBusinessService {

    @Autowired
    private KoulutusDAO koulutusDAO;

    @Override
    public Koulutusmoduuli create(Koulutusmoduuli koulutusmoduuli) {
        return (Koulutusmoduuli) koulutusDAO.insert(koulutusmoduuli);
    }

    @Override
    public KoulutusmoduuliToteutus create(KoulutusmoduuliToteutus toteutus) {
        return (KoulutusmoduuliToteutus) koulutusDAO.insert(toteutus);
    }

    @Override
    public KoulutusmoduuliToteutus create(KoulutusmoduuliToteutus toteutus, Koulutusmoduuli moduuli) {

        final Koulutusmoduuli k = isNew(moduuli) ? create(moduuli) : moduuli;
        toteutus.setKoulutusmoduuli(moduuli);

        return create(toteutus);

    }

    @Override
    public List<Koulutusmoduuli> findAllKoulutusmoduuliVersions(String oid) {
        return koulutusDAO.findAllVersions(Koulutusmoduuli.class, oid);
    }

    @Override
    public List<KoulutusmoduuliToteutus> findAllKoulutusmoduuliToteutusVersions(String oid) {
        return koulutusDAO.findAllVersions(KoulutusmoduuliToteutus.class, oid);
    }

    @Override
    public Koulutusmoduuli findKoulutusmoduuliByOid(String oid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private boolean isNew(BaseEntity e) {
        // no good
        return (e.getId() == null);
    }

}

