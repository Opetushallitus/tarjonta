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

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Koulutus edit form for second degree studies.
 *
 * @author Jani Wilén
 * @author mlyly
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class EditKoulutusPerustiedotFormView extends GridLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedotFormView.class);
    private static final String MODEL_NAME_PROPERY = "nimi";
    private static final String MODEL_DESC_PROPERY = "kuvaus";
    private static final String PROPERTY_PROMPT_SUFFIX = ".prompt";
    private transient I18NHelper _i18n;
    private KoulutusToisenAsteenPerustiedotViewModel koulutusModel;
    private TarjontaPresenter presenter;
    private Map<KoulutusasteType, Set<Component>> selectedComponents;
    private BeanItemContainer<KoulutusohjelmaModel> bicKoulutusohjelma;
    private BeanItemContainer<KoulutuskoodiModel> bicKoulutuskoodi;
    /*
     * Koodisto code (url).
     */
    @NotNull(message = "{validation.Koulutus.koulutus.notNull}")
    @PropertyId( "koulutuskoodiModel")
    private ComboBox cbKoulutusTaiTutkinto;
    /*
     * Koodisto code (url).
     */
    @NotNull(message = "{validation.Koulutus.koulutusohjelma.notNull}")
    @PropertyId("koulutusohjelmaModel")
    private ComboBox cbKoulutusohjelma;
    /*
     * Language, multiple languages are accepted, only single in lukio.
     */
    @NotNull(message = "{validation.Koulutus.opetuskielet.notNull}")
    @PropertyId("opetuskielet")
    private KoodistoComponent kcOpetuskieliMany;
    /*
     * Language, only one selected language is accepted.
     //     */
