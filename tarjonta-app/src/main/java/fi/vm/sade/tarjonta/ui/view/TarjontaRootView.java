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

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.view.common.BreadcrumbsView;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatiohakuView;
import fi.vm.sade.tarjonta.ui.view.common.SearchSpesificationView;
import fi.vm.sade.tarjonta.ui.view.haku.EditHakuViewImpl;
import fi.vm.sade.tarjonta.ui.view.haku.HakuResultRow;
import fi.vm.sade.tarjonta.ui.view.haku.ListHakuViewImpl;
import fi.vm.sade.tarjonta.ui.view.haku.ShowHakuViewImpl;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.dto.ButtonDTO;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;
import fi.vm.sade.vaadin.util.UiUtil;

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
@Configurable(preConstruction=true)
public class TarjontaRootView extends Window {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaRootView.class);

    @Autowired(required = true)
    private TarjontaPresenter _presenter;

    private HorizontalLayout _appRootLayout;
    //private VerticalLayout _appLeftLayout;
    private HorizontalLayout _appRightLayout;

    private BreadcrumbsView _breadcrumbsView;
    private SearchSpesificationView _searchSpesificationView;
    private SearchResultsView _searchResultsView;

    public TarjontaRootView() {
        super();
        LOG.info("TarjontaView(): presenter={}", _presenter);

        // Fixi jrebelille...
        if (_presenter == null) {
            _presenter = new TarjontaPresenter();
        }

        //
        // Create components
        //
        _breadcrumbsView = new BreadcrumbsView();
        _searchSpesificationView = new SearchSpesificationView();
        _searchResultsView = new SearchResultsView();
        
        //Handles navigation to different child views (edit haku, view haku)
        /*_searchResultsView.addListener(new Listener() {

            @Override
            public void componentEvent(Event event) {
                if (event instanceof HakuResultRow.HakuRowMenuEvent) {
                    handleHakuRowMenuEvent((HakuResultRow.HakuRowMenuEvent)event);    
                } else if (event instanceof ListHakuViewImpl.NewHakuEvent) {
                    showHakuEdit(new HakuViewModel());
                }
            }
            
        });*/

        // Initialize presenter with root window
        _presenter.setTarjontaWindow(this);

        
        
        
        // Create root layout
        VerticalLayout layout = UiBuilder.verticalLayout();
        layout.setHeight(-1,UNITS_PIXELS);
        layout.addStyleName(Oph.CONTAINER_MAIN);
        setContent(layout); // root layout
        
        

        // Create application layout and add to root
        _appRootLayout = UiBuilder.horizontalLayout();
        layout.addComponent(_appRootLayout);

        // Create left side
        //_appLeftLayout = UiBuilder.verticalLayout();
        //_appLeftLayout.setWidth("25%");
        //_appRootLayout.addComponent(_appLeftLayout);

        // Create right side
        _appRightLayout = UiBuilder.horizontalLayout();//verticalLayout();
        _appRootLayout.addComponent(_appRightLayout);

        // Show application identifier if needed
        if (_presenter != null && _presenter.isShowIdentifier()) {
            layout.addComponent(new Label("ID=" + _presenter.getIdentifier()));
        }

        _presenter.showMainDefaultView();
        
//        hakuPresenter.setRootView(this);
    }
    
//    private void handleHakuRowMenuEvent(HakuResultRow.HakuRowMenuEvent event) {
//        if (event.getType().equals(HakuResultRow.HakuRowMenuEvent.VIEW)) {
//            showHakuView(event.getHaku());
//        } else if (event.getType().equals(HakuResultRow.HakuRowMenuEvent.EDIT)) {
//            showHakuEdit(event.getHaku());
//        } else if (event.getType().equals(HakuResultRow.HakuRowMenuEvent.EDIT)) {
//            hakuPresenter.removeHaku(event.getHaku());
//        }
//    }

    public HorizontalLayout getAppRootLayout() {
        return _appRootLayout;
    }
    /*
    public VerticalLayout getAppLeftLayout() {
        return _appLeftLayout;
    }*/

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
    
    /**
     * Displays the view component of Haku
     * @param haku
     */
    
