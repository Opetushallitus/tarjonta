package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;/*
 *
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


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakukohdeLiiteViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.DateTimeField;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * @author: Tuomas Katva Date: 14.1.2013
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class HakukohteenLiitteetViewImpl extends CustomComponent implements Property.ValueChangeListener {

    public static final String LANGUAGE_TAB_SHEET_WIDTH = "600px";
    private static final long serialVersionUID = 8051865706102814333L;
    private transient I18NHelper i18n;
    private static final Logger LOG = LoggerFactory.getLogger(PerustiedotViewImpl.class);
    @Autowired
    private TarjontaPresenter presenter;
    @Autowired
    private transient TarjontaUIHelper tarjontaUIHelper;
    private transient UiBuilder uiBuilder;
    private VerticalLayout mainLayout;
    private GridLayout itemContainer;
    @PropertyId("liitteenTyyppi")
    @NotNull(message = "{validation.HakukohdeLiitteet.liitteenTyyppi.notNull}")
    KoodistoComponent liitteenTyyppi;
    private LiitteenSanallinenKuvausTabSheet liitteenSanallinenKuvausTxtArea;
    @NotNull(message = "{validation.HakukohdeLiitteet.toimitettavaMennessa.notNull}")
    @PropertyId("toimitettavaMennessa")
    private DateTimeField toimittettavaMennessa;
    @PropertyId("osoiteRivi1")
    private TextField osoiteRivi1;
    @PropertyId("osoiteRivi2")
    private TextField osoiteRivi2;
    @PropertyId("postinumero")
    private KoodistoComponent postinumero;
    @PropertyId("postitoimiPaikka")
    private TextField postitoimipaikka;
    private CheckBox voidaanToimittaaSahkoisesti;
    @PropertyId("sahkoinenToimitusOsoite")
    private TextField sahkoinenToimitusOsoite;
    private ErrorMessage errorMessage;
    private Button cancelButton;
    private Button saveButton;
    private Form form;
    private String languageTabsheetWidth = "650px";
    private String languageTabsheetHeight = "250px";
    private Button upRightInfoButton;
    private OptionGroup osoiteValinta;
    private HakukohdeLiiteViewModel selectedLiite;
    private TarjontaDialogWindow dialogWindow;
    private boolean modelEdited = false;

    public HakukohteenLiitteetViewImpl(ErrorMessage errorMessage, TarjontaPresenter presenter, UiBuilder uiBuilder) {
        super();
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        this.errorMessage = errorMessage;
        buildMainLayout();
        setCustomOsoiteEnabled(false);
       addValueChangeListeners();
    }

    private void addValueChangeListeners() {
        liitteenTyyppi.addListener(this);
        toimittettavaMennessa.addListener(this);
        osoiteRivi1.addListener(this);
        osoiteRivi2.addListener(this);
        postinumero.addListener(this);
        voidaanToimittaaSahkoisesti.addListener(this);
        osoiteValinta.addListener(this);
    }

    @Override
    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
        modelEdited = true;
    }
    
    @Override
    public void attach() {
        super.attach();
        filterKooditBasedOnPohjakoulutus();
    }

    
    private void filterKooditBasedOnPohjakoulutus() {
        String pkVaatimus = null;
        
        if (presenter.getModel().getSelectedKoulutukset() != null 
                && presenter.getModel().getSelectedKoulutukset().get(0) != null 
                && presenter.getModel().getSelectedKoulutukset().get(0).getPohjakoulutusvaatimus() != null) {
            pkVaatimus =  presenter.getModel().getSelectedKoulutukset().get(0).getPohjakoulutusvaatimus().getUri();
        }
       
        boolean isYksilollistettyPerusopetus = pkVaatimus != null 
                && pkVaatimus.contains(KoodistoURI.KOODI_YKSILOLLISTETTY_PERUSOPETUS_URI);
        if (!isYksilollistettyPerusopetus && !isKoulutusSortOfErityisopetus()) {
            liitteenTyyppi.getField().removeItem(KoodistoURI.KOODI_TODISTUKSET_URI);
        }
    }
    
    private boolean isKoulutusSortOfErityisopetus() {
        KoulutusasteTyyppi koulutustyyppi = presenter.getModel().getSelectedKoulutukset().get(0).getKoulutusasteTyyppi();
        return koulutustyyppi.equals(KoulutusasteTyyppi.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS)
                || koulutustyyppi.equals(KoulutusasteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS)
                || koulutustyyppi.equals(KoulutusasteTyyppi.MAAHANM_AMM_VALMISTAVA_KOULUTUS)
                || koulutustyyppi.equals(KoulutusasteTyyppi.MAAHANM_LUKIO_VALMISTAVA_KOULUTUS)
                || koulutustyyppi.equals(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS)
                || koulutustyyppi.equals(KoulutusasteTyyppi.PERUSOPETUKSEN_LISAOPETUS);
    }

    private String T(String key) {
        if (i18n == null) {
            i18n = new I18NHelper(this);
        }
        return i18n.getMessage(key);
    }

    private void buildMainLayout() {
        mainLayout = new VerticalLayout();

        mainLayout.setMargin(true);

        //mainLayout.addComponent(buildInfoButtonLayout());

        mainLayout.addComponent(buildGridLayout());

        mainLayout.addComponent(buildSaveCancelButtonLayout());

        setCompositionRoot(mainLayout);


        initForm();
    }

    private void closeDialogWindow() {
        if (dialogWindow != null) {
            getWindow().getApplication().getMainWindow().removeWindow(dialogWindow);
        }
    }

    private void initForm() {
        selectedLiite = presenter.getSelectedHakuliite();
        BeanItem<HakukohdeLiiteViewModel> hakukohdeLiiteBean = new BeanItem<HakukohdeLiiteViewModel>(presenter.getSelectedHakuliite());
        form = new ValidatingViewBoundForm(this);
        form.setItemDataSource(hakukohdeLiiteBean);
        if (presenter.getSelectedHakuliite() != null && presenter.getSelectedHakuliite().getSahkoinenToimitusOsoite() != null && presenter.getSelectedHakuliite().getSahkoinenToimitusOsoite().trim().length() > 0) {
            sahkoinenToimitusOsoite.setEnabled(true);
            voidaanToimittaaSahkoisesti.setValue(true);
        } else {
            sahkoinenToimitusOsoite.setEnabled(false);
            voidaanToimittaaSahkoisesti.setValue(false);
        }

        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
        this.form.setValidationVisible(false);
        this.form.setValidationVisibleOnCommit(false);
    }

    public void setMainLayoutSizeFull() {
        mainLayout.setSizeFull();
    }

    private void addItemToGrid(String captionKey, AbstractComponent component) {

        if (itemContainer != null) {
            Label label;
            if (captionKey != null && captionKey.trim().length() > 0) {
            label = UiUtil.label(null, T(captionKey));
            } else {
            label = UiUtil.label(null,"");
            }
            label.setContentMode(Label.CONTENT_XHTML);
            itemContainer.addComponent(label);
            itemContainer.setComponentAlignment(label, Alignment.TOP_RIGHT);
            itemContainer.addComponent(component);
            itemContainer.newLine();
        }

    }

    private KoodistoComponent buildLiitteenTyyppiCombo() {
        liitteenTyyppi = uiBuilder.koodistoComboBox(null, KoodistoURI.KOODISTO_LIITTEEN_TYYPPI_URI);

        return liitteenTyyppi;
    }

    private LiitteenSanallinenKuvausTabSheet buildLiitteenSanallinenKuvaus() {
        liitteenSanallinenKuvausTxtArea = new LiitteenSanallinenKuvausTabSheet(true, LANGUAGE_TAB_SHEET_WIDTH, languageTabsheetHeight);
        liitteenSanallinenKuvausTxtArea.addValueChangeListener(this);

        return liitteenSanallinenKuvausTxtArea;
    }

    public List<KielikaannosViewModel> getLiitteenSanallisetKuvaukset() {
        return liitteenSanallinenKuvausTxtArea.getKieliKaannokset();
    }

    private DateField buildToimitettavaMennessa() {
        toimittettavaMennessa = new DateTimeField();
        toimittettavaMennessa.setResolution(DateField.RESOLUTION_MIN);
        toimittettavaMennessa.setParseErrorMessage(T("toimitettavaMennessa.dateFormat"));
        toimittettavaMennessa.setMissingDateMessage(T("toimitettavaMennessa.missingDate"));
        toimittettavaMennessa.setMissingTimeMessage(T("toimitettavaMennessa.missingTime"));
        toimittettavaMennessa.setEmptyErrorMessage(T("toimitettavaMennessa.dateFormat"));

        return toimittettavaMennessa;
    }

    private GridLayout buildLiitteidenToimitusOsoite() {
        GridLayout osoiteLayout = new GridLayout(2, 3);
        
        osoiteRivi1 = UiUtil.textField(null);
        osoiteRivi1.setWidth("100%");
        osoiteRivi1.setInputPrompt(I18N.getMessage("PerustiedotView.osoiteRivi1"));
        osoiteRivi1.setRequiredError(I18N.getMessage("HakukohteenLiitteetViewImpl.validation.osoite"));
        osoiteLayout.addComponent(osoiteRivi1, 0, 0, 1, 0);

        osoiteRivi2 = UiUtil.textField(null);
        osoiteRivi2.setWidth("100%");

        osoiteLayout.addComponent(osoiteRivi2, 0, 1, 1, 1);

        postinumero = uiBuilder.koodistoComboBox(null, KoodistoURI.KOODISTO_POSTINUMERO_URI);
        osoiteLayout.addComponent(postinumero, 0, 2);
        postinumero.setSizeUndefined();
        postinumero.setRequiredError(I18N.getMessage("HakukohteenLiitteetViewImpl.validation.postinumero"));

        postitoimipaikka = UiUtil.textField(null);
        postitoimipaikka.setInputPrompt(I18N.getMessage("PerustiedotView.postitoimipaikka"));
        osoiteLayout.addComponent(postitoimipaikka, 1, 2);
        postitoimipaikka.setSizeUndefined();

        osoiteLayout.setColumnExpandRatio(0, 2);
        osoiteLayout.setColumnExpandRatio(1, 4);

        postinumero.setFieldValueFormatter(new FieldValueFormatter() {
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

        postinumero.setCaptionFormatter(new CaptionFormatter() {
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

        postinumero.setImmediate(true);
        postinumero.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (tarjontaUIHelper != null) {
                    String koodiUri = (String) valueChangeEvent.getProperty().getValue();
                    String postitoimipaikkaStr = tarjontaUIHelper.getKoodiNimi(koodiUri, I18N.getLocale());
                    postitoimipaikka.setValue(postitoimipaikkaStr);
                }
            }
        });


        return osoiteLayout;
    }

    private boolean isLiiteEdited() {

        if (modelEdited) {
            return true;
        }

        return false;
    }

    private VerticalLayout buildSahkoinenToimitusOsoite() {
        VerticalLayout sahkoinenToimitusOsoiteLayout = new VerticalLayout();

        voidaanToimittaaSahkoisesti = new CheckBox();
        voidaanToimittaaSahkoisesti.setCaption(T("voidaanToimittaaMyosSahkoisesti"));
        voidaanToimittaaSahkoisesti.setImmediate(true);
        voidaanToimittaaSahkoisesti.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (clickEvent.getButton().booleanValue()) {
                    sahkoinenToimitusOsoite.setEnabled(true);
                } else {
                    sahkoinenToimitusOsoite.setEnabled(false);
                    sahkoinenToimitusOsoite.setValue(null);
                }
            }
        });
        sahkoinenToimitusOsoiteLayout.addComponent(voidaanToimittaaSahkoisesti);
        sahkoinenToimitusOsoite = UiUtil.textField(null);
        sahkoinenToimitusOsoiteLayout.addComponent(sahkoinenToimitusOsoite);

        return sahkoinenToimitusOsoiteLayout;
    }

    private HorizontalLayout buildSaveCancelButtonLayout() {

        HorizontalLayout horizontalButtonLayout = UiUtil.horizontalLayout();

        cancelButton = UiBuilder.button(null, T("cancelBtn"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (isLiiteEdited()) {
                    RemovalConfirmationDialog removalDialog = new RemovalConfirmationDialog(i18n.getMessage("modelEditedVarmistusMsg"), null, i18n.getMessage("yesBtn"), i18n.getMessage("noBtn"),
                            new Button.ClickListener() {
                        private static final long serialVersionUID = 5019806363620874205L;

                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            closeDialogWindow();
                            presenter.closeCancelHakukohteenEditView();

                        }
                    }, new Button.ClickListener() {
                        private static final long serialVersionUID = 5019806363620874205L;

                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            closeDialogWindow();
                        }
                    });

                    dialogWindow = new TarjontaDialogWindow(removalDialog, i18n.getMessage("varmistusMsg"));
                    getApplication().getMainWindow().addWindow(dialogWindow);
                } else {
                    presenter.closeCancelHakukohteenEditView();
                }

            }
        });
        horizontalButtonLayout.addComponent(cancelButton);

        saveButton = UiBuilder.button(null, T("saveBtn"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                errorMessage.resetErrors();
                try {
                    form.commit();
                    if (form.isValid() && validateExtras()) {
                        presenter.getModel().getSelectedLiite().setLiitteenSanallinenKuvaus(getLiitteenSanallisetKuvaukset());
                        presenter.saveHakukohteenEditView();
                    }
                } catch (Validator.InvalidValueException e) {
                	LOG.info("Validation error", e);
                    errorMessage.addError(e);
                } catch (Exception exp) {
                	LOG.warn("Error saving hakukohde", exp);
                    errorMessage.addError(exp.toString());
                }
            }
        });
        horizontalButtonLayout.addComponent(saveButton);
        horizontalButtonLayout.setWidth(UiConstant.PCT100);
        horizontalButtonLayout.setComponentAlignment(cancelButton, Alignment.BOTTOM_LEFT);
        horizontalButtonLayout.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);

        return horizontalButtonLayout;
    }

    private boolean validateExtras() {

        boolean retval = true;
         if ((Boolean)voidaanToimittaaSahkoisesti.getValue()) {
              String sahkOsoite = (String)sahkoinenToimitusOsoite.getValue();
              if (sahkOsoite != null && sahkOsoite.trim().length() > 0) {
                  retval = true;
              } else {
                 errorMessage.addError(T("sahkoinenToimitusOsoite.valueMissing"));
                  retval = false;
              }

         }  else {
             retval = true;
         }

        Date now = new Date();

        if (((Date)toimittettavaMennessa.getValue()).before(now)) {
            errorMessage.addError(T("toimPvmMenneessa"));
            retval = false;
        }

        return retval;
    }

    private void setCustomOsoiteEnabled(boolean enabled) {
    	presenter.setCustomLiiteOsoiteSelected(enabled);
        if (osoiteRivi1 != null) {
            osoiteRivi1.setEnabled(enabled);
            osoiteRivi1.setRequired(enabled);
        }
        if (osoiteRivi2 != null) {
            osoiteRivi2.setEnabled(enabled);
        }
        if (postinumero != null) {
            postinumero.setEnabled(enabled);
            postinumero.setRequired(enabled);
        }
        if (postitoimipaikka != null) {
            postitoimipaikka.setEnabled(enabled);
        }
    }

    private VerticalLayout buildOsoiteSelectionLayout() {
        VerticalLayout osoiteSelectLayout = new VerticalLayout();
        
        final String oletus = T("toimitusOsoiteValintaOletus");
        final String muu = T("toimitusOsoiteValintaMuu");

        List<String> selections = new ArrayList<String>();
        selections.add(oletus);
        selections.add(muu);

        osoiteValinta = new OptionGroup("", selections);
        osoiteValinta.setNullSelectionAllowed(false);
        osoiteValinta.select(presenter.isCustomLiiteOsoiteSelected() ? muu : oletus);
        osoiteValinta.setImmediate(true);
        osoiteValinta.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
            	setCustomOsoiteEnabled(valueChangeEvent.getProperty().getValue().equals(muu));
            }
            
        });

        osoiteSelectLayout.addComponent(osoiteValinta);

        return osoiteSelectLayout;
    }

    private GridLayout buildGridLayout() {
        itemContainer = new GridLayout(2, 1);
        itemContainer.setWidth(UiConstant.PCT100);
        itemContainer.setSpacing(true);
        itemContainer.setMargin(false, true, true, true);

        addItemToGrid("liitteenTyyppi", buildLiitteenTyyppiCombo());
        addItemToGrid("liitteenSanallinenKuvaus", buildLiitteenSanallinenKuvaus());
        addItemToGrid("toimitettavaMennessa", buildToimitettavaMennessa());
        addItemToGrid("toimitusOsoite", buildOsoiteSelectionLayout());
        addItemToGrid("", buildLiitteidenToimitusOsoite());
        addItemToGrid("", buildSahkoinenToimitusOsoite());

        setCustomOsoiteEnabled(presenter.isCustomLiiteOsoiteSelected());
        itemContainer.setColumnExpandRatio(0, 0f);
        itemContainer.setColumnExpandRatio(1, 1f);
        return itemContainer;
    }

}
