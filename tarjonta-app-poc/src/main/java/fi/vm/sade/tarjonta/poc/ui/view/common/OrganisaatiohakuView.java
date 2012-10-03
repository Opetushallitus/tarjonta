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
package fi.vm.sade.tarjonta.poc.ui.view.common;

import fi.vm.sade.vaadin.ui.OphAbstractCollapsibleLeft;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.poc.demodata.DataSource;
import fi.vm.sade.tarjonta.poc.demodata.row.TextTreeStyle;
import fi.vm.sade.tarjonta.poc.ui.helper.I18NHelper;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 *
 * @author Jani Wilén
 */
public class OrganisaatiohakuView extends AbstractCollapsibleLeft<VerticalLayout> {

    private static I18NHelper i18n = new I18NHelper(OrganisaatiohakuView.class);
    private static final int PANEL_WIDTH = 250;
    private TextField search;
    private ComboBox organisaatioTyyppi;
    private ComboBox oppilaitosTyyppi;
    private CheckBox lakkautetut;
    private CheckBox suunnitellut;
    private Tree tree;

    public OrganisaatiohakuView() {
        super(VerticalLayout.class);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        layout.setHeight(-1, UNITS_PERCENTAGE);
        layout.setWidth(-1, UNITS_PIXELS);
        Panel panelTop = buildPanel(buildPanelLayout());

        search = UiUtil.textFieldSmallSearch(panelTop);
        organisaatioTyyppi = UiUtil.comboBox(panelTop, null, new String[]{"Organisaatiotyyppi1", "Organisaatiotyyppi1"});
        organisaatioTyyppi.setSizeUndefined();
        oppilaitosTyyppi = UiUtil.comboBox(panelTop, null, new String[]{"oppilaitostyyppi1", "oppilaitostyyppi2"});
        oppilaitosTyyppi.setWidth(210, UNITS_PIXELS);
        
        lakkautetut = UiUtil.checkbox(panelTop, i18n.getMessage("naytaMyosLakkautetut"));
        suunnitellut = UiUtil.checkbox(panelTop, i18n.getMessage("naytaMyosSuunnitellut"));

        Panel panelBottom = buildPanel(buildTreePanelLayout());
        panelBottom.addStyleName(Oph.CONTAINER_SECONDARY);

        layout.addComponent(panelTop);
        layout.addComponent(panelBottom);
    }

    private Panel buildPanel(AbstractLayout layout) {
        Panel panel = new Panel(layout);
        panel.setWidth(PANEL_WIDTH, Sizeable.UNITS_PIXELS);
        panel.setHeight(-1, Sizeable.UNITS_PIXELS);
        panel.addStyleName(Oph.CONTAINER_SECONDARY);
        panel.setScrollable(true);
        return panel;
    }

    private AbstractLayout buildTreePanelLayout() {
        VerticalLayout hl = buildPanelLayout();

        tree = new Tree();
        tree.setSizeUndefined();
        
        //TODO: REAL DATA
        tree.setItemCaptionPropertyId(DataSource.COLUMN_KEY);
        tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_PROPERTY);
        tree.setContainerDataSource(DataSource.treeTableData(new TextTreeStyle(), DataSource.ORGANISAATIOT, DataSource.ORGANISAATIOT));

        hl.addComponent(tree);
        return hl;
    }
    
    private VerticalLayout buildPanelLayout() {
        VerticalLayout hl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        hl.setSizeUndefined();
        return hl;
    }
}
