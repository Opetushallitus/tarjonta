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
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
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

        KuvausV1RDTO<KomoTeksti> komoKuvaus = new KuvausV1RDTO<KomoTeksti>();
        komoKuvaus.putAll(komoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komo.getTekstit(), showMeta));
        kkDto.setKuvausKomo(komoKuvaus);

        //KOMO
        Preconditions.checkNotNull(komo.getRowType(), "KoulutusasteTyyppi cannot be null!");
        KoulutusasteTyyppi koulutusasteTyyppi = komo.getRowType().getKoulutusasteTyyppi();
        switch (koulutusasteTyyppi) {
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
        kkDto.setKoulutusasteTyyppi(koulutusasteTyyppi);
        kkDto.setKoulutuskoodi(commonConverter.convertToKoodiDTO(komo.getKoulutusUri(), locale, FieldNames.KOULUTUSKOODI, showMeta));
        kkDto.setTutkinto(commonConverter.koodiData(komo.getTutkintoUri(), locale, FieldNames.TUTKINTO, showMeta)); //correct data mapping?
        kkDto.setOpintojenLaajuusarvo(commonConverter.koodiData(komo.getOpintojenLaajuusarvoUri(), locale, FieldNames.OPINTOJEN_LAAJUUSARVO, showMeta));
        kkDto.setOpintojenLaajuusyksikko(commonConverter.koodiData(komo.getOpintojenLaajuusyksikkoUri(), locale, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, showMeta));
        kkDto.setTunniste(komo.getUlkoinenTunniste());
        kkDto.setKoulutusaste(commonConverter.koodiData(komo.getKoulutusasteUri(), locale, FieldNames.KOULUTUSASTE, showMeta));
        kkDto.setKoulutusala(commonConverter.koodiData(komo.getKoulutusalaUri(), locale, FieldNames.KOULUTUSALA, showMeta));
        kkDto.setOpintoala(commonConverter.koodiData(komo.getOpintoalaUri(), locale, FieldNames.OPINTOALA, showMeta));
        kkDto.setTutkintonimikes(commonConverter.convertToKoodiUrisDTO(komo.getTutkintonimikes(), locale, FieldNames.TUTKINTONIMIKE, showMeta));
        kkDto.setEqf(commonConverter.komoData(komo.getEqfUri(), locale, FieldNames.EQF, showMeta));
        kkDto.setOppilaitostyyppis(splitLegacyData(komo.getOppilaitostyyppi(), locale, FieldNames.TUTKINTO, showMeta));       
        kkDto.setVersion(komo.getVersion());
        LOG.debug("in EntityConverterToKomoRDTO : {}", kkDto);
        return kkDto;
    }

    private KoodiUrisV1RDTO splitLegacyData(String str, Locale locale, FieldNames fieldName, boolean showMeta) {
        List<String> splitStringToList = EntityUtils.splitStringToList(str);

        return commonConverter.convertToKoodiUrisDTO(splitStringToList, locale, fieldName, showMeta);
    }
}
