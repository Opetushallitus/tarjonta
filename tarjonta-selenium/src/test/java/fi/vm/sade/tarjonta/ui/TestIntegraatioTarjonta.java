package fi.vm.sade.tarjonta.ui;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
    private WebDriver driver;
    private String baseUrl;
    private StringBuffer verificationErrors = new StringBuffer();

	@Before
	public void setUp() throws Exception {
		http = SVTUtils.prop.getProperty("tarjonta-selenium.restapi");
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        firefoxProfile.setEnableNativeEvents(true);
        firefoxProfile.setPreference( "intl.accept_languages", "fi-fi,fi" );
        driver = new FirefoxDriver(firefoxProfile);
        baseUrl = SVTUtils.prop.getProperty("tarjonta-selenium.oph-url");
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	// Tarjonta Rest rajapinta
	@Test
	public void test_T_INT_TAR_REST001() throws IOException {
    	doit.echo("Running test_T_INT_TAR_REST001 ...");
		run.restTestCount(path + "001.qa.txt", http + "/komo?count=2");
    	doit.echo("Running test_T_INT_TAR_REST001 OK");
	}

	//Tarjonnan etusivu
	@Test
	public void test_T_INT_TAR_ETUS001() throws Exception {
    	doit.echo("Running test_T_INT_TAR_ETUS001 ...");
		this.frontPage();
    	doit.echo("Running test_T_INT_TAR_ETUS001 OK");
    }
    
	//Tarjonnan koulutus
	@Test
    public void test_T_INT_TAR_KOUL001() throws Exception {
    	this.frontPage();
    	doit.echo("Running test_T_INT_TAR_KOUL001 ...");
    	doit.ValikotHakukohteidenYllapito(driver, baseUrl);

    	// HAE
		WebElement menu = doit.TarkasteleKoulutusLuonnosta(driver, "ylioppilastut");
		doit.menuOperaatioMenuLuonnos(driver, menu, "Muokkaa");
        Assert.assertNotNull("Running test_T_INT_TAR_KOUL001 muokkaa ei toimi."
                , doit.textElement(driver, "Tutkintonimike"));
    	doit.echo("Running test_T_INT_TAR_KOUL001 OK");
    }
    
    //Tarjonnan hakukohde
	@Test
    public void test_T_INT_TAR_HKOH001() throws Exception {
    	this.frontPage();
    	doit.echo("Running test_T_INT_TAR_HKOH001 ...");
        doit.ValikotHakukohteidenYllapito(driver, baseUrl);
        
        doit.textClick(driver, "Hakukohteet");
        doit.tauko(1);
        WebElement menu = doit.TarkasteleHakukohdeLuonnosta(driver, "");
        if (menu == null)
        {
        	doit.echo("Running Ei ole luonnoksia hakukohteille.");
        	int a = 1 / 0;
        }
        // otetaan organisaatio muistiin
        String organisaatio = doit.getTextMinusY(driver, "luonnos", "//div[@class='v-label v-label-undef-w' and contains(text(),')')]");
        organisaatio = organisaatio.substring(0, organisaatio.indexOf("(") - 1);
    	doit.echo("Running test_T_INT_TAR_HKOH001 OK");
    }

	//(Tarjonnan) haun etusivu
	@Test
    public void test_T_INT_TAR_HAKU001() throws Exception {
    	this.frontPage();
    	doit.echo("Running test_T_INT_TAR_HAKU001 ...");
        doit.ValikotHakujenYllapito(driver, baseUrl);
    	doit.echo("Running test_T_INT_TAR_HAKU001 OK");
        doit.tauko(1);
    }
    
	//(Tarjonnan) valintaperusteen etusivu
	@Test
    public void test_T_INT_TAR_VAPE001() throws Exception {
    	this.frontPage();
    	doit.echo("Running test_T_INT_TAR_VAPE001 ...");
        doit.ValikotValintaperusteKuvaustenYllapito(driver, baseUrl);
    	doit.echo("Running test_T_INT_TAR_VAPE001 OK");
    }

    public void frontPage() throws Exception
    {
            if (first)
            {
                    doit.palvelimenVersio(driver, baseUrl);
                    doit.echo("Running =================================================================");
            }

            // LOGIN
            driver.get(baseUrl);
            doit.tauko(1);
            doit.reppuLogin(driver);
            doit.tauko(1);
            driver.get(baseUrl);
            doit.tauko(1);
            Assert.assertNotNull("Running Etusivu ei toimi."
                            , doit.textElement(driver, "Tervetuloa Opintopolun virkailijan palveluihin!"));
            doit.tauko(1);
            first = false;
    }

    @After
    public void tearDown() throws Exception {
    	if (driver != null) { driver.quit(); }
    	String verificationErrorString = verificationErrors.toString();
    	if (!"".equals(verificationErrorString)) {
    		fail(verificationErrorString);
    	}
    }
}
