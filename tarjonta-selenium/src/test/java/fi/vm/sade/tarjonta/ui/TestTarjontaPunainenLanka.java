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

public class TestTarjontaPunainenLanka {

    private WebDriver driver;
    private String baseUrl;
    private StringBuffer verificationErrors = new StringBuffer();
    private SVTUtils doit = new SVTUtils();
    private static Boolean first = true;
    private static Kattavuus TarjontaTapaukset = new Kattavuus();
//    private static Kattavuus TarjontaVaatimukset = new Kattavuus();
    private static Kattavuus TarjontaPunainenLankaVaatimukset = new Kattavuus();

    @Before
    public void setUp() throws Exception {
            FirefoxProfile firefoxProfile = new FirefoxProfile();
            firefoxProfile.setEnableNativeEvents(true);
            firefoxProfile.setPreference( "intl.accept_languages", "fi-fi,fi" );
            driver = new FirefoxDriver(firefoxProfile);
//          driver = new FirefoxDriver(new FirefoxBinary(new File("c:/Selaimet/Firefox17/firefox.exe")), firefoxProfile);
            baseUrl = SVTUtils.prop.getProperty("tarjonta-selenium.oph-url");
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    public void frontPage() throws Exception
    {
            if (first)
            {
                    doit.palvelimenVersio(driver, baseUrl);
                    TarjontaTapaukset.alustaKattavuusKohde("TarjontaPunainenLankaTestiTapaukset");
                    TarjontaPunainenLankaVaatimukset.alustaKattavuusKohde("TarjontaPunainenLankaVaatimukset");
//                    TarjontaVaatimukset.alustaKattavuusKohde("TarjontaVaatimukset");

                    TarjontaTapaukset.setKattavuus("TC0804", Kattavuus.KATTAVUUSNOTEST);
                    TarjontaTapaukset.setKattavuus("TC0807", Kattavuus.KATTAVUUSNOTEST);
                    TarjontaTapaukset.setKattavuus("TC0808", Kattavuus.KATTAVUUSNOTEST);
                    TarjontaTapaukset.setKattavuus("TC0816", Kattavuus.KATTAVUUSNOTEST);
                    TarjontaTapaukset.setKattavuus("TC0817", Kattavuus.KATTAVUUSNOTEST);
                    TarjontaTapaukset.setKattavuus("TC0811", Kattavuus.KATTAVUUSNOTEST);
                    TarjontaTapaukset.setKattavuus("TC0812", Kattavuus.KATTAVUUSNOTEST);
                    doit.echo("Running =================================================================");
            }

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

    //  TC0802	Luo koulutus (ammatillinen koulutus)
    @Test
    public void test_T_INT_TAR_PUNA001_TC0802_AMPLuokoulutus() throws Exception {
    	try {
    		testTC0802loop();
    	} catch (Exception e) {
    		try {
    			doit.printMyStackTrace(e);
    			testTC0802loop();
    		} catch (Exception e2) {
    			doit.printMyStackTrace(e2);
    			testTC0802loop();
    		}
    	}
    }

    public void testTC0802loop() throws Exception {
    	this.frontPage();
    	doit.echo("Running TarjontaPunainenLanka TC0802 ...");
    	TarjontaTapaukset.setKattavuus("TC0802", Kattavuus.KATTAVUUSERROR);
    	doit.ValikotKoulutustenJaHakukohteidenYllapito(driver, baseUrl);
        
        // HAE
        WebElement haeKentta = driver.findElement(By.className("v-textfield-search-box"));
        haeKentta.clear();
        haeKentta.sendKeys("ihateo");
        doit.tauko(1);
        doit.textClick(driver, "Hae");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Hae Lihateollisuuden tutkimuskeskus ei toimi."
                , doit.textElement(driver, "Lihateollisuuden tutkimuskeskus"));
        doit.tauko(1);
        doit.textClick(driver, "Lihateollisuuden tutkimuskeskus");
        doit.tauko(1);
        
        // poistetaan aikaisemmin mahdollisesti luotu hevoskoulutus
        if (doit.PoistaKoulutus(driver, "Hevo"))
        {
        	doit.textClick(driver, "Lihateollisuuden tutkimuskeskus");
        	doit.tauko(1);
        }
        //
        doit.textClick(driver, "Luo uusi koulutus");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Luo uusi koulutus ei toimi."
                , doit.textElement(driver, "Olet luomassa uutta koulutusta"));
        doit.sendInputPlusX(driver, "Koulutus:", "Ammatillinen peruskoulutus", 200);
        doit.popupItemClick(driver, "Ammatillinen peruskoulutus");
        doit.tauko(1);
        doit.sendInputPlusX(driver, "Pohjakoulutus:", "Peruskoulu", 20);
        doit.popupItemClick(driver, "Peruskoulu");
        doit.tauko(1);
        driver.findElement(By.xpath("//span[@class = 'v-button-caption' and text() = 'Lihateollisuuden tutkimuskeskus']")).click();
        doit.tauko(1);
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Luo uusi ammatillinenkoulutus + jatka ei toimi."
        		, doit.textElement(driver, "posti"));
        doit.sendInput(driver, "Koulutus tai tutkinto", "Hevostalouden perustutkinto");
        doit.tauko(1);
        doit.popupItemClick(driver, "Hevostalouden perustutkinto");
        doit.tauko(1);
        doit.sendInput(driver, "Koulutusohjelma", "Hevostalouden koulutusohjelma, ratsastuksenohjaaja");
        doit.popupItemClick(driver, "Hevostalouden koulutusohjelma, ratsastuksenohjaaja");
        doit.tauko(1);
        
//        if (doit.isPresentText(driver, "Koulutusala")
//        		|| doit.isPresentText(driver, "Maatilatalous")
//        		|| doit.isPresentText(driver, "Luonnonvara- ja ympäristöala")
////        		|| doit.isPresentText(driver, "opintoviikko")
//        		|| doit.isPresentText(driver, "120")
//        		|| doit.isPresentText(driver, "Ratsastuksenohjaaja")
//        		|| doit.isPresentText(driver, "Tutkinnon kaikille pakolliset osat")
//        		|| doit.isPresentText(driver, "ratsastuksenohjaaja vastaa ratsastuskoulun hevosten hoidosta")
//        		|| doit.isPresentText(driver, "Ammatillisista perustutkinnoista sekä ammatti")
//        		)
//        {
//            Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Jotain ei toimi."
//            		, doit.textElement(driver, "Jotain ei toimi"));
//        }
        
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Koulutusaste ei toimi."
        		, doit.textElement(driver, "Koulutusala"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Opintoala ei toimi."
        		, doit.textElement(driver, "Maatilatalous"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Koulutusala ei toimi."
        		, doit.textElement(driver, "Luonnonvara- ja ympäristöala"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Opintojen laajuusyksikkö ei toimi."
        		, doit.textElement(driver, "opintoviikko"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Opintojen laajuus ei toimi."
        		, doit.textElement(driver, "120"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Tutkintonimike ei toimi."
        		, doit.textElement(driver, "Ratsastuksenohjaaja"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Koulutuksen rakenne ei toimi."
        		, doit.textElement(driver, "Tutkinnon kaikille pakolliset osat"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Tutkinnon koulutukselliset ja ammatilliset tavoitteet ei toimi."
        		, doit.textElement(driver, "ratsastuksenohjaaja vastaa ratsastuskoulun hevosten hoidosta"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Jatko-opintomahdollisuudet ei toimi."
        		, doit.textElement(driver, "Ammatillisista perustutkinnoista sekä ammatti"));
        doit.tauko(1);

        doit.sendInput(driver, "Suunniteltu kesto", "3");
        doit.sendInput(driver, "Opetusmuoto", "Oppisopimuskoulutus");
        doit.popupItemClick(driver, "Oppisopimuskoulutus");
        doit.sendInputPlusX(driver, "Suunniteltu kesto", "Kuukausi", 150); // Valitse aikayksikko
        doit.popupItemClick(driver, "Kuukausi");
        doit.sendInput(driver, "Opetuskieli", "suomi");
        doit.popupItemClick(driver, "suomi");
        
        doit.textClick(driver, "Tallenna luonnoksena");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Tallenna ei toimi."
        		, doit.textElement(driver, "Tallennus onnistui"));
        
        doit.textClick(driver, "Koulutuksen kuvailevat tiedot");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Koulutuksen kuvailevat tiedot ei toimi."
        		, doit.textElement(driver, "Kuvaus siitä, miten valinta koulutusohjelmiin on toteutettu."));
        doit.sendInputTiny(driver, "Sijoittuminen", "Sijoitu tyoelmaan");
        doit.sendInput(driver, "Kuvaus siitä, miten valinta koulutusohjelmiin on toteutettu.", "Hevostenvalmentaja");
        doit.popupItemClick(driver, "Hevostenvalmentaja");
        doit.tauko(1);
        
        doit.textClickLast(driver, "Tallenna luonnoksena");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Tallenna ei toimi."
        		, doit.textElement(driver, "Tallennus onnistui"));
        doit.tauko(1);
        
        doit.textClick(driver, "Koulutuksen perustiedot");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Koulutuksen perustiedot + LUONNOS ei toimi."
        		, doit.textElement(driver, "LUONNOS"));
        doit.tauko(1);
        
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Jatka ei toimi."
        		, doit.textElement(driver, "muokkaa"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Jatka ei toimi."
        		, doit.textElement(driver, "Tallennettu"));
        doit.tauko(1);
        
        driver.findElement(By.className("v-button-back")).click();
        doit.tauko(10);
        driver.navigate().refresh();
        doit.tauko(1);
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 valikot ei toimi."
                , doit.textElement(driver, "Koulutuksen alkamisvuosi"));
        doit.tauko(1);
    	
        // poistetaan luotu hevoskoulutus
        doit.PoistaKoulutus(driver, "Hevo");
    	doit.echo("SUCCESSFUL testTC0802");
    	TarjontaTapaukset.setKattavuus("TC0802", Kattavuus.KATTAVUUSOK);
    }

    //  TC0802lukio	Luo koulutus (lukio koulutus)
    @Test
    public void test_T_INT_TAR_PUNA002_TC0802_lukioLuoKoulutus() throws Exception {
    	try {
    		testTC0802lukioloop();
    	} catch (Exception e) {
    		try {
    			doit.printMyStackTrace(e);
    			testTC0802lukioloop();
    		} catch (Exception e2) {
    			doit.printMyStackTrace(e2);
    			testTC0802lukioloop();
    		}
    	}
    }
    
    public void testTC0802lukioloop() throws Exception {
    	this.frontPage();
    	doit.echo("Running TarjontaPunainenLanka TC0802lukio ...");
    	TarjontaTapaukset.setKattavuus("TC0802lukio", Kattavuus.KATTAVUUSERROR);
    	doit.ValikotKoulutustenJaHakukohteidenYllapito(driver, baseUrl);
        
        // HAE
        WebElement haeKentta = driver.findElement(By.className("v-textfield-search-box"));
        haeKentta.clear();
        haeKentta.sendKeys("lucina");
        doit.tauko(1);
        doit.textClick(driver, "Hae");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Hae Lucina Hagmanin lukio ei toimi."
                , doit.textElement(driver, "Lucina Hagmanin lukio")); // kaatui 26.7
        doit.tauko(1);
        doit.textClick(driver, "Lucina Hagmanin lukio"); // ei toimi luokka 2.8
        doit.tauko(1);
        
        // poistetaan aikaisemmin mahdollisesti luotu hevoskoulutus
        if (doit.PoistaKoulutus(driver, "Lukion hevoslinja"))
        {
        	doit.textClick(driver, "Lucina Hagmanin lukio");
        	doit.tauko(1);
        }
        //
        doit.textClick(driver, "Luo uusi koulutus");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Luo uusi koulutus ei toimi."
                , doit.textElement(driver, "Olet luomassa uutta koulutusta"));
//        driver.findElement(By.xpath("(//div[@class = 'v-filterselect-button'])[7]")).click();
//        doit.tauko(1);
//        doit.textClick(driver, "Lukiokoulutus");
//        doit.tauko(1);
//        driver.findElement(By.xpath("//span[@class = 'v-button-caption' and text() = 'Lucina Hagmanin lukio']")).click();
//        doit.tauko(1);
        doit.sendInputPlusX(driver, "Koulutus:", "Lukiokoulutus", 200);
        doit.popupItemClick(driver, "Lukiokoulutus");
        doit.tauko(1);
        doit.textClickLast(driver, "Lucina Hagmanin lukio");
        doit.tauko(1);
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Luo uusi lukiokoulutus + jatka ei toimi."
        		, doit.textElement(driver, "posti"));
        doit.sendInput(driver, "Koulutus / tutkinto", "Ylioppilastutkinto");
        doit.tauko(1);
        doit.popupItemClick(driver, "Ylioppilastutkinto");
        doit.tauko(1);
        
        if (doit.isPresentText(driver, "Lukiokoulutus")
        		|| doit.isPresentText(driver, "Yleissivistävä koulutus")
        		|| doit.isPresentText(driver, "kurssi")
        		|| doit.isPresentText(driver, "Lukion oppimäärä sisältää vähintään 75")
        		|| doit.isPresentText(driver, "antaa laaja-alai")
        		|| doit.isPresentText(driver, "kelpoisuuden")
        		)
        {
            Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Jotain ei toimi."
            		, doit.textElement(driver, "Jotain ei toimi")); // kaatui 26.7
        }
        
        doit.sendInput(driver, "Lukiolinja", "Lukion hevoslinja");
        doit.popupItemClick(driver, "Lukion hevoslinja");
        doit.tauko(1);
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Koulutusaste ei toimi."
        		, doit.textElement(driver, "Lukiokoulutus"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Opintoala ei toimi."
        		, doit.textElement(driver, "Yleissivistävä koulutus"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Koulutusala ei toimi."
        		, doit.textElement(driver, "kurssi"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Opintojen laajuusyksikkö ei toimi."
        		, doit.textElement(driver, "Lukion oppimäärä sisältää vähintään 75")); 
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Opintojen laajuus ei toimi."
        		, doit.textElement(driver, "antaa laaja-alai"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Tutkintonimike ei toimi."
        		, doit.textElement(driver, "kelpoisuuden"));
        doit.tauko(1);

        doit.sendInput(driver, "Suunniteltu kesto", "3");
        doit.doubleclick(driver, "Iltaopetus"); // Opetusmuoto
        doit.sendInputPlusX(driver, "Suunniteltu kesto", "Kuukausi", 150); // Valitse aikayksikko
        doit.popupItemClick(driver, "Kuukausi");
        doit.sendInput(driver, "Opetuskieli", "suomi");
        doit.popupItemClick(driver, "suomi");
        
        doit.textClick(driver, "Tallenna luonnoksena");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Tallenna ei toimi."
        		, doit.textElement(driver, "Tallennus onnistui"));
        
        doit.textClick(driver, "Koulutuksen kuvailevat tiedot");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Koulutuksen kuvailevat tiedot ei toimi."
        		, doit.textElement(driver, "opintopolkujen toteuttamisessa tms. Yritysten nimien mainitsemista on kuitenkin"));
        doit.sendInputTiny(driver, "ulkomailla opiskelu / vaihto-opiskelumahdollisuudet", "Ulkomaille");
        
        doit.textClickLast(driver, "Tallenna luonnoksena");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Tallenna ei toimi."
        		, doit.textElement(driver, "Tallennus onnistui"));
        
        doit.textClick(driver, "Koulutuksen perustiedot");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Koulutuksen perustiedot + LUONNOS ei toimi."
        		, doit.textElement(driver, "LUONNOS"));
        
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Jatka ei toimi."
        		, doit.textElement(driver, "muokkaa"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Jatka ei toimi."
        		, doit.textElement(driver, "Tallennettu"));
        
        driver.findElement(By.className("v-button-back")).click();
        doit.tauko(10);
        driver.navigate().refresh();
        doit.tauko(1);
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio valikot ei toimi."
                , doit.textElement(driver, "Koulutuksen alkamisvuosi"));
    	
     // poistetaan luotu hevoskoulutus
        doit.PoistaKoulutus(driver, "Lukion hevoslinja");
    	doit.echo("SUCCESSFUL testTC0802lukio");
    	TarjontaTapaukset.setKattavuus("TC0802lukio", Kattavuus.KATTAVUUSOK);
    }

    public static String kouluTyypinValinta = "ylioppilastut";
    //TC0804	Muokkaa koulutusta
    @Test
    public void test_T_INT_TAR_PUNA004_TC0804_LukioMuokkaaKoulutus() throws Exception {
		try {
			testTC0804loop();
		} catch (Exception e) {
			try {
				doit.printMyStackTrace(e);
				testTC0804loop();
			} catch (Exception e2) {
				doit.printMyStackTrace(e2);
				testTC0804loop();
			}
		}
    }

    //TC0804	Muokkaa koulutusta
    @Test
    public void test_T_INT_TAR_PUNA003_TC0804_AMP_MuokkaaKoulutus() throws Exception {
    	kouluTyypinValinta = "tusohjel";
		try {
			testTC0804loop();
		} catch (Exception e) {
			try {
				doit.printMyStackTrace(e);
				testTC0804loop();
			} catch (Exception e2) {
				doit.printMyStackTrace(e2);
				testTC0804loop();
			}
		}
    }

    public void testTC0804loop() throws Exception {
    	this.frontPage();
    	doit.echo("Running TarjontaPunainenLanka TC0804 ...");
    	TarjontaTapaukset.setKattavuus("TC0804", Kattavuus.KATTAVUUSERROR);
    	doit.ValikotKoulutustenJaHakukohteidenYllapito(driver, baseUrl);

    	// HAE
//		WebElement menu = doit.linkKoulutusLuonnosta(driver, kouluTyypinValinta);
//		doit.menuOperaatio(driver, "Muokkaa", "luonnos");
    	doit.haeKoulutuksia(driver, "Luonnos", kouluTyypinValinta);
    	doit.triangleClickFirstTriangle(driver);
    	doit.menuOperaatioFirstMenu(driver, "Muokkaa");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0804 muokkaa ei toimi."
                , doit.textElement(driver, "Tutkintonimike"));
        String puhelinnumero = (System.currentTimeMillis() + "").substring(6);
        doit.sendInput(driver, "Puhelin", puhelinnumero);
        doit.textClick(driver, "Tallenna luonnoksena");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0804 Tallenna ei toimi."
                        , doit.textElement(driver, "Tallennus onnistui"));
        doit.textClick(driver, "Koulutuksen kuvailevat tiedot");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0804 Koulutuksen kuvailevat tiedot ei toimi."
                , doit.textElement(driver, "Yritysten nimien mainitsemista on kuitenkin"));
        String ulkomaille = "Ulkomaille " + (System.currentTimeMillis() + "");
        doit.sendInputTiny(driver, "ulkomailla opiskelu / vaihto-opiskelumahdollisuudet", ulkomaille);
        doit.textClickLast(driver, "Tallenna luonnoksena");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0804 Tallenna ei toimi."
                        , doit.textElement(driver, "Tallennus onnistui"));
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0804 Jatka ei toimi."
                , doit.textElement(driver, "muokkaa"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0804 Jatka ei toimi."
                , doit.textElement(driver, "Tallennettu"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0804 ulkomaille ei toimi."
                , doit.textElement(driver, ulkomaille));
        
    	doit.echo("SUCCESSFUL testTC0804");
    	TarjontaTapaukset.setKattavuus("TC0804", Kattavuus.KATTAVUUSOK);
    }

    //    TC0807	Luo haku
    @Test
    public void test_T_INT_TAR_PUNA005_TC0807_LuoHaku() throws Exception {
    	try {
    		testTC0807loop();
    	} catch (Exception e) {
    		try {
    			doit.printMyStackTrace(e);
    			testTC0807loop();
    		} catch (Exception e2) {
    			doit.printMyStackTrace(e2);
    			testTC0807loop();
    		}
    	}
    }

    public void testTC0807loop() throws Exception {
    	this.frontPage();
    	doit.echo("Running TarjontaPunainenLanka TC0807 ...");
    	TarjontaTapaukset.setKattavuus("TC0807", Kattavuus.KATTAVUUSERROR);
        doit.ValikotHakujenYllapito(driver, baseUrl);
        doit.textClick(driver, "Luo uusi haku");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0807 Luo uusi haku ei toimi."
                , doit.textElement(driver, "Hakulomake"));
        doit.tauko(1);
        
        String millis = System.currentTimeMillis() + "";
        String nimi = "nimi " + millis;
        String kuvaus = "kuvaus " + millis;
        doit.sendInput(driver, "Hakutyyppi", "Varsinainen haku");
        doit.popupItemClick(driver, "Varsinainen haku");
        doit.sendInput(driver, "Hakukausi ja -vuosi", "Syksy");
        doit.popupItemClick(driver, "Syksy");
        doit.sendInputPlusX(driver, "Hakukausi ja -vuosi", "2014\t", 200);