//    private void showHakuView(final HakuViewModel haku) {
//
//        LOG.info("loadViewForm()");
//        /*
//        getAppLeftLayout().removeAllComponents();
//        getAppRightLayout().removeAllComponents();
//
//        getAppLeftLayout().addComponent(new Label("LEFT"));
//        */
//        VerticalLayout vl = UiUtil.verticalLayout();
//        vl.addComponent(getBreadcrumbsView());
//        //getAppRightLayout().addComponent(getBreadcrumbsView());
//        Button.ClickListener myClickList = new Button.ClickListener() {
//
//            @Override
//            public void buttonClick(ClickEvent event) {
//                // TODO Auto-generated method stub
//                
//            }
//            
//        };
//        PageNavigationDTO pNav = new PageNavigationDTO(new ButtonDTO("Edellinen", myClickList), new ButtonDTO("Seuraava", myClickList), "Mokkiteksti");
//        this.hakuPresenter.setHakuViewModel(haku);
//        String messageStr = (haku.getNimiFi() != null) ? haku.getNimiFi() : "-";
//        ShowHakuViewImpl showHaku = new ShowHakuViewImpl(this.hakuPresenter.getHakuModel().getNimiFi(), 
//                this.hakuPresenter.getHakuModel().getNimiFi(), 
//                pNav);
//        showHaku.addListener(new Listener() {
//
//            @Override
//            public void componentEvent(Event event) {
//                if (event instanceof ShowHakuViewImpl.BackEvent) {
//                    showMainDefaultView();
//                    hakuPresenter.refreshHakulist();
//                } else if (event instanceof ShowHakuViewImpl.EditEvent) {
//                    showHakuEdit(haku);
//                }
//            }
//            
//        });
//        vl.addComponent(showHaku);
//        getAppRightLayout().addComponent(vl);
//        getAppRightLayout().setExpandRatio(vl, 1f);
//    }
    
    /**
     * Displays the edit form of Haku.
     * @param haku
     */
//    public void showHakuEdit(final HakuViewModel haku) {
//        LOG.info("showHakuEdit()");
//        /*
//        getAppLeftLayout().removeAllComponents();
//        getAppRightLayout().removeAllComponents();
//
//        getAppLeftLayout().addComponent(new Label("LEFT"));
//        */    
//        VerticalLayout vl = UiUtil.verticalLayout();
//        //getAppRightLayout().addComponent(getBreadcrumbsView());
//        vl.addComponent(getBreadcrumbsView());
//        EditHakuViewImpl editHakuView = new EditHakuViewImpl(haku);
//        editHakuView.addListener(new Listener() {
//
//            @Override
//            public void componentEvent(Event event) {
//                if (event instanceof EditHakuViewImpl.CancelEvent) {
//                    showMainDefaultView();
//                    hakuPresenter.refreshHakulist();
//                } else if (event instanceof EditHakuViewImpl.ContinueEvent) {
//                    if (haku.getHakuOid() != null) {
//                        showHakuView(haku);
//                    } 
//                }
//            }
//            
//        });
//        vl.addComponent(editHakuView);
//        getAppRightLayout().addComponent(vl);
//        getAppRightLayout().setExpandRatio(vl, 1f);
//    }
    
    /**
     * Show main default view
     */
    public void showMainDefaultView() {
        LOG.info("showMainDefaultView()");
        /*
        getAppLeftLayout().removeAllComponents();*/
        getAppRightLayout().removeAllComponents();

        /*
            getAppLeftLayout().addComponent(new Label("LEFT"));
        */
        getAppRightLayout().addComponent(new OrganisaatiohakuView(null));
        VerticalLayout vl = UiUtil.verticalLayout();
        vl.addComponent(getBreadcrumbsView());
        vl.addComponent(getSearchSpesificationView());
        vl.addComponent(getSearchResultsView());
        getAppRightLayout().addComponent(vl);
        getAppRightLayout().setExpandRatio(vl, 1f);
    }

}
