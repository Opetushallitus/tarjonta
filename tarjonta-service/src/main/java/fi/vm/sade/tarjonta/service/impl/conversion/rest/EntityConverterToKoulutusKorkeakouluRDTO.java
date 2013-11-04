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
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUriV1DTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.SuunniteltuKestoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.UiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.UiMetaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.TekstiV1RDTO;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Conversion services for REST service.
 *
 * @author jani
 */
public class EntityConverterToKoulutusKorkeakouluRDTO extends AbstractFromDomainConverter<KoulutusmoduuliToteutus, KoulutusKorkeakouluV1RDTO> {

    private static final Logger LOG = LoggerFactory.getLogger(EntityConverterToKoulutusKorkeakouluRDTO.class);
    @Autowired(required = true)
    private CommonRestKoulutusConverters<KomoTeksti> komoKoulutusConverters;
    @Autowired(required = true)
    private CommonRestKoulutusConverters<KomotoTeksti> komotoKoulutusConverters;
    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    private static final String DEMO_LOCALE = "fi";
    @Autowired
    private OrganisaatioService organisaatioService;

    @Override
    public KoulutusKorkeakouluV1RDTO convert(KoulutusmoduuliToteutus komoto) {
        LOG.debug("in KomotoConverterToKorkeakouluDTO : {}", komoto);
        KoulutusKorkeakouluV1RDTO kkDto = new KoulutusKorkeakouluV1RDTO();
        if (komoto == null) {
            return kkDto;
        }

        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
        kkDto.setOid(komoto.getOid());
        kkDto.setKomotoOid(komoto.getOid());
        kkDto.setKomoOid(komo.getOid());
        kkDto.setTila(komoto.getTila());
        kkDto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.fromValue(komo.getModuuliTyyppi().name()));

        // WTF? Database @Temporal DATE becomes string, normal "date" is milliseconds...
        kkDto.setKoulutuksenAlkamisPvm(komoto.getKoulutuksenAlkamisPvm() != null ? new Date(komoto.getKoulutuksenAlkamisPvm().getTime()) : null);
        kkDto.setOpintojenLaajuus(simpleUiDTO("unavailable"));
        kkDto.setKoulutuskoodi(convertToUiMetaDTO(komo.getKoulutusKoodi(), DEMO_LOCALE, "koulutuskoodi"));

