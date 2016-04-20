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
import com.mysema.commons.lang.Pair;
import com.mysema.query.jpa.impl.JPADeleteClause;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.expr.BooleanExpression;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.MassakopiointiDAO;
import fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils;
import fi.vm.sade.tarjonta.model.Massakopiointi;
import fi.vm.sade.tarjonta.model.TarjontaBaseEntity;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fi.vm.sade.tarjonta.model.QMassakopiointi;
import fi.vm.sade.tarjonta.service.copy.EntityToJsonHelper;
import fi.vm.sade.tarjonta.service.copy.MetaObject;
import java.util.Date;

/**
 * @author Jani
 */
@Repository
public class MassakopiontiDAOImpl extends AbstractJpaDAOImpl<Massakopiointi, Long> implements MassakopiointiDAO {

    private static final Logger LOG = LoggerFactory.getLogger(MassakopiontiDAOImpl.class);

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    private String convertToJson(final Object obj) {
        Preconditions.checkNotNull(obj, "Instance of object cannot be null.");
        return EntityToJsonHelper.convertToJson(obj);
    }

    public Pair<Object, MetaObject> convertToEntity(final String json, final Class clazz, String meta) {
        Preconditions.checkNotNull(clazz, "Class instance cannot be null.");
        Preconditions.checkNotNull(json, "Json  cannot be null.");
        return new Pair(EntityToJsonHelper.convertToEntity(json, clazz), meta != null ? EntityToJsonHelper.convertToEntity(meta, MetaObject.class) : null);
    }

    @Override
    public List<Massakopiointi> search(final SearchCriteria search) {
        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        return from(kopiointi).where(query(search, kopiointi)).list(kopiointi);
    }

    @Override
    public List<String> searchOids(final SearchCriteria search) {
        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        return from(kopiointi).where(query(search, kopiointi)).list(kopiointi.oldOid);
    }

    public static BooleanExpression query(final SearchCriteria search, QMassakopiointi kopiointi) {

        BooleanExpression expression = null;
        if (search.getHakuOid() != null) {
            expression = QuerydslUtils.and(expression, kopiointi.hakuOid.eq(search.getHakuOid()));
        }

        if (search.getOldOid() != null) {
            expression = QuerydslUtils.and(expression, kopiointi.oldOid.eq(search.getOldOid()));
        }

        if (search.getNewOid() != null) {
            expression = QuerydslUtils.and(expression, kopiointi.newOid.eq(search.getNewOid()));
        }

        if (search.getTyyppi() != null) {
            expression = QuerydslUtils.and(expression, kopiointi.type.eq(search.getTyyppi()));
        }

        if (search.getProcessId() != null) {
            expression = QuerydslUtils.and(expression, kopiointi.processId.eq(search.getProcessId()));
        }

        if (search.getTila() != null) {
            expression = QuerydslUtils.and(expression, kopiointi.tila.eq(search.getTila()));
        }

        Preconditions.checkNotNull(expression, "An invalid search criteria, no parameters defined.");
        return expression;
    }

    @Override
    public Pair<Object, MetaObject> find(final String processId, final String oldOid, Class clazz) {
        Preconditions.checkNotNull(clazz, "Class instance cannot be null.");
        Massakopiointi result;

        if (processId == null) {
            result = findByOldOid(oldOid);
        }
        else {
            result = find(processId, oldOid);
        }

        if (result == null || result.getJson() == null) {
            if(processId != null) {
                LOG.warn("No item found by oid '{}' class : json : '{}'", oldOid, clazz);
            }
            return null;
        }

        return convertToEntity(result.getJson(), clazz, result.getMeta());
    }

    @Override
    public Massakopiointi find(final String processId, final String oldOid) {
        Preconditions.checkNotNull(processId, "Process ID cannot be null.");
        Preconditions.checkNotNull(oldOid, "Generic OID cannot be null.");

        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        return from(kopiointi).where(kopiointi.processId.eq(processId).and(kopiointi.oldOid.eq(oldOid))).uniqueResult(kopiointi);
    }

    @Override
    public Massakopiointi findFirstKomo(String processId, String komoOid) {
        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;

        return from(kopiointi).where(kopiointi.processId.eq(processId)
                .and(kopiointi.oldOid.startsWith(komoOid + "_")))
                .orderBy(kopiointi.id.asc()).singleResult(kopiointi);
    }

    @Override
    public String findNewOid(final String processId, final String oldOid) {
        Preconditions.checkNotNull(processId, "Process ID cannot be null.");
        Preconditions.checkNotNull(oldOid, "Generic OID cannot be null.");

        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        String newOidFromSameProcessCopy = from(kopiointi)
                                            .where(kopiointi.processId.eq(processId)
                                                    .and(kopiointi.oldOid.eq(oldOid)))
                                            .uniqueResult(kopiointi.newOid);

        if (newOidFromSameProcessCopy != null) {
            return newOidFromSameProcessCopy;
        }

        // Komotot kopioidaan vain yhden kerran: tarkistetaan, onko komoto jo aiemmin
        // kopioitu jonkun toisen haun kopioinnin yhteydessä
        Massakopiointi prevMassCopyRow = findByOldOid(oldOid);
        if (prevMassCopyRow == null) {
            return null;
        }

        KoulutusmoduuliToteutus previouslyCopiedKomoto = koulutusmoduuliToteutusDAO.findByOid(prevMassCopyRow.getNewOid());
        if (previouslyCopiedKomoto == null || TarjontaTila.POISTETTU.equals(previouslyCopiedKomoto.getTila())) {
            return null;
        }

        return previouslyCopiedKomoto.getOid();
    }

