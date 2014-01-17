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
package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.impl.conversion.CommonToDTOConverter;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.CommonRestConverters;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;

import java.util.*;

import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * API V1 converters to/from model/domain.
 *
 * @author mlyly
 */
@Service
public class ConverterV1 {


    public static final String VALINTAPERUSTEKUVAUS_TYYPPI = "valintaperustekuvaus";
    public static final String SORA_TYYPPI = "SORA";
    private static final Logger LOG = LoggerFactory.getLogger(ConverterV1.class);
    @Autowired
    HakuDAO hakuDao;
    @Autowired
    KoulutusmoduuliDAO komoDao;
    @Autowired
    private OIDService oidService;
    @Autowired
    KoulutusmoduuliToteutusDAO komotoDao;
    @Autowired
    HakukohdeDAO hakukohdeDao;
    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    public HakuV1RDTO fromHakuToHakuRDTO(String oid) {
        return fromHakuToHakuRDTO(hakuDao.findByOid(oid),false);
    }

    public HakuV1RDTO fromHakuToHakuRDTO(Haku haku,boolean addHakukohdes) {
        if (haku == null) {
            return null;
        }

        HakuV1RDTO t = new HakuV1RDTO();

        t.setOid(haku.getOid());
        t.setModified(haku.getLastUpdateDate());
        t.setModifiedBy(haku.getLastUpdatedByOid());
        t.setCreated(null);
        t.setCreatedBy(null);
        t.setHakuaikas(convertHakuaikaListToV1RDTO(haku.getHakuaikas()));
        t.setHakukausiUri(haku.getHakukausiUri());
        t.setKoulutuksenAlkamiskausiUri(haku.getKoulutuksenAlkamiskausiUri());
        t.setKoulutuksenAlkamisVuosi(haku.getKoulutuksenAlkamisVuosi());
        t.setHakulomakeUri(haku.getHakulomakeUrl());
        t.setHakutapaUri(haku.getHakutapaUri());
        t.setHakutyyppiUri(haku.getHakutyyppiUri());
        t.setHaunTunniste(haku.getHaunTunniste());
        t.setKohdejoukkoUri(haku.getKohdejoukkoUri());
        t.setLastUpdatedByOid(haku.getLastUpdatedByOid());
        t.setLastUpdatedDate(haku.getLastUpdateDate());
        t.setTila(haku.getTila().name());
        t.setNimi(convertMonikielinenTekstiToTekstiDTOs(haku.getNimi() ));
        t.setHakukausiVuosi(haku.getHakukausiVuosi());

        if (addHakukohdes) {
            if (haku.getHakukohdes() != null) {
                for (Hakukohde hakukohde:haku.getHakukohdes()) {
                    t.getHakukohdeOids().add(hakukohde.getOid());
                }
            }
        }
//        t.set(haku.getHakukohdes());

        return t;
    }

    public Haku convertHakuV1DRDTOToHaku(HakuV1RDTO hakuV1RDTO) {
        Haku haku = new Haku();

        haku.setOid(hakuV1RDTO.getOid());
        haku.setHakukausiUri(hakuV1RDTO.getHakukausiUri());
        haku.setHakukausiVuosi(hakuV1RDTO.getHakukausiVuosi());
        haku.setHakulomakeUrl(hakuV1RDTO.getHakulomakeUri());
        haku.setHaunTunniste(hakuV1RDTO.getHaunTunniste());
        haku.setHakutyyppiUri(hakuV1RDTO.getHakutyyppiUri());
        haku.setHakutapaUri(hakuV1RDTO.getHakutapaUri());
        haku.setKohdejoukkoUri(hakuV1RDTO.getKohdejoukkoUri());
        haku.setKoulutuksenAlkamiskausiUri(hakuV1RDTO.getKoulutuksenAlkamiskausiUri());
        haku.setKoulutuksenAlkamisVuosi(hakuV1RDTO.getKoulutuksenAlkamisVuosi());
        haku.setKohdejoukkoUri(hakuV1RDTO.getKohdejoukkoUri());
        haku.setTila(TarjontaTila.valueOf(hakuV1RDTO.getTila()));
        haku.setNimi(convertTekstiRDTOToMonikielinenTeksti(hakuV1RDTO.getNimi()));
        if (hakuV1RDTO.getHakuaikas() != null ){
           for (HakuaikaV1RDTO hakuaikaRDTO: hakuV1RDTO.getHakuaikas()) {
               haku.addHakuaika(convertHakuaikaV1RDTOToHakuaika(hakuaikaRDTO));

           }
        }


        return haku;
    }


    private Hakuaika convertHakuaikaV1RDTOToHakuaika(HakuaikaV1RDTO hakuaikaV1RDTO) {

        Hakuaika hakuaika = new Hakuaika();

        hakuaika.setSisaisenHakuajanNimi(hakuaikaV1RDTO.getNimi());
        hakuaika.setAlkamisPvm(hakuaikaV1RDTO.getAlkuPvm());
        hakuaika.setPaattymisPvm(hakuaikaV1RDTO.getLoppuPvm());

        return hakuaika;

    }


    private HakuaikaV1RDTO convertHakuaikaToV1RDTO(Hakuaika hakuaika) {
        HakuaikaV1RDTO hakuaikaV1RDTO = new HakuaikaV1RDTO();
        hakuaikaV1RDTO.setHakuaikaId(hakuaika.getId().toString());
        hakuaikaV1RDTO.setAlkuPvm(hakuaika.getAlkamisPvm());
        hakuaikaV1RDTO.setLoppuPvm(hakuaika.getPaattymisPvm());
        hakuaikaV1RDTO.setNimi(hakuaika.getSisaisenHakuajanNimi());

        return hakuaikaV1RDTO;
    }

    private List<HakuaikaV1RDTO> convertHakuaikaListToV1RDTO(Set<Hakuaika> hakuaikas) {
        List<HakuaikaV1RDTO> hakuV1RDTOs = new ArrayList<HakuaikaV1RDTO>();

        if (hakuaikas != null) {

            for (Hakuaika hakuaika:hakuaikas) {

                hakuV1RDTOs.add(convertHakuaikaToV1RDTO(hakuaika));

            }

        }

        return hakuV1RDTOs;
    }


