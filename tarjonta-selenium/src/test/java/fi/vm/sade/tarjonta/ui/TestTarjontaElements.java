package fi.vm.sade.tarjonta.ui;

import static org.junit.Assert.*;

import java.io.IOException;
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
    private static Kattavuus TarjontaElementitTekstit = new Kattavuus();
    private static Kattavuus TarjontaElementitSelaimet = new Kattavuus();
    private static String selain = "";

    private Boolean readPageFromFile = false;
    
    // html
    private String htmlAbsoluteLeft18 = "<div style=\"height: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:";
    private String htmlAbsoluteLeft = "px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:";

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
        if (first) { selain = doit.palvelimenVersio(driver, baseUrl); }
    }
    
    public void frontPage() throws Exception
    {
    	if (readPageFromFile) { return; }
    	if (first) 
    	{ 
    		doit.echo("Running ================================================================="); 
            doit.messagesPropertiesInit();
            TarjontaElementitTekstit.alustaKattavuusKohde("TarjontaElementitTekstit");
            doit.alustaSelaimet(TarjontaElementitSelaimet, "TarjontaElementitSelaimet");
            TarjontaElementitTekstit.KattavuusRaporttiHiljaa = true;
    	}
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
        doit.messagesPropertiesCoverage(driver, TarjontaElementitTekstit);
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
        		+ ".*<span class=\"v-button-caption\">Tyhjennä</span>"
        		+ ".*<span class=\"v-button-caption\">Hae</span>"
        		+ ".*<div tabindex=\"0\" class=\"v-button v-button-vertical-collapse vertical-collapse\" role=\"button\">"
        		+ ".*v-label-undef-w\">OPH</div>"
        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-search-box search-box\" style=\"width:"
        		+ ".*<div class=\"v-label v-label-undef-w\">Tila</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutuksen alkamisvuosi</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutuksen alkamiskausi</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<span class=\"v-button-caption\">Tyhjennä</span>"
				+ ".*<span class=\"v-button-caption\">Hae</span>"
        		+ ".*<span class=\"v-button-caption\">Tulosta raportti</span>"
        		+ ".*<div class=\"v-captiontext\">Koulutukset</div>"
        		+ ".*<div class=\"v-captiontext\">Hakukohteet</div>"
        		+ ".*<span class=\"v-button-caption\">Siirrä tai kopioi</span>"
        		+ ".*<span class=\"v-button-caption\">Luo uusi hakukohde</span>"
        		+ ".*<span class=\"v-button-caption\">Luo uusi koulutus</span>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
//        		+ ".*<input type=\"text\" class=\"v" // ylimaarainen debuggia varten
        		+ ".*<div class=\"v-filterselect-button\"></div>"
//        		+ ".*<div class=\"v-filterselect-button\"></div>" // ylimaarainen debuggia varten
        		+ ".*<input type=\"checkbox\" value=\"on\" id=\"gwt-uid-3\" tabindex=\"0\""
        		+ ".*<label for=\"gwt-uid-3\">Valitse kaikki</label>"
        		+ ".*2KPL<input type=\"text\" class=\"v-textfield v-textfield-search-box search-box"
        		+ ".*8KPL<input type=\"text\" class=\"v"
        		+ ".*2KPL<div class=\"v-captiontext\""
        		+ ".*10KPL<span class=\"v-button-caption\">"
        		+ ".*6KPL<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
        		+ ".*6KPL<div class=\"v-filterselect-button\"></div>"
        		+ ".*3KPL<input type=\"checkbox\""
        		+ ".*3KPL<label for=\"gwt-uid"
        		;

        doit.skipLoading(readPageFromFile);
        Assert.assertTrue("Running TarjontaElements001 EtuSivu ei toimi."
                        , doit.checkElements(driver, elements, false));
        doit.echo("Running TarjontaElements001 EtuSivu OK");
        doit.messagesPropertiesCoverage(driver, TarjontaElementitTekstit);
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
    	WebElement link = driver.findElement(By.className("v-button-link-row"));
        Assert.assertNotNull("Running TarjontaElements000 koulutushaku ei toimi.", link);
        link.click();
        Assert.assertNotNull("Running TarjontaElements000 koulutushaku ei toimi."
        		, doit.textElement(driver, "Koulutusala"));
	}
	
	// tarkasteleLukioKoulutus
	@Test
	public void testTarkasteleLukioKoulutus() throws Exception {
        if (! readPageFromFile)
        {
        	this.frontPage();
        	this.TarkasteleKoulutus("ylioppilastutkint", "Lukio");
        }
        
		String vLabelTextAlignRight = ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: ...px;";
		String divClassvLabelStyleWidthPx = "<div class=\"v-label\" style=\"width: ...px;\"";
//		vLabelTextAlignRight = "";
//		divClassvLabelStyleWidthPx = "";
//		htmlAbsoluteLeft = "";
        String elements = "<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ ".*<span class=\"v-button-caption\">Poista</span>"
        		+ ".*<div class=\"v-label v-label-h1 h1\" style=\"width: 1...px;\">"
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">Koulutuksen perustiedot</div>"
        		+ ".*<div class=\"v-label\" style=\"width: 2..px;\">( Tallennettu"
        		+ ".*<span class=\"v-button-caption\">muokkaa</span>"
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Organisaatio</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Koulutus</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + divClassvLabelStyleWidthPx + ">Ylioppilastutkinto"
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Lukiolinja</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + divClassvLabelStyleWidthPx
				+ ".*" + htmlAbsoluteLeft
				+ vLabelTextAlignRight 
				+ ".*" + htmlAbsoluteLeft
				+ ".*" + htmlAbsoluteLeft
/*
                + vLabelTextAlignRight + ".*>Koulutusaste</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
				+ vLabelTextAlignRight 
        		+ ".*" + htmlAbsoluteLeft
//        		+ ".*" + htmlAbsoluteLeft
//        		+ ".*" + htmlAbsoluteLeft
 */
        		+ ".*" + divClassvLabelStyleWidthPx + ">Yleissivistävä koulutus</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Opintoala</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + divClassvLabelStyleWidthPx + ">Lukiokoulutus</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Tutikintonimike</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + divClassvLabelStyleWidthPx + ">Ylioppilas</div>"
        		+ ".*" + htmlAbsoluteLeft
//        		+ ".*<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: ...px;\">Opintojen laajuusyksikkö</div>"
//        		+ ".*" + htmlAbsoluteLeft
//        		+ ".*" + divClassvLabelStyleWidthPx
//        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Opintojen laajuus</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + divClassvLabelStyleWidthPx
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Koulutuslaji</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Pohjakoulutusvaatimus</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Koulutuksen rakenne</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Koulutuksellset tavoitteet</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Jatko-opintomahdollisuudet</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
//        		+ vLabelTextAlignRight
        		+ vLabelTextAlignRight + ".*>Opetuskieli</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + divClassvLabelStyleWidthPx
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Koulutuksen alkamispäivä</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Suunniteltu kesto</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + divClassvLabelStyleWidthPx
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Opetusmuoto</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + divClassvLabelStyleWidthPx
//        		+ ".*" + divClassvLabelStyleWidthPx // ylimaarainen debuggia varten
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Linkki opetussuunnitelmaan</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Koulutuksen yhteyshenkilö</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">Koulutuksen kuvailevat tiedot</div>"
        		+ ".*<span class=\"v-button-caption\">muokkaa</span>"
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>A1-/A2-kieli</div>"
        		+ ".*" + htmlAbsoluteLeft
//        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">englanti</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>B1-kieli</div>"
        		+ ".*" + htmlAbsoluteLeft
//        		+ ".*<div class=\"v-label\" style=\"width: 889px;\">ruotsi</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>B2-kieli</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>B3-kieli</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Muut kielet</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Lukiodiplomit</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Koulutuksen sisältö</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Kansainvälistyminen</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ vLabelTextAlignRight + ".*>Yhteistyö muiden toimijoiden kanssa</div>"
//        		+ vLabelTextAlignRight // ylimaarainen debuggia varten
        		+ ".*" + htmlAbsoluteLeft
//        		+ ".*" + htmlAbsoluteLeft // ylimaarainen debuggia varten
        		+ ".*32KPL<div class=\"v-label\" style=\"width:"  
//        		+ ".*4KPL<span class=\"v-button-caption\">" // nakyy 4 mutta sorsissa on 7
        		+ ".*31KPL<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width:" // vLabelTextAlignRight
//        		+ ".*54KPLheight: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:"
		;

        doit.skipLoading(readPageFromFile);
        Assert.assertTrue("Running TarjontaElements002 TarkasteleLukioKoulutus ei toimi."
        		, doit.checkElements(driver, elements, false));
        doit.echo("Running TarjontaElements002 TarkasteleLukioKoulutus OK");
        doit.messagesPropertiesCoverage(driver, TarjontaElementitTekstit);
	}
	
	// muokkaaLukioKoulutuksenPerustiedot
	@Test
	public void testMuokkaaLukioKoulutuksenPerustiedot() throws Exception {
        if (! readPageFromFile)
        {
                this.frontPage();
                this.TarkasteleKoulutus("ylioppilastutkint", "Lukio");
                doit.textClick(driver, "muokkaa"); // click Muokkaa(1)
                Assert.assertNotNull("Running TarjontaElements000 koulutushaku ei toimi."
                		, doit.textElement(driver, "Puhelinnumero"));
        }
        
        String elements = "<div class=\"v-label v-label-light light v-label-undef-w\">Olet "
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
//        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\""
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutus / tutkinto</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Lukiolinja</div>"
        		+ ".*" + htmlAbsoluteLeft
//        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutusaste</div>"
//        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">Lukiokoulutus</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutusala</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">Yleissivistävä koulutus</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">Opintoala</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">Lukiokoulutus</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">Tutkintonimike</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">Ylioppilas</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">Opintojen laajuusyksikkö</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutuslaji</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">Pohjakoulutusvaatimus</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutuksen rakenne</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutukselliset tavoitteet</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">Jatko-opintomahdollisuudet</div>"
        		+ ".*" + htmlAbsoluteLeft
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
        		+ ".*<select class=\"v-select-twincol-options\" size=\"10\""
        		+ ".*<div tabindex=\"0\" class=\"v-button\" role=\"button\"><span class=\"v-button-wrap\"><span class=\"v-button-caption\">&gt;&gt;</span></span></div>"
        		+ ".*<div tabindex=\"0\" class=\"v-button\" role=\"button\"><span class=\"v-button-wrap\"><span class=\"v-button-caption\">&lt;&lt;</span></span></div>"
        		+ ".*<select class=\"v-select-twincol-selections\" size=\"10\""
        		+ ".*<div class=\"v-required-field-indicator\">*</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Linkki opetussuunnitemaan</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Yhteyshenkilö</div>"
//        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\""
        		+ ".*<span class=\"v-button-caption\">Tyhjennä</span>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Tehtävänimike</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Sähköpostiosoite</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Puhelinnumero</div>"
        		+ ".*<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ ".*<span class=\"v-button-caption\">Tallenna luonnoksena</span>"
        		+ ".*<span class=\"v-button-caption\">Tallenna valmiina</span>"
        		+ ".*<span class=\"v-button-caption\">Jatka</span>"
//        		+ ".*<div class=\"v-label v-label-undef-w\">" // debug rivi
//        		+ ".*" + htmlAbsoluteLeft  // debug rivi
//        		+ ".*<div class=\"v-filterselect-button\"></div>" // debug rivi
//        		+ ".*<input type=\"text\" class=\"v-filterselect-input" // debug rivi
//        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-required\">" // debug rivi
//        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\"" // debug rivi
        		+ ".*23KPL<div class=\"v-label v-label-undef-w\">"
//        		+ ".*18KPL" + htmlAbsoluteLeft18 // vaihtelee
        		+ ".*11KPL<span class=\"v-button-caption\">"
        		+ ".*5KPLv-required-field-indicator"
//        		+ ".*3KPLv-textfield v-textfield-prompt" // vaihtelee
        		+ ".*4KPL<input type=\"text\" class=\"v-filterselect-input"
        		+ ".*4KPL<div class=\"v-filterselect-button\"></div>"
//        		+ ".*1KPL<div class=\"v-label v-label-undef-w\">Koulutusaste</div>"
        		+ ".*1KPLv-datefield-textfield"
		;

        doit.skipLoading(readPageFromFile);
        Assert.assertTrue("Running TarjontaElements003 MuokkaaLukioKoulutuksenPerustiedot ei toimi."
        		, doit.checkElements(driver, elements, false));
        doit.echo("Running TarjontaElements003 MuokkaaLukioKoulutuksenPerustiedot OK");
        doit.messagesPropertiesCoverage(driver, TarjontaElementitTekstit);
	}

	// muokkaaLukioKoulutusKuvailevatTiedot
	@Test
	public void testMuokkaaLukioKoulutusKuvailevatTiedot() throws Exception {
        if (! readPageFromFile)
        {
                this.frontPage();
                this.TarkasteleKoulutus("ylioppilastutkint", "Lukio");                
                driver.findElement(By.xpath("(//*[text()='muokkaa'])[2]")).click(); // click Muokkaa(2)
                Assert.assertNotNull("Running TarjontaElements000 koulutushaku ei toimi."
                                , doit.textElement(driver, "Yritysten nimien mainitsemista"));
        }
        
        String elements = "<div class=\"v-label v-label-light light v-label-undef-w\">Olet "
        		+ ".*<div class=\"v-captiontext\">Koulutuksen perustiedot</div>"
        		+ ".*<div class=\"v-captiontext\">Koulutuksen kuvailevat tiedot</div>"
        		+ ".*<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ ".*<span class=\"v-button-caption\">Tallenna luonnoksena</span>"
        		+ ".*<span class=\"v-button-caption\">Tallenna valmiina</span>"
        		+ ".*<span class=\"v-button-caption\">Jatka</span>"
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">Koulutuksen kuvailevat tiedot</div>"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: " 
        		+ ".*px;\">Kielivalikoima</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: " 
        		+ ".*px;\">Lukion kielet listataan tässä laajuuksien mukaan. Jos esim. englantia voi opiskella sekä A1- että B3-kielinä, merkitään se molempien laajuuksien kohdalle.</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">A1-/A2-kieli</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 1..px;\""
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: ...px;\">Peruskoulun vuosiluokilla 1-6 alkanut yhteinen tai valinnainen kieli</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">B1-kieli</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 1..px;\""
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: ...px;\">Peruskoulun vuosiluokilla 7.-9. aloitettu yhteinen toinen kotimainen kieli tai englanti</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">B2-kieli</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 1..px;\""
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: ...px;\">Peruskoulun vuosiluokilla 7.-9. aloitettu valinnainen kieli</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">B3-kieli</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 1..px;\""
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: ...px;\">Lukiossa aloitettava valinnainen vieras kieli</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Muut kielet</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 1..px;\""
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: ...px;\">Lukiossa aloitettava valinnainen vieras kieli, jonka laajuus on suuppeampi kuin B2/B3</div>"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: " 
        		+ ".*px;\">Lukiodiplomit</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: " 
        		+ ".*px;\">Listaus niistä oppiaineista, joissa on mahdollista suorittaa lukiodiplomi koulutuksen yhteydessä. Lukiodiplomin suorittamisen järjestäminen on lukiolle vapaaehtoista.</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 1..px;\""
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<img alt=\"\" class=\"v-icon\" style=\"\" src=\"/tarjonta-app/VAADIN/themes/tarjonta/../../themes/oph/img/icon-add-black.png\""
        		+ ".*<div class=\"v-captiontext\">"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: " 
        		+ ".*px;\">Koulutuksen sisältö</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: " 
        		+ ".*px;\">Kuvaus lukiolinjan keskeisistä sisällöistä, painotuksista ja toteutustavoista. Henkilökohtaisen opiskelusuunnitelman laatimisesta on hyvä mainita. Verkkotekstissä on hyvä käyttä lyhyitä lauseita ja kappaleita ja tarvittaessa listoja. Vapaa teksti 1500 merkkiä.</div>"
        		+ ".*tinyMCE.getInstanceById"
        		+ ".*title=\"Rich Text Area"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: " 
        		+ ".*px;\">Kansainvälistyminen</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: " 
        		+ ".*px;\">Kirjoitetaan jos kansainvälisyyys on keskeinen osa koulutusta. Esim. yhteistyö tai ulkomailla opiskelu / vaihto-opiskelumahdollisuudet. Valitse otsikot itse. Vapaa teksti 1500 merkkiä.</div>"
        		+ ".*tinyMCE.getInstanceById"
        		+ ".*title=\"Rich Text Area"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: " 
        		+ ".*px;\">Yhteistyö muiden toimijoiden kanssa</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: " 
        		+ ".*px;\">Tässä voidaan kertoa työelämäyhteistyöstä, oppilaitosten välisestä yhteistyöstä opintopolkujen toteuttamisessa tms. Yritysten nimien mainitsemista on kuitenkin syytä välttää. Vapaa teksti 1500 merkkiä.</div>"
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
//        		+ ".*8KPL<span class=\"v-button-caption\">" // vaihtuva maara (x) kielia
                ;

        doit.skipLoading(readPageFromFile);
        Assert.assertTrue("Running TarjontaElements004 MuokkaaLukioKoulutusKuvailevatTiedot ei toimi."
        		, doit.checkElements(driver, elements, false));
        doit.echo("Running TarjontaElements004 MuokkaaLukioKoulutusKuvailevatTiedot OK");
        doit.messagesPropertiesCoverage(driver, TarjontaElementitTekstit);
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
            
            if (doit.isPresentText(driver, "Poista koulutuksesta")) 
            {
            	poista = ""; 
            }
        }
        
        String textAlignRight = "<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width: ...px;";
