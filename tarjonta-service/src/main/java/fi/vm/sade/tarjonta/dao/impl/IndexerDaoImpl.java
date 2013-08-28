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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Predicate;

import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.model.QHaku;
import fi.vm.sade.tarjonta.model.QHakuaika;
import fi.vm.sade.tarjonta.model.QHakukohde;
import fi.vm.sade.tarjonta.model.QKoodistoUri;
import fi.vm.sade.tarjonta.model.QKoulutusmoduuli;
import fi.vm.sade.tarjonta.model.QKoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.index.HakuAikaIndexEntity;
import fi.vm.sade.tarjonta.model.index.HakukohdeIndexEntity;
import fi.vm.sade.tarjonta.model.index.KoulutusIndexEntity;
import fi.vm.sade.tarjonta.model.index.QHakuAikaIndexEntity;
import fi.vm.sade.tarjonta.model.index.QHakukohdeIndexEntity;
import fi.vm.sade.tarjonta.model.index.QKoulutusIndexEntity;

@Repository
public class IndexerDaoImpl implements IndexerDAO {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<HakukohdeIndexEntity> findAllHakukohteet() {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        final QHaku haku = QHaku.haku;
        return q(hakukohde)
                .join(hakukohde.haku, haku)
                .list(
                        new QHakukohdeIndexEntity(hakukohde.id, hakukohde.oid, hakukohde.hakukohdeNimi,
                                haku.koulutuksenAlkamiskausiUri, haku.koulutuksenAlkamisVuosi, hakukohde.tila, haku.hakutapaUri,
                                hakukohde.aloituspaikatLkm, haku.id, haku.oid));
    }

    @Override
    public List<KoulutusIndexEntity> findKoulutusmoduuliToteutusesByHakukohdeId(Long hakukohdeId) { 
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        final QKoodistoUri koodistoUri = QKoodistoUri.koodistoUri;

        return q(hakukohde).join(hakukohde.koulutusmoduuliToteutuses, komoto).leftJoin(komoto.koulutuslajis, koodistoUri).where(hakukohde.id.eq(hakukohdeId)).list(new QKoulutusIndexEntity(komoto.oid, komoto.tarjoaja, koodistoUri.koodiUri));
    }

    @Override
    public HakukohdeIndexEntity findHakukohdeById(Long id) {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        final QKoodistoUri koodistoUri = QKoodistoUri.koodistoUri;
        
        
        final QHaku haku = QHaku.haku;
        return q(hakukohde)
                .join(hakukohde.haku, haku)
                .where(hakukohde.id.eq(id))
                .singleResult(
                        new QHakukohdeIndexEntity(hakukohde.id, hakukohde.oid, hakukohde.hakukohdeNimi,
                                haku.koulutuksenAlkamiskausiUri, haku.koulutuksenAlkamisVuosi, hakukohde.tila, haku.hakutapaUri,
                                hakukohde.aloituspaikatLkm, haku.id, haku.oid)); 
    }

    @Override
    public List<HakuAikaIndexEntity> findHakuajatForHaku(Long hakuId) {
        final QHaku haku = QHaku.haku;
        final QHakuaika hakuaika = QHakuaika.hakuaika;
        return q(haku)
                .join(haku.hakuaikas, QHakuaika.hakuaika)
                .where(haku.id.eq(hakuId))
                .list(
                        (new QHakuAikaIndexEntity(hakuaika.alkamisPvm, hakuaika.paattymisPvm)));
    }
    
    @Override
    public List<Long> findAllHakukohdeIds() {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        return q(hakukohde).list(hakukohde.id);
    }
    
    @Override
    public List<KoulutusIndexEntity> findAllKoulutukset() {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        final QKoulutusmoduuli koulutusmoduuli = QKoulutusmoduuli.koulutusmoduuli;
        return q(komoto)
                .join(komoto.koulutusmoduuli, koulutusmoduuli)
                .list(
                        (new QKoulutusIndexEntity(komoto.id, komoto.oid, komoto.koulutuksenAlkamisPvm, komoto.tila,
                                koulutusmoduuli.koulutustyyppi, koulutusmoduuli.oid, koulutusmoduuli.koulutusKoodi,
                                koulutusmoduuli.tutkintonimike, koulutusmoduuli.koulutustyyppi,
                                koulutusmoduuli.lukiolinja, koulutusmoduuli.koulutusohjelmaKoodi, komoto.tarjoaja,
                                komoto.pohjakoulutusvaatimus)));        
    }

    @Override
    public KoulutusIndexEntity findKoulutusById(Long koulutusmoduuliToteutusId) {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        final QKoulutusmoduuli koulutusmoduuli = QKoulutusmoduuli.koulutusmoduuli;
        return q(komoto)
                .join(komoto.koulutusmoduuli, koulutusmoduuli)
                .where(komoto.id.eq(koulutusmoduuliToteutusId))
                .singleResult(
                        (new QKoulutusIndexEntity(komoto.id, komoto.oid, komoto.koulutuksenAlkamisPvm, komoto.tila,
                                koulutusmoduuli.koulutustyyppi, koulutusmoduuli.oid, koulutusmoduuli.koulutusKoodi,
                                koulutusmoduuli.tutkintonimike, koulutusmoduuli.koulutustyyppi,
                                koulutusmoduuli.lukiolinja, koulutusmoduuli.koulutusohjelmaKoodi, komoto.tarjoaja,
                                komoto.pohjakoulutusvaatimus)));        
    }

    private BooleanBuilder bb(Predicate initial){
        return new BooleanBuilder(initial);
    }

    private JPAQuery q(EntityPath<?> entityPath){
        return new JPAQuery(entityManager).from(entityPath);
    }

    @Override
    public List<Long> findAllKoulutusIds() {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        final QKoulutusmoduuli komo = QKoulutusmoduuli.koulutusmoduuli;
        final Predicate where = bb(komo.lukiolinja.isNotNull()).or(komo.koulutusohjelmaKoodi.isNotNull()).getValue();
        return q(komoto).join(komoto.koulutusmoduuli, komo).where(where).list(komoto.id);
    }

    @Override
    public List<HakukohdeIndexEntity> findhakukohteetByKoulutusmoduuliToteutusId(
            Long koulutusmoduuliToteutusId) {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        return q(komoto).join(komoto.hakukohdes, hakukohde).where(komoto.id.eq(koulutusmoduuliToteutusId)).list(new QHakukohdeIndexEntity(hakukohde.id, hakukohde.oid));
    }

    @Override
    public List<String> findKoulutusLajisForKoulutus(
            Long koulutusmoduuliToteutusId) {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        
        return q(komoto).join(komoto.koulutuslajis, QKoodistoUri.koodistoUri).where(komoto.id.eq(koulutusmoduuliToteutusId)).list(QKoodistoUri.koodistoUri.koodiUri);
    }
}
