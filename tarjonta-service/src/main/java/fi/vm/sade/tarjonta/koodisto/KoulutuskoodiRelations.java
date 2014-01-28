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
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusCommonV1RDTO;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliRelationV1RDTO;
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
public class KoulutuskoodiRelations {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutuskoodiRelations.class);
    @Autowired(required = true)
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    @Autowired(required = true)
    private KoulutusCommonV1RDTO commonConverter;

    public KoulutusmoduuliRelationV1RDTO getKomoRelationByKoulutuskoodiUri(final String koulutuskoodiUri, final boolean korkeakoulu, final Locale locale, final boolean showMeta) {
        Preconditions.checkNotNull(koulutuskoodiUri, "Koodisto koulutuskoodi URI cannot be null.");
        Collection<KoodiType> koodistoRelations = getKoulutusRelations(koulutuskoodiUri, korkeakoulu);

        KoulutusmoduuliRelationV1RDTO dto = new KoulutusmoduuliRelationV1RDTO();
        dto.setKoulutuskoodi(listaaKoodi(koulutuskoodiUri, FieldNames.KOULUTUSKOODI, locale, showMeta));

        for (KoodiType type : koodistoRelations) {
            LOG.info("KOODISTO : " + type.getKoodisto().getKoodistoUri());
            if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_KOULUTUSALA_URI)) {
                dto.setKoulutusala(listaaKoodi(type.getKoodiUri(), FieldNames.KOULUTUSALA, locale, showMeta));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_OPINTOALA_URI)) {
                dto.setOpintoala(listaaKoodi(type.getKoodiUri(), FieldNames.OPINTOALA, locale, showMeta));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI)) {
                addToTutkintonimikesMap(type, dto.getTutkintonimikes(), locale, showMeta);
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_TUTKINTONIMIKE_KORKEAKOULU_URI)) {
                addToTutkintonimikesMap(type, dto.getTutkintonimikes(), locale, showMeta);
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_TUTKINTO_NIMI_URI)) {
                dto.setTutkinto(listaaKoodi(type.getKoodiUri(), FieldNames.TUTKINTO, locale, showMeta));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_KOULUTUSASTE_URI)) {
                dto.setKoulutusaste(listaaKoodi(type.getKoodiUri(), FieldNames.KOULUTUSASTE, locale, showMeta));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_EQF_LUOKITUS_URI)) {
                dto.setEqf(listaaKoodi(type.getKoodiUri(), FieldNames.EQF, locale, showMeta));
            }
        }

        return dto;
    }

    private void addToTutkintonimikesMap(final KoodiType type, KoodiUrisV1RDTO koodiUris, final Locale locale, final boolean showMeta) {
        KoodiUriAndVersioType uriAndVersion = new KoodiUriAndVersioType();
        uriAndVersion.setKoodiUri(type.getKoodiUri());
        uriAndVersion.setVersio(type.getVersio());
        commonConverter.addToKoodiUrisMap(koodiUris, uriAndVersion, locale, FieldNames.TUTKINTONIMIKE, showMeta);
    }

    private Collection<KoodiType> getKoulutusRelations(final String koulutuskoodiUri, final boolean korkeakoulu) {
        Preconditions.checkNotNull(koulutuskoodiUri, "Koulutuskoodi URI cannot be null");
        Collection<KoodiType> koodiTypes = Lists.<KoodiType>newArrayList();

        for (String koodistoUri : koodisByAste(korkeakoulu)) {
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
    private KoodiV1RDTO listaaKoodi(final String uri, final FieldNames fieldName, final Locale locale, boolean showMeta) {
        Preconditions.checkNotNull(uri, "Koodisto URI was null - an unknown URI data cannot be loaded.");
        return commonConverter.convertToKoodiDTO(uri, locale, fieldName, showMeta);
    }

    private String[] koodisByAste(final boolean korkeakoulu) {
        if (korkeakoulu) {
            return new String[]{
                KoodistoURI.KOODISTO_OPINTOALA_URI,
                KoodistoURI.KOODISTO_TUTKINTO_NIMI_URI,
                KoodistoURI.KOODISTO_TUTKINTONIMIKE_KORKEAKOULU_URI,
                KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI,
                KoodistoURI.KOODISTO_KOULUTUSASTE_URI,
                KoodistoURI.KOODISTO_EQF_LUOKITUS_URI,
                KoodistoURI.KOODISTO_KOULUTUSALA_URI
            };
        } else {
            return new String[]{
                KoodistoURI.KOODISTO_OPINTOALA_URI,
                KoodistoURI.KOODISTO_TUTKINTO_NIMI_URI,
                KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI,
                KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI,
                KoodistoURI.KOODISTO_KOULUTUSASTE_URI,
                KoodistoURI.KOODISTO_EQF_LUOKITUS_URI,
                KoodistoURI.KOODISTO_KOULUTUSALA_URI
            };
        }
    }
}
