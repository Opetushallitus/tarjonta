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

import fi.vm.sade.tarjonta.data.rest.KorkeakoulutusDataUploader;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import java.io.IOException;
import java.net.MalformedURLException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Jani Wilén
 */
public class GenerateTestData {

    /* Will create a small data for script testing. */
    private static final int MAX_ORGANISATIONS = 2;
    /* set how many LOI items you want for organisation */
    private static final int MAX_KOMOTOS_PER_ORGANISATION = 10;

    private static final int MAX_KOULUTUS_PER_ORGANISATION = 4;

    public static final String ENV = "https://itest-virkailija.oph.ware.fi";//"http://localhost:8585";

    public static final String ENV_CAS = "https://itest-virkailija.oph.ware.fi";

    public static final String SERVICE = "/tarjonta-service";

    public static final String SERVICE_REST = "/tarjonta-service/rest/v1";

    public static final String TARJONTA_SERVICE = ENV + SERVICE;

    public static final String TARJONTA_SERVICE_REST = ENV + SERVICE_REST;

    public GenerateTestData() {
    }

    public static void main(final String[] args) throws InterruptedException, MalformedURLException, IOException {
        final ApplicationContext context = new ClassPathXmlApplicationContext("spring/context.xml");

        //https://itest-virkailija.oph.ware.fi/cas/login?service=http%3A%2F%2Flocalhost%3A8585%2Ftarjonta-service%2Fj_spring_cas_security_check
        //final DataUploader uploader = context.getBean(DataUploader.class);
        //uploader.upload(MAX_ORGANISATIONS, MAX_KOMOTOS_PER_ORGANISATION);
        final KoodistoURI uris = context.getBean(KoodistoURI.class);
        final KorkeakoulutusDataUploader uploader = context.getBean(KorkeakoulutusDataUploader.class);
        uploader.upload(MAX_ORGANISATIONS, MAX_KOULUTUS_PER_ORGANISATION);
    }
}
