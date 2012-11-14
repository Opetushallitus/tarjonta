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
import com.mysema.query.types.expr.BooleanExpression;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marko Lyly
 */
@Repository
public class KoulutusmoduuliDAOImpl extends AbstractJpaDAOImpl<Koulutusmoduuli, Long> implements KoulutusmoduuliDAO {

    private static final Logger log = LoggerFactory.getLogger(KoulutusmoduuliDAO.class);

    @Override
    public Koulutusmoduuli findByOid(String oid) {

        QKoulutusmoduuli moduuli = QKoulutusmoduuli.koulutusmoduuli;
        BooleanExpression oidEq = moduuli.oid.eq(oid);

        return from(moduuli).where(oidEq).singleResult(moduuli);

    }

    @Override
    public List<Koulutusmoduuli> find(String tila, int startIndex, int pageSize) {

        return findBy(Koulutusmoduuli.TILA_COLUMN_NAME, tila, startIndex, pageSize);

    }

    @Override
    public List<Koulutusmoduuli> getAlamoduuliList(String oid) {

        QKoulutusmoduuli moduuli = QKoulutusmoduuli.koulutusmoduuli;
        QKoulutusmoduuli ylamoduuli = new QKoulutusmoduuli("ylamoduuli");
        QKoulutusSisaltyvyys sisaltyvyys = QKoulutusSisaltyvyys.koulutusSisaltyvyys;
        BooleanExpression oidEq = ylamoduuli.oid.eq(oid);

        return (List<Koulutusmoduuli>) from(moduuli).
            join(moduuli.sisaltyvyysList, sisaltyvyys).
            join(sisaltyvyys.ylamoduuli, ylamoduuli).
            where(oidEq).
            list(moduuli);

    }

    @Override
    public List<Koulutusmoduuli> search(SearchCriteria criteria) {

        QKoulutusmoduuli moduuli = QKoulutusmoduuli.koulutusmoduuli;
        BooleanExpression whereExpr = null;
        QMonikielinenTeksti nimi = QMonikielinenTeksti.monikielinenTeksti;

        // todo: are we searching for LOI or LOS - that group by e.g. per organisaatio is
        // take from different attribute
        //Expression groupBy = groupBy(criteria);

        if (criteria.getNimiQuery() != null) {
            // todo: FIX THIS
            //whereExpr = and(whereExpr, moduuli.nimi.tekstis.like("%" + criteria.getNimiQuery() + "%"));
        }

        if (criteria.getKoulutusKoodi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusKoodi.eq(criteria.getKoulutusKoodi()));
        }

        if (criteria.getKoulutusohjelmaKoodi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusohjelmaKoodi.eq(criteria.getKoulutusohjelmaKoodi()));
        }


        return from(moduuli).
            where(whereExpr).
            leftJoin(moduuli.nimi, nimi).fetch().
            list(moduuli);

    }

    @Override
    public Koulutusmoduuli findTutkintoOhjelma(String koulutusLuokitusUri, String koulutusOhjelmaUri) {
        QKoulutusmoduuli moduuli = QKoulutusmoduuli.koulutusmoduuli;
        BooleanExpression whereExpr = null;

        SearchCriteria criteria = new SearchCriteria();
        criteria.setKoulutusKoodi(koulutusLuokitusUri);
        criteria.setKoulutusohjelmaKoodi(koulutusOhjelmaUri);

        if (criteria.getKoulutusKoodi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusKoodi.eq(criteria.getKoulutusKoodi()));
        }

        if (criteria.getKoulutusohjelmaKoodi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusohjelmaKoodi.eq(criteria.getKoulutusohjelmaKoodi()));
        }

        return from(moduuli).
            where(whereExpr).
            singleResult(moduuli);

    }


    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }


}

