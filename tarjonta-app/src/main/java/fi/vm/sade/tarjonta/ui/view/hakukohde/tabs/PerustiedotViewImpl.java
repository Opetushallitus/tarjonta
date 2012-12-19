
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

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;

import org.springframework.beans.factory.annotation.Configurable;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import java.util.List;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

import javax.validation.constraints.NotNull;

/**
 *
 * @author Tuomas Katva
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable
public class PerustiedotViewImpl extends CustomComponent implements PerustiedotView {

    private static final Logger LOG = LoggerFactory.getLogger(PerustiedotViewImpl.class);
    private TarjontaPresenter presenter;
    //MainLayout element
    VerticalLayout mainLayout;
    GridLayout itemContainer;
    //Fields
    @NotNull(message = "{validation.Hakukohde.hakukohteenNimi.notNull}")
    @PropertyId("hakukohdeNimi")
    KoodistoComponent hakukohteenNimiCombo;
    @PropertyId("tunnisteKoodi")
    TextField tunnisteKoodiText;
    @NotNull(message = "{validation.Hakukohde.haku.notNull}")
    @PropertyId("hakuOid")
    ComboBox hakuCombo;
    @NotNull(message = "{ShowHakukohdeViewImpl.liitaUusiKoulutusDialogTitle}")
    @PropertyId("aloitusPaikat")
    TextField aloitusPaikatText;
    @PropertyId("valinnoissaKaytettavatPaikat")
    TextField valinnoissaKaytettavatPaikatText;
    @PropertyId("hakukelpoisuusVaatimus")
    Label hakuKelpoisuusVaatimuksetLabel;

    @PropertyId("osoiteRivi1")
    private TextField liitteidenOsoiteRivi1Text;
    @PropertyId("osoiteRivi2")
    private TextField liitteidenOsoiteRivi2Text;
    @PropertyId("postinumero")
    private TextField liitteidenPostinumeroText;
    @PropertyId("postitoimipaikka")
    private TextField liitteidenPostitoimipaikkaText;
    @PropertyId("sahkoinenToimitusSallittu")
    private CheckBox myosSahkoinenToimitusSallittuCb;
    @PropertyId("liitteidenSahkoinenToimitusOsoite")
    private TextField sahkoinenToimitusOsoiteText;
    @PropertyId("kaytaHaunPaattymisenAikaa")
    private CheckBox kaytaHaunPaattymisAikaa;
    @PropertyId("liitteidenToimitusPvm")
    private DateField liitteidenToimitusPvm;
//    LanguageTabSheet valintaPerusteidenKuvausTabs;
    LanguageTabSheet lisatiedotTabs;
    Label serverMessage = new Label("");
    //Info buttons
    Button upRightInfoButton;
    Button downRightInfoButton;
    private Form form;
    private BeanItem<HakukohdeViewModel> hakukohdeBean;
    private UiBuilder uiBuilder;
    private ErrorMessage errorView;

    /*
     *
     * Init view with new model
     *
     */
    public PerustiedotViewImpl(TarjontaPresenter presenter, UiBuilder uiBuilder) {
        super();

        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        buildMainLayout();
        this.presenter.initHakukohdeForm(this);
    }

    @Override
    public void setTunnisteKoodi(String tunnistekoodi) {
        tunnisteKoodiText.setValue(tunnistekoodi);
    }

    @Override
    public void commitForm(String tila) {

        try {
            form.commit();
            presenter.getModel().getHakukohde().setHakukohdeKoodistoNimi(resolveHakukohdeKoodistoNimi() + " " + tila);
            presenter.saveHakuKohde(tila);
            } catch (Validator.InvalidValueException e) {
            errorView.addError(e);
            presenter.showNotification(UserNotification.GENERIC_VALIDATION_FAILED);
        }

    }

    private void showCommitNotification(String tila) {
        if (tila.trim().equalsIgnoreCase("LUONNOS")) {
            getWindow().showNotification(T("tallennettuLuonnoksena"));
        } else if (tila.trim().equalsIgnoreCase("VALMIS")) {
            getWindow().showNotification(T("tallennettuValmiina"));
        }
    }

    @Override
    public void initForm(HakukohdeViewModel model) {
        hakukohdeBean = new BeanItem<HakukohdeViewModel>(model);
        form = new ValidatingViewBoundForm(this);
        form.setItemDataSource(hakukohdeBean);
        form.getFooter().addComponent(serverMessage);

        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
        this.form.setValidationVisible(false);
        this.form.setValidationVisibleOnCommit(false);
        hakukohteenNimiCombo.setImmediate(true);
        hakukohteenNimiCombo.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (event.getProperty().getValue() instanceof String) {
                    presenter.setTunnisteKoodi(event.getProperty().getValue().toString());
                } else {
                    //DEBUGSAWAY:LOG.debug("class" + event.getProperty().getValue().getClass().getName());
                }
            }
        });
    }

    private String resolveHakukohdeKoodistoNimi() {
        // TODO tuomas korjaa :)

        String nimi = presenter.resolveHakukohdeKoodistonimiFields();
        return nimi;
    }

    /*
     * Main layout building method.
     *
     */
    private void buildMainLayout() {
        mainLayout = new VerticalLayout();
        //Add top info button layout
        mainLayout.addComponent(buildInfoButtonLayout());

        //Build main item container
        mainLayout.addComponent(buildGrid());

        //Add bottom addtional info text areas and info button
        mainLayout.addComponent(buildBottomAreaLanguageTab());

        setCompositionRoot(mainLayout);
    }

    private GridLayout buildGrid() {
        itemContainer = new GridLayout(2, 1);
        itemContainer.setWidth(UiConstant.PCT100);
        itemContainer.setSpacing(true);
        itemContainer.setMargin(false, true, true, true);

        addItemToGrid("",buildErrorLayout());
        addItemToGrid("PerustiedotView.hakukohteenNimi", buildHakukode());
        addItemToGrid("PerustiedotView.hakuValinta", buildHakuCombo());

        addItemToGrid("PerustiedotView.aloitusPaikat", buildAloitusPaikat());
        addItemToGrid("PerustiedotView.valinnoissaKaytettavatPaikatText",buildValinnoissaKaytettavatAloitusPaikat());

        addItemToGrid("PerustiedotView.hakukelpoisuusVaatimukset", buildHakukelpoisuusVaatimukset());
        addItemToGrid("PerustiedotView.LiitteidenToimitusOsoite", buildLiitteidenToimitusOsoite());
        addItemToGrid("",buildSahkoinenToimitusOsoiteCheckBox());
        addItemToGrid("",buildSahkoinenToimitusOsoiteTextField());
        addItemToGrid("PerustiedotView.toimitettavaMennessa", buildToimitusPvmField());

        itemContainer.setColumnExpandRatio(0, 0f);
        itemContainer.setColumnExpandRatio(1, 1f);

        return itemContainer;
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

    private void addItemToGrid(String captionKey, AbstractComponent component) {

        if (itemContainer != null) {
            itemContainer.addComponent(UiUtil.label(null, T(captionKey)));
            itemContainer.addComponent(component);
            itemContainer.newLine();
        }

    }

    private TextField buildSahkoinenToimitusOsoiteTextField() {
        sahkoinenToimitusOsoiteText = UiUtil.textField(null);
        sahkoinenToimitusOsoiteText.setEnabled(false);
        return sahkoinenToimitusOsoiteText;
    }

    private VerticalLayout buildToimitusPvmField()  {
         VerticalLayout verticalLayout = new VerticalLayout();

        kaytaHaunPaattymisAikaa = UiUtil.checkbox(null,null);
        kaytaHaunPaattymisAikaa.setImmediate(true);
        kaytaHaunPaattymisAikaa.setCaption(I18N.getMessage("PerustiedotView.haunPaattymisenAikaCheckbox"));
        kaytaHaunPaattymisAikaa.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (clickEvent.getButton().booleanValue()) {

                    if (liitteidenToimitusPvm != null) {
                        liitteidenToimitusPvm.setEnabled(false);
                    }

                }  else {

                    if (liitteidenToimitusPvm != null) {
                        liitteidenToimitusPvm.setEnabled(true);
                    }

                }
            }
        });

        verticalLayout.addComponent(kaytaHaunPaattymisAikaa);

        liitteidenToimitusPvm = UiUtil.dateField();


        verticalLayout.addComponent(liitteidenToimitusPvm);

        kaytaHaunPaattymisAikaa.setValue(true);
        liitteidenToimitusPvm.setEnabled(false);

         return verticalLayout;
    }

    private CheckBox buildSahkoinenToimitusOsoiteCheckBox() {
            myosSahkoinenToimitusSallittuCb = UiUtil.checkbox(null,null);
            myosSahkoinenToimitusSallittuCb.setImmediate(true);
            myosSahkoinenToimitusSallittuCb.setCaption(I18N.getMessage("PerustiedotView.LiiteVoidaanToimittaaSahkoisestiCheckbox"));
            myosSahkoinenToimitusSallittuCb.addListener(new Button.ClickListener() {
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

            return myosSahkoinenToimitusSallittuCb;
    }

    private GridLayout buildLiitteidenToimitusOsoite() {
        GridLayout osoiteLayout = new GridLayout(2,3);


        liitteidenOsoiteRivi1Text =  UiUtil.textField(null);
        liitteidenOsoiteRivi1Text.setWidth("100%");
        liitteidenOsoiteRivi1Text.setInputPrompt(I18N.getMessage("PerustiedotView.osoiteRivi1"));
        osoiteLayout.addComponent(liitteidenOsoiteRivi1Text,0,0,1,0);

        liitteidenOsoiteRivi2Text = UiUtil.textField(null);
        liitteidenOsoiteRivi2Text.setWidth("100%");
        liitteidenOsoiteRivi2Text.setInputPrompt(I18N.getMessage("PerustiedotView.osoiteRivi2"));
        osoiteLayout.addComponent(liitteidenOsoiteRivi2Text,0,1,1,1);

        liitteidenPostinumeroText = UiUtil.textField(null);
        liitteidenPostinumeroText.setInputPrompt(I18N.getMessage("PerustiedotView.postinumero"));
        osoiteLayout.addComponent(liitteidenPostinumeroText,0,2);
        liitteidenPostinumeroText.setSizeUndefined();

        liitteidenPostitoimipaikkaText = UiUtil.textField(null);
        liitteidenPostitoimipaikkaText.setInputPrompt(I18N.getMessage("PerustiedotView.postitoimipaikka"));
        osoiteLayout.addComponent(liitteidenPostitoimipaikkaText,1,2);
        liitteidenPostitoimipaikkaText.setSizeUndefined();

        osoiteLayout.setColumnExpandRatio(0,2);
        osoiteLayout.setColumnExpandRatio(1,4);

        return osoiteLayout;
    }

    private Label buildHakukelpoisuusVaatimukset() {

        hakuKelpoisuusVaatimuksetLabel = UiUtil.label(null, "");


        return hakuKelpoisuusVaatimuksetLabel;
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

    private String tryGetHaunNimi(HakuViewModel haku) {
        if (I18N.getLocale().getLanguage().trim().equals("fi")) {
            return haku.getNimiFi();
        } else if (I18N.getLocale().getLanguage().trim().equals("se")) {
            return haku.getNimiSe();
        } else if (I18N.getLocale().getLanguage().trim().equals("en")) {
            return haku.getNimiEn();
        } else {
            return haku.getNimiFi();
        }
    }

    /*
     *
     * This method is called from presenter, it sets HakuTyyppis for the Haku-ComboBox
     *
     */
    @Override
    public void addItemsToHakuCombobox(List<HakuViewModel> haut) {
        BeanItemContainer<HakuViewModel> hakuContainer = new BeanItemContainer<HakuViewModel>(HakuViewModel.class);
        hakuContainer.addAll(haut);
        hakuCombo.setContainerDataSource(hakuContainer);
        hakuCombo.setRequired(true);
        hakuCombo.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
    }

    @Override
    public void setSelectedHaku(HakuViewModel haku) {
        hakuCombo.setValue(haku);
    }

    private ComboBox buildHakuCombo() {
        hakuCombo = new ComboBox();



        return hakuCombo;
    }

    private KoodistoComponent buildHaku() {

        hakukohteenNimiCombo = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_HAKUKOHDE_URI);


        return hakukohteenNimiCombo;
    }

    /*
     *
     * Build hakukohteen nimi ComboBox and tunnistekoodi textfield
     *
     */
    private HorizontalLayout buildHakukode() {

        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        hakukohteenNimiCombo = buildHaku();
        hakukohteenNimiCombo.setRequired(true);
        hl.addComponent(hakukohteenNimiCombo);
        tunnisteKoodiText = UiUtil.textField(hl, "", T("tunnistekoodi"), true);
        tunnisteKoodiText.setEnabled(false);

        hl.setComponentAlignment(hakukohteenNimiCombo, Alignment.TOP_RIGHT);
//        hl.setExpandRatio(tunnisteKoodiText, 5l);
        hl.setComponentAlignment(tunnisteKoodiText, Alignment.TOP_LEFT);
        return hl;
    }

    private HorizontalLayout buildInfoButtonLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.TOP_RIGHT_LEFT);
        upRightInfoButton = UiUtil.buttonSmallInfo(layout);
        layout.setComponentAlignment(upRightInfoButton, Alignment.TOP_RIGHT);
        return layout;
    }

    @Override
    public List<KielikaannosViewModel> getLisatiedot() {
        return this.lisatiedotTabs.getKieliKaannokset();
    }

    private VerticalLayout buildBottomAreaLanguageTab() {
        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label label = UiUtil.label(hl, T("PerustiedotView.lisatiedot"), LabelStyleEnum.H2);
        downRightInfoButton = UiUtil.buttonSmallInfo(hl);
        hl.setExpandRatio(label, 1l);
        hl.setExpandRatio(downRightInfoButton, 3l);
        hl.setComponentAlignment(label, Alignment.TOP_LEFT);
        hl.setComponentAlignment(downRightInfoButton, Alignment.TOP_RIGHT);
        vl.addComponent(hl);
        lisatiedotTabs = buildLanguageTab();
        vl.addComponent(lisatiedotTabs);
        return vl;
    }

    private LanguageTabSheet buildLanguageTab(List<KielikaannosViewModel> arvot) {
        return new LanguageTabSheet();
    }

    private LanguageTabSheet buildLanguageTab() {
        return new LanguageTabSheet();
    }

    private String T(String key) {
        return I18N.getMessage(key);
    }
}