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
package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.service.impl.resources.v1.KomoResourceImplV1;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Convert entity objects to koulutus rest objects.
 *
 * @author jani
 */
@Component
public class KoulutusCommonV1RDTO {
    
    private static final Logger LOG = LoggerFactory.getLogger(KoulutusCommonV1RDTO.class);
    
    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    
    @Autowired
    private OrganisaatioService organisaatioService;
    
    public NimiV1RDTO koulutusohjelmaUiMetaDTO(final MonikielinenTeksti mt, final Locale langCode, final FieldNames msg, final boolean showMeta) {
        NimiV1RDTO data = new NimiV1RDTO();
        for (TekstiKaannos tk : mt.getTekstis()) {
            final KoodiType koodiType = tarjontaKoodistoHelper.convertKielikoodiToKoodiType(tk.getKieliKoodi());
            
            if (koodiType == null) {
                LOG.error("No koodisto koodi URI found for kielikoodi : '{}'", tk.getKieliKoodi());
                continue;
            }
            
            final String koodiUri = koodiType.getKoodiUri();
            
            data.getTekstis().put(koodiUri, tk.getArvo());
            if (showMeta) {
                if (data.getMeta() == null) {
                    data.setMeta(Maps.<String, KoodiV1RDTO>newHashMap());
                }
                
                KoodiUriAndVersioType type = new KoodiUriAndVersioType();
                type.setKoodiUri(koodiType.getKoodiUri());
                type.setVersio(koodiType.getVersio());
                data.getMeta().put(koodiUri, convertToKoodiDTO(new KoodiV1RDTO(), type, langCode, msg, true));
            }
        }
        return data;
    }
    
    private void convertKoodistoMetaData(KoodiV1RDTO dto, KoodiUriAndVersioType type, final String arvo, Locale locale, boolean showSubMeta) {
        if (type != null && type.getKoodiUri() != null && !type.getKoodiUri().isEmpty()) {
            dto.setUri(type.getKoodiUri());
            dto.setVersio(type.getVersio());
            
            if (arvo != null) { //only when koodisto has thrown an exception
                dto.setArvo(arvo);
                
                final KoodiType koodiType = tarjontaKoodistoHelper.getKoodi(type.getKoodiUri(), type.getVersio());
                if (koodiType != null) {
                    dto.setNimi(tarjontaKoodistoHelper.getKoodiNimi(koodiType, locale));
                    if (showSubMeta) {
                        addOtherLanguages(dto, koodiType, locale, showSubMeta);
                    } else {
                        dto.setMeta(null);
                    }
                }
            }
        } else {
            //koodisto koodi missing
            dto.setUri("");
            dto.setVersio(-1);
            dto.setArvo("");
            dto.setNimi("");
            dto.setMeta(null);
        }
    }

    /*
     * Create JSON object:
     * 
     * "kieli_sv" : {
     *    "kieliUri" : "kieli_sv",
     *    "kieliVersio" : 1,
     *    "kieliArvo" : "SV",
     *    "nimi" : "Agrolog (YH)"
     * }
     */
    public void convertKoodistoKieliData(KoodiV1RDTO dto, final String koodiUri, final KoodiType koodiType, final String langNimi, final Locale locale) {
        if (koodiType != null && koodiType.getKoodiUri() != null && !koodiType.getKoodiUri().isEmpty()) {
            dto.setKieliUri(koodiType.getKoodiUri());
            dto.setKieliVersio(koodiType.getVersio());
            dto.setKieliArvo(koodiType.getKoodiArvo());
            dto.setNimi(langNimi);
        }
    }
    
    public KoodiV1RDTO komoData(String koodistoKoodiUri, final Locale locale, final FieldNames fieldName, final boolean showMeta) {
        if (koodistoKoodiUri != null && !koodistoKoodiUri.isEmpty()) {
            return convertToKoodiDTO(koodistoKoodiUri, locale, fieldName, showMeta);
        }
        return new KoodiV1RDTO();
    }
    
    public KoodiV1RDTO koodiData(String koodistoKoodiUri, final Locale locale, final FieldNames fieldName, final boolean showMeta) {
        return komoData(koodistoKoodiUri, locale, fieldName, showMeta);
    }
    
