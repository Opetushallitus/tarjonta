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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.expr.BooleanExpression;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils;
import static fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils.and;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 */
@Repository
public class KoulutusmoduuliToteutusDAOImpl extends AbstractJpaDAOImpl<KoulutusmoduuliToteutus, Long> implements KoulutusmoduuliToteutusDAO {

    private static final Logger log = LoggerFactory.getLogger(KoulutusmoduuliToteutusDAOImpl.class);

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

    /*
     This is done in JPQL because querydsl seems to have a bug with querying element collections. Even version 2.6 does not seem to work
     */
    @Override
    public List<KoulutusmoduuliToteutus> findKoulutusModuuliWithPohjakoulutusAndTarjoaja(String tarjoaja, String pohjakoulutus, String koulutusluokitus, String koulutusohjelma,
            List<String> opetuskielis, List<String> koulutuslajis) {
        if (opetuskielis != null && opetuskielis.size() > 0 && koulutuslajis != null && koulutuslajis.size() > 0) {
            String query = "SELECT komoto FROM KoulutusmoduuliToteutus komoto, Koulutusmoduuli komo, IN (komoto.opetuskielis) o, IN(komoto.koulutuslajis) k WHERE komoto.koulutusmoduuli = komo AND "
                    + "komoto.pohjakoulutusvaatimus = :pkv AND komoto.tarjoaja = :tarjoaja AND komo.koulutusKoodi = :koulutuskoodi AND komo.koulutusohjelmaKoodi = :koulutusohjelmaKoodi AND o.koodiUri IN (:opetuskielis) AND k.koodiUri IN (:koulutuslajis)";

            return getEntityManager()
                    .createQuery(query)
                    .setParameter("pkv", pohjakoulutus.trim())
                    .setParameter("tarjoaja", tarjoaja.trim())
                    .setParameter("koulutuskoodi", koulutusluokitus.trim())
                    .setParameter("koulutusohjelmaKoodi", koulutusohjelma.trim())
                    .setParameter("opetuskielis", opetuskielis)
                    .setParameter("koulutuslajis", koulutuslajis)
                    .getResultList();

        } else {
            log.info("Koulutuslajis and opetuskielis was null!!!");
            return null;
        }

    }

    @Override
    public List<KoulutusmoduuliToteutus> findKoulutusModuulisWithHakukohdesByOids(List<String> komotoOids) {
        QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        QHakukohde qHakukohde = QHakukohde.hakukohde;

        return from(qHakukohde, qKomoto)
                .join(qKomoto.hakukohdes, qHakukohde)
                .where(qKomoto.oid.in(komotoOids))
                .list(qKomoto);
    }

    @Override
    public KoulutusmoduuliToteutus findKomotoByOid(String oid) {
        QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        QKoulutusmoduuli qKomo = QKoulutusmoduuli.koulutusmoduuli;

        KoulutusmoduuliToteutus komoto = from(qKomoto, qKomo)
                .leftJoin(qKomoto.koulutusmoduuli, qKomo)
                .where(qKomoto.oid.eq(oid.trim()))
                .singleResult(qKomoto);

        if (komoto != null) {
            komoto.getHakukohdes();
        } else {
            log.warn("No KoulutusmoduuliToteutus found by OID '{}'.", oid);
        }

        return komoto;
    }

