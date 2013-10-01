/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.LocalisationDAO;
import fi.vm.sade.tarjonta.model.Localisation;
import fi.vm.sade.tarjonta.model.QLocalisation;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mlyly
 */
@Repository
public class LocalisationDAOImpl extends AbstractJpaDAOImpl<Localisation, Long> implements LocalisationDAO {

    @Override
    public List<Localisation> findByKey(String key) {
        if (key == null) {
            key = "";
        }

        QLocalisation qLocalisation = QLocalisation.localisation;

        JPAQuery from = new JPAQuery(getEntityManager()).from(qLocalisation);

        List<Localisation> localisations = from
                .where(qLocalisation.key.eq(key))
                .list(qLocalisation);

        return localisations;
    }

    @Override
    public List<Localisation> findByKeyPrefix(String key) {
        QLocalisation qLocalisation = QLocalisation.localisation;

        JPAQuery from = new JPAQuery(getEntityManager()).from(qLocalisation);

        List<Localisation> localisations = from
                .where(qLocalisation.key.like(key + "%"))
                .list(qLocalisation);

        return localisations;
    }

    @Override
    public Localisation findByKeyAndLocale(String key, String language) {
        QLocalisation qLocalisation = QLocalisation.localisation;

        JPAQuery from = new JPAQuery(getEntityManager()).from(qLocalisation);

        Localisation localisations = from
                .where(qLocalisation.key.eq(key).and(qLocalisation.language.eq(language)))
                .uniqueResult(qLocalisation);

        return localisations;
    }

    @Override
    public List<Localisation> findByKeyPrefixAndLocale(String key, String language) {
        QLocalisation qLocalisation = QLocalisation.localisation;

        JPAQuery from = new JPAQuery(getEntityManager()).from(qLocalisation);

        List<Localisation> localisations = from
                .where(qLocalisation.key.like(key + "%").and(qLocalisation.language.eq(language)))
                .list(qLocalisation);

        return localisations;
    }
}
