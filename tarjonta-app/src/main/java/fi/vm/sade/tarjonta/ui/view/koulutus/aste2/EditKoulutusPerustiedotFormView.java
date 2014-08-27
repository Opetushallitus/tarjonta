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
package fi.vm.sade.tarjonta.ui.view.koulutus.aste2;

import static fi.vm.sade.generic.common.validation.ValidationConstants.EMAIL_PATTERN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

import com.vaadin.data.Property;
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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.authentication.service.types.dto.HenkiloFatType;
import fi.vm.sade.authentication.service.types.dto.YhteystiedotTyyppiType;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.tarjonta.ui.view.koulutus.AutocompleteTextField;
import fi.vm.sade.tarjonta.ui.view.koulutus.AutocompleteTextField.HenkiloAutocompleteEvent;
import fi.vm.sade.tarjonta.ui.view.koulutus.NoKoulutusDialog;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Koulutus edit form for second degree studies.
 *
 * @author Jani Wilén
 * @author mlyly
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class EditKoulutusPerustiedotFormView extends GridLayout {

    /**
     * Kieli-urit, jotka näytetään valintalistalla ensimmäisinä.
     */
    private static final List<String> PRIORITIZED_LANG_URIS = Arrays.asList("kieli_fi", "kieli_sv", "kieli_en");

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedotFormView.class);
    private static final String PROPERTY_PROMPT_SUFFIX = ".prompt";
    private static final long serialVersionUID = -8964329145514588760L;
    private transient I18NHelper _i18n;
    private KoulutusToisenAsteenPerustiedotViewModel koulutusModel;
    private TarjontaPresenter presenter;
    private Map<KoulutusasteType, Set<Component>> selectedComponents;
    private BeanItemContainer<KoulutusohjelmaModel> bicKoulutusohjelma;
    private BeanItemContainer<MonikielinenTekstiModel> bicKoulutuskoodi;
    /*
     * Koodisto code (url).
     */
    @NotNull(message = "{validation.Koulutus.koulutus.notNull}")
    @PropertyId("koulutuskoodiModel")
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
    @Pattern(regexp = "^[0-9]$|^[0-9]+$|^[0-9]+[-/][0-9]+$|^[0-9],[0-9]$|^[0-9]+,[0-9]$|^[0-9]+,[0-9][-/][0-9]+,[0-9]$", message = "{validation.Koulutus.suunniteltuKesto.invalid}")
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
     * This field is visible to valmentava ja kuntouttava opetus only
     */
    @NotNull(message = "{validation.Koulutus.opintojenLaajuus.notNull}")
    @Pattern(regexp = "^[0-9]$|^[0-9]+$|^[0-9]+[-/][0-9]+$|^[0-9],[0-9]$|^[0-9]+,[0-9]$|^[0-9]+,[0-9][-/][0-9]+,[0-9]$", message = "{validation.Koulutus.suunniteltuKesto.invalid}")
    @PropertyId("opintojenLaajuusTot")
    private TextField tfOpintojenLaajuus;

    /* 
     * This field is visible to valmentava ja kuntouttava opetus only
     */
    @NotNull(message = "{validation.Koulutus.opintojenLaajuusyksikko.notNull}")
    @PropertyId("opintojenLaajuusyksikkoTot")
    private KoodistoComponent kcOpintojenLaajuusyksikko;

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
    /*@NotNull(message = "{validation.Koulutus.pohjakoulutusvaatimus.notNull}")
     @PropertyId("pohjakoulutusvaatimus")
     private KoodistoComponent kcPohjakoulutusvaatimus;*/
    @Size(min = 1, max = 255, message = "{validation.koulutus.tooLong.opsuLink}")
    @Pattern(regexp = "[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", message = "{validation.koulutus.opetussuunnitelma.invalid.www}")
    @PropertyId("opsuLinkki")
    private TextField linkki;

    @Size(min = 1, max = 255, message = "{validation.koulutus.yhteyshenkilo.tooLong.nimi}")
    @PropertyId("yhtHenkKokoNimi")
    private TextField yhtHenkKokoNimi;

    @Size(min = 1, max = 255, message = "{validation.koulutus.yhteyshenkilo.tooLong.titteli}")
    @PropertyId("yhtHenkTitteli")
    private TextField yhtHenkTitteli;

    @Size(min = 1, max = 255, message = "{validation.koulutus.yhteyshenkilo.tooLong.email}")
    @Pattern(regexp = EMAIL_PATTERN, message = "{validation.koulutus.yhteyshenkilo.invalid.email}")
    @PropertyId("yhtHenkEmail")
    private TextField yhtHenkEmail;

    @Pattern(regexp = "(\\+|\\-| |\\(|\\)|[0-9]){3,100}", message = "{validation.koulutus.yhteyshenkilo.invalid.phone}")
    @PropertyId("yhtHenkPuhelin")
    private TextField yhtHenkPuhelin;

    //valmentavan ja kuntouttavan koulutuksen nimi (OVT-6278)
    @PropertyId("nimi")
    private TextField koulutuksenNimi;

    private String initialYhtHenkTitteli;
    private String initialYhtHenkEmail;
    private String initialYhtHenkPuhelin;
    private Label koulutusaste;
    private Label opintoala;
    private Label opintojenLaajuus;
    private Label koulutuksenRakenne;
    private Label tutkintonimike;
    private Label koulutusala;
    private Label tavoitteet;
    private Label jatkoopintomahdollisuudet;
    private Label koulutusohjelmanTavoitteet;
    private TarjontaDialogWindow noKoulutusDialog;
    private transient UiBuilder uiBuilder;
    private boolean isPervako = false;
    private boolean isToinenasteValmentava = false;

    public EditKoulutusPerustiedotFormView(final TarjontaPresenter presenter, final UiBuilder uiBuilder, final KoulutusToisenAsteenPerustiedotViewModel model) {
        super(2, 1);
        setSizeFull();
        this.uiBuilder = uiBuilder;
        selectedComponents = new EnumMap<KoulutusasteType, Set<Component>>(KoulutusasteType.class);
        this.presenter = presenter;
        this.koulutusModel = model;

        if (model.getOid() == null || model.getOid().equals("-1")) {
            // only in ValmentavaJaKuntouttavaOpetus
            model.setOpintojenLaajuusTot("");
            model.setOpintojenLaajuusyksikkoTot("");
        }

        isPervako = KoulutusUtil.isPervako(koulutusModel.getKoulutuksenTyyppi().getKoodi());
        isToinenasteValmentava = KoulutusUtil.isValmentavaJaKuntouttava(koulutusModel.getKoulutuksenTyyppi().getKoodi());
        initializeLayout();
        disableOrEnableComponents(koulutusModel.isLoaded());
        initializeDataContainers();
    }

    /**
     * Attach is used here to pick the initial values of yhteyshenkilo fields.
     * These are restored to the fields if the user tentatively selects some
     * users from the autocompletion list but then decides not to use any of the
     * existing users.
     */
    @Override
    public void attach() {
        super.attach();
        initialYhtHenkTitteli = koulutusModel.getYhtHenkTitteli();
        initialYhtHenkEmail = koulutusModel.getYhtHenkEmail();
        initialYhtHenkPuhelin = koulutusModel.getYhtHenkPuhelin();
        if (koulutusModel.getKoulutuskoodit() == null
                || koulutusModel.getKoulutuskoodit().isEmpty()) {
            showNoKoulutusDialog();
        }
    }

    private void initializeDataContainers() {
        bicKoulutuskoodi = new BeanItemContainer<MonikielinenTekstiModel>(MonikielinenTekstiModel.class, new ArrayList<MonikielinenTekstiModel>());

        cbKoulutusTaiTutkinto.setContainerDataSource(bicKoulutuskoodi);

        bicKoulutusohjelma = new BeanItemContainer<KoulutusohjelmaModel>(KoulutusohjelmaModel.class, koulutusModel.getKoulutusohjelmat());
        cbKoulutusohjelma.setContainerDataSource(bicKoulutusohjelma);

        if (!koulutusModel.isLoaded()) {
            //when data is loaded, it do not need listeners.

            cbKoulutusTaiTutkinto.addListener(new Property.ValueChangeListener() {
                private static final long serialVersionUID = -382717228031608542L;

                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    LOG.debug("Koulutuskoodi event.");
                    if (cbKoulutusohjelma.getVisibleItemIds() != null && !cbKoulutusohjelma.getVisibleItemIds().isEmpty()) {
                        //clear result data.
                        cbKoulutusohjelma.removeAllItems();
                        clearKomoLabels();
                    }
                    presenter.loadKoulutusohjelmat();
                    Collections.sort(koulutusModel.getKoulutusohjelmat());
                    bicKoulutusohjelma.addAll(koulutusModel.getKoulutusohjelmat());
                    if (koulutusModel.getKoulutusohjelmat().size() == 1 && isPervako) {
                        //automatically select if only one selection!
                        koulutusModel.setKoulutusohjelmaModel(koulutusModel.getKoulutusohjelmat().get(0));
                    }
                    disableOrEnableComponents(true);
                }
            });

            cbKoulutusohjelma.addListener(new Property.ValueChangeListener() {
                private static final long serialVersionUID = -382717228031608542L;

                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    presenter.loadSelectedKomoData();
                    reload();
                }
            });

        }

        presenter.loadKoulutuskoodit();
        //sort
        Collections.sort(koulutusModel.getKoulutuskoodit());
        bicKoulutuskoodi.addAll(koulutusModel.getKoulutuskoodit());

        if (koulutusModel.getKoulutuskoodit().size() == 1 && isPervako) {
            KoulutuskoodiModel km = koulutusModel.getKoulutuskoodit().get(0);
            //ainoa valinta, valitse suoraan
            koulutusModel.setKoulutuskoodiModel(km);
        }

        if (koulutusModel.isLoaded()) {
            //reload component data from UI model
            presenter.loadSelectedKomoData();
            reload();
        }
    }

    private void initializeLayout() {
        buildGridKoulutusRow(this, "KoulutusTaiTutkinto");
        buildGridKoulutusohjelmaRow(this);
        buildEmptyGridRow(this);

        //Build a label section, the data for labels are
        //received from koodisto (KOMO).
        koulutusaste = buildLabel(this, "koulutusaste");
        koulutusala = buildLabel(this, "koulutusala");
        opintoala = buildLabel(this, "opintoala");
        tutkintonimike = buildLabel(this, "tutkintonimike");
        if (!isPervako) {
            opintojenLaajuus = buildLabel(this, "opintojenLaajuus");
        } else {
            opintojenLaajuus = new Label();
            opintojenLaajuus.setVisible(false);
        }
        buildEmptyGridRow(this);
        tavoitteet = buildLabel(this, "tavoitteet");
        if (!isPervako) {
            koulutusohjelmanTavoitteet = buildLabel(this, "koTavoitteet");
        } else {
            koulutusohjelmanTavoitteet = new Label();
            koulutusohjelmanTavoitteet.setVisible(false);
        }
        buildEmptyGridRow(this);
        koulutuksenRakenne = buildLabel(this, "koulutuksenRakenne");
        jatkoopintomahdollisuudet = buildLabel(this, "jatkoopintomahdollisuudet");

        if (isPervako) {
            buildGridOpintojenLaajuusRow(this, "opintojenLaajuus");
        }
        buildGridDatesRow(this, "KoulutuksenAlkamisPvm");
        buildGridKestoRow(this, "SuunniteltuKesto");
        buildGridOpetuskieliRow(this, "Opetuskieli");

        //only for 'Ammatillinen perustutkintoon johtava koulutus' -section
        //Removed because not needed... -> OVT-4560
        //buildGridPainotus(this, "painotus");
        buildGridKoulutuslajiRow(this, "Koulutuslaji");
        buildGridOpetusmuotoRow(this, "Opetusmuoto");
        //buildGridPohjakoulutusvaatimusRow(this, "Pohjakoulutusvaatimus");
        buildGridLinkkiRow(this, "Linkki");
        buildGridYhteyshenkiloRows(this, "Yhteyshenkilo");

        //activate all property annotation validations
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);

    }

    private void buildGridOpintojenLaajuusRow(GridLayout grid, String propertyKey) {
        gridLabelMidAlign(grid, propertyKey);
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        tfOpintojenLaajuus = UiUtil.textField(hl, null, null, null, T(propertyKey + PROPERTY_PROMPT_SUFFIX));
        tfOpintojenLaajuus.setRequired(true);
        tfOpintojenLaajuus.setImmediate(true);
        tfOpintojenLaajuus.setValidationVisible(true);

        ComboBox comboBox = new ComboBox();
        comboBox.setNullSelectionAllowed(false);
        kcOpintojenLaajuusyksikko = uiBuilder.koodistoComboBox(hl, KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI, T(propertyKey + "Tyyppi" + PROPERTY_PROMPT_SUFFIX), comboBox, true);
        //kcOpintojenLaajuusyksikko.setImmediate(true);
        kcOpintojenLaajuusyksikko.setCaptionFormatter(koodiNimiFormatter);
        grid.addComponent(hl);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    /**
     * Builds the yhteyshenkilo part of the form.
     *
     * @param grid
     * @param propertyKey
     */
    private void buildGridYhteyshenkiloRows(GridLayout grid, String propertyKey) {
        gridLabelMidAlign(grid, propertyKey);
        VerticalLayout vl = UiUtil.verticalLayout();
        yhtHenkKokoNimi = new AutocompleteTextField(vl, T("prompt.kokoNimi"), "", presenter, this.koulutusModel);
        yhtHenkKokoNimi.addListener(new Listener() {
            private static final long serialVersionUID = 6680073663370984689L;

            @Override
            public void componentEvent(Event event) {
                if (event instanceof HenkiloAutocompleteEvent
                        && ((HenkiloAutocompleteEvent) event).getEventType() == HenkiloAutocompleteEvent.SELECTED) {
                    if (((HenkiloAutocompleteEvent) event).getHenkilo() != null && presenter != null) {
                    HenkiloFatType fatHenkilo = presenter.getFatHenkiloWithOid(((HenkiloAutocompleteEvent) event).getHenkilo().getOidHenkilo());
                    if (fatHenkilo != null) {
                        populateYhtHenkiloFields(fatHenkilo);
                    }
                    }
                } else if (event instanceof HenkiloAutocompleteEvent
                        && ((HenkiloAutocompleteEvent) event).getEventType() == HenkiloAutocompleteEvent.NOT_SELECTED) {
                    restoreInitialValuesToYhtHenkiloFields();
                } else if (event instanceof HenkiloAutocompleteEvent
                        && ((HenkiloAutocompleteEvent) event).getEventType() == HenkiloAutocompleteEvent.CLEAR) {
                    clearInitialValuestoYhtHenkiloFields();
                }
            }
        });
        grid.addComponent(vl);
        grid.newLine();

        gridLabel(grid, "prompt.titteli");
        yhtHenkTitteli = UiUtil.textField(null, "", "", true);
        grid.addComponent(yhtHenkTitteli);
        grid.newLine();

        gridLabel(grid, "prompt.email");
        yhtHenkEmail = UiUtil.textField(null, "", "", true);
        grid.addComponent(yhtHenkEmail);
        grid.newLine();

        gridLabel(grid, "prompt.puhelin");
        yhtHenkPuhelin = UiUtil.textField(null, "", "", true);
        grid.addComponent(yhtHenkPuhelin);
        grid.newLine();
    }

    /**
     * Populating the yhteyshenkilo fields based on user's selection from the
     * autocomplete list
     *
     * @param henkiloType
     */
    private void populateYhtHenkiloFields(HenkiloFatType henkiloType) {
        if (henkiloType == null) {
            return;
        }
        this.yhtHenkKokoNimi.setValue(henkiloType.getEtunimet() + " " + henkiloType.getSukunimi());
        this.koulutusModel.setYhtHenkiloOid(henkiloType.getOidHenkilo());
        if (henkiloType.getOrganisaatioHenkilos() != null && !henkiloType.getOrganisaatioHenkilos().isEmpty()) {
            this.yhtHenkEmail.setValue(TarjontaUIHelper.getYhteystietoFromHenkiloType(henkiloType, YhteystiedotTyyppiType.YHTEYSTIETO_SAHKOPOSTI));
            String puhelinNro = TarjontaUIHelper.getYhteystietoFromHenkiloType(henkiloType,YhteystiedotTyyppiType.YHTEYSTIETO_PUHELINNUMERO) != null ?
                    TarjontaUIHelper.getYhteystietoFromHenkiloType(henkiloType, YhteystiedotTyyppiType.YHTEYSTIETO_PUHELINNUMERO) :
                    TarjontaUIHelper.getYhteystietoFromHenkiloType(henkiloType, YhteystiedotTyyppiType.YHTEYSTIETO_MATKAPUHELINNUMERO);
            this.yhtHenkPuhelin.setValue(puhelinNro);
            this.yhtHenkTitteli.setValue(henkiloType.getOrganisaatioHenkilos().get(0).getTehtavanimike());
        } else {
            this.yhtHenkEmail.setValue(null);
            this.yhtHenkPuhelin.setValue(null);
            this.yhtHenkTitteli.setValue(null);
        }
    }

    /*
     * Restoring the initial values to yhteyshenkilo fields. this functionality is to enable the user
     * to try different yhteyshenkilos from search but then return to the old one. This is
     * important in the cases that the current yhteyshenkilo is not in the user register but is
     * created by the editor of this koulutus (to not loose data).
     */
    private void restoreInitialValuesToYhtHenkiloFields() {
        this.yhtHenkEmail.setValue(initialYhtHenkEmail);
        this.yhtHenkPuhelin.setValue(initialYhtHenkPuhelin);
        this.yhtHenkTitteli.setValue(initialYhtHenkTitteli);
        this.koulutusModel.setYhtHenkiloOid(null);
    }

    /*
     * Nullifying the initial values to yhteyshenkilo fields. When the user decides to
     * remove the existing yhteyshenkilo and possibly add a new one.
     */
    private void clearInitialValuestoYhtHenkiloFields() {
        initialYhtHenkEmail = null;
        initialYhtHenkPuhelin = null;
        initialYhtHenkTitteli = null;
        restoreInitialValuesToYhtHenkiloFields();
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
        label.setContentMode(Label.CONTENT_XHTML);
        grid.addComponent(label);
        grid.newLine();
        buildSpacingGridRow(grid);

        return label;
    }

    private void buildGridKoulutusRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        cbKoulutusTaiTutkinto = new ComboBox();
        cbKoulutusTaiTutkinto.setNullSelectionAllowed(false);
        cbKoulutusTaiTutkinto.setImmediate(true);
        cbKoulutusTaiTutkinto.setWidth(350, UNITS_PIXELS);
        cbKoulutusTaiTutkinto.setReadOnly(koulutusModel.isLoaded());
        cbKoulutusTaiTutkinto.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        cbKoulutusTaiTutkinto.setItemCaptionPropertyId(KoulutusKoodistoModel.MODEL_NAME_PROPERY);
        grid.addComponent(cbKoulutusTaiTutkinto);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutusohjelmaRow(GridLayout grid) {
        final String propKey = "Koulutusohjelma";
        final KoulutusasteType type = KoulutusasteType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
        gridLabel(grid, propKey, type);

        cbKoulutusohjelma = new ComboBox();
        cbKoulutusohjelma.setInputPrompt(T(propKey + PROPERTY_PROMPT_SUFFIX));
        cbKoulutusohjelma.setEnabled(false);
        cbKoulutusohjelma.setWidth(300, UNITS_PIXELS);
        cbKoulutusohjelma.setNullSelectionAllowed(false);
        cbKoulutusohjelma.setImmediate(true);
        cbKoulutusohjelma
                .setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        cbKoulutusohjelma
                .setItemCaptionPropertyId(KoulutusKoodistoModel.MODEL_NAME_PROPERY);
        cbKoulutusohjelma.setReadOnly(koulutusModel.isLoaded());

        koulutuksenNimi = UiUtil.textField(null, null, null, null, null);

        if (isToinenasteValmentava) {
            grid.addComponent(koulutuksenNimi);
            koulutuksenNimi.setWidth(300, UNITS_PIXELS);
            koulutuksenNimi.setRequired(true);
            koulutuksenNimi.setRequiredError(I18N.getMessage("EditKoulutusPerustiedotFormView.koulutusohjelman.nimi.tyhja"));
        } else {
            grid.addComponent(cbKoulutusohjelma);
        }
        grid.newLine();
        buildSpacingGridRow(grid);

        Label oteksti = new Label(T("koulutusOhjelma.ohje"));
        oteksti.addStyleName(Oph.LABEL_SMALL);

        grid.addComponent(new Label());
        grid.addComponent(oteksti);

        buildSpacingGridRow(grid);
        addSelectedFormComponents(type, cbKoulutusohjelma);

    }

    private void buildGridOpetuskieliRow(GridLayout grid, final String propertyKey) {
        // final KoulutusasteType type = KoulutusasteType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
        gridLabel(grid, propertyKey);
        kcOpetuskieli = uiBuilder.koodistoComboBox(null, KoodistoURI.KOODISTO_KIELI_URI, true);
        kcOpetuskieli.setCaptionFormatter(koodiNimiFormatter);
        kcOpetuskieli.setImmediate(true);

        kcOpetuskieli.setComparator(new Comparator<KoodiType>() {
            @Override
            public int compare(KoodiType o1, KoodiType o2) {
                final int p1 = PRIORITIZED_LANG_URIS.indexOf(o1.getKoodiUri());
                final int p2 = PRIORITIZED_LANG_URIS.indexOf(o2.getKoodiUri());
                if (p1 == -1 && p2 == -1) {
                    return o1.getKoodiArvo().compareTo(o2.getKoodiArvo());
                } else if (p1 == -1) {
                    return 1;
                } else if (p2 == -1) {
                    return -1;
                } else {
                    return new Integer(p1).compareTo(p2);
                }
            }
        });

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
        grid.addComponent(hl);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridOpetusmuotoRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        kcOpetusmuoto = uiBuilder.koodistoComboBox(null, KoodistoURI.KOODISTO_OPETUSMUOTO_URI, true);
        kcOpetusmuoto.setCaptionFormatter(koodiNimiFormatter);
        kcOpetusmuoto.setImmediate(true);
        grid.addComponent(kcOpetusmuoto);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutuslajiRow(GridLayout grid, final String propertyKey) {
        final KoulutusasteType type = KoulutusasteType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
        gridLabel(grid, propertyKey, type);
        kcKoulutuslaji = uiBuilder.koodistoComboBox(null, KoodistoURI.KOODISTO_KOULUTUSLAJI_URI, true);
        kcKoulutuslaji.setCaptionFormatter(koodiNimiFormatter);
        kcKoulutuslaji.setImmediate(true);
        grid.addComponent(kcKoulutuslaji);
        grid.newLine();
        buildSpacingGridRow(grid);
        addSelectedFormComponents(type, kcKoulutuslaji);

        // OVT-4607 väliaikainen pakotettu valinta
        kcKoulutuslaji.setEnabled(isPervako);

    }

    /*
     private void buildGridPohjakoulutusvaatimusRow(GridLayout grid, final String propertyKey) {
     final KoulutusasteType type = KoulutusasteType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS;
     gridLabel(grid, propertyKey, type);
     kcPohjakoulutusvaatimus = uiBuilder.koodistoComboBox(null, KoodistoURI.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI, true);
     kcPohjakoulutusvaatimus.setCaptionFormatter(koodiNimiFormatter);
     kcPohjakoulutusvaatimus.setImmediate(true);
     kcPohjakoulutusvaatimus.setEnabled(false);
     grid.addComponent(kcPohjakoulutusvaatimus);
     grid.newLine();
     buildSpacingGridRow(grid);
     addSelectedFormComponents(type, kcPohjakoulutusvaatimus);
     }*/

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

    private void buildEmptyGridRow(GridLayout grid) {
        Label lbl = new Label();
        lbl.addStyleName(Oph.SPACING_BOTTOM_30);
        grid.addComponent(lbl);
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

    private AbstractLayout gridLabelMidAlign(GridLayout grid, final String propertyKey) {
        HorizontalLayout hl = UiUtil.horizontalLayout(false, UiMarginEnum.RIGHT);
        hl.setSizeFull();

        if (propertyKey != null) {
            Label labelValue = UiUtil.label(hl, T(propertyKey));
            hl.setComponentAlignment(labelValue, Alignment.MIDDLE_RIGHT);
            labelValue.setSizeUndefined();
            grid.addComponent(hl);
            grid.setComponentAlignment(hl, Alignment.MIDDLE_RIGHT);

        }
        return hl;
    }

//    private void showOnlySelectedFormComponents() {
//        //Show or hide form components.
//        final KoodiModel koulutusasteModel = koulutusModel.getKoulutusaste();
//
//        if (koulutusasteModel != null) {
//            for (Map.Entry<KoulutusasteType, Set<Component>> entry : selectedComponents.entrySet()) {
//                for (Component c : entry.getValue()) {
//                    //if the map key value matches to TK code 'koulutusaste'
//                    final boolean active = entry.getKey().getKoulutusaste().equals(koulutusasteModel.getKoodi());
//
//                    c.setVisible(active);
//                    c.setEnabled(active);
//
//                    //filter layouts
//                    if (c instanceof Field) {
//                        //disable or enable validation by selected component.
//                        ((Field) c).setRequired(active);
//                    }
//                }
//            }
//        }
//    }
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
        kcOpetuskieli.setEnabled(active);
        cbKoulutusohjelma.setEnabled(active);
        dfKoulutuksenAlkamisPvm.setEnabled(active);
        tfSuunniteltuKesto.setEnabled(active);
        kcSuunniteltuKestoTyyppi.setEnabled(active);
        //kcKoulutuslaji.setEnabled(active); // OVT-4607 väliaikainen käytöstäpoisto
        kcOpetusmuoto.setEnabled(active);
        //kcPohjakoulutusvaatimus.setEnabled(active);
    }
    private CaptionFormatter<KoodiType> koodiNimiFormatter = new CaptionFormatter<KoodiType>() {
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

        final KoulutuskoodiModel koulutuskoodi = koulutusModel.getKoulutuskoodiModel();

        if (koulutuskoodi.getOpintoala() != null) {
            koulutusModel.setOpintoala(koulutuskoodi.getOpintoala());
            opintoala.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getOpintoala(), KoulutusKoodistoModel.MODEL_NAME_PROPERY));
        }

        if (koulutuskoodi.getKoulutusaste() != null) {
            koulutusModel.setKoulutusaste(koulutuskoodi.getKoulutusaste());
            koulutusaste.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getKoulutusaste(), KoulutusKoodistoModel.MODEL_NAME_PROPERY));
        }

        if (koulutuskoodi.getKoulutusala() != null) {
            koulutusModel.setKoulutusala(koulutuskoodi.getKoulutusala());
            koulutusala.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getKoulutusala(), KoulutusKoodistoModel.MODEL_NAME_PROPERY));
        }

        String lySuffix = "";
        if (!isPervako && koulutuskoodi.getOpintojenLaajuusyksikko() != null) {
            koulutusModel.setOpintojenLaajuusyksikko(koulutuskoodi.getOpintojenLaajuusyksikko());
            if (koulutuskoodi.getOpintojenLaajuusyksikko().getKoodistoUri() != null) {
                lySuffix = " " + new TarjontaUIHelper().getKoodiLyhytNimi(koulutuskoodi.getOpintojenLaajuusyksikko().getKoodistoUri(), I18N.getLocale());
            }
        }

        if (!isPervako && koulutuskoodi.getOpintojenLaajuus() != null) {
            koulutusModel.setOpintojenLaajuus(koulutuskoodi.getOpintojenLaajuus());
            opintojenLaajuus.setValue(koulutuskoodi.getOpintojenLaajuus().getNimi() + lySuffix);
        }

        if (koulutuskoodi.getKoulutuksenRakenne() != null) {
            koulutusModel.setKoulutuksenRakenne(koulutuskoodi.getKoulutuksenRakenne());
            koulutuksenRakenne.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getKoulutuksenRakenne(), KoulutusKoodistoModel.MODEL_NAME_PROPERY));
        }

        if (koulutuskoodi.getTavoitteet() != null) {
            koulutusModel.setTavoitteet(koulutuskoodi.getTavoitteet());
            tavoitteet.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getTavoitteet(), KoulutusKoodistoModel.MODEL_NAME_PROPERY));
        }

        if (koulutuskoodi.getJatkoopintomahdollisuudet() != null) {
            koulutusModel.setJatkoopintomahdollisuudet(koulutuskoodi.getJatkoopintomahdollisuudet());
            jatkoopintomahdollisuudet.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getJatkoopintomahdollisuudet(), KoulutusKoodistoModel.MODEL_NAME_PROPERY));
        }

        final KoulutusohjelmaModel koulutusohjelma = koulutusModel.getKoulutusohjelmaModel();

        if (koulutusohjelma != null && koulutusohjelma.getTutkintonimike() != null) {
            koulutusModel.setTutkintonimike(koulutusohjelma.getTutkintonimike());
            tutkintonimike.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getTutkintonimike(), KoulutusKoodistoModel.MODEL_NAME_PROPERY));
        }

        if (koulutusohjelma != null && koulutusohjelma.getTavoitteet() != null) {
            koulutusModel.setKoulutusohjelmaTavoitteet(koulutusohjelma.getTavoitteet());
            this.koulutusohjelmanTavoitteet.setPropertyDataSource(new NestedMethodProperty(koulutusModel.getKoulutusohjelmaTavoitteet(), KoulutusKoodistoModel.MODEL_NAME_PROPERY));
        }
        disableOrEnableComponents(true);

        // OVT-4607 väliaikainen pakotettu valinta
        if(!isPervako) {
          koulutusModel.setKoulutuslaji("koulutuslaji_n#1");
        }
    }

    private void clearKomoLabels() {
        opintoala.setValue("");
        koulutusaste.setValue("");
        koulutusala.setValue("");
        opintojenLaajuus.setValue("");
        koulutuksenRakenne.setValue("");
        tavoitteet.setValue("");
        jatkoopintomahdollisuudet.setValue("");
        tutkintonimike.setValue("");
        koulutusohjelmanTavoitteet.setValue("");
    }

    private void showNoKoulutusDialog() {

        NoKoulutusDialog noKoulutusView = new NoKoulutusDialog("viesti", new Button.ClickListener() {
            private static final long serialVersionUID = -5998239901946190160L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeNoKoulutusDialog();
            }
        });

        noKoulutusView.setWidth("120px");
        noKoulutusView.setHeight("60px");

        noKoulutusDialog = new TarjontaDialogWindow(noKoulutusView, T("noKoulutusLabel"));

        getWindow().addWindow(noKoulutusDialog);
    }

    private void closeNoKoulutusDialog() {
        if (noKoulutusDialog != null) {
            getWindow().removeWindow(noKoulutusDialog);
        }
    }

}
