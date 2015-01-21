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
import com.google.common.collect.ImmutableMap;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.model.searchParams.ListHakuSearchParam;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria.Field;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria.Match;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.*;

/**
 * @author Antti Salonen
 */
@Repository
public class HakuDAOImpl extends AbstractJpaDAOImpl<Haku, Long> implements HakuDAO {

    @Override
    public List<Haku> findAll() {
        // Eager fetch collections for significant total speedup
        String q = ("SELECT DISTINCT h FROM Haku h " +
                "LEFT JOIN FETCH h.nimi AS nimi " +
                "LEFT JOIN FETCH nimi.tekstis " +
                "LEFT JOIN FETCH h.hakuaikas as ha " +
                "LEFT JOIN FETCH ha.nimi AS hanimi " +
                "LEFT JOIN FETCH hanimi.tekstis " +
                "LEFT JOIN FETCH h.sisaltyvatHaut ");
        Query query = getEntityManager().createQuery(q);
        return query.getResultList();
    }

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
    public List<Haku> findByOids(List<String> oids) {
        QHaku qHaku = QHaku.haku;

        return from(qHaku).join(qHaku.nimi, QMonikielinenTeksti.monikielinenTeksti).fetch()
                .join(QMonikielinenTeksti.monikielinenTeksti.tekstis, QTekstiKaannos.tekstiKaannos).fetch().where(qHaku.oid.in(oids)).distinct()
                .list(qHaku);
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
            whereExpr = QuerydslUtils.and(whereExpr, haku.tila.eq(param.getTila()));
        }

