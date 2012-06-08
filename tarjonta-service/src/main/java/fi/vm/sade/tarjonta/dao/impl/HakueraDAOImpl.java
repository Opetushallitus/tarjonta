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
import fi.vm.sade.tarjonta.dao.HakueraDAO;
import fi.vm.sade.tarjonta.model.Hakuera;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static fi.vm.sade.tarjonta.model.Hakuera.HAUN_ALKAMIS_PVM;
import static fi.vm.sade.tarjonta.model.Hakuera.HAUN_LOPPUMIS_PVM;

/**
 * @author Antti Salonen
 */
@Repository
public class HakueraDAOImpl extends AbstractJpaDAOImpl<Hakuera, Long> implements HakueraDAO {

    private static final Logger log = LoggerFactory.getLogger(HakueraDAOImpl.class);

    @Override
    public List<Hakuera> findAll(SearchCriteriaDTO searchCriteria) {
        boolean p = searchCriteria.isPaattyneet();
        boolean m = searchCriteria.isMeneillaan();
        boolean t = searchCriteria.isTulevat();
        String lang = searchCriteria.getLang();

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Hakuera> query = cb.createQuery(Hakuera.class);
        Root<Hakuera> hakuera = query.from(Hakuera.class);
        query.orderBy(createOrderBy(lang, cb, hakuera));
        Predicate where = null;

        if (m && p && t) {
            // kaikki
        } else if (p && m && !t) {
            // päättyneet ja meneillään -> alkuaika pienempi kuin nyt
            where = cb.lessThan(hakuera.<Date>get(HAUN_ALKAMIS_PVM), cb.currentTimestamp());
        } else if (!p && m && t) {
            // meneilläään ja tulevat -> loppuaika suurempi kuin nyt
            where = cb.greaterThan(hakuera.<Date>get(HAUN_LOPPUMIS_PVM), cb.currentTimestamp());
        } else if (p && !m && t) {
            // päättyneet ja tulevat -> loppuaika pienempi kuin nyt TAI alkuaika suurempi kuin nyt
            where = cb.or(
                    cb.lessThan(hakuera.<Date>get(HAUN_LOPPUMIS_PVM), cb.currentTimestamp()),
                    cb.greaterThan(hakuera.<Date>get(HAUN_ALKAMIS_PVM), cb.currentTimestamp())
            );
        } else if (p && !m && !t) {
            // päättyneet -> loppuaika pienempi kuin nyt
            where = cb.lessThan(hakuera.<Date>get(HAUN_LOPPUMIS_PVM), cb.currentTimestamp());
        } else if (!p && m && !t) {
            // meneillään -> alkuaika pienempi kuin nyt JA loppuaika suurempi kuin nyt
            where = cb.between(cb.currentTimestamp(), hakuera.<Date>get(HAUN_ALKAMIS_PVM), hakuera.<Date>get(HAUN_LOPPUMIS_PVM));
        } else if (!p && !m && t) {
            // tulevat -> alkuaika suurempi kuin nyt
            where = cb.greaterThan(hakuera.<Date>get(HAUN_ALKAMIS_PVM), cb.currentTimestamp());
        } else { // (!m && !p && !t)
            // ei mitään
            return new ArrayList<Hakuera>();
        }

        query.select(hakuera);
        if (where != null) {
            query.where(where);
        }

        return getEntityManager().createQuery(query).getResultList();
    }

    private Order createOrderBy(String lang, CriteriaBuilder cb, Root<Hakuera> hakuera) {
        Order orderBy;
        if ("sv".equals(lang)) {
            orderBy = cb.asc(hakuera.get("nimiSv"));
        } else if ("en".equals(lang)) {
            orderBy = cb.asc(hakuera.get("nimiEn"));
        } else {
            orderBy = cb.asc(hakuera.get("nimiFi"));
        }
        return orderBy;
    }
}

