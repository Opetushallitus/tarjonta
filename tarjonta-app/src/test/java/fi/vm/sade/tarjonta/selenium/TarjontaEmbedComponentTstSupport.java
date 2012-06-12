package fi.vm.sade.tarjonta.selenium;

import com.vaadin.ui.Component;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.blackboard.BlackboardContext;
import fi.vm.sade.support.selenium.AbstractEmbedVaadinTest;
import fi.vm.sade.tarjonta.selenium.pageobject.MainWindowPageObject;
import fi.vm.sade.tarjonta.ui.MainWindow;
import fi.vm.sade.tarjonta.ui.hakuera.event.HakueraSavedEvent;
import fi.vm.sade.tarjonta.ui.hakuera.event.HakueraSavedEvent.HakueraSavedEventListener;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.event.KoulutusmoduuliChangedEvent;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.event.KoulutusmoduuliChangedEvent.KoulutusmoduuliChangedEventListener;
import org.springframework.test.context.ContextConfiguration;

import static fi.vm.sade.support.selenium.SeleniumUtils.waitForText;

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

    private void registerListeners() {
        BlackboardContext.getBlackboard().register(KoulutusmoduuliChangedEventListener.class, KoulutusmoduuliChangedEvent.class);
        BlackboardContext.getBlackboard().register(HakueraSavedEventListener.class, HakueraSavedEvent.class);
        BlackboardContext.getBlackboard().enableLogging();
    }


}
