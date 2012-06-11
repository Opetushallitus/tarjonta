package fi.vm.sade.tarjonta.selenium;

import com.vaadin.ui.Component;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.blackboard.BlackboardContext;
import fi.vm.sade.support.selenium.AbstractEmbedVaadinTest;
import fi.vm.sade.support.selenium.SeleniumContext;
import fi.vm.sade.support.selenium.SeleniumUtils;
import fi.vm.sade.tarjonta.selenium.pageobject.MainWindowPageObject;
import fi.vm.sade.tarjonta.ui.MainWindow;
import fi.vm.sade.tarjonta.ui.hakuera.event.HakueraSavedEvent;
import fi.vm.sade.tarjonta.ui.hakuera.event.HakueraSavedEvent.HakueraSavedEventListener;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.event.KoulutusmoduuliChangedEvent;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.event.KoulutusmoduuliChangedEvent.KoulutusmoduuliChangedEventListener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Nullable;

import static fi.vm.sade.support.selenium.SeleniumUtils.waitForText;
import static org.junit.Assert.fail;

@ContextConfiguration("classpath:spring/application-context.xml")
public class TarjontaEmbedComponentTstSupport<COMPONENT extends Component> extends AbstractEmbedVaadinTest<COMPONENT> {

    protected MainWindowPageObject mainWindowPageObject;

    public TarjontaEmbedComponentTstSupport() {
        super(true, true);
        registerListeners();
    }

    @Override
    public void initPageObjects() {
        mainWindowPageObject = new MainWindowPageObject(driver, getComponentByType(MainWindow.class));
    }


    /**
     * Invokes select box that opens up editor for with new empty DTO bound to form.
     * After returning, {@link #koulutusmoduuliEditViewPageObject} has been initialized
     */
    public void clickNewTutkintoonjohtavaKoulutusmoduuliAndWait() {
        mainWindowPageObject.selectLuoUusiTutkintoonJohtava();
        waitForText(I18N.getMessage("TutkintoOhjelmaFormModel.organisaatioStatus.notSaved"));
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

    private void registerListeners() {
        BlackboardContext.getBlackboard().register(KoulutusmoduuliChangedEventListener.class, KoulutusmoduuliChangedEvent.class);
        BlackboardContext.getBlackboard().register(HakueraSavedEventListener.class, HakueraSavedEvent.class);
        BlackboardContext.getBlackboard().enableLogging();
    }


}
