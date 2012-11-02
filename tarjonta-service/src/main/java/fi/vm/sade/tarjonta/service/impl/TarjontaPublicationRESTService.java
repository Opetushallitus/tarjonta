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

import fi.vm.sade.tarjonta.publication.LearningOpportunityJAXBWriter;
import fi.vm.sade.tarjonta.publication.PublicationCollector;
import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * REST service for publication.
 *
 * @author Jukka Raanamo
 */
@Path("/publication")
public class TarjontaPublicationRESTService {

    private static final Logger log = LoggerFactory.getLogger(TarjontaPublicationRESTService.class);

    @Autowired
    private PublicationCollector dataCollector;

    /**
     * Dummy method that can be used to test connection. Always returns "hello" -string.
     *
     * @return
     */
    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {

        return "hello";

    }

    /**
     * Exports current tarjonta content (non-enriched) into response stream.
     *
     * todo: concurrent calls to this method are not prevented - we either need to do that or
     * use method-local data collector.
     *
     * @return
     * @throws JAXBException
     */
    @GET
    @Path("/export")
    @Produces(MediaType.APPLICATION_XML)
    public StreamingOutput export() throws JAXBException {

        final LearningOpportunityJAXBWriter writer = new LearningOpportunityJAXBWriter();

        return new StreamingOutput() {

            @Override
            public void write(OutputStream out) throws IOException, WebApplicationException {

                try {

                    writer.setOutput(out);
                    dataCollector.setHandler(writer);
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

    }

}

