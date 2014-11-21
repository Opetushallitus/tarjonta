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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;

import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.model.Koulutustarjoaja;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.DateTimePath;
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.model.index.HakuAikaIndexEntity;
import fi.vm.sade.tarjonta.model.index.HakukohdeIndexEntity;
import fi.vm.sade.tarjonta.model.index.KoulutusIndexEntity;
import fi.vm.sade.tarjonta.model.index.QHakuAikaIndexEntity;
import fi.vm.sade.tarjonta.model.index.QHakukohdeIndexEntity;
import fi.vm.sade.tarjonta.model.index.QKoulutusIndexEntity;

@Repository
public class IndexerDaoImpl implements IndexerDAO {

    private final DateTimePath<Date> ALKAMISPVM = Expressions.dateTimePath(Date.class, "alkamispvm");

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Override
    public List<HakukohdeIndexEntity> findAllHakukohteet() {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        final QHaku haku = QHaku.haku;
        return q(hakukohde)
                .join(hakukohde.haku, haku)
                .list(
                        new QHakukohdeIndexEntity(hakukohde.id, hakukohde.oid, hakukohde.hakukohdeNimi,
                                haku.koulutuksenAlkamiskausiUri, haku.koulutuksenAlkamisVuosi, hakukohde.tila, haku.hakutapaUri,
                                hakukohde.aloituspaikatLkm, haku.id, haku.oid, haku.hakutyyppiUri, hakukohde.organisaatioRyhmaOids));
    }

    @Override
    public List<KoulutusIndexEntity> findKoulutusmoduuliToteutusesByHakukohdeId(Long hakukohdeId) {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        final QKoodistoUri koodistoUri = QKoodistoUri.koodistoUri;
        final QKoulutusmoduuli komo = QKoulutusmoduuli.koulutusmoduuli;

        return q(hakukohde).join(hakukohde.koulutusmoduuliToteutuses, komoto).join(
                komoto.koulutusmoduuli, komo).leftJoin(
                komoto.koulutuslajis, koodistoUri).where(
                hakukohde.id.eq(hakukohdeId)).list(
                new QKoulutusIndexEntity(komoto.oid, komoto.tarjoaja,
                        koodistoUri.koodiUri,
                        komoto.pohjakoulutusvaatimusUri,
                        komo.koulutustyyppiEnum,
                        komoto.toteutustyyppi,
                        komo.koulutusUri, komoto.alkamiskausiUri, komoto.alkamisVuosi));
    }

    @Override
    public HakukohdeIndexEntity findHakukohdeById(Long id) {
        final QHakukohde hakukohde = QHakukohde.hakukohde;

        final QHaku haku = QHaku.haku;
        return q(hakukohde)
                .join(hakukohde.haku, haku)
                .where(hakukohde.id.eq(id))
                .singleResult(
                        new QHakukohdeIndexEntity(hakukohde.id, hakukohde.oid, hakukohde.hakukohdeNimi,
                                haku.koulutuksenAlkamiskausiUri, haku.koulutuksenAlkamisVuosi, hakukohde.tila, haku.hakutapaUri,
                                hakukohde.aloituspaikatLkm, haku.id, haku.oid, haku.hakutyyppiUri, hakukohde.organisaatioRyhmaOids));
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
        /* query dsl query should be something like this in sql:
         select MAX(kta.alkamispvm), kt.id
         from koulutusmoduuli_toteutus kt left join koulutusmoduuli_toteutus_alkamispvm kta
         on kt.id=kta.koulutusmoduuli_toteutus_id group by kt.id, kta.koulutusmoduuli_toteutus_id
         */
        return q(komoto)
                .join(komoto.koulutusmoduuli, koulutusmoduuli)
                .leftJoin(komoto.koulutuksenAlkamisPvms, ALKAMISPVM).groupBy(
                        komoto.id, komoto.oid, komoto.tila,
                        koulutusmoduuli.koulutustyyppiEnum, komoto.toteutustyyppi, koulutusmoduuli.oid, koulutusmoduuli.koulutusUri,
                        koulutusmoduuli.lukiolinjaUri, koulutusmoduuli.koulutusohjelmaUri, koulutusmoduuli.osaamisalaUri, komoto.tarjoaja,
                        komoto.pohjakoulutusvaatimusUri, komoto.alkamiskausiUri, komoto.alkamisVuosi, komoto.koulutustyyppiUri
                )
                .list(
                        (new QKoulutusIndexEntity(komoto.id, komoto.oid, ALKAMISPVM.min(), ALKAMISPVM.max(), komoto.tila,
                                koulutusmoduuli.koulutustyyppiEnum, komoto.toteutustyyppi, koulutusmoduuli.oid, koulutusmoduuli.koulutusUri,
                                koulutusmoduuli.lukiolinjaUri, koulutusmoduuli.koulutusohjelmaUri, koulutusmoduuli.osaamisalaUri, komoto.tarjoaja,
                                komoto.pohjakoulutusvaatimusUri, komoto.alkamiskausiUri, komoto.alkamisVuosi, komoto.koulutustyyppiUri)));
    }

