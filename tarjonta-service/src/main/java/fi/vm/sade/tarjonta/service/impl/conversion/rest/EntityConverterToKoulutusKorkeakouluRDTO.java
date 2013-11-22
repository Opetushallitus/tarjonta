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
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.MetaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
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

    private boolean showMeta;

    @Override
    public KoulutusKorkeakouluV1RDTO convert(KoulutusmoduuliToteutus komoto) {
        LOG.debug("in KomotoConverterToKorkeakouluDTO : {}", komoto);
        KoulutusKorkeakouluV1RDTO kkDto = new KoulutusKorkeakouluV1RDTO();
        if (komoto == null) {
            return kkDto;
        }

        showMeta = komoto.isShowMeta();

        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
        kkDto.setOid(komoto.getOid());
        kkDto.setKomotoOid(komoto.getOid());
        kkDto.setKomoOid(komo.getOid());
        kkDto.setTila(komoto.getTila());
        kkDto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.fromValue(komo.getModuuliTyyppi().name()));

        // WTF? Database @Temporal DATE becomes string, normal "date" is milliseconds...
        kkDto.setKoulutuksenAlkamisPvm(komoto.getKoulutuksenAlkamisPvm() != null ? new Date(komoto.getKoulutuksenAlkamisPvm().getTime()) : null);

        KuvausV1RDTO<KomotoTeksti> komotoKuvaus = new KuvausV1RDTO<KomotoTeksti>();
        komotoKuvaus.putAll(komotoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit(), showMeta));
        kkDto.setKuvausKomoto(komotoKuvaus);

        KuvausV1RDTO<KomoTeksti> komoKuvaus = new KuvausV1RDTO<KomoTeksti>();
        komoKuvaus.putAll(komoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komo.getTekstit(), showMeta));
        kkDto.setKuvausKomo(komoKuvaus);

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
                kkDto.setKoulutusohjelma(convertToNimiDTO(komo.getKoulutusohjelmaKoodi(), DEMO_LOCALE, koulutusasteTyyppi + "->koulutusohjelma", false));
                break;
            case LUKIOKOULUTUS:
                kkDto.setKoulutusohjelma(convertToNimiDTO(komo.getLukiolinja(), DEMO_LOCALE, koulutusasteTyyppi + "->lukiolinja", false));
                break;
        }
        kkDto.setKoulutuskoodi(convertToKoodiDTO(komo.getKoulutusKoodi(), DEMO_LOCALE, "koulutuskoodi"));
        kkDto.setTutkinto(koodiData(komo.getTutkintoOhjelmanNimi(), DEMO_LOCALE, "tutkinto")); //correct data mapping?
        kkDto.setOpintojenLaajuus(koodiData(komo.getLaajuusArvo(), DEMO_LOCALE, "OpintojenLaajuus (arvo uri)"));
        kkDto.setTunniste(komo.getUlkoinenTunniste());
        kkDto.setKoulutusasteTyyppi(koulutusasteTyyppi);
        kkDto.setOrganisaatio(searchOrganisaationNimi(komoto.getTarjoaja()));
        kkDto.setKoulutusaste(koodiData(komo.getKoulutusAste(), DEMO_LOCALE, "KoulutusAste"));
        kkDto.setKoulutusala(koodiData(komo.getKoulutusala(), DEMO_LOCALE, "Koulutusala"));
        kkDto.setOpintoala(koodiData(komo.getOpintoala(), DEMO_LOCALE, "Opintoala"));
        kkDto.setTutkintonimike(koodiData(komo.getTutkintonimike(), DEMO_LOCALE, "Tutkintonimike"));
        kkDto.setEqf(komoData(komo.getEqfLuokitus(), DEMO_LOCALE, "EQF", true));
        kkDto.setTeemas(convertToKoodiDTO(komoto.getTeemas(), DEMO_LOCALE, "teemas"));
        kkDto.setOpetuskielis(convertToKoodiDTO(komoto.getOpetuskielis(), DEMO_LOCALE, "Opetuskielis"));
        final String maksullisuus = komoto.getMaksullisuus();
        kkDto.setOpintojenMaksullisuus(maksullisuus != null && Boolean.valueOf(maksullisuus));
        kkDto.setOpetusmuodos(convertToKoodiDTO(komoto.getOpetusmuotos(), DEMO_LOCALE, "opetusmuodos"));
        kkDto.setPohjakoulutusvaatimukset(convertToKoodiDTO(komoto.getKkPohjakoulutusvaatimus(), DEMO_LOCALE, "pohjakoulutusvaatimukset"));
        kkDto.setSuunniteltuKestoTyyppi(koodiData(komoto.getSuunniteltuKestoYksikko(), DEMO_LOCALE, "kestotyyppi"));
        kkDto.setSuunniteltuKestoArvo(komoto.getSuunniteltuKestoArvo());
        kkDto.setAmmattinimikkeet(convertToKoodiDTO(komoto.getAmmattinimikes(), DEMO_LOCALE, "Ammattinimikeet"));

        if (komoto.getHinta() != null) {
            kkDto.setHinta(komoto.getHinta().doubleValue());
        }

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

    private NimiV1RDTO koulutusohjelmaUiMetaDTO(final MonikielinenTeksti mt, final String langCode, final String msg) {
        NimiV1RDTO data = new NimiV1RDTO();
        final String code = langCode.toLowerCase();

        for (TekstiKaannos tk : mt.getTekstis()) {

            final KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(tk.getKieliKoodi());
            final String koodiUri = type.getKoodiUri();
            final String text = tk.getArvo();
            data.getTekstis().put(koodiUri, text);
            if (showMeta) {
                data.getMeta().put(koodiUri, convertToKoodiUriDTO(koodiUri, langCode, "koulutusohjelma"));
            }
        }
        return data;
    }

    private void convertKoodistoData(KoodiV1RDTO dto, String fromKoodiUri, final String arvo) {
        if (fromKoodiUri != null && !fromKoodiUri.isEmpty()) {
            final KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(fromKoodiUri);
            dto.setUri(type.getKoodiUri());
            dto.setVersio(type.getVersio());
            dto.setArvo(arvo);
            dto.setKaannos(tarjontaKoodistoHelper.getKoodiNimi(fromKoodiUri, new Locale("FI")));
        }
    }

    private void convertKoodistoKieliData(KoodiV1RDTO dto, String koodiUri, String kieliUri, final String arvo, Locale locale) {
        if (kieliUri != null && !kieliUri.isEmpty()) {
            final KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(kieliUri);
            dto.setKieliUri(type.getKoodiUri());
            dto.setKieliVersio(type.getVersio() + "");
            dto.setKieliArvo(arvo);
            dto.setKieliKaannos(tarjontaKoodistoHelper.getKoodiNimi(kieliUri, locale));
            dto.setKaannos(tarjontaKoodistoHelper.getKoodiNimi(koodiUri, new Locale(arvo)));
        }
    }

    private KoodiV1RDTO komoData(String koodistoKoodiUri, final String locale, final String fieldName, boolean allowNullKoodi) {
        if (koodistoKoodiUri != null && !koodistoKoodiUri.isEmpty()) {
            return convertToKoodiDTO(koodistoKoodiUri, locale, fieldName, allowNullKoodi);
        }
        return new KoodiV1RDTO();
    }

    private KoodiV1RDTO koodiData(String koodistoKoodiUri, final String locale, final String fieldName) {
        return komoData(koodistoKoodiUri, locale, fieldName, false);
    }

    private MetaV1RDTO convertToKoodiDTO(final String fromKoodiUri, final String langCode, final String fieldName, boolean allowNullKoodi) {
        MetaV1RDTO koodiUriDto = new MetaV1RDTO();
        convertKoodiUriToKoodiDTO(fromKoodiUri, koodiUriDto, new Locale(langCode.toLowerCase()), fieldName, allowNullKoodi);
        return koodiUriDto;
    }

    private MetaV1RDTO convertToKoodiDTO(final String fromKoodiUri, final String langCode, final String fieldName) {
        return convertToKoodiDTO(fromKoodiUri, langCode, fieldName, false);
    }

    private NimiV1RDTO convertToNimiDTO(final String fromKoodiUri, final String langCode, final String fieldName, boolean allowNullKoodi) {
        NimiV1RDTO koodiUriDto = new NimiV1RDTO();
        convertKoodiUriToKoodiDTO(fromKoodiUri, koodiUriDto, new Locale(langCode.toLowerCase()), fieldName, allowNullKoodi);
        return koodiUriDto;
    }

    private KoodiUrisV1RDTO convertToKoodiDTO(final Set<KoodistoUri> fromKoodiUris, final String langCode, final String fieldName) {
        KoodiUrisV1RDTO koodiMapDto = new KoodiUrisV1RDTO();
        for (KoodistoUri koodiUri : fromKoodiUris) {
            final KoodiUriAndVersioType type = TarjontaKoodistoHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(koodiUri.getKoodiUri());

            koodiMapDto.getUris().put(type.getKoodiUri(), type.getVersio());
            if (showMeta) {
                //Meta key koodi URI must not have the hashtag!
                koodiMapDto.getMeta().put(type.getKoodiUri(), convertToKoodiUriDTO(type.getKoodiUri(), langCode, fieldName));
            }
        }

        return koodiMapDto;
    }

    private KoodiV1RDTO convertToKoodiUriDTO(KoodiV1RDTO uiDto, final String fromKoodiUri, final String langCode, final String fieldName) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null in field : " + fieldName);
        Preconditions.checkNotNull(langCode, "Locale object cannot be null. field in " + fieldName);

        final KoodiType koodiType = tarjontaKoodistoHelper.getKoodiByUri(fromKoodiUri);

        Preconditions.checkNotNull(koodiType, "No koodisto service koodi URI found by '" + fromKoodiUri + "'.");
        //KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodiType, new Locale(langCode.toLowerCase()));
        convertKoodistoData(uiDto, fromKoodiUri, koodiType.getKoodiArvo());
        return uiDto;
    }

    private KoodiV1RDTO convertToKoodiUriDTO(final String fromKoodiUri, final String langCode, final String fieldName) {
        return convertToKoodiUriDTO(new KoodiV1RDTO(), fromKoodiUri, langCode, fieldName);
    }

    private void convertKoodiUriToKoodiDTO(final String fromKoodiUri, final MetaV1RDTO koodiDto, final Locale locale, final String fieldName, boolean allowNullKoodisto) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null in field : " + fieldName);
        Preconditions.checkNotNull(locale, "Locale object cannot be null in field in " + fieldName);
        Preconditions.checkNotNull(koodiDto, "KoodiV1RDTO object cannot be null in field " + fieldName);

        final KoodiType koodiType = tarjontaKoodistoHelper.getKoodiByUri(fromKoodiUri);

        if (koodiType == null && allowNullKoodisto) {
            //TODO: remove this code block when data is fixed
            toKoodiUriDTO(koodiDto, "", new KoodiType(), locale);
        } else {
            Preconditions.checkNotNull(koodiType, "No result found by koodisto koodi URI '" + fromKoodiUri + "' in field " + fieldName);
            toKoodiUriDTO(koodiDto, fromKoodiUri, koodiType, locale);
            addOtherLanguages(koodiDto, koodiType.getMetadata(), locale);
        }
    }

    private KoodiV1RDTO toKoodiUriDTO(KoodiV1RDTO dto, final String fromKoodiUri, final KoodiType koodiByUri, final Locale locale) {
        Preconditions.checkNotNull(fromKoodiUri, "Koodi URI cannot be null.");
        Preconditions.checkNotNull(koodiByUri, "Locale object cannot be null.");

        if (dto == null) {
            dto = new KoodiV1RDTO();
        }
        convertKoodistoData(dto, fromKoodiUri, koodiByUri.getKoodiArvo());
        return dto;
    }

    private void addOtherLanguages(final MetaV1RDTO koodiUriDto, List<KoodiMetadataType> metadata, final Locale locale) {
        Preconditions.checkNotNull(koodiUriDto, "KoodiUriDTO object cannot be null.");

        for (KoodiMetadataType meta : metadata) {
            final String kieliUri = tarjontaKoodistoHelper.convertKielikoodiToKieliUri(meta.getKieli().value());

            KoodiV1RDTO dto = new KoodiV1RDTO();
            convertKoodistoKieliData(dto, koodiUriDto.getUri(), kieliUri, meta.getKieli().value(), locale);
            if (showMeta) {
                koodiUriDto.getMeta().put(kieliUri, dto);
            }
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
