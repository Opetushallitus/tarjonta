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
import com.google.common.collect.Maps;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import fi.vm.sade.tarjonta.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import jakarta.persistence.Query;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/** */
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
    QYhteystiedot yhteystiedot = QYhteystiedot.yhteystiedot;
    BooleanExpression oidEq = toteutus.oid.eq(koulutusmoduuliToteutusOid);

    return queryFactory()
        .selectFrom(hakukohde)
        .join(hakukohde.koulutusmoduuliToteutuses, toteutus)
        .leftJoin(hakukohde.yhteystiedot, yhteystiedot)
        .where(oidEq.and(hakukohde.tila.notIn(poistettuTila)))
        .fetch();
  }

  @Override
  public Hakukohde findHakukohdeByUlkoinenTunniste(String ulkoinenTunniste, String tarjoajaOid) {

    QHakukohde qHakukohde = QHakukohde.hakukohde;
    QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    QYhteystiedot yhteystiedot = QYhteystiedot.yhteystiedot;

    return queryFactory()
        .selectFrom(qHakukohde)
        .join(qHakukohde.koulutusmoduuliToteutuses, qKomoto)
        .leftJoin(qHakukohde.yhteystiedot, yhteystiedot)
        .where(
            qHakukohde
                .ulkoinenTunniste
                .eq(ulkoinenTunniste)
                .and(qKomoto.tarjoaja.eq(tarjoajaOid))
                .and(qHakukohde.tila.notIn(poistettuTila)))
        .fetchOne();
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

    // getEntityManager().flush();
  }

  @Override
  public void insertLiittees(List<HakukohdeLiite> liites, String hakukohdeOid) {
    Hakukohde hakukohde = findHakukohdeByOid(hakukohdeOid);

    hakukohde.getLiites().clear();
    for (HakukohdeLiite liite : liites) {
      liite.setHakukohde(hakukohde);
    }

    hakukohde.getLiites().addAll(liites);

    // getEntityManager().flush();
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

    // Ugly, but so is Hibernate
    managedLiite.setErapaiva(hakukohdeLiite.getErapaiva());
    managedLiite.setKieli(hakukohdeLiite.getKieli());
    managedLiite.setHakukohdeLiiteNimi(hakukohdeLiite.getHakukohdeLiiteNimi());
    managedLiite.setKuvaus(hakukohdeLiite.getKuvaus());
    managedLiite.setLiitetyyppi(hakukohdeLiite.getLiitetyyppi());
    managedLiite.setSahkoinenToimitusosoite(hakukohdeLiite.getSahkoinenToimitusosoite());
    managedLiite.setToimitusosoite(hakukohdeLiite.getToimitusosoite());

    // getEntityManager().flush();

  }

  @Override
  public List<Valintakoe> findValintakoeByHakukohdeOid(String oid) {
    QHakukohde qHakukohde = QHakukohde.hakukohde;
    QValintakoe qValintakoe = QValintakoe.valintakoe;
    return queryFactory()
        .select(qValintakoe)
        .from(qHakukohde, qValintakoe)
        .where(qHakukohde.oid.eq(oid).and(qValintakoe.hakukohde.eq(qHakukohde)))
        .fetch();
  }

  @Override
  public List<HakukohdeLiite> findHakukohdeLiitesByHakukohdeOid(String oid) {
    QHakukohde qHakukohde = QHakukohde.hakukohde;
    QHakukohdeLiite qHakukohdeLiite = QHakukohdeLiite.hakukohdeLiite;

    return queryFactory()
        .select(qHakukohdeLiite)
        .from(qHakukohde, qHakukohdeLiite)
        .where(qHakukohde.oid.eq(oid).and(qHakukohdeLiite.hakukohde.eq(qHakukohde)))
        .fetch();
  }

  @Override
  public HakukohdeLiite findHakuKohdeLiiteById(String id) {
    QHakukohdeLiite liite = QHakukohdeLiite.hakukohdeLiite;
    Long idLong = Long.valueOf(id);
    return queryFactory().selectFrom(liite).where(liite.id.eq(idLong)).fetchOne();
  }

  public List<KoulutusmoduuliToteutus> komotoTest(String term, int year, String providerOid) {
    Query query =
        getEntityManager()
            .createQuery(
                "SELECT k from KoulutusmoduuliToteutus k join fetch k.hakukohdes"
                    + "WHERE k.alkamiskausi = :kausi "
                    + "AND k.alkamisVuosi = :vuosi "
                    + "AND k.tarjoaja = :tarjoaja");

    query.setParameter("kausi", term);
    query.setParameter("vuosi", year);
    query.setParameter("tarjoaja", providerOid);

    return (List<KoulutusmoduuliToteutus>) query.getResultList();
  }

  @Override
  public List<Hakukohde> findByTarjoajaHakuAndNimiUri(
      Set<String> tarjoajaOids, String hakuOid, String nimiUri) {
    QHakukohde qHakukohde = QHakukohde.hakukohde;
    QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

    return queryFactory()
        .selectFrom(qHakukohde)
        .innerJoin(qHakukohde.koulutusmoduuliToteutuses, qKomoto)
        .where(
            qHakukohde
                .hakukohdeNimi
                .startsWith(nimiUri.replace("#[^#]+$", "#"))
                .and(qHakukohde.tila.notIn(TarjontaTila.POISTETTU, TarjontaTila.PERUTTU))
                .and(qKomoto.tarjoaja.in(tarjoajaOids))
                .and(qHakukohde.haku.oid.eq(hakuOid)))
        .fetch();
  }

  @Override
  public List<Hakukohde> findByNameTermAndYear(
      String name, String term, int year, String providerOid) {

    QHakukohde qHakukohde = QHakukohde.hakukohde;
    QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    QYhteystiedot qYhteystiedot = QYhteystiedot.yhteystiedot;

    return queryFactory()
        .selectFrom(qHakukohde)
        .innerJoin(qHakukohde.koulutusmoduuliToteutuses, qKomoto)
        .leftJoin(qHakukohde.yhteystiedot, qYhteystiedot)
        .where(
            qHakukohde
                .hakukohdeNimi
                .eq(name)
                .and(qHakukohde.tila.notIn(TarjontaTila.POISTETTU))
                .and(
                    qKomoto
                        .alkamiskausiUri
                        .eq(term)
                        .and(qKomoto.alkamisVuosi.eq(year).and(qKomoto.tarjoaja.eq(providerOid)))))
        .fetch();
  }

  @Override
  public List<Hakukohde> findByTermYearAndProvider(String term, int year, String providerOid) {

    QHakukohde qHakukohde = QHakukohde.hakukohde;
    QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    QYhteystiedot qYhteystiedot = QYhteystiedot.yhteystiedot;

    return queryFactory()
        .selectFrom(qHakukohde)
        .innerJoin(qHakukohde.koulutusmoduuliToteutuses, qKomoto)
        .leftJoin(qHakukohde.yhteystiedot, qYhteystiedot)
        .where(
            qKomoto
                .alkamiskausiUri
                .eq(term)
                .and(
                    qKomoto
                        .alkamisVuosi
                        .eq(year)
                        .and(qHakukohde.tila.notIn(TarjontaTila.POISTETTU))
                        .and(qKomoto.tarjoaja.eq(providerOid))))
        .fetch();
  }

  @Override
  public void removeValintakoe(Valintakoe valintakoe) {
    if (valintakoe != null && valintakoe.getId() != null) {
      getEntityManager().remove(getEntityManager().find(Valintakoe.class, valintakoe.getId()));
      // getEntityManager().flush();
    }
  }

  @Override
  public void removeHakukohdeLiite(HakukohdeLiite hakukohdeLiite) {
    if (hakukohdeLiite != null && hakukohdeLiite.getId() != null) {
      getEntityManager()
          .remove(getEntityManager().find(HakukohdeLiite.class, hakukohdeLiite.getId()));
      // getEntityManager().flush();
    }
  }

  @Override
  public Valintakoe findValintaKoeById(String id) {
    QValintakoe qValintakoe = QValintakoe.valintakoe;
    Long idLong = Long.valueOf(id);
    return queryFactory().selectFrom(qValintakoe).where(qValintakoe.id.eq(idLong)).fetchOne();
  }

  @Override
  public Hakukohde findHakukohdeByOid(final String oid) {
    Preconditions.checkNotNull(oid, "Hakukohde OID cannot be null.");

    QHakukohde qHakukohde = QHakukohde.hakukohde;

    return queryFactory().selectFrom(qHakukohde).where(qHakukohde.oid.eq(oid)).fetchOne();
  }

  @Override
  public List<Hakukohde> findHakukohteetByOids(Set<String> oids) {
    Preconditions.checkNotNull(oids, "Hakukohde OID set cannot be null.");

    QHakukohde qHakukohde = QHakukohde.hakukohde;

    return queryFactory().selectFrom(qHakukohde).where(qHakukohde.oid.in(oids)).fetch();
  }

  @Override
  public Hakukohde findHakukohdeById(final Long id) {
    Preconditions.checkNotNull(id, "Hakukohde ID cannot be null.");

    QHakukohde qHakukohde = QHakukohde.hakukohde;

    return queryFactory().selectFrom(qHakukohde).where(qHakukohde.id.eq(id)).fetchOne();
  }

  @Override
  public Hakukohde findHakukohdeByOid(final String oid, final boolean showDeleted) {

    Preconditions.checkNotNull(oid, "Hakukohde OID cannot be null.");
    QHakukohde qHakukohde = QHakukohde.hakukohde;
    if (showDeleted) {
      return queryFactory().selectFrom(qHakukohde).where(qHakukohde.oid.eq(oid)).fetchOne();
    } else {
      return queryFactory()
          .selectFrom(qHakukohde)
          .where(qHakukohde.oid.eq(oid).and(qHakukohde.tila.notIn(TarjontaTila.POISTETTU)))
          .fetchOne();
    }
  }

  @Override
  public Hakukohde findHakukohdeByUniqueExternalId(String uniqueExternalId) {
    QHakukohde qHakukohde = QHakukohde.hakukohde;
    return queryFactory()
        .selectFrom(qHakukohde)
        .where(
            qHakukohde
                .uniqueExternalId
                .eq(uniqueExternalId)
                .and(qHakukohde.tila.notIn(TarjontaTila.POISTETTU)))
        .fetchOne();
  }

  @Override
  public Hakukohde findExistingHakukohde(HakukohdeV1RDTO dto) {
    if (!StringUtils.isBlank(dto.getOid())) {
      return findHakukohdeByOid(dto.getOid());
    } else if (!StringUtils.isBlank(dto.getUniqueExternalId())) {
      return findHakukohdeByUniqueExternalId(dto.getUniqueExternalId());
    }
    return null;
  }

  @Override
  public Hakukohde findHakukohdeWithKomotosByOid(String oid) {
    return findHakukohdeByOid(oid);
  }

  @Override
  public Hakukohde findHakukohdeWithDepenciesByOid(String oid) {
    return findHakukohdeByOid(oid);
  }

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public List<Hakukohde> findOrphanHakukohteet() {
    QHakukohde hakukohde = QHakukohde.hakukohde;
    BooleanExpression toteutusesEmpty = hakukohde.koulutusmoduuliToteutuses.isEmpty();
    return queryFactory().selectFrom(hakukohde).where(toteutusesEmpty).fetch();
  }

  @Override
  public List<String> findOIDsBy(
      fi.vm.sade.tarjonta.service.types.TarjontaTila tila,
      int count,
      int startIndex,
      Date lastModifiedBefore,
      Date lastModifiedSince,
      boolean showKK) {

    QHakukohde hakukohde = QHakukohde.hakukohde;
    BooleanExpression whereExpr = null;

    if (tila != null) {
      // Convert Enums from API enum to DB enum
      whereExpr =
          QuerydslUtils.and(
              whereExpr,
              hakukohde.tila.eq(
                  fi.vm.sade.tarjonta.shared.types.TarjontaTila.valueOf(tila.name())));
    } else {
      whereExpr = QuerydslUtils.and(whereExpr, hakukohde.tila.notIn(poistettuTila));
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
    Expression<?>[] projectionExpr = new Expression<?>[] {hakukohde.oid};

    List<Object[]> tmp = findScalars(whereExpr, count, startIndex, projectionExpr);
    return convertToSingleStringList(tmp);
  }

  @Override
  public List<String> findOidsByKoulutusId(long koulutusId) {
    // TODO use constants
    Query q =
        getEntityManager()
            .createQuery(
                "select h.oid from Hakukohde h JOIN h.koulutusmoduuliToteutuses kmt where kmt.id= :komotoId")
            .setParameter("komotoId", koulutusId);

    List<String> results = (List<String>) q.getResultList();
    return results;
  }

  @Override
  public Map<String, List<String>> findAllHakuToHakukohde() {
    // Haetaan kaikkien hakujen hakukohde oidit yhdellä kyselyllä.
    // Tämä on tehty suorituskyvyn parantamiseksi haku/findAll Rest-kyselyyn.
    log.debug("findAllHakuToHakukohde");
    String q =
        "SELECT h.oid, hk.oid "
            + "FROM Hakukohde hk "
            + "JOIN hk.haku h "
            + "JOIN hk.koulutusmoduuliToteutuses k "
            + "WHERE hk.tila NOT IN :poistettu "
            + "    AND k.tila NOT IN :poistettu";
    Query query = getEntityManager().createQuery(q).setParameter("poistettu", poistettuTila);
    Map<String, List<String>> map = new HashMap<>();
    List result = query.getResultList();
    for (Object row : result) {
      Object[] tuple = (Object[]) row;
      String hakuOid = (String) tuple[0];
      String kohdeOid = (String) tuple[1];
      List<String> hakukohdes = map.get(hakuOid);
      if (hakukohdes == null) {
        hakukohdes = new ArrayList<>();
        map.put(hakuOid, hakukohdes);
      }
      hakukohdes.add(kohdeOid);
    }
    return map;
  }

  @Override
  public List<String> findByHakuOid(
      String hakuOid,
      String searchTerms,
      int count,
      int startIndex,
      Date lastModifiedBefore,
      Date lastModifiedSince) {
    String q =
        "SELECT DISTINCT hk.oid "
            + "FROM Hakukohde hk "
            + "JOIN hk.haku h "
            + "JOIN hk.koulutusmoduuliToteutuses k "
            + "WHERE h.oid = :oid "
            + "    AND hk.tila NOT IN :poistettu "
            + "    AND k.tila NOT IN :poistettu";
    Query query =
        getEntityManager()
            .createQuery(q)
            .setParameter("poistettu", poistettuTila)
            .setParameter("oid", hakuOid);
    List<String> result = new ArrayList<>();
    for (Object s : query.getResultList()) {
      result.add((String) s);
    }
    return result;
  }

  @Override
  public List<String> findHakukohteetWithYlioppilastutkintoAntaaHakukelpoisuuden(
      Long hakuId, Boolean hakuValue) {
    QHakukohde hakukohde = QHakukohde.hakukohde;

    BooleanBuilder where = new BooleanBuilder(hakukohde.haku.id.eq(hakuId));

    if (!hakuValue) {
      where.and(hakukohde.ylioppilastutkintoAntaaHakukelpoisuuden.eq(true));
    } else {
      where.and(
          new BooleanBuilder(hakukohde.ylioppilastutkintoAntaaHakukelpoisuuden.eq(true))
              .or(hakukohde.ylioppilastutkintoAntaaHakukelpoisuuden.isNull()));
    }

    where.and(hakukohde.tila.notIn(poistettuTila));

    return queryFactory().select(hakukohde.oid).from(hakukohde).where(where).fetch();
  }

  @Override
  public Map<Long, List<String>> findAllHakuToHakukohdeWhereYlioppilastutkintoAntaaHakukelpoisuuden(
      List<Haku> hakus) {
    List<Long> hasHakuValue = Lists.newArrayList();
    List<Long> hasNoHakuValue = Lists.newArrayList();

    for (Haku haku : hakus) {
      if (haku.getYlioppilastutkintoAntaaHakukelpoisuuden()) {
        hasHakuValue.add(haku.getId());
      } else {
        hasNoHakuValue.add(haku.getId());
      }
    }

    QHakukohde hakukohde = QHakukohde.hakukohde;

    BooleanExpression hasNoHakuValueExpr =
        hakukohde
            .haku
            .id
            .in(hasNoHakuValue)
            .and(
                hakukohde
                    .ylioppilastutkintoAntaaHakukelpoisuuden
                    .eq(true)
                    .or(hakukohde.ylioppilastutkintoAntaaHakukelpoisuuden.isNull()));

    BooleanExpression hakuValueExpr = hasNoHakuValueExpr;

    if (!hasHakuValue.isEmpty()) {
      BooleanExpression hasHakuValueExpr =
          hakukohde
              .haku
              .id
              .in(hasHakuValue)
              .and(hakukohde.ylioppilastutkintoAntaaHakukelpoisuuden.eq(true));
      hakuValueExpr = hakuValueExpr.or(hasHakuValueExpr);
    }

    BooleanExpression where = hakukohde.tila.notIn(poistettuTila).and(hakuValueExpr);

    List<Tuple> rawRes =
        queryFactory()
            .select(hakukohde.haku.id, hakukohde.oid)
            .from(hakukohde)
            .where(where)
            .fetch();

    Map<Long, List<String>> result = Maps.newHashMap();
    for (Tuple tuple : rawRes) {
      Long hakuId = tuple.get(0, Long.class);
      String hk = tuple.get(1, String.class);
      List<String> hakukohdeList =
          result.containsKey(hakuId) ? result.get(hakuId) : Lists.<String>newArrayList();
      hakukohdeList.add(hk);
      result.put(hakuId, hakukohdeList);
    }

    return result;
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
  private List<Object[]> findScalars(
      BooleanExpression whereExpr, int count, int startIndex, Expression<?>... projectionExpr) {
    log.debug("findScalars({}, {}, {}, {})", whereExpr, count, startIndex, projectionExpr);

    QHakukohde hakukohde = QHakukohde.hakukohde;

    JPAQuery<Tuple> q = queryFactory().select(projectionExpr).from(hakukohde);
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
    List<Tuple> lt = q.fetch();
    for (Tuple tuple : lt) {
      result.add(tuple.toArray());
    }

    log.debug("  result size = {}", result.size());

    return result;
  }

  @Override
  public void update(Hakukohde entity) {
    super.update(entity);
  }

  @Override
  public void merge(Hakukohde hk) {
    getEntityManager().merge(hk);
  }

  @Override
  public boolean removeHakuKohdeLiiteById(String id) {
    return getEntityManager()
            .createQuery("delete from " + HakukohdeLiite.class.getName() + " where id = ?")
            .setParameter(1, Long.parseLong(id))
            .executeUpdate()
        != 0;
  }

  public void updateTilat(
      TarjontaTila toTila, List<String> oidit, Date updateDate, String userOid) {
    final BooleanExpression qHakukohde = QHakukohde.hakukohde.oid.in(oidit);

    JPAUpdateClause hakukohdeUpdate = new JPAUpdateClause(getEntityManager(), QHakukohde.hakukohde);
    hakukohdeUpdate
        .where(qHakukohde)
        .set(QHakukohde.hakukohde.tila, toTila)
        .set(QHakukohde.hakukohde.lastUpdateDate, updateDate)
        .set(QHakukohde.hakukohde.lastUpdatedByOid, userOid);
    hakukohdeUpdate.execute();
  }

  public List<Long> searchHakukohteetByHakuOid(
      final Collection<String> hakuOids, final TarjontaTila... requiredStatus) {
    final QHakukohde hakukohde = QHakukohde.hakukohde;
    final BooleanExpression criteria =
        hakukohde.haku.oid.in(hakuOids).and(hakukohde.tila.in(requiredStatus));

    return queryFactory()
        .select(QHakukohde.hakukohde.id)
        .from(hakukohde)
        .where(criteria)
        .distinct()
        .fetch();
  }

  @Override
  public List<Long> findIdsByoids(Collection<String> oids) {
    final QHakukohde hakukohde = QHakukohde.hakukohde;
    final BooleanExpression criteria = hakukohde.oid.in(oids);
    return queryFactory()
        .select(QHakukohde.hakukohde.id)
        .from(hakukohde)
        .where(criteria)
        .distinct()
        .fetch();
  }

  @Override
  public void safeDelete(final String hakukohdeOid, final String userOid) {
    Preconditions.checkNotNull(hakukohdeOid, "Hakukohde OID string object cannot be null.");
    List<String> oids = Lists.<String>newArrayList();
    oids.add(hakukohdeOid);
    Hakukohde hakukohde = findHakukohdeByOid(hakukohdeOid);
    Preconditions.checkArgument(hakukohde != null, "Delete failed, entity not found.");
    hakukohde.setTila(TarjontaTila.POISTETTU);
    hakukohde.setLastUpdatedByOid(userOid);
    hakukohde.setUniqueExternalId(
        null); // Unique external id is globally unique, make ID available again
  }

  @Override
  public List<String> findAllOids() {
    final QHakukohde hakukohde = QHakukohde.hakukohde;
    return queryFactory().select(hakukohde.oid).from(hakukohde).fetch();
  }

  @Override
  public void setViimIndeksointiPvmToNull(Long id) {
    final BooleanExpression qHakukohde = QHakukohde.hakukohde.id.eq(id);

    JPAUpdateClause updateClause = new JPAUpdateClause(getEntityManager(), QHakukohde.hakukohde);
    updateClause.where(qHakukohde).setNull(QHakukohde.hakukohde.viimIndeksointiPvm);
    updateClause.execute();
  }
}
