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
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.service.impl.resources.KoulutusResourceImpl;
import fi.vm.sade.tarjonta.service.resources.dto.KoulutusmoduuliRelationDTO;
import fi.vm.sade.tarjonta.service.resources.dto.UiDTO;
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

    private static final KoulutusKoodiToUiDTOConverter<UiDTO> koulutusKoodiToKoodiModel = new KoulutusKoodiToUiDTOConverter<UiDTO>();
    private static final Logger LOG = LoggerFactory.getLogger(KoulutusResourceImpl.class);
    private static final String[] SEARCH_KOMO_KOODISTOS = new String[]{
        KoodistoURI.KOODISTO_OPINTOALA_URI,
        KoodistoURI.KOODISTO_TUTKINTO_NIMI_URI,
        KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI,
        KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI,
        KoodistoURI.KOODISTO_KOULUTUSASTE_URI,
        KoodistoURI.KOODISTO_EQF_LUOKITUS_URI
    };
    @Autowired(required = true)
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    public KoulutusmoduuliRelationDTO getKomoRelationByKoulutuskoodiUri(final String koulutuskoodiUri, final Locale locale) {
        Preconditions.checkNotNull(koulutuskoodiUri, "Koodisto koulutuskoodi URI cannot be null.");
        Collection<KoodiType> koodistoRelations = getKoulutusRelations(koulutuskoodiUri);

        KoulutusmoduuliRelationDTO dto = new KoulutusmoduuliRelationDTO();
        dto.setKoulutuskoodi(listaaKoodi(koulutuskoodiUri, locale));

        for (KoodiType type : koodistoRelations) {
            LOG.info("KOODISTO : " + type.getKoodisto().getKoodistoUri());
            if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_KOULUTUSALA_URI)) {
                dto.setKoulutusala(listaaKoodi(type.getKoodiUri(), locale));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_OPINTOALA_URI)) {
                dto.setOpintoala(listaaKoodi(type.getKoodiUri(), locale));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI)) {
                dto.setTutkintonimike(listaaKoodi(type.getKoodiUri(), locale));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_TUTKINTO_NIMI_URI)) {
                dto.setTutkinto(listaaKoodi(type.getKoodiUri(), locale));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_KOULUTUSASTE_URI)) {
                dto.setKoulutusaste(listaaKoodi(type.getKoodiUri(), locale));
            } else if (type.getKoodisto().getKoodistoUri().equals(KoodistoURI.KOODISTO_EQF_LUOKITUS_URI)) {
                dto.setEqf(listaaKoodi(type.getKoodiUri(), locale));
            }
        }

        return dto;
    }

    private Collection<KoodiType> getKoulutusRelations(String koulutuskoodiUri) {
        Preconditions.checkNotNull(koulutuskoodiUri, "Koulutuskoodi URI cannot be null");
        Collection<KoodiType> koodiTypes = Lists.<KoodiType>newArrayList();

        for (String koodistoUri : SEARCH_KOMO_KOODISTOS) {
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
    private UiDTO listaaKoodi(final String uri, final Locale locale) {
        Preconditions.checkNotNull(uri, "Koodisto URI was null - an unknown URI data cannot be loaded.");
        KoodiType koodiByUri = tarjontaKoodistoHelper.getKoodiByUri(uri);
        return koulutusKoodiToKoodiModel.convertKoodiTypeToUiDTO(UiDTO.class, koodiByUri, locale);
    }
}