    // ----------------------------------------------------------------------
    // KUVAUS (esim. KK valintaperuste- tai SORA -kuvaus
    // ----------------------------------------------------------------------

    /**
     *  Convert domain ValintaperusteSoraKuvaus to KuvausV1RDTO
     *
     * @param kuvaus
     *
     * @return
     */

    public KuvausV1RDTO toKuvausRDTO(ValintaperusteSoraKuvaus kuvaus,boolean convertTeksti) {

        KuvausV1RDTO kuvausV1RDTO = new KuvausV1RDTO();

        if (kuvaus.getMonikielinenNimi() != null) {
            HashMap<String,String> nimet = new HashMap<String, String>();

            for (TekstiKaannos tekstiKaannos:kuvaus.getMonikielinenNimi().getKaannoksetAsList()) {

                nimet.put(tekstiKaannos.getKieliKoodi(),tekstiKaannos.getArvo());

            }
            kuvausV1RDTO.setKuvauksenNimet(nimet);
        }
        if (kuvaus.getKausi() != null) {
            kuvausV1RDTO.setKausi(kuvaus.getKausi());
        }

        if (kuvaus.getViimPaivittajaOid() != null) {
            kuvausV1RDTO.setModifiedBy(kuvaus.getViimPaivittajaOid());
        }

        if (kuvaus.getViimPaivitysPvm() != null) {
            kuvausV1RDTO.setModified(kuvaus.getViimPaivitysPvm());

        }

        if (kuvaus.getVuosi() != null) {
            kuvausV1RDTO.setVuosi(kuvaus.getVuosi());
        }

        kuvausV1RDTO.setKuvauksenTyyppi(getStringFromKuvausTyyppi(kuvaus.getTyyppi()));
        if (kuvaus.getId() != null) {
            kuvausV1RDTO.setKuvauksenTunniste(kuvaus.getId().toString());
        }

        kuvausV1RDTO.setOrganisaatioTyyppi(kuvaus.getOrganisaatioTyyppi());
        if (kuvaus.getTekstis() != null && convertTeksti) {
            HashMap<String,String> tekstis = new HashMap<String, String>();
            for(MonikielinenMetadata monikielinenMetadata:kuvaus.getTekstis()) {
                tekstis.put(monikielinenMetadata.getKieli(),monikielinenMetadata.getArvo());
            }
            kuvausV1RDTO.setKuvaukset(tekstis);
        }

        return kuvausV1RDTO;
    }

    public ValintaperusteSoraKuvaus toValintaperusteSoraKuvaus(KuvausV1RDTO kuvausV1RDTO) {

        ValintaperusteSoraKuvaus valintaperusteSoraKuvaus = new ValintaperusteSoraKuvaus();

        if (kuvausV1RDTO.getKuvauksenTunniste() != null) {
            valintaperusteSoraKuvaus.setId(new Long(kuvausV1RDTO.getKuvauksenTunniste()));
        }

        if (kuvausV1RDTO.getKuvauksenNimet() != null) {
            MonikielinenTeksti nimet = new MonikielinenTeksti();

            for (String kieli:kuvausV1RDTO.getKuvauksenNimet().keySet()) {
                nimet.addTekstiKaannos(kieli,kuvausV1RDTO.getKuvauksenNimet().get(kieli));
            }
            valintaperusteSoraKuvaus.setMonikielinenNimi(nimet);
        }

        if (kuvausV1RDTO.getModified() != null) {
            valintaperusteSoraKuvaus.setViimPaivittajaOid(kuvausV1RDTO.getModifiedBy());
        }

        if (kuvausV1RDTO.getModified() != null) {
            valintaperusteSoraKuvaus.setViimPaivitysPvm(kuvausV1RDTO.getModified());
        }

        if (kuvausV1RDTO.getVuosi() != null) {
            valintaperusteSoraKuvaus.setVuosi(kuvausV1RDTO.getVuosi());
        }
        if (kuvausV1RDTO.getKausi() != null) {
            valintaperusteSoraKuvaus.setKausi(kuvausV1RDTO.getKausi());
        }

        valintaperusteSoraKuvaus.setOrganisaatioTyyppi(kuvausV1RDTO.getOrganisaatioTyyppi());
        valintaperusteSoraKuvaus.setTyyppi(getTyyppiFromString(kuvausV1RDTO.getKuvauksenTyyppi()));
        if (kuvausV1RDTO.getKuvaukset() != null) {
            List<MonikielinenMetadata> tekstit = new ArrayList<MonikielinenMetadata>();
            for (String kieli:kuvausV1RDTO.getKuvaukset().keySet()) {
                MonikielinenMetadata teksti = new MonikielinenMetadata();
                teksti.setKieli(kieli);
                teksti.setKategoria(kuvausV1RDTO.getKuvauksenTyyppi());
                teksti.setArvo(kuvausV1RDTO.getKuvaukset().get(kieli));
                tekstit.add(teksti);
            }
            valintaperusteSoraKuvaus.setTekstis(tekstit);
        }

        return valintaperusteSoraKuvaus;
    }

    public static ValintaperusteSoraKuvaus.Tyyppi getTyyppiFromString(String tyyppi) {

        if (tyyppi.trim().equalsIgnoreCase(ConverterV1.VALINTAPERUSTEKUVAUS_TYYPPI)) {
            return ValintaperusteSoraKuvaus.Tyyppi.VALINTAPERUSTEKUVAUS;
        } else if (tyyppi.trim().equalsIgnoreCase(ConverterV1.SORA_TYYPPI)) {
            return ValintaperusteSoraKuvaus.Tyyppi.SORA;
        } else {
            return null;
        }

    }

    public static String getStringFromKuvausTyyppi(ValintaperusteSoraKuvaus.Tyyppi tyyppi) {

        switch (tyyppi) {

            case VALINTAPERUSTEKUVAUS:

                return VALINTAPERUSTEKUVAUS_TYYPPI;



            case SORA:

                return SORA_TYYPPI;


            default:

                return null;


        }

    }

