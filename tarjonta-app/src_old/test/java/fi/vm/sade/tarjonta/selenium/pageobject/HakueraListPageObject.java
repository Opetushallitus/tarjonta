package fi.vm.sade.tarjonta.selenium.pageobject;

import com.vaadin.ui.Component;
import fi.vm.sade.support.selenium.VaadinPageObjectSupport;
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
public class HakueraListPageObject extends VaadinPageObjectSupport<HakueraList> {

    public HakueraListPageObject(WebDriver driver, HakueraList component) {
        super(driver, component);
    }

    public int getResultCount() {
        List<WebElement> items = getItems();
        return items.size();
    }

    public List<WebElement> getItems() {
        WebElement table = getWebElementForDebugId(component.getTable().getDebugId());
        return table.findElements(By.xpath("//tr[contains(@class,'v-table-row')]"));
    }

    /**
     * Convenience method that triggers a click on the WebElement of type "input" represented by given 
     * Vaadin component.
     * 
     * @param component the component to click
     */
    private void clickInput(Component component) {
        click(By.id(component.getDebugId()), "input");
    }
    
    public void clickFilters(boolean paattyneet, boolean meneillaan, boolean tulevat) {
        if (paattyneet) {
            clickInput(component.getPaattyneet());            
        }
        if (meneillaan) {
            clickInput(component.getMeneillaan());
        }
        if (tulevat) {
            clickInput(component.getTulevat());
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
