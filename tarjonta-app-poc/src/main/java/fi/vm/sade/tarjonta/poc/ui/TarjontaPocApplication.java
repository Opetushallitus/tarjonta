package fi.vm.sade.tarjonta.poc.ui;

import com.vaadin.Application;
import org.springframework.beans.factory.annotation.Configurable;


@Configurable(preConstruction = false)
public class TarjontaPocApplication extends Application {

    /**
     *
     */
    private static final long serialVersionUID = -4776083098854013839L;

    @Override
    public void init() {
        TarjontaWindow win = new TarjontaWindow();
        setMainWindow(win);

         setTheme("oph-app-tarjonta");
    }
}
