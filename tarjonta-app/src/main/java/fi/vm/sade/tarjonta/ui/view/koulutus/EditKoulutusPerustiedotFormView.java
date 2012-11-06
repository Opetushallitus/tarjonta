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
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusohjelmaModel;
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
import fi.vm.sade.tarjonta.service.types.koodisto.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.koodisto.KoulutuskoodiTyyppi;

/**
 * Koulutus edit form for second degree studies.
 *
 * @author Jani Wilén
 * @author mlyly
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class EditKoulutusPerustiedotFormView extends GridLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedotFormView.class);
    private static final String PROPERTY_PROMPT_SUFFIX = ".prompt";
    private transient I18NHelper _i18n;
    private KoulutusToisenAsteenPerustiedotViewModel koulutusModel;
    private TarjontaPresenter presenter;
    private Map<KoulutusasteType, Set<Component>> selectedComponents;
    private BeanItemContainer<KoulutusasteTyyppi> bicKoulutusaste;
    private BeanItemContainer<KoulutusohjelmaModel> bicKoulutusohjelma;
    private BeanItemContainer<KoulutuskoodiTyyppi> bicKoulutuskoodi;
    /*
     * Forms can have different set of layout components.
     */
    @PropertyId( "koulutusasteTyyppi")
    private ComboBox cbKoulutusaste;
    /*
     * Koodisto code (url).
     */
    @NotNull(message = "{validation.Koulutus.koulutus.notNull}")
    @PropertyId( "koulutuskoodiTyyppi")
    private ComboBox cbKoulutusTaiTutkinto;
    /*
     * Koodisto code (url).
     */
    @NotNull(message = "{validation.Koulutus.koulutusohjelma.notNull}")
    @PropertyId("koulutusohjelma")
    private ComboBox cbKoulutusohjelma;
    /*
     * Language, multiple languages are accepted, only single in lukio.
     */
    @NotNull(message = "{validation.Koulutus.opetuskielet.notNull}")
    @PropertyId("opetuskielet")
    private KoodistoComponent kcOpetuskieli;
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
    /*
     * Ammatillinen:
     * A list of key words.
     */
//    @PropertyId("avainsanat")
//    private KoodistoComponent kcAvainsanat;
    /*
     * Language selection, only for lukio.
     */
