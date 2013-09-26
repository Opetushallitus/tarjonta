package fi.vm.sade.tarjonta.ui;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class TestTarjontaLuoAineistoa {
    private WebDriver driver;
    private String baseUrl;
    private StringBuffer verificationErrors = new StringBuffer();
    private SVTUtils doit = new SVTUtils();
    private static Boolean first = true;
	private String reppu = "reppu";

    @Before
    public void setUp() throws Exception {
            FirefoxProfile firefoxProfile = new FirefoxProfile();
            firefoxProfile.setEnableNativeEvents(true);
            firefoxProfile.setPreference( "intl.accept_languages", "fi-fi,fi" );
            driver = new FirefoxDriver(firefoxProfile);
            baseUrl = SVTUtils.prop.getProperty("tarjonta-selenium.oph-url");
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    		if (SVTUtils.prop.getProperty("tarjonta-selenium.luokka").equals("true")) { reppu = "luokka"; }
    		if (SVTUtils.prop.getProperty("tarjonta-selenium.qa").equals("true")) { reppu = "qa"; }
    }

    //    Luo haku
    @Test
    public void test_T_INT_TAR_LUO001_LuoHaku() throws Exception {
    	this.frontPage();
    	doit.echo("Running test_T_INT_TAR_LUO001_LuoHaku ...");
        doit.ValikotHakujenYllapito(driver, baseUrl);
        doit.textClick(driver, "Luo uusi haku");
        Assert.assertNotNull("Running LuoHaku Luo uusi haku ei toimi."
                , doit.textElement(driver, "Hakulomake"));
        doit.tauko(1);
        
        String millis = System.currentTimeMillis() + "";
        String yyyymmdd = doit.yyyymmddString();
        String ddmmyyyyhhmi = doit.ddmmyyyyhhmiString();
        String nimi = reppu + "haku" + yyyymmdd + "_" + millis;
        String kuvaus = reppu + "kuvaus " + yyyymmdd;
        doit.sendInput(driver, "Hakutyyppi", "Varsinainen haku");
        doit.popupItemClick(driver, "Varsinainen haku");
        doit.sendInput(driver, "Hakukausi ja -vuosi", "Syksy");
        doit.popupItemClick(driver, "Syksy");
        doit.sendInputPlusX(driver, "Hakukausi ja -vuosi", "2013", 200);
        doit.sendInputPlusX(driver, "Hakukausi ja -vuosi", "2013", 200);
        doit.sendInputPlusX(driver, "Hakukausi ja -vuosi", "2013", 200);
        doit.sendInput(driver, "Koulutuksen alkamiskausi", "Syksy");
        doit.popupItemClick(driver, "Syksy");
        doit.sendInputPlusX(driver, "Koulutuksen alkamiskausi", "2013", 300);
        doit.sendInputPlusX(driver, "Koulutuksen alkamiskausi", "2013", 300);
        doit.sendInputPlusX(driver, "Koulutuksen alkamiskausi", "2013", 300);
        doit.sendInput(driver, "Haun kohdejoukko", "Aikuiskoulutus");
        doit.popupItemClick(driver, "Aikuiskoulutus");
        doit.sendInput(driver, "Hakutapa", "Yhteishaku");
        doit.popupItemClick(driver, "Yhteishaku");
        doit.sendInputPlusY(driver, "Haun nimi", nimi);
        doit.sendInputTextArea(driver, "Hakuajan tunniste", kuvaus);
        doit.sendInput(driver, "Hakuaika alkaa", ddmmyyyyhhmi);
        doit.sendInput(driver, "Hakuaika päättyy", "30.11.2013 15:24");
        doit.sendInput(driver, "Haussa käytetään sijoittelua", "SELECTED");

        doit.textClick(driver, "Tallenna valmiina");
        Assert.assertNotNull("Running LuoHaku Tallenna valmiina ei toimi."
                , doit.textElement(driver, "Tallennus onnistui"));
        doit.tauko(1);
        
        driver.findElement(By.className("v-button-back")).click();
		Assert.assertNotNull("Running LuoHaku back ei toimi."
				, doit.textElement(driver, "Luo uusi haku"));
        doit.tauko(1);

        // HAE
        WebElement haeKentta = driver.findElement(By.className("v-textfield-search-box"));
        haeKentta.clear();
        haeKentta.sendKeys("tatahakuaeiloydy");
        doit.tauko(1);
        doit.textClick(driver, "Hae");
        doit.tauko(3);

//        WebElement haeKentta = driver.findElement(By.className("v-textfield-search-box"));
        haeKentta.clear();
        haeKentta.sendKeys(millis);
        doit.tauko(1);
        doit.textClick(driver, "Hae");
		Assert.assertNotNull("Running LuoHaku Hae ei toimi."
				, doit.textElement(driver, "Yhteishaku"));
		doit.tauko(1);
		WebElement lastTriangle = doit.getTriangleForLastHakukohde(driver);
		lastTriangle.click();        
		Assert.assertNotNull("Running LuoHaku Hae ei toimi."
				, doit.textElement(driver, nimi));
        doit.tauko(1);
        // Julkaise
        doit.menuOperaatio(driver, "Julkaise", nimi);
        Assert.assertNotNull("Running LuoHaku Julkaise ei toimi."
                , doit.textElement(driver, "Haluatko varmasti julkaista alla mainitun haun"));
        doit.tauko(1);
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running LuoHaku Julkaise ei toimi."
                , doit.textElement(driver, "Toiminto onnistui"));
        doit.tauko(1);
    	doit.echo("Running test_T_INT_TAR_LUO001_LuoHaku OK");
    }

    //  Luo koulutus (ammatillinen koulutus)
    @Test
    public void test_T_INT_TAR_LUO002_LuoKoulutusAMK() throws Exception {
    	this.frontPage();
    	doit.echo("Running test_T_INT_TAR_LUO002_LuoKoulutusAMK ...");
    	doit.ValikotHakukohteidenYllapito(driver, baseUrl);
        
        // HAE
        WebElement haeKentta = driver.findElement(By.className("v-textfield-search-box"));
        haeKentta.clear();
        haeKentta.sendKeys(reppu + "laitos");
        doit.tauko(1);
        doit.textClick(driver, "Hae");
        Assert.assertNotNull("Running test_T_INT_TAR_LUO002_LuoKoulutusAMK Hae " + reppu + "laitos2013 ei toimi."
        		, doit.textElement(driver, reppu + "laitos2013"));
        doit.tauko(1);
        String yyyymmdd = doit.yyyymmddString();
        String organisaatio = reppu + "laitos" + yyyymmdd;
        try {
			doit.textClick(driver, organisaatio);
		} catch (Exception e) {
	        organisaatio = reppu + "laitos2013";
			doit.textClick(driver, organisaatio);
		}
        doit.tauko(1);
        
        // poistetaan aikaisemmin mahdollisesti luotu hevoskoulutus
        if (doit.PoistaKoulutus(driver, "Hevo"))
        {
        	doit.textClick(driver, organisaatio);
        	doit.tauko(1);
        }
        //
        doit.textClick(driver, "Luo uusi koulutus");
        Assert.assertNotNull("Running LuoKoulutusAMK Luo uusi koulutus ei toimi."
                , doit.textElement(driver, "Olet luomassa uutta koulutusta"));
        doit.sendInputPlusX(driver, "Koulutus:", "Ammatillinen peruskoulutus", 200);
        doit.popupItemClick(driver, "Ammatillinen peruskoulutus");
        doit.tauko(1);
        doit.sendInputPlusX(driver, "Pohjakoulutus:", "Peruskoulu", 20);
        doit.popupItemClick(driver, "Peruskoulu");
        doit.tauko(1);
        driver.findElement(By.xpath("//span[@class = 'v-button-caption' and contains(text(), '" + reppu + "laitos')]")).click();
        doit.tauko(1);
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running LuoKoulutusAMK Luo uusi ammatillinenkoulutus + jatka ei toimi."
        		, doit.textElement(driver, "posti"));
        doit.sendInput(driver, "Koulutus tai tutkinto", "Hevostalouden perustutkinto");
        doit.tauko(1);
        doit.popupItemClick(driver, "Hevostalouden perustutkinto");
        doit.tauko(1);
        doit.sendInput(driver, "Koulutusohjelma", "Hevostalouden koulutusohjelma, ratsastuksenohjaaja");
        doit.popupItemClick(driver, "Hevostalouden koulutusohjelma, ratsastuksenohjaaja");
        doit.tauko(1);
        
        Assert.assertNotNull("Running LuoKoulutusAMK Koulutusaste ei toimi."
        		, doit.textElement(driver, "Koulutusala"));
        Assert.assertNotNull("Running LuoKoulutusAMK Opintoala ei toimi."
        		, doit.textElement(driver, "Maatilatalous"));
        Assert.assertNotNull("Running LuoKoulutusAMK Koulutusala ei toimi."
        		, doit.textElement(driver, "Luonnonvara- ja ympäristöala"));
        Assert.assertNotNull("Running LuoKoulutusAMK Opintojen laajuusyksikkö ei toimi."
        		, doit.textElement(driver, "opintoviikko"));
        Assert.assertNotNull("Running LuoKoulutusAMK Opintojen laajuus ei toimi."
        		, doit.textElement(driver, "120"));
        Assert.assertNotNull("Running LuoKoulutusAMK Tutkintonimike ei toimi."
        		, doit.textElement(driver, "Ratsastuksenohjaaja"));
        Assert.assertNotNull("Running LuoKoulutusAMK Koulutuksen rakenne ei toimi."
        		, doit.textElement(driver, "Tutkinnon kaikille pakolliset osat"));
        Assert.assertNotNull("Running LuoKoulutusAMK Tutkinnon koulutukselliset ja ammatilliset tavoitteet ei toimi."
        		, doit.textElement(driver, "ratsastuksenohjaaja vastaa ratsastuskoulun hevosten hoidosta"));
        Assert.assertNotNull("Running LuoKoulutusAMK Jatko-opintomahdollisuudet ei toimi."
        		, doit.textElement(driver, "Ammatillisista perustutkinnoista sekä ammatti"));
        doit.tauko(1);

        doit.sendInput(driver, "Suunniteltu kesto", "3");
        doit.sendInput(driver, "Opetusmuoto", "Oppisopimuskoulutus");
        doit.popupItemClick(driver, "Oppisopimuskoulutus");
        doit.sendInputPlusX(driver, "Suunniteltu kesto", "Kuukausi", 150); // Valitse aikayksikko
        doit.popupItemClick(driver, "Kuukausi");
        doit.sendInput(driver, "Opetuskieli", "suomi");
        doit.popupItemClick(driver, "suomi");
        
        doit.textClickLast(driver, "Tallenna valmiina");
        Assert.assertNotNull("Running LuoKoulutusAMK Tallenna ei toimi."
        		, doit.textElement(driver, "Tallennus onnistui"));
        doit.tauko(1);
                
        driver.navigate().refresh();
        doit.tauko(1);
        WebElement back = doit.findNearestElementMinusY("Koulutuksen perustiedot", "//div[contains(@class, 'v-button-back')]", driver);
        doit.focus(driver, back);
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        doit.tauko(10);
        driver.navigate().refresh();
        doit.tauko(1);
        Assert.assertNotNull("Running LuoKoulutusAMK valikot ei toimi."
                , doit.textElement(driver, "Koulutuksen alkamisvuosi"));
        doit.tauko(1);
    	
        // Julkaise
        if (! doit.isPresentText(driver, "Hevo")) { doit.getTriangleForFirstItem(driver).click(); doit.tauko(1); }
        doit.menuOperaatio(driver, "Julkaise", "Hevo");
        Assert.assertNotNull("Running test_T_INT_TAR_LUO002_LuoKoulutusAMK Julkaise ei toimi."
                , doit.textElement(driver, "Toiminto onnistui"));
    	doit.echo("Running test_T_INT_TAR_LUO002_LuoKoulutusAMK OK");
    }

    //    Luo hakukohde
    @Test
    public void test_T_INT_TAR_LUO003_LuoHakukohde() throws Exception {
    	this.frontPage();
    	doit.echo("Running test_T_INT_TAR_LUO003_LuoHakukohde ...");
        doit.ValikotHakukohteidenYllapito(driver, baseUrl);
        
        // HAE
        WebElement haeKentta = driver.findElement(By.className("v-textfield-search-box"));
        if (doit.isPresentText(driver, reppu + "laitos"))
        {
            haeKentta.clear();
            haeKentta.sendKeys("alavuden kaupun");
            doit.tauko(1);
            driver.findElement(By.xpath("//*[text()='Hae']")).click();
            Assert.assertNotNull("Running test_T_INT_TAR_LUO003_LuoHakukohde Hae Alavuden kaupunki ei toimi."
                            , doit.textElement(driver, "Alavuden kaupunki"));
            doit.tauko(1);
        }
        haeKentta.clear();
        haeKentta.sendKeys(reppu + "laitos");
        doit.tauko(1);
        driver.findElement(By.xpath("//*[text()='Hae']")).click();
        Assert.assertNotNull("Running LuoHakukohde Hae " + reppu + "laitos ei toimi."
                        , doit.textElement(driver, reppu + "laitos2013"));
        doit.tauko(1);
        String yyyymmdd = doit.yyyymmddString();
        String organisaatio = reppu + "laitos" + yyyymmdd;
        try {
			doit.textClick(driver, organisaatio);
		} catch (Exception e1) {
	        organisaatio = reppu + "laitos" + yyyymmdd.substring(0, 4); 
			doit.textClick(driver, organisaatio);
		}
        Assert.assertNotNull("Running LuoHakukohde Hae Koulutukset ( ei toimi."
                , doit.textElement(driver, "Koulutukset ("));
        doit.tauko(1);
        if (doit.isPresentText(driver, "Koulutukset (0)"))
        {
        	doit.echo("Ei loydy koulutuksia " + organisaatio + ":lle.");
        	int a = 1 / 0;
        }
        WebElement LuoUusiHakukohde = doit.textElement(driver, "Luo uusi hakukohde");

        // Luo hakukohde
        doit.getTriangleForFirstItem(driver).click();
        doit.tauko(1);
        String gwtIdKoulutus1 = doit.getGwtIdForFirstHakukohde(driver);
        WebElement checkBoxKoulutus1 = driver.findElement(By.id(gwtIdKoulutus1));
        checkBoxKoulutus1.click();
        doit.tauko(1);
        LuoUusiHakukohde = doit.textElement(driver, "Luo uusi hakukohde");
        doit.tauko(1);
        LuoUusiHakukohde.click();

        // validialog
        Assert.assertNotNull("Running LuoHakukohde LuoUusiHakukohde ei toimi."
                , doit.textElement(driver, "Olet luomassa uutta hakukohdetta seuraavista koulutuksista"));
        Assert.assertNotNull("Running LuoHakukohde LuoUusiHakukohde ei toimi."
                , doit.textElement(driver, "Jatka"));
        doit.tauko(1);
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running LuoHakukohde Jatka ei toimi."
                , doit.textElement(driver, "voidaan kuvata muuta hakemiseen olennaisesti liittyv"));
        doit.tauko(1);
        
        // lomakkeen taytto
        // Hakukohteen nimi (listalta ensimmainen mika saattuu tuleen vastaan)
        WebElement hakukohteenNimiPopup = doit.findNearestElement("Hakukohteen nimi", "//input[@class='v-filterselect-input']", driver);
        hakukohteenNimiPopup.clear();
        doit.tauko(1);
        hakukohteenNimiPopup.sendKeys(Keys.ARROW_DOWN);
        doit.tauko(1);
        driver.findElement(By.xpath("(//td[@class='gwt-MenuItem'])[1]")).click();
        doit.tauko(1);
        //Haku joku mika valikosta loytyy
        String haku = reppu + "haku" + yyyymmdd;
        String kuvaus = reppu + "kuvaus " + yyyymmdd;
		doit.sendInputExact(driver, "Haku", haku);
        doit.tauko(1);
        WebElement menuItem;
		try {
			menuItem = driver.findElement(By.xpath("(//td[@class='gwt-MenuItem'])[1]"));
		} catch (Exception e1) {
	        haku = reppu + "haku" + yyyymmdd.substring(0, 4);
	        kuvaus = reppu + "kuvaus " + yyyymmdd.substring(0, 4);
			doit.sendInputExact(driver, "Haku", haku);
			menuItem = driver.findElement(By.xpath("(//td[@class='gwt-MenuItem'])[1]"));
		}
