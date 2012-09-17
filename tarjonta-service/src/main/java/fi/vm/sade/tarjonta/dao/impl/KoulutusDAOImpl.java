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
import com.mysema.query.types.expr.BooleanExpression;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.KoulutusDAO;
import fi.vm.sade.tarjonta.model.*;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marko Lyly
 */
@Repository
public class KoulutusDAOImpl extends AbstractJpaDAOImpl<Koulutus, Long> implements KoulutusDAO {

    private static final Logger log = LoggerFactory.getLogger(KoulutusDAO.class);

    @Override
    public <T extends Koulutus> T findByOid(Class<T> type, String oid) {
        return getEntityManager().find(type, oid);
    }

    public List<KoulutusSisaltyvyys> findAllSisaltyvyys() {
        return getEntityManager().
            createQuery("from " + KoulutusSisaltyvyys.class.getSimpleName() + " as s").
            getResultList();
    }

    @Override
    public List<Koulutus> find(String tila, int startIndex, int pageSize) {

        return findBy("tila", tila, startIndex, pageSize);

    }

    /**
     * Returns a list of koulutus in given type that matches given oid.
     * 
     * @param <T>
     * @param koulutusType
     * @param oid
     * @return
     */
    @Override
    public <T> List<T> findAllVersions(Class<T> koulutusType, String oid) {

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(koulutusType);

        Root<T> root = query.from(koulutusType);
        Predicate oidCondition = cb.equal(root.get("oid"), oid);

        query.select(root).where(oidCondition);

        return em.createQuery(query).getResultList();

    }

    @Override
    public <T extends Koulutus> List<T> findAll(Class<T> type) {

        QKoulutus koulutus = QKoulutus.koulutus;
        BooleanExpression typeCriteria = koulutus.instanceOf(type);

        return (List<T>) from(koulutus).where(typeCriteria).list(koulutus);

    }

    @Override
    public <T extends Koulutus> List<T> findAllChildren(Class<T> type, String oid) {

        QKoulutus koulutus = QKoulutus.koulutus;
        QKoulutus parent = new QKoulutus("parent");
        QKoulutusSisaltyvyys sisaltyvyys = QKoulutusSisaltyvyys.koulutusSisaltyvyys;
        BooleanExpression oidEq = parent.oid.eq(oid);

        return (List<T>) from(koulutus).
            join(koulutus.parents, sisaltyvyys).
            join(sisaltyvyys.parent, parent).
            where(oidEq).
            list(koulutus);

    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

}

