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

public class TestTarjontaSavu {

    private WebDriver driver;
    private String baseUrl;
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
            if (true)
            {
            	FirefoxProfile firefoxProfile = new FirefoxProfile();
            	firefoxProfile.setPreference( "intl.accept_languages", "fi-fi,fi" ); 
                driver = new FirefoxDriver(firefoxProfile);
            }
            else
            {
                    System.setProperty("webdriver.ie.driver", "src/test/resources/IEDriverServer.exe");
                    driver = new InternetExplorerDriver();
            }

            baseUrl = SVTUtils.prop.getProperty("tarjonta-selenium.oph-url"); // "http://localhost:8080/"
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

	@Test
	public void test() throws Exception {
		SVTUtils doit = new SVTUtils();
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

        if (! qa)
        {
        	doit.palvelimenVersio(driver, baseUrl);
        }
		driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.oph-login-url"));
		doit.tauko(1);
		doit.reppuLogin(driver);
		doit.tauko(1);
		System.out.println("Running -------------------------------------------------------");
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
		System.out.println("Running TarjontaSavu001 Etusivu OK");
		doit.tauko(1);

        // HAE
        WebElement haeKentta = driver.findElement(By.className("v-textfield-search-box"));
        haeKentta.clear();
        haeKentta.sendKeys("espoon");
        doit.tauko(1);
        t01 = doit.millis();
        driver.findElement(By.xpath("//*[text()='Hae']")).click();
        Assert.assertNotNull("Running TarjontaSavu002 Hae espoo ei toimi.", doit.textElement(driver, "Espoon kaupunki"));
        t01 = doit.millisDiff(t01);
        System.out.println("Running TarjontaSavu002 Hae espoo OK");
        doit.tauko(1);

        // KOULUTUKSET JA HAKUKOHTEET
        WebElement espoo = driver.findElement(By.xpath("//span[contains(text(), 'Espoon kaupunki')]"));
        t01 = doit.millis();
        espoo.click();
        Assert.assertNotNull("Running TarjontaSavu003 Hae KOULUTUKSET JA HAKUKOHTEET ei toimi.", doit.textElement(driver, "Koulutukset ("));
        t01 = doit.millisDiff(t01);
        Assert.assertNotNull("Running TarjontaSavu003 Hae KOULUTUKSET JA HAKUKOHTEET ei toimi.", doit.textElement(driver, "Hakukohteet ("));
        System.out.println("Running TarjontaSavu003 Hae KOULUTUKSET JA HAKUKOHTEET OK");
        doit.tauko(1);

        // LUO UUSI KOULUTUS (validialog)
        t01 = doit.millis();
        doit.textClick(driver, "Luo uusi koulutus");
        Assert.assertNotNull("Running TarjontaSavu004 Luo uusi koulutus ei toimi."
        		, doit.textElement(driver, "Olet luomassa uutta koulutusta"));
        t01 = doit.millisDiff(t01);
        System.out.println("Running TarjontaSavu004 Luo uusi koulutus OK");
        doit.tauko(1);
        
        // LUO UUSI KOULUTUS (validialog + jatka)
        driver.findElement(By.xpath("(//div[@class = 'v-filterselect-button'])[7]")).click();
        doit.tauko(1);
        if (luokka)
        {
            doit.textClick(driver, "Lukiokoulutus");
        }
        else
        {
            doit.textClick(driver, "Ammatillinen peruskoulutus");
            doit.tauko(1);
            driver.findElement(By.xpath("(//div[@class = 'v-filterselect-button'])[8]")).click();
            doit.tauko(1);
            doit.textClick(driver, "Ammatillinen tutkinto");
        }
        doit.tauko(1);
        driver.findElement(By.xpath("//span[@class = 'v-button-caption' and text() = 'Espoon kaupunki']")).click();
        doit.tauko(1);
        t01 = doit.millis();
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running TarjontaSavu005 Luo uusi koulutus + jatka ei toimi."
        		, doit.textElement(driver, "posti"));
        t01 = doit.millisDiff(t01);
		doit.footerTest(driver, "Running TarjontaSavu005 Luo uusi koulutus + jatka footer ei toimi.", true);
        System.out.println("Running TarjontaSavu005 Luo uusi koulutus + jatka OK");
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();

