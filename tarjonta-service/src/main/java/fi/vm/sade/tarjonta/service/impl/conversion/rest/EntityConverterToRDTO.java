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
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.model.RestParam;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.impl.resources.v1.KoulutusImplicitDataPopulator;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.YhdenPaikanSaantoBuilder;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OppiaineV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.valmistava.ValmistavaV1RDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.OpintopolkuAlkamiskausi;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum.*;

/**
 * Conversion services for REST service.
 *
 * @param <TYPE> KoulutusV1RDTO
 * @author jani
 */
@Component
public class EntityConverterToRDTO<TYPE extends KoulutusV1RDTO> {

    private static final KoulutusCommonConverter.Nullable NO = KoulutusCommonConverter.Nullable.NO;
    private static final Logger LOG = LoggerFactory.getLogger(EntityConverterToRDTO.class);
    private static final KoulutusCommonConverter.Nullable YES = KoulutusCommonConverter.Nullable.YES;
    private static final String NO_OVERRIDE_URI = EntityConverterToKomoRDTO.NO_OVERRIDE_URI;

    @Autowired
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConverters;
    @Autowired
    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKuvausConverters;
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private KoulutusCommonConverter commonConverter;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;
    @Autowired
    private KoulutusImplicitDataPopulator dataPopulator;
    @Autowired
    private YhdenPaikanSaantoBuilder yhdenPaikanSaantoBuilder;


    public TYPE convert(Class<TYPE> clazz, final KoulutusmoduuliToteutus komoto, final RestParam param) {
        LOG.debug("in KomotoConverterToKorkeakouluDTO : {}", komoto);

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
        Preconditions.checkNotNull(komo, "Koulutusmoduuli object cannot be null!");

        dto.setOid(komoto.getOid());

        dto = (TYPE) dataPopulator.defaultValuesForDto(dto);

        dto.setUniqueExternalId(komoto.getUniqueExternalId());
        dto.setKomoOid(komo.getOid());
        dto.setTila(komoto.getTila());
        dto.setModified(komoto.getUpdated());
        dto.setModifiedBy(komoto.getLastUpdatedByOid());
        Preconditions.checkNotNull(komo.getModuuliTyyppi(), "Koulutusmoduuli object cannot be null!");
        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.fromValue(komo.getModuuliTyyppi().name()));

        dto.setKoulutuksenAlkamiskausi(commonConverter.convertToKoodiDTO(komoto.getAlkamiskausiUri(), NO_OVERRIDE_URI, FieldNames.ALKAMISKAUSI, YES, param));
        dto.setKoulutuksenAlkamisvuosi(komoto.getAlkamisVuosi());
        dto.setKoulutuksenAlkamisPvms(komoto.getKoulutuksenAlkamisPvms());
        dto.setOppiaineet(oppiaineetFromEntityToDto(komoto.getOppiaineet()));

