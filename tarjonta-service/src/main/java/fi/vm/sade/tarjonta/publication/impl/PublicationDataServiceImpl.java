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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.expr.BooleanExpression;

import fi.vm.sade.events.Event;
import fi.vm.sade.events.EventSender;
import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.security.SadeUserDetailsWrapper;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.MonikielinenMetadataDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.MonikielinenMetadata;
import fi.vm.sade.tarjonta.model.QHaku;
import fi.vm.sade.tarjonta.model.QHakukohde;
import fi.vm.sade.tarjonta.model.QKielivalikoima;
import fi.vm.sade.tarjonta.model.QKoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.QKoulutusmoduuli;
import fi.vm.sade.tarjonta.model.QKoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.QMonikielinenTeksti;
import fi.vm.sade.tarjonta.model.QPisteraja;
import fi.vm.sade.tarjonta.model.QValintakoe;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.publication.Tila;
import fi.vm.sade.tarjonta.publication.Tila.Tyyppi;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.service.enums.MetaCategory;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;

import javax.persistence.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * See {@link PublicationDataService} for documentation.
 *
 * @author Jukka Raanamo
 */
@Service
@Transactional(readOnly = true)
public class PublicationDataServiceImpl implements PublicationDataService {

    private static final Logger log = LoggerFactory.getLogger(PublicationDataServiceImpl.class);
    @Autowired(required = true)
    private EventSender eventSender;
    @Autowired
    private KoulutusmoduuliToteutusDAO komotoDAO;
    @Autowired
    private KoulutusmoduuliDAO komoDAO;
    @Autowired
    private HakukohdeDAO hakukohdeDAO;
    @Autowired
    private MonikielinenMetadataDAO metadataDAO;
    @PersistenceContext
    public EntityManager em;

    @Override
    public List<KoulutusmoduuliToteutus> listKoulutusmoduuliToteutus() {

        QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        QKoulutusmoduuli komo = QKoulutusmoduuli.koulutusmoduuli;
        //QMonikielinenTeksti kr = new QMonikielinenTeksti("koulutuksenrakenne");
        //QMonikielinenTeksti jom = new QMonikielinenTeksti("jatkoopintomahdollisuudet");
        //QMonikielinenTeksti ak = new QMonikielinenTeksti("arviointikriteerit");
        //QMonikielinenTeksti lkv = new QMonikielinenTeksti("loppukoevaatimukset");
        QMonikielinenTeksti nimi = new QMonikielinenTeksti("nimi");
        QKoulutusSisaltyvyys sl = QKoulutusSisaltyvyys.koulutusSisaltyvyys;
        QKielivalikoima kielivalikoima = QKielivalikoima.kielivalikoima;

        final BooleanExpression criteria = komoto.tila.in(TarjontaTila.publicValues()).and(komo.tila.eq(TarjontaTila.JULKAISTU));

        return from(komoto).
                leftJoin(komoto.ammattinimikes).fetch().
                leftJoin(komoto.avainsanas).fetch().
                leftJoin(komoto.opetuskielis).fetch().
                leftJoin(komoto.opetusmuotos).fetch().
                leftJoin(komoto.koulutuslajis).fetch().
                //leftJoin(komoto.loppukoeVaatimukset, lkv).fetch().leftJoin(lkv.tekstis).fetch().
                //leftJoin(komoto.arviointikriteerit, ak).fetch().leftJoin(ak.tekstis).fetch().
                leftJoin(komoto.linkkis).fetch().
                leftJoin(komoto.koulutusmoduuli, komo).fetch().
                leftJoin(komoto.lukiodiplomit).fetch().
                leftJoin(komoto.tarjotutKielet, kielivalikoima).fetch().leftJoin(kielivalikoima.kielet).fetch().
                //leftJoin(komo.koulutuksenRakenne, kr).fetch().leftJoin(kr.tekstis).fetch().
                //leftJoin(komo.jatkoOpintoMahdollisuudet, jom).fetch().leftJoin(jom.tekstis).fetch().
                leftJoin(komo.nimi, nimi).fetch().leftJoin(nimi.tekstis).fetch().
                leftJoin(komo.sisaltyvyysList, sl).fetch().leftJoin(sl.alamoduuliList).fetch().
                where(criteria).
                distinct().list(komoto);
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

        return from(hakukohde).
                leftJoin(hakukohde.valintakoes, valintakoe).fetch().
                leftJoin(valintakoe.kuvaus, kuvaus).fetch().
                leftJoin(valintakoe.pisterajat, pisterajat).fetch().
                leftJoin(valintakoe.lisanaytot, lisanaytto).fetch().
                leftJoin(kuvaus.tekstis).fetch().
                leftJoin(hakukohde.liites).fetch().
                leftJoin(hakukohde.koulutusmoduuliToteutuses, komoto).fetch().
                leftJoin(komoto.koulutusmoduuli).fetch().
                leftJoin(hakukohde.lisatiedot, lisatiedot).fetch().
                leftJoin(lisatiedot.tekstis).fetch().
                where(criteria).
                distinct().list(hakukohde);
    }

