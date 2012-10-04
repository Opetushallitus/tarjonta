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

import com.github.wolfie.blackboard.Listener;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;
import com.vaadin.ui.Component.Event;

import fi.vm.sade.generic.ui.app.AbstractSadeApplication;
import fi.vm.sade.tarjonta.ui.view.HakuRootView;
import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;
import fi.vm.sade.tarjonta.ui.view.haku.EditHakuViewImpl;

/**
 * Tarjonta WEB application - used for testing and development.
 * For development purposes the app has methods to switch between Haku and Tarjonta management apps.
 *
 * @author mlyly
 */
public class TarjontaWebApplication extends AbstractSadeApplication {

    private Window window;

    @Override
    public synchronized void init() {
        super.init();
       
        window = new HakuRootView(this);
        setMainWindow(window);
    }
    
    public void toTarjonta() {
        this.removeWindow(window);
        window = new TarjontaRootView(this);
        setMainWindow(window);
    }
    
    public void toHaku() {
        this.removeWindow(window);
        window = new HakuRootView(this);
        setMainWindow(window);
    }

}
