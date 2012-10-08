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
package fi.vm.sade.tarjonta.service;

import fi.vm.sade.tarjonta.service.types2.LearningOpportunityDataType;
import fi.vm.sade.tarjonta.service.types2.LearningUploadRequestType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 *
 * @author Jukka Raanamo
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,})
@RunWith(SpringJUnit4ClassRunner.class)
public class LearningUploadServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(LearningUploadServiceTest.class);

    @Autowired(required = true)
    private LearningUploadService learningUploadService;

    @Test
    public void testNone() throws JAXBException, FileNotFoundException, XMLStreamException {
        LOG.info("testNone()...");
        // ImportTarjontaRequestType batch = new ImportTarjontaRequestType();

        LOG.info("learningUploadService: {}", learningUploadService);

        LOG.info("a");
        JAXBContext ctx = JAXBContext.newInstance("fi.vm.sade.tarjonta.service.types2");
        LOG.info("b");
        Unmarshaller jaxbUnmarshaller = ctx.createUnmarshaller();
        // jaxbUnmarshaller.setValidating(true);
        LOG.info("c");
        // Object req =  jaxbUnmarshaller.unmarshal(new File("../tarjonta-api/src/test/resources/learningUploadPOC.xml"));

        //        JAXBElement<LearningOpportunityDataType> req = jaxbUnmarshaller.unmarshal(XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(new File("../tarjonta-api/src/test/resources/learningUploadPOC.xml"))),
        //                LearningOpportunityDataType.class);

        JAXBElement<LearningUploadRequestType> req = jaxbUnmarshaller.unmarshal(XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(new File("../tarjonta-api/src/test/resources/learningUploadPOC.xml"))),
                LearningUploadRequestType.class);

        LOG.info("d: req={}", req);

        // learningUploadService.upload(req.getValue());

        LOG.info("testNone()... done.");
    }

}

