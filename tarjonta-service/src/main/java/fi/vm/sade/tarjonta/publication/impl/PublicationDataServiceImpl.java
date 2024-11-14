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
package fi.vm.sade.tarjonta.publication.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.MonikielinenMetadataDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.publication.Tila;
import fi.vm.sade.tarjonta.publication.Tila.Tyyppi;
import fi.vm.sade.tarjonta.service.enums.MetaCategory;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * See {@link PublicationDataService} for documentation.
 *
 * @author Jukka Raanamo
 */
@Service
@Transactional(readOnly = true)
public class PublicationDataServiceImpl implements PublicationDataService {

  private static final Logger log = LoggerFactory.getLogger(PublicationDataServiceImpl.class);
  @Autowired private KoulutusmoduuliToteutusDAO komotoDAO;
  @Autowired private KoulutusmoduuliDAO komoDAO;
  @Autowired private HakukohdeDAO hakukohdeDAO;
  @Autowired private MonikielinenMetadataDAO metadataDAO;
  @PersistenceContext public EntityManager em;

  @Override
  public List<KoulutusmoduuliToteutus> listKoulutusmoduuliToteutus() {

    QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    QKoulutusmoduuli komo = QKoulutusmoduuli.koulutusmoduuli;
    QMonikielinenTeksti nimi = new QMonikielinenTeksti("nimi");
    QKoulutusSisaltyvyys sl = QKoulutusSisaltyvyys.koulutusSisaltyvyys;
    QKielivalikoima kielivalikoima = QKielivalikoima.kielivalikoima;

    final BooleanExpression criteria =
        komoto.tila.in(TarjontaTila.publicValues()).and(komo.tila.eq(TarjontaTila.JULKAISTU));

    return queryFactory()
        .selectFrom(komoto)
        .leftJoin(komoto.ammattinimikes)
        .leftJoin(komoto.avainsanas)
        .leftJoin(komoto.opetuskielis)
        .leftJoin(komoto.opetusmuotos)
        .leftJoin(komoto.koulutuslajis)
        .leftJoin(komoto.linkkis)
        .leftJoin(komoto.koulutusmoduuli, komo)
        .leftJoin(komoto.lukiodiplomit)
        .leftJoin(komoto.tarjotutKielet, kielivalikoima)
        .leftJoin(kielivalikoima.kielet)
        .leftJoin(komo.nimi, nimi)
        .leftJoin(nimi.tekstis)
        .leftJoin(komo.sisaltyvyysList, sl)
        .leftJoin(sl.alamoduuliList)
        .where(criteria)
        .distinct()
        .fetch();
  }

  @Override
  public List<Hakukohde> listHakukohde() {

    // todo filter only published
    QHakukohde hakukohde = QHakukohde.hakukohde;
    QValintakoe valintakoe = QValintakoe.valintakoe;
    QMonikielinenTeksti kuvaus = new QMonikielinenTeksti("kuvaus");
    QMonikielinenTeksti lisatiedot = new QMonikielinenTeksti("lisatiedot");
    QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
    QPisteraja pisterajat = QPisteraja.pisteraja;
    QMonikielinenTeksti lisanaytto = new QMonikielinenTeksti("lisanaytto");

    final BooleanExpression criteria = hakukohde.tila.in(TarjontaTila.publicValues());

    return queryFactory()
        .selectFrom(hakukohde)
        .leftJoin(hakukohde.valintakoes, valintakoe)
        .leftJoin(valintakoe.kuvaus, kuvaus)
        .leftJoin(valintakoe.pisterajat, pisterajat)
        .leftJoin(valintakoe.lisanaytot, lisanaytto)
        .leftJoin(kuvaus.tekstis)
        .leftJoin(hakukohde.liites)
        .leftJoin(hakukohde.koulutusmoduuliToteutuses, komoto)
        .leftJoin(komoto.koulutusmoduuli)
        .leftJoin(hakukohde.lisatiedot, lisatiedot)
        .leftJoin(lisatiedot.tekstis)
        .where(criteria)
        .distinct()
        .fetch();
  }

  @Override
  public List<Haku> listHaku() {

    QHaku haku = QHaku.haku;
    QMonikielinenTeksti nimi = QMonikielinenTeksti.monikielinenTeksti;

    BooleanExpression criteria = haku.tila.in(TarjontaTila.publicValues());

    return queryFactory()
        .selectFrom(haku)
        .leftJoin(haku.nimi, nimi)
        .leftJoin(nimi.tekstis)
        .where(criteria)
        .distinct()
        .fetch();
  }