        if (param.getKoulutuksenAlkamisKausi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, haku.koulutuksenAlkamiskausiUri.eq(param.getKoulutuksenAlkamisKausi()));
        }

        if (param.getKoulutuksenAlkamisVuosi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, haku.koulutuksenAlkamisVuosi.eq(param.getKoulutuksenAlkamisVuosi()));

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

        entity.setLastUpdateDate(new Date());
        super.update(entity);
    }

    //mappi enumista sarakkeeseen
    private final Map<Field, ComparableExpressionBase> mapping = new ImmutableMap.Builder<Field, ComparableExpressionBase>()
            .put(Field.HAKUKAUSI, QHaku.haku.hakukausiUri)
            .put(Field.HAKUVUOSI, QHaku.haku.hakukausiVuosi)
            .put(Field.KOULUTUKSEN_ALKAMISKAUSI, QHaku.haku.koulutuksenAlkamiskausiUri)
            .put(Field.KOULUTUKSEN_ALKAMISVUOSI, QHaku.haku.koulutuksenAlkamisVuosi)
            .put(Field.TARJOAJAOID, QHaku.haku.tarjoajaOidString)
            .put(Field.HAKUTAPA, QHaku.haku.hakutapaUri)
            .put(Field.HAKUTYYPPI, QHaku.haku.hakutyyppiUri)
            .put(Field.KOHDEJOUKKO, QHaku.haku.kohdejoukkoUri)
            .put(Field.HAKUSANA, QTekstiKaannos.tekstiKaannos.arvo)
            .put(Field.TILA, QHaku.haku.tila)
            .build();

    private final Map<Match, String> matchType = new ImmutableMap.Builder<Match, String>()
            .put(Match.MUST_MATCH, "=")
            .put(Match.MUST_NOT, "!=")
            .put(Match.LIKE, " like ")
            .build();

    public <T> List<T> findByCriteria(int count, int startIndex,
                                      List<HakuSearchCriteria> criteriaList, boolean oidOnly) {

        String q = "SELECT distinct " + (oidOnly ? "haku.oid" : "haku") + " from Haku haku join haku.nimi nimi join nimi.tekstis tekstiKaannos" + (criteriaList.size() > 0 ? " where " : "");

        int orOffset = 0;
        ArrayList<Object> paramList = new ArrayList<Object>();

        for (int i = 0; i < criteriaList.size(); i++) {
            HakuSearchCriteria criteria = criteriaList.get(i);
            String field = mapping.get(criteria.getField()).toString();
            if (field == null) {
                //no mapping available
                throw new IllegalArgumentException("No mapping found for criteria name:" + criteria.getField());
            }
            String template = (criteria.getField() == Field.HAKUSANA ? "LOWER(%s)" : "%s");

            if (criteria.getMatch() == Match.LIKE_OR) {
                q += "(1 = 2";
                String[] orValues = criteria.getValue().toString().split(",");
                orOffset--; // Decrement -1, so that the first loop isn't counted
                for (String val : orValues) {
                    orOffset++;
                    q += " OR " + String.format(template, field) + " LIKE ?" + (i + 1 + orOffset);
                    paramList.add("%" + val + "%");
                }
                q += ")";
            } else {
                if (fieldHasKoodiUriValue(criteria)) {
                    q += String.format(template, field) + matchType.get(Match.LIKE) + "?" + (i + 1 + orOffset);
                    if (valueContainsKoodiVersion(criteria)) {
                        paramList.add(criteria.getValue());
                    } else {
                        paramList.add(criteria.getValue() + "#%");
                    }
                } else {
                    q += String.format(template, field) + matchType.get(criteria.getMatch()) + "?" + (i + 1 + orOffset);
                    paramList.add(criteria.getField() == Field.HAKUSANA ? criteria.getValue().toString().toLowerCase() : criteria.getValue());
                }
            }
            if (i < criteriaList.size() - 1) {
                q += " AND ";
            }
        }

        final Query query = getEntityManager().createQuery(q);
        for (int i = 0; i < paramList.size(); i++) {
            query.setParameter(i + 1, paramList.get(i));
        }

        if (count > 0) {
            query.setMaxResults(count);
        }

        if (startIndex > 0) {
            query.setFirstResult(startIndex);
        }

        return query.getResultList();
    }

    private boolean valueContainsKoodiVersion(HakuSearchCriteria criteria) {
        return criteria.getValue().toString().contains("#");
    }

    private boolean fieldHasKoodiUriValue(HakuSearchCriteria criteria) {
        return Field.HAKUTAPA.equals(criteria.getField()) ||
                Field.HAKUKAUSI.equals(criteria.getField()) ||
                Field.KOULUTUKSEN_ALKAMISKAUSI.equals(criteria.getField()) ||
                Field.HAKUTYYPPI.equals(criteria.getField()) ||
                Field.KOHDEJOUKKO.equals(criteria.getField());
    }

    @Override
    public List<String> findOIDByCriteria(int count, int startIndex, List<HakuSearchCriteria> criteriaList) {
        return findByCriteria(count, startIndex, criteriaList, true);
    }

    @Override
    public List<Haku> findHakuByCriteria(int count, int startIndex, List<HakuSearchCriteria> criteriaList) {
        return findByCriteria(count, startIndex, criteriaList, false);
    }

    @Override
    public void safeDelete(final String hakuOid, final String userOid) {
        Preconditions.checkNotNull(hakuOid, "Haku OID cannot be null.");
        Haku haku = findByOid(hakuOid);
        Preconditions.checkArgument(haku != null, "Delete failed, entity not found.");
        haku.setTila(TarjontaTila.POISTETTU);
        haku.setLastUpdatedByOid(userOid);
        haku.setLastUpdateDate(new Date());
    }

    public Set<String> findOrganisaatioOidsFromHakukohteetByHakuOid(String hakuOid) {
        Set<String> firstSet = getMontaTarjoajaaOrganisaatioOids(hakuOid);
        Set<String> secondSet = getKomotoTarjoajaOrganisaatioOids(hakuOid);
        firstSet.addAll(secondSet);
        return firstSet;
    }

    @Override
    public List<String> findOrganisaatioryhmaOids(String hakuOid) {
        QHakukohde qHakukohde = QHakukohde.hakukohde;
        QRyhmaliitos qRyhmaliitos = QRyhmaliitos.ryhmaliitos;
        QHaku qHaku = QHaku.haku;

        return from(qHaku, qHakukohde, qRyhmaliitos)
                .where(qHakukohde.haku.eq(qHaku)
                        .and(qHaku.oid.eq(hakuOid))
                        .and(qRyhmaliitos.hakukohde.eq(qHakukohde)))
                .distinct()
                .list(qRyhmaliitos.ryhmaOid);
    }

    private Set<String> getKomotoTarjoajaOrganisaatioOids(String hakuOid) {
        String hql = "select komoto.tarjoaja " +
                "from Hakukohde hk " +
                "join hk.koulutusmoduuliToteutuses as komoto " +
                "where hk.haku.oid = :hakuOid";
        Query query = getEntityManager().createQuery(hql);
        query.setParameter("hakuOid", hakuOid);
        return new HashSet<String>(query.getResultList());
    }

    private Set<String> getMontaTarjoajaaOrganisaatioOids(String hakuOid) {
        String hql = "select elements(koulutusmoduuliToteutusTarjoajatiedot.tarjoajaOids) " +
                "from Hakukohde hk " +
                "join hk.koulutusmoduuliToteutusTarjoajatiedot koulutusmoduuliToteutusTarjoajatiedot " +
                "where hk.haku.oid = :hakuOid";
        Query query = getEntityManager().createQuery(hql);
        query.setParameter("hakuOid", hakuOid);
        return new HashSet<String>(query.getResultList());
    }
}
