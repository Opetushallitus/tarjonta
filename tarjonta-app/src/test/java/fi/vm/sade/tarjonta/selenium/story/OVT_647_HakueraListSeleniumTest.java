package fi.vm.sade.tarjonta.selenium.story;

import fi.vm.sade.tarjonta.selenium.TarjontaEmbedComponentTstSupport;
import fi.vm.sade.tarjonta.selenium.pageobject.HakueraEditFormPageObject;
import fi.vm.sade.tarjonta.selenium.pageobject.HakueraListPageObject;
import fi.vm.sade.tarjonta.service.mock.HakueraServiceMock;
import fi.vm.sade.tarjonta.ui.hakuera.HakuView;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraEditForm;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraList;
import org.junit.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

import static fi.vm.sade.support.selenium.SeleniumUtils.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Antti Salonen
 */
public class OVT_647_HakueraListSeleniumTest extends TarjontaEmbedComponentTstSupport<HakuView> {

    private HakueraListPageObject hakueraList;
    private HakueraEditFormPageObject hakueraEdit;
    
    @Autowired
    HakueraServiceMock hakueraService;

    @Override
    public void initPageObjects() {
        hakueraList = new HakueraListPageObject(driver, getComponentByType(HakueraList.class));
        hakueraEdit = new HakueraEditFormPageObject(driver, getComponentByType(HakueraEditForm.class));
    }

    
    @Test
    public void hakueraListWorksCorrectly() throws Throwable {
        STEP("aluksi lista näyttää kaikki hakuerät");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertEquals(hakueraService.getMockRepository().size(), hakueraList.getResultCount());
            }
        });

        STEP("klikataan ruksiboksista 'paattyneet' pois päältä");
        hakueraList.clickFilters(true, false, false);

        STEP("nyt listassa näkyy vain kaksi paattynyttä hakuera");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertEquals(2, hakueraList.getResultCount());
            }
        });

        STEP("kun hakueraa klikkaa listasta, se aukeaa muokkausnakymaan");
        hakueraList.clickHakuera(0);
        waitForElement(By.id(hakueraEdit.getComponent().getDebugId()));
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertEquals(hakueraList.getItemNimi(0), hakueraEdit.getComponent().getHaunNimi().getTextFi().getValue());
            }
        });

    }


}