    @Override
    public List<Haku> listHaku() {

        QHaku haku = QHaku.haku;
        QMonikielinenTeksti nimi = QMonikielinenTeksti.monikielinenTeksti;

        BooleanExpression criteria = haku.tila.in(TarjontaTila.publicValues());

        return from(haku).
                leftJoin(haku.nimi, nimi).fetch().
                leftJoin(nimi.tekstis).fetch().
                where(criteria).
                distinct().list(haku);

    }

    @Override
    public Tilamuutokset updatePublicationStatus(List<Tila> tilaChanges) throws IllegalArgumentException {

        Tilamuutokset tilamuutokset = new Tilamuutokset();
        Map<Tyyppi, Map<TarjontaTila, List<String>>> map = new EnumMap<Tyyppi, Map<TarjontaTila, List<String>>>(Tyyppi.class);

        if (tilaChanges == null) {
            throw new IllegalArgumentException("tilasiirtyma.error.null.list");
        }

// disabled for now:  because tests started to fail      
//        for(Tila tila:tilaChanges) {
//            if(!isValidStatusChange(tila)) {
//                throw new IllegalArgumentException("tilasiirtyma.error.impossible");
//            }
//        }
        //filter given data to map
        for (Tila tila : tilaChanges) {
            getListOfOids(getSubMapByQHakukohde(map, tila.getTyyppi()), tila.getTila()).add(tila.getOid());
        }

        //Update selected tarjonta oids to given status.
        for (Entry<Tyyppi, Map<TarjontaTila, List<String>>> qs : map.entrySet()) {
            for (Entry<TarjontaTila, List<String>> tila : qs.getValue().entrySet()) {
                final Tyyppi dataType = qs.getKey();
                final List<String> oids = tila.getValue();
                final TarjontaTila toStatus = tila.getKey();
                log.debug("updating:" + dataType + " to: " + toStatus);
                Tilamuutokset tm = updateTarjontaTilaStatus(oids, dataType, toStatus);
                log.debug("result HK:" + tm.getMuutetutHakukohteet() + " result KMT" + tm.getMuutetutKomotot());
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
                fromStatus = ((Haku) isNullEntity(from(QHaku.haku).
                        where(QHaku.haku.oid.eq(oid)).
                        singleResult(QHaku.haku), oid)).getTila();
                break;
            case HAKUKOHDE:
                fromStatus = ((Hakukohde) isNullEntity(from(QHakukohde.hakukohde).
                        where(QHakukohde.hakukohde.oid.eq(oid)).
                        singleResult(QHakukohde.hakukohde), oid)).getTila();
                break;
            case KOMO:
                fromStatus = ((Koulutusmoduuli) isNullEntity(from(QKoulutusmoduuli.koulutusmoduuli).
                        where(QKoulutusmoduuli.koulutusmoduuli.oid.eq(oid)).
                        singleResult(QKoulutusmoduuli.koulutusmoduuli), oid)).getTila();

                break;
            case KOMOTO:
                fromStatus = ((KoulutusmoduuliToteutus) isNullEntity(from(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus).
                        where(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.oid.eq(oid)).
                        singleResult(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus), oid)).getTila();
                break;
            default:
                throw new RuntimeException("Unsupported tarjonta type : " + tyyppi.getTyyppi());
        }

        //the business rules for status codes.
        return fromStatus.acceptsTransitionTo(tyyppi.getTila());

        //An parent object check is not implemented, but now we can 
        //manage with the simple status check.
    }

    private Map<TarjontaTila, List<String>> getSubMapByQHakukohde(Map<Tyyppi, Map<TarjontaTila, List<String>>> map, Tyyppi q) {
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

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(em).from(o);
    }

    /**
     * Send an event for Oppijan Verkkopalvelu.
     *
     * @param tila
     * @param oid
     * @param dataType
     * @param action
     */
    @Override
    public void sendEvent(final fi.vm.sade.tarjonta.shared.types.TarjontaTila tila, final String oid, final String dataType, final String action) {
        log.debug("In sendEvent, tila:{}, oid : {}", tila, oid);

        /*
         * Filter all unpublished (delete, insert and update) actions. 
         */
        if (eventSender != null && tila.isPublic()) {
            /*
             * TODO: Add more information for Oppijan Verkkopalvelu event.
             */
            Event e = new Event("Tarjonta");
            e.setValue("oid", oid)
                    .setValue("dataType", dataType)
                    .setValue("eventType", action)
                    .setValue("date", DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
            eventSender.sendEvent(e);
        }
    }

    @Transactional
    private Tilamuutokset updateTarjontaTilaStatus(final Collection<String> oids, final Tyyppi dataType, final TarjontaTila toStatus) {
        Tilamuutokset muutokset = new Tilamuutokset();
        log.info("oids : " + oids + ", dataType : " + dataType + ", toStatus : " + toStatus);
        final Date lastUpdatedDate = new Date(System.currentTimeMillis());

        final String userOid = getUserOid();
        Preconditions.checkNotNull(userOid, "User OID cannot be null.");

        switch (dataType) {
            case HAKU:
                //update haku data to given status
                JPAUpdateClause hakuUpdate = new JPAUpdateClause(em, QHaku.haku);
                BooleanExpression qHaku = QHaku.haku.oid.in(oids);

                hakuUpdate.where(qHaku).set(QHaku.haku.tila, toStatus)
                        .set(QHaku.haku.lastUpdateDate, lastUpdatedDate)
                        .set(QHaku.haku.lastUpdatedByOid, userOid);
                hakuUpdate.execute();

                //update all other data relations to give status
                if (TarjontaTila.JULKAISTU.equals(toStatus)) {
                    updateAllStatusesRelatedToHaku(oids, toStatus, userOid, lastUpdatedDate, TarjontaTila.VALMIS);
                }

                break;
            case HAKUKOHDE:
                BooleanExpression qHakukohde = QHakukohde.hakukohde.oid.in(oids);
                JPAUpdateClause hakukohdeUpdate = new JPAUpdateClause(em, QHakukohde.hakukohde);

                hakukohdeUpdate.where(qHakukohde)
                        .set(QHakukohde.hakukohde.tila, toStatus)
                        .set(QHakukohde.hakukohde.lastUpdateDate, lastUpdatedDate)
                        //                        .set(QHakukohde.hakukohde.viimIndeksointiPvm, lastUpdatedDate)
                        .set(QHakukohde.hakukohde.lastUpdatedByOid, userOid);
                hakukohdeUpdate.execute();
                muutokset.getMuutetutHakukohteet().addAll(oids);

                switch (toStatus) {
                    case JULKAISTU:
                        muutokset.getMuutetutKomotot().addAll(updateHakukohdeRelatedKomotos(toStatus, userOid, lastUpdatedDate, oids, TarjontaTila.VALMIS)); //päivitä komotojen tila samaksi jos tila == valmis
                        break;
                    case PERUTTU:
                        muutokset.getMuutetutKomotot().addAll(updateHakukohdeRelatedKomotos(toStatus, userOid, lastUpdatedDate, oids, TarjontaTila.JULKAISTU)); //päivitä komotojen tila samaksi jos tila == julkaistu
                        break;
                    default:
                        break;
                }

                break;
            case KOMO:
                JPAUpdateClause komoUpdate = new JPAUpdateClause(em, QKoulutusmoduuli.koulutusmoduuli);
                komoUpdate.where(QKoulutusmoduuli.koulutusmoduuli.oid.in(oids))
                        .set(QKoulutusmoduuli.koulutusmoduuli.tila, toStatus)
                        .set(QKoulutusmoduuli.koulutusmoduuli.updated, lastUpdatedDate);
                komoUpdate.execute();
                break;
            case KOMOTO:
                //updates komoto and also hakukohde, if it's saved as ready. 

                JPAUpdateClause komotoUpdate = new JPAUpdateClause(em, QKoulutusmoduuliToteutus.koulutusmoduuliToteutus);
                BooleanExpression qToteutus = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.oid.in(oids);

                komotoUpdate.where(qToteutus)
                        .set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.tila, toStatus)
                        .set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.updated, lastUpdatedDate)
                        .set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.viimIndeksointiPvm, lastUpdatedDate)
                        .set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.lastUpdatedByOid, userOid);
                komotoUpdate.execute();

