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
            Assert.assertNotNull("Running TarjontaPunainenLanka000 Etusivu ei toimi."
                            , doit.textElement(driver, "Tervetuloa Opintopolun virkailijan palveluihin!"));
            doit.tauko(1);
            first = false;
    }

    //  TC0802	Luo koulutus (ammatillinen koulutus)
    @Test
    public void testTC0802() throws Exception {
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
    	doit.ValikotHakukohteidenYllapito(driver, baseUrl);
        
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
        driver.findElement(By.xpath("(//div[@class = 'v-filterselect-button'])[7]")).click();
        doit.tauko(1);
        doit.textClick(driver, "Ammatillinen peruskoulutus");
        doit.tauko(1);
        driver.findElement(By.xpath("(//div[@class = 'v-filterselect-button'])[8]")).click();
        doit.tauko(1);
        doit.textClick(driver, "Peruskoulu");
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
        
        if (doit.isPresentText(driver, "Ammatillinen koulutus")
        		|| doit.isPresentText(driver, "Maatilatalous")
        		|| doit.isPresentText(driver, "Luonnonvara- ja ympäristöala")
        		|| doit.isPresentText(driver, "opintoviikko")
        		|| doit.isPresentText(driver, "120")
        		|| doit.isPresentText(driver, "Ratsastuksenohjaaja")
        		|| doit.isPresentText(driver, "Tutkinnon kaikille pakolliset osat")
        		|| doit.isPresentText(driver, "Tutkinnon suorittaja työskentelee")
        		|| doit.isPresentText(driver, "Ammatillisista perustutkinnoista sekä ammatti")
        		)
        {
            Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Jotain ei toimi."
            		, doit.textElement(driver, "Jotain ei toimi"));
        }
        
        doit.sendInput(driver, "Koulutusohjelma", "Hevostalouden koulutusohjelma, ratsastuksenohjaaja");
        doit.popupItemClick(driver, "Hevostalouden koulutusohjelma, ratsastuksenohjaaja");
        doit.tauko(1);
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Koulutusaste ei toimi."
        		, doit.textElement(driver, "Ammatillinen koulutus"));
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
        		, doit.textElement(driver, "Tutkinnon suorittaja työskentelee"));
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
        
        doit.textClickLast(driver, "Tallenna luonnoksena");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Tallenna ei toimi."
        		, doit.textElement(driver, "Tallennus onnistui"));
        
        doit.textClick(driver, "Koulutuksen perustiedot");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Koulutuksen perustiedot + LUONNOS ei toimi."
        		, doit.textElement(driver, "LUONNOS"));
        
        doit.textClick(driver, "Jatka");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Jatka ei toimi."
        		, doit.textElement(driver, "muokkaa"));
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 Jatka ei toimi."
        		, doit.textElement(driver, "Tallennettu"));
        
        driver.findElement(By.className("v-button-back")).click();
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802 valikot ei toimi."
                , doit.textElement(driver, "Koulutuksen alkamisvuosi"));
    	
     // poistetaan luotu hevoskoulutus
        doit.PoistaKoulutus(driver, "Hevo");
    	doit.echo("SUCCESSFUL testTC0802");
    	TarjontaTapaukset.setKattavuus("TC0802", Kattavuus.KATTAVUUSOK);
    }

    //  TC0802lukio	Luo koulutus (lukio koulutus)
    @Test
    public void testTC0802lukio() throws Exception {
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
    	doit.ValikotHakukohteidenYllapito(driver, baseUrl);
        
        // HAE
        WebElement haeKentta = driver.findElement(By.className("v-textfield-search-box"));
        haeKentta.clear();
        haeKentta.sendKeys("lucina");
        doit.tauko(1);
        doit.textClick(driver, "Hae");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio Hae Lucina Hagmanin lukio ei toimi."
                , doit.textElement(driver, "Lucina Hagmanin lukio")); // kaatui 26.7
        doit.tauko(1);
        doit.textClick(driver, "Lucina Hagmanin lukio");
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
        driver.findElement(By.xpath("(//div[@class = 'v-filterselect-button'])[7]")).click();
        doit.tauko(1);
        doit.textClick(driver, "Lukiokoulutus");
        doit.tauko(1);
        driver.findElement(By.xpath("//span[@class = 'v-button-caption' and text() = 'Lucina Hagmanin lukio']")).click();
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
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0802lukio valikot ei toimi."
                , doit.textElement(driver, "Koulutuksen alkamisvuosi"));
    	
     // poistetaan luotu hevoskoulutus
        doit.PoistaKoulutus(driver, "Lukion hevoslinja");
    	doit.echo("SUCCESSFUL testTC0802lukio");
    	TarjontaTapaukset.setKattavuus("TC0802lukio", Kattavuus.KATTAVUUSOK);
    }

    //TC0804	Muokkaa koulutusta
    @Test
    public void testTC0804() throws Exception {
		testTC0804loop();
//		try {
//    		testTC0804loop();
//    	} catch (Exception e) {
//    		try {
//    			doit.printMyStackTrace(e);
//    			testTC0804loop();
//    		} catch (Exception e2) {
//    			doit.printMyStackTrace(e2);
//    			testTC0804loop();
//    		}
//    	}
    }

    public void testTC0804loop() throws Exception {
    	this.frontPage();
    	doit.echo("Running TarjontaPunainenLanka TC0804 ...");
    	TarjontaTapaukset.setKattavuus("TC0804", Kattavuus.KATTAVUUSERROR);
    	doit.ValikotHakukohteidenYllapito(driver, baseUrl);

    	// HAE
    	driver.findElement(By.xpath("(//span[text() = 'Hae'])[2]")).click();
        
		WebElement menu = doit.TarkasteleKoulutusLuonnosta(driver, "ylioppilastut");
		doit.menuOperaatioMenu(driver, menu, "Muokkaa");
        Assert.assertNotNull("Running TarjontaPunainenLanka TC0804 muokkaa ei toimi."
                , doit.textElement(driver, "Tutkintonimike"));
        String puhelinnumero = (System.currentTimeMillis() + "").substring(6);
        doit.sendInput(driver, "Puhelinnumero", puhelinnumero);
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
    //@Test
    public void testTC0807() throws Exception {
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

    	doit.echo("SUCCESSFUL testTC0807");
    	TarjontaTapaukset.setKattavuus("TC0807", Kattavuus.KATTAVUUSOK);
    }

    //    TC0808	Muokkaa hakua
    //@Test
    public void testTC0808() throws Exception {
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

    	doit.echo("SUCCESSFUL testTC0808");
    	TarjontaTapaukset.setKattavuus("TC0808", Kattavuus.KATTAVUUSOK);
    }

    //    TC0816	Luo yhteiset valintaperustekuvaukset
    //@Test
    public void testTC0816() throws Exception {
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

    	doit.echo("SUCCESSFUL testTC0816");
    	TarjontaTapaukset.setKattavuus("TC0816", Kattavuus.KATTAVUUSOK);
    }

    //    TC0817	Muokkaa valintaperustekuvauksia
    //@Test
    public void testTC0817() throws Exception {
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

    	doit.echo("SUCCESSFUL testTC0817");
    	TarjontaTapaukset.setKattavuus("TC0817", Kattavuus.KATTAVUUSOK);
    }

    //    TC0811	Luo hakukohde
    //@Test
    public void testTC0811() throws Exception {
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
    	doit.echo("Running TarjontaPunainenLanka TC0804 ...");

    	TarjontaTapaukset.setKattavuus("TC0804", Kattavuus.KATTAVUUSERROR);

    	doit.echo("SUCCESSFUL testTC0804");
    	TarjontaTapaukset.setKattavuus("TC0804", Kattavuus.KATTAVUUSOK);
    }
    
    //  TC0812	Muokkaa hakukohdetta 
    //@Test
    public void testTC0812() throws Exception {
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

    	doit.echo("SUCCESSFUL testTC0812");
    	TarjontaTapaukset.setKattavuus("TC0812", Kattavuus.KATTAVUUSOK);
    }

    @Test
    public void testReport() throws Exception {
        SVTUtils doit = new SVTUtils();
        doit.alustaVaatimukset(TarjontaPunainenLankaVaatimukset);
        TarjontaPunainenLankaVaatimukset.KattavuusRaportti();

//        doit.alustaVaatimukset(TarjontaVaatimukset);
//        TarjontaVaatimukset.KattavuusRaportti();

        doit.alustaVaatimukset(TarjontaTapaukset);
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
