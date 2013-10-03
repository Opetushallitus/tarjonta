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
import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import static fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.TUTKINTO;
import static fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.resources.dto.KoodiUriDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KorkeakouluDTO;
import fi.vm.sade.tarjonta.service.resources.dto.UiDTO;
import fi.vm.sade.tarjonta.service.resources.dto.UiListDTO;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import static fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS;
import static fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi.AMMATTIKORKEAKOULUTUS;
import static fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi.LUKIOKOULUTUS;
import static fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi.YLIOPISTOKOULUTUS;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Conversion services for REST service.
 *
 * @author jani
 */
public class KomotoConverterToKorkeakouluDTO extends AbstractFromDomainConverter<KoulutusmoduuliToteutus, KorkeakouluDTO> {

    private static final Logger LOG = LoggerFactory.getLogger(KomotoConverterToKorkeakouluDTO.class);
    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    private static final String DEMO_LOCALE = "fi";

    @Override
    public KorkeakouluDTO convert(KoulutusmoduuliToteutus komoto) {
        KorkeakouluDTO t = new KorkeakouluDTO();
        if (komoto == null) {
            return t;
        }

        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
        t.setOid(komoto.getOid());
        t.setKomoOid(komo.getOid());
        t.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.fromValue(komo.getModuuliTyyppi().name()));

        t.setKoulutuksenAlkamisPvm(komoto.getKoulutuksenAlkamisPvm());
        t.setOpintojenLaajuus(simpleUiDTO("unavailable"));
        //KOMO
        switch (komo.getModuuliTyyppi()) {
            case TUTKINTO:
                t.setKoulutuskoodi(convertToUiListTO(komo.getKoulutusKoodi(), DEMO_LOCALE, "tutkinto->koulutuskoodi"));
                break;
            case TUTKINTO_OHJELMA:
                final KoulutusasteTyyppi koulutusaste = EntityUtils.KoulutusTyyppiStrToKoulutusAsteTyyppi(komo.getKoulutustyyppi());
                switch (koulutusaste) {
                    case YLIOPISTOKOULUTUS:
                    case AMMATTIKORKEAKOULUTUS:
                    case AMMATILLINEN_PERUSKOULUTUS:
                        t.setKoulutusohjelma(convertToUiListTO(komo.getKoulutusohjelmaKoodi(), DEMO_LOCALE, "tutkinto-ohjelma->koulutusohjelma"));
                        break;
                    case LUKIOKOULUTUS:
                        t.setKoulutusohjelma(convertToUiListTO(komo.getLukiolinja(), DEMO_LOCALE, "tutkinto-ohjelma->lukiolinja"));
                        break;
                }

                t.setKoulutuskoodi(convertToUiListTO(komo.getKoulutusKoodi(), DEMO_LOCALE, "tutkinto-ohjelma->koulutuskoodi"));

                t.setKoulutusohjelma(convertToUiListTO(komo.getKoulutusohjelmaKoodi(), DEMO_LOCALE, "tutkinto-ohjelma->koulutusohjelma"));
                t.setTeemas(convertToUiListDTO(komoto.getTeemas(), DEMO_LOCALE, "tutkinto-ohjelma->teemas"));
                t.setOpetuskielis(convertToUiListDTO(komoto.getOpetuskielis(), DEMO_LOCALE, "tutkinto-ohjelma->Opetuskielis"));
                final String maksullisuus = komoto.getMaksullisuus();
                t.setOpintojenMaksullisuus(maksullisuus != null && maksullisuus.equals("true") ? true : false);
                t.setOpetusmuodos(convertToUiListDTO(komoto.getOpetusmuotos(), DEMO_LOCALE, "tutkinto-ohjelma->opetusmuodos"));
                t.setPohjakoulutusvaatimukset(convertToUiListDTO(komoto.getKkPohjakoulutusvaatimus(), DEMO_LOCALE, "tutkinto-ohjelma->pohjakoulutusvaatimukset"));
                t.setSuunniteltuKesto(simpleUiDTO(komoto.getSuunniteltuKestoArvo()));
                t.setSuunniteltuKestoTyyppi(convertToKoodiUriDTO(komoto.getSuunniteltuKestoYksikko(), DEMO_LOCALE, "tutkinto-ohjelma->SuunniteltuKestoTyyppi", true));
                t.setTunniste(komo.getUlkoinenTunniste());
                break;
        }

        t.setKoulutusaste(komoData(komo.getKoulutusAste(), DEMO_LOCALE, "KoulutusAste"));
        t.setKoulutusala(komoData(komo.getKoulutusala(), DEMO_LOCALE, "Koulutusala"));
        t.setOpintoala(komoData(komo.getOpintoala(), DEMO_LOCALE, "Opintoala"));
        t.setTutkinto(komoData(komo.getTutkintoOhjelmanNimi(), DEMO_LOCALE, "TutkintoOhjelmanNimi"));
        t.setTutkintonimike(komoData(komo.getTutkintonimike(), DEMO_LOCALE, "Tutkintonimike"));
        t.setEqf(simpleUiDTO(komo.getEqfLuokitus())); //not a koodisto koodi URI

