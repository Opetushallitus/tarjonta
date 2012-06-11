package fi.vm.sade.tarjonta.selenium.pageobject;

import fi.vm.sade.support.selenium.SeleniumUtils;
import fi.vm.sade.support.selenium.VaadinPageObjectSupport;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraEditForm;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import javax.annotation.Nullable;

import static fi.vm.sade.support.selenium.SeleniumUtils.waitFor;

/**
 * @author Antti Salonen
 */
public class HakueraEditFormPageObject extends VaadinPageObjectSupport<HakueraEditForm> {

    public HakueraEditFormPageObject(WebDriver driver, HakueraEditForm component) {
        super(driver, component);
    }

    public void selectHakutyyppi(String hakutyyppiCode) {
        selectKoodistoWithDebugId(component.getHakutyyppiKoodi().getDebugId(), hakutyyppiCode);
    }

    public void inputNames(String string) {
        WebElement nameFi = SeleniumUtils.waitForElement(component.getHaunNimi().getTextFi());
        SeleniumUtils.input(nameFi, string + " paattynyt FI");
        WebElement nameSv = SeleniumUtils.waitForElement(component.getHaunNimi().getTextSv());
        SeleniumUtils.input(nameSv, string + " paattynyt SV");
        WebElement nameEn = SeleniumUtils.waitForElement(component.getHaunNimi().getTextEn());
        SeleniumUtils.input(nameEn, string + " paattynyt EN");
    }

    public void inputDefaultFields() {
        selectHakutyyppi("Ammatilliset");
        inputNames("Testihakuera");
    }

    public void save() {
        WebElement saveButton = SeleniumUtils.waitForElement(component.getSaveButton());
        saveButton.click();
    }

    public void selectKoodistoWithDebugId(String debugId, final String optionText) {
        final WebElement btn = driver.findElement(By.xpath("//td/div[contains(@id,'" + debugId + "')]//*[@class='v-filterselect-button']"));

        WebElement option = waitFor("KoodistoComponent not found: " + optionText, new ExpectedCondition<WebElement>() {

            @Override
            public WebElement apply(@Nullable WebDriver webDriver) {
                btn.click();
                return driver.findElement(By.xpath("//td[@class='gwt-MenuItem']/span[contains(.,'" + optionText + "')]"));
            }

        });
        option.click();
    }
}
