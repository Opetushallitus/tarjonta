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
package fi.vm.sade.tarjonta.data.rest;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchCriteriaDTO;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Jani Wilén
 */
@Service
public class KorkeakoulutusDataUploader {

    public static final String ENV = "http://localhost:8585";

    public static final String ENV_CAS = "https://itest-virkailija.oph.ware.fi";

    public static final String SERVICE = "/tarjonta-service";

    public static final String SERVICE_REST = "/tarjonta-service/rest/v1";

    public static final String TARJONTA_SERVICE = ENV + SERVICE;

    public static final String TARJONTA_SERVICE_REST = ENV + SERVICE_REST;

    private static final String[] ACCEPTED_OPPILAITOSTYYPPIS = new String[]{
        "oppilaitostyyppi_42#1",
        "oppilaitostyyppi_41#1"
    };
    private static final Logger LOG = LoggerFactory.getLogger(KorkeakoulutusDataUploader.class);
    private static final int MAX_KOMOTO_THREADS = 1;
    @Autowired(required = true)
    private OrganisaatioService organisaatioService;
    private final KorkeakoulutusThreadedDataUploader[] threads = new KorkeakoulutusThreadedDataUploader[MAX_KOMOTO_THREADS];

    private WebResource koulutusResource;

    private WebResource permissionResource;

    private WebResource hakuResource;

    private WebResource hakukohdeResource;

    private WebResource linkResource;

    public KorkeakoulutusDataUploader() {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        Client clientWithJacksonSerializer = Client.create(cc);
        clientWithJacksonSerializer.setFollowRedirects(Boolean.TRUE);

        this.koulutusResource = clientWithJacksonSerializer.resource(TARJONTA_SERVICE_REST + "/koulutus");
        this.permissionResource = clientWithJacksonSerializer.resource(TARJONTA_SERVICE_REST + "/permission");
        this.linkResource = clientWithJacksonSerializer.resource(TARJONTA_SERVICE_REST + "/link");
        this.hakukohdeResource = clientWithJacksonSerializer.resource(TARJONTA_SERVICE_REST + "/hakukohde");
        this.hakuResource = clientWithJacksonSerializer.resource(TARJONTA_SERVICE_REST + "/haku");
    }

    public void upload(final int maxOrganisations, final int maxKoulutus) throws InterruptedException, IOException {
        Preconditions.checkNotNull(maxOrganisations, "Organisation OID cannot be null.");
        Set<OrganisaatioDTO> filtteredOrgs = new HashSet<OrganisaatioDTO>();

        for (String oppilaitostyyppi : ACCEPTED_OPPILAITOSTYYPPIS) {

            OrganisaatioSearchCriteriaDTO search = new OrganisaatioSearchCriteriaDTO();
            search.setOppilaitosTyyppi(oppilaitostyyppi);
            filtteredOrgs.addAll(organisaatioService.searchOrganisaatios(search));

            for (OrganisaatioDTO dto : filtteredOrgs) {
                dto.setOppilaitosTyyppi(oppilaitostyyppi);
            }
        }

        LOG.info("Loaded unique organisations : {}", filtteredOrgs.size());
        LOG.info("Organisations filtered to the given limit : {}", maxOrganisations);

        boolean running = true;

        Set<OrganisaatioDTO> subOrgs = new HashSet<OrganisaatioDTO>();
        for (OrganisaatioDTO t : filtteredOrgs) {
            if (subOrgs.size() > maxOrganisations) {
                break;
            }
            subOrgs.add(t);
        }
        HakuGenerator hakuGenerator = new HakuGenerator(hakuResource);
        final String hakuOid = hakuGenerator.create();
        Preconditions.checkNotNull(hakuOid, "Haku OID cannot be null.");

        final Iterator<OrganisaatioDTO> iterator = subOrgs.iterator();
        while (running) {
            for (int i = 0; i < threads.length; i++) {
                if (!iterator.hasNext()) {
                    LOG.info("Script ended.");
                    running = false;
                    break;
                } else if (threads[i] == null || !threads[i].isAlive()) {
                    threads[i] = new KorkeakoulutusThreadedDataUploader("thread_" + i,
                            hakuOid,
                            maxKoulutus,
                            koulutusResource,
                            permissionResource,
                            hakuResource,
                            hakukohdeResource,
                            linkResource,
                            iterator.next()
                    );

                    threads[i].start();
                }
            }
            LOG.info("Wait...");
            Thread.sleep((long) 10000);
            LOG.info("Go!");
        }
        LOG.info("End!");
    }
}
