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
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Conversion services for REST service.
 *
 * @author jani
 */
@Component
public class EntityConverterToKoulutusKorkeakouluRDTO {

    private static final Logger LOG = LoggerFactory.getLogger(EntityConverterToKoulutusKorkeakouluRDTO.class);
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConverters;
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKuvausConverters;
    @Autowired(required = true)
    private KoulutusCommonV1RDTO commonConverter;

    public KoulutusKorkeakouluV1RDTO convert(final KoulutusmoduuliToteutus komoto, final String lang, final boolean showMeta) {
        LOG.debug("in KomotoConverterToKorkeakouluDTO : {}", komoto);
        final Locale locale = new Locale(lang);

        KoulutusKorkeakouluV1RDTO kkDto = new KoulutusKorkeakouluV1RDTO();
        if (komoto == null) {
            return kkDto;
        }
        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
        kkDto.setOid(komoto.getOid());
        kkDto.setKomotoOid(komoto.getOid());
        kkDto.setKomoOid(komo.getOid());
        kkDto.setTila(komoto.getTila());
        kkDto.setModified(komoto.getUpdated());
        kkDto.setModifiedBy(komoto.getLastUpdatedByOid());
        kkDto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.fromValue(komo.getModuuliTyyppi().name()));

        kkDto.setKoulutuksenAlkamiskausi(commonConverter.convertToKoodiDTO(komoto.getAlkamiskausi(), locale, FieldNames.ALKAMISKAUSI, true, showMeta));
        kkDto.setKoulutuksenAlkamisvuosi(komoto.getAlkamisVuosi());
        kkDto.getKoulutuksenAlkamisPvms().addAll(komoto.getKoulutuksenAlkamisPvms());

        KuvausV1RDTO<KomotoTeksti> komotoKuvaus = new KuvausV1RDTO<KomotoTeksti>();
        komotoKuvaus.putAll(komotoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit(), showMeta));
        kkDto.setKuvausKomoto(komotoKuvaus);

        KuvausV1RDTO<KomoTeksti> komoKuvaus = new KuvausV1RDTO<KomoTeksti>();
        komoKuvaus.putAll(komoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komo.getTekstit(), showMeta));
        kkDto.setKuvausKomo(komoKuvaus);

        //KOMO
        Preconditions.checkNotNull(komo.getKoulutustyyppi(), "KoulutusasteTyyppi cannot be null!");
        KoulutusasteTyyppi koulutusasteTyyppi = EntityUtils.KoulutusTyyppiStrToKoulutusAsteTyyppi(komo.getKoulutustyyppi());
        switch (koulutusasteTyyppi) {
            case KORKEAKOULUTUS:
            case YLIOPISTOKOULUTUS:
            case AMMATTIKORKEAKOULUTUS:
                kkDto.setKoulutusohjelma(commonConverter.koulutusohjelmaUiMetaDTO(komo.getNimi(), locale, FieldNames.KOULUTUSOHJELMA, showMeta));
                break;
            case AMMATILLINEN_PERUSKOULUTUS:
                kkDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getKoulutusohjelmaKoodi(), locale, FieldNames.KOULUTUSOHJELMA, false, showMeta));
                break;
            case LUKIOKOULUTUS:
                kkDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getLukiolinja(), locale, FieldNames.LUKIOLINJA, false, showMeta));
                break;
        }
        kkDto.setKoulutuskoodi(commonConverter.convertToKoodiDTO(komo.getKoulutusKoodi(), locale, FieldNames.KOULUTUSKOODI, showMeta));
        kkDto.setTutkinto(commonConverter.koodiData(komo.getTutkintoOhjelmanNimi(), locale, FieldNames.TUTKINTO, showMeta)); //correct data mapping?
        kkDto.setOpintojenLaajuus(commonConverter.koodiData(komo.getLaajuusArvo(), locale, FieldNames.OPINTOJEN_LAAJUUSARVO, showMeta));
        kkDto.setOpintojenLaajuusyksikko(commonConverter.koodiData(komo.getLaajuusYksikko(), locale, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, showMeta));
        kkDto.setTunniste(komo.getUlkoinenTunniste());
        kkDto.setKoulutusasteTyyppi(koulutusasteTyyppi);
        kkDto.setOrganisaatio(commonConverter.searchOrganisaationNimi(komoto.getTarjoaja(), locale));
        kkDto.setKoulutusaste(commonConverter.koodiData(komo.getKoulutusAste(), locale, FieldNames.KOULUTUSASTE, showMeta));
        kkDto.setKoulutusala(commonConverter.koodiData(komo.getKoulutusala(), locale, FieldNames.KOULUTUSALA, showMeta));
        kkDto.setOpintoala(commonConverter.koodiData(komo.getOpintoala(), locale, FieldNames.OPINTOALA, showMeta));
        kkDto.setTutkintonimikes(commonConverter.convertToKoodiDTO(komo.getTutkintonimikes(), locale, FieldNames.TUTKINTONIMIKE, showMeta));
        kkDto.setEqf(commonConverter.komoData(komo.getEqfLuokitus(), locale, FieldNames.EQF, showMeta));

        if (komoto.getAihees() != null) {
            kkDto.setAihees(commonConverter.convertToKoodiDTO(komoto.getAihees(), locale, FieldNames.AIHEES, showMeta));
        }
        kkDto.setOpetuskielis(commonConverter.convertToKoodiDTO(komoto.getOpetuskielis(), locale, FieldNames.OPETUSKIELIS, showMeta));
        final String maksullisuus = komoto.getMaksullisuus();
        kkDto.setOpintojenMaksullisuus(maksullisuus != null && Boolean.valueOf(maksullisuus));
        kkDto.setOpetusmuodos(commonConverter.convertToKoodiDTO(komoto.getOpetusmuotos(), locale, FieldNames.OPETUSMUODOS, showMeta));
        if (komoto.getOpetusAikas() != null) {
            kkDto.setOpetusAikas(commonConverter.convertToKoodiDTO(komoto.getOpetusAikas(), locale, FieldNames.OPETUSAIKAS, showMeta));
        }
        if (komoto.getOpetusPaikkas() != null) {
            kkDto.setOpetusPaikkas(commonConverter.convertToKoodiDTO(komoto.getOpetusPaikkas(), locale, FieldNames.OPETUSPAIKKAS, showMeta));
        }
        kkDto.setPohjakoulutusvaatimukset(commonConverter.convertToKoodiDTO(komoto.getKkPohjakoulutusvaatimus(), locale, FieldNames.POHJALKOULUTUSVAATIMUKSET, showMeta));
        kkDto.setSuunniteltuKestoTyyppi(commonConverter.koodiData(komoto.getSuunniteltuKestoYksikko(), locale, FieldNames.SUUNNITELTUKESTON_TYYPPI, showMeta));
        kkDto.setSuunniteltuKestoArvo(komoto.getSuunniteltuKestoArvo());
        kkDto.setAmmattinimikkeet(commonConverter.convertToKoodiDTO(komoto.getAmmattinimikes(), locale, FieldNames.AMMATTINIMIKKEET, showMeta));

        if (komoto.getHinta() != null) {
            kkDto.setHinta(komoto.getHinta().doubleValue());
        }

        EntityUtils.copyYhteyshenkilos(komoto.getYhteyshenkilos(), kkDto.getYhteyshenkilos());
        LOG.debug("in KomotoConverterToKorkeakouluDTO : {}", kkDto);
        return kkDto;
    }
}
