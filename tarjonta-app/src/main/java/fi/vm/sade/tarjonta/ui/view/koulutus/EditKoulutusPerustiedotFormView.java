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
import com.vaadin.data.util.NestedMethodProperty;
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
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.enums.KoulutusFormType;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.OhjePopupComponent;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

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
    private KoulutusToisenAsteenPerustiedotViewModel koulutusPerustiedotModel;
    private TarjontaPresenter presenter;
    private Map<KoulutusFormType, Set<Component>> selectedComponents;
    /*
     * Forms can have different set of layout components.
     */
    @PropertyId( "koulutusKoulutusFormType")
    private ComboBox cbSelectForm;
    /*
     * Koodisto code (url).
     */
    @NotNull(message = "{validation.Koulutus.koulutus.notNull}")
    @PropertyId( "koulutusKoodi")
    private KoodistoComponent kcKoulutusKoodi;
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

    public EditKoulutusPerustiedotFormView(TarjontaPresenter presenter, KoulutusToisenAsteenPerustiedotViewModel model) {
        super(2, 1);
        selectedComponents = new EnumMap<KoulutusFormType, Set<Component>>(KoulutusFormType.class);
        this.presenter = presenter;
        this.koulutusPerustiedotModel = model;
        initializeLayout();
    }

    private void initializeLayout() {
        LOG.info("initilizeLayout()");

        this.setSizeFull();
        this.setColumnExpandRatio(0, 10l);
        this.setColumnExpandRatio(1, 20l);

        buildGridDemoSelectRow(this, "ValitseLomakepohja");

        buildGridKoulutusRow(this, "KoulutusTaiTutkinto");
        buildGridKoulutusohjelmaRow(this, "Koulutusohjelma");

        //Build a label section, the data for labels are
        //received from koodisto (KOMO).
        gridLabelRow(this, "koulutuksenTyyppi");
        gridLabelRow(this, "koulutusala");
        gridLabelRow(this, "tutkinto");
        gridLabelRow(this, "tutkintonimike");
        gridLabelRow(this, "opintojenLaajuusyksikko");
        gridLabelRow(this, "opintojenLaajuus");
        gridLabelRow(this, "opintoala");

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
        cbSelectForm = new ComboBox();
        cbSelectForm.setPropertyDataSource(new NestedMethodProperty(koulutusPerustiedotModel, "koulutusFormType"));
        cbSelectForm.setNullSelectionAllowed(false);
        cbSelectForm.setImmediate(true);

        for (KoulutusFormType t : KoulutusFormType.values()) {
            cbSelectForm.addItem(t);
            cbSelectForm.setItemCaption(t, t.getPropertyKey());
        }

        cbSelectForm.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                final Property property = event.getProperty();
                //DEBUGSAWAY:LOG.debug("ValueChangeEvent - selected form type property : {}", property);
                if (property != null && property.getValue() != null) {
                    //DEBUGSAWAY:LOG.debug("Selected form, value : {}", property.getValue());
                    showOnlySelectedFormComponents();
                }
            }
        });

        grid.addComponent(cbSelectForm);
        grid.newLine();
        buildSpacingGridRow(grid);

    }

    private void gridLabelRow(GridLayout grid, final String propertyKey) {
        if (propertyKey == null) {
            throw new RuntimeException("Application error - label caption cannot be null!");
        }

        gridLabel(grid, propertyKey);
        grid.addComponent(UiUtil.label(null, new BeanItem(koulutusPerustiedotModel), propertyKey));
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutusRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        HorizontalLayout hl = UiUtil.horizontalLayout();
        ComboBox comboBox = new ComboBox();
        comboBox.setReadOnly(koulutusPerustiedotModel.isLoaded());
        kcKoulutusKoodi = UiBuilder.koodistoComboBox(hl, KoodistoURIHelper.KOODISTO_KOULUTUS_URI, null, null, T(propertyKey + PROPERTY_PROMPT_SUFFIX), comboBox);
        kcKoulutusKoodi.setFieldValueFormatter(UiBuilder.DEFAULT_URI_AND_VERSIO_FIELD_VALUE_FORMATTER);

        // TODO localizations in Koodisto available?? Using URI to show something.
        kcKoulutusKoodi.setCaptionFormatter(UiBuilder.DEFAULT_URI_CAPTION_FORMATTER);
        kcKoulutusKoodi.setImmediate(true);

        // kcKoulutusKoodi = getComboBox(hl, KoodistoURIHelper.KOODISTO_KOULUTUS_URI, T(propertyKey + PROPERTY_PROMPT_SUFFIX), koulutusPerustiedotModel.getOid() != null);
        OhjePopupComponent ohjePopupComponent = new OhjePopupComponent(T("LOREMIPSUM"), "500px", "300px");
        hl.addComponent(ohjePopupComponent);
        hl.setExpandRatio(kcKoulutusKoodi, 1l);
        grid.addComponent(hl);
        grid.newLine();

        kcKoulutusKoodi.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {

                if (!cbKoulutusohjelma.getVisibleItemIds().isEmpty()) {
                    //clear result data.
                    cbKoulutusohjelma.removeAllItems();
                }
                //DEBUGSAWAY:LOG.debug("ValueChangeEvent - Koodisto : {}, value : {}", KoodistoURIHelper.KOODISTO_KOULUTUS_URI, event);
                final Property property = event.getProperty();

                if (property != null && property.getValue() != null) {
                    final KoodiUriAndVersioType koodi = (KoodiUriAndVersioType) property.getValue();
                    presenter.searchKoulutusOhjelmakoodit(koodi);
                    Set<KoulutusohjelmaModel> koodistoKoulutusohjelma = koulutusPerustiedotModel.getKoodistoKoulutusohjelma();

                    for (KoulutusohjelmaModel m : koodistoKoulutusohjelma) {
                        //add Objects to combo
                        cbKoulutusohjelma.addItem(m);
                        cbKoulutusohjelma.setItemCaption(m, m.getFullName());
                    }

                    if (!koodistoKoulutusohjelma.isEmpty()) {
                        //items found - enable
                        cbKoulutusohjelma.setEnabled(true);
                    } else {
                        //no items - disable
                        cbKoulutusohjelma.setEnabled(false);
                    }
                }
            }
        });

        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutusohjelmaRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        //create new combo box
        cbKoulutusohjelma = new ComboBox();
        cbKoulutusohjelma.setPropertyDataSource(new NestedMethodProperty(koulutusPerustiedotModel, "koulutusohjema"));
        cbKoulutusohjelma.setInputPrompt(T(propertyKey + PROPERTY_PROMPT_SUFFIX));
        cbKoulutusohjelma.setEnabled(false);
        cbKoulutusohjelma.setWidth(300, UNITS_PIXELS);
        cbKoulutusohjelma.setNullSelectionAllowed(false);
        cbKoulutusohjelma.setImmediate(true);
        //cbKoulutusohjelma.setTextInputAllowed(false);

        //if loaded data
        cbKoulutusohjelma.setReadOnly(koulutusPerustiedotModel.isLoaded());

        //listener
        cbKoulutusohjelma.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                //DEBUGSAWAY:LOG.debug("" + event.getProperty());
                //DEBUGSAWAY:LOG.debug("koulutussohjelma obj : {}", koulutusPerustiedotModel.getKoulutusohjema());
            }
        });
        grid.addComponent(cbKoulutusohjelma);
        grid.newLine();

        buildSpacingGridRow(grid);
    }

    private void buildGridOpetuskieliRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        kcOpetuskieli = UiBuilder.koodistoTwinColSelectUri(null, KoodistoURIHelper.KOODISTO_KIELI_URI);
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

        kcOpetusmuoto = UiBuilder.koodistoTwinColSelectUri(null, KoodistoURIHelper.KOODISTO_OPETUSMUOTO_URI);
        kcOpetusmuoto.setImmediate(true);
        grid.addComponent(kcOpetusmuoto);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutuslajiRow(GridLayout grid, final String propertyKey) {
        final KoulutusFormType type = KoulutusFormType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
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
        CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight(4, UNITS_PIXELS);
        grid.addComponent(cssLayout);
        grid.newLine();
    }

    private void gridLabel(GridLayout grid, final String propertyKey, KoulutusFormType type) {
        addSelectedFormComponents(type, gridLabel(grid, propertyKey));
    }

    private void addSelectedFormComponents(KoulutusFormType type, Component component) {
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
        if (koulutusPerustiedotModel.getKoulutusFormType().equals(KoulutusFormType.SHOW_ALL)) {
            //show all form components in single form, only for development/test/demo purpose.
            return;
        }
        //Show or hide form components.
        for (Map.Entry<KoulutusFormType, Set<Component>> entry : selectedComponents.entrySet()) {

            for (Component c : entry.getValue()) {
                final boolean active = entry.getKey().equals(koulutusPerustiedotModel.getKoulutusFormType());
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
}
