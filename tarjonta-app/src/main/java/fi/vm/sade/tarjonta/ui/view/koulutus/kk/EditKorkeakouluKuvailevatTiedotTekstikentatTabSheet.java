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
package fi.vm.sade.tarjonta.ui.view.koulutus.kk;

import com.vaadin.data.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.ui.component.OphRichTextArea;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluLisatietoModel;
import fi.vm.sade.tarjonta.ui.view.common.ImageUploader;
import fi.vm.sade.tarjonta.ui.view.koulutus.aste2.EditLisatiedotTabSheet;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import java.util.HashSet;
import java.util.Set;

public class EditKorkeakouluKuvailevatTiedotTekstikentatTabSheet extends EditLisatiedotTabSheet {

    private static final Logger LOG = LoggerFactory.getLogger(EditKorkeakouluKuvailevatTiedotTekstikentatTabSheet.class);
    private static final String TEXT_AREA_DEFAULT_WIDTH = "550px";
    private static final long serialVersionUID = -7726685044305900176L;

    public EditKorkeakouluKuvailevatTiedotTekstikentatTabSheet(TarjontaModel tarjontaModel, TarjontaUIHelper uiHelper, UiBuilder uiBuilder) {
        super(tarjontaModel, uiHelper, uiBuilder);
    }

    /**
     * Create rich text editors for content editing.
     *
     * @param uri
     * @return
     */
    @Override
    protected AbstractComponent createLanguageEditor(final String uri) {
        final VerticalLayout vl = UiBuilder.verticalLayout();

        vl.setSpacing(true);
        vl.setMargin(true);
        KorkeakouluLisatietoModel model = getModel().getKorkeakouluKuvailevatTiedot().getTekstikentat().get(uri);

        if (model == null) {
            model = new KorkeakouluLisatietoModel();
            getModel().getKorkeakouluKuvailevatTiedot().getTekstikentat().put(uri, model);
        }

        final PropertysetItem psi = new BeanItem(model);
        createEditor(vl, psi, "koulutusohjelmanAmmatillisetTavoitteet");
        createEditor(vl, psi, "paaaineenValinta");
        createEditor(vl, psi, "koulutuksenSisalto");
        createEditor(vl, psi, "koulutuksenRakenne");
        createImageLoaderAndEditor(vl, psi, "kuvausKoulutuksenRakenteesta", model, uri);
        createEditor(vl, psi, "lisatietoaOpetuskielesta");
        createEditor(vl, psi, "lopputyonKuvaus");
        createMaksullisuusAndHinta(vl, psi, "opintojenMaksullisuus");
        createEditor(vl, psi, "sijoittautuminenTyoelamaan");
        createEditor(vl, psi, "patevyys");
        createEditor(vl, psi, "kansainvalistyminen");
        createEditor(vl, psi, "yhteistyoMuidenToimijoidenKanssa");
        createEditor(vl, psi, "tutkimuksenPainopisteet");
        createEditor(vl, psi, "jatkoOpintomahdollisuudet");
        return vl;
    }

    private void createImageLoaderAndEditor(final VerticalLayout vl, final PropertysetItem psi, final String id, KorkeakouluLisatietoModel model, final String uri) {
        Property.ValueChangeListener changeListener = new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                LOG.debug("form valuechenged event fired!", getModel().getKorkeakouluKuvailevatTiedot().getTekstikentat().get(uri));

            }
        };

        createEditor(vl, psi, id);
        ImageUploader imageUploader = new ImageUploader(model.getKuvaKoulutuksenRakenteesta(), vl, changeListener);
    }

    private void createMaksullisuusAndHinta(final VerticalLayout vl, final PropertysetItem psi, final String id) {
        createEditor(vl, psi, id);

        final PropertysetItem kTiedot = new BeanItem(getModel().getKorkeakouluKuvailevatTiedot());
        TextField tfHinta = new TextField("", kTiedot.getItemProperty("hinta"));

        vl.addComponent(tfHinta);
    }

    private void createEditor(final VerticalLayout vl, final PropertysetItem psi, final String id) {
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
        languageUris.addAll(getModel().getKorkeakouluPerustiedot().getOpetuskielis());

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
    }
}
