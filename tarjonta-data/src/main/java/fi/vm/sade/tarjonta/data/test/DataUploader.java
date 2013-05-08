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

import com.google.common.base.Preconditions;
import fi.vm.sade.tarjonta.data.test.modules.HakukohdeGenerator;
import fi.vm.sade.tarjonta.data.test.modules.HakuGenerator;
import fi.vm.sade.tarjonta.data.test.modules.KomotoGenerator;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioOidListType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioOidType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchCriteriaDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchOidType;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Jani Wilén
 */
@Service
public class DataUploader {

    private static final String[] ACCEPTED_OPPILAITOSTYYPPIS = new String[]{
        "oppilaitostyyppi_22#1",
        "oppilaitostyyppi_23#1",
        "oppilaitostyyppi_24#1",
        "oppilaitostyyppi_29#1",
        "oppilaitostyyppi_15#1",
        "oppilaitostyyppi_19#1"
    };
    private static final Logger LOG = LoggerFactory.getLogger(DataUploader.class);
    private static int MAX_KOMOTO_THREADS = 10;
    @Autowired(required = true)
    private OrganisaatioService organisaatioService;
    @Autowired(required = true)
    private TarjontaAdminService tarjotantaAdminService;
    @Autowired(required = true)
    private TarjontaPublicService tarjotantaPublicService;
    private ThreadedDataUploader[] threads = new ThreadedDataUploader[MAX_KOMOTO_THREADS];

    public DataUploader() {
    }

    public void upload(final String organisationOid, final int loiItemCountPerOrganisation) throws InterruptedException {
        Preconditions.checkNotNull(organisationOid, "Organisation OID cannot be null.");
        HakuGenerator haku = new HakuGenerator(tarjotantaAdminService);
        final String hakuOid = haku.create();
        List<OrganisaatioPerustietoType> filtteredOrgs = new ArrayList<OrganisaatioPerustietoType>();

        for (String oppilaitostyyppi : ACCEPTED_OPPILAITOSTYYPPIS) {
            OrganisaatioSearchCriteriaDTO search = new OrganisaatioSearchCriteriaDTO();
            search.setOppilaitosTyyppi(oppilaitostyyppi);
            filtteredOrgs.addAll(organisaatioService.searchBasicOrganisaatios(search));
        }

        int orgIndex = 0;

        while (orgIndex < filtteredOrgs.size()) {
            for (int i = 0; i < threads.length; i++) {
                if (threads[i] == null || !threads[i].isAlive()) {
                    final String organisaatioOid = filtteredOrgs.get(orgIndex).getOid();
                    threads[i] = new ThreadedDataUploader("thread_" + i,
                            hakuOid,
                            tarjotantaAdminService,
                            tarjotantaPublicService,
                            loiItemCountPerOrganisation);

                    threads[i].setOrganisationOid(organisaatioOid);
                    threads[i].start();
                    orgIndex++;
                }
            }
            LOG.info("Waiting...");
            Thread.sleep((long) 10000);
            LOG.info("Go!");
        }
    }
}
