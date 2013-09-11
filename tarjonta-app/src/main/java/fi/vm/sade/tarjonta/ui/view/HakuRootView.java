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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fi.vm.sade.tarjonta.ui.AbstractWebApplication;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.presenter.HakuPresenter;
import fi.vm.sade.tarjonta.ui.view.common.SearchSpesificationView;
import fi.vm.sade.tarjonta.ui.view.haku.HakuResultRow;
import fi.vm.sade.tarjonta.ui.view.haku.ListHakuViewImpl;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Root view for Haku management.
 *
 * @author markus
 */
@Configurable(preConstruction = true)
public class HakuRootView extends Window {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaRootView.class);
    private static final long serialVersionUID = -942466086095854495L;
    private HorizontalLayout appRootLayout;
    private HorizontalLayout appRightLayout;
    //private BreadcrumbsView breadcrumbsView;
    private SearchSpesificationView searchSpesificationView;
    private HakuSearchResultView searchResultsView;
    //hakuPresenter ja kaikki hakutoiminnallisuudet tullaan varmaankin siirtämään pois tarjonnasta.
    @Autowired(required = true)
    private HakuPresenter hakuPresenter;

    public HakuRootView() {
        super();
        init();
    }

    private void init() {
         // Fixi jrebelille...
        if (hakuPresenter == null) {
            hakuPresenter = new HakuPresenter();
        }

        setWidth("100%");
        
        //
        // Create components
        //
        //BreadcrumbsView otettu pois
        //breadcrumbsView = new BreadcrumbsView();
        searchSpesificationView = new SearchSpesificationView(false);

        searchSpesificationView.setMargin(true, true, false, true);
        
        searchResultsView = new HakuSearchResultView();
        searchResultsView.setMargin(true, true, false, true);
        
        searchSpesificationView.addListener(new Listener() {
            private static final long serialVersionUID = -8696709317724642137L;

            @Override
            public void componentEvent(Event event) {
                if (event instanceof SearchSpesificationView.SearchEvent) {
                    SearchSpesificationView.SearchEvent searchEvent = (SearchSpesificationView.SearchEvent) event;
                    LOG.info("search hakukohde with search spesification: {}", searchEvent.getModel().getSearchStr());
                    hakuPresenter.loadListDataWithSearchCriteria(searchEvent.getModel());
                }
            }
        });

        //Handles navigation to different child views (edit haku, view haku)
        searchResultsView.addListener(new Listener() {
            private static final long serialVersionUID = -8696709317724642137L;

            @Override
            public void componentEvent(Event event) {
                if (event instanceof HakuResultRow.HakuRowMenuEvent) {
                    handleHakuRowMenuEvent((HakuResultRow.HakuRowMenuEvent) event);
                } else if (event instanceof ListHakuViewImpl.NewHakuEvent) {
                    hakuPresenter.showHakuEdit(new HakuViewModel());
                }
            }
        });

        // Create root layout
        VerticalLayout layout = UiBuilder.verticalLayout();
        layout.setSizeFull();
        layout.setHeight(-1, UNITS_PIXELS);
        layout.addStyleName(Oph.CONTAINER_MAIN);
        setContent(layout); // root layout

        // Make session to stay alive with small timeout
        layout.addComponent(AbstractWebApplication.createRefersh("HakuRootView.init()"));

        // Create application layout and add to root
        appRootLayout = UiBuilder.horizontalLayout();
        layout.addComponent(appRootLayout);

        // Create right side
        appRightLayout = UiBuilder.horizontalLayout();//verticalLayout();
        appRootLayout.addComponent(appRightLayout);

        showMainDefaultView();
        hakuPresenter.setRootView(this);
    }

    private void handleHakuRowMenuEvent(HakuResultRow.HakuRowMenuEvent event) {
        if (event.getType().equals(HakuResultRow.HakuRowMenuEvent.VIEW)) {
            hakuPresenter.showHakuView(event.getHaku());
        } else if (event.getType().equals(HakuResultRow.HakuRowMenuEvent.EDIT)) {
            hakuPresenter.showHakuEdit(event.getHaku());
        } else if (event.getType().equals(HakuResultRow.HakuRowMenuEvent.REMOVE)) {
            hakuPresenter.removeHaku(event.getHaku());
        }
    }

    public HorizontalLayout getAppRootLayout() {
        return appRootLayout;
    }

    public HorizontalLayout getAppRightLayout() {
        return appRightLayout;
    }

    public SearchSpesificationView getSearchSpesificationView() {
        return searchSpesificationView;
    }

    /*public BreadcrumbsView getBreadcrumbsView() {
        return breadcrumbsView;
    }*/

    public HakuSearchResultView getSearchResultsView() {
        return searchResultsView;
    }

    /**
     * Show main default view
     */
    public void showMainDefaultView() {
        LOG.info("showMainDefaultView()");

        getAppRightLayout().removeAllComponents();
        VerticalLayout vl = UiUtil.verticalLayout();
        vl.setSizeFull();
        //vl.addComponent(getBreadcrumbsView());
        vl.addComponent(getSearchSpesificationView());
        vl.addComponent(getSearchResultsView());

        getAppRightLayout().addComponent(vl);
        getAppRightLayout().setExpandRatio(vl, 1f);
    }
}
