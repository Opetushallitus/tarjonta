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
package fi.vm.sade.tarjonta.koodisto;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.publication.model.RestParam;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusCommonConverter;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliAmmatillinenRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliKorkeakouluRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliLukioRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliStandardRelationV1RDTO;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jani Wilén
 * @param <TYPE> custom relation object
 */
@Component
public class KoulutuskoodiRelations<TYPE extends KoulutusmoduuliStandardRelationV1RDTO> {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutuskoodiRelations.class);

    private static final String KORKEAKOULU[] = new String[]{
        KoodistoURI.KOODISTO_OPINTOALA_URI,
        KoodistoURI.KOODISTO_TUTKINTO_NIMI_URI,
        KoodistoURI.KOODISTO_TUTKINTONIMIKE_KORKEAKOULU_URI,
        KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI,
        KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSARVO_URI,
        KoodistoURI.KOODISTO_KOULUTUSASTE_URI,
        KoodistoURI.KOODISTO_EQF_LUOKITUS_URI,
        KoodistoURI.KOODISTO_KOULUTUSALA_URI,
        KoodistoURI.KOODISTO_TARJONTA_KOULUTUSTYYPPI
    };

    private static final String ASTE2_AMM[] = new String[]{
        KoodistoURI.KOODISTO_OPINTOALA_URI,
        KoodistoURI.KOODISTO_TUTKINTO_NIMI_URI,
        KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI,
        KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI,
        KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSARVO_URI,
        KoodistoURI.KOODISTO_KOULUTUSASTE_URI,
        KoodistoURI.KOODISTO_EQF_LUOKITUS_URI,
        KoodistoURI.KOODISTO_KOULUTUSALA_URI,
        KoodistoURI.KOODISTO_TARJONTA_KOULUTUSTYYPPI,
        KoodistoURI.KOODISTO_OSAAMISALA_URI
    };

    private static final String LUKIO[] = new String[]{
        KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI,
        KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI,
        KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSARVO_URI,
        KoodistoURI.KOODISTO_KOULUTUSALA_URI,
        KoodistoURI.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI,
        KoodistoURI.KOODISTO_KOULUTUSLAJI_URI,
        KoodistoURI.KOODISTO_EQF_LUOKITUS_URI,
        KoodistoURI.KOODISTO_TARJONTA_KOULUTUSTYYPPI
    };

    @Autowired(required = true)
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    @Autowired(required = true)
    private KoulutusCommonConverter commonConverter;

    /**
     * Search code relations to given koodisto code from koulutus-, lukiolinja
     * and koulutusohjelmaamm-koodistos.
     *
     * @param clazz custom return object type.
     * @param defaults default values for the return data
     * @param koodiUri can be any koodisto code, not just the koulutus-code.
     * @param restParam user's locale etc., default location is 'FI'
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public TYPE getKomoRelationByKoulutuskoodiUri(Class<TYPE> clazz, final KoulutusmoduuliStandardRelationV1RDTO defaults, final String koodiUri, final RestParam restParam) throws InstantiationException, IllegalAccessException {
        Preconditions.checkNotNull(koodiUri, "Koodisto URI cannot be null.");
        TYPE dto = clazz.newInstance();

        if (defaults != null) {
            if (hasDefaultValueUri(defaults.getKoulutustyyppi())) {
                dto.setKoulutustyyppi(singleKoodi(defaults.getKoulutustyyppi().getUri(), FieldNames.KOULUTUSTYYPPI, restParam));
            }
            if (hasDefaultValueUri(defaults.getEqf())) {
                dto.setEqf(singleKoodi(defaults.getEqf().getUri(), FieldNames.EQF, restParam));
            }
            if (hasDefaultValueUri(defaults.getKoulutusala())) {
                dto.setKoulutusala(singleKoodi(defaults.getKoulutusala().getUri(), FieldNames.KOULUTUSALA, restParam));
            }
            if (hasDefaultValueUri(defaults.getKoulutusaste())) {
                dto.setKoulutusaste(singleKoodi(defaults.getKoulutusaste().getUri(), FieldNames.KOULUTUSASTE, restParam));
            }
            if (hasDefaultValueUri(defaults.getKoulutuskoodi())) {
                dto.setKoulutuskoodi(singleKoodi(defaults.getKoulutuskoodi().getUri(), FieldNames.KOULUTUS, restParam));
            }
            if (hasDefaultValueUri(defaults.getOpintoala())) {
                dto.setOpintoala(singleKoodi(defaults.getOpintoala().getUri(), FieldNames.OPINTOALA, restParam));
            }
            if (hasDefaultValueUri(defaults.getOpintojenLaajuusyksikko())) {
                dto.setOpintojenLaajuusyksikko(singleKoodi(defaults.getOpintojenLaajuusyksikko().getUri(), FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, restParam));
            }
            if (hasDefaultValueUri(defaults.getTutkinto())) {
                dto.setTutkinto(singleKoodi(defaults.getTutkinto().getUri(), FieldNames.TUTKINTO, restParam));
            }
            if (hasDefaultValueUri(defaults.getEqf())) {
                dto.setEqf(singleKoodi(defaults.getEqf().getUri(), FieldNames.EQF, restParam));
            }

            if (defaults instanceof KoulutusmoduuliKorkeakouluRelationV1RDTO) {
                //TODO: add missing custom fields, if needed
            } else if (dto instanceof KoulutusmoduuliLukioRelationV1RDTO && defaults instanceof KoulutusmoduuliLukioRelationV1RDTO) {
                KoulutusmoduuliLukioRelationV1RDTO lk = (KoulutusmoduuliLukioRelationV1RDTO) dto;
                KoulutusmoduuliLukioRelationV1RDTO lkDefaults = (KoulutusmoduuliLukioRelationV1RDTO) defaults;

                if (hasDefaultValueUri(lkDefaults.getPohjakoulutusvaatimus())) {
                    lk.setPohjakoulutusvaatimus(singleKoodi(lkDefaults.getPohjakoulutusvaatimus().getUri(), FieldNames.POHJALKOULUTUSVAATIMUS, restParam));
                }
                if (hasDefaultValueUri(lkDefaults.getOpintojenLaajuusarvo())) {
                    lk.setOpintojenLaajuusarvo(singleKoodi(lkDefaults.getOpintojenLaajuusarvo().getUri(), FieldNames.OPINTOJEN_LAAJUUSARVO, restParam));
                }
                if (hasDefaultValueUri(lkDefaults.getKoulutuslaji())) {
                    lk.setKoulutuslaji(singleKoodi(lkDefaults.getKoulutuslaji().getUri(), FieldNames.KOULUTUSLAJI, restParam));
                }
                if (hasDefaultValueUri(lkDefaults.getTutkintonimike())) {
                    lk.setTutkintonimike(singleKoodi(lkDefaults.getTutkintonimike().getUri(), FieldNames.TUTKINTONIMIKE, restParam));
                }
            } else if (dto instanceof KoulutusmoduuliAmmatillinenRelationV1RDTO && defaults instanceof KoulutusmoduuliAmmatillinenRelationV1RDTO) {
                KoulutusmoduuliAmmatillinenRelationV1RDTO lk = (KoulutusmoduuliAmmatillinenRelationV1RDTO) dto;
                KoulutusmoduuliAmmatillinenRelationV1RDTO lkDefaults = (KoulutusmoduuliAmmatillinenRelationV1RDTO) defaults;

                if (hasDefaultValueUri(lkDefaults.getPohjakoulutusvaatimus())) {
                    lk.setPohjakoulutusvaatimus(singleKoodi(lkDefaults.getPohjakoulutusvaatimus().getUri(), FieldNames.POHJALKOULUTUSVAATIMUS, restParam));
                }
                if (hasDefaultValueUri(lkDefaults.getOpintojenLaajuusarvo())) {
                    lk.setOpintojenLaajuusarvo(singleKoodi(lkDefaults.getOpintojenLaajuusarvo().getUri(), FieldNames.OPINTOJEN_LAAJUUSARVO, restParam));
                }
                if (hasDefaultValueUri(lkDefaults.getKoulutuslaji())) {
                    lk.setKoulutuslaji(singleKoodi(lkDefaults.getKoulutuslaji().getUri(), FieldNames.KOULUTUSLAJI, restParam));
                }
                if (hasDefaultValueUri(lkDefaults.getTutkintonimike())) {
                    lk.setTutkintonimike(singleKoodi(lkDefaults.getTutkintonimike().getUri(), FieldNames.TUTKINTONIMIKE, restParam));
                }
                if (hasDefaultValueUri(lkDefaults.getOsaamisala())) {
                    lk.setOsaamisala(singleKoodi(lkDefaults.getOsaamisala().getUri(), FieldNames.OSAAMISALA, restParam));
                }
                if (hasDefaultValueUri(lkDefaults.getKoulutusohjelma())) {
                    lk.setKoulutusohjelma(singleKoodi(lkDefaults.getKoulutusohjelma().getUri(), FieldNames.KOULUTUSOHJELMA, restParam));
                }
            }
        }

        final Collection<KoodiType> koodistoRelations = getKoulutusRelations(koodiUri, dto);

        //Set koodisto data to correct dto fields.
        for (KoodiType type : koodistoRelations) {
            if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_KOULUTUSALA_URI)) {
                dto.setKoulutusala(singleKoodi(type.getKoodiUri(), FieldNames.KOULUTUSALA, restParam));

            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_TUTKINTO_URI)) {
                //THIS IS THE KOULUTUS-koodisto NOT TUTKINTO-koodisto!
                //TODO:  PLEASE RENAME THE KEY
                dto.setKoulutuskoodi(singleKoodi(type.getKoodiUri(), FieldNames.KOULUTUS, restParam));

            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_TARJONTA_KOULUTUSTYYPPI)) {
                dto.setKoulutustyyppi(singleKoodi(type.getKoodiUri(), FieldNames.KOULUTUSTYYPPI, restParam));

            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_LUKIOLINJA_URI)) {
                if (dto instanceof KoulutusmoduuliLukioRelationV1RDTO) {
                    KoulutusmoduuliLukioRelationV1RDTO lk = (KoulutusmoduuliLukioRelationV1RDTO) dto;
                    //in lukio : 'koulutusohjelma' == 'lukiolinja'
                    lk.setKoulutusohjelma(singleKoodi(type.getKoodiUri(), FieldNames.LUKIOLINJA, restParam));
                    lk.setLukiolinja(singleKoodi(type.getKoodiUri(), FieldNames.LUKIOLINJA, restParam));
                }

            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSARVO_URI)) {
                if (dto instanceof KoulutusmoduuliKorkeakouluRelationV1RDTO) {
                    KoulutusmoduuliKorkeakouluRelationV1RDTO kk = (KoulutusmoduuliKorkeakouluRelationV1RDTO) dto;
                    multipleKoodis(type, kk.getOpintojenLaajuusarvos(), FieldNames.OPINTOJEN_LAAJUUSARVO, restParam);
                } else if (dto instanceof KoulutusmoduuliLukioRelationV1RDTO) {
                    KoulutusmoduuliLukioRelationV1RDTO lk = (KoulutusmoduuliLukioRelationV1RDTO) dto;
                    lk.setOpintojenLaajuusarvo(singleKoodi(type.getKoodiUri(), FieldNames.OPINTOJEN_LAAJUUSARVO, restParam));
                }

            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI)) {
                dto.setOpintojenLaajuusyksikko(singleKoodi(type.getKoodiUri(), FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, restParam));

            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_OPINTOALA_URI)) {
                dto.setOpintoala(singleKoodi(type.getKoodiUri(), FieldNames.OPINTOALA, restParam));

            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI)) {
                if (dto instanceof KoulutusmoduuliLukioRelationV1RDTO) {
                    KoulutusmoduuliLukioRelationV1RDTO lk = (KoulutusmoduuliLukioRelationV1RDTO) dto;
                    lk.setTutkintonimike(singleKoodi(type.getKoodiUri(), FieldNames.TUTKINTONIMIKE, restParam));
                } else if (dto instanceof KoulutusmoduuliAmmatillinenRelationV1RDTO) {
                    KoulutusmoduuliAmmatillinenRelationV1RDTO lk = (KoulutusmoduuliAmmatillinenRelationV1RDTO) dto;
                    lk.setTutkintonimike(singleKoodi(type.getKoodiUri(), FieldNames.TUTKINTONIMIKE, restParam));
                }
            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_TUTKINTONIMIKE_KORKEAKOULU_URI)) {
                if (dto instanceof KoulutusmoduuliKorkeakouluRelationV1RDTO) {
                    KoulutusmoduuliKorkeakouluRelationV1RDTO kk = (KoulutusmoduuliKorkeakouluRelationV1RDTO) dto;
                    multipleKoodis(type, kk.getTutkintonimikes(), FieldNames.TUTKINTONIMIKE, restParam);
                }

            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_TUTKINTO_NIMI_URI)) {
                dto.setTutkinto(singleKoodi(type.getKoodiUri(), FieldNames.TUTKINTO, restParam));

            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_KOULUTUSASTE_URI)) {
                dto.setKoulutusaste(singleKoodi(type.getKoodiUri(), FieldNames.KOULUTUSASTE, restParam));

            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_EQF_LUOKITUS_URI)) {
                dto.setEqf(singleKoodi(type.getKoodiUri(), FieldNames.EQF, restParam));

            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI)) {
                if (dto instanceof KoulutusmoduuliLukioRelationV1RDTO) {
                    KoulutusmoduuliLukioRelationV1RDTO lk = (KoulutusmoduuliLukioRelationV1RDTO) dto;
                    lk.setPohjakoulutusvaatimus(singleKoodi(type.getKoodiUri(), FieldNames.POHJALKOULUTUSVAATIMUS, restParam));
                }

            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_KOULUTUSLAJI_URI)) {
                if (dto instanceof KoulutusmoduuliLukioRelationV1RDTO) {
                    KoulutusmoduuliLukioRelationV1RDTO lk = (KoulutusmoduuliLukioRelationV1RDTO) dto;
                    lk.setKoulutuslaji(singleKoodi(type.getKoodiUri(), FieldNames.KOULUTUSLAJI, restParam));
                } else if (dto instanceof KoulutusmoduuliAmmatillinenRelationV1RDTO) {
                    KoulutusmoduuliAmmatillinenRelationV1RDTO lk = (KoulutusmoduuliAmmatillinenRelationV1RDTO) dto;
                    lk.setKoulutuslaji(singleKoodi(type.getKoodiUri(), FieldNames.KOULUTUSLAJI, restParam));
                }

            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_OSAAMISALA_URI)) {
                if (dto instanceof KoulutusmoduuliAmmatillinenRelationV1RDTO) {
                    KoulutusmoduuliAmmatillinenRelationV1RDTO lk = (KoulutusmoduuliAmmatillinenRelationV1RDTO) dto;
                    lk.setKoulutusohjelma(singleKoodi(type.getKoodiUri(), FieldNames.OSAAMISALA, restParam));
                    lk.setOsaamisala(singleKoodi(type.getKoodiUri(), FieldNames.OSAAMISALA, restParam));
                }
            } else if (hasRelationToKoodisto(type, KoodistoURI.KOODISTO_KOULUTUSOHJELMA_URI)) {
                if (dto instanceof KoulutusmoduuliAmmatillinenRelationV1RDTO) {
                    KoulutusmoduuliAmmatillinenRelationV1RDTO lk = (KoulutusmoduuliAmmatillinenRelationV1RDTO) dto;
                    lk.setKoulutusohjelma(singleKoodi(type.getKoodiUri(), FieldNames.KOULUTUSOHJELMA, restParam));
                }
            } else {
                LOG.trace("Ignored koodisto relation : '{}'", type.getKoodisto().getKoodistoUri());
            }
        }

        return dto;
    }

    private static boolean hasRelationToKoodisto(final KoodiType type, final String koodisto) {
        return type.getKoodisto().getKoodistoUri().equals(koodisto);
    }

    private void multipleKoodis(final KoodiType type, KoodiUrisV1RDTO koodiUris, final FieldNames fieldNames, RestParam restParam) {
        KoodiUriAndVersioType uriAndVersion = new KoodiUriAndVersioType();
        uriAndVersion.setKoodiUri(type.getKoodiUri());
        uriAndVersion.setVersio(type.getVersio());
        commonConverter.addToKoodiUrisMap(koodiUris, uriAndVersion, restParam.getLocale(), fieldNames, restParam.getShowMeta());
    }

    private Collection<KoodiType> getKoulutusRelations(final String koodiUri, final TYPE obj) {
        Preconditions.checkNotNull(koodiUri, "URI cannot be null");
        Collection<KoodiType> koodiTypes = Lists.<KoodiType>newArrayList();

        //add target koodi item to the list
        koodiTypes.add(tarjontaKoodistoHelper.getKoodiByUri(koodiUri));

        //search relations for the target uri
        for (String koodistoUri : koodisByAste(obj)) {
            koodiTypes.addAll(tarjontaKoodistoHelper.getKoodistoRelations(koodiUri, koodistoUri, SuhteenTyyppiType.SISALTYY, false));
        }

        return koodiTypes;
    }

    /**
     * Search single generic koodi object from Koodisto service. Accepts koodi
     * with URI version information.
     *
     * @param model
     * @param locale
     * @return
     */
    private KoodiV1RDTO singleKoodi(final String uri, final FieldNames fieldName, final RestParam restParam) {
        Preconditions.checkNotNull(uri, "Koodisto URI was null - an unknown URI data cannot be loaded.");
        return commonConverter.convertToKoodiDTO(uri, null, fieldName, KoulutusCommonConverter.Nullable.YES, restParam);
    }

    private String[] koodisByAste(final TYPE obj) {
        if (obj instanceof KoulutusmoduuliKorkeakouluRelationV1RDTO) {
            return KORKEAKOULU;
        } else if (obj instanceof KoulutusmoduuliLukioRelationV1RDTO) {
            return LUKIO;
        } else if (obj instanceof KoulutusmoduuliAmmatillinenRelationV1RDTO) {
            return ASTE2_AMM;
        } else {
            return ASTE2_AMM; //TODO : return ???
        }

    }

    private static boolean hasDefaultValueUri(KoodiV1RDTO koodi) {
        return koodi != null && koodi.getUri() != null && !koodi.getUri().isEmpty();

    }
}
