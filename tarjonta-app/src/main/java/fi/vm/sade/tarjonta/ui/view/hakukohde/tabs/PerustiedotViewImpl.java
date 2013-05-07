
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
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
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OsoiteDTO;
import fi.vm.sade.organisaatio.api.model.types.OsoiteTyyppi;
import fi.vm.sade.organisaatio.api.model.types.YhteystietoDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeNameUriModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.PainotettavaOppiaineViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Hakukohde basic information.
 *
 * @author Tuomas Katva
 * @author Timo Santasalo / Teknokala Ky
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class PerustiedotViewImpl extends VerticalLayout implements PerustiedotView {

    private static final long serialVersionUID = 1L;
    @Autowired
    private TarjontaUIHelper tarjontaUIHelper;
    private static final Logger LOG = LoggerFactory.getLogger(PerustiedotViewImpl.class);
    private TarjontaPresenter presenter;
    //MainLayout element
    private VerticalLayout mainLayout;
    private GridLayout itemContainer;
    //Fields
    @NotNull(message = "{validation.Hakukohde.hakukohteenNimi.notNull}")
//    @PropertyId("hakukohdeNimi")
//    KoodistoComponent hakukohteenNimiCombo;
    private ComboBox hakukohteenNimiCombo;
    @PropertyId("tunnisteKoodi")
    private TextField tunnisteKoodiText;
    @NotNull(message = "{validation.Hakukohde.haku.notNull}")
    @PropertyId("hakuOid")
    private ComboBox hakuCombo;
    
    private ComboBox hakuAikaCombo;
    
    @Min(value = 0, message = "{validation.Hakukohde.aloituspaikat.num}")
    @NotNull(message = "{ShowHakukohdeViewImpl.liitaUusiKoulutusDialogTitle}")
    @PropertyId("aloitusPaikat")
    private TextField aloitusPaikatText;
    @Min(value = 0, message = "{validation.Hakukohde.valinnoissaKaytettavatPaikat.num}")
    @NotNull(message = "{validation.Hakukohde.valinnoissaKaytettavatPaikatText.notNull}")
    @PropertyId("valinnoissaKaytettavatPaikat")
    private TextField valinnoissaKaytettavatPaikatText;
    @PropertyId("hakukelpoisuusVaatimus")
    private Label hakuKelpoisuusVaatimuksetLabel;
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
//    LanguageTabSheet valintaPerusteidenKuvausTabs;
    private HakukohdeLisatiedotTabSheet lisatiedotTabs;
    private Label osoiteSelectLabel;
    //private Label serverMessage = new Label("");
    //Info buttons
    private Button upRightInfoButton;
    private Button downRightInfoButton;
    private String languageTabsheetWidth = "500px";
    private String languageTabsheetHeight = "230px";
    private transient UiBuilder uiBuilder;
    private ErrorMessage errorView;
    private GridLayout painotettavatOppiaineet;
    private KoulutusasteTyyppi koulutusasteTyyppi;
    
    private List<TextField> painotettavat = Lists.newArrayList();

    public List<TextField> getPainotettavat() {
        return painotettavat;
    }

    private boolean muuOsoite;

    /*
     *
     * Init view with new model
     *
     */
    public PerustiedotViewImpl(TarjontaPresenter presenter, UiBuilder uiBuilder) {
        super();
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;

        this.koulutusasteTyyppi = getKoulutusasteTyyppi();

        buildMainLayout();
        this.presenter.initHakukohdeForm(this);
    }

    // Figure out the type
    private KoulutusasteTyyppi getKoulutusasteTyyppi() {
        final HakukohdeViewModel model = presenter.getModel().getHakukohde();

        Preconditions.checkNotNull(model);

        // first check if there are some koulutuses attached
        if (model.getKoulukses() != null && model.getKoulukses().size() > 0) {
            return model.getKoulukses().get(0).getKoulutustyyppi();
        }
        if (presenter.getModel().getSelectedKoulutukset() != null
                && presenter.getModel().getSelectedKoulutukset().size() > 0) {
            return presenter.getModel().getSelectedKoulutukset().get(0).getKoulutus().getKoulutustyyppi();
        }

        if (model.getKomotoOids().size() > 0) {
            //XXX probably this information is available somewhere in the presenter
            LueKoulutusVastausTyyppi koulutus = presenter.getKoulutusByOid(model.getKomotoOids().get(0));
            return koulutus.getKoulutusmoduuli().getKoulutustyyppi();
        }

        throw new RuntimeException("Can not figure out the type!");
    }

    @Override
    public void setTunnisteKoodi(String tunnistekoodi) {
        tunnisteKoodiText.setValue(tunnistekoodi);
    }

    @Override
    public void initForm(HakukohdeViewModel model) {
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
        hakukohteenNimiCombo.setImmediate(true);

        hakukohteenNimiCombo.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (event.getProperty().getValue() instanceof HakukohdeNameUriModel) {
                    HakukohdeNameUriModel selectedHakukohde = (HakukohdeNameUriModel) event.getProperty().getValue();
                    tunnisteKoodiText.setEnabled(true);
                    tunnisteKoodiText.setValue(selectedHakukohde.getHakukohdeArvo());
                    tunnisteKoodiText.setEnabled(false);
                } else {
                    LOG.warn("hakukohteenNimiCombo / value change listener - value was not a String! class = {}",
                            (event.getProperty().getValue() != null) ? event.getProperty().getValue().getClass() : "NULL");
                }


            }
        });

        if (presenter != null && presenter.getModel() != null && presenter.getModel().getHakukohde() != null) {
        	if (presenter.getModel().getHakukohde().getSelectedHakukohdeNimi() != null) {
                hakukohteenNimiCombo.setValue(presenter.getModel().getHakukohde().getSelectedHakukohdeNimi());
        	}
        	selectHakuAika(presenter.getModel().getHakukohde().getHakuaika(), presenter.getModel().getHakukohde().getHakuOid(), true);
        }
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

        addComponent(mainLayout);
    }

    private GridLayout buildGrid() {
        itemContainer = new GridLayout(2, 1);
        itemContainer.setWidth(UiConstant.PCT100);
        itemContainer.setSpacing(true);
        itemContainer.setMargin(false, true, true, true);

        addItemToGrid("", buildErrorLayout());
        addItemToGrid("PerustiedotView.hakukohteenNimi", buildHakukode());
        addItemToGrid("PerustiedotView.hakuValinta", buildHakuCombo());
        addItemToGrid("PerustiedotView.hakuaikaValinta", buildHakuaikaCombo());

        addItemToGrid("PerustiedotView.hakukelpoisuusVaatimukset", buildHakukelpoisuusVaatimukset());

        addItemToGrid("PerustiedotView.aloitusPaikat", buildAloitusPaikat());
        addItemToGrid("PerustiedotView.valinnoissaKaytettavatPaikatText", buildValinnoissaKaytettavatAloitusPaikat());

        if (this.koulutusasteTyyppi == KoulutusasteTyyppi.LUKIOKOULUTUS) {
            addItemToGrid("PerustiedotView.alinHyvaksyttavaKeskiarvoText", buildAlinHyvaksyttavaKeskiarvo());
            addItemToGrid("PerustiedotView.painotettavatOppiaineet", buildPainotettavatOppiaineet());
        }

        //addItemToGrid("PerustiedotView.LiitteidenToimitusOsoite", buildLiitteidenToimitusOsoite());
        addItemToGrid("PerustiedotView.LiitteidenToimitusOsoite", buildOsoiteSelectLabel());
        addItemToGrid("", buildOsoiteSelect());
        addItemToGrid("", buildLiitteidenToimitusOsoite());
        addItemToGrid("", buildSahkoinenToimitusOsoiteCheckBox());
        addItemToGrid("", buildSahkoinenToimitusOsoiteTextField());
        addItemToGrid("PerustiedotView.toimitettavaMennessa", buildToimitusPvmField());

        itemContainer.setColumnExpandRatio(0, 0f);
        itemContainer.setColumnExpandRatio(1, 1f);

        checkCheckboxes();

        if (muuOsoite) {
            enableOrDeEnableOsoite(true);
        } else {
            enableOrDeEnableOsoite(false);
        }

        return itemContainer;
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
    public void refreshOppiaineet() {
        painotettavat.clear();
        while (painotettavatOppiaineet.getRows() > 1) {
            painotettavatOppiaineet.removeRow(1);
        }

        for (PainotettavaOppiaineViewModel painotettava : presenter.getModel().getHakukohde().getPainotettavat()) {
            addOppiaine(painotettava);
        }
    }

    private void addOppiaine(final PainotettavaOppiaineViewModel painotettava) {
        final PropertysetItem psi = new BeanItem(painotettava);
        //TODO change koodisto to oppiaine
        System.out.println("uri:" + KoodistoURIHelper.KOODISTO_OPPIAINEET_URI);
        final KoodistoComponent painotus = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_OPPIAINEET_URI, psi, "oppiaine", T("PerusTiedotView.oppiainePrompt"), true);
        painotus.getField().setRequired(false);
        painotus.getField().setNullSelectionAllowed(false);
        painotettavatOppiaineet.addComponent(painotus);

        final TextField tf = uiBuilder.integerField(null, psi, "painokerroin", null, null, 1, 100,  T("validation.PerustiedotView.painokerroin.num"));
        painotettavat.add(tf);
        // uiBuilder.textField(null, psi, "painokerroin", null, null);
        // tf.addValidator(new IntegerValidator(T("validation.PerustiedotView.painokerroin.num")));
        painotettavatOppiaineet.addComponent(tf);
        final Button removeRowButton = UiUtil.button(null, T("PerustiedotView.poistaPainotettavaOppiaine"),
                new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                deleteOppiaineRow(painotettava);
            }

            private void deleteOppiaineRow(PainotettavaOppiaineViewModel painotettava) {
                for (int y = 1; y < painotettavatOppiaineet.getRows(); y++) {
                    final TextField textField = (TextField) painotettavatOppiaineet.getComponent(1, y);

                    if (textField == tf) { //yes I am comparing references
                        presenter.getModel().getHakukohde().getPainotettavat().remove(painotettava);
                        painotettavat.remove(tf); //remove from validation
                        painotettavatOppiaineet.removeRow(y);
                    }
                }
            }
        });
        painotettavatOppiaineet.addComponent(removeRowButton);
    }

    private void addNewOppiaineRow() {
        PainotettavaOppiaineViewModel uusi = new PainotettavaOppiaineViewModel();
        presenter.getModel().getHakukohde().getPainotettavat().add(uusi);
        addOppiaine(uusi);
    }

    private void checkCheckboxes() {
        if (this.presenter != null && this.presenter.getModel().getHakukohde() != null) {

            if (presenter.getModel().getHakukohde().getLiitteidenSahkoinenToimitusOsoite() != null) {
                myosSahkoinenToimitusSallittuCb.setValue(true);
            } else {
                myosSahkoinenToimitusSallittuCb.setValue(false);
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

    public void setSelectedHakukohdenimi(HakukohdeNameUriModel koodiType) {
        if (hakukohteenNimiCombo != null) {
            hakukohteenNimiCombo.setValue(koodiType);
        }
    }

    public HakukohdeNameUriModel getSelectedHakukohde() {
        if (hakukohteenNimiCombo != null) {
            return (HakukohdeNameUriModel) hakukohteenNimiCombo.getValue();
        } else {
            return null;
        }
    }

    private void addItemToGrid(String captionKey, AbstractComponent component) {

        if (itemContainer != null) {
            Label label = UiUtil.label(null, T(captionKey));
            itemContainer.addComponent(label);
            itemContainer.setComponentAlignment(label, Alignment.TOP_RIGHT);
            itemContainer.addComponent(component);
            itemContainer.newLine();
        }

    }

    private AbstractLayout buildOsoiteSelectLabel() {
        VerticalLayout osoiteSelectLayout = new VerticalLayout();
        osoiteSelectLayout.setSizeFull();


        osoiteSelectLabel = new Label(T("PerustiedotView.osoiteSelectLabel"));
        //osoiteSelectLayout.addComponent(osoiteSelectLabel);

        osoiteSelectLayout.addComponent(osoiteSelectLabel);








        return osoiteSelectLayout;
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
        if (presenter.getModel().getHakukohde().getOsoiteRivi1() != null && presenter.getModel().getHakukohde().getPostinumero() != null ) {

            String hakukohdeOsoite = presenter.getModel().getHakukohde().getOsoiteRivi1().trim();
            String hakukohdePostinumero = presenter.getModel().getHakukohde().getPostinumero().trim();
            if (osoite.getOsoite().trim().equalsIgnoreCase(hakukohdeOsoite) && osoite.getPostinumero().trim().equalsIgnoreCase(hakukohdePostinumero)) {
                return false;
            } else {
                return true;
            }


        } else {
            setOsoiteToOrganisaationPostiOsoite(osoite);
            return false;
        }


    }

    private void setOsoiteToOrganisaationPostiOsoite(OsoiteDTO osoite) {
        if (osoite != null) {
        presenter.getModel().getHakukohde().setOsoiteRivi1(osoite.getOsoite());
        presenter.getModel().getHakukohde().setPostinumero(osoite.getPostinumero());
        presenter.getModel().getHakukohde().setPostitoimipaikka(osoite.getPostitoimipaikka());
        }
    }

    private OsoiteDTO getOrganisaationPostiOsoite() {
        OrganisaatioDTO organisaatioDTO = presenter.getSelectOrganisaatioModel();
        OsoiteDTO returnValue = null;
        if (organisaatioDTO != null) {
            for (YhteystietoDTO yhteystietoDTO : organisaatioDTO.getYhteystiedot()) {
                if (yhteystietoDTO instanceof OsoiteDTO) {
                    OsoiteDTO osoite = (OsoiteDTO) yhteystietoDTO;
                    if (osoite.getOsoiteTyyppi().equals(OsoiteTyyppi.POSTI)) {
                        returnValue = osoite;
                    }
                }
            }
        }
        return returnValue;
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
        osoiteSelectOptionGroup.setImmediate(true);
        osoiteSelectOptionGroup.addListener(new Property.ValueChangeListener() {
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
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (clickEvent.getButton().booleanValue()) {

                    if (liitteidenToimitusPvm != null) {
                        if (hakuCombo != null) {
                            Object id = hakuCombo.getValue();

                            if (id instanceof HakuViewModel) {
                                liitteidenToimitusPvm.setValue(((HakuViewModel) id).getAlkamisPvm());
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

//        liitteidenToimitusPvm = UiUtil.dateField();
        liitteidenToimitusPvm = new DateField();
        liitteidenToimitusPvm.setResolution(DateField.RESOLUTION_MIN);
        liitteidenToimitusPvm.setDateFormat("dd.MM.yyyy hh:mm");


        verticalLayout.addComponent(liitteidenToimitusPvm);

        kaytaHaunPaattymisAikaa.setValue(true);


        return verticalLayout;
    }

    private CheckBox buildSahkoinenToimitusOsoiteCheckBox() {
        myosSahkoinenToimitusSallittuCb = UiUtil.checkbox(null, null);
        myosSahkoinenToimitusSallittuCb.setImmediate(true);
        myosSahkoinenToimitusSallittuCb.setCaption(T("PerustiedotView.LiiteVoidaanToimittaaSahkoisestiCheckbox"));
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

        liitteidenPostinumeroText = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_POSTINUMERO_URI);
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
            @Override
            public void valueChange(ValueChangeEvent valueChangeEvent) {
                String koodiUri = (String) valueChangeEvent.getProperty().getValue();
                String postitoimipaikka = tarjontaUIHelper.getKoodiNimi(koodiUri, I18N.getLocale());
                liitteidenPostitoimipaikkaText.setValue(postitoimipaikka);
            }
        });

        return osoiteLayout;
    }

    private Label buildHakukelpoisuusVaatimukset() {
        //TODO get the text from valintaperusteista
        hakuKelpoisuusVaatimuksetLabel = UiUtil.label((AbstractLayout) null, T("PerustiedotView.hakukelpoisuusvaatimukset.help"), LabelStyleEnum.TEXT);
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

    private TextField buildAlinHyvaksyttavaKeskiarvo() {
        alinHyvaksyttavaKeskiarvoText = UiUtil.textField(null);
        alinHyvaksyttavaKeskiarvoText.setRequired(true);
        alinHyvaksyttavaKeskiarvoText.addValidator(new DoubleValidator(T("validation.PerustiedotView.alinHyvaksyttavaKeskiarvo.num")));
        return alinHyvaksyttavaKeskiarvoText;
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
    	System.err.println("SET SELECTED HAKU "+haku.getSisaisetHakuajat());
        hakuCombo.setValue(haku);
    }
    
    @Override
    public HakuaikaViewModel getSelectedHakuaika() {
    	return (HakuaikaViewModel) hakuAikaCombo.getValue();
    }
    
    private void prepareHakuAikas(HakuViewModel hvm) {
    	BeanItemContainer<HakuaikaViewModel> container = new BeanItemContainer<HakuaikaViewModel>(HakuaikaViewModel.class);
    	container.addAll(hvm.getSisaisetHakuajat());
    	hakuAikaCombo.setReadOnly(false);
    	hakuAikaCombo.setContainerDataSource(container);
    	
    	selectHakuAika(presenter.getModel().getHakukohde().getHakuaika(), hvm, false);
    }
    
    private void selectHakuAika(HakuaikaViewModel hvm, HakuViewModel hk, boolean initial) {
		
    	System.err.println((initial ? "INITIAL" : "COMBO")+" SELECT "+hvm+" OF "+hk+" -- "+hk.getSisaisetHakuajat());
    	
    	hakuAikaCombo.setReadOnly(false); // setValue ei toimi jos readonly
    	if (hk==null || hk.getSisaisetHakuajat().isEmpty()) {
    		hakuAikaCombo.setValue(null);
        	hakuAikaCombo.setReadOnly(true);
        	hakuAikaCombo.setEnabled(false);
        } else if (hk.getSisaisetHakuajat().size()==1) {
    		hakuAikaCombo.setValue(hakuAikaCombo.getContainerDataSource().getItemIds().iterator().next());
        	hakuAikaCombo.setReadOnly(true);
        	hakuAikaCombo.setEnabled(true);
    	} else {
    		hakuAikaCombo.setValue(hvm);
        	hakuAikaCombo.setEnabled(true);
    	}


    }
    
    private ComboBox buildHakuaikaCombo() {
    	hakuAikaCombo = new ComboBox();
    	hakuAikaCombo.setRequired(true);
    	hakuAikaCombo.setEnabled(false);
    	return hakuAikaCombo;
    }
    	 

    private ComboBox buildHakuCombo() {
        hakuCombo = new ComboBox();
        hakuCombo.setImmediate(true);
        hakuAikaCombo = new ComboBox();
        
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

        //hakukohteenNimiCombo = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_HAKUKOHDE_URI);
        hakukohteenNimiCombo = UiUtil.comboBox(null, null, null);

        Collection<KoodiType> hakukohdeKoodis = tarjontaUIHelper.getRelatedHakukohdeKoodisByKomotoOids(presenter.getModel().getHakukohde().getKomotoOids());
        Set<HakukohdeNameUriModel> hakukohdes = new HashSet<HakukohdeNameUriModel>();
        for (KoodiType koodiType : hakukohdeKoodis) {
            hakukohdes.add(presenter.hakukohdeNameUriModelFromKoodi(koodiType));
        }
        BeanItemContainer<HakukohdeNameUriModel> hakukohdeContainer = new BeanItemContainer<HakukohdeNameUriModel>(HakukohdeNameUriModel.class, hakukohdes);
        hakukohteenNimiCombo.setContainerDataSource(hakukohdeContainer);
        hakukohteenNimiCombo.setImmediate(true);



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
        tunnisteKoodiText = UiUtil.textField(hl, "", T("PerustiedotView.tunnistekoodi.prompt"), true);
        tunnisteKoodiText.setEnabled(false);

        hl.setComponentAlignment(hakukohteenNimiCombo, Alignment.TOP_LEFT);
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
        UiUtil.label(vl, T("PerustiedotView.lisatiedot.help"), LabelStyleEnum.TEXT);
        lisatiedotTabs = buildLanguageTab();
        vl.addComponent(lisatiedotTabs);
        return vl;
    }

    /*
     private HakukohdeLisatiedotTabSheet buildLanguageTab(List<KielikaannosViewModel> arvot) {
     return new HakukohdeLisatiedotTabSheet();
     }
     */
    private HakukohdeLisatiedotTabSheet buildLanguageTab() {
        return new HakukohdeLisatiedotTabSheet(true, languageTabsheetWidth, languageTabsheetHeight);
    }

    private String T(String key) {
        return I18N.getMessage(key);
    }
}