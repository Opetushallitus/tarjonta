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

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusasteModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jani Wilén
 */
@Component
public class KoulutusKoodistoConverter {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusKoodistoConverter.class);
    private final String LUKIO = KoulutusasteType.TOINEN_ASTE_LUKIO.getKoulutusaste();
    private final String AMMATILLINEN = KoulutusasteType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS.getKoulutusaste();
    @Autowired(required = true)
    private TarjontaUIHelper tarjontaUiHelper;

    public KoulutusKoodistoConverter() {
        super();
    }

    public List<KoulutusasteModel> listaaKoulutusasteet( final Locale locale) {
        List<KoodiType> koodistoData = tarjontaUiHelper.getKoodisByKoodisto(KoodistoURIHelper.KOODISTO_KOULUTUSASTE_URI);
        return mapKoulutusaste(koodistoData, locale);
    }

    public KoulutusasteModel listaaKoulutusaste(final KoodistoKoodiTyyppi tyyppi, final Locale locale) {
        final List<KoodiType> koodistoData = tarjontaUiHelper.gethKoodis(tyyppi.getUri());
        final KoulutusConverter<KoulutusasteModel> kc = new KoulutusConverter<KoulutusasteModel>();
        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<KoulutusasteModel> list = kc.mapKoodistoToModel(KoulutusasteModel.class, handleLocale(locale), koodistoData);

            if (!list.isEmpty() && list.size() > 0) {
                return list.get(0);
            }
        }

        return null;
    }

    /**
     * Search single koulutusohjelma object from Koodisto service.
     *
     * @param model
     * @param locale
     * @return
     */
    public KoulutusohjelmaModel listaaKoulutusohjelma(final KoodistoKoodiTyyppi tyyppi, final Locale locale) {
        final List<KoodiType> koodistoData = tarjontaUiHelper.gethKoodis(tyyppi.getUri());
        final KoulutusConverter<KoulutusohjelmaModel> kc = new KoulutusConverter<KoulutusohjelmaModel>();
        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<KoulutusohjelmaModel> list = kc.mapKoodistoToModel(KoulutusohjelmaModel.class, handleLocale(locale), koodistoData);

            if (!list.isEmpty() && list.size() > 0) {
                return list.get(0);
            }
        }

        return null;
    }

    /**
     * Search all koulutusohjelma objects from Koodisto service.
     *
     * @param model
     * @param locale
     * @return
     */
    public List<KoulutusohjelmaModel> listaaKoulutusohjelmat( final Locale locale) {
        final List<KoodiType> koodistoData = tarjontaUiHelper.getKoodisByKoodisto(KoodistoURIHelper.KOODISTO_KOULUTUSOHJELMA_URI);
        final KoulutusConverter<KoulutusohjelmaModel> kc = new KoulutusConverter<KoulutusohjelmaModel>();

        List<KoulutusohjelmaModel> models = new ArrayList<KoulutusohjelmaModel>();
        models.addAll(kc.mapKoodistoToModel(KoulutusohjelmaModel.class, handleLocale(locale), koodistoData));
        return models;
    }

    /**
     * Search single koulutuskoodi object from Koodisto service.
     *
     * @param model
     * @param locale
     * @return
     */
    public KoulutuskoodiModel listaaKoulutuskoodi(final KoodistoKoodiTyyppi tyyppi, final Locale locale) {
        final List<KoodiType> koodistoData = tarjontaUiHelper.gethKoodis(tyyppi.getUri());

        if (koodistoData != null && !koodistoData.isEmpty()) {
            final KoulutusConverter<KoulutuskoodiModel> kc = new KoulutusConverter<KoulutuskoodiModel>();
            final List<KoulutuskoodiModel> list = kc.mapKoodistoToModel(KoulutuskoodiModel.class, handleLocale(locale), koodistoData);

            if (!list.isEmpty() && list.size() > 0) {
                return list.get(0);
            }
        }

        return null;
    }

    /**
     * Search all koulutuskoodi objects from Koodisto service.
     *
     * @param model
     * @param locale
     * @return
     */
    public List<KoulutuskoodiModel> listaaKoulutuskoodit(final KoulutusasteModel model, final Locale locale) {
        final List<KoodiType> koodistoData = tarjontaUiHelper.getKoodisByKoodisto(KoodistoURIHelper.KOODISTO_KOULUTUS_URI);
        KoulutusasteType type = null;

        if (model.getKoodi() != null) {
            //TODO: better koodisto data filter.
            //A simple and ugly filter, fix it after koodisto has better data.
            for (KoulutusasteType t : KoulutusasteType.values()) {
                if (t.getKoulutusaste().equals(model.getKoodi())) {
                    type = t;
                    break;
                }
            }

            if (type == null) {
                throw new RuntimeException("Koulutus type is required.");
            }
        }

        return mapKoulutuskoodi(type, koodistoData, locale);
    }

    private List<KoulutusasteModel> mapKoulutusaste(final List<KoodiType> koodit, final Locale locale) {
        List<KoulutusasteModel> model = new ArrayList<KoulutusasteModel>();
        final KoulutusConverter<KoulutusasteModel> kc = new KoulutusConverter<KoulutusasteModel>();
        for (KoodiType koodiType : koodit) {

            //A very simple way to filter 'koulutusasteet'.
            if (koodiType.getKoodiArvo().equals(LUKIO)
                    || koodiType.getKoodiArvo().equals(AMMATILLINEN)) {

                model.add(kc.mapKoodiTypeToModel(KoulutusasteModel.class, koodiType, locale));
            }
        }

        return model;
    }

    private List<KoulutuskoodiModel> mapKoulutuskoodi(final KoulutusasteType type, final List<KoodiType> koodit, final Locale locale) {
        List<KoulutuskoodiModel> model = new ArrayList<KoulutuskoodiModel>();
        final KoulutusConverter<KoulutuskoodiModel> kc = new KoulutusConverter<KoulutuskoodiModel>();

        for (KoodiType koodiType : koodit) {
            if (type == null) {
                //do not filter anything
                model.add(kc.mapKoodiTypeToModel(KoulutuskoodiModel.class, koodiType, locale));
            } else if (koodiType.getKoodiArvo().startsWith(type.getKoulutuskoodiFilter())) {
                //TODO: fix this after koodisto references are finalised. 
                //A bad way to filter koodisto data. 
                model.add(kc.mapKoodiTypeToModel(KoulutuskoodiModel.class, koodiType, locale));
            }
        }

        LOG.debug("Mapped count of koulutuskoodit : {}", model.size());
        return model;
    }

    private Locale handleLocale( Locale locale) {
        if (locale == null) {
            locale = I18N.getLocale();
        }

        return locale;
    }
}
