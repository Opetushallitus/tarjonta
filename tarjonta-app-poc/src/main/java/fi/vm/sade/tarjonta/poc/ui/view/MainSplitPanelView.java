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

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.poc.ui.view.common.OrganisaatiohakuView;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable(preConstruction = true)
public class MainSplitPanelView extends HorizontalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(MainSplitPanelView.class);
    private OrganisaatiohakuView mainLeftLayout;
    private VerticalLayout mainRightLayout;

    /**
     * The constructor should first build the main layout, set the composition
     * root and then do any custom initialization.
     */
    public MainSplitPanelView() {
        /*
         * REMEBER:
         * Please do not set width 100% to horizontal layout, or it 
         * will break the collapsible Organisaatio haku view. * 
         */

        LOG.info("In MainSplitPanelView");
        setWidth(100, UNITS_PERCENTAGE);
        setHeight(-1, UNITS_PIXELS);
        buildMainLayout();
    }

    private void buildMainLayout() {
       // mainLeftLayout = new OrganisaatiohakuView(); //main collapsible layout left
        mainRightLayout = UiUtil.verticalLayout(); //main tool layout right
        mainRightLayout.setHeight(-1, UNITS_PIXELS);

        //addComponent(mainLeftLayout);
        addComponent(mainRightLayout);

    }

    /**
     * @return the mainRightLayout
     */
    public VerticalLayout getMainRightLayout() {
        return mainRightLayout;
    }

    /**
     * @param mainRightLayout the mainRightLayout to set
     */
    public void setMainRightLayout(VerticalLayout mainRightLayout) {
        this.mainRightLayout = mainRightLayout;
    }

    /**
     * @return the mainLeftLayout
     */
    public OrganisaatiohakuView getMainLeftLayout() {
        return mainLeftLayout;
    }

}