//        doit.sendInputPlusX(driver, "Hakukausi ja -vuosi", "2014", 200);
//        doit.sendInputPlusX(driver, "Hakukausi ja -vuosi", "2014", 200);
        doit.sendInput(driver, "Koulutuksen alkamiskausi", "Syksy");
        doit.popupItemClick(driver, "Syksy");
        doit.sendInputPlusX(driver, "Koulutuksen alkamiskausi", "2014\t", 300);
//        doit.sendInputPlusX(driver, "Koulutuksen alkamiskausi", "2014", 300);
//        doit.sendInputPlusX(driver, "Koulutuksen alkamiskausi", "2014", 300);
        doit.sendInput(driver, "Haun kohdejoukko", "Aikuiskoulutus");
        doit.popupItemClick(driver, "Aikuiskoulutus");
        doit.sendInput(driver, "Hakutapa", "Erillishaku");
        doit.popupItemClick(driver, "Erillishaku");
        doit.sendInputPlusY(driver, "Haun nimi", nimi);
        doit.sendInputTextArea(driver, "Hakuajan tunniste", kuvaus);
        doit.sendInput(driver, "Hakuaika alkaa", "31.07.2014 15:24");
        doit.sendInput(driver, "Hakuaika päättyy", "31.08.2014 15:24");
        doit.sendInput(driver, "Haussa käytetään sijoittelua", "SELECTED");

        doit.textClick(driver, "Tallenna luonnoksena");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0807 Tallenna luonnoksena ei toimi."
                , doit.textElement(driver, "Tallennus onnistui"));
        doit.tauko(1);
        
        driver.findElement(By.className("v-button-back")).click();
		Assert.assertNotNull("Running TarjontaPunainenLanka TC0807 back ei toimi."
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
		Assert.assertNotNull("Running TarjontaPunainenLanka TC0807 Hae ei toimi."
				, doit.textElement(driver, "Erillishaku"));
		doit.tauko(1);
		WebElement lastTriangle = doit.getTriangleForLastHakukohde(driver);
		lastTriangle.click();        
		Assert.assertNotNull("Running TarjontaPunainenLanka TC0807 Hae ei toimi."
				, doit.textElement(driver, nimi));
        doit.tauko(1);
        
        // POISTA LUOTU HAKU
        doit.menuOperaatio(driver, "Poista", nimi);
		Assert.assertNotNull("Running TarjontaPunainenLanka TC0807 Haun poisto ei toimi."
				, doit.textElement(driver, "Haluatko varmasti poistaa alla mainitun haun"));
        doit.textClick(driver, "Jatka");
        doit.tauko(1);
        driver.navigate().refresh();
        doit.tauko(1);
        if (doit.isPresentText(driver, nimi)) 
        { 	
        	Assert.assertNull("Running TarjontaPunainenLanka TC0807 Haun poisto ei toimi."
				, doit.textElement(driver, nimi));
        }
    	doit.echo("SUCCESSFUL testTC0807");
    	TarjontaTapaukset.setKattavuus("TC0807", Kattavuus.KATTAVUUSOK);
    }

    //    TC0808	Muokkaa hakua
    @Test
    public void test_T_INT_TAR_PUNA006_TC0808_MuokkaaHakua() throws Exception {
    	try {
    		testTC0808loop();
    	} catch (Exception e) {
    		try {
    			doit.printMyStackTrace(e);
    			testTC0808loop();
    		} catch (Exception e2) {
    			doit.printMyStackTrace(e2);
    			testTC0808loop();
    		}
    	}
    }

    public void testTC0808loop() throws Exception {
    	this.frontPage();
    	doit.echo("Running TarjontaPunainenLanka TC0808 ...");
    	TarjontaTapaukset.setKattavuus("TC0808", Kattavuus.KATTAVUUSERROR);
        doit.ValikotHakujenYllapito(driver, baseUrl);
        
        WebElement lastTriangle = doit.getTriangleForLastHakukohde(driver);
        lastTriangle.click();
        doit.tauko(1);
        doit.menuOperaatioFirstMenu(driver, "Muokkaa");

        String millis = System.currentTimeMillis() + "";
        String kuvaus = "kuvaus " + millis;
        doit.sendInputTextArea(driver, "Hakuajan tunniste", kuvaus);
        doit.sendInput(driver, "Hakuaika alkaa", "31.07.2014 15:24");
        doit.sendInput(driver, "Hakuaika päättyy", "31.08.2014 15:24");

        doit.textClick(driver, "Tallenna valmiina");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0808 Tallenna valmiina ei toimi."
                , doit.textElement(driver, "Tallennus onnistui"));
        doit.tauko(1);
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0808 Jatka ei toimi."
                , doit.textElement(driver, "Tallennettu"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0808 Jatka ei toimi."
                , doit.textElement(driver, "muokkaa"));
        doit.tauko(1);
        
    	doit.echo("SUCCESSFUL testTC0808");
    	TarjontaTapaukset.setKattavuus("TC0808", Kattavuus.KATTAVUUSOK);
    }

    //    TC0816	Luo yhteiset valintaperustekuvaukset
    @Test
    public void test_T_INT_TAR_PUNA007_TC0816_Valintaperustekuvaus() throws Exception {
    	try {
    		testTC0816loop();
    	} catch (Exception e) {
    		try {
    			doit.printMyStackTrace(e);
    			testTC0816loop();
    		} catch (Exception e2) {
    			doit.printMyStackTrace(e2);
    			testTC0816loop();
    		}
    	}
    }

    public void testTC0816loop() throws Exception {
    	this.frontPage();
    	doit.echo("Running TarjontaPunainenLanka TC0816 ...");
    	TarjontaTapaukset.setKattavuus("TC0816", Kattavuus.KATTAVUUSERROR);
        doit.ValikotValintaperusteKuvaustenYllapito(driver, baseUrl);
        doit.sendInput(driver, "Ryhmä", "Lukio");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0816 Ryhma Lukio ei toimi."
                , doit.textElement(driver, "Lukio"));
        if (doit.isPresentText(driver, "Lukio * "))
        {
            doit.popupItemClick(driver, "Lukio * ");
        }
        else
        {
            doit.popupItemClick(driver, "Lukio");
        }
        
        String millis = System.currentTimeMillis() + "";
        String kuvaus = "kuvaus " + millis;
        doit.sendInputTiny(driver, "Kuvausteksti", kuvaus);
        
        doit.textClick(driver, "Tallenna");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0816 Tallenna ei toimi."
                , doit.textElement(driver, "Tallennus onnistui"));
        doit.tauko(1);

    	doit.echo("SUCCESSFUL testTC0816");
    	TarjontaTapaukset.setKattavuus("TC0816", Kattavuus.KATTAVUUSOK);
    }

    //    TC0817	Muokkaa valintaperustekuvauksia
    @Test
    public void test_T_INT_TAR_PUNA008_TC0817_MuokkaaValintaperustekuvaus() throws Exception {
    	try {
    		testTC0817loop();
    	} catch (Exception e) {
    		try {
    			doit.printMyStackTrace(e);
    			testTC0817loop();
    		} catch (Exception e2) {
    			doit.printMyStackTrace(e2);
    			testTC0817loop();
    		}
    	}
    }

    public void testTC0817loop() throws Exception {
    	this.frontPage();
    	doit.echo("Running TarjontaPunainenLanka TC0817 ...");
    	TarjontaTapaukset.setKattavuus("TC0817", Kattavuus.KATTAVUUSERROR);
        doit.ValikotValintaperusteKuvaustenYllapito(driver, baseUrl);
        
        doit.sendInput(driver, "Ryhmä", "Lukio * ");
        doit.popupItemClick(driver, "Lukio * ");

        String millis = System.currentTimeMillis() + "";
        String kuvaus = "kuvaus muutos " + millis;
        doit.sendInputTiny(driver, "Kuvausteksti", kuvaus);

        doit.textClick(driver, "Tallenna");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0817 Tallenna ei toimi."
                , doit.textElement(driver, "Tallennus onnistui"));
        doit.tauko(1);
    	
    	doit.echo("SUCCESSFUL testTC0817");
    	TarjontaTapaukset.setKattavuus("TC0817", Kattavuus.KATTAVUUSOK);
    }

    //    TC0811	Luo hakukohde
    @Test
    public void test_T_INT_TAR_PUNA009_TC0811_LuoHakukohde() throws Exception {
    	try {
    		testTC0811loop();
    	} catch (Exception e) {
    		try {
    			doit.printMyStackTrace(e);
    			testTC0811loop();
    		} catch (Exception e2) {
    			doit.printMyStackTrace(e2);
    			testTC0811loop();
    		}
    	}
    }

    public void testTC0811loop() throws Exception {
    	this.frontPage();
    	doit.echo("Running TarjontaPunainenLanka TC0811 ...");
    	TarjontaTapaukset.setKattavuus("TC0811", Kattavuus.KATTAVUUSERROR);
    	doit.ValikotKoulutustenJaHakukohteidenYllapito(driver, baseUrl);
        
        // HAE
        WebElement haeKentta = driver.findElement(By.className("v-textfield-search-box"));
        if (doit.isPresentText(driver, "Jyväskylän aikuisopisto"))
        {
            haeKentta.clear();
            haeKentta.sendKeys("alavuden kaupun");
            doit.tauko(1);
            driver.findElement(By.xpath("//*[text()='Hae']")).click();
            Assert.assertNotNull("Running TarjontaPunainenLanka TC0811 Hae Alavuden kaupunki ei toimi."
                            , doit.textElement(driver, "Alavuden kaupunki"));
            doit.tauko(1);
        }
        haeKentta.clear();
        haeKentta.sendKeys("jyväskylän aikuiso");
        doit.tauko(1);
        driver.findElement(By.xpath("//*[text()='Hae']")).click();
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0811 Hae Jyväskylän aikuisopisto ei toimi."
                        , doit.textElement(driver, "Jyväskylän aikuisopisto"));
        doit.tauko(1);
        doit.textClick(driver, "Jyväskylän aikuisopisto");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0811 Hae Koulutukset ( ei toimi."
                , doit.textElement(driver, "Koulutukset ("));
        doit.tauko(1);
        if (doit.isPresentText(driver, "Koulutukset (0)"))
        {
        	doit.echo("Ei loydy koulutuksia Jyväskylän aikuisopistolle");
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
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0811 LuoUusiHakukohde ei toimi."
                , doit.textElement(driver, "Olet luomassa uutta hakukohdetta seuraavista koulutuksista"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0811 LuoUusiHakukohde ei toimi."
                , doit.textElement(driver, "Jatka"));
        doit.tauko(1);
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0811 Jatka ei toimi."
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
        doit.sendInputExact(driver, "Haku", " ");
        doit.tauko(1);
        WebElement menuItem = driver.findElement(By.xpath("(//td[@class='gwt-MenuItem'])[1]"));
        if (menuItem != null && menuItem.getText().length() == 0)
        {
        	menuItem = driver.findElement(By.xpath("(//td[@class='gwt-MenuItem'])[2]"));
        }
        menuItem.click();
        doit.tauko(1);
    	
        String ilmoitettavat = (System.currentTimeMillis() + "").substring(7);
        doit.sendInput(driver, "Hakijoille ilmoitettavat aloituspaikat", ilmoitettavat + "\t");
        doit.sendInput(driver, "Valinnoissa käytettävät aloituspaikat", "10\t");
        
//        doit.sendInputPlusX(driver, "Alku:", "31.07.2014 15:24", 20);
//        doit.sendInput(driver, "Loppu:", "30.09.2014 15:24");
        
        // Tallenna
        doit.textClick(driver, "Tallenna luonnoksena");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0811 Tallenna ei toimi."
                        , doit.textElement(driver, "Tallennus onnistui"));

        // Jatka
        doit.textClick(driver, "Jatka");
        doit.tauko(60);
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0811 Jatka ei toimi."
                , doit.textElement(driver, "Tallennettu"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0811 Jatka ei toimi."
                , doit.textElement(driver, "muokkaa"));
        doit.tauko(60);
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0811 Jatka ei toimi."
                , doit.textElement(driver, ilmoitettavat));
        doit.tauko(1);
        driver.findElement(By.className("v-button-back")).click();
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0811 Jatka ei toimi."
                , doit.textElement(driver, "Hakukohteet ("));
        doit.tauko(1);

        // Hae luotu hakukohde
        doit.textClick(driver, "Hakukohteet (");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0811 Hae haku ei toimi."
                , doit.textElement(driver, "Hakukohteet ("));
        doit.tauko(1);
        WebElement triangle = doit.getTriangleForFirstItem(driver);
        try {
        	if (! doit.isPresentText(driver, ilmoitettavat)) { triangle.click(); }
        	Assert.assertNotNull("Running TarjontaPunainenLanka TC0811 Hae haku ei toimi."
        			, doit.textElement(driver, ilmoitettavat));
        } catch (Exception e) {
        	triangle = doit.getTriangleForLastHakukohde(driver);
        	triangle.click();
        	Assert.assertNotNull("Running TarjontaPunainenLanka TC0811 Hae haku ei toimi."
        			, doit.textElement(driver, ilmoitettavat));
        }
        doit.tauko(1);

    	doit.echo("SUCCESSFUL testTC0811");
    	TarjontaTapaukset.setKattavuus("TC0811", Kattavuus.KATTAVUUSOK);
    }
    
    //  TC0812	Muokkaa hakukohdetta 
    @Test
    public void test_T_INT_TAR_PUNA010_TC0812_MuokkaaHakukohdetta() throws Exception {
		try {
			testTC0812loop();
		} catch (Exception e) {
			try {
				doit.printMyStackTrace(e);
				testTC0812loop();
			} catch (Exception e2) {
				doit.printMyStackTrace(e2);
				testTC0812loop();
			}
		}
    }

    public void testTC0812loop() throws Exception {
    	this.frontPage();
    	doit.echo("Running TarjontaPunainenLanka TC0812 ...");
    	TarjontaTapaukset.setKattavuus("TC0812", Kattavuus.KATTAVUUSERROR);
    	doit.ValikotKoulutustenJaHakukohteidenYllapito(driver, baseUrl);
        doit.haeHakukohteita(driver, "Luonnos", null);
        doit.triangleClickLastTriangle(driver);
//        doit.textClick(driver, "Hakukohteet");
//        doit.tauko(1);
//        WebElement menu = doit.TarkasteleHakukohdeLuonnosta(driver, "");
//        if (menu == null)
//        {
//        	doit.echo("Running Ei ole luonnoksia hakukohteille.");
//        	int a = 1 / 0;
//        }
        // otetaan organisaatio muistiin
        String organisaatio = doit.getTextMinusY(driver, "luonnos", "//div[@class='v-label v-label-undef-w' and contains(text(),')')]");
        organisaatio = organisaatio.substring(0, organisaatio.indexOf("(") - 1);
        
        // muokkaa
        doit.menuOperaatio(driver, "Muokkaa", "luonnos");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0812 Menu muokkaa ei toimi."
                , doit.textElement(driver, "Tallenna valmiina"));
        doit.tauko(1);
        
        // otetaan koulutus, vuosi ja kausi muistiin
        String koulutusA = driver.findElements(By.className("v-label-light")).get(1).getText();
        String koulutusB = driver.findElement(By.className("v-label-h1")).getText();
        String koulutus = doit.commonStringBegin(koulutusA, koulutusB);
        koulutus = koulutus.replace("Lukion ", "");
        
        // muokkaa
        String ilmoitettavat = (System.currentTimeMillis() + "").substring(7);
        doit.sendInput(driver, "Hakijoille ilmoitettavat aloituspaikat", ilmoitettavat);
        doit.tauko(1);
        
        // Tallenna
        doit.textClick(driver, "Tallenna luonnoksena");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0812 Tallenna ei toimi."
                        , doit.textElement(driver, "Tallennus onnistui"));

        // back
        driver.findElement(By.className("v-button-back")).click();
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0812 back ei toimi."
                , doit.textElement(driver, "Hakukohteet ("));
        doit.tauko(1);

        // tarkista muutos
        // Hae organisaatio
        WebElement haeKentta = driver.findElement(By.className("v-textfield-search-box"));
        haeKentta.clear();
        haeKentta.sendKeys(organisaatio.substring(0, organisaatio.length() - 2));
        doit.tauko(1);
        doit.textClick(driver, "Hae");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Hae organisaatio ei toimi."
                , doit.textElement(driver, organisaatio));
        doit.tauko(1);
        doit.textElement(driver, organisaatio).click();
        doit.tauko(1);

        // Hae muutettu hakukohde
        doit.textClick(driver, "Hakukohteet (");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0812 Hae haku ei toimi."
                , doit.textElement(driver, "Hakukohteet ("));
        doit.tauko(1);
        doit.textClick(driver, "Tyhjennä");
    	driver.findElement(By.xpath("(//span[@class='v-button-caption' and text()='Tyhjennä'])[2]")).click();
        doit.tauko(1);
    	WebElement search = driver.findElements(By.className("v-textfield-search-box")).get(1);
    	search.clear();
    	search.sendKeys(koulutus);
    	doit.tauko(1);
        driver.findElement(By.xpath("(//span[text() = 'Hae'])[2]")).click();
    	doit.tauko(5);
        WebElement triangle = doit.getTriangleForFirstItem(driver);
        triangle.click(); 
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0812 Muutos ei toimi."
                , doit.textElement(driver, ilmoitettavat));
        doit.tauko(1);

        doit.echo("SUCCESSFUL testTC0812");
    	TarjontaTapaukset.setKattavuus("TC0812", Kattavuus.KATTAVUUSOK);
    }

    @Test
    public void testReport() throws Exception {
        SVTUtils doit = new SVTUtils();
//        doit.alustaVaatimukset(TarjontaPunainenLankaVaatimukset);
        TarjontaPunainenLankaVaatimukset.KattavuusRaportti();

//        doit.alustaVaatimukset(TarjontaVaatimukset);
//        TarjontaVaatimukset.KattavuusRaportti();

//        doit.alustaVaatimukset(TarjontaTapaukset);
        TarjontaTapaukset.KattavuusRaportti();
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