    // ----------------------------------------------------------------------
    // HAKUKOHDE
    // ----------------------------------------------------------------------

    /**
     * Convert domain Hakukohde to REST HakukohdeRDTO.
     *
     * @param hakukohde
     * @return
     */
    public HakukohdeV1RDTO toHakukohdeRDTO(Hakukohde hakukohde) {
        HakukohdeV1RDTO hakukohdeRDTO = new HakukohdeV1RDTO();



        hakukohdeRDTO.setHakukohteenNimiUri(checkAndRemoveForEmbeddedVersionInUri(hakukohde.getHakukohdeNimi()));
        hakukohdeRDTO.setVersion(hakukohde.getVersion());
        hakukohdeRDTO.setOid(hakukohde.getOid());
        hakukohdeRDTO.setAloituspaikatLkm(hakukohde.getAloituspaikatLkm());

        for (KoulutusmoduuliToteutus komoto:hakukohde.getKoulutusmoduuliToteutuses()) {
            hakukohdeRDTO.getHakukohdeKoulutusOids().add(komoto.getOid());

            hakukohdeRDTO.getTarjoajaOids().add(komoto.getTarjoaja());


        }

        if (hakukohde.getHakukohdeMonikielinenNimi() != null) {
            hakukohdeRDTO.setHakukohteenNimet(convertMonikielinenTekstiToHashMap(hakukohde.getHakukohdeMonikielinenNimi()));
            //hakukohdeRDTO.setHakukohteenNimet(convertMonikielinenTekstiToHashMap(hakukohde.getHakukohdeMonikielinenNimi()));
        }

        if(hakukohde.getValintaPerusteKuvausKielet() != null) {
            hakukohdeRDTO.setValintaPerusteKuvausKielet(hakukohde.getValintaPerusteKuvausKielet());
        }

        if (hakukohde.getSoraKuvausKielet() != null) {
            hakukohdeRDTO.setSoraKuvausKielet(hakukohde.getSoraKuvausKielet());
        }

        if (hakukohde.getValintaPerusteKuvausTunniste() != null) {
            hakukohdeRDTO.setValintaPerusteKuvausTunniste(hakukohde.getValintaPerusteKuvausTunniste());
        }

        if (hakukohde.getSoraKuvausTunniste() != null) {
            hakukohdeRDTO.setSoraKuvausTunniste(hakukohde.getSoraKuvausTunniste());
        }

        if (hakukohde.getLastUpdateDate() != null) {
            hakukohdeRDTO.setModified(hakukohde.getLastUpdateDate());
        }

        if (hakukohde.getLastUpdatedByOid() != null) {
            hakukohdeRDTO.setModifiedBy(hakukohde.getLastUpdatedByOid());
        }

        if (hakukohde.getHakuaika() != null) {
            hakukohdeRDTO.setHakuaikaId(hakukohde.getHakuaika().getId().toString());
        }

        for (String hakukelpoisuusVaatimus:hakukohde.getHakukelpoisuusVaatimukset()) {
            hakukohdeRDTO.getHakukelpoisuusvaatimusUris().add(checkAndRemoveForEmbeddedVersionInUri(hakukelpoisuusVaatimus));
        }

        hakukohdeRDTO.setHakuOid(hakukohde.getHaku().getOid());
        if (hakukohde.getAlinHyvaksyttavaKeskiarvo() != null) {
            hakukohdeRDTO.setAlinHyvaksyttavaKeskiarvo(hakukohde.getAlinHyvaksyttavaKeskiarvo());
        }
        if (hakukohde.getAlinValintaPistemaara() != null) {
            hakukohdeRDTO.setAlinValintaPistemaara(hakukohde.getAlinValintaPistemaara());
        }

        if (hakukohde.getHakukelpoisuusVaatimusKuvaus() != null) {
            hakukohdeRDTO.setHakukelpoisuusVaatimusKuvaukset(convertMonikielinenTekstiToHashMap(hakukohde.getHakukelpoisuusVaatimusKuvaus()));
        }

        if (hakukohde.getValintojenAloituspaikatLkm() != null) {
            hakukohdeRDTO.setValintojenAloituspaikatLkm(hakukohde.getValintojenAloituspaikatLkm());
        }
        if (hakukohde.getYlinValintaPistemaara() != null) {
            hakukohdeRDTO.setYlinValintapistemaara(hakukohde.getYlinValintaPistemaara());
        }

        if (hakukohde.getHakuaikaAlkuPvm() != null) {
            hakukohdeRDTO.setHakuaikaAlkuPvm(hakukohde.getHakuaikaAlkuPvm());
        }

        if (hakukohde.getHakuaikaLoppuPvm() != null) {
            hakukohdeRDTO.setHakuaikaLoppuPvm(hakukohde.getHakuaikaLoppuPvm());
        }

        hakukohdeRDTO.setSahkoinenToimitusOsoite(hakukohde.getSahkoinenToimitusOsoite());
        hakukohdeRDTO.setSoraKuvausKoodiUri(checkAndRemoveForEmbeddedVersionInUri(hakukohde.getSoraKuvausKoodiUri()));
        hakukohdeRDTO.setTila(hakukohde.getTila().name());
        hakukohdeRDTO.setValintaperustekuvausKoodiUri(checkAndRemoveForEmbeddedVersionInUri(hakukohde.getValintaperustekuvausKoodiUri()));
        hakukohdeRDTO.setLiitteidenToimitusPvm(hakukohde.getLiitteidenToimitusPvm());
        hakukohdeRDTO.setLisatiedot(convertMonikielinenTekstiToHashMap(hakukohde.getLisatiedot()));
        if (hakukohde.getValintaperusteKuvaus() != null) {
            hakukohdeRDTO.setValintaperusteKuvaukset(convertMonikielinenTekstiToHashMap(hakukohde.getValintaperusteKuvaus()));
        }
        if (hakukohde.getSoraKuvaus() != null) {
            hakukohdeRDTO.setSoraKuvaukset(convertMonikielinenTekstiToHashMap(hakukohde.getSoraKuvaus()));
        }
        hakukohdeRDTO.setKaytetaanJarjestelmanValintaPalvelua(hakukohde.isKaytetaanJarjestelmanValintapalvelua());
        hakukohdeRDTO.setKaytetaanHaunPaattymisenAikaa(hakukohde.isKaytetaanHaunPaattymisenAikaa());
        hakukohdeRDTO.setLiitteidenToimitusOsoite(CommonToDTOConverter.convertOsoiteToOsoiteDTO(hakukohde.getLiitteidenToimitusOsoite()));
        LOG.debug("HAKUKOHDE LISATIEDOT : {} " , hakukohdeRDTO.getLisatiedot() != null ? hakukohdeRDTO.getLisatiedot().size() : "IS EMPTY" );


        if (hakukohde.getValintakoes() != null) {
            List<ValintakoeV1RDTO> valintakoeDtos = new ArrayList<ValintakoeV1RDTO>();
            for (Valintakoe valintakoe: hakukohde.getValintakoes()) {
               valintakoeDtos.add(convertValintakoeToValintakoeV1RDTO(valintakoe));
            }
            hakukohdeRDTO.setValintakokeet(valintakoeDtos);

        }

        if (hakukohde.getLiites() != null) {
            List<HakukohdeLiiteV1RDTO> liites = new ArrayList<HakukohdeLiiteV1RDTO>();
            for (HakukohdeLiite liite : hakukohde.getLiites()) {
               liites.add(convertLiiteToDto(liite));
            }
            hakukohdeRDTO.setHakukohteenLiitteet(liites);
        }

        return hakukohdeRDTO;
    }

