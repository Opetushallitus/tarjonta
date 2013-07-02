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
package fi.vm.sade.tarjonta.ui.view.valinta;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.enums.MetaCategory;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.valinta.ValintaperusteModel;
import fi.vm.sade.tarjonta.ui.presenter.ValintaperustekuvausPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractSimpleEditLayoutView;
import fi.vm.sade.vaadin.constants.StyleEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Valintaperustekuvausryhma view.
 *
 * Note: This ui is used to manage and update both Valintaperustekuvaus AND SORA-vaatimus descriptions.
 *
 * TODO hakukelpoisuusvaatimus
 *
 * @author Jani Wilén
 */
public class EditValintakuvausView extends AbstractSimpleEditLayoutView {

    private static final Logger LOG = LoggerFactory.getLogger(EditValintakuvausView.class);
    private static final long serialVersionUID = 7150512914130889822L;
    private transient UiBuilder uiBuilder;
    private MetaCategory category;
    private ValintaperustekuvausPresenter presenter;
    private EditValintakuvausForm form;

    public EditValintakuvausView(MetaCategory category, ValintaperustekuvausPresenter presenter, UiBuilder uiBuilder) {
        super();
        this.category = category;
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        super.buildLayout(layout); //init base navigation here
        /*
         *  FORM LAYOUT (form components under navigation buttons)
         */
        form = new EditValintakuvausForm(category, presenter, uiBuilder);
        buildFormLayout(presenter, layout, getValintaModel(), form);

        //Koodisto selectbox listener
        form.getRyhma().addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (event != null && event.getProperty() != null && event.getProperty().getValue() != null) {
                    final String newUri = (String) event.getProperty().getValue();
                    final String selectedUri = getValintaModel().getSelectedUri();

                    setModelDataToValidationHandler();

                    if (presenter.getModel().isForward()) {
                        /*
                         * When an user has decided to leave page without saving.
                         */
                        LOG.debug("Form data : go to");
                        final String to = presenter.getModel().getForwardToUri();
                        reload(to);

                        presenter.getModel().setForwardToUri(null);
                        presenter.getModel().setForward(false);
                    } else if (selectedUri != null && selectedUri.equals(newUri)) {
                        LOG.debug("Form data : ignore event");
                    } else if (selectedUri != null && !isSaved()) {
                        LOG.debug("Form data : modified");

                        /*
                         * When user have unsaved data.
                         */
                        changeSelectedRyhma(selectedUri); //revert event value change

                        //Open modal dialog.
                        final SaveDialogView modal = presenter.showSaveDialog();
                        if (!presenter.getModel().isForward()) {
                            presenter.getModel().setForwardToUri(newUri);
                        }

                        modal.addNavigationButton(I18N.getMessage("hylkaa"), new Button.ClickListener() {
                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                final String forwardUri = presenter.getModel().getForwardToUri();
                                LOG.debug("Form data : Load data and got to the next page. Goto : {}", forwardUri);

                                presenter.getRootView().removeWindow(modal);
                                modal.removeDialogButtons();
                                presenter.getModel().setForward(true);
                                changeSelectedRyhma(forwardUri); //revert event value change
                            }
                        }, StyleEnum.STYLE_BUTTON_PRIMARY);

                        modal.buildDialogButtons();
                    } else if (newUri != null) {
                        LOG.debug("Form data : unmodified");
                        //change page.

                        reload(newUri);
                    }
                }
            }
        });
    }

    @Override
    public boolean isformDataLoaded() {
        return presenter.getModel().getKuvausModelByCategory(category).isLoaded();
    }

    @Override
    public void actionSave(ClickEvent event) throws Exception {
        LOG.debug("actionSave");
        if (form.getRemovedLanguages() != null && !form.getRemovedLanguages().isEmpty()) {
            LOG.debug("remove");
            presenter.remove(category, getValintaModel().getSelectedUri(), form.getRemovedLanguages());
        }

        getValintaModel().setKuvaus(form.getkuvaus());

        if (getValintaModel().getSelectedUri() != null) {
            presenter.save(category);
        } else {
            throw new Validator.InvalidValueException("error");
        }

        //presenter.getRootView().executeJavaScript("window.location.reload();");
         presenter.loadMetaDataToModel(category);
        form.reloadKoodistoComponentRyhmaCaption();
    }

    /*
     * Remove back button from default layout.
     */
    @Override
    protected void buildNavigationButtons() {
        //override the abstract layout, because we oly need the save button.
        addNavigationSaveButton(CommonTranslationKeys.TALLENNA, getClickListenerSave());
    }

    private ValintaperusteModel getValintaModel() {
        return presenter.getModel().getKuvausModelByCategory(category);
    }

    public void changeSelectedRyhma(String selectedUri) {
        form.getRyhma().setValue(selectedUri);
    }

    /*
     * Reload form data.
     */
    private void reload(final String uri) {
        //Load data from back-end service and store it to meta data model.
        presenter.load(category, uri);
        //Reset all tabsheet to 'the factory settings'.
        form.resetKuvausTabSheet();
        //Clear description data from the form.
        form.getkuvaus().clear();
        //Initialize tabsheet data by valinta model values.
        form.initializeKuvausTabSheet();
        //Tab form data binding hard to implement, so we need to manually set
        //data model to super class.
        setModelDataToValidationHandler();
        //Reset checksum.
        makeFormDataUnmodified();
    }

    private void setModelDataToValidationHandler() {
        //this is a quick data binding hack.
        getValintaModel().setKuvaus(form.getkuvaus());
        setModel(getValintaModel());
    }
}