//        textAlignRight = "";
        String elements = "<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ poista
        		+ ".*<span class=\"v-button-caption\">Lisää rinnakkainen toteutus</span>"
        		+ ".*<div class=\"v-label v-label-h1 h1\" style=\"width: 1...px;\">"
        		+ ".*<div class=\"v-captiontext\">suomi</div>"
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">Koulutuksen perustiedot</div>"
        		+ ".*<div class=\"v-label\" style=\"width: 2..px;\">( Tallennettu"
        		+ ".*<span class=\"v-button-caption\">muokkaa</span>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Organisaatio</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
				+ ".*" + textAlignRight + ".*>Koulutus tai tutkinto</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
				+ ".*" + textAlignRight + ".*>Koulutusohjelma</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
				+ ".*" + textAlignRight + ".*>Koulutusaste</div>"
        		+ ".*" + htmlAbsoluteLeft
				+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
				+ ".*" + textAlignRight + ".*>Koulutusala</div>"
        		+ ".*" + htmlAbsoluteLeft
				+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Opintoala</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Tutkintonimike</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Opintojen laajuus</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Koulutuslaji</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Pohjakoulutusvaatimus</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Koulutuksen alkamispäivä</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Suunniteltu kesto</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Opetuskieli- / kielet</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Opetusmuoto</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Linkki opetussuunnitelmaan</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Koulutuksen yhteyshenkilö</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">Koulutuksen kuvailevat tiedot</div>"
        		+ ".*<span class=\"v-button-caption\">muokkaa</span>"
        		+ ".*" + textAlignRight + ".*>Tutkinnon koulutukselliset ja ammatilliset tavoitteet</div>"
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + textAlignRight + ".*>Koulutuksen koulutukselliset ja ammatilliset tavoitteet</div>"
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + textAlignRight + ".*>Koulutusohjelman valinta</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + textAlignRight + ".*>Koulutuksen sisältö</div>"
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + textAlignRight + ".*>Koulutuksen rakenne</div>"
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
//        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Sijoittuminen työelämään</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Kansainvälistyminen</div>"
//        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
//        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Yhteistyö muiden toimijoiden kanssa</div>"
//        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
//        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Ammattinimikkeet</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*" + textAlignRight + ".*>Jatko-opintomahdollisuudet</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*<div class=\"v-label v-label-h2 h2 v-label-undef-w\">"
        		+ ".*akuko"
        		+ ".*<span class=\"v-button-caption\">Luo uusi hakukohde</span>"