  @Override
  public Tilamuutokset updatePublicationStatus(List<Tila> tilaChanges)
      throws IllegalArgumentException {

    Tilamuutokset tilamuutokset = new Tilamuutokset();
    Map<Tyyppi, Map<TarjontaTila, List<String>>> map =
        new EnumMap<Tyyppi, Map<TarjontaTila, List<String>>>(Tyyppi.class);

    if (tilaChanges == null) {
      throw new IllegalArgumentException("tilasiirtyma.error.null.list");
    }

    // filter given data to map
    for (Tila tila : tilaChanges) {
      getListOfOids(getSubMapByQHakukohde(map, tila.getTyyppi()), tila.getTila())
          .add(tila.getOid());
    }

    // Update selected tarjonta oids to given status.
    for (Entry<Tyyppi, Map<TarjontaTila, List<String>>> qs : map.entrySet()) {
      for (Entry<TarjontaTila, List<String>> tila : qs.getValue().entrySet()) {
        final Tyyppi dataType = qs.getKey();
        final List<String> oids = tila.getValue();
        final TarjontaTila toStatus = tila.getKey();
        log.debug("updating:" + dataType + " to: " + toStatus);
        Tilamuutokset tm = updateTarjontaTilaStatus(oids, dataType, toStatus);
        log.debug(
            "result HK:" + tm.getMuutetutHakukohteet() + " result KMT" + tm.getMuutetutKomotot());
        tilamuutokset.getMuutetutHakukohteet().addAll(tm.getMuutetutHakukohteet());
        tilamuutokset.getMuutetutKomotot().addAll(tm.getMuutetutKomotot());
      }
    }

    return tilamuutokset;
  }

  @Override
  public boolean isValidStatusChange(Tila tyyppi) {
    checkParam(tyyppi, "GeneerinenTilaTyyppi");
    checkParam(tyyppi.getOid(), "OID");
    checkParam(tyyppi.getTila(), "TarjontaTila");
    checkParam(tyyppi.getTyyppi(), "SisaltoTyyppi");

    TarjontaTila fromStatus = null;
    final String oid = tyyppi.getOid();

    switch (tyyppi.getTyyppi()) {
      case HAKU:
        fromStatus =
            ((Haku)
                    isNullEntity(
                        queryFactory()
                            .selectFrom(QHaku.haku)
                            .where(QHaku.haku.oid.eq(oid))
                            .fetchOne(),
                        oid))
                .getTila();
        break;
      case HAKUKOHDE:
        fromStatus =
            ((Hakukohde)
                    isNullEntity(
                        queryFactory()
                            .selectFrom(QHakukohde.hakukohde)
                            .where(QHakukohde.hakukohde.oid.eq(oid))
                            .fetchOne(),
                        oid))
                .getTila();
        break;
      case KOMO:
        fromStatus =
            ((Koulutusmoduuli)
                    isNullEntity(
                        queryFactory()
                            .selectFrom(QKoulutusmoduuli.koulutusmoduuli)
                            .where(QKoulutusmoduuli.koulutusmoduuli.oid.eq(oid))
                            .fetchOne(),
                        oid))
                .getTila();

        break;
      case KOMOTO:
        fromStatus =
            ((KoulutusmoduuliToteutus)
                    isNullEntity(
                        queryFactory()
                            .selectFrom(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus)
                            .where(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.oid.eq(oid))
                            .fetchOne(),
                        oid))
                .getTila();
        break;
      default:
        throw new RuntimeException("Unsupported tarjonta type : " + tyyppi.getTyyppi());
    }

    // the business rules for status codes.
    return fromStatus.acceptsTransitionTo(tyyppi.getTila());

    // An parent object check is not implemented, but now we can
    // manage with the simple status check.
  }

  private Map<TarjontaTila, List<String>> getSubMapByQHakukohde(
      Map<Tyyppi, Map<TarjontaTila, List<String>>> map, Tyyppi q) {
    if (map.containsKey(q)) {
      return map.get(q);
    } else {
      Map subMap = new EnumMap<TarjontaTila, List<String>>(TarjontaTila.class);
      map.put(q, subMap);
      return subMap;
    }
  }

  private List getListOfOids(Map<TarjontaTila, List<String>> map, TarjontaTila tila) {
    if (map.containsKey(tila)) {
      return map.get(tila);
    } else {
      List<String> emptyList = new ArrayList<String>();
      map.put(tila, emptyList);
      return emptyList;
    }
  }

  private void checkParam(final Object obj, final String message) {
    if (obj == null) {
      throw new IllegalArgumentException(message + " object cannot be null.");
    }
  }