    private HashMap<String,String> convertMonikielinenTekstiToHashMap(MonikielinenTeksti teksti) {
        HashMap<String,String> returnValue = new HashMap<String, String>();

        for (TekstiKaannos tekstiKaannos : teksti.getKaannoksetAsList()) {
            LOG.debug("TEKSTIKAANNOS KIELI : {} ARVO : {} ", tekstiKaannos.getKieliKoodi(),tekstiKaannos.getArvo());
            returnValue.put(tekstiKaannos.getKieliKoodi(),tekstiKaannos.getArvo());

        }

        return returnValue;
    }


    public Hakukohde toHakukohde(HakukohdeV1RDTO hakukohdeRDTO) {

        Hakukohde hakukohde = new Hakukohde();
        String newHakukohdeOid = null;
        LOG.debug("OIDSERVICE: {}", oidService);
        try {
            newHakukohdeOid = oidService.newOid(NodeClassCode.TEKN_5);
            LOG.debug("OID SERVICE NEW OID : {}", newHakukohdeOid);
        }  catch (ExceptionMessage emm) {
            LOG.warn("UNABLE TO GET OID : {}", emm.toString() );
        }

        if (hakukohdeRDTO.getOid() != null && hakukohdeRDTO.getOid().trim().length() > 0) {
            hakukohde.setOid(hakukohdeRDTO.getOid());
        } else {
            LOG.debug("NO OID FOUND ADDING NEW ONE : {}", newHakukohdeOid);
            hakukohde.setOid(newHakukohdeOid);
        }
        hakukohde.setAloituspaikatLkm(hakukohdeRDTO.getAloituspaikatLkm());
        hakukohde.setHakuaikaAlkuPvm(hakukohdeRDTO.getHakuaikaLoppuPvm());
        if (hakukohdeRDTO.getHakukohteenNimet() != null && hakukohdeRDTO.getHakukohteenNimet().size() > 0) {
            //hakukohde.setHakukohdeMonikielinenNimi(convertTekstiRDTOToMonikielinenTeksti(hakukohdeRDTO.getHakukohteenNimet()));
            hakukohde.setHakukohdeMonikielinenNimi(convertHashMapToMonikielinenTeksti(hakukohdeRDTO.getHakukohteenNimet()));
        }
        if (hakukohdeRDTO.getHakukohteenNimiUri() != null) {
            hakukohde.setHakukohdeNimi(hakukohdeRDTO.getHakukohteenNimiUri());
        }

        if (hakukohdeRDTO.getValintaPerusteKuvausTunniste() != null) {
            hakukohde.setValintaPerusteKuvausTunniste(hakukohdeRDTO.getValintaPerusteKuvausTunniste());
        }

        if (hakukohdeRDTO.getSoraKuvausTunniste() != null ) {
            hakukohde.setSoraKuvausTunniste(hakukohdeRDTO.getSoraKuvausTunniste());
        }

        if (hakukohdeRDTO.getModified() != null) {
            hakukohde.setLastUpdateDate(hakukohdeRDTO.getModified());
        }

        if (hakukohdeRDTO.getModifiedBy() != null) {
            hakukohde.setLastUpdatedByOid(hakukohdeRDTO.getModifiedBy());
        }

        if (hakukohdeRDTO.getValintaPerusteKuvausKielet() != null) {
            hakukohde.setValintaPerusteKuvausKielet(hakukohdeRDTO.getValintaPerusteKuvausKielet());
        }

        if (hakukohdeRDTO.getSoraKuvausKielet() != null) {
            hakukohde.setSoraKuvausKielet(hakukohdeRDTO.getSoraKuvausKielet());
        }

        hakukohde.setTila(TarjontaTila.valueOf(hakukohdeRDTO.getTila()));
        hakukohde.setLiitteidenToimitusPvm(hakukohdeRDTO.getLiitteidenToimitusPvm());
        hakukohde.setValintojenAloituspaikatLkm(hakukohdeRDTO.getValintojenAloituspaikatLkm());
        hakukohde.setSahkoinenToimitusOsoite(hakukohdeRDTO.getSahkoinenToimitusOsoite());
        hakukohde.setKaytetaanJarjestelmanValintapalvelua(hakukohdeRDTO.isKaytetaanJarjestelmanValintaPalvelua());
        hakukohde.setKaytetaanHaunPaattymisenAikaa(hakukohdeRDTO.isKaytetaanHaunPaattymisenAikaa());
        hakukohde.setSoraKuvausKoodiUri(hakukohdeRDTO.getSoraKuvausKoodiUri());
        hakukohde.setValintaperustekuvausKoodiUri(hakukohdeRDTO.getValintaperustekuvausKoodiUri());
        if (hakukohdeRDTO.getValintaperusteKuvaukset() != null) {
            hakukohde.setValintaperusteKuvaus(convertHashMapToMonikielinenTeksti(hakukohdeRDTO.getValintaperusteKuvaukset()));
        }
        if (hakukohdeRDTO.getSoraKuvaukset() != null) {
            hakukohde.setSoraKuvaus(convertHashMapToMonikielinenTeksti(hakukohdeRDTO.getSoraKuvaukset()));
        }
        hakukohde.setAlinHyvaksyttavaKeskiarvo(hakukohdeRDTO.getAlinHyvaksyttavaKeskiarvo());
        hakukohde.setAlinValintaPistemaara(hakukohdeRDTO.getAlinValintaPistemaara());
        hakukohde.setYlinValintaPistemaara(hakukohdeRDTO.getYlinValintapistemaara());

        if (hakukohdeRDTO.getLisatiedot() != null){
            hakukohde.setLisatiedot(convertStringHashMapToMonikielinenTeksti(hakukohdeRDTO.getLisatiedot()));
        }

        if (hakukohdeRDTO.getHakukelpoisuusvaatimusUris() != null) {
            for (String hakukelpoisuusVaatimus : hakukohdeRDTO.getHakukelpoisuusvaatimusUris()) {
                hakukohde.getHakukelpoisuusVaatimukset().add(hakukelpoisuusVaatimus);
            }
        }

        if (hakukohdeRDTO.getHakukelpoisuusVaatimusKuvaukset() != null) {
            hakukohde.setHakukelpoisuusVaatimusKuvaus(convertHashMapToMonikielinenTeksti(hakukohdeRDTO.getHakukelpoisuusVaatimusKuvaukset()));
        }

        hakukohde.setLiitteidenToimitusOsoite(CommonRestConverters.convertOsoiteRDTOToOsoite(hakukohdeRDTO.getLiitteidenToimitusOsoite()));


        if (hakukohdeRDTO.getValintakokeet() != null) {
            List<Valintakoe> valintakoeList = new ArrayList<Valintakoe>();
            for (ValintakoeV1RDTO valintakoeV1RDTO: hakukohdeRDTO.getValintakokeet()) {
                valintakoeList.add(convertValintakoeRDTOToValintakoe(valintakoeV1RDTO));
            }
            hakukohde.getValintakoes().addAll(valintakoeList);
        }

        return hakukohde;
    }

