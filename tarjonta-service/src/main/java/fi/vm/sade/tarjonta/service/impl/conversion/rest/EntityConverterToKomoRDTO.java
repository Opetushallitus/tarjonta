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
import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.publication.model.RestParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
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
    private static final KoulutusCommonConverter.Nullable YES = KoulutusCommonConverter.Nullable.YES;
    private static final KoulutusCommonConverter.Nullable NO = KoulutusCommonConverter.Nullable.NO;
    public static final String NO_OVERRIDE_URI = null;

    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConverters;
    @Autowired(required = true)
    private KoulutusCommonConverter commonConverter;

    public KomoV1RDTO convert(final Koulutusmoduuli komo, final RestParam restParam) {
        LOG.debug("in KomotoConverterToKorkeakouluDTO : {}", komo);
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
        komoKuvaus.putAll(komoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komo.getTekstit(), restParam.getShowMeta()));
        kkDto.setKuvausKomo(komoKuvaus);

        //KOMO
        Preconditions.checkNotNull(komo.getKoulutustyyppiEnum(), "KoulutustyyppiEnum cannot be null!");
        List<ToteutustyyppiEnum> convertStringListToEnums = convertStringListToEnums(komo.getKoulutustyyppiUri());

        if (!convertStringListToEnums.isEmpty()) {
            switch (convertStringListToEnums.get(0)) {
                case KORKEAKOULUTUS:
                    kkDto.setKoulutusohjelma(commonConverter.koulutusohjelmaUiMetaDTO(komo.getNimi(), FieldNames.KOULUTUSOHJELMA, restParam));
                    break;
                case AMMATILLINEN_PERUSTUTKINTO:
                case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
                    switch (komo.getModuuliTyyppi()) {
                        case TUTKINTO_OHJELMA:
                            kkDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getKoulutusohjelmaUri(), FieldNames.KOULUTUSOHJELMA, NO, restParam));
                            break;
                    }

                    break;
                case LUKIOKOULUTUS:
                case LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA:
                    switch (komo.getModuuliTyyppi()) {
                        case TUTKINTO_OHJELMA:
                            kkDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getLukiolinjaUri(), FieldNames.LUKIOLINJA, NO, restParam));
                            break;
                    }
                    break;
                default:
                    if (komo.getKoulutusohjelmaUri() != null && !komo.getKoulutusohjelmaUri().isEmpty()) {
                        kkDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getKoulutusohjelmaUri(), FieldNames.KOULUTUSOHJELMA, NO, restParam));
                    } else if (komo.getLukiolinjaUri() != null && !komo.getLukiolinjaUri().isEmpty()) {
                        kkDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getLukiolinjaUri(), FieldNames.LUKIOLINJA, NO, restParam));
                    } else {
                        kkDto.setKoulutusohjelma(commonConverter.koulutusohjelmaUiMetaDTO(komo.getNimi(), FieldNames.KOULUTUSOHJELMA, restParam));
                    }
                    break;
            }
        }

        kkDto.setKoulutusasteTyyppi(komo.getKoulutustyyppiEnum().getKoulutusasteTyyppi());
        kkDto.setKoulutuskoodi(commonConverter.convertToKoodiDTO(komo.getKoulutusUri(), NO_OVERRIDE_URI, FieldNames.KOULUTUS, NO, restParam));
        kkDto.setTutkinto(commonConverter.convertToKoodiDTO(komo.getTutkintoUri(), NO_OVERRIDE_URI, FieldNames.TUTKINTO, YES, restParam));
        kkDto.setOpintojenLaajuusarvo(commonConverter.convertToKoodiDTO(komo.getOpintojenLaajuusarvoUri(), NO_OVERRIDE_URI, FieldNames.OPINTOJEN_LAAJUUSARVO, YES, restParam));
        kkDto.setOpintojenLaajuusyksikko(commonConverter.convertToKoodiDTO(komo.getOpintojenLaajuusyksikkoUri(), NO_OVERRIDE_URI, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, YES, restParam));
        kkDto.setTunniste(komo.getUlkoinenTunniste());
        kkDto.setKoulutusaste(commonConverter.convertToKoodiDTO(komo.getKoulutusasteUri(), NO_OVERRIDE_URI, FieldNames.KOULUTUSASTE, YES, restParam));
        kkDto.setKoulutusala(commonConverter.convertToKoodiDTO(komo.getKoulutusalaUri(), NO_OVERRIDE_URI, FieldNames.KOULUTUSALA, YES, restParam));
        kkDto.setOpintoala(commonConverter.convertToKoodiDTO(komo.getOpintoalaUri(), NO_OVERRIDE_URI, FieldNames.OPINTOALA, YES, restParam));
        kkDto.setTutkintonimikes(commonConverter.convertToKoodiUrisDTO(komo.getTutkintonimikes(), FieldNames.TUTKINTONIMIKE, restParam));
        kkDto.setEqf(commonConverter.convertToKoodiDTO(komo.getEqfUri(), NO_OVERRIDE_URI, FieldNames.EQF, YES, restParam));
        kkDto.setNqf(commonConverter.convertToKoodiDTO(komo.getNqfUri(), NO_OVERRIDE_URI, FieldNames.NQF, YES, restParam));

        kkDto.setKoulutustyyppis(splitData(komo.getKoulutustyyppiUri(), FieldNames.KOULUTUSTYYPPI, restParam));

        //legacy data
        kkDto.setOppilaitostyyppis(splitData(komo.getOppilaitostyyppi(), FieldNames.OPPILAITOSTYYPPI, restParam));

        LOG.debug("in EntityConverterToKomoRDTO : {}", kkDto);
        return kkDto;
    }

    /*
     * Remove ppilaitostyyppi data after the Vaadin UI has been removed from tarjonta project.
     */
    private KoodiUrisV1RDTO splitData(String str, FieldNames fieldName, final RestParam restParam) {
        List<String> splitStringToList = EntityUtils.splitStringToList(str);

        return commonConverter.convertToKoodiUrisDTO(splitStringToList, fieldName, restParam);
    }

    private List<ToteutustyyppiEnum> convertStringListToEnums(final String str) {
        ArrayList<ToteutustyyppiEnum> types = Lists.<ToteutustyyppiEnum>newArrayList();

        if (str != null && !str.isEmpty()) {
            String[] split = StringUtils.split(str, EntityUtils.STR_ARRAY_SEPARATOR);

            for (String s : split) {
                types.add(ToteutustyyppiEnum.fromString(s));
            }
        }

        return types;
    }
}
