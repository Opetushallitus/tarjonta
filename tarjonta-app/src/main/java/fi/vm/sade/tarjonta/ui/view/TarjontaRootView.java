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

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.view.common.BreadcrumbsView;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatiohakuView;
import fi.vm.sade.tarjonta.ui.view.common.SearchSpesificationView;
import fi.vm.sade.tarjonta.ui.view.koulutus.ListKoulutusView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

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
@Configurable(preConstruction = true)
public class TarjontaRootView extends Window {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaRootView.class);
    @Autowired(required = true)
    private TarjontaPresenter _presenter;
    // Show label that shows last modification
    @Value("${common.showAppIdentifier:true}")
    private Boolean _showIdentifier;
    @Value("${tarjonta-app.identifier:APPLICATION IDENTIFIER NOT AVAILABLE}")
    private String _identifier;
    private VerticalLayout _appRootLayout;
    private OrganisaatiohakuView _organisationSearchView;
    private BreadcrumbsView _breadcrumbsView;
    private SearchSpesificationView _searchSpesificationView;
    private SearchResultsView _searchResultsView;
    private VerticalLayout vrightLayout;
    private boolean isAttached = false;

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
        _presenter.setRootView(this);
        initLayoutComponents();
    }

    private void initLayoutComponents() {
        // Create root layout
        _appRootLayout = UiBuilder.verticalLayout();
        _appRootLayout.setHeight(-1, UNITS_PIXELS);
        _appRootLayout.addStyleName(Oph.CONTAINER_MAIN);
        setContent(_appRootLayout); // root layout

        // Create components
        _organisationSearchView = new OrganisaatiohakuView();
        _breadcrumbsView = new BreadcrumbsView();
        _searchResultsView = new SearchResultsView();
        _searchSpesificationView = new SearchSpesificationView(_presenter.getModel().getSearchSpec());

        // Add listener for search events
        _searchSpesificationView.addListener(new Listener() {
            @Override
            public void componentEvent(Event event) {
                if (event instanceof SearchSpesificationView.SearchEvent) {
                    _presenter.getReloadKoulutusListData();
                    _presenter.getHakukohdeListView().reload();
                }
            }
        });

        //bind the components together
        vrightLayout = UiUtil.verticalLayout();
        vrightLayout.setHeight(-1, VerticalLayout.UNITS_PIXELS);
        vrightLayout.addComponent(this.getBreadcrumbsView());
        vrightLayout.addComponent(this.getSearchSpesificationView());
        vrightLayout.addComponent(this.getSearchResultsView());
        this.getOrganisaatiohakuView().addComponent(vrightLayout);
        this.getOrganisaatiohakuView().setExpandRatio(vrightLayout, 1f);

        // Show application identifier if needed
        _presenter.getModel().setShowIdentifier(_showIdentifier);
        _presenter.getModel().setIdentifier(_identifier);
        if (_presenter.isShowIdentifier()) {
            _appRootLayout.addComponent(new Label("ID=" + _presenter.getIdentifier()));
        }

        // The default view to show is main default view (called here since "main" app cannot access presenter)
    }

    @Override
    public void attach() {
        super.attach();
        if (isAttached) {
            return;
        }
        isAttached = true;
        showMainView();
    }

    public void showMainView() {
        LOG.debug("showMainView()");
        changeView(this.getOrganisaatiohakuView());
    }

    public ListKoulutusView getListKoulutusView() {
        return this.getSearchResultsView().getKoulutusList();
    }

    public void changeView(AbstractLayout layout) {
        changeView(layout, true);
    }

    public void changeView(AbstractLayout layout, boolean clear) {
        if (clear) {
            this.removeAllComponents();
        }

        this.addComponent(layout);
    }

    public VerticalLayout getAppRootLayout() {
        return _appRootLayout;
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
