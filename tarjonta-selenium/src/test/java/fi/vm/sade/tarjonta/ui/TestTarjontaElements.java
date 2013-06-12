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

public class TestTarjontaElements {

    private WebDriver driver;
    private String baseUrl;
    private StringBuffer verificationErrors = new StringBuffer();
    private static Boolean first = true;
    private SVTUtils doit;
    private Boolean qa = false;
    private Boolean luokka = false;
    private Boolean reppu = false;

    private Boolean readPageFromFile = false;

    @Before
    public void setUp() throws Exception {
        doit = new SVTUtils();
        if (readPageFromFile) { return; }

        FirefoxProfile firefoxProfile = new FirefoxProfile();
        firefoxProfile.setPreference( "intl.accept_languages", "fi-fi,fi" ); 
        driver = new FirefoxDriver(firefoxProfile);

        baseUrl = SVTUtils.prop.getProperty("tarjonta-selenium.oph-url"); // "http://localhost:8080/"
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        qa = false;
        if (SVTUtils.prop.getProperty("tarjonta-selenium.qa").equals("true"))
        {
        	qa = true;
        }
        reppu = false;
        if (SVTUtils.prop.getProperty("tarjonta-selenium.reppu").equals("true"))
        {
        	reppu = true;
        }
        luokka = false;
        if (SVTUtils.prop.getProperty("tarjonta-selenium.luokka").equals("true"))
        {
        	luokka = true;
        }
        if (first) { doit.palvelimenVersio(driver, baseUrl); }
    }
    
    public void frontPage() throws Exception
    {
    	if (readPageFromFile) { return; }
    	if (first) { doit.echo("Running ================================================================="); }
		driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.oph-login-url"));
		doit.tauko(1);
        // LOGIN REPULLE tai luokka
        if (reppu || luokka || qa)
        {
            doit.reppuLogin(driver);
            driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.tarjonta-url"));
        }
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
        Assert.assertNotNull("Running TarjontaElements000 Etusivu ei toimi."
        , doit.textElement(driver, "Valitse kaikki"));
        doit.footerTest(driver, "Running TarjontaElements000 Etusivu footer ei toimi.", true);
        if (first) { doit.echo("Running TarjontaElements000 Etusivu OK"); }
        first = false;
        doit.tauko(1);
    }

	@Test
	public void testEtuSivu() throws Exception {
        if (! readPageFromFile)
        {
        	this.frontPage();
        }

        String elements = ""
        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-search-box search-box v-textfield-prompt\""
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<input type=\"checkbox\" value=\"on\" id=\"gwt-uid-1\" tabindex=\"0\""
        		+ ".*<label for=\"gwt-uid-1\">Näytä myös lakkautetut</label>"
        		+ ".*<input type=\"checkbox\" value=\"on\" id=\"gwt-uid-2\" tabindex=\"0\""
        		+ ".*<label for=\"gwt-uid-2\">Näytä myös suunnitellut</label>"
        		+ ".*<span class=\"v-button-caption\">Hae</span>"
        		+ ".*<span class=\"v-button-caption\">Tyhjennä</span>"
        		+ ".*<div tabindex=\"0\" class=\"v-button v-button-vertical-collapse vertical-collapse\" role=\"button\">"
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">OPH</div>"
        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-search-box search-box\" style=\"width: 268px;\""
				+ ".*<span class=\"v-button-caption\">Hae</span>"
        		+ ".*<div class=\"v-captiontext\">Koulutuksen alkamisvuosi</div>"
        		+ ".*<div class=\"v-captiontext\" style=\"width: 200px;\">Koulutuksen alkamiskausi</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<span class=\"v-button-caption\">Tyhjennä</span>"
        		+ ".*<div class=\"v-captiontext\">Koulutukset</div>"
        		+ ".*<div class=\"v-captiontext\">Hakukohteet</div>"
        		+ ".*<span class=\"v-button-caption\">Siirrä tai kopioi</span>"
        		+ ".*<span class=\"v-button-caption\">Luo uusi hakukohde</span>"
        		+ ".*<span class=\"v-button-caption\">Luo uusi koulutus</span>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div tabindex=\"0\" class=\"v-button v-button-info info\" role=\"button\">"
        		+ ".*<input type=\"checkbox\" value=\"on\" id=\"gwt-uid-3\" tabindex=\"0\""
        		+ ".*<label for=\"gwt-uid-3\">Valitse kaikki</label>"
        		+ ".*2KPL<input type=\"text\" class=\"v-textfield v-textfield-search-box search-box"
        		+ ".*7KPL<input type=\"text\" class=\"v"
        		+ ".*4KPL<div class=\"v-captiontext\">"
        		+ ".*10KPL<span class=\"v-button-caption\">"
        		+ ".*5KPL<div class=\"v-filterselect-button\"></div>"
        		+ ".*3KPL<input type=\"checkbox\""
        		+ ".*3KPL<label for=\"gwt-uid"
        		;

        doit.skipLoading(readPageFromFile);
        Assert.assertTrue("Running TarjontaElements001 EtuSivu ei toimi."
                        , doit.checkElements(driver, elements, false));
        doit.echo("Running TarjontaElements001 EtuSivu OK");
	}

