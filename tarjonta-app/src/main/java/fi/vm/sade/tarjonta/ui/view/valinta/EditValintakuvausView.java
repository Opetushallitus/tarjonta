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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.enums.MetaCategory;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.valinta.ValintaperusteModel;
import fi.vm.sade.tarjonta.ui.presenter.ValintaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractSimpleEditLayoutView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Valintaperustekuvausryhma view.
 *
 * @author Jani Wilén
 */
public class EditValintakuvausView extends AbstractSimpleEditLayoutView {

    private static final Logger LOG = LoggerFactory.getLogger(EditValintakuvausView.class);
    private transient UiBuilder uiBuilder;
    private MetaCategory category;
    private ValintaPresenter presenter;
    private EditValintakuvausForm form;

    public EditValintakuvausView(MetaCategory category, ValintaPresenter presenter, UiBuilder uiBuilder) {
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
        buildFormLayout("Valintaperustekuvaus", presenter, layout, getModel(), form);

        //Koodisto selectbox listener
        form.getRyhma().addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (!isSaved()) {
                    //TODO:
                    errorView.addError(new Validator.InvalidValueException("unsaved"));
                }

                form.resetKuvaus();
                form.getkuvaus().clear(); //clear model data

                if (event != null && event.getProperty() != null) {
                    final String uri = (String) event.getProperty().getValue();
                    LOG.debug("selected uri : {}", uri);
                    ValintaperusteModel model = presenter.getValintaperustemodel(category);
                    presenter.load(category, uri);

//                    if (model != null && model.getKuvaus() != null) {
//                        form.getkuvaus().addAll(model.getKuvaus());
//                    }
//                    
                    form.reloadkuvaus();
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
            presenter.remove(category, getModel().getSelectedUri(), form.getRemovedLanguages());
        }

        getModel().setKuvaus(form.getkuvaus());

        if (getModel().getSelectedUri() != null) {
            presenter.save(category);
        } else {
            notifyValidationError();
        }
    }

    /*
     * Remove back button from default layout.
     */
    @Override
    protected void buildNavigationButtons() {
        //override the abstract layout, because we oly need the save button.
        addNavigationSaveButton(CommonTranslationKeys.TALLENNA, getClickListenerSave());
    }

    private ValintaperusteModel getModel() {
        return presenter.getModel().getKuvausModelByCategory(category);
    }

    private void notifyValidationError() {

        errorView.addError(new Validator.InvalidValueException("error"));
    }
}