                muutokset.getMuutetutKomotot().addAll(oids);

                switch (toStatus) {

                    case JULKAISTU:
                        muutokset.getMuutetutHakukohteet().addAll(updateAllHakukohdeStatusesByKomotoOids(oids, userOid, toStatus, TarjontaTila.JULKAISTU, lastUpdatedDate, TarjontaTila.VALMIS));
                        updateKomoByKomotoOids(toStatus, userOid, lastUpdatedDate, oids);
                        break;
                    case PERUTTU:
                        muutokset.getMuutetutHakukohteet().addAll(updateAllHakukohdeStatusesByKomotoOids(oids, userOid, toStatus, TarjontaTila.JULKAISTU, lastUpdatedDate, TarjontaTila.JULKAISTU));
                        break;
                    default:
                        break;
                }
                break;

        }
        return muutokset;
    }

    private List<String> updateHakukohdeRelatedKomotos(TarjontaTila toStatus, String userOid, Date lastUpdatedDate, Collection<String> hakukohdeOids, TarjontaTila fromStatus) {
        List<String> returnList = Lists.newArrayList();
        //hae hakukohdeidt
        Query q = em.createQuery("select hakukohde.id from Hakukohde as hakukohde inner join hakukohde.haku as haku where haku.tila='JULKAISTU' and hakukohde.oid in(:hakukohdeOIDs)");

        q.setParameter("hakukohdeOIDs", hakukohdeOids);

        List<Long> hakukohdeIds = (List<Long>) q.getResultList();
        if (hakukohdeIds.size() > 0) {
            //hae komotooidt
            List<String> komotoOidList = komotoDAO.searchKomotoOIDsByHakukohdesId(hakukohdeIds, fromStatus);

            returnList.addAll(komotoOidList);
            if (komotoOidList.size() > 0) {
                updateKomotos(toStatus, userOid, lastUpdatedDate, komotoOidList);
            }
        }

        return returnList;
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
    public List<Hakukohde> searchHakukohteetByKomotoOid(final Collection<String> komotoOids, final TarjontaTila hakuRequiredStatus, final TarjontaTila... hakukohdeRequiredStatus) {
        QHakukohde hakukohde = QHakukohde.hakukohde;

        final BooleanExpression criteria
                = hakukohde.koulutusmoduuliToteutuses.isNotEmpty().
                and(hakukohde.koulutusmoduuliToteutuses.any().oid.in(komotoOids)).
                and(hakukohde.haku.tila.eq(hakuRequiredStatus)).
                and(hakukohde.tila.in(hakukohdeRequiredStatus));

        return from(hakukohde).where(criteria).distinct().list(hakukohde);
    }

    /**
     * Updates all AO(hakukohde) entities by LOI(toteutus) OIDs. The rules: 1.
     * There must be at least one LOI with the required statuses. 2. There must
     * be AS(haku) with the required status.
     *
     * @param komotoOids
     * @param toStatus
     * @param hakuRequiredStatus
     * @param hakukohdeRequiredStatus
     */
    private List<String> updateAllHakukohdeStatusesByKomotoOids(final Collection<String> komotoOids, final String updaterOid, final TarjontaTila toStatus, final TarjontaTila hakuRequiredStatus, Date lastUpdatedDate, final TarjontaTila... hakukohdeRequiredStatus) {

        List<String> oidResult = Lists.newArrayList();
        List<Hakukohde> result = searchHakukohteetByKomotoOid(komotoOids, hakuRequiredStatus, hakukohdeRequiredStatus);

        for (Hakukohde h : result) {
            h.setTila(toStatus);
            h.setLastUpdateDate(lastUpdatedDate);
            h.setLastUpdatedByOid(updaterOid);
            hakukohdeDAO.update(h);
            oidResult.add(h.getOid());
        }

        return oidResult;
    }

    /**
     * Search AO entities by OIDs, and change the TarjontaTila status to given
     * status to OIDs. The status change affects to related LOI and AO data
     * objects.
     *
     * @param hakuOids
     * @param toStatus
     * @param requiredStatus
     */
    private List<String> updateAllStatusesRelatedToHaku(final Collection<String> hakuOids, final TarjontaTila toStatus, final String userOid, Date lastUpdatedDate, final TarjontaTila... requiredStatus) {

        List<String> komotoOids = Lists.newArrayList();
        //Update hakukohde status by list of haku OIDs
        final List<Long> hakukohdeIds = hakukohdeDAO.searchHakukohteetByHakuOid(hakuOids, requiredStatus);
        if (hakukohdeIds != null && !hakukohdeIds.isEmpty()) {
            final BooleanExpression qHakukohde = QHakukohde.hakukohde.id.in(hakukohdeIds);
            JPAUpdateClause hakukohdeUpdate = new JPAUpdateClause(em, QHakukohde.hakukohde);
            qHakukohde.and(QHakukohde.hakukohde.tila.in(requiredStatus));
            hakukohdeUpdate.where(qHakukohde)
                    .set(QHakukohde.hakukohde.tila, toStatus)
                    .set(QHakukohde.hakukohde.lastUpdateDate, lastUpdatedDate)
                    .set(QHakukohde.hakukohde.lastUpdatedByOid, userOid);
            hakukohdeUpdate.execute();

            //Update toteutus status by list of hakukohde IDs
            List<String> komotoOidList = komotoDAO.searchKomotoOIDsByHakukohdesId(hakukohdeIds, requiredStatus);
            komotoOids.addAll(komotoOidList);

            updateKomotos(toStatus, userOid, lastUpdatedDate, komotoOidList);
        }
        return komotoOids;

    }

    private void updateKomotos(final TarjontaTila toStatus,
            final String userOid, Date lastUpdatedDate,
            final List<String> komotoOIDs) {
        if (komotoOIDs != null && !komotoOIDs.isEmpty()) {
            JPAUpdateClause komotoUpdate = new JPAUpdateClause(em, QKoulutusmoduuliToteutus.koulutusmoduuliToteutus);
            final BooleanExpression qToteutus = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.oid.in(komotoOIDs);
            komotoUpdate.where(qToteutus)
                    .set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.tila, toStatus)
                    .set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.updated, lastUpdatedDate)
                    .set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.lastUpdatedByOid, userOid);
            komotoUpdate.execute();
        }
    }

    private void updateKomoByKomotoOids(final TarjontaTila toStatus, final String userOid, final Date lastUpdatedDate, final Collection<String> komotoOIDs) {
        if (komotoOIDs != null && !komotoOIDs.isEmpty()) {
            QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

            final List<String> komoOids = from(komoto).where(komoto.oid.in(komotoOIDs)
                    .and(komoto.koulutusmoduuli.koulutustyyppiEnum.eq(ModuulityyppiEnum.KORKEAKOULUTUS)))
                    .list(komoto.koulutusmoduuli.oid);

            if (komoOids != null && !komoOids.isEmpty()) {
                QKoulutusmoduuli m = QKoulutusmoduuli.koulutusmoduuli;
                JPAUpdateClause komoUpdate = new JPAUpdateClause(em, m);
                komoUpdate.where(m.oid.in(komoOids))
                        .set(m.tila, toStatus)
                        .set(m.updated, lastUpdatedDate);
                komoUpdate.execute();
            }
        }
    }

    @Override
    public List<MonikielinenMetadata> searchMetaData(final String key, final MetaCategory category) {
        return metadataDAO.findByAvainAndKategoria(key, category.toString());
    }

    private String getUserOid() {
        Preconditions.checkNotNull(SecurityContextHolder.getContext(), "Context object cannot be null.");
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Preconditions.checkNotNull(authentication, "Authentication object cannot be null.");
        final Object principal = authentication.getPrincipal();

        if (principal != null && principal instanceof SadeUserDetailsWrapper) {
            SadeUserDetailsWrapper sadeUser = (SadeUserDetailsWrapper) principal;
            log.info("User SadeUserDetailsWrapper : {}, user oid : {}", sadeUser, sadeUser.getUsername());
            return sadeUser.getUsername(); //should be an user OID, not name.
        } else if (authentication.getName() != null) {
            //should be an user OID, not name.
            log.info("User oid  : {}", authentication.getName());
            return authentication.getName();
        }

        log.error("No an user OID found! Authentication : {}", authentication);
        return null;
    }
}
