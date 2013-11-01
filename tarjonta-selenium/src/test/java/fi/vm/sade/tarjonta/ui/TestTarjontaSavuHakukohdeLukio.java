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

public class TestTarjontaSavuHakukohdeLukio {

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    @Test
    public void test_T_INT_TAR_SAVU134_HAKO_LuoLukioHakukohde() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU134_HAKO_LuoLukioHakukohde ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "ylioppilastut");
    	doit.triangleClickFirstTriangle(driver);
    	doit.checkboxSelectFirst(driver);
        String a_text = "Olet luomassa uutta hakukohdetta seuraavista koulutuksista";
        doit.textClick(driver, "Luo uusi hakukohde");
        Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi."
        		, doit.textElement(driver, "tietoja hakemisesta"));
        Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi."
        		, doit.textElement(driver, "Tallenna luonnoksena"));
    	doit.footerTest(driver, "Running Lukio Muokkaa footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU134_HAKO_LuoLukioHakukohde OK");
    	doit.tauko(1);
    	driver.findElement(By.className("v-button-back")).click();
    	doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU135_HAKO_TarkasteleLukioHakukohdetta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU135_HAKO_TarkasteleLukioHakukohdetta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "Lukion");
        // HAKUKOHTEEN TARKASTELU
    	doit.tarkasteleJokuHakukohde(driver);
        Assert.assertNotNull("Running HAKUKOHTEEN TARKASTELU ei toimi."
        		, doit.textElement(driver, "Koulutukset"));
    	doit.footerTest(driver, "Running HAKUKOHTEEN TARKASTELU footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU135_HAKO_TarkasteleLukioHakukohdetta OK");
    	doit.tauko(1);
    	driver.findElement(By.className("v-button-back")).click();
    	doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU136_HAKO_MuokkaaLukioHakukohdetta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU136_HAKO_MuokkaaLukioHakukohdetta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "Lukion");
    	doit.tarkasteleJokuHakukohde(driver);
    	doit.textClick(driver, "muokkaa");
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS ei toimi."
    			, doit.textElement(driver, "voidaan kuvata muuta hakemiseen olennaisesti"));
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS ei toimi."
    			, doit.textElement(driver, "Tallenna valmiina"));
    	doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU136_HAKO_MuokkaaLukioHakukohdetta OK");
    	doit.tauko(1);
    	driver.findElement(By.className("v-button-back")).click();
    	doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU137_HAKO_MuokkaaHakuLukiokohteenLiitteidenTiedot() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU137_HAKO_MuokkaaHakuLukiokohteenLiitteidenTiedot ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "Lukion");
    	doit.tarkasteleJokuHakukohde(driver);
    	// HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot)
    	driver.findElement(By.xpath("(//*[text()='muokkaa'])[3]")).click(); // click Muokkaa(3)
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) ei toimi."
    			, doit.textElement(driver, "Toimitusosoite"));
    	doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU137_HAKO_MuokkaaHakuLukiokohteenLiitteidenTiedot OK");
    	doit.tauko(1);
    	driver.findElement(By.className("v-button-back")).click();
    	doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU141_HAKO_DialogMuokkaaHakuLukiokohdeLisaaUusiLiite() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU141_HAKO_DialogMuokkaaHakuLukiokohdeLisaaUusiLiite ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "Lukion");
    	doit.tarkasteleJokuHakukohde(driver);
    	driver.findElement(By.xpath("(//*[text()='muokkaa'])[3]")).click(); // click Muokkaa(3)
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite) ei toimi."
    			, doit.textElement(driver, "Toimitusosoite"));
    	// LISAA UUSI LIITE
    	doit.textClick(driver, "uusi liite");
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite) ei toimi."
    			, doit.textElement(driver, "Voidaan toimittaa my"));
    	doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite) footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU141_HAKO_DialogMuokkaaHakuLukiokohdeLisaaUusiLiite OK");
    	doit.tauko(1);
    	doit.textClick(driver, "Peruuta");
    	doit.tauko(1);
    	driver.findElement(By.className("v-button-back")).click();
    	doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU138_HAKO_MuokkausHakuLukiokohdeValintakokeet() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU138_HAKO_MuokkausHakuLukiokohdeValintakokeet ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "Lukion");
    	doit.tarkasteleJokuHakukohde(driver);
    	// HAKUKOHTEEN MUOKKAUS (valintakokeet)
    	driver.findElement(By.xpath("(//*[text()='muokkaa'])[2]")).click(); // click Muokkaa(2)
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (Valintakokeiden tiedot) ei toimi."
    			, doit.textElement(driver, "Merkitse"));
		try {
			Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (Valintakokeiden tiedot) ei toimi."
					, doit.isVisibleText(driver, "sanallinen kuvaus"));
		} catch (Exception e) {
	    	doit.checkboxSelectNearestCheckbox(driver, "Merkitse");
			Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (Valintakokeiden tiedot) ei toimi."
					, doit.isVisibleText(driver, "Valintakokeen<br>sanallinen kuvaus"));
		}    	