    public Valintakoe toValintakoe(ValintakoeV1RDTO valintakoeV1RDTO) {
        LOG.debug("toValintakoe({})",valintakoeV1RDTO);
        Valintakoe valintakoe = convertValintakoeRDTOToValintakoe(valintakoeV1RDTO);
        LOG.debug("toValintakoe result ->  {}", valintakoe);
        return valintakoe;
    }

    public ValintakoeV1RDTO fromValintakoe(Valintakoe valintakoe) {
        LOG.debug("fromValintakoe({})",valintakoe);
        ValintakoeV1RDTO valintakoeV1RDTO = convertValintakoeToValintakoeV1RDTO(valintakoe);
        LOG.debug("fromValintakoe result -> {}",valintakoeV1RDTO);
        return valintakoeV1RDTO;

    }

    public HakukohdeLiite toHakukohdeLiite(HakukohdeLiiteV1RDTO hakukohdeLiiteV1RDTO) {
        HakukohdeLiite hakukohdeLiite = new HakukohdeLiite();
        if (hakukohdeLiiteV1RDTO.getOid() != null) {
            hakukohdeLiite.setId(new Long(hakukohdeLiiteV1RDTO.getOid()));
        }

        hakukohdeLiite.setKieli(hakukohdeLiiteV1RDTO.getKieliUri());
        hakukohdeLiite.setHakukohdeLiiteNimi(hakukohdeLiiteV1RDTO.getLiitteenNimi());
        hakukohdeLiite.setSahkoinenToimitusosoite(hakukohdeLiiteV1RDTO.getSahkoinenToimitusOsoite());
        hakukohdeLiite.setErapaiva(hakukohdeLiiteV1RDTO.getToimitettavaMennessa());
        hakukohdeLiite.setToimitusosoite(CommonRestConverters.convertOsoiteRDTOToOsoite(hakukohdeLiiteV1RDTO.getLiitteenToimitusOsoite()));
        List<TekstiRDTO> tekstiRDTOs = new ArrayList<TekstiRDTO>();
        tekstiRDTOs.add(hakukohdeLiiteV1RDTO.getLiitteenKuvaus());
        hakukohdeLiite.setKuvaus(convertTekstiRDTOToMonikielinenTeksti(tekstiRDTOs));

        return hakukohdeLiite;
    }

    public HakukohdeLiiteV1RDTO fromHakukohdeLiite(HakukohdeLiite hakukohdeLiite) {
        HakukohdeLiiteV1RDTO hakukohdeLiiteV1RDTO = new HakukohdeLiiteV1RDTO();

        if (hakukohdeLiite.getId() != null) {
            hakukohdeLiiteV1RDTO.setOid(hakukohdeLiite.getId().toString());
        }
        if (hakukohdeLiite.getKieli() != null) {
            hakukohdeLiiteV1RDTO.setKieliUri(hakukohdeLiite.getKieli());
            KoodiType kieliKoodi = tarjontaKoodistoHelper.getKoodiByUri(hakukohdeLiite.getKieli());
            hakukohdeLiiteV1RDTO.setKieliNimi(getDefaultKoodinimi(kieliKoodi.getMetadata()));
        }


        hakukohdeLiiteV1RDTO.setLiitteenNimi(hakukohdeLiite.getHakukohdeLiiteNimi());
        hakukohdeLiiteV1RDTO.setToimitettavaMennessa(hakukohdeLiite.getErapaiva());
        hakukohdeLiiteV1RDTO.setSahkoinenToimitusOsoite(hakukohdeLiite.getSahkoinenToimitusosoite());
        hakukohdeLiiteV1RDTO.setLiitteenToimitusOsoite(CommonToDTOConverter.convertOsoiteToOsoiteDTO(hakukohdeLiite.getToimitusosoite()));
        List<TekstiRDTO> tekstiRDTOs = convertMonikielinenTekstiToTekstiDTOs(hakukohdeLiite.getKuvaus());
        if (tekstiRDTOs != null && tekstiRDTOs.size() > 0) {
            hakukohdeLiiteV1RDTO.setLiitteenKuvaus(tekstiRDTOs.get(0));
        }



        return hakukohdeLiiteV1RDTO;
    }

