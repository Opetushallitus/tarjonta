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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.tarjonta.service.resources.dto.kk.UiDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.UiMetaDTO;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Jani Wilén
 */
public class KoulutusKoodiToUiDTOConverter<DTO extends UiDTO> {

    public KoulutusKoodiToUiDTOConverter() {
    }

    public List<DTO> convertKoodistoToUiDTO(Class modelClass, final Collection<KoodiType> koodit, final Locale locale) {

        List<DTO> list = Lists.<DTO>newArrayList();
        for (KoodiType koodiType : koodit) {
            list.add(convertKoodiTypeToUiDTO(modelClass, koodiType, locale));
        }

        return list;
    }

    public DTO convertKoodiTypeToUiDTO(Class modelClass, KoodiType koodiType, Locale locale) {
        DTO dto;
        try {
            dto = (DTO) modelClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Application error - class initialization failed.", ex);
        }
        final KoodiMetadataType koodiMetadata = getKoodiMetadataForLanguage(koodiType, locale);
        dto.setArvo(koodiMetadata.getNimi());
        dto.setKoodiUri(koodiType.getKoodiUri(), koodiType.getVersio() + "", koodiType.getKoodiArvo());

        if (dto instanceof UiMetaDTO) {
            //add all languages to the UI object
            UiMetaDTO meta = (UiMetaDTO) dto;
            meta.setMeta(convertMetadata(koodiType.getMetadata()));
        }

        return dto;
    }

    private Map<String, UiDTO> convertMetadata(final List<KoodiMetadataType> languageMetaData) {
        Map<String, UiDTO> teksti = Maps.<String, UiDTO>newHashMap();

        for (KoodiMetadataType meta : languageMetaData) {
            final KieliType kieli = meta.getKieli();

            if (kieli != null && meta.getNimi() != null && !meta.getNimi().isEmpty()) {
                teksti.put(kieli.value(), new UiDTO(null, kieli.name(), null, kieli.value()));
            }
        }

        return teksti;
    }

    public static KoodiMetadataType getKoodiMetadataForLanguage(KoodiType koodiType, Locale locale) {
        KoodiMetadataType kmdt = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KoodistoHelper.getKieliForLocale(locale));
        if (kmdt == null || (kmdt.getNimi() == null || kmdt.getNimi().length() == 0)) {
            // Try finnish if current locale is not found
            kmdt = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KieliType.FI);
        }

        return kmdt;
    }
}
