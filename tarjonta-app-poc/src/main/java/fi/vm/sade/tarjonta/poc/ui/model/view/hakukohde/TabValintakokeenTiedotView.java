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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import fi.vm.sade.koodisto.service.types.dto.KoodiDTO;
import fi.vm.sade.tarjonta.poc.ui.helper.KoodistoHelper;
import fi.vm.sade.tarjonta.poc.ui.model.view.AbstractVerticalLayout;
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
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public class TabValintakokeenTiedotView extends AbstractVerticalLayout {

    @Value("${koodisto-uris.kieli:http://kieli}")
    private String _koodistoUriKieli;
    private static final Logger LOG = LoggerFactory.getLogger(TabValintakokeenTiedotView.class);
    private List<Entry<String, AbstractComponent>> items = new ArrayList<Entry<String, AbstractComponent>>();

    public TabValintakokeenTiedotView() {
        super(TabValintakokeenTiedotView.class);
        setMargin(false, false, true, false); //only bottom
        buildInfoButtonLayout();
        /* add items to the grid */
        buildValintakokeenTyyppi();
        buildTopAreaLanguageTab();
        
        /* build the grid layout */
        buildGrid();
    }

   
    private void buildValintakokeenTyyppi() {
        addItem("valintakokeenTyyppi", UiUtil.comboBox(null, null, new String[]{"1", "2", "3"}));
    }


    private TabSheet buildLanguageTab() {
        TabSheet tab = new TabSheet();

        KoodistoHelper koodistoHelper = new KoodistoHelper();

        if (_koodistoUriKieli != null) {
            List<KoodiDTO> koodisto = koodistoHelper.getKoodisto(_koodistoUriKieli);

            for (KoodiDTO k : koodisto) {
                TextField textField = UiUtil.textField(null);
                textField.setHeight("100px");
                textField.setWidth(UiConstant.PCT100);

                tab.addTab(textField, k.getKoodiArvo(), null);
            }
        } else {
            //Do not add this code block to the real application! 
            //A fix for JRebel development as sometimes the JRebel fails to 
            //initialize value beans... 

            TextField textField = UiUtil.textField(null);
            textField.setHeight("100px");
            textField.setWidth(UiConstant.PCT100);

            tab.addTab(textField, "Suomi", null);
        }

        return tab;
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
}
