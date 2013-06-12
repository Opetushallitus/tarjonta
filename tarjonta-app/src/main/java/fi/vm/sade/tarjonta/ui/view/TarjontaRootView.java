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

import com.google.common.base.Preconditions;
import static com.vaadin.terminal.Sizeable.UNITS_EM;
import static com.vaadin.terminal.Sizeable.UNITS_PIXELS;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.BreadcrumbsView;
import fi.vm.sade.tarjonta.ui.view.common.ButtonBorderView;
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
    @Value("${root.organisaatio.oid}")
    private String rootOphOid;
    private VerticalLayout _appRootLayout;
    private OrganisaatiohakuView organisationSearchView;
    private BreadcrumbsView breadcrumbsView;
    private SearchSpesificationView searchSpesificationView;
    private SearchResultsView searchResultsView;
    private VerticalLayout vlMainRight;
    private boolean isAttached = false;
    private HorizontalLayout hlMainLayout;

    public TarjontaRootView() {
        super();
    }

    public void init() {
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
        // create app layout with organization navigation
        buildMainLayout();
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

    public void showMainView() {
        LOG.debug("showMainView()");
        changeView(getAppRootLayout());
    }

    private void buildMainLayout() {
        // Create components

        breadcrumbsView = new BreadcrumbsView(_presenter);
        breadcrumbsView.setSizeFull();

        searchSpesificationView = new SearchSpesificationView(_presenter.getModel().getSearchSpec());
        searchSpesificationView.setSizeFull();

        searchResultsView = new SearchResultsView();
        ButtonBorderView borderView = new ButtonBorderView();
        organisationSearchView = new OrganisaatiohakuView();

        _presenter.setSearchResultsView(searchResultsView);

        vlMainRight = new VerticalLayout();
        vlMainRight.setHeight(-1, UNITS_PIXELS);
        vlMainRight.addComponent(breadcrumbsView);
        vlMainRight.addComponent(searchSpesificationView);
        vlMainRight.addComponent(searchResultsView);

        vlMainRight.setSizeFull();
        searchSpesificationView.setSizeFull();
        searchResultsView.setSizeFull();


        hlMainLayout = new HorizontalLayout();
        hlMainLayout.addComponent(organisationSearchView);
        hlMainLayout.addComponent(borderView);
        hlMainLayout.addComponent(vlMainRight);

        hlMainLayout.setExpandRatio(organisationSearchView, 0.2f);
        hlMainLayout.setExpandRatio(vlMainRight, 0.8f);
        hlMainLayout.setSizeFull();
        getAppRootLayout().addComponent(hlMainLayout);
        organisationSearchView.setWidth("100%");

        //close and open organisation navigation tree
        borderView.setButtonListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (organisationSearchView.isVisible()) {
                    organisationSearchView.setVisible(false);

                    hlMainLayout.setExpandRatio(organisationSearchView, 0f);
                    hlMainLayout.setExpandRatio(vlMainRight, 1f);
                } else {
                    organisationSearchView.setVisible(true);

                    hlMainLayout.setExpandRatio(organisationSearchView, 0.2f);
                    hlMainLayout.setExpandRatio(vlMainRight, 0.8f);
                }

                searchResultsView.refreshTabs();//reset width to 100%
            }
        });

        searchSpesificationView.addListener(new Listener() {
            private static final long serialVersionUID = -8696709317724642137L;

            @Override
            public void componentEvent(Event event) {
                if (event instanceof SearchSpesificationView.SearchEvent) {
                    _presenter.reloadMainView(true);
                }
            }
        });
    }

    public TarjontaPresenter getTarjontaPresenter() {
        return _presenter;
    }

    private void initializeOrganisationData() {
        TarjontaModel model = _presenter.getModel();

        //Set OPH organisaatio OID.
        model.setRootOrganisaatioOid(rootOphOid);
    }
}
