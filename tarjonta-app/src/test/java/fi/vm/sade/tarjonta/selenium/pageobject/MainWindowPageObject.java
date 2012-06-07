package fi.vm.sade.tarjonta.selenium.pageobject;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.support.selenium.SeleniumUtils;
import fi.vm.sade.support.selenium.VaadinPageObjectSupport;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.ui.MainWindow;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.KoulutusmoduuliEditView;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author Antti Salonen
 */
public class MainWindowPageObject extends VaadinPageObjectSupport<MainWindow> {

    public MainWindowPageObject(WebDriver driver, MainWindow component) {
        super(driver, component);
    }

    @Override
    public void initPageObjects() {
    }

    public KoulutusmoduuliEditViewPageObject selectLuoUusiTutkintoonJohtava() {
        WebElement combo = SeleniumUtils.waitForElement(getKoulutusmoduuliEditView().getCreateNewModuuli());
        SeleniumUtils.select(combo, I18N.getMessage("KoulutusmoduuliEditView.uusiKoulutusmoduuliSelect.tutkintoonJohtava"));
        return new KoulutusmoduuliEditViewPageObject(driver, component.getKoulutusmoduuliEditView());
    }
    
    
    private KoulutusmoduuliEditView getKoulutusmoduuliEditView() {
        return component.getKoulutusmoduuliEditView();
    }
    
    
    public KoulutusmoduuliDTO getKoulutusmoduuli() {
        return getKoulutusmoduuliEditView().getKoulutusmoduuliDTO();
    }
}