//        		+ ".*" + htmlAbsoluteLeft // ylimaarainen debuggia varten
//        		+ ".*<div class=\"v-label\" style=\"width:" // ylimaarainen debuggia varten
//        		+ ".*" + textAlignRight  // ylimaarainen debuggia varten // edelliselta sivulta nakymattomia N kpl
        		+ ".*30KPL<div class=\"v-label\" style=\"width:"
//        		+ ".*28KPL<div class=\"v-label v-label-text-align-right text-align-right\" style=\"width:" // jotain nakymattomia 3 kpl
//        		+ ".*10KPL<span class=\"v-button-caption\">" // hakukohteita on lopussa vaihteleva maara 
//        		+ ".*42KPLheight: 18px; overflow: hidden; padding-left: 0px; padding-top: 0px; position: absolute; left:" // vaihtuu
		;


        doit.skipLoading(readPageFromFile);
        Assert.assertTrue("Running TarjontaElements005 TarkasteleAmmatillinenKoulutus ei toimi."
        		, doit.checkElements(driver, elements, false));
        doit.echo("Running TarjontaElements005 TarkasteleAmmatillinenKoulutus OK");
        doit.messagesPropertiesCoverage(driver, TarjontaElementitTekstit);
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
                    , doit.textElement(driver, "Tyhjenn"));
            Assert.assertNotNull("Running TarjontaElements006 MuokkaaAmmatillinenKoulutuksenPerustiedot ei toimi."
                    , doit.textElement(driver, "Olet muokkaamassa"));
            Assert.assertNotNull("Running TarjontaElements006 MuokkaaAmmatillinenKoulutuksenPerustiedot ei toimi."
                    , doit.textElement(driver, "Tutkinnon koulutukselliset ja ammatilliset tavoitteet"));
            Assert.assertNotNull("Running TarjontaElements006 MuokkaaAmmatillinenKoulutuksenPerustiedot ei toimi."
                    , doit.textElement(driver, "Koulutuksen koulutukselliset ja ammatilliset tavoitteet"));
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
        		+ ".*" + htmlAbsoluteLeft
