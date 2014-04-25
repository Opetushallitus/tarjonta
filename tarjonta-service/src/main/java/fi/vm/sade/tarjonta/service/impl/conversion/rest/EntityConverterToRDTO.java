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
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.WebLinkki;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPeruskoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import java.util.Locale;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Conversion services for REST service.
 *
 * @author jani
 * @param <TYPE> KoulutusV1RDTO
 */
@Component
public class EntityConverterToRDTO<TYPE extends KoulutusV1RDTO> {

    private static final Logger LOG = LoggerFactory.getLogger(EntityConverterToRDTO.class);
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConverters;
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKuvausConverters;
    @Autowired(required = true)
    private KoulutusCommonConverter commonConverter;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    public TYPE convert(Class<TYPE> clazz, final KoulutusmoduuliToteutus komoto, final String lang, final boolean showMeta) {
        LOG.debug("in KomotoConverterToKorkeakouluDTO : {}", komoto);
        final Locale locale = new Locale(lang);

        TYPE dto = null;
        try {
            dto = clazz.newInstance();
        } catch (InstantiationException ex) {
            LOG.error("Converter initialization failed.", ex);
        } catch (IllegalAccessException ex) {
            LOG.error("Converter initialization failed.", ex);
        }

        if (komoto == null) {
            return dto;
        }

        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
        dto.setOid(komoto.getOid());
        dto.setKomoOid(komo.getOid());
        dto.setTila(komoto.getTila());
        dto.setModified(komoto.getUpdated());
        dto.setModifiedBy(komoto.getLastUpdatedByOid());
        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.fromValue(komo.getModuuliTyyppi().name()));

        dto.setKoulutuksenAlkamiskausi(commonConverter.convertToKoodiDTO(komoto.getAlkamiskausiUri(), locale, FieldNames.ALKAMISKAUSI, true, showMeta));
        dto.setKoulutuksenAlkamisvuosi(komoto.getAlkamisVuosi());
        dto.getKoulutuksenAlkamisPvms().addAll(komoto.getKoulutuksenAlkamisPvms());

