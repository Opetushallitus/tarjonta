/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

import fi.vm.sade.generic.ui.app.AbstractSadeApplication;
import fi.vm.sade.tarjonta.ui.view.HakuRootView;
import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

/**
 * Tarjonta WEB application - used for testing and development. For development
 * purposes the app has methods to switch between Haku and Tarjonta management
 * apps.
 *
 * @author mlyly
 */
@Configurable(preConstruction = true)
public class TarjontaWebApplication extends AbstractSadeApplication {

    private Window window;
    @Value("${tarjonta-app.dev.redirect:}")
    private String developmentRedirect;
    @Value("${tarjonta-app.dev.theme:}")
    private String developmentTheme;

    @Override
    public synchronized void init() {
        super.init();

        window = new Window("Valitse");
        setMainWindow(window);

       developmentConfiguration();

        Button tarjontaButton = new Button("Tarjontaan", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                toTarjonta();
            }
        });
        window.addComponent(tarjontaButton);

        Button hakuButton = new Button("Hakuihin", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                toHaku();
            }
        });
        window.addComponent(hakuButton);
    }

    public void toTarjonta() {
        this.removeWindow(window);
        window = new TarjontaRootView();
        setMainWindow(window);
    }

    public void toHaku() {
        this.removeWindow(window);
        window = new HakuRootView(this);
        setMainWindow(window);
    }

    /*
     * Development configurations, no real use in production environment.
     */
    private void developmentConfiguration(){
         if (developmentTheme != null && developmentTheme.length() > 0) {
            //set a development theme.
            setTheme(developmentTheme);
        }

        if (developmentRedirect != null && developmentRedirect.length() > 0) {
            //This code block is only for making UI development little bit faster
            //Add the property to tarjonta-app.properties:
            //
            //tarjonta-app.dev.redirect=KOULUTUS

            if (developmentRedirect.equalsIgnoreCase("HAKU")) {
                toHaku();
            }

            if (developmentRedirect.equalsIgnoreCase("KOULUTUS")) {
                toTarjonta();
            }
        }

    }
}
