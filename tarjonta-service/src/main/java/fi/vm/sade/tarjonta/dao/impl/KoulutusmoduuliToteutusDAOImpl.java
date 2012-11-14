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
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils;
import fi.vm.sade.tarjonta.model.*;

import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 */
@Repository
public class KoulutusmoduuliToteutusDAOImpl extends AbstractJpaDAOImpl<KoulutusmoduuliToteutus, Long> implements KoulutusmoduuliToteutusDAO {

    @Override
    public KoulutusmoduuliToteutus findByOid(String oid) {

        List<KoulutusmoduuliToteutus> list = findBy(KoulutusmoduuliToteutus.OID_COLUMN_NAME, oid);
        if (list.isEmpty()) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            throw new IllegalStateException("multiple results for oid: " + oid);
        }
    }

    @Override
    public KoulutusmoduuliToteutus findKomotoByOid(String oid) {
        Query query = getEntityManager().createQuery(""
            + "SELECT k FROM KoulutusmoduuliToteutus k "
            + "LEFT JOIN FETCH k.koulutusmoduuli "
            + "where k.oid=:oid");
        query.setParameter("oid", oid);
        return (KoulutusmoduuliToteutus) query.getSingleResult();

    }

    @Override
    public KoulutusmoduuliToteutus findKomotoWithYhteyshenkilosByOid(String oid) {
        Query query = getEntityManager().createQuery(""
            + "SELECT k FROM KoulutusmoduuliToteutus k "
            + "LEFT JOIN FETCH k.yhteyshenkilos "
            + "where k.oid=:oid");
        query.setParameter("oid", oid);
        return (KoulutusmoduuliToteutus) query.getSingleResult();
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    @Override
    public List<KoulutusmoduuliToteutus> findByCriteria(List<String> tarjoajaOids, String matchNimi) {

        QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        QMonikielinenTeksti nimi = QMonikielinenTeksti.monikielinenTeksti;
        QTekstiKaannos nimiTeksti = QTekstiKaannos.tekstiKaannos;

        BooleanExpression criteria = null;

        if (matchNimi != null) {
            criteria = QuerydslUtils.and(criteria, nimiTeksti.teksti.toLowerCase().contains(matchNimi.toLowerCase()));
        }

        if (!tarjoajaOids.isEmpty()) {
            criteria = QuerydslUtils.and(criteria, komoto.tarjoaja.in(tarjoajaOids));
        }

        return from(komoto).
            leftJoin(komoto.nimi, nimi).fetch().
            leftJoin(nimi.tekstis, nimiTeksti).fetch().
            where(criteria).
            list(komoto);

    }

}

