/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import com.google.common.base.Preconditions;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Conversion services for REST service.
 *
 * @author jani
 */
@Component
public class EntityConverterToKomoRDTO {

    private static final Logger LOG = LoggerFactory.getLogger(EntityConverterToKomoRDTO.class);
    private static final boolean ALLOW_NULL_KOODI_URI = true;

    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConverters;
    @Autowired(required = true)
    private KoulutusCommonConverter commonConverter;

    public KomoV1RDTO convert(final Koulutusmoduuli komo, final String lang, final boolean showMeta) {
        LOG.debug("in KomotoConverterToKorkeakouluDTO : {}", komo);
        final Locale locale = new Locale(lang);

        KomoV1RDTO kkDto = new KomoV1RDTO();
        if (komo == null) {
            return kkDto;
        }
        kkDto.setOid(komo.getOid());
        kkDto.setKomoOid(komo.getOid());
        kkDto.setTila(komo.getTila());
        kkDto.setModified(komo.getUpdated());
        kkDto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.fromValue(komo.getModuuliTyyppi().name()));
        kkDto.setVersion(komo.getVersion());
        
        KuvausV1RDTO<KomoTeksti> komoKuvaus = new KuvausV1RDTO<KomoTeksti>();
        komoKuvaus.putAll(komoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komo.getTekstit(), showMeta));
        kkDto.setKuvausKomo(komoKuvaus);

        //KOMO
        Preconditions.checkNotNull(komo.getKoulutustyyppiEnum(), "RowType cannot be null!");
        switch (komo.getKoulutustyyppiEnum()) {
            case KORKEAKOULUTUS:
            case YLIOPISTOKOULUTUS:
            case AMMATTIKORKEAKOULUTUS:
                kkDto.setKoulutusohjelma(commonConverter.koulutusohjelmaUiMetaDTO(komo.getNimi(), locale, FieldNames.KOULUTUSOHJELMA, showMeta));
                break;
            case AMMATILLINEN_PERUSKOULUTUS:
                switch (komo.getModuuliTyyppi()) {
                    case TUTKINTO_OHJELMA:
                        kkDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getKoulutusohjelmaUri(), locale, FieldNames.KOULUTUSOHJELMA, false, showMeta));
                        break;
                }

                break;
            case LUKIOKOULUTUS:
                switch (komo.getModuuliTyyppi()) {
                    case TUTKINTO_OHJELMA:
                        kkDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getLukiolinjaUri(), locale, FieldNames.LUKIOLINJA, false, showMeta));
                        break;
                }
                break;
        }
        kkDto.setKoulutusasteTyyppi(komo.getKoulutustyyppiEnum().getKoulutusasteTyyppi());
        kkDto.setKoulutuskoodi(commonConverter.convertToKoodiDTO(komo.getKoulutusUri(), locale, FieldNames.KOULUTUSKOODI, showMeta));
        kkDto.setTutkinto(commonConverter.convertToKoodiDTO(komo.getTutkintoUri(), locale, FieldNames.TUTKINTO, ALLOW_NULL_KOODI_URI, showMeta));
        kkDto.setOpintojenLaajuusarvo(commonConverter.convertToKoodiDTO(komo.getOpintojenLaajuusarvoUri(), locale, FieldNames.OPINTOJEN_LAAJUUSARVO, ALLOW_NULL_KOODI_URI, showMeta));
        kkDto.setOpintojenLaajuusyksikko(commonConverter.convertToKoodiDTO(komo.getOpintojenLaajuusyksikkoUri(), locale, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, ALLOW_NULL_KOODI_URI, showMeta));
        kkDto.setTunniste(komo.getUlkoinenTunniste());
        kkDto.setKoulutusaste(commonConverter.convertToKoodiDTO(komo.getKoulutusasteUri(), locale, FieldNames.KOULUTUSASTE, ALLOW_NULL_KOODI_URI, showMeta));
        kkDto.setKoulutusala(commonConverter.convertToKoodiDTO(komo.getKoulutusalaUri(), locale, FieldNames.KOULUTUSALA, ALLOW_NULL_KOODI_URI, showMeta));
        kkDto.setOpintoala(commonConverter.convertToKoodiDTO(komo.getOpintoalaUri(), locale, FieldNames.OPINTOALA, ALLOW_NULL_KOODI_URI, showMeta));
        kkDto.setTutkintonimikes(commonConverter.convertToKoodiUrisDTO(komo.getTutkintonimikes(), locale, FieldNames.TUTKINTONIMIKE, showMeta));
        kkDto.setEqf(commonConverter.convertToKoodiDTO(komo.getEqfUri(), locale, FieldNames.EQF, ALLOW_NULL_KOODI_URI, showMeta));
        kkDto.setNqf(commonConverter.convertToKoodiDTO(komo.getNqfUri(), locale, FieldNames.NQF, ALLOW_NULL_KOODI_URI, showMeta));
        kkDto.setKoulutustyyppi(commonConverter.convertToKoodiDTO(komo.getKoulutustyyppiUri(), locale, FieldNames.KOULUTUSTYYPPI, ALLOW_NULL_KOODI_URI, showMeta));

        //legacy data
        kkDto.setOppilaitostyyppis(splitLegacyData(komo.getOppilaitostyyppi(), locale, FieldNames.OPPILAITOSTYYPPI, showMeta));

        LOG.debug("in EntityConverterToKomoRDTO : {}", kkDto);
        return kkDto;
    }

    /*
     * Remove ppilaitostyyppi data after the Vaadin UI has been removed from tarjonta project.
     */
    private KoodiUrisV1RDTO splitLegacyData(String str, Locale locale, FieldNames fieldName, boolean showMeta) {
        List<String> splitStringToList = EntityUtils.splitStringToList(str);

        return commonConverter.convertToKoodiUrisDTO(splitStringToList, locale, fieldName, showMeta);
    }
}
