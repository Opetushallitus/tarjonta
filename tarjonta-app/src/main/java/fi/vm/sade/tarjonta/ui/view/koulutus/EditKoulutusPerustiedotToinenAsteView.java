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
package fi.vm.sade.tarjonta.ui.view.koulutus;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.tarjonta.service.GenericFault;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.tarjonta.ui.model.KoulutusYhteyshenkiloViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.tarjonta.ui.view.common.DialogDataTable;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 * @author Jani Wilén
 */
@Configurable(preConstruction = true)
public class EditKoulutusPerustiedotToinenAsteView extends AbstractVerticalNavigationLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedotToinenAsteView.class);
    private KoulutusToisenAsteenPerustiedotViewModel koulutusPerustiedotModel;
    private ErrorMessage errorView;
    @Autowired(required = true)
    private TarjontaPresenter presenter;
    private Label documentStatus;

    public EditKoulutusPerustiedotToinenAsteView() {
        super();
        setMargin(true);
        setSpacing(true);
        setHeight(-1, UNITS_PIXELS);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        initialize(layout); //add layout to navigation container
    }

    //
    // Define data fields
    //
    private void initialize(AbstractLayout layout) {
        LOG.info("initialize() {}", presenter);

        if (presenter == null) {
            //jrebel fix...
            presenter = new TarjontaPresenter();
        }

        koulutusPerustiedotModel = presenter.getModel().getKoulutusPerustiedotModel();
        BeanItem<KoulutusPerustiedotViewModel> hakuBean = new BeanItem<KoulutusPerustiedotViewModel>(koulutusPerustiedotModel);
        /*
         *  PAGE HEADLINE
         */
        HorizontalLayout header = UiUtil.horizontalLayout();
        header.setSizeFull();
        Label pageLabel = UiUtil.label(header, "KoulutuksenPerustiedot", LabelStyleEnum.H2);
        pageLabel.setSizeUndefined();
        documentStatus = UiUtil.label(header, new BeanItem(koulutusPerustiedotModel), "userFrienlyDocumentStatus"); //show document status
        documentStatus.setSizeUndefined();
        header.setExpandRatio(documentStatus, 1l);
        header.setComponentAlignment(documentStatus, Alignment.TOP_RIGHT);
        layout.addComponent(header);
        UiUtil.hr(layout);

        /*
         * TOP ERROR LAYOUT
         */
        layout.addComponent(buildErrorLayout());

        /*
         *  FORM LAYOUT (form components under navigation buttons)
         */
        EditKoulutusPerustiedotFormView editKoulutusPerustiedotFormView = new EditKoulutusPerustiedotFormView(presenter, koulutusPerustiedotModel);
        final Form form = new ValidatingViewBoundForm(editKoulutusPerustiedotFormView);
        form.setItemDataSource(hakuBean);
        form.setValidationVisible(false);
        form.setValidationVisibleOnCommit(false);
        form.setSizeFull();

        layout.addComponent(form);

        /*
         * BOTTOM LAYOUT (TABLES)
         */
        UiUtil.hr(layout);

        final Form yhteisTieto = new ValidatingViewBoundForm(new EditKoulutusYhteystieto(koulutusPerustiedotModel));
        addComponent(yhteisTieto);
        UiUtil.hr(layout);
        addLinkkiSelectorAndEditor(layout);

        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.showMainDefaultView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        addNavigationButton(T("tallennaLuonnoksena"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.saveKoulutusLuonnoksenaModel();
            }
        });

        addNavigationButton(T("tallennaValmiina"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                try {
                    //form.validate();
                    errorView.resetErrors();
                    form.commit();

                    LOG.debug("Form validated successfully.");
                    try {
                        presenter.saveKoulutusValmiina();
                        presenter.showNotification(UserNotification.SAVE_SUCCESS);
                    } catch (GenericFault e) {
                        LOG.error("Application error - KOMOTO persist failed, message :  " + e.getMessage(), e);
                        presenter.showNotification(UserNotification.SAVE_FAILED);
                    } catch (Exception ex) {
                        LOG.error("An unknown application error - KOMOTO persist failed, message :  " + ex.getMessage(), ex);
                        presenter.showNotification(UserNotification.SAVE_FAILED);
                    }
                } catch (Validator.InvalidValueException e) {
                    LOG.debug("Form is missing data - message : {}, causes : {}", e.getMessage(), e.getCauses());
                    errorView.addError(e);
                    presenter.showNotification(UserNotification.GENERIC_VALIDATION_FAILED);
                }
            }
        });
        final Button.ClickListener clickListener = new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // TODO if changes, ask if really wants to navigate away
                presenter.showShowKoulutusView(null);
            }
        };

        addNavigationButton(T("jatka"), clickListener);

        // Make modification to enable/disable the Save button
        form.setFormFieldFactory(new DefaultFieldFactory() {
            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                final AbstractField field = (AbstractField) super.createField(item, propertyId, uiContext);
                field.addListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        if (form.isModified()) {
                            koulutusPerustiedotModel.setDocumentStatus(DocumentStatus.EDITED);
                            for (Button b : getButtonByName(clickListener)) {
                                b.setEnabled(true);
                            }
                        }
                    }
                });
                field.setImmediate(true);

                return field;
            }
        });
    }

    private HorizontalLayout buildErrorLayout() {
        HorizontalLayout topErrorArea = UiUtil.horizontalLayout();
        HorizontalLayout padding = UiUtil.horizontalLayout();
        padding.setWidth(30, UNITS_PERCENTAGE);
        errorView = new ErrorMessage();
        errorView.setSizeUndefined();

        topErrorArea.addComponent(padding);
        topErrorArea.addComponent(errorView);

        return topErrorArea;
    }

    /**
     * Create yhteystiedot part of the form.
     *
     * @param layout
     */
    private void addYhteyshenkiloSelectorAndEditor(AbstractLayout layout) {
        headerLayout(layout, "Yhteyshenkilo");

        //Attach data model to Vaadin bean container.
        final BeanItemContainer<KoulutusYhteyshenkiloViewModel> yhteyshenkiloContainer =
                new BeanItemContainer<KoulutusYhteyshenkiloViewModel>(KoulutusYhteyshenkiloViewModel.class);
        yhteyshenkiloContainer.addAll(koulutusPerustiedotModel.getYhteyshenkilot());

        //Initialize dialog table with control buttons.
        DialogDataTable<KoulutusPerustiedotViewModel> ddt = new DialogDataTable<KoulutusPerustiedotViewModel>(KoulutusYhteyshenkiloViewModel.class, yhteyshenkiloContainer);

        //Overide default button property
        ddt.setButtonProperties("LisaaUusi.Yhteyshenkilo");

        //Add form for dialog.
        ddt.buildByFormLayout(layout, "Luo uusi yhteystieto", 350, 500, new EditKoulutusPerustiedotYhteystietoView());

        //Add visible table columns.
        ddt.setColumnHeader("etunimet", "Etunimi");
        ddt.setColumnHeader("sukunimi", "Sukunimi");
        ddt.setColumnHeader("email", "Sähköposti");
        ddt.setColumnHeader("puhelin", "Puhelin");
        ddt.setColumnHeader("kielet", "Pätee kielille");
        ddt.setVisibleColumns(new Object[]{"etunimet", "sukunimi", "titteli", "email", "puhelin", "kielet"});
        layout.addComponent(ddt);
    }

    /**
     * Create linkkityyppi part of the form.
     *
     * @param layout
     */
    private void addLinkkiSelectorAndEditor(AbstractLayout layout) {
        headerLayout(layout, "Linkit");

        final Class modelClass = KoulutusLinkkiViewModel.class;

        final BeanItemContainer<KoulutusLinkkiViewModel> linkkiContainer =
                new BeanItemContainer<KoulutusLinkkiViewModel>(modelClass, presenter.getModel().getKoulutusPerustiedotModel().getKoulutusLinkit());

        DialogDataTable<KoulutusPerustiedotViewModel> ddt =
                new DialogDataTable<KoulutusPerustiedotViewModel>(modelClass, linkkiContainer);
        ddt.setButtonProperties("LisaaUusi.Linkkityyppi");
        ddt.buildByFormLayout(layout, "Luo uusi linkkityyppi", 400, 360, new EditKoulutusPerustiedotLinkkiView());
        ddt.setColumnHeader("linkkityyppi", T("Linkkityyppi"));
        ddt.setColumnHeader("url", T("LinkkiURL"));
        ddt.setColumnHeader("kielet", T("LinkkiKielet"));
        ddt.setVisibleColumns(new Object[]{"linkkityyppi", "url", "kielet"});
        layout.addComponent(ddt);
    }

    private void headerLayout(final AbstractLayout layout, final String i18nProperty) {
        CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight(20, UNITS_PIXELS);
        cssLayout.addComponent(UiUtil.label(null, i18nProperty));
        layout.addComponent(cssLayout);
    }
}
