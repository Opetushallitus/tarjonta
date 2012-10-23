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

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import java.util.List;

/**
 *
 * @author Jukka Raanamo
 */
public class SimpleSyncTaskListener implements KoodistoSyncTask.KoodistoSyncTaskListener {

    private int countOnSynCalled;

    private int countOnFailedCalled;

    private Exception exception;

    private KoodistoSyncTask.KoodistoSyncResult result;

    private String spec;

    @Override
    public void onSyncFailed(String spec, Exception e) {
        this.spec = spec;
        this.exception = e;
        this.countOnFailedCalled++;
    }

    @Override
    public void onSyncLoaded(String spec, KoodistoSyncTask.KoodistoSyncResult result) {
        this.exception = null;
        this.spec = spec;
        this.countOnSynCalled++;
        this.result = result;
    }

    public int getCountOnFailedCalled() {
        return countOnFailedCalled;
    }

    public int getCountOnSynCalled() {
        return countOnSynCalled;
    }

    public Exception getException() {
        return exception;
    }

    public List<KoodiType> getKoodis() {
        return result.getKoodis();
    }

    public KoodistoSyncTask.KoodistoSyncResult getResult() {
        return result;
    }

    public String getSpec() {
        return spec;
    }

}