        KuvausV1RDTO<KomotoTeksti> komotoKuvaus = new KuvausV1RDTO<KomotoTeksti>();
        komotoKuvaus.putAll(komotoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit(), param.getShowMeta()));
        dto.setKuvausKomoto(komotoKuvaus);

        Map<String, String> extraParams = new HashMap<String, String>();
        if (komoto.getOpintopolkuAlkamiskausi() != null) {
            dto.setOpintopolkuAlkamiskausi(
                OpintopolkuAlkamiskausi.getMapFromEnum(komoto.getOpintopolkuAlkamiskausi())
            );
            extraParams.put("opintopolkuKesaKausi", "true");
        }
        dto.setExtraParams(extraParams);

        if (komoto.getHinta() != null) {
            dto.setHinta(komoto.getHinta().doubleValue());
        }
        dto.setHintaString(komoto.getHintaString());
        dto.setOpintojenMaksullisuus(komoto.getMaksullisuus());

        String komoOid = komoto.getKoulutusmoduuli().getOid();
        if (komoOid != null) {
            Set<String> parents = new HashSet<String>(koulutusSisaltyvyysDAO.getParents(komoOid));
            dto.setParents(parents);

            Set<String> children = new HashSet<String>(koulutusSisaltyvyysDAO.getChildren(komoOid));
            dto.setChildren(children);
        }

        if (!komoto.getSisaltyvatKoulutuskoodit().isEmpty()) {
            dto.setSisaltyvatKoulutuskoodit(commonConverter.convertToKoodiUrisDTO(komoto.getSisaltyvatKoulutuskoodit(), null, param));
        }

        dto.setIsAvoimenYliopistonKoulutus(komoto.getIsAvoimenYliopistonKoulutus());

        setSisaltyyKoulutuksiin(dto, komoto);

        //KOMO
        if (dto instanceof KoulutusKorkeakouluV1RDTO) {
            /**
             * KORKEAKOULU
             */
            KoulutusKorkeakouluV1RDTO kkDto = (KoulutusKorkeakouluV1RDTO) dto;
            kkDto.setKomotoOid(komoto.getOid());
            kkDto.setKoulutuksenTunnisteOid(komo.getKoulutuksenTunnisteOid());

            final boolean useKomotoName = komoto.getNimi() != null && !komoto.getNimi().getTekstiKaannos().isEmpty(); //OVT-7531
            kkDto.setKoulutusohjelma(commonConverter.koulutusohjelmaUiMetaDTO(useKomotoName ? komoto.getNimi() : komo.getNimi(), FieldNames.KOULUTUSOHJELMA, param));

            // Taaksepäin yhteensopivuuden takaamiseksi
            // KI käyttää vielä tätä kenttää, uusi kenttä on dto.getSisaltyvatKoulutuskoodit
            if (!komoto.getSisaltyvatKoulutuskoodit().isEmpty()) {
                kkDto.setKandidaatinKoulutuskoodi(commonConverter.convertToKoodiDTO(
                    komoto.getSisaltyvatKoulutuskoodit().iterator().next().getKoodiUri(), null, FieldNames.KOULUTUSKOODI_KANDIDAATTI, YES, param
                ));
            }

            kkDto.setTutkintonimikes(commonConverter.convertToKoodiUrisDTO(getTutkintonimikes(komoto, komo), FieldNames.TUTKINTONIMIKE, param));
            kkDto.setPohjakoulutusvaatimukset(commonConverter.convertToKoodiUrisDTO(komoto.getKkPohjakoulutusvaatimus(), FieldNames.POHJALKOULUTUSVAATIMUS, param));
            convertFlatKomoToRDTO(dto, komo, komoto, param);

            //Map<String, BinaryData> findAllImagesByKomotoOid = koulutusmoduuliToteutusDAO.findAllImagesByKomotoOid(komotoOid);
            if (param.getShowImg() && komoto.getKuvat() != null && !komoto.getKuvat().isEmpty()) {
                komoto.getKuvat().forEach((key, value) ->
                        kkDto.getOpintojenRakenneKuvas().put(key, new KuvaV1RDTO(value.getFilename(), value.getMimeType(), key, Base64.encodeBase64String(value.getData())))
                );
            }
            try {
                kkDto.setJohtaaTutkintoon(yhdenPaikanSaantoBuilder.koulutusJohtaaTutkintoon(komoto));
            } catch (IllegalStateException e) {
                LOG.error("Failed to determine tutkintoonjohtavuus for koulutus {} due to missing code relation. This will affect valintas!", kkDto.getOid(), e);
                kkDto.setJohtaaTutkintoon(false);
            }

            // tämä on nyt vain korkeakoulutuksessa, voidaan yleistää jos tarpeen
            if (komoto.getKoulutuksenlaajuusUri() != null) {
                kkDto.setKoulutuksenLaajuusKoodi(commonConverter.convertToKoodiDTO(komoto.getKoulutuksenlaajuusUri(), komoto.getKoulutuksenlaajuusUri(), FieldNames.KOULUTUKSENLAAJUUS, YES, param));
            }

        }

        else if (dto instanceof TutkintoonJohtamatonKoulutusV1RDTO) {
            /**
             * TJKK
             */
            // Implemented now as a separate if block
            // TODO Refactor this converter without if blocks to prevent unnecessary duplicate code?!
            TutkintoonJohtamatonKoulutusV1RDTO tjkkDto = (TutkintoonJohtamatonKoulutusV1RDTO) dto;
            tjkkDto.setKomotoOid(komoto.getOid());

            // Koulutusryhmä OIDit
            tjkkDto.getKoulutusRyhmaOids().clear();
            tjkkDto.getKoulutusRyhmaOids().addAll(komoto.getKoulutusRyhmaOids());
            // Opinnontyyppi
            tjkkDto.setOpinnonTyyppiUri(komoto.getOpinnonTyyppiUri());

            final boolean useKomotoName = komoto.getNimi() != null && !komoto.getNimi().getTekstiKaannos().isEmpty(); //OVT-7531
            tjkkDto.setKoulutusohjelma(commonConverter.koulutusohjelmaUiMetaDTO(useKomotoName ? komoto.getNimi() : komo.getNimi(), FieldNames.KOULUTUSOHJELMA, param));

            tjkkDto.setKoulutuksenLoppumisPvm(komoto.getKoulutuksenLoppumisPvm());

            try {
                tjkkDto.setOpintojenLaajuusPistetta(komoto.getOpintojenLaajuusArvo());
                tjkkDto.setOpintojenLaajuusyksikko(new KoodiV1RDTO("opintojenlaajuusyksikko_2", 1, null));
            } catch(NumberFormatException nfe) {
                // Invalid value will not be set
            }
            convertFlatKomoToRDTO(dto, komo, komoto, param);

            tjkkDto.setOppiaine(komoto.getOppiaine());
            tjkkDto.setOpettaja(komoto.getOpettaja());

            if (komoto.getTarjoajanKoulutus() != null) {
                tjkkDto.setTarjoajanKoulutus(komoto.getTarjoajanKoulutus().getOid());
            }

            if (tjkkDto.getParents() != null && !tjkkDto.getParents().isEmpty()) {
                List<String> komotoOids = koulutusmoduuliToteutusDAO.findOidsByKomoOids(tjkkDto.getParents());
                if (!komotoOids.isEmpty()) {
                    tjkkDto.setOpintokokonaisuusOid(komotoOids.iterator().next());
                }
            }
            if (tjkkDto.getChildren() != null && !tjkkDto.getChildren().isEmpty()) {
                List<String> komotoOids = koulutusmoduuliToteutusDAO.findOidsByKomoOids(tjkkDto.getChildren());
                if (!komotoOids.isEmpty()) {
                    tjkkDto.setOpintojaksoOids(Sets.newHashSet(komotoOids));
                }
            }
        }

        else if (dto instanceof KoulutusLukioV1RDTO) {
            /**
             * 2ASTE : LUKIO
             */
            KoulutusLukioV1RDTO lukioDto = (KoulutusLukioV1RDTO) dto;
            lukioDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getLukiolinjaUri(), komoto.getLukiolinjaUri(), FieldNames.LUKIOLINJA, NO, param));
            lukioDto.setKielivalikoima(commonConverter.convertToKielivalikoimaDTO(komoto.getTarjotutKielet(), param));
            lukioDto.setLukiodiplomit(commonConverter.convertToKoodiUrisDTO(komoto.getLukiodiplomit(), FieldNames.LUKIODIPLOMI, param));

            //tutkintonimike is on child komo object 'koulutusohjelma', not parent
            lukioDto.setTutkintonimike(commonConverter.convertToKoodiDTO(komo.getTutkintonimikeUri(), komoto.getTutkintonimikeUri(), FieldNames.TUTKINTONIMIKE, NO, param));
            lukioDto.setPohjakoulutusvaatimus(commonConverter.convertToKoodiDTO(komoto.getPohjakoulutusvaatimusUri(), NO_OVERRIDE_URI, FieldNames.POHJALKOULUTUSVAATIMUS, NO, param));
            lukioDto.setLinkkiOpetussuunnitelmaan(getFirstUrlOrNull(komoto.getLinkkis()));
            lukioDto.setKoulutuslaji(commonConverter.convertToKoodiDTO(getFirstUriOrNull(komoto.getKoulutuslajis()), NO_OVERRIDE_URI, FieldNames.KOULUTUSLAJI, NO, param));
            //has parent texts data : Tavoite, Opintojen rakenne and Jatko-opintomahdollisuudet
            final Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findParentKomo(komo);
            //override parent komo data by the child komo data
            mergeParentAndChildDataToRDTO(dto, parentKomo, komo, komoto, param);
        }

        else if (dto instanceof KoulutusAikuistenPerusopetusV1RDTO) {
            KoulutusAikuistenPerusopetusV1RDTO perusDto = (KoulutusAikuistenPerusopetusV1RDTO) dto;

            perusDto.setKielivalikoima(commonConverter.convertToKielivalikoimaDTO(komoto.getTarjotutKielet(), param));
            perusDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getOsaamisalaUri(), komoto.getOsaamisalaUri(), FieldNames.OSAAMISALA, NO, param));
            perusDto.setPohjakoulutusvaatimus(commonConverter.convertToKoodiDTO(komoto.getPohjakoulutusvaatimusUri(), NO_OVERRIDE_URI, FieldNames.POHJALKOULUTUSVAATIMUS, NO, param));
            perusDto.setLinkkiOpetussuunnitelmaan(getFirstUrlOrNull(komoto.getLinkkis()));
            perusDto.setKoulutuslaji(commonConverter.convertToKoodiDTO(getFirstUriOrNull(komoto.getKoulutuslajis()), NO_OVERRIDE_URI, FieldNames.KOULUTUSLAJI, NO, param));

            final Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findParentKomo(komo);
            mergeParentAndChildDataToRDTO(dto, parentKomo, komo, komoto, param);
        }

        else if (dto instanceof KoulutusAmmatillinenPerustutkintoV1RDTO) {
            /**
             * 2ASTE : AMMATILLINEN_PERUSTUTKINTO
             */
            KoulutusAmmatillinenPerustutkintoV1RDTO amisDto = (KoulutusAmmatillinenPerustutkintoV1RDTO) dto;

            NimiV1RDTO koulutusohjelmaOrOsaamisala = getKoulutusohjelmaOrOsaamisala(komo, komoto, param);
            if (koulutusohjelmaOrOsaamisala != null) {
                amisDto.setKoulutusohjelma(koulutusohjelmaOrOsaamisala);
            }
            amisDto.setTutkintonimikes(commonConverter.convertToKoodiUrisDTO(
                getTutkintonimikes(komoto, komo),
                FieldNames.TUTKINTONIMIKE,
                param
            ));
            // Aseta myös yksittäinen "tutkintonimike"-kenttä, jotta vanha rajapinta ei hajoa
            amisDto.setTutkintonimike(commonConverter.convertToKoodiDTO(
                komo.getTutkintonimikeUri(),
                komoto.getTutkintonimikes().isEmpty() ? null : komoto.getTutkintonimikes().iterator().next().getKoodiUri(),
                FieldNames.TUTKINTONIMIKE,
                NO,
                param
            ));
            amisDto.setPohjakoulutusvaatimus(commonConverter.convertToKoodiDTO(komoto.getPohjakoulutusvaatimusUri(), NO_OVERRIDE_URI, FieldNames.POHJALKOULUTUSVAATIMUS, NO, param));
            amisDto.setLinkkiOpetussuunnitelmaan(getFirstUrlOrNull(komoto.getLinkkis()));
            amisDto.setKoulutuslaji(commonConverter.convertToKoodiDTO(getFirstUriOrNull(komoto.getKoulutuslajis()), NO_OVERRIDE_URI, FieldNames.KOULUTUSLAJI, NO, param));

            for (Map.Entry<KomoTeksti, MonikielinenTeksti> teksti : komo.getTekstit().entrySet()) {
                if (KomoTeksti.TAVOITTEET.equals(teksti.getKey())) {
                    amisDto.setKoulutuksenTavoitteet(CommonRestConverters.toStringMap(teksti.getValue()));
                    break;
                }
            }

            // Tutke 2 muutoksen myötä koulutuksella ei aina ole koulutusohjelmaa, joten ei myöskään aina
            // ole parent komoa
            Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findParentKomo(komo);
            if (parentKomo == null) {
                parentKomo = komo;
            }

            mergeParentAndChildDataToRDTO(dto, parentKomo, komo, komoto, param);
        }
        else if (dto instanceof KoulutusAmmatillinenPerustutkintoAlk2018V1RDTO) {
            /**
             * 2ASTE : AMMATILLINEN_PERUSTUTKINTO ALK 2018
             */
            KoulutusAmmatillinenPerustutkintoAlk2018V1RDTO amisAlk2018Dto = (KoulutusAmmatillinenPerustutkintoAlk2018V1RDTO) dto;

            NimiV1RDTO koulutusohjelmaOrOsaamisala = getKoulutusohjelmaOrOsaamisala(komo, komoto, param);
            if (koulutusohjelmaOrOsaamisala != null) {
                amisAlk2018Dto.setKoulutusohjelma(koulutusohjelmaOrOsaamisala);
            }
            amisAlk2018Dto.setTutkintonimikes(commonConverter.convertToKoodiUrisDTO(
                getTutkintonimikes(komoto, komo),
                FieldNames.TUTKINTONIMIKE,
                param
            ));
            // Aseta myös yksittäinen "tutkintonimike"-kenttä, jotta vanha rajapinta ei hajoa
            amisAlk2018Dto.setTutkintonimike(commonConverter.convertToKoodiDTO(
                komo.getTutkintonimikeUri(),
                komoto.getTutkintonimikes().isEmpty() ? null : komoto.getTutkintonimikes().iterator().next().getKoodiUri(),
                FieldNames.TUTKINTONIMIKE,
                NO,
                param
            ));
            // tässä vapaaehtoinen
            if(komoto.getPohjakoulutusvaatimusUri() != null) {
                amisAlk2018Dto.setPohjakoulutusvaatimus(commonConverter.convertToKoodiDTO(komoto.getPohjakoulutusvaatimusUri(), NO_OVERRIDE_URI, FieldNames.POHJALKOULUTUSVAATIMUS, NO, param));
            }
            amisAlk2018Dto.setLinkkiOpetussuunnitelmaan(getFirstUrlOrNull(komoto.getLinkkis()));

            for (Map.Entry<KomoTeksti, MonikielinenTeksti> teksti : komo.getTekstit().entrySet()) {
                if (KomoTeksti.TAVOITTEET.equals(teksti.getKey())) {
                    amisAlk2018Dto.setKoulutuksenTavoitteet(CommonRestConverters.toStringMap(teksti.getValue()));
                    break;
                }
            }

            // Tutke 2 muutoksen myötä koulutuksella ei aina ole koulutusohjelmaa, joten ei myöskään aina
            // ole parent komoa
            Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findParentKomo(komo);
            if (parentKomo == null) {
                parentKomo = komo;
            }

            mergeParentAndChildDataToRDTO(dto, parentKomo, komo, komoto, param);
        }

        else if (dto instanceof ValmistavaKoulutusV1RDTO) {
            /**
             * 2-ASTE :
             *         - VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS
             *         - AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS
             *         - PERUSOPETUKSEN_LISAOPETUS
             *         - MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS
             *         - MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS
             *         - VAPAAN_SIVISTYSTYON_KOULUTUS
             */
            ValmistavaKoulutusV1RDTO valmDto = (ValmistavaKoulutusV1RDTO) dto;
            NimiV1RDTO koulutusohjelmaOrOsaamisala = getKoulutusohjelmaOrOsaamisala(komo, komoto, param);
            if (koulutusohjelmaOrOsaamisala != null) {
                valmDto.setKoulutusohjelma(koulutusohjelmaOrOsaamisala);
            }
            // TODO: aiheuttaa nyt "org.apache.cxf.interceptor.Fault: Koodi uri with version string object cannot be null"
            // valmKuntDto.setTutkintonimike(commonConverter.convertToKoodiDTO(komo.getTutkintonimikeUri(), komoto.getTutkintonimikeUri(), FieldNames.TUTKINTONIMIKE, NO, param));
            valmDto.setPohjakoulutusvaatimus(commonConverter.convertToKoodiDTO(komoto.getPohjakoulutusvaatimusUri(), NO_OVERRIDE_URI, FieldNames.POHJALKOULUTUSVAATIMUS, NO, param));
            valmDto.setLinkkiOpetussuunnitelmaan(getFirstUrlOrNull(komoto.getLinkkis()));
            valmDto.setKoulutuslaji(commonConverter.convertToKoodiDTO(getFirstUriOrNull(komoto.getKoulutuslajis()), NO_OVERRIDE_URI, FieldNames.KOULUTUSLAJI, NO, param));
            valmDto.setOpintojenLaajuusarvoKannassa(komoto.getOpintojenLaajuusArvo());

            if ( komoto.getNimi() != null ) {
                valmDto.setKoulutusohjelmanNimiKannassa(CommonRestConverters.toStringMap(komoto.getNimi()));
            }

            Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findParentKomo(komo);
            if (parentKomo == null) {
                parentKomo = komo;
            }

            mergeParentAndChildDataToRDTO(dto, parentKomo, komo, komoto, param);
        }

        else if (dto instanceof NayttotutkintoV1RDTO) {
            /**
             * Nayttotutkinto:
             * KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO,
             * AmmattitutkintoV1RDTO and ErikoisammattitutkintoV1RDTO
             */
            NayttotutkintoV1RDTO ammDto = (NayttotutkintoV1RDTO) dto;

            switch (komo.getModuuliTyyppi()) {
                case TUTKINTO_OHJELMA:
                    //primary 'tutkinto-ohjelma'
                    NimiV1RDTO ohjelma = commonConverter.convertToNimiDTO(komo.getOsaamisalaUri(), komoto.getOsaamisalaUri(), FieldNames.OSAAMISALA, YES, param);

                    //secondary or old type of 'tutkinto-ohjelma'
                    if (ohjelma == null || ohjelma.getUri() == null) {
                        //required osaamiala or koulutusohjelma data
                        ohjelma = commonConverter.convertToNimiDTO(komo.getKoulutusohjelmaUri(), komoto.getKoulutusohjelmaUri(), FieldNames.KOULUTUSOHJELMA, NO, param);
                    }

                    ammDto.setKoulutusohjelma(ohjelma);

                    break;
            }

            ammDto.setKoulutuslaji(commonConverter.convertToKoodiDTO(getFirstUriOrNull(komoto.getKoulutuslajis()), NO_OVERRIDE_URI, FieldNames.KOULUTUSLAJI, YES, param));

            if (!getTutkintonimikes(komoto, komo).isEmpty()
                    || !StringUtils.isBlank(komo.getTutkintonimikeUri())
                    || !StringUtils.isBlank(komoto.getTutkintonimikeUri())) {
                if (dto instanceof KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO
                        && !komoto.getTutkintonimikes().isEmpty()) {
                    ((KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO) dto)
                            .setTutkintonimikes(
                                    commonConverter.convertToKoodiUrisDTO(
                                            getTutkintonimikes(komoto, komo),
                                            FieldNames.TUTKINTONIMIKE,
                                            param
                                    )
                            );
                } else {
                    ammDto.setTutkintonimikes(commonConverter.convertToKoodiUrisDTO(getTutkintonimikes(komoto, komo),
                        FieldNames.TUTKINTONIMIKE, param));
                }
            }

            if (komoto.getNimi() != null && komoto.getNimi().getKaannoksetAsList() != null && !komoto.getNimi().getKaannoksetAsList().isEmpty()) {
                //tarkenne is suffix for the aiku education
                ammDto.setTarkenne(komoto.getNimi().getKaannoksetAsList().get(0).getArvo());
            }

            final Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findParentKomo(komo);
            //override parent komo data by the child komo data

            //at least in some cases, the education erikoisammattitutkinto do not have parent komo
            mergeParentAndChildDataToRDTO(dto, parentKomo != null ? parentKomo : komo, komo, komoto, param);

            if (komo.getModuuliTyyppi().equals(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA)) {
                //Prevent TAVOITTEET child komo desc overwrite by copying it to other map object named KOULUTUSOHJELMAN_TAVOITTEET.
                //At least used in amm & amm aiku education, not required in kk education.
                KuvausV1RDTO komoChildDesc = komoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komo.getTekstit(), param.getShowMeta());
                if (komoChildDesc != null && komoChildDesc.get(KomoTeksti.TAVOITTEET) != null) {
                    ammDto.getKuvausKomo().put(KomoTeksti.KOULUTUSOHJELMAN_TAVOITTEET, (NimiV1RDTO) komoChildDesc.get(KomoTeksti.TAVOITTEET));
                }
            }

            final NayttotutkintoV1RDTO nayttoDto = (NayttotutkintoV1RDTO) dto;

            if (!StringUtils.isBlank(komoto.getJarjesteja())) {
                nayttoDto.setJarjestavaOrganisaatio(commonConverter.searchOrganisaationNimi(komoto.getJarjesteja(), param.getLocale()));
            }

            if (komoto.getValmistavaKoulutus() != null) {
                nayttoDto.setValmistavaKoulutus(convertToValmistavaDto(komoto.getValmistavaKoulutus(), param));
            }

            if (komoto.getNimi() != null && komoto.getNimi().getTekstiKaannos() != null) {
                //only name in fi is needed.
                TekstiKaannos fi = komoto.getNimi().getTekstiKaannos().iterator().next();
                if (fi != null) {
                    nayttoDto.setTunniste(fi.getArvo()); //fi
                }
            }
        }

        dto.setAmmattinimikkeet(commonConverter.convertToKoodiUrisDTO(komoto.getAmmattinimikes(), FieldNames.AMMATTINIMIKKEET, param));

        if (komoto.getTarjoaja() != null) {
            dto.setOrganisaatio(commonConverter.searchOrganisaationNimi(komoto.getTarjoaja(), param.getLocale()));
        }

        if (komoto.getOpetuskielis() != null) {
            dto.setOpetuskielis(commonConverter.convertToKoodiUrisDTO(komoto.getOpetuskielis(), FieldNames.OPETUSKIELIS, param));
        }

        if (komoto.getOpetusmuotos() != null) {
            dto.setOpetusmuodos(commonConverter.convertToKoodiUrisDTO(komoto.getOpetusmuotos(), FieldNames.OPETUSMUODOS, param));
        }

        if (komoto.getOpetusAikas() != null) {
            dto.setOpetusAikas(commonConverter.convertToKoodiUrisDTO(komoto.getOpetusAikas(), FieldNames.OPETUSAIKAS, param));
        }

        if (komoto.getOpetusPaikkas() != null) {
            dto.setOpetusPaikkas(commonConverter.convertToKoodiUrisDTO(komoto.getOpetusPaikkas(), FieldNames.OPETUSPAIKKAS, param));
        }
        if (komoto.getSuunniteltukestoYksikkoUri() != null) {
            dto.setSuunniteltuKestoTyyppi(commonConverter.convertToKoodiDTO(komoto.getSuunniteltukestoYksikkoUri(), NO_OVERRIDE_URI, FieldNames.SUUNNITELTUKESTON_TYYPPI, YES, param));
        }

        if (komoto.getAihees() != null) {
            dto.setAihees(commonConverter.convertToKoodiUrisDTO(komoto.getAihees(), FieldNames.AIHEES, param));
        }

        dto.setHakijalleNaytettavaTunniste(komoto.getHakijalleNaytettavaTunniste());
        dto.setSuunniteltuKestoArvo(komoto.getSuunniteltukestoArvo());
        dto.setYhteyshenkilos(EntityUtils.copyYhteyshenkilos(komoto.getYhteyshenkilos()));
        dto.setVersion(komoto.getVersion());

        /**
         * Tutke 2 muutos: KJOH-951
         * - Päätettiin, että ei muuteta koodiarvoja tietokanta-ajona siihen sisältyvien riskien takia,
         * vaan sen sijaan hoidetaan converterissa koodiarvojen käsittely. Koodiarvojen kovakoodaaminen
         * tähän on tietysti ylläpidettävyyden kannalta huono ratkaisu, mutta tässä tilanteessa
         * päädyttiin tällaiseen kompromissiin.
         */
        switch (komoto.getToteutustyyppi()) {
            case AMMATILLINEN_PERUSTUTKINTO:
            case AMMATILLINEN_PERUSTUTKINTO_ALK_2018:
            case AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA:
            case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
                if (komoto.isSyksy2015OrLater()
                        && dto.getOpintojenLaajuusarvo() != null
                        && dto.getOpintojenLaajuusyksikko() != null
                        && dto.getOpintojenLaajuusarvo().getUri().equals("opintojenlaajuus_120")
                        && dto.getOpintojenLaajuusyksikko().getUri().equals("opintojenlaajuusyksikko_1")) {
                    dto.setOpintojenLaajuusarvo(
                            commonConverter.convertToKoodiDTO("opintojenlaajuus_180", null, FieldNames.OPINTOJEN_LAAJUUSARVO, YES, param)
                    );
                    dto.setOpintojenLaajuusyksikko(
                            commonConverter.convertToKoodiDTO("opintojenlaajuusyksikko_6", null, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, YES, param)
                    );
                }
                break;
        }

        //
        // KJOH-778 multiple owners, API output
        //
        for (KoulutusOwner owner : komoto.getOwners()) {
            switch (owner.getOwnerType()) {
                case KoulutusOwner.TARJOAJA:
                    dto.getOpetusTarjoajat().add(owner.getOwnerOid());
                    break;
                case KoulutusOwner.JARJESTAJA:
                    dto.getOpetusJarjestajat().add(owner.getOwnerOid());
                    break;
                default:
                    LOG.error("SKIPPING komoto oid: {}, invalid KoulutusOwner oid/type: {}/{}, accepted; TARJOAJA/JARJESTAJA",
                            komoto.getOid(), owner.getOwnerOid(), owner.getOwnerType());
                    break;
            }
        }

        // Always ensure that we have at least something in opetusTarjoajat (JavaScript depends on it)
        if (dto.getOpetusTarjoajat().isEmpty()) {
            dto.getOpetusTarjoajat().add(komoto.getTarjoaja());
        }

        return dto;
    }

    private void setSisaltyyKoulutuksiin(KoulutusV1RDTO dto, KoulutusmoduuliToteutus komoto) {
        Set<ToteutustyyppiEnum> toteutustyyppisWithSisaltyvyys = Sets.newHashSet(KORKEAKOULUOPINTO, KORKEAKOULUTUS);
        if (!toteutustyyppisWithSisaltyvyys.contains(komoto.getToteutustyyppi())) {
            return;
        }

        List<String> parents = koulutusSisaltyvyysDAO.getParents(komoto.getKoulutusmoduuli().getOid());
        Set<KoulutusIdentification> sisaltyyKoulutuksiin = new HashSet<KoulutusIdentification>();
        for (String parentKomoOid : parents) {
            KoulutusmoduuliToteutus parentKomoto = koulutusmoduuliToteutusDAO.findFirstByKomoOid(parentKomoOid);
            sisaltyyKoulutuksiin.add(new KoulutusIdentification(parentKomoto.getOid(), parentKomoto.getUniqueExternalId()));
        }
        dto.setSisaltyyKoulutuksiin(sisaltyyKoulutuksiin);
    }

    private NimiV1RDTO getKoulutusohjelmaOrOsaamisala(Koulutusmoduuli komo, KoulutusmoduuliToteutus komoto, RestParam param) {
        NimiV1RDTO nimiV1RDTO = null;

        if (komoto.isSyksy2015OrLater() && ( komo.getOsaamisalaUri() != null || komoto.getOsaamisalaUri() != null ) ) {
            nimiV1RDTO = commonConverter.convertToNimiDTO(
                komo.getOsaamisalaUri(), komoto.getOsaamisalaUri(), FieldNames.OSAAMISALA, YES, param
            );
            if (nimiV1RDTO != null && !nimiV1RDTO.getUri().isEmpty()) {
                return nimiV1RDTO;
            }
        }

        if (komo.getKoulutusohjelmaUri() != null || komoto.getKoulutusohjelmaUri() != null) {
            nimiV1RDTO = commonConverter.convertToNimiDTO(
                komo.getKoulutusohjelmaUri(), komoto.getKoulutusohjelmaUri(), FieldNames.KOULUTUSOHJELMA, YES, param
            );
            return nimiV1RDTO;
        }

        return null;
    }

    private Set<KoodistoUri> getTutkintonimikes(KoulutusmoduuliToteutus komoto, Koulutusmoduuli komo) {
        Set<KoodistoUri> tutkintonimikes = komoto.getTutkintonimikes();
        if (tutkintonimikes.isEmpty()) {
            tutkintonimikes = komo.getTutkintonimikes();
        }
        return tutkintonimikes;
    }

    /**
     * Common data for all 'koulutus' types, if the data has any kind of
     * 'koulutus' difference, do not add it here!
     */
    private void convertCommonToRDTO(TYPE dto, Koulutusmoduuli komo, KoulutusmoduuliToteutus komoto, final RestParam restParam) {
        Preconditions.checkNotNull(komo, "Koulutusmoduuli object cannot be null!");
        Preconditions.checkNotNull(komo.getKoulutustyyppiEnum(), "Koulutustyyppi enum cannot be null!");

        //FYI: Non-symmetrical data - the KOMO has string('|uri_1|uri_2|') collection of uris, put the KOMOTO has only single uri.
        dto.setKoulutustyyppi(commonConverter.convertToKoodiDTO(komoto.getToteutustyyppi() != null ? komoto.getToteutustyyppi().uri() : null, komoto.getKoulutustyyppiUri(), FieldNames.KOULUTUSTYYPPI, NO, restParam));

        if(!(dto instanceof TutkintoonJohtamatonKoulutusV1RDTO)) {
            // Not applicable for tjkk
            dto.setKoulutuskoodi(commonConverter.convertToKoodiDTO(komo.getKoulutusUri(), komoto.getKoulutusUri(), FieldNames.KOULUTUS, NO, restParam));
            dto.setTutkinto(commonConverter.convertToKoodiDTO(komo.getTutkintoUri(), komoto.getTutkintoUri(), FieldNames.TUTKINTO, YES, restParam));
            dto.setKoulutusaste(commonConverter.convertToKoodiDTO(komo.getKoulutusasteUri(), komoto.getKoulutusasteUri(), FieldNames.KOULUTUSASTE, YES, restParam));
            dto.setKoulutusala(commonConverter.convertToKoodiDTO(komo.getKoulutusalaUri(), komoto.getKoulutusalaUri(), FieldNames.KOULUTUSALA, NO, restParam));
            dto.setOpintoala(commonConverter.convertToKoodiDTO(komo.getOpintoalaUri(), komoto.getOpintoalaUri(), FieldNames.OPINTOALA, NO, restParam));
        }

        if (EB_RP_ISH.equals(komoto.getToteutustyyppi())) {
            dto.setOpintojenLaajuusarvo(commonConverter.convertToKoodiDTO(komoto.getOpintojenLaajuusarvoUri(), null, FieldNames.OPINTOJEN_LAAJUUSARVO, YES, restParam));
            dto.setOpintojenLaajuusyksikko(commonConverter.convertToKoodiDTO(komoto.getOpintojenLaajuusyksikkoUri(), null, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, YES, restParam));
        }
        else {
            dto.setOpintojenLaajuusarvo(commonConverter.convertToKoodiDTO(komo.getOpintojenLaajuusarvoUri(), komoto.getOpintojenLaajuusarvoUri(), FieldNames.OPINTOJEN_LAAJUUSARVO, YES, restParam));
            dto.setOpintojenLaajuusyksikko(commonConverter.convertToKoodiDTO(komo.getOpintojenLaajuusyksikkoUri(), komoto.getOpintojenLaajuusyksikkoUri(), FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, YES, restParam));
        }

        dto.setTunniste(komoto.getUlkoinenTunniste() != null ? komoto.getUlkoinenTunniste() : komo.getUlkoinenTunniste());

        KuvausV1RDTO<KomoTeksti> komoKuvaus = new KuvausV1RDTO<KomoTeksti>();

        komoKuvaus.putAll(komoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komo.getTekstit(), restParam.getShowMeta()));

        dto.setKuvausKomo(komoKuvaus);
    }

    /**
     * No hierachy, currenly only for korkeakoulutus.
     */
    private void convertFlatKomoToRDTO(TYPE dto, Koulutusmoduuli komo, KoulutusmoduuliToteutus komoto, final RestParam restParam) {
        Preconditions.checkNotNull(komo, "Koulutusmoduuli object cannot be null!");
        Preconditions.checkNotNull(komo.getKoulutustyyppiEnum(), "Koulutustyyppi enum cannot be null!");

        //1. return komoto uri
        //2. fallback : return parent (tutkinto) komo uri, when no komoto uri
        convertCommonToRDTO(dto, komo, komoto, restParam);

        //1. return komoto uri
        //2. fallback : return parent (tutkinto) komo uri, when no komoto uri
        dto.setEqf(commonConverter.convertToKoodiDTO(komo.getEqfUri(), komoto.getEqfUri(), FieldNames.EQF, YES, restParam));
        dto.setNqf(commonConverter.convertToKoodiDTO(komo.getNqfUri(), komoto.getNqfUri(), FieldNames.NQF, YES, restParam));
    }

    /**
     * Flatten parent and child hierarchy.
     */
    private void mergeParentAndChildDataToRDTO(TYPE dto, Koulutusmoduuli komoParent, Koulutusmoduuli komoChild, KoulutusmoduuliToteutus komoto, final RestParam restParam) {
        Preconditions.checkNotNull(komoParent, "Koulutusmoduuli parent object cannot be null!");
        Preconditions.checkNotNull(komoChild, "Koulutusmoduuli child object cannot be null!");
        Preconditions.checkNotNull(komoChild.getKoulutustyyppiEnum(), "Koulutustyyppi enum cannot be null!");

        //1. return komoto uri
        //2. fallback : return parent (tutkinto) komo uri, when no komoto uri
        convertCommonToRDTO(dto, komoParent, komoto, restParam);
        //1. return komoto uri
        //2. fallback : return child (tutkinto ohjelma) komo uri override
        //3. fallback : return parent (tutkinto) komo uri, when no child komo uri
        dto.setEqf(commonConverter.convertToKoodiDTO(childIfNotNull(komoParent.getEqfUri(), komoChild.getEqfUri()), komoto.getEqfUri(), FieldNames.EQF, YES, restParam));
        dto.setNqf(commonConverter.convertToKoodiDTO(childIfNotNull(komoParent.getNqfUri(), komoChild.getNqfUri()), komoto.getNqfUri(), FieldNames.NQF, YES, restParam));
    }

    private String getFirstUriOrNull(Set<KoodistoUri> uris) {
        if (uris != null && !uris.isEmpty()) {
            return uris.iterator().next().getKoodiUri();
        }
        return null;
    }

    private String getFirstUrlOrNull(Set<WebLinkki> uris) {
        if (uris != null && !uris.isEmpty()) {
            return uris.iterator().next().getUrl();
        }
        return null;
    }

    private String childIfNotNull(String uriParent, String uriChild) {
        return uriChild != null && !uriChild.isEmpty() ? uriChild : uriParent;
    }

    private ValmistavaV1RDTO convertToValmistavaDto(KoulutusmoduuliToteutus komoto, final RestParam param) {
        ValmistavaV1RDTO dto = new ValmistavaV1RDTO();

        if (komoto.getSuunniteltukestoYksikkoUri() != null) {
            dto.setSuunniteltuKestoTyyppi(commonConverter.convertToKoodiDTO(komoto.getSuunniteltukestoYksikkoUri(), NO_OVERRIDE_URI, FieldNames.SUUNNITELTUKESTON_TYYPPI, YES, param));
        }

        dto.setOpintojenMaksullisuus(komoto.getMaksullisuus());

        if (komoto.getHinta() != null) {
            dto.setHinta(komoto.getHinta().doubleValue());
        }

        dto.setHintaString(komoto.getHintaString());

        if (komoto.getOpetusmuotos() != null) {
            dto.setOpetusmuodos(commonConverter.convertToKoodiUrisDTO(komoto.getOpetusmuotos(), FieldNames.OPETUSMUODOS, param));
        }

        if (komoto.getOpetusAikas() != null) {
            dto.setOpetusAikas(commonConverter.convertToKoodiUrisDTO(komoto.getOpetusAikas(), FieldNames.OPETUSAIKAS, param));
        }

        if (komoto.getOpetusPaikkas() != null) {
            dto.setOpetusPaikkas(commonConverter.convertToKoodiUrisDTO(komoto.getOpetusPaikkas(), FieldNames.OPETUSPAIKKAS, param));
        }

        dto.setSuunniteltuKestoArvo(komoto.getSuunniteltukestoArvo());
        dto.setYhteyshenkilos(EntityUtils.copyYhteyshenkilos(komoto.getYhteyshenkilos()));
        dto.setLinkkiOpetussuunnitelmaan(getFirstUrlOrNull(komoto.getLinkkis()));

        KuvausV1RDTO<KomotoTeksti> komotoKuvaus = new KuvausV1RDTO<KomotoTeksti>();
        komotoKuvaus.putAll(komotoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit(), param.getShowMeta()));
        dto.setKuvaus(komotoKuvaus);

        return dto;
    }

    public Set<OppiaineV1RDTO> oppiaineetFromEntityToDto(Set<Oppiaine> oppiaineet) {
        Set<OppiaineV1RDTO> oppiaineetDto = new HashSet<OppiaineV1RDTO>();

        for (Oppiaine oppiaine : oppiaineet) {
            OppiaineV1RDTO dto = new OppiaineV1RDTO();
            dto.setOppiaine(oppiaine.getOppiaine());
            dto.setKieliKoodi(oppiaine.getKieliKoodi());
            oppiaineetDto.add(dto);
        }

        return oppiaineetDto;
    }
}
