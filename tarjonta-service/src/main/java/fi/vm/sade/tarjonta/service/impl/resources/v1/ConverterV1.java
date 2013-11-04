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
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.impl.conversion.CommonToDTOConverter;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.CommonRestConverters;
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

    private static final Logger LOG = LoggerFactory.getLogger(ConverterV1.class);
    @Autowired
    HakuDAO hakuDao;
    @Autowired
    KoulutusmoduuliDAO komoDao;
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


    private HakuaikaV1RDTO convertHakuaikaToV1RDTO(Hakuaika hakuaika) {
        HakuaikaV1RDTO hakuaikaV1RDTO = new HakuaikaV1RDTO();

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
    // HAKUKOHDE
    // ----------------------------------------------------------------------

    /**
     * Convert domain Hakukohde to REST HakukohdeRDTO.
     *
     * @param hakukohde
     * @return
     */
    public HakukohdeV1RDTO toHakukohdeRDTO(Hakukohde hakukohde) {
        LOG.info("toHakukohdeRDTO({})", hakukohde);

        HakukohdeV1RDTO t = new HakukohdeV1RDTO();


        LOG.info("  -> result = {}", t);
        return t;
    }

    /**
     * Convert from REST HakukohdeRDTO to domain Hakukohde.
     *
     * @param hakukohde
     * @return
     */
    public Hakukohde toHakukohde(HakukohdeV1RDTO hakukohde) {
        LOG.info("toHakukohde({})", hakukohde);
        Hakukohde t = null;

        LOG.info("  -> result = {}", t);
        return t;
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

    private MonikielinenTeksti convertTekstiRDTOToMonikielinenTeksti(List<TekstiRDTO> tekstis) {
        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();

        for (TekstiRDTO tekstiRDTO:tekstis){
            monikielinenTeksti.addTekstiKaannos(tekstiRDTO.getUri(),tekstiRDTO.getTeksti());
            LOG.debug("MONIKIELINEN TEKSTI : {}", tekstiRDTO.getTeksti());
        }

        return monikielinenTeksti;
    }


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
