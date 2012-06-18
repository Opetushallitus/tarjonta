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
package fi.vm.sade.tarjonta.ui.koulutusmoduuli.tutkintoohjelma;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.blackboard.BlackboardContext;
import fi.vm.sade.koodisto.service.mock.MockDataHandler;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliSummaryDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTila;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.AbstractKoulutusmoduuliEditPanel;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.AbstractKoulutusmoduuliFormModel;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.event.KoulutusmoduuliChangedEvent;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.event.KoulutusmoduuliChangedEvent.KoulutusmoduuliChangedEventListener;
import fi.vm.sade.tarjonta.ui.util.I18NHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Jukka Raanamo
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class TutkintoOhjelmaEditPanel extends AbstractKoulutusmoduuliEditPanel<TutkintoOhjelmaDTO> {

    private static final long serialVersionUID = -4038416408035942931L;

    private static final int NUM_RELATED_ITEMS_TO_DISPLAY = 5;

    private static final String KOODISTO_URI_KOULUTUKSET = MockDataHandler.KOULUTUS_URI;

    private static final String PROPERTY_KOULUTUS_KOODI = "koulutusKoodiUri";

    private static final String PROPERTY_ORGANISAATIO = "organisaatioOid";

    private static final Logger log = LoggerFactory.getLogger(TutkintoOhjelmaEditPanel.class);

    private Label moduuliTitleLabel;

    private Label moduuliStatusLabel;

    @PropertyId(PROPERTY_ORGANISAATIO)
    private TextField organisaatioField;

    @PropertyId(PROPERTY_KOULUTUS_KOODI)
    private KoodistoComponent koulutusKoodi;

    private Table childrenTable;

    private Table parentsTable;

    private KoulutusPanel koulutusPanel;

    private static final I18NHelper i18n = new I18NHelper("TutkintoOhjelmaEditForm.");

    private KoulutusmoduuliChangedEventListener saveHandler = new KoulutusmoduuliChangedEventListener() {

        @Override
        public void onKoulutusmoduuliChanged(KoulutusmoduuliChangedEvent event) {
            // todo: fix me: triggers event on status label to force updating changed value
            // from backing bean (updated timestamp). not the ideal way but did not know what 
            // is the right practise. this would not be an issue if the form is removed after save
            // and something else is diplayed
            moduuliStatusLabel.valueChange(new Label().new ValueChangeEvent(moduuliStatusLabel));
        }

    };

    private Property.ValueChangeListener koulutusSelectedListener = new Property.ValueChangeListener() {

        private static final long serialVersionUID = -382717228031608542L;

        @Override
        public void valueChange(final ValueChangeEvent event) {

            handleKoulutusChangedEvent(event);

        }

    };

    public TutkintoOhjelmaEditPanel() {

        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);

        moduuliTitleLabel = new Label("title");
        moduuliStatusLabel = new Label();
        koulutusPanel = new KoulutusPanel();

        organisaatioField = createOrganisaatioField();
        organisaatioField.addListener(moduuliTitleLabel);

        koulutusKoodi = createKoulutusAutocompleteField();
        koulutusKoodi.addListener(moduuliTitleLabel);

        mainLayout.addComponent(moduuliTitleLabel);
        mainLayout.addComponent(moduuliStatusLabel);

        FormLayout formLayout = new FormLayout();
        formLayout.addComponent(organisaatioField);
        formLayout.addComponent(koulutusKoodi);

        VerticalLayout fieldsAndKoodisto = new VerticalLayout();

        fieldsAndKoodisto.addComponent(formLayout);
        fieldsAndKoodisto.addComponent(koulutusPanel);

        HorizontalLayout leftAndRight = new HorizontalLayout();
        leftAndRight.addComponent(fieldsAndKoodisto);
        leftAndRight.addComponent(createNavigations());

        mainLayout.addComponent(leftAndRight);
        //mainLayout.addComponent(createMultilingualEditors());

        setCompositionRoot(mainLayout);

        BlackboardContext.getBlackboard().addListener(saveHandler);
    }

    public KoodistoComponent getKoulutusComponent() {
        return koulutusKoodi;
    }

    /**
     * Returns text field to display organisaatio. This has been changed to read-only for now since it is assumed that organisaatio has been selected prior to
     * opening this panel.
     *
     * @return
     */
    private TextField createOrganisaatioField() {

        final TextField field = new TextField(i18n.getMessage("organisaatioLabel"));
        field.setNullRepresentation("");
        field.setImmediate(true);
        field.setRequired(true);
        field.setReadOnly(true);
        field.setRequiredError(i18n.getMessage("organisaatioIsRequired"));

        return field;

    }

    @Override
    public BeanItem<? extends AbstractKoulutusmoduuliFormModel<TutkintoOhjelmaDTO>> createBeanItem(TutkintoOhjelmaDTO dto) {

        final TutkintoOhjelmaFormModel model = new TutkintoOhjelmaFormModel(dto);
        final BeanItem<TutkintoOhjelmaFormModel> beanItem = new BeanItem<TutkintoOhjelmaFormModel>(model);

        // this is not getting set via bean item??
        moduuliStatusLabel.setPropertyDataSource(new KoulutusmoduuliStatusProperty(dto));
        moduuliTitleLabel.setPropertyDataSource(new KoulutusmoduuliTitleProperty());

        // todo: add bean properties to populate
        final String[] properties = {
            PROPERTY_ORGANISAATIO,
            PROPERTY_KOULUTUS_KOODI
        };

        for (String property : properties) {
            beanItem.addItemProperty(property, new NestedMethodProperty(model, property));
        }

        // todo: i'd rather see this being trigged by an event
        populateKoulutusmoduuliChildren(dto.getOid());
        populateKoulutusmoduuliParents(dto.getOid());

        return beanItem;
    }

    private Component createKoodistoPanel() {


        return new KoulutusPanel();

    }

    /**
     * Triggered when user selects a new koulutus from autocomplete. Fires loading koodisto data from server.
     *
     * @param event
     */
    private void handleKoulutusChangedEvent(ValueChangeEvent event) {

        koulutusPanel.update((String) event.getProperty().getValue());

    }

    private KoodistoComponent createKoulutusAutocompleteField() {

        final ComboBox combo = new ComboBox();
        combo.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
        combo.setImmediate(true);
        combo.setDebugId(PROPERTY_KOULUTUS_KOODI);

        KoodistoComponent wrapper = new KoodistoComponent(KOODISTO_URI_KOULUTUKSET);
        wrapper.setField(combo);
        wrapper.setRequired(true);
        wrapper.setRequiredError(i18n.getMessage("koulutusIsRequired"));
        wrapper.setCaption(i18n.getMessage("koulutusLabel"));
        wrapper.addListener(koulutusSelectedListener);
        

        return wrapper;

    }

    private Component createMultilingualEditors() {

        TabSheet tabs = new TabSheet();

        Locale[] locales = new Locale[] {
            new Locale("fi"), new Locale("sv"), new Locale("en")
        };

        for (Locale locale : locales) {
            VerticalLayout vl = new VerticalLayout();
            vl.setSpacing(true);
            vl.setCaption(locale.getDisplayLanguage());

            HorizontalLayout hl = new HorizontalLayout();
            vl.addComponent(hl);

            hl.addComponent(new RichTextArea(i18n.getMessage("tutkinnonRakenne")));

            VerticalLayout vl2 = new VerticalLayout();
            vl2.setSpacing(true);
            hl.addComponent(vl2);

            ExternalResource imageResource = new ExternalResource("http://myy.helia.fi/~heita/funktrak.gif");
            final Embedded embedded = new Embedded("", imageResource);
            embedded.setWidth("150px");

            Upload uploadImage = new Upload();
            uploadImage.setCaption(i18n.getMessage("lataaTutkinnonRakenneKuva"));

            vl2.addComponent(uploadImage);
            vl2.addComponent(embedded);

            vl.addComponent(new RichTextArea(i18n.getMessage("koulutuksellisetTavoitteet")));
            vl.addComponent(new RichTextArea(i18n.getMessage("sijoittuminenTyoelamaan")));

            vl.addComponent(new Label("Tehtävänimikkeet placeholder"));
            vl.addComponent(new TextField());

            vl.addComponent(new RichTextArea(i18n.getMessage("koulutuksenSisalto")));
            vl.addComponent(new RichTextArea(i18n.getMessage("jatkoOpinnot")));

            tabs.addTab(vl, locale.getDisplayLanguage(I18N.getLocale()));
            tabs.getTab(vl).setClosable(true);
        }

        final Label addNewLanguage = new Label(i18n.getMessage("lisaaUusiKielisyys"));
        tabs.addTab(addNewLanguage);

        tabs.addListener(new TabSheet.SelectedTabChangeListener() {

            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                if (event.getTabSheet().getSelectedTab() == addNewLanguage) {
                    getWindow().showNotification("TODO new language tab selected...");
                }
            }

        });

        return tabs;
    }

    private Component createNavigations() {

        final VerticalLayout layout = new VerticalLayout();

        final Panel parentsContainer = new Panel(i18n.getMessage("sisaltyyModuleihin"));
        final Panel childrenContainer = new Panel(i18n.getMessage("sisaltyvatModuulit"));

        childrenTable = new Table();
        childrenTable.addContainerProperty(i18n.getMessage("sisaltyyModuleihin.col1"), String.class, null);
        childrenTable.setPageLength(NUM_RELATED_ITEMS_TO_DISPLAY);
        childrenContainer.addComponent(childrenTable);

        parentsTable = new Table();
        parentsTable.addContainerProperty(i18n.getMessage("sisaltyvatModuulit.col1"), String.class, null);
        parentsTable.setPageLength(NUM_RELATED_ITEMS_TO_DISPLAY);
        parentsContainer.addComponent(parentsTable);

        layout.addComponent(parentsContainer);
        layout.addComponent(childrenContainer);

        return layout;

    }

    private void populateKoulutusmoduuliChildren(String koulutusmoduuliOID) {

        childrenTable.removeAllItems();

        List<KoulutusmoduuliSummaryDTO> children = uiService.getChildModuulis(koulutusmoduuliOID);
        for (KoulutusmoduuliSummaryDTO child : children) {
            final String[] rowData = new String[] {child.getNimi()};
            childrenTable.addItem(rowData, child);
        }

    }

    private void populateKoulutusmoduuliParents(String koulutusmoduuliOid) {

        parentsTable.removeAllItems();
        List<KoulutusmoduuliSummaryDTO> parents = uiService.getParentModuulis(koulutusmoduuliOid);
        for (KoulutusmoduuliSummaryDTO parent : parents) {
            final String[] rowData = new String[] {parent.getNimi()};
            parentsTable.addItem(rowData, parent);
        }

    }

    /**
     * Helper class that generates value for Koulutusmoduuli's status by concatenating status and updated 
     * timestamp if any.
     */
    private class KoulutusmoduuliStatusProperty extends AbstractProperty {

        private static final long serialVersionUID = -8671743512655403988L;

        private KoulutusmoduuliDTO koulutusmoduuli;

        public KoulutusmoduuliStatusProperty(KoulutusmoduuliDTO koulutusmoduuli) {
            this.koulutusmoduuli = koulutusmoduuli;
        }

        @Override
        public Class<?> getType() {
            return String.class;
        }

        @Override
        public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
            // read-only
        }

        @Override
        public Object getValue() {

            Date updated = koulutusmoduuli.getUpdated();
            if (updated == null) {
                return I18N.getMessage("TutkintoOhjelmaFormModel.organisaatioStatus.notSaved");
            } else {
                if (KoulutusmoduuliTila.SUUNNITTELUSSA.name().equals(koulutusmoduuli.getTila())) {
                    return I18N.getMessage("TutkintoOhjelmaFormModel.organisaatioStatus.savedLuonnos", updated);
                } else {
                    return I18N.getMessage("TutkintoOhjelmaFormModel.organisaatioStatus.savedValmis", updated);
                }

            }

        }

    }


    /**
     * Helper class that generates the Koulutusmoduuli's title by concatenating owner organisaatio nimi with 
     * koulutus name if any.
     */
    private class KoulutusmoduuliTitleProperty extends AbstractProperty {

        private static final long serialVersionUID = -3220959237478842249L;

        @Override
        public Class<?> getType() {
            return String.class;
        }

        @Override
        public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
            // read-only
        }

        @Override
        public Object getValue() {

            String organisaatioNimi = (String) organisaatioField.getValue();
            if (organisaatioNimi == null) {
                return i18n.getMessage("organisaatioLabel.emptyValue");
            } else {

                final ComboBox combo = (ComboBox) koulutusKoodi.getField();
                final String koulutusName = combo.getItemCaption(combo.getValue());

                if (StringUtils.isNotEmpty(koulutusName)) {
                    return organisaatioNimi + ", " + koulutusName;
                } else {
                    return organisaatioNimi;
                }
            }

        }

    }


    /**
     * Static fields for demonstration only.
     */
    private class KoulutusPanel extends CustomComponent {

        private static final long serialVersionUID = -5449743469080570194L;

        private Label koulutusala = new Label();

        private Label opintoala = new Label();

        private Label tutkinnonNimi = new Label();

        private Label tutkintoNimike = new Label();

        private Label laajuusYksikko = new Label();

        private Label laajuus = new Label();

        private Label koulutuskoodi = new Label();

        private AtomicInteger counter = new AtomicInteger();

        public KoulutusPanel() {
            GridLayout grid = new GridLayout(2, 7);
            grid.setSpacing(true);
            addWithCaption(grid, "Koulutusala:", koulutusala);
            addWithCaption(grid, "Opintoala:", opintoala);
            addWithCaption(grid, "Tutkinnon nimi:", tutkinnonNimi);
            addWithCaption(grid, "Tutkintonimike:", tutkintoNimike);
            addWithCaption(grid, "Opintojen laajuusyksikko:", laajuusYksikko);
            addWithCaption(grid, "Opintojen laajuus:", laajuus);
            addWithCaption(grid, "Koulutuskoodi:", koulutuskoodi);
            setCompositionRoot(grid);
        }

        private void addWithCaption(GridLayout grid, String caption, Label label) {
            Label captionLabel = new Label(caption);
            grid.addComponent(captionLabel);
            grid.setComponentAlignment(captionLabel, Alignment.MIDDLE_RIGHT);
            grid.addComponent(label);
            label.setImmediate(true);
        }

        public void update(String koulutusUri) {
            final String i = "(" + counter.incrementAndGet() + ") ";
            koulutusala.setValue(i + "Luonnontieteiden ala");
            opintoala.setValue(i + "Tietojenkasittely");
            tutkinnonNimi.setValue(i + "Liiketalouden ammattikorkeatutkinto");
            tutkintoNimike.setValue(i + "Tradenomi");
            laajuusYksikko.setValue(i + "Opintopisteet");
            laajuus.setValue(i + "210 op");
            koulutuskoodi.setValue(i + "123456");
        }

    }


}

