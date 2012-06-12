/*
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

package fi.vm.sade.tarjonta.ui.hakuera;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.validation.MLTextSize;
import fi.vm.sade.generic.ui.blackboard.BlackboardContext;
import fi.vm.sade.generic.ui.component.MultiLingualTextField;
import fi.vm.sade.koodisto.model.dto.Kieli;
import fi.vm.sade.koodisto.model.dto.KoodiDTO;
import fi.vm.sade.koodisto.service.KoodiPublicService;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.koodisto.widget.factory.WidgetFactory;
import fi.vm.sade.tarjonta.service.types.dto.HakueraDTO;
import fi.vm.sade.tarjonta.service.types.dto.HakueraSimpleDTO;
import fi.vm.sade.tarjonta.ui.hakuera.event.HakueraSavedEvent;
import fi.vm.sade.tarjonta.ui.service.TarjontaUiService;
import fi.vm.sade.tarjonta.ui.util.I18NHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static fi.vm.sade.generic.common.validation.ValidationConstants.GENERIC_MAX;
import static fi.vm.sade.generic.common.validation.ValidationConstants.GENERIC_MIN;

/**
 * The form for creating and modifying a Hakuera.
 * 
 * @author markus
 *
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = false)
public class HakueraEditForm extends CustomComponent {

    public final static String KOODISTO_HAKUTYYPPI_URI = "http://hakutyyppi";
    public final static String KOODISTO_HAKUKAUSI_URI = "http://hakukausi";
    public final static String KOODISTO_KOULUTUKSEN_ALKAMIS_URI = "http://alkamiskausi";
    public final static String KOODISTO_KOHDEJOUKKO_URI = "http://kohdejoukko";
    public final static String KOODISTO_HAKUTAPA_URI = "http://hakutapa";
    
    private final static String PROPERTY_HAKUTYYPPI = "hakutyyppi";
    private final static String PROPERTY_HAKUKAUSI = "hakukausi";
    private final static String PROPERTY_KOULUTUKSEN_ALKAMINEN = "koulutuksenAlkaminen";
    private final static String PROPERTY_KOHDEJOUKKO = "kohdejoukko";
    private final static String PROPERTY_HAKUTAPA = "hakutapa";
    private final static String PROPERTY_HAKUNIMI = "nimi";
    private final static String PROPERTY_SIJOITTELU = "sijoittelu";
    private final static String PROPERTY_YKSI_HAKU = "yksiHakuaika";
    private final static String PROPERTY_USEITA_HAKUJA = "useitaHakuja";
    private final static String PROPERTY_JARJ_LOMAKE = "jarjLomake";
    private final static String PROPERTY_OMA_LOMAKE = "omaLomake";
    private final static String PROPERTY_HAKUAIKA = "hakuaika";
    
    private static final Logger log = LoggerFactory.getLogger(HakueraEditForm.class);
    
    private static final I18NHelper i18n = new I18NHelper("HakueraEditForm.");
    
    Label lomakeOtsikko;
    
    private KoodistoComponent hakutyyppiKoodi;
    private KoodistoComponent hakukausiKoodi;
    private KoodistoComponent koulutuksenAlkamiskausiKoodi;
    private KoodistoComponent haunKohdejoukkoKoodi;
    private KoodistoComponent hakutapaKoodi;

    @MLTextSize(min = GENERIC_MIN, max = GENERIC_MAX, message = "{validation.Organisaatio.nimiFi}", oneRequired = false)
    private HakueraMlTextField haunNimi;
    
    private OptionGroup hakuaikaOptions;
    private HakuaikaRange haunVoimassaolo;
    
    private CheckBox hakuSijoittelu;
    
    private OptionGroup hakulomakeOptions;
    
    private TextField hakulomakeUrl;
    
    private Button saveButton;
    private Button cancelButton;
    
    HakueraDTO model;

    @Autowired
    protected TarjontaUiService uiService;
    
    private KoodiPublicService koodiService;

    private boolean isNimiEditedByHand = false;
    
    
    public HakueraEditForm() {
        
        HorizontalLayout mainLayout = new HorizontalLayout();
        lomakeOtsikko = new Label(i18n.getMessage("otsikko"));
        mainLayout.addComponent(lomakeOtsikko);
        
        FormLayout leftPanel = new FormLayout();
        createKoodistoComponents(leftPanel);
        haunNimi = new HakueraMlTextField(this);
        haunNimi.setCaption(i18n.getMessage(PROPERTY_HAKUNIMI));
        addNimiListeners();
        leftPanel.addComponent(haunNimi);
        hakuaikaOptions = createOptionGroup(leftPanel, Arrays.asList(new String[]{i18n.getMessage(PROPERTY_YKSI_HAKU), i18n.getMessage(PROPERTY_USEITA_HAKUJA)}), PROPERTY_HAKUAIKA);
        haunVoimassaolo = new HakuaikaRange();
        leftPanel.addComponent(haunVoimassaolo);
        mainLayout.addComponent(leftPanel);
        
        FormLayout rightPanel = new FormLayout();
        hakuSijoittelu = new CheckBox(i18n.getMessage(PROPERTY_SIJOITTELU));
        rightPanel.addComponent(hakuSijoittelu);
        hakulomakeOptions = createOptionGroup(rightPanel, Arrays.asList(new String[]{i18n.getMessage(PROPERTY_JARJ_LOMAKE), i18n.getMessage(PROPERTY_OMA_LOMAKE)}), "lomakeOptions");
        hakulomakeUrl = new TextField();
        rightPanel.addComponent(hakulomakeUrl);
        createButtons(leftPanel);
        
        mainLayout.addComponent(rightPanel);
        
        setCompositionRoot(mainLayout);
    }
    
    private OptionGroup createOptionGroup(FormLayout layout, List<String> options, String captionKey) {
        OptionGroup optGroup = new OptionGroup(i18n.getMessage(captionKey), options);
        optGroup.setMultiSelect(false);
        layout.addComponent(optGroup);
        return optGroup;
    }
    
    /**
     * Creation of save and cancel buttons. Adding them to the layout.
     * 
     * @param layout
     */
    private void createButtons(FormLayout layout) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        saveButton = new Button(I18N.getMessage("tarjonta.tallenna"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                save();
            }
        });
        buttonLayout.addComponent(saveButton);
        cancelButton = new Button(I18N.getMessage("tarjonta.peruuta"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                bind(new HakueraDTO());
            }
        });
        buttonLayout.addComponent(cancelButton);
        layout.addComponent(buttonLayout);
    }
    
    private void createKoodistoComponents(FormLayout layout) {
        hakutyyppiKoodi = createKoodistoComponent(KOODISTO_HAKUTYYPPI_URI, PROPERTY_HAKUTYYPPI, PROPERTY_HAKUTYYPPI, layout);
        hakukausiKoodi = createKoodistoComponent(KOODISTO_HAKUKAUSI_URI, PROPERTY_HAKUKAUSI, PROPERTY_HAKUKAUSI, layout);
        koulutuksenAlkamiskausiKoodi = createKoodistoComponent(KOODISTO_KOULUTUKSEN_ALKAMIS_URI, PROPERTY_KOULUTUKSEN_ALKAMINEN, PROPERTY_KOULUTUKSEN_ALKAMINEN, layout);
        haunKohdejoukkoKoodi = createKoodistoComponent(KOODISTO_KOHDEJOUKKO_URI, PROPERTY_KOHDEJOUKKO, PROPERTY_KOHDEJOUKKO, layout);
        hakutapaKoodi = createKoodistoComponent(KOODISTO_HAKUTAPA_URI, PROPERTY_HAKUTAPA, PROPERTY_HAKUTAPA, layout);
        addListeners(new KoodistoComponent[]{hakutyyppiKoodi, hakukausiKoodi, haunKohdejoukkoKoodi});
        this.koodiService = hakutyyppiKoodi.getKoodiService();
    }
    
   
    
    /**
     * Adding listeners to koodisto components that are used in constructing the 
     * automatically generated localized names (nimiFi, nimiSv, nimiEn) for the Hakuera.
     * 
     * @param koodistoComponents
     */
    private void addListeners(KoodistoComponent[] koodistoComponents) {
        for (KoodistoComponent curComp : koodistoComponents) {
            curComp.addListener(new Property.ValueChangeListener() {
                
                @Override
                public void valueChange(ValueChangeEvent event) {
                    if (!isNimiEditedByHand) {
                        haunNimi.updateNimiField();
                    }
                }
            });
        }
    }
    
    /**
     * 
     * Creationg of a KoodistoComponent. 
     * 
     * @param koodistoUri The URI of the koodisto used in this component.
     * @param captionKey The key of the caption used.
     * @param debugId The debug id of this component
     * @param layout The layout where this component will be added.
     * @return The created KoodistoComponent.
     */
    private KoodistoComponent createKoodistoComponent(String koodistoUri, String captionKey, String debugId, FormLayout layout) {
        KoodistoComponent koodistoComponent = WidgetFactory.create(koodistoUri, Kieli.FI);
        koodistoComponent.setCaption(i18n.getMessage(captionKey));
        ComboBox koodistoCombo = new ComboBox();
        koodistoComponent.setDebugId(i18n.getMessage(debugId));
        koodistoCombo.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        koodistoCombo.setImmediate(true);
        koodistoComponent.setField(koodistoCombo);
        layout.addComponent(koodistoComponent);
        return koodistoComponent;
    }
    
    //Getters for components. Might be used by Selenium tests.
    
    public KoodistoComponent getHakutyyppiKoodi() {
        return hakutyyppiKoodi;
    }

    public KoodistoComponent getHakukausiKoodi() {
        return hakukausiKoodi;
    }

    public KoodistoComponent getKoulutuksenAlkamiskausiKoodi() {
        return koulutuksenAlkamiskausiKoodi;
    }

    public KoodistoComponent getHaunKohdejoukkoKoodi() {
        return haunKohdejoukkoKoodi;
    }

    public KoodistoComponent getHakutapaKoodi() {
        return hakutapaKoodi;
    }

    public MultiLingualTextField getHaunNimi() {
        return haunNimi;
    }

    public OptionGroup getHakuaikaOptions() {
        return hakuaikaOptions;
    }

    public HakuaikaRange getHaunVoimassaolo() {
        return haunVoimassaolo;
    }

    public CheckBox getHakuSijoittelu() {
        return hakuSijoittelu;
    }

    public OptionGroup getHakulomakeOptions() {
        return hakulomakeOptions;
    }

    public TextField getHakulomakeUrl() {
        return hakulomakeUrl;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }
    
    public KoodiPublicService getKoodiService() {
        return koodiService;
    }
    
    public HakueraDTO getModel() {
        return model;
    }

    /**
     * Binding the form fields to the model (HakueraDTO).
     * 
     * @param model
     */
    public void bind(HakueraDTO model) {
        this.model = model;
        isNimiEditedByHand = !haunNimi.nimiMatchesKoodistoFields();
        haunNimi.getTextFi().setPropertyDataSource(new NestedMethodProperty(model, "nimiFi"));
        haunNimi.getTextSv().setPropertyDataSource(new NestedMethodProperty(model, "nimiSv"));
        haunNimi.getTextEn().setPropertyDataSource(new NestedMethodProperty(model, "nimiEn"));
        hakutyyppiKoodi.setPropertyDataSource(new NestedMethodProperty(model, PROPERTY_HAKUTYYPPI));
        hakukausiKoodi.setPropertyDataSource(new NestedMethodProperty(model, PROPERTY_HAKUKAUSI));
        koulutuksenAlkamiskausiKoodi.setPropertyDataSource(new NestedMethodProperty(model, PROPERTY_KOULUTUKSEN_ALKAMINEN));
        haunKohdejoukkoKoodi.setPropertyDataSource(new NestedMethodProperty(model, PROPERTY_KOHDEJOUKKO));
        hakutapaKoodi.setPropertyDataSource(new NestedMethodProperty(model, PROPERTY_HAKUTAPA));
        haunVoimassaolo.populateVoimassaoloDates(model);
        hakuSijoittelu.setPropertyDataSource(new NestedMethodProperty(model, PROPERTY_SIJOITTELU));
        if (model.getLomake() != null) {
            hakulomakeOptions.setValue(i18n.getMessage(PROPERTY_OMA_LOMAKE));
        } else {
            hakulomakeOptions.setValue(i18n.getMessage(PROPERTY_JARJ_LOMAKE));
        }
        hakulomakeUrl.setPropertyDataSource(new NestedMethodProperty(model, "lomake"));
    }
    
    

    /**
     * Populating the HakueraEditForm according to the current selection in the HakueraList.
     * 
     * @param value
     */
    public void populate(HakueraList.HakueraSimple value) {
        HakueraSimpleDTO curHakuera = value.getDto();
        if (curHakuera instanceof HakueraDTO) {
            bind((HakueraDTO)curHakuera);
        } else {
            HakueraDTO newModel = new HakueraDTO();
            newModel.setNimiFi(curHakuera.getNimiFi());
            newModel.setNimiSv(curHakuera.getNimiSv());
            newModel.setNimiEn(curHakuera.getNimiEn());
            newModel.setOid(curHakuera.getOid());
            bind(newModel);
        }
    }
    
    /**
     * Saving the form. If the Hakuera is already in the database (has oid), update is called.
     */
    private void save() {
        haunVoimassaolo.getVoimassaoloDates(model);
        if (model.getOid() == null) {
            bind(uiService.createHakuera(model));
        } else {
            bind(uiService.updateHakuera(model));
        }
        BlackboardContext.getBlackboard().fire(new HakueraSavedEvent(model));
        getWindow().showNotification(I18N.getMessage("save.success"));
    }
    
    private void addNimiListeners() {
        haunNimi.getTextFi().addListener(new FieldEvents.TextChangeListener() {
            public void textChange(final FieldEvents.TextChangeEvent event) {
                isNimiEditedByHand = true;
            }
        });
        haunNimi.getTextSv().addListener(new FieldEvents.TextChangeListener() {
            public void textChange(final FieldEvents.TextChangeEvent event) {
                isNimiEditedByHand = true;
            }
        });
        haunNimi.getTextEn().addListener(new FieldEvents.TextChangeListener() {
            public void textChange(final FieldEvents.TextChangeEvent event) {
                isNimiEditedByHand = true;
            }
        });
    }
}
