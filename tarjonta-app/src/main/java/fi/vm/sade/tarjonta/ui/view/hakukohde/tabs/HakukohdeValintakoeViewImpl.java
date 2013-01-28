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

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeAikaViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Tuomas Katva
 * Date: 23.1.2013
 */

@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class HakukohdeValintakoeViewImpl extends CustomComponent {

    private ErrorMessage errorView;

    @Autowired
    private TarjontaUIHelper tarjontaUIHelper;

    @Autowired(required = true)
    private transient UiBuilder uiBuilder;

    @Autowired
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

    private String languageTabsheetWidth = "500px";
    private String languageTabsheetHeight = "250px";

    private HakukohdeValintaKoeAikaEditView valintaKoeAikaEditView;
    private Form valintaKoeAikaForm;

    public HakukohdeValintakoeViewImpl() {
        super();
        buildMainLayout();
    }

    private void initForm() {
        BeanItem<ValintakoeViewModel> valintakoeViewModel = new BeanItem<ValintakoeViewModel>(presenter.getSelectedValintakoe());
        form = new ValidatingViewBoundForm(this);
        form.setItemDataSource(valintakoeViewModel);

        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
        this.form.setValidationVisible(false);
        this.form.setValidationVisibleOnCommit(false);
    }

    private VerticalLayout buildOsoiteEditLayout() {
        aikaInputFormLayout = new VerticalLayout();

        valintaKoeAikaEditView = new HakukohdeValintaKoeAikaEditView();

        BeanItem<ValintakoeAikaViewModel> valintakoeAikaViewModelBean = new BeanItem<ValintakoeAikaViewModel>(presenter.getModel().getSelectedValintakoeAika());
        valintaKoeAikaForm = new ValidatingViewBoundForm(valintaKoeAikaEditView);
        valintaKoeAikaForm.setItemDataSource(valintakoeAikaViewModelBean);

        JSR303FieldValidator.addValidatorsBasedOnAnnotations(valintaKoeAikaForm);

        aikaInputFormLayout.addComponent(valintaKoeAikaEditView);

        valintaKoeAikaEditView.addClickListenerToLisaaButton(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                valintaKoeAikaForm.commit();
                if (valintaKoeAikaForm.isValid()) {

                    presenter.getModel().getSelectedValintaKoe().getValintakoeAjat().add(presenter.getSelectedAikaView());
                    createNewModelToValintakoeAika();

                    loadTableData();
                }

            }
            });

        return aikaInputFormLayout;
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

        mainLayout.addComponent(buildErrorLayout());

        mainLayout.addComponent(buildInfoButtonLayout());

        mainLayout.addComponent(buildGridLayout());

        mainLayout.addComponent(buildSaveCancelButtonLayout());

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

    private GridLayout buildGridLayout() {
        itemContainer =  new GridLayout(2,1);
        itemContainer.setWidth(UiConstant.PCT100);
        itemContainer.setSpacing(true);
        itemContainer.setMargin(false, true, true, true);

        addItemToGrid("HakukohdeValintakoeViewImpl.valintakokeenTyyppi",buildValintakokeenTyyppi());
        addItemToGrid("HakukohdeValintakoeViewImpl.kuvaus",buildValintakoeKuvausTabSheet());
        addItemToGrid("HakukohdeValintakoeViewImpl.valintakokeenSijainti",buildOsoiteEditLayout());
//        addItemToGrid("HakukohdeValintakoeViewImpl.valintakokeenAjankohta",buildValintakoeAikaLayout());
        addItemToGrid("HakukohdeValintakoeViewImpl.tableHdr",buildValintakoeAikaTableLayout());

        itemContainer.setColumnExpandRatio(0, 0f);
        itemContainer.setColumnExpandRatio(1, 1f);
        return itemContainer;

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
        }  else {
            valintakoeAikasTable = new Table();
        }

        valintakoeAikasTable.setContainerDataSource(createTableContainer(presenter.getModel().getSelectedValintaKoe().getValintakoeAjat()));
        valintakoeAikasTable.setVisibleColumns(new String[]{"sijainti", "ajankohta", "lisatietoja", "poistaBtn"});
        valintakoeAikasTable.setColumnHeader("sijainti", T("HakukohdeValintakoeViewImpl.tableSijainti"));
        valintakoeAikasTable.setColumnHeader("ajankohta",T("HakukohdeValintakoeViewImpl.tableAjankohta"));

        valintakoeAikasTable.setColumnHeader("lisatietoja",T("HakukohdeValintakoeViewImpl.tableLisatietoja"));

        valintakoeAikasTable.setColumnHeader("poistaBtn","");

