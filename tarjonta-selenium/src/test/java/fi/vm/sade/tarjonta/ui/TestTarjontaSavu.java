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
    private static Kattavuus TarjontaSavuTekstit = new Kattavuus();
    private static Kattavuus TarjontaSavuSelaimet = new Kattavuus();
    private static String selain = "";

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

    	baseUrl = SVTUtils.prop.getProperty("tarjonta-selenium.oph-url"); // "http://localhost:8080/"
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    static Boolean first = true;
	@Test
    public void testKoulutus() throws Exception {
		SVTUtils doit = new SVTUtils();
    	try {
    		testKoulutusLoop();
    	} catch (Exception e) {
    		try {
    			doit.printMyStackTrace(e);
    			testKoulutusLoop();
    		} catch (Exception e2) {
    			doit.printMyStackTrace(e2);
    			testKoulutusLoop();
    		}
    	}
    }

	public void testKoulutusLoop() throws Exception {
		SVTUtils doit = new SVTUtils();
        doit.messagesPropertiesInit();
        TarjontaSavuTekstit.alustaKattavuusKohde("TarjontaSavuTekstit");
        doit.alustaSelaimet(TarjontaSavuSelaimet, "TarjontaSavuSelaimet");
        TarjontaSavuTekstit.KattavuusRaporttiHiljaa = true;
        Boolean qa = false;
        Boolean luokka = false;
        if (SVTUtils.prop.getProperty("tarjonta-selenium.luokka").equals("true"))
        {
                luokka = true;
        }
        if (SVTUtils.prop.getProperty("tarjonta-selenium.qa").equals("true"))
        {
                qa = true;
        }
        if (first)
        {
        	selain = doit.palvelimenVersio(driver, baseUrl);
        	driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.oph-login-url"));
        	doit.tauko(1);
        }
        first = false;
		doit.reppuLogin(driver);
		doit.tauko(1);
		doit.echo("Running -------------------------------------------------------");
		long t01 = doit.millis();
		driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.tarjonta-url"));
		Boolean skip = true;
		while (skip)
		{
			if (doit.isPresentText(driver, "Tarjontaan")) { skip = false; }
			if (doit.isPresentText(driver, "Valitse kaikki")) { skip = false; }
			doit.tauko(1);
		}
		if (doit.isPresentText(driver, "Tarjontaan"))
		{
			doit.textClick(driver, "Tarjontaan");
		}
		Assert.assertNotNull("Running TarjontaSavu001 Etusivu ei toimi."
                , doit.textElement(driver, "Valitse kaikki"));
		t01 = doit.millisDiff(t01);
		doit.footerTest(driver, "Running TarjontaSavu001 Etusivu footer ei toimi.", true);
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.echo("Running TarjontaSavu001 Etusivu OK");
		doit.tauko(1);

        // HAE
        WebElement haeKentta = driver.findElement(By.className("v-textfield-search-box"));
        haeKentta.clear();
        haeKentta.sendKeys("optima");
        doit.tauko(1);
        t01 = doit.millis();
        driver.findElement(By.xpath("//*[text()='Hae']")).click();
        Assert.assertNotNull("Running TarjontaSavu002 Hae Optima samkommun ei toimi."
        		, doit.textElement(driver, "Optima samkommun"));
        t01 = doit.millisDiff(t01);
        doit.echo("Running TarjontaSavu002 Hae Optima samkommun OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);

        // KOULUTUKSET JA HAKUKOHTEET
        WebElement espoo = driver.findElement(By.xpath("//span[contains(text(), 'Optima samkommun')]"));
        t01 = doit.millis();
        espoo.click();
        Assert.assertNotNull("Running TarjontaSavu003 Hae KOULUTUKSET JA HAKUKOHTEET ei toimi.", doit.textElement(driver, "Koulutukset ("));
        t01 = doit.millisDiff(t01);
        Assert.assertNotNull("Running TarjontaSavu003 Hae KOULUTUKSET JA HAKUKOHTEET ei toimi.", doit.textElement(driver, "Hakukohteet ("));
        doit.echo("Running TarjontaSavu003 Hae KOULUTUKSET JA HAKUKOHTEET OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);

        // LUO UUSI AMMATILLINENKOULUTUS (validialog)
        t01 = doit.millis();
        doit.textClick(driver, "Luo uusi koulutus");
        Assert.assertNotNull("Running TarjontaSavu004a Luo uusi ammatillinenkoulutus ei toimi."
        		, doit.textElement(driver, "Olet luomassa uutta koulutusta"));
        t01 = doit.millisDiff(t01);
        doit.echo("Running TarjontaSavu004a Luo uusi ammatillinenkoulutus OK");
//        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);
        
        // LUO UUSI AMMATILLINENKOULUTUS (validialog + jatka)
        doit.sendInputPlusX(driver, "Koulutus:", "Ammatillinen peruskoulutus", 200);
        doit.popupItemClick(driver, "Ammatillinen peruskoulutus");
        doit.tauko(1);
        doit.sendInputPlusX(driver, "Pohjakoulutus:", "Peruskoulu", 20);
        doit.popupItemClick(driver, "Peruskoulu");
        doit.tauko(1);
        driver.findElement(By.xpath("//span[@class = 'v-button-caption' and text() = 'Optima samkommun']")).click();
        doit.tauko(1);
        t01 = doit.millis();
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running TarjontaSavu004b Luo uusi ammatillinenkoulutus + jatka ei toimi."
        		, doit.textElement(driver, "posti"));
        t01 = doit.millisDiff(t01);
		doit.footerTest(driver, "Running TarjontaSavu004b Luo uusi ammatillinenkoulutus + jatka footer ei toimi.", true);
        doit.echo("Running TarjontaSavu004b Luo uusi ammatillinenkoulutus + jatka OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();

        // LUO UUSI LUKIOKOULUTUS (validialog)
        // hae kerttulin lukio
        WebElement haeKentta2 = driver.findElement(By.className("v-textfield-search-box"));
        haeKentta2.clear();
        haeKentta2.sendKeys("kerttulin");
        doit.tauko(1);
        t01 = doit.millis();
        driver.findElement(By.xpath("//*[text()='Hae']")).click();
        Assert.assertNotNull("Running TarjontaSavu002 Hae Kerttulin lukio ei toimi."
                        , doit.textElement(driver, "Kerttulin lukio"));
        t01 = doit.millisDiff(t01);
        doit.echo("Running TarjontaSavu002 Hae Kerttulin lukio OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);

        doit.textClick(driver, "Kerttulin lukio");
        doit.tauko(1);
        t01 = doit.millis();
        doit.textClick(driver, "Luo uusi koulutus");
        Assert.assertNotNull("Running TarjontaSavu005a Luo uusi lukiokoulutus ei toimi."
        		, doit.textElement(driver, "Olet luomassa uutta koulutusta"));
        t01 = doit.millisDiff(t01);
        doit.echo("Running TarjontaSavu005a Luo uusi lukiokoulutus OK");
//        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);
        
        // LUO UUSI LUKIOKOULUTUS (validialog + jatka)
        doit.sendInputPlusX(driver, "Koulutus:", "Lukiokoulutus", 200);
        doit.popupItemClick(driver, "Lukiokoulutus");
        doit.tauko(1);
        driver.findElement(By.xpath("//span[@class = 'v-button-caption' and text() = 'Kerttulin lukio']")).click();
        doit.tauko(1);
        t01 = doit.millis();
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running TarjontaSavu005b Luo uusi lukiokoulutus + jatka ei toimi."
        		, doit.textElement(driver, "posti"));
        t01 = doit.millisDiff(t01);
		doit.footerTest(driver, "Running TarjontaSavu005b Luo uusi lukiokoulutus + jatka footer ei toimi.", true);
        doit.echo("Running TarjontaSavu005b Luo uusi lukiokoulutus + jatka OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();

        TarkasteleJaMuokkaaLukioKoulutusta();
        TarkasteleJaMuokkaaAmmatillistaKoulutusta();

        // SIIRRA TAI KOPIOI KOULUTUS
        Assert.assertNotNull("Running TarjontaSavu012 SIIRRA TAI KOPIOI KOULUTUS ei toimi."
                , doit.textElement(driver, "Koulutukset ("));
        if (! doit.isPresentText(driver, "Koulutukset (0)"))
        {
        if (! doit.isPresentText(driver, "v-icon"))
        {
        	driver.findElement(By.className("v-treetable-treespacer")).click();
            doit.tauko(1);
        }
        Assert.assertNotNull("Running TarjontaSavu012 SIIRRA TAI KOPIOI KOULUTUS ei toimi."
                , driver.findElement(By.xpath("//img[@class='v-icon']")));
        String gwtId = doit.getGwtIdForFirstHakukohde(driver);
        driver.findElement(By.id(gwtId)).click();
        doit.tauko(1);
        t01 = doit.millis();
        doit.textClick(driver, "tai kopioi");
//        Assert.assertNotNull("Running TarjontaSavu012 SIIRRA TAI KOPIOI KOULUTUS ei toimi.", doit.textElement(driver
//           		, "koulutuksen toiseen organisaatioon tai kopioida koulutuksen uuden koulutuksen pohjaksi. Valitse toimenpide, jonka haluat"));
        while (! doit.isPresentText(driver, "koulutuksen toiseen organisaatioon tai kopioida koulutuksen uuden koulutuksen pohjaksi. Valitse toimenpide, jonka haluat")) 
        { doit.tauko(1); }
        t01 = doit.millisDiff(t01);
        doit.echo("Running TarjontaSavu012 SIIRRA TAI KOPIOI KOULUTUS OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);
        doit.textClick(driver, "Peruuta");
        doit.tauko(1);
        }
        else
        {
                doit.echo("Running Ei ollut lainkaan koulutuksia valmiina.");
        }
        doit.echo("Running TarjontaSavu END OK");
        // END
	}

	@Test
    public void testHakukohteet() throws Exception {
		SVTUtils doit = new SVTUtils();
    	try {
    		testHakukohteetLoop();
    	} catch (Exception e) {
    		try {
    			doit.printMyStackTrace(e);
    			testHakukohteetLoop();
    		} catch (Exception e2) {
    			doit.printMyStackTrace(e2);
    			testHakukohteetLoop();
    		}
    	}
    }

	public void testHakukohteetLoop() throws Exception {
		SVTUtils doit = new SVTUtils();
        doit.messagesPropertiesInit();
        driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.oph-login-url"));
		doit.tauko(1);
		doit.reppuLogin(driver);
		doit.tauko(1);
		Boolean luokka = false;
		if (SVTUtils.prop.getProperty("tarjonta-selenium.luokka").equals("true"))
		{
			luokka = true;
		}
		doit.echo("Running -------------------------------------------------------");
		long t01 = doit.millis();
		driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.tarjonta-url"));
		Boolean skip = true;
		while (skip)
		{
			if (doit.isPresentText(driver, "Tarjontaan")) { skip = false; }
			if (doit.isPresentText(driver, "Valitse kaikki")) { skip = false; }
			doit.tauko(1);
		}
		if (doit.isPresentText(driver, "Tarjontaan"))
		{
			doit.textClick(driver, "Tarjontaan");
		}
		Assert.assertNotNull("Running TarjontaHakukohteetSavu001 Etusivu ei toimi."
                , doit.textElement(driver, "Valitse kaikki"));
		t01 = doit.millisDiff(t01);
//		doit.footerTest(driver, "Running TarjontaHakukohteetSavu001 Etusivu footer ei toimi.", true);
		doit.echo("Running TarjontaHakukohteetSavu001 Etusivu OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
		doit.tauko(1);
		
        // HAE
        WebElement haeKentta = driver.findElement(By.className("v-textfield-search-box"));
        haeKentta.clear();
        haeKentta.sendKeys("espoon");
        doit.tauko(1);
        t01 = doit.millis();
        driver.findElement(By.xpath("//*[text()='Hae']")).click();
        Assert.assertNotNull("Running TarjontaHakukohteetSavu002 Hae espoo ei toimi.", doit.textElement(driver, "Espoon kaupunki"));
        t01 = doit.millisDiff(t01);
        doit.echo("Running TarjontaHakukohteetSavu002 Hae espoo OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);

        // KOULUTUKSET JA HAKUKOHTEET
        WebElement espoo = driver.findElement(By.xpath("//span[contains(text(), 'Espoon kaupunki')]"));
        t01 = doit.millis();
        espoo.click();
        Assert.assertNotNull("Running TarjontaHakukohteetSavu003 Hae KOULUTUKSET JA HAKUKOHTEET ei toimi.", doit.textElement(driver, "Koulutukset ("));
        t01 = doit.millisDiff(t01);
        Assert.assertNotNull("Running TarjontaHakukohteetSavu003 Hae KOULUTUKSET JA HAKUKOHTEET ei toimi.", doit.textElement(driver, "Hakukohteet ("));
        doit.echo("Running TarjontaHakukohteetSavu003 Hae KOULUTUKSET JA HAKUKOHTEET OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);

        // LUO UUSI HAKUKOHDE (validialog)
        Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
        		, doit.textElement(driver, "Koulutukset ("));
        if (! doit.isPresentText(driver, "Koulutukset (0)"))
        {
        	doit.notPresentText(driver, "Koulutukset (0)"
        			, "Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE Ei Koulutuksia. Ei voi testata.");
        t01 = doit.millis();
        driver.findElement(By.className("v-treetable-treespacer")).click();
        Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
        		, driver.findElement(By.xpath("//img[@class='v-icon']")));
        t01 = doit.millisDiff(t01);
        String gwtId = doit.getGwtIdForFirstHakukohde(driver);
        // too many dialog
        doit.notPresentText(driver, "window_close"
                , "Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE Close nakyy jo. Ei toimi.");
        WebElement checkBox3 = driver.findElement(By.id("gwt-uid-3"));
        WebElement checkBoxId = driver.findElement(By.id(gwtId));
        Boolean a_scenario = false; // Ammatillinen koulutus vain yksi loytyi 
        Boolean b1_scenario = false; // too many dialog
        Boolean b2_scenario = false; // too many dialog
        Boolean c_scenario = false; // Lukiokohde loytyi ja menee jo muokkaa sivulle
        String a_text = "Olet luomassa uutta hakukohdetta seuraavista koulutuksista";
        String b1_text = "Olet valinnut useita koulutuksia. Hakukohteeseen voi kuulua vain yksi a";
        String b2_text = "Olet valinnut useita koulutuksia. Hakukohteeseen voi kuulua vain yksi lukio";
        String c_text = "tietoja hakemisesta";
        if (! doit.isPresentText(driver, "Koulutukset (1)"))
        {
        t01 = doit.millis();
        checkBox3.click();
        while (! checkBoxId.isSelected()) { doit.tauko(1); }
        t01 = doit.millisDiff(t01);
        Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi.", checkBoxId.isSelected());
        doit.tauko(1);
        t01 = doit.millis();
        doit.textClick(driver, "Luo uusi hakukohde"); // <=================================== 1 / 2
        Boolean skip2 = true;
        while (skip2)
        {
        	if (doit.isPresentText(driver, a_text)) { a_scenario = true; skip2 = false; }
        	if (doit.isPresentText(driver, b1_text)) { b1_scenario = true; skip2 = false; }
        	if (doit.isPresentText(driver, b2_text)) { b2_scenario = true; skip2 = false; }
        	if (doit.isPresentText(driver, c_text)) { c_scenario = true; skip2 = false; }
        	doit.tauko(1);
        }
        t01 = doit.millisDiff(t01);
        if (a_scenario)
        {
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
        			, doit.textElement(driver, a_text));
        	doit.textClick(driver, "Peruuta");
        	doit.tauko(1);
        }
        if (b1_scenario)
        {
            Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
            		, doit.textElement(driver, b1_text));
        	doit.textClick(driver, "Sulje");
        	doit.tauko(1);
        }        	
        if (b2_scenario)
        {
            Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
            		, doit.textElement(driver, b2_text));
        	doit.textClick(driver, "Sulje");
        	doit.tauko(1);
        }        	
        if (c_scenario)
        {
            Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
            		, doit.textElement(driver, c_text));
            doit.tauko(1);
            driver.findElement(By.className("v-button-back")).click();
            Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE back ei toimi.", doit.textElement(driver, "Hakukohteet ("));
            doit.tauko(1);
            checkBox3 = doit.findNearestElementExact("Valitse kaikki", "//input[@type='checkbox']", driver);
            gwtId = doit.getGwtIdForFirstHakukohde(driver);
            checkBoxId = driver.findElement(By.id(gwtId));
        }        	
//        checkBox3 = driver.findElement(By.id("gwt-uid-3"));
    	t01 = doit.millis();
    	checkBox3.click();
    	while (checkBoxId.isSelected()) { doit.tauko(1); }
    	t01 = doit.millisDiff(t01);
    	Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi.", checkBoxId.isSelected());
    	doit.tauko(1);
        }
        // ok to continue
        checkBoxId.click();
        doit.tauko(1);
        t01 = doit.millis();
        doit.textClick(driver, "Luo uusi hakukohde"); // <=================================== 2 / 2
        if (a_scenario || b1_scenario)
        {
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
        			, doit.textElement(driver, a_text));
        	t01 = doit.millisDiff(t01);
        	t01 = doit.millis();
        	doit.textClick(driver, "Jatka");
        }
        if (c_scenario || b2_scenario)
        {
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
        			, doit.textElement(driver, c_text));
        	t01 = doit.millisDiff(t01);
//        	t01 = doit.millis();
//        	doit.textClick(driver, "Jatka");
        }
        Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
        		, doit.textElement(driver, "tietoja hakemisesta"));
        t01 = doit.millisDiff(t01);
        Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
        		, doit.textElement(driver, "Tallenna luonnoksena"));
        doit.echo("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        
        // HAKUKOHTEEN TARKASTELU
        Assert.assertNotNull("Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU ei toimi."
        		, doit.textElement(driver, "Hakukohteet ("));
        doit.tauko(1);
        if (! doit.isPresentText(driver, "Hakukohteet (0)"))
        {
        	doit.notPresentText(driver, "Hakukohteet (0)"
        			, "Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU Ei hakukohteita. Ei voi testata.");
        	t01 = doit.millis();
        	doit.textClick(driver, "Hakukohteet (");
        	doit.notPresentText(driver, gwtId
        			, "Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU " + gwtId + " nakyy viela. Ei toimi.");
        	t01 = doit.millisDiff(t01);
        	doit.tauko(1);
        	WebElement lastTriangle = doit.getTriangleForLastHakukohde(driver);
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU ei toimi.", lastTriangle);
        	lastTriangle.click();
        	doit.tauko(1);
        	driver.findElement(By.xpath("(//img[@class='v-icon'])[last()]")).click();
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU ei toimi."
        			, doit.textElement(driver, "Tarkastele"));
        	doit.tauko(1);
        	t01 = doit.millis();
        	doit.textClick(driver, "Tarkastele");
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU ei toimi."
        			, doit.textElement(driver, "Liitä uusi koulutus"));
        	t01 = doit.millisDiff(t01);
        	doit.echo("Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU OK");
            doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        	doit.tauko(1);

        	// HAKUKOHTEEN MUOKKAUS
        	if (! doit.isPresentText(driver, "julkaistu"))
        	{
        	t01 = doit.millis();
        	doit.textClick(driver, "muokkaa");
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu006 HAKUKOHTEEN MUOKKAUS ei toimi."
        			, doit.textElement(driver, "voidaan kuvata muuta hakemiseen olennaisesti"));
        	t01 = doit.millisDiff(t01);
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu006 HAKUKOHTEEN MUOKKAUS ei toimi."
        			, doit.textElement(driver, "Tallenna valmiina"));
        	doit.echo("Running TarjontaHakukohteetSavu006 HAKUKOHTEEN MUOKKAUS OK");
            doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        	doit.tauko(1);

        	// HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot)
        	t01 = doit.millis();
        	doit.textClick(driver, "Liitteiden tiedot");
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu007 HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) ei toimi."
        			, doit.textElement(driver, "Toimitusosoite"));
        	t01 = doit.millisDiff(t01);
        	doit.echo("Running TarjontaHakukohteetSavu007 HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) OK");
            doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        	doit.tauko(1);

        	// HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite)
    		// tullaan toista kautta samaan jotta aikaisemmat sivut pyyhkiytyvat hidden osiosta
            driver.findElement(By.className("v-button-back")).click();
            Assert.assertNotNull("Running TarjontaHakukohteetSavu007b HAKUKOHTEEN TARKASTELU ei toimi."
            		, doit.textElement(driver, "Hakukohteet ("));
            doit.tauko(1);
            lastTriangle = doit.getTriangleForLastHakukohde(driver);
            Assert.assertNotNull("Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU ei toimi.", lastTriangle);
            lastTriangle.click();
            doit.tauko(1);
            driver.findElement(By.xpath("(//img[@class='v-icon'])[last()]")).click();
            Assert.assertNotNull("Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU ei toimi."
                            , doit.textElement(driver, "Tarkastele"));
            doit.tauko(1);
            t01 = doit.millis();
            doit.textClick(driver, "Tarkastele");

            driver.findElement(By.xpath("(//*[text()='muokkaa'])[3]")).click(); // click Muokkaa(3)
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu007b HAKUKOHTEEN MUOKKAUS ei toimi."
        			, doit.textElement(driver, "Toimitusosoite"));
        	//
        	t01 = doit.millis();
        	doit.textClick(driver, "uusi liite");
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu007b HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite) ei toimi."
        			, doit.textElement(driver, "Voidaan toimittaa my"));
        	t01 = doit.millisDiff(t01);
        	doit.echo("Running TarjontaHakukohteetSavu007b HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite) OK");
            doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        	doit.tauko(1);
        	doit.textClick(driver, "Peruuta");
        	doit.tauko(1);

        	// HAKUKOHTEEN MUOKKAUS (valintakokeet)
        	t01 = doit.millis();
        	doit.textClick(driver, "Valintakokeiden tiedot");
        	// kahtalajia lomakkeita
        	skip = true;
        	Boolean paasykoe = false;
        	Boolean uusiValintakoe = false;
        	while (skip)
        	{
        		if (doit.isPresentText(driver, "sykoe")) { skip = false; paasykoe = true; }
        		if (doit.isPresentText(driver, "uusi valintakoe")) { skip = false; uusiValintakoe = true; }
        		doit.tauko(1);
        	}
        	if (paasykoe)
        	{
        		String paasykoeCheckBoxId = doit.getGwtIdBeforeText(driver, "sykoe</label>");
        		WebElement paasykoeCheckBox = driver.findElement(By.id(paasykoeCheckBoxId));
        		if (paasykoeCheckBox.getAttribute("checked") == null || ! paasykoeCheckBox.getAttribute("checked").equals("true"))
        		{
        			paasykoeCheckBox.click();
        		}
        		Assert.assertNotNull("Running TarjontaHakukohteetSavu008 HAKUKOHTEEN MUOKKAUS (valintakokeet) ei toimi."
        				, doit.textElement(driver, "Ajankohta"));
        	}
        	if (uusiValintakoe)
        	{
        		Assert.assertNotNull("Running TarjontaHakukohteetSavu008 HAKUKOHTEEN MUOKKAUS (valintakokeet) ei toimi."
        				, doit.textElement(driver, "Valintakokeen kuvaus"));
        	}
        	t01 = doit.millisDiff(t01);
        	doit.echo("Running TarjontaHakukohteetSavu008 HAKUKOHTEEN MUOKKAUS (valintakokeet) OK");
            doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        	doit.tauko(1);

        	// UUSI VALINTAKOE
        	if (uusiValintakoe)
        	{
        		t01 = doit.millis();
        		doit.textClick(driver, "uusi valintakoe");
        		Assert.assertNotNull("Running TarjontaHakukohteetSavu008b HAKUKOHTEEN MUOKKAUS (uusi valintakoe) ei toimi."
        				, doit.textElement(driver, "Ajankohta"));
        		t01 = doit.millisDiff(t01);
        		doit.echo("Running TarjontaHakukohteetSavu008b HAKUKOHTEEN MUOKKAUS (uusi valintakoe) OK");
                doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        		doit.tauko(1);
        		doit.textClick(driver, "Peruuta");
        		doit.tauko(1);
        	}
        	doit.textClick(driver, "Hakukohteen perustiedot");
        	doit.tauko(1);
        	}

        	// HAKUKOHTEEN POISTO
        	driver.findElement(By.className("v-button-back")).click();
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu009 HAKUKOHTEEN POISTO ei toimi."
        			, doit.textElement(driver, "Hakukohteet ("));
        	doit.getTriangleForLastHakukohde(driver).click();
        	doit.tauko(1);
        	driver.findElement(By.xpath("(//img[@class='v-icon'])[last()]")).click();
        	doit.tauko(1);
        	doit.notPresentText(driver, "window_close"
        			, "Running TarjontaHakukohteetSavu009 HAKUKOHTEEN POISTO Close nakyy jo. Ei toimi.");
        	t01 = doit.millis();
        	Boolean poista = false;
        	Boolean peruutaHakukohde = false;
        	skip = true;
        	while (skip)
        	{
        		if (doit.isPresentText(driver, ">Poista<")) { skip = false; poista = true; }
        		if (doit.isPresentText(driver, ">Peruuta hakukohde<")) { skip = false; peruutaHakukohde = true; }
        		doit.tauko(1);
        	}
        	if (poista)
        	{
        		driver.findElement(By.xpath("//span[@class='v-menubar-menuitem-caption' and text()='Poista']")).click();
        		Assert.assertNotNull("Running TarjontaHakukohteetSavu009 HAKUKOHTEEN POISTO ei toimi."
        				, doit.textElement(driver, "Haluatko varmasti poistaa seuraavan hakukohteen"));
        	}
        	if (peruutaHakukohde)
        	{
        		driver.findElement(By.xpath("//span[@class='v-menubar-menuitem-caption' and text()='Peruuta hakukohde']")).click();
        		Assert.assertNotNull("Running TarjontaHakukohteetSavu009 HAKUKOHTEEN POISTO ei toimi."
        				, doit.textElement(driver, "Olet peruuttamassa hakukohdetta"));
        	}
        	t01 = doit.millisDiff(t01);
        	String closeId2 = doit.idLike(driver, "window_close");
        	WebElement close = driver.findElement(By.id(closeId2));
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu009 HAKUKOHTEEN POISTO ei toimi.", close);
        	t01 = doit.millisDiff(t01);
        	doit.tauko(1);
        	close.click();
        	doit.tauko(1);
        	doit.notPresentText(driver, "window_close"
        			, "Running TarjontaHakukohteetSavu009 HAKUKOHTEEN POISTO Close nakyy viela. Ei toimi.");
        	doit.echo("Running TarjontaHakukohteetSavu009 HAKUKOHTEEN POISTO OK");
            doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        }
        else
        {
        	doit.echo("Running TarjontaHakukohteetSavu HAKUKOHTEIDEN TESTAUS SIVUUTETTIIN");
        }
        }
        else
        {
        	doit.echo("Running TarjontaHakukohteetSavu HAKUKOHTEIDEN TESTAUS SIVUUTETTIIN");
        }
        
        doit.tauko(1);
        doit.echo("Running TarjontaHakukohteetSavu END OK");
	}
	
	private void TarkasteleJaMuokkaaLukioKoulutusta() throws Exception
	{
        // TARKASTELE LUKIOKOULUTUS
		SVTUtils doit = new SVTUtils();
        Assert.assertNotNull("Running TarjontaSavu006 TARKASTELE LUKIOKOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Koulutukset ("));
        doit.textClick(driver, "[Poista valinta]");
        Assert.assertNotNull("Running TarjontaSavu006 TARKASTELE LUKIOKOULUTUSTA ei toimi."
        		, doit.textElement(driver, "OPH"));
        doit.tauko(1);
        WebElement menu = doit.TarkasteleKoulutusLuonnosta(driver, "ylioppilastutkint*");
        if (! doit.isPresentText(driver, "Koulutukset (0)") && menu != null)
        {
        	doit.notPresentText(driver, "Koulutukset (0)"
        			, "Running TarjontaSavu006 TARKASTELE KOULUTUSTA Ei koulutuksia. Ei voi testata.");
//        t01 = doit.millisDiff(t01);
//        driver.findElement(By.className("v-treetable-treespacer")).click();
//        Assert.assertNotNull("Running TarjontaSavu006 TARKASTELE KOULUTUSTA ei toimi."
//        		, driver.findElement(By.xpath("//img[@class='v-icon']")));
//        t01 = doit.millis();
//            driver.findElement(By.xpath("//img[@class='v-icon']")).click();
        menu.click();
        doit.tauko(1);
        long t01 = doit.millis();
        doit.textClick(driver, "Tarkastele");
        Assert.assertNotNull("Running TarjontaSavu006 TARKASTELE LUKIOKOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Lukiokoulutus"));
        Assert.assertNotNull("Running TarjontaSavu006 TARKASTELE LUKIOKOULUTUSTA ei toimi."
        		, doit.textElement(driver, "muiden toimijoiden kanssa")); // lukiokoulutus
        t01 = doit.millis();
		doit.footerTest(driver, "Running TarjontaSavu006 TARKASTELE LUKIOKOULUTUSTA footer ei toimi.", true);
        doit.echo("Running TarjontaSavu006 TARKASTELE LUKIOKOULUTUSTA OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);
        
        // poista hakukohde
        String closeId = "";
        WebElement close = null;
        if (doit.isPresentText(driver, "Poista koulutuksesta"))
        {
        	doit.notPresentText(driver, "window_close"
        			, "Running TarjontaSavu007a POISTA KOULUTUS Close nakyy jo. Ei toimi.");
        	t01 = doit.millisDiff(t01);
        	doit.textClick(driver, "Poista koulutuksesta");
        	Assert.assertNotNull("Running TarjontaSavu007a POISTA KOULUTUS ei toimi."
        			, doit.textElement(driver, "Haluatko poistaa hakukohteen koulutukselta"));
        	t01 = doit.millis();
        	closeId = doit.idLike(driver, "window_close");
        	close = driver.findElement(By.id(closeId));
        	Assert.assertNotNull("Running TarjontaSavu007a POISTA KOULUTUS ei toimi.", close);
        	t01 = doit.millisDiff(t01);
        	doit.tauko(1);
        	close.click();
        	doit.tauko(1);
        	doit.notPresentText(driver, "window_close"
        			, "Running TarjontaSavu007a POISTA KOULUTUS Close nakyy viela. Ei toimi.");
        	doit.echo("Running TarjontaSavu007a POISTA KOULUTUKSELTA HAKUKOHDE OK");
            doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        }
        
        // POISTA KOULUTUS
        if (doit.isPresentText(driver, "Poista") 
        		&& ! doit.isPresentText(driver, "Poista koulutuksesta"))
        {
        	doit.notPresentText(driver, "window_close"
        			, "Running TarjontaSavu007b POISTA KOULUTUS Close nakyy jo. Ei toimi.");
        	t01 = doit.millisDiff(t01);
        	doit.textClick(driver, "Poista");
        	Assert.assertNotNull("Running TarjontaSavu007b POISTA KOULUTUS ei toimi."
        			, doit.textElement(driver, "Haluatko varmasti poistaa"));
        	t01 = doit.millis();
        	closeId = doit.idLike(driver, "window_close");
        	close = driver.findElement(By.id(closeId));
        	Assert.assertNotNull("Running TarjontaSavu007b POISTA KOULUTUS ei toimi.", close);
        	t01 = doit.millisDiff(t01);
        	doit.tauko(1);
        	close.click();
        	doit.tauko(1);
        	doit.notPresentText(driver, "window_close"
        			, "Running TarjontaSavu007b POISTA KOULUTUS Close nakyy viela. Ei toimi.");
        	doit.echo("Running TarjontaSavu007b POISTA KOULUTUS OK");
            doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        	doit.tauko(1);
        
        	// KOPIOI UUDEKSI
//        	t01 = doit.millis();
//        	doit.textClick(driver, "Kopioi uudeksi");
//        	Assert.assertNotNull("Running TarjontaSavu008 KOPIOI UUDEKSI ei toimi."
//        			, doit.textElement(driver, "koulutuksen toiseen organisaatioon"));
//        	t01 = doit.millisDiff(t01);
//        	closeId = doit.idLike(driver, "window_close");
//        	close = driver.findElement(By.id(closeId));
//        	Assert.assertNotNull("Running TarjontaSavu008 KOPIOI UUDEKSI ei toimi.", close);
//        	doit.tauko(1);
//        	close.click();
//        	doit.tauko(1);
//        	doit.notPresentText(driver, "window_close"
//        			, "Running TarjontaSavu008 KOPIOI UUDEKSI Close nakyy viela. Ei toimi.");
//        	doit.echo("Running TarjontaSavu008 KOPIOI UUDEKSI OK");
//        	doit.tauko(1);
        
        }

    	// Lisaa rinnakkainen toteutus
        if (doit.isPresentText(driver, "rinnakkainen toteutus"))
    	{
    		t01 = doit.millis();
    		doit.textClick(driver, "rinnakkainen toteutus");
    		Assert.assertNotNull("Running TarjontaSavu009 Lisaa rinnakkainen toteutus ei toimi."
    				, doit.textElement(driver, "Valitse pohjakoulutus"));
    		t01 = doit.millisDiff(t01);
    		doit.echo("Running TarjontaSavu009 Lisaa rinnakkainen toteutus OK");
            doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
    		doit.tauko(1);
    		t01 = doit.millis();
    		doit.textClick(driver, "Peruuta");
    		t01 = doit.millisDiff(t01);
    	}
//        doit.tauko(1);
//    	t01 = doit.millis();
//        driver.findElement(By.className("v-button-back")).click();
//        t01 = doit.millisDiff(t01);
//        doit.tauko(1);
//        driver.findElement(By.className("v-treetable-treespacer")).click();


        // MUOKKAA KOULUTUSTA
//        Assert.assertNotNull("Running TarjontaSavu010 MUOKKAA KOULUTUSTA ei toimi."
//        		, doit.textElement(driver, "Koulutukset ("));
//        doit.tauko(1);
//        Assert.assertNotNull("Running TarjontaSavu010 MUOKKAA KOULUTUSTA ei toimi."
//        		, driver.findElement(By.xpath("//img[@class='v-icon']")));
//        driver.findElement(By.xpath("//img[@class='v-icon']")).click();
//        doit.tauko(1);
        t01 = doit.millis();
        doit.textClick(driver, "muokkaa");
        Assert.assertNotNull("Running TarjontaSavu010 MUOKKAA KOULUTUSTA ei toimi."
        		, doit.textElement(driver, "posti"));
        t01 = doit.millisDiff(t01);
        Assert.assertNotNull("Running TarjontaSavu010 MUOKKAA KOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Tallenna valmiina"));
		doit.footerTest(driver, "Running TarjontaSavu010 MUOKKAA KOULUTUSTA footer ei toimi.", true);
        doit.echo("Running TarjontaSavu010 MUOKKAA KOULUTUSTA OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);
        
        // MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot
        t01 = doit.millis();
        doit.textClick(driver, "Koulutuksen kuvailevat tiedot");
        Assert.assertNotNull("Running TarjontaSavu011 MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot ei toimi."
        		, doit.textElement(driver, "muiden toimijoiden kanssa")); // lukiokoulutus
        t01 = doit.millisDiff(t01);
		doit.footerTest(driver, "Running TarjontaSavu011 MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot footer ei toimi.", true);
        doit.echo("Running TarjontaSavu011 MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);
        doit.textClick(driver, "Koulutuksen perustiedot");
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        doit.tauko(1);
        }
        else
        {
        	doit.echo("Running Ei ollut lainkaan lukiokoulutuksia luonnostilassa valmiina.");
        }
	}

	private void TarkasteleJaMuokkaaAmmatillistaKoulutusta() throws Exception
	{
        // TARKASTELE LUKIOKOULUTUS
		SVTUtils doit = new SVTUtils();
        Assert.assertNotNull("Running TarjontaSavu006 TARKASTELE AMMATILLISTAKOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Koulutukset ("));
        doit.textClick(driver, "[Poista valinta]");
        WebElement menu = doit.TarkasteleKoulutusLuonnosta(driver, "*tusohjel*");
        if (! doit.isPresentText(driver, "Koulutukset (0)") && menu != null)
        {
        	doit.notPresentText(driver, "Koulutukset (0)"
        			, "Running TarjontaSavu006 TARKASTELE AMMATILLISTAKOULUTUSTA Ei koulutuksia. Ei voi testata.");
//        t01 = doit.millisDiff(t01);
//        driver.findElement(By.className("v-treetable-treespacer")).click();
//        Assert.assertNotNull("Running TarjontaSavu006 TARKASTELE KOULUTUSTA ei toimi."
//        		, driver.findElement(By.xpath("//img[@class='v-icon']")));
//        t01 = doit.millis();
//            driver.findElement(By.xpath("//img[@class='v-icon']")).click();
        menu.click();
        doit.tauko(1);
        long t01 = doit.millis();
        doit.textClick(driver, "Tarkastele");
        Assert.assertNotNull("Running TarjontaSavu006 TARKASTELE AMMATILLISTAKOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Ammatillinen koulutus"));
        Assert.assertNotNull("Running TarjontaSavu006 TARKASTELE AMMATILLISTAKOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Ammattinimikkeet")); // Ammatillinen koulutus
        t01 = doit.millis();
		doit.footerTest(driver, "Running TarjontaSavu006 TARKASTELE AMMATILLISTAKOULUTUSTA footer ei toimi.", true);
        doit.echo("Running TarjontaSavu006 TARKASTELE AMMATILLISTAKOULUTUSTA OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);
        
        // poista hakukohde
        String closeId = "";
        WebElement close = null;
        if (doit.isPresentText(driver, "Poista koulutuksesta"))
        {
        	doit.notPresentText(driver, "window_close"
        			, "Running TarjontaSavu007 POISTA KOULUTUS Close nakyy jo. Ei toimi.");
        	t01 = doit.millisDiff(t01);
        	doit.textClick(driver, "Poista koulutuksesta");
        	Assert.assertNotNull("Running TarjontaSavu007 POISTA KOULUTUS ei toimi."
        			, doit.textElement(driver, "Haluatko poistaa hakukohteen koulutukselta"));
        	t01 = doit.millis();
        	closeId = doit.idLike(driver, "window_close");
        	close = driver.findElement(By.id(closeId));
        	Assert.assertNotNull("Running TarjontaSavu007 POISTA KOULUTUS ei toimi.", close);
        	t01 = doit.millisDiff(t01);
        	doit.tauko(1);
        	close.click();
        	doit.tauko(1);
        	doit.notPresentText(driver, "window_close"
        			, "Running TarjontaSavu007 POISTA KOULUTUS Close nakyy viela. Ei toimi.");
        	doit.echo("Running TarjontaSavu007 POISTA KOULUTUKSELTA HAKUKOHDE OK");
            doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        }
        
        // POISTA KOULUTUS
        if (doit.isPresentText(driver, "Poista") 
        		&& ! doit.isPresentText(driver, "Poista koulutuksesta"))
        {
        	doit.notPresentText(driver, "window_close"
        			, "Running TarjontaSavu007 POISTA KOULUTUS Close nakyy jo. Ei toimi.");
        	t01 = doit.millisDiff(t01);
        	doit.textClick(driver, "Poista");
        	Assert.assertNotNull("Running TarjontaSavu007 POISTA KOULUTUS ei toimi."
        			, doit.textElement(driver, "Haluatko varmasti poistaa"));
        	t01 = doit.millis();
        	closeId = doit.idLike(driver, "window_close");
        	close = driver.findElement(By.id(closeId));
        	Assert.assertNotNull("Running TarjontaSavu007 POISTA KOULUTUS ei toimi.", close);
        	t01 = doit.millisDiff(t01);
        	doit.tauko(1);
        	close.click();
        	doit.tauko(1);
        	doit.notPresentText(driver, "window_close"
        			, "Running TarjontaSavu007 POISTA KOULUTUS Close nakyy viela. Ei toimi.");
        	doit.echo("Running TarjontaSavu007 POISTA KOULUTUS OK");
            doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        	doit.tauko(1);
        
        	// KOPIOI UUDEKSI
//        	t01 = doit.millis();
//        	doit.textClick(driver, "Kopioi uudeksi");
//        	Assert.assertNotNull("Running TarjontaSavu008 KOPIOI UUDEKSI ei toimi."
//        			, doit.textElement(driver, "koulutuksen toiseen organisaatioon"));
//        	t01 = doit.millisDiff(t01);
//        	closeId = doit.idLike(driver, "window_close");
//        	close = driver.findElement(By.id(closeId));
//        	Assert.assertNotNull("Running TarjontaSavu008 KOPIOI UUDEKSI ei toimi.", close);
//        	doit.tauko(1);
//        	close.click();
//        	doit.tauko(1);
//        	doit.notPresentText(driver, "window_close"
//        			, "Running TarjontaSavu008 KOPIOI UUDEKSI Close nakyy viela. Ei toimi.");
//        	doit.echo("Running TarjontaSavu008 KOPIOI UUDEKSI OK");
//        	doit.tauko(1);
        
        }

    	// Lisaa rinnakkainen toteutus
        if (doit.isPresentText(driver, "rinnakkainen toteutus"))
    	{
    		t01 = doit.millis();
    		doit.textClick(driver, "rinnakkainen toteutus");
    		Assert.assertNotNull("Running TarjontaSavu009 Lisaa rinnakkainen toteutus ei toimi."
    				, doit.textElement(driver, "Valitse pohjakoulutus"));
    		t01 = doit.millisDiff(t01);
    		doit.echo("Running TarjontaSavu009 Lisaa rinnakkainen toteutus OK");
            doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
    		doit.tauko(1);
    		t01 = doit.millis();
    		doit.textClick(driver, "Peruuta");
    		t01 = doit.millisDiff(t01);
    		doit.tauko(1);
    	}
//        doit.tauko(1);
//    	t01 = doit.millis();
//        driver.findElement(By.className("v-button-back")).click();
//        t01 = doit.millisDiff(t01);
//        doit.tauko(1);
//        driver.findElement(By.className("v-treetable-treespacer")).click();
//
//
//        // MUOKKAA KOULUTUSTA
//        Assert.assertNotNull("Running TarjontaSavu010 MUOKKAA KOULUTUSTA ei toimi.", doit.textElement(driver, "Koulutukset ("));
//        doit.tauko(1);
//        Assert.assertNotNull("Running TarjontaSavu010 MUOKKAA KOULUTUSTA ei toimi."
//        		, driver.findElement(By.xpath("//img[@class='v-icon']")));
//        driver.findElement(By.xpath("//img[@class='v-icon']")).click();
//        doit.tauko(1);
        driver.navigate().refresh();
        doit.tauko(1);
        t01 = doit.millis();
        doit.textClick(driver, "muokkaa");
        Assert.assertNotNull("Running TarjontaSavu010 MUOKKAA KOULUTUSTA ei toimi."
        		, doit.textElement(driver, "posti"));
        t01 = doit.millisDiff(t01);
        Assert.assertNotNull("Running TarjontaSavu010 MUOKKAA KOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Tallenna valmiina"));
		doit.footerTest(driver, "Running TarjontaSavu010 MUOKKAA KOULUTUSTA footer ei toimi.", true);
        doit.echo("Running TarjontaSavu010 MUOKKAA KOULUTUSTA OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);
        
        // MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot
        t01 = doit.millis();
        doit.textClick(driver, "Koulutuksen kuvailevat tiedot");
        Assert.assertNotNull("Running TarjontaSavu011 MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot ei toimi."
        		, doit.textElement(driver, "Koulutusohjelman valinta")); // Ammatillinen koulutus
//        	Assert.assertNotNull("Running TarjontaSavu011 MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot ei toimi."
//        			, doit.textElement(driver, "listyminen")); // toinen aste (kansainvalistyminen)
        t01 = doit.millisDiff(t01);
		doit.footerTest(driver, "Running TarjontaSavu011 MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot footer ei toimi.", true);
        doit.echo("Running TarjontaSavu011 MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
        doit.tauko(1);
        doit.textClick(driver, "Koulutuksen perustiedot");
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        doit.tauko(1);
        }
        else
        {
        	doit.echo("Running Ei ollut lainkaan ammatillisia koulutuksia luonnostilassa valmiina.");
        }
	}

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
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
		doit.tauko(1);
		
		// LUO UUSI HAKU
		t01 = doit.millis();
		doit.textClick(driver, "Luo uusi haku");
		Assert.assertNotNull("Running TarjontaHakuSavu002 Luo uusi haku ei toimi."
                , doit.textElement(driver, "hakulomaketta"));
		t01 = doit.millisDiff(t01);
		doit.footerTest(driver, "Running TarjontaHakuSavu002 Luo uusi haku footer ei toimi.", true);
		doit.echo("Running TarjontaHakuSavu002 Luo uusi haku OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
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
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
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
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
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
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
		doit.tauko(1);
		
		// SORA-VAATIMUKSET
		doit.textClick(driver, "SORA-vaatimukset");
		doit.tauko(10);
		Assert.assertNotNull("Running TarjontaValintaSavu002 Sora-vaatimukset ei toimi."
                , doit.textElement(driver, "Kuvausteksti"));
		doit.footerTest(driver, "Running TarjontaValintaSavu002 Sora-vaatimukset footer ei toimi.", true);
		doit.echo("Running TarjontaValintaSavu002 Sora-vaatimukset OK");
        doit.messagesPropertiesCoverage(driver, TarjontaSavuTekstit);
		doit.tauko(1);
		
		// END
        TarjontaSavuTekstit.alustaKattavuusKohde("TarjontaSavuTekstit");
        doit.messagesPropertiesSave(TarjontaSavuTekstit);
        if (selain != null && selain.length() > 0)
        {
        	TarjontaSavuSelaimet.setKattavuus(selain, Kattavuus.KATTAVUUSOK);
        	TarjontaSavuSelaimet.KattavuusRaportti();
        }
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