//        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutusaste</div>"
//        		+ ".*" + htmlAbsoluteLeft
//        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">Ammatillinen koulutus</div>"
//        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutusala</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">Opintoala</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*" + htmlAbsoluteLeft
//        		+ ".*<div class=\"v-label v-label-undef-w\">Opintojen laajuusyksikkö</div>"
//        		+ ".*" + htmlAbsoluteLeft
//        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">opintoviikko</div>"
//        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">Tutkintonimike</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">"
        		+ ".*<div class=\"v-label v-label-undef-w\">Opintojen laajuus</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label\" style=\"width: ...px;\">120 ov</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">Tutkinnon koulutukselliset ja ammatilliset tavoitteet</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutuksen koulutukselliset ja ammatilliset tavoitteet</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutuksen rakenne</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">Jatko-opintomahdollisuudet</div>"
        		+ ".*" + htmlAbsoluteLeft
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutuksen alkamispäivä</div>"
        		+ ".*<div class=\"v-label v-label-undef-w\">Suunniteltu kesto</div>"
//        		+ ".*<div class=\"v-label v-label-undef-w\">Opetuskieli</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*v-required-field-indicator"
        		+ ".*<div class=\"v-label v-label-undef-w\">Koulutuslaji</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
				+ ".*v-required-field-indicator"
        		+ ".*<div class=\"v-label v-label-undef-w\">Opetusmuoto</div>"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width:"
        		+ ".*<div class=\"v-filterselect-button\"></div>"
				+ ".*v-required-field-indicator"
        		+ ".*<div class=\"v-label v-label-undef-w\">Linkki opetussuunnitelmaan</div>"
