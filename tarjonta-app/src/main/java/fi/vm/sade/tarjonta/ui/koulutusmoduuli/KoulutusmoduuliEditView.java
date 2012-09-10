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
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import fi.vm.sade.generic.ui.blackboard.BlackboardContext;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusTila;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.event.KoulutusmoduuliChangedEvent;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.tutkintoohjelma.TutkintoOhjelmaEditPanel;
import fi.vm.sade.tarjonta.ui.service.TarjontaUiService;
import fi.vm.sade.tarjonta.ui.util.I18NHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.ViewBoundForm;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

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
    private AbstractKoulutusmoduuliEditPanel koulutusmoduuliEditPanel;

    /**
     * Wrapper which binds our for which is not of type Form to form model.
     */
    private ViewBoundForm editForm;

    private KoulutusmoduuliDTO koulutusmoduuliDTO;

    @Autowired
    private TarjontaUiService uiService;

    private Button saveAsDraft;

    private Button saveAsFinal;

    private static I18NHelper i18n = new I18NHelper("KoulutusmoduuliEditView.");

    private ComboBox createNewModuuli;

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
        enableActionButtons(false);

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

        createNewModuuli = new ComboBox(null, createNewModuuliOptions());
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

        // todo: when UI flow is defined, organisatioOID will be given to use from .... somewhere

        koulutusmoduuliDTO = uiService.createTutkintoOhjelma("http://organisaatio.fi/suborganisaatio");

        final ViewBoundForm oldForm = editForm;
        final ViewBoundForm newForm = createTutkintoOhjelmaEditForm();

        // could go with "selected" event too
        enableActionButtons(true);

        editFormContainer.replaceComponent(oldForm, newForm);
        editForm = newForm;

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

        koulutusmoduuliEditPanel = new TutkintoOhjelmaEditPanel();
        final ViewBoundForm viewBoundForm = new ViewBoundForm(koulutusmoduuliEditPanel);

        final BeanItem<KoulutusmoduuliDTO> beanItem = koulutusmoduuliEditPanel.createBeanItem(koulutusmoduuliDTO);
        viewBoundForm.setItemDataSource(beanItem);

        return viewBoundForm;

    }

    private void enableActionButtons(boolean enable) {
        saveAsDraft.setEnabled(enable);
        saveAsFinal.setEnabled(enable);
    }

    private Component createActionButtons() {

        final HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);

        saveAsDraft = new Button(i18n.getMessage("saveAsDraftButton"));
        saveAsDraft.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                saveAsDraft();
            }

        });

        saveAsFinal = new Button(i18n.getMessage("saveAsCompleteButton"));
        saveAsFinal.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                saveAsComplete();
            }

        });

        final Button cancelButton = new Button(i18n.getMessage("cancelButton"));

        layout.addComponent(cancelButton);
        layout.addComponent(saveAsDraft);
        layout.addComponent(saveAsFinal);

        return layout;


    }

    private void saveAsDraft() {
        koulutusmoduuliDTO.setTila(KoulutusTila.SUUNNITTELUSSA.name());
        save();
    }

    private void saveAsComplete() {
        koulutusmoduuliDTO.setTila(KoulutusTila.VALMIS.name());
        save();
    }

    private void save() {

        if (!editForm.isValid()) {

            getWindow().showNotification(i18n.getMessage("save.notValid"));
            editForm.setValidationVisible(true);

        } else {

            try {
                // flush values through form model to dto
                editForm.commit();

                // current edit panel know how to push state to server
                koulutusmoduuliEditPanel.save(uiService, koulutusmoduuliDTO);

                BlackboardContext.getBlackboard().fire(new KoulutusmoduuliChangedEvent(koulutusmoduuliDTO,
                        KoulutusmoduuliChangedEvent.EventType.MODIFIED));

                getWindow().showNotification(i18n.getMessage("save.success"));

            } catch (Validator.EmptyValueException e) {
                // no-op
            }

        }

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


    public ComboBox getCreateNewModuuli() {
        return createNewModuuli;
    }

    public Button getSaveAsDraftButton() {
        return saveAsDraft;
    }

    public Button getSaveAsFinalButton() {
        return saveAsFinal;
    }

    public KoulutusmoduuliDTO getKoulutusmoduuliDTO() {
        return koulutusmoduuliDTO;
    }
    
    public AbstractKoulutusmoduuliEditPanel getKoulutusmoduuliEditPanel() {
        return koulutusmoduuliEditPanel;
    }

}

