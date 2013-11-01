package fi.vm.sade.tarjonta.ui;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class TestTarjontaSavuHakuJaValinta {

    private static WebDriver driver = null;
    private static Boolean driverQuit = false;
    private String baseUrl;
    private StringBuffer verificationErrors = new StringBuffer();
    private SVTUtils doit = new SVTUtils();
    private Boolean qa = false;
    private Boolean luokka = false;

    @Before
    public void setUp() throws Exception {
    	if (true)
    	{
    		FirefoxProfile firefoxProfile = new FirefoxProfile();
    		firefoxProfile.setPreference( "intl.accept_languages", "fi-fi,fi" ); 
            if (driver == null || driverQuit) { driver = new FirefoxDriver(firefoxProfile); driverQuit = false; }
//    		driver = new FirefoxDriver(new FirefoxBinary(new File("c:/Selaimet/Firefox17/firefox.exe")), firefoxProfile);
    	}
    	else
    	{
    		// IE browser will not open unless
    		// - all security zone have toggle "Protected Mode" checked
    		// - view zoom = 100%
    		// - help browser to get certificate clicking the link once
    		// IE9 mode toimii (reppu organisaatio) 5/2013
    		System.setProperty("webdriver.ie.driver", "src/test/resources/IEDriverServer.exe");
    		driver = new InternetExplorerDriver();
    	}
        if (SVTUtils.prop.getProperty("testaus-selenium.luokka").equals("true"))
        {
                luokka = true;
        }
        if (SVTUtils.prop.getProperty("testaus-selenium.qa").equals("true"))
        {
                qa = true;
        }

    	baseUrl = SVTUtils.prop.getProperty("testaus-selenium.oph-url"); // "http://localhost:8080/"
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    	doit.virkailijanPalvelut(driver, baseUrl);
    }
    
    ///////////////////////////////////////////////////////////////////////////////

//    @Test
//    public void test_T_INT_TAR_SAVU201_HAKU_Etusivu() throws Exception {
//    	doit.echo("Running test_T_INT_TAR_SAVU201_HAKU_Etusivu ...");
//    	int a = 1 / 0;
//    	doit.tarjonnanEtusivu(driver, baseUrl);
//    	doit.echo("Running test_T_INT_TAR_SAVU201_HAKU_Etusivu OK");
//    }
//
//    @Test
//    public void test_T_INT_TAR_SAVU202_HAKU_Tarkastele() throws Exception {
//    	doit.echo("Running test_T_INT_TAR_SAVU202_HAKU_Tarkastele ...");
//    	int a = 1 / 0;
//    	doit.tarjonnanEtusivu(driver, baseUrl);
//    	doit.echo("Running test_T_INT_TAR_SAVU202_HAKU_Tarkastele OK");
//    }
//
//    @Test
//    public void test_T_INT_TAR_SAVU203_HAKU_Muokkaa() throws Exception {
//    	doit.echo("Running test_T_INT_TAR_SAVU203_HAKU_Muokkaa ...");
//    	int a = 1 / 0;
//    	doit.tarjonnanEtusivu(driver, baseUrl);
//    	doit.echo("Running test_T_INT_TAR_SAVU203_HAKU_Muokkaa OK");
//    }
//
//    @Test
//    public void test_T_INT_TAR_SAVU204_HAKU_Poista() throws Exception {
//    	doit.echo("Running test_T_INT_TAR_SAVU204_HAKU_Poista ...");
//    	int a = 1 / 0;
//    	doit.tarjonnanEtusivu(driver, baseUrl);
//    	doit.echo("Running test_T_INT_TAR_SAVU204_HAKU_Poista OK");
//    }
//
//    @Test
//    public void test_T_INT_TAR_SAVU301_VAPK_Etusivu() throws Exception {
//    	doit.echo("Running test_T_INT_TAR_SAVU301_VAPK_Etusivu ...");
//    	int a = 1 / 0;
//    	doit.tarjonnanEtusivu(driver, baseUrl);
//    	doit.echo("Running test_T_INT_TAR_SAVU301_VAPK_Etusivu OK");
//    }
//
//    @Test
//    public void test_T_INT_TAR_SAVU401_SORA_Etusivu() throws Exception {
//    	doit.echo("Running test_T_INT_TAR_SAVU401_SORA_Etusivu ...");
//    	int a = 1 / 0;
//    	doit.tarjonnanEtusivu(driver, baseUrl);
//    	doit.echo("Running test_T_INT_TAR_SAVU401_SORA_Etusivu OK");
//    }

    ////////////////////////////////////////////////////////////////////////
	
	@Test
    public void testHaku() throws Exception {
		SVTUtils doit = new SVTUtils();
    	try {
    		testHakuLoop();
    	} catch (Exception e) {
    		try {
    			doit.printMyStackTrace(e);
    			testHakuLoop();
    		} catch (Exception e2) {
    			doit.printMyStackTrace(e2);
    			testHakuLoop();
    		}
    	}
    }

	public void testHakuLoop() throws Exception {
		SVTUtils doit = new SVTUtils();
//        doit.messagesPropertiesInit();
//        driver.get(baseUrl + SVTUtils.prop.getProperty("testaus-selenium.oph-login-url"));
//        doit.tauko(1);
//		doit.reppuLogin(driver);
//		doit.tauko(1);
		doit.virkailijanPuoli(driver, baseUrl);
		doit.echo("Running -------------------------------------------------------");
		long t01 = doit.millis();
		driver.get(baseUrl + SVTUtils.prop.getProperty("testaus-selenium.haku-url"));
		Assert.assertNotNull("Running TarjontaHakuSavu001 Etusivu ei toimi."
                , doit.textElement(driver, "Luo uusi haku"));
		t01 = doit.millisDiff(t01);
		doit.footerTest(driver, "Running TarjontaHakuSavu001 Etusivu footer ei toimi.", true);
		doit.echo("Running TarjontaHakuSavu001 Etusivu OK");
		doit.tauko(1);
		
		// LUO UUSI HAKU
		t01 = doit.millis();
		doit.textClick(driver, "Luo uusi haku");
		Assert.assertNotNull("Running TarjontaHakuSavu002 Luo uusi haku ei toimi."
                , doit.textElement(driver, "hakulomaketta"));
		t01 = doit.millisDiff(t01);
		doit.footerTest(driver, "Running TarjontaHakuSavu002 Luo uusi haku footer ei toimi.", true);
		doit.echo("Running TarjontaHakuSavu002 Luo uusi haku OK");
		doit.tauko(1);
		driver.findElement(By.className("v-button-back")).click();
		Assert.assertNotNull("Running TarjontaHakuSavu002 Luo uusi haku ei toimi."
                , doit.textElement(driver, "Luo uusi haku"));
		doit.tauko(1);

		// POISTA HAKU
		WebElement triangle = doit.getTriangleForFirstItem(driver);
		triangle.click();
		while (true)
		{
			if (doit.isPresentText(driver, "Syksy 20")) { break; }
			if (doit.isPresentText(driver, "Kevät 20")) { break; }
			doit.tauko(1);
		}
		doit.tauko(1);
		String gwtId = doit.getGwtIdForFirstHakukohde(driver);
		WebElement checkBoxId = driver.findElement(By.id(gwtId));
		t01 = doit.millis();
		checkBoxId.click();
		doit.tauko(1);
		Assert.assertNotNull("Running TarjontaHakuSavu003 Poista haku ei toimi."
                , doit.textElement(driver, "Poista").isEnabled());
		t01 = doit.millisDiff(t01);
		doit.tauko(1);
		t01 = doit.millis();
		doit.textClick(driver, "Poista");
		Assert.assertNotNull("Running TarjontaHakuSavu003 Poista haku ei toimi."
                , doit.textElement(driver, "Haluatko varmasti poistaa seuraavan haun?"));
		t01 = doit.millisDiff(t01);
		doit.echo("Running TarjontaHakuSavu003 Poista haku OK");
		doit.tauko(1);
		doit.textClick(driver, "Peruuta");
		Assert.assertNotNull("Running TarjontaHakuSavu003 Poista haku ei toimi."
                , doit.textElement(driver, "Luo uusi haku"));
		doit.tauko(1);
		
		// TARKASTELE
		driver.findElement(By.xpath("//img[@class='v-icon']")).click();
		doit.tauko(1);
		t01 = doit.millis();
		doit.textClick(driver, "Tarkastele");
		Assert.assertNotNull("Running TarjontaHakuSavu004 Tarkastele hakua ei toimi."
                , doit.textElement(driver, "Hakulomake"));
		t01 = doit.millisDiff(t01);
		doit.echo("Running TarjontaHakuSavu004 Tarkastele hakua OK");
		doit.echo("Running TarjontaHakuSavu END OK");
		doit.tauko(1);
	}		

	@Test
    public void testValinnat() throws Exception {
		SVTUtils doit = new SVTUtils();
    	try {
    		testValinnatLoop();
    	} catch (Exception e) {
    		try {
    			doit.printMyStackTrace(e);
    			testValinnatLoop();
    		} catch (Exception e2) {
    			doit.printMyStackTrace(e2);
    			testValinnatLoop();
    		}
    	}
        driver.quit();
        driverQuit = true;
    }

	public void testValinnatLoop() throws Exception {
		SVTUtils doit = new SVTUtils();
		doit.virkailijanPuoli(driver, baseUrl);
//        doit.messagesPropertiesInit();
//        driver.get(baseUrl + SVTUtils.prop.getProperty("testaus-selenium.oph-login-url"));
//        doit.tauko(1);
//		doit.reppuLogin(driver);
//		doit.tauko(1);
//		doit.echo("Running -------------------------------------------------------");
//		long t01 = doit.millis();
		driver.get(baseUrl + SVTUtils.prop.getProperty("testaus-selenium.valinta-url"));
		Assert.assertNotNull("Running TarjontaValintaSavu001 Etusivu ei toimi."
                , doit.textElement(driver, "Kuvausteksti"));
//		t01 = doit.millisDiff(t01);
		doit.footerTest(driver, "Running TarjontaValintaSavu001 Etusivu footer ei toimi.", true);
		doit.echo("Running TarjontaValintaSavu001 Etusivu OK");
		doit.tauko(1);
		
		// SORA-VAATIMUKSET
		doit.textClick(driver, "SORA-vaatimukset");
		doit.tauko(10);
		Assert.assertNotNull("Running TarjontaValintaSavu002 Sora-vaatimukset ei toimi."
                , doit.textElement(driver, "Kuvausteksti"));
		doit.footerTest(driver, "Running TarjontaValintaSavu002 Sora-vaatimukset footer ei toimi.", true);
		doit.echo("Running TarjontaValintaSavu002 Sora-vaatimukset OK");
		doit.echo("Running TarjontaValintaSavu END OK");
		doit.tauko(1);
		driver.quit();
		driverQuit = true;
	}
	
    @After
    public void tearDown() throws Exception {
//    	driver.quit();
//    	driverQuit = true;
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
        	fail(verificationErrorString);
        }
    }
}
