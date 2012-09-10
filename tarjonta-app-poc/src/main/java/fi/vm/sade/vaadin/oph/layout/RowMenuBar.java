package fi.vm.sade.vaadin.oph.layout;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;

public class RowMenuBar extends MenuBar {

    private MenuBar.MenuItem file;

    public RowMenuBar(String iconUrl) {
        this.setWidth(-1, UNITS_PIXELS);
        file = this.addItem("", new ThemeResource(iconUrl), null);
    }

    public void addMenuCommand(String caption, Command command) {
        file.addItem(caption, command);
    }
    
}