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

import fi.vm.sade.tarjonta.ui.koulutusmoduuli.tutkintoohjelma.TutkintoOhjelmaEditForm;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.ui.service.TarjontaUiService;
import fi.vm.sade.tarjonta.ui.util.I18NHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.ViewBoundForm;

/**
 * Main view for editing different types of Koulutusmoduuli.
 *
 * @author Jukka Raanamo
 */
@Configurable(preConstruction = true)
public class KoulutusmoduuliEditView extends CustomComponent {

    private HorizontalLayout mainLayout;

    private AbstractKoulutusmoduuliEditForm editForm;

    private ViewBoundForm viewBoundForm;

    private KoulutusmoduuliDTO koulutusmoduuli;

    @Autowired
    private TarjontaUiService uiService;

    private static I18NHelper i18n = new I18NHelper("KoulutusmoduuliEditView.");

    public KoulutusmoduuliEditView() {

        super();

        // todo: dummy for now
        koulutusmoduuli = uiService.createTutkintoOhjelma();

        mainLayout = new HorizontalLayout();
        // todo: added to make all components visible, remove when we have some CSS in place
        mainLayout.setSizeFull();
        mainLayout.addComponent(createLeftPanel());

        final VerticalLayout formAndButtons = new VerticalLayout();
        formAndButtons.addComponent(createEditForm());
        formAndButtons.addComponent(createActionButtons());

        mainLayout.addComponent(formAndButtons);

        setCompositionRoot(mainLayout);
    }

    /**
     * Creates left hand side, i.e. organisaatio with moduulit tree.
     *
     * @return
     */
    private Component createLeftPanel() {
        Label label = new Label("left");
        label.setSizeUndefined();
        return label;
    }

    /**
     * Creates component that knows how to edit currently selected Koulutusmoduuli.
     *
     * @return
     */
    private Component createEditForm() {

        // todo: what triggers the type?
        editForm = new TutkintoOhjelmaEditForm();

        viewBoundForm = new ViewBoundForm(editForm);
        final BeanItem<KoulutusmoduuliDTO> beanItem = editForm.createBeanItem(koulutusmoduuli);
        viewBoundForm.setItemDataSource(beanItem);

        return viewBoundForm;

    }

    private Component createActionButtons() {

        final HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);

        final Button saveButton = new Button(i18n.getMessage("saveButton"));
        saveButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                viewBoundForm.commit();
                save();
            }

        });

        layout.addComponent(saveButton);

        return layout;


    }

    private void save() {
        editForm.save(uiService, koulutusmoduuli);
        getWindow().showNotification(i18n.getMessage("save.success"));
    }

}

