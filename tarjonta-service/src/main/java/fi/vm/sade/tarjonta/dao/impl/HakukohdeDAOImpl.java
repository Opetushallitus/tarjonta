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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.common.base.Preconditions;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.BooleanExpression;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.QHakukohde;
import fi.vm.sade.tarjonta.model.QHakukohdeLiite;
import fi.vm.sade.tarjonta.model.QKoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.QValintakoe;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.model.util.CollectionUtils;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetKyselyTyyppi;
import java.util.Set;

/**
 */
@Repository
public class HakukohdeDAOImpl extends AbstractJpaDAOImpl<Hakukohde, Long> implements HakukohdeDAO {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Value("${tarjonta-alkamiskausi-syksy}")
    private String tarjontaAlkamiskausiSyksyUri;

    @Override
    public List<Hakukohde> findByKoulutusOid(String koulutusmoduuliToteutusOid) {

        QHakukohde hakukohde = QHakukohde.hakukohde;
        QKoulutusmoduuliToteutus toteutus = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        BooleanExpression oidEq = toteutus.oid.eq(koulutusmoduuliToteutusOid);

        return from(hakukohde).
                join(hakukohde.koulutusmoduuliToteutuses, toteutus).
                where(oidEq).
                list(hakukohde);

    }

    @Override
    public void updateValintakoe(List<Valintakoe> valintakoes, String hakukohdeOid) {
        Hakukohde hakukohde = findHakukohdeByOid(hakukohdeOid);
        for (Valintakoe koe : hakukohde.getValintakoes()) {
            getEntityManager().remove(koe);
        }
        hakukohde.getValintakoes().clear();

        for (Valintakoe valintakoe : valintakoes) {
            valintakoe.setHakukohdeId(hakukohde.getId());
        }

        hakukohde.getValintakoes().addAll(valintakoes);


        getEntityManager().flush();
    }

    @Override
    public void updateLiittees(List<HakukohdeLiite> liites, String hakukohdeOid) {
        Hakukohde hakukohde = findHakukohdeByOid(hakukohdeOid);

        hakukohde.getLiites().clear();

        for (HakukohdeLiite liite : liites) {
            liite.setHakukohde(hakukohde);
        }

        hakukohde.getLiites().addAll(liites);

        getEntityManager().flush();
    }

    @Override
    public List<Valintakoe> findValintakoeByHakukohdeOid(String oid) {
        QHakukohde qHakukohde = QHakukohde.hakukohde;
        QValintakoe qValintakoe = QValintakoe.valintakoe;
        return from(qHakukohde, qValintakoe)
                .where(qHakukohde.oid.eq(oid).and(qValintakoe.hakukohdeId.eq(qHakukohde.id)))
                .list(qValintakoe);
    }

    @Override
    public HakukohdeLiite findHakuKohdeLiiteById(String id) {
        QHakukohdeLiite liite = QHakukohdeLiite.hakukohdeLiite;
        Long idLong = new Long(id);
        return from(liite).where(liite.id.eq(idLong)).singleResult(liite);
    }

    @Override
    public void removeValintakoe(Valintakoe valintakoe) {
        if (valintakoe != null && valintakoe.getId() != null) {
            getEntityManager().remove(getEntityManager().find(Valintakoe.class, valintakoe.getId()));
            getEntityManager().flush();
        }
    }

    @Override
    public Valintakoe findValintaKoeById(String id) {
        QValintakoe qValintakoe = QValintakoe.valintakoe;
        Long idLong = new Long(id);
        return from(qValintakoe).where(qValintakoe.id.eq(idLong)).singleResult(qValintakoe);

    }

    @Override
    public Hakukohde findHakukohdeByOid(final String oid) {
        Preconditions.checkNotNull(oid, "Hakukohde OID cannot be null.");

        return (Hakukohde) getEntityManager().createQuery("FROM " + Hakukohde.class.getName() + " WHERE oid=?")
                .setParameter(1, oid)
                .getSingleResult();
    }

    @Override
    public Hakukohde findHakukohdeWithKomotosByOid(String oid) {
        return findHakukohdeByOid(oid);
    }

    @Override
    public Hakukohde findHakukohdeWithDepenciesByOid(String oid) {
        return findHakukohdeByOid(oid);
    }

