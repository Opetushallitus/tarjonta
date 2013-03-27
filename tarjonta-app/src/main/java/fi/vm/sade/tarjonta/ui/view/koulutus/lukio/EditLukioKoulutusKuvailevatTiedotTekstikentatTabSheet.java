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
package fi.vm.sade.tarjonta.ui.view.koulutus.lukio;

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
import fi.vm.sade.tarjonta.ui.view.koulutus.aste2.EditLisatiedotTabSheet;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import java.util.HashSet;
import java.util.Set;

public class EditLukioKoulutusKuvailevatTiedotTekstikentatTabSheet extends EditLisatiedotTabSheet {

    private static final Logger LOG = LoggerFactory.getLogger(EditLukioKoulutusKuvailevatTiedotTekstikentatTabSheet.class);
    private static final String TEXT_AREA_DEFAULT_WIDTH = "550px";
    private static final long serialVersionUID = -7726685044305900176L;

    public EditLukioKoulutusKuvailevatTiedotTekstikentatTabSheet(TarjontaModel tarjontaModel, TarjontaUIHelper uiHelper, UiBuilder uiBuilder) {
        super(tarjontaModel, uiHelper, uiBuilder);
    }

    /**
     * Create rich text editors for content editing.
     *
     * @param uri
     * @return
     */
    @Override
    protected AbstractComponent createLanguageEditor(String uri) {
        final VerticalLayout vl = UiBuilder.verticalLayout();

        vl.setSpacing(true);
        vl.setMargin(true);

        KoulutusLisatietoModel model = getModel().getKoulutusLukioKuvailevatTiedot().getTekstikentat().get(uri);
        
        if(model==null) {
            model = new KoulutusLisatietoModel();
            getModel().getKoulutusLukioKuvailevatTiedot().getTekstikentat().put(uri, model);
        }
        
        final PropertysetItem psi = new BeanItem(model);

        createEditor(uri, vl, psi, "sisalto");
        createEditor(uri, vl, psi, "kansainvalistyminen");
        createEditor(uri, vl, psi, "yhteistyoMuidenToimijoidenKanssa");

        return vl;
    }

    private void createEditor(final String langUri, final VerticalLayout vl, final PropertysetItem psi, final String id) {
        final OphRichTextArea rta = UiBuilder.richTextArea(null, psi, id);
        rta.setWidth(TEXT_AREA_DEFAULT_WIDTH);
        vl.addComponent(UiBuilder.label((AbstractLayout) null, T(id + ".label"), LabelStyleEnum.H2));
        vl.addComponent(UiBuilder.label((AbstractLayout) null, T(id + ".help"), LabelStyleEnum.TEXT));
        vl.addComponent(rta);
    }
    
    
    @Override
    protected void initializeTabsheet(boolean allowDefault) {
        // What languages should we have as preselection when initializing the form?
        // Current hypothesis is that we should use the opetuskielet + any possible additional languages added to additional information
        Set<String> languageUris = new HashSet<String>();
        final String opetuskieliKoodiUri = getModel().getKoulutusLukioPerustiedot().getOpetuskieli();

        if (opetuskieliKoodiUri != null) {
            languageUris.add(opetuskieliKoodiUri); //only single language in 2aste
        }
        languageUris.addAll(getModel().getKoulutusLukioKuvailevatTiedot().getKielet());

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
}
