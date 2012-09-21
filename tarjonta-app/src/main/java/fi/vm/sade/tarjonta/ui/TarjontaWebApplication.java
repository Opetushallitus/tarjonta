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

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import fi.vm.sade.generic.ui.app.AbstractSadeApplication;

/**
 * Tarjonta WEB application - used for testing and development.
 *
 * @author mlyly
 */
public class TarjontaWebApplication extends AbstractSadeApplication {

    private Window window;

    @Override
    public synchronized void init() {
        super.init();

        window = new Window("Tarjonta");

        window.addComponent(new Label("TARJONTA"));

        // Component rootComponent = new SampleViewBuilder().createRootComponent(window);
        // window.addComponent(rootComponent);

        setMainWindow(window);
    }

}
