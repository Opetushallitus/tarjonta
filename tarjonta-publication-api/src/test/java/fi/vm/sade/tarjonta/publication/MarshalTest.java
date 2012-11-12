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

import fi.vm.sade.tarjonta.publication.types.*;
import fi.vm.sade.tarjonta.publication.types.SelectionCriterionsType.EntranceExaminations.Examination;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>Simple smoke test that validates that marshalling does not fail.</p>
 *
 * <p>
 * To run this test with results printed to System.out:
 * <code>
 * mvn -Dtest=MarshalTest test -DprintXML=true
 * </code>
 * </p>
 *
 * @author Jukka Raanamo
 */
public class MarshalTest {

    private static final String PACKAGE_NAME = LearningOpportunityDownloadDataType.class.getPackage().getName();

    private static JAXBContext sJaxbContext;

    private static boolean sPrintXML;

    private StringWriter out;

    @BeforeClass
    public static void setUpTest() throws Exception {
        sJaxbContext = JAXBContext.newInstance(PACKAGE_NAME);
        sPrintXML = Boolean.parseBoolean(System.getProperty("printXML", "false"));
    }

    @Before
    public void setUp() {
        out = new StringWriter();
    }

    @After
    public void unmarshal() throws Exception {
        Unmarshaller um = sJaxbContext.createUnmarshaller();
        um.unmarshal(new StringReader(out.toString()));
    }

    @Test
    public void testMarshalDownloadData() throws Exception {

        LearningOpportunityDownloadData downloadData = new LearningOpportunityDownloadData();
        createApplicationObject(downloadData);
        marshal(downloadData);

    }

    private void createApplicationObject(LearningOpportunityDownloadData data) {

        ApplicationOptionType option = new ApplicationOptionType();

        Examination exam = new Examination();
        ExaminationEventType event = new ExaminationEventType();
        SelectionCriterionsType.EntranceExaminations examinations = new SelectionCriterionsType.EntranceExaminations();
        SelectionCriterionsType criterions = new SelectionCriterionsType();


        // test java.util.Date -> xs:dateTime conversion as specified in bindingds.xjb
        event.setStart(new Date());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 30);
        event.setEnd(cal.getTime());

        exam.getExaminationEvent().add(event);
        examinations.getExamination().add(exam);
        criterions.setEntranceExaminations(examinations);
        option.setSelectionCriterions(criterions);

        data.getApplicationOption().add(option);

    }

    private void marshal(LearningOpportunityDownloadDataType download) throws Exception {

        Marshaller m = sJaxbContext.createMarshaller();
        m.marshal(download, out);

        if (sPrintXML) {
            System.out.println("marshalled: " + out);
        }

    }

}

