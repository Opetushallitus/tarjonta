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
package fi.vm.sade.tarjonta.data.test;

import fi.vm.sade.tarjonta.data.test.modules.AbstractGenerator;
import fi.vm.sade.tarjonta.data.test.modules.HakukohdeGenerator;
import fi.vm.sade.tarjonta.data.test.modules.KomotoGenerator;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class ThreadedDataUploader extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadedDataUploader.class);
    private String organisationOid;
    private String hakuOid;
    private KomotoGenerator komoto;
    private HakukohdeGenerator hakukohde;
    private int maxKomotosPerOrganisation;
    private List<String> komotoOids;

    public ThreadedDataUploader(
            String threadName,
            String hakuOid,
            TarjontaAdminService tarjontaAdminService,
            TarjontaPublicService tarjontaPublicService,
            int loiItemCountPerOrganisation) {
        super(threadName);
        this.hakuOid = hakuOid;
        this.maxKomotosPerOrganisation = loiItemCountPerOrganisation;
        this.komotoOids = new ArrayList<String>();

        komoto = new KomotoGenerator(threadName, tarjontaAdminService, tarjontaPublicService);
        hakukohde = new HakukohdeGenerator(tarjontaAdminService);
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        LOG.info("Thread start {}", getName());
        final int maxKomotos = AbstractGenerator.randomIntByRange(1, maxKomotosPerOrganisation);
        for (int i = 0; i < maxKomotos; i++) {
            komotoOids.add(komoto.create(getOrganisationOid()));
        }

        final int maxHakukohdes = AbstractGenerator.randomIntByRange(2, HakukohdeGenerator.HAKUKOHTEET_KOODISTO_ARVO.length - 1);
        for (int i = 0; i < maxHakukohdes; i++) {
            hakukohde.create(hakuOid, HakukohdeGenerator.HAKUKOHTEET_KOODISTO_ARVO[i], komotoOids);
        }

        long timePassed = System.currentTimeMillis() - startTime;
        LOG.info("Thread end {}, time passed {} seconds", getName(), TimeUnit.MILLISECONDS.toSeconds(timePassed));
    }

    /**
     * @return the organisationOid
     */
    public String getOrganisationOid() {
        return organisationOid;
    }

    /**
     * @param organisationOid the organisationOid to set
     */
    public void setOrganisationOid(String organisationOid) {
        this.organisationOid = organisationOid;
    }
}