    public KoodiV1RDTO convertToKoodiDTO(final String fromKoodiUri, final Locale locale, final FieldNames fieldName, final boolean allowNullKoodi, final boolean showMeta) {
        KoodiV1RDTO koodiUriDto = new KoodiV1RDTO();
        
        if (allowNullKoodi && fromKoodiUri == null) {
            //use empty string arg to return empty data object
            convertKoodistoMetaData(koodiUriDto, null, "", locale, false);
        } else {
            convertKoodiUriToKoodiDTO(fromKoodiUri, koodiUriDto, locale, fieldName, allowNullKoodi, showMeta);
        }
        return koodiUriDto;
    }
    
    public KoodiV1RDTO convertToKoodiDTO(final String fromKoodiUri, final Locale locale, final FieldNames fieldName, final boolean showMeta) {
        return convertToKoodiDTO(fromKoodiUri, locale, fieldName, false, showMeta);
    }
    
    public NimiV1RDTO convertToNimiDTO(final String fromKoodiUri, final Locale locale, final FieldNames fieldName, final boolean allowNullKoodi, final boolean showMeta) {
        NimiV1RDTO koodiUriDto = new NimiV1RDTO();
        convertKoodiUriToKoodiDTO(fromKoodiUri, koodiUriDto, locale, fieldName, allowNullKoodi, showMeta);
        return koodiUriDto;
    }
    
    public KoodiUrisV1RDTO convertToKoodiUrisDTO(final Set<KoodistoUri> fromKoodiUris, final Locale Locale, final FieldNames fieldName, final boolean showMeta) {
        KoodiUrisV1RDTO koodiMapDto = new KoodiUrisV1RDTO();
        if (koodiMapDto.getUris() == null) {
            koodiMapDto.setUris(Maps.<String, Integer>newHashMap());
        }
        
        for (KoodistoUri koodiUri : fromKoodiUris) {
            final KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(koodiUri.getKoodiUri());
            addToKoodiUrisMap(koodiMapDto, type, Locale, fieldName, showMeta);
        }
        
        return koodiMapDto;
    }
    
    public KoodiUrisV1RDTO convertToKoodiUrisDTO(final List<String> fromKoodiUris, final Locale Locale, final FieldNames fieldName, final boolean showMeta) {
        KoodiUrisV1RDTO koodiMapDto = new KoodiUrisV1RDTO();
        if (koodiMapDto.getUris() == null) {
            koodiMapDto.setUris(Maps.<String, Integer>newHashMap());
        }
        
        for (String koodiUri : fromKoodiUris) {
            final KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(koodiUri);
            addToKoodiUrisMap(koodiMapDto, type, Locale, fieldName, showMeta);
        }
        
        return koodiMapDto;
    }
    
    public KoodiV1RDTO convertToKoodiDTO(KoodiV1RDTO uiDto, final KoodiUriAndVersioType type, final Locale locale, final FieldNames fieldName, final boolean showSubMeta) {
        Preconditions.checkNotNull(type, "Koodi URI cannot be null in field : " + fieldName);
        Preconditions.checkNotNull(locale, "Locale object cannot be null. field in " + fieldName);
        KoodiType koodiType = null;
        try {
            koodiType = tarjontaKoodistoHelper.getKoodi(type.getKoodiUri(), type.getVersio());
        } catch (Exception e) {
            LOG.error("Koodisto service error.", e);
        }
        if (koodiType == null) {
            LOG.error("No koodisto service koodi URI found by '{}' and version {}.", type.getKoodiUri(), type.getVersio());
        }
        convertKoodistoMetaData(uiDto, type, koodiType != null ? koodiType.getKoodiArvo() : null, locale, showSubMeta);
        return uiDto;
    }
    
    public void convertKoodiUriToKoodiDTO(
            final String fromKoodiUri,
            final KoodiV1RDTO koodiDto,
            final Locale locale,
            final FieldNames fieldName,
            final boolean allowNullKoodisto, final boolean showMeta) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null in field : " + fieldName);
        Preconditions.checkNotNull(locale, "Locale object cannot be null in field in " + fieldName);
        Preconditions.checkNotNull(koodiDto, "KoodiV1RDTO object cannot be null in field " + fieldName);
        
        final KoodiType koodiType = tarjontaKoodistoHelper.getKoodiByUri(fromKoodiUri);
        
