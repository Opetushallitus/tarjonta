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
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
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
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private KoulutusSisaltyvyysDAO sisaltyvyysDAO;

    @Override
    public Koulutusmoduuli create(Koulutusmoduuli koulutusmoduuli, String parentOid, boolean optional) {

        final Koulutusmoduuli newModuuli = create(koulutusmoduuli);

        Koulutusmoduuli ylamoduuli = koulutusmoduuliDAO.findByOid(parentOid);

        sisaltyvyysDAO.insert(new KoulutusSisaltyvyys(ylamoduuli, newModuuli, optional
            ? KoulutusSisaltyvyys.ValintaTyyppi.SOME_OFF
            : KoulutusSisaltyvyys.ValintaTyyppi.ONE_OFF));

        return newModuuli;

    }

    @Override
    public Koulutusmoduuli create(Koulutusmoduuli moduuli) {

        return koulutusmoduuliDAO.insert(moduuli);

    }

    @Override
    public KoulutusmoduuliToteutus create(KoulutusmoduuliToteutus toteutus, String koulutusmoduuliOid) {

        return create(toteutus, koulutusmoduuliDAO.findByOid(koulutusmoduuliOid));

    }

    @Override
    public KoulutusmoduuliToteutus create(KoulutusmoduuliToteutus toteutus, Koulutusmoduuli moduuli) {

        final Koulutusmoduuli m = isNew(moduuli) ? create(moduuli) : moduuli;
        toteutus.setKoulutusmoduuli(m);

        return (KoulutusmoduuliToteutus) koulutusmoduuliToteutusDAO.insert(toteutus);

    }


    @Override
    public Koulutusmoduuli findByOid(String oid) {

        return koulutusmoduuliDAO.findByOid(oid);

    }

    @Override
    public Koulutusmoduuli update(Koulutusmoduuli moduuli) {

        koulutusmoduuliDAO.update(moduuli);
        return moduuli;

    }

    @Override
    public KoulutusmoduuliToteutus update(KoulutusmoduuliToteutus toteutus) {

        koulutusmoduuliToteutusDAO.update(toteutus);
        return toteutus;

    }

    @Override
    public Koulutusmoduuli findTutkintoOhjelma(String koulutusLuokitusUri, String koulutusOhjelmaUri) {

        // todo: dao kerroksen voisi poistaa, ainoastaan vaikeammat haut voisi sijoittaa helper:n taakse

        return koulutusmoduuliDAO.findTutkintoOhjelma(koulutusLuokitusUri, koulutusOhjelmaUri);

    }

    @Override
    public void deleteKoulutusmoduuliByOid(String oid) {

        List<Koulutusmoduuli> list = koulutusmoduuliDAO.findBy(BaseKoulutusmoduuli.OID_COLUMN_NAME, oid);

        if (list.isEmpty()) {
            // nothing to delete
            return;
        } else if (list.size() > 1) {
            // todo: versioning issue needs to be resolved - oid should always be unique
            throw new IllegalStateException("multiple matches for oid: " + oid + ", refusing to delete");
        }

        final Koulutusmoduuli moduuli = list.get(0);
        final String tila = moduuli.getTila();

        // validate that state is non-published or ready
        if (!KoodistoContract.TarjontaTilat.SUUNNITTELUSSA.equals(tila)) {
            throw new IllegalStateException("refusing to delete Koulutusmoduuli in state: " + tila);
        }

        // validate that we have no children
        if (!moduuli.getSisaltyvyysList().isEmpty()) {
            throw new IllegalStateException("refusing to delete Koulutusmoduuli with children");
        }

        koulutusmoduuliDAO.remove(moduuli);

    }

    private boolean isNew(BaseEntity e) {
        // no good
        return (e.getId() == null);
    }

}

