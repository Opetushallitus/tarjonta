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

public class TestTarjontaSekalaista {
    private WebDriver driver;
    private String baseUrl;
    private StringBuffer verificationErrors = new StringBuffer();
    private SVTUtils doit = new SVTUtils();

    @Before
    public void setUp() throws Exception {
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		firefoxProfile.setPreference( "intl.accept_languages", "fi-fi,fi" ); 
		driver = new FirefoxDriver(firefoxProfile);
    	baseUrl = SVTUtils.prop.getProperty("testaus-selenium.oph-url"); // "http://localhost:8080/"
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void test_T_INT_TAR_SEKA016_PoistaLukiokoulutukseltaHakukohde() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SEKA016_PoistaLukiokoulutukseltaHakukohde ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeHakukohteita(driver, "Luonnos", "Lukion", "Syksy", "2014");
    	doit.tarkasteleJokuHakukohde(driver);
    	Assert.assertNotNull("Running LINK HAKUKOHDE ei toimi."
    			, doit.textElement(driver, "Liitteet"));
    	// valitse alhaalta koulutus
    	WebElement link = doit.findNearestElement("Koulutukset", "//span[contains(@class, 'v-button-wrap')]", driver);
    	link.click();
    	Assert.assertNotNull("Running LINK KOULUTUS ei toimi."
    			, doit.textElement(driver, "Koulutuksen perustiedot"));
    	doit.tauko(1);
    	// "poista koulutuksesta"
    	doit.notPresentText(driver, "window_close"
    			, "Running POISTA KOULUTUKSESTA Close nakyy jo. Ei toimi.");
    	doit.footerTest(driver, "Running HAKUKOHTEEN KOULUTUKSESTA footer ei toimi.", true);
    	while (true)
    	{
    		doit.textClick(driver, "Poista koulutuksesta");
    		Assert.assertNotNull("Running POISTA KOULUTUKSESTA ei toimi."
    				, doit.textElement(driver, "Haluatko poistaa hakukohteen koulutukselta"));
    		String closeId = doit.idLike(driver, "window_close");
    		WebElement close = driver.findElement(By.id(closeId));
    		Assert.assertNotNull("Running POISTA KOULUTUKSESTA ei toimi.", close);
    		doit.findNearestElementPlusY("Haluatko poistaa hakukohteen koulutukselta"
    				, "//span[@class='v-button-caption' and text()='Poista']", driver).click();
    		try {
				Assert.assertNotNull("Running POISTA KOULUTUKSESTA ei toimi.", doit.textElement(driver, "Ei hakukohteita"));
				break;
			} catch (Exception e) {
			}
    	}
    	doit.echo("Running test_T_INT_TAR_SEKA016_PoistaLukiokoulutukseltaHakukohde OK");
    	doit.tauko(1);
    }


    @Test
    public void test_T_INT_TAR_SEKA026_PoistaAMPstaHakukohde() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SEKA026_PoistaAMPstaHakukohde ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeHakukohteita(driver, "Luonnos", "tusohj", "Syksy", "2014");
    	doit.tarkasteleJokuHakukohde(driver);
    	Assert.assertNotNull("Running LINK HAKUKOHDE ei toimi."
    			, doit.textElement(driver, "Liitteet"));
    	// valitse alhaalta koulutus
    	WebElement link = doit.findNearestElement("Koulutukset", "//span[contains(@class, 'v-button-wrap')]", driver);
    	link.click();
    	Assert.assertNotNull("Running LINK KOULUTUS ei toimi."
    			, doit.textElement(driver, "Koulutuksen perustiedot"));
    	doit.tauko(1);
    	// "poista koulutuksesta"
    	doit.notPresentText(driver, "window_close"
    			, "Running POISTA KOULUTUKSESTA Close nakyy jo. Ei toimi.");
    	doit.footerTest(driver, "Running HAKUKOHTEEN KOULUTUKSESTA footer ei toimi.", true);
    	while (true)
    	{
    		doit.textClick(driver, "Poista koulutuksesta");
    		Assert.assertNotNull("Running POISTA KOULUTUKSESTA ei toimi."
    				, doit.textElement(driver, "Haluatko poistaa hakukohteen koulutukselta"));
    		String closeId = doit.idLike(driver, "window_close");
    		WebElement close = driver.findElement(By.id(closeId));
    		Assert.assertNotNull("Running POISTA KOULUTUKSESTA ei toimi.", close);
    		doit.findNearestElementPlusY("Haluatko poistaa hakukohteen koulutukselta"
    				, "//span[@class='v-button-caption' and text()='Poista']", driver).click();
    		try {
				Assert.assertNotNull("Running POISTA KOULUTUKSESTA ei toimi.", doit.textElement(driver, "Ei hakukohteita"));
				break;
			} catch (Exception e) {
			}
    	}
    	doit.echo("Running test_T_INT_TAR_SEKA026_PoistaAMPstaHakukohde OK");
    	doit.tauko(1);
    }

    @After
    public void tearDown() throws Exception {
            driver.quit();
            String verificationErrorString = verificationErrors.toString();
            if (!"".equals(verificationErrorString)) {
                    fail(verificationErrorString);
            }
    }
}