    @Override
    public List<KoulutusmoduuliToteutus> findKoulutusModuuliToteutusesByOids(List<String> oids) {
        QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        //Added to enable ordering
        QKoulutusmoduuli komo = QKoulutusmoduuli.koulutusmoduuli;
        return from(komoto)
                .where(komoto.oid.in(oids))
                .join(komoto.koulutusmoduuli, komo)
                .orderBy(komo.koulutusKoodi.asc())
                .list(komoto);

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
    public List<KoulutusmoduuliToteutus> findByCriteria(List<String> tarjoajaOids, String matchNimi, int koulutusAlkuVuosi, List<Integer> koulutusAlkuKuukaudet) {

        QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        BooleanExpression criteria = null;

        if (matchNimi != null) {

            QKoulutusmoduuli komo = QKoulutusmoduuli.koulutusmoduuli;
            QTekstiKaannos nimiTeksti = QTekstiKaannos.tekstiKaannos;

            JPASubQuery subQuery = new JPASubQuery().from(komo).
                    join(komo.nimi.tekstis, nimiTeksti).
                    where(nimiTeksti.arvo.toLowerCase().contains(matchNimi.toLowerCase()));

            criteria = komoto.koulutusmoduuli.in(subQuery.list(komo));
        }

        if (!tarjoajaOids.isEmpty()) {
            criteria = and(criteria, komoto.tarjoaja.in(tarjoajaOids));
        }

        if (koulutusAlkuVuosi > 0) {
            criteria = and(criteria, komoto.koulutuksenAlkamisPvm.isNotNull()).and(komoto.koulutuksenAlkamisPvm.year().isNotNull()).and(komoto.koulutuksenAlkamisPvm.year().eq(koulutusAlkuVuosi));
        }

        if (!koulutusAlkuKuukaudet.isEmpty()) {
            criteria = and(criteria, komoto.koulutuksenAlkamisPvm.isNotNull()).and(komoto.koulutuksenAlkamisPvm.month().isNotNull()).and(komoto.koulutuksenAlkamisPvm.month().in(koulutusAlkuKuukaudet));
        }

        List<KoulutusmoduuliToteutus> komotos;
        if (criteria == null) {
            komotos = from(komoto).
                    list(komoto);
        } else {
            komotos = from(komoto).
                    where(criteria).
                    list(komoto);
        }

        return filterTutkintos(komotos);
    }

    @Override
    public List<KoulutusmoduuliToteutus> findKomotosByKomoTarjoajaPohjakoulutus(
            Koulutusmoduuli parentKomo, String tarjoaja, String pohjakoulutusvaatimusUri) {

        if (parentKomo == null) {
            return null;
        }

        QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        List<KoulutusmoduuliToteutus> komotoRes = null;
        try {
            BooleanExpression criteria = komoto.koulutusmoduuli.oid.eq(parentKomo.getOid());
            if (tarjoaja != null) {
                criteria = criteria.and(komoto.tarjoaja.eq(tarjoaja));
            }
            if (pohjakoulutusvaatimusUri != null) {
                criteria = criteria.and(komoto.pohjakoulutusvaatimus.eq(pohjakoulutusvaatimusUri));
            }

            komotoRes = from(komoto).
                    join(komoto.koulutusmoduuli).fetch().
                    where(criteria).list(komoto);
        } catch (Exception ex) {
            log.error("findKomotosByKomoTarjoajaPohjakoulutus() - Exception: {}", ex);
        }
        return komotoRes;
    }

    private List<KoulutusmoduuliToteutus> filterTutkintos(List<KoulutusmoduuliToteutus> komotos) {
        List<KoulutusmoduuliToteutus> result = new ArrayList<KoulutusmoduuliToteutus>();
        for (KoulutusmoduuliToteutus curKomoto : komotos) {
            if (!curKomoto.getKoulutusmoduuli().getModuuliTyyppi().name().equals(KoulutusmoduuliTyyppi.TUTKINTO.name())) {
                result.add(curKomoto);
            }
        }
        return result;
    }

    @Override
    public List<String> findOIDsBy(TarjontaTila tila, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedAfter) {

        QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

        BooleanExpression whereExpr = null;

        if (tila != null) {
            whereExpr = QuerydslUtils.and(whereExpr, komoto.tila.eq(tila));
        }
        if (lastModifiedBefore != null) {
            whereExpr = QuerydslUtils.and(whereExpr, komoto.updated.before(lastModifiedBefore));
        }
        if (lastModifiedAfter != null) {
            whereExpr = QuerydslUtils.and(whereExpr, komoto.updated.after(lastModifiedAfter));
        }

        JPAQuery q = from(komoto);
        if (whereExpr != null) {
            q = q.where(whereExpr);
        }

        if (count > 0) {
            q = q.limit(count);
        }

        if (startIndex > 0) {
            q.offset(startIndex);
        }

        return q.list(komoto.oid);
    }

    @Override
    public List<String> findOidsByHakukohdeId(long hakukohdeId) {
        //TODO use constants
        Query q = getEntityManager().createQuery("select k.oid from KoulutusmoduuliToteutus k JOIN k.hakukohdes hk where hk.id= :hakukohdeId").setParameter("hakukohdeId", hakukohdeId);
        List<String> results = (List<String>) q.getResultList();
        return results;
    }

    @Override
    public List<String> findOidsByKomoOid(String oid, int count, int startIndex) {

        QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        BooleanExpression whereExpr = komoto.koulutusmoduuli.oid.eq(oid);

        JPAQuery q = from(komoto);
        q = q.where(whereExpr);

        if (count > 0) {
            q = q.limit(count);
        }
        if (startIndex > 0) {
            q.offset(startIndex);
        }

        return q.list(komoto.oid);
    }

    @Override
    public void update(KoulutusmoduuliToteutus entity) {
        detach(entity); //optimistic locking requires detach + reload so that the entity exists in hibernate session before merging
        Preconditions.checkNotNull(getEntityManager().find(KoulutusmoduuliToteutus.class, entity.getId()));
        super.update(entity);
    }

    @Override
    public BinaryData findKuvaByKomotoOidAndKieliUri(final String komotoOid, final String kieliUri) {
        QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        QBinaryData qBinaryData = QBinaryData.binaryData;
        return from(qKomoto).
                join(qKomoto.kuvat, qBinaryData).
                where(qKomoto.oid.eq(komotoOid).and(qKomoto.kuvat.containsKey(kieliUri))).
                singleResult(qBinaryData);
    }
    
    /**
     * Search LOI entities by application option OIDs.
     *
     * @param hakukohdeIds
     * @param requiredStatus
     * @return Map<koulutusmoduuliId, koulutusmoduuliOid>
     */
    public List<Long> searchKomotoIdsByHakukohdesId(final Collection<Long> hakukohdeIds, final TarjontaTila... requiredStatus) {

        boolean hasStatus =requiredStatus!=null && requiredStatus.length>0 && requiredStatus[0]!=null;  
        
        Query q = getEntityManager().createQuery(hasStatus?
                "SELECT ktm.id FROM Hakukohde hk, IN(hk.koulutusmoduuliToteutuses) ktm "
                + "WHERE hk.id IN(:hakukohdeIds) AND ktm.tila IN(:requiredStatus)":
                    
                    "SELECT ktm.id FROM Hakukohde hk, IN(hk.koulutusmoduuliToteutuses) ktm "
                    + "WHERE hk.id IN(:hakukohdeIds)"
                );
        q.setParameter("hakukohdeIds", hakukohdeIds);
        log.debug("searching for komotos with status:" + Lists.newArrayList(requiredStatus));

        if(hasStatus) { 
            q.setParameter("requiredStatus", Lists.newArrayList(requiredStatus));
        }

        List<Long> list = (List<Long>) q.getResultList();

        return list;
    }
    
    @Override
    public List<String> searchKomotoOIDsByHakukohdesId(
            Collection<Long> hakukohdeIds, TarjontaTila... requiredStatus) {
        boolean hasStatus =requiredStatus!=null && requiredStatus.length>0 && requiredStatus[0]!=null;  
        
        Query q = getEntityManager().createQuery(hasStatus?
                "SELECT ktm.oid FROM Hakukohde hk, IN(hk.koulutusmoduuliToteutuses) ktm "
                + "WHERE hk.id IN(:hakukohdeIds) AND ktm.tila IN(:requiredStatus)":
                    
                    "SELECT ktm.oid FROM Hakukohde hk, IN(hk.koulutusmoduuliToteutuses) ktm "
                    + "WHERE hk.id IN(:hakukohdeIds)"
                );
        q.setParameter("hakukohdeIds", hakukohdeIds);
        log.debug("searching for komotos with status:" + Lists.newArrayList(requiredStatus));

        if(hasStatus) { 
            q.setParameter("requiredStatus", Lists.newArrayList(requiredStatus));
        }

        List<String> list = (List<String>) q.getResultList();

        return list;
    }
}
