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
package fi.vm.sade.tarjonta.publication;

import fi.vm.sade.tarjonta.publication.types.LearningOpportunityDownloadData;
import fi.vm.sade.tarjonta.publication.types.LearningOpportunityDownloadDataType;
import fi.vm.sade.tarjonta.publication.types.LearningOpportunityUploadDataType;
import java.io.File;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Simple smoke test that validates that POC xml files can be unmarshalled with JAXB.
 *
 * @author Jukka Raanamo
 */
public class UnmarshalTest {

    private static final String PACKAGE_NAME = LearningOpportunityDownloadDataType.class.getPackage().getName();

    private static JAXBContext sJaxbContext;

    @BeforeClass
    public static void setUpTest() throws Exception {
        sJaxbContext = JAXBContext.newInstance(PACKAGE_NAME);
    }

    @Test
    public void testUnmarshalDownloadPOC() throws Exception {
        LearningOpportunityDownloadDataType download = unmarshalDownload("src/test/resources/learningDownloadPOC.xml");
        marshal(download);
    }

    @Test
    public void testUnmarshalUploadPOC() throws Exception {
        unmarshalUpload("src/test/resources/learningUploadPOC.xml");
    }


    private LearningOpportunityUploadDataType unmarshalUpload(String filepath) throws Exception {
        return (LearningOpportunityUploadDataType) unmarshal(filepath);
    }

    private LearningOpportunityDownloadData unmarshalDownload(String filepath) throws Exception {
        return (LearningOpportunityDownloadData) unmarshal(filepath);
    }

    private void marshal(LearningOpportunityDownloadDataType download) throws Exception {
        StringWriter out = new StringWriter();
        Marshaller m = sJaxbContext.createMarshaller();
        m.marshal(download, out);

        //System.out.println("Wrote: " + out);
    }

    private Object unmarshal(String filepath) throws Exception {
        Unmarshaller um = sJaxbContext.createUnmarshaller();
        return um.unmarshal(new File(filepath));
    }

}

