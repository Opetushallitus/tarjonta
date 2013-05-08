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
            String name,
            String hakuOid,
            TarjontaAdminService tarjontaAdminService,
            TarjontaPublicService tarjontaPublicService,
            int loiItemCountPerOrganisation) {
        super(name);
        this.hakuOid = hakuOid;
        this.maxKomotosPerOrganisation = loiItemCountPerOrganisation;
        this.komotoOids = new ArrayList<String>();

        komoto = new KomotoGenerator(tarjontaAdminService, tarjontaPublicService);
        hakukohde = new HakukohdeGenerator(tarjontaAdminService);
    }

    @Override
    public void run() {
        LOG.info("Thread start {}", getName());
        for (int i = 0; i < AbstractGenerator.randomIntByRange(1, maxKomotosPerOrganisation); i++) {
            komotoOids.add(komoto.create(getOrganisationOid()));
        }

        for (Integer koodiarvo : HakukohdeGenerator.HAKUKOHTEET_KOODISTO_ARVO) {
            hakukohde.create(hakuOid, koodiarvo, komotoOids);
        }

        LOG.info("Thread end {}", getName());
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
