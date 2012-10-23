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
package fi.vm.sade.tarjonta.koodisto.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.SubQueryExpression;
import com.mysema.query.types.expr.BooleanExpression;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;

import fi.vm.sade.koodisto.service.types.common.KoodiType;

import fi.vm.sade.tarjonta.koodisto.model.Koodi;
import fi.vm.sade.tarjonta.koodisto.model.QKoodi;
import fi.vm.sade.tarjonta.koodisto.service.KoodiBusinessService;
import fi.vm.sade.tarjonta.koodisto.sync.KoodistoSyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jukka Raanamo
 */
@Service
@Transactional
public class KoodiBusinessServiceImpl implements KoodiBusinessService {

    @PersistenceContext
    private EntityManager em;

    private static final Logger log = LoggerFactory.getLogger(KoodiBusinessService.class);

    private static final SubQueryExpression sMaxKoodiVersioSubQuery;

    private static final SubQueryExpression sMaxKoodistoVersioSubQuery;

    private static final BooleanExpression sEqMaxKoodiVersio;

    private static final BooleanExpression sEqMaxKoodistoVersio;

    static {

        sMaxKoodiVersioSubQuery = new JPASubQuery().from(QKoodi.koodi).
            unique(QKoodi.koodi.koodiVersio.max());
        sMaxKoodistoVersioSubQuery = new JPASubQuery().from(QKoodi.koodi).
            unique(QKoodi.koodi.koodistoVersio.max());

        sEqMaxKoodiVersio = QKoodi.koodi.koodiVersio.eq(sMaxKoodiVersioSubQuery);
        sEqMaxKoodistoVersio = QKoodi.koodi.koodistoVersio.eq(sMaxKoodistoVersioSubQuery);

    }

    @Override
    public Koodi findByKoodiUri(String koodiUri) {

        QKoodi koodi = QKoodi.koodi;
        BooleanExpression koodiUriEq = koodi.koodiUri.eq(koodiUri);

        return from(koodi).
            where(koodiUriEq, sEqMaxKoodiVersio).
            singleResult(koodi);

    }

    @Override
    public List<Koodi> findKoodisByKoodistoUri(String koodistoUri) {

        QKoodi koodi = QKoodi.koodi;
        BooleanExpression koodistoUriEq = koodi.koodistoUri.eq(koodistoUri);

        return from(koodi).where(koodistoUriEq, sEqMaxKoodistoVersio).
            list(koodi);

    }

    public Koodi findById(String id) {

        return em.find(Koodi.class, id);

    }

    @Override
    public Koodi updateOrInsert(Koodi koodi) {

        final Koodi existingKoodi = findById(Koodi.makeId(koodi.getKoodiUri(), koodi.getKoodiVersio()));

        if (existingKoodi != null) {

            return em.merge(new Koodi(koodi));

        } else {

            em.persist(koodi);
            return koodi;

        }

    }

    @Override
    public List<Koodi> searchKoodis(SearchKoodisCriteriaType criteria) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void batchImportKoodis(String koodistoUri, Integer koodistoVersion,
        List<KoodiType> koodis) {

        if (log.isInfoEnabled()) {
            log.info("importing koodis, koodistoUri: " + koodistoUri + ", koodistoVersion: " + koodistoVersion
                + ", num koodis: " + koodis.size());
        }


        for (KoodiType koodi : koodis) {
            Koodi model = new Koodi(koodistoUri, koodistoVersion, koodi);
            updateOrInsert(model);
        }

    }

    protected JPAQuery from(EntityPath<?>... o) {

        return new JPAQuery(em).from(o);

    }

}

