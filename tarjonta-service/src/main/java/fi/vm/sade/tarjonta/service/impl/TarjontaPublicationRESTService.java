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
package fi.vm.sade.tarjonta.service.impl;

import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.tarjonta.publication.ExportParams;
import fi.vm.sade.tarjonta.publication.LearningOpportunityJAXBWriter;
import fi.vm.sade.tarjonta.publication.PublicationCollector;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.io.IOException;
import java.io.OutputStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * REST service for tarjonta publication. This is not intended for production
 * use. Awaits for proper architecture choice for data publication at OPH Sade
 * -project.
 *
 * @author Jukka Raanamo
 */
@Path("/publication")
public class TarjontaPublicationRESTService {

    private static final Logger log = LoggerFactory.getLogger(TarjontaPublicationRESTService.class);
    @Autowired
    private PublicationCollector dataCollector;
    @PersistenceContext
    private EntityManager em;
    @Autowired(required = true)
    protected OrganisaatioService organisaatioService;

    @GET
    @Path("/export-rich")
    public Response exportRich(@PathParam("images") boolean images) throws JAXBException {

        // enrichment is done is separate servlet filter, this method is just an endpoint to separate
        // the raw and enriched content. remove this when ESB is in place.
        return export(images);

    }

    /**
     * Exports current tarjonta content (non-enriched) into response stream.
     *
     * @return
     * @throws JAXBException
     */
    @GET
    @Path("/export")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_XML})
    @Transactional(readOnly = true)
    public Response export(
            @DefaultValue(value = "false")
            @QueryParam("images") boolean images) throws JAXBException {
        final ExportParams params = new ExportParams();
        log.info("Param : show images : '{}'", images);
        params.setShowImages(images);

        final LearningOpportunityJAXBWriter writer = new LearningOpportunityJAXBWriter(params);
        final StreamingOutput output = new StreamingOutput() {
            @Override
            public void write(OutputStream out) throws IOException, WebApplicationException {

                try {

                    writer.setOutput(out);
                    dataCollector.setOrganisaatioService(organisaatioService);
                    dataCollector.setHandler(writer);
                    dataCollector.setParams(params);
                    dataCollector.start();

                } catch (XMLStreamException e) {

                    log.error("error writing xml ", e);
                    throw new IOException("writing publication data failed", e);

                } catch (Exception e) {

                    log.error("data handler error", e);
                    throw new WebApplicationException(e, Response.serverError().
                            entity(e.getMessage()).
                            build());

                }

            }
        };

        return Response.ok().
                type(MediaType.APPLICATION_XML).
                entity(output).build();

    }
}