//    @NotNull(message = "{validation.Koulutus.opetuskielet.notNull}")
//    @PropertyId("opetuskieli")
//    private KoodistoComponent kcOpetuskieliOne;
    /*
     * Start date.
     */
    @NotNull(message = "{validation.Koulutus.koulutuksenAlkamisPvm.notNull}")
    @PropertyId("koulutuksenAlkamisPvm")
    private DateField dfKoulutuksenAlkamisPvm;
    /*
     * Planned duration of the education, or something like that...
     * Used for add text like 5 + 2.
     */
    @Size(min = 1, message = "{validation.Koulutus.suunniteltuKesto.notNull}")
    @NotNull(message = "{validation.Koulutus.suunniteltuKesto.size}")
    @PropertyId("suunniteltuKesto")
    private TextField tfSuunniteltuKesto;
    /*
     * Planned duration of the education, or something like that...
     * A type of time duration .
     */
    @NotNull(message = "{validation.Koulutus.suunniteltuKestoTyyppi.notNull}")
    @PropertyId("suunniteltuKestoTyyppi")
    private KoodistoComponent kcSuunniteltuKestoTyyppi;
    /*
     * Ammatillinen:
     * A list of key words like Lähiopetus, Etäopiskelu, Verkko-opiskelu etc.
     */
    @NotNull(message = "{validation.Koulutus.koulutuslaji.notNull}")
    @PropertyId("koulutuslaji")
    private KoodistoComponent kcKoulutuslaji;
    /*
     * Lukio:
     * A list of key words like Lähiopetus, Etäopiskelu, Verkko-opiskelu etc.
     */
    @NotNull(message = "{validation.Koulutus.opetusmuoto.notNull}")
    @PropertyId("opetusmuoto")
    private KoodistoComponent kcOpetusmuoto;
    @NotNull(message = "{validation.Koulutus.pohjakoulutusvaatimus.notNull}")
    @PropertyId("pohjakoulutusvaatimus")
    private KoodistoComponent kcPohjakoulutusvaatimus;
    private Label koulutusaste;
    private Label opintoala;
    private Label opintojenLaajuusyksikko;
    private Label opintojenLaajuus;
    private Label koulutuksenRakenne;
    private Label tutkintonimike;
    private Label koulutusala;
    private Label tavoitteet;
    private Label jatkoopintomahdollisuudet;
    

    public EditKoulutusPerustiedotFormView() {
    }

    public EditKoulutusPerustiedotFormView(TarjontaPresenter presenter, KoulutusToisenAsteenPerustiedotViewModel model) {
        super(2, 1);
        selectedComponents = new EnumMap<KoulutusasteType, Set<Component>>(KoulutusasteType.class);
        this.presenter = presenter;
        this.koulutusModel = model;
        initializeLayout();
        disableOrEnableComponents(koulutusModel.isLoaded());
        initializeDataContainers();
    }

    private void initializeDataContainers() {
        bicKoulutuskoodi = new BeanItemContainer<KoulutuskoodiModel>(KoulutuskoodiModel.class, koulutusModel.getKoulutuskoodit());
        cbKoulutusTaiTutkinto.setContainerDataSource(bicKoulutuskoodi);

        bicKoulutusohjelma = new BeanItemContainer<KoulutusohjelmaModel>(KoulutusohjelmaModel.class, koulutusModel.getKoulutusohjelmat());
        cbKoulutusohjelma.setContainerDataSource(bicKoulutusohjelma);

        if (!koulutusModel.isLoaded()) {
            //when data is loaded, it do not need listeners.

            cbKoulutusTaiTutkinto.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    LOG.debug("Koulutuskoodi event.");
                    if (cbKoulutusohjelma.getVisibleItemIds() != null && !cbKoulutusohjelma.getVisibleItemIds().isEmpty()) {
                        //clear result data.
                        cbKoulutusohjelma.removeAllItems();
                    }

                    final KoulutuskoodiModel koulutuskoodi = koulutusModel.getKoulutuskoodiModel();
                    if (koulutuskoodi.getOpintoala() != null) {
                        koulutusModel.setOpintoala(koulutuskoodi.getOpintoala());
                        opintoala.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getOpintoala(), MODEL_NAME_PROPERY));
                    }

                    if (koulutuskoodi.getKoulutusaste() != null) {
                        koulutusModel.setKoulutusaste(koulutuskoodi.getKoulutusaste());
                        koulutusaste.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getKoulutusaste(), MODEL_NAME_PROPERY));
                    }

                    if (koulutuskoodi.getKoulutusala() != null) {
                        koulutusModel.setKoulutusala(koulutuskoodi.getKoulutusala());
                        koulutusala.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getKoulutusala(), MODEL_NAME_PROPERY));
                    }

                    if (koulutuskoodi.getOpintojenLaajuusyksikko() != null) {
                        koulutusModel.setOpintojenLaajuusyksikko(koulutuskoodi.getOpintojenLaajuusyksikko());
                        opintojenLaajuusyksikko.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getOpintojenLaajuusyksikko(), MODEL_NAME_PROPERY));
                    }

                    if (koulutuskoodi.getOpintojenLaajuus() != null) {
                        koulutusModel.setOpintojenLaajuus(koulutuskoodi.getOpintojenLaajuus());
                        opintojenLaajuus.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getOpintojenLaajuus(), MODEL_NAME_PROPERY));
                    }

                    if (koulutuskoodi.getKoulutuksenRakenne() != null) {
                        koulutusModel.setKoulutuksenRakenne(koulutuskoodi.getKoulutuksenRakenne());
                        koulutuksenRakenne.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getKoulutuksenRakenne(), MODEL_DESC_PROPERY));
                    }

                    if (koulutuskoodi.getTavoitteet() != null) {
                        koulutusModel.setTavoitteet(koulutuskoodi.getTavoitteet());
                        tavoitteet.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getTavoitteet(), MODEL_DESC_PROPERY));
                    }

                    if (koulutuskoodi.getJatkoopintomahdollisuudet() != null) {
                        koulutusModel.setJakoopintomahdollisuudet(koulutuskoodi.getJatkoopintomahdollisuudet());
                        jatkoopintomahdollisuudet.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getJakoopintomahdollisuudet(), MODEL_DESC_PROPERY));
                    }

                    presenter.loadKoulutusohjelmat();
                    bicKoulutusohjelma.addAll(koulutusModel.getKoulutusohjelmat());
                    disableOrEnableComponents(true);
                }
            });

            cbKoulutusohjelma.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    final KoulutusohjelmaModel koulutusohjelma = koulutusModel.getKoulutusohjelmaModel();
                    if (koulutusModel.getKoulutusohjelmaModel() != null && koulutusModel.getKoulutusohjelmaModel().getTutkintonimike() != null) {
                        koulutusModel.setTutkintonimike(koulutusohjelma.getTutkintonimike());
                        tutkintonimike.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getTutkintonimike(), "nimi"));
                    }
                }
            });
        }

        presenter.loadKoulutuskoodit();
        bicKoulutuskoodi.addAll(koulutusModel.getKoulutuskoodit());
    }

    private void initializeLayout() {
        LOG.info("initilizeLayout()");

        buildGridKoulutusRow(this, "KoulutusTaiTutkinto");
        buildGridKoulutusohjelmaRow(this, "Koulutusohjelma");

        //Build a label section, the data for labels are
        //received from koodisto (KOMO).

        gridLabelRow(this, "koulutuksenTyyppi");
        koulutusaste = buildLabel(this, "koulutusaste");
        opintoala = buildLabel(this, "opintoala");
        koulutusala = buildLabel(this, "koulutusala");
        opintojenLaajuusyksikko = buildLabel(this, "opintojenLaajuusyksikko");
        opintojenLaajuus = buildLabel(this, "opintojenLaajuus");
        koulutuksenRakenne = buildLabel(this, "koulutuksenRakenne");
        tutkintonimike = buildLabel(this, "tutkintonimike");
        tavoitteet = buildLabel(this, "tavoitteet");
        jatkoopintomahdollisuudet = buildLabel(this, "jatkoopintomahdollisuudet");


        buildGridOpetuskieletRow(this, "Opetuskieli"); //select one or many
        // buildGridOpetuskieliRow(this, "Opetuskieli"); //select one
        buildGridDatesRow(this, "KoulutuksenAlkamisPvm");
        buildGridKestoRow(this, "SuunniteltuKesto");
        //Added later

        //only for 'Ammatillinen perustutkintoon johtava koulutus' -section
        buildGridPainotus(this, "painotus");
        buildGridKoulutuslajiRow(this, "Koulutuslaji");
        buildGridOpetusmuotoRow(this, "Opetusmuoto");
        buildGridPohjakoulutusvaatimusRow(this, "Pohjakoulutusvaatimus");

        //only for 'Ammatillinen perustutkintoon johtava koulutus' -section
        // buildGridAvainsanatRow(this, "Avainsanat");

//        //only for 'lukio' -section
//        buildGridKielivalikoimaRow(this, "Kielivalikoima");

        //set components to visible or hide them by selected form type.
        //An example 'lukio', 'ammatillinen koulutus' ...

        //activate all property annotation validations
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);

        //disable or enable reguired validations
        showOnlySelectedFormComponents();
    }

    private Label buildLabel(GridLayout grid, final String propertyKey) {
        if (propertyKey == null) {
            throw new RuntimeException("Application error - label caption cannot be null!");
        }
        gridLabel(grid, propertyKey);
        final Label label = new Label();
        grid.addComponent(label);
        grid.newLine();
        buildSpacingGridRow(grid);

        return label;
    }

    private void gridLabelRow(GridLayout grid, final String propertyKey) {
        if (propertyKey == null) {
            throw new RuntimeException("Application error - label caption cannot be null!");
        }

        gridLabel(grid, propertyKey);
        grid.addComponent(UiUtil.label(null, new BeanItem(koulutusModel), propertyKey));
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutusRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        cbKoulutusTaiTutkinto = new ComboBox();
        cbKoulutusTaiTutkinto.setNullSelectionAllowed(false);
        cbKoulutusTaiTutkinto.setImmediate(true);
        cbKoulutusTaiTutkinto.setWidth(350, UNITS_PIXELS);
        cbKoulutusTaiTutkinto.setReadOnly(koulutusModel.isLoaded());
        cbKoulutusTaiTutkinto.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        cbKoulutusTaiTutkinto.setItemCaptionPropertyId(MODEL_NAME_PROPERY);
        grid.addComponent(cbKoulutusTaiTutkinto);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutusohjelmaRow(GridLayout grid, final String propertyKey) {
        final KoulutusasteType type = KoulutusasteType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
        gridLabel(grid, propertyKey, type);

        cbKoulutusohjelma = new ComboBox();
        cbKoulutusohjelma.setInputPrompt(T(propertyKey + PROPERTY_PROMPT_SUFFIX));
        cbKoulutusohjelma.setEnabled(false);
        cbKoulutusohjelma.setWidth(300, UNITS_PIXELS);
        cbKoulutusohjelma.setNullSelectionAllowed(false);
        cbKoulutusohjelma.setImmediate(true);
        cbKoulutusohjelma.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        cbKoulutusohjelma.setItemCaptionPropertyId(MODEL_NAME_PROPERY);
        cbKoulutusohjelma.setReadOnly(koulutusModel.isLoaded());
        grid.addComponent(cbKoulutusohjelma);
        grid.newLine();

        buildSpacingGridRow(grid);
        addSelectedFormComponents(type, cbKoulutusohjelma);
    }

    private void buildGridOpetuskieletRow(GridLayout grid, final String propertyKey) {
        // final KoulutusasteType type = KoulutusasteType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
        gridLabel(grid, propertyKey);
        kcOpetuskieliMany = UiBuilder.koodistoTwinColSelectUri(null, KoodistoURIHelper.KOODISTO_KIELI_URI, true);
        kcOpetuskieliMany.setImmediate(true);

        //kcOpetuskieliMany.getField().setMultiSelect(false);
        grid.addComponent(kcOpetuskieliMany);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridDatesRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        dfKoulutuksenAlkamisPvm = UiUtil.dateField(null, null, null, null, propertyKey);
        grid.addComponent(dfKoulutuksenAlkamisPvm);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridPainotus(GridLayout grid, final String propertyKey) {
        final KoulutusasteType type = KoulutusasteType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;

        gridLabel(grid, propertyKey, type);
        EditKoulutusPainotusFormView editKoulutusPainotusFormView = new EditKoulutusPainotusFormView(koulutusModel);

        grid.addComponent(new EditKoulutusPainotusFormView(koulutusModel));
        grid.newLine();
        buildSpacingGridRow(grid);

        addSelectedFormComponents(type, editKoulutusPainotusFormView);
    }

    private void buildGridKestoRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        tfSuunniteltuKesto = UiUtil.textField(hl, null, null, null, T(propertyKey + PROPERTY_PROMPT_SUFFIX));
        tfSuunniteltuKesto.setRequired(true);
        tfSuunniteltuKesto.setImmediate(true);

        ComboBox comboBox = new ComboBox();
        comboBox.setNullSelectionAllowed(false);
        kcSuunniteltuKestoTyyppi = UiBuilder.koodistoComboBox(hl, KoodistoURIHelper.KOODISTO_SUUNNITELTU_KESTO_URI, T(propertyKey + "Tyyppi" + PROPERTY_PROMPT_SUFFIX), comboBox, true);
        kcSuunniteltuKestoTyyppi.setImmediate(true);

        kcSuunniteltuKestoTyyppi.setCaptionFormatter(koodiNimiFormatter);
        grid.addComponent(hl);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridOpetusmuotoRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        kcOpetusmuoto = UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_OPETUSMUOTO_URI, true);
        kcOpetusmuoto.setCaptionFormatter(koodiNimiFormatter);
        kcOpetusmuoto.setImmediate(true);
        grid.addComponent(kcOpetusmuoto);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutuslajiRow(GridLayout grid, final String propertyKey) {
        final KoulutusasteType type = KoulutusasteType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
        gridLabel(grid, propertyKey, type);
        kcKoulutuslaji = UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_KOULUTUSLAJI_URI, true);
        kcKoulutuslaji.setCaptionFormatter(koodiNimiFormatter);
        kcKoulutuslaji.setImmediate(true);
        grid.addComponent(kcKoulutuslaji);
        grid.newLine();
        buildSpacingGridRow(grid);
        addSelectedFormComponents(type, kcKoulutuslaji);
    }

    private void buildGridPohjakoulutusvaatimusRow(GridLayout grid, final String propertyKey) {
        final KoulutusasteType type = KoulutusasteType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
        gridLabel(grid, propertyKey, type);
        kcPohjakoulutusvaatimus = UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_KOULUTUSLAJI_URI, true);
        kcPohjakoulutusvaatimus.setCaptionFormatter(koodiNimiFormatter);

        kcPohjakoulutusvaatimus.setImmediate(true);
        grid.addComponent(kcPohjakoulutusvaatimus);
        grid.newLine();
        buildSpacingGridRow(grid);
        addSelectedFormComponents(type, kcPohjakoulutusvaatimus);
    }

    /*
     * PRIVATE HELPER METHODS:
     */
    private void buildSpacingGridRow(GridLayout grid) {
        gridLabel(grid, "");
        CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight(4, UNITS_PIXELS);
        grid.addComponent(cssLayout);
        grid.newLine();
    }

    private void gridLabel(GridLayout grid, final String propertyKey, KoulutusasteType type) {
        addSelectedFormComponents(type, gridLabel(grid, propertyKey));
    }

    private void addSelectedFormComponents(KoulutusasteType type, Component component) {
        if (!selectedComponents.containsKey(type)) {
            selectedComponents.put(type, new HashSet<Component>());
        }

        selectedComponents.get(type).add(component);
    }

    private AbstractLayout gridLabel(GridLayout grid, final String propertyKey) {
        if (propertyKey == null) {
            throw new RuntimeException("Application error - label caption cannot be null!");
        }
        HorizontalLayout hl = UiUtil.horizontalLayout(false, UiMarginEnum.RIGHT);
        hl.setSizeFull();
        Label labelValue = UiUtil.label(hl, T(propertyKey));
        labelValue.setSizeFull();
        grid.addComponent(hl);
        grid.setComponentAlignment(hl, Alignment.TOP_RIGHT);
        grid.setComponentAlignment(labelValue, Alignment.TOP_LEFT);
        return hl;
    }

    private void showOnlySelectedFormComponents() {
        //Show or hide form components.
        final KoodiModel koulutusaste = koulutusModel.getKoulutusaste();
        if (koulutusaste != null) {
            for (Map.Entry<KoulutusasteType, Set<Component>> entry : selectedComponents.entrySet()) {
                for (Component c : entry.getValue()) {
                    //if the map key value matches to TK code 'koulutusaste'
                    final boolean active = entry.getKey().getKoulutusaste().equals(koulutusaste.getKoodi());
                    c.setVisible(active);
                    c.setEnabled(active);

                    //filter layouts
                    if (c instanceof Field) {
                        //disable or enable validation by selected component.
                        ((Field) c).setRequired(active);
                    }
                }
            }
        }
    }

    // Generic translatio helpers
    private String T(String key) {
        return getI18n().getMessage(key);
    }

    private I18NHelper getI18n() {
        if (_i18n == null) {
            _i18n = new I18NHelper(this);
        }
        return _i18n;
    }

    private void disableOrEnableComponents(boolean active) {
        kcOpetuskieliMany.setEnabled(active);
        cbKoulutusohjelma.setEnabled(active);
        dfKoulutuksenAlkamisPvm.setEnabled(active);
        tfSuunniteltuKesto.setEnabled(active);
        kcSuunniteltuKestoTyyppi.setEnabled(active);
        kcKoulutuslaji.setEnabled(active);
        kcOpetusmuoto.setEnabled(active);
    }
    private CaptionFormatter koodiNimiFormatter = new CaptionFormatter<KoodiType>() {
        @Override
        public String formatCaption(KoodiType dto) {
            if (dto == null) {
                return "";
            }

            return TarjontaUIHelper.getKoodiMetadataForLanguage(dto, I18N.getLocale()).getNimi();
        }
    };
}