	public void TarkasteleKoulutus(String haku, String linkki) throws Exception {
    	WebElement search = driver.findElements(By.className("v-textfield-search-box")).get(1);
    	search.clear();
    	search.sendKeys(haku);
    	doit.tauko(1);
    	driver.findElement(By.xpath("(//span[text() = 'Hae'])[2]")).click();
    	WebElement triangle = doit.getTriangleForFirstItem(driver);
        Assert.assertNotNull("Running TarjontaElements000 koulutushaku ei toimi.", triangle);
    	doit.tauko(1);
    	triangle.click();
    	WebElement link = doit.textElement(driver, linkki); 
        Assert.assertNotNull("Running TarjontaElements000 koulutushaku ei toimi.", link);
        link.click();
        Assert.assertNotNull("Running TarjontaElements000 koulutushaku ei toimi."
        		, doit.textElement(driver, "Koulutusaste"));
	}
	
	// tarkasteleLukioKoulutus
	@Test
	public void testTarkasteleLukioKoulutus() throws Exception {
        if (! readPageFromFile)
        {
        	this.frontPage();
        	this.TarkasteleKoulutus("ylioppilastutkint", "Ylioppilastutkinto");
        }
        
        String elements = "<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ ".*<span class=\"v-button-caption\">Poista</span>"
        		+ ".*<div class=\"v-label v-label-h1 h1\" style=\"width: 1162px;\">"
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">Koulutuksen perustiedot</div>"
        		+ ".*<div class=\"v-label\" style=\"width: 2..px;\">( Tallennettu"
        		+ ".*<span class=\"v-button-caption\">muokkaa</span>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Organisaatio</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutus</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">Ylioppilastutkinto"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Lukiolinja</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
				+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
				+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
				+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutusaste</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">Yleissivistävä koulutus</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Opintoala</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">Lukiokoulutus</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Tutikintonimike</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">Ylioppilas</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Opintojen laajuusyksikkö</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">opintopiste</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Opintojen laajuus</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">70</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutuslaji</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Pohjakoulutusvaatimus</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutuksen rakenne</div>"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutuksellset tavoitteet</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Jatko-opintomahdollisuudet</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Opetuskieli</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutuksen alkamispäivä</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Suunniteltu kesto</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Opetusmuoto</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Linkki opetussuunnitelmaan</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutuksen yhteyshenkilö</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">Koulutuksen kuvailevat tiedot</div>"
        		+ ".*<span class=\"v-button-caption\">muokkaa</span>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">A1-/A2-kieli</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
//        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">englanti</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">B1-kieli</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
//        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">ruotsi</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">B2-kieli</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">B3-kieli</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Muut kielet</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Lukiodiplomit</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutuksen sisältö</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Kansainvälistyminen</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Yhteistyö muiden toimijoiden kanssa</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
//        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:" // ylimaarainen debuggia varten
        		+ ".*32KPL<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*4KPL<span class=\"v-button-caption\">"
        		+ ".*32KPL<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">"
//        		+ ".*54KPLheight: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
		;

        doit.skipLoading(readPageFromFile);
        Assert.assertTrue("Running TarjontaElements002 TarkasteleLukioKoulutus ei toimi."
        		, doit.checkElements(driver, elements, false));
        doit.echo("Running TarjontaElements002 TarkasteleLukioKoulutus OK");
	}
	
