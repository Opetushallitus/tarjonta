
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
package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.validator.DoubleValidator;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.organisaatio.api.model.types.OsoiteDTO;
import fi.vm.sade.organisaatio.api.model.types.OsoiteTyyppi;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.helper.conversion.HakukohdeViewModelToDTOConverter;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeNameUriModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.PainotettavaOppiaineViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.DateRangeEnforcer;
import fi.vm.sade.tarjonta.ui.view.haku.HakuajatView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Hakukohde basic information.
 *
 * @author Tuomas Katva
 * @author Timo Santasalo / Teknokala Ky
 * @author Jani Wilén
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class PerustiedotViewImpl extends VerticalLayout implements PerustiedotView {

    private static final long serialVersionUID = 1L;

    private final String HAKUKELPOISUUSVAATIMUS_KOODISTO_URI = "hakukelpoisuusvaatimusta";

    private final String PERUSKOULUPOHJAINEN_ARVO = "1";



    @Autowired
    private TarjontaUIHelper tarjontaUIHelper;
    private static final Logger LOG = LoggerFactory.getLogger(PerustiedotViewImpl.class);
    private TarjontaPresenter presenter;
    //MainLayout element
    private VerticalLayout mainLayout;
    private GridLayout itemContainer;
    //Fields
//    KoodistoComponent hakukohteenNimiCombo;
    @PropertyId("selectedHakukohdeNimi")
    @NotNull(message = "{validation.Hakukohde.hakukohteenNimi.notNull}")
    private ComboBox hakukohteenNimiCombo;
    @PropertyId("editedHakukohdeNimi")
    @NotNull(message = "{validation.Hakukohde.hakukohteenNimi.notNull}")
    private TextField hakukohteenNimiText;
//    @PropertyId("tunnisteKoodi")
    private TextField tunnisteKoodiText;
    @NotNull(message = "{validation.Hakukohde.haku.notNull}")
    @PropertyId("hakuViewModel")
    private ComboBox hakuCombo;
    private Label hakuAikaLabel;
    private ComboBox hakuAikaCombo; //TODO refaktoroi tama kokonaan pois (nyt piilotettu)
    private Label hakuAikaContentLabel;
    @Min(value = 1, message = "{validation.Hakukohde.aloituspaikat.num}")
    @Max(value = Integer.MAX_VALUE, message = "{validation.Hakukohde.aloituspaikat.max}")
    @NotNull(message = "{validation.Hakukohde.aloitusPaikat.notNull}")
    @PropertyId("aloitusPaikat")
    private TextField aloitusPaikatText;
    @Min(value = 1, message = "{validation.Hakukohde.valinnoissaKaytettavatPaikat.num}")
    @Max(value = Integer.MAX_VALUE, message = "{validation.Hakukohde.aloituspaikat.max}")
    @NotNull(message = "{validation.Hakukohde.valinnoissaKaytettavatPaikatText.notNull}")
    @PropertyId("valinnoissaKaytettavatPaikat")
    private TextField valinnoissaKaytettavatPaikatText;
    @PropertyId("osoiteRivi1")
    private TextField liitteidenOsoiteRivi1Text;
    @PropertyId("osoiteRivi2")
    private TextField liitteidenOsoiteRivi2Text;
    @PropertyId("postinumero")
    private KoodistoComponent liitteidenPostinumeroText;
    @PropertyId("postitoimipaikka")
    private TextField liitteidenPostitoimipaikkaText;
    @PropertyId("sahkoinenToimitusSallittu")
    private CheckBox myosSahkoinenToimitusSallittuCb;
    @Pattern(regexp = "[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", message = "{validation.invalid.www}")
    @PropertyId("liitteidenSahkoinenToimitusOsoite")
    private TextField sahkoinenToimitusOsoiteText;
    @PropertyId("kaytaHaunPaattymisenAikaa")
    private CheckBox kaytaHaunPaattymisAikaa;
    @PropertyId("liitteidenToimitusPvm")
    private DateField liitteidenToimitusPvm;
    @PropertyId("alinHyvaksyttavaKeskiarvo")
    private TextField alinHyvaksyttavaKeskiarvoText;
    private OptionGroup osoiteSelectOptionGroup;

    @PropertyId("kaksoisTutkinto")
    private CheckBox kaksoistutkintoCheckbox;

    private Label kaksoisTutkintoLabel;

    @PropertyId("customHakuaikaEnabled")
    private CheckBox customHakuaika;

    @PropertyId("hakuaikaAlkuPvm")
    @NotNull(message = "{validation.Hakukohde.hakuaikaAlku.notNull}")
    private DateField hakuaikaAlkuPvm;
    @PropertyId("hakuaikaLoppuPvm")
    @NotNull(message = "{validation.Hakukohde.hakuaikaLoppu.notNull}")
    private DateField hakuaikaLoppuPvm;

//    LanguageTabSheet valintaPerusteidenKuvausTabs;
    private HakukohteenKuvausTabSheet valintaperusteTabs;
    private HakukohdeLisatiedotTabSheet lisatiedotTabs;
    //private Label osoiteSelectLabel;
    //private Label serverMessage = new Label("");
    //Info buttons
    private Button upRightInfoButton;
    private String languageTabsheetWidth = "500px";
    private String languageTabsheetHeight = "230px";
    private transient UiBuilder uiBuilder;
    private ErrorMessage errorView;
    private GridLayout painotettavatOppiaineet;
    private HakukohdeViewModel model;

    private HorizontalLayout customDatesLayout;

    public GridLayout getPainotettavatOppiaineet() {
        return painotettavatOppiaineet;
    }
    private KoulutusasteTyyppi koulutusasteTyyppi;
    private List<TextField> painotettavat = Lists.newArrayList();

    public List<TextField> getPainotettavat() {
        return painotettavat;
    }
    private boolean muuOsoite;

    @Value("${koodisto-uris.yhteishaku}")
    private String hakutapaYhteishakuUrl;

    @Value("${koodisto-uris.lisahaku}")
    private String hakutyyppiLisahakuUrl;

    private String pkVaatimus;
    private KoulutusasteTyyppi koulutusastetyyppi;
    
    @Value("${koodisto-uris.erillishaku}")
    private String hakutapaErillishaku;

    /*
     *
     * Init view with new model
     *
     */
    public PerustiedotViewImpl(TarjontaPresenter presenter, UiBuilder uiBuilder) {
        super();
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        model = presenter.getModel().getHakukohde();
        this.koulutusasteTyyppi = model.getKoulutusasteTyyppi();

        buildMainLayout();
        this.presenter.initHakukohdeForm(this);
    }

    public boolean isSahkoinenToimOsoiteChecked() {
        return (myosSahkoinenToimitusSallittuCb == null) ? false : myosSahkoinenToimitusSallittuCb.booleanValue();
    }

    @Override
    public void setTunnisteKoodi(String tunnistekoodi) {
        tunnisteKoodiText.setEnabled(true);
        tunnisteKoodiText.setValue(tunnistekoodi);
        tunnisteKoodiText.setEnabled(false);
    }

    @Override
    public void initForm() {
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
        if (!KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS.equals(model.getKoulutusasteTyyppi())) {
            hakukohteenNimiCombo.setImmediate(true);
            hakukohteenNimiCombo.setRequired(true);
            hakukohteenNimiCombo.addListener(new Property.ValueChangeListener() {
                private static final long serialVersionUID = -382717228031608542L;

                @Override
                public void valueChange(ValueChangeEvent event) {
                    if (event.getProperty().getValue() instanceof HakukohdeNameUriModel) {
                        HakukohdeNameUriModel selectedHakukohde = (HakukohdeNameUriModel) event.getProperty().getValue();
                        setKaksoistutkintoEnabledOrDisabled(selectedHakukohde.getHakukohdeUri());
                        setTunnisteKoodi(selectedHakukohde.getHakukohdeArvo());
                    } else {
                        LOG.warn("hakukohteenNimiCombo / value change listener - value was not a String! class = {}",
                                (event.getProperty().getValue() != null) ? event.getProperty().getValue().getClass() : "NULL");
                    }
                }
            });
        }

        if (presenter != null && presenter.getModel() != null && model != null) {
            selectHakuAika(model.getHakuaika(), model.getHakuViewModel());
            setCustomHakuaika(this.doesHakukohdeNeedCustomhakuaika(), (model.getHakuaikaAlkuPvm() != null) && (model.getHakuaikaLoppuPvm() != null));//hakutyyppiLisahakuUrl.equals(model.getHakuViewModel() != null ?  model.getHakuViewModel().getHakutyyppi() : null)
            hakuAikaCombo.setEnabled(model.getHakuaika()!=null);
        }
        hakuAikaCombo.setVisible(false);
    }

    private boolean doesHakukohdeNeedCustomhakuaika() {
        KoulutusasteTyyppi kTyyppi = presenter.getModel().getSelectedKoulutukset().get(0).getKoulutustyyppi();
        boolean isErityisopetus = (pkVaatimus != null && pkVaatimus.contains(KoodistoURI.KOODI_YKSILOLLISTETTY_PERUSOPETUS_URI))
                || kTyyppi.equals(KoulutusasteTyyppi.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS)
                || kTyyppi.equals(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS);
        boolean isLisahaku = hakutyyppiLisahakuUrl.equals(model.getHakuViewModel() != null ?  model.getHakuViewModel().getHakutyyppi() : null);;
        return isLisahaku || isErityisopetus;
    }

    /*
     * Main layout building method.
     *
     */
    private void buildMainLayout() {
        mainLayout = new VerticalLayout();


        if (presenter.getModel().getSelectedKoulutukset() != null
                && !presenter.getModel().getSelectedKoulutukset().isEmpty()) {
            pkVaatimus = presenter.getModel().getSelectedKoulutukset().get(0).getPohjakoulutusvaatimus() != null
                    ? presenter.getModel().getSelectedKoulutukset().get(0).getPohjakoulutusvaatimus().getUri() : null;
            final KoulutusPerustieto koulutus = presenter.getModel()
                    .getSelectedKoulutukset().get(0);
            koulutusastetyyppi = koulutus.getKoulutustyyppi();
        }

        //Build main item container
        mainLayout.addComponent(buildGrid());

        //Add bottom addtional info text areas and info button
        mainLayout.addComponent(buildBottomAreaLanguageTab());

        if (isHakukohdeAnErkkaOrValmentava()) {
            mainLayout.addComponent(buildBottomAreaValintaperusteTab());
        }

        addComponent(mainLayout);
    }

    private boolean isHakukohdeAnErkkaOrValmentava() {
        return (pkVaatimus != null && pkVaatimus.contains(KoodistoURI.KOODI_YKSILOLLISTETTY_PERUSOPETUS_URI))
                || (koulutusastetyyppi!=null && koulutusastetyyppi == KoulutusasteTyyppi.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS)
                || presenter.isKoulutusNivelvaihe();
    }

    private boolean checkPeruskouluPohjaisuus(KoodiType koodiType) {
        if (koodiType.getKoodiArvo().trim().equals(PERUSKOULUPOHJAINEN_ARVO)) {
            return true;
        } else {
            return false;
        }
    }

    private void setKaksoistutkintoEnabledOrDisabled(String hakukohdeNimiUri) {
        if(this.isHakukohdeAnErkkaOrValmentava()) {
            //kaksoistutkinto is only applicable to reqular ammmatillinen koulutus 
            return;
        }
        if (isPeruskoulupohjainen(hakukohdeNimiUri))  {
            kaksoistutkintoCheckbox.setVisible(true);
            kaksoisTutkintoLabel.setVisible(true);
        } else {
            kaksoistutkintoCheckbox.setVisible(false);
            kaksoisTutkintoLabel.setVisible(false);
        }

    }

    private boolean isPeruskoulupohjainen(String hakukohdeNimiUriParam) {
       String hakukohdeNimiUri = null;
       if (hakukohdeNimiUriParam != null) {
           hakukohdeNimiUri = hakukohdeNimiUriParam;
       } else if (presenter.getModel().getHakukohde() != null && presenter.getModel().getHakukohde().getSelectedHakukohdeNimi() != null) {
           hakukohdeNimiUri =  presenter.getModel().getHakukohde().getSelectedHakukohdeNimi().getHakukohdeUri();
       }


       if (hakukohdeNimiUri != null) {

           Collection<KoodiType> koodiTypes = tarjontaUIHelper.getKoodistoRelations(hakukohdeNimiUri,HAKUKELPOISUUSVAATIMUS_KOODISTO_URI,
                   false,SuhteenTyyppiType.SISALTYY);

           for (KoodiType koodi: koodiTypes) {
               if (checkPeruskouluPohjaisuus(koodi)) {
                   return true;
               }
           }

           return false;
       } else {
           return false;
       }

    }

    private GridLayout buildGrid() {
        Preconditions.checkNotNull(koulutusasteTyyppi, "KoulutusasteTyyppi enum cannot be null.");

        itemContainer = new GridLayout(2, 1);
        itemContainer.setWidth(UiConstant.PCT100);
        itemContainer.setSpacing(true);
        itemContainer.setMargin(false, true, true, true);

        addItemToGrid("", buildErrorLayout());
        addItemToGrid("PerustiedotView.hakukohteenNimi", buildHakukode());
        addItemToGrid("PerustiedotView.hakuValinta", buildHakuCombo());
        hakuAikaLabel = addItemToGrid("PerustiedotView.hakuaikaValinta", buildHakuaikaSelector());

        //OVT-4671, agreed that hakukelpoisuus vaatimus is removed from form.
        //addItemToGrid("PerustiedotView.hakukelpoisuusVaatimukset", buildHakukelpoisuusVaatimukset());

        addItemToGrid("PerustiedotView.aloitusPaikat", buildAloitusPaikat());
        addItemToGrid("PerustiedotView.valinnoissaKaytettavatPaikatText", buildValinnoissaKaytettavatAloitusPaikat());

        if (this.koulutusasteTyyppi == KoulutusasteTyyppi.LUKIOKOULUTUS) {
            addItemToGrid("PerustiedotView.alinHyvaksyttavaKeskiarvoText", buildAlinHyvaksyttavaKeskiarvo());
            addItemToGrid("PerustiedotView.painotettavatOppiaineet", buildPainotettavatOppiaineet());
        }

        //addItemToGrid("PerustiedotView.LiitteidenToimitusOsoite", buildLiitteidenToimitusOsoite());
        if (!this.isHakukohdeAnErkkaOrValmentava()) {
            addItemToGrid("PerustiedotView.LiitteidenToimitusOsoite", buildOsoiteSelectLabel());
            addItemToGrid("", buildOsoiteSelect());
            addItemToGrid("", buildLiitteidenToimitusOsoite());
            addItemToGrid("", buildSahkoinenToimitusOsoiteCheckBox());
            addItemToGrid("", buildSahkoinenToimitusOsoiteTextField());
            addItemToGrid("PerustiedotView.toimitettavaMennessa", buildToimitusPvmField());


            addItemToGrid("", buildKaksoistutkintoField("PerustiedotView.kaksoistutkinto",(koulutusasteTyyppi != KoulutusasteTyyppi.LUKIOKOULUTUS && isPeruskoulupohjainen(null))));

            checkCheckboxes();

            if (muuOsoite) {
                enableOrDeEnableOsoite(true);
            } else {
                enableOrDeEnableOsoite(false);
            }
        } else {
            setLiitteidenToimOsoite();
        }

        itemContainer.setColumnExpandRatio(0, 0f);
        itemContainer.setColumnExpandRatio(1, 1f);

        return itemContainer;
    }

    private AbstractComponent buildKaksoistutkintoField(String captionKey, boolean isVisible) {

        HorizontalLayout verticalLayout = new HorizontalLayout();

        kaksoisTutkintoLabel = UiUtil.label(null, T(captionKey));

        kaksoistutkintoCheckbox = new CheckBox();

        verticalLayout.addComponent(kaksoistutkintoCheckbox);
        verticalLayout.addComponent(kaksoisTutkintoLabel);

        kaksoistutkintoCheckbox.setVisible(isVisible);
        kaksoisTutkintoLabel.setVisible(isVisible);

        return verticalLayout;

    }

    private AbstractComponent buildPainotettavatOppiaineet() {
        final VerticalLayout lo = new VerticalLayout();

        painotettavatOppiaineet = new GridLayout(3, 1);
        lo.addComponent(painotettavatOppiaineet);
        painotettavatOppiaineet.addComponent(UiUtil.label(null, T("PerustiedotView.painokerroin")), 1, 0);
        painotettavatOppiaineet.newLine();

        refreshOppiaineet();

        //lisää nappula
        UiUtil.button(lo, T("PerustiedotView.lisaaPainotettavaOppiaine"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                addNewOppiaineRow();
            }
        });

        return lo;
    }

    /**
     * (re)draw painotettavat oppiaineet contents, first remove old content (if
     * any) and then re add form fields
     */
    @Override
    public void refreshOppiaineet() {
        painotettavat.clear();
        if (painotettavatOppiaineet != null) {

            while (painotettavatOppiaineet.getRows() > 1) {
                painotettavatOppiaineet.removeRow(1);
            }

            for (PainotettavaOppiaineViewModel painotettava : model.getPainotettavat()) {
                addOppiaine(painotettava);
            }
        }
    }

    private void addOppiaine(final PainotettavaOppiaineViewModel painotettava) {
        final PropertysetItem psi = new BeanItem(painotettava);
        //TODO change koodisto to oppiaine
        final KoodistoComponent painotus = uiBuilder.koodistoComboBox(null, KoodistoURI.KOODISTO_OPPIAINEET_URI, psi, "oppiaine", T("PerusTiedotView.oppiainePrompt"), true);
        painotus.getField().setRequired(false);
        painotus.getField().setNullSelectionAllowed(false);
        painotettavatOppiaineet.addComponent(painotus);

        final TextField tf = UiBuilder.doubleField(null, psi, "painokerroin", null, null, 1, 20, T("validation.PerustiedotView.painokerroin.num"), I18N.getLocale());
        tf.setData(painotettava);
        painotettavat.add(tf);

        painotettavatOppiaineet.addComponent(tf);
        final Button removeRowButton = UiUtil.button(null, T("PerustiedotView.poistaPainotettavaOppiaine"),
                new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                removeOppiaineRow(painotettava);
            }
        });
        painotettavatOppiaineet.addComponent(removeRowButton);
    }

    @Override
    public void addNewOppiaineRow(PainotettavaOppiaineViewModel oppiaine) {
        model.getPainotettavat().add(oppiaine);
        addOppiaine(oppiaine);
    }

    @Override
    public void removeOppiaineRow(PainotettavaOppiaineViewModel painotettava) {
//        Preconditions.checkNotNull(painotettava, "PainotettavaOppiaineViewModel object cannot be null.");
//        Preconditions.checkNotNull(model, "HakukohdeViewModel object cannot be null.");
//        Preconditions.checkNotNull(model.getPainotettavat(), "PainotettavaOppiaine list object cannot be null.");

        for (int y = 1; y < painotettavatOppiaineet.getRows(); y++) {
            final TextField textField = (TextField) painotettavatOppiaineet.getComponent(1, y);
            if (textField.getData() != null && textField.getData().equals(painotettava)) {
                model.getPainotettavat().remove(painotettava);
                painotettavat.remove(textField); //remove from validation
                painotettavatOppiaineet.removeRow(y);
            }
        }
        model.getPainotettavat().remove(painotettava);
    }

    private void addNewOppiaineRow() {
        addNewOppiaineRow(new PainotettavaOppiaineViewModel());
    }

    private void checkCheckboxes() {
        if (this.presenter != null && this.model != null) {

            if (model.getLiitteidenSahkoinenToimitusOsoite() != null && model.getLiitteidenSahkoinenToimitusOsoite().trim().length() > 0) {
                sahkoinenToimitusOsoiteText.setEnabled(true);
            } else {
                sahkoinenToimitusOsoiteText.setEnabled(false);
            }
        }
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


    private Label addItemToGrid(String captionKey, AbstractComponent component) {

        if (itemContainer != null) {
            Label label = UiUtil.label(null, T(captionKey));
            itemContainer.addComponent(label);
            itemContainer.setComponentAlignment(label, Alignment.TOP_RIGHT);
            itemContainer.addComponent(component);
            itemContainer.newLine();
            return label;
        } else {
            return null;
        }

    }

    private Label buildOsoiteSelectLabel() {
        Label label = UiUtil.label((AbstractLayout) null, T("PerustiedotView.osoiteSelectLabel"), LabelStyleEnum.TEXT);
        label.setWidth("725px");
        return label;
    }

    private void enableOrDeEnableOsoite(boolean toEnableOrNot) {
        if (liitteidenOsoiteRivi1Text != null) {
            liitteidenOsoiteRivi1Text.setEnabled(toEnableOrNot);
        }
        if (liitteidenOsoiteRivi2Text != null) {
            liitteidenOsoiteRivi2Text.setEnabled(toEnableOrNot);
        }
        if (liitteidenPostinumeroText != null) {
            liitteidenPostinumeroText.setEnabled(toEnableOrNot);
        }
        if (liitteidenPostitoimipaikkaText != null) {
            liitteidenPostitoimipaikkaText.setEnabled(toEnableOrNot);
        }
    }

    private boolean setLiitteidenToimOsoite() {
        OsoiteDTO osoite = getOrganisaationPostiOsoite();
        if (model.getOsoiteRivi1() != null && model.getPostinumero() != null) {

            String hakukohdeOsoite = model.getOsoiteRivi1().trim();
            String hakukohdePostinumero = model.getPostinumero().trim();
            if (osoite != null && osoite.getOsoite() != null && osoite.getPostinumero() != null && osoite.getOsoite().trim().equalsIgnoreCase(hakukohdeOsoite) && osoite.getPostinumero().trim().equalsIgnoreCase(hakukohdePostinumero)) {
                return false;
            } else {
                return true;
            }
        } else if (osoite != null) {
            setOsoiteToOrganisaationPostiOsoite(osoite);
            return false;
        } else {
            return true;
        }
    }

    private void setOsoiteToOrganisaationPostiOsoite(OsoiteDTO osoite) {
        Preconditions.checkNotNull(osoite, "OsoiteDTO object cannot be null.");

        model.setOsoiteRivi1(osoite.getOsoite());
        model.setPostinumero(osoite.getPostinumero());
        model.setPostitoimipaikka(osoite.getPostitoimipaikka());
    }

    private OsoiteDTO getOrganisaationPostiOsoite() {
        return presenter.resolveSelectedOrganisaatioOsoite(OsoiteTyyppi.POSTI);
    }

    private AbstractLayout buildOsoiteSelect() {
        VerticalLayout osoiteSelectOptionLayout = new VerticalLayout();
        List<String> selections = new ArrayList<String>();
        selections.add(T("PerustiedotView.osoiteSelectOrganisaatioPostiOsoite"));
        selections.add(T("PerustiedotView.osoiteSelectMuuOsoite"));
        //selections.add("Organisaation postiosoite");
        //selections.add("Muu osoite");
        osoiteSelectOptionGroup = new OptionGroup("", selections);
        osoiteSelectOptionGroup.setNullSelectionAllowed(false);
        boolean isMuuOsoiteOsoite = setLiitteidenToimOsoite();
        if (isMuuOsoiteOsoite) {
            osoiteSelectOptionGroup.select(T("PerustiedotView.osoiteSelectMuuOsoite"));
        } else {
            osoiteSelectOptionGroup.select(T("PerustiedotView.osoiteSelectOrganisaatioPostiOsoite"));
        }

        osoiteSelectOptionGroup.setEnabled(getOrganisaationPostiOsoite() != null);

        osoiteSelectOptionGroup.setImmediate(true);
        osoiteSelectOptionGroup.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(ValueChangeEvent valueChangeEvent) {
                if (valueChangeEvent.getProperty().getValue().equals(T("PerustiedotView.osoiteSelectOrganisaatioPostiOsoite"))) {
                    enableOrDeEnableOsoite(true);
                    setLiitteidenToimOsoite();
                    enableOrDeEnableOsoite(false);
                } else {
                    enableOrDeEnableOsoite(true);
                    presenter.setHakukohteenOletusOsoiteToEmpty();
                }
            }
        });
        muuOsoite = isMuuOsoiteOsoite;
        osoiteSelectOptionLayout.addComponent(osoiteSelectOptionGroup);
        return osoiteSelectOptionLayout;
    }

    private TextField buildSahkoinenToimitusOsoiteTextField() {
        sahkoinenToimitusOsoiteText = UiUtil.textField(null);
        sahkoinenToimitusOsoiteText.setInputPrompt(T("PerustiedotView.sahkoinenToimitusOsoite.prompt"));
        sahkoinenToimitusOsoiteText.setEnabled(false);
        return sahkoinenToimitusOsoiteText;
    }

    private VerticalLayout buildToimitusPvmField() {
        VerticalLayout verticalLayout = new VerticalLayout();

        kaytaHaunPaattymisAikaa = UiUtil.checkbox(null, null);
        kaytaHaunPaattymisAikaa.setImmediate(true);
        kaytaHaunPaattymisAikaa.setCaption(T("PerustiedotView.haunPaattymisenAikaCheckbox"));
        kaytaHaunPaattymisAikaa.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (clickEvent.getButton().booleanValue()) {

                    if (liitteidenToimitusPvm != null) {
                        if (hakuCombo != null) {
                            Object id = hakuCombo.getValue();

                            if (id instanceof HakuViewModel) {
                                liitteidenToimitusPvm.setValue(((HakuViewModel) id).getPaattymisPvm());
                            }
                        }
                        liitteidenToimitusPvm.setEnabled(false);
                    }

                } else {

                    if (liitteidenToimitusPvm != null) {
                        liitteidenToimitusPvm.setEnabled(true);
                    }

                }
            }
        });

        verticalLayout.addComponent(kaytaHaunPaattymisAikaa);
        liitteidenToimitusPvm = new DateField();
        liitteidenToimitusPvm.setResolution(DateField.RESOLUTION_MIN);
        liitteidenToimitusPvm.setDateFormat(HakuajatView.DATE_FORMAT);

        verticalLayout.addComponent(liitteidenToimitusPvm);
        kaytaHaunPaattymisAikaa.setValue(true);
        return verticalLayout;
    }

    private VerticalLayout buildSahkoinenToimitusOsoiteCheckBox() {
        VerticalLayout vl = new VerticalLayout();
        myosSahkoinenToimitusSallittuCb = UiUtil.checkbox(null, null);
        myosSahkoinenToimitusSallittuCb.setImmediate(true);
        myosSahkoinenToimitusSallittuCb.setCaption(T("PerustiedotView.LiiteVoidaanToimittaaSahkoisestiCheckbox"));
        myosSahkoinenToimitusSallittuCb.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (clickEvent.getButton().booleanValue()) {
                    if (sahkoinenToimitusOsoiteText != null) {
                        sahkoinenToimitusOsoiteText.setEnabled(true);
                    }
                } else {
                    if (sahkoinenToimitusOsoiteText != null) {
                        sahkoinenToimitusOsoiteText.setEnabled(false);
                    }
                }
            }
        });
        vl.addComponent(myosSahkoinenToimitusSallittuCb);
        myosSahkoinenToimitusSallittuCb.setValue(true);
        return vl;

    }

    private GridLayout buildLiitteidenToimitusOsoite() {
        GridLayout osoiteLayout = new GridLayout(2, 3);

        liitteidenOsoiteRivi1Text = UiUtil.textField(null);
        liitteidenOsoiteRivi1Text.setWidth("100%");
        liitteidenOsoiteRivi1Text.setInputPrompt(T("PerustiedotView.osoiteRivi1"));
        liitteidenOsoiteRivi1Text.setImmediate(true);
        osoiteLayout.addComponent(liitteidenOsoiteRivi1Text, 0, 0, 1, 0);

        liitteidenOsoiteRivi2Text = UiUtil.textField(null);
        liitteidenOsoiteRivi2Text.setWidth("100%");
        liitteidenOsoiteRivi2Text.setImmediate(true);
        osoiteLayout.addComponent(liitteidenOsoiteRivi2Text, 0, 1, 1, 1);

        liitteidenPostinumeroText = uiBuilder.koodistoComboBox(null, KoodistoURI.KOODISTO_POSTINUMERO_URI);
        liitteidenPostinumeroText.setImmediate(true);

        osoiteLayout.addComponent(liitteidenPostinumeroText, 0, 2);
        liitteidenPostinumeroText.setSizeUndefined();

        liitteidenPostitoimipaikkaText = UiUtil.textField(null);
        liitteidenPostitoimipaikkaText.setImmediate(true);
        liitteidenPostitoimipaikkaText.setInputPrompt(T("PerustiedotView.postitoimipaikka"));
        osoiteLayout.addComponent(liitteidenPostitoimipaikkaText, 1, 2);
        liitteidenPostitoimipaikkaText.setSizeUndefined();

        osoiteLayout.setColumnExpandRatio(0, 2);
        osoiteLayout.setColumnExpandRatio(1, 4);
        liitteidenPostinumeroText.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object dto) {
                if (dto instanceof KoodiType) {
                    KoodiType koodi = (KoodiType) dto;
                    return koodi.getKoodiUri();
                } else {
                    return dto;
                }
            }
        });

        liitteidenPostinumeroText.setCaptionFormatter(new CaptionFormatter() {
            @Override
            public String formatCaption(Object dto) {
                if (dto instanceof KoodiType) {
                    KoodiType koodi = (KoodiType) dto;
                    return koodi.getKoodiArvo();
                } else {
                    return dto.toString();
                }
            }
        });
        liitteidenPostinumeroText.setImmediate(true);
        liitteidenPostinumeroText.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(ValueChangeEvent valueChangeEvent) {
                String koodiUri = (String) valueChangeEvent.getProperty().getValue();
                String postitoimipaikka = tarjontaUIHelper.getKoodiNimi(koodiUri, I18N.getLocale());
                liitteidenPostitoimipaikkaText.setValue(postitoimipaikka);
            }
        });

        return osoiteLayout;
    }

    private TextField buildAloitusPaikat() {
        aloitusPaikatText = UiUtil.textField(null);
        aloitusPaikatText.setRequired(true);
        return aloitusPaikatText;
    }

    private TextField buildValinnoissaKaytettavatAloitusPaikat() {
        valinnoissaKaytettavatPaikatText = UiUtil.textField(null);
        valinnoissaKaytettavatPaikatText.setRequired(true);
        return valinnoissaKaytettavatPaikatText;
    }

    private TextField buildAlinHyvaksyttavaKeskiarvo() {
        alinHyvaksyttavaKeskiarvoText = UiUtil.textField(null);
        alinHyvaksyttavaKeskiarvoText.setRequired(false);
        alinHyvaksyttavaKeskiarvoText.addListener(new TextChangeListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void textChange(TextChangeEvent event) {

                String rep = event.getText().replace(',', '.');
                if (!rep.equals(event.getText())) {
                    alinHyvaksyttavaKeskiarvoText.setValue(rep);
                }
            }
        });
        alinHyvaksyttavaKeskiarvoText.addValidator(new DoubleValidator(
                T("validation.PerustiedotView.alinHyvaksyttavaKeskiarvo.num")) {
            private static final long serialVersionUID = -2489722709663286849L;

            @Override
            protected boolean isValidString(String value) {
                if (value.indexOf(".") != -1) {
                    int decimals = value.length() - (value.indexOf(".") + 1);
                    if (decimals > 2) {
                        return false;
                    }
                }
                boolean isValidDouble = super.isValidString(value);
                if (isValidDouble) {
                    double d = Double.parseDouble(value);
                    if (d < 4 || d > 10) {
                        return false;
                    }
                }

                return isValidDouble;
            }
        });

        return alinHyvaksyttavaKeskiarvoText;
    }


    /*
     * Checks if the hakuaika is acceptable for hakukohde
     */
    private boolean accepts(HakuaikaViewModel ham, boolean isLisahakuOrErillishaku) {
        //Oph user has her own rules
        if (presenter.getPermission().userIsOphCrud()) {
            return acceptsForOph(ham);
        }
        //If it is lisahaku it is acceptable if hakuaika has not ended yet.
        if (isLisahakuOrErillishaku) {
            return ham.equals(model.getHakuaika()) || !ham.getPaattymisPvm().before(new Date());
        }
        
        //Hakuaika is ok if it has not started yet.
        return ham.equals(model.getHakuaika()) || !ham.getAlkamisPvm().before(new Date());
    }

    /*
     * Checks if hakuaika is acceptable for hakukohde in case the user is oph user
     */
    private boolean acceptsForOph(HakuaikaViewModel ham) {
        //If hakuaika has not ended it is acceptable for hakukohde
        return ham.equals(model.getHakuaika()) || !ham.getPaattymisPvm().before(new Date());
    }

    /*
     * Checks if haku is acceptable for hakukohde in case the user is oph user
     */
    private boolean acceptsForOph(HakuViewModel hm) {
        //If hakuaika has not ended it is ok
        if (hm.getPaattymisPvm() != null && !hm.getPaattymisPvm().before(new Date())) {
                return true;
        }
        //If at least 1 hakuaika is acceptabe, then the haku is acceptable.
        for (HakuaikaViewModel ham : hm.getSisaisetHakuajat()) {
                if (accepts(ham, isErillishakuOrLisahaku(hm))) {
                        return true;
                }
        }
        return false;
    }

    private boolean isErillishakuOrLisahaku(HakuViewModel hm) {
        return this.hakutyyppiLisahakuUrl.equals(hm.getHakutyyppi()) || this.hakutapaErillishaku.equals(hm.getHakutapa());
    }

    /*
     *
     * This method is called from presenter, it sets HakuTyyppis for the Haku-ComboBox
     *
     */
    @Override
    public void addItemsToHakuCombobox(List<HakuViewModel> haut) {
        BeanItemContainer<HakuViewModel> hakuContainer = new BeanItemContainer<HakuViewModel>(HakuViewModel.class);

        List<HakuViewModel> fhaut = new ArrayList<HakuViewModel>();
        for (HakuViewModel hvm : haut) {
 		fhaut.add(hvm);
        }

        hakuContainer.addAll(fhaut);
        hakuCombo.setContainerDataSource(hakuContainer);
        hakuCombo.setNullSelectionAllowed(true);
        hakuCombo.setRequired(true);
        hakuCombo.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        hakuCombo.setValue(null);

    }

    @Override
    public void setSelectedHaku(HakuViewModel haku) {
        hakuCombo.setValue(haku);
    }

    @Override
    public HakuaikaViewModel getSelectedHakuaika() {
        return (HakuaikaViewModel) hakuAikaCombo.getValue();
    }

    private void prepareHakuAikas(HakuViewModel hk) {
        BeanItemContainer<HakuaikaViewModel> container = new BeanItemContainer<HakuaikaViewModel>(HakuaikaViewModel.class);

        if (hk != null) {
            model.setHakuaika(null);
            if (hk.getSisaisetHakuajat().isEmpty()) {
                ListaaHakuTyyppi lht = new ListaaHakuTyyppi();
                lht.setHakuOid(hk.getHakuOid());
                hk = presenter.findHakuByOid(hk.getHakuOid());
            }

        	List<HakuaikaViewModel> hvms = new ArrayList<HakuaikaViewModel>();

        	boolean hamSelected = false;
            for (HakuaikaViewModel ham : hk.getSisaisetHakuajat()) {
            	if (accepts(ham, isErillishakuOrLisahaku(hk))) {

            	    hvms.add(ham);
            	    if (!hamSelected) {
            	        model.setHakuaika(ham);
            	        hamSelected = true;
            	    }
            	}
            }

            container.addAll(hvms);
        }

        hakuAikaCombo.setContainerDataSource(container);

        selectHakuAika(model.getHakuaika(), hk);
    	setCustomHakuaika(doesHakukohdeNeedCustomhakuaika(), (model.getHakuaika()==null));//this.hakutyyppiLisahakuUrl.equals(model.getHakuViewModel().getHakutyyppi())
    	//hakuAikaCombo.setEnabled(true);
    }

    private void selectHakuAika(HakuaikaViewModel hvm, HakuViewModel hk) {

        hakuAikaCombo.setEnabled(hk != null && hk.getSisaisetHakuajat().size() > 1);
        //hakuAikaLabel.setVisible(hakuAikaCombo.isVisible());

        if (hvm == null || hk == null || hk.getSisaisetHakuajat().isEmpty()) {
            hakuAikaCombo.setValue(null);
            hakuAikaContentLabel.setValue("");
        } else if (hk.getSisaisetHakuajat().size() == 1) {
            HakuaikaViewModel hakuaikaVM = (HakuaikaViewModel)(hakuAikaCombo.getContainerDataSource().getItemIds().iterator().next());
            hakuAikaCombo.setValue(hakuaikaVM);
            hakuAikaContentLabel.setValue(getHakuaikaString(hakuaikaVM.getAlkamisPvm(), hakuaikaVM.getPaattymisPvm()));
        } else {
            hakuAikaCombo.setValue(hvm);
            hakuAikaContentLabel.setValue(getHakuaikaString(hvm.getAlkamisPvm(), hvm.getPaattymisPvm()));
        }
    }

    private String getHakuaikaString(Date alkamisPvm, Date paattymisPvm) {
        SimpleDateFormat df = new SimpleDateFormat(HakuajatView.DATE_FORMAT);
        return df.format(alkamisPvm) + " - " + df.format(paattymisPvm) ;
    }

    private void setCustomHakuaika(boolean visible, boolean selected) {
    	setCustomHakuaikaEnabled(visible);
    	setCustomHakuaikaSelected(visible && true);
    }

    private void setCustomHakuaikaEnabled(boolean visible) {
    	customHakuaika.setVisible(false);
    	customDatesLayout.setVisible(visible);
        /*customHakuaika.setEnabled(enabled);
    	hakuaikaAlkuPvm.setEnabled(enabled);
    	hakuaikaLoppuPvm.setEnabled(enabled);*/
    }

    private void setCustomHakuaikaSelected(boolean selected) {
    	if (!customHakuaika.getValue().equals(selected)) {
    		customHakuaika.setValue(selected);
    	}
    	hakuaikaAlkuPvm.setEnabled(selected);
    	hakuaikaAlkuPvm.setRequired(selected);

    	hakuaikaLoppuPvm.setEnabled(selected);
    	hakuaikaLoppuPvm.setRequired(selected);

    	hakuAikaCombo.setEnabled(!selected);
    	hakuAikaCombo.setRequired(!selected);

    	//if (selected) {
    	//	hakuAikaCombo.setValue(null);
    	//}
    }

    private class HakuaikaRangeValidator implements Listener {

		private static final long serialVersionUID = 1L;

		private final boolean alku;

    	public HakuaikaRangeValidator(boolean alku) {
			super();
			this.alku = alku;
		}

		@Override
    	public void componentEvent(Event event) {
			HakuViewModel hvm = (HakuViewModel) hakuCombo.getValue();
			if (hvm==null) {
				return;
			}

			DateField df = alku ? hakuaikaAlkuPvm : hakuaikaLoppuPvm;
			Date spvm = (Date) df.getValue();
			Date opvm = (Date) (alku ? hakuaikaLoppuPvm : hakuaikaAlkuPvm).getValue();

			if (spvm==null) {
				return;
			}

			// rajaa haun alkamis- ja päättymispäivän mukaan
			if (spvm.before(hvm.getAlkamisPvm())) {
				spvm = hvm.getAlkamisPvm();
			} else if (spvm.after(hvm.getPaattymisPvm())) {
				spvm = hvm.getPaattymisPvm();
			}

			// rajaa annetun alku/loppupvm:n mukaan
			if (opvm!=null && ((alku && spvm.after(opvm)) || (!alku && spvm.before(opvm)))) {
				spvm = opvm;
			}

			if (!spvm.equals(df.getValue())) {
				df.setValue(spvm);
			}

    	}
    }

    private AbstractComponent buildHakuaikaSelector() {
    	VerticalLayout layout = new VerticalLayout();

        hakuAikaCombo = new ComboBox();
        hakuAikaCombo.setRequired(true);
        hakuAikaCombo.setWidth("500px");

        this.hakuAikaContentLabel = new Label();

        layout.addComponent(hakuAikaCombo);
        layout.addComponent(hakuAikaContentLabel);


        customHakuaika = new CheckBox("Käytetään hakukohdekohtaista hakuaikaa"); // TODO i18n
        customHakuaika.setImmediate(true);
        customHakuaika.addListener(new Listener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void componentEvent(Event event) {
				setCustomHakuaikaSelected(((Boolean)customHakuaika.getValue()).booleanValue());
			}
		});

        hakuaikaAlkuPvm = new DateField();
        hakuaikaAlkuPvm.setDateFormat(HakuajatView.DATE_FORMAT);
        hakuaikaAlkuPvm.setImmediate(true);
        //hakuaikaAlkuPvm.addListener(new HakuaikaRangeValidator(true));

        hakuaikaLoppuPvm = new DateField();
        hakuaikaLoppuPvm.setDateFormat(HakuajatView.DATE_FORMAT);
        hakuaikaLoppuPvm.setImmediate(true);
        //hakuaikaLoppuPvm.addListener(new HakuaikaRangeValidator(false));

        // estää aikakojen asettamisen väärinpäin
        new DateRangeEnforcer(hakuaikaAlkuPvm, hakuaikaLoppuPvm);

        layout.addComponent(customHakuaika);
        customHakuaika.addStyleName(Oph.SPACING_BOTTOM_10);
        customHakuaika.addStyleName(Oph.SPACING_TOP_30);

        customDatesLayout = new HorizontalLayout();
        layout.addComponent(customDatesLayout);

        Label alkuLabel = new Label("Alku:"); // TODO i18n
        Label loppuLabel = new Label("Loppu:"); // TODO i18n
        alkuLabel.addStyleName(Oph.SPACING_RIGHT_10);

        loppuLabel.addStyleName(Oph.SPACING_RIGHT_10);
        loppuLabel.addStyleName(Oph.SPACING_LEFT_20);

        customDatesLayout.addComponent(alkuLabel);
        customDatesLayout.setComponentAlignment(alkuLabel, Alignment.BOTTOM_LEFT);

        customDatesLayout.addComponent(hakuaikaAlkuPvm);

        customDatesLayout.addComponent(loppuLabel);
        customDatesLayout.setComponentAlignment(loppuLabel, Alignment.BOTTOM_LEFT);

        customDatesLayout.addComponent(hakuaikaLoppuPvm);


        return layout;
    }

    private ComboBox buildHakuCombo() {
        hakuCombo = new ComboBox();
        hakuCombo.setImmediate(true);
        hakuCombo.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                prepareHakuAikas((HakuViewModel) hakuCombo.getValue());
            }
        });

        return hakuCombo;
    }

    private ComboBox buildHaku() {
        hakukohteenNimiCombo = UiUtil.comboBox(null, null, null);

        Preconditions.checkArgument(tarjontaUIHelper!=null, "tarjonta UI helper ei saa olla null");
        Preconditions.checkArgument(model!=null, "model ei saa olla null");

        Collection<KoodiType> hakukohdeKoodis = tarjontaUIHelper.getRelatedHakukohdeKoodisByKomotoOids(model.getKomotoOids());

        if (presenter.getModel().getSelectedKoulutukset() != null) {
            //We can get the first koulutukses pohjakouluvaatimus, because all selected koulutukses should have
            //the same pohjakoulutus
            if (presenter.getModel().getSelectedKoulutukset() != null
                    && !presenter.getModel().getSelectedKoulutukset().isEmpty()
                    && !presenter.getModel().getSelectedKoulutukset().get(0).getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {//getKoulutustyyppi() ei viittaa koulutustyyppi-koodiston arvoihin vaan on oma enumeraatio
                //String pkVaatimus = presenter.getModel().getSelectedKoulutukset().get(0).getPohjakoulutusvaatimus().getUri();
                Collection<KoodiType> pkHakukohdeKoodis = tarjontaUIHelper.getKoodistoRelations(pkVaatimus, KoodistoURI.KOODISTO_HAKUKOHDE_URI, false, SuhteenTyyppiType.SISALTYY);
                hakukohdeKoodis.retainAll(pkHakukohdeKoodis);
            }
            Set<HakukohdeNameUriModel> hakukohdes = new HashSet<HakukohdeNameUriModel>();
            for (KoodiType koodiType : hakukohdeKoodis) {
                hakukohdes.add(HakukohdeViewModelToDTOConverter.hakukohdeNameUriModelFromKoodi(koodiType));
            }

            BeanItemContainer<HakukohdeNameUriModel> hakukohdeContainer = new BeanItemContainer<HakukohdeNameUriModel>(HakukohdeNameUriModel.class, hakukohdes);
            hakukohteenNimiCombo.setContainerDataSource(hakukohdeContainer);
            hakukohteenNimiCombo.setImmediate(true);
        }

        return hakukohteenNimiCombo;
    }

    /*
     *
     * Build hakukohteen nimi ComboBox and tunnistekoodi textfield
     *
     */
    private HorizontalLayout buildHakukode() {
        //Tänne vaihtoehtoisesti tekstikenttä!
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        if (KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS.equals(model.getKoulutusasteTyyppi())) {
            this.hakukohteenNimiText = UiUtil.textField(hl);
        } else {

            hakukohteenNimiCombo = buildHaku();
            hakukohteenNimiCombo.setRequired(true);
            hl.addComponent(hakukohteenNimiCombo);
            tunnisteKoodiText = UiUtil.textField(hl, "", T("PerustiedotView.tunnistekoodi.prompt"), true);
            tunnisteKoodiText.setEnabled(false);

            hl.setComponentAlignment(hakukohteenNimiCombo, Alignment.TOP_LEFT);
            //        hl.setExpandRatio(tunnisteKoodiText, 5l);
            hl.setComponentAlignment(tunnisteKoodiText, Alignment.TOP_LEFT);
        }
        return hl;
    }

    @Override
    public List<KielikaannosViewModel> getLisatiedot() {
        return this.lisatiedotTabs.getKieliKaannokset();
    }

    @Override
    public List<KielikaannosViewModel> getValintaperusteet() {
        if (this.valintaperusteTabs != null) {
            return this.valintaperusteTabs.getKieliKaannokset();
        }
        return new ArrayList<KielikaannosViewModel>();
    }

    @Override
    public void reloadLisatiedot(final List<KielikaannosViewModel> lisatiedot) {
        Preconditions.checkNotNull(lisatiedot, "List of KielikaannosViewModel objects cannot be null");
        getLisatiedot().clear();
        getLisatiedot().addAll(lisatiedot);
    }

    @Override
    public void reloadValintaperusteet(final List<KielikaannosViewModel> valintaperusteet) {
        Preconditions.checkNotNull(valintaperusteet, "List of KielikaannosViewModel objects cannot be null");
        getValintaperusteet().clear();
        getValintaperusteet().addAll(valintaperusteet);
    }

    private VerticalLayout buildBottomAreaLanguageTab() {
        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label label = UiUtil.label(hl, T("PerustiedotView.lisatiedot"), LabelStyleEnum.H2);
        hl.setExpandRatio(label, 1l);
        hl.setComponentAlignment(label, Alignment.TOP_LEFT);
        vl.addComponent(hl);
        UiUtil.label(vl, T("PerustiedotView.lisatiedot.help"), LabelStyleEnum.TEXT);
        lisatiedotTabs = buildLanguageTab();
        vl.addComponent(lisatiedotTabs);
        return vl;
    }

    private VerticalLayout buildBottomAreaValintaperusteTab() {
        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label label = UiUtil.label(hl, T("PerustiedotView.valintaperusteet"), LabelStyleEnum.H2);
        hl.setExpandRatio(label, 1l);
        hl.setComponentAlignment(label, Alignment.TOP_LEFT);
        vl.addComponent(hl);
        UiUtil.label(vl, T("PerustiedotView.valintaperusteet.help"), LabelStyleEnum.TEXT);
        valintaperusteTabs = buildValintaperusteTab();
        vl.addComponent(valintaperusteTabs);
        return vl;
    }

    private HakukohdeLisatiedotTabSheet buildLanguageTab() {
        return new HakukohdeLisatiedotTabSheet(true, languageTabsheetWidth, languageTabsheetHeight);
    }

    private HakukohteenKuvausTabSheet buildValintaperusteTab() {//String width, String height, LisaaKuvausDialog.Mode mode
        return new HakukohteenKuvausTabSheet(this.presenter, "500px", "250px", LisaaKuvausDialog.Mode.VAPE);
    }

    private String T(String key) {
        return I18N.getMessage(key);
    }
}
