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

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodistoHelper;

import org.springframework.beans.factory.annotation.Configurable;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.enums.MetaCategory;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.presenter.ValintaperustekuvausPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalLayout;
import java.util.List;
import java.util.Map;
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
    private ValintaperustekuvausPresenter presenter;
    //MainLayout element
    private VerticalLayout mainLayout;
    private GridLayout itemContainer;
    //Fields
    @NotNull(message = "{validation.Valinta.ryhma.notNull}")
    private KoodistoComponent kcRyhma;
    private ValintaLanguageTabSheet languagesTab;
    private ErrorMessage errorView;
    private MetaCategory category;
    private HorizontalLayout hlRyhma;

    /*
     * Init view with new model
     */
    public EditValintakuvausForm(MetaCategory category, ValintaperustekuvausPresenter presenter, UiBuilder uiBuilder) {
        super();
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        this.category = category;
        presenter.loadMetaDataToModel(category);
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
        hlRyhma = UiUtil.horizontalLayout(true, UiMarginEnum.BOTTOM);

        itemContainer = new GridLayout(2, 1);
        addItemToGrid("ryhma", createKoodistoComponentRyhma());

        languagesTab = new ValintaLanguageTabSheet(presenter, category, true, "650", "600");
        addItemToGrid("kuvaus", languagesTab);

        return itemContainer;
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

    /**
     * Method updates selected caption item in the koodisto combobox. The method
     * is called when user saves form data. TODO: Remove this when
     * KoodistoComponent has a reload data method...
     */
    public void reloadKoodistoComponentRyhmaCaption() {
        if (kcRyhma != null && formatter != null && presenter != null) {
            final FieldValueFormatter fcf = kcRyhma.getFieldValueFormatter();
            final String koodiUri = (String) fcf.formatFieldValue(kcRyhma.getField().getValue());
            final Item item = kcRyhma.getField().getItem(koodiUri);
            Property fieldCaption = item.getItemProperty("fieldCaption");
            fieldCaption.setValue(formatter.formatCaption(presenter.getKoodiByUri(koodiUri)));
        }
    }

    public List<KielikaannosViewModel> getkuvaus() {
        return this.languagesTab.getKieliKaannokset();
    }

    /**
     * Get all removed language uris.
     *
     * @return set
     */
    public Set<String> getRemovedLanguages() {
        return this.languagesTab.getRemovedTabs();
    }

    public void initializeKuvausTabSheet() {
        this.languagesTab.initializeTabsheet();
    }

    public void resetKuvausTabSheet() {
        this.languagesTab.resetTabSheets();
    }

    @Override
    protected void buildLayout() {
        buildMainLayout();
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    }

    /**
     * @return the kcRyhma
     */
    public KoodistoComponent getRyhma() {
        return kcRyhma;
    }

    private static String createLanguageCodeCaption(final String name, final List<KielikaannosViewModel> categoryUris) {
        LOG.debug("createLanguageCodeCaption");
        if (categoryUris == null || categoryUris.isEmpty()) {
            return name;
        }

        StringBuilder sb = new StringBuilder(name).append(" ");
        for (KielikaannosViewModel kvm : categoryUris) {
            sb.append("* ");
        }

        LOG.debug("caption : {}", sb.toString());

        return sb.toString();
    }

    private KoodistoComponent createKoodistoComponentRyhma() {
        if (category.equals(MetaCategory.SORA_KUVAUS)) {
            kcRyhma = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_SORA_KUVAUSRYHMA_URI);
        } else if (category.equals(MetaCategory.VALINTAPERUSTEKUVAUS)) {
            kcRyhma = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_VALINTAPERUSTEKUVAUSRYHMA_URI);
        } else {
            throw new RuntimeException("An unknown meta category. Meta : " + category);
        }
        kcRyhma.setRequired(true);
        kcRyhma.setImmediate(true);
        kcRyhma.setCaptionFormatter(formatter);

        kcRyhma.getField()
                .setImmediate(true);
        kcRyhma.getField()
                .setNullSelectionAllowed(false);

        return kcRyhma;
    }
    private final CaptionFormatter formatter = new CaptionFormatter() {
        @Override
        public String formatCaption(Object dto) {
            if (dto instanceof KoodiType) {
                KoodiType koodiDTO = (KoodiType) dto;
                final String koodiVersionUri = TarjontaUIHelper.createVersionUri(
                        koodiDTO.getKoodiUri(),
                        koodiDTO.getVersio());

                Map<String, List<KielikaannosViewModel>> categoryUris = presenter.getValintaperustemodel(category).getCategoryUris();
                KoodiMetadataType metadata = KoodistoHelper.getKoodiMetadataForLanguage(koodiDTO, KoodistoHelper.getKieliForLocale(I18N.getLocale()));
                if ((metadata == null)) {
                    //only koodisto data available
                    return koodiDTO.getKoodiArvo();
                } else if (categoryUris != null && categoryUris.containsKey(koodiVersionUri)) {
                    return createLanguageCodeCaption(
                            metadata.getNimi(),
                            categoryUris.get(koodiVersionUri));
                } else {
                    return metadata.getNimi();
                }
            } else {
                return dto.toString();
            }
        }
    };
}