    @Override
    public List<Hakukohde> haeHakukohteetJaKoulutukset(HaeHakukohteetKyselyTyyppi kysely) {
        BooleanExpression criteriaExpr = null;
        QHakukohde qHakukohde = QHakukohde.hakukohde;
        if (kysely.getNimiKoodiUri() != null) {
            //search by koodisto koodi uri (hakukohde combobox uri)
            criteriaExpr = qHakukohde.hakukohdeNimi.eq(kysely.getNimiKoodiUri());
        } else {
            //search by concatenated text (result list name)
            String searchStr = (kysely.getNimi() != null) ? kysely.getNimi().toLowerCase() : "";
            criteriaExpr = qHakukohde.hakukohdeKoodistoNimi.toLowerCase().contains(searchStr);
        }

        List<Hakukohde> hakukohdes = from(qHakukohde)
                .where(criteriaExpr).
                list(qHakukohde);

        //Creating grouping such that there is a hakukohde object for each koulutusmoduulitoteutus
        hakukohdes = createGrouping(hakukohdes, kysely);

        List<Hakukohde> vastaus = new ArrayList<Hakukohde>();
        //If a list of organisaatio oids is provided only hakukohdes that match
        //the list are returned
        if (!kysely.getTarjoajaOids().isEmpty()) {
            for (Hakukohde curHk : hakukohdes) {
                if (kysely.getTarjoajaOids().contains(CollectionUtils.singleItem(curHk.getKoulutusmoduuliToteutuses()).getTarjoaja())) {
                    vastaus.add(curHk);
                }
            }
        } else {
            vastaus = hakukohdes;
        }
        return vastaus;
    }

    /*
     * Creating grouping such that there is a hakukohde object for each koulutusmoduulitoteutus
     */
    private List<Hakukohde> createGrouping(List<Hakukohde> hakukohdes, HaeHakukohteetKyselyTyyppi kysely) {
        List<Hakukohde> vastaus = new ArrayList<Hakukohde>();
        for (Hakukohde curHakukohde : hakukohdes) {
            List<String> tarjoajat = new ArrayList<String>();
            if (curHakukohde.getKoulutusmoduuliToteutuses().size() > 1) {
                vastaus.addAll(handleKomotos(curHakukohde, kysely, tarjoajat));
            } else if (isHakukohdeMatch(curHakukohde, kysely, tarjoajat)) {
                vastaus.add(curHakukohde);
                tarjoajat.add(curHakukohde.getKoulutusmoduuliToteutuses().iterator().next().getTarjoaja());
            }
        }
        return vastaus;
    }

    private List<Hakukohde> handleKomotos(Hakukohde hakukohde, HaeHakukohteetKyselyTyyppi kysely, List<String> tarjoajat) {
        List<Hakukohde> vastaus = new ArrayList<Hakukohde>();
        for (KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
            if (isKomotoMatch(komoto, kysely) && !tarjoajat.contains(komoto.getTarjoaja())) {
                Hakukohde newHakukohde = new Hakukohde();
                newHakukohde.setHakukohdeNimi(hakukohde.getHakukohdeNimi());
                newHakukohde.setTila(hakukohde.getTila());
                newHakukohde.setOid(hakukohde.getOid());
                newHakukohde.addKoulutusmoduuliToteutus(komoto);
                newHakukohde.setHaku(hakukohde.getHaku());
                vastaus.add(newHakukohde);
                tarjoajat.add(komoto.getTarjoaja());
            }
        }
        return vastaus;
    }

    private boolean isHakukohdeMatch(Hakukohde hakukohde, HaeHakukohteetKyselyTyyppi kysely, List<String> tarjoajat) {
        KoulutusmoduuliToteutus komoto = !hakukohde.getKoulutusmoduuliToteutuses().isEmpty() ? hakukohde.getKoulutusmoduuliToteutuses().iterator().next() : null;
        return isKomotoMatch(komoto, kysely) && !tarjoajat.contains(komoto.getTarjoaja());
    }

