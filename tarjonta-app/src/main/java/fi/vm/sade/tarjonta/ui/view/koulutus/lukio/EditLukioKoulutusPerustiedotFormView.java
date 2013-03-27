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
package fi.vm.sade.tarjonta.ui.view.koulutus.lukio;

import com.google.common.base.Preconditions;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusLukioConverter;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.LukiolinjaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.YhteyshenkiloModel;
import fi.vm.sade.tarjonta.ui.view.koulutus.NoKoulutusDialog;
import fi.vm.sade.tarjonta.ui.view.koulutus.YhteyshenkiloViewForm;

/**
 * Koulutus edit form for second degree studies.
 *
 * @author Jani Wilén
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class EditLukioKoulutusPerustiedotFormView extends GridLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditLukioKoulutusPerustiedotFormView.class);
    private static final String MODEL_NAME_PROPERY = "nimi";
    private static final String PROPERTY_PROMPT_SUFFIX = ".prompt";
    private static final long serialVersionUID = -8964329145514588760L;
    private transient I18NHelper _i18n;
    private KoulutusLukioPerustiedotViewModel model;
    private TarjontaPresenter presenter;
    private TarjontaDialogWindow noKoulutusDialog;
    private transient UiBuilder uiBuilder;
    private YhteyshenkiloViewForm yhteyshenkiloForm;
    private BeanItemContainer<KoulutuskoodiModel> bicKoulutuskoodi;
    private BeanItemContainer<LukiolinjaModel> bicLukiolinjas;

    /*
     * Koodisto code (url).
     */
    @NotNull(message = "{validation.Koulutus.koulutus.notNull}")
    @PropertyId("koulutuskoodiModel") //KoulutusRelaatioModel.koulutuskoodiModel
    private ComboBox cbKoulutusTaiTutkinto;
    /*
     * Koodisto code (url).
     */
    @NotNull(message = "{validation.Koulutus.lukiolinja.notNull}")
    @PropertyId("lukiolinja") //KoulutusLukioPerustiedotViewModel.lukiolinja
    private ComboBox cbLukiolinja;
    /*
     * Language, multiple languages are accepted, only single in lukio.
     */
    @NotNull(message = "{validation.Koulutus.opetuskielet.notNull}")
    @PropertyId("opetuskieli")
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
    @NotNull(message = "{validation.Koulutus.suunniteltuKesto.notNull}")
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
     * Lukio:
     * A list of key words like Lähiopetus, Etäopiskelu, Verkko-opiskelu etc.
     */
    @NotNull(message = "{validation.Koulutus.opetusmuoto.notNull}")
    @PropertyId("opetusmuoto")
    private KoodistoComponent kcOpetusmuoto;
    @Pattern(regexp = "[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", message = "{validation.koulutus.opetussuunnitelma.invalid.www}")
    @PropertyId("opsuLinkki")
    private TextField linkki;
    @PropertyId("koulutuskoodi")
    private TextField koulutuskoodi;
    private Label koulutusaste;
    private Label opintoala;
    private Label opintojenLaajuusyksikko;
    private Label opintojenLaajuus;
    private Label koulutuksenRakenne;
    private Label tutkintonimike;
    private Label koulutusala;
    private Label tavoitteet;
    private Label jatkoopintomahdollisuudet;
    private Label pohjakoulutusvaatimus;
    private Label koulutuslaji;

    public EditLukioKoulutusPerustiedotFormView() {
    }

    public EditLukioKoulutusPerustiedotFormView(final TarjontaPresenter presenter, final UiBuilder uiBuilder, final KoulutusLukioPerustiedotViewModel model) {
        super(2, 1);
        setSizeFull();
        this.uiBuilder = uiBuilder;
        this.presenter = presenter;
        this.model = model;
        initializeLayout();
        disableOrEnableComponents(model.isLoaded());
        initializeDataContainers();
    }

    private void initializeDataContainers() {
        bicKoulutuskoodi = new BeanItemContainer<KoulutuskoodiModel>(KoulutuskoodiModel.class, model.getKoulutuskoodis());
        cbKoulutusTaiTutkinto.setContainerDataSource(bicKoulutuskoodi);

        bicLukiolinjas = new BeanItemContainer<LukiolinjaModel>(LukiolinjaModel.class, model.getLukiolinjas());
        cbLukiolinja.setContainerDataSource(bicLukiolinjas);

        if (!model.isLoaded()) {
            //when data is loaded, it do not need listeners.

            cbKoulutusTaiTutkinto.addListener(new Property.ValueChangeListener() {
                private static final long serialVersionUID = -382717228031608542L;

                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    LOG.debug("Koulutuskoodi event.");
                    if (cbLukiolinja.getVisibleItemIds() != null && !cbLukiolinja.getVisibleItemIds().isEmpty()) {
                        //clear result data.
                        cbLukiolinja.removeAllItems();
                        clearKomoLabels();
                    }
                    presenter.getLukioPresenter().loadLukiolinjas();
                    bicLukiolinjas.addAll(model.getLukiolinjas());
                    disableOrEnableComponents(true);
                }
            });

            cbLukiolinja.addListener(new Property.ValueChangeListener() {
                private static final long serialVersionUID = -382717228031608542L;

                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    presenter.getLukioPresenter().loadSelectedKomoData();
                    reload();
                }
            });
        }

        presenter.getLukioPresenter().loadKoulutuskoodis();
        bicKoulutuskoodi.addAll(model.getKoulutuskoodis());

        if (model.isLoaded()) {
            //reload component data from UI model
            presenter.loadSelectedKomoData();
            reload();
        }
    }

    private void initializeLayout() {
        buildGridKoulutusRow(this, "KoulutusTaiTutkinto");
        buildGridKoulutusohjelmaRow(this, "Lukiolinja");

        //Build a label section, the data for labels are
        //received from koodisto service (KOMO).
        koulutusaste = buildLabel(this, "koulutusaste");
        koulutusala = buildLabel(this, "koulutusala");
        opintoala = buildLabel(this, "opintoala");
        tutkintonimike = buildLabel(this, "tutkintonimike");
        opintojenLaajuusyksikko = buildLabel(this, "opintojenLaajuusyksikko");
        opintojenLaajuus = buildLabel(this, "opintojenLaajuus");
        koulutuslaji = buildLabel(this, "koulutuslaji");
        pohjakoulutusvaatimus = buildLabel(this, "pohjakoulutusvaatimus");

        koulutuksenRakenne = buildLabel(this, "koulutuksenRakenne");
        tavoitteet = buildLabel(this, "tavoitteet");
        jatkoopintomahdollisuudet = buildLabel(this, "jatkoopintomahdollisuudet");

        buildGridOpetuskieliRow(this, "Opetuskieli");
        buildGridDatesRow(this, "KoulutuksenAlkamisPvm");
        buildGridKestoRow(this, "SuunniteltuKesto");

        buildGridOpetusmuotoRow(this, "Opetusmuoto");
        buildGridLinkkiRow(this, "Linkki");
        buildGridYhteyshenkiloRows(this);

        //activate all property annotation validations
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    }

    /**
     * Builds the linkki field.
     *
     * @param grid
     * @param propertyKey
     */
    private void buildGridLinkkiRow(GridLayout grid, String propertyKey) {
        gridLabel(grid, propertyKey);
        this.linkki = UiUtil.textField(null, "", T("prompt.Linkki"), true);
        grid.addComponent(this.linkki);
        grid.newLine();
        buildSpacingGridRow(grid);
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

    private void buildGridKoulutusRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        HorizontalLayout hl = new HorizontalLayout();

        cbKoulutusTaiTutkinto = new ComboBox();
        cbKoulutusTaiTutkinto.setNullSelectionAllowed(false);
        cbKoulutusTaiTutkinto.setImmediate(true);
        cbKoulutusTaiTutkinto.setWidth(350, UNITS_PIXELS);
        cbKoulutusTaiTutkinto.setReadOnly(model.isLoaded());
        cbKoulutusTaiTutkinto.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        cbKoulutusTaiTutkinto.setItemCaptionPropertyId(MODEL_NAME_PROPERY);

        this.koulutuskoodi = UiUtil.textField(null, "", "", true);
        this.koulutuskoodi.setEnabled(false);

        hl.addComponent(cbKoulutusTaiTutkinto);
        hl.addComponent(koulutuskoodi);

        grid.addComponent(hl);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutusohjelmaRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        cbLukiolinja = new ComboBox();
        cbLukiolinja.setInputPrompt(T(propertyKey + PROPERTY_PROMPT_SUFFIX));
        cbLukiolinja.setEnabled(false);
        cbLukiolinja.setWidth(300, UNITS_PIXELS);
        cbLukiolinja.setNullSelectionAllowed(false);
        cbLukiolinja.setImmediate(true);
        cbLukiolinja.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        cbLukiolinja.setItemCaptionPropertyId(MODEL_NAME_PROPERY);
        cbLukiolinja.setReadOnly(model.isLoaded());
        grid.addComponent(cbLukiolinja);
        grid.newLine();

        buildSpacingGridRow(grid);
    }

    private void buildGridOpetuskieliRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        kcOpetuskieli = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_KIELI_URI, true);
        kcOpetuskieli.setCaptionFormatter(koodiNimiFormatter);
        kcOpetuskieli.setImmediate(true);
        grid.addComponent(kcOpetuskieli);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridDatesRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        dfKoulutuksenAlkamisPvm = UiUtil.dateField(null, null, null, null, propertyKey);
        dfKoulutuksenAlkamisPvm.setImmediate(true);
        grid.addComponent(dfKoulutuksenAlkamisPvm);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    /**
     * Builds the yhteyshenkilo part of the form.
     *
     * @param grid
     * @param propertyKey
     */
    private void buildGridYhteyshenkiloRows(GridLayout grid) {
        yhteyshenkiloForm = new YhteyshenkiloViewForm(presenter, model.getYhteyshenkilo());
        yhteyshenkiloForm.getYhtHenkKokoNimi().setInputPrompt(T("prompt.kokoNimi"));

        Form form = new ValidatingViewBoundForm(yhteyshenkiloForm);
        form.setItemDataSource(new BeanItem<YhteyshenkiloModel>(model.getYhteyshenkilo()));
        form.setValidationVisible(false);
        form.setValidationVisibleOnCommit(false);

        gridLabel(grid, "prompt.yhteyshenkilo");
        grid.addComponent(yhteyshenkiloForm.getYhtHenkKokoNimi());
        grid.newLine();

        gridLabel(grid, "prompt.titteli");
        grid.addComponent(yhteyshenkiloForm.getYhtHenkTitteli());
        grid.newLine();

        gridLabel(grid, "prompt.email");
        grid.addComponent(yhteyshenkiloForm.getYhtHenkEmail());
        grid.newLine();

        gridLabel(grid, "prompt.puhelin");
        grid.addComponent(yhteyshenkiloForm.getYhtHenkPuhelin());
        grid.newLine();
    }

    private void buildGridKestoRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        tfSuunniteltuKesto = UiUtil.textField(hl, null, null, null, T(propertyKey + PROPERTY_PROMPT_SUFFIX));
        tfSuunniteltuKesto.setRequired(true);
        tfSuunniteltuKesto.setImmediate(true);
        tfSuunniteltuKesto.setValidationVisible(true);

        ComboBox comboBox = new ComboBox();
        comboBox.setNullSelectionAllowed(false);
        kcSuunniteltuKestoTyyppi = uiBuilder.koodistoComboBox(hl, KoodistoURIHelper.KOODISTO_SUUNNITELTU_KESTO_URI, T(propertyKey + "Tyyppi" + PROPERTY_PROMPT_SUFFIX), comboBox, true);
        kcSuunniteltuKestoTyyppi.setImmediate(true);
        kcSuunniteltuKestoTyyppi.setCaptionFormatter(koodiNimiFormatter);
        grid.addComponent(hl);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridOpetusmuotoRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        kcOpetusmuoto = uiBuilder.koodistoTwinColSelectUri(null, KoodistoURIHelper.KOODISTO_OPETUSMUOTO_URI, true);
        kcOpetusmuoto.setCaptionFormatter(koodiNimiFormatter);
        kcOpetusmuoto.setImmediate(true);
        grid.addComponent(kcOpetusmuoto);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    /*
     * PRIVATE HELPER METHODS:
     */
    private void buildSpacingGridRow(GridLayout grid) {
        gridLabel(grid, null);
        CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight(4, UNITS_PIXELS);
        grid.addComponent(cssLayout);
        grid.newLine();
    }

    private AbstractLayout gridLabel(GridLayout grid, final String propertyKey) {
        HorizontalLayout hl = UiUtil.horizontalLayout(false, UiMarginEnum.RIGHT);
        hl.setSizeFull();

        if (propertyKey != null) {
            Label labelValue = UiUtil.label(hl, T(propertyKey));
            hl.setComponentAlignment(labelValue, Alignment.TOP_RIGHT);
            labelValue.setSizeUndefined();
            grid.addComponent(hl);
            grid.setComponentAlignment(hl, Alignment.TOP_RIGHT);

        }
        return hl;
    }

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
        kcOpetuskieli.setEnabled(active);
        cbLukiolinja.setEnabled(active);
        dfKoulutuksenAlkamisPvm.setEnabled(active);
        tfSuunniteltuKesto.setEnabled(active);
        kcSuunniteltuKestoTyyppi.setEnabled(active);
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

    /*
     * Reload data from UI model
     */
    public void reload() {
        KoulutusLukioConverter.copySelectedKoodiDataToModel(model);

        labelDataBind(opintoala, model.getOpintoala());
        labelDataBind(koulutusaste, model.getKoulutusaste());
        labelDataBind(koulutusala, model.getKoulutusala());
        labelDataBind(opintojenLaajuusyksikko, model.getOpintojenLaajuusyksikko());
        labelDataBind(opintojenLaajuus, model.getOpintojenLaajuus());
        labelDataBind(tutkintonimike, model.getTutkintonimike());
        labelDataBind(koulutuksenRakenne, model.getKoulutuksenRakenne());
        labelDataBind(tavoitteet, model.getTavoitteet());
        labelDataBind(jatkoopintomahdollisuudet, model.getJatkoopintomahdollisuudet());
        labelDataBind(koulutuslaji, model.getKoulutuslaji());
        labelDataBind(pohjakoulutusvaatimus, model.getPohjakoulutusvaatimus());

        disableOrEnableComponents(true);
    }

    private void clearKomoLabels() {
        opintoala.setValue("");
        koulutusaste.setValue("");
        koulutusala.setValue("");
        opintojenLaajuusyksikko.setValue("");
        opintojenLaajuus.setValue("");
        koulutuksenRakenne.setValue("");
        tavoitteet.setValue("");
        jatkoopintomahdollisuudet.setValue("");
        tutkintonimike.setValue("");
        pohjakoulutusvaatimus.setValue("");
    }

    private void showNoKoulutusDialog() {
        NoKoulutusDialog noKoulutusView = new NoKoulutusDialog("viesti", new Button.ClickListener() {
            private static final long serialVersionUID = -5998239901946190160L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeNoKoulutusDialog();
            }
        });
        noKoulutusDialog = new TarjontaDialogWindow(noKoulutusView, T("noKoulutusLabel"));
        getWindow().addWindow(noKoulutusDialog);
    }

    private void closeNoKoulutusDialog() {
        if (noKoulutusDialog != null) {
            getWindow().removeWindow(noKoulutusDialog);
        }
    }

    @Override
    public void attach() {
        super.attach();

        yhteyshenkiloForm.initialize();
    }

    private void labelDataBind(Label label, KoulutusKoodistoModel dataField) {
        Preconditions.checkNotNull(label, "Label object cannot be null.");
        if (dataField != null) {
            label.setPropertyDataSource(new NestedMethodProperty(dataField, MODEL_NAME_PROPERY));
        }
    }
}
