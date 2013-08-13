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
package fi.vm.sade.tarjonta.ui.view;

import fi.vm.sade.tarjonta.ui.presenter.ValintaperustekuvausPresenter;
import com.vaadin.ui.AbstractLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Window;
import fi.vm.sade.tarjonta.ui.AbstractWebApplication;

import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.vaadin.Oph;

/**
 * Root view for Valintaperustekuvaus management.
 *
 * @author janiw
 */
@Configurable(preConstruction = true)
public class ValintaperustekuvausRootView extends Window {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaRootView.class);
    private static final long serialVersionUID = -942466086095854495L;
    private AbstractLayout appRootLayout;
    @Autowired(required = true)
    private ValintaperustekuvausPresenter valintaPresenter;

    public ValintaperustekuvausRootView() {
        super();
        init();
    }

    private void init() {
        // Fixi jrebelille...
        if (valintaPresenter == null) {
            valintaPresenter = new ValintaperustekuvausPresenter();
        }
        valintaPresenter.setRootView(this);

        // Create root layout
        appRootLayout = UiBuilder.verticalLayout();
        appRootLayout.addStyleName(Oph.CONTAINER_MAIN);
        appRootLayout.setSizeFull();
        appRootLayout.setHeight(-1, UNITS_PIXELS);
        setContent(appRootLayout);

        // Create main layout
        valintaPresenter.showMainView();
    }

    /**
     * Show main default view
     */
    public void addToWin(AbstractLayout layout) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("addToWin()");
        }

        appRootLayout.removeAllComponents();
        appRootLayout.addComponent(layout);

        // Make session to stay alive with small timeout
        appRootLayout.addComponent(AbstractWebApplication.createRefersh("ValintaperustekuvausRootView.addToWin()"));
    }
}
