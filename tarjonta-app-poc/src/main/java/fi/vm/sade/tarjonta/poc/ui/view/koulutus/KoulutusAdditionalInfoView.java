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
package fi.vm.sade.tarjonta.poc.ui.view.koulutus;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import fi.vm.sade.vaadin.util.UiUtil;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import fi.vm.sade.vaadin.ui.OphAbstractDialogWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public class KoulutusAdditionalInfoView extends OphAbstractDialogWindow {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusAdditionalInfoView.class);

    public KoulutusAdditionalInfoView(String label) {
        super(label, null, null);
        setWidth("700px");
        setHeight("500px");
    }

    @Override
    public void buildLayout(VerticalLayout layout) {
        /* YOUR LAYOUT BETWEEN TOPIC AND BUTTONS */
        HorizontalLayout hlTop = UiUtil.horizontalLayout();
        TextField textField = UiUtil.textField(hlTop, "", "Laajuus", true);
        ComboBox comboBox = UiUtil.comboBox(hlTop, "", new String[]{"OV"});

        hlTop.setExpandRatio(comboBox, 1f);
        hlTop.setComponentAlignment(textField, Alignment.BOTTOM_LEFT);
        hlTop.setComponentAlignment(comboBox, Alignment.BOTTOM_LEFT);

        layout.addComponent(hlTop);

        TabSheet tabSheet = new TabSheet();
        VerticalLayout tabLayout = UiUtil.verticalLayout();

        UiUtil.label(tabLayout, "Nimi");
        UiUtil.textField(tabLayout);
        UiUtil.label(tabLayout, "Kuvaus");
        TextArea textArea = new TextArea();
        textArea.setWidth(100, UNITS_PERCENTAGE);
        textArea.setHeight(100, UNITS_PIXELS);
        tabLayout.addComponent(textArea);

        tabSheet.addTab(tabLayout, "Suomi");
        tabSheet.addTab(UiUtil.horizontalLayout(), "Ruotsi");
        tabSheet.addTab(UiUtil.horizontalLayout(), "Englanti");
        layout.addComponent(tabSheet);
    }

    /**
     * In case the window is closed otherwise.
     */
    @Override
    public void windowClose(CloseEvent e) {
        LOG.debug("In windowClose - close event");
        removeDialogButtons();
    }
}
