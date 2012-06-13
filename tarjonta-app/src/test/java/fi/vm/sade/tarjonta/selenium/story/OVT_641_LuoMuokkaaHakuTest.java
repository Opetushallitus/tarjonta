package fi.vm.sade.tarjonta.selenium.story;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.selenium.TarjontaEmbedComponentTstSupport;
import fi.vm.sade.tarjonta.selenium.pageobject.HakueraEditFormPageObject;
import fi.vm.sade.tarjonta.selenium.pageobject.HakueraListPageObject;
import fi.vm.sade.tarjonta.service.mock.HakueraServiceMock;
import fi.vm.sade.tarjonta.ui.MainWindow;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraEditForm;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraList;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static fi.vm.sade.support.selenium.SeleniumUtils.*;
import static org.junit.Assert.*;

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
        hakueraService.resetRepository();
        hakueraList.getComponent().reload();
        assertNotNull(mainWindowPageObject.getComponent());
        assertNotNull(mainWindowPageObject.getComponent().getHakuView());

        waitForText(I18N.getMessage("tarjonta.tabs.koulutusmoduulit"));
        //KoulutusmoduuliEditViewPageObject editor = mainWindowPageObject.selectLuoUusiTutkintoonJohtava();
        mainWindowPageObject.openHakuTab();
        waitForText(I18N.getMessage("HakueraEditForm.otsikko"));
        hakueraEdit.inputDefaultFields();
        hakueraEdit.save();
        waitForText(I18N.getMessage("save.success"));
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
        final String hakukausi = "Syksy";
        final String kohdejoukko = "Aikuiskoulutus";
        
        assertNotNull(mainWindowPageObject.getComponent());
        assertNotNull(mainWindowPageObject.getComponent().getHakuView());

        waitForText(I18N.getMessage("tarjonta.tabs.koulutusmoduulit"));
        mainWindowPageObject.openHakuTab();
        waitForText(I18N.getMessage("HakueraEditForm.otsikko"));
        hakueraEdit.inputCustomFields(hakutyyppi, hakukausi, null, kohdejoukko, null, null);
        hakueraEdit.save();
        waitForText(I18N.getMessage("save.success"));
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertEquals(hakueraService.getMockRepository().size(), hakueraList.getResultCount());
            }
        });
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
        assertNotNull(mainWindowPageObject.getComponent());
        assertNotNull(mainWindowPageObject.getComponent().getHakuView());

        waitForText(I18N.getMessage("tarjonta.tabs.koulutusmoduulit"));
        mainWindowPageObject.openHakuTab();
        waitForText(I18N.getMessage("HakueraEditForm.otsikko"));
        hakueraEdit.selectHakutapa(yhteishaku);
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertFalse(hakueraEdit.getComponent().getHakulomakeOptions().isEnabled()
                            || hakueraEdit.getComponent().getHakuSijoittelu().isEnabled()
                            || hakueraEdit.getComponent().getHakulomakeUrl().isEnabled());
            }
        });
        hakueraEdit.selectHakutapa("Muu haku");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertTrue(hakueraEdit.getComponent().getHakulomakeOptions().isEnabled()
                            && hakueraEdit.getComponent().getHakuSijoittelu().isEnabled()
                            && hakueraEdit.getComponent().getHakulomakeUrl().isEnabled());
            }
        });
    }
}
