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
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
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
    @Autowired
    private KoodiService _koodiService;

    public KoulutusKoodistoConverter() {
        super();
    }

    /**
     * Full KOMO data search from Koodisto service.
     *
     * @param locale
     * @return
     */
    public List<KoulutuskoodiModel> listaaKoulutukset(final Locale locale) {
        LOG.debug(KoodistoURIHelper.KOODISTO_TUTKINTO_URI);
        SearchKoodisByKoodistoCriteriaType criteriUri = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(KoodistoURIHelper.KOODISTO_TUTKINTO_URI);
        List<KoodiType> koodistoData = _koodiService.searchKoodisByKoodisto(criteriUri);
        LOG.debug("listaaKoulutukset data size : " + koodistoData.size());

        final KoulutusKoodiToModelConverter<KoulutuskoodiModel> kc = new KoulutusKoodiToModelConverter<KoulutuskoodiModel>();
        final KoulutusKoodiToModelConverter<KoulutusohjelmaModel> kcOhjelma = new KoulutusKoodiToModelConverter<KoulutusohjelmaModel>();
        final KoulutusKoodiToModelConverter<KoodiModel> kcKoodi = new KoulutusKoodiToModelConverter<KoodiModel>();
        List<KoulutuskoodiModel> list = new ArrayList<KoulutuskoodiModel>();

        for (KoodiType type : koodistoData) {
            list.add(serachKoodistoRelations(type, locale, kc, kcOhjelma, kcKoodi));
        }

        return list;
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
        List<KoulutuskoodiModel> list = new ArrayList<KoulutuskoodiModel>();

        if (koodistoData != null && !koodistoData.isEmpty()) {
            final KoulutusKoodiToModelConverter<KoulutuskoodiModel> kc = new KoulutusKoodiToModelConverter<KoulutuskoodiModel>();
            final KoulutusKoodiToModelConverter<KoulutusohjelmaModel> kcOhjelma = new KoulutusKoodiToModelConverter<KoulutusohjelmaModel>();
            final KoulutusKoodiToModelConverter<KoodiModel> kcKoodi = new KoulutusKoodiToModelConverter<KoodiModel>();

            for (KoodiType type : koodistoData) {
                list.add(serachKoodistoRelations(type, locale, kc, kcOhjelma, kcKoodi));
            }

            if (!list.isEmpty() && list.size() > 0) {
                return list.get(0);
            }
        }

        return null;
    }

    /**
     * Search single koulutusohjelma object from Koodisto service.
     * Accepts koodi with URI version information.
     *
     * @param model
     * @param locale
     * @return
     */
    public KoulutusohjelmaModel listaaKoulutusohjelma(final KoodistoKoodiTyyppi tyyppi, final Locale locale) {
        final List<KoodiType> koodistoData = tarjontaUiHelper.gethKoodis(tyyppi.getUri());
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
     * Search single generic koodi object from Koodisto service.
     * Accepts koodi with URI version information.
     *
     * @param model
     * @param locale
     * @return
     */
    public KoodiModel listaaKoodi(final String uri, final Locale locale) {
        final List<KoodiType> koodistoData = tarjontaUiHelper.gethKoodis(uri);
        final KoulutusKoodiToModelConverter<KoodiModel> kc = new KoulutusKoodiToModelConverter<KoodiModel>();
        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<KoodiModel> list = kc.mapKoodistoToModel(KoodiModel.class, handleLocale(locale), koodistoData);

            if (!list.isEmpty() && list.size() > 0) {
                return list.get(0);
            }
        }

        return null;
    }

    private Locale handleLocale(Locale locale) {
        if (locale == null) {
            locale = I18N.getLocale();
        }

        return locale;
    }

    private boolean matchUris(final String uri1, final String uri2) {
        return uri1.equals(uri2);
    }

    private KoulutuskoodiModel serachKoodistoRelations(final KoodiType type, final Locale locale,
            final KoulutusKoodiToModelConverter<KoulutuskoodiModel> kc,
            final KoulutusKoodiToModelConverter<KoulutusohjelmaModel> kcOhjelma,
            final KoulutusKoodiToModelConverter<KoodiModel> kcKoodi) {

        KoodiUriAndVersioType koodiUriAndVersioType = new KoodiUriAndVersioType();
        koodiUriAndVersioType.setKoodiUri(type.getKoodiUri());
        koodiUriAndVersioType.setVersio(type.getVersio());
        List<KoodiType> listKoodiByRelation = _koodiService.listKoodiByRelation(koodiUriAndVersioType, false, SuhteenTyyppiType.SISALTYY);
        KoulutuskoodiModel koulutuskoodiModel = kc.mapKoodiTypeToModel(KoulutuskoodiModel.class, type, locale);

        for (KoodiType relation : listKoodiByRelation) {
            final String koodistoUri = relation.getKoodisto().getKoodistoUri();

            if (matchUris(KoodistoURIHelper.KOODISTO_KOULUTUSOHJELMA_URI, koodistoUri)) {
                //add objects to koulutusohjelma
                koulutuskoodiModel.getKoulutusohjelmaModels().add(populateTutkintonimike(relation, locale, kcOhjelma, kcKoodi));
               // koulutuskoodiModel.getKoulutusohjelmaModels().add(kcOhjelma.mapKoodiTypeToModel(KoulutusohjelmaModel.class, relation, locale));
            } else if (matchUris(KoodistoURIHelper.KOODISTO_KOULUTUSASTE_URI, koodistoUri)) {
                koulutuskoodiModel.setKoulutusaste(kcKoodi.mapKoodiTypeToModel(KoodiModel.class, relation, locale));
            } else if (matchUris(KoodistoURIHelper.KOODISTO_KOULUTUSALA_URI, koodistoUri)) {
                koulutuskoodiModel.setKoulutusala(kcKoodi.mapKoodiTypeToModel(KoodiModel.class, relation, locale));
            } else if (matchUris(KoodistoURIHelper.KOODISTO_KOULUTUKSEN_RAKENNE_URI, koodistoUri)) {
                koulutuskoodiModel.setKoulutuksenRakenne(kcKoodi.mapKoodiTypeToModel(KoodiModel.class, relation, locale));
            } else if (matchUris(KoodistoURIHelper.KOODISTO_OPINTOALA_URI, koodistoUri)) {
                koulutuskoodiModel.setOpintoala(kcKoodi.mapKoodiTypeToModel(KoodiModel.class, relation, locale));
            } else if (matchUris(KoodistoURIHelper.KOODISTO_OPINTOJEN_LAAJUUS_URI, koodistoUri)) {
                koulutuskoodiModel.setOpintojenLaajuus(kcKoodi.mapKoodiTypeToModel(KoodiModel.class, relation, locale));
            } else if (matchUris(KoodistoURIHelper.KOODISTO_TAVOITTEET_URI, koodistoUri)) {
                LOG.debug("Tavoitteet : " + kcKoodi.mapKoodiTypeToModel(KoodiModel.class, relation, locale));
                
                koulutuskoodiModel.setTavoitteet(kcKoodi.mapKoodiTypeToModel(KoodiModel.class, relation, locale));
            } else if (matchUris(KoodistoURIHelper.KOODISTO_JATKOOPINTOMAHDOLLISUUDET_URI, koodistoUri)) {
                koulutuskoodiModel.setJatkoopintomahdollisuudet(kcKoodi.mapKoodiTypeToModel(KoodiModel.class, relation, locale));
            }
        }

        return koulutuskoodiModel;
    }

    public KoulutusohjelmaModel populateTutkintonimike(final KoodiType type, final Locale locale, final KoulutusKoodiToModelConverter<KoulutusohjelmaModel> kc, final KoulutusKoodiToModelConverter<KoodiModel> kc2) {
        KoodiUriAndVersioType koodiUriAndVersioType = new KoodiUriAndVersioType();
        koodiUriAndVersioType.setKoodiUri(type.getKoodiUri());
        koodiUriAndVersioType.setVersio(type.getVersio());
        List<KoodiType> listKoodiByRelation = _koodiService.listKoodiByRelation(koodiUriAndVersioType, false, SuhteenTyyppiType.SISALTYY);
        KoulutusohjelmaModel koulutuohjelmaModel = kc.mapKoodiTypeToModel(KoulutusohjelmaModel.class, type, locale);

        for (KoodiType relation : listKoodiByRelation) {
            final String koodistoUri = relation.getKoodisto().getKoodistoUri();
            if (matchUris(KoodistoURIHelper.KOODISTO_TUTKINTONIMIKE_URI, koodistoUri)) {
                koulutuohjelmaModel.setTutkintonimike(kc2.mapKoodiTypeToModel(KoodiModel.class, relation, locale));
            }
        }
        
        return koulutuohjelmaModel;
    }
}
