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
package fi.vm.sade.tarjonta.poc.ui.view.hakukohde;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.poc.ui.view.common.AbstractVerticalLayout;
import fi.vm.sade.tarjonta.poc.ui.view.common.LanguageTabSheet;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public class TabLiitteidenTiedotView extends AbstractVerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(TabLiitteidenTiedotView.class);
    private List<Entry<String, AbstractComponent>> items = new ArrayList<Entry<String, AbstractComponent>>();

    public TabLiitteidenTiedotView() {
        super(TabLiitteidenTiedotView.class);
        setMargin(false, false, true, false); //only bottom
        buildInfoButtonLayout();
        /* add items to the grid */
        buildValintakokeenTyyppi();
        buildTopAreaLanguageTab();
        buildToimitettavaMennessa();
        buildToimitusosoite();
        buildLisaaButton();

        /* build the grid layout */
        buildGrid();
    }

    private void buildLisaaButton() {
        addItem("lisaaLiite", UiUtil.buttonSmallPrimary(null, "Lisää uusi liite"));
    }

    private void buildValintakokeenTyyppi() {
        addItem("liitteidenTyyppi", UiUtil.comboBox(null, null, new String[]{"1", "2", "3"}));
    }

    private void buildToimitettavaMennessa() {
        HorizontalLayout hl = UiUtil.horizontalLayout();
        PopupDateField dateField = new PopupDateField();
        dateField.setResolution(PopupDateField.RESOLUTION_DAY);
        hl.addComponent(dateField);

        UiUtil.label(hl, "").setWidth(20, UNITS_PIXELS);
        Label lableTime = UiUtil.label(hl, T("timeLabel"));

        TextField tfTime = UiUtil.textField(hl);
        tfTime.setWidth(50, UNITS_PIXELS);
        hl.setExpandRatio(tfTime, 1l);

        hl.setComponentAlignment(lableTime, Alignment.MIDDLE_RIGHT);
        addItem("toimitettavaMennessa", hl);
    }

    private void buildToimitusosoite() {
        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.NONE);
        vl.setSizeUndefined();
        vl.setWidth(UiConstant.PCT100);
        TextField tfAddress1 = UiUtil.textField(vl);
        tfAddress1.setWidth("300px");
        tfAddress1.setInputPrompt(T("address.street1"));
        TextField tfAddress2 = UiUtil.textField(vl);
        tfAddress2.setWidth("300px");

        HorizontalLayout hl = UiUtil.horizontalLayout();
        TextField tfAddress3 = UiUtil.textField(hl);
        tfAddress3.setWidth("100px");
        tfAddress3.setInputPrompt(T("address.postcode"));

        TextField tfAddress4 = UiUtil.textField(hl);
        tfAddress4.setWidth("200px");
        tfAddress4.setInputPrompt(T("address.city"));
        hl.setExpandRatio(tfAddress4, 1l);
        vl.addComponent(hl);

        CssLayout cssLayout = UiUtil.cssLayout(UiMarginEnum.TOP);
        CheckBox checkbox = UiUtil.checkbox(cssLayout, T("voidaanToimittaaMyosSahkoisesti"));
        vl.addComponent(cssLayout);

        TextField ePostLocation = UiUtil.textField(vl);
        ePostLocation.setWidth(300, UNITS_PIXELS);
        ePostLocation.setInputPrompt(T("url"));
        addItem("toimitusosoite", vl);
    }

    private TabSheet buildLanguageTab() {
        return new LanguageTabSheet(getKoodistoUriKieli());
    }

    private void buildTopAreaLanguageTab() {
        addItem("liitteenSanallinenKuvaus", buildLanguageTab());
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
