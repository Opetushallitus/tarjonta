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
import com.google.common.collect.Sets;
import com.mysema.query.jpa.impl.JPADeleteClause;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;
import com.mysema.query.types.EntityPath;
import org.springframework.stereotype.Repository;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.MassakopiointiDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Massakopiointi;
import fi.vm.sade.tarjonta.model.TarjontaBaseEntity;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fi.vm.sade.tarjonta.model.QMassakopiointi;
import fi.vm.sade.tarjonta.service.copy.MetaObject;
import java.util.Date;

/**
 * @author Jani
 */
@Repository
public class MassakopiontiDAOImpl extends AbstractJpaDAOImpl<Massakopiointi, Long> implements MassakopiointiDAO {

    private static final Logger LOG = LoggerFactory.getLogger(MassakopiontiDAOImpl.class);

    @Override
    public String convertToJson(final TarjontaBaseEntity entityToJson) throws IOException {
        return convertAnyObjectToJson(entityToJson);
    }

    private String convertAnyObjectToJson(final Object obj) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Writer strWriter = new StringWriter();
        mapper.writeValue(strWriter, obj);
        return strWriter.toString();
    }

    @Override
    public Object convertToEntity(final String json, final Class clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }

    @Override
    public Object find(final String hakuOid, final String oid, Class clazz) {
        Object object = null;
        Massakopiointi result = find(hakuOid, oid);

        if (result == null) {
            LOG.info("No item found by oid '{}' class : json : '{}'", oid, clazz);
        } else {
            try {
                object = convertToEntity(result.getJson(), clazz);
            } catch (IOException e) {
                LOG.error("Convert json to entity failed by oid '{}' class : json : '{}'", oid, clazz, e);
            }
        }

        return object;
    }

    @Override
    public Massakopiointi find(final String hakuOid, final String oid) {
        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        return from(kopiointi).where(kopiointi.hakuOid.eq(hakuOid).and(kopiointi.oid.eq(oid))).uniqueResult(kopiointi);

    }

    @Override
    public List<Massakopiointi> findByHakuOid(final String hakuOid) {
        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        return from(kopiointi).where(kopiointi.hakuOid.eq(hakuOid)).list(kopiointi);
    }

    @Override
    public List<Massakopiointi> findByHakuOidAndOids(final String hakuOid, final List<String> oids) {
        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        return from(kopiointi).where(kopiointi.hakuOid.eq(hakuOid).and(kopiointi.oid.in(oids))).list(kopiointi);
    }

    @Override
    public void saveEntityAsJson(final String hakuOid, final String oid, final Massakopiointi.Tyyppi type, final Class clazz, final TarjontaBaseEntity entityToJson, final MetaObject meta) {
        if (meta == null) {
            saveFullEntity(hakuOid, oid, type, clazz, entityToJson, null);
        } else {
            try {
                saveFullEntity(hakuOid, oid, type, clazz, entityToJson, convertAnyObjectToJson(meta));
            } catch (IOException e) {
                LOG.error("Convert meta data object to json failed '{}'", meta);
            }
        }
    }

    private void saveFullEntity(final String hakuOid, final String oid, final Massakopiointi.Tyyppi type, final Class clazz, final TarjontaBaseEntity entityToJson, final String meta) {
        Preconditions.checkNotNull(hakuOid, "Haku OID cannot be null.");
        Preconditions.checkNotNull(oid, "Generic OID cannot be null.");
        Preconditions.checkNotNull(type, "Tyyppi enum cannot be null.");
        Preconditions.checkNotNull(clazz, "Class instance cannot be null.");
        Preconditions.checkNotNull(entityToJson, "Entity instance cannot be null.");

        //clear entity id
        entityToJson.setId(null);

        //clear oids and set object status to copied
        if (entityToJson instanceof Hakukohde) {
            Hakukohde hk = (Hakukohde) entityToJson;
            hk.setTila(TarjontaTila.KOPIOITU);
            hk.setKoulutusmoduuliToteutuses(Sets.<KoulutusmoduuliToteutus>newHashSet());
        } else if (entityToJson instanceof KoulutusmoduuliToteutus) {
            KoulutusmoduuliToteutus kt = (KoulutusmoduuliToteutus) entityToJson;
            kt.setTila(TarjontaTila.KOPIOITU);
        }

        try {
            Massakopiointi m = new Massakopiointi(hakuOid, oid, type, convertToJson(entityToJson), meta);
            m.setKopioinninTila(Massakopiointi.KopioinninTila.READY_FOR_COPY);
            insert(m);
        } catch (IOException e) {
            LOG.error("Convert entity to json failed by oid '{}' class : json : '{}'", oid, clazz, e);
        }
    }

    @Override
    public long updateTila(final String hakuOid, final String oid, final Massakopiointi.KopioinninTila toTila, final Date updated) {
        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        JPAUpdateClause komotoUpdate = new JPAUpdateClause(getEntityManager(), kopiointi);

        return komotoUpdate.
                where(kopiointi.hakuOid.eq(hakuOid).and(kopiointi.oid.eq(oid))).
                set(kopiointi.updated, updated).
                set(kopiointi.tila, toTila).execute();
    }

    @Override
    public long deleteAllByHakuOid(final String hakuOid) {
        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        JPADeleteClause komotoUpdate = new JPADeleteClause(getEntityManager(), kopiointi);
        return komotoUpdate.where(kopiointi.hakuOid.eq(hakuOid)).execute();
    }

    @Override
    public long deleteByHakuOidAndKopioinninTila(final String hakuOid, Massakopiointi.KopioinninTila tila) {
        QMassakopiointi kopiointi = QMassakopiointi.massakopiointi;
        JPADeleteClause komotoUpdate = new JPADeleteClause(getEntityManager(), kopiointi);
        return komotoUpdate.where(kopiointi.hakuOid.eq(hakuOid).and(kopiointi.tila.eq(tila))).execute();
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }
}
