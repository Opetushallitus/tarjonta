package fi.vm.sade.tarjonta.selenium.story;

import fi.vm.sade.support.selenium.SeleniumContext;
import fi.vm.sade.support.selenium.SeleniumUtils;
import fi.vm.sade.tarjonta.selenium.TarjontaEmbedComponentTstSupport;
import fi.vm.sade.tarjonta.selenium.pageobject.HakueraEditFormPageObject;
import fi.vm.sade.tarjonta.selenium.pageobject.HakueraListPageObject;
import fi.vm.sade.tarjonta.ui.haku.HakuEditForm;
import fi.vm.sade.tarjonta.ui.haku.HakuView;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraList;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nullable;

import static fi.vm.sade.support.selenium.SeleniumUtils.STEP;
import static fi.vm.sade.support.selenium.SeleniumUtils.waitForElement;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Antti Salonen
 */
public class HakueraListSeleniumTest extends TarjontaEmbedComponentTstSupport<HakuView> {

    private HakueraListPageObject hakueraList;
    private HakueraEditFormPageObject hakueraEdit;

    @Override
    public void initPageObjects() {
        hakueraList = new HakueraListPageObject(driver, getComponentByType(HakueraList.class));
        hakueraEdit = new HakueraEditFormPageObject(driver, getComponentByType(HakuEditForm.class));
    }

    
    @Test
    public void hakueraListWorksCorrectly() throws Throwable {
        STEP("aluksi lista näyttää kaikki hakuerät");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() {
                assertEquals(3, hakueraList.getResultCount());
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

    // TODO: siirrä yleisiin

    public void waitAssert(final AssertionCallback assertionCallback) throws Throwable {
        final Throwable[] exception = new Throwable[1];
        try {
            Object result = new WebDriverWait(SeleniumContext.getDriver(), SeleniumUtils.TIME_OUT_IN_SECONDS).until(new ExpectedCondition<Object>() {
                @Override
                public Object apply(@Nullable WebDriver webDriver) {
                    try {
                        assertionCallback.doAssertion();
                        return "OK";
                    } catch (Throwable e) {
                        exception[0] = e;
                        log.warn("waitAssert not yet succeeded: " + e);
                        return null;
                    }
                }
            });
        } catch (org.openqa.selenium.TimeoutException te) {
            if (exception[0] != null) {
                throw exception[0];
            } else {
                fail("waitAssert failed but exception is null?!");
            }
        }
    }

    public abstract class AssertionCallback {
        public abstract void doAssertion();
    }
}
