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
package fi.vm.sade.tarjonta.poc.ui.model.view.hakukohde;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.poc.demodata.DataSource;
import fi.vm.sade.tarjonta.poc.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.poc.ui.model.view.AbstractVerticalLayout;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public class TabPerustiedotView extends AbstractVerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(TabPerustiedotView.class);
    private List<Entry<String, AbstractComponent>> items = new ArrayList<Entry<String, AbstractComponent>>();

    public TabPerustiedotView() {
        super(TabPerustiedotView.class);
        setMargin(false, false, true, false); //only bottom
        //TOP AREA
        buildInfoButtonLayout();
        /* add items to the grid */
        buildHakukode();
        buildHaku();
        buildPisteet();
        buildAloituspaikat();
        buildVaatimukset();
        buildTopAreaLanguageTab();

        /* build the grid layout */
        addComponent(buildGrid());

        //MID AREA
        buildMiddleArea();

        //BOTTOM AREA
        UiUtil.horizontalLine(this);
        buildBottomAreaLanguageTab();
    }

    private void buildHakukode() {
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        ComboBox comboBox = UiUtil.comboBox(hl, null, new String[]{"Tunnistekoodi1", "Tunnistekoodi2", "Tunnistekoodi3"});
        Button button = UiUtil.button(hl, T("tunnistekoodi"), new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        hl.setExpandRatio(button, 5l);
        hl.setComponentAlignment(button, Alignment.TOP_LEFT);
        addItem("hakukohteenNimi", hl);
    }

    private void buildHaku() {
        addItem("haku", UiUtil.comboBox(null, null, DataSource.HAKUKOHTEEN_HAUT));
    }

    private void buildPisteet() {
        GridLayout grid = new GridLayout(3, 1);

        Label label = buildGridTextRow(grid, "edellisenVuodenAlinHyvaksyttyPistemaara", RandomUtils.nextInt() + "");
        Label label1 = buildGridTextRow(grid, "edellisenVuodenKorkeinMahdollinenPistemaara", RandomUtils.nextInt() + "");
        Label label2 = buildGridTextRow(grid, "edellisenaVuonnaHakeneetJaHyvaksytyt", RandomUtils.nextInt() + "/" + RandomUtils.nextInt());
        Label label3 = buildGridTextRow(grid, "edellisenaVuonnaKokeeseenOsallistuneet", RandomUtils.nextInt() + "/" + RandomUtils.nextInt());

        grid.setColumnExpandRatio(0, 0l);
        grid.setColumnExpandRatio(1, 0l);
        grid.setColumnExpandRatio(2, 2l);
        grid.setWidth(UiConstant.PCT100);
        grid.setComponentAlignment(label, Alignment.TOP_RIGHT);
        grid.setComponentAlignment(label1, Alignment.TOP_RIGHT);
        grid.setComponentAlignment(label2, Alignment.TOP_RIGHT);
        grid.setComponentAlignment(label3, Alignment.TOP_RIGHT);


        addItem("Pisteet", grid);
    }

    private Label buildGridTextRow(GridLayout grid, String in18Property, String result) {
        Label label = UiUtil.label(grid, T(in18Property));
        UiUtil.label(grid, "").setWidth("10px");
        UiUtil.label(grid, result);
        grid.newLine();

        return label;
    }

    private void buildAloituspaikat() {
        addItem("paatoksenMukaisetAloituspaikat", UiUtil.textField(null));
    }

    private void buildVaatimukset() {
        addItem("hakukelpoisuusvaatimukset", UiUtil.comboBox(null, null, new String[]{"1", "2", "3"}));
    }

    private TabSheet buildLanguageTab() {
        return UiBuilder.koodistoLanguageTabSheets(getPresenter() != null ? getPresenter().getKoodistoKielet() : null);
    }

    private void buildTopAreaLanguageTab() {
        addItem("valintaperusteidenSanallinenKuvaus", buildLanguageTab());
    }

    private void buildMiddleArea() {
        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.RIGHT_BOTTOM_LEFT);

        UiUtil.checkbox(vl, T("kaytaValintaperustekuvausta"));
        vl.addComponent(buildLanguageTab());

        addComponent(vl);
    }

    private void buildBottomAreaLanguageTab() {
        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label label = UiUtil.label(hl, T("lisatiedot"), LabelStyleEnum.H2);
        Button btnInfo = UiUtil.buttonSmallInfo(hl);
        hl.setExpandRatio(label, 1l);
        hl.setExpandRatio(btnInfo, 3l);
        hl.setComponentAlignment(label, Alignment.TOP_LEFT);
        hl.setComponentAlignment(btnInfo, Alignment.TOP_RIGHT);
        vl.addComponent(hl);
        vl.addComponent(buildLanguageTab());
        addComponent(vl);
    }

    private void buildInfoButtonLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.TOP_RIGHT_LEFT);
        Button btnInfo = UiUtil.buttonSmallInfo(layout);
        layout.setComponentAlignment(btnInfo, Alignment.TOP_RIGHT);
        addComponent(layout);
    }

    private GridLayout buildGrid() {
        GridLayout grid = new GridLayout(2, 1);
        grid.setWidth(UiConstant.PCT100);
        grid.setSpacing(true);
        grid.setMargin(false, true, true, true);
        addComponent(grid);

        for (Entry<String, AbstractComponent> e : items) {
            grid.addComponent(UiUtil.label(null, T(e.getKey())));
            grid.addComponent(e.getValue());
            grid.newLine();
        }

        grid.setColumnExpandRatio(0, 0f);
        grid.setColumnExpandRatio(1, 1f);

        return grid;
    }

    private void addItem(String key, AbstractComponent value) {
        items.add(new AbstractMap.SimpleEntry<String, AbstractComponent>(key, value));
    }
}
