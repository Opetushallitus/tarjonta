package fi.vm.sade.tarjonta.ui;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class TestTarjontaHakuTilat {
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

    //    Luo haku
    @Test
    public void testLuoHaku_TILA001() throws Exception {
    	luoHaku(reppu + "_julkaistu_paattynyt", "2012", "16.08.2012 17:10", "30.12.2012 17:10", "julkaistu");
    }

    @Test
    public void testLuoHaku_TILA002() throws Exception {
    	luoHaku(reppu + "_julkaistu_suunnitteilla", "2023", "16.08.2023 17:10", "30.12.2023 17:10", "julkaistu");
    }

    @Test
    public void testLuoHaku_TILA003() throws Exception {
    	luoHaku(reppu + "_julkaistu_voimassa", "2013", "16.08.2013 17:10", "30.12.2013 17:10", "julkaistu");
    }

    @Test
    public void testLuoHaku_TILA004() throws Exception {
    	luoHaku(reppu + "_valmis_paattynyt", "2012", "16.08.2012 17:10", "30.12.2012 17:10", "valmis");
    }

    @Test
    public void testLuoHaku_TILA005() throws Exception {
    	luoHaku(reppu + "_valmis_suunnitteilla", "2023", "16.08.2023 17:10", "30.12.2023 17:10", "valmis");
    }

    @Test
    public void testLuoHaku_TILA006() throws Exception {
    	luoHaku(reppu + "_valmis_voimassa", "2013", "16.08.2013 17:10", "30.12.2013 17:10", "valmis");
    }

    @Test
    public void testLuoHaku_TILA007() throws Exception {
    	luoHaku(reppu + "_peruttu_paattynyt", "2012", "16.08.2012 17:10", "30.12.2012 17:10", "peruttu");
    }

    @Test
    public void testLuoHaku_TILA008() throws Exception {
    	luoHaku(reppu + "_peruttu_suunnitteilla", "2023", "16.08.2023 17:10", "30.12.2023 17:10", "peruttu");
    }

    @Test
    public void testLuoHaku_TILA009() throws Exception {
    	luoHaku(reppu + "_peruttu_voimassa", "2013", "16.08.2013 17:10", "30.12.2013 17:10", "peruttu");
    }

    @Test
    public void testLuoHaku_TILA010() throws Exception {
    	luoHaku(reppu + "_luonnos_paattynyt", "2012", "16.08.2012 17:10", "30.12.2012 17:10", "luonnos");
    }

    @Test
    public void testLuoHaku_TILA011() throws Exception {
    	luoHaku(reppu + "_luonnos_suunnitteilla", "2023", "16.08.2023 17:10", "30.12.2023 17:10", "luonnos");
    }

    @Test
    public void testLuoHaku_TILA012() throws Exception {
    	luoHaku(reppu + "_luonnos_voimassa", "2013", "16.08.2013 17:10", "30.12.2013 17:10", "luonnos");
    }

    ///////////////////////////////////////////////////////////////////////////////////
    
    @Test
    public void testHaeHaku_TILA021() throws Exception {
    	haeHaku(reppu + "_luonnos_", "Luonnos", true, false, false, true, true);
    }

    @Test
    public void testHaeHaku_TILA022() throws Exception {
    	haeHaku(reppu + "_valmis_", "Valmis", true, true, false, true, true);
    }

    @Test
    public void testHaeHaku_TILA023() throws Exception {
    	haeHaku(reppu + "_julkaistu_", "Julkaistu", true, false, true, true, true);
    }

    @Test public void testHaeHaku_TILA024() throws Exception { 
    	haeHaku(reppu + "_peruttu_", "Peruttu", true, false, false, true, true);
    	driver.quit();
        driverQuit = true;
    }

    //    Hae haku
    public void haeHaku(String hakuSana, String tila
    		, Boolean muokkaa, Boolean julkaise, Boolean peruuta, Boolean poista, Boolean valmiina) throws Exception {
    	doit.virkailijanPalvelut(driver, baseUrl);
        doit.ValikotHakujenYllapito(driver, baseUrl);
    	doit.echo("Running testHaeHaku ...");
    	this.haeHakua(tila, hakuSana);
    	// LOOPATAAN LOYDETTYJEN MENUT LAPI JA TEHDAAN TARKISTUS
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
	    		Assert.assertNotNull("Running HaeHaku Muokkaa ei toimi.", doit.textElement(driver, "Muokkaa"));
	    	}
	    	else
	    	{
	    		Assert.assertFalse("Running HaeHaku Muokkaa ei toimi.", doit.isPresentText(driver, "Muokkaa"));
	    	}
	    	if (julkaise)
	    	{
	    		Assert.assertNotNull("Running HaeHaku Julkaise ei toimi.", doit.textElement(driver, "Julkaise"));
	    	}
	    	else
	    	{
	    		Assert.assertFalse("Running HaeHaku Julkaise ei toimi.", doit.isPresentText(driver, "Julkaise"));
	    	}
	    	if (peruuta)
	    	{
	    		Assert.assertNotNull("Running HaeHaku Peruuta ei toimi.", doit.textElement(driver, "Peruuta haku"));
	    	}
	    	else
	    	{
	    		Assert.assertFalse("Running HaeHaku Peruuta ei toimi.", doit.isPresentText(driver, "Peruuta haku"));
	    	}
	    	if (poista)
	    	{
	    		Assert.assertNotNull("Running HaeHaku Poista ei toimi.", doit.textElement(driver, "Poista"));
	    	}
	    	else
	    	{
	    		Assert.assertFalse("Running HaeHaku Poista ei toimi.", doit.isPresentText(driver, "Poista"));
	    	}
		}
		doit.textClick(driver, "Muokkaa");
		Assert.assertNotNull("Running HaeHaku Muokkaa ei toimi.", doit.textElement(driver, "Hakulomake"));
		doit.tauko(1);
    	if (poista)
    	{
    		Assert.assertNotNull("Running HaeHaku Poista ei toimi.", doit.textElement(driver, "Poista"));
    	}
    	else
    	{
    		Assert.assertFalse("Running HaeHaku Poista ei toimi.", doit.isPresentText(driver, "Poista"));
    	}
    	Assert.assertTrue("Running HaeHaku Valmiina ei toimi.", doit.textElement(driver, "Tallenna valmiina").isEnabled());
		Assert.assertNotNull("Running HaeHaku Tila ei toimi.", doit.textElement(driver, tila.toUpperCase()));
    	if (tila.equals("Luonnos"))
    	{
	    	Assert.assertTrue("Running HaeHaku Luonnoksena ei toimi.", doit.textElement(driver, "Tallenna luonnoksena").isEnabled());
    	}
    	else
    	{
	    	Assert.assertNotNull("Running HaeHaku Luonnoksena ei toimi."
	    	, driver.findElement(By.xpath("//div[contains(@class,'v-disabled') and span/span[text()='Tallenna luonnoksena']]")));
    	}
    	
    	doit.echo("Running testHaeHaku OK");
    	doit.tauko(1);
    }

    //    Luo haku
    public void luoHaku(String nimi, String hakuVuosi, String alkaa, String paattyy, String tila) throws Exception {
    	doit.virkailijanPalvelut(driver, baseUrl);
        doit.ValikotHakujenYllapito(driver, baseUrl);
    	doit.echo("Running testLuoHaku ...");
        doit.textClick(driver, "Luo uusi haku");
        Assert.assertNotNull("Running LuoHaku Luo uusi haku ei toimi."
                , doit.textElement(driver, "Hakulomake"));
        doit.tauko(1);
        
        String millis = System.currentTimeMillis() + "";
        String yyyymmdd = doit.yyyymmddString();
        String ddmmyyyyhhmi = doit.ddmmyyyyhhmiString();
//        String nimi = reppu + "haku" + yyyymmdd + "_" + millis;
        String kuvaus = "kuvaus " + nimi + millis;
        
        String output = hakuVuosi + "\t\t" // -vuosi
        		+ hakuVuosi + "\t\t\t\t" // -vuosi
        		+ kuvaus + "\t" // Hakuajan tunniste
        		+ alkaa + "\t" // Hakuaika alkaa
        		+ paattyy; // Hakuaika päättyy
        doit.sendInputPlusX(driver, "Hakukausi ja -vuosi", output, 200);

        doit.sendInput(driver, "Hakutyyppi", "Varsinainen haku");
        doit.popupItemClick(driver, "Varsinainen haku");
        doit.sendInput(driver, "Hakukausi ja -vuosi", "Syksy");
        doit.popupItemClick(driver, "Syksy");
//        doit.sendInputPlusX(driver, "Hakukausi ja -vuosi", hakuVuosi + "\t", 200);
        doit.sendInput(driver, "Koulutuksen alkamiskausi", "Syksy");
        doit.popupItemClick(driver, "Syksy");
//        doit.sendInputPlusX(driver, "Koulutuksen alkamiskausi", hakuVuosi + "\t", 300);
        doit.sendInput(driver, "Haun kohdejoukko", "Ammatillinen koulutus ja lukiokoulutus");
        doit.popupItemClick(driver, "Ammatillinen koulutus ja lukiokoulutus");
        doit.sendInput(driver, "Hakutapa", "Yhteishaku");
        doit.popupItemClick(driver, "Yhteishaku");
        doit.sendInputPlusY(driver, "Haun nimi*", nimi);
//        doit.sendInputTextArea(driver, "Hakuajan tunniste", kuvaus);
//        doit.sendInput(driver, "Hakuaika alkaa", alkaa);
//        doit.sendInput(driver, "Hakuaika päättyy", paattyy);
        doit.sendInput(driver, "Haussa käytetään sijoittelua", "SELECTED");

        if (tila.equals("luonnos"))
        {
        	doit.textClick(driver, "Tallenna luonnoksena");
        }
        else
        {
        	doit.textClick(driver, "Tallenna valmiina");
        }
        if (nimi.indexOf("paattynyt") > 0 || nimi.indexOf("voimassa") > 0)
        {
        	while (true)
        	{
        		try {
        			Assert.assertNotNull("Running LuoHaku Tallenna valmiina ei toimi."
        					, doit.textElement(driver, "Haun alkamisaika ei voi olla menneess"));
        			break;
        		} catch (Exception e) {
        			while (doit.isPresentText(driver, "Hakuajan alku- ja loppu")
        					|| doit.isPresentText(driver, "Hakuvuosi on pakollinen tieto")
        					|| doit.isPresentText(driver, "Haun nimi puuttuu")
        					|| doit.isPresentText(driver, "Koulutuksen alkamisvuosi on pakollinen tieto")
        					|| doit.isPresentText(driver, "Koulutuksen alkamisvuosi tulee olla numeerinen arvo")
        					)
        			{
        				if (doit.isPresentText(driver, "Hakuajan alku- ja loppu"))
        				{
        					doit.sendInput(driver, "Hakuaika alkaa", alkaa);
        					doit.sendInput(driver, "Hakuaika päättyy", paattyy);
        				}
        				else if (doit.isPresentText(driver, "Hakuvuosi on pakollinen tieto"))
        				{        					
        					doit.sendInputPlusX(driver, "Hakukausi ja -vuosi", hakuVuosi + "\t", 200);
        				}
        				else if (doit.isPresentText(driver, "Koulutuksen alkamisvuosi on pakollinen tieto")
        					|| doit.isPresentText(driver, "Koulutuksen alkamisvuosi tulee olla numeerinen arvo"))
        				{
        					doit.sendInputPlusX(driver, "Koulutuksen alkamiskausi", hakuVuosi + "\t", 300);
        				}
        				else if (doit.isPresentText(driver, "Haun nimi puuttuu"))
        				{
        					doit.sendInputPlusY(driver, "Haun nimi*", nimi);
        				}
        		        if (tila.equals("luonnos"))
        		        {
        		        	doit.textClick(driver, "Tallenna luonnoksena");
        		        }
        		        else
        		        {
        		        	doit.textClick(driver, "Tallenna valmiina");
        		        }
        			}
        		}
        	}
        	Assert.assertNotNull("Running LuoHaku Tallenna valmiina ei toimi."
        			, doit.textElement(driver, "Tallennus ep"));
        	doit.textClick(driver, "Tallennus ep");
        	//
        	String tomorrow = doit.ddmmyyyyhhmiTomorrow();
        	doit.sendInput(driver, "Hakuaika alkaa", tomorrow);
        	if (nimi.indexOf("paattynyt") > 0) { doit.sendInput(driver, "Hakuaika päättyy", tomorrow); }
        	doit.sendInputPlusX(driver, "Hakukausi ja -vuosi", yyyymmdd.substring(0, 4) + "\t", 200);
        	doit.sendInputPlusX(driver, "Koulutuksen alkamiskausi", yyyymmdd.substring(0, 4) + "\t", 300);

            if (tila.equals("luonnos"))
            {
            	doit.textClick(driver, "Tallenna luonnoksena");
            }
            else
            {
            	doit.textClick(driver, "Tallenna valmiina");
            }
        }
        Assert.assertNotNull("Running LuoHaku Tallenna valmiina ei toimi."
        		, doit.textElement(driver, "Tallennus onnistui"));
        doit.tauko(1);
        
        if (tila.equals("julkaistu") || tila.equals("peruttu"))
        {
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

        	this.haeHakua("Valmis", nimi);

        	// Julkaise
        	doit.menuOperaatio(driver, "Julkaise", nimi);
        	Assert.assertNotNull("Running LuoHaku Julkaise ei toimi."
        			, doit.textElement(driver, "Haluatko varmasti julkaista alla mainitun haun"));
        	doit.tauko(1);
        	doit.textClick(driver, "Jatka");
        	Assert.assertNotNull("Running LuoHaku Julkaise ei toimi."
        			, doit.textElement(driver, "Toiminto onnistui"));
        }

        if (tila.equals("peruttu"))
        {
            doit.menuOperaatio(driver, "Peruuta haku", nimi);
            Assert.assertNotNull("Running LuoHaku Peruuta ei toimi."
                    , doit.textElement(driver, "Haluatko varmasti perua alla mainitun haun"));
            doit.tauko(1);
            doit.textClick(driver, "Jatka");
            Assert.assertNotNull("Running LuoHaku Peruuta ei toimi."
                    , doit.textElement(driver, "Toiminto onnistui"));
        }
    	doit.echo("Running testLuoHaku OK");
        doit.tauko(1);
    }

	public void haeHakua(String tila, String nimi)
	{
    	WebElement haeKentta = driver.findElement(By.className("v-textfield-search-box"));
		doit.filterTila(driver, tila); 
		haeKentta.clear();
		haeKentta.sendKeys(nimi);
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
	}

    @After
    public void tearDown() throws Exception {
//    	driver.quit();
//    	driverQuit = true;
    	doit.quit(driver);
    	String verificationErrorString = verificationErrors.toString();
    	if (!"".equals(verificationErrorString)) {
    		fail(verificationErrorString);
    	}
    }
}
