package fi.vm.sade.tarjonta.selenium.pageobject;

import fi.vm.sade.support.selenium.VaadinPageObjectSupport;
import fi.vm.sade.tarjonta.ui.haku.HakuEditForm;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static fi.vm.sade.support.selenium.SeleniumUtils.click;
import static fi.vm.sade.support.selenium.SeleniumUtils.getWebElementForDebugId;

/**
 * @author Antti Salonen
 */
public class HakueraEditFormPageObject extends VaadinPageObjectSupport<HakuEditForm> {

    public HakueraEditFormPageObject(WebDriver driver, HakuEditForm component) {
        super(driver, component);
    }


}
