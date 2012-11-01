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

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.ui.component.OphRichTextArea;
import fi.vm.sade.generic.ui.component.OphTokenField;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.tarjonta.ui.view.common.KoodistoSelectionTabSheet;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.StyleEnum;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * For editing the studies (koulutus) additional information.
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

    @Override
    protected void buildLayout(VerticalLayout layout) {
        LOG.info("buildLayout()");

        setSpacing(true);
        setMargin(true);

        addNavigationButtons();

        //
        // Ammattinimikkeet
        //

        {
            addComponent(UiBuilder.label((AbstractLayout) null, T("ammattinimikkeet"), LabelStyleEnum.H2));

            PropertysetItem psi = new BeanItem(_tarjontaModel.getKoulutusLisatiedotModel());
            OphTokenField f = UiBuilder.koodistoTokenField(null, KoodistoURIHelper.KOODISTO_KIELI_URI, psi, "ammattinimikkeet");
            f.setFormatter(new OphTokenField.SelectedTokenToTextFormatter() {

                @Override
                public String formatToken(Object selectedToken) {
                    return _uiHelper.getKoodiNimi((String) selectedToken);
                }
            });

            addComponent(f);
        }

        //
        // Language dependant information
        //

        // What languages should we have as preselection when initializing the form?
        // Current hypothesis is that we should use the opetuskielet + any possible additional languages added to additional information
        Set<String> languageUris = new HashSet<String>();
        languageUris.addAll(_tarjontaModel.getKoulutusPerustiedotModel().getOpetuskielet());
        languageUris.addAll(_tarjontaModel.getKoulutusLisatiedotModel().getKielet());

        // Update language selections to contain opetuskielet AND lisätiedot languages
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

        // TODO Autoselect first content tab?

        // Initialize with all preselected languages
        tabs.getKcSelection().setValue(_tarjontaModel.getKoulutusLisatiedotModel().getKielet());

        addComponent(UiBuilder.label((AbstractLayout) null, T("kieliriippuvatTiedot"), LabelStyleEnum.H2));
        addComponent(tabs);
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
                _presenter.saveKoulutusLuonnoksenaModel();
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);
        addNavigationButton(T("tallennaValmiina"), new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    _presenter.saveKoulutusValmiina();
                } catch (ExceptionMessage ex) {
                    LOG.error("Failed to save.", ex);
                    getWindow().showNotification("FAILED: " + ex);
                }
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);
        addNavigationButton(T("esikatsele"), new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                _presenter.showKoulutusPreview();
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);
        addNavigationButton(T("jatka"), new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                _presenter.showShowKoulutusView();
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);
    }


    private Component createLanguageEditor(String uri) {
        VerticalLayout vl = UiBuilder.verticalLayout();

        vl.setSpacing(true);
        vl.setMargin(true);

        KoulutusLisatietoModel model = _tarjontaModel.getKoulutusLisatiedotModel().getLisatiedot(uri);
        PropertysetItem psi = new BeanItem(model);

        {
            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "kuvailevatTiedot");
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kuvailevatTiedot"), LabelStyleEnum.H2));
            vl.addComponent(rta);
        }

        {
            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "sisalto");
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("koulutuksenSisalto"), LabelStyleEnum.H2));
            vl.addComponent(rta);
        }

        {
            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "sijoittuminenTyoelamaan");
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("sijoittuminenTyoelamaan"), LabelStyleEnum.H2));
            vl.addComponent(rta);
        }

        {
            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "kansainvalistyminen");
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kansainvalistyminen"), LabelStyleEnum.H2));
            vl.addComponent(rta);
        }

        {
            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "yhteistyoMuidenToimijoidenKanssa");
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("yhteistyoMuidenToimijoidenKanssa"), LabelStyleEnum.H2));
            vl.addComponent(rta);
        }

        return vl;
    }

}
