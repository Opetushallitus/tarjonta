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
package fi.vm.sade.tarjonta.koodisto.sync;


import org.springframework.beans.factory.annotation.Autowired;

import fi.vm.sade.tarjonta.koodisto.service.KoodiBusinessService;

/**
 * Mapping from Koodisto synchronization to local persistence.
 *
 * @author Jukka Raanamo
 */
public class KoodistoSyncHandler implements KoodistoSyncTask.KoodistoSyncTaskListener {

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Override
    public void onSyncFailed(String spec, Exception e) {
        // no-op
    }

    @Override
    public void onSyncLoaded(String specString, KoodistoSyncTask.KoodistoSyncResult result) {

        final String koodistoUri = result.getCriteria().getKoodistoUri();
        final int koodistoVersio = result.getCriteria().getKoodistoVersio();

        koodiBusinessService.batchImportKoodis(koodistoUri, koodistoVersio, result.getKoodis());

    }

}

