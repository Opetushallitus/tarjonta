package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;


public class RowMenuBar extends MenuBar {

    public RowMenuBar() {
        this.setWidth(-1, UNITS_PIXELS);

        final MenuBar.MenuItem file = this.addItem("", new ThemeResource("../../themes/oph/img/search.png"), null);
       
        file.addItem("File", menuCommand);
        file.addItem("Folder", menuCommand);
        file.addItem("Project...", menuCommand);
    }

    private Command menuCommand = new Command() {
        @Override
        public void menuSelected(MenuItem selectedItem) {
            getWindow().showNotification("Action " + selectedItem.getText());
        }
    };

}