//        if (menuItem != null && menuItem.getText().length() == 0)
//        {
//        	menuItem = driver.findElement(By.xpath("(//td[@class='gwt-MenuItem'])[2]"));
//        }
        menuItem.click();
        doit.tauko(1);
        
        doit.sendInput(driver, "Hakuaika", kuvaus);
        String ilmoitettavat = (System.currentTimeMillis() + "").substring(7);
        while (ilmoitettavat.substring(0,1) == "0") { ilmoitettavat = ilmoitettavat.substring(1); }
        doit.sendInput(driver, "Hakijoille ilmoitettavat aloituspaikat", ilmoitettavat);
        doit.sendInput(driver, "Valinnoissa käytettävät aloituspaikat", "10");
        doit.sendInputTiny(driver, "voidaan kuvata muuta hakemiseen olennaisesti", reppu + "hakukohde" + yyyymmdd);
        
//        doit.sendInputPlusX(driver, "Alku:", "31.07.2013 15:24", 20);
//        doit.sendInput(driver, "Loppu:", "30.11.2013 15:24");
        
        // Tallenna
        doit.textClick(driver, "Tallenna valmiina");
        Assert.assertNotNull("Running LuoHakukohde Tallenna ei toimi."
                        , doit.textElement(driver, "Tallennus onnistui"));

        // Back
        driver.findElement(By.className("v-button-back")).click();
        Assert.assertNotNull("Running LuoHakukohde Jatka ei toimi."
                , doit.textElement(driver, "Hakukohteet ("));
        doit.tauko(1);

        // Hae luotu hakukohde
        doit.textClick(driver, "Hakukohteet (");
        Assert.assertNotNull("Running LuoHakukohde Hae haku ei toimi."
                , doit.textElement(driver, "Hakukohteet ("));
        doit.tauko(1);
        WebElement triangle = doit.getTriangleForFirstItem(driver);
        try {
        	if (! doit.isPresentText(driver, ilmoitettavat)) { triangle.click(); }
        	Assert.assertNotNull("Running LuoHakukohde Hae haku ei toimi."
        			, doit.textElement(driver, ilmoitettavat));
        } catch (Exception e) {
        	triangle = doit.getTriangleForLastHakukohde(driver);
        	triangle.click();
        	Assert.assertNotNull("Running LuoHakukohde Hae haku ei toimi."
        			, doit.textElement(driver, ilmoitettavat));
        }
        // Julkaise
        if (! doit.isPresentText(driver, "julkaistu"))
        {
        	doit.menuOperaatio(driver, "Julkaise", "Hevo");
        	Assert.assertNotNull("Running test_T_INT_TAR_LUO003_LuoHakukohde Julkaise ei toimi."
        			, doit.textElement(driver, "Toiminto onnistui"));
        }
        doit.tauko(1);
    	doit.echo("Running test_T_INT_TAR_LUO003_LuoHakukohde OK");
    }

    public void frontPage() throws Exception
    {
    	// LOGIN
    	driver.get(baseUrl);
    	doit.tauko(1);
    	doit.reppuLogin(driver);
    	doit.tauko(1);
    	driver.get(baseUrl);
    	doit.tauko(1);
    	Assert.assertNotNull("Running TarjontaPunainenLanka000 Etusivu ei toimi."
    			, doit.textElement(driver, "Tervetuloa Opintopolun virkailijan palveluihin!"));
    	doit.tauko(1);
    	first = false;
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
