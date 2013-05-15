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

import com.vaadin.ui.Window;

import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;
import fi.vm.sade.vaadin.Oph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Tarjonta WEB application - used for testing and development. For development
 * purposes the app has methods to switch between Haku and Tarjonta management
 * apps.
 *
 * @author mlyly
 */
@Configurable(preConstruction = true)
public class TarjontaApplication extends WebApplication {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaApplication.class);
    private static final long serialVersionUID = 7402559260126333807L;
    private Window window;

    @Override
    protected void initApplication() {
        window = new TarjontaRootView(true);
        setMainWindow(window);
        setTheme(Oph.THEME_NAME);
    }
}