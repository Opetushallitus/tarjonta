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
package fi.vm.sade.tarjonta.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.tarjonta.model.Localisation;
import java.util.List;

/**
 *
 * @author mlyly
 */
public interface LocalisationDAO extends JpaDAO<Localisation, Long> {

    /**
     * Find by key prefix.
     *
     * @param key key/key prefix
     * @return list of hits in every language
     */
    List<Localisation> findByKeyPrefix(String key);

    /**
     * Find by key.
     *
     * @param key
     * @return
     */
    List<Localisation> findByKey(String key);

    /**
     * Find by (exact) key and language.
     *
     * @param key a key AND language
     * @param key language
     * @return hit
     */
    Localisation findByKeyAndLocale(String key, String language);

    /**
     * Find by key (prefix) and language.
     *
     * @param key a key AND language
     * @param key language
     * @return hit
     */
    List<Localisation> findByKeyPrefixAndLocale(String key, String language);
}
