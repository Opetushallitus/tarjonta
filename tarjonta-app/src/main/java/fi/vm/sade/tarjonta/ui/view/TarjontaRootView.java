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
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;

import fi.vm.sade.tarjonta.ui.TarjontaWebApplication;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.view.common.BreadcrumbsView;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatiohakuView;
import fi.vm.sade.tarjonta.ui.view.common.SearchSpesificationView;
import fi.vm.sade.tarjonta.ui.view.haku.EditHakuViewImpl;
import fi.vm.sade.tarjonta.ui.view.haku.HakuResultRow;
import fi.vm.sade.tarjonta.ui.view.haku.ListHakuViewImpl;
import fi.vm.sade.tarjonta.ui.view.koulutus.EditKoulutusPerustiedotToinenAsteView;
import fi.vm.sade.tarjonta.ui.view.koulutus.ListKoulutusView;
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
@Configurable(preConstruction = true)
public class TarjontaRootView extends Window {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaRootView.class);
    @Autowired(required = true)
    private TarjontaPresenter _presenter;
    private VerticalLayout _appRootLayout;
    private OrganisaatiohakuView _organisationSearchView;
    private BreadcrumbsView _breadcrumbsView;
    private SearchSpesificationView _searchSpesificationView;
    private SearchResultsView _searchResultsView;
    //Huom tämä on vain kehityksen ajaksi tehty kenttä, mahdollistaa vaihtamisen
    //Haku- ja Tarjontasovellusten välillä.
    private TarjontaWebApplication tWebApp;

    //Tämä on vain kehitystä helpottamaan tehty konstruktori.
    public TarjontaRootView(TarjontaWebApplication tWebApp) {
        super();
        init();
        this.tWebApp = tWebApp;
    }

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
        // Create root layout
        //
        _appRootLayout = UiBuilder.verticalLayout();
        _appRootLayout.setHeight(-1, UNITS_PIXELS);
        _appRootLayout.addStyleName(Oph.CONTAINER_MAIN);
        setContent(_appRootLayout); // root layout

        //
        // Create components
        //
        _organisationSearchView = new OrganisaatiohakuView();
        _breadcrumbsView = new BreadcrumbsView();
        _searchSpesificationView = new SearchSpesificationView();
        _searchResultsView = new SearchResultsView();

        // Add listener for search events
        _searchSpesificationView.addListener(new Listener() {
            @Override
            public void componentEvent(Event event) {
                if (event instanceof SearchSpesificationView.SearchEvent) {
                    SearchSpesificationView.SearchEvent searchEvent = (SearchSpesificationView.SearchEvent) event;
                    LOG.error("SEARCH NOT IMPLEMENTED: searchEvent={}", searchEvent);
                }
            }
        });
        
        //Handles navigation to different child views (edit haku, view haku)
        _searchResultsView.addListener(new Listener() {

            @Override
            public void componentEvent(Event event) {
            	if (event instanceof ListKoulutusView.NewKoulutusEvent) {
                    showKoulutusEdit(new HakuViewModel());
                }
            }

        });

        _presenter.setRootView(this);

        // Show application identifier if needed
        if (_presenter != null && _presenter.isShowIdentifier()) {
            _appRootLayout.addComponent(new Label("ID=" + _presenter.getIdentifier()));
        }

        // The default view to show is main default view (called here since "main" app cannot access presenter)
        _presenter.showMainDefaultView();
    }

    protected void showKoulutusEdit(HakuViewModel hakuViewModel) {
    	LOG.info("showMainDefaultView()");

        OrganisaatiohakuView organisaatiohakuView = new OrganisaatiohakuView(null);
        getAppRootLayout().addComponent(organisaatiohakuView);
        VerticalLayout vl = UiUtil.verticalLayout();
        vl.setHeight(-1, VerticalLayout.UNITS_PIXELS);
        vl.addComponent(getBreadcrumbsView());
        vl.addComponent(new EditKoulutusPerustiedotToinenAsteView());
        organisaatiohakuView.addComponent(vl);
        organisaatiohakuView.setExpandRatio(vl, 1f);
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
