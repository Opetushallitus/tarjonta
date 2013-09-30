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

public class TestTarjontaSavu {

    private WebDriver driver;
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
    		driver = new FirefoxDriver(firefoxProfile);
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
        if (SVTUtils.prop.getProperty("tarjonta-selenium.luokka").equals("true"))
        {
                luokka = true;
        }
        if (SVTUtils.prop.getProperty("tarjonta-selenium.qa").equals("true"))
        {
                qa = true;
        }

    	baseUrl = SVTUtils.prop.getProperty("tarjonta-selenium.oph-url"); // "http://localhost:8080/"
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
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
    }

    @Test
    public void test_T_INT_TAR_SAVU017_PoistaLukiokoulutus() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU017_PoistaLukiokoulutus ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, "luonnos", "ylioppilastut");
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

    @Test
    public void test_T_INT_TAR_SAVU021_DialogLuoAMK() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU021_DialogLuoAMK ...");
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
    	doit.echo("Running test_T_INT_TAR_SAVU021_DialogLuoAMK OK");
        doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU022_MuokkaaAMKkoulutusta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU022_MuokkaaAMKkoulutusta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.tarkasteleKoulutusta(driver, "tusohje", "Luonnos");
    	doit.textClick(driver, "muokkaa");
        Assert.assertNotNull("Running TARKASTELE AMMATILLISTAKOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Ammatillinen koulutus"));
        Assert.assertNotNull("Running TARKASTELE AMMATILLISTAKOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Ammattinimikkeet")); // Ammatillinen koulutus
		doit.footerTest(driver, "Running TARKASTELE AMMATILLISTAKOULUTUSTA footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU022_MuokkaaAMKkoulutusta OK");
        doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU023_MuokkaaAMKkoulutustaKuvailevat() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU023_MuokkaaAMKkoulutustaKuvailevat ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.tarkasteleKoulutusta(driver, "tusohje", "Luonnos");
        // MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot
    	driver.findElement(By.xpath("(//*[text()='muokkaa'])[2]")).click(); // click Muokkaa(2)
        Assert.assertNotNull("Running MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot ei toimi."
        		, doit.textElement(driver, "Koulutusohjelman valinta")); // Ammatillinen koulutus
		doit.footerTest(driver, "Running MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU023_MuokkaaAMKkoulutustaKuvailevat OK");
        doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU024_DialogSiirraTaiKopioiAMK() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU024_DialogSiirraTaiKopioiAMK ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "tusohje");
    	doit.triangleClickFirstTriangle(driver);
    	doit.checkboxSelectFirst(driver);
    	doit.textClick(driver, "tai kopioi");
    	String dlgText = "koulutuksen toiseen organisaatioon tai kopioida koulutuksen uuden koulutuksen pohjaksi";
        Assert.assertNotNull("Running SIIRRA TAI KOPIOI ei toimi.", doit.textElement(driver, dlgText));
    	doit.echo("Running test_T_INT_TAR_SAVU024_DialogSiirraTaiKopioiAMK OK");
        doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU025_TarkasteleAMKkoulutusta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU025_TarkasteleAMKkoulutusta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.tarkasteleKoulutusta(driver, "tusohje", null);
        Assert.assertNotNull("Running TARKASTELE KOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Jatko-opintomahdollisuudet"));
		doit.footerTest(driver, "Running TARKASTELE KOULUTUSTA footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU025_TarkasteleAMKkoulutusta OK");
        doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU026_PoistaAMKstaHakukohde() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU026_PoistaAMKstaHakukohde ...");
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
    	doit.footerTest(driver, "Running HAKUKOHTEEN KOULUTUKSESTA footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU026_PoistaAMKstaHakukohde OK");
    	doit.tauko(1);
    }
    
    @Test
    public void test_T_INT_TAR_SAVU027_DialogPoistaAMKKoulutus() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU027_DialogPoistaAMKKoulutus ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, "Luonnos", "tusohj");
        doit.triangleClickFirstTriangle(driver);
    	doit.menuOperaatio(driver, "Poista", "luonnos");
    	Assert.assertNotNull("Running POISTA KOULUTUS ei toimi."
    			, doit.textElement(driver, "Haluatko varmasti poistaa"));
    	String closeId = doit.idLike(driver, "window_close");
    	WebElement close = driver.findElement(By.id(closeId));
    	Assert.assertNotNull("Running POISTA KOULUTUS ei toimi.", close);
    	doit.echo("Running test_T_INT_TAR_SAVU027_DialogPoistaAMKKoulutus OK");
    	doit.tauko(1);
    }
    
    @Test
    public void test_T_INT_TAR_SAVU028_DialogAMKNaytaHakukohteet() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU028_DialogAMKNaytaHakukohteet ...");
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
    	doit.echo("Running test_T_INT_TAR_SAVU028_DialogAMKNaytaHakukohteet OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU029_DialogRinnakkainenAMKkoulutus() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU029_DialogRinnakkainenAMKkoulutus ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.tarkasteleKoulutusta(driver, "tusohje", "Luonnos");
		doit.textClick(driver, "rinnakkainen toteutus");
		Assert.assertNotNull("Running Lisaa rinnakkainen toteutus ei toimi."
				, doit.textElement(driver, "Valitse pohjakoulutus"));
    	doit.echo("Running test_T_INT_TAR_SAVU029_DialogRinnakkainenAMKkoulutus OK");
		doit.tauko(1);
    }

    ///////////////////////////////////////////////////////////////////////////////

    @Test
    public void test_T_INT_TAR_SAVU104_HAKO_LuoAMKHakukohde() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU104_HAKO_LuoAMKHakukohde ...");
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
        doit.echo("Running test_T_INT_TAR_SAVU104_HAKO_LuoAMKHakukohde OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU105_HAKO_TarkasteleAMKHakukohdetta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU105_HAKO_TarkasteleAMKHakukohdetta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "tusohj");
        // HAKUKOHTEEN TARKASTELU
    	doit.tarkasteleJokuHakukohde(driver);
        Assert.assertNotNull("Running HAKUKOHTEEN TARKASTELU ei toimi."
        		, doit.textElement(driver, "Koulutukset"));
    	doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU105_HAKO_TarkasteleAMKHakukohdetta OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU106_HAKO_MuokkaaAMKHakukohdetta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU106_HAKO_MuokkaaAMKHakukohdetta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "tusohj");
    	doit.tarkasteleJokuHakukohde(driver);
    	doit.textClick(driver, "muokkaa");
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS ei toimi."
    			, doit.textElement(driver, "voidaan kuvata muuta hakemiseen olennaisesti"));
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS ei toimi."
    			, doit.textElement(driver, "Tallenna valmiina"));
    	doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU106_HAKO_MuokkaaAMKHakukohdetta OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU107_HAKO_MuokkaaAMKHakukohteenLiitteidenTiedot() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU107_HAKO_MuokkaaAMKHakukohteenLiitteidenTiedot ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "tusohj");
    	doit.tarkasteleJokuHakukohde(driver);
    	// HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot)
    	driver.findElement(By.xpath("(//*[text()='muokkaa'])[3]")).click(); // click Muokkaa(3)
    	Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) ei toimi."
    			, doit.textElement(driver, "Toimitusosoite"));
    	doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU107_HAKO_MuokkaaAMKHakukohteenLiitteidenTiedot OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU111_HAKO_MuokkaaAMKHakukohdeLisaaUusiLiite() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU111_HAKO_MuokkaaAMKHakukohdeLisaaUusiLiite ...");
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
    	doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite) footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU111_HAKO_MuokkaaAMKHakukohdeLisaaUusiLiite OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU108_HAKO_MuokkausHakuAMKkohdeValintakokeet() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU108_HAKO_MuokkausHakuAMKkohdeValintakokeet ...");
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
    	doit.echo("Running test_T_INT_TAR_SAVU108_HAKO_MuokkausHakuAMKkohdeValintakokeet OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU112_HAKO_MuokkausHakuAMKkohdeUusiValintakoe() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU112_HAKO_MuokkausHakuAMKkohdeUusiValintakoe ...");
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
    	doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (uusi valintakoe) footer ei toimi.", true);
    	doit.echo("Running test_T_INT_TAR_SAVU112_HAKO_MuokkausHakuAMKkohdeUusiValintakoe OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU109_HAKO_DialogHakukohteenAMKPoisto() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU109_HAKO_DialogHakukohteenAMKPoisto ...");
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
			Assert.assertNotNull("Running HAKUKOHTEEN POISTO ei toimi."
					, doit.textElement(driver, "Hakukohdetta ei voi poistaa koska haku on jo"));
		}
    	doit.echo("Running test_T_INT_TAR_SAVU109_HAKO_DialogHakukohteenAMKPoisto OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU110_HAKO_DialogKoulutuksenPoistoAMKHakukohteesta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU110_HAKO_KoulutuksenPoistoAMKHakukohteesta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "tusohj");
    	doit.tarkasteleJokuHakukohde(driver);
    	doit.textClick(driver, "Poista hakukohteesta");
		Assert.assertNotNull("Running Koulutuksen poisto hakukohteesta ei toimi."
				, doit.textElement(driver, "Haluatko poistaa koulutuksen hakukohteelta"));
    	doit.echo("Running test_T_INT_TAR_SAVU110_HAKO_KoulutuksenPoistoAMKHakukohteesta OK");
    	doit.tauko(1);
    }


    @Test
    public void test_T_INT_TAR_SAVU101_HAKO_DialogHakukohteenAMKPeruutus() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU101_HAKO_DialogHakukohteenAMKPeruutus ...");
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
    	doit.echo("Running test_T_INT_TAR_SAVU101_HAKO_DialogHakukohteenAMKPeruutus OK");
    	doit.tauko(1);
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
    }

    @Test
    public void test_T_INT_TAR_SAVU141_HAKO_MuokkaaHakuLukiokohdeLisaaUusiLiite() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU141_HAKO_MuokkaaHakuLukiokohdeLisaaUusiLiite ...");
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
    	doit.echo("Running test_T_INT_TAR_SAVU141_HAKO_MuokkaaHakuLukiokohdeLisaaUusiLiite OK");
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
    			, doit.textElement(driver, "sykoe"));
		Assert.assertNotNull("Running HAKUKOHTEEN MUOKKAUS (Valintakokeiden tiedot) ei toimi."
				, doit.textElement(driver, "sanallinen kuvaus"));    	
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
    		Assert.assertNotNull("Running HAKUKOHTEEN POISTO ei toimi."
    				, doit.textElement(driver, "Hakukohdetta ei voi poistaa koska haku on jo"));
    	}
    	doit.echo("Running test_T_INT_TAR_SAVU139_HAKO_DialogHakukohteenLukioPoisto OK");
    	doit.tauko(1);
    }

    @Test
    public void test_T_INT_TAR_SAVU140_HAKO_DialogKoulutuksenPoistoLukioHakukohteesta() throws Exception {
    	doit.echo("Running test_T_INT_TAR_SAVU140_HAKO_DialogKoulutuksenPoistoLukioHakukohteesta ...");
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.haeKoulutuksia(driver, null, "Lukion");
    	doit.tarkasteleJokuHakukohde(driver);
    	doit.textClick(driver, "Poista hakukohteesta");
		Assert.assertNotNull("Running Koulutuksen poisto hakukohteesta ei toimi."
				, doit.textElement(driver, "Haluatko poistaa koulutuksen hakukohteelta"));
    	doit.echo("Running test_T_INT_TAR_SAVU140_HAKO_DialogKoulutuksenPoistoLukioHakukohteesta OK");
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
        doit.messagesPropertiesInit();
        driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.oph-login-url"));
        doit.tauko(1);
		doit.reppuLogin(driver);
		doit.tauko(1);
		doit.echo("Running -------------------------------------------------------");
		long t01 = doit.millis();
		driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.haku-url"));
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
    }

	public void testValinnatLoop() throws Exception {
		SVTUtils doit = new SVTUtils();
        doit.messagesPropertiesInit();
        driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.oph-login-url"));
        doit.tauko(1);
		doit.reppuLogin(driver);
		doit.tauko(1);
		doit.echo("Running -------------------------------------------------------");
		long t01 = doit.millis();
		driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.valinta-url"));
		Assert.assertNotNull("Running TarjontaValintaSavu001 Etusivu ei toimi."
                , doit.textElement(driver, "Kuvausteksti"));
		t01 = doit.millisDiff(t01);
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
