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

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Jani Wilén
 */
public class GenerateTestData {

    /* Will create a small data for script testing. */
    private static final String ORGANISATION_OID_SMALL = "1.2.246.562.10.44513634004";
    /* HERE BE DRAGONS!!! - will generate a huge data, you've been warned.   */
    private static final String ORGANISATION_OID_OPH = "1.2.246.562.10.00000000001";
    /* set how many LOI items you want for organisation */
    private static final int MAX_KOMOTOS_PER_ORGANISATION = 5;

    public GenerateTestData() {
    }

    public static void main(final String[] args) throws InterruptedException {
        final ApplicationContext context = new ClassPathXmlApplicationContext("spring/context.xml");
        final DataUploader uploader = context.getBean(DataUploader.class);
        uploader.upload(ORGANISATION_OID_OPH, MAX_KOMOTOS_PER_ORGANISATION);
    }
}
