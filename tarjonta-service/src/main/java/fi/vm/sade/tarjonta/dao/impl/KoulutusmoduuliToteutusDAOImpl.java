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

import static fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils.and;
import static fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO.stripVersionFromKoodiUri;
import static fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysema.commons.lang.Pair;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import fi.vm.sade.tarjonta.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusIdentification;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import jakarta.persistence.Query;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/** */
@Repository
public class KoulutusmoduuliToteutusDAOImpl
    extends AbstractJpaDAOImpl<KoulutusmoduuliToteutus, Long>
    implements KoulutusmoduuliToteutusDAO {

  private static final Logger log = LoggerFactory.getLogger(KoulutusmoduuliToteutusDAOImpl.class);

  private KoulutusmoduuliToteutus getFirstFromList(List<KoulutusmoduuliToteutus> list) {
    if (list.isEmpty()) {
      return null;
    } else if (list.size() == 1) {
      return list.get(0);
    } else {
      throw new IllegalStateException("multiple results found!");
    }
  }

  @Override
  public KoulutusmoduuliToteutus findByOid(String oid) {
    return getFirstFromList(findBy(KoulutusmoduuliToteutus.OID_COLUMN_NAME, oid));
  }

  @Override
  public KoulutusmoduuliToteutus findByUniqueExternalId(String uniqueExternalId) {
    return getFirstFromList(
        findBy(KoulutusmoduuliToteutus.UNIQUE_EXTERNAL_ID_COLUMN_NAME, uniqueExternalId));
  }

  @Override
  public KoulutusmoduuliToteutus findKomotoByKoulutusId(KoulutusIdentification id) {
    if (!StringUtils.isBlank(id.getOid())) {
      return getFirstFromList(findBy(BaseKoulutusmoduuli.OID_COLUMN_NAME, id.getOid()));
    } else if (!StringUtils.isBlank(id.getUniqueExternalId())) {
      return findByUniqueExternalId(id.getUniqueExternalId());
    }
    return null;
  }

  @Override
  public KoulutusmoduuliToteutus findFirstByKomoOid(String komoOid) {
    QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    QKoulutusmoduuli qKomo = QKoulutusmoduuli.koulutusmoduuli;
    List<KoulutusmoduuliToteutus> list =
        queryFactory()
            .selectFrom(qKomoto)
            .join(qKomoto.koulutusmoduuli, qKomo)
            .where(qKomo.oid.eq(komoOid))
            .distinct()
            .fetch();
    if (list.isEmpty()) {
      return null;
    } else {
      return list.get(0);
    }
  }

  @Override
  public List<KoulutusmoduuliToteutus> findSiblingKomotos(KoulutusmoduuliToteutus komoto) {
    // Sisarkoulutukset palautetaan vain ammatillisille perustutkinnoille
    Set<ToteutustyyppiEnum> siblingToteutustyyppis = new HashSet<ToteutustyyppiEnum>();

    siblingToteutustyyppis.add(AMMATILLINEN_PERUSTUTKINTO);
    siblingToteutustyyppis.add(AMMATILLINEN_PERUSTUTKINTO_ALK_2018);
    siblingToteutustyyppis.add(AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA);

    if (!siblingToteutustyyppis.contains(komoto.getToteutustyyppi())) {
      return null;
    }

    QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

    return queryFactory()
        .selectFrom(qKomoto)
        .where(
            qKomoto
                .koulutusUri
                .startsWith(komoto.getKoodiUriWithoutVersion(komoto.getKoulutusUri()) + "#")
                .and(qKomoto.tarjoaja.eq(komoto.getTarjoaja()))
                .and(qKomoto.toteutustyyppi.eq(komoto.getToteutustyyppi()))
                .and(
                    qKomoto.alkamiskausiUri.startsWith(
                        komoto.getKoodiUriWithoutVersion(komoto.getAlkamiskausiUri()) + "#"))
                .and(qKomoto.alkamisVuosi.eq(komoto.getAlkamisVuosi()))
                .and(qKomoto.oid.ne(komoto.getOid()))
                .and(qKomoto.tila.ne(TarjontaTila.POISTETTU)))
        .fetch();
  }

  @Override
  public List<KoulutusmoduuliToteutus> findSameKoulutus(
      String tarjoaja,
      String pohjakoulutus,
      String koulutuskoodi,
      String koulutusohjelma,
      List<String> opetuskielis,
      List<String> koulutuslajis) {

    if (opetuskielis != null
        && opetuskielis.size() > 0
        && koulutuslajis != null
        && koulutuslajis.size() > 0) {
      QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

      pohjakoulutus = stripVersionFromKoodiUri(pohjakoulutus) + "#";
      koulutuskoodi = stripVersionFromKoodiUri(koulutuskoodi) + "#";

      StringExpression koulutuslajiW = qKomoto.koulutuslajis.any().koodiUri.append("#");
      StringExpression opetuskieliW = qKomoto.opetuskielis.any().koodiUri.append("#");

      BooleanExpression where =
          qKomoto
              .pohjakoulutusvaatimusUri
              .append("#")
              .startsWith(pohjakoulutus)
              .and(qKomoto.tarjoaja.eq(tarjoaja))
              .and(qKomoto.tila.notIn(TarjontaTila.PERUTTU, TarjontaTila.POISTETTU))
              .and(qKomoto.koulutusUri.append("#").startsWith(koulutuskoodi))
              .and(
                  koulutuslajiW
                      .substring(0, koulutuslajiW.indexOf("#"))
                      .in(matchWithoutVersion(koulutuslajis)))
              .and(
                  opetuskieliW
                      .substring(0, opetuskieliW.indexOf("#"))
                      .in(matchWithoutVersion(opetuskielis)));

      if (!StringUtils.isBlank(koulutusohjelma)) {
        koulutusohjelma = stripVersionFromKoodiUri(koulutusohjelma) + "#";
        where =
            where.and(
                qKomoto
                    .koulutusohjelmaUri
                    .append("#")
                    .startsWith(koulutusohjelma)
                    .or(qKomoto.osaamisalaUri.append("#").startsWith(koulutusohjelma)));
      }

      return queryFactory().selectFrom(qKomoto).where(where).fetch();
    } else {
      log.info("Koulutuslajis and opetuskielis was null!!!");
      return new ArrayList<KoulutusmoduuliToteutus>();
    }
  }

  private static List<String> matchWithoutVersion(List<String> koodiList) {
    return FluentIterable.from(koodiList)
        .transform(koodi -> stripVersionFromKoodiUri(koodi))
        .toList();
  }

  @Override
  public List<KoulutusmoduuliToteutus> findKoulutusModuulisWithHakukohdesByOids(
      List<String> komotoOids) {
    QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    QHakukohde qHakukohde = QHakukohde.hakukohde;

    return queryFactory()
        .select(qKomoto)
        .from(qHakukohde, qKomoto)
        .join(qKomoto.hakukohdes, qHakukohde)
        .where(qKomoto.oid.in(komotoOids))
        .fetch();
  }

  @Override
  public KoulutusmoduuliToteutus findKomotoByOid(String oid) {
    QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

    KoulutusmoduuliToteutus komoto =
        queryFactory().selectFrom(qKomoto).where(qKomoto.oid.eq(oid.trim())).fetchOne();

    if (komoto == null) {
      log.warn("No KoulutusmoduuliToteutus found by OID '{}'.", oid);
    }

    return komoto;
  }

  @Override
  public List<KoulutusmoduuliToteutus> findKomotosByTarjoajanKoulutusOid(String oid) {
    QKoulutusmoduuliToteutus qKoulutusmoduuliToteutus =
        QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

    BooleanExpression criteria = qKoulutusmoduuliToteutus.tarjoajanKoulutus.oid.eq(oid);
    criteria = and(criteria, qKoulutusmoduuliToteutus.tila.notIn(TarjontaTila.POISTETTU));

    return queryFactory().selectFrom(qKoulutusmoduuliToteutus).where(criteria).fetch();
  }

  @Override
  public List<KoulutusmoduuliToteutus> findKoulutusModuuliToteutusesByOids(List<String> oids) {
    QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    // Added to enable ordering
    QKoulutusmoduuli komo = QKoulutusmoduuli.koulutusmoduuli;
    return queryFactory()
        .selectFrom(komoto)
        .where(komoto.oid.in(oids))
        .join(komoto.koulutusmoduuli, komo)
        .orderBy(komo.koulutusUri.asc())
        .fetch();
  }

  @Override
  public List<KoulutusmoduuliToteutus> findFutureKoulutukset(
      List<ToteutustyyppiEnum> toteutustyyppis, int offset, int limit) {
    QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

    String kausi = IndexDataUtils.parseKausiKoodi(new Date());
    Integer year = IndexDataUtils.parseYearInt(new Date());

    return queryFactory()
        .selectFrom(komoto)
        .where(
            komoto
                .toteutustyyppi
                .in(toteutustyyppis)
                .and(
                    komoto
                        .alkamisVuosi
                        .gt(year)
                        .or(
                            komoto
                                .alkamisVuosi
                                .eq(year)
                                .and(
                                    komoto
                                        .alkamiskausiUri
                                        .eq(kausi)
                                        .or(komoto.alkamiskausiUri.eq(IndexDataUtils.SYKSY_URI)))))
                .and(komoto.tila.notIn(TarjontaTila.POISTETTU, TarjontaTila.PERUTTU)))
        .orderBy(komoto.id.asc())
        .offset(offset)
        .limit(limit)
        .fetch();
  }

  @Override
  public KoulutusmoduuliToteutus findKomotoWithYhteyshenkilosByOid(String oid) {
    Query query =
        getEntityManager()
            .createQuery(
                ""
                    + "SELECT k FROM KoulutusmoduuliToteutus k "
                    + "LEFT JOIN FETCH k.yhteyshenkilos "
                    + "where k.oid=:oid");
    query.setParameter("oid", oid);
    return (KoulutusmoduuliToteutus) query.getSingleResult();
  }

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public List<KoulutusmoduuliToteutus> findByCriteria(
      List<String> tarjoajaOids,
      String matchNimi,
      int koulutusAlkuVuosi,
      List<Integer> koulutusAlkuKuukaudet) {

    QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    BooleanExpression criteria = null;

    if (matchNimi != null) {

      QKoulutusmoduuli komo = QKoulutusmoduuli.koulutusmoduuli;
      QTekstiKaannos nimiTeksti = QTekstiKaannos.tekstiKaannos;

      // JPASubQuery subQuery = new JPASubQuery().from(komo).
      JPQLQuery<Koulutusmoduuli> subQuery =
          JPAExpressions.selectFrom(komo)
              .join(komo.nimi.tekstis, nimiTeksti)
              .where(nimiTeksti.arvo.toLowerCase().contains(matchNimi.toLowerCase()));

      criteria = komoto.koulutusmoduuli.in(subQuery);
    }

    if (!tarjoajaOids.isEmpty()) {
      criteria = and(criteria, komoto.tarjoaja.in(tarjoajaOids));
    }

    if (koulutusAlkuVuosi > 0) {
      criteria =
          and(criteria, komoto.koulutuksenAlkamisPvms.isNotEmpty())
              .and(komoto.koulutuksenAlkamisPvms.any().year().isNotNull())
              .and(komoto.koulutuksenAlkamisPvms.any().year().eq(koulutusAlkuVuosi));
    }

    if (!koulutusAlkuKuukaudet.isEmpty()) {
      criteria =
          and(criteria, komoto.koulutuksenAlkamisPvms.isNotEmpty())
              .and(komoto.koulutuksenAlkamisPvms.any().month().isNotNull())
              .and(komoto.koulutuksenAlkamisPvms.any().month().in(koulutusAlkuKuukaudet));
    }

    List<KoulutusmoduuliToteutus> komotos;
    if (criteria == null) {
      komotos = queryFactory().selectFrom(komoto).fetch();
    } else {
      komotos = queryFactory().selectFrom(komoto).where(criteria).fetch();
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
        criteria = criteria.and(komoto.pohjakoulutusvaatimusUri.eq(pohjakoulutusvaatimusUri));
      }

      komotoRes =
          queryFactory().selectFrom(komoto).join(komoto.koulutusmoduuli).where(criteria).fetch();
    } catch (Exception ex) {
      log.error("findKomotosByKomoTarjoajaPohjakoulutus() - Exception: {}", ex);
    }
    return komotoRes;
  }

  private List<KoulutusmoduuliToteutus> filterTutkintos(List<KoulutusmoduuliToteutus> komotos) {
    List<KoulutusmoduuliToteutus> result = new ArrayList<KoulutusmoduuliToteutus>();
    for (KoulutusmoduuliToteutus curKomoto : komotos) {
      if (!curKomoto
          .getKoulutusmoduuli()
          .getModuuliTyyppi()
          .name()
          .equals(KoulutusmoduuliTyyppi.TUTKINTO.name())) {
        result.add(curKomoto);
      }
    }
    return result;
  }

  @Override
  public List<String> findOIDsBy(
      TarjontaTila tila,
      int count,
      int startIndex,
      Date lastModifiedBefore,
      Date lastModifiedAfter) {

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

    whereExpr =
        QuerydslUtils.and(whereExpr, komoto.toteutustyyppi.notIn(getValmistavatToteutustyypit()));

    JPAQuery<String> q = queryFactory().select(komoto.oid).from(komoto);
    if (whereExpr != null) {
      q = q.where(whereExpr);
    }

    if (count > 0) {
      q = q.limit(count);
    }

    if (startIndex > 0) {
      q.offset(startIndex);
    }

    return q.fetch();
  }

  @Override
  public List<String> findOidsByHakukohdeId(long hakukohdeId) {
    // TODO use constants
    Query q =
        getEntityManager()
            .createQuery(
                "select k.oid from KoulutusmoduuliToteutus k JOIN k.hakukohdes hk where hk.id= :hakukohdeId")
            .setParameter("hakukohdeId", hakukohdeId);
    List<String> results = (List<String>) q.getResultList();
    return results;
  }

  @Override
  public List<String> findOidsByKomoOid(String oid, int count, int startIndex) {

    QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    BooleanExpression whereExpr = komoto.koulutusmoduuli.oid.eq(oid);

    JPAQuery<String> q = queryFactory().select(komoto.oid).from(komoto);
    q = q.where(whereExpr);

    if (count > 0) {
      q = q.limit(count);
    }
    if (startIndex > 0) {
      q.offset(startIndex);
    }

    return q.fetch();
  }

  @Override
  public List<String> findOidsByKomoOids(Set<String> komoOids) {
    QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

    return queryFactory()
        .select(komoto.oid)
        .from(komoto)
        .where(komoto.koulutusmoduuli.oid.in(komoOids))
        .fetch();
  }

  @Override
  public void update(KoulutusmoduuliToteutus entity) {
    detach(entity); // optimistic locking requires detach + reload so that the entity exists in
    // hibernate session before merging
    Preconditions.checkNotNull(
        getEntityManager().find(KoulutusmoduuliToteutus.class, entity.getId()));
    super.update(entity);
  }

  @Override
  public BinaryData findKuvaByKomotoOidAndKieliUri(final String komotoOid, final String kieliUri) {
    QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    QBinaryData qBinaryData = QBinaryData.binaryData;

    return queryFactory()
        .select(qBinaryData)
        .from(qKomoto)
        .join(qKomoto.kuvat, qBinaryData)
        .where(qKomoto.oid.eq(komotoOid).and(qKomoto.kuvat.get(kieliUri).eq(qBinaryData)))
        .fetchOne();
  }

  @Override
  public Map<String, BinaryData> findAllImagesByKomotoOid(final String komotoOid) {
    QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    QBinaryData qBinaryData = QBinaryData.binaryData;
    KoulutusmoduuliToteutus t =
        queryFactory()
            .selectFrom(qKomoto)
            .leftJoin(qKomoto.kuvat, qBinaryData)
            .where(qKomoto.oid.eq(komotoOid))
            .fetchOne();

    if (t == null) { // no results
      return Maps.<String, BinaryData>newHashMap();
    }

    return t.getKuvat();
  }

  /**
   * Search LOI entities by application option OIDs.
   *
   * @param hakukohdeIds
   * @param requiredStatus
   * @return Map<koulutusmoduuliId, koulutusmoduuliOid>
   */
  public List<Long> searchKomotoIdsByHakukohdesId(
      final Collection<Long> hakukohdeIds, final TarjontaTila... requiredStatus) {

    boolean hasStatus =
        requiredStatus != null && requiredStatus.length > 0 && requiredStatus[0] != null;

    Query q =
        getEntityManager()
            .createQuery(
                hasStatus
                    ? "SELECT ktm.id FROM Hakukohde hk, IN(hk.koulutusmoduuliToteutuses) ktm "
                        + "WHERE hk.id IN(:hakukohdeIds) AND ktm.tila IN(:requiredStatus)"
                    : "SELECT ktm.id FROM Hakukohde hk, IN(hk.koulutusmoduuliToteutuses) ktm "
                        + "WHERE hk.id IN(:hakukohdeIds)");
    q.setParameter("hakukohdeIds", hakukohdeIds);
    log.debug("searching for komotos with status:" + Lists.newArrayList(requiredStatus));

    if (hasStatus) {
      q.setParameter("requiredStatus", Lists.newArrayList(requiredStatus));
    }

    List<Long> list = (List<Long>) q.getResultList();

    return list;
  }

  @Override
  public List<String> searchKomotoOIDsByHakukohdesId(
      Collection<Long> hakukohdeIds, TarjontaTila... requiredStatus) {
    boolean hasStatus =
        requiredStatus != null && requiredStatus.length > 0 && requiredStatus[0] != null;

    Query q =
        getEntityManager()
            .createQuery(
                hasStatus
                    ? "SELECT ktm.oid FROM Hakukohde hk, IN(hk.koulutusmoduuliToteutuses) ktm "
                        + "WHERE hk.id IN(:hakukohdeIds) AND ktm.tila IN(:requiredStatus)"
                    : "SELECT ktm.oid FROM Hakukohde hk, IN(hk.koulutusmoduuliToteutuses) ktm "
                        + "WHERE hk.id IN(:hakukohdeIds)");
    q.setParameter("hakukohdeIds", hakukohdeIds);
    log.debug("searching for komotos with status:" + Lists.newArrayList(requiredStatus));

    if (hasStatus) {
      q.setParameter("requiredStatus", Lists.newArrayList(requiredStatus));
    }

    List<String> list = (List<String>) q.getResultList();

    return list;
  }

  @Override
  public List<Long> findIdsByoids(Collection<String> oids) {
    final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    final BooleanExpression criteria = komoto.oid.in(oids);
    return queryFactory().select(komoto.id).from(komoto).where(criteria).distinct().fetch();
  }

  @Override
  public void safeDelete(final String komotoOid, final String userOid) {
    Preconditions.checkNotNull(komotoOid, "Komoto OID string object cannot be null.");
    List<String> oids = Lists.<String>newArrayList();
    oids.add(komotoOid);
    KoulutusmoduuliToteutus komoto = findByOid(komotoOid);
    Preconditions.checkArgument(komoto != null, "Delete failed, entity not found.");
    komoto.setTila(TarjontaTila.POISTETTU);
    komoto.setLastUpdatedByOid(userOid);
    komoto.setUniqueExternalId(
        null); // Unique external id is globally unique, make ID available again
  }

  @Override
  public void setViimIndeksointiPvmToNull(Long id) {
    final BooleanExpression qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.id.eq(id);

    JPAUpdateClause updateClause =
        new JPAUpdateClause(getEntityManager(), QKoulutusmoduuliToteutus.koulutusmoduuliToteutus);
    updateClause
        .where(qKomoto)
        .setNull(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.viimIndeksointiPvm);
    updateClause.execute();
  }

  public List<KoulutusmoduuliToteutus> findKomotosSharingCommonFields(
      KoulutusmoduuliToteutus komoto) {

    String pohjakoulutusvaatimusWhere =
        komoto.getPohjakoulutusvaatimusUri() == null
            ? "komoto.pohjakoulutusvaatimusUri IS NULL"
            : "komoto.pohjakoulutusvaatimusUri = :pkv";

    String rawQuery =
        "SELECT komoto FROM "
            + "KoulutusmoduuliToteutus komoto, IN (komoto.opetuskielis) o, IN(komoto.koulutuslajis) k "
            + "WHERE "
            + pohjakoulutusvaatimusWhere
            + " "
            + "AND komoto.tarjoaja = :tarjoaja "
            + "AND komoto.koulutusUri = :koulutusUri "
            + "AND komoto.toteutustyyppi = :toteutustyyppi "
            + "AND o.koodiUri IN (:opetuskielis) "
            + "AND k.koodiUri IN (:koulutuslajis) "
            + "AND komoto.oid <> :komotoOid "
            + "AND komoto.tila NOT IN ("
            + TarjontaTila.POISTETTU
            + ")";

    List<String> opetuskielis = new ArrayList<String>();
    for (KoodistoUri uri : komoto.getOpetuskielis()) {
      opetuskielis.add(uri.getKoodiUri());
    }
    List<String> koulutuslajis = new ArrayList<String>();
    for (KoodistoUri uri : komoto.getKoulutuslajis()) {
      koulutuslajis.add(uri.getKoodiUri());
    }

    Query query = getEntityManager().createQuery(rawQuery);

    if (komoto.getPohjakoulutusvaatimusUri() != null) {
      query.setParameter("pkv", komoto.getPohjakoulutusvaatimusUri());
    }

    query
        .setParameter("tarjoaja", komoto.getTarjoaja())
        .setParameter("koulutusUri", komoto.getKoulutusUri())
        .setParameter("toteutustyyppi", komoto.getToteutustyyppi())
        .setParameter("opetuskielis", opetuskielis)
        .setParameter("koulutuslajis", koulutuslajis)
        .setParameter("komotoOid", komoto.getOid());

    return query.getResultList();
  }

  @Override
  public Pair<ToteutustyyppiEnum, String> getToteutustyyppiAndKoulutusmoduuliOidByKomotoId(
      Long komotoId) {
    QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    QKoulutusmoduuli qKoulutusmoduuli = QKoulutusmoduuli.koulutusmoduuli;
    Tuple row =
        queryFactory()
            .select(qKomoto.toteutustyyppi, qKoulutusmoduuli.oid)
            .from(qKomoto)
            .join(qKomoto.koulutusmoduuli, qKoulutusmoduuli)
            .where(qKomoto.id.eq(komotoId))
            .fetchOne();
    return new Pair(row.get(0, ToteutustyyppiEnum.class), row.get(1, String.class));
  }

  @Override
  public Pair<Long, TarjontaTila> getFirstIdAndTilaByKomoOid(String komoOid) {
    QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    QKoulutusmoduuli qKomo = QKoulutusmoduuli.koulutusmoduuli;
    List<Tuple> list =
        queryFactory()
            .select(qKomoto.id, qKomoto.tila)
            .from(qKomoto)
            .join(qKomoto.koulutusmoduuli, qKomo)
            .where(qKomo.oid.eq(komoOid))
            .distinct()
            .fetch();

    if (list.isEmpty()) {
      return null;
    } else {
      Tuple row = list.get(0);
      return new Pair(row.get(0, Long.class), row.get(1, TarjontaTila.class));
    }
  }
}
