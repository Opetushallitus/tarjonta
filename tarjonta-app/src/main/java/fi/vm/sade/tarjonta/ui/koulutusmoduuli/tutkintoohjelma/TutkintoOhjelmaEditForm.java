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

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.*;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.AbstractKoulutusmoduuliEditForm;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.AbstractKoulutusmoduuliFormModel;
import fi.vm.sade.tarjonta.ui.util.I18NHelper;
import fi.vm.sade.tarjonta.ui.util.VaadinUtils;
import java.io.File;
import java.io.OutputStream;
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

    private Label moduuliTitleLabel;

    private Label moduuliStatusLabel;

    @PropertyId("organisaatioOid")
    private TextField organisaatioField;

    @PropertyId("todo_koulutus")
    private TextField koulutusField;

    private static final I18NHelper i18n = new I18NHelper("TutkintoOhjelmaEditForm.");

    public TutkintoOhjelmaEditForm() {

        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);

        moduuliTitleLabel = new Label("title");
        moduuliStatusLabel = new Label("status");

        mainLayout.addComponent(moduuliTitleLabel);
        mainLayout.addComponent(moduuliStatusLabel);

        final GridLayout grid = new GridLayout(2, 2);
        grid.setSpacing(true);

        organisaatioField = VaadinUtils.newTextField();
        koulutusField = VaadinUtils.newTextField();

        addFieldWithLabel(grid, new Label(i18n.getMessage("organisaatioLabel")), organisaatioField);
        addFieldWithLabel(grid, new Label(i18n.getMessage("koulutusLabel")), koulutusField);

        VerticalLayout vertical = new VerticalLayout();
        vertical.addComponent(grid);
        vertical.addComponent(createKoodistoPanel());

        mainLayout.addComponent(VaadinUtils.newTwoColumnHorizontalLayout(vertical, createNavigations()));
        mainLayout.addComponent(createMultilingualEditors());

        setCompositionRoot(mainLayout);
    }

    @Override
    public BeanItem<? extends AbstractKoulutusmoduuliFormModel<TutkintoOhjelmaDTO>> createBeanItem(TutkintoOhjelmaDTO dto) {

        final TutkintoOhjelmaFormModel model = new TutkintoOhjelmaFormModel(dto);
        final BeanItem<TutkintoOhjelmaFormModel> beanItem = new BeanItem<TutkintoOhjelmaFormModel>(model);

        // todo: add bean properties to populate
        final String[] properties = {
            "organisaatioOid"
        };

        for (String property : properties) {
            beanItem.addItemProperty(property, new NestedMethodProperty(model, property));
        }

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
        final Panel parents = new Panel(i18n.getMessage("sisaltyyModuleihin"));
        final Panel children = new Panel(i18n.getMessage("sisaltyvatModuulit"));

        parents.setWidth(100, UNITS_PERCENTAGE);
        children.setWidth(100, UNITS_PERCENTAGE);

        layout.addComponent(parents);
        layout.addComponent(children);

        return layout;

    }

}

