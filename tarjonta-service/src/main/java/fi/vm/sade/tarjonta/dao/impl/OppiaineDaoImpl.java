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

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.OppiaineDAO;
import fi.vm.sade.tarjonta.model.Oppiaine;
import fi.vm.sade.tarjonta.model.QOppiaine;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

    public void deleteUnusedOppiaineet() {
        // Hae poistettavien oppiaineiden id:t
        List<Long> oppianeIdsToDelete = getEntityManager().createNativeQuery(
                "SELECT id FROM oppiaineet WHERE id NOT IN (" +
                        " SELECT DISTINCT oppiaine_id from koulutusmoduuli_toteutus_oppiaineet koulutus_oppiaine" +
                        " JOIN koulutusmoduuli_toteutus koulutus on koulutus.id = koulutus_oppiaine.koulutusmoduuli_toteutus_id" +
                        " WHERE koulutus.tila != 'POISTETTU'" +
                ")"
        ).getResultList();

        if (oppianeIdsToDelete.isEmpty()) {
            return;
        }

        // Poista many-to-many join taulusta
        Query deleteFromJoinTableQ = getEntityManager().createNativeQuery(
                "DELETE FROM koulutusmoduuli_toteutus_oppiaineet WHERE oppiaine_id IN (:ids)"
        );
        deleteFromJoinTableQ.setParameter("ids", oppianeIdsToDelete);
        deleteFromJoinTableQ.executeUpdate();

        // Poista oppiaine
        Query deleteFromOppiaineQ = getEntityManager().createNativeQuery(
                "DELETE FROM oppiaineet WHERE id IN (:ids)"
        );
        deleteFromOppiaineQ.setParameter("ids", oppianeIdsToDelete);
        deleteFromOppiaineQ.executeUpdate();
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

}