        if (koodiType == null && allowNullKoodisto) {
            //TODO: remove this code block when data is fixed
            toKoodiUriDTO(koodiDto, new KoodiUriAndVersioType(), new KoodiType(), locale);
        } else if (koodiType == null) {
            LOG.error("No koodisto service koodi URI found by '{}'.", fromKoodiUri);
            KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(fromKoodiUri);
            toKoodiUriDTO(koodiDto, type, koodiType, locale);
            addOtherLanguages(koodiDto, koodiType, locale, showMeta);
        } else {
            Preconditions.checkNotNull(koodiType, "No result found by koodisto koodi URI '" + fromKoodiUri + "' in field " + fieldName);
            KoodiUriAndVersioType type = new KoodiUriAndVersioType();
            type.setKoodiUri(koodiType.getKoodiUri());
            type.setVersio(koodiType.getVersio());
            toKoodiUriDTO(koodiDto, type, koodiType, locale);
            addOtherLanguages(koodiDto, koodiType, locale, showMeta);
        }
    }
    
    public KoodiV1RDTO toKoodiUriDTO(KoodiV1RDTO dto, final KoodiUriAndVersioType fromKoodiUri, final KoodiType koodiByUri, final Locale locale) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null.");
        Preconditions.checkNotNull(locale, "Locale object cannot be null.");
        
        if (dto == null) {
            dto = new KoodiV1RDTO();
        }
        convertKoodistoMetaData(dto, fromKoodiUri, koodiByUri != null ? koodiByUri.getKoodiArvo() : null, locale, false);
        return dto;
    }
    
    public void addOtherLanguages(final KoodiV1RDTO koodiUriDto, KoodiType koodiType, final Locale locale, final boolean showMeta) {
        Preconditions.checkNotNull(koodiUriDto, "KoodiUriDTO object cannot be null.");
        
        if (koodiType != null) {
            for (KoodiMetadataType meta : koodiType.getMetadata()) {
                //get a koodi for 'FI', 'SV' etc.
                final KoodiType langKoodiType = tarjontaKoodistoHelper.convertKielikoodiToKoodiType(meta.getKieli().value());
                
                KoodiV1RDTO dto = new KoodiV1RDTO();
                convertKoodistoKieliData(dto, koodiUriDto.getUri(), langKoodiType, meta.getNimi(), locale);
                if (showMeta && langKoodiType != null) {
                    if (koodiUriDto.getMeta() == null) {
                        koodiUriDto.setMeta(Maps.<String, KoodiV1RDTO>newHashMap());
                    }
                    koodiUriDto.getMeta().put(langKoodiType.getKoodiUri(), dto);
                }
            }
        } else {
            LOG.error("Unable to show koodisto koodi metadata.");
        }
    }
    
    public OrganisaatioV1RDTO searchOrganisaationNimi(String tarjoajaOid, Locale locale) {
        final OrganisaatioDTO organisaatioDto = organisaatioService.findByOid(tarjoajaOid);
        
        Preconditions.checkNotNull(organisaatioDto, "OrganisaatioDTO object cannot be null.");
        Preconditions.checkNotNull(organisaatioDto.getOid(), "OrganisaatioDTO OID cannot be null.");
        Preconditions.checkNotNull(organisaatioDto.getNimi(), "OrganisaatioDTO name object cannot be null.");
        
        List<MonikielinenTekstiTyyppi.Teksti> tekstis = organisaatioDto.getNimi().getTeksti();
        
        String nimi = null;
        
        for (MonikielinenTekstiTyyppi.Teksti teksti : tekstis) {
            Preconditions.checkNotNull(teksti.getKieliKoodi(), "Locale language code cannot be null.");
            if (teksti.getKieliKoodi().toLowerCase().equals(locale.getLanguage())) {
                nimi = teksti.getValue();
                break;
            }
        }
        
        Preconditions.checkNotNull(nimi, "OrganisaatioDTO name object cannot be null.");
        OrganisaatioV1RDTO organisaatioRDTO = new OrganisaatioV1RDTO();
        organisaatioRDTO.setOid(organisaatioDto.getOid());
        organisaatioRDTO.setNimi(nimi);
        return organisaatioRDTO;
    }
    
    public void addToKoodiUrisMap(final KoodiUrisV1RDTO koodiUris, final KoodiUriAndVersioType type, final Locale locale, final FieldNames fieldName, final boolean showMeta) {
        koodiUris.getUris().put(type.getKoodiUri(), type.getVersio());
        //do not use hashtag uris as map key!
        if (koodiUris.getMeta() == null) {
            koodiUris.setMeta(Maps.<String, KoodiV1RDTO>newHashMap());
        }
        koodiUris.getMeta().put(type.getKoodiUri(), convertToKoodiDTO(new KoodiUrisV1RDTO(), type, locale, fieldName, showMeta));
    }
}
