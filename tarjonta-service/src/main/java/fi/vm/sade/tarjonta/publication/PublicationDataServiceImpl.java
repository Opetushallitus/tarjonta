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
package fi.vm.sade.tarjonta.publication;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.tarjonta.model.*;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jukka Raanamo
 */
@Service
@Transactional(readOnly = true)
public class PublicationDataServiceImpl implements PublicationDataService {

    @PersistenceContext
    public EntityManager em;

    @Override
    public List<KoulutusmoduuliToteutus> listKoulutusmoduuliToteutus() {

        QKoulutusmoduuliToteutus toteutus = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        QKoulutusmoduuli m = QKoulutusmoduuli.koulutusmoduuli;
        QMonikielinenTeksti kr = new QMonikielinenTeksti("koulutuksenrakenne");
        QMonikielinenTeksti jom = new QMonikielinenTeksti("jatkoopintomahdollisuudet");
        QMonikielinenTeksti pkm = new QMonikielinenTeksti("pohjakoulutusvaatimus");
        QMonikielinenTeksti ak = new QMonikielinenTeksti("arviointikriteerit");
        QMonikielinenTeksti lkv = new QMonikielinenTeksti("loppukoevaatimukset");

        // todo: filter only published

        return from(toteutus).
            leftJoin(toteutus.ammattinimikes).fetch().
            leftJoin(toteutus.avainsanas).fetch().
            leftJoin(toteutus.opetuskielis).fetch().
            leftJoin(toteutus.opetusmuotos).fetch().
            leftJoin(toteutus.koulutuslajis).fetch().
            leftJoin(toteutus.loppukoeVaatimukset, lkv).fetch().leftJoin(lkv.tekstis).fetch().
            leftJoin(toteutus.arviointikriteerit, ak).fetch().leftJoin(ak.tekstis).fetch().
            leftJoin(toteutus.pohjakoulutusvaatimus, pkm).fetch().leftJoin(pkm.tekstis).fetch().
            leftJoin(toteutus.linkkis).fetch().
            leftJoin(toteutus.koulutusmoduuli, m).fetch().
            leftJoin(m.koulutuksenRakenne, kr).fetch().leftJoin(kr.tekstis).fetch().
            leftJoin(m.jatkoOpintoMahdollisuudet, jom).fetch().leftJoin(jom.tekstis).fetch().
            list(toteutus);
    }

    @Override
    public List<Hakukohde> listHakukohde() {

        // todo filter only published

        QHakukohde hakukohde = QHakukohde.hakukohde;
        QValintakoe valintakoe = QValintakoe.valintakoe;
        QMonikielinenTeksti kuvaus = QMonikielinenTeksti.monikielinenTeksti;

        return from(hakukohde).
            leftJoin(hakukohde.valintakoes, valintakoe).fetch().
            leftJoin(valintakoe.kuvaus, kuvaus).fetch().
            leftJoin(kuvaus.tekstis).fetch().
            leftJoin(hakukohde.liites).fetch().
            leftJoin(hakukohde.koulutusmoduuliToteutuses).fetch().
            list(hakukohde);

    }

    @Override
    public List<Haku> listHaku() {

        // todo: filter only published

        QHaku haku = QHaku.haku;
        QMonikielinenTeksti nimi = QMonikielinenTeksti.monikielinenTeksti;

        return from(haku).
            leftJoin(haku.nimi, nimi).fetch().
            leftJoin(nimi.tekstis).fetch().
            list(haku);

    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(em).from(o);
    }

}

