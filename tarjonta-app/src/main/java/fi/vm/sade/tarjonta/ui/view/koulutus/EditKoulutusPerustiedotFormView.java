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
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.BeanItemMapper;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.OhjePopupComponent;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
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

/**
 *
 * @author Jani Wilén
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class EditKoulutusPerustiedotFormView extends GridLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedotFormView.class);
    private FormType selectedForm = FormType.TOINEN_ASTE_LUKIO; //default form
    private transient I18NHelper _i18n;
    private KoulutusToisenAsteenPerustiedotViewModel koulutusPerustiedotModel;
    private TarjontaPresenter presenter;
    private Map<FormType, Set<Component>> selectedComponents;
    /*
     * Koodisto code (url).
     */
    @NotNull(message = "{validation.Koulutus.koulutusohjelma.notNull}")
    @PropertyId("koulutusohjelmaKoodi")
    private ComboBox cbKoulutusohjelma;
    /*
     * Koodisto code (url).
     */
    @NotNull(message = "{validation.Koulutus.koulutus.notNull}")
    @PropertyId( "koulutusKoodi")
    private KoodistoComponent kcKoulutusKoodi;
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
    @PropertyId("avainsanat")
    private KoodistoComponent kcAvainsanat;
    /*
     * Language selection, only for lukio.
     */
    @NotNull(message = "{validation.Koulutus.kielivalikoima.notNull}")
    @PropertyId("kielivalikoima")
    private KoodistoComponent kcKielivalikoima;

    public EditKoulutusPerustiedotFormView(TarjontaPresenter presenter, KoulutusToisenAsteenPerustiedotViewModel model, BeanItemMapper<KoulutusPerustiedotViewModel, EditKoulutusPerustiedotToinenAsteView> bim) {
        super(2, 1);
        selectedComponents = new EnumMap<FormType, Set<Component>>(FormType.class);
        this.presenter = presenter;
        this.koulutusPerustiedotModel = model;
        initializeLayout();
    }

    private void initializeLayout() {

        this.setSizeFull();
        this.setColumnExpandRatio(0, 10l);
        this.setColumnExpandRatio(1, 20l);

        buildGridDemoSelectRow(this, "ValitseLomakepohja");

        buildGridKoulutusRow(this, "KoulutusTaiTutkinto");
        buildGridKoulutusohjelmaRow(this, "Koulutusohjelma");

        //Build a label section, the data for labes are
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
        buildGridAvainsanatRow(this, "Avainsanat");

        //only for 'lukio' -section
        buildGridKielivalikoimaRow(this, "Kielivalikoima");

        //set components to visible or hide them by selected form type. 
        //An example 'lukio', 'ammatillinen koulutus' ...
        showOnlySelectedFormComponents();
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    }

    private void buildGridDemoSelectRow(GridLayout grid, final String propertyKey) {
        if (propertyKey == null) {
            throw new RuntimeException("Application error - label caption cannot be null!");
        }

        gridLabel(grid, propertyKey);
        ComboBox selectFormComboBox = new ComboBox();
        FormTypeItem sel = null;

        for (FormType t : FormType.values()) {
            FormTypeItem formTypeItem = new FormTypeItem(t.getPropertyKey(), t);
            if (t.equals(selectedForm)) {
                sel = formTypeItem;
            }
            selectFormComboBox.addItem(formTypeItem);
        }

        selectFormComboBox.setValue(sel);
        selectFormComboBox.setNullSelectionAllowed(false);
        selectFormComboBox.setImmediate(true);

        selectFormComboBox.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                final Property property = event.getProperty();
                LOG.debug("ValueChangeEvent - selected form type property : {}", property);
                if (property != null && property.getValue() != null) {
                    selectedForm = ((FormTypeItem) property.getValue()).getValue();

                    showOnlySelectedFormComponents();

                }
            }
        });

        grid.addComponent(selectFormComboBox);
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
        kcKoulutusKoodi = UiBuilder.koodistoComboBox(hl, KoodistoURIHelper.KOODISTO_KOULUTUS_URI, T("koulutusTaiTutkinto.prompt"));
        OhjePopupComponent ohjePopupComponent = new OhjePopupComponent(T("LOREMIPSUM"), "500px", "300px");
        hl.addComponent(ohjePopupComponent);
        hl.setExpandRatio(kcKoulutusKoodi, 1l);

        grid.addComponent(hl);
        grid.newLine();

        kcKoulutusKoodi.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                LOG.debug("ValueChangeEvent - Koodisto : {}, value : {}", KoodistoURIHelper.KOODISTO_KOULUTUS_URI, event);
                final String koodiUri = (String) event.getProperty().getValue();
                presenter.searchKoulutusOhjelmakoodit(koodiUri);
                Set<String> koodistoKoulutusohjelma = koulutusPerustiedotModel.getKoodistoKoulutusohjelma();
                for (String m : koodistoKoulutusohjelma) {
                    cbKoulutusohjelma.addItem(m);
                }

                if (!koodistoKoulutusohjelma.isEmpty()) {
                    //items found - enable
                    cbKoulutusohjelma.setEnabled(true);
                } else {
                    //no items - disable
                    cbKoulutusohjelma.setEnabled(false);
                }

                LOG.debug("koulutussohjelma : {}", koulutusPerustiedotModel.getKoodistoKoulutusohjelma());
            }
        });

        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutusohjelmaRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        cbKoulutusohjelma = new ComboBox();
        cbKoulutusohjelma.setNullSelectionAllowed(false);
        cbKoulutusohjelma.setImmediate(true);
        // cbSelectKoulutusohjelma.setTextInputAllowed(true);
        cbKoulutusohjelma.setEnabled(false);
        cbKoulutusohjelma.setWidth(300, UNITS_PIXELS);

        cbKoulutusohjelma.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                LOG.debug("ValueChangeEvent - koulutusohjelma : {}", event.getProperty());
                LOG.debug("koulutussohjelma : {}", koulutusPerustiedotModel.getKoulutusKoodi());
            }
        });
        grid.addComponent(cbKoulutusohjelma);
        grid.newLine();

        buildSpacingGridRow(grid);
    }

    private void buildGridOpetuskieliRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        kcOpetuskieli = UiBuilder.koodistoTwinColSelect(null, KoodistoURIHelper.KOODISTO_KIELI_URI, null, null);
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
//        final FormType type = FormType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
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
        tfSuunniteltuKesto = UiUtil.textField(hl, null, null, null, T("SuunniteltuKesto.prompt"));
        tfSuunniteltuKesto.setRequired(true);
        tfSuunniteltuKesto.setImmediate(true);
        kcSuunniteltuKestoTyyppi = UiBuilder.koodistoComboBox(hl, KoodistoURIHelper.KOODISTO_SUUNNITELTU_KESTO_URI, "SuunniteltuKestoTyyppi.prompt");
        grid.addComponent(hl);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridAvainsanatRow(GridLayout grid, final String propertyKey) {
        final FormType type = FormType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
        gridLabel(grid, propertyKey, type);
        kcAvainsanat = UiBuilder.koodistoTwinColSelect(null, KoodistoURIHelper.KOODISTO_AVAINSANAT_URI, null, null);
        grid.addComponent(kcAvainsanat);

        grid.newLine();
        buildSpacingGridRow(grid);
        addSelectedFormComponents(type, kcAvainsanat);
    }

    private void buildGridKielivalikoimaRow(GridLayout grid, final String propertyKey) {
        final FormType type = FormType.TOINEN_ASTE_LUKIO;
        gridLabel(grid, propertyKey, type);

        kcKielivalikoima = UiBuilder.koodistoTwinColSelect(null, KoodistoURIHelper.KOODISTO_KIELIVALIKOIMA_URI, null, null);
        grid.addComponent(kcKielivalikoima);

        grid.newLine();
        buildSpacingGridRow(grid);
        addSelectedFormComponents(type, kcKielivalikoima);
    }

    private void buildGridOpetusmuotoRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        kcOpetusmuoto = UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_OPETUSMUOTO_URI, "Opetusmuoto.prompt");
        grid.addComponent(kcOpetusmuoto);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutuslajiRow(GridLayout grid, final String propertyKey) {
        final FormType type = FormType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
        gridLabel(grid, propertyKey, type);
        kcKoulutuslaji = UiBuilder.koodistoTwinColSelect(null, KoodistoURIHelper.KOODISTO_KOULUTUSLAJI_URI, null, null);
        grid.addComponent(kcKoulutuslaji);
        grid.newLine();
        buildSpacingGridRow(grid);
        addSelectedFormComponents(type, kcKoulutuslaji);
    }

    private void buildSpacingGridRow(GridLayout grid) {
        CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight(4, UNITS_PIXELS);
        grid.addComponent(cssLayout);
        grid.newLine();
    }

    private class FormTypeItem {

        public String caption;
        private FormType value;

        public FormTypeItem(String caption, FormType value) {
            this.caption = caption;
            this.value = value;
        }

        @Override
        public String toString() {
            // Generates the text shown in the combo box
            return caption;
        }

        /**
         * @return the value
         */
        public FormType getValue() {
            return value;
        }
    }

    private class FormKoulutusohjelmaItem {

        public String caption;
        private String value;

        public FormKoulutusohjelmaItem(String caption, String value) {
            this.caption = caption;
            this.value = value;
        }

        @Override
        public String toString() {
            // Generates the text shown in the combo box
            return caption;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }
    }

    private void gridLabel(GridLayout grid, final String propertyKey, FormType type) {
        addSelectedFormComponents(type, gridLabel(grid, propertyKey));
    }

    private void addSelectedFormComponents(FormType type, Component component) {
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
        for (Map.Entry<FormType, Set<Component>> entry : selectedComponents.entrySet()) {

            for (Component c : entry.getValue()) {
                if (entry.getKey().equals(selectedForm)) {
                    c.setVisible(true); //visible
                } else {
                    c.setVisible(false); //hidden
                }
            }
        }
        //requestRepaint();
    }

    protected String T(String key) {
        return getI18n().getMessage(key);
    }

    protected I18NHelper getI18n() {
        if (_i18n == null) {
            _i18n = new I18NHelper(this);
        }
        return _i18n;
    }

    private enum FormType {

        TOINEN_ASTE_LUKIO("Lukio"), TOINEN_ASTE_AMMATILLINEN_KOULUTUS("Ammatillinen");
        private String property;

        FormType(String property) {
            this.property = property;
        }

        public String getPropertyKey() {
            return property;
        }
    }
}
