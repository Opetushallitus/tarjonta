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
        Window mainWindow = new Window("Tarjonta", new Main());
        mainWindow.setSizeFull();
        setMainWindow(mainWindow);

        if (UI.isThemeOPH()) {
            setTheme(Oph.THEME_NAME);
        }
    }
}