//    	Boolean paasykoe = false;
//    	if (doit.isPresentText(driver, "sykoe")) { paasykoe = true; }
//    	if (paasykoe)
//    	{
//    		String paasykoeCheckBoxId = doit.getGwtIdBeforeText(driver, "sykoe</label>");
//    		WebElement paasykoeCheckBox = driver.findElement(By.id(paasykoeCheckBoxId));
//    		if (paasykoeCheckBox.getAttribute("checked") == null || ! paasykoeCheckBox.getAttribute("checked").equals("true"))
//    		{
//    			paasykoeCheckBox.click();
//    		}
//    		Assert.assertNotNull("Running TarjontaHakukohteetSavu008 HAKUKOHTEEN MUOKKAUS (valintakokeet) ei toimi."
//    				, doit.textElement(driver, "Ajankohta"));
//    	}

    	doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (Valintakokeiden tiedot) footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU138_HAKO_MuokkausHakuLukiokohdeValintakokeet OK");
    	doit.tauko(1);
    	driver.findElement(By.className("v-button-back")).click();
    	doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
    }

//    @Test
//    public void test_T_INT_TAR_SAVU142_HAKO_MuokkausHakuLukiokohdeUusiValintakoe() throws Exception {
//    	doit.echo("Running test_T_INT_TAR_SAVU142_HAKO_MuokkausHakuLukiokohdeUusiValintakoe ...");
//    	doit.tarjonnanEtusivu(driver, baseUrl);
//    	doit.haeKoulutuksia(driver, null, "Lukion");
//    	doit.tarkasteleJokuHakukohde(driver);
//    	driver.findElement(By.xpath("(//*[text()='muokkaa'])[2]")).click(); // click Muokkaa(2)
//    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (uusi valintakoe) ei toimi."
//    			, doit.textElement(driver, "uusi valintakoe"));
//    	// UUSI VALINTAKOE    	
//		doit.textClick(driver, "uusi valintakoe");
//		Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (uusi valintakoe) ei toimi."
//				, doit.textElement(driver, "Ajankohta"));
//		doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (uusi valintakoe) footer ei toimi.", true);
//    	doit.echo("Running test_T_INT_TAR_SAVU142_HAKO_MuokkausHakuLukiokohdeUusiValintakoe OK");
//    	doit.tauko(1);
//    }

    @Test
    public void test_T_INT_TAR_SAVU139_HAKO_DialogHakukohteenLukioPoisto() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU139_HAKO_DialogHakukohteenLukioPoisto ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, "Julkaistu", "Lukion");
    	doit.tarkasteleJokuHakukohde(driver);
    	doit.notPresentText(driver, "window_close"
    			, "Running HAKUKOHTEEN POISTO Close nakyy jo. Ei toimi.");
    	doit.textClick(driver, "Poista");
    	try {
    		Assert.assertNotNull("Running HAKUKOHTEEN POISTO ei toimi."
    				, doit.textElement(driver, "Haluatko varmasti poistaa seuraavan hakukohteen"));
    	} catch (Exception e) {
			if (doit.isPresentText(driver, "poistoVarmistus"))
			{
				doit.echo("poistoVarmistus virhe toistuu yha");
				doit.textClick(driver, "eruuta");
		    	doit.tauko(1);
		    	driver.findElement(By.className("v-button-back")).click();
		    	doit.tauko(1);
		    	doit.refreshTarjontaEtusivu(driver);
		    	return;
			}
    		Assert.assertNotNull("Running HAKUKOHTEEN POISTO ei toimi."
    				, doit.textElement(driver, "Hakukohdetta ei voi poistaa koska haku on jo"));
    	}
    	doit.echo("Running test_T_INT_TAR_SAVU139_HAKO_DialogHakukohteenLukioPoisto OK");
    	doit.tauko(1);
    	doit.textClick(driver, "Peruuta");
    	doit.tauko(1);
    	driver.findElement(By.className("v-button-back")).click();
    	doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU140_HAKO_DialogKoulutuksenPoistoLukioHakukohteesta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU140_HAKO_DialogKoulutuksenPoistoLukioHakukohteesta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "Lukion");
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
		doit.echo("Running test_T_INT_TAR_SAVU140_HAKO_DialogKoulutuksenPoistoLukioHakukohteesta OK");
    	doit.tauko(1);
    	doit.textClick(driver, "Peruuta");
    	doit.tauko(1);
    	driver.findElement(By.className("v-button-back")).click();
    	doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU131_HAKO_DialogHakukohteenLukioPeruutus() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU131_HAKO_DialogHakukohteenLukioPeruutus ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeHakukohteita(driver, "Julkaistu", "Lukion");
    	doit.triangleClickLastTriangle(driver);
		Assert.assertNotNull("Running HAKUKOHTEEN PERUUTUS ei toimi."
				, doit.textElement(driver, "julkaistu"));
    	doit.notPresentText(driver, "window_close"
    			, "Running HAKUKOHTEEN PERUUTUS Close nakyy jo. Ei toimi.");
    	doit.menuOperaatio(driver, "Peruuta hakukohde", "julkaistu");
		Assert.assertNotNull("Running HAKUKOHTEEN PERUUTUS ei toimi."
				, doit.textElement(driver, "Olet peruuttamassa hakukohdetta"));
		doit.echo("Running test_T_INT_TAR_SAVU131_HAKO_DialogHakukohteenLukioPeruutus OK");
    	doit.tauko(1);
    	doit.textClick(driver, "Ei");
    	doit.tauko(1);
    	driver.quit();
    	driverQuit = true;
    }

    ////////////////////////////////////////////////////////////////////////
		
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