//        valintakoeAikasTable.setSizeFull();
        valintakoeAikasTable.setHeight("200px");
        valintakoeAikasTable.setWidth("750px");
        valintakoeAikasTable.requestRepaint();


    }

    private BeanContainer<String,HakukohdeValintakoeAikaRow> createTableContainer(List<ValintakoeAikaViewModel> aikas) {
        BeanContainer<String,HakukohdeValintakoeAikaRow> aikasContainer = new BeanContainer<String, HakukohdeValintakoeAikaRow>(HakukohdeValintakoeAikaRow.class);

        for (ValintakoeAikaViewModel aika:aikas) {
            HakukohdeValintakoeAikaRow aikaRow = new HakukohdeValintakoeAikaRow(aika);
            aikaRow.setParent(HakukohdeValintakoeViewImpl.this);
            aikasContainer.addItem(aika.getOsoiteRivi() + aika.getValintakoeAikaTiedot(),aikaRow);

        }

        return aikasContainer;
    }


    private KoodistoComponent buildValintakokeenTyyppi() {
        valintakoeTyyppi = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_LIITTEEN_TYYPPI_URI);

        return valintakoeTyyppi;
    }





    private ValintakoeKuvausTabSheet buildValintakoeKuvausTabSheet() {
        valintaKoeKuvaus = new ValintakoeKuvausTabSheet(true,languageTabsheetWidth,languageTabsheetHeight);

        valintaKoeKuvaus.setSizeUndefined();
        return valintaKoeKuvaus;

    }

    public List<KielikaannosViewModel> getValintakokeenKuvaukset() {
        if (valintaKoeKuvaus != null) {
            return valintaKoeKuvaus.getKieliKaannokset();
        }
        else {
            return new ArrayList<KielikaannosViewModel>();
        }
    }

    private HorizontalLayout buildSaveCancelButtonLayout() {

        HorizontalLayout horizontalButtonLayout = UiUtil.horizontalLayout();

        cancelButton = UiBuilder.button(null, T("HakukohdeValintakoeViewImpl.cancelBtn"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                  presenter.closeValintakoeEditWindow();
            }
        });
        horizontalButtonLayout.addComponent(cancelButton);

        saveButton = UiBuilder.button(null,T("HakukohdeValintakoeViewImpl.saveBtn"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                 errorView.resetErrors();
                try {
                 form.commit();
                if (form.isValid()) {
                 presenter.saveHakukohdeValintakoe(getValintakokeenKuvaukset());
                }
                } catch (Validator.InvalidValueException e) {
                    errorView.addError(e);
                }  catch (Exception exp) {

                }
            }
        });
        horizontalButtonLayout.addComponent(saveButton);
        horizontalButtonLayout.setWidth(UiConstant.PCT100);
        horizontalButtonLayout.setComponentAlignment(cancelButton,Alignment.BOTTOM_LEFT);
        horizontalButtonLayout.setComponentAlignment(saveButton,Alignment.BOTTOM_RIGHT);

        return horizontalButtonLayout;
    }


    private void addItemToGrid(String captionKey, AbstractComponent component) {

        if (itemContainer != null) {
            itemContainer.addComponent(UiUtil.label(null, T(captionKey)));
            itemContainer.addComponent(component);
            itemContainer.newLine();
        }

    }
}
