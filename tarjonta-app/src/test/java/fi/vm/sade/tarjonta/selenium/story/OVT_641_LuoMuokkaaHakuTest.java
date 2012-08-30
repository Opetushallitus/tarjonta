package fi.vm.sade.tarjonta.selenium.story;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.support.selenium.SeleniumUtils.*;
import fi.vm.sade.tarjonta.selenium.TarjontaEmbedComponentTstSupport;
import fi.vm.sade.tarjonta.selenium.pageobject.HakueraEditFormPageObject;
import fi.vm.sade.tarjonta.selenium.pageobject.HakueraListPageObject;
import fi.vm.sade.tarjonta.service.mock.HakueraServiceMock;
import fi.vm.sade.tarjonta.ui.MainWindow;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraEditForm;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraList;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;
import static fi.vm.sade.support.selenium.SeleniumUtils.*;

/**
 * Test for HakueraEditForm.  
 * 
 * @author markus
 *
 */
public class OVT_641_LuoMuokkaaHakuTest extends TarjontaEmbedComponentTstSupport<MainWindow> {
    
    private HakueraListPageObject hakueraList;
    private HakueraEditFormPageObject hakueraEdit;
    
    @Autowired
    HakueraServiceMock hakueraService;
    

    @Override
    public void initPageObjects() {
        super.initPageObjects();     
        hakueraList = new HakueraListPageObject(driver, getComponentByType(HakueraList.class));
        hakueraEdit = new HakueraEditFormPageObject(driver, getComponentByType(HakueraEditForm.class));
    }
    
