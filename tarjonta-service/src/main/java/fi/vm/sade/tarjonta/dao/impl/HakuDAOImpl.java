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

import static fi.vm.sade.tarjonta.model.Haku.HAUN_ALKAMIS_PVM;
import static fi.vm.sade.tarjonta.model.Haku.HAUN_LOPPUMIS_PVM;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.google.common.base.Preconditions;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.expr.BooleanExpression;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.QHaku;
import fi.vm.sade.tarjonta.model.QMonikielinenTeksti;
import fi.vm.sade.tarjonta.model.QTekstiKaannos;
import fi.vm.sade.tarjonta.model.searchParams.ListHakuSearchParam;
import fi.vm.sade.tarjonta.service.types.SearchCriteriaType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 * @author Antti Salonen
 */
@Repository
public class HakuDAOImpl extends AbstractJpaDAOImpl<Haku, Long> implements HakuDAO {

    @Override
    public List<Haku> findByKoulutuksenKausi(String kausi, Integer alkamisVuosi) {
        QHaku qHaku = QHaku.haku;
        return from(qHaku)
                .where(qHaku.koulutuksenAlkamiskausiUri.eq(kausi.trim()).and(qHaku.koulutuksenAlkamisVuosi.eq((alkamisVuosi))))
                .list(qHaku);

    }

    @Override
    public List<Haku> findHakukohdeHakus(Haku haku) {
        return getEntityManager()
                .createQuery("select h.haku from Hakukohde h where h.haku.oid = :oid")
                .setParameter("oid", haku.getOid()).getResultList();

    }

    @Override
    public Haku findByOid(String oidString) {
        QHaku qHaku = QHaku.haku;

        return from(qHaku)
                .where(qHaku.oid.eq(oidString.trim()))
                .singleResult(qHaku);
    }

    @Override
    public List<Haku> findBySearchString(String searchString, String kieliKoodi) {
        QMonikielinenTeksti qTekstis = QMonikielinenTeksti.monikielinenTeksti;
        QTekstiKaannos qKaannos = QTekstiKaannos.tekstiKaannos;
        QHaku qHaku = QHaku.haku;

        List<Haku> haut = from(qTekstis, qHaku, qKaannos)
                .join(qHaku.nimi, qTekstis)
                .join(qTekstis.tekstis, qKaannos)
                .where(qKaannos.kieliKoodi.eq(kieliKoodi).and(qKaannos.arvo.like("%" + searchString + "%")))
                .distinct()
                .list(qHaku);
        return haut;
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }
    
    @Override
    public List<Haku> findBySearchCriteria(ListHakuSearchParam param) {


        QHaku haku = QHaku.haku;



        BooleanExpression whereExpr = null;

        if (param.getTila() != null) {
            whereExpr = QuerydslUtils.and(whereExpr,haku.tila.eq(param.getTila()));
        }

        if (param.getKoulutuksenAlkamisKausi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr,haku.koulutuksenAlkamiskausiUri.eq(param.getKoulutuksenAlkamisKausi()));
        }

        if (param.getKoulutuksenAlkamisVuosi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr,haku.koulutuksenAlkamisVuosi.eq(param.getKoulutuksenAlkamisVuosi()));

        }

        JPAQuery q = from(haku);
        if (whereExpr != null) {
        q = q.where(whereExpr);
        }

        return q.list(haku);
    }

    @Override
    public List<String> findOIDsBy(TarjontaTila tila, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {

        // Convert Enums from API enum to DB enum

        QHaku haku = QHaku.haku;

        BooleanExpression whereExpr = null;

        if (tila != null) {
            whereExpr = QuerydslUtils.and(whereExpr, haku.tila.eq(tila));
        }
        if (lastModifiedBefore != null) {
            whereExpr = QuerydslUtils.and(whereExpr, haku.lastUpdateDate.before(lastModifiedBefore));
        }
        if (lastModifiedSince != null) {
            whereExpr = QuerydslUtils.and(whereExpr, haku.lastUpdateDate.after(lastModifiedSince));
        }

        JPAQuery q = from(haku);
        if (whereExpr != null) {
            q = q.where(whereExpr);
        }
        if (count > 0) {
            q = q.limit(count);
        }
        if (startIndex > 0) {
            q.offset(startIndex);
        }

        return q.list(haku.oid);
    }
    
    @Override
    public void update(final Haku entity) {
        getEntityManager().detach(entity);
        Preconditions.checkNotNull(getEntityManager().find(Haku.class, entity.getId()));
        super.update(entity);
    }

}