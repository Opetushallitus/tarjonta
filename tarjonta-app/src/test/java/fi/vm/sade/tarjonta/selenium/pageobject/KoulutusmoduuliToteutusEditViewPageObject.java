package fi.vm.sade.tarjonta.selenium.pageobject;

import com.vaadin.ui.AbstractSelect;
import fi.vm.sade.generic.ui.component.MultipleSelectToTableWrapper;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.support.selenium.VaadinPageObjectSupport;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus.KoulutusmoduuliToteutusEditView;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.support.selenium.SeleniumUtils.*;

/**
 * @author Antti Salonen
 */
public class KoulutusmoduuliToteutusEditViewPageObject extends VaadinPageObjectSupport<KoulutusmoduuliToteutusEditView> {

    public KoulutusmoduuliToteutusEditViewPageObject(WebDriver driver, KoulutusmoduuliToteutusEditView component) {
        super(driver, component);
    }

    @Override
    public void initPageObjects() {
    }

    public List<String> getOpetuskieliOptions() {
        return getOptions(getCombo(component.getOpetuskielis()));
    }

    public List<String> getOpetusmuotoOptions() {
        return getOptions(getCombo(component.getOpetusmuotos()));
    }

    private WebElement getCombo(MultipleSelectToTableWrapper wrapper) {
        AbstractSelect combo = ((KoodistoComponent) wrapper.getField()).getField();
        return waitForElement(combo);
    }

    public void addOpetuskieli(String value) {
        addItem(value, component.getOpetuskielis());
    }

    public void addOpetusmuoto(String value) {
        addItem(value, component.getOpetusmuotos());
    }

    private void addItem(String value, MultipleSelectToTableWrapper wrapper) {
        select(getCombo(wrapper), value);
        click(By.id(wrapper.getAddButton().getDebugId()));
    }

    public void removeFirstOpetuskieli() {
        removeFirstItem(component.getOpetuskielis());
    }

    public void removeFirstOpetusmuoto() {
        removeFirstItem(component.getOpetusmuotos());
    }

    private void removeFirstItem(MultipleSelectToTableWrapper wrapper) {
        WebElement removeBtn = getDriver().findElement(By.xpath("//div[@id='"+wrapper.getDebugId()+"']//span[contains(.,'-')]"));
        removeBtn.click();
    }

    public List<String> getOpetuskieliSelected() {
        return getSelected(component.getOpetuskielis());
    }

    public List<String> getOpetusmuotoSelected() {
        return getSelected(component.getOpetusmuotos());
    }

    private List<String> getSelected(MultipleSelectToTableWrapper wrapper) {
        List<WebElement> items = waitForElement(wrapper.getTable()).findElements(By.xpath("//tr/td"));
        List<String> result = new ArrayList<String>();
        for (WebElement item : items) {
            result.add(item.getText());
        }
        return result;
    }

    public List<String> getKoulutusmoduuliOptions() {
        return getOptions(waitForElement(component.getKoulutusmoduuliTextfield().getField()));
    }

    public void selectKoulutusmoduuli(String koulutusmoduuli) {
        select(component.getKoulutusmoduuliTextfield().getField(), koulutusmoduuli);
    }
}
