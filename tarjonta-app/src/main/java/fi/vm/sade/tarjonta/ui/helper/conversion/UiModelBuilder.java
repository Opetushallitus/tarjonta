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

import com.google.common.base.Preconditions;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.convertToKielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import java.util.List;
import java.util.Locale;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author Jani Wilén
 */
@Configurable(preConstruction = false)
public class UiModelBuilder<MODEL extends MonikielinenTekstiModel> {

    private transient static final Locale LOCALE_FI = new Locale("FI");
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(UiModelBuilder.class);
    private Class<MODEL> modelClass;
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    public UiModelBuilder(Class<MODEL> modelClass, TarjontaKoodistoHelper tarjontaKoodistoHelper) {
        if (modelClass == null) {
            throw new RuntimeException("An invalid constructor argument - the class argument cannot be null.");
        }
        this.modelClass = modelClass;
        this.tarjontaKoodistoHelper = tarjontaKoodistoHelper;
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
            final MonikielinenTekstiTyyppi.Teksti teksti = searchTekstiTyyppiByLanguage(tyyppi.getTeksti(), locale);

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
            final MonikielinenTekstiTyyppi.Teksti teksti = searchTekstiTyyppiByLanguage(tyyppi.getTeksti(), LOCALE_FI);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Language code fallback : " + LOCALE_FI.getLanguage());
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

    public MonikielinenTekstiTyyppi.Teksti searchTekstiTyyppiByLanguage(List<MonikielinenTekstiTyyppi.Teksti> tekstis, final Locale locale) {
        LOG.debug("locale : " + locale.getLanguage() + ", teksti : " + (tekstis != null ? tekstis.size() : tekstis));
        Preconditions.checkNotNull(tarjontaKoodistoHelper, "TarjontaKoodistoHelper object cannot be null");

        final String requiredLangCode = convertToKielikoodiUriWithoutVersion(locale.getLanguage());

        for (MonikielinenTekstiTyyppi.Teksti teksti : tekstis) {
            if (teksti.getKieliKoodi() != null) {
                final String tekstiLang = convertToKielikoodiUriWithoutVersion(teksti.getKieliKoodi());
                if (tekstiLang != null && tekstiLang.equals(requiredLangCode)) {
                    return teksti;
                }
            } else {
                LOG.error("An unknown data bug : MonikielinenTekstiTyyppi.Teksti KieliKoodi was null?");
            }
        }

        LOG.debug("  --> no text found by locale : " + locale.getLanguage());

        return null;
    }

    public MODEL build(final MonikielinenTekstiTyyppi tyyppi, final String koodiUri) {
        MODEL m = newModelInstance();

        if (tyyppi == null) {
            LOG.warn("MonikielinenTekstiTyyppi object was null, the missing data cannot be show on UI.");
            return m;
        }

        if (koodiUri != null) {
            final MonikielinenTekstiTyyppi.Teksti teksti = TarjontaUIHelper.searchTekstiTyyppiByLanguage(tyyppi.getTeksti(), koodiUri);
            updateTextToModel(teksti, m, koodiUri);
        }

        if (m.getNimi() == null || m.getNimi().isEmpty()) {
            //FI default fallback
            final String fiKoodiUri = tarjontaKoodistoHelper.convertKielikoodiToKieliUri(LOCALE_FI.getLanguage());
            final MonikielinenTekstiTyyppi.Teksti teksti = TarjontaUIHelper.searchTekstiTyyppiByLanguage(tyyppi.getTeksti(), fiKoodiUri);
            updateTextToModel(teksti, m, koodiUri);
        }

        m.setKielikaannos(convertToKielikaannosViewModel(tyyppi));
        return m;
    }

    /**
     * Trim and clean.
     *
     * @param kielikoodi
     * @return
     */
    private String convertToKielikoodiUriWithoutVersion(final String kielikoodi) {
        final String code = tarjontaKoodistoHelper.convertKielikoodiToKieliUri(kielikoodi);
        return code != null ? TarjontaUIHelper.noVersionUri(code.trim()) : code;
    }

    private void updateTextToModel(final MonikielinenTekstiTyyppi.Teksti teksti, MODEL m, String locale) {
        if (teksti != null) {
            m.setKielikoodi(teksti.getKieliKoodi());
            m.setNimi(teksti.getValue());

            if (LOG.isDebugEnabled()) {
                LOG.debug("Language code : '{}', text value : {}.", teksti.getKieliKoodi(), teksti.getValue());
            }
        } else {
            LOG.debug("No text data found for locale '{}'.", locale);
        }
    }
}