        TekstiV1RDTO<KomotoTeksti> komotoKuvaus = new TekstiV1RDTO<KomotoTeksti>();
        komotoKuvaus.setTekstis(komotoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit()).getTekstis());
        kkDto.setKuvausKomoto(komotoKuvaus);

        //KOMO
        Preconditions.checkNotNull(komo.getKoulutustyyppi(), "KoulutusasteTyyppi cannot be null!");
        KoulutusasteTyyppi koulutusasteTyyppi = EntityUtils.KoulutusTyyppiStrToKoulutusAsteTyyppi(komo.getKoulutustyyppi());
        switch (koulutusasteTyyppi) {
            case KORKEAKOULUTUS:
            case YLIOPISTOKOULUTUS:
            case AMMATTIKORKEAKOULUTUS:
                kkDto.setKoulutusohjelma(koulutusohjelmaUiMetaDTO(komo.getNimi(), DEMO_LOCALE, koulutusasteTyyppi + "->koulutusohjelma"));
                break;
            case AMMATILLINEN_PERUSKOULUTUS:
                kkDto.setKoulutusohjelma(convertToUiMetaDTO(komo.getKoulutusohjelmaKoodi(), DEMO_LOCALE, koulutusasteTyyppi + "->koulutusohjelma"));
                break;
            case LUKIOKOULUTUS:
                kkDto.setKoulutusohjelma(convertToUiMetaDTO(komo.getLukiolinja(), DEMO_LOCALE, koulutusasteTyyppi + "->lukiolinja"));
                break;
        }

        kkDto.setTutkinto(komoData(komo.getTutkintoOhjelmanNimi(), DEMO_LOCALE, "tutkinto")); //correct data mapping?
        kkDto.setOpintojenLaajuus(komoData(komo.getLaajuusArvo(), DEMO_LOCALE, "OpintojenLaajuus (arvo uri)"));
        kkDto.setTunniste(komo.getUlkoinenTunniste());
        kkDto.setKoulutusasteTyyppi(koulutusasteTyyppi);
        kkDto.setOrganisaatio(searchOrganisaationNimi(komoto.getTarjoaja()));
        kkDto.setKoulutusaste(komoData(komo.getKoulutusAste(), DEMO_LOCALE, "KoulutusAste"));
        kkDto.setKoulutusala(komoData(komo.getKoulutusala(), DEMO_LOCALE, "Koulutusala"));
        kkDto.setOpintoala(komoData(komo.getOpintoala(), DEMO_LOCALE, "Opintoala"));
        //kkDto.setTutkinto(komoData(komo.getTutkinto(), DEMO_LOCALE, "Tutkinto"));
        kkDto.setTutkintonimike(komoData(komo.getTutkintonimike(), DEMO_LOCALE, "Tutkintonimike"));
        kkDto.setEqf(komoData(komo.getEqfLuokitus(), DEMO_LOCALE, "EQF", true));

        kkDto.setTeemas(convertToUiMetaDTO(komoto.getTeemas(), DEMO_LOCALE, "teemas"));
        kkDto.setOpetuskielis(convertToUiMetaDTO(komoto.getOpetuskielis(), DEMO_LOCALE, "Opetuskielis"));
        final String maksullisuus = komoto.getMaksullisuus();
        kkDto.setOpintojenMaksullisuus(maksullisuus != null && Boolean.valueOf(maksullisuus));
        kkDto.setOpetusmuodos(convertToUiMetaDTO(komoto.getOpetusmuotos(), DEMO_LOCALE, "opetusmuodos"));
        kkDto.setPohjakoulutusvaatimukset(convertToUiMetaDTO(komoto.getKkPohjakoulutusvaatimus(), DEMO_LOCALE, "pohjakoulutusvaatimukset"));
        kkDto.setSuunniteltuKesto(suunniteltuKestoDTO(komoto.getSuunniteltuKestoArvo(), komoto.getSuunniteltuKestoYksikko()));
        kkDto.setAmmattinimikkeet(convertToUiMetaDTO(komoto.getAmmattinimikes(), DEMO_LOCALE, "Ammattinimikeet"));

        if (komoto.getHinta() != null) {
            kkDto.setHinta(komoto.getHinta().doubleValue());
        }

        TekstiV1RDTO<KomoTeksti> komoKuvaus = new TekstiV1RDTO<KomoTeksti>();
        komoKuvaus.setTekstis(komoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komo.getTekstit()).getTekstis());
        kkDto.setKuvausKomo(komoKuvaus);

        EntityUtils.copyYhteyshenkilos(komoto.getYhteyshenkilos(), kkDto.getYhteyshenkilos());
        LOG.debug("in KomotoConverterToKorkeakouluDTO : {}", kkDto);
        return kkDto;
    }

    private String nullStr(final String s) {
        if (s != null) {
            return s;
        }

        return "";
    }

    private UiV1RDTO simpleUiDTO(final String arvo) {
        UiV1RDTO dto = new UiV1RDTO();
        dto.setArvo(nullStr(arvo));
        return dto;
    }

    private static UiMetaV1RDTO koulutusohjelmaUiMetaDTO(final MonikielinenTeksti mt, final String langCode, final String msg) {
        UiMetaV1RDTO meta = new UiMetaV1RDTO();
        final String code = langCode.toLowerCase();

        for (TekstiKaannos tk : mt.getTekstis()) {

            final KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(tk.getKieliKoodi());
            final String koodiUri = type.getKoodiUri();
            final String text = tk.getArvo();

            LOG.debug("contains: {} {}", tk.getKieliKoodi(), type.getVersio());

            if (koodiUri.contains(code)) {
                meta.setArvo(text); //in user's language.
            }
            meta.getMeta().put(koodiUri, new UiV1RDTO(null, koodiUri, type.getVersio() + "", text));
        }
        return meta;
    }

    private SuunniteltuKestoV1RDTO suunniteltuKestoDTO(final String kestoArvo, String kestoArvoKoodiUri) {
        SuunniteltuKestoV1RDTO kesto = new SuunniteltuKestoV1RDTO();
        SuunniteltuKestoV1RDTO dto = (SuunniteltuKestoV1RDTO) convertToKoodiUriDTO(kesto, kestoArvoKoodiUri, DEMO_LOCALE, "tutkinto-ohjelma->SuunniteltuKestoTyyppi", false);
        dto.setArvo(nullStr(kestoArvo));
        return dto;
    }

    private KoodiUriV1DTO convertKoodiUri(final String koodistoKoodiUri, final String arvo) {
        KoodiUriV1DTO koodiUri = new KoodiUriV1DTO();
        if (koodistoKoodiUri != null && !koodistoKoodiUri.isEmpty()) {
            final KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(koodistoKoodiUri);
            koodiUri.setUri(type.getKoodiUri());
            koodiUri.setVersio(type.getVersio() + "");
        }

        koodiUri.setArvo(arvo);
        koodiUri.setKaannos(tarjontaKoodistoHelper.getKoodiNimi(koodiUri.getUri(), new Locale("FI")));

        return koodiUri;
    }

    private UiV1RDTO komoData(String koodistoKoodiUri, final String locale, final String fieldName, boolean allowNullKoodi) {
        if (koodistoKoodiUri != null && !koodistoKoodiUri.isEmpty()) {
            return convertToUiMetaDTO(koodistoKoodiUri, locale, fieldName, allowNullKoodi);
        }
        return simpleUiDTO(null);
    }

    private UiV1RDTO komoData(String koodistoKoodiUri, final String locale, final String fieldName) {
        return komoData(koodistoKoodiUri, locale, fieldName, false);
    }

    private UiMetaV1RDTO convertToUiMetaDTO(final String fromKoodiUri, final String langCode, final String fieldName, boolean allowNullKoodi) {
        UiMetaV1RDTO koodiUriDto = new UiMetaV1RDTO();
        convertKoodiUriToKoodiUriListDTO(fromKoodiUri, koodiUriDto, new Locale(langCode.toLowerCase()), fieldName, allowNullKoodi);
        return koodiUriDto;
    }

    private UiMetaV1RDTO convertToUiMetaDTO(final String fromKoodiUri, final String langCode, final String fieldName) {
        return convertToUiMetaDTO(fromKoodiUri, langCode, fieldName, false);
    }

    private UiMetaV1RDTO convertToUiMetaDTO(final Set<KoodistoUri> fromKoodiUris, final String langCode, final String fieldName) {
        UiMetaV1RDTO koodiUriListDTO = new UiMetaV1RDTO();
        for (KoodistoUri koodiUri : fromKoodiUris) {
            final KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(koodiUri.getKoodiUri());

            //Meta key koodi URI must not have the hashtag!
            koodiUriListDTO.getMeta().put(type.getKoodiUri(), convertToKoodiUriDTO(koodiUri.getKoodiUri(), langCode, fieldName, false));
        }

        return koodiUriListDTO;
    }

    private UiV1RDTO convertToKoodiUriDTO(UiV1RDTO uiDto, final String fromKoodiUri, final String langCode, final String fieldName, boolean convertKoodiLang) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null in field : " + fieldName);
        Preconditions.checkNotNull(langCode, "Locale object cannot be null. field in " + fieldName);

        final KoodiType koodiType = tarjontaKoodistoHelper.getKoodiByUri(fromKoodiUri);

        Preconditions.checkNotNull(koodiType, "No koodisto service koodi URI found by '" + fromKoodiUri + "'.");

        KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodiType, new Locale(langCode.toLowerCase()));
        if (convertKoodiLang) {
            convertKoodiUri(fromKoodiUri, koodiType.getKoodiArvo());
        } else {
            uiDto.setKoodi(convertKoodiUri(fromKoodiUri, koodiType.getKoodiArvo()));
        }

        uiDto.setArvo(metadata.getNimi());

        return uiDto;
    }

    private UiV1RDTO convertToKoodiUriDTO(final String fromKoodiUri, final String langCode, final String fieldName, boolean convertKoodiLang) {
        return convertToKoodiUriDTO(new UiV1RDTO(), fromKoodiUri, langCode, fieldName, convertKoodiLang);
    }

    private void convertKoodiUriToKoodiUriListDTO(final String fromKoodiUri, final UiMetaV1RDTO koodiUriListDto, final Locale locale, final String fieldName, boolean allowNullKoodisto) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null in field : " + fieldName);
        Preconditions.checkNotNull(locale, "Locale object cannot be null in field in " + fieldName);
        Preconditions.checkNotNull(koodiUriListDto, "UiListDTO object cannot be null in field " + fieldName);

        final KoodiType koodiType = tarjontaKoodistoHelper.getKoodiByUri(fromKoodiUri);

        if (koodiType == null && allowNullKoodisto) {
            //TODO: remove this code block when data is fixed
            toKoodiUriDTO(koodiUriListDto, "", new KoodiType(), locale);
        } else {
            Preconditions.checkNotNull(koodiType, "No result found by koodisto koodi URI '" + fromKoodiUri + "' in field " + fieldName);
            toKoodiUriDTO(koodiUriListDto, fromKoodiUri, koodiType, locale);
            addOtherLanguages(koodiUriListDto, koodiType.getMetadata(), locale);
        }
    }

    private UiV1RDTO toKoodiUriDTO(UiV1RDTO uiDto, final String fromKoodiUri, final KoodiType koodiByUri, final Locale locale) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null.");
        Preconditions.checkNotNull(koodiByUri, "Locale object cannot be null.");

        if (uiDto == null) {
            uiDto = new UiV1RDTO();
        }

        uiDto.setKoodi(convertKoodiUri(fromKoodiUri, koodiByUri.getKoodiArvo()));

        KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodiByUri, locale);
        if (metadata != null) {
            uiDto.setArvo(metadata.getNimi());
        }
        return uiDto;
    }

    private void addOtherLanguages(final UiMetaV1RDTO koodiUriDto, List<KoodiMetadataType> metadata, final Locale locale) {
        Preconditions.checkNotNull(koodiUriDto, "KoodiUriDTO object cannot be null.");
        for (KoodiMetadataType meta : metadata) {
            final String kieliUri = tarjontaKoodistoHelper.convertKielikoodiToKieliUri(meta.getKieli().value());

            UiV1RDTO dto = new UiV1RDTO();
            dto.setKoodi(convertKoodiUri(kieliUri, meta.getKieli().value()));
            dto.setArvo(meta.getNimi());
            koodiUriDto.getMeta().put(kieliUri, dto);
        }
    }

    private OrganisaatioV1RDTO searchOrganisaationNimi(String tarjoajaOid) {
        final OrganisaatioDTO organisaatioDto = organisaatioService.findByOid(tarjoajaOid);

        Preconditions.checkNotNull(organisaatioDto, "OrganisaatioDTO object cannot be null.");
        Preconditions.checkNotNull(organisaatioDto.getOid(), "OrganisaatioDTO OID cannot be null.");
        Preconditions.checkNotNull(organisaatioDto.getNimi(), "OrganisaatioDTO name object cannot be null.");

        List<MonikielinenTekstiTyyppi.Teksti> tekstis = organisaatioDto.getNimi().getTeksti();

        String nimi = null;
        final String locale = DEMO_LOCALE.toLowerCase();

        for (MonikielinenTekstiTyyppi.Teksti teksti : tekstis) {
            Preconditions.checkNotNull(teksti.getKieliKoodi(), "Locale language code cannot be null.");
            LOG.debug("{} {}", locale, teksti.getKieliKoodi().toLowerCase());

            if (teksti.getKieliKoodi().toLowerCase().equals(locale)) {
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
}
