package fi.vm.sade.tarjonta.ui;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class TestIntegraatioTarjonta {
	static private String path = "./src/test/resources/restApiResult";
	static private String http = "";
    private static Boolean first = true;
    private SVTUtils doit = new SVTUtils();
	TestTUtils run = new TestTUtils();   
    private static WebDriver driver = null;
    private static Boolean driverQuit = false;
    private String baseUrl;
    private StringBuffer verificationErrors = new StringBuffer();
	String tarjontaVersioUrl;

	@Before
	public void setUp() throws Exception {
		http = SVTUtils.prop.getProperty("testaus-selenium.restapi");
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        firefoxProfile.setEnableNativeEvents(true);
        firefoxProfile.setPreference( "intl.accept_languages", "fi-fi,fi" );
        if (driver == null || driverQuit) { driver = new FirefoxDriver(firefoxProfile); driverQuit = false; }
        baseUrl = SVTUtils.prop.getProperty("testaus-selenium.oph-url");
    	tarjontaVersioUrl = SVTUtils.prop.getProperty("testaus-selenium.tarjonta-versio-url");
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	// Tarjonta Rest rajapinta
	//@Test
	public void test_T_INT_TAR_REST001() throws IOException {
    	doit.echo("Running test_T_INT_TAR_REST001 ...");
		run.restTestCount(path + "001.qa.txt", http + "/komo?count=2");
    	doit.echo("Running test_T_INT_TAR_REST001 OK");
	}

	//Tarjonnan etusivu
	@Test
	public void test_T_INT_TAR_ETUS001() throws Exception {
    	doit.echo("Running test_T_INT_TAR_ETUS001 ...");
		doit.frontPage(driver, baseUrl);
		doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.echo("Running test_T_INT_TAR_ETUS001 OK");
    }
    
	//Tarjonnan koulutus
	@Test
    public void test_T_INT_TAR_KOUL001() throws Exception {
    	try {
    		KOUL001loop();
    	} catch (Exception e) {
    		try {
    			doit.printMyStackTrace(e);
    			KOUL001loop();
    		} catch (Exception e2) {
    			doit.printMyStackTrace(e2);
    			KOUL001loop();
    		}
    	}
    }

    public void KOUL001loop() throws Exception {
		doit.frontPage(driver, baseUrl);
    	doit.echo("Running test_T_INT_TAR_KOUL001 ...");
    	doit.ValikotKoulutustenJaHakukohteidenYllapito(driver, baseUrl);

    	// HAE
//    	WebElement menu = doit.linkKoulutusLuonnosta(driver, "ylioppilastut");
//    	doit.menuOperaatio(driver, "Muokkaa", "luonnos");
		doit.haeKoulutuksia(driver, "Luonnos", "ylioppilastut");
		doit.triangleClickFirstTriangle(driver);
		doit.menuOperaatioFirstMenu(driver, "Muokkaa");
        Assert.assertNotNull("Running test_T_INT_TAR_KOUL001 muokkaa ei toimi."
                , doit.textElement(driver, "Tutkintonimike"));
    	doit.echo("Running test_T_INT_TAR_KOUL001 OK");
    	driver.findElement(By.className("v-button-back")).click();
    	doit.tauko(1);
    }
    
    //Tarjonnan hakukohde
	@Test
    public void test_T_INT_TAR_HKOH001() throws Exception {
    	try {
    		HKOH001loop();
    	} catch (Exception e) {
    		try {
    			doit.printMyStackTrace(e);
    			HKOH001loop();
    		} catch (Exception e2) {
    			doit.printMyStackTrace(e2);
    			HKOH001loop();
    		}
    	}
    }

    public void HKOH001loop() throws Exception {
		doit.frontPage(driver, baseUrl);
    	doit.echo("Running test_T_INT_TAR_HKOH001 ...");
    	doit.ValikotKoulutustenJaHakukohteidenYllapito(driver, baseUrl);
        doit.haeHakukohteita(driver, "Luonnos", null);
//        doit.triangleClickLastTriangle(driver);
        doit.sendPageToFile(driver);
        doit.screenShot("triangleClickNearestTriangle", driver);
        doit.triangleClickNearestTriangle(driver, "Valitse kaikki");
        Assert.assertNotNull("Running hae luonnos ei toimi.", doit.textElement(driver, "luonnos"));
    	doit.echo("Running test_T_INT_TAR_HKOH001 OK");
    }

	//(Tarjonnan) haun etusivu
	@Test
    public void test_T_INT_TAR_HAKU001() throws Exception {
    	try {
    		HAKU001loop();
    	} catch (Exception e) {
    		try {
    			doit.printMyStackTrace(e);
    			HAKU001loop();
    		} catch (Exception e2) {
    			doit.printMyStackTrace(e2);
    			HAKU001loop();
    		}
    	}
    }

    public void HAKU001loop() throws Exception {
		doit.frontPage(driver, baseUrl);
    	doit.echo("Running test_T_INT_TAR_HAKU001 ...");
        doit.ValikotHakujenYllapito(driver, baseUrl);
    	doit.echo("Running test_T_INT_TAR_HAKU001 OK");
        doit.tauko(1);
    }
    
	//(Tarjonnan) valintaperusteen etusivu
	@Test
    public void test_T_INT_TAR_VAPE001() throws Exception {
		doit.frontPage(driver, baseUrl);
    	doit.echo("Running test_T_INT_TAR_VAPE001 ...");
        doit.ValikotValintaperusteKuvaustenYllapito(driver, baseUrl);
    	doit.echo("Running test_T_INT_TAR_VAPE001 OK");
    	if (driver != null) { driver.quit(); }
    	driverQuit = true;
    }

//    public void frontPage() throws Exception
//    {
//    	if (first)
//    	{
//    		doit.palvelimenVersio(driver, baseUrl, tarjontaVersioUrl);
//    		doit.echo("Running =================================================================");
//    	}
//
//    	// LOGIN
//    	driver.get(baseUrl);
//    	doit.tauko(1);
//    	doit.reppuLogin(driver);
//    	doit.tauko(1);
//    	driver.get(baseUrl);
//    	doit.tauko(1);
//    	Assert.assertNotNull("Running Etusivu ei toimi."
//    			, doit.textElement(driver, "Tervetuloa Opintopolun virkailijan palveluihin!"));
//    	doit.tauko(1);
//    	first = false;
//    }

    @After
    public void tearDown() throws Exception {
//    	if (driver != null) { driver.quit(); }
//      driverQuit = true;
    	doit.quit(driver);
    	String verificationErrorString = verificationErrors.toString();
    	if (!"".equals(verificationErrorString)) {
    		fail(verificationErrorString);
    	}
    }
}
