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
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.model.RestParam;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.valmistava.ValmistavaV1RDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

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

    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConverters;
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKuvausConverters;
    @Autowired(required = true)
    private KoulutusCommonConverter commonConverter;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

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
        dto.setKomoOid(komo.getOid());
        dto.setTila(komoto.getTila());
        dto.setModified(komoto.getUpdated());
        dto.setModifiedBy(komoto.getLastUpdatedByOid());
        Preconditions.checkNotNull(komo.getModuuliTyyppi(), "Koulutusmoduuli object cannot be null!");
        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.fromValue(komo.getModuuliTyyppi().name()));

        dto.setKoulutuksenAlkamiskausi(commonConverter.convertToKoodiDTO(komoto.getAlkamiskausiUri(), NO_OVERRIDE_URI, FieldNames.ALKAMISKAUSI, YES, param));
        dto.setKoulutuksenAlkamisvuosi(komoto.getAlkamisVuosi());
        dto.getKoulutuksenAlkamisPvms().addAll(komoto.getKoulutuksenAlkamisPvms());

        KuvausV1RDTO<KomotoTeksti> komotoKuvaus = new KuvausV1RDTO<KomotoTeksti>();
        komotoKuvaus.putAll(komotoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit(), param.getShowMeta()));
        dto.setKuvausKomoto(komotoKuvaus);

        //KOMO
        if (dto instanceof KoulutusKorkeakouluV1RDTO) {
            /**
             * KORKEAKOULU
             */
            KoulutusKorkeakouluV1RDTO kkDto = (KoulutusKorkeakouluV1RDTO) dto;
            kkDto.setKomotoOid(komoto.getOid());

            final boolean useKomotoName = komoto.getNimi() != null && !komoto.getNimi().getTekstiKaannos().isEmpty(); //OVT-7531
            kkDto.setKoulutusohjelma(commonConverter.koulutusohjelmaUiMetaDTO(useKomotoName ? komoto.getNimi() : komo.getNimi(), FieldNames.KOULUTUSOHJELMA, param));

            if (komo.getKandidaatinKoulutusUri() != null || komoto.getKandidaatinKoulutusUri() != null) {
                /*
                 Without the null check response json will look like this:
                 "kandidaatinKoulutuskoodi" : {
                 "uri" : "",
                 "versio" : -1,
                 "arvo" : "",
                 "nimi" : ""
                 }
                 */
                kkDto.setKandidaatinKoulutuskoodi(commonConverter.convertToKoodiDTO(komo.getKandidaatinKoulutusUri(), komoto.getKandidaatinKoulutusUri(), FieldNames.KOULUTUSKOODI_KANDIDAATTI, YES, param));
            }

            if (komoto.getTutkintonimikes().isEmpty()) {
                kkDto.setTutkintonimikes(commonConverter.convertToKoodiUrisDTO(komo.getTutkintonimikes(), FieldNames.TUTKINTONIMIKE, param));
            } else {
                //huomaa: rinnakkainen komoton takia tutkintonimikeet haetaan komoto:lta
                kkDto.setTutkintonimikes(commonConverter.convertToKoodiUrisDTO(komoto.getTutkintonimikes(), FieldNames.TUTKINTONIMIKE, param));
            }

            if (komoto.getAihees() != null) {
                kkDto.setAihees(commonConverter.convertToKoodiUrisDTO(komoto.getAihees(), FieldNames.AIHEES, param));
            }

            final String maksullisuus = komoto.getMaksullisuus();
            kkDto.setOpintojenMaksullisuus(maksullisuus != null && Boolean.valueOf(maksullisuus));

            if (komoto.getHinta() != null) {
                kkDto.setHinta(komoto.getHinta().doubleValue());
            }

            kkDto.setPohjakoulutusvaatimukset(commonConverter.convertToKoodiUrisDTO(komoto.getKkPohjakoulutusvaatimus(), FieldNames.POHJALKOULUTUSVAATIMUS, param));
            convertFlatKomoToRDTO(dto, komo, komoto, param);

            //Map<String, BinaryData> findAllImagesByKomotoOid = koulutusmoduuliToteutusDAO.findAllImagesByKomotoOid(komotoOid);
            if (param.getShowImg() && komoto.getKuvat() != null && !komoto.getKuvat().isEmpty()) {
                for (Map.Entry<String, BinaryData> e : komoto.getKuvat().entrySet()) {
                    kkDto.getOpintojenRakenneKuvas().put(e.getKey(), new KuvaV1RDTO(e.getValue().getFilename(), e.getValue().getMimeType(), e.getKey(), Base64.encodeBase64String(e.getValue().getData())));
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

        else if (dto instanceof KoulutusAmmatillinenPerustutkintoV1RDTO) {
            /**
             * 2ASTE : AMMATILLINEN_PERUSTUTKINTO
             */
            KoulutusAmmatillinenPerustutkintoV1RDTO amisDto = (KoulutusAmmatillinenPerustutkintoV1RDTO) dto;
            amisDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getKoulutusohjelmaUri(), komoto.getKoulutusohjelmaUri(), FieldNames.KOULUTUSOHJELMA, NO, param));
            amisDto.setTutkintonimike(commonConverter.convertToKoodiDTO(komo.getTutkintonimikeUri(), komoto.getTutkintonimikeUri(), FieldNames.TUTKINTONIMIKE, NO, param));
            amisDto.setPohjakoulutusvaatimus(commonConverter.convertToKoodiDTO(komoto.getPohjakoulutusvaatimusUri(), NO_OVERRIDE_URI, FieldNames.POHJALKOULUTUSVAATIMUS, NO, param));
            amisDto.setLinkkiOpetussuunnitelmaan(getFirstUrlOrNull(komoto.getLinkkis()));
            amisDto.setKoulutuslaji(commonConverter.convertToKoodiDTO(getFirstUriOrNull(komoto.getKoulutuslajis()), NO_OVERRIDE_URI, FieldNames.KOULUTUSLAJI, NO, param));

            for (Map.Entry<KomoTeksti, MonikielinenTeksti> teksti : komo.getTekstit().entrySet()) {
                if (KomoTeksti.TAVOITTEET.equals(teksti.getKey())) {
                    amisDto.setKoulutuksenTavoitteet(CommonRestConverters.toStringMap(teksti.getValue()));
                    break;
                }
            }

            mergeParentAndChildDataToRDTO(dto, koulutusmoduuliDAO.findParentKomo(komo), komo, komoto, param);
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
            valmDto.setKoulutusohjelma(commonConverter.convertToNimiDTO(komo.getKoulutusohjelmaUri(), komoto.getKoulutusohjelmaUri(), FieldNames.KOULUTUSOHJELMA, NO, param));
            // TODO: aiheuttaa nyt "org.apache.cxf.interceptor.Fault: Koodi uri with version string object cannot be null"
            // valmKuntDto.setTutkintonimike(commonConverter.convertToKoodiDTO(komo.getTutkintonimikeUri(), komoto.getTutkintonimikeUri(), FieldNames.TUTKINTONIMIKE, NO, param));
            valmDto.setPohjakoulutusvaatimus(commonConverter.convertToKoodiDTO(komoto.getPohjakoulutusvaatimusUri(), NO_OVERRIDE_URI, FieldNames.POHJALKOULUTUSVAATIMUS, NO, param));
            valmDto.setLinkkiOpetussuunnitelmaan(getFirstUrlOrNull(komoto.getLinkkis()));
            valmDto.setKoulutuslaji(commonConverter.convertToKoodiDTO(getFirstUriOrNull(komoto.getKoulutuslajis()), NO_OVERRIDE_URI, FieldNames.KOULUTUSLAJI, NO, param));
            valmDto.setOpintojenLaajuusarvoKannassa(komoto.getOpintojenLaajuusArvo());

            if ( komoto.getNimi() != null ) {
                valmDto.setKoulutusohjelmanNimiKannassa(CommonRestConverters.toStringMap(komoto.getNimi()));
            }

            mergeParentAndChildDataToRDTO(dto, koulutusmoduuliDAO.findParentKomo(komo), komo, komoto, param);
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

            ammDto.setKoulutuslaji(commonConverter.convertToKoodiDTO(getFirstUriOrNull(komoto.getKoulutuslajis()), NO_OVERRIDE_URI, FieldNames.KOULUTUSLAJI, NO, param));
            ammDto.setTutkintonimike(commonConverter.convertToKoodiDTO(komo.getTutkintonimikeUri(), komoto.getTutkintonimikeUri(), FieldNames.TUTKINTONIMIKE, NO, param));

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
            //nayttoDto.setLinkkiOpetussuunnitelmaan(getFirstUrlOrNull(komoto.getLinkkis()));
            nayttoDto.setOpintojenMaksullisuus(komoto.getMaksullisuus() != null && Boolean.valueOf(komoto.getMaksullisuus()));

            if (komoto.getHinta() != null) {
                nayttoDto.setHinta(komoto.getHinta().doubleValue());
            }
            nayttoDto.setJarjestavaOrganisaatio(commonConverter.searchOrganisaationNimi(komoto.getJarjesteja(), param.getLocale()));

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

        dto.setSuunniteltuKestoArvo(komoto.getSuunniteltukestoArvo());
        EntityUtils.copyYhteyshenkilos(komoto.getYhteyshenkilos(), dto.getYhteyshenkilos());
        dto.setVersion(komoto.getVersion());

        //
        // KJOH-778 multiple owners, API output
        //
        for (KoulutusOwner owner : komoto.getOwners()) {
            if (KoulutusOwner.TARJOAJA.equalsIgnoreCase(owner.getOwnerType())) {
                dto.getOpetusTarjoajat().add(owner.getOwnerOid());
            } else if (KoulutusOwner.JARJESTAJA.equalsIgnoreCase(owner.getOwnerType())) {
                dto.getOpetusJarjestajat().add(owner.getOwnerOid());
            } else {
                LOG.error("SKIPPING komoto oid: {}, invalid KoulutusOwner oid/type: {}/{}, accepted; TARJOAJA/JARJESTAJA",
                        komoto.getOid(), owner.getOwnerOid(), owner.getOwnerType());
            }
        }

        return dto;
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
        dto.setKoulutuskoodi(commonConverter.convertToKoodiDTO(komo.getKoulutusUri(), komoto.getKoulutusUri(), FieldNames.KOULUTUS, NO, restParam));
        dto.setTutkinto(commonConverter.convertToKoodiDTO(komo.getTutkintoUri(), komoto.getTutkintoUri(), FieldNames.TUTKINTO, YES, restParam));
        dto.setOpintojenLaajuusarvo(commonConverter.convertToKoodiDTO(komo.getOpintojenLaajuusarvoUri(), komoto.getOpintojenLaajuusarvoUri(), FieldNames.OPINTOJEN_LAAJUUSARVO, YES, restParam));
        dto.setOpintojenLaajuusyksikko(commonConverter.convertToKoodiDTO(komo.getOpintojenLaajuusyksikkoUri(), komoto.getOpintojenLaajuusyksikkoUri(), FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, YES, restParam));
        dto.setKoulutusaste(commonConverter.convertToKoodiDTO(komo.getKoulutusasteUri(), komoto.getKoulutusasteUri(), FieldNames.KOULUTUSASTE, YES, restParam));
        dto.setKoulutusala(commonConverter.convertToKoodiDTO(komo.getKoulutusalaUri(), komoto.getKoulutusalaUri(), FieldNames.KOULUTUSALA, NO, restParam));
        dto.setOpintoala(commonConverter.convertToKoodiDTO(komo.getOpintoalaUri(), komoto.getOpintoalaUri(), FieldNames.OPINTOALA, NO, restParam));
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

        final String maksullisuus = komoto.getMaksullisuus();
        dto.setOpintojenMaksullisuus(maksullisuus != null && Boolean.valueOf(maksullisuus));

        if (komoto.getHinta() != null) {
            dto.setHinta(komoto.getHinta().doubleValue());
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

        dto.setSuunniteltuKestoArvo(komoto.getSuunniteltukestoArvo());
        EntityUtils.copyYhteyshenkilos(komoto.getYhteyshenkilos(), dto.getYhteyshenkilos());
        dto.setLinkkiOpetussuunnitelmaan(getFirstUrlOrNull(komoto.getLinkkis()));

        KuvausV1RDTO<KomotoTeksti> komotoKuvaus = new KuvausV1RDTO<KomotoTeksti>();
        komotoKuvaus.putAll(komotoKuvausConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit(), param.getShowMeta()));
        dto.setKuvaus(komotoKuvaus);

        return dto;
    }
}
