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
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusCommonConverter;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliKorkeakouluRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliLukioRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliStandardRelationV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.Collection;
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
public class KoulutuskoodiRelations<T extends KoulutusmoduuliStandardRelationV1RDTO> {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutuskoodiRelations.class);

    private static final String KORKEAKOULU[] = new String[]{
        KoodistoURI.KOODISTO_OPINTOALA_URI,
        KoodistoURI.KOODISTO_TUTKINTO_NIMI_URI,
        KoodistoURI.KOODISTO_TUTKINTONIMIKE_KORKEAKOULU_URI,
        KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI,
        KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSARVO_URI,
        KoodistoURI.KOODISTO_KOULUTUSASTE_URI,
        KoodistoURI.KOODISTO_EQF_LUOKITUS_URI,
        KoodistoURI.KOODISTO_KOULUTUSALA_URI
    };

    private static final String ASTE2_AMM[] = new String[]{
        KoodistoURI.KOODISTO_OPINTOALA_URI,
        KoodistoURI.KOODISTO_TUTKINTO_NIMI_URI,
        KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI,
        KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI,
        KoodistoURI.KOODISTO_KOULUTUSASTE_URI,
        KoodistoURI.KOODISTO_EQF_LUOKITUS_URI,
        KoodistoURI.KOODISTO_KOULUTUSALA_URI
    };

    private static final String LUKIO[] = new String[]{
        KoodistoURI.KOODISTO_KOULUTUSALA_URI,
        KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI,
        KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI,
        KoodistoURI.KOODISTO_KOULUTUSALA_URI,
        KoodistoURI.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI,
        KoodistoURI.KOODISTO_KOULUTUSLAJI_URI
    };

    @Autowired(required = true)
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    @Autowired(required = true)
    private KoulutusCommonConverter commonConverter;

    public T getKomoRelationByKoulutuskoodiUri(Class<T> clazz, final String koulutuskoodiUri, final Locale locale, final boolean showMeta) throws InstantiationException, IllegalAccessException {
        Preconditions.checkNotNull(koulutuskoodiUri, "Koodisto koulutuskoodi URI cannot be null.");
        T dto = clazz.newInstance();

        Collection<KoodiType> koodistoRelations = getKoulutusRelations(koulutuskoodiUri, dto);

        dto.setKoulutuskoodi(singleKoodi(koulutuskoodiUri, FieldNames.KOULUTUSKOODI, locale, showMeta));

        for (KoodiType type : koodistoRelations) {
            //LOG.trace("KOODISTO : " + type.getKoodisto().getKoodistoUri());
            if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_KOULUTUSALA_URI)) {
                dto.setKoulutusala(singleKoodi(type.getKoodiUri(), FieldNames.KOULUTUSALA, locale, showMeta));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSARVO_URI)) {
                if (dto instanceof KoulutusmoduuliKorkeakouluRelationV1RDTO) {
                    KoulutusmoduuliKorkeakouluRelationV1RDTO kk = (KoulutusmoduuliKorkeakouluRelationV1RDTO) dto;
                    multipleKoodis(type, kk.getOpintojenLaajuusarvos(), FieldNames.OPINTOJEN_LAAJUUSARVO, locale, showMeta);
                }
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI)) {
                dto.setOpintojenLaajuusyksikko(singleKoodi(type.getKoodiUri(), FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, locale, showMeta));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_OPINTOALA_URI)) {
                dto.setOpintoala(singleKoodi(type.getKoodiUri(), FieldNames.OPINTOALA, locale, showMeta));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI)) {
                if (dto instanceof KoulutusmoduuliLukioRelationV1RDTO) {
                    KoulutusmoduuliLukioRelationV1RDTO lk = (KoulutusmoduuliLukioRelationV1RDTO) dto;
                    lk.setTutkintonimike(singleKoodi(type.getKoodiUri(), FieldNames.TUTKINTONIMIKE, locale, showMeta));
                }
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_TUTKINTONIMIKE_KORKEAKOULU_URI)) {
                if (dto instanceof KoulutusmoduuliKorkeakouluRelationV1RDTO) {
                    KoulutusmoduuliKorkeakouluRelationV1RDTO kk = (KoulutusmoduuliKorkeakouluRelationV1RDTO) dto;
                    multipleKoodis(type, kk.getTutkintonimikes(), FieldNames.TUTKINTONIMIKE, locale, showMeta);
                }
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_TUTKINTO_NIMI_URI)) {
                dto.setTutkinto(singleKoodi(type.getKoodiUri(), FieldNames.TUTKINTO, locale, showMeta));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_KOULUTUSASTE_URI)) {
                dto.setKoulutusaste(singleKoodi(type.getKoodiUri(), FieldNames.KOULUTUSASTE, locale, showMeta));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_EQF_LUOKITUS_URI)) {
                dto.setEqf(singleKoodi(type.getKoodiUri(), FieldNames.EQF, locale, showMeta));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_EQF_LUOKITUS_URI)) {
                dto.setEqf(singleKoodi(type.getKoodiUri(), FieldNames.EQF, locale, showMeta));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI)) {
                if (dto instanceof KoulutusmoduuliLukioRelationV1RDTO) {
                    KoulutusmoduuliLukioRelationV1RDTO lk = (KoulutusmoduuliLukioRelationV1RDTO) dto;
                    lk.setPohjakoulutusvaatimus(singleKoodi(type.getKoodiUri(), FieldNames.POHJALKOULUTUSVAATIMUKSET, locale, showMeta));
                }
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_KOULUTUSLAJI_URI)) {
                if (dto instanceof KoulutusmoduuliLukioRelationV1RDTO) {
                    KoulutusmoduuliLukioRelationV1RDTO lk = (KoulutusmoduuliLukioRelationV1RDTO) dto;
                    lk.setKoulutuslaji(singleKoodi(type.getKoodiUri(), FieldNames.KOULUTUSLAJI, locale, showMeta));
                }
            }
        }

        return dto;
    }

    private void multipleKoodis(final KoodiType type, KoodiUrisV1RDTO koodiUris, final FieldNames fieldNames, final Locale locale, final boolean showMeta) {
        KoodiUriAndVersioType uriAndVersion = new KoodiUriAndVersioType();
        uriAndVersion.setKoodiUri(type.getKoodiUri());
        uriAndVersion.setVersio(type.getVersio());
        commonConverter.addToKoodiUrisMap(koodiUris, uriAndVersion, locale, fieldNames, showMeta);
    }

    private Collection<KoodiType> getKoulutusRelations(final String koulutuskoodiUri, final T obj) {
        Preconditions.checkNotNull(koulutuskoodiUri, "Koulutuskoodi URI cannot be null");
        Collection<KoodiType> koodiTypes = Lists.<KoodiType>newArrayList();

        for (String koodistoUri : koodisByAste(obj)) {
            koodiTypes.addAll(tarjontaKoodistoHelper.getKoodistoRelations(koulutuskoodiUri, koodistoUri, SuhteenTyyppiType.SISALTYY, false));
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
    private KoodiV1RDTO singleKoodi(final String uri, final FieldNames fieldName, final Locale locale, boolean showMeta) {
        Preconditions.checkNotNull(uri, "Koodisto URI was null - an unknown URI data cannot be loaded.");
        return commonConverter.convertToKoodiDTO(uri, locale, fieldName, showMeta);
    }

    private String[] koodisByAste(final T obj) {
        if (obj instanceof KoulutusmoduuliKorkeakouluRelationV1RDTO) {
            return KORKEAKOULU;
        } else if (obj instanceof KoulutusmoduuliLukioRelationV1RDTO) {
            KoulutusmoduuliLukioRelationV1RDTO lk = (KoulutusmoduuliLukioRelationV1RDTO) obj;
            //TODO: add the relation to koodisto service
            lk.setKoulutuslaji(new KoodiV1RDTO()); //in future
            lk.setPohjakoulutusvaatimus(new KoodiV1RDTO()); //in future
            lk.setTutkintonimike(new KoodiV1RDTO()); //in future
            return LUKIO;
        } else {
            return ASTE2_AMM;
        }

    }
}
