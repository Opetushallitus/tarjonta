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
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliSisaltyvyys;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Jukka Raanamo
 * @author Marko Lyly
 */
@Repository
public class KoulutusmoduuliDAOImpl extends AbstractJpaDAOImpl<Koulutusmoduuli, Long> implements KoulutusmoduuliDAO {

    public List<KoulutusmoduuliSisaltyvyys> findAllSisaltyvyys() {
        return getEntityManager().
                createQuery("from " + KoulutusmoduuliSisaltyvyys.class.getSimpleName() + " as s").
                getResultList();
    }

    public Koulutusmoduuli findByOid(String oid) {
        Koulutusmoduuli result = null;

        List<Koulutusmoduuli> results = findBy("oid", oid);
        if (results.size() == 0) {
            // TODO not found
        } else if (results.size() == 1) {
            result = results.get(0);
        } else {
            // TODO too many matches!
        }

        return result;
    }
}