        // TARKASTELE KOULUTUSTA
        Assert.assertNotNull("Running TarjontaSavu006 TARKASTELE KOULUTUSTA ei toimi.", doit.textElement(driver, "Koulutukset ("));
        t01 = doit.millisDiff(t01);
        driver.findElement(By.className("v-treetable-treespacer")).click();
        Assert.assertNotNull("Running TarjontaSavu006 TARKASTELE KOULUTUSTA ei toimi."
        		, driver.findElement(By.xpath("//img[@class='v-icon']")));
        t01 = doit.millis();
        driver.findElement(By.xpath("//img[@class='v-icon']")).click();
        doit.tauko(1);
        t01 = doit.millis();
        doit.textClick(driver, "Tarkastele");
        
        Boolean lukiokoulutus = false;
        Boolean ammatillinenKoulutus = false;
        skip = true;
        while (skip)
        {
            if (doit.isPresentText(driver, "Lukiokoulutus")) { lukiokoulutus = true; skip = false; }
            if (doit.isPresentText(driver, "Ammatillinen koulutus")) { ammatillinenKoulutus = true; skip = false; }
            doit.tauko(1);
        }
        if (lukiokoulutus)
        {
                // Luo uusi lukiokoulutus
                Assert.assertNotNull("Running TarjontaSavu006 TARKASTELE KOULUTUSTA ei toimi."
                                , doit.textElement(driver, "osaksi toista koulutusta")); // lukiokoulutus ??
        }
        if (ammatillinenKoulutus)
        {
                Assert.assertNotNull("Running TarjontaSavu006 TARKASTELE KOULUTUSTA ei toimi."
                                , doit.textElement(driver, "Ammattinimikkeet")); // Ammatillinen koulutus
        }
        
        t01 = doit.millis();
		doit.footerTest(driver, "Running TarjontaSavu006 TARKASTELE KOULUTUSTA footer ei toimi.", true);
        System.out.println("Running TarjontaSavu006 TARKASTELE KOULUTUSTA OK");
        doit.tauko(1);
        
        // POISTA KOULUTUS
        String closeId = "";
        WebElement close = null;
        if (ammatillinenKoulutus)
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
        	System.out.println("Running TarjontaSavu007 POISTA KOULUTUS OK");
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
//        	System.out.println("Running TarjontaSavu008 KOPIOI UUDEKSI OK");
//        	doit.tauko(1);
        
