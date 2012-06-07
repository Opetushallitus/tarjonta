package fi.vm.sade.tarjonta.selenium.pageobject;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.support.selenium.SeleniumContext;
import fi.vm.sade.support.selenium.SeleniumUtils;
import fi.vm.sade.support.selenium.VaadinPageObjectSupport;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.KoulutusmoduuliEditView;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.tutkintoohjelma.TutkintoOhjelmaEditPanel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author Antti
 */
public class KoulutusmoduuliEditViewPageObject extends VaadinPageObjectSupport<KoulutusmoduuliEditView> {

    public KoulutusmoduuliEditViewPageObject(WebDriver driver, KoulutusmoduuliEditView component) {
        super(driver, component);
    }

    public KoulutusmoduuliEditViewPageObject(KoulutusmoduuliEditView component) {
        this(SeleniumContext.getDriver(), component);
    }

    @Override
    public void initPageObjects() {
    }

    public void setKoulutus(String value) {
        SeleniumUtils.select(getKoulutusWebElement(), value);
    }

    public WebElement getKoulutusWebElement() {
        return SeleniumUtils.waitForElement(getTutkintoOhjelmaEditPanel().getKoulutusComponent().getField());
    }

    /**
     * Convenience method that assumes that currently selected edit panel is of type Tutkinto ohjelma -edit panel.
     *
     * @return
     */
    public TutkintoOhjelmaEditPanel getTutkintoOhjelmaEditPanel() {
        return (TutkintoOhjelmaEditPanel) component.getKoulutusmoduuliEditPanel();
    }

    public void selectNewKoulutusmoduuliTutkintoonJohtava() {
        WebElement combo = SeleniumUtils.waitForElement(component.getCreateNewModuuli());
        SeleniumUtils.select(combo, I18N.getMessage("KoulutusmoduuliEditView.uusiKoulutusmoduuliSelect.tutkintoonJohtava"));
    }

    public void clickSaveAsDraft() {
        SeleniumUtils.waitForElement(component.getSaveAsDraftButton()).click();
    }

    public void clickSaveAsFinal() {
        SeleniumUtils.waitForElement(component.getSaveAsFinalButton()).click();
    }

}

