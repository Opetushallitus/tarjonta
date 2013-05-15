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

import static com.vaadin.terminal.Sizeable.UNITS_PIXELS;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.BreadcrumbsView;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatiohakuView;
import fi.vm.sade.tarjonta.ui.view.common.SearchSpesificationView;
import fi.vm.sade.tarjonta.ui.view.koulutus.ListKoulutusView;
import fi.vm.sade.vaadin.Oph;
import java.util.List;

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
    private static final long serialVersionUID = -1669758858870001028L;
    @Autowired(required = true)
    private TarjontaPresenter _presenter;
    // Show label that shows last modification
    @Value("${common.showAppIdentifier:true}")
    private Boolean _showIdentifier = false;
    @Value("${tarjonta-app.identifier:APPLICATION IDENTIFIER NOT AVAILABLE}")
    private String _identifier;
    @Value("${root.organisaatio.oid:NOT_SET}")
    private String ophOid;
    private VerticalLayout _appRootLayout;
    private OrganisaatiohakuView organisationSearchView;
    private BreadcrumbsView breadcrumbsView;
    private SearchSpesificationView searchSpesificationView;
    private SearchResultsView searchResultsView;
    private VerticalLayout vlRight;
    private boolean isAttached = false;

    public TarjontaRootView() {
        super();
    }
    
    public void init(){
        LOG.info("TarjontaView(): presenter={}", _presenter);

        // Fixi jrebelille...
        if (_presenter == null) {
            _presenter = new TarjontaPresenter();
        }
        _presenter.setRootView(this);

        // Create root layout
        _appRootLayout = UiBuilder.verticalLayout();
        _appRootLayout.setSizeFull();
        _appRootLayout.setHeight(-1, UNITS_PIXELS);

        _appRootLayout.addStyleName(Oph.CONTAINER_MAIN);
        setContent(_appRootLayout); // root layout

        // create app layout with organization navigation
        buildMainLayout();
        // Show application identifier if needed
        final TarjontaModel model = _presenter.getModel();
        model.setShowIdentifier(_showIdentifier);
        model.setIdentifier(_identifier);
        initializeOrganisationData();
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

    /**
     * Change view by given AbstractLayout.
     *
     * @param layout
     */
    public void changeView(AbstractLayout layout) {
        changeView(layout, true);
    }

    /**
     * Change view by given AbstractLayout. If a clear parameter is given, then
     * all components are cleared from window.
     *
     * @param layout
     * @param clear
     */
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
        return searchSpesificationView;
    }

    public BreadcrumbsView getBreadcrumbsView() {
        return breadcrumbsView;
    }

    public SearchResultsView getSearchResultsView() {
        return searchResultsView;
    }

    public OrganisaatiohakuView getOrganisaatiohakuView() {
        return organisationSearchView;
    }

    private void buildMainLayout() {
        // Create components

        organisationSearchView = new OrganisaatiohakuView();
        breadcrumbsView = new BreadcrumbsView(_presenter);
        searchResultsView = new SearchResultsView();
        _presenter.setSearchResultsView(searchResultsView);
        searchSpesificationView = new SearchSpesificationView(_presenter.getModel().getSearchSpec());

        // Add listener for search events
        searchSpesificationView.addListener(new Listener() {
            private static final long serialVersionUID = -8696709317724642137L;

            @Override
            public void componentEvent(Event event) {
                if (event instanceof SearchSpesificationView.SearchEvent) {
                    _presenter.reloadMainView(true);
                }
            }
        });

        //bind the components together
        vlRight = new VerticalLayout();
        vlRight.setSizeFull();
        vlRight.addComponent(breadcrumbsView);
        vlRight.addComponent(searchSpesificationView);
        vlRight.addComponent(searchResultsView);
        vlRight.setExpandRatio(searchResultsView, 1f);
        vlRight.setSizeFull();


        organisationSearchView.addComponent(vlRight);
        organisationSearchView.setExpandRatio(vlRight, 1f);
    }

    public TarjontaPresenter getTarjontaPresenter() {
        return _presenter;
    }

    private void initializeOrganisationData() {
        TarjontaModel model = _presenter.getModel();

        //Set OPH organisaatio OID.
        model.setRootOrganisaatioOid(ophOid);
    }
}
