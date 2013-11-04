package fi.vm.sade.tarjonta.ui;

import static org.junit.Assert.*;

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

public class TestTarjontaHakukohdeTilat {
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

    //    Luo hakukohde
    @Test public void testLuoHakukohde_TILA201() throws Exception { luoHakukohde("2012", "luonnos"); }
//    @Test public void testLuoHakukohde_TILA202() throws Exception { luoHakukohde("2012", "valmis"); }
//    @Test public void testLuoHakukohde_TILA203() throws Exception { luoHakukohde("2012", "julkaistu"); }
//    @Test public void testLuoHakukohde_TILA204() throws Exception { luoHakukohde("2012", "peruttu"); }
    @Test public void testLuoHakukohde_TILA205() throws Exception { luoHakukohde("2013", "luonnos"); }
    @Test public void testLuoHakukohde_TILA206() throws Exception { luoHakukohde("2013", "valmis"); }
    @Test public void testLuoHakukohde_TILA207() throws Exception { luoHakukohde("2013", "julkaistu"); }
    @Test public void testLuoHakukohde_TILA208() throws Exception { luoHakukohde("2013", "peruttu"); }
    @Test public void testLuoHakukohde_TILA209() throws Exception { luoHakukohde("2023", "luonnos"); }
    @Test public void testLuoHakukohde_TILA210() throws Exception { luoHakukohde("2023", "valmis"); }
    @Test public void testLuoHakukohde_TILA211() throws Exception { luoHakukohde("2023", "julkaistu"); }
    @Test public void testLuoHakukohde_TILA212() throws Exception { luoHakukohde("2023", "peruttu"); }

    @Test public void testHaeHakukohde_TILA221() throws Exception { haeHakukohde("Luonnos", true, true, false, false, true); }
    @Test public void testHaeHakukohde_TILA222() throws Exception { haeHakukohde("Valmis", true, true, true, false, true); }
    @Test public void testHaeHakukohde_TILA223() throws Exception { haeHakukohde("Julkaistu", true, false, false, true, true); }
    @Test public void testHaeHakukohde_TILA224() throws Exception { haeHakukohde("Peruttu", true, false, true, false, true);
    driver.quit();
    driverQuit = true;
    }

