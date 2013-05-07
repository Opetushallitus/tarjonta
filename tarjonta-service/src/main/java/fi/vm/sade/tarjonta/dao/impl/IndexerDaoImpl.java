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

import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.model.index.HakuAikaIndexEntity;
import fi.vm.sade.tarjonta.model.index.HakukohdeIndexEntity;
import fi.vm.sade.tarjonta.model.index.KoulutusIndexEntity;

@Repository
public class IndexerDaoImpl implements IndexerDAO {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<HakukohdeIndexEntity> findAllHakukohteet() {
        String q = "select NEW " + HakukohdeIndexEntity.class.getName() + "(hakukohde.id, hakukohde.oid, hakukohde.hakukohdeNimi, haku.hakukausiUri, haku.hakukausiVuosi, hakukohde.tila, haku.hakutapaUri, hakukohde.aloituspaikatLkm, haku.id) from Hakukohde as hakukohde join hakukohde.haku as haku";
        return entityManager.createQuery(q, HakukohdeIndexEntity.class).getResultList();
    }

    @Override
    public List<KoulutusIndexEntity> findKoulutusmoduuliToteutusesByHakukohdeId(Long hakukohdeId) { 
        final String q="select NEW " + KoulutusIndexEntity.class.getName() + "(koulutusmoduulitoteutuses.oid, koulutusmoduulitoteutuses.tarjoaja) from Hakukohde as hakukohde join hakukohde.koulutusmoduuliToteutuses as koulutusmoduulitoteutuses where hakukohde.id= :hakukohdeId";
        return entityManager.createQuery(q, KoulutusIndexEntity.class).setParameter("hakukohdeId", hakukohdeId).getResultList();
    }

    @Override
    public HakukohdeIndexEntity findHakukohdeById(Long id) {
        String HAKUKOHDE_BY_ID = "select NEW " + HakukohdeIndexEntity.class.getName() + "(hakukohde.id, hakukohde.oid, hakukohde.hakukohdeNimi, haku.hakukausiUri, haku.hakukausiVuosi, hakukohde.tila, haku.hakutapaUri, hakukohde.aloituspaikatLkm, haku.id) from Hakukohde as hakukohde join hakukohde.haku as haku where hakukohde.id = :hakukohdeId";
        return entityManager.createQuery(HAKUKOHDE_BY_ID, HakukohdeIndexEntity.class).setParameter("hakukohdeId", id)
                .getSingleResult();
    }

    @Override
    public List<HakuAikaIndexEntity> findHakuajatForHaku(Long hakuId) {
        final String HAKU_BY_ID = "select NEW " + HakuAikaIndexEntity.class.getName() +"(hakuaika.alkamisPvm, hakuaika.paattymisPvm) from Haku as haku join haku.hakuaikas as hakuaika where haku.id=:hakuId";
        return entityManager.createQuery(HAKU_BY_ID, HakuAikaIndexEntity.class).setParameter("hakuId", hakuId)
                .getResultList();
    }
    
    @Override
    public List<Long> findAllHakukohdeIds() {
        String q = "select hakukohde.id from Hakukohde as hakukohde";
        return entityManager.createQuery(q, Long.class).getResultList();
    }
    
    @Override
    public List<KoulutusIndexEntity> findAllKoulutukset() {
        final String ALL_KOULUTUKSET = "select NEW " + KoulutusIndexEntity.class.getName() + "(koulutusmoduulitoteutus.id, koulutusmoduulitoteutus.oid, koulutusmoduulitoteutus.koulutuksenAlkamisPvm, koulutusmoduulitoteutus.tila, koulutusmoduuli.koulutustyyppi, koulutusmoduuli.oid, koulutusmoduuli.koulutusKoodi, koulutusmoduuli.tutkintonimike, koulutusmoduuli.koulutustyyppi, koulutusmoduuli.lukiolinja, koulutusmoduuli.koulutusohjelmaKoodi, koulutusmoduulitoteutus.tarjoaja) from KoulutusmoduuliToteutus koulutusmoduulitoteutus join koulutusmoduulitoteutus.koulutusmoduuli koulutusmoduuli where (koulutusmoduuli.lukiolinja is not NULL OR koulutusmoduuli.koulutusohjelmaKoodi is not NULL)";
        return entityManager.createQuery(ALL_KOULUTUKSET, KoulutusIndexEntity.class).getResultList();
    }

    @Override
    public KoulutusIndexEntity findKoulutusById(Long koulutusmoduuliToteutusId) {
        final String KOULUTUS_BY_ID = "select NEW " + KoulutusIndexEntity.class.getName() + "(koulutusmoduulitoteutus.id, koulutusmoduulitoteutus.oid, koulutusmoduulitoteutus.koulutuksenAlkamisPvm, koulutusmoduulitoteutus.tila, koulutusmoduuli.koulutustyyppi, koulutusmoduuli.oid, koulutusmoduuli.koulutusKoodi, koulutusmoduuli.tutkintonimike, koulutusmoduuli.koulutustyyppi, koulutusmoduuli.lukiolinja, koulutusmoduuli.koulutusohjelmaKoodi, koulutusmoduulitoteutus.tarjoaja) from KoulutusmoduuliToteutus koulutusmoduulitoteutus join koulutusmoduulitoteutus.koulutusmoduuli koulutusmoduuli where (koulutusmoduuli.lukiolinja is not NULL OR koulutusmoduuli.koulutusohjelmaKoodi is not NULL) and koulutusmoduulitoteutus.id= :koulutusmoduuliToteutusId";
        return entityManager.createQuery(KOULUTUS_BY_ID, KoulutusIndexEntity.class).setParameter("koulutusmoduuliToteutusId", koulutusmoduuliToteutusId)
                .getSingleResult();
    }

    @Override
    public List<Long> findAllKoulutusIds() {
        final String q="select koulutusmoduulitoteutus.id from KoulutusmoduuliToteutus koulutusmoduulitoteutus join koulutusmoduulitoteutus.koulutusmoduuli as koulutusmoduuli where (koulutusmoduuli.lukiolinja is not NULL OR koulutusmoduuli.koulutusohjelmaKoodi is not NULL)";
        return entityManager.createQuery(q, Long.class).getResultList();
    }

    @Override
    public List<HakukohdeIndexEntity> findhakukohteetByKoulutusmoduuliToteutusId(
            Long koulutusmoduuliToteutusId) {
        String q="select NEW " + HakukohdeIndexEntity.class.getName() + "(hakukohde.id, hakukohde.oid) from KoulutusmoduuliToteutus koulutusmoduulitoteutus join koulutusmoduulitoteutus.hakukohdes as hakukohde where koulutusmoduulitoteutus.id= :koulutusmoduuliToteutusId";
        return entityManager.createQuery(q, HakukohdeIndexEntity.class).setParameter("koulutusmoduuliToteutusId",  koulutusmoduuliToteutusId).getResultList();
    }

    @Override
    public List<String> findKoulutusLajisForKoulutus(
            Long koulutusmoduuliToteutusId) {
        String q="select koulutuslajis.koodiUri from KoulutusmoduuliToteutus as koulutusmoduulitoteutus join koulutusmoduulitoteutus.koulutuslajis as koulutuslajis where koulutusmoduulitoteutus.id= :koulutusmoduulitoteutusId";
        return entityManager.createQuery(q, String.class).setParameter("koulutusmoduulitoteutusId",  koulutusmoduuliToteutusId).getResultList();
    }

}
