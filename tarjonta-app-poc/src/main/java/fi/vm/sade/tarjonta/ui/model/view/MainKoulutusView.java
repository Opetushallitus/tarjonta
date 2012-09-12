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
package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.vaadin.oph.demodata.DataSource;
import fi.vm.sade.vaadin.oph.demodata.row.MultiActionTableStyle;
import fi.vm.sade.vaadin.oph.enums.UiMarginEnum;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;

/**
 *
 * @author mlyly
 */
// @Configurable(preConstruction=true)
public class MainKoulutusView extends VerticalLayout {

    MainSearchView _searchView = new MainSearchView();
    MainResultView _resultView = new MainResultView();

    private Link breadCrumb;

    public MainKoulutusView() {
        removeAllComponents();

        //RIGHT LAYOUT IN SPLIT PANEL
        buildBreadCrumb(this);

        addComponent(_searchView);
        addComponent(_resultView);

        this.setExpandRatio(_searchView, 0.03f);
        this.setExpandRatio(_resultView, 0.97f);

        // TODO Dummy data
        _resultView.setCategoryDataSource(DataSource.treeTableData(new MultiActionTableStyle()));
    }

    private void buildBreadCrumb(VerticalLayout vlayout) {
        HorizontalLayout breadCrumblayout = UiBuilder.newHorizontalLayout(true, UiMarginEnum.TOP_LEFT_BOTTOM);

        breadCrumb = UiBuilder.newLink("Rantalohjan koulutuskuntayhtymä Rantalohjan ammattiopisto", breadCrumblayout);

        vlayout.addComponent(breadCrumblayout);
        vlayout.setComponentAlignment(breadCrumblayout, Alignment.TOP_LEFT);
    }

}
