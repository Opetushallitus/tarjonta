package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;/*
 *
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

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakukohdeLiiteViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Created by: Tuomas Katva
 * Date: 15.1.2013
 */
@Configurable(preConstruction = true)
public class HakukohteenLiitteetTabImpl extends AbstractVerticalNavigationLayout {

    @Autowired
    private TarjontaPresenter presenter;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;

    private Form form;
    private BeanItem<HakukohdeLiiteViewModel> hakukohdeLiiteBean;
    private ErrorMessage errorView;
    private HakukohteenLiitteetViewImpl liitteet;

    public HakukohteenLiitteetTabImpl() {
        super();
        setHeight(-1, UNITS_PIXELS);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        liitteet = new HakukohteenLiitteetViewImpl(presenter,uiBuilder);
        //TODO, after the table is inserted in this layout change this
        presenter.getModel().getHakukohde().getLiites().add(new HakukohdeLiiteViewModel());
        this.initForm(presenter.getModel().getHakukohde().getLiites().get(0));
        layout.addComponent(buildErrorLayout());
        layout.addComponent(form);
        form.setSizeFull();
        createButtons();
    }

    public void initForm(HakukohdeLiiteViewModel hakukohdeLiite) {
        hakukohdeLiiteBean = new BeanItem<HakukohdeLiiteViewModel>(hakukohdeLiite);
        form = new ValidatingViewBoundForm(liitteet);
        form.setItemDataSource(hakukohdeLiiteBean);

        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
        this.form.setValidationVisible(false);
        this.form.setValidationVisibleOnCommit(false);
    }


    private void createButtons() {
        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.showMainDefaultView();
                presenter.getHakukohdeListView().reload();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        /*addNavigationButton(T("tallennaLuonnoksena"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

            }
        });*/

        addNavigationButton(T("tallennaValmiina"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                //presenter.commitHakukohdeForm("VALMIS");
                saveForm(null);
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

    private void saveForm(String tila) {
        try {
            errorView.resetErrors();
            form.commit();
            presenter.saveHakukohdeLiite(presenter.getModel().getHakukohde().getLiites().get(0));
        }   catch (Validator.InvalidValueException e) {
            errorView.addError(e);
            presenter.showNotification(UserNotification.GENERIC_VALIDATION_FAILED);
        }
    }
}
