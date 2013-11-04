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

public class TestTarjontaSavuHakukohdeAMP {

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

    @Test
    public void test_T_INT_TAR_SAVU104_HAKO_LuoAMPHakukohde() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU104_HAKO_LuoAMPHakukohde ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "tusohj");
    	doit.triangleClickFirstTriangle(driver);
    	doit.checkboxSelectFirst(driver);
        String a_text = "Olet luomassa uutta hakukohdetta seuraavista koulutuksista";
        doit.textClick(driver, "Luo uusi hakukohde");
        Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi."
        		, doit.textElement(driver, a_text));
    	doit.tauko(1);

    	doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi."
        		, doit.textElement(driver, "tietoja hakemisesta"));
        Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi."
        		, doit.textElement(driver, "Tallenna luonnoksena"));
    	doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) footer ei toimi.", true);
        doit.echo("Running test_T_INT_TAR_SAVU104_HAKO_LuoAMPHakukohde OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU105_HAKO_TarkasteleAMPHakukohdetta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU105_HAKO_TarkasteleAMPHakukohdetta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "tusohj");
        // HAKUKOHTEEN TARKASTELU
    	doit.tarkasteleJokuHakukohde(driver);
        Assert.assertNotNull("Running HAKUKOHTEEN TARKASTELU ei toimi."
        		, doit.textElement(driver, "Koulutukset"));
    	doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU105_HAKO_TarkasteleAMPHakukohdetta OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU106_HAKO_MuokkaaAMPHakukohdetta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU106_HAKO_MuokkaaAMPHakukohdetta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "tusohj");
    	doit.tarkasteleJokuHakukohde(driver);
    	doit.textClick(driver, "muokkaa");
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS ei toimi."
    			, doit.textElement(driver, "voidaan kuvata muuta hakemiseen olennaisesti"));
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS ei toimi."
    			, doit.textElement(driver, "Tallenna valmiina"));
    	doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU106_HAKO_MuokkaaAMPHakukohdetta OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU107_HAKO_MuokkaaAMPHakukohteenLiitteidenTiedot() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU107_HAKO_MuokkaaAMPHakukohteenLiitteidenTiedot ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "tusohj");
    	doit.tarkasteleJokuHakukohde(driver);
    	// HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot)
    	driver.findElement(By.xpath("(//*[text()='muokkaa'])[3]")).click(); // click Muokkaa(3)
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) ei toimi."
    			, doit.textElement(driver, "Toimitusosoite"));
    	doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU107_HAKO_MuokkaaAMPHakukohteenLiitteidenTiedot OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU111_HAKO_DialogMuokkaaAMPHakukohdeLisaaUusiLiite() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU111_HAKO_DialogMuokkaaAMPHakukohdeLisaaUusiLiite ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "tusohj");
    	doit.tarkasteleJokuHakukohde(driver);
    	driver.findElement(By.xpath("(//*[text()='muokkaa'])[3]")).click(); // click Muokkaa(3)
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite) ei toimi."
    			, doit.textElement(driver, "Toimitusosoite"));
    	doit.tauko(1);
    	// LISAA UUSI LIITE
    	doit.textClick(driver, "uusi liite");
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite) ei toimi."
    			, doit.textElement(driver, "Voidaan toimittaa my"));
    	doit.echo("Running test_T_INT_TAR_SAVU111_HAKO_DialogMuokkaaAMPHakukohdeLisaaUusiLiite OK");
    	doit.tauko(1);
    	doit.textClick(driver, "Peruuta");
    	doit.tauko(1);
    	driver.findElement(By.className("v-button-back")).click();
    	doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    }

    @Test
    public void test_T_INT_TAR_SAVU108_HAKO_MuokkausHakuAMPkohdeValintakokeet() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU108_HAKO_MuokkausHakuAMPkohdeValintakokeet ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "tusohj");
    	doit.tarkasteleJokuHakukohde(driver);
    	// HAKUKOHTEEN MUOKKAUS (valintakokeet)
    	driver.findElement(By.xpath("(//*[text()='muokkaa'])[2]")).click(); // click Muokkaa(2)
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (Valintakokeiden tiedot) ei toimi."
    			, doit.textElement(driver, "uusi valintakoe"));
		Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (Valintakokeiden tiedot) ei toimi."
				, doit.textElement(driver, "Valintakokeen kuvaus"));
		doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (Valintakokeiden tiedot) footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU108_HAKO_MuokkausHakuAMPkohdeValintakokeet OK");
    	doit.tauko(1);
    	driver.findElement(By.className("v-button-back")).click();
    	doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    }

    @Test
    public void test_T_INT_TAR_SAVU112_HAKO_DialogMuokkausHakuAMPkohdeUusiValintakoe() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU112_HAKO_MuokkausHakuAMPkohdeUusiValintakoe ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "tusohj");
    	doit.tarkasteleJokuHakukohde(driver);
    	driver.findElement(By.xpath("(//*[text()='muokkaa'])[2]")).click(); // click Muokkaa(2)
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (uusi valintakoe) ei toimi."
    			, doit.textElement(driver, "uusi valintakoe"));
    	// UUSI VALINTAKOE    	
		doit.textClick(driver, "uusi valintakoe");
		Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (uusi valintakoe) ei toimi."
				, doit.textElement(driver, "Ajankohta"));
    	doit.echo("Running test_T_INT_TAR_SAVU112_HAKO_MuokkausHakuAMPkohdeUusiValintakoe OK");
    	doit.tauko(1);
    	doit.textClick(driver, "Peruuta");
    	doit.tauko(1);
    	driver.findElement(By.className("v-button-back")).click();
    	doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    }

    @Test
    public void test_T_INT_TAR_SAVU109_HAKO_DialogHakukohteenAMPPoisto() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU109_HAKO_DialogHakukohteenAMPPoisto ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, "Luonnos", "tusohj");
    	doit.tarkasteleJokuHakukohde(driver);
    	doit.notPresentText(driver, "window_close"
    			, "Running HAKUKOHTEEN POISTO Close nakyy jo. Ei toimi.");
    	doit.textClick(driver, "Poista");
		try {
			Assert.assertNotNull("Running HAKUKOHTEEN POISTO ei toimi."
					, doit.textElement(driver, "Haluatko varmasti poistaa seuraavan hakukohteen"));
		} catch (Exception e) {
			if (doit.isPresentText(driver, "hakukohdePoistoEpaonnistui"))
			{
				doit.echo("hakukohdePoistoEpaonnistui virhe toistuu yha");
				doit.textClick(driver, "hakukohdePoistoEpaonnistui");
		    	doit.tauko(1);
		    	driver.findElement(By.className("v-button-back")).click();
		    	doit.tauko(1);
		    	doit.refreshTarjontaEtusivu(driver);
		    	return;
			}
			else
			{
				Assert.assertNotNull("Running HAKUKOHTEEN POISTO ei toimi."
						, doit.textElement(driver, "Hakukohdetta ei voi poistaa koska haku on jo"));
			}
		}
    	doit.echo("Running test_T_INT_TAR_SAVU109_HAKO_DialogHakukohteenAMPPoisto OK");
    	doit.tauko(1);
    	doit.textClick(driver, "Peruuta");
    	driver.findElement(By.className("v-button-back")).click();
    	doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    }

    @Test
    public void test_T_INT_TAR_SAVU110_HAKO_DialogKoulutuksenPoistoAMPHakukohteesta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU110_HAKO_KoulutuksenPoistoAMPHakukohteesta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "tusohj");
    	doit.tarkasteleJokuHakukohde(driver);
    	doit.textClick(driver, "Poista hakukohteesta");
		try {
			Assert.assertNotNull("Running Koulutuksen poisto hakukohteesta ei toimi."
					, doit.textElement(driver, "Haluatko poistaa koulutuksen hakukohteelta"));
		} catch (Exception e) {
			if (doit.isPresentText(driver, "removeKoulutusFromHakukohde"))
			{
				doit.echo("removeKoulutusFromHakukohde virhe toistuu yha");
				doit.textClick(driver, "eruuta");
		    	doit.tauko(1);
		    	driver.findElement(By.className("v-button-back")).click();
		    	doit.tauko(1);
		    	doit.refreshTarjontaEtusivu(driver);
		    	return;
			}
		}
    	doit.echo("Running test_T_INT_TAR_SAVU110_HAKO_KoulutuksenPoistoAMPHakukohteesta OK");
    	doit.tauko(1);
    	doit.textClick(driver, "Peruuta");
    	driver.findElement(By.className("v-button-back")).click();
    	doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU101_HAKO_DialogHakukohteenAMPPeruutus() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU101_HAKO_DialogHakukohteenAMPPeruutus ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeHakukohteita(driver, "Julkaistu", "tusohj");
//    	doit.tarkasteleJokuHakukohde(driver);
    	doit.triangleClickLastTriangle(driver);
    	doit.notPresentText(driver, "window_close"
    			, "Running HAKUKOHTEEN PERUUTUS Close nakyy jo. Ei toimi.");
    	doit.menuOperaatio(driver, "Peruuta hakukohde", "julkaistu");
//    	doit.textClick(driver, "Peruuta");
		Assert.assertNotNull("Running HAKUKOHTEEN PERUUTUS ei toimi."
				, doit.textElement(driver, "Olet peruuttamassa hakukohdetta"));
    	doit.echo("Running test_T_INT_TAR_SAVU101_HAKO_DialogHakukohteenAMPPeruutus OK");
    	doit.tauko(1);
    	doit.textClick(driver, "Ei");
//    	driver.findElement(By.className("v-button-back")).click();
//    	doit.tauko(1);
//    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
//      driver.quit();
//      driverQuit = true;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    	
    @After
    public void tearDown() throws Exception {
    	driver.quit();
    	driverQuit = true;
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
        	fail(verificationErrorString);
        }
    }
}
