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

public class TestTarjontaKoulutusTilat {
    private static WebDriver driver = null;
    private static Boolean driverQuit = false;
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
            if (driver == null || driverQuit) { driver = new FirefoxDriver(firefoxProfile); driverQuit = false; }
            baseUrl = SVTUtils.prop.getProperty("testaus-selenium.oph-url");
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    		if (SVTUtils.prop.getProperty("testaus-selenium.luokka").equals("true")) { reppu = "luokka"; }
    		if (SVTUtils.prop.getProperty("testaus-selenium.qa").equals("true")) { reppu = "qa"; }
    }

    //    Luo Lukio koulutus
    @Test public void testLuoKoulutus_TILA101() throws Exception { luoLukioKoulutus("2023", "Luonnos"); }
    @Test public void testLuoKoulutus_TILA102() throws Exception { luoLukioKoulutus("2013", "Luonnos"); }
    @Test public void testLuoKoulutus_TILA103() throws Exception { luoLukioKoulutus("2012", "Luonnos"); }
    @Test public void testLuoKoulutus_TILA104() throws Exception { luoLukioKoulutus("2023", "Valmis"); }
    @Test public void testLuoKoulutus_TILA105() throws Exception { luoLukioKoulutus("2013", "Valmis"); }
    @Test public void testLuoKoulutus_TILA106() throws Exception { luoLukioKoulutus("2012", "Valmis"); }
    @Test public void testLuoKoulutus_TILA107() throws Exception { luoLukioKoulutus("2023", "Julkaistu"); }
    @Test public void testLuoKoulutus_TILA108() throws Exception { luoLukioKoulutus("2013", "Julkaistu"); }
    @Test public void testLuoKoulutus_TILA109() throws Exception { luoLukioKoulutus("2012", "Julkaistu"); }
    @Test public void testLuoKoulutus_TILA110() throws Exception { luoLukioKoulutus("2023", "Peruutettu"); }
    @Test public void testLuoKoulutus_TILA111() throws Exception { luoLukioKoulutus("2013", "Peruutettu"); }
    @Test public void testLuoKoulutus_TILA112() throws Exception { luoLukioKoulutus("2012", "Peruutettu"); }

    ///////////////////////////////////////////////////////////////////////////////////
    
    @Test public void testHaeKoulutus_TILA121() throws Exception { haeKoulutus("Luonnos"  , true, false, false, true, true); }
    @Test public void testHaeKoulutus_TILA122() throws Exception { haeKoulutus("Valmis"   , true, true, false, true, true); }
    @Test public void testHaeKoulutus_TILA123() throws Exception { haeKoulutus("Julkaistu", true, false, true, false, true); }
    @Test public void testHaeKoulutus_TILA124() throws Exception { haeKoulutus("Peruttu"  , true, true, false, false, true); 
    driver.quit();
    driverQuit = true;
    }

    public void haeKoulutus(String tila
    		, Boolean muokkaa, Boolean julkaise, Boolean peruuta, Boolean poista, Boolean valmiina) throws Exception {
    	doit.virkailijanPalvelut(driver, baseUrl);
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.echo("Running haeKoulutus ...");
    	doit.haePalvelunTarjoaja(driver, "kerttulin", "Kerttulin lukio");
    	doit.textClick(driver, "Kerttulin lukio");
		Assert.assertNotNull("Running Muokkaa ei toimi.", doit.textElement(driver, "Koulutukset ("));
    	doit.tauko(1);
    	doit.haeKoulutuksia(driver, tila, "ICT");
    	// LOOPATAAN LOYDETTYJEN MENUT LAPI JA TEHDAAN TARKISTUS
    	doit.triangleClickFirstTriangle(driver);
    	String htmlOperaatioMuokkaa = "<span class=\"v-menubar-menuitem-caption\">Muokkaa</span>";
    	String[] gwtidList = doit.getGwtIdList(driver);
    	for (int i = gwtidList.length - 3; i < gwtidList.length; i++) {
    		doit.textClick(driver, "Koulutuksen alkamisvuosi");
    		doit.tauko(1);
			String gwtId = gwtidList[i];
			doit.echo("gwtId=" + gwtId + " / " + gwtidList[gwtidList.length-1]);
	    	doit.menuWakeTargetGwt(driver, htmlOperaatioMuokkaa, gwtId);
	    	doit.tauko(1);
	    	if (muokkaa)
	    	{
	    		Assert.assertNotNull("Running Muokkaa ei toimi.", doit.textElement(driver, "Muokkaa"));
	    	}
	    	else
	    	{
	    		Assert.assertFalse("Running Muokkaa ei toimi.", doit.isPresentText(driver, "Muokkaa"));
	    	}
	    	if (julkaise)
	    	{
	    		Assert.assertNotNull("Running Julkaise ei toimi.", doit.textElement(driver, "Julkaise"));
	    	}
	    	else
	    	{
	    		Assert.assertFalse("Running Julkaise ei toimi.", doit.isPresentText(driver, "Julkaise"));
	    	}
	    	if (peruuta)
	    	{
	    		Assert.assertNotNull("Running Peruuta ei toimi.", doit.textElement(driver, "Peruuta koulutus"));
	    	}
	    	else
	    	{
	    		Assert.assertFalse("Running Peruuta ei toimi.", doit.isPresentText(driver, "Peruuta koulutus"));
	    	}
	    	if (poista)
	    	{
	    		Assert.assertTrue("Running Poista ei toimi."
	    				, doit.isPresentText(driver, "<span class=\"v-menubar-menuitem-caption\">Poista</span>"));
	    	}
	    	else
	    	{
	    		Assert.assertFalse("Running Poista ei toimi."
	    				, doit.isPresentText(driver, "<span class=\"v-menubar-menuitem-caption\">Poista</span>"));
	    	}
		}
		doit.textClick(driver, "Tarkastele");
		Assert.assertNotNull("Running Muokkaa ei toimi.", doit.textElement(driver, "muiden toimijoiden kanssa"));
		Assert.assertNotNull("Running Poista ei toimi.", doit.textElement(driver, "Poista"));
		doit.tauko(1);
		doit.textClick(driver, "muokkaa");
		Assert.assertNotNull("Running Muokkaa ei toimi.", doit.textElement(driver, "Puhelinnumero"));
		doit.tauko(1);
    	Assert.assertTrue("Running HaeHaku Valmiina ei toimi.", doit.textElement(driver, "Tallenna valmiina").isEnabled());
		Assert.assertNotNull("Running HaeHaku Tila ei toimi.", doit.textElement(driver, tila.toUpperCase()));
    	if (tila.equals("Luonnos"))
    	{
	    	Assert.assertTrue("Running Luonnoksena ei toimi.", doit.textElement(driver, "Tallenna luonnoksena").isEnabled());
    	}
    	else
    	{
	    	Assert.assertNotNull("Running Luonnoksena ei toimi."
	    			, driver.findElement(By.xpath("//div[contains(@class,'v-disabled') and span/span[text()='Tallenna luonnoksena']]")));
    	}
    	
    	doit.echo("Running haeKoulutus OK");
    	doit.tauko(1);
    }
    
    public void luoLukioKoulutus(String vuosi, String tila) throws Exception {
    	doit.virkailijanPalvelut(driver, baseUrl);
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.echo("Running luoLukioKoulutus ...");
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
        		, doit.textElement(driver, "Puhelinnumero"));
		doit.footerTest(driver, "Running Luo uusi lukiokoulutus + jatka footer ei toimi.", true);
        doit.tauko(1);
        //
        doit.sendInput(driver, "Koulutus / tutkinto", "Ylioppilastutkinto");
        doit.popupItemClick(driver, "Ylioppilastutkinto");
        doit.sendInput(driver, "Lukiolinja", "Lukion ICT-linja");
        doit.popupItemClick(driver, "Lukion ICT-linja");
        doit.tauko(1);
        
        Assert.assertNotNull("Running Opintoala ei toimi.", doit.textElement(driver, "Lukiokoulutus"));
        Assert.assertNotNull("Running Tutkintonimike ei toimi.", doit.textElement(driver, "Ylioppilas"));
        Assert.assertNotNull("Running Opintojen Koulutuslaji ei toimi.", doit.textElement(driver, "Nuorten koulutus"));
        doit.tauko(1);

        doit.sendInput(driver, "Koulutuksen alkamisp", "16.08." + vuosi);
        doit.sendInput(driver, "Suunniteltu kesto", "3");
        doit.sendInputSelect(driver, "Opetusmuoto", "Oppisopimuskoulutus\n");