        KuvausV1RDTO<KomotoTeksti> komotoKuvaus = new KuvausV1RDTO<KomotoTeksti>();
        komotoKuvaus.putAll(komotoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit(), showMeta));
        dto.setKuvausKomoto(komotoKuvaus);

        //KOMO
        if (dto instanceof KoulutusKorkeakouluV1RDTO) {
            /**
             * KORKEAKOULU
             */
            KoulutusKorkeakouluV1RDTO kkDto = (KoulutusKorkeakouluV1RDTO) dto;
            kkDto.setKomotoOid(komoto.getOid());

            final boolean useKomotoName = komoto.getNimi() != null && !komoto.getNimi().getTekstis().isEmpty(); //OVT-7531
            kkDto.setKoulutusohjelma(commonConverter.koulutusohjelmaUiMetaDTO(useKomotoName ? komoto.getNimi() : komo.getNimi(), locale, FieldNames.KOULUTUSOHJELMA, showMeta));

            kkDto.setTutkintonimikes(commonConverter.convertToKoodiUrisDTO(komo.getTutkintonimikes(), locale, FieldNames.TUTKINTONIMIKE, showMeta));
            if (komo.getKandidaatinKoulutuskoodi() != null) {
                kkDto.setKandidaatinKoulutuskoodi(commonConverter.convertToKoodiDTO(komo.getKandidaatinKoulutuskoodi(), locale, FieldNames.KOULUTUSKOODI_KANDIDAATTI, showMeta));
            }

            if (komoto.getAihees() != null) {
                kkDto.setAihees(commonConverter.convertToKoodiUrisDTO(komoto.getAihees(), locale, FieldNames.AIHEES, showMeta));
            }

            final String maksullisuus = komoto.getMaksullisuus();
            kkDto.setOpintojenMaksullisuus(maksullisuus != null && Boolean.valueOf(maksullisuus));

            if (komoto.getHinta() != null) {
                kkDto.setHinta(komoto.getHinta().doubleValue());
            }

            kkDto.setAmmattinimikkeet(commonConverter.convertToKoodiUrisDTO(komoto.getAmmattinimikes(), locale, FieldNames.AMMATTINIMIKKEET, showMeta));

            kkDto.setPohjakoulutusvaatimukset(commonConverter.convertToKoodiUrisDTO(komoto.getKkPohjakoulutusvaatimus(), locale, FieldNames.POHJALKOULUTUSVAATIMUS, showMeta));
            convertKomoCommonToRDTO(dto, komo, locale, showMeta);
        } else if (dto instanceof KoulutusLukioV1RDTO) {
            /**
             * 2ASTE : LUKIO
             */
            KoulutusLukioV1RDTO lukioDto = (KoulutusLukioV1RDTO) dto;
            lukioDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getLukiolinjaUri(), locale, FieldNames.LUKIOLINJA, false, showMeta));
            lukioDto.setKielivalikoima(commonConverter.convertToKielivalikoimaDTO(komoto.getTarjotutKielet(), locale, showMeta));
            lukioDto.setLukiodiplomit(commonConverter.convertToKoodiUrisDTO(komoto.getLukiodiplomit(), locale, FieldNames.LUKIODIPLOMI, showMeta));
            lukioDto.setTutkintonimike(commonConverter.koodiData(getFirstUriOrNull(komo.getTutkintonimikes()), locale, FieldNames.TUTKINTONIMIKE, showMeta));
            lukioDto.setPohjakoulutusvaatimus(commonConverter.koodiData(komoto.getPohjakoulutusvaatimusUri(), locale, FieldNames.POHJALKOULUTUSVAATIMUS, showMeta));
            lukioDto.setLinkkiOpetussuunnitelmaan(getFirstUrlOrNull(komoto.getLinkkis()));
            lukioDto.setKoulutuslaji(commonConverter.koodiData(getFirstUriOrNull(komoto.getKoulutuslajis()), locale, FieldNames.KOULUTUSLAJI, showMeta));
            //has parent texts data : Tavoite, Opintojen rakenne and Jatko-opintomahdollisuudet	
            final Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findParentKomo(komo);
            //convert parent komo data to RDTO
            convertKomoCommonToRDTO(dto, parentKomo, locale, showMeta);
            //override parent komo data by the child komo data
            handleLukioKomoInheritance(dto, parentKomo, komo, locale, showMeta);
        } else if (dto instanceof KoulutusAmmatillinenPeruskoulutusV1RDTO) {
            /**
             * 2ASTE : AMMATILLINEN
             */
            KoulutusAmmatillinenPeruskoulutusV1RDTO ammDto = (KoulutusAmmatillinenPeruskoulutusV1RDTO) dto;
            ammDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getKoulutusohjelmaUri(), locale, FieldNames.KOULUTUSOHJELMA, false, showMeta));
            final Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findParentKomo(komo);
            //convert parent komo data to RDTO
            convertKomoCommonToRDTO(dto, parentKomo, locale, showMeta);
            //override parent komo data by the child komo data
            handleLukioKomoInheritance(dto, parentKomo, komo, locale, showMeta);
        }

        dto.setOrganisaatio(commonConverter.searchOrganisaationNimi(komoto.getTarjoaja(), locale));
        dto.setOpetuskielis(commonConverter.convertToKoodiUrisDTO(komoto.getOpetuskielis(), locale, FieldNames.OPETUSKIELIS, showMeta));
        dto.setOpetusmuodos(commonConverter.convertToKoodiUrisDTO(komoto.getOpetusmuotos(), locale, FieldNames.OPETUSMUODOS, showMeta));

        if (komoto.getOpetusAikas() != null) {
            dto.setOpetusAikas(commonConverter.convertToKoodiUrisDTO(komoto.getOpetusAikas(), locale, FieldNames.OPETUSAIKAS, showMeta));
        }
        if (komoto.getOpetusPaikkas() != null) {
            dto.setOpetusPaikkas(commonConverter.convertToKoodiUrisDTO(komoto.getOpetusPaikkas(), locale, FieldNames.OPETUSPAIKKAS, showMeta));
        }
        dto.setSuunniteltuKestoTyyppi(commonConverter.koodiData(komoto.getSuunniteltukestoYksikkoUri(), locale, FieldNames.SUUNNITELTUKESTON_TYYPPI, showMeta));
        dto.setSuunniteltuKestoArvo(komoto.getSuunniteltukestoArvo());

        EntityUtils.copyYhteyshenkilos(komoto.getYhteyshenkilos(), dto.getYhteyshenkilos());
        dto.setVersion(komoto.getVersion());

        return dto;
    }

    private void convertKomoCommonToRDTO(TYPE dto, Koulutusmoduuli komo, Locale locale, boolean showMeta) {
        Preconditions.checkNotNull(komo.getKoulutustyyppi(), "KoulutusasteTyyppi cannot be null!");

        dto.setKoulutuskoodi(commonConverter.convertToKoodiDTO(komo.getKoulutusUri(), locale, FieldNames.KOULUTUSKOODI, showMeta));
        dto.setTutkinto(commonConverter.koodiData(komo.getTutkintoUri(), locale, FieldNames.TUTKINTO, showMeta)); //correct data mapping?
        dto.setOpintojenLaajuusarvo(commonConverter.koodiData(komo.getOpintojenLaajuusarvoUri(), locale, FieldNames.OPINTOJEN_LAAJUUSARVO, showMeta));
        dto.setOpintojenLaajuusyksikko(commonConverter.koodiData(komo.getOpintojenLaajuusyksikkoUri(), locale, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, showMeta));
        dto.setKoulutusaste(commonConverter.koodiData(komo.getKoulutusasteUri(), locale, FieldNames.KOULUTUSASTE, showMeta));
        dto.setKoulutusala(commonConverter.koodiData(komo.getKoulutusalaUri(), locale, FieldNames.KOULUTUSALA, showMeta));
        dto.setOpintoala(commonConverter.koodiData(komo.getOpintoalaUri(), locale, FieldNames.OPINTOALA, showMeta));

        if (komo.getEqfUri() != null && !komo.getEqfUri().isEmpty()) {
            dto.setEqf(commonConverter.komoData(komo.getEqfUri(), locale, FieldNames.EQF, showMeta));
        }

        if (komo.getNqfUri() != null && !komo.getNqfUri().isEmpty()) {
            dto.setNqf(commonConverter.komoData(komo.getNqfUri(), locale, FieldNames.NQF, showMeta));
        }

        dto.setTunniste(komo.getUlkoinenTunniste());
        KuvausV1RDTO<KomoTeksti> komoKuvaus = new KuvausV1RDTO<KomoTeksti>();
        komoKuvaus.putAll(komoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komo.getTekstit(), showMeta));
        dto.setKuvausKomo(komoKuvaus);
    }

    private void handleLukioKomoInheritance(TYPE dto, Koulutusmoduuli komoParent, Koulutusmoduuli komoChild, Locale locale, boolean showMeta) {
        //when komo data is null, override by child komo data,

        if (komoParent.getEqfUri() == null && komoChild.getEqfUri() != null && !komoChild.getEqfUri().isEmpty()) {
            dto.setEqf(commonConverter.komoData(komoChild.getEqfUri(), locale, FieldNames.EQF, showMeta));
        }

        if (komoParent.getNqfUri() == null && komoChild.getNqfUri() != null && !komoChild.getNqfUri().isEmpty()) {
            dto.setNqf(commonConverter.komoData(komoChild.getNqfUri(), locale, FieldNames.NQF, showMeta));
        }
    }

    private static String getFirstUriOrNull(Set<KoodistoUri> uris) {
        if (uris != null && !uris.isEmpty()) {
            return uris.iterator().next().getKoodiUri();
        }
        return null;
    }

    private static String getFirstUrlOrNull(Set<WebLinkki> uris) {
        if (uris != null && !uris.isEmpty()) {
            return uris.iterator().next().getUrl();
        }
        return null;
    }
}
