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
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.expr.BooleanExpression;

import com.mysema.query.types.path.StringPath;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import static fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils.and;
import fi.vm.sade.tarjonta.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
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
    public List<KoulutusmoduuliToteutus> findKoulutusModuuliWithPohjakoulutusAndTarjoaja(String tarjoaja, String pohjakoulutus, String koulutusluokitus,String koulutusohjelma,
                                                                                         List<String> opetuskielis, List<String> koulutuslajis) {
       if (opetuskielis != null && opetuskielis.size() > 0 && koulutuslajis != null && koulutuslajis.size() > 0 ) {
        String query = "SELECT komoto FROM KoulutusmoduuliToteutus komoto, Koulutusmoduuli komo, IN (komoto.opetuskielis) o, IN(komoto.koulutuslajis) k WHERE komoto.koulutusmoduuli = komo AND " +
                "komoto.pohjakoulutusvaatimus = :pkv AND komoto.tarjoaja = :tarjoaja AND komo.koulutusKoodi = :koulutuskoodi AND komo.koulutusohjelmaKoodi = :koulutusohjelmaKoodi AND o.koodiUri IN (:opetuskielis) AND k.koodiUri IN (:koulutuslajis)";


        return getEntityManager()
                .createQuery(query)
                .setParameter("pkv",pohjakoulutus.trim())
                .setParameter("tarjoaja",tarjoaja.trim())
                .setParameter("koulutuskoodi",koulutusluokitus.trim())
                .setParameter("koulutusohjelmaKoodi",koulutusohjelma.trim())
                .setParameter("opetuskielis",opetuskielis)
                .setParameter("koulutuslajis",koulutuslajis)
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

        return from(qHakukohde,qKomoto)
                .join(qKomoto.hakukohdes,qHakukohde)
                .where(qKomoto.oid.in(komotoOids))
                .list(qKomoto);
    }

    @Override
    public KoulutusmoduuliToteutus findKomotoByOid(String oid) {
         QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
         QKoulutusmoduuli qKomo = QKoulutusmoduuli.koulutusmoduuli;
         
         KoulutusmoduuliToteutus komoto = from(qKomoto,qKomo)
                 .leftJoin(qKomoto.koulutusmoduuli,qKomo)
                 .where(qKomoto.oid.eq(oid.trim()))
                 .singleResult(qKomoto);
         
         komoto.getHakukohdes();
         
        return  komoto;

    }

    @Override
    public List<KoulutusmoduuliToteutus> findKoulutusModuuliToteutusesByOids(List<String> oids) {
         QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
         //Added to enable ordering
         QKoulutusmoduuli komo = QKoulutusmoduuli.koulutusmoduuli;
         return from(komoto)
                 .where(komoto.oid.in(oids))
                 .join(komoto.koulutusmoduuli,komo)
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

        List<KoulutusmoduuliToteutus> komotos = from(komoto).
                where(criteria).
                list(komoto);
        
        return filterTutkintos(komotos); 
    }
    

    @Override
    public List<KoulutusmoduuliToteutus> findKomotosByKomoAndtarjoaja(
            Koulutusmoduuli parentKomo, String tarjoaja) {
        QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        List<KoulutusmoduuliToteutus> komotoRes = null;
        try {
            komotoRes = from(komoto).
                join(komoto.koulutusmoduuli).fetch().
                where(komoto.koulutusmoduuli.oid.eq(parentKomo.getOid()).and(komoto.tarjoaja.eq(tarjoaja))).list(komoto);
        } catch (Exception ex) {
            log.debug("Exception: " + ex.getMessage());
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

}