  private Object isNullEntity(final BaseEntity obj, final String oid) {
    if (obj == null) {
      throw new RuntimeException("No result found by OID '" + oid + "'.");
    }
    return obj;
  }

  @Transactional
  private Tilamuutokset updateTarjontaTilaStatus(
      final Collection<String> oids, final Tyyppi dataType, final TarjontaTila toStatus) {
    Tilamuutokset muutokset = new Tilamuutokset();
    log.info("oids : " + oids + ", dataType : " + dataType + ", toStatus : " + toStatus);
    final Date lastUpdatedDate = new Date(System.currentTimeMillis());

    final String userOid = getUserOid();
    Preconditions.checkNotNull(userOid, "User OID cannot be null.");

    switch (dataType) {
      case HAKU:
        // update haku data to given status
        JPAUpdateClause hakuUpdate = new JPAUpdateClause(em, QHaku.haku);
        BooleanExpression qHaku = QHaku.haku.oid.in(oids);

        hakuUpdate
            .where(qHaku)
            .set(QHaku.haku.tila, toStatus)
            .set(QHaku.haku.lastUpdateDate, lastUpdatedDate)
            .set(QHaku.haku.lastUpdatedByOid, userOid);
        hakuUpdate.execute();

        // update all other data relations to give status
        if (TarjontaTila.JULKAISTU.equals(toStatus)) {
          updateAllStatusesRelatedToHaku(
              oids, toStatus, userOid, lastUpdatedDate, TarjontaTila.VALMIS);
        }

        break;
      case HAKUKOHDE:
        BooleanExpression qHakukohde = QHakukohde.hakukohde.oid.in(oids);
        JPAUpdateClause hakukohdeUpdate = new JPAUpdateClause(em, QHakukohde.hakukohde);

        hakukohdeUpdate
            .where(qHakukohde)
            .set(QHakukohde.hakukohde.tila, toStatus)
            .set(QHakukohde.hakukohde.lastUpdateDate, lastUpdatedDate)
            .set(QHakukohde.hakukohde.lastUpdatedByOid, userOid);
        hakukohdeUpdate.execute();
        muutokset.getMuutetutHakukohteet().addAll(oids);

        switch (toStatus) {
          case JULKAISTU:
            muutokset
                .getMuutetutKomotot()
                .addAll(
                    updateHakukohdeRelatedKomotos(
                        toStatus,
                        userOid,
                        lastUpdatedDate,
                        oids,
                        TarjontaTila.VALMIS)); // päivitä komotojen tila samaksi jos tila == valmis
            break;
          case PERUTTU:
            muutokset
                .getMuutetutKomotot()
                .addAll(
                    updateHakukohdeRelatedKomotos(
                        toStatus,
                        userOid,
                        lastUpdatedDate,
                        oids,
                        TarjontaTila
                            .JULKAISTU)); // päivitä komotojen tila samaksi jos tila == julkaistu
            break;
          default:
            break;
        }

        break;
      case KOMO:
        JPAUpdateClause komoUpdate = new JPAUpdateClause(em, QKoulutusmoduuli.koulutusmoduuli);
        komoUpdate
            .where(QKoulutusmoduuli.koulutusmoduuli.oid.in(oids))
            .set(QKoulutusmoduuli.koulutusmoduuli.tila, toStatus)
            .set(QKoulutusmoduuli.koulutusmoduuli.updated, lastUpdatedDate);
        komoUpdate.execute();
        break;
      case KOMOTO:
        // updates komoto and also hakukohde, if it's saved as ready.

        JPAUpdateClause komotoUpdate =
            new JPAUpdateClause(em, QKoulutusmoduuliToteutus.koulutusmoduuliToteutus);
        BooleanExpression qToteutus = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.oid.in(oids);

        komotoUpdate
            .where(qToteutus)
            .set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.tila, toStatus)
            .set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.updated, lastUpdatedDate)
            .set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.lastUpdatedByOid, userOid);
        komotoUpdate.execute();

        muutokset.getMuutetutKomotot().addAll(oids);

        switch (toStatus) {
          case JULKAISTU:
            muutokset
                .getMuutetutHakukohteet()
                .addAll(
                    updateAllHakukohdeStatusesByKomotoOids(
                        oids,
                        userOid,
                        toStatus,
                        TarjontaTila.JULKAISTU,
                        lastUpdatedDate,
                        TarjontaTila.VALMIS));
            updateKomoByKomotoOids(toStatus, userOid, lastUpdatedDate, oids);
            break;
          case PERUTTU:
            muutokset
                .getMuutetutHakukohteet()
                .addAll(
                    updateAllHakukohdeStatusesByKomotoOids(
                        oids,
                        userOid,
                        toStatus,
                        TarjontaTila.JULKAISTU,
                        lastUpdatedDate,
                        TarjontaTila.JULKAISTU));
            break;
          default:
            break;
        }
        break;
    }
    return muutokset;
  }

  private List<String> updateHakukohdeRelatedKomotos(
      TarjontaTila toStatus,
      String userOid,
      Date lastUpdatedDate,
      Collection<String> hakukohdeOids,
      TarjontaTila fromStatus) {
    List<String> returnList = Lists.newArrayList();

    QHakukohde qHakukohde = QHakukohde.hakukohde;
    QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

    final BooleanExpression criteria =
        qHakukohde
            .oid
            .in(hakukohdeOids)
            .and(qHakukohde.haku.tila.eq(TarjontaTila.JULKAISTU))
            .and(qKomoto.tila.eq(fromStatus));

    List<KoulutusmoduuliToteutus> komotos =
        (List<KoulutusmoduuliToteutus>)
            queryFactory()
                .from(qHakukohde, qKomoto)
                .join(qKomoto.hakukohdes, qHakukohde)
                .where(criteria)
                .distinct()
                .fetch();

    for (KoulutusmoduuliToteutus komoto : komotos) {
      // älä peruuta koulutusta, jos sillä on edelleen julkaistu-tilassa oleva hakukohde
      if (toStatus.equals(TarjontaTila.PERUTTU)
          && komotoHasHakukohdesInPublishedState(komoto, hakukohdeOids)) {
        continue;
      }
      updateKomoto(komoto, toStatus, userOid);
      returnList.add(komoto.getOid());
    }

    return returnList;
  }

  private boolean komotoHasHakukohdesInPublishedState(
      KoulutusmoduuliToteutus komoto, Collection<String> ignoreHakukohdeOids) {
    for (Hakukohde hakukohde : komoto.getHakukohdes()) {
      if (hakukohde.getTila().equals(TarjontaTila.JULKAISTU)
          && !ignoreHakukohdeOids.contains(hakukohde.getOid())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Search application option entities by LOI OIDs.
   *
   * @param komotoOids
   * @param hakuRequiredStatus
   * @param hakukohdeRequiredStatus
   * @return
   */
  @Override
  public List<Hakukohde> searchHakukohteetByKomotoOid(
      final Collection<String> komotoOids,
      final TarjontaTila hakuRequiredStatus,
      final TarjontaTila... hakukohdeRequiredStatus) {
    QHakukohde hakukohde = QHakukohde.hakukohde;
    QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

    final BooleanExpression criteria =
        komoto
            .oid
            .in(komotoOids)
            .and(hakukohde.haku.tila.eq(hakuRequiredStatus))
            .and(hakukohde.tila.in(hakukohdeRequiredStatus));

    return (List<Hakukohde>)
        queryFactory()
            .from(hakukohde, komoto)
            .join(komoto.hakukohdes, hakukohde)
            .where(criteria)
            .distinct()
            .fetch();
  }

  /**
   * Updates all AO(hakukohde) entities by LOI(toteutus) OIDs. The rules: 1. There must be at least
   * one LOI with the required statuses. 2. There must be AS(haku) with the required status.
   *
   * @param komotoOids
   * @param toStatus
   * @param hakuRequiredStatus
   * @param hakukohdeRequiredStatus
   */
  private List<String> updateAllHakukohdeStatusesByKomotoOids(
      final Collection<String> komotoOids,
      final String updaterOid,
      final TarjontaTila toStatus,
      final TarjontaTila hakuRequiredStatus,
      Date lastUpdatedDate,
      final TarjontaTila... hakukohdeRequiredStatus) {

    List<String> oidResult = Lists.newArrayList();
    List<Hakukohde> result =
        searchHakukohteetByKomotoOid(komotoOids, hakuRequiredStatus, hakukohdeRequiredStatus);

    for (Hakukohde h : result) {

      // BUG-110, älä peruuta hakukohdetta, jos sillä on edelleen julkaistu-tilassa olevia
      // koulutuksia
      if (toStatus.equals(TarjontaTila.PERUTTU)
          && hakukohdeHasKomotosInPublishedState(h, komotoOids)) {
        continue;
      }

      h.setTila(toStatus);
      h.setLastUpdateDate(lastUpdatedDate);
      h.setLastUpdatedByOid(updaterOid);
      hakukohdeDAO.update(h);
      oidResult.add(h.getOid());
    }

    return oidResult;
  }

  private boolean hakukohdeHasKomotosInPublishedState(
      Hakukohde hakukohde, Collection<String> ignoreKomotoOids) {
    for (KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
      if (komoto.getTila().equals(TarjontaTila.JULKAISTU)
          && !ignoreKomotoOids.contains(komoto.getOid())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Search AO entities by OIDs, and change the TarjontaTila status to given status to OIDs. The
   * status change affects to related LOI and AO data objects.
   *
   * @param hakuOids
   * @param toStatus
   * @param requiredStatus
   */
  private List<String> updateAllStatusesRelatedToHaku(
      final Collection<String> hakuOids,
      final TarjontaTila toStatus,
      final String userOid,
      Date lastUpdatedDate,
      final TarjontaTila... requiredStatus) {

    List<String> komotoOids = Lists.newArrayList();
    // Update hakukohde status by list of haku OIDs
    final List<Long> hakukohdeIds =
        hakukohdeDAO.searchHakukohteetByHakuOid(hakuOids, requiredStatus);
    if (hakukohdeIds != null && !hakukohdeIds.isEmpty()) {
      final BooleanExpression qHakukohde = QHakukohde.hakukohde.id.in(hakukohdeIds);
      JPAUpdateClause hakukohdeUpdate = new JPAUpdateClause(em, QHakukohde.hakukohde);
      qHakukohde.and(QHakukohde.hakukohde.tila.in(requiredStatus));
      hakukohdeUpdate
          .where(qHakukohde)
          .set(QHakukohde.hakukohde.tila, toStatus)
          .set(QHakukohde.hakukohde.lastUpdateDate, lastUpdatedDate)
          .set(QHakukohde.hakukohde.lastUpdatedByOid, userOid);
      hakukohdeUpdate.execute();

      // Update toteutus status by list of hakukohde IDs
      List<String> komotoOidList =
          komotoDAO.searchKomotoOIDsByHakukohdesId(hakukohdeIds, requiredStatus);
      komotoOids.addAll(komotoOidList);

      updateKomotos(toStatus, userOid, lastUpdatedDate, komotoOidList);
    }
    return komotoOids;
  }

  private void updateKomoto(KoulutusmoduuliToteutus komoto, TarjontaTila toStatus, String userOid) {
    komoto.setTila(toStatus);
    komoto.setLastUpdatedByOid(userOid);
  }

  private void updateKomotos(
      final TarjontaTila toStatus,
      final String userOid,
      Date lastUpdatedDate,
      final List<String> komotoOIDs) {
    if (komotoOIDs != null && !komotoOIDs.isEmpty()) {
      JPAUpdateClause komotoUpdate =
          new JPAUpdateClause(em, QKoulutusmoduuliToteutus.koulutusmoduuliToteutus);
      final BooleanExpression qToteutus =
          QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.oid.in(komotoOIDs);
      komotoUpdate
          .where(qToteutus)
          .set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.tila, toStatus)
          .set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.updated, lastUpdatedDate)
          .set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.lastUpdatedByOid, userOid);
      komotoUpdate.execute();
    }
  }

  private void updateKomoByKomotoOids(
      final TarjontaTila toStatus,
      final String userOid,
      final Date lastUpdatedDate,
      final Collection<String> komotoOIDs) {
    if (komotoOIDs != null && !komotoOIDs.isEmpty()) {
      QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

      final List<String> komoOids =
          queryFactory()
              .select(komoto.koulutusmoduuli.oid)
              .from(komoto)
              .where(
                  komoto
                      .oid
                      .in(komotoOIDs)
                      .and(
                          komoto.koulutusmoduuli.koulutustyyppiEnum.eq(
                              ModuulityyppiEnum.KORKEAKOULUTUS)))
              .fetch();

      if (komoOids != null && !komoOids.isEmpty()) {
        QKoulutusmoduuli m = QKoulutusmoduuli.koulutusmoduuli;
        JPAUpdateClause komoUpdate = new JPAUpdateClause(em, m);
        komoUpdate.where(m.oid.in(komoOids)).set(m.tila, toStatus).set(m.updated, lastUpdatedDate);
        komoUpdate.execute();
      }
    }
  }

  @Override
  public List<MonikielinenMetadata> searchMetaData(final String key, final MetaCategory category) {
    return metadataDAO.findByAvainAndKategoria(key, category.toString());
  }

  private String getUserOid() {
    Preconditions.checkNotNull(
        SecurityContextHolder.getContext(), "Context object cannot be null.");
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Preconditions.checkNotNull(authentication, "Authentication object cannot be null.");
    String oid = authentication.getName();
    Preconditions.checkNotNull(oid, "User oid cannot be null.");
    return oid;
  }

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(em);
  }
}