	// muokkaaLukioKoulutuksenPerustiedot
	@Test
	public void testMuokkaaLukioKoulutuksenPerustiedot() throws Exception {
        if (! readPageFromFile)
        {
                this.frontPage();
                this.TarkasteleKoulutus("ylioppilastutkint", "Ylioppilastutkinto");
                doit.textClick(driver, "muokkaa"); // click Muokkaa(1)
                Assert.assertNotNull("Running TarjontaElements000 koulutushaku ei toimi."
                		, doit.textElement(driver, "Puhelinnumero"));
        }
        
        String elements = "<div class=\"v-label v-label-light light v-label-undef-w\">Olet luomassa"
        		+ ".*<div class=\"v-captiontext\">Koulutuksen perustiedot</div>"
        		+ ".*<div class=\"v-captiontext\">Koulutuksen kuvailevat tiedot</div>"
        		+ ".*<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ ".*<span class=\"v-button-caption\">Tallenna luonnoksena</span>"
        		+ ".*<span class=\"v-button-caption\">Tallenna valmiina</span>"
        		+ ".*<span class=\"v-button-caption\">Jatka</span>"
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">Luo uusi lukiokoulutus</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<button type=\"button\" class=\"v-datefield-button\" tabindex=\"-2\"></button>"
        		+ ".*<div class=\"v-required-field-indicator\">*</div>"
        		+ ".*<div class=\"v-required-field-indicator\">*</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-required-field-indicator\">*</div>"
        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\""
        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\""
        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\""
        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\""
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutus / tutkinto</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Lukiolinja</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutusaste</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 8..px;\">Lukiokoulutus</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutusala</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 8..px;\">Yleissivistävä koulutus</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Opintoala</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 8..px;\">Lukiokoulutus</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Tutkintonimike</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 8..px;\">Ylioppilas</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Opintojen laajuusyksikkö</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 8..px;\">opintopiste</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutuslaji</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Pohjakoulutusvaatimus</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutuksen rakenne</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutukselliset tavoitteet</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Jatko-opintomahdollisuudet</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Opetuskieli</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input"
				+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-required-field-indicator\">*</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutuksen alkamispäivä</div>"
//        		+ ".*<input type=\"text\" class=\"v-textfield v-datefield-textfield\">"
        		+ ".*<div class=\"v-label v-label-undef-w\">Suunniteltu kesto</div>"
//        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-required\">"
//        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 120px;\">"
//        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Opetusmuoto</div>"
        		+ ".*<select class=\"v-select-twincol-options\" size=\"10\" multiple="
        		+ ".*<div tabindex=\"0\" class=\"v-button\" role=\"button\"><span class=\"v-button-wrap\"><span class=\"v-button-caption\">&gt;&gt;</span></span></div>"
        		+ ".*<div tabindex=\"0\" class=\"v-button\" role=\"button\"><span class=\"v-button-wrap\"><span class=\"v-button-caption\">&lt;&lt;</span></span></div>"
        		+ ".*<select class=\"v-select-twincol-selections\" size=\"10\" multiple="
        		+ ".*<div class=\"v-required-field-indicator\">*</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Linkki opetussuunnitemaan</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Yhteyshenkilö</div>"
        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\""
        		+ ".*<span class=\"v-button-caption\">Tyhjennä</span>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Tehtävänimike</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Sähköpostiosoite</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Puhelinnumero</div>"
        		+ ".*<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ ".*<span class=\"v-button-caption\">Tallenna luonnoksena</span>"
        		+ ".*<span class=\"v-button-caption\">Tallenna valmiina</span>"
        		+ ".*<span class=\"v-button-caption\">Jatka</span>"
//        		+ ".*<div class=\"v-label v-label-undef-w\">" // debug rivi
//        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"  // debug rivi
//        		+ ".*<div class=\"v-filterselect-button\"></div>" // debug rivi
//        		+ ".*<input type=\"text\" class=\"v-filterselect-input" // debug rivi
//        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-required\">" // debug rivi
//        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\"" // debug rivi
        		+ ".*23KPL<div class=\"v-label v-label-undef-w\">"
        		+ ".*18KPL<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*11KPL<span class=\"v-button-caption\">"
        		+ ".*5KPLv-required-field-indicator"
        		+ ".*5KPLv-textfield v-textfield-prompt"
        		+ ".*4KPL<input type=\"text\" class=\"v-filterselect-input"
        		+ ".*4KPL<div class=\"v-filterselect-button\"></div>"
        		+ ".*1KPL<div class=\"v-label v-label-undef-w\">Koulutusaste</div>"
        		+ ".*1KPLv-datefield-textfield"
		;

        doit.skipLoading(readPageFromFile);
        Assert.assertTrue("Running TarjontaElements003 MuokkaaLukioKoulutuksenPerustiedot ei toimi."
        		, doit.checkElements(driver, elements, false));
        doit.echo("Running TarjontaElements003 MuokkaaLukioKoulutuksenPerustiedot OK");
	}

