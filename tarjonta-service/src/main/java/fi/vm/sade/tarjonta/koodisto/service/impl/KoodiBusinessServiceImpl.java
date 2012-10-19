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

import fi.vm.sade.koodisto.service.types.common.KoodiType;

import fi.vm.sade.tarjonta.koodisto.model.Koodi;
import fi.vm.sade.tarjonta.koodisto.model.QKoodi;
import fi.vm.sade.tarjonta.koodisto.service.KoodiBusinessService;
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

    private static final SubQueryExpression sMaxKoodiVersionSubQuery;

    private static final BooleanExpression sEqMaxKoodiVersion;

    static {

        sMaxKoodiVersionSubQuery = new JPASubQuery().from(QKoodi.koodi).
            unique(QKoodi.koodi.koodiVersio.max());

        sEqMaxKoodiVersion = QKoodi.koodi.koodiVersio.eq(sMaxKoodiVersionSubQuery);

    }

    @Override
    public Koodi findByKoodiUri(String koodiUri) {

        QKoodi koodi = QKoodi.koodi;
        BooleanExpression koodiUriEq = koodi.koodiUri.eq(koodiUri);

        return from(koodi).
            where(koodiUriEq, sEqMaxKoodiVersion).
            singleResult(koodi);

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
    public void batchImportKoodis(String koodistoUri, Integer koodistoVersion,
        List<KoodiType> koodis) {

        for (KoodiType koodi : koodis) {
            Koodi model = new Koodi(koodistoUri, koodistoVersion, koodi);
            updateOrInsert(model);
        }

    }

    protected JPAQuery from(EntityPath<?>... o) {

        return new JPAQuery(em).from(o);

    }

}

