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

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.tutkintoohjelma.TutkintoOhjelmaEditForm;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.ui.TarjontaApplication;
import fi.vm.sade.tarjonta.ui.event.KoulutusmoduuliChangedEvent;
import fi.vm.sade.tarjonta.ui.service.TarjontaUiService;
import fi.vm.sade.tarjonta.ui.util.I18NHelper;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
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

    /**
     * Container whos only child is the form used to edit currently selected Koulutusmoduuli.
     */
    private ComponentContainer editFormContainer;

    /**
     * Form that knows how to handle currently selected Koulutusmoduuli.
     */
    private AbstractKoulutusmoduuliEditForm editForm;

    /**
     * Wrapper which binds our for which is not of type Form to form model.
     */
    private ViewBoundForm editFormBinding;

    private KoulutusmoduuliDTO koulutusmoduuliDTO;

    @Autowired
    private TarjontaUiService uiService;

    private static I18NHelper i18n = new I18NHelper("KoulutusmoduuliEditView.");

    public KoulutusmoduuliEditView() {

        super();

        final HorizontalLayout mainLayout = new HorizontalLayout();
        // todo: added to make all components visible, remove when we have some CSS in place
        mainLayout.setSizeFull();
        mainLayout.addComponent(createLeftPanel());

        final VerticalLayout formAndButtons = new VerticalLayout();

        editFormContainer = new Panel((String) null);
        formAndButtons.addComponent(editFormContainer);

        formAndButtons.addComponent(createActionButtons());

        mainLayout.addComponent(formAndButtons);

        setCompositionRoot(mainLayout);
    }

    /**
     * Creates left hand side, i.e. organisaatio + koulutusmoduuli -tree.
     *
     * @return
     */
    private Component createLeftPanel() {

        final VerticalLayout layout = new VerticalLayout();

        final ComboBox createNewModuuli = new ComboBox(null, createNewModuuliOptions());
        createNewModuuli.setInputPrompt(i18n.getMessage("uusiKoulutusmoduuliSelect.prompt"));
        createNewModuuli.setNullSelectionAllowed(false);
        createNewModuuli.setImmediate(true);
        createNewModuuli.addListener(new Property.ValueChangeListener() {

            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                doCreateNewModuuli((KoulutusmoduuliOption) createNewModuuli.getValue());
            }

        });
        layout.addComponent(createNewModuuli);

        return layout;
    }

    private void doCreateNewModuuli(KoulutusmoduuliOption option) {

        if (option.tyyppi == null) {
            getWindow().showNotification(option.title + " is not yet implemented", Notification.TYPE_WARNING_MESSAGE);
            return;
        }

        // todo: should check if current dto has been modified

        koulutusmoduuliDTO = uiService.createTutkintoOhjelma();

        final ViewBoundForm oldForm = editFormBinding;
        final ViewBoundForm newForm = createTutkintoOhjelmaEditForm();

        editFormContainer.replaceComponent(oldForm, newForm);
        editFormBinding = newForm;

    }

    private Collection createNewModuuliOptions() {

        return Arrays.asList(
            new KoulutusmoduuliOption(KoulutusmoduuliTyyppi.TUTKINTOON_JOHTAVA, i18n.getMessage("uusiKoulutusmoduuliSelect.tutkintoonJohtava")),
            new KoulutusmoduuliOption(null, i18n.getMessage("uusiKoulutusmoduuliSelect.opintokokonaisuus")),
            new KoulutusmoduuliOption(null, i18n.getMessage("uusiKoulutusmoduuliSelect.opinto")));

    }

    /**
     * Creates component that knows how to edit currently selected Koulutusmoduuli.
     *
     * @return
     */
    private ViewBoundForm createTutkintoOhjelmaEditForm() {

        editForm = new TutkintoOhjelmaEditForm();
        final ViewBoundForm viewBoundForm = new ViewBoundForm(editForm);

        final BeanItem<KoulutusmoduuliDTO> beanItem = editForm.createBeanItem(koulutusmoduuliDTO);
        viewBoundForm.setItemDataSource(beanItem);

        return viewBoundForm;

    }

    private Component createActionButtons() {

        final HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);

        final Button saveAsDraftButton = new Button(i18n.getMessage("saveAsDraftButton"));
        saveAsDraftButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                editFormBinding.commit();
                save();
            }

        });
        
        final Button saveAsCompleteButton = new Button(i18n.getMessage("saveAsCompleteButton"));
        saveAsCompleteButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                editFormBinding.commit();
                // same logic here
                save();
            }
        });

        final Button cancelButton = new Button(i18n.getMessage("cancelButton"));
        
        layout.addComponent(cancelButton);
        layout.addComponent(saveAsDraftButton);
        layout.addComponent(saveAsCompleteButton);

        return layout;


    }

    private void save() {
        
        editForm.save(uiService, koulutusmoduuliDTO);
        
        final KoulutusmoduuliChangedEvent event = new KoulutusmoduuliChangedEvent(koulutusmoduuliDTO, KoulutusmoduuliChangedEvent.EventType.MODIFIED); 
        TarjontaApplication.getBlackboard().fire(event);
        
        getWindow().showNotification(i18n.getMessage("save.success"));        
        
    }

    /**
     * Helper class for Select widgets to pick Koulutusmoduuli type.
     */
    private static class KoulutusmoduuliOption implements Serializable {

        private static final long serialVersionUID = 376737094560614828L;

        private KoulutusmoduuliTyyppi tyyppi;

        private String title;

        public KoulutusmoduuliOption(KoulutusmoduuliTyyppi tyyppi, String title) {
            this.tyyppi = tyyppi;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }

    }


}

