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

package fi.vm.sade.tarjonta.ui.haku;

import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.component.MultiLingualTextField;
import fi.vm.sade.koodisto.model.dto.Kieli;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.koodisto.widget.factory.WidgetFactory;
import fi.vm.sade.tarjonta.service.types.dto.HakueraSimpleDTO;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraList;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;

import java.util.Arrays;
import java.util.List;

/**
 * The form for creating and modifying a Haku (Hakuerä).
 * 
 * @author markus
 *
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = false)
public class HakuEditForm extends CustomComponent {


    private final static String KOODISTO_HAKUTYYPPI_URI = "http://oppilaitostyyppi";
    private final static String KOODISTO_HAKUKAUSI_URI = "http://oppilaitostyyppi";
    private final static String KOODISTO_KOULUTUKSEN_ALKAMIS_URI = "http://oppilaitostyyppi";
    private final static String KOODISTO_KOHDEJOUKKO_URI = "http://oppilaitostyyppi";
    private final static String KOODISTO_HAKUTAPA_URI = "http://oppilaitostyyppi";
    
    private final static String PROPERTY_HAKUTYYPPI = "haku.hakutyyppi";
    private final static String PROPERTY_HAKUKAUSI = "haku.hakukausi";
    private final static String PROPERTY_KOULUTUKSEN_ALKAMINEN = "haku.koulutuksenAlkaminen";
    private final static String PROPERTY_KOHDEJOUKKO = "haku.kohdejoukko";
    private final static String PROPERTY_HAKUTAPA = "haku.hakutapa";
    private final static String PROPERTY_HAKUNIMI = "haku.nimi";
    private final static String PROPERTY_SIJOITTELU = "haku.sijoittelu";
    private final static String PROPERTY_YKSI_HAKU = "haku.yksiHakuaika";
    private final static String PROPERTY_USEITA_HAKUJA = "haku.useitaHakuja";
    private final static String PROPERTY_JARJ_LOMAKE = "haku.jarjLomake";
    private final static String PROPERTY_OMA_LOMAKE = "haku.omaLomake";
    private final static String PROPERTY_HAKUAIKA = "haku.hakuaika";
    
    Label lomakeOtsikko;
    
    private KoodistoComponent hakutyyppiKoodi;
    private KoodistoComponent hakukausiKoodi;
    private KoodistoComponent koulutuksenAlkamiskausiKoodi;
    private KoodistoComponent haunKohdejoukkoKoodi;
    private KoodistoComponent hakutapaKoodi;
    
    private MultiLingualTextField haunNimi;
    
    private OptionGroup hakuaikaOptions;
    private HakuaikaRange haunVoimassaolo;
    
    private CheckBox hakuSijoittelu;
    
    private OptionGroup hakulomakeOptions;
    
    private TextField hakulomakeUrl;
    
    private Button saveButton;
    private Button cancelButton;
    
    
    public HakuEditForm() {
        
        HorizontalLayout mainLayout = new HorizontalLayout();
        lomakeOtsikko = new Label(I18N.getMessage("haku.otsikko"));
        mainLayout.addComponent(lomakeOtsikko);
        
        FormLayout leftPanel = new FormLayout();
        hakutyyppiKoodi = createKoodistoComponent(KOODISTO_HAKUTYYPPI_URI, PROPERTY_HAKUTYYPPI, PROPERTY_HAKUTYYPPI, leftPanel);
        hakukausiKoodi = createKoodistoComponent(KOODISTO_HAKUKAUSI_URI, PROPERTY_HAKUKAUSI, PROPERTY_HAKUKAUSI, leftPanel);
        koulutuksenAlkamiskausiKoodi = createKoodistoComponent(KOODISTO_KOULUTUKSEN_ALKAMIS_URI, PROPERTY_KOULUTUKSEN_ALKAMINEN, PROPERTY_KOULUTUKSEN_ALKAMINEN, leftPanel);
        haunKohdejoukkoKoodi = createKoodistoComponent(KOODISTO_KOHDEJOUKKO_URI, PROPERTY_KOHDEJOUKKO, PROPERTY_KOHDEJOUKKO, leftPanel);
        hakutapaKoodi = createKoodistoComponent(KOODISTO_HAKUTAPA_URI, PROPERTY_HAKUTAPA, PROPERTY_HAKUTAPA, leftPanel);
        haunNimi = new MultiLingualTextField();
        haunNimi.setCaption(I18N.getMessage(PROPERTY_HAKUNIMI));
        leftPanel.addComponent(haunNimi);
        hakuaikaOptions = createOptionGroup(leftPanel, Arrays.asList(new String[]{I18N.getMessage(PROPERTY_YKSI_HAKU), I18N.getMessage(PROPERTY_USEITA_HAKUJA)}), PROPERTY_HAKUAIKA);
        haunVoimassaolo = new HakuaikaRange();
        leftPanel.addComponent(haunVoimassaolo);
        mainLayout.addComponent(leftPanel);
        
        FormLayout rightPanel = new FormLayout();
        hakuSijoittelu = new CheckBox(I18N.getMessage(PROPERTY_SIJOITTELU));
        rightPanel.addComponent(hakuSijoittelu);
        hakulomakeOptions = createOptionGroup(rightPanel, Arrays.asList(new String[]{I18N.getMessage(PROPERTY_JARJ_LOMAKE), I18N.getMessage(PROPERTY_OMA_LOMAKE)}), "");
        hakulomakeUrl = new TextField();
        rightPanel.addComponent(hakulomakeUrl);
        
        mainLayout.addComponent(rightPanel);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        saveButton = new Button(I18N.getMessage("tarjonta.tallenna"));
        buttonLayout.addComponent(saveButton);
        cancelButton = new Button(I18N.getMessage("tarjonta.peruuta"));
        buttonLayout.addComponent(cancelButton);
        mainLayout.addComponent(buttonLayout);
        setCompositionRoot(mainLayout);
    }
    
    private OptionGroup createOptionGroup(FormLayout layout, List<String> options, String captionKey) {
        OptionGroup optGroup = new OptionGroup(I18N.getMessage(captionKey), options);
        optGroup.setMultiSelect(false);
        layout.addComponent(optGroup);
        return optGroup;
    }
    
    private KoodistoComponent createKoodistoComponent(String koodistoUri, String captionKey, String debugId, FormLayout layout) {
        KoodistoComponent koodistoComponent = WidgetFactory.create(koodistoUri, Kieli.FI);
        koodistoComponent.setCaption(I18N.getMessage(captionKey));
        ComboBox koodistoCombo = new ComboBox();
        koodistoCombo.setDebugId(I18N.getMessage(debugId));
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

    public void populate(HakueraList.HakueraSimple value) {
        // TODO: bindaukset
        haunNimi.getTextFi().setValue(value.getNimi());
    }
}
