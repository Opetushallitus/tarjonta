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
import com.vaadin.ui.Form;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractDataTableDialog;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DialogKoulutusYhteystiedotView extends AbstractDataTableDialog {

    @Autowired(required = true)
    private TarjontaPresenter _presenter;
    private Form form;
    private EditKoulutusPerustiedotYhteystietoView editor;

    public DialogKoulutusYhteystiedotView(String label) {
        super(label, null, null);
        setWidth("700px");
        setHeight("500px");

        editor = new EditKoulutusPerustiedotYhteystietoView();
        form = new ViewBoundForm(editor);
        form.setSizeFull();
        form.setWriteThrough(false);
        form.setEnabled(true);

        addLayoutComponent(form);
    }

    @Override
    public void buildLayout(VerticalLayout layout) {
    }

    /**
     * In case the window is closed otherwise.
     */
    @Override
    public void windowClose(CloseEvent e) {
        removeDialogButtons();
    }

    @Override
    public Form getForm() {
        return form;
    }

    @Override
    public Component getInstance() {
        return editor;
    }
}