    //------------------------------------
    //Hakukohde helper converters
    //------------------------------------
    private Valintakoe convertValintakoeRDTOToValintakoe(ValintakoeV1RDTO valintakoeV1RDTO) {
        Valintakoe valintakoe = new Valintakoe();

        if (valintakoeV1RDTO.getOid() != null) {
            try {
                valintakoe.setId(new Long(valintakoeV1RDTO.getOid()));
            } catch (Exception exp) {

            }

        }

            valintakoe.setValintakoeNimi(valintakoeV1RDTO.getValintakoeNimi());
            valintakoe.setKieli(valintakoeV1RDTO.getKieliUri());
            List<TekstiRDTO> tekstiRDTOs = new ArrayList<TekstiRDTO>();
            tekstiRDTOs.add(valintakoeV1RDTO.getValintakokeenKuvaus());
            valintakoe.setKuvaus(convertTekstiRDTOToMonikielinenTeksti(tekstiRDTOs));
            if (valintakoeV1RDTO.getValintakoeAjankohtas() != null) {
                valintakoe.getAjankohtas().addAll(convertAjankohtaRDTOToValintakoeAjankohta(valintakoeV1RDTO.getValintakoeAjankohtas()));
            }



        return valintakoe;
    }


    private MonikielinenTeksti convertHashMapToMonikielinenTeksti(HashMap<String,String> nimet) {
        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();
        for(String key : nimet.keySet()) {
            TekstiKaannos tekstiKaannos = new TekstiKaannos(monikielinenTeksti,key,nimet.get(key));
            monikielinenTeksti.addTekstiKaannos(tekstiKaannos);
        }
        return monikielinenTeksti;
    }


    private Set<ValintakoeAjankohta> convertAjankohtaRDTOToValintakoeAjankohta(List<ValintakoeAjankohtaRDTO> valintakoeAjankohtaRDTOs) {
        Set<ValintakoeAjankohta> valintakoeAjankohtas = new HashSet<ValintakoeAjankohta>();

        for (ValintakoeAjankohtaRDTO valintakoeAjankohtaRDTO:valintakoeAjankohtaRDTOs) {
            ValintakoeAjankohta valintakoeAjankohta = new ValintakoeAjankohta();

            valintakoeAjankohta.setLisatietoja(valintakoeAjankohtaRDTO.getLisatiedot());
            valintakoeAjankohta.setAjankohdanOsoite(CommonRestConverters.convertOsoiteRDTOToOsoite(valintakoeAjankohtaRDTO.getOsoite()));
            valintakoeAjankohta.setAlkamisaika(valintakoeAjankohtaRDTO.getAlkaa());
            valintakoeAjankohta.setPaattymisaika(valintakoeAjankohtaRDTO.getLoppuu());
            valintakoeAjankohtas.add(valintakoeAjankohta);

        }

        return valintakoeAjankohtas;
    }

    public MonikielinenTeksti convertTekstiRDTOToMonikielinenTeksti(List<TekstiRDTO> tekstis) {
        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();

        for (TekstiRDTO tekstiRDTO:tekstis){
            monikielinenTeksti.addTekstiKaannos(tekstiRDTO.getUri(),tekstiRDTO.getTeksti());
            LOG.debug("MONIKIELINEN TEKSTI : {}", tekstiRDTO.getTeksti());
        }

        return monikielinenTeksti;
    }

    private HakukohdeLiiteV1RDTO convertLiiteToDto(HakukohdeLiite liite) {
        HakukohdeLiiteV1RDTO liiteDto = new HakukohdeLiiteV1RDTO();

        liiteDto.setLiitteenToimitusOsoite(convertOsoiteToDto(liite.getToimitusosoite()));
        liiteDto.setKieliUri(liite.getKieli());
        liiteDto.setLiitteenNimi(liite.getHakukohdeLiiteNimi());
        liiteDto.setToimitettavaMennessa(liite.getErapaiva());
        liiteDto.setLiitteenKuvaukset(convertMonikielinenTekstiToHashMap(liite.getKuvaus()));



        return liiteDto;
    }





    private OsoiteRDTO convertOsoiteToDto(Osoite osoite) {

        OsoiteRDTO osoiteRDTO = new OsoiteRDTO();

        osoiteRDTO.setOsoiterivi1(osoite.getOsoiterivi1());
        osoiteRDTO.setOsoiterivi2(osoite.getOsoiterivi2());
        osoiteRDTO.setPostinumero(osoite.getPostinumero());
        if (osoite.getPostinumero() != null) {
            KoodiType postinumeroKoodi = tarjontaKoodistoHelper.getKoodiByUri(osoite.getPostinumero());
            osoiteRDTO.setPostinumeroArvo(postinumeroKoodi != null ? postinumeroKoodi.getKoodiArvo() : null);
        }

        osoiteRDTO.setPostitoimipaikka(osoite.getPostitoimipaikka());

        return osoiteRDTO;

    };


