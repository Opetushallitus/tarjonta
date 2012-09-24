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

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.view.common.BreadcrumbsView;
import fi.vm.sade.tarjonta.ui.view.common.SearchSpesificationView;
import fi.vm.sade.vaadin.Oph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Simple root view.
 *
 * <pre>
 * Structure:
 * ========================
 * VL (root layout)
 * - HL (app root layout)
 * -- VL (left layout)
 * -- VL (right layout)
 * </pre>
 *
 * @author mlyly
 */
@Configurable
public class TarjontaRootView extends Window {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaRootView.class);

    @Autowired(required = true)
    private TarjontaPresenter _presenter;

    private HorizontalLayout _appRootLayout;
    private VerticalLayout _appLeftLayout;
    private VerticalLayout _appRightLayout;

    private BreadcrumbsView _breadcrumbsView;
    private SearchSpesificationView _searchSpesificationView;

    public TarjontaRootView() {
        super();
        LOG.info("TarjontaView(): presenter={}", _presenter);

        //
        // Create components
        //
        _breadcrumbsView = new BreadcrumbsView();
        _searchSpesificationView = new SearchSpesificationView();

        // Initialize presenter with root window
        _presenter.setTarjontaWindow(this);

        // Create root layout
        VerticalLayout layout = UiBuilder.newVerticalLayout();
        layout.setHeight(-1,UNITS_PIXELS);
        layout.addStyleName(Oph.CONTAINER_MAIN);
        setContent(layout); // root layout

        // Create application layout and add to root
        _appRootLayout = UiBuilder.newHorizontalLayout();
        layout.addComponent(_appRootLayout);

        // Create left side
        _appLeftLayout = UiBuilder.newVerticalLayout();
        _appRootLayout.addComponent(_appLeftLayout);

        // Create right side
        _appRightLayout = UiBuilder.newVerticalLayout();
        _appRootLayout.addComponent(_appRightLayout);

        // Show application identifier if needed
        if (_presenter != null && _presenter.isShowIdentifier()) {
            layout.addComponent(new Label("ID=" + _presenter.getIdentifier()));
        }

        _presenter.showMainDefaultView();
    }

    public HorizontalLayout getAppRootLayout() {
        return _appRootLayout;
    }

    public VerticalLayout getAppLeftLayout() {
        return _appLeftLayout;
    }

    public VerticalLayout getAppRightLayout() {
        return _appRightLayout;
    }

    public SearchSpesificationView getSearchSpesificationView() {
        return _searchSpesificationView;
    }

    public BreadcrumbsView getBreadcrumbsView() {
        return _breadcrumbsView;
    }

}
