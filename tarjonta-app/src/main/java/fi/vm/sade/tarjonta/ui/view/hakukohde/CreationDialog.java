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
package fi.vm.sade.tarjonta.ui.view.hakukohde;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/*
 * Author: Tuomas Katva
 *
 * Common dialog for presenting user an option group with all options preselected.
 * Dialog has two buttons "jatka" and "peruuta" you must provide listeners.
 * OptionGroup has getter method so you can get selected options from listeners
 *
 */
@Configurable
public class CreationDialog<T> extends CustomComponent {

    private static final long serialVersionUID = 1L;
    private VerticalLayout rootLayout;
    private HorizontalLayout titleLayout;
    private HorizontalLayout middleLayout;
    private HorizontalLayout buttonLayout;
    private OptionGroup optionGroup;
    protected ErrorMessage errorView;
    private boolean attached = false;
    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;
    private List<T> selectedThings;
    //TODO Use UiBuilder instead of UiUtil ???
    private Button peruutaBtn;
    private Button jatkaBtn;
    private Class<T> typeClazz;
    private static final Logger LOG = LoggerFactory.getLogger(CreationDialog.class);
    private String dialogTitleKey = null;
    private String dialogOptionGroupTitleKey = null;

    public CreationDialog(List<T> selectedThingsParam, Class<T> clazzParam, String dialogTitle, String optionGroupTitle) {
        selectedThings = selectedThingsParam;
        typeClazz = clazzParam;
        rootLayout = new VerticalLayout();
        rootLayout.setMargin(true);
        errorView = new ErrorMessage();

        this.dialogTitleKey = dialogTitle;
        this.dialogOptionGroupTitleKey = optionGroupTitle;
        setCompositionRoot(rootLayout);
    }

    @Override
    public void attach() {
        super.attach();

        if (attached) {
            return;
        }

        if (tarjontaPresenter != null) {
            buildLayout(selectedThings);
        }

        attached = true;
    }

    public void buildLayout(List<T> thingsModel) {
        rootLayout.addComponent(createTitleLayout());
        rootLayout.addComponent(errorView);
        rootLayout.addComponent(createOptionGroupLayout(thingsModel));
        rootLayout.addComponent(createButtonLayout());
    }

    public void removeErrorMessages() {
        if (errorView != null) {
             errorView.resetErrors();
        }
    }

    public void addErrorMessage(String message) {
        if (errorView != null) {
            errorView.addError(message);
        }
    }
    /*
     * Create top horizontal layout containing Dialog title
     */
    private HorizontalLayout createTitleLayout() {
        titleLayout = UiUtil.horizontalLayout();
        titleLayout.setMargin(true, false, false, true);

        Label titleLabel = UiUtil.label(null, I18N.getMessage(this.dialogTitleKey));
        titleLayout.addComponent(titleLabel);
        return titleLayout;
    }

    private HorizontalLayout createOptionGroupLayout(List<T> values) {
        middleLayout = UiUtil.horizontalLayout();
        middleLayout.setMargin(true, false, false, false);
        BeanItemContainer<T> beanValues = new BeanItemContainer<T>(typeClazz, values);

        LOG.debug("values : " + values);

        optionGroup = new OptionGroup(null, beanValues);
        getOptionGroup().setMultiSelect(true);
        //Set all selected as default
        for (Object obj : getOptionGroup().getItemIds()) {
            getOptionGroup().select(obj);
        }
        Label lbl = new Label(I18N.getMessage(this.dialogOptionGroupTitleKey));
        middleLayout.addComponent(lbl);
        middleLayout.addComponent(getOptionGroup());

        return middleLayout;
    }

    private HorizontalLayout createButtonLayout() {
        buttonLayout = UiUtil.horizontalLayout();

        buttonLayout.addComponent(getPeruutaBtn());
        buttonLayout.addComponent(getJatkaBtn());
        buttonLayout.setComponentAlignment(getPeruutaBtn(), Alignment.MIDDLE_LEFT);
        buttonLayout.setComponentAlignment(getJatkaBtn(), Alignment.MIDDLE_RIGHT);

        return buttonLayout;
    }

    public OptionGroup getOptionGroup() {
        return optionGroup;
    }

    public Button getPeruutaBtn() {
        if (peruutaBtn == null) {
            peruutaBtn = UiUtil.buttonSmallPrimary(null, I18N.getMessage("HakukohdeCreationDialog.peruutaBtn"));
        }
        return peruutaBtn;
    }

    public Button getJatkaBtn() {
        if (jatkaBtn == null) {
            jatkaBtn = UiUtil.buttonSmallPrimary(null, I18N.getMessage("HakukohdeCreationDialog.jatkaBtn"));
        }
        return jatkaBtn;
    }

    public String getDialogTitleKey() {
        return dialogTitleKey;
    }

    public void setDialogTitleKey(String dialogTitleKey) {
        this.dialogTitleKey = dialogTitleKey;
    }

    public String getDialogOptionGroupTitleKey() {
        return dialogOptionGroupTitleKey;
    }

    public void setDialogOptionGroupTitleKey(String dialogOptionGroupTitleKey) {
        this.dialogOptionGroupTitleKey = dialogOptionGroupTitleKey;
    }
}