    public Massakopiointi findByOldOid(String oldOid) {
        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        return from(kopiointi)
                .where(kopiointi.oldOid.eq(oldOid))
                .orderBy(kopiointi.id.asc())
                .singleResult(kopiointi);
    }

    @Override
    public List<Massakopiointi> findByHakuOid(final String hakuOid) {
        Preconditions.checkNotNull(hakuOid, "Haku OID cannot be null.");

        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        return from(kopiointi).where(kopiointi.hakuOid.eq(hakuOid)).list(kopiointi);
    }

    @Override
    public long rowCount(final String processId, final String hakuOid) {
        Preconditions.checkNotNull(processId, "Process ID cannot be null.");
        Preconditions.checkNotNull(hakuOid, "Haku OID cannot be null.");

        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        return from(kopiointi).where(kopiointi.processId.eq(processId).and(kopiointi.hakuOid.eq(hakuOid))).singleResult(kopiointi.id.count());
    }

    @Override
    public List<Massakopiointi> findByHakuOidAndOids(final String hakuOid, final List<String> oids) {
        Preconditions.checkNotNull(hakuOid, "Haku OID cannot be null.");

        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        return from(kopiointi).where(kopiointi.hakuOid.eq(hakuOid).and(kopiointi.oldOid.in(oids))).list(kopiointi);
    }

    @Override
    public void saveEntityAsJson(final String hakuOid, final String oldOid, String newOid, String processId, final Massakopiointi.Tyyppi type, final Class clazz, final TarjontaBaseEntity entityToJson, final MetaObject meta) {
        if (meta == null) {
            saveFullEntity(hakuOid, oldOid, newOid, processId, type, clazz, entityToJson, null);
        } else {
            saveFullEntity(hakuOid, oldOid, newOid, processId, type, clazz, entityToJson, convertToJson(meta));
        }
    }

    private void saveFullEntity(
            final String hakuOid,
            final String oldOid,
            final String newOid,
            final String processId,
            final Massakopiointi.Tyyppi type,
            final Class clazz,
            final TarjontaBaseEntity entityToJson,
            final String meta) {
        Preconditions.checkNotNull(hakuOid, "Haku OID cannot be null.");
        Preconditions.checkNotNull(oldOid, "Original OID cannot be null.");
        Preconditions.checkNotNull(newOid, "New OID cannot be null.");
        Preconditions.checkNotNull(type, "Tyyppi enum cannot be null.");
        Preconditions.checkNotNull(clazz, "Class instance cannot be null.");

        String entityAsJson = null;
        if (entityToJson != null) {
            entityAsJson = convertToJson(entityToJson);
        }

        Massakopiointi m = new Massakopiointi(hakuOid, oldOid, newOid, processId, type, entityAsJson, meta);
        m.setKopioinninTila(Massakopiointi.KopioinninTila.READY_FOR_COPY);
        insert(m);
    }

    @Override
    public long updateTila(final String processId, final String oldOid, final Massakopiointi.KopioinninTila toTila, final Date updated) {
        Preconditions.checkNotNull(processId, "Process ID cannot be null.");
        Preconditions.checkNotNull(oldOid, "Generic OID cannot be null.");
        Preconditions.checkNotNull(toTila, "Status enum cannot be null.");
        Preconditions.checkNotNull(updated, "Update date cannot be null.");

        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        JPAUpdateClause komotoUpdate = new JPAUpdateClause(getEntityManager(), kopiointi);

        return komotoUpdate.
                where(kopiointi.processId.eq(processId).and(kopiointi.oldOid.eq(oldOid))).
                set(kopiointi.updated, updated).
                set(kopiointi.tila, toTila).execute();
    }

    @Override
    public long deleteAllByHakuOid(final String hakuOid) {
        Preconditions.checkNotNull(hakuOid, "Haku OID cannot be null.");

        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        JPADeleteClause komotoUpdate = new JPADeleteClause(getEntityManager(), kopiointi);
        return komotoUpdate.where(kopiointi.hakuOid.eq(hakuOid)).execute();
    }

    @Override
    public long deleteByHakuOidAndKopioinninTila(final String hakuOid, Massakopiointi.KopioinninTila tila) {
        Preconditions.checkNotNull(hakuOid, "Haku OID cannot be null.");
        Preconditions.checkNotNull(tila, "Status enum cannot be null.");

        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        JPADeleteClause komotoUpdate = new JPADeleteClause(getEntityManager(), kopiointi);
        return komotoUpdate.where(kopiointi.hakuOid.eq(hakuOid).and(kopiointi.tila.eq(tila))).execute();
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }
}
