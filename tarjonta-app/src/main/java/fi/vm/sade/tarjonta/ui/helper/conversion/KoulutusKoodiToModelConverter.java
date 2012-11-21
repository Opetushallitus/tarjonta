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
package fi.vm.sade.tarjonta.ui.helper.conversion;

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author Jani Wilén
 */
public class KoulutusKoodiToModelConverter<MODEL extends KoulutusKoodistoModel> {

    public KoulutusKoodiToModelConverter() {
    }

    public List<MODEL> mapKoodistoToModel(Class modelClass, final Locale locale, final List<KoodiType> koodit) {

        List<MODEL> models = new ArrayList<MODEL>();
        for (KoodiType koodiType : koodit) {
            models.add(mapKoodiTypeToModel(modelClass, koodiType, locale));
        }

        return models;
    }

    public MODEL mapKoodiTypeToModel(Class modelClass, KoodiType koodiType, Locale locale) {
        MODEL model;
        try {
            model = (MODEL) modelClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Application error - class initialization failed.", ex);
        }
        final KoodiMetadataType koodiMetadata = TarjontaUIHelper.getKoodiMetadataForLanguage(koodiType, locale);
        model.setNimi(koodiMetadata.getNimi());
        model.setKuvaus(koodiMetadata.getKuvaus());

        model.setKoodi(koodiType.getKoodiArvo());
        model.setKoodistoUri(koodiType.getKoodiUri());
        model.setKoodistoVersio(koodiType.getVersio());

        final String uriWithVersio = KoulutusConverter.mapToVersionUri(koodiType.getKoodiUri(), koodiType.getVersio());
        model.setKoodistoUriVersio(uriWithVersio);

        if (model instanceof MonikielinenTekstiModel) {
            //add all languages to the UI object
            MonikielinenTekstiModel o = (MonikielinenTekstiModel) model;
            o.setKielikaannos(map(koodiType.getMetadata()));
        }

        return model;
    }

    private Set<KielikaannosViewModel> map(final List<KoodiMetadataType> languageMetaData) {
        Set<KielikaannosViewModel> teksti = new HashSet<KielikaannosViewModel>();

        for (KoodiMetadataType meta : languageMetaData) {
            final KieliType kieli = meta.getKieli();

            if (kieli != null && meta.getNimi() != null && !meta.getNimi().isEmpty()) {
                teksti.add(new KielikaannosViewModel(kieli.name(), meta.getNimi()));
            }
        }


        return teksti;
    }
}
