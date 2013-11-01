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

public class TestTarjontaSavuKoulutusLukio {

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
    public void test_T_INT_TAR_SAVU011_DialogLuoLukiokoulutus() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU011_DialogLuoLukiokoulutus ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haePalvelunTarjoaja(driver, "kerttulin", "Kerttulin lukio");
        // LUO UUSI LUKIOKOULUTUS (validialog)
        doit.textClick(driver, "Kerttulin lukio");
        doit.tauko(1);
        doit.textClick(driver, "Luo uusi koulutus");
        Assert.assertNotNull("Running Luo uusi lukiokoulutus ei toimi."
        		, doit.textElement(driver, "Olet luomassa uutta koulutusta"));
        doit.tauko(1);
        
        // LUO UUSI LUKIOKOULUTUS (validialog + jatka)
        doit.sendInputPlusX(driver, "Koulutus:", "Lukiokoulutus", 200);
        doit.popupItemClick(driver, "Lukiokoulutus");
        doit.tauko(1);
        driver.findElement(By.xpath("//span[@class = 'v-button-caption' and text() = 'Kerttulin lukio']")).click();
        doit.tauko(1);
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running Luo uusi lukiokoulutus + jatka ei toimi."
        		, doit.textElement(driver, "posti"));
		doit.footerTest(driver, "Running Luo uusi lukiokoulutus + jatka footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU011_DialogLuoLukiokoulutus OK");
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU012_MuokkaaLukiokoulutusta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU012_MuokkaaLukiokoulutusta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.tarkasteleKoulutusta(driver, "ylioppilastut", "Luonnos");
        doit.textClick(driver, "muokkaa");
        Assert.assertNotNull("Running MUOKKAA KOULUTUSTA ei toimi."
        		, doit.textElement(driver, "posti"));
        Assert.assertNotNull("Running MUOKKAA KOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Tallenna valmiina"));
		doit.footerTest(driver, "Running MUOKKAA KOULUTUSTA footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU012_MuokkaaLukiokoulutusta OK");
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU013_MuokkaaLukiokoulutustaKuvailevat() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU013_MuokkaaLukiokoulutustaKuvailevat ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.tarkasteleKoulutusta(driver, "ylioppilastut", "Luonnos");
    	driver.findElement(By.xpath("(//*[text()='muokkaa'])[2]")).click(); // click Muokkaa(2)
        Assert.assertNotNull("Running MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot ei toimi."
        		, doit.textElement(driver, "muiden toimijoiden kanssa")); // lukiokoulutus
		doit.footerTest(driver, "Running MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU013_MuokkaaLukiokoulutustaKuvailevat OK");
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU014_DialogSiirraTaiKopioiLukio() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU014_DialogSiirraTaiKopioiLukio ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haePalvelunTarjoaja(driver, "kerttulin", "Kerttulin lukio");
    	doit.textClick(driver, "Kerttulin lukio");
    	doit.tauko(1);
    	// SIIRRA TAI KOPIOI KOULUTUS
    	Assert.assertNotNull("Running SIIRRA TAI KOPIOI KOULUTUS ei toimi."
    			, doit.textElement(driver, "Koulutukset ("));
//        if (! doit.isPresentText(driver, "Koulutukset (0)"))
//        {
        if (! doit.isPresentText(driver, "v-icon"))
        {
        	driver.findElement(By.className("v-treetable-treespacer")).click();
            doit.tauko(1);
        }
        Assert.assertNotNull("Running SIIRRA TAI KOPIOI KOULUTUS ei toimi."
                , driver.findElement(By.xpath("//img[@class='v-icon']")));
        String gwtId = doit.getGwtIdForFirstHakukohde(driver);
        driver.findElement(By.id(gwtId)).click();
        doit.tauko(1);
        doit.textClick(driver, "tai kopioi");
