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
package fi.vm.sade.tarjonta.ui.view.koulutus.kk;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;
import static com.vaadin.terminal.Sizeable.UNITS_PERCENTAGE;
import static com.vaadin.terminal.Sizeable.UNITS_PIXELS;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Form;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.OphTokenField;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.TutkintoohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.YhteyshenkiloModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaKorkeakouluPresenter;
import fi.vm.sade.tarjonta.ui.view.koulutus.YhteyshenkiloViewForm;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Koulutus edit form for university studies.
 *
 * @author Jani Wilén
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class EditKorkeakouluPerustiedotFormView extends GridLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditKorkeakouluPerustiedotFormView.class);
    private static final String PROPERTY_PROMPT_SUFFIX = ".prompt";
    private static final long serialVersionUID = -8964329145514588760L;
    private transient I18NHelper _i18n;
    private transient UiBuilder uiBuilder;
    private transient TarjontaUIHelper uiHelper;
    private KorkeakouluPerustiedotViewModel model;
    private TarjontaPresenter tarjontaPresenter;
    private TarjontaKorkeakouluPresenter korkeakouluPresenter;
    private YhteyshenkiloViewForm yhteyshenkiloForm;
    /*
     * Koodisto code (url).
     */
    private Label labelKoulutusTaiTutkinto;
    private Button btVaihdaTukintokoodi;

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
     * A list of key words like Lähiopetus, Etäopiskelu, Verkko-opiskelu etc.
     */
    @NotNull(message = "{validation.Koulutus.opetusmuodot.notNull}")
    @PropertyId("opetusmuodos")
    private KoodistoComponent kcOpetusmuodos;
    /*
     * A list of key words like Ylioppilas, Ammatillinen perustutkinto, Ammattitutkinto etc.
     */
    @NotNull(message = "{validation.Koulutus.pohjakoulutusvaatimus.notNull}")
    @PropertyId("pohjakoulutusvaatimukset")
    private KoodistoComponent kcPohjakoulutusvaatimus;
    /*
     * A list of key words like Aluetiede, Antropologia, Argeologia etc.
     */
    @NotNull(message = "{validation.Koulutus.teemat.notNull}")
    @PropertyId("teemas")
    private KoodistoComponent kcTeemas;
    /*
     * Koulutukoodi code value
     */
    @PropertyId("koulutuskoodi")
    private TextField koulutuskoodi;
    @PropertyId("opintojenMaksullisuus")
    private CheckBox opintojenMaksullisuus;
    @PropertyId("tunniste")
    private TextField tfTunniste;
    /*
     * Language, multiple languages are accepted, only single in lukio.
     */
    @NotNull(message = "{validation.Koulutus.opetuskielet.notNull}")
    private OphTokenField opetuskielet;
    /*
     * Value of laajuus, a numeric value like '180'.
     */
    @PropertyId("opintojenLaajuus")
    private KoodistoComponent kcLaajuusarvo;
    /*
     * Labels
     */
    private Label koulutusaste;
    private Label koulutusala;
    private Label opintoala;
    private Label tutkinto;
    private Label tutkintonimike;
    private Label eqf;
    private Label opintojenLaajuusyksikko;
    private Button lisaaKieli;
    private Button haeListalta;
    private VerticalLayout vlContainer;

    public EditKorkeakouluPerustiedotFormView() {
    }

    public EditKorkeakouluPerustiedotFormView(final TarjontaPresenter presenter, final UiBuilder uiBuilder, TarjontaUIHelper uiHelper, final KorkeakouluPerustiedotViewModel model) {
        super(2, 1);
        setSizeFull();
        this.uiHelper = uiHelper;
        this.uiBuilder = uiBuilder;
        this.tarjontaPresenter = presenter;
        this.korkeakouluPresenter = tarjontaPresenter.getKorkeakouluPresenter();
        this.model = model;
        initializeLayout();
        disableOrEnableComponents(model.isLoaded());
        initializeDataContainers();
    }

    private void initializeDataContainers() {
        if (!model.isLoaded()) {
        }
    }

    private void initializeLayout() {

        vlContainer = new VerticalLayout();

        buildGridKoulutusRow(this, "koulutusTaiTutkinto");
        buildGridKoulutusohjelmaRow(this, "tutkintoohjelma");
        buildGridLRowHelp(this, "tutkintoohjelma");
        buildGridTunnisteRow(this, "tutkintoohjelmanTunniste");
        buildGridLRowHelp(this, "tutkintoohjelmanTunniste");
        //Build a label section, the data for labels are
        //received from koodisto service (KOMO).
        koulutusaste = buildLabel(this, "koulutusaste");
        koulutusala = buildLabel(this, "koulutusala");
        opintoala = buildLabel(this, "opintoala");
        tutkinto = buildLabel(this, "tutkinto");
        tutkintonimike = buildLabel(this, "tutkintonimike");
        eqf = buildLabel(this, "eqfLuokitus");
        opintojenLaajuusyksikko = buildLabel(this, "opintojenLaajuusyksikko");

        //OTHER FORM FIELDS AND LABELS
        buildGridLRow(this, "opintojenLaajuusarvo");
        buildGridLRowHelp(this, "opintojenLaajuusarvo");
        buildGridDatesRow(this, "koulutuksenAlkamisPvm");
        buildGridKestoRow(this, "suunniteltuKesto");
        buildGridOpetuskieletRow(this, "opetuskielet");
        buildGridLRowHelp(this, "opetuskielet");
        buildGridOpintojenMaksullisuusRow(this, "opintojenMaksullisuus");
        buildGridLRowHelp(this, "opintojenMaksullisuus");
        buildTitleToTwinColRight(this, "opetusmuodot");
        buildGridOpetusmuotoRow(this, "opetusmuodot");
        buildTitleToTwinColRight(this, "pohjakoulutusvaatimus");
        buildGridPohjakoulutusvaatimuksetRow(this, "pohjakoulutusvaatimus");
        buildTitleToTwinColRight(this, "teematJaAiheet");
        buildGridTeematRow(this, "teematJaAiheet");
        buildGridYhteyshenkiloRows(this);
        buildGridEctsKoordinaattoriRows(this);

        //activate all property annotation validations
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);

        reload();
    }

    private Label buildLabel(GridLayout grid, final String propertyKey) {
        if (propertyKey == null) {
            throw new RuntimeException("Application error - label caption cannot be null!");
        }
        gridLabel(grid, propertyKey);
        final Label label = new Label();
        addGridRowItems(grid, label);
        return label;
    }

    private void buildGridKoulutusRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        HorizontalLayout hl = UiUtil.horizontalLayout();
        hl.setWidth(100, UNITS_PERCENTAGE);

        labelKoulutusTaiTutkinto = UiUtil.label(hl, model.getKoulutuskoodiModel().getNimi() + " " + model.getKoulutuskoodiModel().getKoodi());
        labelKoulutusTaiTutkinto.setSizeFull();
        btVaihdaTukintokoodi = UiUtil.buttonLink(hl, T("button.edit"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                korkeakouluPresenter.showValitseKoulutusDialog();
            }
        });

        hl.setComponentAlignment(labelKoulutusTaiTutkinto, Alignment.TOP_LEFT);
        hl.setComponentAlignment(btVaihdaTukintokoodi, Alignment.TOP_LEFT);

        addGridRowItems(grid, hl);
    }

    private void buildGridKoulutusohjelmaRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        lisaaKieli = UiUtil.buttonLink(null, T("button.lisaaKieli"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                korkeakouluPresenter.showLisaaKieliDialog();
            }
        });

        haeListalta = UiUtil.buttonLink(null, T("button.haeListalta"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                korkeakouluPresenter.showValitseTutkintoohjelmaDialog();
            }
        });

        rebuildLanguageTextFields();
        addGridRowItems(grid, vlContainer);

    }

    private void buildGridDatesRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        dfKoulutuksenAlkamisPvm = UiUtil.dateField(null, null, null, null, propertyKey);
        dfKoulutuksenAlkamisPvm.setImmediate(true);
        dfKoulutuksenAlkamisPvm.setResolution(PopupDateField.RESOLUTION_DAY);
        addGridRowItems(grid, dfKoulutuksenAlkamisPvm);
    }

    private void buildGridLRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        ComboBox comboBox = new ComboBox();
        comboBox.setNullSelectionAllowed(false);
        kcLaajuusarvo = uiBuilder.koodistoComboBox(null, KoodistoURI.KOODISTO_SUUNNITELTU_KESTO_URI, T("opintojenLaajuusarvo" + PROPERTY_PROMPT_SUFFIX), comboBox, true);
        kcLaajuusarvo.setImmediate(true);
        kcLaajuusarvo.setCaptionFormatter(koodiNimiFormatter);
        addGridRowItems(grid, kcLaajuusarvo);
    }

    private void buildGridTunnisteRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        tfTunniste = UiUtil.textField(null, null, null, null, T(propertyKey + PROPERTY_PROMPT_SUFFIX));
        tfTunniste.setRequired(true);
        tfTunniste.setImmediate(true);
        tfTunniste.setValidationVisible(true);
        tfTunniste.setWidth(300, UNITS_PIXELS);
        addGridRowItems(grid, tfTunniste);
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
        kcSuunniteltuKestoTyyppi = uiBuilder.koodistoComboBox(hl, KoodistoURI.KOODISTO_SUUNNITELTU_KESTO_URI, T(propertyKey + "Tyyppi" + PROPERTY_PROMPT_SUFFIX), comboBox, true);
        kcSuunniteltuKestoTyyppi.setImmediate(true);
        kcSuunniteltuKestoTyyppi.setCaptionFormatter(koodiNimiFormatter);

        addGridRowItems(grid, hl);
    }

    private void buildGridOpetusmuotoRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        kcOpetusmuodos = uiBuilder.koodistoTwinColSelectUri(null, KoodistoURI.KOODISTO_OPETUSMUOTO_URI, true);
        kcOpetusmuodos.setCaptionFormatter(koodiNimiFormatter);
        kcOpetusmuodos.setImmediate(true);

        addGridRowItems(grid, kcOpetusmuodos);
    }

    private void buildGridTeematRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        kcTeemas = uiBuilder.koodistoTwinColSelectUri(null, KoodistoURI.KOODISTO_TEEMAT_URI, true);
        kcTeemas.setCaptionFormatter(koodiNimiFormatter);
        kcTeemas.setImmediate(true);

        addGridRowItems(grid, kcTeemas);
    }

    private void buildGridPohjakoulutusvaatimuksetRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        kcPohjakoulutusvaatimus = uiBuilder.koodistoTwinColSelectUri(null, KoodistoURI.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI, true);
        kcPohjakoulutusvaatimus.setCaptionFormatter(koodiNimiFormatter);
        kcPohjakoulutusvaatimus.setImmediate(true);

        addGridRowItems(grid, kcPohjakoulutusvaatimus);
    }

    private void buildGridOpintojenMaksullisuusRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        opintojenMaksullisuus = UiUtil.checkbox(null, T(propertyKey + ".maksullista"));
        opintojenMaksullisuus.setImmediate(true);
        addGridRowItems(grid, opintojenMaksullisuus);
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

    private void buildBigSpacingGridRow(GridLayout grid) {
        gridLabel(grid, null);
        CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight(20, UNITS_PIXELS);
        grid.addComponent(cssLayout);
        grid.newLine();
    }

    private AbstractLayout gridText(GridLayout grid, final String text) {
        HorizontalLayout hl = UiUtil.horizontalLayout(false, UiMarginEnum.RIGHT);
        hl.setSizeFull();

        if (text != null) {
            Label labelValue = UiUtil.label(hl, text);
            hl.setComponentAlignment(labelValue, Alignment.TOP_RIGHT);
            labelValue.setSizeUndefined();
            grid.addComponent(hl);
            grid.setComponentAlignment(hl, Alignment.TOP_RIGHT);

        }
        return hl;
    }

    private AbstractLayout gridLabel(GridLayout grid, final String propertyKey) {
        return gridText(grid, T(propertyKey));
    }

    private void disableOrEnableComponents(boolean active) {
        /*atfTutkintoohjelma.setEnabled(active);
         dfKoulutuksenAlkamisPvm.setEnabled(active);
         tfSuunniteltuKesto.setEnabled(active);
         kcSuunniteltuKestoTyyppi.setEnabled(active);
         kcOpetusmuodos.setEnabled(active);
         */
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
        KoulutuskoodiModel m = model.getKoulutuskoodiModel();

        if (m != null) {
            tutkinto.setPropertyDataSource(new NestedMethodProperty(m, KoulutusKoodistoModel.MODEL_NAME_PROPERY));
        }

//        if (m.getOpintojenLaajuus() != null) {
//            opintojenLaajuus.setPropertyDataSource(new NestedMethodProperty(m, "opintojenLaajuus"));
//        }

        labelDataBind(opintoala, m.getOpintoala());
        labelDataBind(koulutusaste, m.getKoulutusaste());
        labelDataBind(koulutusala, m.getKoulutusala());
        //  labelDataBind(opintojenLaajuusyksikko, m.getOpintojenLaajuusyksikko());
        labelDataBind(tutkintonimike, m.getTutkintonimike());
        disableOrEnableComponents(true);
    }

    /**
     * Builds the contact person part of the form.
     *
     * @param grid
     * @param propertyKey
     */
    private void buildGridYhteyshenkiloRows(GridLayout grid) {
        yhteyshenkiloForm = new YhteyshenkiloViewForm(tarjontaPresenter, model.getYhteyshenkilo());
        yhteyshenkiloForm.setFieldWidth(250);
        yhteyshenkiloForm.getYhtHenkKokoNimi().setInputPrompt(T("prompt.kokoNimi"));

        Form form = new ValidatingViewBoundForm(yhteyshenkiloForm);
        form.setItemDataSource(new BeanItem<YhteyshenkiloModel>(model.getYhteyshenkilo()));
        form.setValidationVisible(false);
        form.setValidationVisibleOnCommit(false);

        gridLabel(grid, "prompt.yhteyshenkilo");
        yhteyshenkiloForm.getSelectionFieldLayout().setWidth(450, UNITS_PIXELS);
        grid.addComponent(yhteyshenkiloForm.getSelectionFieldLayout());
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

    /**
     * Builds the ECTS koordinaattori part of the form.
     *
     * @param grid
     * @param propertyKey
     */
    private void buildGridEctsKoordinaattoriRows(GridLayout grid) {
        yhteyshenkiloForm = new YhteyshenkiloViewForm(tarjontaPresenter, model.getEctsKoordinaattori());
        yhteyshenkiloForm.setFieldWidth(250);
        yhteyshenkiloForm.getYhtHenkKokoNimi().setInputPrompt(T("prompt.kokoNimi"));

        Form form = new ValidatingViewBoundForm(yhteyshenkiloForm);
        form.setItemDataSource(new BeanItem<YhteyshenkiloModel>(model.getEctsKoordinaattori()));
        form.setValidationVisible(false);
        form.setValidationVisibleOnCommit(false);

        gridLabel(grid, "prompt.ectsKoordinaattori");
        grid.addComponent(yhteyshenkiloForm.getSelectionFieldLayout());
        grid.newLine();

        gridLabel(grid, "prompt.email");
        grid.addComponent(yhteyshenkiloForm.getYhtHenkEmail());
        grid.newLine();

        gridLabel(grid, "prompt.puhelin");
        grid.addComponent(yhteyshenkiloForm.getYhtHenkPuhelin());
        grid.newLine();
    }

    private void buildGridOpetuskieletRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        final PropertysetItem psi = new BeanItem(tarjontaPresenter.getModel().getKorkeakouluPerustiedot());
        opetuskielet = uiBuilder.koodistoTokenField(null, KoodistoURI.KOODISTO_KIELI_URI, psi, "opetuskielis");
        opetuskielet.setFormatter(new OphTokenField.SelectedTokenToTextFormatter() {
            @Override
            public String formatToken(Object selectedToken) {
                String nimi = (String) selectedToken;
                return uiHelper.getKoodiNimi(nimi);
            }
        });
        addGridRowItems(grid, opetuskielet);
    }

    private void clearKomoLabels() {
        opintoala.setValue("");
        koulutusaste.setValue("");
        koulutusala.setValue("");
        opintojenLaajuusyksikko.setValue("");
        //opintojenLaajuus.setValue("");
        tutkintonimike.setValue("");
        koulutuskoodi.setValue("");
    }

    @Override
    public void attach() {
        LOG.debug("Attach");


        super.attach();
        yhteyshenkiloForm.initialize();
    }

    private void labelDataBind(Label label, KoulutusKoodistoModel dataField) {
        labelDataBind(label, dataField, KoulutusKoodistoModel.MODEL_NAME_PROPERY);
    }

    private void labelDataBind(Label label, KoulutusKoodistoModel dataField, String fieldProperty) {
        Preconditions.checkNotNull(label, "Label object cannot be null.");
        if (dataField != null) {
            label.setPropertyDataSource(new NestedMethodProperty(dataField, fieldProperty));
        }
    }

    private void addGridRowItems(final GridLayout grid, final Component com) {
        grid.addComponent(com);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private String T(String key) {
        return key != null ? getI18n().getMessage(key) : "";
    }

    private I18NHelper getI18n() {
        if (_i18n == null) {
            _i18n = new I18NHelper(this);
        }
        return _i18n;
    }

    private void buildTitleToTwinColRight(GridLayout grid, final String key) {
        grid.addComponent(new Label(""));//empty grid
        Label label = new Label(T(key + ".title"));
        label.addStyleName("component-twincolsel-title");
        grid.addComponent(label);
        grid.newLine();
    }

    private void buildGridLRowHelp(GridLayout grid, final String propertyKey) {
        grid.addComponent(new Label(""));//empty grid
        addGridRowItems(grid, UiUtil.label((AbstractLayout) null, T(propertyKey + ".help"), LabelStyleEnum.TEXT));
        buildBigSpacingGridRow(grid);
    }

    public void rebuildLanguageTextFields() {
        TutkintoohjelmaModel tutkintoohjelma = model.getTutkintoohjelma();
        if (tutkintoohjelma == null) {
            tutkintoohjelma = new TutkintoohjelmaModel();
            model.setTutkintoohjelma(tutkintoohjelma);
        }

        vlContainer.removeAllComponents();

        final String[] defaultLanguages = new String[]{"kieli_fi", "kieli_sv", "kieli_en"};
        final LinkedList<KielikaannosViewModel> languages = tutkintoohjelma.getLangByOrder(defaultLanguages);
        GridLayout g = new GridLayout(2, 1);

        for (KielikaannosViewModel lang : languages) {
            gridText(g, uiHelper.getKoodiNimi(lang.getKielikoodi()));

            HorizontalLayout hl = new HorizontalLayout();
            hl.setSpacing(true);
            final PropertysetItem psi = new BeanItem(lang);
            TextField tfLang = UiUtil.textField(null, psi, "nimi", null, T("tutkintoohjelma" + PROPERTY_PROMPT_SUFFIX));
            tfLang.setRequired(false);
            tfLang.setImmediate(true);
            tfLang.setValidationVisible(true);
            tfLang.setWidth("262px");
            hl.addComponent(tfLang);
            hl.setComponentAlignment(tfLang, Alignment.TOP_LEFT);
            final String kielikoodi = lang.getKielikoodi();

            if (languages.indexOf(lang) > (defaultLanguages.length - 1)) {
                Button btDeleteRow = UiUtil.buttonLink(hl, T("button.delete"), new Button.ClickListener() {
                    private static final long serialVersionUID = 5019806363620874205L;

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        TutkintoohjelmaModel tutkintoohjelma = model.getTutkintoohjelma();
                        tutkintoohjelma.removeKielikaannos(kielikoodi);
                        rebuildLanguageTextFields();
                    }
                });
                hl.addComponent(btDeleteRow);
            }

            if (lang.equals(languages.getLast())) {
                //add to the first hl row.
                hl.addComponent(lisaaKieli);
            } else if (lang.equals(languages.getFirst())) {
                //add to the first hl row.
                hl.addComponent(haeListalta);
            }
            g.addComponent(hl);
            g.newLine();
        }

        vlContainer.addComponent(g);
    }
}
