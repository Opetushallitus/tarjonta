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
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import static fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper.LUKIO_KOODI_POHJAKOULUTUSVAATIMUS_URI;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.LukiolinjaModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
    @Autowired(required = true)
    private TarjontaUIHelper tarjontaUiHelper;
    @Autowired
    private KoodiService koodiService;

    public KoulutusKoodistoConverter() {
        super();
    }

    public List<KoulutuskoodiModel> listaaKoulutukses(final Set<String> koodiUris, final Locale locale) {
        LOG.debug(KoodistoURIHelper.KOODISTO_TUTKINTO_URI);

        List<String> listKoodiUris = new ArrayList<String>();
        for (String uri : koodiUris) {
            listKoodiUris.add(TarjontaUIHelper.splitKoodiURI(uri)[0]);
        }
        SearchKoodisByKoodistoCriteriaType koodisByKoodistoUri = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(listKoodiUris, KoodistoURIHelper.KOODISTO_TUTKINTO_URI);

        List<KoodiType> koodistoData = koodiService.searchKoodisByKoodisto(koodisByKoodistoUri);
        LOG.debug("listaaKoulutukses data size : " + koodistoData.size());

        final KoulutusKoodiToModelConverter<KoulutuskoodiModel> kc = new KoulutusKoodiToModelConverter<KoulutuskoodiModel>();
        List<KoulutuskoodiModel> list = new ArrayList<KoulutuskoodiModel>();

        for (KoodiType type : koodistoData) {
            list.add(searchKoulutuskoodiData(type, locale, kc));
        }

        return list;
    }

    public List<KoulutusohjelmaModel> listaaKoulutusohjelmas(final List<KoulutusmoduuliKoosteTyyppi> komos, final Locale locale) {
        LOG.debug(KoodistoURIHelper.KOODISTO_KOULUTUSOHJELMA_URI);

        List<String> listKoodiUris = new ArrayList<String>();
        for (KoulutusmoduuliKoosteTyyppi t : komos) {
            listKoodiUris.add(TarjontaUIHelper.splitKoodiURI(t.getKoulutusohjelmakoodiUri())[0]);
        }
        SearchKoodisByKoodistoCriteriaType koodisByKoodistoUri = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(listKoodiUris, KoodistoURIHelper.KOODISTO_KOULUTUSOHJELMA_URI);
        List<KoodiType> koodistoData = koodiService.searchKoodisByKoodisto(koodisByKoodistoUri);
        LOG.debug("listaaKoulutusohjelmas data size : " + koodistoData.size());

        final KoulutusKoodiToModelConverter<KoulutusohjelmaModel> kc = new KoulutusKoodiToModelConverter<KoulutusohjelmaModel>();
        List<KoulutusohjelmaModel> list = new ArrayList<KoulutusohjelmaModel>();

        for (KoodiType type : koodistoData) {
            list.add(searchKoulutusmodelData(type, locale, kc));
        }

        return list;
    }

    public List<LukiolinjaModel> listaaLukiolinjas(final List<KoulutusmoduuliKoosteTyyppi> komos, final Locale locale) {
        LOG.debug(KoodistoURIHelper.KOODISTO_LUKIOLINJA_URI);

        List<String> listKoodiUris = new ArrayList<String>();
        for (KoulutusmoduuliKoosteTyyppi t : komos) {
            listKoodiUris.add(TarjontaUIHelper.splitKoodiURI(t.getLukiolinjakoodiUri())[0]);
        }
        SearchKoodisByKoodistoCriteriaType koodisByKoodistoUri = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(listKoodiUris, KoodistoURIHelper.KOODISTO_LUKIOLINJA_URI);
        List<KoodiType> koodistoData = koodiService.searchKoodisByKoodisto(koodisByKoodistoUri);
        LOG.debug("listaaLukiolinjas data size : " + koodistoData.size());

        final KoulutusKoodiToModelConverter<LukiolinjaModel> kc = new KoulutusKoodiToModelConverter<LukiolinjaModel>();
        List<LukiolinjaModel> list = new ArrayList<LukiolinjaModel>();

        for (KoodiType type : koodistoData) {
            list.add(searchLukiolinjaData(type, locale, kc));
        }

        return list;
    }

    public void listaaLukioSisalto(final KoulutuskoodiModel tutkinto, final LukiolinjaModel lukiolinja, final KoulutusmoduuliKoosteTyyppi tyyppi, final Locale locale) {
        final KoulutusKoodiToModelConverter<KoodiModel> kc = new KoulutusKoodiToModelConverter<KoodiModel>();

        if (tyyppi == null) {
            LOG.error("An invalid data error - KoulutusmoduuliKoosteTyyppi object was null?");
            return;
        }

        komoBaseData(tutkinto, tyyppi, kc, locale);

        //TODO:  Fix this after koodisto data and koodi relations are fixed.
        if (lukiolinja != null) {
            lukiolinja.setKoulutuslaji(listaaKoodi(KoodistoURIHelper.LUKIO_KOODI_KOULUTUSLAJI_URI, kc, locale));
            lukiolinja.setPohjakoulutusvaatimus(listaaKoodi(KoodistoURIHelper.LUKIO_KOODI_POHJAKOULUTUSVAATIMUS_URI, kc, locale));
        }
    }

    public void listaa2asteSisalto(final KoulutuskoodiModel tutkinto, final KoulutusohjelmaModel ohjelma, final KoulutusmoduuliKoosteTyyppi tyyppi, final Locale locale) {
        final KoulutusKoodiToModelConverter<KoodiModel> kc = new KoulutusKoodiToModelConverter<KoodiModel>();

        if (tyyppi == null) {
            LOG.error("An invalid data error - KoulutusmoduuliKoosteTyyppi object was null?");
            return;
        }

        //text data models
        komoBaseData(tutkinto, tyyppi, kc, locale);

        if (ohjelma != null) {
            ohjelma.setTutkintonimike(listaaKoodi(tyyppi.getTutkintonimikeUri(), kc, locale));
            ohjelma.setTavoitteet(KoulutusConveter.convertToMonikielinenTekstiModel(tyyppi.getTavoitteet(), locale));
        }
    }

    /**
     * Search single generic koodi object from Koodisto service. Accepts koodi
     * with URI version information.
     *
     * @param model
     * @param locale
     * @return
     */
    public KoodiModel listaaKoodi(final String uri, final Locale locale) {
        if (uri == null) {
            LOG.warn("Koodisto URI was null - an unknown URI data cannot be loaded.");
            return null;
        }

        final List<KoodiType> koodistoData = tarjontaUiHelper.getKoodis(uri);
        final KoulutusKoodiToModelConverter<KoodiModel> kc = new KoulutusKoodiToModelConverter<KoodiModel>();
        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<KoodiModel> list = kc.mapKoodistoToModel(KoodiModel.class, handleLocale(locale), koodistoData);

            if (!list.isEmpty() && list.size() > 0) {
                return list.get(0);
            }
        } else {
        }

        return null;
    }

    /**
     * Search single koulutusohjelma object from Koodisto service. Accepts koodi
     * with URI version information.
     *
     * @param model
     * @param locale
     * @return
     */
    public KoulutusohjelmaModel listaaKoulutusohjelma(final KoodistoKoodiTyyppi tyyppi, final Locale locale) {
        final List<KoodiType> koodistoData = tarjontaUiHelper.getKoodis(tyyppi.getUri());
        final KoulutusKoodiToModelConverter<KoulutusohjelmaModel> kc = new KoulutusKoodiToModelConverter<KoulutusohjelmaModel>();
        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<KoulutusohjelmaModel> list = kc.mapKoodistoToModel(KoulutusohjelmaModel.class, handleLocale(locale), koodistoData);

            if (!list.isEmpty() && list.size() > 0) {
                return list.get(0);
            }
        }

        return null;
    }

    /**
     * Search single lukiolina object from Koodisto service. Accepts koodi with
     * URI version information.
     *
     * @param model
     * @param locale
     * @return
     */
    public LukiolinjaModel listaaLukiolinja(final KoodistoKoodiTyyppi tyyppi, final Locale locale) {
        final List<KoodiType> koodistoData = tarjontaUiHelper.getKoodis(tyyppi.getUri());
        final KoulutusKoodiToModelConverter<LukiolinjaModel> kc = new KoulutusKoodiToModelConverter<LukiolinjaModel>();
        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<LukiolinjaModel> list = kc.mapKoodistoToModel(LukiolinjaModel.class, handleLocale(locale), koodistoData);

            if (!list.isEmpty() && list.size() > 0) {
                return list.get(0);
            }
        }

        return null;
    }

    /**
     * Search single koulutuskoodi object from Koodisto service.
     *
     * @param model
     * @param locale
     * @return
     */
    public KoulutuskoodiModel listaaKoulutuskoodi(final KoodistoKoodiTyyppi tyyppi, final Locale locale) {
        final List<KoodiType> koodistoData = tarjontaUiHelper.getKoodis(tyyppi.getUri());
        final KoulutusKoodiToModelConverter<KoulutuskoodiModel> kc = new KoulutusKoodiToModelConverter<KoulutuskoodiModel>();
        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<KoulutuskoodiModel> list = kc.mapKoodistoToModel(KoulutuskoodiModel.class, handleLocale(locale), koodistoData);

            if (!list.isEmpty() && list.size() > 0) {
                return list.get(0);
            }
        }

        return null;
    }

    public KoodiModel listaaKoodi(final String uri, final KoulutusKoodiToModelConverter<KoodiModel> kc, final Locale locale) {
        if (uri == null) {
            LOG.warn("Koodisto URI was null - an unknown URI data cannot be loaded.");
            return null;
        }

        final List<KoodiType> koodistoData = tarjontaUiHelper.getKoodis(uri);
        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<KoodiModel> list = kc.mapKoodistoToModel(KoodiModel.class, handleLocale(locale), koodistoData);

            if (!list.isEmpty() && list.size() > 0) {
                return list.get(0);
            }
        } else {
            LOG.warn("Koodisto koodi not found by URI : '" + uri + "'");
        }

        return null;
    }

    private Locale handleLocale(Locale locale) {
        if (locale == null) {
            locale = I18N.getLocale();
        }

        return locale;
    }

    private KoulutuskoodiModel searchKoulutuskoodiData(final KoodiType type, final Locale locale, final KoulutusKoodiToModelConverter<KoulutuskoodiModel> kc) {
        KoulutuskoodiModel koulutuskoodiModel = kc.mapKoodiTypeToModel(KoulutuskoodiModel.class, type, locale);
        return koulutuskoodiModel;
    }

    private KoulutusohjelmaModel searchKoulutusmodelData(final KoodiType type, final Locale locale, final KoulutusKoodiToModelConverter<KoulutusohjelmaModel> kc) {
        KoulutusohjelmaModel model = kc.mapKoodiTypeToModel(KoulutusohjelmaModel.class, type, locale);
        return model;
    }

    private LukiolinjaModel searchLukiolinjaData(final KoodiType type, final Locale locale, final KoulutusKoodiToModelConverter<LukiolinjaModel> kc) {
        LukiolinjaModel model = kc.mapKoodiTypeToModel(LukiolinjaModel.class, type, locale);
        return model;
    }

    private void komoBaseData(final KoulutuskoodiModel tutkinto, final KoulutusmoduuliKoosteTyyppi tyyppi, final KoulutusKoodiToModelConverter<KoodiModel> kc, final Locale locale) {
        //text data models
        tutkinto.setTavoitteet(KoulutusConveter.convertToMonikielinenTekstiModel(tyyppi.getTutkinnonTavoitteet(), locale));
        tutkinto.setJatkoopintomahdollisuudet(KoulutusConveter.convertToMonikielinenTekstiModel(tyyppi.getJatkoOpintoMahdollisuudet(), locale));
        tutkinto.setKoulutuksenRakenne(KoulutusConveter.convertToMonikielinenTekstiModel(tyyppi.getKoulutuksenRakenne(), locale));

        //koodisto koodi data models 
        tutkinto.setOpintojenLaajuus(listaaKoodi(tyyppi.getLaajuusarvoUri(), kc, locale));
        tutkinto.setOpintojenLaajuusyksikko(listaaKoodi(tyyppi.getLaajuusyksikkoUri(), kc, locale));
        tutkinto.setOpintoala(listaaKoodi(tyyppi.getOpintoalaUri(), kc, locale));
        tutkinto.setKoulutusaste(listaaKoodi(tyyppi.getKoulutusasteUri(), kc, locale));
        tutkinto.setKoulutusala(listaaKoodi(tyyppi.getKoulutusalaUri(), kc, locale));
        tutkinto.setTutkintonimike(listaaKoodi(tyyppi.getTutkintonimikeUri(), kc, locale));
    }
}
