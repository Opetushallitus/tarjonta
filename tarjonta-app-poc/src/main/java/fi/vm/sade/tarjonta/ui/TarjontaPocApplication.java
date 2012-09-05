package fi.vm.sade.tarjonta.ui;

import com.vaadin.Application;
import com.vaadin.ui.*;
import fi.vm.sade.tarjonta.ui.poc.MainSplitPanel;
import fi.vm.sade.tarjonta.ui.poc.TarjontaWindow;
import fi.vm.sade.tarjonta.ui.poc.UI;
import fi.vm.sade.vaadin.Oph;

public class TarjontaPocApplication extends Application {

    /**
     *
     */
    private static final long serialVersionUID = -4776083098854013839L;

    @Override
    public void init() {
        TarjontaWindow win = new TarjontaWindow();
        setMainWindow(win);
       
       setTheme("tarjonta");
    }
}
