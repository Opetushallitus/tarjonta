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

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.poc.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.poc.ui.model.AddressDTO;
import fi.vm.sade.tarjonta.poc.ui.view.common.AbstractVerticalLayout;
import fi.vm.sade.tarjonta.poc.ui.view.common.LanguageTabSheet;
import fi.vm.sade.tarjonta.poc.ui.view.common.TwinColSelectKoodisto;
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
public class TabValintakokeenTiedotView extends AbstractVerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(TabValintakokeenTiedotView.class);
    private static final Object[] FIELDS = new Object[]{"osoite1", "osoite2", "osoite3", "osoite4"};
    private Table table;
    private List<Entry<String, AbstractComponent>> items = new ArrayList<Entry<String, AbstractComponent>>();
    final BeanItemContainer<AddressDTO> addresses =
            new BeanItemContainer<AddressDTO>(AddressDTO.class);

    public TabValintakokeenTiedotView() {
        super(TabValintakokeenTiedotView.class);
        setMargin(false, false, true, false); //only bottom
        buildInfoButtonLayout();
        /* add items to the grid */
        buildValintakokeenTyyppi();
        buildTopAreaLanguageTab();
        buildToimitusosoite();
        /* build the grid layout */
        buildGrid();

        addSelectorAndEditor(this);
    }

    private void buildValintakokeenTyyppi() {
        addItem("valintakokeenTyyppi", buildLanguageTab());
    }

   private TabSheet buildLanguageTab() {
        return new LanguageTabSheet(getKoodistoUriKieli());
    }

    private void buildTopAreaLanguageTab() {
        addItem("valintakokeenSanallinenKuvaus", buildLanguageTab());
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

    private void addSelectorAndEditor(AbstractLayout layout) {
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        hl.setSpacing(true);

        table = new Table(null, addresses);
        table.setPageLength(10);
        table.setSizeFull();
        table.setSelectable(true);
        table.setImmediate(true);

        table.setColumnHeader((String) FIELDS[0], "Osoite1");
        table.setColumnHeader((String) FIELDS[1], "Osoite2");
        table.setColumnHeader((String) FIELDS[2], "Osoite3");
        table.setColumnHeader((String) FIELDS[3], "Osoite4");
        table.setVisibleColumns(FIELDS);

        hl.addComponent(table);

        // Span editor and table to two columns
        layout.addComponent(hl);

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

        HorizontalLayout hl2 = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        TextField ePostLocation = UiUtil.textField(hl2);
        ePostLocation.setInputPrompt(T("url")); 
        ePostLocation.setWidth(300, UNITS_PIXELS);
        
       
        Button buttonSmallPlus = UiUtil.buttonSmallPlus(hl2, T("lisaaUusi"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                AddressDTO dto = new AddressDTO();
                BeanItem<AddressDTO> bi = addresses.addItem(dto);
                table.select(dto);
            }
        });

        hl2.setExpandRatio(buttonSmallPlus, 1l);
        vl.addComponent(hl2);
        addItem("toimitusosoite", vl);
    }
}