    @Test
    public void luoHakueraTest() throws Throwable {
        STEP("Avataan hakulomake");
        hakueraService.resetRepository();
        hakueraList.getComponent().reload();
        assertNotNull(mainWindowPageObject.getComponent());
        assertNotNull(mainWindowPageObject.getComponent().getHakuView());

        waitForText(I18N.getMessage("tarjonta.tabs.koulutusmoduulit"));
        //KoulutusmoduuliEditViewPageObject editor = mainWindowPageObject.selectLuoUusiTutkintoonJohtava();
        mainWindowPageObject.openHakuTab();
        waitForText(I18N.getMessage("HakueraEditForm.otsikko"));
        STEP("Luodaan uusi hakuerä, jolloin luotu organisaatio ilmestyy hakulistaukseen");
        hakueraEdit.inputDefaultFields();
        hakueraEdit.save();
        waitForText(I18N.getMessage("c_save_successful"));
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertEquals(hakueraService.getMockRepository().size(), hakueraList.getResultCount());
            }
        });
    }
    
    @Test
    public void automaticNimiConstructionTest() throws Throwable {
        final String hakutyyppi = "Varsinainen haku";
        final String hakukausi = "Syksy 2012";
        final String kohdejoukko = "Aikuiskoulutus";
        STEP("Avataan hakulomake");
        assertNotNull(mainWindowPageObject.getComponent());
        assertNotNull(mainWindowPageObject.getComponent().getHakuView());

        waitForText(I18N.getMessage("tarjonta.tabs.koulutusmoduulit"));
        mainWindowPageObject.openHakuTab();
        waitForText(I18N.getMessage("HakueraEditForm.otsikko"));
        STEP("Täytetään lomake, jolloin nimi-kenttien arvot täydentyvät valintojen mukaisesti");
        hakueraEdit.inputCustomFields(hakutyyppi, hakukausi, null, kohdejoukko, null, null);
        hakueraEdit.save();
        waitForText(I18N.getMessage("c_save_successful"));
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertEquals(hakueraService.getMockRepository().size(), hakueraList.getResultCount());
            }
        });
        STEP("Tarkistetaan että päivitys on mennyt oikein");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertTrue(hakueraEdit.getNimiFiValue().contains(hakutyyppi)
                        && hakueraEdit.getNimiFiValue().contains(hakukausi)
                        && hakueraEdit.getNimiFiValue().contains(kohdejoukko));
            }
        });
        
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertTrue(hakueraEdit.getNimiSvValue().contains(hakutyyppi)
                        && hakueraEdit.getNimiSvValue().contains(hakukausi)
                        && hakueraEdit.getNimiSvValue().contains(kohdejoukko));
            }
        });
        
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertTrue(hakueraEdit.getNimiEnValue().contains(hakutyyppi)
                        && hakueraEdit.getNimiEnValue().contains(hakukausi)
                        && hakueraEdit.getNimiEnValue().contains(kohdejoukko));
            }
        });
    }
    
    @Test
    public void dynamicFieldsTest() throws Throwable {
        String yhteishaku = I18N.getMessage("HakueraEditForm.yhteishaku");
        final String hakulomakeUrl = "http://www.lomake.fi";
        
        STEP("Avataan hakuformi");
        assertNotNull(mainWindowPageObject.getComponent());
        assertNotNull(mainWindowPageObject.getComponent().getHakuView());

        waitForText(I18N.getMessage("tarjonta.tabs.koulutusmoduulit"));
        mainWindowPageObject.openHakuTab();
        waitForText(I18N.getMessage("HakueraEditForm.otsikko"));
        
        STEP("Täytetään formi sisältäen hakulomakkeen url");
        hakueraEdit.inputDefaultFields();
        hakueraEdit.inputHakulomake(hakulomakeUrl);
        
        STEP("Valitaan hakutavaksi Yhteishaku, jolloin sijoittelu- ja hakulomake-kentät disabloituvat");
        hakueraEdit.selectHakutapa(yhteishaku);
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertFalse(hakueraEdit.getComponent().getHakulomakeOptions().isEnabled()
                            || hakueraEdit.getComponent().getHakuSijoittelu().isEnabled()
                            || hakueraEdit.getComponent().getHakulomakeUrl().isEnabled());
            }
        });
        
        STEP("Valitaan hakutavaksi Muu haku, jolloin sijoittelu- ja hakulomake-kentät enabloituvat");
        hakueraEdit.selectHakutapa("Muu haku");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertTrue(hakueraEdit.getComponent().getHakulomakeOptions().isEnabled()
                            && hakueraEdit.getComponent().getHakuSijoittelu().isEnabled()
                            && hakueraEdit.getComponent().getHakulomakeUrl().isEnabled());
            }
        });
        
        STEP("Varmistutaan, että hakulomakekentän urli on tallella");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertEquals((String)(hakueraEdit.getComponent().getHakulomakeUrl().getValue()), hakulomakeUrl);
            }
        });
        
        STEP("Valitaan hakutavaksi Yhteishaku, jolloin sijoittelu- ja hakulomake-kentät disabloituvat");
        hakueraEdit.selectHakutapa(yhteishaku);
        
        STEP("Tallennetaan lomake");
        hakueraEdit.save();
        waitForText(I18N.getMessage("c_save_successful"));
        
        STEP("kun hakueraa klikkaa listasta, se aukeaa muokkausnakymaan");
        hakueraList.clickHakuera((hakueraService.getMockRepository().size() -1));
        waitForElement(By.id(hakueraEdit.getComponent().getDebugId()));
        
        STEP("Varmistutaan että hakulomakekentän arvo on null");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertNull(hakueraEdit.getComponent().getHakulomakeUrl().getValue());
            }
        });
        
    }
    
    @Test
    public void luoUusiButtonTest() throws Throwable {
        STEP("Avataan hakuformi");
        assertNotNull(mainWindowPageObject.getComponent());
        assertNotNull(mainWindowPageObject.getComponent().getHakuView());

        waitForText(I18N.getMessage("tarjonta.tabs.koulutusmoduulit"));
        mainWindowPageObject.openHakuTab();
        waitForText(I18N.getMessage("HakueraEditForm.otsikko"));
        
        STEP("kun hakueraa klikkaa listasta, se aukeaa muokkausnakymaan");
        hakueraList.clickHakuera(0);
        waitForElement(By.id(hakueraEdit.getComponent().getDebugId()));
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertEquals(hakueraList.getItemNimi(0), hakueraEdit.getComponent().getHaunNimi().getTextFi().getValue());
            }
        });
        
        STEP("Kun klikataan Luo uusi haku -nappia lomake tyhjenee ja puun valinta poistuu");
        hakueraEdit.clickLuoUusiButton();
        
        STEP("Varmistutaan että lomake on tyhjä");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                //hakueraEdit = new HakueraEditFormPageObject(driver, getComponentByType(HakueraEditForm.class));
                assertNull(hakueraEdit.getComponent().getHaunNimi().getTextFi().getValue());
            }
        });
        
        STEP("Luodaan uusi hakuerä, jolloin luotu organisaatio ilmestyy hakulistaukseen");
        //hakueraEdit = new HakueraEditFormPageObject(driver, getComponentByType(HakueraEditForm.class));
        hakueraEdit.inputDefaultFields();
        hakueraEdit.save();
        waitForText(I18N.getMessage("c_save_successful"));
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertEquals(hakueraService.getMockRepository().size(), hakueraList.getResultCount());
            }
        });
    }
    
    @Test
    public void cancelButtonTest() throws Throwable {
        STEP("Avataan hakulomake");
        hakueraService.resetRepository();
        hakueraList.getComponent().reload();
        assertNotNull(mainWindowPageObject.getComponent());
        assertNotNull(mainWindowPageObject.getComponent().getHakuView());

        waitForText(I18N.getMessage("tarjonta.tabs.koulutusmoduulit"));
        //KoulutusmoduuliEditViewPageObject editor = mainWindowPageObject.selectLuoUusiTutkintoonJohtava();
        mainWindowPageObject.openHakuTab();
        waitForText(I18N.getMessage("HakueraEditForm.otsikko"));
        
        STEP("Täytetään hakuformi painamatta tallennusnappia");
        hakueraEdit.inputDefaultFields();
        
        STEP("Painetaa peruaatanappia, jolloin lomake tyhjenee");
        hakueraEdit.cancel();
        
        STEP("Varmistutaan että lomake on tyhjä");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertNull(hakueraEdit.getComponent().getHaunNimi().getTextFi().getValue());
            }
        });
    }
    
    @Test
    public void modifyHakueraTest() throws Throwable {
        final String testinimi = "TESTINIMI";
        STEP("Avataan hakuformi");
        assertNotNull(mainWindowPageObject.getComponent());
        assertNotNull(mainWindowPageObject.getComponent().getHakuView());

        waitForText(I18N.getMessage("tarjonta.tabs.koulutusmoduulit"));
        mainWindowPageObject.openHakuTab();
        waitForText(I18N.getMessage("HakueraEditForm.otsikko"));
        
        STEP("Kun hakueraa klikkaa listasta, se aukeaa muokkausnakymaan");
        final int initialHakueraCount = hakueraList.getItems().size();
        hakueraList.clickHakuera(0);
        waitForElement(By.id(hakueraEdit.getComponent().getDebugId()));
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertEquals(hakueraList.getItemNimi(0), hakueraEdit.getComponent().getHaunNimi().getTextFi().getValue());
            }
        });
        
        STEP("Muokataan haun nimeä ja tallennetaan");
        hakueraEdit.inputNames(testinimi);
        hakueraEdit.save();
        
        STEP("Varmistutaan, että hakuerän nimi on muuttunut puussa ja hakuerien määrä ei ole muuttunut.");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertEquals(initialHakueraCount, hakueraList.getItems().size());
            }
        });
        
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertTrue(hakueraList.getItemNimi(0).contains(testinimi));
            }
        });
        
    }
}