	// muokkaaLukioKoulutusKuvailevatTiedot
	@Test
	public void testMuokkaaLukioKoulutusKuvailevatTiedot() throws Exception {
        if (! readPageFromFile)
        {
                this.frontPage();
                this.TarkasteleKoulutus("ylioppilastutkint", "Ylioppilastutkinto");                
                driver.findElement(By.xpath("(//*[text()='muokkaa'])[2]")).click(); // click Muokkaa(2)
                Assert.assertNotNull("Running TarjontaElements000 koulutushaku ei toimi."
                                , doit.textElement(driver, "Yritysten nimien mainitsemista"));
        }
        
        String elements = "<div class=\"v-label v-label-light light v-label-undef-w\">Olet luomassa"
        		+ ".*<div class=\"v-captiontext\">Koulutuksen perustiedot</div>"
        		+ ".*<div class=\"v-captiontext\">Koulutuksen kuvailevat tiedot</div>"
        		+ ".*<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ ".*<span class=\"v-button-caption\">Tallenna luonnoksena</span>"
        		+ ".*<span class=\"v-button-caption\">Tallenna valmiina</span>"
        		+ ".*<span class=\"v-button-caption\">Jatka</span>"
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">Koulutuksen kuvailevat tiedot</div>"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: 1122px;\">Kielivalikoima</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 1122px;\">Lukion kielet listataan tässä laajuuksien mukaan. Jos esim. englantia voi opiskella sekä A1- että B3-kielinä, merkitään se molempien laajuuksien kohdalle.</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">A1-/A2-kieli</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 1..px;\""
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 953px;\">Tyypillisesti peruskoulun vuosiluokilla 1.-2. aloitettu vieras kieli</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">B1-kieli</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 1..px;\""
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 953px;\">Peruskoulun vuosiluokilla 7.-9. aloitettu yhteinen toinen kotimainen kieli tai englanti</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">B2-kieli</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 1..px;\""
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 953px;\">Peruskoulun vuosiluokilla 7.-9. aloitettu valinnainen kieli</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">B3-kieli</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 1..px;\""
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 953px;\">Lukiossa aloitettava valinnainen vieras kieli</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Muut kielet</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 1..px;\""
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 953px;\">Lukiossa aloitettava valinnainen vieras kieli, jonka laajuus on suuppeampi kuin B2/B3</div>"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: 1122px;\">Lukiodiplomit</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 1122px;\">Listaus niistä oppiaineista, joissa on mahdollista suorittaa lukiodiplomi koulutuksen yhteydessä. Lukiodiplomin suorittamisen järjestäminen on lukiolle vapaaehtoista.</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 1..px;\""
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<img alt=\"\" class=\"v-icon\" style=\"\" src=\"/tarjonta-app/VAADIN/themes/oph/../../themes/oph/img/icon-add-black.png\""
        		+ ".*<div class=\"v-captiontext\">"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: 1084px;\">Koulutuksen sisältö</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 1084px;\">Kuvaus lukiolinjan keskeisistä sisällöistä, painotuksista ja toteutustavoista. Henkilökohtaisen opiskelusuunnitelman laatimisesta on hyvä mainita. Verkkotekstissä on hyvä käyttä lyhyitä lauseita ja kappaleita ja tarvittaessa listoja. Vapaa teksti 16.384 merkkiä.</div>"
        		+ ".*tinyMCE.getInstanceById"
        		+ ".*title=\"Rich Text Area"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: 1084px;\">Kansainvälistyminen</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 1084px;\">Kirjoitetaan jos kansainvälisyyys on keskeinen osa koulutusta. Esim. yhteistyö tai ulkomailla opiskelu / vaihto-opiskelumahdollisuudet. Valitse otsikot itse. Vapaa teksti 16.384 merkkiä.</div>"
        		+ ".*tinyMCE.getInstanceById"
        		+ ".*title=\"Rich Text Area"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: 1084px;\">Yhteistyö muiden toimijoiden kanssa</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 1084px;\">Tässä voidaan kertoa työelämäyhteistyöstä, oppilaitosten välisestä yhteistyöstä opintopolkujen toteuttamisessa tms. Yritysten nimien mainitsemista on kuitenkin syytä välttää. Vapaa teksti 16.384 merkkiä.</div>"
        		+ ".*tinyMCE.getInstanceById"
        		+ ".*title=\"Rich Text Area"
        		+ ".*<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ ".*<span class=\"v-button-caption\">Tallenna luonnoksena</span>"
        		+ ".*<span class=\"v-button-caption\">Tallenna valmiina</span>"
        		+ ".*<span class=\"v-button-caption\">Jatka</span>"
        		+ ".*6KPL<div class=\"v-filterselect-button\"></div>"
        		+ ".*6KPL<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 1..px;\""
        		+ ".*6KPL<div class=\"v-label v-label-undef-w\">"
        		+ ".*10KPL<div class=\"v-label v-label-light light\" style=\"width:"
        		+ ".*3KPLtitle=\"Rich Text Area"
        		+ ".*3KPLtinyMCE.getInstanceById"
        		+ ".*8KPL<span class=\"v-button-caption\">"
                ;

        doit.skipLoading(readPageFromFile);
        Assert.assertTrue("Running TarjontaElements004 MuokkaaLukioKoulutusKuvailevatTiedot ei toimi."
        		, doit.checkElements(driver, elements, false));
        doit.echo("Running TarjontaElements004 MuokkaaLukioKoulutusKuvailevatTiedot OK");
	}
	
