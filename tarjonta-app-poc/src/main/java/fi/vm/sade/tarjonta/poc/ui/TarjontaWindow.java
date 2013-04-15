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
package fi.vm.sade.tarjonta.poc.ui;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.tarjonta.poc.ui.view.MainSplitPanelView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public class TarjontaWindow extends Window {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaWindow.class);
    @Autowired(required = true)
    private TarjontaPresenter _presenter;
    private VerticalLayout layout;

    public TarjontaWindow() {
        super();
        LOG.info("TarjontaWindow(): {}", _presenter);

        _presenter.setTarjontaWindow(this);

        layout = UiUtil.verticalLayout();
        layout.setWidth(100, UNITS_PERCENTAGE);
        layout.setHeight(-1, UNITS_PIXELS);
        setContent(layout); //override default layout
        layout.addStyleName(Oph.CONTAINER_MAIN);

        if (_presenter != null && _presenter.showIdentifier()) {
            layout.addComponent(new Label("ID=" + _presenter.getIdentifier()));
        }
        _presenter.showMainKoulutusView();
    }

    public VerticalLayout getMainSplitPanel() {
        return layout;
    }
}
