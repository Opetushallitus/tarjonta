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
package fi.vm.sade.tarjonta.poc.ui.view;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.poc.ui.view.common.AutoSizeVerticalLayout;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 *
 * @author mlyly
 */
public class MainKoulutusView extends AutoSizeVerticalLayout {

    MainSearchView _searchView = new MainSearchView();
    MainTabSheetView _resultView = new MainTabSheetView();

    private Link breadCrumb;

    public MainKoulutusView() {
        removeAllComponents();

        //RIGHT LAYOUT IN SPLIT PANEL
        buildBreadCrumb(this);

        addComponent(_searchView);
        addComponent(_resultView);

        this.setExpandRatio(_searchView, 0.03f);
        this.setExpandRatio(_resultView, 0.97f);
    }

    private void buildBreadCrumb(VerticalLayout vlayout) {
        HorizontalLayout breadCrumblayout = UiUtil.horizontalLayout(true, UiMarginEnum.TOP_BOTTOM_LEFT);

        breadCrumb = UiUtil.link(breadCrumblayout,"Rantalohjan koulutuskuntayhtymä Rantalohjan ammattiopisto");

        vlayout.addComponent(breadCrumblayout);
        vlayout.setComponentAlignment(breadCrumblayout, Alignment.TOP_LEFT);
    }

}
