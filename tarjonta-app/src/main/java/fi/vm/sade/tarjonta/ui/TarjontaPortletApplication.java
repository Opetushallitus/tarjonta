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

import com.github.wolfie.blackboard.Blackboard;
import com.vaadin.ui.Window;
import fi.vm.sade.generic.ui.app.AbstractSadePortletApplication;
import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO since UI is in flux this portlet contains "Haku" functonality which will be moved to "Haut ja valinnat" part at some point.
 *
 * @author mlyly
 */
public class TarjontaPortletApplication extends AbstractSadePortletApplication {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaPortletApplication.class);

    private Window window;

    @Override
    protected void registerListeners(Blackboard blackboard) {
        LOG.debug("registerListeners()");
    }

    @Override
    public synchronized void init() {
        LOG.debug("init()");

        super.init();

        window = new TarjontaRootView();
        setMainWindow(window);
    }
}
