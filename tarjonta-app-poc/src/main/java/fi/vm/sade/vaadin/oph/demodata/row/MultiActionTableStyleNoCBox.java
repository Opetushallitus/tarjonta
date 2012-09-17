package fi.vm.sade.vaadin.oph.demodata.row;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import fi.vm.sade.vaadin.oph.layout.RowMenuBar;
import fi.vm.sade.vaadin.oph.demodata.ITableRowFormat;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;

/**
 *
 * @author jani
 */
public class MultiActionTableStyleNoCBox implements ITableRowFormat<HorizontalLayout> {

    private MenuBar.Command menuCommand = new MenuBar.Command() {
        @Override
        public void menuSelected(MenuBar.MenuItem selectedItem) {
        }
    };
    RowMenuBar rowMenuBar;

    private RowMenuBar newMenuBar() {
        rowMenuBar = new RowMenuBar("../oph/img/icon-treetable-button.png");
        rowMenuBar.addMenuCommand("Luo uusi koulutus", menuCommand);
        rowMenuBar.addMenuCommand("Näytä hakukohteet", menuCommand);
        rowMenuBar.addMenuCommand("Poista", menuCommand);

        return rowMenuBar;
    }

    @Override
    public HorizontalLayout format(String text) {
        Label label = new Label(text);
        label.setSizeUndefined(); // -1,-1
        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.setWidth(-1, Sizeable.UNITS_PIXELS);
        horizontal.setHeight(-1, Sizeable.UNITS_PIXELS); //Tämä toimii!!!

        horizontal.addComponent(newMenuBar());
        horizontal.addComponent(label);

        horizontal.setExpandRatio(label, 1f); //default == 0

        return horizontal;
    }
}