    private boolean isKomotoMatch(KoulutusmoduuliToteutus komoto, HaeHakukohteetKyselyTyyppi kysely) {
        if (komoto == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(komoto.getKoulutuksenAlkamisPvm());
        return isYearMatch(cal, kysely) && isKausiMatch(cal, kysely);
    }

    private boolean isYearMatch(Calendar cal, HaeHakukohteetKyselyTyyppi kysely) {
        if (kysely.getKoulutuksenAlkamisvuosi() == null || kysely.getKoulutuksenAlkamisvuosi() <= 0) {
            return true;
        }
        return cal.get(Calendar.YEAR) == kysely.getKoulutuksenAlkamisvuosi().intValue();
    }

    private boolean isKausiMatch(Calendar cal, HaeHakukohteetKyselyTyyppi kysely) {
        log.debug("ALKAMISKAUSI URI : " + this.tarjontaAlkamiskausiSyksyUri);
        if (kysely.getKoulutuksenAlkamiskausi() == null || kysely.getKoulutuksenAlkamiskausi().isEmpty()) {
            return true;
        }
        if (kysely.getKoulutuksenAlkamiskausi().contains(this.tarjontaAlkamiskausiSyksyUri)) {
            return cal.get(Calendar.MONTH) >= 6;
        }
        return cal.get(Calendar.MONTH) < 6;
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    @Override
    public List<Hakukohde> findOrphanHakukohteet() {
        QHakukohde hakukohde = QHakukohde.hakukohde;
        BooleanExpression toteutusesEmpty = hakukohde.koulutusmoduuliToteutuses.isEmpty();
        return from(hakukohde).where(toteutusesEmpty).list(hakukohde);
    }

    @Override
    public List<String> findOIDsBy(fi.vm.sade.tarjonta.service.types.TarjontaTila tila, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {

        QHakukohde hakukohde = QHakukohde.hakukohde;
        BooleanExpression whereExpr = null;

        if (tila != null) {
            // Convert Enums from API enum to DB enum
            whereExpr = QuerydslUtils.and(whereExpr, hakukohde.tila.eq(fi.vm.sade.tarjonta.shared.types.TarjontaTila.valueOf(tila.name())));
        }
        if (lastModifiedBefore != null) {
            whereExpr = QuerydslUtils.and(whereExpr, hakukohde.lastUpdateDate.before(lastModifiedBefore));
        }
        if (lastModifiedSince != null) {
            whereExpr = QuerydslUtils.and(whereExpr, hakukohde.lastUpdateDate.after(lastModifiedSince));
        }

        // Result selection
        Expression<?>[] projectionExpr = new Expression<?>[]{hakukohde.oid};

        List<Object[]> tmp = findScalars(whereExpr, count, startIndex, projectionExpr);
        return convertToSingleStringList(tmp);
    }

    @Override
    public List<String> findOidsByKoulutusId(long koulutusId) {
        //TODO use constants
        Query q = getEntityManager().createQuery("select h.oid from Hakukohde h JOIN h.koulutusmoduuliToteutuses kmt where kmt.id= :komotoId").setParameter("komotoId", koulutusId);

        List<String> results = (List<String>) q.getResultList();
        return results;
    }

    @Override
    public List<String> findByHakuOid(String hakuOid, String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        log.info("findByHakuOid({}, ...)", hakuOid);

        QHakukohde hakukohde = QHakukohde.hakukohde;

        // Select by haku OID
        BooleanExpression whereExpr = hakukohde.haku.oid.eq(hakuOid);

        // Result selection
        Expression<?>[] projectionExpr = new Expression<?>[]{hakukohde.oid};

        if (lastModifiedBefore != null) {
            whereExpr = whereExpr.and(hakukohde.lastUpdateDate.before(lastModifiedBefore));
        }
        if (lastModifiedSince != null) {
            whereExpr = whereExpr.and(hakukohde.lastUpdateDate.after(lastModifiedSince));
        }

        List<Object[]> tmp = findScalars(whereExpr, count, startIndex, projectionExpr);

        return convertToSingleStringList(tmp);
    }

    /**
     * Convert Object[] listo to String list.
     *
     * @param input
     * @return
     */
    private List<String> convertToSingleStringList(List<Object[]> input) {
        List<String> result = new ArrayList<String>();

        for (Object[] row : input) {
            if (row != null && row.length != 0 && row[0] != null) {
                if (row[0] instanceof String) {
                    result.add((String) row[0]);
                } else {
                    result.add(row[0].toString());
                }
            }
        }

        return result;
    }

    /**
     * Do actual search, convert to list of result object arrays.
     *
     * @param whereExpr
     * @param count
     * @param startIndex
     * @param projectionExpr
     * @return
     */
    private List<Object[]> findScalars(BooleanExpression whereExpr, int count, int startIndex, Expression<?>... projectionExpr) {
        log.info("findScalars({}, {}, {}, {})", new Object[]{whereExpr, count, startIndex, projectionExpr});

        QHakukohde hakukohde = QHakukohde.hakukohde;

        JPAQuery q = from(hakukohde);
        if (whereExpr != null) {
            q = q.where(whereExpr);
        }
        if (count > 0) {
            q = q.limit(count);
        }
        if (startIndex > 0) {
            q.offset(startIndex);
        }

        List<Object[]> result = new ArrayList<Object[]>();

        // Get result and convert to result array
        List<Tuple> lt = q.list(projectionExpr);
        for (Tuple tuple : lt) {
            result.add(tuple.toArray());
        }

        log.info("  result size = {}", result.size());

        return result;
    }

    @Override
    public void update(Hakukohde entity) {
        detach(entity); //optimistic locking requires detach + reload so that the entity exists in hibernate session before merging
        Preconditions.checkNotNull(getEntityManager().find(Hakukohde.class, entity.getId()));
        super.update(entity);
    }
}
