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

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Predicate;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.dao.OppiaineDAO;
import fi.vm.sade.tarjonta.model.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Repository
public class OppiaineDaoImpl extends AbstractJpaDAOImpl<Oppiaine, Long> implements OppiaineDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public Oppiaine findById(Long id) {
        QOppiaine qOppiaine = QOppiaine.oppiaine1;

        return from(qOppiaine)
                .where(qOppiaine.id.eq(id))
                .uniqueResult(qOppiaine);
    }

    public Oppiaine findOneByOppiaineKieliKoodi(String oppiaine, String kieliKoodi) {
        QOppiaine qOppiaine = QOppiaine.oppiaine1;

        return from(qOppiaine)
                .where(
                        qOppiaine.oppiaine.eq(oppiaine)
                                .and(qOppiaine.kieliKoodi.eq(kieliKoodi))
                )
                .uniqueResult(qOppiaine);
    }

    public List<Oppiaine> findByOppiaineKieliKoodi(String oppiaine, String kieliKoodi) {
        QOppiaine qOppiaine = QOppiaine.oppiaine1;

        return from(qOppiaine)
                .where(
                        qOppiaine.oppiaine.containsIgnoreCase(oppiaine)
                                .and(qOppiaine.kieliKoodi.eq(kieliKoodi))
                )
                .list(qOppiaine);
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

}
