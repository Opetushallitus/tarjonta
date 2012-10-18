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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.BeanItemMapper;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.OhjePopupComponent;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.tarjonta.ui.model.KoulutusYhteyshenkiloViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.tarjonta.ui.view.common.DialogDataTable;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 *
 * @author mlyly
 * @author Jani Wilén
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class EditKoulutusPerustiedotToinenAsteView extends AbstractVerticalNavigationLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedotToinenAsteView.class);

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
    @Autowired(required = true)
    private TarjontaPresenter presenter;
    private BeanItemMapper<KoulutusPerustiedotViewModel, EditKoulutusPerustiedotToinenAsteView> bim;
    private FormType selectedForm = FormType.TOINEN_ASTE_LUKIO; //default form
    private Map<FormType, Set<Component>> selectedComponents;
    private ErrorMessage errorView;
    @NotNull(message = "{validation.koulutus}")
    private KoulutusToisenAsteenPerustiedotViewModel koulutusPerustiedotModel;
    @NotNull(message = "{validation.koulutus}")
    @PropertyId("koulutusohjelmaKoodi")
    private ComboBox cbSelectKoulutusohjelma;
    @NotNull(message = "{validation.koulutus}")
    @PropertyId( "koulutusKoodi")
    private KoodistoComponent kcKoulutusKoodi;
    @NotNull(message = "{validation.koulutus}")
    @PropertyId("opetuskielet")
    private KoodistoComponent kcOpetusKieli;
    @NotNull(message = "{validation.koulutus}")
    private DateField dfKoulutuksenAlkamisPvm;
    @Size(min = 1)
    @NotNull(message = "{validation.koulutus}")
    @PropertyId("suunniteltuKesto")
    private TextField tfSuunniteltuKesto;
    @NotNull(message = "{validation.koulutus}")
    private KoodistoComponent kcSuunniteltuKestoTyyppi;
    @NotNull(message = "{validation.koulutus}")
    private KoodistoComponent kcKoulutuslaji;
    @NotNull(message = "{validation.koulutus}")
    private KoodistoComponent kcOpetusmuoto;
    @NotNull(message = "{validation.koulutus}")
    private KoodistoComponent kcAvainsanat;
    @NotNull(message = "{validation.koulutus}")
    private KoodistoComponent kcKielivalikoima;

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
        selectedComponents = new EnumMap<FormType, Set<Component>>(FormType.class);
        koulutusPerustiedotModel = presenter.getModel().getKoulutusPerustiedotModel();

        BeanItem<KoulutusPerustiedotViewModel> hakuBean = new BeanItem<KoulutusPerustiedotViewModel>(koulutusPerustiedotModel);

        bim = new BeanItemMapper<KoulutusPerustiedotViewModel, EditKoulutusPerustiedotToinenAsteView>(koulutusPerustiedotModel,
                getI18n(), this);
        bim.label(layout, "KoulutuksenPerustiedot", LabelStyleEnum.H2);

        UiUtil.hr(layout);

        GridLayout grid = new GridLayout(2, 1);
        grid.setSizeFull();
        grid.setColumnExpandRatio(0, 10l);
        grid.setColumnExpandRatio(1, 20l);

        final Form form = new ValidatingViewBoundForm(grid);
        form.setItemDataSource(hakuBean);
        form.setValidationVisible(true);
        form.setValidationVisibleOnCommit(true);
        form.setSizeFull();

        errorView = new ErrorMessage();
        errorView.setHeight(200, UNITS_PIXELS);
        errorView.setWidth(-1, UNITS_PIXELS);

        layout.addComponent(errorView);
        layout.addComponent(form);

        buildGridDemoSelectRow(grid, "ValitseLomakepohja");

        buildGridKoulutusRow(grid, "KoulutusTaiTutkinto");
        buildGridKoulutusohjelmaRow(grid, "Koulutusohjelma");

        //Build a label section, the data for labes are
        //received from koodisto (KOMO).
        gridLabelRow(grid, "koulutuksenTyyppi");
        gridLabelRow(grid, "koulutusala");
        gridLabelRow(grid, "tutkinto");
        gridLabelRow(grid, "tutkintonimike");
        gridLabelRow(grid, "opintojenLaajuusyksikko");
        gridLabelRow(grid, "opintojenLaajuus");
        gridLabelRow(grid, "opintoala");

        buildGridOpetuskieliRow(grid, "Opetuskieli");
        buildGridDatesRow(grid, "KoulutuksenAlkamisPvm");
        buildGridKestoRow(grid, "SuunniteltuKesto");
        //Added later

        //only for 'Ammatillinen perustutkintoon johtava koulutus' -section
        //TODO: Currently not implemented
        //buildGridPainotus(grid, "AdditionalInformation");

        buildGridOpetusmuotoRow(grid, "Opetusmuoto");
        buildGridKoulutuslajiRow(grid, "Koulutuslaji");

        //only for 'Ammatillinen perustutkintoon johtava koulutus' -section
        buildGridAvainsanatRow(grid, "Avainsanat");

        //only for 'lukio' -section
        buildGridKielivalikoimaRow(grid, "Kielivalikoima");

        UiUtil.hr(layout);
        addYhteyshenkiloSelectorAndEditor(layout);
        UiUtil.hr(layout);
        addLinkkiSelectorAndEditor(layout);

        //set components to visible or hide them by selected form type. 
        //An example 'lukio', 'ammatillinen koulutus' ...
        showOnlySelectedFormComponents();

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
                    form.commit();
                    form.validate();
                    LOG.debug("Form is valid.");
                    //presenter.saveKoulutusValmiina();

                } catch (Validator.InvalidValueException e) {
                    errorView.addError(e);
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

        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);

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

    private void gridLabel(GridLayout grid, final String propertyKey, FormType type) {
        addSelectedFormComponents(type, gridLabel(grid, propertyKey));
    }

    private void addSelectedFormComponents(FormType type, Component component) {
        if (!selectedComponents.containsKey(type)) {
            selectedComponents.put(type, new HashSet<Component>());
        }

        selectedComponents.get(type).add(component);
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
            public void valueChange(ValueChangeEvent event) {
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
        grid.addComponent(bim.addLabel(null, propertyKey));
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutusRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        HorizontalLayout hl = UiUtil.horizontalLayout();
        kcKoulutusKoodi = UiBuilder.koodistoComboBox(hl, KoodistoURIHelper.KOODISTO_KOULUTUS_URI, "koulutusTaiTutkinto.prompt");
        OhjePopupComponent ohjePopupComponent = new OhjePopupComponent(T("LOREMIPSUM"), "500px", "300px");
        hl.addComponent(ohjePopupComponent);
        hl.setExpandRatio(kcKoulutusKoodi, 1l);

        grid.addComponent(hl);
        grid.newLine();

        kcKoulutusKoodi.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                LOG.debug("ValueChangeEvent - Koodisto : {}, value : {}", KoodistoURIHelper.KOODISTO_KOULUTUS_URI, event);
                final String koodiUri = (String) event.getProperty().getValue();
                presenter.searchKoulutusOhjelmakoodit(koodiUri);
                Set<String> koodistoKoulutusohjelma = koulutusPerustiedotModel.getKoodistoKoulutusohjelma();
                for (String m : koodistoKoulutusohjelma) {
                    cbSelectKoulutusohjelma.addItem(m);
                }

                if (!koodistoKoulutusohjelma.isEmpty()) {
                    //items found - enable
                    cbSelectKoulutusohjelma.setEnabled(true);
                } else {
                    //no items - disable
                    cbSelectKoulutusohjelma.setEnabled(false);
                }

                LOG.debug("koulutussohjelma : {}", koulutusPerustiedotModel.getKoodistoKoulutusohjelma());
            }
        });

        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutusohjelmaRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        cbSelectKoulutusohjelma = new ComboBox();
        cbSelectKoulutusohjelma.setNullSelectionAllowed(false);
        cbSelectKoulutusohjelma.setImmediate(true);
        // cbSelectKoulutusohjelma.setTextInputAllowed(true);
        cbSelectKoulutusohjelma.setEnabled(false);
        cbSelectKoulutusohjelma.setWidth(300, UNITS_PIXELS);

        cbSelectKoulutusohjelma.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                LOG.debug("ValueChangeEvent - koulutusohjelma : {}", event.getProperty());
                LOG.debug("koulutussohjelma : {}", koulutusPerustiedotModel.getKoulutusKoodi());
            }
        });
        grid.addComponent(cbSelectKoulutusohjelma);
        grid.newLine();

        buildSpacingGridRow(grid);
    }

    private void buildGridOpetuskieliRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        kcOpetusKieli = UiBuilder.koodistoTwinColSelect(null, KoodistoURIHelper.KOODISTO_KIELI_URI, null, null);
        grid.addComponent(kcOpetusKieli);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridDatesRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        dfKoulutuksenAlkamisPvm = bim.addDate(null, "koulutuksenAlkamisPvm");
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
        tfSuunniteltuKesto.setImmediate(true);
        kcSuunniteltuKestoTyyppi = bim.addKoodistoComboBox(hl, KoodistoURIHelper.KOODISTO_SUUNNITELTU_KESTO_URI, "suunniteltuKestoTyyppi", "SuunniteltuKesto.tyyppi.prompt");
        grid.addComponent(hl);
        grid.newLine();
        buildSpacingGridRow(grid);

    }

    private void buildGridAvainsanatRow(GridLayout grid, final String propertyKey) {
        final FormType type = FormType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
        gridLabel(grid, propertyKey, type);
        kcAvainsanat = bim.addKoodistoTwinColSelect(null, KoodistoURIHelper.KOODISTO_AVAINSANAT_URI, "avainsanat");
        grid.addComponent(kcAvainsanat);

        grid.newLine();
        buildSpacingGridRow(grid);
        addSelectedFormComponents(type, kcAvainsanat);
    }

    private void buildGridKielivalikoimaRow(GridLayout grid, final String propertyKey) {
        final FormType type = FormType.TOINEN_ASTE_LUKIO;
        gridLabel(grid, propertyKey, type);

        kcKielivalikoima = bim.addKoodistoTwinColSelect(null, KoodistoURIHelper.KOODISTO_KIELIVALIKOIMA_URI, "kielivalikoima");
        grid.addComponent(kcKielivalikoima);

        grid.newLine();
        buildSpacingGridRow(grid);
        addSelectedFormComponents(type, kcKielivalikoima);
    }

    private void buildGridOpetusmuotoRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        kcOpetusmuoto = bim.addKoodistoComboBox(null, KoodistoURIHelper.KOODISTO_OPETUSMUOTO_URI, "opetusmuoto", "Opetusmuoto.prompt");
        grid.addComponent(kcOpetusmuoto);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutuslajiRow(GridLayout grid, final String propertyKey) {
        final FormType type = FormType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
        gridLabel(grid, propertyKey, type);
        kcKoulutuslaji = bim.addKoodistoTwinColSelect(null, KoodistoURIHelper.KOODISTO_KOULUTUSLAJI_URI, "koulutuslaji");
        grid.addComponent(kcKoulutuslaji);
        grid.newLine();
        buildSpacingGridRow(grid);
        addSelectedFormComponents(type, kcKoulutuslaji);
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

    private void buildSpacingGridRow(GridLayout grid) {
        CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight(4, UNITS_PIXELS);
        grid.addComponent(cssLayout);
        grid.newLine();
    }

    private void headerLayout(final AbstractLayout layout, final String i18nProperty) {
        CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight(20, UNITS_PIXELS);
        cssLayout.addComponent(bim.label(null, i18nProperty));
        layout.addComponent(cssLayout);
    }

    private void showOnlySelectedFormComponents() {
        //Show or hide form components.
        for (Entry<FormType, Set<Component>> entry : selectedComponents.entrySet()) {

            for (Component c : entry.getValue()) {
                if (entry.getKey().equals(selectedForm)) {
                    c.setVisible(true); //visible
                } else {
                    c.setVisible(false); //hidden
                }
            }
        }
        requestRepaint();
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
}
