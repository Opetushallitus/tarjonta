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

import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.convertToKielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import java.util.Locale;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class UiModelBuilder<MODEL extends MonikielinenTekstiModel> {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(UiModelBuilder.class);
    private Class<MODEL> modelClass;

    public UiModelBuilder(Class<MODEL> modelClass) {
        if (modelClass == null) {
            throw new RuntimeException("An invalid constructor argument - the class argument cannot be null.");
        }
        this.modelClass = modelClass;
    }

    private MODEL newModelInstance() {
        try {
            return modelClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Model initialization failed.", ex);
        }
    }

    public MODEL build(final MonikielinenTekstiTyyppi tyyppi, final Locale locale) {
        MODEL m = newModelInstance();

        if (tyyppi == null) {
            LOG.warn("MonikielinenTekstiTyyppi object was null, the missing data cannot be show on UI.");
            return m;
        }

        if (locale != null) {
            final MonikielinenTekstiTyyppi.Teksti teksti = TarjontaUIHelper.searchTekstiTyyppiByLanguage(tyyppi.getTeksti(), locale);

            if (teksti != null) {
                m.setKielikoodi(teksti.getKieliKoodi());
                m.setNimi(teksti.getValue());

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Language code : " + teksti.getKieliKoodi());
                    LOG.debug("Text value : " + (teksti != null ? teksti.getValue() : teksti));
                }
            } else {
                LOG.debug("No text data found for locale " + locale.getLanguage());
            }
        }

        if (m.getNimi() == null || m.getNimi().isEmpty()) {
            //FI default fallback
            final Locale locale1 = new Locale("FI");
            final MonikielinenTekstiTyyppi.Teksti teksti = TarjontaUIHelper.searchTekstiTyyppiByLanguage(tyyppi.getTeksti(), locale1);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Language code fallback : " + locale1.getLanguage());
                LOG.debug("Text value : " + (teksti != null ? teksti.getValue() : teksti));
            }

            if (teksti != null) {
                m.setKielikoodi(teksti.getKieliKoodi());
                m.setNimi(teksti.getValue());
            } else {
                LOG.error("An invalid data error -´MonikielinenTekstiModel object was missing Finnish language data.");
            }
        }

        m.setKielikaannos(convertToKielikaannosViewModel(tyyppi));
        return m;
    }
}