        Map<KomoTeksti, UiListDTO> tekstis = t.getTekstis();
        for (Map.Entry<KomoTeksti, MonikielinenTeksti> e : komo.getTekstit().entrySet()) {
            UiListDTO dto = new UiListDTO();

            Collection<TekstiKaannos> tekstis1 = e.getValue().getTekstis();
            for (TekstiKaannos kaannos : tekstis1) {
                UiDTO uri = new UiDTO();
                uri.setKoodi(convertKoodiUri(kaannos.getKieliKoodi(), kaannos.getArvo()));
                uri.setArvo(kaannos.getArvo());
                dto.getTekstis().add(uri);
            }
            tekstis.put(KomoTeksti.PATEVYYS, dto);
        }

        EntityUtils.copyYhteyshenkilos(komoto.getYhteyshenkilos(), t.getYhteyshenkilos());


        return t;
    }

    private String nullStr(final String s) {
        if (s != null) {
            return s;
        }

        return "";
    }

    private UiDTO simpleUiDTO(final String arvo) {
        UiDTO dto = new UiDTO();
        dto.setArvo(nullStr(arvo));
        return dto;
    }

    private KoodiUriDTO convertKoodiUri(final String koodistoKoodiUri, final String arvo) {
        KoodiUriDTO koodiUri = new KoodiUriDTO();
        if (koodistoKoodiUri != null && !koodistoKoodiUri.isEmpty()) {
            final String[] koodiUriSplit = TarjontaKoodistoHelper.splitKoodiURIWithVersion(koodistoKoodiUri);
            koodiUri.setUri(koodiUriSplit[0]);
            koodiUri.setVersio(koodiUriSplit[1]);
        }
        koodiUri.setArvo(arvo);

        return koodiUri;
    }

    private UiDTO komoData(String koodistoKoodiUri, final String locale, final String fieldName) {
        if (koodistoKoodiUri != null && !koodistoKoodiUri.isEmpty()) {
            return convertToUiListTO(koodistoKoodiUri, locale, fieldName);
        }
        return simpleUiDTO(null);
    }

    private UiListDTO convertToUiListTO(final String fromKoodiUri, final String langCode, final String fieldName) {
        UiListDTO koodiUriDto = new UiListDTO();
        convertKoodiUriToKoodiUriListDTO(fromKoodiUri, koodiUriDto, new Locale(langCode.toLowerCase()), fieldName);
        return koodiUriDto;
    }

    private UiListDTO convertToUiListDTO(final Set<KoodistoUri> fromKoodiUris, final String langCode, final String fieldName) {
        UiListDTO koodiUriListDTO = new UiListDTO();
        for (KoodistoUri s : fromKoodiUris) {
            koodiUriListDTO.getTekstis().add(convertToKoodiUriDTO(s.getKoodiUri(), langCode, fieldName, false));
        }

        return koodiUriListDTO;
    }

    private UiDTO convertToKoodiUriDTO(final String fromKoodiUri, final String langCode, final String fieldName, boolean convertKoodiLang) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null in field : " + fieldName);
        Preconditions.checkNotNull(langCode, "Locale object cannot be null. field in " + fieldName);

        UiDTO uiDto = new UiDTO();
        final KoodiType koodiByUri = tarjontaKoodistoHelper.getKoodiByUri(fromKoodiUri);

        KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodiByUri, new Locale(langCode.toLowerCase()));
        if (convertKoodiLang) {
            convertKoodiUri(fromKoodiUri, koodiByUri.getKoodiArvo());
        } else {
            uiDto.setKoodi(convertKoodiUri(fromKoodiUri, koodiByUri.getKoodiArvo()));
        }

        uiDto.setArvo(metadata.getNimi());

        return uiDto;
    }

    private void convertKoodiUriToKoodiUriListDTO(final String fromKoodiUri, final UiListDTO koodiUriListDto, final Locale locale, final String fieldName) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null in field : " + fieldName);
        Preconditions.checkNotNull(locale, "Locale object cannot be null in field in " + fieldName);
        Preconditions.checkNotNull(koodiUriListDto, "UiListDTO object cannot be null in field " + fieldName);

        final KoodiType koodiByUri = tarjontaKoodistoHelper.getKoodiByUri(fromKoodiUri);
        Preconditions.checkNotNull(koodiByUri, "No result found by koodisto koodi URI '" + fromKoodiUri + "' in field " + fieldName);

        toKoodiUriDTO(koodiUriListDto, fromKoodiUri, koodiByUri, locale);
        addOtherLanguages(koodiUriListDto, koodiByUri.getMetadata(), locale);
    }

    private UiDTO toKoodiUriDTO(UiDTO uiDto, final String fromKoodiUri, final KoodiType koodiByUri, final Locale locale) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null.");
        Preconditions.checkNotNull(koodiByUri, "Locale object cannot be null.");

        if (uiDto == null) {
            uiDto = new UiDTO();
        }

        uiDto.setKoodi(convertKoodiUri(fromKoodiUri, koodiByUri.getKoodiArvo()));
        KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodiByUri, locale);
        uiDto.setArvo(metadata.getNimi());
        return uiDto;
    }

    private void addOtherLanguages(final UiListDTO koodiUriDto, List<KoodiMetadataType> metadata, final Locale locale) {
        Preconditions.checkNotNull(koodiUriDto, "KoodiUriDTO object cannot be null.");
        for (KoodiMetadataType meta : metadata) {
            final String kieliUri = tarjontaKoodistoHelper.convertKielikoodiToKieliUri(meta.getKieli().value());

            UiDTO dto = new UiDTO();
            dto.setKoodi(convertKoodiUri(kieliUri, meta.getKieli().value()));
            dto.setArvo(meta.getNimi());
            koodiUriDto.getTekstis().add(dto);
        }
    }
}
