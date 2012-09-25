package fi.vm.sade.tarjonta.ui.view.haku;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;

import fi.vm.sade.vaadin.ui.OphRowMenuBar;
import fi.vm.sade.vaadin.util.UiUtil;

public class HakuResultRow {
    
    private MenuBar.Command menuCommand = new MenuBar.Command() {
        @Override
        public void menuSelected(MenuBar.MenuItem selectedItem) {
        }
        
    };
    OphRowMenuBar rowMenuBar;

    private OphRowMenuBar newMenuBar() {
        rowMenuBar = new OphRowMenuBar("../oph/img/icon-treetable-button.png");
        rowMenuBar.addMenuCommand("Luo uusi koulutus", menuCommand);
        rowMenuBar.addMenuCommand("Näytä hakukohteet", menuCommand);
        rowMenuBar.addMenuCommand("Poista", menuCommand);
        return rowMenuBar;
    }

    public HorizontalLayout format(String text) {
        CheckBox newCheckbox = UiUtil.checkbox(null, null);
        Label label = new Label(text);
        label.setSizeUndefined(); // -1,-1
        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.setWidth(-1, Sizeable.UNITS_PIXELS);
        horizontal.setHeight(-1, Sizeable.UNITS_PIXELS); //Tämä toimii!!!

        horizontal.addComponent(newCheckbox);
        horizontal.addComponent(newMenuBar());
        horizontal.addComponent(label);

        horizontal.setExpandRatio(label, 1f); //default == 0

        return horizontal;
    }

}
