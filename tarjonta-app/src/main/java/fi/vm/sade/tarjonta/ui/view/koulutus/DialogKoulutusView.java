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
package fi.vm.sade.tarjonta.ui.view.koulutus;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Form;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.view.common.AbstractDataTableDialog;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.ViewBoundForm;

/**
 *
 * @author jani
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class DialogKoulutusView extends AbstractDataTableDialog {

    private Form form;

    public DialogKoulutusView(String label, int width, int height, ComponentContainer component) {
        super(label, null, null);
        setWidth(width, UNITS_PIXELS);
        setHeight(height, UNITS_PIXELS);

        form = new ViewBoundForm(component);
        form.setSizeFull();
        form.setWriteThrough(false);
        form.setEnabled(true);
        addLayoutComponent(form);
    }

    @Override
    public void buildLayout(VerticalLayout layout) {
    }

    @Override
    public Form getForm() {
        return form;
    }

    @Override
    public Component getInstance() {
        return getLayout();
    }
}
