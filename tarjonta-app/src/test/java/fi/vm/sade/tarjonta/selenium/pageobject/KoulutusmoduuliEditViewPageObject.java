package fi.vm.sade.tarjonta.selenium.pageobject;

import fi.vm.sade.support.selenium.VaadinPageObjectSupport;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.KoulutusmoduuliEditView;
import org.openqa.selenium.WebDriver;

/**
 * @author Antti
 */
public class KoulutusmoduuliEditViewPageObject extends VaadinPageObjectSupport<KoulutusmoduuliEditView> {

    public KoulutusmoduuliEditViewPageObject(WebDriver driver, KoulutusmoduuliEditView component) {
        super(driver, component);
    }

    @Override
    public void initPageObjects() {
    }
}