        	// Lisaa rinnakkainen toteutus
//        	t01 = doit.millis();
//        	doit.textClick(driver, "rinnakkainen toteutus");
//        	Assert.assertNotNull("Running TarjontaSavu009 Lisaa rinnakkainen toteutus ei toimi."
//        			, doit.textElement(driver, "Valitse pohjakoulutus"));
//        	t01 = doit.millisDiff(t01);
//        	// doit.footerTest(driver, "Running TarjontaSavu009 Lisaa rinnakkainen toteutus footer ei toimi.", true);
//        	System.out.println("Running TarjontaSavu009 Lisaa rinnakkainen toteutus OK");
//            doit.tauko(1);
//        	t01 = doit.millis();
//            doit.textClick(driver, "Peruuta");
//            t01 = doit.millisDiff(t01);
        }
        doit.tauko(1);
    	t01 = doit.millis();
        driver.findElement(By.className("v-button-back")).click();
        t01 = doit.millisDiff(t01);
        doit.tauko(1);
        driver.findElement(By.className("v-treetable-treespacer")).click();


        // MUOKKAA KOULUTUSTA
        Assert.assertNotNull("Running TarjontaSavu010 MUOKKAA KOULUTUSTA ei toimi.", doit.textElement(driver, "Koulutukset ("));
        doit.tauko(1);
        Assert.assertNotNull("Running TarjontaSavu010 MUOKKAA KOULUTUSTA ei toimi."
        		, driver.findElement(By.xpath("//img[@class='v-icon']")));
        driver.findElement(By.xpath("//img[@class='v-icon']")).click();
        doit.tauko(1);
        t01 = doit.millis();
        doit.textClick(driver, "Muokkaa");
        Assert.assertNotNull("Running TarjontaSavu010 MUOKKAA KOULUTUSTA ei toimi."
        		, doit.textElement(driver, "posti"));
        t01 = doit.millisDiff(t01);
        Assert.assertNotNull("Running TarjontaSavu010 MUOKKAA KOULUTUSTA ei toimi."
        		, doit.textElement(driver, "Tallenna valmiina"));
		doit.footerTest(driver, "Running TarjontaSavu010 MUOKKAA KOULUTUSTA footer ei toimi.", true);
        System.out.println("Running TarjontaSavu010 MUOKKAA KOULUTUSTA OK");
        doit.tauko(1);
        
        // MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot
        lukiokoulutus = false;
        ammatillinenKoulutus = false;
        skip = true;
        while (skip)
        {
            if (doit.isPresentText(driver, "Luo uusi lukiokoulutus")) { lukiokoulutus = true; skip = false; }
            if (doit.isPresentText(driver, "Ammatillinen koulutus")) { ammatillinenKoulutus = true; skip = false; }
            doit.tauko(1);
        }
        t01 = doit.millis();
        doit.textClick(driver, "Koulutuksen kuvailevat tiedot");
        if (lukiokoulutus)
        {
        	// Luo uusi lukiokoulutus
        	Assert.assertNotNull("Running TarjontaSavu011 MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot ei toimi."
        			, doit.textElement(driver, "muiden toimijoiden kanssa")); // lukiokoulutus
        }
        if (ammatillinenKoulutus)
        {
        	Assert.assertNotNull("Running TarjontaSavu011 MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot ei toimi."
        			, doit.textElement(driver, "Koulutusohjelman valinta")); // Ammatillinen koulutus
//        	Assert.assertNotNull("Running TarjontaSavu011 MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot ei toimi."
//        			, doit.textElement(driver, "listyminen")); // toinen aste (kansainvalistyminen)
        }
        t01 = doit.millisDiff(t01);
		doit.footerTest(driver, "Running TarjontaSavu011 MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot footer ei toimi.", true);
        System.out.println("Running TarjontaSavu011 MUOKKAA KOULUTUSTA koulutuksen kuvailevat tiedot OK");
        doit.tauko(1);
        doit.textClick(driver, "Koulutuksen perustiedot");
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        doit.tauko(1);

        // SIIRRA TAI KOPIOI KOULUTUS
        Assert.assertNotNull("Running TarjontaSavu012 SIIRRA TAI KOPIOI KOULUTUS ei toimi."
                , doit.textElement(driver, "Koulutukset ("));
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
        System.out.println("Running TarjontaSavu012 SIIRRA TAI KOPIOI KOULUTUS OK");
        doit.tauko(1);
        doit.textClick(driver, "Peruuta");
        doit.tauko(1);
        System.out.println("Running TarjontaSavu END OK");
        // END
	}

	// TODO 01
	@Test
	public void test01() throws Exception {
		SVTUtils doit = new SVTUtils();
//		doit.palvelimenVersio(driver, baseUrl);
		driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.oph-login-url"));
		doit.tauko(1);
		doit.reppuLogin(driver);
		doit.tauko(1);
		Boolean luokka = false;
		if (SVTUtils.prop.getProperty("tarjonta-selenium.luokka").equals("true"))
		{
			luokka = true;
		}
		System.out.println("Running -------------------------------------------------------");
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
		System.out.println("Running TarjontaHakukohteetSavu001 Etusivu OK");
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
        System.out.println("Running TarjontaHakukohteetSavu002 Hae espoo OK");
        doit.tauko(1);

        // KOULUTUKSET JA HAKUKOHTEET
        WebElement espoo = driver.findElement(By.xpath("//span[contains(text(), 'Espoon kaupunki')]"));
        t01 = doit.millis();
        espoo.click();
        Assert.assertNotNull("Running TarjontaHakukohteetSavu003 Hae KOULUTUKSET JA HAKUKOHTEET ei toimi.", doit.textElement(driver, "Koulutukset ("));
        t01 = doit.millisDiff(t01);
        Assert.assertNotNull("Running TarjontaHakukohteetSavu003 Hae KOULUTUKSET JA HAKUKOHTEET ei toimi.", doit.textElement(driver, "Hakukohteet ("));
        System.out.println("Running TarjontaHakukohteetSavu003 Hae KOULUTUKSET JA HAKUKOHTEET OK");
        doit.tauko(1);

        // LUO UUSI HAKUKOHDE (validialog)
        Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
        		, doit.textElement(driver, "Koulutukset ("));
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
        t01 = doit.millis();
        checkBox3.click();
        while (! checkBoxId.isSelected()) { doit.tauko(1); }
        t01 = doit.millisDiff(t01);
        Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi.", checkBoxId.isSelected());
        doit.tauko(1);
        t01 = doit.millis();
        doit.textClick(driver, "Luo uusi hakukohde");
        Boolean a_scenario = false;
        Boolean b_scenario = false;
        String a_text = "Olet luomassa uutta hakukohdetta seuraavista koulutuksista";
        String b_text = "Olet valinnut useita koulutuksia. Hakukohteeseen voi kuulua vain yksi";
        Boolean skip2 = true;
        while (skip2)
        {
        	if (doit.isPresentText(driver, a_text)) { a_scenario = true; skip2 = false; }
        	if (doit.isPresentText(driver, b_text)) { b_scenario = true; skip2 = false; }
        	doit.tauko(1);
        }
        t01 = doit.millisDiff(t01);
        if (a_scenario)
        {
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
        			, doit.textElement(driver, a_text));
        }
        if (b_scenario)
        {
            Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
            		, doit.textElement(driver, b_text));
        }        	
        doit.tauko(1);
        String closeId = doit.idLike(driver, "window_close");
        driver.findElement(By.id(closeId)).click();
        doit.tauko(1);
        t01 = doit.millis();
        checkBox3.click();
        while (checkBoxId.isSelected()) { doit.tauko(1); }
        t01 = doit.millisDiff(t01);
        Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi.", checkBoxId.isSelected());
        doit.tauko(1);
        // ok to continue
        checkBoxId.click();
        doit.tauko(1);
        t01 = doit.millis();
        doit.textClick(driver, "Luo uusi hakukohde");
        if (luokka)
        {
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
        			, doit.textElement(driver, "Olet luomassa uutta hakukohdetta seuraavista koulutuksista"));
        	t01 = doit.millisDiff(t01);
        	t01 = doit.millis();
        	doit.textClick(driver, "Jatka");
        }
        Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
        		, doit.textElement(driver, "tietoja hakemisesta"));
        t01 = doit.millisDiff(t01);
        Assert.assertNotNull("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE ei toimi."
        		, doit.textElement(driver, "Tallenna luonnoksena"));
        System.out.println("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE OK");
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        
        // HAKUKOHTEEN TARKASTELU
        doit.notPresentText(driver, "Hakukohteet (0)"
                , "Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU Ei hakukohteita. Ei voi testata.");
        Assert.assertNotNull("Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU ei toimi."
        		, doit.textElement(driver, "Hakukohteet ("));
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
        doit.tauko(1);
        t01 = doit.millis();
        doit.textClick(driver, "Tarkastele");
        Assert.assertNotNull("Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU ei toimi."
        		, doit.textElement(driver, "uusi koulutus"));
        t01 = doit.millisDiff(t01);
        System.out.println("Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU OK");
        doit.tauko(1);

        // HAKUKOHTEEN MUOKKAUS
        t01 = doit.millis();
        doit.textClick(driver, "muokkaa");
        Assert.assertNotNull("Running TarjontaHakukohteetSavu006 HAKUKOHTEEN MUOKKAUS ei toimi."
        		, doit.textElement(driver, "tietoja hakemisesta"));
        t01 = doit.millisDiff(t01);
        Assert.assertNotNull("Running TarjontaHakukohteetSavu006 HAKUKOHTEEN MUOKKAUS ei toimi."
        		, doit.textElement(driver, "Tallenna valmiina"));
        System.out.println("Running TarjontaHakukohteetSavu006 HAKUKOHTEEN MUOKKAUS OK");
        doit.tauko(1);
        
        // HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot)
        t01 = doit.millis();
        doit.textClick(driver, "Liitteiden tiedot");
        Assert.assertNotNull("Running TarjontaHakukohteetSavu007 HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) ei toimi."
        		, doit.textElement(driver, "Toimitusosoite"));
        t01 = doit.millisDiff(t01);
        System.out.println("Running TarjontaHakukohteetSavu007 HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) OK");
        doit.tauko(1);
        
        // TODO this
        // HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite)
        t01 = doit.millis();
        doit.textClick(driver, "uusi liite");
        Assert.assertNotNull("Running TarjontaHakukohteetSavu007b HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite) ei toimi."
        		, doit.textElement(driver, "Voidaan toimittaa my"));
        t01 = doit.millisDiff(t01);
        System.out.println("Running TarjontaHakukohteetSavu007b HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite) OK");
        doit.tauko(1);
        doit.textClick(driver, "Peruuta");
        doit.tauko(1);
        
        // HAKUKOHTEEN MUOKKAUS (valintakokeet)
        t01 = doit.millis();
        doit.textClick(driver, "Valintakokeiden tiedot");
        // kahtalajia lomakkeita
        skip = true;
        Boolean kokonaispisteet = false;
        Boolean uusiValintakoe = false;
        while (skip)
        {
        	if (doit.isPresentText(driver, "Kokonaispisteet")) { skip = false; kokonaispisteet = true; }
        	if (doit.isPresentText(driver, "uusi valintakoe")) { skip = false; uusiValintakoe = true; }
        	doit.tauko(1);
        }
        if (kokonaispisteet)
        {
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu008 HAKUKOHTEEN MUOKKAUS (valintakokeet) ei toimi."
        			, doit.textElement(driver, "Ajankohta"));
        }
        if (uusiValintakoe)
        {
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu008 HAKUKOHTEEN MUOKKAUS (valintakokeet) ei toimi."
        			, doit.textElement(driver, "uusi valintakoe"));
        }
    	t01 = doit.millisDiff(t01);
        System.out.println("Running TarjontaHakukohteetSavu008 HAKUKOHTEEN MUOKKAUS (valintakokeet) OK");
        doit.tauko(1);
        
        // UUSI VALINTAKOE
        if (uusiValintakoe)
        {
            t01 = doit.millis();
            doit.textClick(driver, "uusi valintakoe");
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu008b HAKUKOHTEEN MUOKKAUS (uusi valintakoe) ei toimi."
        			, doit.textElement(driver, "Ajankohta"));
        	t01 = doit.millisDiff(t01);
            System.out.println("Running TarjontaHakukohteetSavu008b HAKUKOHTEEN MUOKKAUS (uusi valintakoe) OK");
            doit.tauko(1);
            doit.textClick(driver, "Peruuta");
            doit.tauko(1);
        }
        
        // HAKUKOHTEEN POISTO
        t01 = doit.millis();
        doit.textClick(driver, "Hakukohteen perustiedot");
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        Assert.assertNotNull("Running TarjontaHakukohteetSavu009 HAKUKOHTEEN POISTO ei toimi."
                , doit.textElement(driver, "Hakukohteet ("));
        t01 = doit.millisDiff(t01);
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
        closeId = doit.idLike(driver, "window_close");
        WebElement close = driver.findElement(By.id(closeId));
        Assert.assertNotNull("Running TarjontaHakukohteetSavu009 HAKUKOHTEEN POISTO ei toimi.", close);
        t01 = doit.millisDiff(t01);
        doit.tauko(1);
        close.click();
        doit.tauko(1);
        doit.notPresentText(driver, "window_close"
                , "Running TarjontaHakukohteetSavu009 HAKUKOHTEEN POISTO Close nakyy viela. Ei toimi.");
        System.out.println("Running TarjontaHakukohteetSavu009 HAKUKOHTEEN POISTO OK");

        doit.tauko(1);
        System.out.println("Running TarjontaHakukohteetSavu END OK");
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
