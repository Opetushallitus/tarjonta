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
import fi.vm.sade.tarjonta.ui.service.TarjontaUiService;
import fi.vm.sade.tarjonta.ui.util.I18NHelper;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private TarjontaUiService uiService;
    
    private static final int VIEW_WIDTH = 100;
    private static final int LEFT_SIDE_WIDTH_PERCENTAGE = 1;
    private static final int RIGHT_SIDE_WIDTH_PERCENTAGE = VIEW_WIDTH - LEFT_SIDE_WIDTH_PERCENTAGE;
    
    private static I18NHelper i18n = new I18NHelper(KoulutusmoduuliEditView.class);

    public KoulutusmoduuliEditView() {

        super();
        
        mainLayout = new HorizontalLayout();
        mainLayout.setWidth(VIEW_WIDTH, UNITS_PERCENTAGE);
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
        Label label = new Label("left");
        label.setWidth(LEFT_SIDE_WIDTH_PERCENTAGE, UNITS_PERCENTAGE);        
        return label;
    }

    private Component createEditForm() {

        final VerticalLayout formLayout = new VerticalLayout();
        formLayout.setSpacing(true);

        moduuliTitleLabel = new Label("title");
        moduuliStatusLabel = new Label("status");

        formLayout.addComponent(moduuliTitleLabel);
        formLayout.addComponent(moduuliStatusLabel);

        formLayout.addComponent(createMainFieldsAndNavigation());        
        formLayout.addComponent(createMultilingualEditors());
        formLayout.addComponent(createActionButtons());
        
        // wrap all panels in ViewBoundForm to bind fields from multiple
        // components
        Form boundForm = new ViewBoundForm(formLayout);        
        
        boundForm.setWidth(RIGHT_SIDE_WIDTH_PERCENTAGE, UNITS_PERCENTAGE);
        
        this.form = boundForm;
        
        return form;

    }
    
    private Component createMainFieldsAndNavigation() {
        // two column layout for left hand side is for organisaatio and koodisto
        // right hand side is for relationships of this module        
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth(100, UNITS_PERCENTAGE);
        layout.setSpacing(true);
        layout.addComponent(createFieldsPanel());
        layout.addComponent(createRelationshipsPanel());
        return layout;
    }

    private Component createFieldsPanel() {
        KoulutusmoduuliEditForm editForm = new KoulutusmoduuliEditForm();
        return editForm;
    }
    
    private Component createActionButtons() {
        
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        
        final Button saveButton = new Button(i18n.getMessage("saveButton"));
        
        layout.addComponent(saveButton);
        
        return layout;
        
        
    }
    
    private Component createMultilingualEditors() {
        // this is a placeholder, create actual editor component here
        Panel p = new Panel("multilingual editor placeholder");
        // add some artificial height
        p.setWidth(100, UNITS_PERCENTAGE);
        p.setHeight(400, UNITS_PIXELS);        
        return p;
    }

    private Component createRelationshipsPanel() {

        final VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setWidth(100, UNITS_PERCENTAGE);

        // replace with actual panels
        final Panel parents = new Panel(i18n.getMessage("sisaltyyModuleihin"));
        final Panel children = new Panel(i18n.getMessage("sisaltyvatModuulit"));
        parents.setWidth(100, UNITS_PERCENTAGE);
        children.setWidth(100, UNITS_PERCENTAGE);

        layout.addComponent(parents);
        layout.addComponent(children);

        return layout;

    }

}

