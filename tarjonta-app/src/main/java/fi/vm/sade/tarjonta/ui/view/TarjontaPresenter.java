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

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatiohakuView;
import fi.vm.sade.vaadin.util.UiUtil;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

/**
 *
 * @author mlyly
 */
@Component
@Configurable(preConstruction = false)
public class TarjontaPresenter {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaPresenter.class);

    @Autowired(required=true)
    private TarjontaModel _model;

    private TarjontaRootView _rootView;

    public TarjontaPresenter() {
    }

    @PostConstruct
    public void initialize() {
        LOG.info("initialize(): model={}", _model);
    }

    public TarjontaModel getModel() {
        return _model;
    }

    public void setTarjontaWindow(TarjontaRootView rootView) {
        _rootView = rootView;
    }

    public TarjontaRootView getRootView() {
        return _rootView;
    }

    public boolean isShowIdentifier() {
        return _model.isShowIdentifier();
    }

    public String getIdentifier() {
        return _model.getIdentifier();
    }

    /**
     * Show main default view
     */
    public void showMainDefaultView() {
        LOG.info("showMainDefaultView()");
        /*
        _rootView.getAppLeftLayout().removeAllComponents();
        _rootView.getAppRightLayout().removeAllComponents();

        _rootView.getAppLeftLayout().addComponent(new Label("LEFT"));*/

        _rootView.getAppRightLayout().addComponent(new OrganisaatiohakuView(null));
        
        VerticalLayout vl = UiUtil.verticalLayout(); 
        vl.addComponent(_rootView.getBreadcrumbsView());
        vl.addComponent(_rootView.getSearchSpesificationView());
        vl.addComponent(_rootView.getSearchResultsView());
        _rootView.getAppRightLayout().addComponent(vl);
        _rootView.getAppRightLayout().setExpandRatio(vl, 1f);
        /*_rootView.getAppRightLayout().addComponent(_rootView.getBreadcrumbsView());
        _rootView.getAppRightLayout().addComponent(_rootView.getSearchSpesificationView());
        _rootView.getAppRightLayout().addComponent(_rootView.getSearchResultsView());*/
    }

    public void doSearch() {
        LOG.info("doSearch(): searchSpec={}", _model.getSearchSpec());
    }


}
