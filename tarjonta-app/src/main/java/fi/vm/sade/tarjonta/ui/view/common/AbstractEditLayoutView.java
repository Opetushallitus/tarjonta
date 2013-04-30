
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
package fi.vm.sade.tarjonta.ui.view.common;

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.koodisto.service.GenericFault;
//import fi.vm.sade.tarjonta.poc.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;
import fi.vm.sade.tarjonta.ui.presenter.CommonPresenter;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author Tuomas Katva
 * @author Jani Wilén
 */
@Configurable(preConstruction = true)
public abstract class AbstractEditLayoutView<MODEL extends BaseUIViewModel, VIEW extends AbstractLayout> extends AbstractVerticalNavigationLayout {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractEditLayoutView.class);
    private static final TarjontaTila TO_STATE_LUONNOS = TarjontaTila.LUONNOS;
    private static final TarjontaTila[] TO_STATE_VALMIS = {TO_STATE_LUONNOS, TarjontaTila.VALMIS, TarjontaTila.JULKAISTU};
    private static final long serialVersionUID = 6843368378990612314L;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    @Autowired(required = true)
    private transient TarjontaUIHelper uiHelper;
    protected Button.ClickListener clickListenerBack;
    protected Button.ClickListener clickListenerSaveAsDraft;
    protected Button.ClickListener clickListenerSaveAsReady;
    protected Button.ClickListener clickListenerNext;
    private String modelOid;
    private SisaltoTyyppi sisalto;
    private int formDataUnmodifiedHashcode = -1;
    protected ErrorMessage errorView;
    private Form form;
    private MODEL model;
    private String tilaNestedProperty = "tila"; //all models should have a variable name 'tila' for TarjontaTila enum.
    private CommonPresenter presenter;
    private Label labelDocumentStatus;
    private boolean manualFormAttach = false;
    private Panel formPanel;

    public AbstractEditLayoutView(String oid, SisaltoTyyppi sisalto) {
        super();
        setFormDataObject(oid, sisalto, null);
    }

    public AbstractEditLayoutView(final String oid, final SisaltoTyyppi sisalto, final MODEL model) {
        super();
        setFormDataObject(oid, sisalto, model);
    }

    public AbstractEditLayoutView(final String oid, final SisaltoTyyppi sisalto, final MODEL model, final boolean manualFormAttach) {
        super();
        setFormDataObject(oid, sisalto, model);
        this.manualFormAttach = manualFormAttach; //do not add form layout to the navigation parent layout
    }

    public void setFormDataObject(final String oid, final SisaltoTyyppi sisalto, final MODEL model) {
        this.sisalto = sisalto;
        this.modelOid = oid;
        if (model != null) {
            setFormDataHashcode(model);
        }
    }

    public void buildFormLayout(final String titleProperty, final CommonPresenter presenter, final AbstractLayout layout, final MODEL model, final VIEW view) {
        //check arguments
        validArg(presenter, "the presenter object has not been set correctly");
        validArg(model, "the form data model has not been set correctly");
        validArg(view, "the form data view has not been set correctly");
        validArg(layout, "the form base layout view has not been set correctly");

        //set data
        this.model = model;
        this.errorView = new ErrorMessage();

        //set presenter reference
        setPresenter(presenter);

        //build buttons
        buildNavigationButtons();

        //build whole layout
        buildValidationLayout(titleProperty, layout, view);

    }

    private void buildValidationLayout(final String titleProperty, final AbstractLayout layout, final VIEW view) {
        //create panel inside navigation layout
        formPanel = new Panel();

        //create layout inside of the panel
        VerticalLayout vlBaseFormLayout = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        getFormPanel().setContent(vlBaseFormLayout);

        if (!manualFormAttach) {
            //some cases it's better to make manual attach to parent layout.
            layout.addComponent(formPanel);
        }

        /*
         * TOP TITLE AND STATUS LAYOUT 
         */
        buildInformationLayout(titleProperty, vlBaseFormLayout);

        /*
         * MIDDLE ERROR LAYOUT (only visible when is has validation errors)
         */
        buildErrorLayoutWrapper(vlBaseFormLayout);

        /*
         * THE GIVEN VIEW FORM
         */
        bindModelToViewForm(vlBaseFormLayout, view);

    }

    private void buildNavigationButtons() {
        //add buttons to layout
        addNavigationButton("", clickListenerBack, StyleEnum.STYLE_BUTTON_BACK);
        addNavigationSaveButton(CommonTranslationKeys.TALLENNA_LUONNOKSENA, clickListenerSaveAsDraft, TO_STATE_LUONNOS);
        addNavigationSaveButton(CommonTranslationKeys.TALLENNA_VALMIINA, clickListenerSaveAsReady, TO_STATE_VALMIS);
        addNavigationButton(T(CommonTranslationKeys.JATKA), clickListenerNext, StyleEnum.STYLE_BUTTON_PRIMARY);

        //enable or disable the save button states
        updateNavigationButtonStates(modelOid, sisalto);
    }

    private void buildInformationLayout(final String titleProperty, final AbstractLayout layout) {
        /*
         *  PAGE HEADLINE
         */
        HorizontalLayout header = UiUtil.horizontalLayout();
        header.setSizeFull();
        Label pageLabel = UiUtil.label(header, T(titleProperty), LabelStyleEnum.H2);
        pageLabel.setSizeUndefined();

        labelDocumentStatus = UiUtil.label(layout, ""); //show document status
        labelDocumentStatus.setSizeUndefined();
        labelDocumentStatus.setImmediate(true);
        labelDocumentStatus.setPropertyDataSource(new NestedMethodProperty(model, tilaNestedProperty));
        header.addComponent(labelDocumentStatus);

        header.setExpandRatio(labelDocumentStatus, 1l);
        header.setComponentAlignment(labelDocumentStatus, Alignment.TOP_RIGHT);
        layout.addComponent(header);
        UiUtil.hr(layout);
    }

    private void bindModelToViewForm(AbstractLayout layout, VIEW view) {

        //bind data to form
        form = new ValidatingViewBoundForm(view);
        form.setItemDataSource(new BeanItem<MODEL>(model));
        form.setValidationVisible(false);
        form.setValidationVisibleOnCommit(false);
        form.setSizeFull();

        setFormDataHashcode(model);

        layout.addComponent(form);
    }

    private void buildErrorLayoutWrapper(AbstractLayout layout) {
        HorizontalLayout topErrorArea = UiUtil.horizontalLayout();
        HorizontalLayout padding = UiUtil.horizontalLayout();
        padding.setWidth(30, UNITS_PERCENTAGE);
        errorView = new ErrorMessage();
        errorView.setSizeUndefined();

        topErrorArea.addComponent(padding);
        topErrorArea.addComponent(errorView);

        layout.addComponent(topErrorArea);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        //INIT listeners

        clickListenerBack = new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                eventBack(event);
            }
        };

        clickListenerSaveAsDraft = new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                eventSaveAsDraft(event);
            }
        };
        clickListenerSaveAsReady = new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                eventSaveAsReady(event);
            }
        };

        clickListenerNext = new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                eventNext(event);
            }
        };
    }

    /*
     * 
     * Methods for tarjonta states for buttons.
     * 
     */
    protected void addNavigationSaveButton(final String property, final Button.ClickListener listener, final TarjontaTila... tila) {
        addNavigationButton(T(property), listener, StyleEnum.STYLE_BUTTON_PRIMARY);
    }

    protected void updateNavigationButtonStates(final Button.ClickListener listener, final String oid, final TarjontaTila... tila) {
        LOG.debug("updateNavigationButtonStates, {}, {}", oid, sisalto);
        enableButtonByListener(listener,
                presenter.isSaveButtonEnabled(
                oid,
                sisalto, tila));
        visibleButtonByListener(listener, presenter.getPermission().userCanEditHaku());
    }

    protected void updateNavigationButtonStates(final String oid, final SisaltoTyyppi sisalto) {
        updateNavigationButtonStates(clickListenerSaveAsDraft, oid, TO_STATE_LUONNOS);
        updateNavigationButtonStates(clickListenerSaveAsReady, oid, TO_STATE_VALMIS);
    }

    /*
     * 
     * Navigation button events.
     * 
     */
    protected void eventBack(Button.ClickEvent event) {
        presenter.showMainDefaultView();
    }

    protected void eventSaveAsDraft(Button.ClickEvent event) {
        save(SaveButtonState.SAVE_AS_DRAFT, event);
    }

    protected void eventSaveAsReady(Button.ClickEvent event) {
        save(SaveButtonState.SAVE_AS_READY, event);
    }

    protected void eventNext(Button.ClickEvent event) {
        if (!isSaved()) {
            try {
                validateFormData();
            } catch (Validator.InvalidValueException e) {
                errorView.addError(e);
            }

            presenter.showNotification(UserNotification.UNSAVED);
            return;
        }

        try {
            validateFormData();
            actionNext(event);
        } catch (Validator.InvalidValueException e) {
            errorView.addError(e);
            presenter.showNotification(UserNotification.GENERIC_VALIDATION_FAILED);
        }
    }

    public abstract void actionNext(Button.ClickEvent event);

    public abstract boolean isformDataLoaded();

    /**
     * Always called after form validation. Return tarjonta data OID.
     *
     * @return OID
     */
    public abstract String actionSave(SaveButtonState tila, Button.ClickEvent event) throws Exception;

    private void validateFormData() throws Validator.InvalidValueException {
        errorView.resetErrors();
        form.commit();
    }

    /*
     * Return true, if data in model has changed.
     */
    private boolean isModified() {
        if (model == null) {
            throw new RuntimeException("Initialization error - the form data model has not been set correctly.");
        }

        return model.hashCode() != formDataUnmodifiedHashcode;
    }

    private boolean isSaved() {
        return isformDataLoaded() && !isModified();
    }

    /*
     * Take a snapshot of model hashcode.
     * Used to check data model modifications.
     */
    private int makeFormDataUnmodified() {
        if (model == null) {
            throw new RuntimeException("Initialization error - the form data model has not been set correctly.");
        }

        formDataUnmodifiedHashcode = model.hashCode();
        return formDataUnmodifiedHashcode;
    }

    private void save(final SaveButtonState tila, Button.ClickEvent event) {
        try {
            validateFormData();
            try {
                modelOid = actionSave(tila, event);
                makeFormDataUnmodified();
                presenter.showNotification(UserNotification.SAVE_SUCCESS);
                updateNavigationButtonStates(modelOid, sisalto);
            } catch (javax.xml.ws.WebServiceException e) {
                LOG.error("Unknown backend service error - persist failed, message :  " + e.getMessage(), e);
                presenter.showNotification(UserNotification.SERVICE_UNAVAILABLE);
            } catch (GenericFault e) {
                LOG.error("Application error - persist failed, message :  " + e.getMessage(), e);
                presenter.showNotification(UserNotification.SAVE_FAILED);
            } catch (Validator.InvalidValueException ex) {
                errorView.addError(ex.getMessage());
                presenter.showNotification(UserNotification.GENERIC_VALIDATION_FAILED);
            } catch (Exception ex) {
                LOG.error("An unknown application error - persist failed, message :  " + ex.getMessage(), ex);
                presenter.showNotification(UserNotification.SAVE_FAILED);
            }
        } catch (Validator.InvalidValueException e) {
            errorView.addError(e);
            presenter.showNotification(UserNotification.GENERIC_VALIDATION_FAILED);
        }
    }

    /**
     * @return the form
     */
    private Form getForm() {
        if (form == null) {
            throw new RuntimeException("Initialization error - the form not initialized or set correctly.");
        }

        return form;
    }

    /**
     * @param formDataHashcode the formDataHashcode to set
     */
    public void setFormDataHashcode(final MODEL model) {
        this.model = model;
        makeFormDataUnmodified();
    }

    /**
     * @return the presenter
     */
    public void setPresenter(CommonPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * @return the uiBuilder
     */
    public UiBuilder getUiBuilder() {
        return uiBuilder;
    }

    /**
     * @return the uiHelper
     */
    public TarjontaUIHelper getUiHelper() {
        return uiHelper;
    }

    /**
     * @param tilaNestedProperty the tilaNestedProperty to set
     */
    public void setTilaNestedProperty(String tilaNestedProperty) {
        this.tilaNestedProperty = tilaNestedProperty;
    }

    private void validArg(final Object obj, final String msg) {
        if (obj == null) {
            throw new IllegalArgumentException("Initialization error - " + msg + ".");
        }
    }

    /**
     * @return the formPanel
     */
    public Panel getFormPanel() {
        return formPanel;
    }
}
