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
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPeruskoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import java.util.Locale;
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
        
        dto.setKoulutuksenAlkamiskausi(commonConverter.convertToKoodiDTO(komoto.getAlkamiskausi(), locale, FieldNames.ALKAMISKAUSI, true, showMeta));
        dto.setKoulutuksenAlkamisvuosi(komoto.getAlkamisVuosi());
        dto.getKoulutuksenAlkamisPvms().addAll(komoto.getKoulutuksenAlkamisPvms());
        
        KuvausV1RDTO<KomotoTeksti> komotoKuvaus = new KuvausV1RDTO<KomotoTeksti>();
        komotoKuvaus.putAll(komotoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit(), showMeta));
        dto.setKuvausKomoto(komotoKuvaus);
        
        KuvausV1RDTO<KomoTeksti> komoKuvaus = new KuvausV1RDTO<KomoTeksti>();
        komoKuvaus.putAll(komoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komo.getTekstit(), showMeta));
        dto.setKuvausKomo(komoKuvaus);

        //KOMO
        Preconditions.checkNotNull(komo.getKoulutustyyppi(), "KoulutusasteTyyppi cannot be null!");
        KoulutusasteTyyppi koulutusasteTyyppi = EntityUtils.KoulutusTyyppiStrToKoulutusAsteTyyppi(komo.getKoulutustyyppi());
        
        if (dto instanceof KoulutusKorkeakouluV1RDTO) {
            KoulutusKorkeakouluV1RDTO kkDto = (KoulutusKorkeakouluV1RDTO) dto;
            kkDto.setKomotoOid(komoto.getOid());
            kkDto.setKoulutusohjelma(commonConverter.koulutusohjelmaUiMetaDTO(komo.getNimi(), locale, FieldNames.KOULUTUSOHJELMA, showMeta));
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
            
        } else if (dto instanceof KoulutusLukioV1RDTO) {
            KoulutusLukioV1RDTO lukioDto = (KoulutusLukioV1RDTO) dto;
            lukioDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getLukiolinja(), locale, FieldNames.LUKIOLINJA, false, showMeta));            
            lukioDto.setKielivalikoima(commonConverter.convertToKielivalikoimaDTO(komoto.getTarjotutKielet(), locale, showMeta));

            //has parent texts data : Tavoite, Opintojen rakenne and Jatko-opintomahdollisuudet	
            final Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findParentKomo(komo);
            komoKuvaus.putAll(komoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(parentKomo.getTekstit(), showMeta));
        } else if (dto instanceof KoulutusAmmatillinenPeruskoulutusV1RDTO) {
            KoulutusAmmatillinenPeruskoulutusV1RDTO ammDto = (KoulutusAmmatillinenPeruskoulutusV1RDTO) dto;
            ammDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getKoulutusohjelmaKoodi(), locale, FieldNames.KOULUTUSOHJELMA, false, showMeta));
        }
        
        dto.setKoulutuskoodi(commonConverter.convertToKoodiDTO(komo.getKoulutusKoodi(), locale, FieldNames.KOULUTUSKOODI, showMeta));
        dto.setTutkinto(commonConverter.koodiData(komo.getTutkintoOhjelmanNimi(), locale, FieldNames.TUTKINTO, showMeta)); //correct data mapping?
        dto.setOpintojenLaajuusarvo(commonConverter.koodiData(komo.getLaajuusArvo(), locale, FieldNames.OPINTOJEN_LAAJUUSARVO, showMeta));
        dto.setOpintojenLaajuusyksikko(commonConverter.koodiData(komo.getLaajuusYksikko(), locale, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, showMeta));
        dto.setTunniste(komo.getUlkoinenTunniste());
        dto.setOrganisaatio(commonConverter.searchOrganisaationNimi(komoto.getTarjoaja(), locale));
        dto.setKoulutusaste(commonConverter.koodiData(komo.getKoulutusAste(), locale, FieldNames.KOULUTUSASTE, showMeta));
        dto.setKoulutusala(commonConverter.koodiData(komo.getKoulutusala(), locale, FieldNames.KOULUTUSALA, showMeta));
        dto.setOpintoala(commonConverter.koodiData(komo.getOpintoala(), locale, FieldNames.OPINTOALA, showMeta));
        
        dto.setEqf(commonConverter.komoData(komo.getEqfLuokitus(), locale, FieldNames.EQF, showMeta));
        
        dto.setOpetuskielis(commonConverter.convertToKoodiUrisDTO(komoto.getOpetuskielis(), locale, FieldNames.OPETUSKIELIS, showMeta));
        
        dto.setOpetusmuodos(commonConverter.convertToKoodiUrisDTO(komoto.getOpetusmuotos(), locale, FieldNames.OPETUSMUODOS, showMeta));
        if (komoto.getOpetusAikas() != null) {
            dto.setOpetusAikas(commonConverter.convertToKoodiUrisDTO(komoto.getOpetusAikas(), locale, FieldNames.OPETUSAIKAS, showMeta));
        }
        if (komoto.getOpetusPaikkas() != null) {
            dto.setOpetusPaikkas(commonConverter.convertToKoodiUrisDTO(komoto.getOpetusPaikkas(), locale, FieldNames.OPETUSPAIKKAS, showMeta));
        }
        dto.setSuunniteltuKestoTyyppi(commonConverter.koodiData(komoto.getSuunniteltuKestoYksikko(), locale, FieldNames.SUUNNITELTUKESTON_TYYPPI, showMeta));
        dto.setSuunniteltuKestoArvo(komoto.getSuunniteltuKestoArvo());
        
        EntityUtils.copyYhteyshenkilos(komoto.getYhteyshenkilos(), dto.getYhteyshenkilos());
        dto.setVersion(komoto.getVersion());
        
        return dto;
    }
}