	// tarkasteleAmmatillinenKoulutus
	@Test
	public void testTarkasteleAmmatillinenKoulutus() throws Exception {
        String poista = ".*<span class=\"v-button-caption\">Poista</span>";
        if (! readPageFromFile)
        {
            this.frontPage();
            this.TarkasteleKoulutus("tusohjel", "koulutusohjelma");
            Assert.assertNotNull("Running TarjontaElements005 TarkasteleAmmatillinenKoulutus ei toimi."
                            , doit.textElement(driver, "Jatko-opintomahdollisuudet"));
            
            if (doit.isPresentText(driver, "JULKAISTU</span>")) 
            {
            	poista = ""; 
            }
        }
        
        String elements = "<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ poista
        		+ ".*<span class=\"v-button-caption\">Lisää rinnakkainen toteutus</span>"
        		+ ".*<div class=\"v-label v-label-h1 h1\" style=\"width: 1162px;\">"
        		+ ".*<div class=\"v-captiontext\">suomi</div>"
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">Koulutuksen perustiedot</div>"
        		+ ".*<div class=\"v-label\" style=\"width: 2..px;\">( Tallennettu"
        		+ ".*<span class=\"v-button-caption\">muokkaa</span>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Organisaatio</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
				+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutus / tutkinto</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
				+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutusohjelma</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
				+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutusaste</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
				+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
				+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutusala</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
				+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Opintoala</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Tutkintonimike</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Opintojen laajuus</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutuslaji</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Pohjakoulutusvaatimus</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutuksen alkamispäivä</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Suunniteltu kesto</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Opetuskieli / -kielet</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Opetusmuoto</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Linkki opetussuunnitelmaan</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutuksen yhteyshenkilö</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">Koulutuksen kuvailevat tiedot</div>"
        		+ ".*<span class=\"v-button-caption\">muokkaa</span>"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Tutkinnon koulutukselliset ja ammatilliset tavoitteet</div>"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutuksen koulutukselliset ja ammatilliset tavoitteet</div>"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutusohjelman valinta</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutuksen sisältö</div>"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Koulutuksen rakenne</div>"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
//        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Kansainvälistyminen</div>"
//        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
//        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Sijoittuminen työelämään</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
//        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
//        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Ammattinimikkeet</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">Jatko-opintomahdollisuudet</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">Hakuko"
        		+ ".*<span class=\"v-button-caption\">Luo uusi hakukohde</span>"
//        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:" // ylimaarainen debuggia varten
//        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">" // ylimaarainen debuggia varten
        		+ ".*25KPL<div class=\"v-label\" style=\"width: 889px;\">"
        		+ ".*25KPL<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: 223px;\">"
//        		+ ".*10KPL<span class=\"v-button-caption\">" // hakukohteita on lopussa vaihteleva maara
//        		+ ".*42KPLheight: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:" // vaihtuu
		;


        doit.skipLoading(readPageFromFile);
        Assert.assertTrue("Running TarjontaElements005 TarkasteleAmmatillinenKoulutus ei toimi."
        		, doit.checkElements(driver, elements, false));
        doit.echo("Running TarjontaElements005 TarkasteleAmmatillinenKoulutus OK");
	}

