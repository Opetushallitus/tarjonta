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
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
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
 * @author Jani Wilén
 */
@Configurable(preConstruction = true)
public abstract class AbstractSimpleEditLayoutView<MODEL extends BaseUIViewModel, VIEW extends AbstractLayout> extends AbstractVerticalNavigationLayout {
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSimpleEditLayoutView.class);
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    @Autowired(required = true)
    private transient TarjontaUIHelper uiHelper;
    private Button.ClickListener clickListenerBack;
    private Button.ClickListener clickListenerSave;
    private int formDataUnmodifiedHashcode = -1;
    protected ErrorMessage errorView;
    private Form form;
    private MODEL model;
    private CommonPresenter presenter;
    private Label labelDocumentStatus;
    private Panel formPanel;
    
    public AbstractSimpleEditLayoutView() {
        super();
    }
    
    public void setFormDataObject(final MODEL model) {
        if (model != null) {
            setFormDataHashcode(model);
        }
    }
    
    public void buildFormLayout(final CommonPresenter presenter, final AbstractLayout layout, final MODEL model, final VIEW view) {
        buildFormLayout(null, presenter, layout, model, view);
    }
    
    public void buildFormLayout(final String titleProperty, final CommonPresenter presenter, final AbstractLayout layout, final MODEL model, final VIEW view) {
        //check arguments
        validArg(presenter, "the presenter object has not been set correctly");
        validArg(model, "the form data model has not been set correctly");
        validArg(view, "the form data view has not been set correctly");
        validArg(layout, "the form base layout view has not been set correctly");

        //set data
        setFormDataObject(model);
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
        layout.addComponent(formPanel);


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
    
    protected void buildNavigationButtons() {

        //add buttons to layout
        addNavigationButton("", getClickListenerBack(), StyleEnum.STYLE_BUTTON_BACK);
        addNavigationSaveButton(CommonTranslationKeys.TALLENNA, getClickListenerSave());
    }
    
    private void buildInformationLayout(final String titleProperty, final AbstractLayout layout) {
        /*
         *  PAGE HEADLINE
         */
        
        if (titleProperty != null) {
            HorizontalLayout header = UiUtil.horizontalLayout();
            header.setSizeFull();
            Label pageLabel = UiUtil.label(header, T(titleProperty), LabelStyleEnum.H2);
            pageLabel.setSizeUndefined();
            
            labelDocumentStatus = UiUtil.label(layout, ""); //show document status
            labelDocumentStatus.setSizeUndefined();
            labelDocumentStatus.setImmediate(true);
            header.addComponent(labelDocumentStatus);
            
            header.setExpandRatio(labelDocumentStatus, 1l);
            header.setComponentAlignment(labelDocumentStatus, Alignment.TOP_RIGHT);
            layout.addComponent(header);
            UiUtil.hr(layout);
        }
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
        
        clickListenerSave = new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            
            @Override
            public void buttonClick(Button.ClickEvent event) {
                eventSave(event);
            }
        };
    }

    /*
     * 
     * Methods for tarjonta states for buttons.
     * 
     */
    protected void addNavigationSaveButton(final String property, final Button.ClickListener listener) {
        addNavigationButton(T(property), listener, StyleEnum.STYLE_BUTTON_PRIMARY);
    }

    /*
     * 
     * Navigation button events.
     * 
     */
    protected void eventBack(Button.ClickEvent event) {
        presenter.showMainDefaultView();
    }
    
    protected void eventSave(Button.ClickEvent event) {
        save(event);
    }
    
    public abstract boolean isformDataLoaded();

    /**
     * Always called after form validation. Return tarjonta data OID.
     *
     * @return OID
     */
    public abstract void actionSave(Button.ClickEvent event) throws Exception;
    
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
    
    protected boolean isSaved() {
        
        return isformDataLoaded() && !isModified();
    }

    /*
     * Take a snapshot of model hashcode.
     * Used to check data model modifications.
     */
    public int makeFormDataUnmodified() {
        if (model == null) {
            throw new RuntimeException("Initialization error - the form data model has not been set correctly.");
        }
        
        formDataUnmodifiedHashcode = model.hashCode();
        return formDataUnmodifiedHashcode;
    }
    
    private void save(Button.ClickEvent event) {
        try {
            validateFormData();
            try {
                actionSave(event);
                makeFormDataUnmodified();
                presenter.showNotification(UserNotification.SAVE_SUCCESS);
            } catch (javax.xml.ws.WebServiceException e) {
                LOG.error("Unknown backend service error - persist failed, message :  " + e.getMessage(), e);
                presenter.showNotification(UserNotification.SERVICE_UNAVAILABLE);
            } catch (GenericFault e) {
                LOG.error("Application error - persist failed, message :  " + e.getMessage(), e);
                presenter.showNotification(UserNotification.SAVE_FAILED);
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
        this.setModel(model);
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

    /**
     * @return the clickListenerBack
     */
    public Button.ClickListener getClickListenerBack() {
        return clickListenerBack;
    }

    /**
     * @return the clickListenerSave
     */
    public Button.ClickListener getClickListenerSave() {
        return clickListenerSave;
    }

    /**
     * @param model the model to set
     */
    public void setModel(MODEL model) {
        this.model = model;
    }
}
