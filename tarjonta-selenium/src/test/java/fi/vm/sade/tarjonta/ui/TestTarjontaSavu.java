package fi.vm.sade.tarjonta.ui;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class TestTarjontaSavu { // KOULUTUS AMP

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

    @Test
    public void test_T_INT_TAR_SAVU001_Etusivu() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU001_Etusivu ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
		Assert.assertNotNull("Running Etusivu ei toimi."
                , doit.textElement(driver, "Valitse kaikki"));
		doit.footerTest(driver, "Running TarjontaSavu001 Etusivu footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU001_Etusivu OK");
		doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU002_HaeOrganisaatiota() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU002_HaeOrganisaatiota ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
        // HAE
    	doit.haePalvelunTarjoaja(driver, "optima", "Optima samkommun");
        Assert.assertNotNull("Running Hae Optima samkommun ei toimi."
        		, doit.textElement(driver, "Optima samkommun"));
    	doit.echo("Running test_T_INT_TAR_SAVU002_HaeOrganisaatiota OK");
        doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU003_HaeKoulutuksia() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU003_HaeKoulutuksia ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haePalvelunTarjoaja(driver, "optima", "Optima samkommun");
        // KOULUTUKSET JA HAKUKOHTEET
        WebElement espoo = driver.findElement(By.xpath("//span[contains(text(), 'Optima samkommun')]"));
        espoo.click();
        Assert.assertNotNull("Running Hae KOULUTUKSET ja hakukohteet ei toimi."
        		, doit.textElement(driver, "Koulutukset ("));
        Assert.assertNotNull("Running Hae koulutukset ja HAKUKOHTEET ei toimi."
        		, doit.textElement(driver, "Hakukohteet ("));
    	doit.echo("Running test_T_INT_TAR_SAVU003_HaeKoulutuksia OK");
        doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU021_DialogLuoAMP() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU021_DialogLuoAMP ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haePalvelunTarjoaja(driver, "optima", "Optima samkommun");
    	doit.textClick(driver, "Optima samkommun");
        // LUO UUSI AMMATILLINENKOULUTUS (validialog)
        doit.textClick(driver, "Luo uusi koulutus");
        Assert.assertNotNull("Running Luo uusi ammatillinenkoulutus ei toimi."
        		, doit.textElement(driver, "Olet luomassa uutta koulutusta"));
        // LUO UUSI AMMATILLINENKOULUTUS (validialog + jatka)
        doit.sendInputPlusX(driver, "Koulutus:", "Ammatillinen peruskoulutus", 200);
        doit.popupItemClick(driver, "Ammatillinen peruskoulutus");
        Assert.assertNotNull("Running Luo uusi ammatillinenkoulutus + jatka ei toimi."
        		, doit.textElement(driver, "Pohjakoulutus:"));
        doit.tauko(1);
        doit.sendInputPlusY(driver, "Pohjakoulutus:", "Peruskoulu");
        doit.popupItemClick(driver, "Peruskoulu");
        doit.tauko(1);
        driver.findElement(By.xpath("//span[@class = 'v-button-caption' and text() = 'Optima samkommun']")).click();
        doit.tauko(1);
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running Luo uusi ammatillinenkoulutus + jatka ei toimi."
        		, doit.textElement(driver, "posti"));
		doit.footerTest(driver, "Running Luo uusi ammatillinenkoulutus + jatka footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU021_DialogLuoAMP OK");
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU022_MuokkaaAMPkoulutusta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU022_MuokkaaAMPkoulutusta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	if (doit.isPresentText(driver, "Optima")) {	doit.textClick(driver, "Poista valinta"); doit.tauko(1); }
    	doit.tarkasteleKoulutusta(driver, "tusohje", "Luonnos");
    	doit.textClick(driver, "muokkaa");
        Assert.assertNotNull("Running TARKASTELE AMMATILLISTAKOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Ammatillinen koulutus"));
        Assert.assertNotNull("Running TARKASTELE AMMATILLISTAKOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Ammattinimikkeet")); // Ammatillinen koulutus
		doit.footerTest(driver, "Running TARKASTELE AMMATILLISTAKOULUTUSTA footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU022_MuokkaaAMPkoulutusta OK");
    	doit.refresh(driver);
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click(); // ei toimi ilman refresh komentoa
        doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU023_MuokkaaAMPkoulutustaKuvailevat() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU023_MuokkaaAMPkoulutustaKuvailevat ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.tarkasteleKoulutusta(driver, "tusohje", "Luonnos");
        // MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot
    	driver.findElement(By.xpath("(//*[text()='muokkaa'])[2]")).click(); // click Muokkaa(2)
        Assert.assertNotNull("Running MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot ei toimi."
        		, doit.textElement(driver, "Koulutusohjelman valinta")); // Ammatillinen koulutus
		doit.footerTest(driver, "Running MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU023_MuokkaaAMPkoulutustaKuvailevat OK");
    	doit.refresh(driver);
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU024_DialogSiirraTaiKopioiAMP() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU024_DialogSiirraTaiKopioiAMP ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "tusohje");
    	doit.triangleClickFirstTriangle(driver);
    	doit.checkboxSelectFirst(driver);
    	doit.textClick(driver, "tai kopioi");
    	String dlgText = "koulutuksen toiseen organisaatioon tai kopioida koulutuksen uuden koulutuksen pohjaksi";
        Assert.assertNotNull("Running SIIRRA TAI KOPIOI ei toimi.", doit.textElement(driver, dlgText));
    	doit.echo("Running test_T_INT_TAR_SAVU024_DialogSiirraTaiKopioiAMP OK");
        doit.tauko(1);
        doit.textClick(driver, "Peruuta");
        doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU025_TarkasteleAMPkoulutusta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU025_TarkasteleAMPkoulutusta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.tarkasteleKoulutusta(driver, "tusohje", null);
        Assert.assertNotNull("Running TARKASTELE KOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Jatko-opintomahdollisuudet"));
		doit.footerTest(driver, "Running TARKASTELE KOULUTUSTA footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU025_TarkasteleAMPkoulutusta OK");
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU026_PoistaAMPstaHakukohde() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU026_PoistaAMPstaHakukohde ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	// hae luonnos hakukohteita
    	doit.haeKoulutuksia(driver, null, "tusohj");
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
    	doit.textClick(driver, "Poista koulutuksesta");
    	Assert.assertNotNull("Running POISTA KOULUTUKSESTA ei toimi."
    			, doit.textElement(driver, "Haluatko poistaa hakukohteen koulutukselta"));
    	String closeId = doit.idLike(driver, "window_close");
    	WebElement close = driver.findElement(By.id(closeId));
    	Assert.assertNotNull("Running POISTA KOULUTUKSESTA ei toimi.", close);
    	doit.echo("Running test_T_INT_TAR_SAVU026_PoistaAMPstaHakukohde OK");
    	doit.tauko(1);
    	close.click();
    	doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        doit.tauko(1);
    	doit.refreshTarjontaEtusivu(driver);
    	doit.tauko(1);
    }
    
    @Test
    public void test_T_INT_TAR_SAVU027_DialogPoistaAMPKoulutus() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU027_DialogPoistaAMPKoulutus ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, "Luonnos", "tusohj");
        doit.triangleClickFirstTriangle(driver);
    	doit.menuOperaatio(driver, "Poista", "luonnos");
    	Assert.assertNotNull("Running POISTA KOULUTUS ei toimi."
    			, doit.textElement(driver, "Haluatko varmasti poistaa"));
    	String closeId = doit.idLike(driver, "window_close");
    	WebElement close = driver.findElement(By.id(closeId));
    	Assert.assertNotNull("Running POISTA KOULUTUS ei toimi.", close);
    	doit.echo("Running test_T_INT_TAR_SAVU027_DialogPoistaAMPKoulutus OK");
    	doit.tauko(1);
    	close.click();
    	doit.tauko(1);
    }
    
    @Test
    public void test_T_INT_TAR_SAVU028_DialogAMPNaytaHakukohteet() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU028_DialogAMPNaytaHakukohteet ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, "Julkaistu", "tusohj");
        doit.triangleClickFirstTriangle(driver);
    	doit.menuOperaatioFirstMenu(driver, "Näytä hakukohteet");
    	try {
			Assert.assertNotNull("Running NAYTA HAKUKOHTEET ei toimi."
					, doit.textElement(driver, "Olet tarkastelemassa hakukohteita, jotka liittyv"));
		} catch (Exception e) {
			Assert.assertNotNull("Running NAYTA HAKUKOHTEET ei toimi."
					, doit.textElement(driver, "Koulutukseen ei ole liitetty hakukohteita"));
		}
    	doit.echo("Running test_T_INT_TAR_SAVU028_DialogAMPNaytaHakukohteet OK");
    	doit.tauko(1);
    	doit.textClick(driver, "Sulje");
    	doit.tauko(1);    	
    }

    @Test
    public void test_T_INT_TAR_SAVU029_DialogRinnakkainenAMPkoulutus() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU029_DialogRinnakkainenAMPkoulutus ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.tarkasteleKoulutusta(driver, "tusohje", "Luonnos");
		doit.textClick(driver, "rinnakkainen toteutus");
		Assert.assertNotNull("Running Lisaa rinnakkainen toteutus ei toimi."
				, doit.textElement(driver, "Valitse pohjakoulutus"));
    	doit.echo("Running test_T_INT_TAR_SAVU029_DialogRinnakkainenAMPkoulutus OK");
		doit.tauko(1);
		driver.quit();
		driverQuit = true;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    @After
    public void tearDown() throws Exception {
    	doit.quit(driver);
//    	driver.quit();
//    	driverQuit = true;
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
        	fail(verificationErrorString);
        }
    }
}
