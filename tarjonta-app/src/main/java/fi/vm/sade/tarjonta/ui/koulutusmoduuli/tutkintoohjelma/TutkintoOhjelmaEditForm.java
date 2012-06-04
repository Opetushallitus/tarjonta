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

import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.model.dto.*;
import fi.vm.sade.tarjonta.ui.TarjontaApplication;
import fi.vm.sade.tarjonta.ui.event.KoulutusmoduuliChangedEvent;
import fi.vm.sade.tarjonta.ui.event.KoulutusmoduuliChangedEvent.KoulutusmoduuliChangedEventListener;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.AbstractKoulutusmoduuliEditForm;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.AbstractKoulutusmoduuliFormModel;
import fi.vm.sade.tarjonta.ui.util.I18NHelper;
import fi.vm.sade.tarjonta.ui.util.VaadinUtils;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import java.util.Locale;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 *
 * @author Jukka Raanamo
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class TutkintoOhjelmaEditForm extends AbstractKoulutusmoduuliEditForm<TutkintoOhjelmaDTO> {

    private static final long serialVersionUID = -4038416408035942931L;
    
    private static final int NUM_RELATED_ITEMS_TO_DISPLAY = 5;

    private Label moduuliTitleLabel;

    private Label moduuliStatusLabel;

    @PropertyId("organisaatioOid")
    private TextField organisaatioField;

    @PropertyId("todo_koulutus")
    private TextField koulutusField;
    
    private Table childrenTable;
    
    private Table parentsTable;

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

    public TutkintoOhjelmaEditForm() {

        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);

        final GridLayout grid = new GridLayout(2, 2);
        grid.setSpacing(true);

        organisaatioField = VaadinUtils.newTextField();
        koulutusField = VaadinUtils.newTextField();

        moduuliTitleLabel = new Label("title");
        moduuliStatusLabel = new Label();

        mainLayout.addComponent(moduuliTitleLabel);
        mainLayout.addComponent(moduuliStatusLabel);

        organisaatioField.addListener(moduuliTitleLabel);
        koulutusField.addListener(moduuliTitleLabel);
        organisaatioField.setImmediate(true);
        koulutusField.setImmediate(true);

        addFieldWithLabel(grid, new Label(i18n.getMessage("organisaatioLabel")), organisaatioField);
        addFieldWithLabel(grid, new Label(i18n.getMessage("koulutusLabel")), koulutusField);

        VerticalLayout vertical = new VerticalLayout();
        vertical.addComponent(grid);
        vertical.addComponent(createKoodistoPanel());

        mainLayout.addComponent(VaadinUtils.newTwoColumnHorizontalLayout(vertical, createNavigations()));
        
        //
        // commented out since not included in spring 2
        //
        //mainLayout.addComponent(createMultilingualEditors());

        TarjontaApplication.getBlackboard().addListener(saveHandler);

        setCompositionRoot(mainLayout);
    }

    @Override
    public BeanItem<? extends AbstractKoulutusmoduuliFormModel<TutkintoOhjelmaDTO>> createBeanItem(TutkintoOhjelmaDTO dto) {

        final TutkintoOhjelmaFormModel model = new TutkintoOhjelmaFormModel(dto);
        final BeanItem<TutkintoOhjelmaFormModel> beanItem = new BeanItem<TutkintoOhjelmaFormModel>(model);

        // this is not getting set via bean item??
        moduuliStatusLabel.setPropertyDataSource(new OrganisaatioStatusProperty(dto));
        moduuliTitleLabel.setPropertyDataSource(new OrganisaatioLabelProperty());

        // todo: add bean properties to populate
        final String[] properties = {
            "organisaatioOid"
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

        return new Panel("Koodisto field");

    }

    private void addFieldWithLabel(GridLayout grid, Label label, Component content) {

        grid.addComponent(label);
        grid.addComponent(content);
        grid.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);

    }

    private Component createMultilingualEditors() {
        // this is a placeholder, create actual editor component here

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
        layout.setSpacing(true);
        layout.setWidth(100, UNITS_PERCENTAGE);

        // replace with actual panels
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

        parentsContainer.setWidth(100, UNITS_PERCENTAGE);
        childrenContainer.setWidth(100, UNITS_PERCENTAGE);

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

    private class OrganisaatioStatusProperty extends AbstractProperty {

        private static final long serialVersionUID = -8671743512655403988L;

        private KoulutusmoduuliDTO koulutusmoduuli;

        public OrganisaatioStatusProperty(KoulutusmoduuliDTO koulutusmoduuli) {
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
                return I18N.getMessage("TutkintoOhjelmaFormModel.organisaatioStatus.savedLuonnos", updated);
            }

        }

    }


    private class OrganisaatioLabelProperty extends AbstractProperty {

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
                String koulutus = (String) koulutusField.getValue();
                if (StringUtils.isNotEmpty(koulutus)) {
                    return organisaatioNimi + ", " + koulutus;
                } else {
                    return organisaatioNimi;
                }
            }

        }

    }


}

