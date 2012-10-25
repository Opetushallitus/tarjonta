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

import fi.vm.sade.tarjonta.service.LearningUploadService;
import fi.vm.sade.tarjonta.service.types.LearningOpportunityUploadDataType;
import fi.vm.sade.tarjonta.service.types.LearningUploadRequestType;
import java.io.File;
import java.io.FileInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 *
 * @author mlyly
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class LearningUploadServiceImplTest {

    private static final Logger LOG = LoggerFactory.getLogger(LearningUploadServiceImplTest.class);

    @Autowired
    private LearningUploadService learningUploadService;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of upload method, of class LearningUploadServiceImpl.
     */
    @Test
    @Ignore
    public void testUpload() throws Exception {
        LOG.info("testUpload: {}", learningUploadService);

        // Read test XML
        JAXBContext ctx = JAXBContext.newInstance("fi.vm.sade.tarjonta.service.types2");
        Unmarshaller jaxbUnmarshaller = ctx.createUnmarshaller();
        JAXBElement<LearningOpportunityUploadDataType> req = jaxbUnmarshaller.unmarshal(XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(new File("../tarjonta-api/src/test/resources/learningUploadPOC.xml"))),
                LearningOpportunityUploadDataType.class);

        LearningUploadRequestType request = new LearningUploadRequestType();
        request.setLearningOpportunityData(req.getValue());
        learningUploadService.upload(request);

        LOG.info("testUpload()... done.");
    }
}
