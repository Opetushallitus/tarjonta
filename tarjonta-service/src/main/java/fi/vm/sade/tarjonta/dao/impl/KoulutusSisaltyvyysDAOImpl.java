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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Predicate;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
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

    private JPAQuery q(EntityPath<?> entityPath) {
        return new JPAQuery(entityManager).from(entityPath);
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
        return q(koulutusmoduuli).join(koulutusmoduuli.sisaltyvyysList, QKoulutusSisaltyvyys.koulutusSisaltyvyys).join(koulutusSisaltyvyys.alamoduuliList, child).where(where).list(child.oid);
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
        return q(koulutusmoduuli)
                .join(koulutusmoduuli.sisaltyvyysList, QKoulutusSisaltyvyys.koulutusSisaltyvyys)
                .leftJoin(koulutusSisaltyvyys.alamoduuliList, child)
                .where(where)
                .distinct()
                .list(koulutusSisaltyvyys.ylamoduuli.oid);
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