    // argumentti poista - tarkoittaa pikkumenujen nakyvyytta
    public void haeHakukohde(String tila
    		, Boolean muokkaa, Boolean poista, Boolean julkaise, Boolean peruuta, Boolean poistaKoulutus) throws Exception {
    	doit.virkailijanPalvelut(driver, baseUrl);
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.echo("Running haeHakukohde ...");
    	doit.haePalvelunTarjoaja(driver, "kerttulin", "Kerttulin lukio");
    	doit.textClick(driver, "Kerttulin lukio");
        Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, "Koulutukset ("));
        doit.tauko(1);
        doit.haeHakukohteita(driver, tila, "ICT");
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
	    		Assert.assertNotNull("Running Peruuta ei toimi.", doit.textElement(driver, "Peruuta hakukohde"));
	    	}
	    	else
	    	{
	    		Assert.assertFalse("Running Peruuta ei toimi.", doit.isPresentText(driver, "Peruuta hakukohde"));
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
		Assert.assertNotNull("Running Muokkaa ei toimi.", doit.textElement(driver, "Hakukohteen tiedot"));
		Assert.assertNotNull("Running Muokkaa ei toimi.", doit.textElement(driver, "Liitteiden toimitusosoite"));
		Assert.assertNotNull("Running Poista ei toimi.", doit.textElement(driver, "Poista"));
    	if (tila.equals("Luonnos"))
    	{
	    	Assert.assertNotNull("Running luonnos ei toimi.", doit.textElement(driver, ", luonnos"));
    	}
    	if (tila.equals("Valmis"))
    	{
	    	Assert.assertNotNull("Running valmis ei toimi.", doit.textElement(driver, ", valmis"));
    	}
    	if (tila.equals("Julkaistu"))
    	{
	    	Assert.assertNotNull("Running JULKAISTU ei toimi.", doit.textElement(driver, ", julkaistu"));
    	}
    	if (tila.equals("Peruttu"))
    	{
	    	Assert.assertNotNull("Running valmis ei toimi.", doit.textElement(driver, ", peruttu"));
    	}
		doit.tauko(1);

		doit.textClick(driver, "muokkaa");
		Assert.assertNotNull("Running Muokkaa ei toimi."
				, doit.textElement(driver, "voidaan kuvata muuta hakemiseen olennaisesti liittyv"));
		doit.tauko(1);
    	Assert.assertTrue("Running Valmiina ei toimi.", doit.textElement(driver, "Tallenna valmiina").isEnabled());
		Assert.assertNotNull("Running Tila ei toimi.", doit.textElement(driver, tila.toUpperCase()));
    	if (tila.equals("Luonnos"))
    	{
	    	Assert.assertTrue("Running Luonnoksena ei toimi.", doit.textElement(driver, "Tallenna luonnoksena").isEnabled());
    	}
    	else
    	{
	    	Assert.assertNotNull("Running Luonnoksena ei toimi."
	    			, driver.findElement(By.xpath("//div[contains(@class,'v-disabled') and span/span[text()='Tallenna luonnoksena']]")));
    	}
    	Assert.assertNotNull("Running " + tila + " ei toimi.", doit.textElement(driver, tila.toUpperCase()));
    	
    	doit.echo("Running haeHakukohde OK");
    	doit.tauko(1);        
    }

    public void luoHakukohde(String koulutusVuosi, String tila) throws Exception {
    	doit.virkailijanPalvelut(driver, baseUrl);
    	doit.tarjonnanEtusivu(driver, baseUrl);
    	doit.echo("Running luoHakukohde ...");
    	doit.haePalvelunTarjoaja(driver, "kerttulin", "Kerttulin lukio");
    	doit.textClick(driver, "Kerttulin lukio");
        Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, "Koulutukset ("));
        Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, "Kerttulin lukio ("));
        doit.tauko(1);
    	doit.haeKoulutuksia(driver, null, "ICT", koulutusVuosi);
    	doit.triangleClickFirstTriangle(driver);
    	doit.checkboxSelectFirst(driver);
        doit.textClick(driver, "Luo uusi hakukohde");
        Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, "tietoja hakemisesta"));
        Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, "Tallenna luonnoksena"));
    	doit.footerTest(driver, "Running HAKUKOHTEEN MUOKKAUS (Liitteiden tiedot) footer ei toimi.", true);
    	
        // lomakkeen taytto
    	doit.sendInput(driver, "Hakukohteen nimi", "Lukion ICT-linja");
    	doit.popupItemClick(driver, "Lukion ICT-linja");
    	
    	doit.sendInputExact(driver, "Haku", reppu + "_");
        doit.tauko(1);
        if (koulutusVuosi.equals("2012"))
        {
            Assert.assertFalse("Running LUO UUSI HAKUKOHDE ei toimi.", doit.isPresentText(driver, reppu + "_"));
            doit.echo("Running luoHakukohde OK");
        	doit.tauko(1);
            driver.findElement(By.className("v-button-back")).click();
        	doit.tauko(1);
        	return;
        }
        if (koulutusVuosi.equals("2013"))
        {
            try {
				Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, reppu + "_luonnos_voimassa"));
				Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, reppu + "_luonnos_paattynyt"));
				Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, reppu + "_valmis_voimassa"));
				Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, reppu + "_valmis_paattynyt"));
				Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, reppu + "_julkaistu_voimassa"));
				Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, reppu + "_julkaistu_paattynyt"));
				Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, reppu + "_peruttu_voimassa"));
				Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, reppu + "_peruttu_paattynyt"));
			} catch (Exception e) {
	    		doit.sendInputExact(driver, "Haku", reppu + "_luonnos_voimassa"); doit.popupItemClick(driver, reppu + "_luonnos_voimassa");
	    		doit.sendInputExact(driver, "Haku", reppu + "_luonnos_paattynyt"); doit.popupItemClick(driver, reppu + "_luonnos_paattynyt");
	    		doit.sendInputExact(driver, "Haku", reppu + "_valmis_voimassa"); doit.popupItemClick(driver, reppu + "_valmis_voimassa");
	    		doit.sendInputExact(driver, "Haku", reppu + "_valmis_paattynyt"); doit.popupItemClick(driver, reppu + "_valmis_paattynyt");
	    		doit.sendInputExact(driver, "Haku", reppu + "_julkaistu_voimassa"); doit.popupItemClick(driver, reppu + "_julkaistu_voimassa");
	    		doit.sendInputExact(driver, "Haku", reppu + "_julkaistu_paattynyt"); doit.popupItemClick(driver, reppu + "_julkaistu_paattynyt");
	    		doit.sendInputExact(driver, "Haku", reppu + "_peruttu_voimassa"); doit.popupItemClick(driver, reppu + "_peruttu_voimassa");
	    		doit.sendInputExact(driver, "Haku", reppu + "_peruttu_paattynyt"); doit.popupItemClick(driver, reppu + "_peruttu_paattynyt");
			}        	
    		doit.sendInputExact(driver, "Haku", reppu + "_luonnos_paattynyt");
    		doit.popupItemClick(driver, reppu + "_luonnos_paattynyt");
        }
        if (koulutusVuosi.equals("2023"))
        {
            try {
				Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, reppu + "_luonnos_suunnitteilla"));
				Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, reppu + "_valmis_suunnitteilla"));
				Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, reppu + "_julkaistu_suunnitteilla"));
				Assert.assertNotNull("Running LUO UUSI HAKUKOHDE ei toimi.", doit.textElement(driver, reppu + "_peruttu_suunnitteilla"));
			} catch (Exception e) {
	    		doit.sendInputExact(driver, "Haku", reppu + "_luonnos_suunnitteilla"); doit.popupItemClick(driver, reppu + "_luonnos_suunnitteilla");
	    		doit.sendInputExact(driver, "Haku", reppu + "_valmis_suunnitteilla"); doit.popupItemClick(driver, reppu + "_valmis_suunnitteilla");
	    		doit.sendInputExact(driver, "Haku", reppu + "_julkaistu_suunnitteilla"); doit.popupItemClick(driver, reppu + "_julkaistu_suunnitteilla");
	    		doit.sendInputExact(driver, "Haku", reppu + "_peruttu_suunnitteilla"); doit.popupItemClick(driver, reppu + "_peruttu_suunnitteilla");
			}
    		doit.sendInputExact(driver, "Haku", reppu + "_peruttu_suunnitteilla");
    		doit.popupItemClick(driver, reppu + "_peruttu_suunnitteilla");
        }
        
        String yyyymmdd = doit.yyyymmddString();
        String ilmoitettavat = (System.currentTimeMillis() + "").substring(7);
        while (ilmoitettavat.substring(0,1).equals("0")) { ilmoitettavat = ilmoitettavat.substring(1); }
        doit.sendInput(driver, "Hakijoille ilmoitettavat aloituspaikat", ilmoitettavat + "\t");
        doit.sendInput(driver, "Valinnoissa käytettävät aloituspaikat", "10\t");
        doit.sendInputTiny(driver, "voidaan kuvata muuta hakemiseen olennaisesti", reppu + "hakukohde" + yyyymmdd);
        
        // Tallenna
        while (true)
        {
        	if (tila.equals("luonnos"))
        	{
        		doit.textClick(driver, "Tallenna luonnoksena");
        	}
        	else
        	{
        		doit.textClick(driver, "Tallenna valmiina");
        	}
        	try {
        		Assert.assertNotNull("Running Tallenna ei toimi.", doit.textElement(driver, "Tallennus onnistui"));
        		break;
        	} catch (Exception e) {
        		if (doit.isPresentText(driver, "Aloituspaikka tulee sy") || doit.isPresentText(driver, "hakupaikat ei saa olla tyh"))
        		{
        			doit.sendInput(driver, "Hakijoille ilmoitettavat aloituspaikat", ilmoitettavat + "\t");
        			doit.sendInput(driver, "Valinnoissa käytettävät aloituspaikat", "10\t");
        		}
        	}
		}
        driver.findElement(By.className("v-button-back")).click();
        doit.refreshTarjontaEtusivu(driver);
    	
        // JULKAISE
        if (tila.equals("julkaistu") || tila.equals("peruttu"))
        {
//            if (! doit.isPresentText(driver, "Koulutuksen alkamisvuosi"))
//            {
//            	doit.tauko(10);
//            	driver.navigate().refresh();
//            	doit.tauko(1);
//            }
//            Assert.assertNotNull("Running valikot ei toimi.", doit.textElement(driver, "Koulutuksen alkamisvuosi"));
//            doit.tauko(1);
            doit.textClick(driver, "Hakukohteet (");
            doit.tauko(2);
            doit.triangleClickFirstTriangle(driver);
            doit.menuOperaatio(driver, "Julkaise", ilmoitettavat);
            Assert.assertNotNull("Running Julkaise ei toimi.", doit.textElement(driver, "Toiminto onnistui"));
        }
        // PERUUTA
        if (tila.equals("peruttu"))
        {
            doit.menuOperaatio(driver, "Peruuta hakukohde", ilmoitettavat);
            Assert.assertNotNull("Running Peruuta ei toimi.", doit.textElement(driver, "Olet peruuttamassa hakukohdetta"));
            doit.tauko(1);
            doit.textClick(driver, "Kyllä");
            Assert.assertNotNull("Running Peruuta ei toimi.", doit.textElement(driver, "Toiminto onnistui"));
        }
        
        doit.echo("Running luoHakukohde OK");
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
