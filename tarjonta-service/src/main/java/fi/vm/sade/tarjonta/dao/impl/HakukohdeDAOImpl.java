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

import java.util.*;

import javax.persistence.Query;

import fi.vm.sade.tarjonta.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.BooleanExpression;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 */
@Repository
public class HakukohdeDAOImpl extends AbstractJpaDAOImpl<Hakukohde, Long> implements HakukohdeDAO {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Value("${tarjonta-alkamiskausi-syksy}")
    private String tarjontaAlkamiskausiSyksyUri;

    private Collection<TarjontaTila> poistettuTila = Arrays.asList(TarjontaTila.POISTETTU);


    @Override
    public List<Hakukohde> findByKoulutusOid(String koulutusmoduuliToteutusOid) {

        QHakukohde hakukohde = QHakukohde.hakukohde;
        QKoulutusmoduuliToteutus toteutus = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        BooleanExpression oidEq = toteutus.oid.eq(koulutusmoduuliToteutusOid);

        return from(hakukohde).
                join(hakukohde.koulutusmoduuliToteutuses, toteutus).
                where(oidEq.and(hakukohde.tila.notIn(poistettuTila))).
                list(hakukohde);

    }

    @Override
    public Hakukohde findHakukohdeByUlkoinenTunniste(String ulkoinenTunniste, String tarjoajaOid) {

        QHakukohde qHakukohde = QHakukohde.hakukohde;
        QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

        return from(qHakukohde)
                .join(qHakukohde.koulutusmoduuliToteutuses, qKomoto)
                .where(qHakukohde.ulkoinenTunniste.eq(ulkoinenTunniste).and(qKomoto.tarjoaja.eq(tarjoajaOid)).and(qHakukohde.tila.notIn(poistettuTila)))
                .singleResult(qHakukohde);

    }

    @Override
    public void updateValintakoe(List<Valintakoe> valintakoes, String hakukohdeOid) {
        Hakukohde hakukohde = findHakukohdeByOid(hakukohdeOid);
        for (Valintakoe koe : hakukohde.getValintakoes()) {
            getEntityManager().remove(koe);
        }
        hakukohde.getValintakoes().clear();

        for (Valintakoe valintakoe : valintakoes) {
            valintakoe.setHakukohde(hakukohde);
        }

        hakukohde.getValintakoes().addAll(valintakoes);

       //getEntityManager().flush();
    }

    @Override
    public void insertLiittees(List<HakukohdeLiite> liites, String hakukohdeOid) {
        Hakukohde hakukohde = findHakukohdeByOid(hakukohdeOid);

        hakukohde.getLiites().clear();
        for (HakukohdeLiite liite : liites) {
            liite.setHakukohde(hakukohde);
        }

        hakukohde.getLiites().addAll(liites);

        //getEntityManager().flush();
    }

    @Override
    public void updateSingleValintakoe(Valintakoe valintakoe, String hakukohdeOid) {

        Valintakoe managedValintakoe = findValintaKoeById(valintakoe.getId().toString());

        if (valintakoe.getKuvaus() != null) {
            managedValintakoe.setKuvaus(valintakoe.getKuvaus());
        }

        managedValintakoe.getAjankohtas().removeAll(managedValintakoe.getAjankohtas());

        managedValintakoe.getAjankohtas().addAll(valintakoe.getAjankohtas());
        managedValintakoe.setKieli(valintakoe.getKieli());
        managedValintakoe.setLisanaytot(valintakoe.getLisanaytot());
        managedValintakoe.getPisterajat().removeAll(managedValintakoe.getPisterajat());
        managedValintakoe.getPisterajat().addAll(valintakoe.getPisterajat());
        managedValintakoe.setValintakoeNimi(valintakoe.getValintakoeNimi());
        managedValintakoe.setTyyppiUri(valintakoe.getTyyppiUri());

    }

    @Override
    public void updateLiite(HakukohdeLiite hakukohdeLiite, String hakukohdeOid) {

        HakukohdeLiite managedLiite = findHakuKohdeLiiteById(hakukohdeLiite.getId().toString());

        //Ugly, but so is Hibernate
        managedLiite.setErapaiva(hakukohdeLiite.getErapaiva());
        managedLiite.setKieli(hakukohdeLiite.getKieli());
        managedLiite.setHakukohdeLiiteNimi(hakukohdeLiite.getHakukohdeLiiteNimi());
        managedLiite.setKuvaus(hakukohdeLiite.getKuvaus());
        managedLiite.setLiitetyyppi(hakukohdeLiite.getLiitetyyppi());
        managedLiite.setSahkoinenToimitusosoite(hakukohdeLiite.getSahkoinenToimitusosoite());
        managedLiite.setToimitusosoite(hakukohdeLiite.getToimitusosoite());

        //getEntityManager().flush();

    }

