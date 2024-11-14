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

import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import fi.vm.sade.tarjonta.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.QKoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.QKoulutusmoduuli;

/**
 *
 */
@Repository
public class KoulutusSisaltyvyysDAOImpl extends
        AbstractJpaDAOImpl<KoulutusSisaltyvyys, Long> implements
        KoulutusSisaltyvyysDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private BooleanBuilder bb(Predicate initial) {
        return new BooleanBuilder(initial);
    }

    private JPAQueryFactory queryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    /**
     * Palauttaa koulutusmoduulin childrenit
     * @param parentKomoOid
     * @return
     */
    public List<String> getChildren(String parentKomoOid) {
        final QKoulutusSisaltyvyys koulutusSisaltyvyys = QKoulutusSisaltyvyys.koulutusSisaltyvyys;
        final QKoulutusmoduuli koulutusmoduuli = QKoulutusmoduuli.koulutusmoduuli;
        final QKoulutusmoduuli child = QKoulutusmoduuli.koulutusmoduuli;
        final Predicate where = bb(koulutusSisaltyvyys.ylamoduuli.oid.eq(parentKomoOid));
        return queryFactory().select(child.oid).from(koulutusmoduuli).join(koulutusmoduuli.sisaltyvyysList, QKoulutusSisaltyvyys.koulutusSisaltyvyys).join(koulutusSisaltyvyys.alamoduuliList, child).where(where).fetch();
    }

    /**
     * Palauttaa koulutusmoduulin parentit
     * @return
     */
    public List<String> getParents(String childId) {
        final QKoulutusSisaltyvyys koulutusSisaltyvyys = QKoulutusSisaltyvyys.koulutusSisaltyvyys;
        final QKoulutusmoduuli koulutusmoduuli = QKoulutusmoduuli.koulutusmoduuli;
        final QKoulutusmoduuli child = QKoulutusmoduuli.koulutusmoduuli;
        final Predicate where = bb(child.oid.eq(childId));
        return queryFactory().select(koulutusSisaltyvyys.ylamoduuli.oid).from(koulutusmoduuli)
                .join(koulutusmoduuli.sisaltyvyysList, QKoulutusSisaltyvyys.koulutusSisaltyvyys)
                .leftJoin(koulutusSisaltyvyys.alamoduuliList, child)
                .where(where)
                .distinct()
                .fetch();
    }

    @Override
    public void update(KoulutusSisaltyvyys entity) {
        super.update(entity);
        entityManager.detach(entity.getYlamoduuli());
        for(Koulutusmoduuli komo: entity.getAlamoduuliList()){
            entityManager.detach(komo);
        }
    }

    @Override
    public KoulutusSisaltyvyys insert(KoulutusSisaltyvyys entity) {
        KoulutusSisaltyvyys ent = super.insert(entity);
        entityManager.detach(ent.getYlamoduuli());
        for(Koulutusmoduuli komo: ent.getAlamoduuliList()){
            entityManager.detach(komo);
        }
        return ent;
    }

    @Override
    public void remove(KoulutusSisaltyvyys entity) {
        super.remove(entity);
        entityManager.detach(entity.getYlamoduuli());
        for(Koulutusmoduuli komo: entity.getAlamoduuliList()){
            entityManager.detach(komo);
        }

    }
}
