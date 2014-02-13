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
import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.data.test.modules.HakuGenerator;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchCriteriaDTO;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
    private static int MAX_KOMOTO_THREADS = 5;
    @Autowired(required = true)
    private OrganisaatioService organisaatioService;
    @Autowired(required = true)
    private TarjontaAdminService tarjotantaAdminService;
    @Autowired(required = true)
    private TarjontaPublicService tarjotantaPublicService;
    private ThreadedDataUploader[] threads = new ThreadedDataUploader[MAX_KOMOTO_THREADS];

    public DataUploader() {
    }

    public void upload(final int maxOrganisations, final int loiItemCountPerOrganisation) throws InterruptedException {
        Preconditions.checkNotNull(maxOrganisations, "Organisation OID cannot be null.");
        HakuGenerator haku = new HakuGenerator(tarjotantaAdminService);
        final String hakuOid = haku.create();
        Set<OrganisaatioPerustietoType> filtteredOrgs = new HashSet<OrganisaatioPerustietoType>();

        for (String oppilaitostyyppi : ACCEPTED_OPPILAITOSTYYPPIS) {
            OrganisaatioSearchCriteriaDTO search = new OrganisaatioSearchCriteriaDTO();
            search.setOppilaitosTyyppi(oppilaitostyyppi);
            //FIXME filtteredOrgs.addAll(organisaatioService.searchBasicOrganisaatios(search));
        }
        LOG.info("Loaded unique organisations : {}", filtteredOrgs.size());
        LOG.info("Organisations filtered to the given limit : {}", maxOrganisations);

        boolean running = true;

        Set<OrganisaatioPerustietoType> subOrgs = new HashSet<OrganisaatioPerustietoType>();
        for (OrganisaatioPerustietoType t : filtteredOrgs) {
            if (subOrgs.size() > maxOrganisations) {
                break;
            }
            subOrgs.add(t); 
        }

        final Iterator<OrganisaatioPerustietoType> iterator = subOrgs.iterator();
        while (running) {
            for (int i = 0; i < threads.length; i++) {
                if (!iterator.hasNext()) {
                    LOG.info("Script ended.");
                    running = false;
                    break;
                } else if (threads[i] == null || !threads[i].isAlive()) {
                    threads[i] = new ThreadedDataUploader("thread_" + i,
                            hakuOid,
                            tarjotantaAdminService,
                            tarjotantaPublicService,
                            loiItemCountPerOrganisation);

                    threads[i].setOrganisationOid(iterator.next().getOid());
                    threads[i].start();
                }
            }
            LOG.info("Wait...");
            Thread.sleep((long) 10000);
            LOG.info("Go!");
        }
    }
}
