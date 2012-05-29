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
package fi.vm.sade.tarjonta.ui.koulutusmoduuli;

import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import org.vaadin.addon.formbinder.ViewBoundForm;

/**
 *
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliEditView extends CustomComponent {

    private HorizontalLayout mainLayout;

    private Label moduuliTitleLabel;

    private Label moduuliStatusLabel;
    
    private Form form;

    public KoulutusmoduuliEditView() {

        super();

        mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.addComponent(createLeftPanel());
        mainLayout.addComponent(createEditForm());

        setCompositionRoot(mainLayout);
    }

    /**
     * Creates left hand side, i.e. organisaatio with moduulit tree.
     *
     * @return
     */
    private Component createLeftPanel() {
        return new Label("left");
    }

    private Component createEditForm() {

        final VerticalLayout formLayout = new VerticalLayout();

        moduuliTitleLabel = new Label("title");
        moduuliStatusLabel = new Label("status");

        formLayout.addComponent(moduuliTitleLabel);
        formLayout.addComponent(moduuliStatusLabel);

        // two column layout for left hand side is for organisaatio and koodisto
        // right hand side is for relationships of this module        
        final HorizontalLayout content = new HorizontalLayout();
        content.setSpacing(true);
        content.addComponent(createFieldsPanel());
        content.addComponent(createRelationshipsPanel());

        formLayout.addComponent(content);

        this.form = new ViewBoundForm(formLayout);        
        return form;

    }

    private Component createFieldsPanel() {

        return new KoulutusmoduuliEditForm();

    }

    private Component createRelationshipsPanel() {

        final VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);

        // replace with actual panels
        final Panel parents = new Panel(I18N.getMessage("KoulutusmoduuliEditorPanel.sisaltyyModuleihin"));
        final Panel children = new Panel(I18N.getMessage("KoulutusmoduuliEditorPanel.sisaltyvatModuulit"));

        layout.addComponent(parents);
        layout.addComponent(children);

        return layout;

    }

}