    private ValintakoeV1RDTO convertValintakoeToValintakoeV1RDTO(Valintakoe valintakoe) {
        ValintakoeV1RDTO valintakoeV1RDTO = new ValintakoeV1RDTO();
        valintakoeV1RDTO.setOid(valintakoe.getId().toString());
        valintakoeV1RDTO.setKieliUri(valintakoe.getKieli());
        valintakoeV1RDTO.setValintakoeNimi(valintakoe.getValintakoeNimi());
        List<TekstiRDTO> lisatiedot = convertMonikielinenTekstiToTekstiDTOs(valintakoe.getKuvaus());
        if (lisatiedot != null && lisatiedot.size() > 0) {
            valintakoeV1RDTO.setValintakokeenKuvaus(lisatiedot.get(0));
        }

        if (valintakoeV1RDTO.getKieliUri() != null && tarjontaKoodistoHelper != null) {
            if (valintakoeV1RDTO.getKieliUri() != null) {
                KoodiType koodiType = tarjontaKoodistoHelper.getKoodiByUri(valintakoeV1RDTO.getKieliUri());

                valintakoeV1RDTO.setKieliNimi(getDefaultKoodinimi(koodiType.getMetadata()));
            }
        }

        if (valintakoe.getAjankohtas() != null) {
            for (ValintakoeAjankohta valintakoeAjankohta : valintakoe.getAjankohtas()) {
                valintakoeV1RDTO.getValintakoeAjankohtas().add(convertValintakoeAjankohtaToValintakoeAjankohtaRDTO(valintakoeAjankohta));
            }
        }

        return valintakoeV1RDTO;
    }

    private String getDefaultKoodinimi(List<KoodiMetadataType> koodiMetadataTypes) {
        //TODO: add some logic to determine which language should be shown
          String koodiNimi = null;
          for (KoodiMetadataType koodiMetadataType : koodiMetadataTypes) {
              if (koodiMetadataType.getKieli().equals(KieliType.FI))  {
                  koodiNimi = koodiMetadataType.getNimi();
              }
          }
        return koodiNimi;
    }


    private ValintakoeAjankohtaRDTO convertValintakoeAjankohtaToValintakoeAjankohtaRDTO(ValintakoeAjankohta valintakoeAjankohta) {

        ValintakoeAjankohtaRDTO valintakoeAjankohtaRDTO = new ValintakoeAjankohtaRDTO();
        if (valintakoeAjankohta.getId() != null) {
            valintakoeAjankohtaRDTO.setOid(valintakoeAjankohta.getId().toString());
        }

        valintakoeAjankohtaRDTO.setAlkaa(valintakoeAjankohta.getAlkamisaika());
        valintakoeAjankohtaRDTO.setLoppuu(valintakoeAjankohta.getPaattymisaika());
        valintakoeAjankohtaRDTO.setLisatiedot(valintakoeAjankohta.getLisatietoja());
        valintakoeAjankohtaRDTO.setOsoite(CommonToDTOConverter.convertOsoiteToOsoiteDTO(valintakoeAjankohta.getAjankohdanOsoite()));
        if (valintakoeAjankohtaRDTO.getOsoite() != null && valintakoeAjankohtaRDTO.getOsoite().getPostinumero() != null && tarjontaKoodistoHelper != null) {
            KoodiType postinumeroKoodi = tarjontaKoodistoHelper.getKoodiByUri(valintakoeAjankohtaRDTO.getOsoite().getPostinumero());
            valintakoeAjankohtaRDTO.getOsoite().setPostinumeroArvo(postinumeroKoodi.getKoodiArvo());
        }


        return valintakoeAjankohtaRDTO;

    }

    public MonikielinenTeksti convertStringHashMapToMonikielinenTeksti(HashMap<String,String> tekstit) {
        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();

        for (String key : tekstit.keySet()) {
            monikielinenTeksti.addTekstiKaannos(key,tekstit.get(key));
        }


        return monikielinenTeksti;
    }

    public HashMap<String,String> convertMonikielinenTekstiToStringHashMap(MonikielinenTeksti monikielinenTeksti) {

        HashMap<String,String> resultMap = new HashMap<String,String>();

        for (TekstiKaannos tekstiKaannos:monikielinenTeksti.getKaannoksetAsList()) {
            resultMap.put(tekstiKaannos.getKieliKoodi(),tekstiKaannos.getArvo());
        }

        return resultMap;

    }

    public List<TekstiRDTO> convertSimpleMonikielinenTekstiDTO(MonikielinenTeksti monikielinenTeksti) {
        if (monikielinenTeksti != null) {
            List<TekstiRDTO> tekstis = new ArrayList<TekstiRDTO>();

            for (TekstiKaannos tekstiKaannos : monikielinenTeksti.getKaannoksetAsList())  {
                TekstiRDTO tekstiRDTO = new TekstiRDTO();
                tekstiRDTO.addKieliAndNimi(tekstiKaannos.getKieliKoodi(),tekstiKaannos.getArvo());
                tekstis.add(tekstiRDTO);

            }

            return tekstis;
        } else {
            return null;
        }
    }

    private List<TekstiRDTO> convertMonikielinenTekstiToTekstiDTOs(MonikielinenTeksti monikielinenTeksti) {

        if (monikielinenTeksti != null) {
            List<TekstiRDTO> tekstiRDTOs = new ArrayList<TekstiRDTO>();

            for(TekstiKaannos tekstiKaannos:monikielinenTeksti.getTekstis()) {
                TekstiRDTO tekstiRDTO = new TekstiRDTO();
                tekstiRDTO.setUri(checkAndRemoveForEmbeddedVersionInUri(tekstiKaannos.getKieliKoodi()));
                tekstiRDTO.setTeksti(tekstiKaannos.getArvo());
                try {
                    KoodiType koodiType = tarjontaKoodistoHelper.getKoodiByUri(tekstiKaannos.getKieliKoodi());
                    //TODO: should it return nimi instead ? But with what language ?
                    tekstiRDTO.setArvo(koodiType.getKoodiArvo());
                    tekstiRDTO.setVersio(koodiType.getVersio());
                    if (koodiType.getMetadata() != null) {
                        for (KoodiMetadataType meta:koodiType.getMetadata()) {
                            //By default set default name finnish
                            if (meta.getKieli().equals(KieliType.FI)) {
                                tekstiRDTO.setNimi(meta.getNimi());
                            }
                            tekstiRDTO.addKieliAndNimi(meta.getKieli().value(),meta.getNimi());
                        }
                    }


                } catch (Exception exp) {

                }
                tekstiRDTOs.add(tekstiRDTO);

            }

            return tekstiRDTOs;
        }  else {
            return null;
        }

    }

