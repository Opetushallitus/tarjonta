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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KoulutuskoodiRowModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.LukiolinjaModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jani Wilén
 */
@Component
public class KoulutusKoodistoConverter {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusKoodistoConverter.class);
    private static final KoulutusKoodiToModelConverter<KoodiModel> koulutusKoodiToKoodiModel = new KoulutusKoodiToModelConverter<KoodiModel>();
    private static final KoulutusKoodiToModelConverter<LukiolinjaModel> koulutusKoodiToLukiolinjaModel = new KoulutusKoodiToModelConverter<LukiolinjaModel>();
    private static final KoulutusKoodiToModelConverter<KoulutusohjelmaModel> koulutusKoodiToKoulutusohjelmaModel = new KoulutusKoodiToModelConverter<KoulutusohjelmaModel>();
    private static final KoulutusKoodiToModelConverter<KoulutuskoodiModel> koulutusKoodiToKoulutuskoodiModel = new KoulutusKoodiToModelConverter<KoulutuskoodiModel>();
    private static final KoulutusKoodiToModelConverter<KoulutuskoodiRowModel> koulutusKoodiToKoulutuskoodiRowModel = new KoulutusKoodiToModelConverter<KoulutuskoodiRowModel>();
    @Autowired(required = true)
    private TarjontaUIHelper tarjontaUiHelper;
    @Autowired
    private KoodiService koodiService;
    @Value("${tarjonta.koulutusaste.korkeakoulut:NOT_SET}")
    private String[] korkeakouluKoodiarvos = null;

    public KoulutusKoodistoConverter() {
        super();
    }

    public List<KoulutuskoodiModel> listaaKoulutukses(final KoulutusKoodistoModel model, final Locale locale) {
        HashSet<String> setOfUri = Sets.<String>newHashSet();
        setOfUri.add(model.getKoodistoUriVersio());
        return listaaKoulutukses(setOfUri, locale);

    }

    public List<KoulutuskoodiModel> listaaKoulutukses(final Set<String> koodiUris, final Locale locale) {
        LOG.debug(KoodistoURIHelper.KOODISTO_TUTKINTO_URI);
        List<String> listKoodiUris = noVersionUris(koodiUris);

        SearchKoodisByKoodistoCriteriaType koodisByKoodistoUri = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(listKoodiUris, KoodistoURIHelper.KOODISTO_TUTKINTO_URI);

        List<KoodiType> koodistoData = koodiService.searchKoodisByKoodisto(koodisByKoodistoUri);
        LOG.debug("listaaKoulutukses data size : " + koodistoData.size());

        List<KoulutuskoodiModel> list = new ArrayList<KoulutuskoodiModel>();
        Converter<KoulutuskoodiModel> converter = new Converter<KoulutuskoodiModel>();

        for (KoodiType type : koodistoData) {
            list.add(converter.transform(type, locale, koulutusKoodiToKoulutuskoodiModel, KoulutuskoodiModel.class));
        }

        return list;
    }

    public List<KoulutusohjelmaModel> listaaKoulutusohjelmas(final List<KoulutusmoduuliKoosteTyyppi> komos, final Locale locale) {
        LOG.debug(KoodistoURIHelper.KOODISTO_KOULUTUSOHJELMA_URI);

        Preconditions.checkNotNull(komos, "List of KoulutusmoduuliKoosteTyyppi object cannot be null.");

        List<String> listKoodiUris = new ArrayList<String>();
        for (KoulutusmoduuliKoosteTyyppi t : komos) {
            listKoodiUris.add(noVersionUri(t.getKoulutusohjelmakoodiUri()));
        }
        SearchKoodisByKoodistoCriteriaType koodisByKoodistoUri = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(listKoodiUris, KoodistoURIHelper.KOODISTO_KOULUTUSOHJELMA_URI);
        List<KoodiType> koodistoData = koodiService.searchKoodisByKoodisto(koodisByKoodistoUri);
        LOG.debug("listaaKoulutusohjelmas data size : " + koodistoData.size());

        List<KoulutusohjelmaModel> list = new ArrayList<KoulutusohjelmaModel>();

        for (KoodiType type : koodistoData) {
            list.add(searchKoulutusmodelData(type, locale, koulutusKoodiToKoulutusohjelmaModel));
        }

        return list;
    }

    public List<LukiolinjaModel> listaaLukiolinjas(final List<KoulutusmoduuliKoosteTyyppi> komos, final Locale locale) {
        LOG.debug(KoodistoURIHelper.KOODISTO_LUKIOLINJA_URI);

        List<String> listKoodiUris = new ArrayList<String>();
        for (KoulutusmoduuliKoosteTyyppi t : komos) {
            listKoodiUris.add(noVersionUri(t.getLukiolinjakoodiUri()));
        }
        SearchKoodisByKoodistoCriteriaType koodisByKoodistoUri = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(listKoodiUris, KoodistoURIHelper.KOODISTO_LUKIOLINJA_URI);
        List<KoodiType> koodistoData = koodiService.searchKoodisByKoodisto(koodisByKoodistoUri);
        LOG.debug("listaaLukiolinjas data size : " + koodistoData.size());

        List<LukiolinjaModel> list = new ArrayList<LukiolinjaModel>();

        for (KoodiType type : koodistoData) {
            list.add(searchLukiolinjaData(type, locale, koulutusKoodiToLukiolinjaModel));
        }

        return list;
    }

    public void listaaLukioSisalto(final KoulutuskoodiModel tutkinto, final LukiolinjaModel lukiolinja, final KoulutusmoduuliKoosteTyyppi tyyppi, final Locale locale) {
        if (tyyppi == null) {
            LOG.error("An invalid data error - KoulutusmoduuliKoosteTyyppi object was null?");
            return;
        }

        komoBaseData(tutkinto, tyyppi, koulutusKoodiToKoodiModel, locale);

        //TODO:  Fix this after koodisto data and koodi relations are fixed.
        if (lukiolinja != null) {
            lukiolinja.setKoulutuslaji(listaaKoodi(KoodistoURIHelper.LUKIO_KOODI_KOULUTUSLAJI_URI, koulutusKoodiToKoodiModel, locale));
            lukiolinja.setPohjakoulutusvaatimus(listaaKoodi(KoodistoURIHelper.LUKIO_KOODI_POHJAKOULUTUSVAATIMUS_URI, koulutusKoodiToKoodiModel, locale));
        }
    }

    public void listaa2asteSisalto(final KoulutuskoodiModel tutkinto, final KoulutusohjelmaModel ohjelma, final KoulutusmoduuliKoosteTyyppi tyyppi, final Locale locale) {

        if (tyyppi == null) {
            LOG.error("An invalid data error - KoulutusmoduuliKoosteTyyppi object was null?");
            return;
        }

        //text data models
        komoBaseData(tutkinto, tyyppi, koulutusKoodiToKoodiModel, locale);

        if (ohjelma != null) {
            ohjelma.setTutkintonimike(listaaKoodi(tyyppi.getTutkintonimikeUri(), koulutusKoodiToKoodiModel, locale));
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
        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<KoodiModel> list = koulutusKoodiToKoodiModel.mapKoodistoToModel(KoodiModel.class, handleLocale(locale), koodistoData);

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
        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<KoulutusohjelmaModel> list = koulutusKoodiToKoulutusohjelmaModel.mapKoodistoToModel(KoulutusohjelmaModel.class, handleLocale(locale), koodistoData);

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

        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<LukiolinjaModel> list = koulutusKoodiToLukiolinjaModel.mapKoodistoToModel(LukiolinjaModel.class, handleLocale(locale), koodistoData);

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
        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<KoulutuskoodiModel> list = koulutusKoodiToKoulutuskoodiModel.mapKoodistoToModel(KoulutuskoodiModel.class, handleLocale(locale), koodistoData);

            if (!list.isEmpty() && list.size() > 0) {
                return list.get(0);
            }
        }

        return null;
    }
    
    public KoulutuskoodiModel listaaKoulutuskoodi(final String koulutuskoodiUri, final Locale locale) {
        final List<KoodiType> koodistoData = tarjontaUiHelper.getKoodis(koulutuskoodiUri);
        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<KoulutuskoodiModel> list = koulutusKoodiToKoulutuskoodiModel.mapKoodistoToModel(KoulutuskoodiModel.class, handleLocale(locale), koodistoData);

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

    private KoulutusohjelmaModel searchKoulutusmodelData(final KoodiType type, final Locale locale, final KoulutusKoodiToModelConverter<KoulutusohjelmaModel> kc) {
        return kc.mapKoodiTypeToModel(KoulutusohjelmaModel.class, type, locale);
    }

    private LukiolinjaModel searchLukiolinjaData(final KoodiType type, final Locale locale, final KoulutusKoodiToModelConverter<LukiolinjaModel> kc) {
        return kc.mapKoodiTypeToModel(LukiolinjaModel.class, type, locale);
    }

    private void komoBaseData(final KoulutuskoodiModel tutkinto, final KoulutusmoduuliKoosteTyyppi tyyppi, final KoulutusKoodiToModelConverter<KoodiModel> kc, final Locale locale) {
        //text data models
        tutkinto.setTavoitteet(KoulutusConveter.convertToMonikielinenTekstiModel(tyyppi.getTutkinnonTavoitteet(), locale));
        tutkinto.setJatkoopintomahdollisuudet(KoulutusConveter.convertToMonikielinenTekstiModel(tyyppi.getJatkoOpintoMahdollisuudet(), locale));
        tutkinto.setKoulutuksenRakenne(KoulutusConveter.convertToMonikielinenTekstiModel(tyyppi.getKoulutuksenRakenne(), locale));

        //koodisto koodi data models 
        tutkinto.setOpintojenLaajuus(tyyppi.getLaajuusarvoUri());
        tutkinto.setOpintojenLaajuusyksikko(listaaKoodi(tyyppi.getLaajuusyksikkoUri(), kc, locale));
        tutkinto.setOpintoala(listaaKoodi(tyyppi.getOpintoalaUri(), kc, locale));
        tutkinto.setKoulutusaste(listaaKoodi(tyyppi.getKoulutusasteUri(), kc, locale));
        tutkinto.setKoulutusala(listaaKoodi(tyyppi.getKoulutusalaUri(), kc, locale));
        tutkinto.setTutkintonimike(listaaKoodi(tyyppi.getTutkintonimikeUri(), kc, locale));
    }

    public List<KoulutuskoodiRowModel> listaaKoulutuksesByKoulutusala(String koulutusalaKoodiUri, final Locale locale) {
        final Set<String> korkeakoulutKoulutusasteUris = convertToKoulutusasteUris(korkeakouluKoodiarvos);
        Collection<KoodiType> koulutuskoodisByAste = tarjontaUiHelper.getKoulutusasteRelatedKoulutuskoodis(korkeakoulutKoulutusasteUris);

        if (koulutusalaKoodiUri != null && !koulutusalaKoodiUri.isEmpty()) {
            Collection<KoodiType> koulutuskoodisByAla = tarjontaUiHelper.getKoulutusalaRelatedKoulutuskoodis(koulutusalaKoodiUri);
            koulutuskoodisByAste.retainAll(koulutuskoodisByAla);
        }

        return convertToKoulutuskoodiRowModels(koulutuskoodisByAste, locale);
    }

    private List<KoulutuskoodiRowModel> convertToKoulutuskoodiRowModels(final Collection<KoodiType> koodistoData, final Locale locale) {
        List<KoulutuskoodiRowModel> list = Lists.<KoulutuskoodiRowModel>newArrayList();
        if (koodistoData != null && !koodistoData.isEmpty()) {
            Converter<KoulutuskoodiRowModel> converter = new Converter<KoulutuskoodiRowModel>();

            for (KoodiType type : koodistoData) {
                list.add(converter.transform(type, locale, koulutusKoodiToKoulutuskoodiRowModel, KoulutuskoodiRowModel.class));
            }
        }
        return list;
    }

    /**
     * Remove version information ('#X') from Koodisto uri.
     *
     * @param uriWithVersion
     * @return
     */
    private static String noVersionUri(String uriWithVersion) {
        String[] splitKoodiURI = TarjontaUIHelper.splitKoodiURI(uriWithVersion);
        return splitKoodiURI[TarjontaUIHelper.PLAIN_URI];
    }

    /**
     * Remove version information ('#X') from set of Koodisto uris.
     *
     * @param uriWithVersion
     * @return
     */
    private static List<String> noVersionUris(Set<String> uris) {
        List<String> listKoodiUrisWithoutVersion = Lists.<String>newArrayList();
        for (String uri : uris) {
            listKoodiUrisWithoutVersion.add(noVersionUri(uri));
        }
        return listKoodiUrisWithoutVersion;
    }

    private class Converter<MODEL extends KoulutusKoodistoModel> {

        public MODEL transform(final KoodiType type, final Locale locale, final KoulutusKoodiToModelConverter<MODEL> kc, Class clazz) {
            return kc.mapKoodiTypeToModel(clazz, type, locale);
        }
    }

    private static Set<String> convertToKoulutusasteUris(String[] koulutusasteKoodiarvos) {
        Set<String> koodiUris = Sets.<String>newHashSet();
        for (String arvo : koulutusasteKoodiarvos) {
            koodiUris.add(KoodistoURIHelper.KOODISTO_KOULUTUSASTE_URI + "_" + arvo);
        }
        return Collections.unmodifiableSet(koodiUris);
    }
}
