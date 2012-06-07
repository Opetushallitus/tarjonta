package fi.vm.sade.tarjonta.selenium;

import com.vaadin.ui.Component;
import fi.vm.sade.support.selenium.AbstractEmbedVaadinTest;
import fi.vm.sade.tarjonta.selenium.pageobject.MainWindowPageObject;
import fi.vm.sade.tarjonta.ui.MainWindow;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration("classpath:spring/application-context.xml")
public class TarjontaEmbedComponentTstSupport<COMPONENT extends Component> extends AbstractEmbedVaadinTest<COMPONENT> {

    protected MainWindowPageObject mainWindowPageObject;

    public TarjontaEmbedComponentTstSupport() {
        super(true, true);
    }

    @Override
    public void initPageObjects() {
        mainWindowPageObject = new MainWindowPageObject(driver, getComponentByType(MainWindow.class));
    }

}