//        //        Assert.assertNotNull("Running TarjontaSavu012 SIIRRA TAI KOPIOI KOULUTUS ei toimi.", doit.textElement(driver
//        //           		, "koulutuksen toiseen organisaatioon tai kopioida koulutuksen uuden koulutuksen pohjaksi. Valitse toimenpide, jonka haluat"));
    	while (! doit.isPresentText(driver, "koulutuksen toiseen organisaatioon tai kopioida koulutuksen uuden koulutuksen pohjaksi. Valitse toimenpide, jonka haluat")) 
    	{ doit.tauko(1); }
    	doit.echo("Running test_T_INT_TAR_SAVU014_DialogSiirraTaiKopioiLukio OK");
    	doit.tauko(1);
    	doit.textClick(driver, "Peruuta");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU015_TarkasteleLukiokoulutusta() throws Exception 
    {
    	doit.echo("Running test_T_INT_TAR_SAVU015_TarkasteleLukiokoulutusta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
        // TARKASTELE LUKIOKOULUTUS
    	doit.tarkasteleKoulutusta(driver, "ylioppilastut", "Luonnos");
        Assert.assertNotNull("Running TARKASTELE LUKIOKOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Lukiokoulutus"));
        Assert.assertNotNull("Running TARKASTELE LUKIOKOULUTUSTA ei toimi."
        		, doit.textElement(driver, "muiden toimijoiden kanssa")); // lukiokoulutus
		doit.footerTest(driver, "Running TARKASTELE LUKIOKOULUTUSTA footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU015_TarkasteleLukiokoulutusta OK");
        doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU016_PoistaLukiokoulutukseltaHakukohde() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU016_PoistaLukiokoulutukseltaHakukohde ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	// hae lukion hakukohteita
    	doit.haeKoulutuksia(driver, null, "Lukion");
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
    	doit.echo("Running test_T_INT_TAR_SAVU016_PoistaLukiokoulutukseltaHakukohde OK");
    	doit.tauko(1);
    	close.click();
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU017_PoistaLukiokoulutus() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU017_PoistaLukiokoulutus ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, "Luonnos", "ylioppilastut");
        doit.triangleClickFirstTriangle(driver);
    	doit.menuOperaatio(driver, "Poista", "luonnos");
    	Assert.assertNotNull("Running POISTA KOULUTUS ei toimi."
    			, doit.textElement(driver, "Haluatko varmasti poistaa"));
    	String closeId = doit.idLike(driver, "window_close");
    	WebElement close = driver.findElement(By.id(closeId));
    	Assert.assertNotNull("Running TarjontaSavu007b POISTA KOULUTUS ei toimi.", close);
    	doit.echo("Running test_T_INT_TAR_SAVU017_PoistaLukiokoulutus OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU018_DialogLukioNaytaHakukohteet() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU018_DialogLukioNaytaHakukohteet ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, "Julkaistu", "ylioppilastut");
        doit.triangleClickFirstTriangle(driver);
    	doit.menuOperaatioFirstMenu(driver, "Näytä hakukohteet");
    	Assert.assertNotNull("Running NAYTA HAKUKOHTEET ei toimi."
    			, doit.textElement(driver, "Olet tarkastelemassa hakukohteita, jotka liittyv"));
    	doit.echo("Running test_T_INT_TAR_SAVU018_DialogLukioNaytaHakukohteet OK");
    	doit.tauko(1);
//      driver.quit();
//      driverQuit = true;
    }

//@Test
//public void test_T_INT_TAR_SAVU019_RinnakkainenLukiokoulutus() throws Exception {
//    	doit.echo("Running test_T_INT_TAR_SAVU019_RinnakkainenLukiokoulutus ...");
//    	doit.tarjonnanEtusivu(driver, baseUrl);
//    	doit.tarkasteleKoulutusta(driver, "ylioppilastut", "Luonnos");
//		doit.textClick(driver, "rinnakkainen toteutus");
//		Assert.assertNotNull("Running Lisaa rinnakkainen toteutus ei toimi."
//				, doit.textElement(driver, "Valitse pohjakoulutus"));
//    	doit.echo("Running test_T_INT_TAR_SAVU019_RinnakkainenLukiokoulutus OK");
//		doit.tauko(1);
//    }

    ///////////////////////////////////////////////////////////////////////////////
	
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
