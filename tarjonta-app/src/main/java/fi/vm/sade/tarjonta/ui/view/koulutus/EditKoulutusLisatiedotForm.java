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
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.generic.ui.component.OphRichTextArea;
import fi.vm.sade.generic.ui.component.OphTokenField;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.koodisto.widget.DefaultKoodiCaptionFormatter;
import fi.vm.sade.koodisto.widget.DefaultKoodiFieldValueFormatter;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusLisatiedotModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.tarjonta.ui.view.common.KoodistoSelectionTabSheet;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.StyleEnum;
import java.util.HashSet;
import java.util.Set;
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
    private static final long serialVersionUID = -4054591599209251060L;
    @Autowired(required = true)
    private TarjontaPresenter _presenter;
    @Autowired(required = true)
    private TarjontaUIHelper _uiHelper;
    private KoulutusLisatiedotModel koulutusLisatiedotModel;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;

    @Override
    protected void buildLayout(VerticalLayout layout) {
        setSpacing(true);
        setMargin(true);
        koulutusLisatiedotModel = _presenter.getModel().getKoulutusLisatiedotModel();
        addNavigationButtons();

        //
        // Ammattinimikkeet
        //

        {
            addComponent(UiBuilder.label((AbstractLayout) null, T("ammattinimikkeet"), LabelStyleEnum.H2));
            addComponent(UiBuilder.label((AbstractLayout) null, T("ammattinimikkeet.help"), LabelStyleEnum.TEXT));

            PropertysetItem psi = new BeanItem(koulutusLisatiedotModel);
            OphTokenField f = uiBuilder.koodistoTokenField(null, KoodistoURIHelper.KOODISTO_AMMATTINIMIKKEET_URI, psi, "ammattinimikkeet");
            f.setFormatter(new OphTokenField.SelectedTokenToTextFormatter() {
                @Override
                public String formatToken(Object selectedToken) {
                    return _uiHelper.getKoodiNimi((String) selectedToken);
                }
            });

            // Create caption formatter for koodisto component.
            KoodistoComponent k = (KoodistoComponent) f.getSelectionComponent();
            k.setCaptionFormatter(new CaptionFormatter() {
                @Override
                public String formatCaption(Object dto) {
                    if (dto instanceof KoodiType) {
                      KoodiType kt = (KoodiType) dto;
                      return _uiHelper.getKoodiNimi(kt, null);
                      // return _uiHelper.getKoodiNimi(kt.getKoodiUri());
                    } else {
                        return "??? " + dto;
                    }
                }
            });

            addComponent(f);
            f.getSelectionLayout().setWidth("600px");
        }

        //
        // Language dependant information
        //

        // What languages should we have as preselection when initializing the form?
        // Current hypothesis is that we should use the opetuskielet + any possible additional languages added to additional information
        Set<String> languageUris = new HashSet<String>();
        languageUris.add(_presenter.getModel().getKoulutusPerustiedotModel().getOpetuskieli()); //only single language in 2aste
        languageUris.addAll(koulutusLisatiedotModel.getKielet());

        // Update language selections to contain opetuskielet AND lisätiedot languages
        koulutusLisatiedotModel.setKielet(languageUris);

        //
        // Build tabsheet for languages with koodisto select languages
        //
        final KoodistoSelectionTabSheet tabs = new KoodistoSelectionTabSheet(KoodistoURIHelper.KOODISTO_KIELI_URI, uiBuilder) {
            private static final long serialVersionUID = -7916177514458213528L;
            @Override
            public void doAddTab(String uri) {
                Component c = createLanguageEditor(uri);
                addTab(uri, c, _uiHelper.getKoodiNimi(uri));
            }
        };

        // TODO Autoselect first content tab?

        // Initialize with all preselected languages
        tabs.getKcSelection().setValue(koulutusLisatiedotModel.getKielet());

        addComponent(UiBuilder.label((AbstractLayout) null, T("kieliriippuvatTiedot"), LabelStyleEnum.H2));
        addComponent(tabs);
    }

    private void addNavigationButtons() {

        addNavigationButton("", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(ClickEvent event) {
                _presenter.showMainDefaultView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);
        addNavigationButton(T(CommonTranslationKeys.TALLENNA_LUONNOKSENA), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    _presenter.saveKoulutus(SaveButtonState.SAVE_AS_DRAFT);
                } catch (ExceptionMessage ex) {
                    LOG.error("Failed to save.", ex);
                    getWindow().showNotification("FAILED: " + ex);
                }
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);
        addNavigationButton(T(CommonTranslationKeys.TALLENNA_VALMIINA), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    _presenter.saveKoulutus(SaveButtonState.SAVE_AS_READY);
                } catch (ExceptionMessage ex) {
                    LOG.error("Failed to save.", ex);
                    getWindow().showNotification("FAILED: " + ex);
                }
            }
        }, StyleEnum.STYLE_BUTTON_SECONDARY);
        addNavigationButton(T("esikatsele"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(ClickEvent event) {
                _presenter.showKoulutusPreview();
            }
        }, StyleEnum.STYLE_BUTTON_SECONDARY, false);
        addNavigationButton(T(CommonTranslationKeys.JATKA), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(ClickEvent event) {
                _presenter.showShowKoulutusView();
            }
        }, StyleEnum.STYLE_BUTTON_SECONDARY);
    }

    /**
     * Create rich text editors for content editing.
     *
     * @param uri
     * @return
     */
    private Component createLanguageEditor(String uri) {
        VerticalLayout vl = UiBuilder.verticalLayout();

        vl.setSpacing(true);
        vl.setMargin(true);

        vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kuvailevatTiedot.title"), LabelStyleEnum.H2));
        vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kuvailevatTiedot.help"), LabelStyleEnum.TEXT));

        KoulutusLisatietoModel model = koulutusLisatiedotModel.getLisatiedot(uri);
        PropertysetItem psi = new BeanItem(model);

//        {
//            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "kuvailevatTiedot");
//            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kuvailevatTiedot"), LabelStyleEnum.H2));
//            vl.addComponent(rta);
//        }

        {
            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "sisalto");
            rta.setWidth("460px");
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("koulutuksenSisalto"), LabelStyleEnum.H2));
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("koulutuksenSisalto.help"), LabelStyleEnum.TEXT));
            vl.addComponent(rta);
        }

        {
            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "sijoittuminenTyoelamaan");
            rta.setWidth("460px");
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("sijoittuminenTyoelamaan"), LabelStyleEnum.H2));
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("sijoittuminenTyoelamaan.help"), LabelStyleEnum.TEXT));
            vl.addComponent(rta);
        }

        {
            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "kansainvalistyminen");
            rta.setWidth("460px");
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kansainvalistyminen"), LabelStyleEnum.H2));
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kansainvalistyminen.help"), LabelStyleEnum.TEXT));
            vl.addComponent(rta);
        }

        {
            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "yhteistyoMuidenToimijoidenKanssa");
            rta.setWidth("460px");
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("yhteistyoMuidenToimijoidenKanssa"), LabelStyleEnum.H2));
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("yhteistyoMuidenToimijoidenKanssa.help"), LabelStyleEnum.TEXT));
            vl.addComponent(rta);
        }

        return vl;
    }
}
