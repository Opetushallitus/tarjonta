/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.data.test;

import fi.vm.sade.authentication.cas.CasClient;
import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.commons.httpclient.HttpMethod;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jani
 */
public class GenerateTestDataTest {

    public GenerateTestDataTest() {
    }

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

    @Test
    public void testMain() throws Exception {
        Assert.assertTrue(true);
    }

    public void testTicket() throws Exception {
        final ApplicationContext context = new ClassPathXmlApplicationContext("spring/context.xml");

        //https://itest-virkailija.oph.ware.fi/cas/login?service=http%3A%2F%2Flocalhost%3A8585%2Ftarjonta-service%2Fj_spring_cas_security_check
        //final DataUploader uploader = context.getBean(DataUploader.class);
        //uploader.upload(MAX_ORGANISATIONS, MAX_KOMOTOS_PER_ORGANISATION);
        String CAS_VALIDATE_URL = "https://itest-virkailija.oph.ware.fi/cas/login?service=http%3A%2F%2Flocalhost%3A8585%2Ftarjonta-service%2Fj_spring_cas_security_check";

        String ticket = CasClient.getTicket("https://itest-virkailija.oph.ware.fi/cas", "ophadmin", "ilonkautta!", "https://itest-virkailija.oph.ware.fi/tarjonta-service");
        HttpClient client = new HttpClient();

        HttpMethod request1 = new GetMethod("https://itest-virkailija.oph.ware.fi/tarjonta-service/rest/v1/permission/authorize");
        request1.setRequestHeader("CasSecurityTicket", ticket);
        request1.setRequestHeader("Connection", "keep-alive");
        request1.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request1.setRequestHeader("Accept-Encoding", "gzip,deflate,sdch");
        request1.setRequestHeader("Accept-Language", "en,en-US;q=0.8,fi;q=0.6");
        request1.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
        request1.setRequestHeader("Host", "itest-virkailija.oph.ware.fi");
        request1.setRequestHeader("Cookie", "JSESSIONID=8365F4958766DF67077A193F4F778E6C;");
        request1.setRequestHeader("DNT", "1");
        request1.setRequestHeader("Origin", "https://itest-virkailija.oph.ware.fi");
        request1.setRequestHeader("Referer", "https://itest-virkailija.oph.ware.fi/tarjonta-service");
        request1.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");

        int executeMethod = client.executeMethod(request1);
        System.out.println("executeMethod :" + executeMethod);
        printResponse(request1, client);

//        String urlString = CAS_VALIDATE_URL;// + "?service=" + THIS_APPS_URL;
//
//        URL url = new URL(urlString);
//        URLConnection connection = url.openConnection();
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//
//        String xmlResponse = "";
//        String line = "";
//        while ((line = in.readLine()) != null) {
//            //System.out.println(line);
//            xmlResponse += line;
//        }
//        in.close();
//
//        System.out.println("URLConnection :" + xmlResponse);
//        System.out.println("start ---------------------");
        Cas20ProxyTicketValidator sv = new Cas20ProxyTicketValidator("https://itest-virkailija.oph.ware.fi/cas");
        sv.setAcceptAnyProxy(true);
        try {
            // there is no need, that the legacy application is accessible
            // through this URL. But for validation purpose, even a non-web-app
            // needs a valid looking URL as identifier.
            Assertion a = sv.validate(ticket, "https://itest-virkailija.oph.ware.fi/tarjonta-service/j_spring_cas_security_check");
            AttributePrincipal principal = a.getPrincipal();
            System.out.println("user name:" + principal.getName());
        } catch (TicketValidationException e) {
            e.printStackTrace(); // bad style, but only for demonstration purpose.
        }

        HttpMethod post1 = new PostMethod("https://itest-virkailija.oph.ware.fi/tarjonta-service/rest/v1/koulutus/KORKEAKOULUTUS");

//        System.out.println("end ---------------------");
//
//        if (SecurityContextHolder.getContext() != null
//                && SecurityContextHolder.getContext().getAuthentication() != null
//                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
//
//            System.out.println("isAuthenticated " + SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
//            SecurityContext sc = SecurityContextHolder.getContext();
//            if (sc.getAuthentication() != null) {
//                CasAuthenticationToken auth = (CasAuthenticationToken) sc.getAuthentication();
//                Assertion assertion = auth.getAssertion();
//                String proxyTicket = assertion.getPrincipal().getProxyTicketFor(THIS_APPS_URL);
//                System.out.println("Proxy ticket " + proxyTicket);
//            }
//        }
    }

    private void printResponse(HttpMethod method, HttpClient client) throws IOException {

        String responseTxt = method.getResponseBodyAsString();

        System.out.println("----\n\nStatus : " + method.getStatusCode());
        System.out.println("\nURI: " + method.getURI());
        System.out.println("\nResponse Path: " + method.getPath());
        System.out.println("\nRequest Headers: " + method.getRequestHeaders().length);
        for (Header h : method.getRequestHeaders()) {
            System.out.println("  " + h.getName() + " = " + h.getValue());
        }
        System.out.println("\nCookies: " + client.getState().getCookies().length);
        for (org.apache.commons.httpclient.Cookie c : client.getState().getCookies()) {
            System.out.println("  " + c.getName() + " = " + c.getValue());
        }
        System.out.println("Response Text: ");
        System.out.println(responseTxt);
    }

}
