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

import fi.vm.sade.tarjonta.ui.TarjontaWebApplication;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.view.common.BreadcrumbsView;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatiohakuView;
import fi.vm.sade.tarjonta.ui.view.common.SearchSpesificationView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Simple root view for Tarjonta.
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
@Configurable(preConstruction=true)
public class TarjontaRootView extends Window {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaRootView.class);

    @Autowired(required = true)
    private TarjontaPresenter _presenter;

    private HorizontalLayout _appRootLayout;
    //private VerticalLayout _appLeftLayout;
    private HorizontalLayout _appRightLayout;

    private OrganisaatiohakuView _organisationSearchView;
    private BreadcrumbsView _breadcrumbsView;
    private SearchSpesificationView _searchSpesificationView;
    private SearchResultsView _searchResultsView;

    public TarjontaRootView() {
        super();
        init();
    }

    private void init() {
        LOG.info("TarjontaView(): presenter={}", _presenter);

        // Fixi jrebelille...
        if (_presenter == null) {
            _presenter = new TarjontaPresenter();
        }

        //
        // Create components
        //
        _organisationSearchView = new OrganisaatiohakuView();
        _breadcrumbsView = new BreadcrumbsView();
        _searchSpesificationView = new SearchSpesificationView();
        _searchResultsView = new SearchResultsView();


        _searchSpesificationView.addListener(new Listener() {

            @Override
            public void componentEvent(Event event) {
                if (event instanceof SearchSpesificationView.SearchEvent) {
                    throw new UnsupportedOperationException("Not supported yet. GET THE SEARCH SPEC FROM THE EVENT!!!");
                } else {
                    throw new RuntimeException("illegal event from SearchSpesificationView");
                }
            }
        });

        
        _presenter.setTarjontaWindow(this);

        // Create root layout
        VerticalLayout layout = UiBuilder.verticalLayout();
        layout.setHeight(-1,UNITS_PIXELS);
        layout.addStyleName(Oph.CONTAINER_MAIN);
        setContent(layout); // root layout

        // Create application layout and add to root
        _appRootLayout = UiBuilder.horizontalLayout();
        layout.addComponent(_appRootLayout);

        // Create right side
        _appRightLayout = UiBuilder.horizontalLayout();//verticalLayout();
        _appRootLayout.addComponent(_appRightLayout);

        // Show application identifier if needed
        if (_presenter != null && _presenter.isShowIdentifier()) {
            layout.addComponent(new Label("ID=" + _presenter.getIdentifier()));
        }

        // The default view to show is main default view (called here since "main" app cannot access presenter)
        _presenter.showMainDefaultView();
    }

    public HorizontalLayout getAppRootLayout() {
        return _appRootLayout;
    }

    public HorizontalLayout getAppRightLayout() {
        return _appRightLayout;
    }

    public SearchSpesificationView getSearchSpesificationView() {
        return _searchSpesificationView;
    }

    public BreadcrumbsView getBreadcrumbsView() {
        return _breadcrumbsView;
    }

    public SearchResultsView getSearchResultsView() {
        return _searchResultsView;
    }

    public OrganisaatiohakuView getOrganisaatiohakuView() {
        return _organisationSearchView;
    }

}
