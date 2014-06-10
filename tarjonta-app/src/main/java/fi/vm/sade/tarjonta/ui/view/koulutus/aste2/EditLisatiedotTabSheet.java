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
package fi.vm.sade.tarjonta.ui.view.koulutus.aste2;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.ui.component.OphRichTextArea;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.view.koulutus.LisatiedotTabSheet;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * "Toinen aste" extra / additional information.
 *
 * @author Jani Wilén
 */
public class EditLisatiedotTabSheet extends LisatiedotTabSheet {

    private static final Logger LOG = LoggerFactory.getLogger(EditLisatiedotTabSheet.class);

    private static final String TEXT_AREA_DEFAULT_WIDTH = "550px";

    private static final int MAX_LENGTH = 16384;

    private static final long serialVersionUID = -7726685044305900176L;

    private boolean isToinenasteValmentava;
    private boolean isPervako;

    public EditLisatiedotTabSheet(TarjontaModel tarjontaModel, TarjontaUIHelper uiHelper, UiBuilder uiBuilder) {
        super(tarjontaModel, uiHelper, uiBuilder);
    }

    @Override
    protected void initializeTabsheet(boolean allowDefault) {
        
        // What languages should we have as preselection when initializing the form?
        // Current hypothesis is that we should use the opetuskielet + any possible additional languages added to additional information
        Set<String> languageUris = new HashSet<String>();
        final String opetuskieliKoodiUri = getModel().getKoulutusPerustiedotModel().getOpetuskieli();

        if (opetuskieliKoodiUri != null) {
            languageUris.add(opetuskieliKoodiUri); //only single language in 2aste
        }

        languageUris.addAll(getModel().getKoulutusLisatiedotModel().getKielet());

        if (!languageUris.isEmpty()) {
            //get loaded data from model
            setInitialValues(languageUris);
        } else if (allowDefault) {
            //no data
            final String defautLang = getDefaultLanguageKoodiUri();
            Set<String> values = new HashSet<String>(1);
            values.add(defautLang);
            getKcSelection().setValue(values);
        }

        setSelectedTab(opetuskieliKoodiUri);
    }

    /**
     * Create rich text editors for content editing.
     *
     * @param uri
     * @return
     */
    @Override
    protected AbstractComponent createLanguageEditor(String uri) {
        VerticalLayout vl = UiBuilder.verticalLayout();

        final String koulututuksenTyyppiUrl = getModel()!=null && getModel().getKoulutusPerustiedotModel().getKoulutuksenTyyppi()!=null ? getModel().getKoulutusPerustiedotModel().getKoulutuksenTyyppi().getKoodi():null;
        isPervako = KoulutusUtil.isPervako(koulututuksenTyyppiUrl);
        isToinenasteValmentava = KoulutusUtil.isValmentavaJaKuntouttava(koulututuksenTyyppiUrl);

        String kieli = "-";

        vl.setSpacing(true);
        vl.setMargin(true);

        vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kuvailevatTiedot.title"), LabelStyleEnum.H2));
        vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kuvailevatTiedot.help", MAX_LENGTH), LabelStyleEnum.TEXT));

        KoulutusLisatietoModel model = getModel().getKoulutusLisatiedotModel().getLisatiedot(uri);
        PropertysetItem psi = new BeanItem(model);

        {
            OphRichTextArea rta = UiUtil.richTextArea(null, psi, "koulutusohjelmanValinta", MAX_LENGTH,
                    T("_textTooLong", T("koulutusOhjelmanValinta") + " (" + kieli + ")", MAX_LENGTH));
            rta.setWidth(TEXT_AREA_DEFAULT_WIDTH);
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("koulutusOhjelmanValinta"), LabelStyleEnum.H2));
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("koulutusOhjelmanValinta.help", 1000), LabelStyleEnum.TEXT));
            vl.addComponent(rta);
        }

        {
            OphRichTextArea rta = UiUtil.richTextArea(null, psi, "sisalto", MAX_LENGTH,
                    T("_textTooLong", T("koulutuksenSisalto") + " (" + kieli + ")", 5000));
            rta.setWidth(TEXT_AREA_DEFAULT_WIDTH);
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("koulutuksenSisalto"), LabelStyleEnum.H2));
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("koulutuksenSisalto.help", 5000), LabelStyleEnum.TEXT));
            vl.addComponent(rta);
        }

        if(isPervako||isToinenasteValmentava) {
            {
                OphRichTextArea rta = UiUtil.richTextArea(null, psi, "kohderyhma", MAX_LENGTH,
                        T("_textTooLong", T("kohderyhma") + " (" + kieli + ")", 5000));
                rta.setWidth(TEXT_AREA_DEFAULT_WIDTH);
                vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kohderyhma"), LabelStyleEnum.H2));
                vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kohderyhma.help", 5000), LabelStyleEnum.TEXT));
                vl.addComponent(rta);
            }
        }

        {
            OphRichTextArea rta = UiUtil.richTextArea(null, psi, "sijoittuminenTyoelamaan", MAX_LENGTH,
                    T("_textTooLong", T("sijoittuminenTyoelamaan") + " (" + kieli + ")", 1000));
            rta.setWidth(TEXT_AREA_DEFAULT_WIDTH);
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("sijoittuminenTyoelamaan"), LabelStyleEnum.H2));
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("sijoittuminenTyoelamaan.help", 1000), LabelStyleEnum.TEXT));
            vl.addComponent(rta);
        }

        {
            OphRichTextArea rta = UiUtil.richTextArea(null, psi, "kansainvalistyminen", MAX_LENGTH,
                    T("_textTooLong", T("kansainvalistyminen") + " (" + kieli + ")", 5000));
            rta.setWidth(TEXT_AREA_DEFAULT_WIDTH);
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kansainvalistyminen"), LabelStyleEnum.H2));
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("kansainvalistyminen.help", 5000), LabelStyleEnum.TEXT));
            vl.addComponent(rta);
        }

        {
            OphRichTextArea rta = UiUtil.richTextArea(null, psi, "yhteistyoMuidenToimijoidenKanssa", MAX_LENGTH,
                    T("_textTooLong", T("yhteistyoMuidenToimijoidenKanssa") + " (" + kieli + ")", 2000));
            rta.setWidth(TEXT_AREA_DEFAULT_WIDTH);
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("yhteistyoMuidenToimijoidenKanssa"), LabelStyleEnum.H2));
            vl.addComponent(UiBuilder.label((AbstractLayout) null, T("yhteistyoMuidenToimijoidenKanssa.help", 2000), LabelStyleEnum.TEXT));
            vl.addComponent(rta);
        }

        return vl;
    }
}
