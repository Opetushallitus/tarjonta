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
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.tarjonta.ui.view.common.DialogKoodistoDataTable;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.ViewBoundForm;

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
    private int unmodifiedHashcode; //if document is modified after save or load.
    private EditKoulutusPerustiedotFormView editKoulutusPerustiedotFormView;

    public EditKoulutusPerustiedotToinenAsteView() {
        super();
        setMargin(true);
        setHeight(-1, UNITS_PIXELS);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        Panel panel = new Panel();

        panel.setContent(vl);
        layout.addComponent(panel);

        initialize(vl); //add layout to navigation container
        unmodifiedHashcode = koulutusPerustiedotModel.hashCode();
    }

    //
    // Define data fields
    //
    private void initialize(AbstractLayout layout) {
        koulutusPerustiedotModel = presenter.getModel().getKoulutusPerustiedotModel();
        BeanItem<KoulutusPerustiedotViewModel> hakuBean = new BeanItem<KoulutusPerustiedotViewModel>(koulutusPerustiedotModel);
        /*
         *  PAGE HEADLINE
         */
        HorizontalLayout header = UiUtil.horizontalLayout();
        header.setSizeFull();
        Label pageLabel = UiUtil.label(header, T("KoulutuksenPerustiedot"), LabelStyleEnum.H2);
        pageLabel.setSizeUndefined();

//        if (koulutusPerustiedotModel.getTila() != null) {
//            documentStatus = UiUtil.label(header, T("tila." + koulutusPerustiedotModel.getTila().name())); //show document status
//        } else {
//
//        }
        documentStatus = UiUtil.label(layout, ""); //show document status
        documentStatus.setSizeUndefined();
        documentStatus.setImmediate(true);
        documentStatus.setPropertyDataSource(new NestedMethodProperty(koulutusPerustiedotModel, "tila"));
        header.addComponent(documentStatus);

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
        editKoulutusPerustiedotFormView = new EditKoulutusPerustiedotFormView(presenter, koulutusPerustiedotModel);
        final Form form = new ViewBoundForm(editKoulutusPerustiedotFormView);
        form.setItemDataSource(hakuBean);
        form.setValidationVisible(false);
        form.setValidationVisibleOnCommit(false);
        form.setSizeFull();

        layout.addComponent(form);

        /*
         * BOTTOM LAYOUTS
         */
        UiUtil.hr(layout);
        final Form yhteisTieto = new ValidatingViewBoundForm(new EditKoulutusYhteystietoFormView(koulutusPerustiedotModel));
        layout.addComponent(yhteisTieto);

        addLinkkiSelectorAndEditor(layout);

        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.showMainDefaultView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        ClickListener btnListenerTallennaLuonnoksena = new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                save(form, TarjontaTila.LUONNOS);
            }
        };

        addNavigationButton(T("tallennaLuonnoksena"), btnListenerTallennaLuonnoksena, StyleEnum.STYLE_BUTTON_PRIMARY);
        Set<Button> buttonByListener = getButtonByListener(btnListenerTallennaLuonnoksena);
        for (Button btn : buttonByListener) {
            btn.setVisible(presenter.getPermission().userCanReadAndUpdate());
        }

        ClickListener btnListenerTallennaValmiina = new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                save(form, TarjontaTila.VALMIS);
            }
        };

        addNavigationButton(T("tallennaValmiina"), btnListenerTallennaValmiina, StyleEnum.STYLE_BUTTON_PRIMARY);
        buttonByListener = getButtonByListener(btnListenerTallennaValmiina);
        for (Button btn : buttonByListener) {
            btn.setVisible(presenter.getPermission().userCanReadAndUpdate());
        }

        final Button.ClickListener clickListener = new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (isSaved()) {
                    presenter.showNotification(UserNotification.UNSAVED);
                    return;
                }
                try {
                    errorView.resetErrors();
                    form.validate();
                    form.commit();
                    presenter.showShowKoulutusView();
                } catch (Validator.InvalidValueException e) {
                    errorView.addError(e);
                    presenter.showNotification(UserNotification.GENERIC_VALIDATION_FAILED);
                }
            }
        };

        addNavigationButton(T("jatka"), clickListener, StyleEnum.STYLE_BUTTON_PRIMARY);
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
     * Create linkkityyppi part of the form.
     *
     * @param layout
     */
    private void addLinkkiSelectorAndEditor(AbstractLayout layout) {
        final Class modelClass = KoulutusLinkkiViewModel.class;
        List<KoulutusLinkkiViewModel> koulutusLinkit =
                presenter.getModel().getKoulutusPerustiedotModel().getKoulutusLinkit();

        final DialogKoodistoDataTable<KoulutusLinkkiViewModel> ddt =
                new DialogKoodistoDataTable<KoulutusLinkkiViewModel>(modelClass, koulutusLinkit);

        ddt.setButtonProperties("LisaaUusi.Linkkityyppi");
        ddt.buildByFormLayout(layout, "Luo uusi linkkityyppi", 400, 250, new EditKoulutusPerustiedotLinkkiView());
        ddt.setColumnHeader("linkkityyppi", T("Linkkityyppi"));
        ddt.setColumnHeader("url", T("LinkkiURL"));
        ddt.setColumnHeader("kieli", T("LinkkiKielet"));
        ddt.setKoodistoColumns(new String[]{"kieli"});
        ddt.setVisibleColumns(new Object[]{"linkkityyppi", "url", "kieli"});
        ddt.setPageLength(4); //four rows
        layout.addComponent(ddt);
    }

    private void headerLayout(final AbstractLayout layout, final String i18nProperty) {
        CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight(20, UNITS_PIXELS);
        cssLayout.addComponent(UiUtil.label(null, i18nProperty));
        layout.addComponent(cssLayout);
    }

    /*
     * Take a snapshot of model hashcode.
     * Used to check data model modifications.
     */
    private int makeUnmodified() {
        unmodifiedHashcode = koulutusPerustiedotModel.hashCode();
        return unmodifiedHashcode;
    }

    /*
     * Return true, if data in model has changed.
     */
    private boolean isModified() {
        return koulutusPerustiedotModel.hashCode() != unmodifiedHashcode;
    }

    private boolean isSaved() {
        return koulutusPerustiedotModel.isLoaded() && isModified();
    }

    private void save(Form form, TarjontaTila tila) {
        try {
            errorView.resetErrors();
            form.commit();
            try {
                presenter.saveKoulutus(tila);
                makeUnmodified();
                presenter.showNotification(UserNotification.SAVE_SUCCESS);
                presenter.getReloadKoulutusListData();
            } catch (javax.xml.ws.WebServiceException e) {
                LOG.error("Unknown backend service error - KOMOTO persist failed, message :  " + e.getMessage(), e);
                presenter.showNotification(UserNotification.SERVICE_UNAVAILABLE);
            } catch (GenericFault e) {
                LOG.error("Application error - KOMOTO persist failed, message :  " + e.getMessage(), e);
                presenter.showNotification(UserNotification.SAVE_FAILED);
            } catch (Exception ex) {
                LOG.error("An unknown application error - KOMOTO persist failed, message :  " + ex.getMessage(), ex);
                presenter.showNotification(UserNotification.SAVE_FAILED);
            }
        } catch (Validator.InvalidValueException e) {
            errorView.addError(e);
            presenter.showNotification(UserNotification.GENERIC_VALIDATION_FAILED);
        }
    }
}
