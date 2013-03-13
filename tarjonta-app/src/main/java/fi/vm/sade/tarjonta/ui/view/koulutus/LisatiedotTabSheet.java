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
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.component.OphRichTextArea;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.view.common.KoodistoSelectionTabSheet;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class LisatiedotTabSheet extends KoodistoSelectionTabSheet {

    private static final Logger LOG = LoggerFactory.getLogger(LisatiedotTabSheet.class);
    private static final String TEXT_AREA_DEFAULT_WIDTH = "550px";
    private static final long serialVersionUID = 8350473574707759159L;
    TarjontaModel tarjontaModel;
    private transient I18NHelper _i18n;

    public LisatiedotTabSheet(TarjontaModel tarjontaModel, TarjontaUIHelper uiHelper, UiBuilder uiBuilder) {
        super(KoodistoURIHelper.KOODISTO_KIELI_URI, uiHelper, uiBuilder);
        this.tarjontaModel = tarjontaModel;

        // Initialize with all preselected languages
        initializeTabsheet(false);
    }

    @Override
    public void doAddTab(String uri) {
        addTab(uri, createLanguageEditor(uri));
    }

    private void initializeTabsheet(boolean allowDefault) {
        // What languages should we have as preselection when initializing the form?
        // Current hypothesis is that we should use the opetuskielet + any possible additional languages added to additional information
        Set<String> languageUris = new HashSet<String>();
        final String opetuskieliKoodiUri = tarjontaModel.getKoulutusPerustiedotModel().getOpetuskieli();

        if (opetuskieliKoodiUri != null) {
            languageUris.add(opetuskieliKoodiUri); //only single language in 2aste
        }
        languageUris.addAll(tarjontaModel.getKoulutusLisatiedotModel().getKielet());

        if (!languageUris.isEmpty()) {
            //get loaded data from model
            setInitialValues(languageUris);
        } else if(allowDefault) { 
            LOG.info("Add default language.");
            //no data
            final String defautLang = getDefaultLanguageKoodiUri();
            Set<String> values = new HashSet<String>(1);
            values.add(defautLang);
            getKcSelection().setValue(values);       
        }

        setSelectedTab(opetuskieliKoodiUri);
    }

    private void setInitialValues(final Set<String> values) {
        if (values != null) {
            for (String kieliKaannos : values) {
                addTab(kieliKaannos, createLanguageEditor(kieliKaannos));
            }
            getKcSelection().setValue(values);
        }
    }

    public void reload() {
        removeAllComponents();
        addLanguageMenuTab();
        initializeTabsheet(true);
    }

    /**
     * Create rich text editors for content editing.
     *
     * @param uri
     * @return
     */
    private AbstractComponent createLanguageEditor(String uri) {
        VerticalLayout vl = UiBuilder.verticalLayout();

        vl.setSpacing(true);
        vl.setMargin(true);

        vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kuvailevatTiedot.title"), LabelStyleEnum.H2));
        vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kuvailevatTiedot.help"), LabelStyleEnum.TEXT));

        KoulutusLisatietoModel model = tarjontaModel.getKoulutusLisatiedotModel().getLisatiedot(uri);
        PropertysetItem psi = new BeanItem(model);

        {
            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "sisalto");
            rta.setWidth(TEXT_AREA_DEFAULT_WIDTH);
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("koulutuksenSisalto"), LabelStyleEnum.H2));
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("koulutuksenSisalto.help"), LabelStyleEnum.TEXT));
            vl.addComponent(rta);
        }

        {
            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "sijoittuminenTyoelamaan");
            rta.setWidth(TEXT_AREA_DEFAULT_WIDTH);
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("sijoittuminenTyoelamaan"), LabelStyleEnum.H2));
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("sijoittuminenTyoelamaan.help"), LabelStyleEnum.TEXT));
            vl.addComponent(rta);
        }

        {
            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "kansainvalistyminen");
            rta.setWidth(TEXT_AREA_DEFAULT_WIDTH);
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kansainvalistyminen"), LabelStyleEnum.H2));
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kansainvalistyminen.help"), LabelStyleEnum.TEXT));
            vl.addComponent(rta);
        }

        {
            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "yhteistyoMuidenToimijoidenKanssa");
            rta.setWidth(TEXT_AREA_DEFAULT_WIDTH);
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("yhteistyoMuidenToimijoidenKanssa"), LabelStyleEnum.H2));
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("yhteistyoMuidenToimijoidenKanssa.help"), LabelStyleEnum.TEXT));
            vl.addComponent(rta);
        }

        {
            OphRichTextArea rta = UiBuilder.richTextArea(null, psi, "koulutusohjelmanValinta");
            rta.setWidth(TEXT_AREA_DEFAULT_WIDTH);
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("koulutusOhjelmanValinta"), LabelStyleEnum.H2));
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("koulutusOhjelmanValinta.help"), LabelStyleEnum.TEXT));
            vl.addComponent(rta);
        }

        return vl;
    }

    // Generic translatio helpers
    private String T(String key) {
        return getI18n().getMessage(key);
    }

    private I18NHelper getI18n() {
        if (_i18n == null) {
            _i18n = new I18NHelper("EditKoulutusLisatiedotForm.");
        }
        return _i18n;
    }
}
