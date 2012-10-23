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
package fi.vm.sade.tarjonta.koodisto.service;

import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.koodisto.model.Koodi;
import java.util.List;

/**
 * Business service contract for reading and storing locally stored Koodisto Service data.
 * The primary function for this service was to provide local copies from centrally managed
 * Koodisto data. It should be noted that the Koodisto Service will always maintain the
 * master and most up-to-date versions of the data.
 *
 * @author Jukka Raanamo
 */
public interface KoodiBusinessService {

    /**
     * Returns latest koodi for given koodiUri.
     *
     * @param koodiUri
     * @return
     */
    public Koodi findByKoodiUri(String koodiUri);

    /**
     * Returns latest koodi's from given koodisto.
     *
     * @param koodistoUri
     * @return
     */
    public List<Koodi> findKoodisByKoodistoUri(String koodistoUri);

    /**
     * Inserts or update a single Koodi
     *
     * @param template values to import
     * @return
     */
    public Koodi updateOrInsert(Koodi template);

    /**
     *
     * @param koodistoUri
     * @param koodistoVersion
     * @param koodis
     */
    public void batchImportKoodis(String koodistoUri, Integer koodistoVersion, List<KoodiType> koodis);

    /**
     * Search koodis using criteria from Koodisto api. Note that not all search parameters are supported.
     *
     * @param criteria
     * @return
     */
    public List<Koodi> searchKoodis(SearchKoodisCriteriaType criteria);

}

