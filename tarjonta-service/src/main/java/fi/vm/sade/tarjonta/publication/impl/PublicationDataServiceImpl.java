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

import com.google.common.collect.Lists;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.expr.BooleanExpression;

import fi.vm.sade.events.Event;
import fi.vm.sade.events.EventSender;
import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.MonikielinenMetadataDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
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
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.enums.MetaCategory;
import fi.vm.sade.tarjonta.service.types.GeneerinenTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

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
    public void updatePublicationStatus(List<GeneerinenTilaTyyppi> tilaOids) {
        Map<SisaltoTyyppi, Map<TarjontaTila, List<String>>> map = new EnumMap<SisaltoTyyppi, Map<TarjontaTila, List<String>>>(SisaltoTyyppi.class);

        if (tilaOids == null) {
            throw new IllegalArgumentException("List of GeneerinenTilaTyyppi objects cannot be null.");
        }

        //filter given data to map
        for (GeneerinenTilaTyyppi tila : tilaOids) {
            final TarjontaTila convertedTila = EntityUtils.convertTila(tila.getTila());
            getListOfOids(getSubMapByQHakukohde(map, tila.getSisalto()), convertedTila).add(tila.getOid());
        }

        //Update selected tarjonta oids to given status.
        for (Entry<SisaltoTyyppi, Map<TarjontaTila, List<String>>> qs : map.entrySet()) {
            for (Entry<TarjontaTila, List<String>> tila : qs.getValue().entrySet()) {
                final SisaltoTyyppi dataType = qs.getKey();
                final List<String> oids = tila.getValue();
                final TarjontaTila toStatus = tila.getKey();
                updateTarjontaTilaStatus(oids, dataType, toStatus, null);
            }
        }
    }

    @Override
    public boolean isValidStatusChange(GeneerinenTilaTyyppi tyyppi) {
        checkParam(tyyppi, "GeneerinenTilaTyyppi");
        checkParam(tyyppi.getOid(), "OID");
        checkParam(tyyppi.getTila(), "TarjontaTila");
        checkParam(tyyppi.getSisalto(), "SisaltoTyyppi");

        TarjontaTila fromStatus = null;
        final TarjontaTila toStatus = EntityUtils.convertTila(tyyppi.getTila());
        final String oid = tyyppi.getOid();

        switch (tyyppi.getSisalto()) {
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
        }

        //the business rules for status codes.
        return fromStatus.acceptsTransitionTo(toStatus);

        //An parent object check is not implemented, but now we can 
        //manage with the simple status check.

    }

    private Map<TarjontaTila, List<String>> getSubMapByQHakukohde(Map<SisaltoTyyppi, Map<TarjontaTila, List<String>>> map, SisaltoTyyppi q) {
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

    private String toggleChangePublishedState(TarjontaTila fromState, TarjontaTila toState) {

        String resultMsg = "toggling state from: " + fromState + ", to: " + toState;
        String fromStateName = fromState.name();
        String toStateName = toState.name();

        String[] entityNames = {"Haku", "Hakukohde", "Koulutusmoduuli", "KoulutusmoduuliToteutus"};

        for (String entityName : entityNames) {
            resultMsg += "\nupdated "
                    + em.createQuery("UPDATE " + entityName + " set tila = '" + toStateName + "' where tila = '" + fromStateName + "'").executeUpdate()
                    + " " + entityName + " -objects";

        }

        return resultMsg;

    }

    @Transactional
    private void updateTarjontaTilaStatus(final Collection<String> oids, final SisaltoTyyppi dataType, final TarjontaTila toStatus, final TarjontaTila requiredStatus) {
        if (log.isDebugEnabled()) {
            log.debug("oids : " + oids + ", dataType : " + dataType + ", toStatus : " + toStatus + ", requiredStatus: " + requiredStatus);
        }

        switch (dataType) {
            case HAKU:
                //update haku data to given status
                JPAUpdateClause hakuUpdate = new JPAUpdateClause(em, QHaku.haku);
                BooleanExpression qHaku = QHaku.haku.oid.in(oids);

                if (requiredStatus != null) {
                    qHaku.and(QHaku.haku.tila.eq(requiredStatus));
                }

                hakuUpdate.where(qHaku).set(QHaku.haku.tila, toStatus);
                hakuUpdate.execute();

                //update all other data relations to give status
                if (TarjontaTila.JULKAISTU.equals(toStatus)) {
                    updateAllStatusesRelatedToHaku(oids, toStatus, TarjontaTila.VALMIS);
                } /*else if (TarjontaTila.PERUTTU.equals(toStatus)) {
                    updateAllStatusesRelatedToHakuCancel(oids, TarjontaTila.cancellableValues());
                }*/

                break;
            case HAKUKOHDE:
                BooleanExpression qHakukohde = QHakukohde.hakukohde.oid.in(oids);
                JPAUpdateClause hakukohdeUpdate = new JPAUpdateClause(em, QHakukohde.hakukohde);

                if (requiredStatus != null) {
                    qHakukohde.and(QHakukohde.hakukohde.tila.eq(requiredStatus));
                }

                hakukohdeUpdate.where(qHakukohde).set(QHakukohde.hakukohde.tila, toStatus);
                hakukohdeUpdate.execute();
                break;
            case KOMO:
                JPAUpdateClause komoUpdate = new JPAUpdateClause(em, QKoulutusmoduuli.koulutusmoduuli);
                komoUpdate.where(QKoulutusmoduuli.koulutusmoduuli.oid.in(oids)).set(QKoulutusmoduuli.koulutusmoduuli.tila, toStatus);
                komoUpdate.execute();
                break;
            case KOMOTO:
                //updates komoto and also hakukohde, if it's saved as ready. 

                JPAUpdateClause komotoUpdate = new JPAUpdateClause(em, QKoulutusmoduuliToteutus.koulutusmoduuliToteutus);
                BooleanExpression qToteutus = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.oid.in(oids);

                if (requiredStatus != null) {
                    qToteutus.and(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.tila.eq(requiredStatus));
                }

                komotoUpdate.where(qToteutus).set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.tila, toStatus);
                komotoUpdate.execute();

                if (TarjontaTila.JULKAISTU.equals(toStatus)) {
                    updateAllHakukohdeStatusesByKomotoOids(oids, toStatus, TarjontaTila.JULKAISTU, TarjontaTila.VALMIS);
                } else if (TarjontaTila.PERUTTU.equals(toStatus)) {
                    updateAllHakukohdeStatusesByKomotoOids(oids, toStatus, TarjontaTila.JULKAISTU, TarjontaTila.cancellableValues());
                }
                break;
        }
    }

    /**
     * Search application option entities by application system OIDs.
     *
     * @param hakuOids
     * @param requiredStatus
     * @return
     */
    public List<Hakukohde> searchHakukohteetByHakuOid(final Collection<String> hakuOids, final TarjontaTila... requiredStatus) {
        final QHakukohde hakukohde = QHakukohde.hakukohde;

        final BooleanExpression criteria =
                hakukohde.tila.in(requiredStatus).
                and(hakukohde.haku.oid.in(hakuOids)).
                and(hakukohde.koulutusmoduuliToteutuses.isNotEmpty().
                and(hakukohde.koulutusmoduuliToteutuses.any().tila.in(requiredStatus)));

        return from(hakukohde).
                leftJoin(hakukohde.koulutusmoduuliToteutuses).fetch().
                where(criteria).
                distinct().list(hakukohde);
    }

    /**
     * Search application option entities by LOI OIDs.
     *
     * @param komotoOids
     * @param hakuRequiredStatus
     * @param hakukohdeRequiredStatus
     * @return
     */
    public List<Hakukohde> searchHakukohteetByKomotoOid(final Collection<String> komotoOids, final TarjontaTila hakuRequiredStatus, final TarjontaTila... hakukohdeRequiredStatus) {
        QHakukohde hakukohde = QHakukohde.hakukohde;

        final BooleanExpression criteria =
                hakukohde.koulutusmoduuliToteutuses.isNotEmpty().
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
    private void updateAllHakukohdeStatusesByKomotoOids(final Collection<String> komotoOids, final TarjontaTila toStatus, final TarjontaTila hakuRequiredStatus, final TarjontaTila... hakukohdeRequiredStatus) {
        List<Hakukohde> result = searchHakukohteetByKomotoOid(komotoOids, hakuRequiredStatus, hakukohdeRequiredStatus);

        for (Hakukohde h : result) {
            h.setTila(toStatus);
        }
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
    private void updateAllStatusesRelatedToHaku(final Collection<String> hakuOids, final TarjontaTila toStatus, final TarjontaTila... requiredStatus) {
        List<Hakukohde> result = searchHakukohteetByHakuOid(hakuOids, requiredStatus);

        for (Hakukohde hakukohde : result) {
            hakukohde.setTila(toStatus);

            for (KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
                komoto.setTila(toStatus);
                updateParentKomotoStatus(komoto, toStatus);

            }
        }
    }

    /**
     * Updates status of parent of komoto according to parameters
     *
     * @param komoto - the komoto the parent of which to update
     * @param toStatus - the status
     */
    private void updateParentKomotoStatus(KoulutusmoduuliToteutus komoto, final TarjontaTila toStatus) {
        KoulutusmoduuliToteutus parentKomoto = getParentKomoto(komoto);
        if (parentKomoto != null) {
            parentKomoto.setTila(toStatus);
        }
    }

    private KoulutusmoduuliToteutus getParentKomoto(KoulutusmoduuliToteutus komoto) {
        Koulutusmoduuli parentKomo = getParentKomo(komoto.getKoulutusmoduuli());
        if (parentKomo != null) {
            return findKomotoByKomoAndTarjoaja(parentKomo, komoto.getTarjoaja());
        }
        return null;
    }

    private Koulutusmoduuli getParentKomo(Koulutusmoduuli komo) {
        QKoulutusSisaltyvyys sisaltyvyys = QKoulutusSisaltyvyys.koulutusSisaltyvyys;

        List<KoulutusSisaltyvyys> parents = from(sisaltyvyys).
                join(sisaltyvyys.alamoduuliList).fetch().
                where(sisaltyvyys.alamoduuliList.contains(komo)).
                list(sisaltyvyys);

        if (parents == null || parents.isEmpty()) {
            return null;
        }

        return parents.get(0).getYlamoduuli();
    }

    private KoulutusmoduuliToteutus findKomotoByKomoAndTarjoaja(
            Koulutusmoduuli komo, String tarjoaja) {
        QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        KoulutusmoduuliToteutus komotoRes = null;
        try {
            komotoRes = from(komoto).
                    join(komoto.koulutusmoduuli).fetch().
                    where(komoto.koulutusmoduuli.oid.eq(komo.getOid()).and(komoto.tarjoaja.eq(tarjoaja))).singleResult(komoto);
        } catch (Exception ex) {
            log.debug("Exception: " + ex.getMessage());
        }
        return komotoRes;
    }

    /**
     * Search AS relations by OIDs, and change the TarjontaTila status to
     * cancelled. The status change affects to related AO data objects.
     *
     * @param hakuOids
     * @param toStatus
     * @param requiredStatus
     */
    private void updateAllStatusesRelatedToHakuCancel(final Collection<String> hakuOids, final TarjontaTila... requiredStatus) {
        List<Hakukohde> result = searchHakukohteetByHakuOid(hakuOids, requiredStatus);
        for (Hakukohde hakukohde : result) {
            hakukohde.setTila(TarjontaTila.PERUTTU);
        }
    }

    @Override
    public List<MonikielinenMetadata> searchMetaData(final String key, final MetaCategory category) {
        return metadataDAO.findByAvainAndKategoria(key, category.toString());
    }
}
