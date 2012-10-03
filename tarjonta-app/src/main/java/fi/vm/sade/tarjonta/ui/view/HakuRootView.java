package fi.vm.sade.tarjonta.ui.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;

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

@Configurable(preConstruction=true)
public class HakuRootView extends Window {
    
    private static final Logger LOG = LoggerFactory.getLogger(TarjontaRootView.class);

    private HorizontalLayout _appRootLayout;
    //private VerticalLayout _appLeftLayout;
    private HorizontalLayout _appRightLayout;

    private BreadcrumbsView _breadcrumbsView;
    private SearchSpesificationView _searchSpesificationView;
    private HakuSearchResultView _searchResultsView;
    
    //hakuPresenter ja kaikki hakutoiminnallisuudet tullaan varmaankin siirtämään pois tarjonnasta.
    @Autowired(required = true)
    private HakuPresenter hakuPresenter;

    public HakuRootView() {
        super();

        //
        // Create components
        //
        _breadcrumbsView = new BreadcrumbsView();
        _searchSpesificationView = new SearchSpesificationView();
        _searchResultsView = new HakuSearchResultView();
        
        //Handles navigation to different child views (edit haku, view haku)
        _searchResultsView.addListener(new Listener() {

            @Override
            public void componentEvent(Event event) {
                if (event instanceof HakuResultRow.HakuRowMenuEvent) {
                    handleHakuRowMenuEvent((HakuResultRow.HakuRowMenuEvent)event);    
                } else if (event instanceof ListHakuViewImpl.NewHakuEvent) {
                    showHakuEdit(new HakuViewModel());
                }
            }
            
        });


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

        //_presenter.showMainDefaultView();
        showMainDefaultView();
//        hakuPresenter.setRootView(this);
    }
    
    private void handleHakuRowMenuEvent(HakuResultRow.HakuRowMenuEvent event) {
        if (event.getType().equals(HakuResultRow.HakuRowMenuEvent.VIEW)) {
            showHakuView(event.getHaku());
        } else if (event.getType().equals(HakuResultRow.HakuRowMenuEvent.EDIT)) {
            showHakuEdit(event.getHaku());
        } else if (event.getType().equals(HakuResultRow.HakuRowMenuEvent.EDIT)) {
            hakuPresenter.removeHaku(event.getHaku());
        }
    }

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

    public HakuSearchResultView getSearchResultsView() {
        return _searchResultsView;
    }
    
    /**
     * Displays the view component of Haku
     * @param haku
     */
    private void showHakuView(final HakuViewModel haku) {

        LOG.info("loadViewForm()");
        getAppRightLayout().removeAllComponents();
        
        VerticalLayout vl = UiUtil.verticalLayout();
        vl.addComponent(getBreadcrumbsView());
        
        Button.ClickListener myClickList = new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                // TODO Auto-generated method stub
                
            }
            
        };
        PageNavigationDTO pNav = new PageNavigationDTO(new ButtonDTO("Edellinen", myClickList), new ButtonDTO("Seuraava", myClickList), "Mokkiteksti");
        this.hakuPresenter.setHakuViewModel(haku);
        String messageStr = (haku.getNimiFi() != null) ? haku.getNimiFi() : "-";
        ShowHakuViewImpl showHaku = new ShowHakuViewImpl(this.hakuPresenter.getHakuModel().getNimiFi(), 
                this.hakuPresenter.getHakuModel().getNimiFi(), 
                pNav);
        showHaku.addListener(new Listener() {

            @Override
            public void componentEvent(Event event) {
                if (event instanceof ShowHakuViewImpl.BackEvent) {
                    showMainDefaultView();
                    hakuPresenter.refreshHakulist();
                } else if (event instanceof ShowHakuViewImpl.EditEvent) {
                    showHakuEdit(haku);
                }
            }
            
        });
        vl.addComponent(showHaku);
        getAppRightLayout().addComponent(vl);
        getAppRightLayout().setExpandRatio(vl, 1f);
    }
    
    /**
     * Displays the edit form of Haku.
     * @param haku
     */
    public void showHakuEdit(final HakuViewModel haku) {
        LOG.info("showHakuEdit()");
        getAppRightLayout().removeAllComponents();
        
        
        VerticalLayout vl = UiUtil.verticalLayout();
       
        vl.addComponent(getBreadcrumbsView());
        EditHakuViewImpl editHakuView = new EditHakuViewImpl(haku);
        editHakuView.addListener(new Listener() {

            @Override
            public void componentEvent(Event event) {
                if (event instanceof EditHakuViewImpl.CancelEvent) {
                    showMainDefaultView();
                    hakuPresenter.refreshHakulist();
                } else if (event instanceof EditHakuViewImpl.ContinueEvent) {
                    if (haku.getHakuOid() != null) {
                        showHakuView(haku);
                    } 
                }
            }
            
        });
        vl.addComponent(editHakuView);
        getAppRightLayout().addComponent(vl);
        getAppRightLayout().setExpandRatio(vl, 1f);
    }
    
    /**
     * Show main default view
     */
    public void showMainDefaultView() {
        LOG.info("showMainDefaultView()");
        
        getAppRightLayout().removeAllComponents();
        VerticalLayout vl = UiUtil.verticalLayout();
        vl.addComponent(getBreadcrumbsView());
        vl.addComponent(getSearchSpesificationView());
        vl.addComponent(getSearchResultsView());
        getAppRightLayout().addComponent(vl);
        getAppRightLayout().setExpandRatio(vl, 1f);
    }

}
