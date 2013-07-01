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


import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import com.vaadin.ui.Window.Notification;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeAikaViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by: Tuomas Katva Date: 23.1.2013
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class HakukohdeValintakoeViewImpl extends CustomComponent {
    private static final long serialVersionUID = -3141387197879909448L;

    private transient I18NHelper i18n = new I18NHelper(this);

    private ErrorMessage errorView;
    private transient UiBuilder uiBuilder;
    private TarjontaPresenter presenter;
    @NotNull(message = "{validation.Hakukohde.valintakoe.valintakoetyyppi.notNull}")
    @PropertyId("valintakoeTyyppi")
    private KoodistoComponent valintakoeTyyppi;
    private ValintakoeKuvausTabSheet valintaKoeKuvaus;
    private VerticalLayout aikaInputFormLayout;
    private Table valintakoeAikasTable;
    private VerticalLayout mainLayout;
    private GridLayout itemContainer;
    private Button upRightInfoButton;
    private Form form;
    private Button cancelButton;
    private Button saveButton;
    private String languageTabsheetWidth = "650px";
    private String languageTabsheetHeight = "250px";
    private HakukohdeValintaKoeAikaEditView valintaKoeAikaEditView;
    private Form valintaKoeAikaForm;
    private KoulutusasteTyyppi koulutustyyppi;
    private ValintakoeViewModel editableValintakoe;
    private boolean aikaAdded = false;

    private TarjontaDialogWindow dialogWindow;

    public HakukohdeValintakoeViewImpl(ErrorMessage errorView, TarjontaPresenter presenter, UiBuilder uiBuilder, KoulutusasteTyyppi koulutustyyppi) {
        super();
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        this.errorView = errorView;
        this.koulutustyyppi = koulutustyyppi;
        buildMainLayout();
    }

    private void initForm() {
        editableValintakoe = presenter.getSelectedValintakoe();
        BeanItem<ValintakoeViewModel> valintakoeViewModel = new BeanItem<ValintakoeViewModel>(presenter.getSelectedValintakoe());
        form = new ValidatingViewBoundForm(this);
        form.setItemDataSource(valintakoeViewModel);

        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
        this.form.setValidationVisible(false);
        this.form.setValidationVisibleOnCommit(false);
    }
    
    public void clearData() {
        valintaKoeKuvaus.resetTabSheets();
        valintaKoeKuvaus.initializeTabsheet();
        loadTableData();
        valintaKoeAikaEditView.clearData();
    }

    public void setEditableValintakoeAika(ValintakoeAikaViewModel valintakoeAika) {
        if (valintaKoeAikaEditView != null && valintaKoeAikaForm != null) {

            BeanItem<ValintakoeAikaViewModel> valintakoeAikaViewModelBean = new BeanItem<ValintakoeAikaViewModel>(valintakoeAika);
            valintaKoeAikaForm.setItemDataSource(valintakoeAikaViewModelBean);
        }
    }

    private VerticalLayout buildOsoiteEditLayout() {
        aikaInputFormLayout = new VerticalLayout();

        valintaKoeAikaEditView = new HakukohdeValintaKoeAikaEditView();

        BeanItem<ValintakoeAikaViewModel> valintakoeAikaViewModelBean = new BeanItem<ValintakoeAikaViewModel>(presenter.getModel().getSelectedValintakoeAika());
        valintaKoeAikaForm = new ValidatingViewBoundForm(valintaKoeAikaEditView);
        valintaKoeAikaForm.setItemDataSource(valintakoeAikaViewModelBean);

        JSR303FieldValidator.addValidatorsBasedOnAnnotations(valintaKoeAikaForm);

        aikaInputFormLayout.addComponent(valintaKoeAikaEditView);



        return aikaInputFormLayout;
    }

    private boolean checkValintakoeAikaOsoite(ValintakoeAikaViewModel valintakoeAika) {
        if(valintakoeAika.getOsoiteRivi() != null && valintakoeAika.getOsoiteRivi().length() > 0 &&
                valintakoeAika.getPostinumero() != null && valintakoeAika.getPostinumero().length() > 0 &&
                valintakoeAika.getPostitoimiPaikka() != null && valintakoeAika.getPostitoimiPaikka().length() > 0) {

            return true;
        } else {
            return false;
        }
    }

    private void createNewModelToValintakoeAika() {
        aikaInputFormLayout.removeAllComponents();
        BeanItem<ValintakoeAikaViewModel> valintakoeAikaViewModelBean = new BeanItem<ValintakoeAikaViewModel>(presenter.getModel().getSelectedValintakoeAika());
        valintaKoeAikaForm = new ValidatingViewBoundForm(valintaKoeAikaEditView);
        valintaKoeAikaForm.setItemDataSource(valintakoeAikaViewModelBean);

        JSR303FieldValidator.addValidatorsBasedOnAnnotations(valintaKoeAikaForm);

        aikaInputFormLayout.addComponent(valintaKoeAikaEditView);
    }

    private void buildMainLayout() {

        mainLayout = new VerticalLayout();

        mainLayout.setMargin(true);

        if (koulutustyyppi.equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS)) {
            //mainLayout.addComponent(buildInfoButtonLayout());
        }

        mainLayout.addComponent(buildGridLayout());

        if (koulutustyyppi.equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS)) {
            mainLayout.addComponent(buildSaveCancelButtonLayout());
        }

        setCompositionRoot(mainLayout);

        initForm();
    }

    private String T(String key) {
        return I18N.getMessage(key);
    }

    private HorizontalLayout buildInfoButtonLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.TOP_RIGHT);
        layout.setWidth(UiConstant.PCT100);
        upRightInfoButton = UiUtil.buttonSmallInfo(layout);
        layout.setComponentAlignment(upRightInfoButton, Alignment.TOP_RIGHT);
        return layout;
    }

    private GridLayout buildGridLayout() {
        itemContainer = new GridLayout(2, 1);
        itemContainer.setWidth(UiConstant.PCT100);
        itemContainer.setSpacing(true);
        itemContainer.setMargin(false, true, true, true);

        if (koulutustyyppi.equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS)) {
            addItemToGrid("HakukohdeValintakoeViewImpl.valintakokeenTyyppi", buildValintakokeenTyyppi());
        }
        addItemToGrid("HakukohdeValintakoeViewImpl.kuvaus", buildValintakoeKuvausTabSheet());

        //Terrible hack to get labels etc aligned properly, sorry...
        Label dummyPlaceholder = new Label("");
        itemContainer.addComponent(dummyPlaceholder);
        itemContainer.removeComponent(dummyPlaceholder);
        final int y = itemContainer.getCursorY();
        itemContainer.addComponent(buildOsoiteLayout(),0,y,1,y);

        addItemToGrid("HakukohdeValintakoeViewImpl.tableHdr", buildValintakoeAikaTableLayout());
        addLisaaButtonListener();
        itemContainer.setColumnExpandRatio(0, 0f);
        itemContainer.setColumnExpandRatio(1, 1f);
        return itemContainer;
    }

    private GridLayout buildOsoiteLayout() {
        GridLayout osoiteGrid = new GridLayout(2,2);
        osoiteGrid.setWidth(UiConstant.PCT100);

        Label label = new Label(T("HakukohdeValintakoeViewImpl.valintakokeenSijainti"));

        osoiteGrid.addComponent(label,0,0);
        osoiteGrid.setComponentAlignment(label, Alignment.TOP_RIGHT);
        osoiteGrid.addComponent(buildOsoiteEditLayout(),1,0);


        Label label1 = new Label(T("HakukohdeValintakoeViewImpl.valintakoeaika"));
        osoiteGrid.addComponent(label1,0,1);
        osoiteGrid.setComponentAlignment(label1, Alignment.TOP_RIGHT);
        osoiteGrid.addComponent(valintaKoeAikaEditView.buildValintakoeAikaLayout(),1,1);

        osoiteGrid.setColumnExpandRatio(0,0.15f);
        osoiteGrid.setColumnExpandRatio(1,1f);
        return osoiteGrid;
    }

    private void addLisaaButtonListener() {
        valintaKoeAikaEditView.addClickListenerToLisaaButton(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                errorView.resetErrors();
                valintaKoeAikaForm.commit();


                    ValintakoeAikaViewModel valintakoeAika = presenter.getSelectedAikaView();
                    boolean dateValidationFailed = false;
                    if (!valintaKoeAikaForm.isValid()) {
                        errorView.addError(valintaKoeAikaForm.getErrorMessage().toString());
                         dateValidationFailed = true;
                    }

                    if (!checkValintakoeAikaOsoite(valintakoeAika)) {
                        errorView.addError(T("HakukohdeValintakoeViewImpl.valintakoeAikaOsoiteNotNull"));
                        dateValidationFailed = true;
                    }
                    //Stupid but header is needed in the grid for valintakoeaika also so aika and lisatieto binding must done manually
                    if (valintaKoeAikaEditView.getAlkupvm().getValue() == null) {
                         errorView.addError(T("HakukohdeValintaKoeAikaEditView.alkamisAikaNotNull"));
                         dateValidationFailed = true;
                    }
                    if (valintaKoeAikaEditView.getLoppuPvm().getValue() == null) {
                        errorView.addError(T("HakukohdeValintaKoeAikaEditView.paattymisAikaNotNull"));
                        dateValidationFailed = true;
                    }
                    if (dateValidationFailed) {
                        return;
                    }
                    if (valintaKoeAikaEditView.getLisatietoja() != null) {
                    valintakoeAika.setValintakoeAikaTiedot((String)valintaKoeAikaEditView.getLisatietoja().getValue());
                    }
                    valintakoeAika.setAlkamisAika((Date)valintaKoeAikaEditView.getAlkupvm().getValue());
                    valintakoeAika.setPaattymisAika((Date)valintaKoeAikaEditView.getLoppuPvm().getValue());


                        if (valintakoeAika != null
                                && valintakoeAika.getAlkamisAika() != null
                                && valintakoeAika.getPaattymisAika() != null
                                && valintakoeAika.getAlkamisAika().before(valintakoeAika.getPaattymisAika())) {
                            presenter.getModel().getSelectedValintaKoe().getValintakoeAjat().add(valintakoeAika);
                            presenter.getModel().setSelectedValintakoeAika(new ValintakoeAikaViewModel());
                            createNewModelToValintakoeAika();
                            aikaAdded = true;
                            loadTableData();
                        } else if (koulutustyyppi.equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS)){
                            errorView.addError(T("HakukohdeValintakoeViewImpl.dateValidationFailed"));
                        } else  if (koulutustyyppi.equals(KoulutusasteTyyppi.LUKIOKOULUTUS)){
                            getWindow().showNotification(T("HakukohdeValintakoeViewImpl.dateValidationFailed"), Notification.TYPE_WARNING_MESSAGE);
                        }

            }
        });
    }

    private VerticalLayout buildValintakoeAikaTableLayout() {
        VerticalLayout tableLayout = new VerticalLayout();

        valintakoeAikasTable = new Table();
        tableLayout.addComponent(valintakoeAikasTable);
        loadTableData();

        return tableLayout;
    }

    public void loadTableData() {
        if (valintakoeAikasTable != null) {
            valintakoeAikasTable.removeAllItems();
        } else {
            valintakoeAikasTable = new Table();
        }

        final List<ValintakoeAikaViewModel> valintakoeAjat = presenter.getModel().getSelectedValintaKoe() != null ? presenter.getModel().getSelectedValintaKoe().getValintakoeAjat() : new ArrayList<ValintakoeAikaViewModel>();


        valintakoeAikasTable.setContainerDataSource(createTableContainer(valintakoeAjat));
        valintakoeAikasTable.setVisibleColumns(new String[]{"sijainti", "ajankohta", "lisatietoja", "muokkaaBtn" ,"poistaBtn"});
        valintakoeAikasTable.setColumnHeader("sijainti", T("HakukohdeValintakoeViewImpl.tableSijainti"));
        valintakoeAikasTable.setColumnHeader("ajankohta", T("HakukohdeValintakoeViewImpl.tableAjankohta"));
        valintakoeAikasTable.setColumnHeader("lisatietoja", T("HakukohdeValintakoeViewImpl.tableLisatietoja"));
        valintakoeAikasTable.setColumnHeader("muokkaaBtn", "");
        valintakoeAikasTable.setColumnHeader("poistaBtn", "");

        valintakoeAikasTable.setImmediate(true);
        valintakoeAikasTable.setSizeFull();
        valintakoeAikasTable.requestRepaint();
        valintakoeAikasTable.setPageLength(3);

        valintakoeAikasTable.setColumnExpandRatio("sijainti", 14);
        valintakoeAikasTable.setColumnExpandRatio("ajankohta", 30);
        valintakoeAikasTable.setColumnExpandRatio("lisatietoja", 30);
        valintakoeAikasTable.setColumnExpandRatio("muokkaaBtn", 8);
        valintakoeAikasTable.setColumnExpandRatio("poistaBtn", 8);
    }

    public Form getForm() {
        return form;
    }

    private BeanContainer<String, HakukohdeValintakoeAikaRow> createTableContainer(List<ValintakoeAikaViewModel> aikas) {
        BeanContainer<String, HakukohdeValintakoeAikaRow> aikasContainer = new BeanContainer<String, HakukohdeValintakoeAikaRow>(HakukohdeValintakoeAikaRow.class);

        for (ValintakoeAikaViewModel aika : aikas) {
            HakukohdeValintakoeAikaRow aikaRow = new HakukohdeValintakoeAikaRow(aika);
            aikaRow.setParent(HakukohdeValintakoeViewImpl.this);

            aikasContainer.addItem(aika.getOsoiteRivi() + aika.getValintakoeAikaTiedot(), aikaRow);
        }

        return aikasContainer;
    }

    private KoodistoComponent buildValintakokeenTyyppi() {
        valintakoeTyyppi = uiBuilder.koodistoComboBox(null, KoodistoURI.KOODISTO_VALINTAKOE_TYYPPI_URI);
        return valintakoeTyyppi;
    }

    private ValintakoeKuvausTabSheet buildValintakoeKuvausTabSheet() {
        valintaKoeKuvaus = new ValintakoeKuvausTabSheet(true, languageTabsheetWidth, languageTabsheetHeight);

        valintaKoeKuvaus.setSizeUndefined();
        return valintaKoeKuvaus;

    }
    
    public Table getValintakoeAikasTable() {
        return valintakoeAikasTable;
    }

    public List<KielikaannosViewModel> getValintakokeenKuvaukset() {
        if (valintaKoeKuvaus != null) {
            return valintaKoeKuvaus.getKieliKaannokset();
        } else {
            return new ArrayList<KielikaannosViewModel>();
        }
    }

    private boolean isModelEdited() {
        if (editableValintakoe.getSanallisetKuvaukset() != null && editableValintakoe.getSanallisetKuvaukset().size() > 0) {
            return true;
        }
        if (editableValintakoe.getValintakoeTyyppi() != null && editableValintakoe.getValintakoeTyyppi().trim().length() > 1 ) {
            return true;
        }
        if (editableValintakoe.getLisanayttoKuvaukset() != null && editableValintakoe.getLisanayttoKuvaukset().size() > 0) {
            return true;
        }



        if (aikaAdded) {
            return true;
        }

        return false;
    }


    private void closeDialogWindow() {
        if (dialogWindow != null) {
            getWindow().getApplication().getMainWindow().removeWindow(dialogWindow);

        }
    }

    private HorizontalLayout buildSaveCancelButtonLayout() {

        HorizontalLayout horizontalButtonLayout = UiUtil.horizontalLayout();

        cancelButton = UiBuilder.button(null, T("HakukohdeValintakoeViewImpl.cancelBtn"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                if (isModelEdited()) {

                    RemovalConfirmationDialog removalDialog = new RemovalConfirmationDialog(i18n.getMessage("modelEditedVarmistusMsg"),null,i18n.getMessage("yesBtn"),i18n.getMessage("noBtn"),
                            new Button.ClickListener() {
                                @Override
                                public void buttonClick(Button.ClickEvent clickEvent) {
                                    closeDialogWindow();
                                    presenter.closeValintakoeEditWindow();

                                }
                            }, new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            closeDialogWindow();
                        }
                    });

                    dialogWindow = new TarjontaDialogWindow(removalDialog,i18n.getMessage("varmistusMsg"));
                    getWindow().getApplication().getMainWindow().addWindow(dialogWindow);

                } else {

                    presenter.closeValintakoeEditWindow();

                }
            }
        });
        horizontalButtonLayout.addComponent(cancelButton);

        saveButton = UiBuilder.button(null, T("HakukohdeValintakoeViewImpl.saveBtn"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                errorView.resetErrors();
                try {
                    form.commit();
                    if (form.isValid()) {

                        if (presenter.getSelectedValintakoe().getValintakoeAjat() != null && presenter.getSelectedValintakoe().getValintakoeAjat().size() > 0) {

                        presenter.saveHakukohdeValintakoe(getValintakokeenKuvaukset());
                        } else {
                          errorView.addError(T("HakukohdeValintakoeViewImpl.vahintaaYksiValintakoeAika"));
                        }
                    }
                } catch (Validator.InvalidValueException e) {
                    errorView.addError(e);
                } catch (Exception exp) {
                }
            }
        });
        horizontalButtonLayout.addComponent(saveButton);
        horizontalButtonLayout.setWidth(UiConstant.PCT100);
        horizontalButtonLayout.setComponentAlignment(cancelButton, Alignment.BOTTOM_LEFT);
        horizontalButtonLayout.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);

        return horizontalButtonLayout;
    }

    private void addItemToGrid(String captionKey, AbstractComponent component) {
        if (itemContainer != null) {
            Label label = null;
            if (captionKey != null)  {
                label = UiUtil.label(null, T(captionKey));
            } else {
                label = new Label("");
            }
            label.setContentMode(Label.CONTENT_XHTML);
            itemContainer.addComponent(label);
            itemContainer.setComponentAlignment(label, Alignment.TOP_RIGHT);
            itemContainer.addComponent(component);
            itemContainer.newLine();
        }

    }


}