    @Override
    public List<Valintakoe> findValintakoeByHakukohdeOid(String oid) {
        QHakukohde qHakukohde = QHakukohde.hakukohde;
        QValintakoe qValintakoe = QValintakoe.valintakoe;
        return from(qHakukohde, qValintakoe)
                .where(qHakukohde.oid.eq(oid).and(qValintakoe.hakukohde.eq(qHakukohde)))
                .list(qValintakoe);
    }

    @Override
    public List<HakukohdeLiite> findHakukohdeLiitesByHakukohdeOid(String oid) {
        QHakukohde qHakukohde = QHakukohde.hakukohde;
        QHakukohdeLiite qHakukohdeLiite = QHakukohdeLiite.hakukohdeLiite;

        return from(qHakukohde, qHakukohdeLiite)
                .where(qHakukohde.oid.eq(oid).and(qHakukohdeLiite.hakukohde.eq(qHakukohde)))
                .list(qHakukohdeLiite);
    }

    @Override
    public HakukohdeLiite findHakuKohdeLiiteById(String id) {
        QHakukohdeLiite liite = QHakukohdeLiite.hakukohdeLiite;
        Long idLong = new Long(id);
        return from(liite).where(liite.id.eq(idLong)).singleResult(liite);
    }

    public List<KoulutusmoduuliToteutus> komotoTest(String term, int year, String providerOid) {

        /*QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

         return from(qKomoto)
         .where(qKomoto.alkamiskausi.eq(term).and(qKomoto.alkamisVuosi.eq(year).and(qKomoto.tarjoaja.eq(providerOid)))).list(qKomoto);     */
        Query query = getEntityManager().createQuery("SELECT k from KoulutusmoduuliToteutus k join fetch k.hakukohdes"
                + "WHERE k.alkamiskausi = :kausi "
                + "AND k.alkamisVuosi = :vuosi "
                + "AND k.tarjoaja = :tarjoaja");

        query.setParameter("kausi", term);
        query.setParameter("vuosi", year);
        query.setParameter("tarjoaja", providerOid);

        return (List<KoulutusmoduuliToteutus>) query.getResultList();

    }

    @Override
    public List<Hakukohde> findByNameTermAndYear(String name, String term, int year, String providerOid) {

        QHakukohde qHakukohde = QHakukohde.hakukohde;
        QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

        return from(qHakukohde)
                .innerJoin(qHakukohde.koulutusmoduuliToteutuses, qKomoto)
                .where(qHakukohde.hakukohdeNimi.eq(name)
                        .and(qHakukohde.tila.notIn(TarjontaTila.POISTETTU))
                        .and(qKomoto.alkamiskausiUri.eq(term).and(qKomoto.alkamisVuosi.eq(year).and(qKomoto.tarjoaja.eq(providerOid))))).list(qHakukohde);

    }

    @Override
    public List<Hakukohde> findByTermYearAndProvider(String term, int year, String providerOid) {

        QHakukohde qHakukohde = QHakukohde.hakukohde;
        QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

        return from(qHakukohde)
                .innerJoin(qHakukohde.koulutusmoduuliToteutuses, qKomoto)
                .where(qKomoto.alkamiskausiUri.eq(term).and(qKomoto.alkamisVuosi.eq(year)
                        .and(qHakukohde.tila.notIn(TarjontaTila.POISTETTU))
                        .and(qKomoto.tarjoaja.eq(providerOid)))).list(qHakukohde);

    }

    @Override
    public void removeValintakoe(Valintakoe valintakoe) {
        if (valintakoe != null && valintakoe.getId() != null) {
            getEntityManager().remove(getEntityManager().find(Valintakoe.class, valintakoe.getId()));
            //getEntityManager().flush();
        }
    }