//        		+ ".*<div class=\"v-label v-label-undef-w\">Yhteyshenkilö</div>"
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
//        		+ ".*" + htmlAbsoluteLeft  // debug rivi
//        		+ ".*<div class=\"v-filterselect-button\"></div>" // debug rivi
//        		+ ".*<div class=\"v-required-field-indicator\">*</div>" // debug rivi
//        		+ ".*v-required-field-indicator" // debug rivi
//        		+ ".*<input type=\"text\" class=\"v-filterselect-input" // debug rivi
//        		+ ".*<input type=\"text\" class=\"v-textfield v-textfield-prompt\"" // debug rivi
        		+ ".*22KPL<div class=\"v-label v-label-undef-w\">"
//        		+ ".*14KPL" + htmlAbsoluteLeft18 // rivitys vaihtelee
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
        doit.messagesPropertiesCoverage(driver, TarjontaElementitTekstit);
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
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: "
        		+ ".*px;\">Ammattinimikkeet</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: " 
        		+ ".*px;\">Valitse valikosta ne ammattinimikkeet, joissa koulutuksesta valmistuva"
        		+ ".*<input type=\"text\" class=\"v-filterselect-input\" style=\"width: 1..px;\""
        		+ ".*<div class=\"v-filterselect-button\"></div>"
        		+ ".*<div class=\"v-caption\" style=\"width: 18px;\"><img alt=\"\" class=\"v-icon\" style=\"\" src=\"/tarjonta-app/VAADIN/themes/tarjonta/../../themes/oph/img/icon-add-black.png\""
        		+ ".*<div class=\"v-captiontext\">suomi</div>"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: " 
        		+ ".*px;\">Koulutuksen kuvailevat tiedot</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: " 
        		+ ".*px;\">Tarjonnan kentistä osa on pakollisia"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: " 
        		+ ".*px;\">Koulutusohjelman valinta</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: " 
        		+ ".*px;\">Kuvaus siitä, miten valinta koulutusohjelmiin on toteutettu"
        		+ ".*tinyMCE.getInstanceById"
        		+ ".*title=\"Rich Text Area"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: " 
        		+ ".*px;\">Koulutuksen sisältö</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: " 
        		+ ".*px;\">Lyhyt kuvaus"
        		+ ".*tinyMCE.getInstanceById"
        		+ ".*title=\"Rich Text Area"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: "  // 43947
        		+ ".*px;\">Sijoittuminen työelämään</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: " 
        		+ ".*px;\">Tässä kentässä kuvataan, millaisiin tehtäviin ja työpaikkoihin koulutuksesta valmistutaan"
        		+ ".*tinyMCE.getInstanceById"
        		+ ".*title=\"Rich Text Area"
        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: " // 54096
        		+ ".*px;\">Kansainvälistyminen</div>"
        		+ ".*<div class=\"v-label v-label-light light\" style=\"width: " 
        		+ ".*px;\">Kirjoitetaan jos kansainvälisyyys on keskeinen osa koulutusta"
        		+ ".*tinyMCE.getInstanceById"
        		+ ".*title=\"Rich Text Area"
//        		+ ".*<div class=\"v-label v-label-h2 h2\" style=\"width: " // ylimaarainen debug rivi
        		+ ".*<div tabindex=\"0\" class=\"v-button v-button-small small v-button-back back\" role=\"button\">"
        		+ ".*<span class=\"v-button-caption\">Tallenna luonnoksena</span>"
        		+ ".*<span class=\"v-button-caption\">Jatka</span>"
		;

        doit.skipLoading(readPageFromFile);
        Assert.assertTrue("Running TarjontaElements007 MuokkaaAmmatillinenKoulutuksenKuvailevatTiedot ei toimi."
        		, doit.checkElements(driver, elements, false));
        doit.echo("Running TarjontaElements007 MuokkaaAmmatillinenKoulutuksenKuvailevatTiedot OK");
        doit.messagesPropertiesCoverage(driver, TarjontaElementitTekstit);
	}

	@Test
	public void testReport() throws IOException
	{
		// END
        doit.messagesPropertiesSaveElements(TarjontaElementitTekstit);
        if (selain != null && selain.length() > 0)
        {
        	TarjontaElementitSelaimet.setKattavuus(selain, Kattavuus.KATTAVUUSOK);
        	TarjontaElementitSelaimet.KattavuusRaportti();
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