	// muokkaaAmmatillinenKoulutusKoulutuksenPerustiedot
	@Test
	public void testMuokkaaAmmatillinenKoulutuksenPerustiedot() throws Exception {
        if (! readPageFromFile)
        {
            this.frontPage();
            this.TarkasteleKoulutus("tusohjel", "koulutusohjelma");
            driver.findElement(By.xpath("//*[text()='muokkaa']")).click(); // click Muokkaa(1)
            Assert.assertNotNull("Running TarjontaElements006 MuokkaaAmmatillinenKoulutuksenPerustiedot ei toimi."
                            , doit.textElement(driver, "Tehtävänimike"));
        }
        
        String elements = "<div class=\"v-label v-label-light light v-label-undef-w\">Olet "
        		+ ".*<div class=\"v-captiontext\">Koulutuksen perustiedot</div>"
        		+ ".*<div class=\"v-captiontext\">Koulutuksen kuvailevat tiedot</div>"
        		+ ".*<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ ".*<span class=\"v-button-caption\">Tallenna luonnoksena</span>"
        		+ ".*<span class=\"v-button-caption\">Tallenna valmiina</span>"
        		+ ".*<span class=\"v-button-caption\">Jatka</span>"
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">Koulutuksen perustiedot</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<button type=\"button\" class=\"v-datefield-button\" tabindex=\"-2\"></button>"
        		+ ".*v-required-field-indicator"
        		+ ".*v-required-field-indicator"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*v-required-field-indicator"
        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\""
        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\""
        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\""
        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\""
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutus tai tutkinto</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutusohjelma</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutusaste</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 736px;\">Ammatillinen koulutus</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Opintoala</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 736px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutusala</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 736px;\">"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Opintojen laajuusyksikkö</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 736px;\">opintoviikko</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Opintojen laajuus</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 736px;\">120</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Tutkintonimike</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label\" style=\"width: 736px;\">"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutuksen rakenne</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Tutkinnon koulutukselliset ja ammatilliset tavoitteet</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Jatko-opintomahdollisuudet</div>"
        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutusohjelman koulutukselliset ja ammatilliset tavoitteet</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Opetuskieli</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*v-required-field-indicator"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutuksen alkamispäivä</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Suunniteltu kesto</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutuslaji</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
				+ ".*v-required-field-indicator"
        		+ ".*<div class=\"v-label v-label-undef-w\">Opetusmuoto</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
				+ ".*v-required-field-indicator"
        		+ ".*<div class=\"v-label v-label-undef-w\">Linkki opetussuunnitelmaan</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Yhteyshenkilö</div>"
        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\""
        		+ ".*<span class=\"v-button-caption\">Tyhjennä tiedot</span>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Tehtävänimike</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Sähköposti</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Puhelin</div>"
        		+ ".*<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ ".*<span class=\"v-button-caption\">Tallenna luonnoksena</span>"
        		+ ".*<span class=\"v-button-caption\">Tallenna valmiina</span>"
        		+ ".*<span class=\"v-button-caption\">Jatka</span>"
//        		+ ".*<div class=\"v-label v-label-undef-w\">" // debug rivi
//        		+ ".*<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"  // debug rivi
//        		+ ".*<div class=\"v-filterselect-button\"></div>" // debug rivi
//        		+ ".*<div class=\"v-required-field-indicator\">*</div>" // debug rivi
//        		+ ".*v-required-field-indicator" // debug rivi
//        		+ ".*<input type=\"text\" class=\"v-filterselect-input" // debug rivi
//        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\"" // debug rivi
        		+ ".*23KPL<div class=\"v-label v-label-undef-w\">"
        		+ ".*14KPL<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
        		+ ".*9KPL<span class=\"v-button-caption\">"
        		+ ".*6KPLv-required-field-indicator"
        		+ ".*5KPLv-textfield v-textfield-prompt"
        		+ ".*6KPL<input type=\"text\" class=\"v-filterselect-input"
        		+ ".*6KPL<div class=\"v-filterselect-button\"></div>"
        		+ ".*1KPLv-datefield-textfield"
                ;

        doit.skipLoading(readPageFromFile);
        Assert.assertTrue("Running TarjontaElements006 MuokkaaAmmatillinenKoulutuksenPerustiedot ei toimi."
        		, doit.checkElements(driver, elements, false));
        doit.echo("Running TarjontaElements006 MuokkaaAmmatillinenKoulutuksenPerustiedot OK");
	}
	// muokkaaAmmatillinenKoulutuksenKuvailevatTiedot
	@Test
	public void testMuokkaaAmmatillinenKoulutuksenKuvailevatTiedot() throws Exception {
        if (! readPageFromFile)
        {
                this.frontPage();
                this.TarkasteleKoulutus("tusohjel", "koulutusohjelma");
                driver.findElement(By.xpath("(//*[text()='muokkaa'])[2]")).click(); // click Muokkaa(2)
                Assert.assertNotNull("Running TarjontaElements007 MuokkaaAmmatillinenKoulutuksenKuvailevatTiedot ei toimi."
                                , doit.textElement(driver, "miten valinta koulutusohjelmiin on toteutettu"));
        }
        
        String elements = "<div class=\"v-label v-label-light light v-label-undef-w\">Olet muokkaamassa"
        		+ ".*<div class=\"v-captiontext\">Koulutuksen perustiedot</div>"
        		+ ".*<div class=\"v-captiontext\">Koulutuksen kuvailevat tiedot</div>"
        		+ ".*<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ ".*<span class=\"v-button-caption\">Tallenna luonnoksena</span>"
        		+ ".*<span class=\"v-button-caption\">Jatka</span>"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: 1122px;\">Ammattinimikkeet</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 1122px;\">Valitse valikosta ne ammattinimikkeet, joissa koulutuksesta valmistuva yleensä toimii.</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 120px;\""
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-caption\" style=\"width: 18px;\"><img alt=\"\" class=\"v-icon\" style=\"\" src=\"/tarjonta-app/VAADIN/themes/oph/../../themes/oph/img/icon-add-black.png\""
        		+ ".*<div class=\"v-captiontext\">suomi</div>"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: 1084px;\">Koulutuksen kuvailevat tiedot</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 1084px;\">Tarjonnan kentistä osa on pakollisia"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: 1084px;\">Koulutuksen sisältö</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 1084px;\">Lyhyt kuvaus"
        		+ ".*tinyMCE.getInstanceById"
        		+ ".*title=\"Rich Text Area"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: 1084px;\">Sijoittuminen työelämään</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 1084px;\">Tässä kentässä kuvataan, millaisiin tehtäviin ja työpaikkoihin koulutuksesta valmistutaan"
        		+ ".*tinyMCE.getInstanceById"
        		+ ".*title=\"Rich Text Area"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: 1084px;\">Kansainvälistyminen</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 1084px;\">Kirjoitetaan jos kansainvälisyyys on keskeinen osa koulutusta"
        		+ ".*tinyMCE.getInstanceById"
        		+ ".*title=\"Rich Text Area"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: 1084px;\">Yhteistyö muoden toimijoiden kanssa</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 1084px;\">Tässä voidaan kertoa työelämäyhteistyöstä, työssäoppimisesta, oppilaitosten välisestä yhteistyöstä opintopolkujen toteuttamisessa tms"
        		+ ".*tinyMCE.getInstanceById"
        		+ ".*title=\"Rich Text Area"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: 1084px;\">Koulutusohjelman valinta</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: 1084px;\">Kuvaus siitä, miten valinta koulutusohjelmiin on toteutettu"
        		+ ".*tinyMCE.getInstanceById"
        		+ ".*title=\"Rich Text Area"
        		+ ".*<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ ".*<span class=\"v-button-caption\">Tallenna luonnoksena</span>"
        		+ ".*<span class=\"v-button-caption\">Jatka</span>"
		;

        doit.skipLoading(readPageFromFile);
        Assert.assertTrue("Running TarjontaElements007 MuokkaaAmmatillinenKoulutuksenKuvailevatTiedot ei toimi."
        		, doit.checkElements(driver, elements, false));
        doit.echo("Running TarjontaElements007 MuokkaaAmmatillinenKoulutuksenKuvailevatTiedot OK");
	}