    private String checkAndRemoveForEmbeddedVersionInUri(String uri) {
        if (uri != null) {
            if (uri.contains("#")) {
                StringTokenizer st = new StringTokenizer(uri,"#");
                return st.nextToken();
            } else {
                return uri;
            }
        } else {
            return null;
        }
    }


    // ----------------------------------------------------------------------
    // KOULUTUS
    // ----------------------------------------------------------------------

    public KoulutusV1RDTO fromKomotoToKoulutusRDTO(KoulutusmoduuliToteutus komoto) {
        LOG.warn("fromKomotoToKoulutusRDTO({}) -- ONLY PARTIALLY IMPLEMENTED!", komoto);

        // TODO implement me!

        KoulutusV1RDTO t = null;

        if (komoto != null) {
            // TODO TYYPPI!?
            KoulutusKorkeakouluV1RDTO k = new KoulutusKorkeakouluV1RDTO();

            k.setCreated(komoto.getUpdated());
            k.setCreatedBy(komoto.getLastUpdatedByOid());
            k.setModified(komoto.getUpdated());
            k.setModifiedBy(komoto.getLastUpdatedByOid());

            Koulutusmoduuli komo = komoto.getKoulutusmoduuli();

            k.setKomotoOid(komoto.getOid());
            k.setKomoOid(komo.getOid());


            t = k;
        }

        return t;
    }

    public KoulutusV1RDTO fromKomotoToKoulutusRDTO(String oid) {
        return fromKomotoToKoulutusRDTO(komotoDao.findByOid(oid));
    }

    
    public HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO> fromHakukohteetVastaus(HakukohteetVastaus source) {
        HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO> ret = new HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>();

        Map<String, TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO>> tarjoajat = new HashMap<String, TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO>>();

        for (HakukohdePerustieto ht : source.getHakukohteet()) {
            TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO> rets = getTarjoaja(
                    ret, tarjoajat, ht);
            rets.getTulokset().add(convert(ht));
        }

        // XX use hitCount when implemented
        ret.setTuloksia(source.getHakukohteet().size());

        return ret;
    }

    private HakukohdeHakutulosV1RDTO convert(HakukohdePerustieto ht) {
        HakukohdeHakutulosV1RDTO ret = new HakukohdeHakutulosV1RDTO();

        ret.setOid(ht.getOid());
        ret.setNimi(ht.getNimi());
        ret.setKausi(ht.getKoulutuksenAlkamiskausi() == null ? null : ht
                .getKoulutuksenAlkamiskausi().getNimi());
        ret.setVuosi(ht.getKoulutuksenAlkamisvuosi());
        ret.setHakutapa(ht.getHakutapaNimi());
        ret.setAloituspaikat(Integer.valueOf(ht.getAloituspaikat()));
        ret.setKoulutuslaji(ht.getKoulutuslaji() == null ? null : ht
                .getKoulutuslaji().getNimi());
        ret.setTila(TarjontaTila.valueOf(ht.getTila()));

        return ret;
    }

    private TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO> getTarjoaja(
            HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO> tulos,
            Map<String, TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO>> tarjoajat,
            HakukohdePerustieto ht) {
        TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO> ret = tarjoajat
                .get(ht.getTarjoajaOid());
        if (ret == null) {
            ret = new TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO>();
            tarjoajat.put(ht.getTarjoajaOid(), ret);
            ret.setOid(ht.getTarjoajaOid());
            ret.setNimi(ht.getTarjoajaNimi());
            tulos.getTulokset().add(ret);
        }
        return ret;
    }
    
    public HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> fromKoulutuksetVastaus(KoulutuksetVastaus source) {
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> ret = new HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>();

        Map<String, TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO>> tarjoajat = new HashMap<String, TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO>>();

        for (KoulutusPerustieto ht : source.getKoulutukset()) {
            TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> rets = getTarjoaja(
                    ret, tarjoajat, ht);
            rets.getTulokset().add(convert(ht));
        }

        ret.setTuloksia(source.getHitCount());

        return ret;
    }

    private KoulutusHakutulosV1RDTO convert(KoulutusPerustieto ht) {
        KoulutusHakutulosV1RDTO ret = new KoulutusHakutulosV1RDTO();

        ret.setOid(ht.getKomotoOid());
        ret.setKomoOid(ht.getKoulutusmoduuli());
        ret.setNimi(ht.getNimi());
        ret.setKausi(ht.getKoulutuksenAlkamiskausi() == null ? null : ht
                .getKoulutuksenAlkamiskausi().getNimi());
        ret.setVuosi(ht.getKoulutuksenAlkamisVuosi());
        if (ht.getPohjakoulutusvaatimus() != null) {
            ret.setPohjakoulutusvaatimus(ht.getPohjakoulutusvaatimus()
                    .getNimi());
        }
        if (ht.getKoulutuslaji() != null) {
            ret.setKoulutuslaji(ht.getKoulutuslaji().getNimi());
        }
        ret.setTila(TarjontaTila.valueOf(ht.getTila()));
        ret.setKoulutusasteTyyppi(ht.getKoulutustyyppi());
        ret.setKoulutuskoodi(ht.getKoulutuskoodi().getUri());

        return ret;
    }

    private TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> getTarjoaja(
            HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> tulos,
            Map<String, TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO>> tarjoajat,
            KoulutusPerustieto ht) {
        TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> ret = tarjoajat.get(ht
                .getTarjoaja().getOid());
        if (ret == null) {
            ret = new TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO>();
            tarjoajat.put(ht.getTarjoaja().getOid(), ret);
            ret.setOid(ht.getTarjoaja().getOid());
            ret.setNimi(ht.getTarjoaja().getNimi());
            tulos.getTulokset().add(ret);
        }
        return ret;
    }



}
