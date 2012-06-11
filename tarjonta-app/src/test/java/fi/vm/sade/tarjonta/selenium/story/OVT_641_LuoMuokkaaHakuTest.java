package fi.vm.sade.tarjonta.selenium.story;

import static fi.vm.sade.support.selenium.SeleniumUtils.waitForText;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.selenium.TarjontaEmbedComponentTstSupport;
import fi.vm.sade.tarjonta.selenium.pageobject.HakueraEditFormPageObject;
import fi.vm.sade.tarjonta.selenium.pageobject.HakueraListPageObject;
import fi.vm.sade.tarjonta.service.mock.HakueraServiceMock;
import fi.vm.sade.tarjonta.ui.MainWindow;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraEditForm;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraList;

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
                assertEquals(4, hakueraList.getResultCount());
            }
        });
    }

}