    // TODO here

	//@Test
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
        doit.tauko(1);

        // KOULUTUKSET JA HAKUKOHTEET
        WebElement espoo = driver.findElement(By.xpath("//span[contains(text(), 'Espoon kaupunki')]"));
        t01 = doit.millis();
        espoo.click();
        Assert.assertNotNull("Running TarjontaHakukohteetSavu003 Hae KOULUTUKSET JA HAKUKOHTEET ei toimi.", doit.textElement(driver, "Koulutukset ("));
        t01 = doit.millisDiff(t01);
        Assert.assertNotNull("Running TarjontaHakukohteetSavu003 Hae KOULUTUKSET JA HAKUKOHTEET ei toimi.", doit.textElement(driver, "Hakukohteet ("));
        doit.echo("Running TarjontaHakukohteetSavu003 Hae KOULUTUKSET JA HAKUKOHTEET OK");
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
        doit.echo("Running TarjontaHakukohteetSavu004 LUO UUSI HAKUKOHDE OK");
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
        	doit.tauko(1);
        	t01 = doit.millis();
        	doit.textClick(driver, "Tarkastele");
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU ei toimi."
        			, doit.textElement(driver, "uusi koulutus"));
        	t01 = doit.millisDiff(t01);
        	doit.echo("Running TarjontaHakukohteetSavu005 HAKUKOHTEEN TARKASTELU OK");
        	doit.tauko(1);

        	// HAKUKOHTEEN MUOKKAUS
        	t01 = doit.millis();
        	doit.textClick(driver, "muokkaa");
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu006 HAKUKOHTEEN MUOKKAUS ei toimi."
        			, doit.textElement(driver, "tietoja hakemisesta"));
        	t01 = doit.millisDiff(t01);
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu006 HAKUKOHTEEN MUOKKAUS ei toimi."
        			, doit.textElement(driver, "Tallenna valmiina"));
        	doit.echo("Running TarjontaHakukohteetSavu006 HAKUKOHTEEN MUOKKAUS OK");
        	doit.tauko(1);

        	// HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot)
        	t01 = doit.millis();
        	doit.textClick(driver, "Liitteiden tiedot");
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu007 HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) ei toimi."
        			, doit.textElement(driver, "Toimitusosoite"));
        	t01 = doit.millisDiff(t01);
        	doit.echo("Running TarjontaHakukohteetSavu007 HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) OK");
        	doit.tauko(1);

        	// HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite)
        	t01 = doit.millis();
        	doit.textClick(driver, "uusi liite");
        	Assert.assertNotNull("Running TarjontaHakukohteetSavu007b HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite) ei toimi."
        			, doit.textElement(driver, "Voidaan toimittaa my"));
        	t01 = doit.millisDiff(t01);
        	doit.echo("Running TarjontaHakukohteetSavu007b HAKUKOHTEEN MUOKKAUS (Lisaa uusi liite) OK");
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
        		String paasykoeCheckBoxId = doit.getGwtIdBeforeText(driver, "sykoe");
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
        	doit.echo("Running TarjontaHakukohteetSavu009 HAKUKOHTEEN POISTO OK");
        }
        else
        {
        	doit.echo("Running TarjontaHakukohteetSavu HAKUKOHTEIDEN TESTAUS SIVUUTETTIIN");
        }
        
        doit.tauko(1);
        doit.echo("Running TarjontaHakukohteetSavu END OK");
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
