package fi.vm.sade.tarjonta.selenium.pageobject;

import fi.vm.sade.support.selenium.SeleniumUtils;
import fi.vm.sade.support.selenium.VaadinPageObjectSupport;
import fi.vm.sade.tarjonta.ui.MainWindow;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static fi.vm.sade.support.selenium.SeleniumUtils.*;

/**
 * @author Antti Salonen
 */
public class HakueraListPageObject extends VaadinPageObjectSupport<HakueraList> {

    public HakueraListPageObject(WebDriver driver, HakueraList component) {
        super(driver, component);
    }

    @Override
    public void initPageObjects() {
    }

    public int getResultCount() {
        List<WebElement> items = getItems();
        return items.size();
    }

    public List<WebElement> getItems() {
        WebElement table = getWebElementForDebugId(component.getTable().getDebugId());
        return table.findElements(By.xpath("//tr[contains(@class,'v-table-row')]"));
    }

    public void clickFilters(boolean paattyneet, boolean meneillaan, boolean tulevat) {
        if (paattyneet) {
            click(By.id(component.getPaattyneet().getDebugId()));
        }
        if (meneillaan) {
            click(By.id(component.getMeneillaan().getDebugId()));
        }
        if (paattyneet) {
            click(By.id(component.getTulevat().getDebugId()));
        }
    }

    public void clickHakuera(int index) {
        WebElement item = getItems().get(index);
        item.click();
    }

    public String getItemNimi(int index) {
        return getItems().get(index).findElement(By.tagName("td")).getText();
    }
}
