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
package fi.vm.sade.tarjonta.ui.koulutusmoduuli.tutkintoohjelma;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.*;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.AbstractKoulutusmoduuliEditForm;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.AbstractKoulutusmoduuliFormModel;
import fi.vm.sade.tarjonta.ui.util.I18NHelper;
import fi.vm.sade.tarjonta.ui.util.VaadinUtils;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 *
 * @author Jukka Raanamo
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class TutkintoOhjelmaEditForm extends AbstractKoulutusmoduuliEditForm<TutkintoOhjelmaDTO> {

    private Label moduuliTitleLabel;

    private Label moduuliStatusLabel;

    @PropertyId("organisaatioOid")
    private TextField organisaatioField;

    @PropertyId("todo_koulutus")
    private TextField koulutusField;

    private static final I18NHelper i18n = new I18NHelper("TutkintoOhjelmaEditForm.");

    public TutkintoOhjelmaEditForm() {

        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);

        moduuliTitleLabel = new Label("title");
        moduuliStatusLabel = new Label("status");

        mainLayout.addComponent(moduuliTitleLabel);
        mainLayout.addComponent(moduuliStatusLabel);

        final GridLayout grid = new GridLayout(2, 2);
        grid.setSpacing(true);

        organisaatioField = VaadinUtils.newTextField();
        koulutusField = VaadinUtils.newTextField();
        
        addFieldWithLabel(grid, new Label(i18n.getMessage("organisaatioLabel")), organisaatioField);
        addFieldWithLabel(grid, new Label(i18n.getMessage("koulutusLabel")), koulutusField);

        VerticalLayout vertical = new VerticalLayout();
        vertical.addComponent(grid);
        vertical.addComponent(createKoodistoPanel());

        mainLayout.addComponent(VaadinUtils.newTwoColumnHorizontalLayout(vertical, createNavigations()));
        mainLayout.addComponent(createMultilingualEditors());

        setCompositionRoot(mainLayout);
    }

    @Override
    public BeanItem<? extends AbstractKoulutusmoduuliFormModel<TutkintoOhjelmaDTO>> createBeanItem(TutkintoOhjelmaDTO dto) {

        final TutkintoOhjelmaFormModel model = new TutkintoOhjelmaFormModel(dto);
        final BeanItem<TutkintoOhjelmaFormModel> beanItem = new BeanItem<TutkintoOhjelmaFormModel>(model);

        // todo: add bean properties to populate
        final String[] properties = {
            "organisaatioOid"
        };

        for (String property : properties) {
            beanItem.addItemProperty(property, new NestedMethodProperty(model, property));
        }

        return beanItem;

    }

    

    private Component createKoodistoPanel() {

        return new Panel("Koodisto field");

    }

    private void addFieldWithLabel(GridLayout grid, Label label, Component content) {

        grid.addComponent(label);
        grid.addComponent(content);
        grid.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);

    }

    private Component createMultilingualEditors() {
        // this is a placeholder, create actual editor component here
        Panel p = new Panel("multilingual editor placeholder");
        // add some artificial height
        p.setWidth(100, UNITS_PERCENTAGE);
        p.setHeight(400, UNITS_PIXELS);
        return p;
    }

    private Component createNavigations() {

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

