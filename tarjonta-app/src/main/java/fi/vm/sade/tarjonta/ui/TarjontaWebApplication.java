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

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;
import fi.vm.sade.generic.ui.app.AbstractSadeApplication;
import fi.vm.sade.tarjonta.ui.view.HakuRootView;
import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;

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

        //HUOM! HakuRootView on nyt asetettu perusikkunaksi, TarjontaRootView:llä näkyy tarjonta.
        window = new HakuRootView();//TarjontaRootView();
        setMainWindow(window);
    }

}
