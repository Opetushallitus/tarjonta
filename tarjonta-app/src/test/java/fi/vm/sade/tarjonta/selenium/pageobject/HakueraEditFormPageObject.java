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
import static fi.vm.sade.support.selenium.SeleniumUtils.waitForElement;

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
        WebElement nameFi = waitForNimiFi();
        String curValue = nameFi.getAttribute("value");
        SeleniumUtils.input(nameFi, curValue + " " + string + " FI");
        WebElement nameSv = waitForNimiSv();
        curValue = nameSv.getAttribute("value");
        SeleniumUtils.input(nameSv, curValue + " " + string + " SV");
        WebElement nameEn = waitForNimiEn();
        curValue = nameEn.getAttribute("value");
        SeleniumUtils.input(nameEn, curValue + " " + string + " EN");
    }

    public WebElement waitForNimiEn() {
        return SeleniumUtils.waitForElement(component.getHaunNimi().getTextEn());
    }

    public WebElement waitForNimiSv() {
        return SeleniumUtils.waitForElement(component.getHaunNimi().getTextSv());
    }

    public WebElement waitForNimiFi() {
        return SeleniumUtils.waitForElement(component.getHaunNimi().getTextFi());
    }

    public void inputDefaultFields() {
        selectHakutyyppi("Varsinainen haku");
        selectHakukausi("Syksy");
        selectAlkamiskausi("Syksy 2013");
        selectKohdejoukko("Korkeakoulutus");
        selectHakutapa("Yhteishaku");
        inputNames("paattynyt");
    }
    
    public void inputCustomFields(String hakutyyppi, String hakukausi, String alkamiskausi, String kohdejoukko, String hakutapa, String namePostfix) {
        selectHakutyyppi(((hakutyyppi != null) ? hakutyyppi : "Varsinainen haku"));
        selectHakukausi(((hakukausi != null) ? hakukausi : "Syksy"));
        selectAlkamiskausi(((alkamiskausi != null) ? alkamiskausi : "Syksy 2013"));
        selectKohdejoukko(((kohdejoukko != null) ? kohdejoukko : "Korkeakoulutus"));
        selectHakutapa(((hakutapa != null) ? hakutapa : "Yhteishaku"));
        inputNames(((namePostfix != null) ? namePostfix : "paattynyt"));
    }

    private void selectHakutapa(String string) {
        selectKoodistoWithDebugId(component.getHakutapaKoodi().getDebugId(), string); 
    }

    private void selectKohdejoukko(String string) {
        selectKoodistoWithDebugId(component.getHaunKohdejoukkoKoodi().getDebugId(), string); 
    }

    private void selectAlkamiskausi(String string) {
        selectKoodistoWithDebugId(component.getKoulutuksenAlkamiskausiKoodi().getDebugId(), string);
    }

    private void selectHakukausi(String string) {
        selectKoodistoWithDebugId(component.getHakukausiKoodi().getDebugId(), string);
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
    
    public String getNimiFiValue() {
        WebElement nameFi = SeleniumUtils.waitForElement(component.getHaunNimi().getTextFi());
        return nameFi.getAttribute("value");
    }
    
    public String getNimiSvValue() {
        WebElement nameFi = SeleniumUtils.waitForElement(component.getHaunNimi().getTextSv());
        return nameFi.getAttribute("value");
    }
    
    public String getNimiEnValue() {
        WebElement nameFi = SeleniumUtils.waitForElement(component.getHaunNimi().getTextSv());
        return nameFi.getAttribute("value");
    }

    public WebElement waitForNimi() {
        return waitForElement(component.getHaunNimi());
    }

    public WebElement waitForHakulomakeUrl() {
        return waitForElement(component.getHakulomakeUrl());
    }    
}
