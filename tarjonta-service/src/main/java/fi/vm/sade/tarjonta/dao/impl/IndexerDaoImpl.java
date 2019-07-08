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

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Predicate;
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.model.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Repository
public class IndexerDaoImpl implements IndexerDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Long> findAllHakukohdeIds() {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        return q(hakukohde).list(hakukohde.id);
    }

    @Override
    public List<Long> findAllKoulutusIds() {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        final QKoulutusmoduuli komo = QKoulutusmoduuli.koulutusmoduuli;
        final Predicate where = bb(komo.lukiolinjaUri.isNotNull()).or(komo.koulutusohjelmaUri.isNotNull().or(komoto.nimi.isNotNull()).or(komo.nimi.isNotNull())).getValue();
        return q(komoto).join(komoto.koulutusmoduuli, komo).where(where).list(komoto.id);
    }

    @Override
    public List<Long> findUnindexedHakukohdeIds() {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        return q(hakukohde).where(hakukohde.viimIndeksointiPvm.isNull().or(hakukohde.viimIndeksointiPvm.before(hakukohde.lastUpdateDate))).list(hakukohde.id);
    }

    @Override
    public List<Long> findUnindexedKoulutusIds() {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        return q(komoto).where(
                bb(komoto.viimIndeksointiPvm.isNull()
                        .or(komoto.viimIndeksointiPvm.before(komoto.updated)))
                        .and(komoto.alkamisVuosi.isNotNull())
        ).list(komoto.id);
    }

    @Override
    public Long setKoulutusViimindeksointiPvmToNull() {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        JPAUpdateClause u = new JPAUpdateClause(entityManager, komoto);
        return u.setNull(komoto.viimIndeksointiPvm).execute();
    }

    @Override
    public Long setHakukohdeViimindeksointiPvmToNull(Haku haku) {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        JPAUpdateClause u = new JPAUpdateClause(entityManager, hakukohde);
        u.where(hakukohde.haku.id.eq(haku.getId()));
        return u.setNull(hakukohde.viimIndeksointiPvm).execute();
    }

    @Override
    public Long setHakukohdeViimindeksointiPvmToNull() {
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        JPAUpdateClause u = new JPAUpdateClause(entityManager, hakukohde);
        return u.setNull(hakukohde.viimIndeksointiPvm).execute();
    }

    @Override
    public void updateHakukohteesIndexed(List<Long> ids, Date time) {
        if (ids != null && !ids.isEmpty()) {
            final QHakukohde hakukohde = QHakukohde.hakukohde;
            JPAUpdateClause u = new JPAUpdateClause(entityManager, hakukohde);
            u.set(hakukohde.viimIndeksointiPvm, time).where(hakukohde.id.in(ids)).execute();
        }
    }

    @Override
    public void updateKoulutuksesIndexed(List<Long> ids, Date time) {
        if (ids != null && !ids.isEmpty()) {
            final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
            JPAUpdateClause u = new JPAUpdateClause(entityManager, komoto);
            u.where(komoto.id.in(ids)).set(komoto.viimIndeksointiPvm, time).execute();
        }
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

    @Override
    public void reindexOrganizationChanges(List<String> organizationOids) {
        final QKoulutusmoduuliToteutus komoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        final QHakukohde hakukohde = QHakukohde.hakukohde;
        final QKoulutusOwner owner = QKoulutusOwner.koulutusOwner;

        List<Long> koulutusIds = q(komoto).leftJoin(komoto.owners, owner).where(
                bb(owner.ownerOid.in(organizationOids).or(komoto.tarjoaja.in(organizationOids))))
                .distinct().list(komoto.id);

        if (koulutusIds.isEmpty()) return;

        new JPAUpdateClause(entityManager, komoto)
                .where(komoto.id.in(koulutusIds))
                .setNull(komoto.viimIndeksointiPvm).execute();

        List<Long> hakukohdeIds = q(hakukohde).join(hakukohde.koulutusmoduuliToteutuses, komoto)
                                    .where(komoto.id.in(koulutusIds)).distinct().list(hakukohde.id);

        if (hakukohdeIds.isEmpty()) return;

        new JPAUpdateClause(entityManager, hakukohde)
                .where(hakukohde.id.in(hakukohdeIds))
                .setNull(hakukohde.viimIndeksointiPvm).execute();
    }

    private BooleanBuilder bb(Predicate initial) {
        return new BooleanBuilder(initial);
    }

    private JPAQuery q(EntityPath<?> entityPath) {
        return new JPAQuery(entityManager).from(entityPath);
    }
}
