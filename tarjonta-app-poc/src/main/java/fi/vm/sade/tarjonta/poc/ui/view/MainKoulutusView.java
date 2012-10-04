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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.poc.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.poc.ui.view.common.AutoSizeVerticalLayout;
import fi.vm.sade.tarjonta.poc.ui.view.koulutus.CreateKoulutusView;
import fi.vm.sade.tarjonta.poc.ui.view.koulutus.KoulutusAdditionalInfoView;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable(preConstruction = true)
public class MainKoulutusView extends AutoSizeVerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(MainKoulutusView.class);
    private MainSearchView _searchView = new MainSearchView();
    private MainTabSheetView _resultView = new MainTabSheetView();
    private Button breadCrumb;
    @Autowired
    private TarjontaPresenter _presenter;

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

        breadCrumb = UiUtil.buttonLink(
                breadCrumblayout,
                "Rantalohjan koulutuskuntayhtymä Rantalohjan ammattiopisto",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                    }
                });

        vlayout.addComponent(breadCrumblayout);
        vlayout.setComponentAlignment(breadCrumblayout, Alignment.TOP_LEFT);
    }
}