//        doit.popupItemClick(driver, "Oppisopimuskoulutus");
        doit.sendInputPlusX(driver, "Suunniteltu kesto", "kuukautta", 150); // Valitse aikayksikko
        doit.popupItemClick(driver, "kuukautta");

        doit.sendInput(driver, "Opetuskieli", "suomi");
        doit.tauko(2);
        doit.popupItemClick(driver, "suomi");
        
        if (tila.equals("Luonnos"))
        {
            doit.textClickLast(driver, "Tallenna luonnoksena");
        }
        else
        {
            doit.textClickLast(driver, "Tallenna valmiina");
        }
        Assert.assertNotNull("Running Tallenna ei toimi.", doit.textElement(driver, "Tallennus onnistui"));
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        doit.tauko(1);
        doit.refreshTarjontaEtusivu(driver);
        doit.tauko(1);
                    	
        if (tila.equals("Julkaistu") || tila.equals("Peruutettu"))
        {
            Assert.assertNotNull("Running valikot ei toimi.", doit.textElement(driver, "Koulutuksen alkamisvuosi"));
            doit.tauko(1);

            if (! doit.isPresentText(driver, "ICT")) { doit.getTriangleForFirstItem(driver).click(); doit.tauko(1); }
            doit.menuOperaatioLastMenu(driver, "Julkaise");
            Assert.assertNotNull("Running Julkaise ei toimi.", doit.textElement(driver, "Toiminto onnistui"));
        }
        if (tila.equals("Peruutettu"))
        {
            if (! doit.isPresentText(driver, "ICT")) { doit.getTriangleForFirstItem(driver).click(); doit.tauko(1); }
            doit.menuOperaatioLastMenu(driver, "Peruuta koulutus");
            Assert.assertNotNull("Running Peruuta ei toimi.", doit.textElement(driver, "Olet peruuttamassa koulutusta"));
            doit.tauko(1);
            doit.textClick(driver, "Kyllä");
            Assert.assertNotNull("Running Peruuta ei toimi.", doit.textElement(driver, "Toiminto onnistui"));
        }
    	doit.echo("Running luoLukioKoulutus OK");
        doit.tauko(1);
    }

    @After
    public void tearDown() throws Exception {
//        driver.quit();
//        driverQuit = true;
    	doit.quit(driver);
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
        	fail(verificationErrorString);
        }
    }
}
