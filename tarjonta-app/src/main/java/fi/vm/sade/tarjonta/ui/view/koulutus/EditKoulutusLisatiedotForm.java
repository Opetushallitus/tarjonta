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
package fi.vm.sade.tarjonta.ui.view.koulutus;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.ui.component.OphRichTextArea;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusLisatiedotModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.tarjonta.ui.view.common.KoodistoSelectionTabSheet;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiConstant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * For editing the lisätiedot.
 *
 * @author mlyly
 */
@Configurable
public class EditKoulutusLisatiedotForm extends AbstractVerticalNavigationLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusLisatiedotForm.class);

    @Autowired
    private TarjontaPresenter _presenter;
    @Autowired
    private TarjontaModel _tarjontaModel;
    @Autowired
    private TarjontaUIHelper _uiHelper;

    private static final ThemeResource TAB_ICON_PLUS = new ThemeResource(UiConstant.RESOURCE_URL_OPH_IMG + "icon-add-black.png");

    @Override
    protected void buildLayout(VerticalLayout layout) {
        LOG.info("buildLayout()");

        KoulutusLisatiedotModel model = _tarjontaModel.getKoulutusLisatiedotModel();

        addNavigationButtons();

        // What languages should we have as preselection when initializing the form?
        Set<String> languageUris = new HashSet<String>();
        languageUris.addAll(_tarjontaModel.getKoulutusPerustiedotModel().getOpetuskielet());
        languageUris.addAll(_tarjontaModel.getKoulutusLisatiedotModel().getKielet());

        // Update language selections
        _tarjontaModel.getKoulutusLisatiedotModel().setKielet(languageUris);

        //
        // Build tabsheet for languages with koodisto select languages
        //
        final KoodistoSelectionTabSheet tabs = new KoodistoSelectionTabSheet(KoodistoURIHelper.KOODISTO_KIELI_URI) {

            @Override
            public void doAddTab(String uri) {
                Component c = createLanguageEditor(uri);
                addTab(uri, c, _uiHelper.getKoodiNimi(uri));
            }
        };

        // Initialize with all preselected languages
        tabs.getKcSelection().setValue(_tarjontaModel.getKoulutusLisatiedotModel().getKielet());

        addComponent(tabs);

        addComponent(new Label("EditKoulutusLisatiedotForm"));
    }

    private void addNavigationButtons() {

        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                _presenter.showMainDefaultView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);
        addNavigationButton(T("tallennaLuonnoksena"), new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);
        addNavigationButton(T("tallennaValmiina"), new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);
        addNavigationButton(T("esikatsele"), new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);
        addNavigationButton(T("jatka"), new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);
    }


    private Component createLanguageEditor(String uri) {
        VerticalLayout vl = UiBuilder.verticalLayout();

        vl.addComponent(new OphRichTextArea());
        vl.addComponent(new OphRichTextArea());
        vl.addComponent(new OphRichTextArea());
        vl.addComponent(new OphRichTextArea());
        vl.addComponent(new OphRichTextArea());

        return vl;
    }

}
