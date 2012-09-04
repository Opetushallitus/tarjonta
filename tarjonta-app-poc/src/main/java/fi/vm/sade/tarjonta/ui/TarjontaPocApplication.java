package fi.vm.sade.tarjonta.ui;

import com.vaadin.Application;
import com.vaadin.ui.*;
import fi.vm.sade.tarjonta.ui.poc.Main;
import fi.vm.sade.tarjonta.ui.poc.UI;
import fi.vm.sade.vaadin.Oph;

public class TarjontaPocApplication extends Application {

    /**
     *
     */
    private static final long serialVersionUID = -4776083098854013839L;

    @Override
    public void init() {
        Main main = new Main();
        Window mainWindow = new Window("Tarjonta", main);
        mainWindow.setSizeFull();
        setMainWindow(mainWindow);
        main.setMainWindow(mainWindow);
        
        if (UI.isThemeOPH()) {
            setTheme(Oph.THEME_NAME);
        }
    }
}
