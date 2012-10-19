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

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.service.GenericFault;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.helper.BeanItemMapper;
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
import java.util.logging.Level;
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
    @Autowired(required = true)
    private TarjontaPresenter presenter;
    private BeanItemMapper<KoulutusPerustiedotViewModel, EditKoulutusPerustiedotToinenAsteView> bim;
    private ErrorMessage errorView;
    private KoulutusToisenAsteenPerustiedotViewModel koulutusPerustiedotModel;

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

        bim = new BeanItemMapper<KoulutusPerustiedotViewModel, EditKoulutusPerustiedotToinenAsteView>(koulutusPerustiedotModel,
                getI18n(), this);
        bim.label(layout, "KoulutuksenPerustiedot", LabelStyleEnum.H2);

        UiUtil.hr(layout);

        EditKoulutusPerustiedotFormView editKoulutusPerustiedotFormView = new EditKoulutusPerustiedotFormView(presenter, koulutusPerustiedotModel, bim);
        final Form form = new ValidatingViewBoundForm(editKoulutusPerustiedotFormView);

        /*
         * TOP ERROR LAYOUT
         */
        layout.addComponent(buildErrorLayout());

        /*
         * TOP LAYOUT (form components under navigation buttons)
         */
        //final Form form = new ValidatingViewBoundForm(this);
        form.setItemDataSource(hakuBean);
        form.setValidationVisible(false);
        form.setValidationVisibleOnCommit(false);
        form.setSizeFull();
        layout.addComponent(form);

        /*
         * BOTTOM LAYOUT (TABLES)
         */
        UiUtil.hr(layout);
        addYhteyshenkiloSelectorAndEditor(layout);
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
                    } catch (ExceptionMessage ex) {
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

        addNavigationButton(T("jatka"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // TODO if changes, ask if really wants to navigate away
                presenter.showShowKoulutusView(null);
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
        DialogDataTable<KoulutusPerustiedotViewModel> ddt = new DialogDataTable<KoulutusPerustiedotViewModel>(KoulutusYhteyshenkiloViewModel.class, yhteyshenkiloContainer, bim);

        //Overide default button property
        ddt.setButtonProperties("DialogDataTable.LisaaUusi.Yhteyshenkilo");

        //Add form for dialog.
        ddt.buildByFormLayout(layout, "Luo uusi yhteystieto", 350, 450, new EditKoulutusPerustiedotYhteystietoView());

        //Add visible table columns.
        ddt.setColumnHeader("email", "Sähköposti");
        ddt.setColumnHeader("puhelin", "Puhelin");
        ddt.setColumnHeader("nimi", "Nimi");
        ddt.setColumnHeader("kielet", "Pätee kielille");
        ddt.setVisibleColumns(new Object[]{"nimi", "titteli", "email", "puhelin", "kielet"});
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
        List<KoulutusLinkkiViewModel> koulutusLinkit =
                presenter.getKoulutusToisenAsteenPerustiedotViewModel().getKoulutusLinkit();

        final BeanItemContainer<KoulutusLinkkiViewModel> linkkiContainer =
                new BeanItemContainer<KoulutusLinkkiViewModel>(modelClass);

        linkkiContainer.addAll(koulutusLinkit);

        DialogDataTable<KoulutusPerustiedotViewModel> ddt =
                new DialogDataTable<KoulutusPerustiedotViewModel>(modelClass, linkkiContainer, bim);
        ddt.setButtonProperties("DialogDataTable.LisaaUusi.Linkkityyppi");
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
        cssLayout.addComponent(bim.label(null, i18nProperty));
        layout.addComponent(cssLayout);
    }
}