    @Override
    public KoulutusIndexEntity findKoulutusById(Long koulutusmoduuliToteutusId) {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        final QKoulutusmoduuli koulutusmoduuli = QKoulutusmoduuli.koulutusmoduuli;

        return q(komoto)
                .join(komoto.koulutusmoduuli, koulutusmoduuli)
                .leftJoin(komoto.koulutuksenAlkamisPvms, ALKAMISPVM).groupBy(
                        komoto.id, komoto.oid, komoto.tila,
                        koulutusmoduuli.koulutustyyppiEnum, komoto.toteutustyyppi, koulutusmoduuli.oid, koulutusmoduuli.koulutusUri,
                        koulutusmoduuli.lukiolinjaUri, koulutusmoduuli.koulutusohjelmaUri, koulutusmoduuli.osaamisalaUri, komoto.tarjoaja,
                        komoto.pohjakoulutusvaatimusUri, komoto.alkamiskausiUri, komoto.alkamisVuosi, komoto.koulutustyyppiUri
                )
                .where(komoto.id.eq(koulutusmoduuliToteutusId))
                .singleResult(
                        (new QKoulutusIndexEntity(komoto.id, komoto.oid, ALKAMISPVM.min(), ALKAMISPVM.max(), komoto.tila,
                                koulutusmoduuli.koulutustyyppiEnum, komoto.toteutustyyppi, koulutusmoduuli.oid, koulutusmoduuli.koulutusUri,
                                koulutusmoduuli.lukiolinjaUri, koulutusmoduuli.koulutusohjelmaUri, koulutusmoduuli.osaamisalaUri, komoto.tarjoaja,
                                komoto.pohjakoulutusvaatimusUri, komoto.alkamiskausiUri, komoto.alkamisVuosi, komoto.koulutustyyppiUri)));
    }

    private BooleanBuilder bb(Predicate initial) {
        return new BooleanBuilder(initial);
    }

    private JPAQuery q(EntityPath<?> entityPath) {
        return new JPAQuery(entityManager).from(entityPath);
    }

    @Override
    public List<Long> findAllKoulutusIds() {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        final QKoulutusmoduuli komo = QKoulutusmoduuli.koulutusmoduuli;
        final Predicate where = bb(komo.lukiolinjaUri.isNotNull()).or(komo.koulutusohjelmaUri.isNotNull().or(komoto.nimi.isNotNull()).or(komo.nimi.isNotNull())).getValue();
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

    @Override
    public MonikielinenTeksti getKomoNimi(Long koulutusId) {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        final QKoulutusmoduuli koulutusmoduuli = QKoulutusmoduuli.koulutusmoduuli;
        return q(komoto)
                .join(komoto.koulutusmoduuli, koulutusmoduuli).join(koulutusmoduuli.nimi).where(komoto.id.eq(koulutusId)).singleResult(koulutusmoduuli.nimi);
    }

    @Override
    public MonikielinenTeksti getKomotoNimi(Long koulutusId) {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        return q(komoto)
                .join(komoto.nimi).where(komoto.id.eq(koulutusId)).singleResult(komoto.nimi);
    }

    @Override
    public MonikielinenTeksti getNimiForHakukohde(Long hakukohdeId) {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        return q(hakukohde).join(hakukohde.hakukohdeMonikielinenNimi).where(hakukohde.id.eq(hakukohdeId)).singleResult(hakukohde.hakukohdeMonikielinenNimi);

    }

    /**
     * @see #findAllHakukohdeIds(), tämä metodi lisää timestamp tarkistuksen
     */
    @Override
    public List<Long> findUnindexedHakukohdeIds() {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        return q(hakukohde).where(hakukohde.viimIndeksointiPvm.isNull().or(hakukohde.viimIndeksointiPvm.before(hakukohde.lastUpdateDate))).limit(100).list(hakukohde.id);
    }

    /**
     * @see #findAllKoulutusIds(), tämä metodi lisää timestamp tarkistuksen
     */
    @Override
    public List<Long> findUnindexedKoulutusIds() {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        return q(komoto).where(komoto.viimIndeksointiPvm.isNull().or(komoto.viimIndeksointiPvm.before(komoto.updated))).limit(100).list(komoto.id);
    }

    @Override
    public void updateHakukohdeIndexed(Long id, Date time) {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        JPAUpdateClause u = new JPAUpdateClause(entityManager, hakukohde);
        u.set(hakukohde.viimIndeksointiPvm, time).where(hakukohde.id.eq(id)).execute();
    }

    @Override
    public void updateKoulutusIndexed(Long id, Date time) {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        JPAUpdateClause u = new JPAUpdateClause(entityManager, komoto);
        u.where(komoto.id.eq(id)).set(komoto.viimIndeksointiPvm, time).execute();
    }

    /**
     * Hakee tarjoajan/järjestäjän tyypin mukaan. Olisi varmaan parempi, jos tämän tiedon saisi suoraan
     * tuotua left joinilla findAllKoulutukset / findKoulutusById metodeissa
     *
     * @param oid
     * @param type
     * @return
     */
    @Override
    public Set<String> getOwners(String oid, String type) {

        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findKomotoByOid(oid);

        Set<KoulutusOwner> owners = komoto.getOwners();

        Set<String> ownerOids = new HashSet<String>();

        for (KoulutusOwner owner : owners) {
            if (owner.getOwnerType().equals(type)) {
                ownerOids.add(owner.getOwnerOid());
            }
        }

        // Fallback Vaadinta varten
        if (ownerOids.isEmpty()) {
            ownerOids.add(komoto.getTarjoaja());
        }

        return ownerOids;
    }

    @Override
    public MonikielinenTeksti getAloituspaikatKuvausForHakukohde(long hakukohdeId) {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        return q(hakukohde).join(hakukohde.aloituspaikatKuvaus).where(hakukohde.id.eq(hakukohdeId)).singleResult(hakukohde.aloituspaikatKuvaus);
    }
}
