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
import com.mysema.query.types.Expression;
import com.mysema.query.types.Visitor;
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
public class KoulutusDAOImpl extends AbstractJpaDAOImpl<LearningOpportunityObject, Long> implements KoulutusDAO {

    private static final Logger log = LoggerFactory.getLogger(KoulutusDAO.class);

    @Override
    public <T extends LearningOpportunityObject> T findByOid(Class<T> type, String oid) {

        QLearningOpportunityObject loo = QLearningOpportunityObject.learningOpportunityObject;
        BooleanExpression typeCriteria = loo.instanceOf(type);
        BooleanExpression oidCriteria = loo.oid.eq(oid);
        BooleanExpression andCriteria = typeCriteria.and(oidCriteria);

        return (T) from(loo).where(andCriteria).singleResult(loo);

    }

    @Override
    public List<LearningOpportunityObject> find(String tila, int startIndex, int pageSize) {

        return findBy("tila", tila, startIndex, pageSize);

    }

    /**
     * Returns version history of a LearningOpportunityObject.
     *
     * TODO: double check if and how version management is handled in LOO. This implementation assumes
     * that several LOO's can share a single OID and be separated by version number.
     *
     * @param <T>
     * @param koulutusType
     * @param oid
     * @return
     */
    @Override
    public List<? extends LearningOpportunityObject> findAllVersions(String oid) {

        QLearningOpportunityObject loo = QLearningOpportunityObject.learningOpportunityObject;
        BooleanExpression oidEq = loo.oid.eq(oid);

        return from(loo).where(oidEq).orderBy(loo.version.asc()).list(loo);

    }

    @Override
    public <T extends LearningOpportunityObject> List<T> findAll(Class<T> type) {

        QLearningOpportunityObject loo = QLearningOpportunityObject.learningOpportunityObject;
        BooleanExpression typeCriteria = loo.instanceOf(type);

        return (List<T>) from(loo).where(typeCriteria).list(loo);

    }

    @Override
    public <T extends LearningOpportunityObject> List<T> findAllChildren(Class<T> type, String oid) {

        QLearningOpportunityObject loo = QLearningOpportunityObject.learningOpportunityObject;
        QLearningOpportunityObject parent = new QLearningOpportunityObject("parent");
        QKoulutusRakenne rakennes = QKoulutusRakenne.koulutusRakenne;
        BooleanExpression oidEq = parent.oid.eq(oid);

        return (List<T>) from(loo).
            join(loo.structures, rakennes).
            join(rakennes.parent, parent).
            where(oidEq).
            list(loo);

    }

    @Override
    public <T extends LearningOpportunityObject> List<T> search(SearchCriteria criteria) {

        QLearningOpportunityObject loo = QLearningOpportunityObject.learningOpportunityObject;
        BooleanExpression whereExpr = null;

        // todo: are we searching for LOI or LOS - that group by e.g. per organisaatio is
        // take from different attribute
        Expression groupBy = groupBy(criteria);

        if (criteria.getNimiQuery() != null) {
            // todo: limit if too expensive - make case insensitive
            whereExpr = and(whereExpr, loo.nimi.like("%" + criteria.getNimiQuery() + "%"));
        }
        if (criteria.getType() != null) {
            whereExpr = and(whereExpr, loo.instanceOf(criteria.getType()));
        }

        // todo: joins

        return (List<T>) from(loo).
            where(whereExpr).
            list(loo);

    }

    private BooleanExpression and(BooleanExpression existing, BooleanExpression what) {
        return (existing == null ? what : existing.and(what));
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    private Expression groupBy(SearchCriteria criteria) {
        final SearchCriteria.GroupBy groupBy = criteria.getGroupBy();
        return null;
    }

}