    @Override
    public void removeHakukohdeLiite(HakukohdeLiite hakukohdeLiite) {
        if (hakukohdeLiite != null && hakukohdeLiite.getId() != null) {
            getEntityManager().remove(getEntityManager().find(HakukohdeLiite.class, hakukohdeLiite.getId()));
           // getEntityManager().flush();
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

        QHakukohde qHakukohde = QHakukohde.hakukohde;

        return from(qHakukohde)
                .where(qHakukohde.oid.eq(oid)).singleResult(qHakukohde);

    }

    @Override
    public Hakukohde findHakukohdeByOid(final String oid, final boolean showDeleted) {

        Preconditions.checkNotNull(oid, "Hakukohde OID cannot be null.");
        QHakukohde qHakukohde = QHakukohde.hakukohde;
        if (showDeleted) {



            return from(qHakukohde)
                    .where(qHakukohde.oid.eq(oid)).singleResult(qHakukohde);

        } else {
            return from(qHakukohde)
                    .where(qHakukohde.oid.eq(oid).and(qHakukohde.tila.notIn(TarjontaTila.POISTETTU))).singleResult(qHakukohde);

        }

    }

    @Override
    public Hakukohde findHakukohdeWithKomotosByOid(String oid) {
        return findHakukohdeByOid(oid);
    }

    @Override
    public Hakukohde findHakukohdeWithDepenciesByOid(String oid) {
        return findHakukohdeByOid(oid);
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
    public List<String> findOIDsBy(fi.vm.sade.tarjonta.service.types.TarjontaTila tila, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince, boolean showKK) {

        QHakukohde hakukohde = QHakukohde.hakukohde;
        BooleanExpression whereExpr = null;

        if (tila != null) {
            // Convert Enums from API enum to DB enum
            whereExpr = QuerydslUtils.and(whereExpr, hakukohde.tila.eq(fi.vm.sade.tarjonta.shared.types.TarjontaTila.valueOf(tila.name())));
        } else {
            whereExpr = QuerydslUtils.and(whereExpr,hakukohde.tila.notIn(poistettuTila));
        }
        if (lastModifiedBefore != null) {
            whereExpr = QuerydslUtils.and(whereExpr, hakukohde.lastUpdateDate.before(lastModifiedBefore));
        }
        if (lastModifiedSince != null) {
            whereExpr = QuerydslUtils.and(whereExpr, hakukohde.lastUpdateDate.after(lastModifiedSince));
        }

        if (!showKK) {
            whereExpr = QuerydslUtils.and(whereExpr, hakukohde.hakukohdeNimi.isNotNull());
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
        whereExpr = whereExpr.and(hakukohde.tila.notIn(poistettuTila));

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
    
    @Override
    public void merge(Hakukohde hk) {
	    getEntityManager().merge(hk);	
    }
    
    @Override
    public boolean removeHakuKohdeLiiteById(String id) {
    	return getEntityManager().createQuery("delete from "+HakukohdeLiite.class.getName()+" where id = ?").setParameter(1, Long.parseLong(id)).executeUpdate() != 0;
    }

    public void updateTilat(TarjontaTila toTila, List<String> oidit, Date updateDate, String userOid) {
        final BooleanExpression qHakukohde = QHakukohde.hakukohde.oid.in(oidit);

        JPAUpdateClause hakukohdeUpdate = new JPAUpdateClause(getEntityManager(), QHakukohde.hakukohde);
        hakukohdeUpdate.where(qHakukohde)
                .set(QHakukohde.hakukohde.tila, toTila)
                .set(QHakukohde.hakukohde.lastUpdateDate, updateDate)
                .set(QHakukohde.hakukohde.lastUpdatedByOid, userOid);
        hakukohdeUpdate.execute();
    }

    public List<Long> searchHakukohteetByHakuOid(final Collection<String> hakuOids, final TarjontaTila... requiredStatus) {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        final BooleanExpression criteria = hakukohde.haku.oid.in(hakuOids).and(hakukohde.tila.in(requiredStatus));

        return from(hakukohde).where(criteria).distinct().list(QHakukohde.hakukohde.id);
    }

    @Override
    public List<Long> findIdsByoids(Collection<String> oids) {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        final BooleanExpression criteria = hakukohde.oid.in(oids);
        return from(hakukohde).where(criteria).distinct().list(QHakukohde.hakukohde.id);
    }

    @Override
    public void safeDelete(final String hakukohdeOid, final String userOid) {
        Preconditions.checkNotNull(hakukohdeOid, "Hakukohde OID string object cannot be null.");
        List<String> oids = Lists.<String>newArrayList();
        oids.add(hakukohdeOid);
        Hakukohde findByOid = findHakukohdeByOid(hakukohdeOid);
        Preconditions.checkArgument(findByOid != null, "Delete failed, entity not found.");
        findByOid.setTila(TarjontaTila.POISTETTU);
        findByOid.setLastUpdatedByOid(userOid);
    }

}
