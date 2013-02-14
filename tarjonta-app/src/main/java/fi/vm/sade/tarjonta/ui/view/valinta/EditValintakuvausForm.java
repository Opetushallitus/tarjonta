
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

import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.*;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.*;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;

import org.springframework.beans.factory.annotation.Configurable;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.enums.MetaCategory;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.valinta.ValintaModel;
import fi.vm.sade.tarjonta.ui.model.valinta.ValintaperusteModel;
import fi.vm.sade.tarjonta.ui.presenter.ValintaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalLayout;
import java.util.List;
import java.util.Set;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;

import javax.validation.constraints.NotNull;

/**
 * Valintaperustekuvausryhma form view. The class do not use Vaadin property
 * data binding as there are only very few form fields.
 *
 * @author Jani Wilén
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable
public class EditValintakuvausForm extends AbstractVerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditValintakuvausForm.class);
    private transient UiBuilder uiBuilder;
    private ValintaPresenter presenter;
    //MainLayout element
    private VerticalLayout mainLayout;
    private GridLayout itemContainer;
    //Fields
    @NotNull(message = "{validation.Valinta.ryhma.notNull}")
    private KoodistoComponent kcRyhma;
    private ValintaLanguageTabSheet languagesTab;
    private ErrorMessage errorView;
    private MetaCategory category;

    /*
     * Init view with new model
     */
    public EditValintakuvausForm(MetaCategory category, ValintaPresenter presenter, UiBuilder uiBuilder) {
        super();
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        this.category = category;
    }

    public void initForm(ValintaModel model) {
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    }

    /*
     * Main layout building method.
     */
    private void buildMainLayout() {
        mainLayout = new VerticalLayout();

        //Build main item container
        mainLayout.addComponent(buildGrid());
        addComponent(mainLayout);
    }

    private GridLayout buildGrid() {
        itemContainer = new GridLayout(2, 1);

        addItemToGrid("", buildErrorLayout());
        addItemToGrid("ryhma", buildSelectableKoodistoGroup());

        languagesTab = new ValintaLanguageTabSheet(presenter, category, true, "650", "600");
        addItemToGrid("kuvaus", languagesTab);

        return itemContainer;
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

    private void addItemToGrid(String captionKey, AbstractComponent component) {
        if (captionKey != null) {
            HorizontalLayout hl = UiUtil.horizontalLayout(false, UiMarginEnum.RIGHT);
            hl.setSizeFull();
            Label labelValue = UiUtil.label(hl, T(captionKey));
            hl.setComponentAlignment(labelValue, Alignment.TOP_RIGHT);
            labelValue.setSizeUndefined();
            itemContainer.addComponent(hl);
            itemContainer.setComponentAlignment(hl, Alignment.TOP_RIGHT);
            itemContainer.addComponent(component);
        }
    }

    private HorizontalLayout buildSelectableKoodistoGroup() {
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.BOTTOM);
        kcRyhma = uiBuilder.koodistoComboBox(hl, KoodistoURIHelper.KOODISTO_VALINTAPERUSTEKUVAUSRYHMA);
        kcRyhma.setRequired(true);
        kcRyhma.setImmediate(true);

        return hl;
    }

    public List<KielikaannosViewModel> getkuvaus() {
        return this.languagesTab.getKieliKaannokset();
    }

    public Set<String> getRemovedLanguages() {
        return this.languagesTab.getRemovedTabs();
    }

    public void reloadkuvaus() {
        this.languagesTab.initializeTabsheet();
    }

    public void resetKuvaus() {
        this.languagesTab.resetTabSheets();
    }

    @Override
    protected void buildLayout() {
        buildMainLayout();
        initForm(null);
    }

    /**
     * @return the kcRyhma
     */
    public KoodistoComponent getRyhma() {
        return kcRyhma;
    }
}
