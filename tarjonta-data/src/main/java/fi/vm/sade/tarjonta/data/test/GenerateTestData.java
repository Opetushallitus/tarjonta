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
import fi.vm.sade.tarjonta.data.rest.KoulutusGenerator;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Jani Wilén
 */
public class GenerateTestData {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(GenerateTestData.class);
    /* Will create a small data for script testing. */
    private static final int MAX_ORGANISATIONS = 2;
    /* set how many LOI items you want for organisation */
    private static final int MAX_KOMOTOS_PER_ORGANISATION = 10;

    private static final int MAX_KOULUTUS_PER_ORGANISATION = 4;

    public static final String ENV = "https://itest-virkailija.oph.ware.fi";

    public static final String ENV_CAS = "https://itest-virkailija.oph.ware.fi";

    public static final String SERVICE = "/tarjonta-service";

    public static final String SERVICE_REST = "/tarjonta-service/rest/v1";

    public static final String TARJONTA_SERVICE = ENV + SERVICE;

    public static final String TARJONTA_SERVICE_REST = ENV + SERVICE_REST;

    public static String PASSWORD = "";

    public static String USERNAME = "";

    public GenerateTestData() {
    }

    public static void main(final String[] args) throws InterruptedException, MalformedURLException, IOException {
        final ApplicationContext context = new ClassPathXmlApplicationContext("spring/context.xml");

        //https://itest-virkailija.oph.ware.fi/cas/login?service=http%3A%2F%2Flocalhost%3A8585%2Ftarjonta-service%2Fj_spring_cas_security_check
        //final DataUploader uploader = context.getBean(DataUploader.class);
        //uploader.upload(MAX_ORGANISATIONS, MAX_KOMOTOS_PER_ORGANISATION);
//        final KoodistoURI uris = context.getBean(KoodistoURI.class);
        Properties props = context.getBean("appProperties", Properties.class);
        USERNAME = props.getProperty("auth.username");
        PASSWORD = props.getProperty("auth.password");
        final KorkeakoulutusDataUploader uploader = context.getBean(KorkeakoulutusDataUploader.class);
        uploader.upload(MAX_ORGANISATIONS, MAX_KOULUTUS_PER_ORGANISATION);
    }

    public static String getTicket() {
        HttpClient client = new HttpClient();
        HttpMethod request1 = new GetMethod(
                ENV_CAS + "/service-access/accessTicket"
                + "?client_id=" + USERNAME
                + "&client_secret=" + PASSWORD
                + "&service_url=" + TARJONTA_SERVICE);
        try {
            int executeMethod = client.executeMethod(request1);
            LOG.info("executeMethod :" + executeMethod + " '" + request1.getResponseBodyAsString().trim() + "'");
            HttpMethod request2 = new GetMethod(TARJONTA_SERVICE_REST + "/permission/authorize?ticket=" + request1.getResponseBodyAsString().trim());

            executeMethod = client.executeMethod(request2);
            String jsessionId = "";
            LOG.info("\nCookies: " + client.getState().getCookies().length);
            for (org.apache.commons.httpclient.Cookie c : client.getState().getCookies()) {
                jsessionId = c.getName() + " = " + c.getValue();
                LOG.info("  " + jsessionId);
                break;
            }

            LOG.info("executeMethod :" + executeMethod + " '" + request2.getResponseBodyAsString().trim() + "' " + jsessionId);
            return request1.getResponseBodyAsString().trim();
        } catch (IOException ex) {
            Logger.getLogger(GenerateTestData.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static String getJsessionId(final String ticket) {
        HttpClient client = new HttpClient();
        try {

            HttpMethod method = new GetMethod(TARJONTA_SERVICE_REST + "/permission/authorize?ticket=" + ticket);
            int executeMethod = client.executeMethod(method);

            LOG.info("----\n\nStatus : " + method.getStatusCode());
            LOG.info("\nURI: " + method.getURI());
            LOG.info("\nResponse Path: " + method.getPath());
            LOG.info("\nRequest Headers: " + method.getRequestHeaders().length);
            for (Header h : method.getRequestHeaders()) {
                LOG.info("  " + h.getName() + " = " + h.getValue());
            }

            String jsessionId = "";
            LOG.info("\nCookies: " + client.getState().getCookies().length);
            for (org.apache.commons.httpclient.Cookie c : client.getState().getCookies()) {
                jsessionId = c.getName() + " = " + c.getValue();
                break;
            }

            jsessionId = jsessionId.trim();

            LOG.info("executeMethod :" + executeMethod + " '" + method.getResponseBodyAsString().trim() + "' jession : '" + jsessionId + "'");
            return jsessionId;
        } catch (IOException ex) {
            Logger.getLogger(GenerateTestData.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