//    @NotNull(message = "{validation.Koulutus.kielivalikoima.notNull}")
//    @PropertyId("kielivalikoima")
//    private KoodistoComponent kcKielivalikoima;

    /*
     * 
     * TODO: fix this
     * KOMO HACK
     * 
     * 
     * 
     */
    @PropertyId("tutkintonimike")
    private KoodistoComponent kcTutkintonimike;
    @PropertyId("opintojenLaajuusyksikko")
    private KoodistoComponent kcOpintojenLaajuusyksikko;
    @PropertyId("opintojenLaajuus")
    private KoodistoComponent kcOpintojenLaajuus;

    public EditKoulutusPerustiedotFormView() {
    }

    public EditKoulutusPerustiedotFormView(TarjontaPresenter presenter, KoulutusToisenAsteenPerustiedotViewModel model) {
        super(2, 1);
        setSpacing(true);
        selectedComponents = new EnumMap<KoulutusasteType, Set<Component>>(KoulutusasteType.class);
        this.presenter = presenter;
        this.koulutusModel = model;
        initializeLayout();
        disableOrEnableComponents(koulutusModel.isLoaded());
        initializeDataContainers();
    }

    private void initializeDataContainers() {
        LOG.info("initializeDataContainers()");

        bicKoulutusaste = new BeanItemContainer<KoulutusasteTyyppi>(KoulutusasteTyyppi.class, koulutusModel.getKoulutusasteet());
        cbKoulutusaste.setContainerDataSource(bicKoulutusaste);

        bicKoulutuskoodi = new BeanItemContainer<KoulutuskoodiTyyppi>(KoulutuskoodiTyyppi.class, koulutusModel.getKoulutuskoodit());
        cbKoulutusTaiTutkinto.setContainerDataSource(bicKoulutuskoodi);

        bicKoulutusohjelma = new BeanItemContainer<KoulutusohjelmaModel>(KoulutusohjelmaModel.class, koulutusModel.getKoulutusohjelmat());
        cbKoulutusohjelma.setContainerDataSource(bicKoulutusohjelma);

        //load data
        presenter.loadKoulutusasteKoodit(getLocale());
        bicKoulutusaste.addAll(koulutusModel.getKoulutusasteet());

        if (!koulutusModel.isLoaded()) {
            //select first item
            koulutusModel.setKoulutusasteTyyppi(!koulutusModel.getKoulutusasteet().isEmpty()
                    ? koulutusModel.getKoulutusasteet().iterator().next()
                    : null);

            //when data is loaded, it do not need listeners.
            cbKoulutusaste.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {

                    LOG.debug("Koulutusaste event.");
                    if (!cbKoulutusTaiTutkinto.getVisibleItemIds().isEmpty()) {
                        //clear result data.
                        cbKoulutusTaiTutkinto.removeAllItems();
                    }
                    presenter.loadKoulutuskoodit(getLocale());
                    bicKoulutuskoodi.addAll(koulutusModel.getKoulutuskoodit());
                    showOnlySelectedFormComponents();
                    disableOrEnableComponents(true);
                }
            });

            cbKoulutusTaiTutkinto.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    LOG.debug("Koulutukoodi event.");
                    if (cbKoulutusohjelma.getVisibleItemIds() != null && !cbKoulutusohjelma.getVisibleItemIds().isEmpty()) {
                        //clear result data.
                        cbKoulutusohjelma.removeAllItems();
                    }
                    presenter.loadKoulutusohjelmat(getLocale());
                    bicKoulutusohjelma.addAll(koulutusModel.getKoulutusohjelmat());
                    cbKoulutusohjelma.setEnabled(true);
                }
            });
        }
    }

    private void initializeLayout() {
        LOG.info("initilizeLayout()");
        this.setColumnExpandRatio(0, 10l);
        this.setColumnExpandRatio(1, 20l);

        buildGridDemoSelectRow(this, "ValitseLomakepohja");
        buildGridKoulutusRow(this, "KoulutusTaiTutkinto");
        buildGridKoulutusohjelmaRow(this, "Koulutusohjelma");

        //Build a label section, the data for labels are
        //received from koodisto (KOMO).
        gridLabelRow(this, "opintoala");
        gridLabelRow(this, "koulutuksenTyyppi");
        gridLabelRow(this, "koulutusala");
        
        buildTutkintonimike(this, "tutkinto");
        buildOpintojenLaajuusyksikko(this, "opintojenLaajuusyksikko");
        buildOpintojenLaajuus(this, "opintojenLaajuus");

        buildGridOpetuskieliRow(this, "Opetuskieli");
        buildGridDatesRow(this, "KoulutuksenAlkamisPvm");
        buildGridKestoRow(this, "SuunniteltuKesto");
        //Added later

        //only for 'Ammatillinen perustutkintoon johtava koulutus' -section
        //TODO: Painotus currently not implemented.
        //buildGridPainotus(grid, "AdditionalInformation");

        buildGridOpetusmuotoRow(this, "Opetusmuoto");
        buildGridKoulutuslajiRow(this, "Koulutuslaji");

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

    private void buildGridDemoSelectRow(GridLayout grid, final String propertyKey) {
        if (propertyKey == null) {
            throw new RuntimeException("Application error - label caption cannot be null!");
        }
        gridLabel(grid, propertyKey);
        cbKoulutusaste = new ComboBox();
        cbKoulutusaste.setNullSelectionAllowed(false);
        cbKoulutusaste.setImmediate(true);
        cbKoulutusaste.setReadOnly(koulutusModel.isLoaded());
        cbKoulutusaste.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        cbKoulutusaste.setItemCaptionPropertyId("koulutusasteNimi");
        grid.addComponent(cbKoulutusaste);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildOpintojenLaajuusyksikko(GridLayout grid, final String propertyKey) {
        if (propertyKey == null) {
            throw new RuntimeException("Application error - label caption cannot be null!");
        }
        gridLabel(grid, propertyKey);
        kcOpintojenLaajuusyksikko = UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_KIELI_URI, true);
        kcOpintojenLaajuusyksikko.setReadOnly(koulutusModel.isLoaded());
        grid.addComponent(kcOpintojenLaajuusyksikko);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildTutkintonimike(GridLayout grid, final String propertyKey) {
        if (propertyKey == null) {
            throw new RuntimeException("Application error - label caption cannot be null!");
        }
        gridLabel(grid, propertyKey);
        kcTutkintonimike = UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_KIELI_URI, true);
        kcTutkintonimike.setReadOnly(koulutusModel.isLoaded());
        grid.addComponent(kcTutkintonimike);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildOpintojenLaajuus(GridLayout grid, final String propertyKey) {
        if (propertyKey == null) {
            throw new RuntimeException("Application error - label caption cannot be null!");
        }
        gridLabel(grid, propertyKey);
        kcOpintojenLaajuus = UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_KIELI_URI, true);
        kcOpintojenLaajuus.setReadOnly(koulutusModel.isLoaded());
        grid.addComponent(kcOpintojenLaajuus);
        grid.newLine();
        buildSpacingGridRow(grid);
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
        cbKoulutusTaiTutkinto.setEnabled(false);
        cbKoulutusTaiTutkinto.setWidth(350, UNITS_PIXELS);
        cbKoulutusTaiTutkinto.setReadOnly(koulutusModel.isLoaded());
        cbKoulutusTaiTutkinto.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        cbKoulutusTaiTutkinto.setItemCaptionPropertyId("koulutuskoodiNimi");
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
        cbKoulutusohjelma.setReadOnly(koulutusModel.isLoaded());
        cbKoulutusohjelma.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        cbKoulutusohjelma.setItemCaptionPropertyId("fullName");
        cbKoulutusohjelma.setReadOnly(koulutusModel.isLoaded());
        grid.addComponent(cbKoulutusohjelma);
        grid.newLine();

        buildSpacingGridRow(grid);
        addSelectedFormComponents(type, cbKoulutusohjelma);
    }

    private void buildGridOpetuskieliRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        kcOpetuskieli = UiBuilder.koodistoTwinColSelectUri(null, KoodistoURIHelper.KOODISTO_KIELI_URI, true);
        kcOpetuskieli.setImmediate(true);
        grid.addComponent(kcOpetuskieli);
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

//    private void buildGridPainotus(GridLayout grid, final String propertyKey) {
//        final KoulutusFormType type = KoulutusFormType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
//
//        gridLabel(grid, propertyKey, type);
//        Button painotus = UiUtil.buttonLink(
//                null,
//                "Painotus",
//                new Button.ClickListener() {
//                    @Override
//                    public void buttonClick(ClickEvent event) {
//                        //TODO: handle painotus button click event.
//                    }
//                });
//        grid.addComponent(painotus);
//        grid.newLine();
//        buildSpacingGridRow(grid);
//
//        addSelectedFormComponents(type, painotus);
//    }
    private void buildGridKestoRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        tfSuunniteltuKesto = UiUtil.textField(hl, null, null, null, T(propertyKey + PROPERTY_PROMPT_SUFFIX));
        tfSuunniteltuKesto.setRequired(true);
        tfSuunniteltuKesto.setImmediate(true);
        ComboBox comboBox = new ComboBox();
        comboBox.setNullSelectionAllowed(false);

        kcSuunniteltuKestoTyyppi = UiBuilder.koodistoComboBox(hl, KoodistoURIHelper.KOODISTO_SUUNNITELTU_KESTO_URI, T(propertyKey + "Tyyppi" + PROPERTY_PROMPT_SUFFIX), comboBox);
        kcSuunniteltuKestoTyyppi.setImmediate(true);

        // TODO check koodisto metadata / localized names for suunnitelti kesto... now using Arvo as caption
        kcSuunniteltuKestoTyyppi.setCaptionFormatter(UiBuilder.DEFAULT_URI_CAPTION_FORMATTER);

        grid.addComponent(hl);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

//    private void buildGridAvainsanatRow(GridLayout grid, final String propertyKey) {
//        final KoulutusFormType type = KoulutusFormType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
//        gridLabel(grid, propertyKey, type);
//        kcAvainsanat = UiBuilder.koodistoTwinColSelectUri(null, KoodistoURIHelper.KOODISTO_AVAINSANAT_URI);
//        kcAvainsanat.setImmediate(true);
//        grid.addComponent(kcAvainsanat);
//
//        grid.newLine();
//        buildSpacingGridRow(grid);
//        addSelectedFormComponents(type, kcAvainsanat);
//    }
//    private void buildGridKielivalikoimaRow(GridLayout grid, final String propertyKey) {
//        final KoulutusFormType type = KoulutusFormType.TOINEN_ASTE_LUKIO;
//        gridLabel(grid, propertyKey, type);
//
//        kcKielivalikoima = UiBuilder.koodistoTwinColSelectUri(null, KoodistoURIHelper.KOODISTO_KIELIVALIKOIMA_URI);
//        kcKielivalikoima.setImmediate(true);
//        grid.addComponent(kcKielivalikoima);
//
//        grid.newLine();
//        buildSpacingGridRow(grid);
//        addSelectedFormComponents(type, kcKielivalikoima);
//    }
    private void buildGridOpetusmuotoRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        //UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_OPETUSMUOTO_URI, T(propertyKey + PROPERTY_PROMPT_SUFFIX));

        kcOpetusmuoto = UiBuilder.koodistoTwinColSelectUri(null, KoodistoURIHelper.KOODISTO_OPETUSMUOTO_URI, true);
        kcOpetusmuoto.setImmediate(true);
        grid.addComponent(kcOpetusmuoto);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutuslajiRow(GridLayout grid, final String propertyKey) {
        final KoulutusasteType type = KoulutusasteType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
        gridLabel(grid, propertyKey, type);
        kcKoulutuslaji = UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_KOULUTUSLAJI_URI);
        kcKoulutuslaji.setCaptionFormatter(UiBuilder.DEFAULT_URI_CAPTION_FORMATTER);

        kcKoulutuslaji.setImmediate(true);
        grid.addComponent(kcKoulutuslaji);
        grid.newLine();
        buildSpacingGridRow(grid);
        addSelectedFormComponents(type, kcKoulutuslaji);
    }

    /*
     * PRIVATE HELPER METHODS:
     */
    private void buildSpacingGridRow(GridLayout grid) {
//        CssLayout cssLayout = new CssLayout();
//        cssLayout.setHeight(4, UNITS_PIXELS);
//        grid.addComponent(cssLayout);
//        grid.newLine();
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
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.RIGHT);
        layout.setSizeFull();
        Label label = new Label(T(propertyKey));
        label.setSizeUndefined();
        layout.addComponent(label);
        layout.setComponentAlignment(label, Alignment.TOP_RIGHT);
        grid.addComponent(layout);

        return layout;
    }

    private void showOnlySelectedFormComponents() {
        //Show or hide form components.
        final KoulutusasteTyyppi koulutusaste = koulutusModel.getKoulutusasteTyyppi();
        if (koulutusaste != null) {
            for (Map.Entry<KoulutusasteType, Set<Component>> entry : selectedComponents.entrySet()) {
                for (Component c : entry.getValue()) {
                    //if the map key value matches to TK code 'koulutusaste'
                    final boolean active = entry.getKey().getKoulutusaste().equals(
                            koulutusaste.getKoulutusasteKoodi());
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
        cbKoulutusTaiTutkinto.setEnabled(active);
        kcOpetuskieli.setEnabled(active);
        dfKoulutuksenAlkamisPvm.setEnabled(active);
        tfSuunniteltuKesto.setEnabled(active);
        kcSuunniteltuKestoTyyppi.setEnabled(active);
        kcKoulutuslaji.setEnabled(active);
        kcOpetusmuoto.setEnabled(active);
    }
}
