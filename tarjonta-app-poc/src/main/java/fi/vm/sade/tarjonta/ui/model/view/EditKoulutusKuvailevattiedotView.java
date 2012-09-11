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

import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable
public class EditKoulutusKuvailevattiedotView extends VerticalLayout {

    public EditKoulutusKuvailevattiedotView() {
        super();
        setSizeUndefined();
        setWidth("100%");
        setSpacing(true);
        setMargin(false, false, false, true);

        initialize();
    }

    private void initialize() {
        removeAllComponents();

        // TOP BUTTONS
        {
            HorizontalLayout hl = UiBuilder.newHorizontalLayout();
            UiBuilder.newButton("Peruuta", hl);
            UiBuilder.newButton("Tallenna luonnoksena", hl);
            UiBuilder.newButton("Tallenna valmiina", hl);
            UiBuilder.newButton("Jatka", hl);
            addComponent(hl);
        }

        VerticalLayout vl = UiBuilder.newVerticalLayout();

        Panel p = UiBuilder.newPanel();
        p.setCaption("Koulutuksen kuvaievat tiedot");
        p.setContent(vl);
        addComponent(p);

        {
            UiBuilder.newTextPanel("Tutkinnon rakenne...", "100%", null, vl);

            TabSheet tabs = UiBuilder.newTabSheet(vl);
            tabs.addTab(UiBuilder.newRichTextArea(null, null, null), "Suomi");
            tabs.addTab(UiBuilder.newRichTextArea(null, null, null), "Ruotsi");
            tabs.addTab(UiBuilder.newRichTextArea(null, null, null), "Englanti");
        }

        {
            UiBuilder.newTextPanel("Tutkinnon koulutukselliset tavoitteet...", "100%", null, vl);

            TabSheet tabs = UiBuilder.newTabSheet(vl);
            tabs.addTab(UiBuilder.newRichTextArea(null, null, null), "Suomi");
            tabs.addTab(UiBuilder.newRichTextArea(null, null, null), "Ruotsi");
            tabs.addTab(UiBuilder.newRichTextArea(null, null, null), "Englanti");
        }

        {
            UiBuilder.newTextPanel("Koulutuksen sisältö...", "100%", null, vl);

            TabSheet tabs = UiBuilder.newTabSheet(vl);
            tabs.addTab(UiBuilder.newRichTextArea(null, null, null), "Suomi");
            tabs.addTab(UiBuilder.newRichTextArea(null, null, null), "Ruotsi");
            tabs.addTab(UiBuilder.newRichTextArea(null, null, null), "Englanti");
        }

        // BOTTOM BUTTONS
        {
            HorizontalLayout hl = UiBuilder.newHorizontalLayout();
            UiBuilder.newButton("Peruuta", hl);
            UiBuilder.newButton("Tallenna luonnoksena", hl);
            UiBuilder.newButton("Tallenna valmiina", hl);
            UiBuilder.newButton("Jatka", hl);
            addComponent(hl);
        }

    }




}
