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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.expr.BooleanExpression;
import fi.vm.sade.events.Event;
import fi.vm.sade.events.EventSender;
import fi.vm.sade.generic.model.BaseEntity;


import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.GeneerinenTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private TarjontaTila[] PUBLIC_DATA = {TarjontaTila.JULKAISTU, TarjontaTila.PERUTTU};
    @Autowired(required = true)
    private EventSender eventSender;
    @PersistenceContext
    public EntityManager em;

    @Override
    public List<KoulutusmoduuliToteutus> listKoulutusmoduuliToteutus() {

        QKoulutusmoduuliToteutus toteutus = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        QKoulutusmoduuli m = QKoulutusmoduuli.koulutusmoduuli;
        QMonikielinenTeksti kr = new QMonikielinenTeksti("koulutuksenrakenne");
        QMonikielinenTeksti jom = new QMonikielinenTeksti("jatkoopintomahdollisuudet");
        QMonikielinenTeksti ak = new QMonikielinenTeksti("arviointikriteerit");
        QMonikielinenTeksti lkv = new QMonikielinenTeksti("loppukoevaatimukset");
        QMonikielinenTeksti nimi = new QMonikielinenTeksti("nimi");

        BooleanExpression criteria = toteutus.tila.in(PUBLIC_DATA).and(m.tila.eq(TarjontaTila.JULKAISTU));

        return from(toteutus).
                leftJoin(toteutus.ammattinimikes).fetch().
                leftJoin(toteutus.avainsanas).fetch().
                leftJoin(toteutus.opetuskielis).fetch().
                leftJoin(toteutus.opetusmuotos).fetch().
                leftJoin(toteutus.koulutuslajis).fetch().
                leftJoin(toteutus.loppukoeVaatimukset, lkv).fetch().leftJoin(lkv.tekstis).fetch().
                leftJoin(toteutus.arviointikriteerit, ak).fetch().leftJoin(ak.tekstis).fetch().
                leftJoin(toteutus.linkkis).fetch().
                leftJoin(toteutus.koulutusmoduuli, m).fetch().
                leftJoin(m.koulutuksenRakenne, kr).fetch().leftJoin(kr.tekstis).fetch().
                leftJoin(m.jatkoOpintoMahdollisuudet, jom).fetch().leftJoin(jom.tekstis).fetch().
                leftJoin(m.nimi, nimi).fetch().leftJoin(nimi.tekstis).fetch().
                where(criteria).
                distinct().list(toteutus);
    }

    @Override
    public List<Hakukohde> listHakukohde() {

        // todo filter only published

        QHakukohde hakukohde = QHakukohde.hakukohde;
        QValintakoe valintakoe = QValintakoe.valintakoe;
        QMonikielinenTeksti kuvaus = new QMonikielinenTeksti("kuvaus");
        QMonikielinenTeksti valintaperuste = new QMonikielinenTeksti("valintaperuste");
        QMonikielinenTeksti lisatiedot = new QMonikielinenTeksti("lisatiedot");

        BooleanExpression criteria = hakukohde.tila.in(PUBLIC_DATA);

        return from(hakukohde).
                leftJoin(hakukohde.valintakoes, valintakoe).fetch().
                leftJoin(valintakoe.kuvaus, kuvaus).fetch().leftJoin(kuvaus.tekstis).fetch().
                leftJoin(hakukohde.valintaperusteKuvaus, valintaperuste).fetch().leftJoin(valintaperuste.tekstis).fetch().
                leftJoin(hakukohde.liites).fetch().
                leftJoin(hakukohde.koulutusmoduuliToteutuses).fetch().
                leftJoin(hakukohde.lisatiedot, lisatiedot).fetch().leftJoin(lisatiedot.tekstis).fetch().
                where(criteria).
                distinct().list(hakukohde);

    }

    @Override
    public List<Haku> listHaku() {

        QHaku haku = QHaku.haku;
        QMonikielinenTeksti nimi = QMonikielinenTeksti.monikielinenTeksti;

        BooleanExpression criteria = haku.tila.in(PUBLIC_DATA);

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
                JPAUpdateClause jpaUpdateClause = null;

                switch (qs.getKey()) {
                    case HAKU:
                        jpaUpdateClause = new JPAUpdateClause(em, QHaku.haku);
                        jpaUpdateClause.where(QHaku.haku.oid.in(tila.getValue())).set(QHaku.haku.tila, tila.getKey());
                        break;
                    case HAKUKOHDE:
                        jpaUpdateClause = new JPAUpdateClause(em, QHakukohde.hakukohde);
                        jpaUpdateClause.where(QHakukohde.hakukohde.oid.in(tila.getValue())).set(QHakukohde.hakukohde.tila, tila.getKey());
                        break;
                    case KOMO:
                        jpaUpdateClause = new JPAUpdateClause(em, QKoulutusmoduuli.koulutusmoduuli);
                        jpaUpdateClause.where(QKoulutusmoduuli.koulutusmoduuli.oid.in(tila.getValue())).set(QKoulutusmoduuli.koulutusmoduuli.tila, tila.getKey());
                        break;
                    case KOMOTO:
                        jpaUpdateClause = new JPAUpdateClause(em, QKoulutusmoduuliToteutus.koulutusmoduuliToteutus);
                        jpaUpdateClause.where(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.oid.in(tila.getValue())).set(QKoulutusmoduuliToteutus.koulutusmoduuliToteutus.tila, tila.getKey());
                        break;
                }
                jpaUpdateClause.execute();
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
        switch (toStatus) {
            //to state
            case LUONNOS:
                //check if a step is allowed from A state to B state
                return TarjontaTila.LUONNOS.equals(fromStatus) ? true : false;
            case VALMIS:
                return TarjontaTila.LUONNOS.equals(fromStatus) || TarjontaTila.VALMIS.equals(fromStatus) ? true : false;
            case PERUTTU:
                return TarjontaTila.JULKAISTU.equals(fromStatus) ? true : false;
            case JULKAISTU:
                return TarjontaTila.VALMIS.equals(fromStatus) || TarjontaTila.JULKAISTU.equals(fromStatus) ? true : false;
            default:
                return false;
        }


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
    public void sendEvent(final fi.vm.sade.tarjonta.model.TarjontaTila tila, final String oid, final String dataType, final String action) {
        log.debug("In sendEvent, tila:{}, oid : {}", tila, oid);

        /*
         * Filter all unpublished (delete, insert and update) actions. 
         */
        if (eventSender != null && PUBLIC_DATA[0].equals(tila) || PUBLIC_DATA[1].equals(tila)) {
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
}
