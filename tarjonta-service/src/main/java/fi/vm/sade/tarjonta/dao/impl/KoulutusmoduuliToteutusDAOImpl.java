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
package fi.vm.sade.tarjonta.dao.impl;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliSisaltyvyys;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.NoSuchOIDException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Jukka Raanamo
 */
@Repository
public class KoulutusmoduuliToteutusDAOImpl extends AbstractJpaDAOImpl<KoulutusmoduuliToteutus, Long> implements KoulutusmoduuliToteutusDAO {

    private static final Logger log = LoggerFactory.getLogger(KoulutusmoduuliToteutusDAO.class);

    @Override
    public KoulutusmoduuliToteutus findByOid(String oid) {

        List<KoulutusmoduuliToteutus> results = findBy("oid", oid);
        if (results.isEmpty()) {
            throw new NoSuchOIDException(oid);
        } else if (results.size() == 1) {
            return results.get(0);
        } else {
            throw new IllegalStateException("more than one match for OID: " + oid);
        }

    }

}

