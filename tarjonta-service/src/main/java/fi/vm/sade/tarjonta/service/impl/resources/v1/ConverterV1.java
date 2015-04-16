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

import com.google.common.collect.Iterables;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.dao.*;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.impl.conversion.BaseRDTOConverter;
import fi.vm.sade.tarjonta.service.impl.conversion.CommonToDTOConverter;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.CommonRestConverters;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.ValintaperusteetUtil;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.ValintaperustekuvausHelper;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.search.*;
import fi.vm.sade.tarjonta.service.types.ValinnanPisterajaTyyppi;
import fi.vm.sade.tarjonta.shared.KoulutusasteResolver;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

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
    private OidService oidService;

    @Autowired
    KoulutusmoduuliToteutusDAO komotoDao;

    @Autowired
    HakukohdeDAO hakukohdeDao;

    @Autowired
    KuvausDAO kuvausDAO;

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @Autowired
    private ContextDataService contextDataService;

    @Autowired
    private ValintaperustekuvausHelper valintaperustekuvausHelper;

    @Value("${koodisto.hakutapa.jatkuvaHaku.uri}")
    private String _jatkuvaHakutapaUri;

    public static boolean isJatkuvaHaku(HakuV1RDTO haku, String jatkuvaHakutapaUriParam) {

        if (isEmpty(haku.getHakutapaUri())) {
            return false;
        }

        boolean result = (haku.getHakutapaUri().equals(jatkuvaHakutapaUriParam));

        return result;
    }

    public static boolean isEmpty(String s) {
        return (s == null || s.trim().isEmpty());
    }

    public HakuV1RDTO fromHakuToHakuRDTO(String oid) {
        return fromHakuToHakuRDTO(hakuDao.findByOid(oid), true);
    }

    public HakuV1RDTO fromHakuToHakuRDTO(Haku haku, boolean addHakukohdes) {
        return fromHakuToHakuRDTO(haku, addHakukohdes, null);
    }

    public HakuV1RDTO fromHakuToHakuRDTO(Haku haku, boolean addHakukohdes, List<String> hakukohteet) {
        if (haku == null) {
            return null;
        }

        HakuV1RDTO hakuDTO = new HakuV1RDTO();

        hakuDTO.setOid(haku.getOid());
        hakuDTO.setModified(haku.getLastUpdateDate());
        hakuDTO.setModifiedBy(haku.getLastUpdatedByOid());
        hakuDTO.setCreated(null);
        hakuDTO.setCreatedBy(null);
        hakuDTO.setHakuaikas(convertHakuaikaListToV1RDTO(haku.getHakuaikas()));
        hakuDTO.setHakukausiUri(haku.getHakukausiUri());
        if (haku.getKoulutuksenAlkamiskausiUri() != null) {
            hakuDTO.setKoulutuksenAlkamiskausiUri(haku.getKoulutuksenAlkamiskausiUri());
        }
        if (haku.getKoulutuksenAlkamisVuosi() != null) {
            hakuDTO.setKoulutuksenAlkamisVuosi(haku.getKoulutuksenAlkamisVuosi());
        }

        hakuDTO.setHakulomakeUri(haku.getHakulomakeUrl());
        hakuDTO.setHakutapaUri(haku.getHakutapaUri());
        hakuDTO.setHakutyyppiUri(haku.getHakutyyppiUri());
        hakuDTO.setHaunTunniste(haku.getHaunTunniste());
        hakuDTO.setKohdejoukkoUri(haku.getKohdejoukkoUri());
        hakuDTO.setTila(haku.getTila().name());
        hakuDTO.setHakukausiVuosi(haku.getHakukausiVuosi());
        hakuDTO.setMaxHakukohdes(haku.getMaxHakukohdes());

        // Assumes translation key is Koodisto kieli uri (has kieli_ prefix)!
        hakuDTO.setNimi(convertMonikielinenTekstiToMap(haku.getNimi(), true));

        if (addHakukohdes) {
            if (hakukohteet != null) {
                hakuDTO.setHakukohdeOids(hakukohteet);
            } else {
                List<String> tmp = hakukohdeDao.findByHakuOid(hakuDTO.getOid(), null, 0, 0, null, null);
                hakuDTO.setHakukohdeOids(tmp);
            }
        }

        hakuDTO.setOrganisaatioOids(haku.getOrganisationOids());
        hakuDTO.setTarjoajaOids(haku.getTarjoajaOids());

        hakuDTO.setUsePriority(haku.isUsePriority());
        hakuDTO.setSijoittelu(haku.isSijoittelu());
        hakuDTO.setJarjestelmanHakulomake(haku.isJarjestelmanHakulomake());

        for (Haku sisaltyvaHaku : haku.getSisaltyvatHaut()) {
            hakuDTO.getSisaltyvatHaut().add(sisaltyvaHaku.getOid());
        }

        if (haku.getParentHaku() != null) {
            hakuDTO.setParentHakuOid(haku.getParentHaku().getOid());
        }

        if (haku.getKoulutusmoduuliTyyppi() != null) {
            hakuDTO.setKoulutusmoduuliTyyppi(
                    fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.fromValue(haku.getKoulutusmoduuliTyyppi().name()));
        }

        return hakuDTO;
    }

    public Haku convertHakuV1DRDTOToHaku(HakuV1RDTO hakuV1RDTO, Haku haku) throws OIDCreationException {
        if (hakuV1RDTO == null) {
            return null;
        }

        if (haku == null) {
            haku = new Haku();

            if (hakuV1RDTO.getOid() == null) {
                hakuV1RDTO.setOid(oidService.get(TarjontaOidType.HAKU));
            }
        }

        if (isJatkuvaHaku(hakuV1RDTO, _jatkuvaHakutapaUri)) {

            haku.setHakukausiUri(getKausiForForJatkuvaHakuAloitusPvm(getEarliestStartDate(getAloitusPvmsFromHakuaikas(hakuV1RDTO.getHakuaikas()))));
            haku.setHakukausiVuosi(getHakuvuosiForJatkuvaHakuAloitusPvm(getEarliestStartDate(getAloitusPvmsFromHakuaikas(hakuV1RDTO.getHakuaikas()))));
        } else {
            haku.setKoulutuksenAlkamiskausiUri(hakuV1RDTO.getKoulutuksenAlkamiskausiUri());
            haku.setKoulutuksenAlkamisVuosi(hakuV1RDTO.getKoulutuksenAlkamisVuosi());
            haku.setHakukausiUri(hakuV1RDTO.getHakukausiUri());
            haku.setHakukausiVuosi(hakuV1RDTO.getHakukausiVuosi());
        }

        haku.setLastUpdatedByOid(contextDataService.getCurrentUserOid());
        haku.setOid(hakuV1RDTO.getOid());
        haku.setHakulomakeUrl(hakuV1RDTO.getHakulomakeUri());
        haku.setHaunTunniste(hakuV1RDTO.getHaunTunniste());
        haku.setHakutyyppiUri(hakuV1RDTO.getHakutyyppiUri());
        haku.setHakutapaUri(hakuV1RDTO.getHakutapaUri());
        haku.setKohdejoukkoUri(hakuV1RDTO.getKohdejoukkoUri());

        haku.setKohdejoukkoUri(hakuV1RDTO.getKohdejoukkoUri());
        if (hakuV1RDTO.getTila() == null) {
            hakuV1RDTO.setTila(TarjontaTila.LUONNOS.name());
        }
        haku.setTila(TarjontaTila.valueOf(hakuV1RDTO.getTila()));
        haku.setNimi(convertMapToMonikielinenTeksti(hakuV1RDTO.getNimi()));
        haku.setMaxHakukohdes(hakuV1RDTO.getMaxHakukohdes());

        // Temporary list of hakuaikas to process
        ArrayList<Hakuaika> tmpHakuaikas = new ArrayList<Hakuaika>();
        tmpHakuaikas.addAll(haku.getHakuaikas());

        // Process UI hakuaikas.
        for (HakuaikaV1RDTO hakuaikaDTO : hakuV1RDTO.getHakuaikas()) {
            LOG.debug("hakuaika: ", hakuaikaDTO);

            Hakuaika ha = haku.getHakuaikaById(hakuaikaDTO.getHakuaikaId());
            if (ha == null) {
                LOG.debug(" == new hakuaika");

                // NEW hakuaika
                ha = new Hakuaika();
                ha.setAlkamisPvm(hakuaikaDTO.getAlkuPvm());
                ha.setPaattymisPvm(hakuaikaDTO.getLoppuPvm());
                ha.setNimi(convertMapToMonikielinenTeksti(hakuaikaDTO.getNimet()));
                haku.addHakuaika(ha);
            } else {
                LOG.debug(" == old hakuaika TODO");

                ha.setAlkamisPvm(hakuaikaDTO.getAlkuPvm());
                ha.setPaattymisPvm(hakuaikaDTO.getLoppuPvm());
                ha.setNimi(convertMapToMonikielinenTeksti(hakuaikaDTO.getNimet()));

                // Remove update form the list to find out deleted hakuaikas
                tmpHakuaikas.remove(ha);
            }
        }

        // Remove hakuaikas that are deleted.
        for (Hakuaika hakuaika : tmpHakuaikas) {
            LOG.debug("DELETED hakuaika: ", hakuaika);
            haku.removeHakuaika(hakuaika);
        }

        haku.setOrganisationOids(hakuV1RDTO.getOrganisaatioOids());
        haku.setTarjoajaOids(hakuV1RDTO.getTarjoajaOids());

        haku.setUsePriority(hakuV1RDTO.isUsePriority());
        haku.setSijoittelu(hakuV1RDTO.isSijoittelu());
        haku.setJarjestelmanHakulomake(hakuV1RDTO.isJarjestelmanHakulomake());

        if (hakuV1RDTO.getKoulutusmoduuliTyyppi() != null) {
            haku.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.valueOf(hakuV1RDTO.getKoulutusmoduuliTyyppi().value()));
        } else {
            haku.setKoulutusmoduuliTyyppi(null);
        }

        if (hakuV1RDTO.getParentHakuOid() != null) {
            haku.setParentHaku(hakuDao.findByOid(hakuV1RDTO.getParentHakuOid()));
        } else {
            haku.setParentHaku(null);
        }

        return haku;
    }

    private List<Date> getAloitusPvmsFromHakuaikas(List<HakuaikaV1RDTO> hakuaikaV1RDTOs) {
        List<Date> aloitusDates = new ArrayList<Date>();

        for (HakuaikaV1RDTO ha : hakuaikaV1RDTOs) {
            aloitusDates.add(ha.getAlkuPvm());
        }

        return aloitusDates;
    }

    private Date getEarliestStartDate(List<Date> startDates) {

        Date aloitusPvm = null;

        for (Date ap : startDates) {
            if (aloitusPvm == null) {
                aloitusPvm = ap;
            }

            if (ap.before(aloitusPvm)) {
                aloitusPvm = ap;
            }

        }

        return aloitusPvm;
    }

    private int getHakuvuosiForJatkuvaHakuAloitusPvm(Date aloitusPvm) {

        return IndexDataUtils.parseYearInt(aloitusPvm);

    }

    private String getKausiForForJatkuvaHakuAloitusPvm(Date aloitusPvm) {
        return IndexDataUtils.parseKausiKoodi(aloitusPvm);
    }

    private HakuaikaV1RDTO convertHakuaikaToV1RDTO(Hakuaika hakuaika) {
        HakuaikaV1RDTO hakuaikaV1RDTO = new HakuaikaV1RDTO();
        hakuaikaV1RDTO.setHakuaikaId(hakuaika.getId().toString());
        hakuaikaV1RDTO.setAlkuPvm(hakuaika.getAlkamisPvm());
        hakuaikaV1RDTO.setLoppuPvm(hakuaika.getPaattymisPvm());
        hakuaikaV1RDTO.setNimet(convertMonikielinenTekstiToMap(hakuaika.getNimi(), false));
        return hakuaikaV1RDTO;
    }

    private List<HakuaikaV1RDTO> convertHakuaikaListToV1RDTO(Set<Hakuaika> hakuaikas) {
        List<HakuaikaV1RDTO> hakuV1RDTOs = new ArrayList<HakuaikaV1RDTO>();

        if (hakuaikas != null) {
            for (Hakuaika hakuaika : hakuaikas) {
                hakuV1RDTOs.add(convertHakuaikaToV1RDTO(hakuaika));
            }
        }
        return hakuV1RDTOs;
    }

    // ----------------------------------------------------------------------
    // KUVAUS (esim. KK valintaperuste- tai SORA -kuvaus
    // ----------------------------------------------------------------------

    /**
     * Convert domain ValintaperusteSoraKuvaus to KuvausV1RDTO
     *
     * @param kuvaus
     * @return
     */
    public KuvausV1RDTO toKuvausRDTO(ValintaperusteSoraKuvaus kuvaus, boolean convertTeksti) {

        KuvausV1RDTO kuvausV1RDTO = new KuvausV1RDTO();

//        if (kuvaus.getMonikielinenNimi() != null) {
//            HashMap<String,String> nimet = new HashMap<String, String>();
//
//            for (TekstiKaannos tekstiKaannos:kuvaus.getMonikielinenNimi().getKaannoksetAsList()) {
//
//                nimet.put(tekstiKaannos.getKieliKoodi(),tekstiKaannos.getArvo());
//
//            }
//            kuvausV1RDTO.setKuvauksenNimet(nimet);
//        }
        kuvausV1RDTO.setKuvauksenNimet(convertMonikielinenTekstiToMap(kuvaus.getMonikielinenNimi(), false));

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
            HashMap<String, String> tekstis = new HashMap<String, String>();
            for (MonikielinenMetadata monikielinenMetadata : kuvaus.getTekstis()) {
                tekstis.put(monikielinenMetadata.getKieli(), monikielinenMetadata.getArvo());
            }
            kuvausV1RDTO.setKuvaukset(tekstis);
        }

        if (kuvaus.getAvain() != null) {
            kuvausV1RDTO.setAvain(kuvaus.getAvain());
        }

        return kuvausV1RDTO;
    }

    public ValintaperusteSoraKuvaus toValintaperusteSoraKuvaus(KuvausV1RDTO kuvausV1RDTO) {

        ValintaperusteSoraKuvaus valintaperusteSoraKuvaus = new ValintaperusteSoraKuvaus();

        if (kuvausV1RDTO.getKuvauksenTunniste() != null) {
            valintaperusteSoraKuvaus.setId(new Long(kuvausV1RDTO.getKuvauksenTunniste()));
        }

        if (kuvausV1RDTO.getKuvauksenNimet() != null) {
            valintaperusteSoraKuvaus.setMonikielinenNimi(convertMapToMonikielinenTeksti(kuvausV1RDTO.getKuvauksenNimet()));
        }

        valintaperusteSoraKuvaus.setViimPaivittajaOid(contextDataService.getCurrentUserOid());
        valintaperusteSoraKuvaus.setViimPaivitysPvm(new Date());

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
            for (String kieli : kuvausV1RDTO.getKuvaukset().keySet()) {
                MonikielinenMetadata teksti = new MonikielinenMetadata();
                teksti.setKieli(kieli);
                teksti.setKategoria(kuvausV1RDTO.getKuvauksenTyyppi());
                teksti.setArvo(kuvausV1RDTO.getKuvaukset().get(kieli));
                tekstit.add(teksti);
            }
            valintaperusteSoraKuvaus.setTekstis(tekstit);
        }

        valintaperusteSoraKuvaus.setAvain(kuvausV1RDTO.getAvain());

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

    public HakukohdeV1RDTO toHakukohdeRDTO(Hakukohde hakukohde) {
        HakukohdeV1RDTO hakukohdeRDTO = new HakukohdeV1RDTO();

        for (KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
            hakukohdeRDTO.getHakukohdeKoulutusOids().add(komoto.getOid());
            hakukohdeRDTO.getTarjoajaOids().add(komoto.getTarjoaja());
        }

        if (hakukohde.getSoraKuvausTunniste() != null) {
            hakukohdeRDTO.setSoraKuvaukset(getKuvauksetWithId(hakukohde.getSoraKuvausTunniste(), hakukohde.getSoraKuvausKielet()));
        }

        if (hakukohde.getValintaPerusteKuvausTunniste() != null) {
            hakukohdeRDTO.setValintaperusteKuvaukset(getKuvauksetWithId(hakukohde.getValintaPerusteKuvausTunniste(), hakukohde.getValintaPerusteKuvausKielet()));
        }

        if (hakukohde.getHakukohdeKoodistoNimi() != null) {
            hakukohdeRDTO.setHakukohteenNimi(hakukohde.getHakukohdeKoodistoNimi());
        }

        if (hakukohde.getAloituspaikatKuvaus() != null) {
            hakukohdeRDTO.setAloituspaikatKuvaukset(convertMonikielinenTekstiToMap(hakukohde.getAloituspaikatKuvaus(), false));
        }

        if (hakukohde.getValintaPerusteKuvausKielet() != null) {
            hakukohdeRDTO.setValintaPerusteKuvausKielet(hakukohde.getValintaPerusteKuvausKielet());
        }

        if (hakukohde.getSoraKuvausKielet() != null) {
            hakukohdeRDTO.setSoraKuvausKielet(hakukohde.getSoraKuvausKielet());
        }

        if (hakukohde.getHakuaika() != null) {
            hakukohdeRDTO.setHakuaikaId(hakukohde.getHakuaika().getId().toString());
        }

        for (String hakukelpoisuusVaatimus : hakukohde.getHakukelpoisuusVaatimukset()) {
            hakukohdeRDTO.getHakukelpoisuusvaatimusUris().add(checkAndRemoveForEmbeddedVersionInUri(hakukelpoisuusVaatimus));
        }

        if (hakukohde.getAlinHyvaksyttavaKeskiarvo() != null) {
            hakukohdeRDTO.setAlinHyvaksyttavaKeskiarvo(hakukohde.getAlinHyvaksyttavaKeskiarvo());
        }

        if (hakukohde.getAlinValintaPistemaara() != null) {
            hakukohdeRDTO.setAlinValintaPistemaara(hakukohde.getAlinValintaPistemaara());
        }

        if (hakukohde.getYlinValintaPistemaara() != null) {
            hakukohdeRDTO.setYlinValintapistemaara(hakukohde.getYlinValintaPistemaara());
        }

        hakukohdeRDTO.setHakukohteenNimiUri(hakukohde.getHakukohdeNimi());
        hakukohdeRDTO.setVersion(hakukohde.getVersion());
        hakukohdeRDTO.setOid(hakukohde.getOid());
        hakukohdeRDTO.setAloituspaikatLkm(hakukohde.getAloituspaikatLkm());
        hakukohdeRDTO.setKaksoisTutkinto(hakukohde.isKaksoisTutkinto());
        hakukohdeRDTO.setVersion(hakukohde.getVersion());
        hakukohdeRDTO.setValintaPerusteKuvausTunniste(hakukohde.getValintaPerusteKuvausTunniste());
        hakukohdeRDTO.setSoraKuvausTunniste(hakukohde.getSoraKuvausTunniste());
        hakukohdeRDTO.setModified(hakukohde.getLastUpdateDate());
        hakukohdeRDTO.setModifiedBy(hakukohde.getLastUpdatedByOid());
        hakukohdeRDTO.setUlkoinenTunniste(hakukohde.getUlkoinenTunniste());
        hakukohdeRDTO.setHakuOid(hakukohde.getHaku().getOid());
        hakukohdeRDTO.setValintojenAloituspaikatLkm(hakukohde.getValintojenAloituspaikatLkm());
        hakukohdeRDTO.setHakuaikaAlkuPvm(hakukohde.getHakuaikaAlkuPvm());
        hakukohdeRDTO.setHakuaikaLoppuPvm(hakukohde.getHakuaikaLoppuPvm());
        hakukohdeRDTO.setKaytetaanHakukohdekohtaistaHakuaikaa(hakukohde.hakuaikasSet());
        hakukohdeRDTO.setSahkoinenToimitusOsoite(hakukohde.getSahkoinenToimitusOsoite());
        hakukohdeRDTO.setSoraKuvausKoodiUri(checkAndRemoveForEmbeddedVersionInUri(hakukohde.getSoraKuvausKoodiUri()));
        hakukohdeRDTO.setTila(hakukohde.getTila().name());
        hakukohdeRDTO.setValintaperustekuvausKoodiUri(checkAndRemoveForEmbeddedVersionInUri(hakukohde.getValintaperustekuvausKoodiUri()));
        hakukohdeRDTO.setLiitteidenToimitusPvm(hakukohde.getLiitteidenToimitusPvm());
        hakukohdeRDTO.setLisatiedot(convertMonikielinenTekstiToMapWithoutVersions(hakukohde.getLisatiedot()));
        hakukohdeRDTO.setKaytetaanJarjestelmanValintaPalvelua(hakukohde.isKaytetaanJarjestelmanValintapalvelua());
        hakukohdeRDTO.setKaytetaanHaunPaattymisenAikaa(hakukohde.isKaytetaanHaunPaattymisenAikaa());

        hakukohdeRDTO.setHakuMenettelyKuvaukset(convertMonikielinenTekstiToMapWithoutVersions(hakukohde.getHakuMenettelyKuvaus()));
        hakukohdeRDTO.setPeruutusEhdotKuvaukset(convertMonikielinenTekstiToMapWithoutVersions(hakukohde.getPeruutusEhdotKuvaus()));

        convertTarjoatiedotToDTO(hakukohde, hakukohdeRDTO);
        convertHakukohteenNimetToDTO(hakukohde, hakukohdeRDTO, null);
        convertOpetuskieletToDTO(hakukohde, hakukohdeRDTO);
        convertHakukelpoisuusVaatimuksetToDTO(hakukohde, hakukohdeRDTO);
        convertSoraAndValintaperustekuvauksetToDTO(hakukohde, hakukohdeRDTO);
        convertLiitteidenToimitusosoiteToDTO(hakukohde, hakukohdeRDTO);
        convertYhteystiedotToDTO(hakukohde, hakukohdeRDTO);
        convertValintakokeetToDTO(hakukohde, hakukohdeRDTO);
        convertLiitteetToDTO(hakukohde, hakukohdeRDTO);
        convertTarjoajaNimetToDTO(hakukohdeRDTO);
        convertOrganisaatioRyhmaOidsToDTO(hakukohde, hakukohdeRDTO);
        convertPainotettavatOppianeetToDTO(hakukohde, hakukohdeRDTO);
        convertKoulutusmoduuliTyyppiToDTO(hakukohde, hakukohdeRDTO);
        convertRyhmaliitoksetToDTO(hakukohde, hakukohdeRDTO);

        return hakukohdeRDTO;
    }

    private void convertRyhmaliitoksetToDTO(Hakukohde hakukohde, HakukohdeV1RDTO hakukohdeRDTO) {
        for (Ryhmaliitos ryhmaliitos : hakukohde.getRyhmaliitokset()) {
            RyhmaliitosV1RDTO ryhmaliitosDTO = new RyhmaliitosV1RDTO();
            ryhmaliitosDTO.setRyhmaOid(ryhmaliitos.getRyhmaOid());
            ryhmaliitosDTO.setPrioriteetti(ryhmaliitos.getPrioriteetti());
            hakukohdeRDTO.getRyhmaliitokset().add(ryhmaliitosDTO);
        }
    }

    private void convertKoulutusmoduuliTyyppiToDTO(Hakukohde hakukohde, HakukohdeV1RDTO hakukohdeRDTO) {
        Set<String> koulutusmoduulityypit = new HashSet<String>();
        for (KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
            koulutusmoduulityypit.add(komoto.getKoulutusmoduuli().getModuuliTyyppi().name());
        }
        if (koulutusmoduulityypit.size() > 1) {
            throw new IllegalArgumentException(String.format(
                    "Eri tyyppiset koulutukset hakukohteella %s ei tuettu", hakukohde.getOid()));
        }
        hakukohdeRDTO.setKoulutusmoduuliTyyppi(Iterables.getFirst(koulutusmoduulityypit, null));
    }

    private void convertHakukohteenNimetToDTO(Hakukohde hakukohde, HakukohdeV1RDTO hakukohdeRDTO,
                                              HakukohdeValintaperusteetV1RDTO valintaperusteetV1RDTO) {
        if (hakukohde.getHakukohdeMonikielinenNimi() != null) {
            Map <String, String> nimet = convertMonikielinenTekstiToMap(hakukohde.getHakukohdeMonikielinenNimi(), false);
            if (hakukohdeRDTO != null) {
                hakukohdeRDTO.setHakukohteenNimet(nimet);
            }
            else {
                valintaperusteetV1RDTO.setHakukohdeNimi(nimet);
            }
        } else if (hakukohde.getHakukohdeNimi() != null) {
            if (hakukohdeRDTO != null) {
                hakukohdeRDTO.setHakukohteenNimet(getHakukohteenNimet(hakukohde));
            }
            else {
                valintaperusteetV1RDTO.setHakukohdeNimi(getHakukohteenNimet(hakukohde));
            }
        }
    }

    private void convertOpetuskieletToDTO(Hakukohde hakukohde, HakukohdeV1RDTO hakukohdeRDTO) {
        Set<String> opetusKielet = new TreeSet<String>();
        for (KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
            for (KoodistoUri ku : komoto.getOpetuskielis()) {
                int p = ku.getKoodiUri().lastIndexOf('#');
                opetusKielet.add(p == -1 ? ku.getKoodiUri() : ku.getKoodiUri().substring(0, p));
            }
        }
        hakukohdeRDTO.setOpetusKielet(opetusKielet);
    }

    private void convertHakukelpoisuusVaatimuksetToDTO(Hakukohde hakukohde, HakukohdeV1RDTO hakukohdeRDTO) {
        if (hakukohde.getHakukelpoisuusVaatimusKuvaus() != null) {
            hakukohdeRDTO.setHakukelpoisuusVaatimusKuvaukset(convertMonikielinenTekstiToMap(hakukohde.getHakukelpoisuusVaatimusKuvaus(), false));
        } else {
            if (hakukohde.getHakukelpoisuusVaatimukset() != null) {
                HashMap<String, String> hakukelpoisuusVaatimusKuvaukset = new HashMap<String, String>();
                for (String hakukelpoisuusVaatimusUri : hakukohde.getHakukelpoisuusVaatimukset()) {
                    hakukelpoisuusVaatimusKuvaukset.putAll(tarjontaKoodistoHelper.getKoodiMetadataKuvaus(hakukelpoisuusVaatimusUri));

                }
                if (hakukelpoisuusVaatimusKuvaukset.size() < 1) {
                    String hakukelpoisuusryhmaUri = tarjontaKoodistoHelper.getHakukelpoisuusvaatimusrymaUriForHakukohde(hakukohde.getHakukohdeNimi());
                    if (hakukelpoisuusryhmaUri != null) {
                        hakukohdeRDTO.getHakukelpoisuusvaatimusUris().add(hakukelpoisuusryhmaUri);
                        hakukelpoisuusVaatimusKuvaukset.putAll(tarjontaKoodistoHelper.getKoodiMetadataKuvaus(hakukelpoisuusryhmaUri));
                    }
                }
                hakukohdeRDTO.setHakukelpoisuusVaatimusKuvaukset(hakukelpoisuusVaatimusKuvaukset);
            }
        }
    }

    private void convertLiitteidenToimitusosoiteToDTO(Hakukohde hakukohde, HakukohdeV1RDTO hakukohdeRDTO) {
        if (hakukohde.getLiitteidenToimitusOsoite() != null) {
            hakukohdeRDTO.setLiitteidenToimitusOsoite(CommonToDTOConverter.convertOsoiteToOsoiteDTO(hakukohde.getLiitteidenToimitusOsoite()));
            if (hakukohdeRDTO.getLiitteidenToimitusOsoite().getPostinumero() != null) {
                KoodiType postinumeroKoodi = tarjontaKoodistoHelper.getKoodiByUri(hakukohdeRDTO.getLiitteidenToimitusOsoite().getPostinumero());
                if (postinumeroKoodi != null) {
                    hakukohdeRDTO.getLiitteidenToimitusOsoite().setPostinumeroArvo(postinumeroKoodi.getKoodiArvo());
                }
            }
        }
    }

    private void convertTarjoajaNimetToDTO(HakukohdeV1RDTO hakukohdeRDTO) {
        if (hakukohdeRDTO.getTarjoajaOids() != null && hakukohdeRDTO.getTarjoajaOids().size() > 0) {
            for (String tarjoajaOid : hakukohdeRDTO.getTarjoajaOids()) {
                OrganisaatioDTO org = organisaatioService.findByOid(tarjoajaOid);

                if (org == null) {
                    continue;
                }

                for (MonikielinenTekstiTyyppi.Teksti text : org.getNimi().getTeksti()) {
                    hakukohdeRDTO.getTarjoajaNimet().put(text.getKieliKoodi(), text.getValue());
                }
            }
        }
    }

    private void convertLiitteetToDTO(Hakukohde hakukohde, HakukohdeV1RDTO hakukohdeRDTO) {
        for (HakukohdeLiite liite : hakukohde.getLiites()) {
            hakukohdeRDTO.getHakukohteenLiitteet().add(fromHakukohdeLiite(liite));
        }
    }

    private void convertValintakokeetToDTO(Hakukohde hakukohde, HakukohdeV1RDTO hakukohdeRDTO) {
        if (hakukohde.getValintakoes() != null) {
            List<ValintakoeV1RDTO> valintakoeDtos = new ArrayList<ValintakoeV1RDTO>();
            for (Valintakoe valintakoe : hakukohde.getValintakoes()) {
                valintakoeDtos.add(convertValintakoeToValintakoeV1RDTO(valintakoe));
            }
            hakukohdeRDTO.setValintakokeet(valintakoeDtos);

        }
    }

    private void convertYhteystiedotToDTO(Hakukohde hakukohde, HakukohdeV1RDTO hakukohdeRDTO) {
        List<YhteystiedotV1RDTO> yhteystietoDTOs = new ArrayList<YhteystiedotV1RDTO>();

        for (Yhteystiedot yh : hakukohde.getYhteystiedot()) {
            YhteystiedotV1RDTO yhteystietoDTO = CommonToDTOConverter.convertYhteystiedotToYhteystiedotRDTO(yh);

            if (yhteystietoDTO.getPostinumero() != null) {
                KoodiType postinumeroKoodi = tarjontaKoodistoHelper.getKoodiByUri(yhteystietoDTO.getPostinumero());
                if (postinumeroKoodi != null) {
                    yhteystietoDTO.setPostinumeroArvo(postinumeroKoodi.getKoodiArvo());
                }
            }
            yhteystietoDTOs.add(yhteystietoDTO);
        }

        hakukohdeRDTO.setYhteystiedot(yhteystietoDTOs);
    }

    private Map<String, String> getHakukohteenNimet(Hakukohde hakukohde) {
        if (!hakukohde.getKoulutusmoduuliToteutuses().isEmpty()) {
            KoulutusmoduuliToteutus komoto = hakukohde.getKoulutusmoduuliToteutuses().iterator().next();

            Map<String, String> hakukohteenNimet = new HashMap<String, String>();

            if (ToteutustyyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS.equals(komoto.getToteutustyyppi())) {
                hakukohteenNimet.put("kieli_fi", hakukohde.getHakukohdeNimi());
            } else {
                KoodiType koodiType = tarjontaKoodistoHelper.getKoodiByUri(hakukohde.getHakukohdeNimi());
                if (koodiType != null && koodiType.getMetadata() != null) {
                    KoodiMetadataType fi = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KieliType.FI);
                    KoodiMetadataType sv = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KieliType.SV);
                    KoodiMetadataType en = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KieliType.EN);

                    if (fi != null && fi.getNimi() != null) {
                        hakukohteenNimet.put("kieli_fi", fi.getNimi());
                    }

                    if (sv != null && sv.getNimi() != null) {
                        hakukohteenNimet.put("kieli_sv", sv.getNimi());
                    }

                    if (en != null && en.getNimi() != null) {
                        hakukohteenNimet.put("kieli_en", en.getNimi());
                    }
                }
            }

            return hakukohteenNimet;
        }
        return null;
    }

    private void convertSoraAndValintaperustekuvauksetToDTO(Hakukohde hakukohde, HakukohdeV1RDTO hakukohdeRDTO) {
        Set<KoulutusmoduuliToteutus> komotos = hakukohde.getKoulutusmoduuliToteutuses();
        KoulutusmoduuliToteutus komoto = null;
        if (komotos != null && komotos.size() > 0) {
            komoto = komotos.iterator().next();
        }

        MonikielinenTeksti valintaperustekuvaus = hakukohde.getValintaperusteKuvaus();
        if (valintaperustekuvaus != null && valintaperustekuvaus.getKaannoksetAsList().size() > 0) {
            hakukohdeRDTO.setValintaperusteKuvaukset(convertMonikielinenTekstiToMapWithoutVersions(valintaperustekuvaus));
        } else {
            String uri = tarjontaKoodistoHelper.getValintaperustekuvausryhmaUriForHakukohde(hakukohde.getHakukohdeNimi());
            if (uri != null) {
                hakukohdeRDTO.setValintaperustekuvausKoodiUri(uri);

                if (komoto != null) {
                    hakukohdeRDTO.setValintaperusteKuvaukset(valintaperustekuvausHelper.getKuvausByAvainTyyppiKausiVuosi(
                            uri,
                            ValintaperusteSoraKuvaus.Tyyppi.VALINTAPERUSTEKUVAUS,
                            komoto.getAlkamiskausiUri(),
                            komoto.getAlkamisVuosi()
                    ));
                }
            }
        }

        MonikielinenTeksti sorakuvaus = hakukohde.getSoraKuvaus();
        if (sorakuvaus != null && sorakuvaus.getKaannoksetAsList().size() > 0) {
            hakukohdeRDTO.setSoraKuvaukset(convertMonikielinenTekstiToMap(sorakuvaus, false));
        } else {
            String uri = tarjontaKoodistoHelper.getSORAKysymysryhmaUriForHakukohde(hakukohde.getHakukohdeNimi());
            if (uri != null) {
                hakukohdeRDTO.setSoraKuvausKoodiUri(uri);

                if (komoto != null) {
                    hakukohdeRDTO.setSoraKuvaukset(valintaperustekuvausHelper.getKuvausByAvainTyyppiKausiVuosi(
                            uri,
                            ValintaperusteSoraKuvaus.Tyyppi.SORA,
                            komoto.getAlkamiskausiUri(),
                            komoto.getAlkamisVuosi()
                    ));
                }
            }
        }
    }

    private void convertOrganisaatioRyhmaOidsToDTO(Hakukohde hakukohde, HakukohdeV1RDTO hakukohdeRDTO) {
        List<String> organisaatioRyhmaOids = (List<String>) CollectionUtils.collect(hakukohde.getRyhmaliitokset(),
                new BeanToPropertyValueTransformer("ryhmaOid"));
        hakukohdeRDTO.setOrganisaatioRyhmaOids(organisaatioRyhmaOids.toArray(new String[organisaatioRyhmaOids.size()]));
    }

    private void convertPainotettavatOppianeetToDTO(Hakukohde hakukohde, HakukohdeV1RDTO hakukohdeRDTO) {
        for (PainotettavaOppiaine painotettavaOppiaine : hakukohde.getPainotettavatOppiaineet()) {
            PainotettavaOppiaineV1RDTO painotettavaOppiaineV1RDTO = new PainotettavaOppiaineV1RDTO();
            painotettavaOppiaineV1RDTO.setOid(painotettavaOppiaine.getId().toString());
            painotettavaOppiaineV1RDTO.setOppiaineUri(painotettavaOppiaine.getOppiaine());
            painotettavaOppiaineV1RDTO.setPainokerroin(painotettavaOppiaine.getPainokerroin());
            hakukohdeRDTO.getPainotettavatOppiaineet().add(painotettavaOppiaineV1RDTO);
        }
    }

    private void convertTarjoatiedotToDTO(Hakukohde hakukohde, HakukohdeV1RDTO hakukohdeRDTO) {
        if (hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().isEmpty()) {
            for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {
                String oid = koulutusmoduuliToteutus.getOid();

                KoulutusmoduuliTarjoajatiedotV1RDTO koulutusmoduuliTarjoajatiedotV1RDTO = new KoulutusmoduuliTarjoajatiedotV1RDTO();

                // Lisää oletuksena vain se tarjoja, joka loi koulutuksen
                koulutusmoduuliTarjoajatiedotV1RDTO.getTarjoajaOids().add(koulutusmoduuliToteutus.getTarjoaja());

                hakukohdeRDTO.getKoulutusmoduuliToteutusTarjoajatiedot().put(oid, koulutusmoduuliTarjoajatiedotV1RDTO);
            }
        } else {
            Set<String> komotoOids = new HashSet<String>();
            for (KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
                komotoOids.add(komoto.getOid());
            }
            for (Map.Entry<String, KoulutusmoduuliToteutusTarjoajatiedot> entry : hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().entrySet()) {
                String oid = entry.getKey();

                // Komoto voi olla poistettu jolloin sitä ei palauteta
                // (komotoOids sisältää kaikki komotot jotka eivät ole poistettu)
                if (!komotoOids.contains(oid)) {
                    continue;
                }

                KoulutusmoduuliToteutusTarjoajatiedot tarjoajatiedot = entry.getValue();

                KoulutusmoduuliTarjoajatiedotV1RDTO koulutusmoduuliTarjoajatiedotV1RDTO = new KoulutusmoduuliTarjoajatiedotV1RDTO();
                for (String tarjoajaOid : tarjoajatiedot.getTarjoajaOids()) {
                    koulutusmoduuliTarjoajatiedotV1RDTO.addTarjoajaOid(tarjoajaOid);
                }

                hakukohdeRDTO.getKoulutusmoduuliToteutusTarjoajatiedot().put(oid, koulutusmoduuliTarjoajatiedotV1RDTO);
            }
        }
    }

    private HashMap<String, String> convertMonikielinenMetadata(List<MonikielinenMetadata> metadatas) {

        if (metadatas != null) {

            HashMap<String, String> metamap = new HashMap<String, String>();

            for (MonikielinenMetadata metadata : metadatas) {
                metamap.put(metadata.getKieli(), metadata.getArvo());
            }

            return metamap;

        } else {
            return null;
        }

    }

    /**
     * Convert MonikielinenTeksti to Map<S, S>. If assumeKieliKoodiIsKoodistoUri
     * is true the kielikoodi is assumend to be "koodi_" prefixed - if not then
     * added.
     *
     * @param teksti
     * @param assumeKieliKoodiIsKoodistoUri
     * @return
     */
    public HashMap<String, String> convertMonikielinenTekstiToMap(MonikielinenTeksti teksti, boolean assumeKieliKoodiIsKoodistoUri) {
        HashMap<String, String> result = new HashMap<String, String>();

        if (teksti == null) {
            return result;
        }

        for (TekstiKaannos tekstiKaannos : teksti.getKaannoksetAsList()) {
            String kieliKoodi = tekstiKaannos.getKieliKoodi();

            // Old data may not contain "Koodisto URIs" as keys... so if we assume they SHOULD have it the we just add it ... :)
            // Old "Haku" data does not have this, fixing it here
            if (assumeKieliKoodiIsKoodistoUri && kieliKoodi != null && !kieliKoodi.startsWith("kieli_")) {
                kieliKoodi = "kieli_" + kieliKoodi;
            }

            result.put(kieliKoodi, tekstiKaannos.getArvo());
        }

        return result;
    }

    public Hakukohde toHakukohde(HakukohdeV1RDTO hakukohdeRDTO) {
        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setOid(hakukohdeRDTO.getOid());
        hakukohde.setAloituspaikatLkm(hakukohdeRDTO.getAloituspaikatLkm());
        hakukohde.setAloituspaikatKuvaus(convertMapToMonikielinenTeksti(hakukohdeRDTO.getAloituspaikatKuvaukset()));
        hakukohde.setHakuaikaAlkuPvm(hakukohdeRDTO.getHakuaikaAlkuPvm());
        hakukohde.setHakuaikaLoppuPvm(hakukohdeRDTO.getHakuaikaLoppuPvm());

        if (ToteutustyyppiEnum.KORKEAKOULUTUS.toString().equals(hakukohdeRDTO.getToteutusTyyppi())
                || ToteutustyyppiEnum.KORKEAKOULUOPINTO.toString().equals(hakukohdeRDTO.getToteutusTyyppi())) {
            if (hakukohdeRDTO.getHakukohteenNimet() != null && hakukohdeRDTO.getHakukohteenNimet().size() > 0) {
                hakukohde.setHakukohdeMonikielinenNimi(convertMapToMonikielinenTeksti(hakukohdeRDTO.getHakukohteenNimet()));
            }
        }

        hakukohde.setKaksoisTutkinto(hakukohdeRDTO.getKaksoisTutkinto());

        if (hakukohdeRDTO.getHakukohteenNimiUri() != null) {
            hakukohde.setHakukohdeNimi(hakukohdeRDTO.getHakukohteenNimiUri());
        }

        if (hakukohdeRDTO.getValintaPerusteKuvausTunniste() != null) {
            hakukohde.setValintaPerusteKuvausTunniste(hakukohdeRDTO.getValintaPerusteKuvausTunniste());
        }

        if (hakukohdeRDTO.getSoraKuvausTunniste() != null) {
            hakukohde.setSoraKuvausTunniste(hakukohdeRDTO.getSoraKuvausTunniste());
        }

        if (hakukohdeRDTO.getUlkoinenTunniste() != null) {
            hakukohde.setUlkoinenTunniste(hakukohdeRDTO.getUlkoinenTunniste());
        }
        if (hakukohdeRDTO.getVersion() != null) {
            hakukohde.setVersion(hakukohdeRDTO.getVersion());
        }

        hakukohde.setLastUpdatedByOid(contextDataService.getCurrentUserOid());
        hakukohde.setLastUpdateDate(new Date());

        if (KoulutusasteResolver.isToisenAsteenKoulutus(ToteutustyyppiEnum.valueOf(hakukohdeRDTO.getToteutusTyyppi()))) {
            hakukohde.setHakuaikaAlkuPvm(hakukohdeRDTO.getHakuaikaAlkuPvm());
            hakukohde.setHakuaikaLoppuPvm(hakukohdeRDTO.getHakuaikaLoppuPvm());
        }

        hakukohde.setTila(TarjontaTila.valueOf(hakukohdeRDTO.getTila()));
        hakukohde.setLiitteidenToimitusPvm(hakukohdeRDTO.getLiitteidenToimitusPvm());
        hakukohde.setValintojenAloituspaikatLkm(hakukohdeRDTO.getValintojenAloituspaikatLkm());

        if (StringUtils.isNotBlank(hakukohdeRDTO.getSahkoinenToimitusOsoite())) {
            hakukohde.setSahkoinenToimitusOsoite(hakukohdeRDTO.getSahkoinenToimitusOsoite());
        } else {
            hakukohde.setSahkoinenToimitusOsoite(null);
        }

        hakukohde.setKaytetaanHaunPaattymisenAikaa(hakukohdeRDTO.isKaytetaanHaunPaattymisenAikaa());
        hakukohde.setSoraKuvausKoodiUri(hakukohdeRDTO.getSoraKuvausKoodiUri());
        hakukohde.setSoraKuvausKoodiUri(hakukohdeRDTO.getSoraKuvausKoodiUri());
        hakukohde.setValintaperustekuvausKoodiUri(hakukohdeRDTO.getValintaperustekuvausKoodiUri());

        if (KoulutusasteResolver.isToisenAsteenKoulutus(ToteutustyyppiEnum.valueOf(hakukohdeRDTO.getToteutusTyyppi()))) {
            hakukohde.setKaytetaanJarjestelmanValintapalvelua(true);
        } else {
            hakukohde.setKaytetaanJarjestelmanValintapalvelua(hakukohdeRDTO.isKaytetaanJarjestelmanValintaPalvelua());
        }

        if (hakukohdeRDTO.getValintaperusteKuvaukset() != null) {
            hakukohde.setValintaperusteKuvaus(convertMapToMonikielinenTeksti(hakukohdeRDTO.getValintaperusteKuvaukset()));
        }
        if (hakukohdeRDTO.getSoraKuvaukset() != null) {
            hakukohde.setSoraKuvaus(convertMapToMonikielinenTeksti(hakukohdeRDTO.getSoraKuvaukset()));
        }
        hakukohde.setAlinHyvaksyttavaKeskiarvo(hakukohdeRDTO.getAlinHyvaksyttavaKeskiarvo());
        hakukohde.setAlinValintaPistemaara(hakukohdeRDTO.getAlinValintaPistemaara());
        hakukohde.setYlinValintaPistemaara(hakukohdeRDTO.getYlinValintapistemaara());

        if (hakukohdeRDTO.getLisatiedot() != null) {
            hakukohde.setLisatiedot(convertMapToMonikielinenTeksti(hakukohdeRDTO.getLisatiedot()));
        }

        if (hakukohdeRDTO.getHakukelpoisuusvaatimusUris() != null) {
            for (String hakukelpoisuusVaatimus : hakukohdeRDTO.getHakukelpoisuusvaatimusUris()) {
                hakukohde.getHakukelpoisuusVaatimukset().add(hakukelpoisuusVaatimus);
            }
        }

        if (hakukohdeRDTO.getValintaPerusteKuvausKielet() != null) {

            hakukohde.setValintaPerusteKuvausKielet(hakukohdeRDTO.getValintaPerusteKuvausKielet());

        }

        if (hakukohdeRDTO.getSoraKuvausKielet() != null) {

            hakukohde.setSoraKuvausKielet(hakukohdeRDTO.getSoraKuvausKielet());

        }

        if (hakukohdeRDTO.getHakukelpoisuusVaatimusKuvaukset() != null) {
            hakukohde.setHakukelpoisuusVaatimusKuvaus(convertMapToMonikielinenTeksti(hakukohdeRDTO.getHakukelpoisuusVaatimusKuvaukset()));
        }
        if (hakukohdeRDTO.getYhteystiedot() != null) {
            for (YhteystiedotV1RDTO yh : hakukohdeRDTO.getYhteystiedot()) {
                hakukohde.addYhteystiedot(CommonRestConverters.convertYhteystiedotV1RDTOToYhteystiedot(yh));
            }
        }
        if (hakukohdeRDTO.getLiitteidenToimitusOsoite() != null) {
            hakukohde.setLiitteidenToimitusOsoite(CommonRestConverters.toOsoite(hakukohdeRDTO.getLiitteidenToimitusOsoite()));
        }

        for (ValintakoeV1RDTO valintakoeV1RDTO : hakukohdeRDTO.getValintakokeet()) {
            hakukohde.addValintakoe(convertValintakoeRDTOToValintakoe(valintakoeV1RDTO));
        }

        for (HakukohdeLiiteV1RDTO liite : hakukohdeRDTO.getHakukohteenLiitteet()) {
            hakukohde.addLiite(toHakukohdeLiite(liite));
        }

        for (PainotettavaOppiaineV1RDTO painotettavaOppiaineV1RDTO : hakukohdeRDTO.getPainotettavatOppiaineet()) {
            hakukohde.addPainotettavaOppiaine(convertPainotettavaOppiaineRDTOToPainotettavaOppiaine(painotettavaOppiaineV1RDTO));
        }

        if (hakukohdeRDTO.getHakuMenettelyKuvaukset() != null) {
            hakukohde.setHakuMenettelyKuvaus(convertMapToMonikielinenTeksti(hakukohdeRDTO.getHakuMenettelyKuvaukset()));
        }
        if (hakukohdeRDTO.getPeruutusEhdotKuvaukset() != null) {
            hakukohde.setPeruutusEhdotKuvaus(convertMapToMonikielinenTeksti(hakukohdeRDTO.getPeruutusEhdotKuvaukset()));
        }

        return hakukohde;
    }

    public HakukohdeValintaperusteetV1RDTO valintaperusteetFromHakukohde(Hakukohde hakukohde) {
        HakukohdeValintaperusteetV1RDTO t = new HakukohdeValintaperusteetV1RDTO();

        t.setOid(hakukohde.getOid());
        t.setVersion(hakukohde.getVersion());

        // tarjoajaOid, tarjoajaNimi
        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {
            if (koulutusmoduuliToteutus.getTarjoaja() != null) {
                // Assumes that only one provider for koulutus - is this true?
                String organisaatioOid = koulutusmoduuliToteutus.getTarjoaja();
                t.setTarjoajaOid(organisaatioOid);
                if (organisaatioOid != null) {
                    OrganisaatioDTO organisaatio = organisaatioService.findByOid(organisaatioOid);
                    if (organisaatio != null) {
                        Map<String, String> map = new HashMap<String, String>();
                        for (MonikielinenTekstiTyyppi.Teksti teksti : organisaatio.getNimi().getTeksti()) {
                            map.put(tarjontaKoodistoHelper.convertKielikoodiToKieliUri(teksti.getKieliKoodi()),
                                    teksti.getValue());
                        }
                        t.setTarjoajaNimi(map);
                    }
                }
                break;
            }
        }

        BigDecimal nolla = new BigDecimal("0.0");

        convertHakukohteenNimetToDTO(hakukohde, null, t);
        if (t.getHakukohdeNimi() == null) {
            Map<String, String> nimiMap = new HashMap<String, String>();
            nimiMap.put("kieli_fi", "Hakukohteella ei ole nimeä");
            t.setHakukohdeNimi(nimiMap);
        }

        // Painotetun keskiarvon arvoväli
        t.setPainotettuKeskiarvoHylkaysMax(hakukohde.getAlinHyvaksyttavaKeskiarvo() != null ? new BigDecimal(String.valueOf(hakukohde.getAlinHyvaksyttavaKeskiarvo())) : nolla);
        t.setPainotettuKeskiarvoHylkaysMin(nolla);

        // Kokonaishylkäyksen arvoväli
        t.setHylkaysMax(hakukohde.getAlinValintaPistemaara() != null ? new BigDecimal(String.valueOf(hakukohde.getAlinValintaPistemaara())) : nolla);
        t.setHylkaysMin(nolla);

        // Valintakokeiden arvovälit
        t.setLisanayttoMax(nolla);
        t.setLisanayttoMin(nolla);
        t.setLisapisteMax(nolla);
        t.setLisapisteMin(nolla);
        t.setPaasykoeMax(nolla);
        t.setPaasykoeMin(nolla);

        t.setLisanayttoHylkaysMax(nolla);
        t.setLisanayttoHylkaysMin(nolla);
        t.setLisapisteHylkaysMax(nolla);
        t.setLisapisteHylkaysMin(nolla);
        t.setPaasykoeHylkaysMax(nolla);
        t.setPaasykoeHylkaysMin(nolla);

        BigDecimal kokonaispisteet = null;

        // Muutetaan yhdistetyt valintakokeet erillisiksi kokeiksi
        Set<Valintakoe> result = new HashSet<Valintakoe>();
        for (Valintakoe koe : hakukohde.getValintakoes()) {
            Valintakoe vk = null;
            Valintakoe lt = null;

            Set<Pisteraja> addToBothVKs = new HashSet<Pisteraja>();

            for (Pisteraja pisteraja : koe.getPisterajat()) {

                if (ValinnanPisterajaTyyppi.PAASYKOE.value().equals(pisteraja.getValinnanPisterajaTyyppi())) {
                    vk = (vk == null) ? new Valintakoe() : vk;
                    if (vk.getPisterajat() == null) {
                        vk.setPisterajat(new HashSet<Pisteraja>());
                    }
                    vk.getPisterajat().add(pisteraja);
                } else if (ValinnanPisterajaTyyppi.LISAPISTEET.value().equals(pisteraja.getValinnanPisterajaTyyppi())) {
                    lt = (lt == null) ? new Valintakoe() : lt;
                    if (lt.getPisterajat() == null) {
                        lt.setPisterajat(new HashSet<Pisteraja>());
                    }
                    lt.getPisterajat().add(pisteraja);
                } else {
                    if (ValinnanPisterajaTyyppi.KOKONAISPISTEET.value().equals(pisteraja.getValinnanPisterajaTyyppi())) {
                        kokonaispisteet = pisteraja.getAlinHyvaksyttyPistemaara();
                    }
                    addToBothVKs.add(pisteraja);
                }
            }

            if (vk != null) {
                vk.setKuvaus(koe.getKuvaus());
                vk.setTyyppiUri(ValintaperusteetUtil.PAASY_JA_SOVELTUVUUSKOE);
                vk.getPisterajat().addAll(addToBothVKs);
                result.add(vk);
            }

            if (lt != null) {

                lt.setKuvaus(koe.getLisanaytot());
                if (koe.getTyyppiUri() != null
                        && (koe.getTyyppiUri().split("#")[0].equals(ValintaperusteetUtil.LISANAYTTO) || koe.getTyyppiUri().split("#")[0].equals(ValintaperusteetUtil.LISAPISTE))) {
                    lt.setTyyppiUri(koe.getTyyppiUri());
                } else {
                    lt.setTyyppiUri(ValintaperusteetUtil.LISANAYTTO);
                }

                lt.getPisterajat().addAll(addToBothVKs);

                result.add(lt);
            }

            // Jos valintakokeella ei ole pisterajoja
            if (lt == null && vk == null) {
                result.add(koe);
            }
        }

        for (Valintakoe koe : result) {
            if (koe.getTyyppiUri() != null) {
                if (koe.getTyyppiUri().split("#")[0].equals(ValintaperusteetUtil.PAASY_JA_SOVELTUVUUSKOE)) {
                    for (Pisteraja p : koe.getPisterajat()) {
                        if (p.getValinnanPisterajaTyyppi().equals(ValinnanPisterajaTyyppi.PAASYKOE.value())) {
                            t.setPaasykoeMax(p.getYlinPistemaara());
                            t.setPaasykoeMin(p.getAlinPistemaara());
                            t.setPaasykoeHylkaysMax(p.getAlinHyvaksyttyPistemaara());
                        }
                    }
                }
                if (koe.getTyyppiUri().split("#")[0].equals(ValintaperusteetUtil.LISANAYTTO)) {
                    for (Pisteraja p : koe.getPisterajat()) {
                        if (p.getValinnanPisterajaTyyppi().equals(ValinnanPisterajaTyyppi.LISAPISTEET.value())) {
                            t.setLisanayttoMax(p.getYlinPistemaara());
                            t.setLisanayttoMin(p.getAlinPistemaara());
                            t.setLisanayttoHylkaysMax(p.getAlinHyvaksyttyPistemaara());
                        }
                    }
                }
                if (koe.getTyyppiUri().split("#")[0].equals(ValintaperusteetUtil.LISAPISTE)) {
                    for (Pisteraja p : koe.getPisterajat()) {
                        if (p.getValinnanPisterajaTyyppi().equals(ValinnanPisterajaTyyppi.LISAPISTEET.value())) {
                            t.setLisapisteMax(p.getYlinPistemaara());
                            t.setLisapisteMin(p.getAlinPistemaara());
                            t.setLisapisteHylkaysMax(p.getAlinHyvaksyttyPistemaara());
                        }
                    }
                }
            }

        }

        if (t.getHylkaysMax().equals(nolla) && kokonaispisteet != null) {
            t.setHylkaysMax(kokonaispisteet);
        }

        // Alimman valintapistemäärän asettaminen ei ole pakollista, jos kohteella on vain pääsykoe
        if (t.getHylkaysMax().equals(nolla) && !t.getPaasykoeHylkaysMax().equals(nolla)) {
            t.setHylkaysMax(t.getPaasykoeHylkaysMax());
        }

        for (Valintakoe koe : result) {
            ValintakoeRDTO koeDto = new ValintakoeRDTO();

            koeDto.setCreated(null);
            koeDto.setCreatedBy(null);
            koeDto.setModified(koe.getLastUpdateDate());
            koeDto.setModifiedBy(koe.getLastUpdatedByOid());
            koeDto.setOid("" + koe.getId());
            koeDto.setVersion(koe.getVersion() != null ? koe.getVersion().intValue() : 0);
            koeDto.setKuvaus(convertMonikielinenTekstiToMap(koe.getKuvaus(), false));
            koeDto.setLisanaytot(convertMonikielinenTekstiToMap(koe.getLisanaytot(), false));
            koeDto.setTyyppiUri(koe.getTyyppiUri());

            for (ValintakoeAjankohta kohta : koe.getAjankohtas()) {
                ValintakoeAjankohtaRDTO kohtaDto = new ValintakoeAjankohtaRDTO();

                OsoiteRDTO osoite = new OsoiteRDTO();

                osoite.setOsoiterivi1(kohta.getAjankohdanOsoite().getOsoiterivi1());
                osoite.setOsoiterivi2(kohta.getAjankohdanOsoite().getOsoiterivi2());
                osoite.setPostinumero(kohta.getAjankohdanOsoite().getPostinumero());
                osoite.setPostitoimipaikka(kohta.getAjankohdanOsoite().getPostitoimipaikka());

                kohtaDto.setOsoite(osoite);
                kohtaDto.setAlkaa(kohta.getAlkamisaika());
                kohtaDto.setLisatiedot(kohta.getLisatietoja());
                kohtaDto.setLoppuu(kohta.getPaattymisaika());

                kohtaDto.setOid("" + kohta.getId());
                kohtaDto.setVersion(kohta.getVersion() != null ? kohta.getVersion().intValue() : 0);

                koeDto.getValintakoeAjankohtas().add(kohtaDto);
            }

            for (Pisteraja raja : koe.getPisterajat()) {
                ValintakoePisterajaRDTO rajaDto = new ValintakoePisterajaRDTO();

                rajaDto.setAlinHyvaksyttyPistemaara(raja.getAlinHyvaksyttyPistemaara() != null ? raja.getAlinHyvaksyttyPistemaara().doubleValue() : null);
                rajaDto.setAlinPistemaara(raja.getAlinPistemaara() != null ? raja.getAlinPistemaara().doubleValue() : null);
                rajaDto.setTyyppi(raja.getValinnanPisterajaTyyppi());
                rajaDto.setYlinPistemaara(raja.getYlinPistemaara() != null ? raja.getYlinPistemaara().doubleValue() : null);

                rajaDto.setOid("" + raja.getId());
                rajaDto.setVersion(raja.getVersion() != null ? raja.getVersion().intValue() : 0);

                koeDto.getValintakoePisterajas().add(rajaDto);
            }

            t.getValintakokeet().add(koeDto);

        }

        if (hakukohde.getHaku() != null) {
            t.setHakuOid(hakukohde.getHaku().getOid());
            t.setHakuKohdejoukkoUri(hakukohde.getHaku().getKohdejoukkoUri());
        }

        t.setHakukohdeNimiUri(hakukohde.getHakukohdeNimi());
        t.setTila(hakukohde.getTila() != null ? hakukohde.getTila().name() : null);

        t.setModified(hakukohde.getLastUpdateDate());
        t.setModifiedBy(hakukohde.getLastUpdatedByOid());

        t.setValintojenAloituspaikatLkm(hakukohde.getValintojenAloituspaikatLkm());

        // Opetuskielet
        Set<String> opetuskielis = new HashSet<String>();
        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {
            for (KoodistoUri koodistoUri : koulutusmoduuliToteutus.getOpetuskielis()) {
                opetuskielis.add(ValintaperusteetUtil.sanitizeOpetuskieliUri(koodistoUri.getKoodiUri()));
            }
        }
        t.setOpetuskielet(new ArrayList<String>(opetuskielis));

        t.setPainokertoimet(ValintaperusteetUtil.convertPainotettavatOppianeet(hakukohde.getPainotettavatOppiaineet()));

        Haku haku = hakukohde.getHaku();
        if (haku != null) {
            t.setHakuVuosi(haku.getHakukausiVuosi());
            t.setHakuKausi(tarjontaKoodistoHelper.getKoodiMetadataNimi(haku.getHakukausiUri()));
        } else {
            t.setHakuVuosi(-1);
            t.setHakuKausi(null);
        }

        return t;
    }

    public Valintakoe toValintakoe(ValintakoeV1RDTO valintakoeV1RDTO) {
        LOG.debug("toValintakoe({})", valintakoeV1RDTO);
        Valintakoe valintakoe = convertValintakoeRDTOToValintakoe(valintakoeV1RDTO);
        LOG.debug("toValintakoe result ->  {}", valintakoe);
        return valintakoe;
    }

    public ValintakoeV1RDTO fromValintakoe(Valintakoe valintakoe) {
        LOG.debug("fromValintakoe({})", valintakoe);
        ValintakoeV1RDTO valintakoeV1RDTO = convertValintakoeToValintakoeV1RDTO(valintakoe);
        LOG.debug("fromValintakoe result -> {}", valintakoeV1RDTO);
        return valintakoeV1RDTO;

    }

    public HakukohdeLiite toHakukohdeLiite(HakukohdeLiiteV1RDTO hakukohdeLiiteV1RDTO) {
        HakukohdeLiite hakukohdeLiite = new HakukohdeLiite();
        hakukohdeLiite.setId(oidFromString(hakukohdeLiiteV1RDTO.getOid()));

        hakukohdeLiite.setKieli(hakukohdeLiiteV1RDTO.getKieliUri());
        hakukohdeLiite.setHakukohdeLiiteNimi(hakukohdeLiiteV1RDTO.getLiitteenNimi());
        hakukohdeLiite.setSahkoinenToimitusosoite(hakukohdeLiiteV1RDTO.getSahkoinenToimitusOsoite());
        hakukohdeLiite.setErapaiva(hakukohdeLiiteV1RDTO.getToimitettavaMennessa());
        hakukohdeLiite.setToimitusosoite(CommonRestConverters.toOsoite(hakukohdeLiiteV1RDTO.getLiitteenToimitusOsoite()));
        hakukohdeLiite.setKuvaus(convertMapToMonikielinenTeksti(hakukohdeLiiteV1RDTO.getLiitteenKuvaukset()));
        hakukohdeLiite.setLiitetyyppi(hakukohdeLiiteV1RDTO.getLiitteenTyyppi());
        hakukohdeLiite.setJarjestys(hakukohdeLiiteV1RDTO.getJarjestys());
        hakukohdeLiite.setKaytetaanHakulomakkeella(hakukohdeLiiteV1RDTO.isKaytetaanHakulomakkeella());

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
        hakukohdeLiiteV1RDTO.setLiitteenTyyppi(hakukohdeLiite.getLiitetyyppi());

        if (hakukohdeLiiteV1RDTO.getLiitteenToimitusOsoite() != null && hakukohdeLiiteV1RDTO.getLiitteenToimitusOsoite().getPostinumero() != null && tarjontaKoodistoHelper != null) {
            KoodiType postinumeroKoodi = tarjontaKoodistoHelper.getKoodiByUri(hakukohdeLiiteV1RDTO.getLiitteenToimitusOsoite().getPostinumero());
            if (postinumeroKoodi != null) {
                hakukohdeLiiteV1RDTO.getLiitteenToimitusOsoite().setPostinumeroArvo(postinumeroKoodi.getKoodiArvo());
            }
        }

        hakukohdeLiiteV1RDTO.setLiitteenKuvaukset(BaseRDTOConverter.convertToMap(hakukohdeLiite.getKuvaus(), tarjontaKoodistoHelper));
        hakukohdeLiiteV1RDTO.setJarjestys(hakukohdeLiite.getJarjestys());
        hakukohdeLiiteV1RDTO.setKaytetaanHakulomakkeella(hakukohdeLiite.isKaytetaanHakulomakkeella());

        return hakukohdeLiiteV1RDTO;
    }

    private Long oidFromString(String oid) {
        if (oid == null) {
            return null;
        }
        try {
            return Long.parseLong(oid);
        } catch (NumberFormatException exp) {
            throw new IllegalArgumentException("Invalid oid: " + oid, exp);
        }
    }

    private Valintakoe convertValintakoeRDTOToValintakoe(ValintakoeV1RDTO valintakoeV1RDTO) {
        Valintakoe valintakoe = new Valintakoe();

        valintakoe.setId(oidFromString(valintakoeV1RDTO.getOid()));
        valintakoe.setValintakoeNimi(valintakoeV1RDTO.getValintakoeNimi());
        valintakoe.setKieli(valintakoeV1RDTO.getKieliUri());
        valintakoe.setTyyppiUri(valintakoeV1RDTO.getValintakoetyyppi());

        if (valintakoeV1RDTO.hasPisterajat()) {
            MonikielinenTeksti kuvaus = convertMapToMonikielinenTeksti(valintakoeV1RDTO.getKuvaukset());
            valintakoe.setKuvaus(kuvaus);

            MonikielinenTeksti lisanaytot = convertMapToMonikielinenTeksti(valintakoeV1RDTO.getLisanaytot());
            valintakoe.setLisanaytot(lisanaytot);
        } else {
            List<TekstiRDTO> tekstiRDTOs = new ArrayList<TekstiRDTO>();
            tekstiRDTOs.add(valintakoeV1RDTO.getValintakokeenKuvaus());
            valintakoe.setKuvaus(convertTekstiRDTOToMonikielinenTeksti(tekstiRDTOs));
        }

        if (valintakoeV1RDTO.getValintakoeAjankohtas() != null) {
            valintakoe.getAjankohtas().addAll(convertAjankohtaRDTOToValintakoeAjankohta(valintakoe, valintakoeV1RDTO.getValintakoeAjankohtas()));
        }

        for (ValintakoePisterajaV1RDTO pisterajaRDTO : valintakoeV1RDTO.getPisterajat()) {
            valintakoe.getPisterajat().add(convertPisterajaRDTOToPisteraja(valintakoe, pisterajaRDTO));
        }
        return valintakoe;
    }

    private PainotettavaOppiaine convertPainotettavaOppiaineRDTOToPainotettavaOppiaine(PainotettavaOppiaineV1RDTO painotettavaOppiaineV1RDTO) {
        PainotettavaOppiaine painotettavaOppiaine = new PainotettavaOppiaine();

        painotettavaOppiaine.setId(oidFromString(painotettavaOppiaineV1RDTO.getOid()));
        painotettavaOppiaine.setOppiaine(painotettavaOppiaineV1RDTO.getOppiaineUri());
        painotettavaOppiaine.setPainokerroin(painotettavaOppiaineV1RDTO.getPainokerroin());

        return painotettavaOppiaine;
    }

    public HashMap<String, String> convertMonikielinenTekstiToMapWithoutVersions(MonikielinenTeksti teksti) {
        HashMap<String, String> result = new HashMap<String, String>();

        if (teksti == null) {
            return result;
        }

        for (TekstiKaannos tekstiKaannos : teksti.getKaannoksetAsList()) {
            String kieliKoodi = tekstiKaannos.getKieliKoodi();
            result.put(kieliKoodi.split("#")[0], tekstiKaannos.getArvo());
        }
        return result;
    }

    public MonikielinenTeksti convertMapToMonikielinenTeksti(Map<String, String> nimet) {
        if (nimet == null || nimet.isEmpty()) {
            return null;
        }

        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();
        for (String key : nimet.keySet()) {
            TekstiKaannos tekstiKaannos = new TekstiKaannos(monikielinenTeksti, key, nimet.get(key));
            monikielinenTeksti.addTekstiKaannos(tekstiKaannos);
        }
        return monikielinenTeksti;
    }

    private Set<ValintakoeAjankohta> convertAjankohtaRDTOToValintakoeAjankohta(Valintakoe vk, List<ValintakoeAjankohtaRDTO> valintakoeAjankohtaRDTOs) {
        Set<ValintakoeAjankohta> valintakoeAjankohtas = new HashSet<ValintakoeAjankohta>();

        for (ValintakoeAjankohtaRDTO valintakoeAjankohtaRDTO : valintakoeAjankohtaRDTOs) {
            ValintakoeAjankohta valintakoeAjankohta = new ValintakoeAjankohta();

            valintakoeAjankohta.setValintakoe(vk);
            valintakoeAjankohta.setLisatietoja(valintakoeAjankohtaRDTO.getLisatiedot());
            valintakoeAjankohta.setAjankohdanOsoite(CommonRestConverters.toOsoite(valintakoeAjankohtaRDTO.getOsoite()));
            valintakoeAjankohta.setAlkamisaika(valintakoeAjankohtaRDTO.getAlkaa());
            valintakoeAjankohta.setPaattymisaika(valintakoeAjankohtaRDTO.getLoppuu());
            valintakoeAjankohta.setKellonaikaKaytossa(valintakoeAjankohtaRDTO.isKellonaikaKaytossa());
            valintakoeAjankohtas.add(valintakoeAjankohta);

        }

        return valintakoeAjankohtas;
    }

    /**
     * Convert list of TekstiRDTO to MonikielinenTeksti for storage.
     *
     * @param tekstis
     * @return
     */
    public MonikielinenTeksti convertTekstiRDTOToMonikielinenTeksti(List<TekstiRDTO> tekstis) {
        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();

        for (TekstiRDTO tekstiRDTO : tekstis) {
            if (tekstiRDTO != null) {
                monikielinenTeksti.addTekstiKaannos(tekstiRDTO.getUri(), tekstiRDTO.getTeksti());
                LOG.debug("MONIKIELINEN TEKSTI : {}", tekstiRDTO.getTeksti());
            }
        }

        return monikielinenTeksti;
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

    }

    private ValintakoeV1RDTO convertValintakoeToValintakoeV1RDTO(Valintakoe valintakoe) {
        ValintakoeV1RDTO valintakoeV1RDTO = new ValintakoeV1RDTO();
        valintakoeV1RDTO.setOid(valintakoe.getId().toString());
        valintakoeV1RDTO.setKieliUri(valintakoe.getKieli());
        valintakoeV1RDTO.setValintakoeNimi(valintakoe.getValintakoeNimi());
        valintakoeV1RDTO.setValintakoetyyppi(valintakoe.getTyyppiUri());

        if (valintakoe.hasPisterajas()) {
            addMultilanguageValintakokeenKuvaus(valintakoe, valintakoeV1RDTO);
            addMultilanguageValintakokeenLisanaytot(valintakoe, valintakoeV1RDTO);
        } else {
            addValintakokeenKuvausForSingleLanguageValintakoe(valintakoe, valintakoeV1RDTO);
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

        for (Pisteraja pisteraja : valintakoe.getPisterajat()) {
            valintakoeV1RDTO.getPisterajat().add(convertPisterajaToPisterajaRDTO(pisteraja));
        }

        return valintakoeV1RDTO;
    }

    private void addMultilanguageValintakokeenLisanaytot(Valintakoe valintakoe, ValintakoeV1RDTO valintakoeV1RDTO) {
        MonikielinenTeksti lisanaytot = valintakoe.getLisanaytot();
        if (lisanaytot != null) {
            valintakoeV1RDTO.setLisanaytot(lisanaytot.asMapWithoutVersions());
        }
    }

    private void addMultilanguageValintakokeenKuvaus(Valintakoe valintakoe, ValintakoeV1RDTO valintakoeV1RDTO) {
        MonikielinenTeksti kuvaus = valintakoe.getKuvaus();
        if (kuvaus != null) {
            valintakoeV1RDTO.setKuvaukset(kuvaus.asMapWithoutVersions());
        }
    }

    private void addValintakokeenKuvausForSingleLanguageValintakoe(Valintakoe valintakoe, ValintakoeV1RDTO valintakoeV1RDTO) {
        List<TekstiRDTO> kuvaus = convertMonikielinenTekstiToTekstiDTOs(valintakoe.getKuvaus());
        if (kuvaus != null && kuvaus.size() > 0) {
            valintakoeV1RDTO.setValintakokeenKuvaus(kuvaus.get(0));
        }
    }

    private ValintakoePisterajaV1RDTO convertPisterajaToPisterajaRDTO(Pisteraja pisteraja) {
        ValintakoePisterajaV1RDTO valintakoePisterajaRDTO = new ValintakoePisterajaV1RDTO();

        valintakoePisterajaRDTO.setAlinHyvaksyttyPistemaara(pisteraja.getAlinHyvaksyttyPistemaara());
        if (valintakoePisterajaRDTO.getAlinHyvaksyttyPistemaara().equals(Pisteraja.DEFAULT_ALIN_HYVAKSYTTY_PISTEMAARA)) {
            valintakoePisterajaRDTO.setAlinHyvaksyttyPistemaara(null);
        }

        valintakoePisterajaRDTO.setAlinPistemaara(pisteraja.getAlinPistemaara());
        valintakoePisterajaRDTO.setPisterajatyyppi(pisteraja.getValinnanPisterajaTyyppi());
        valintakoePisterajaRDTO.setYlinPistemaara(pisteraja.getYlinPistemaara());

        return valintakoePisterajaRDTO;
    }

    private Pisteraja convertPisterajaRDTOToPisteraja(Valintakoe valintakoe, ValintakoePisterajaV1RDTO valintakoePisterajaRDTO) {
        Pisteraja pisteraja = new Pisteraja();

        pisteraja.setValintakoe(valintakoe);
        pisteraja.setAlinPistemaara(valintakoePisterajaRDTO.getAlinPistemaara());
        pisteraja.setYlinPistemaara(valintakoePisterajaRDTO.getYlinPistemaara());
        pisteraja.setAlinHyvaksyttyPistemaara(valintakoePisterajaRDTO.getAlinHyvaksyttyPistemaara());
        pisteraja.setValinnanPisterajaTyyppi(valintakoePisterajaRDTO.getPisterajatyyppi());

        if (pisteraja.getAlinHyvaksyttyPistemaara() == null) {
            pisteraja.setAlinHyvaksyttyPistemaara(Pisteraja.DEFAULT_ALIN_HYVAKSYTTY_PISTEMAARA);
        }

        return pisteraja;
    }

    private String getDefaultKoodinimi(List<KoodiMetadataType> koodiMetadataTypes) {
        //TODO: add some logic to determine which language should be shown
        String koodiNimi = null;
        for (KoodiMetadataType koodiMetadataType : koodiMetadataTypes) {
            if (koodiMetadataType.getKieli().equals(KieliType.FI)) {
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
        valintakoeAjankohtaRDTO.setKellonaikaKaytossa(valintakoeAjankohta.isKellonaikaKaytossa());

        if (valintakoeAjankohtaRDTO.getOsoite() != null && valintakoeAjankohtaRDTO.getOsoite().getPostinumero() != null && tarjontaKoodistoHelper != null) {
            KoodiType postinumeroKoodi = tarjontaKoodistoHelper.getKoodiByUri(valintakoeAjankohtaRDTO.getOsoite().getPostinumero());
            if (postinumeroKoodi != null) {
                valintakoeAjankohtaRDTO.getOsoite().setPostinumeroArvo(postinumeroKoodi.getKoodiArvo());
            }
        }

        return valintakoeAjankohtaRDTO;

    }

    public List<TekstiRDTO> convertSimpleMonikielinenTekstiDTO(MonikielinenTeksti monikielinenTeksti) {
        if (monikielinenTeksti != null) {
            List<TekstiRDTO> tekstis = new ArrayList<TekstiRDTO>();

            for (TekstiKaannos tekstiKaannos : monikielinenTeksti.getKaannoksetAsList()) {
                TekstiRDTO tekstiRDTO = new TekstiRDTO();
                tekstiRDTO.addKieliAndNimi(tekstiKaannos.getKieliKoodi(), tekstiKaannos.getArvo());
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

            for (TekstiKaannos tekstiKaannos : monikielinenTeksti.getTekstiKaannos()) {
                TekstiRDTO tekstiRDTO = new TekstiRDTO();
                tekstiRDTO.setUri(checkAndRemoveForEmbeddedVersionInUri(tekstiKaannos.getKieliKoodi()));
                tekstiRDTO.setTeksti(tekstiKaannos.getArvo());
                try {
                    KoodiType koodiType = tarjontaKoodistoHelper.getKoodiByUri(tekstiKaannos.getKieliKoodi());
                    //TODO: should it return nimi instead ? But with what language ?
                    tekstiRDTO.setArvo(koodiType.getKoodiArvo());
                    tekstiRDTO.setVersio(koodiType.getVersio());
                    if (koodiType.getMetadata() != null) {
                        for (KoodiMetadataType meta : koodiType.getMetadata()) {
                            //By default set default name finnish
                            if (meta.getKieli().equals(KieliType.FI)) {
                                tekstiRDTO.setNimi(meta.getNimi());
                            }
                            tekstiRDTO.addKieliAndNimi(meta.getKieli().value(), meta.getNimi());
                        }
                    }

                } catch (Exception exp) {

                }
                tekstiRDTOs.add(tekstiRDTO);

            }

            return tekstiRDTOs;
        } else {
            return null;
        }

    }

    private String checkAndRemoveForEmbeddedVersionInUri(String uri) {
        if (uri != null) {
            if (uri.contains("#")) {
                StringTokenizer st = new StringTokenizer(uri, "#");
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
            TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO> rets = getTarjoaja(ret, tarjoajat, ht);
            rets.getTulokset().add(convert(ht));
        }

        // XX use hitCount when implemented
        ret.setTuloksia(source.getHakukohteet().size());

        return ret;
    }

    private HakukohdeHakutulosV1RDTO convert(HakukohdePerustieto hakukohdePerustieto) {
        HakukohdeHakutulosV1RDTO dto = new HakukohdeHakutulosV1RDTO();

        dto.setOid(hakukohdePerustieto.getOid());
        dto.setNimi(hakukohdePerustieto.getNimi());
        dto.setKausi(hakukohdePerustieto.getKoulutuksenAlkamiskausi() == null ? null : hakukohdePerustieto
                .getKoulutuksenAlkamiskausi().getNimi());
        dto.setVuosi(hakukohdePerustieto.getKoulutuksenAlkamisvuosi());
        dto.setHakuOid(hakukohdePerustieto.getHakuOid());
        dto.setHakutapa(hakukohdePerustieto.getHakutapaNimi());
        dto.setAloituspaikat(Integer.valueOf(hakukohdePerustieto.getAloituspaikat()));
        dto.setKoulutuslaji(hakukohdePerustieto.getKoulutuslaji() == null ? null : hakukohdePerustieto
                .getKoulutuslaji().getNimi());
        dto.setTila(TarjontaTila.valueOf(hakukohdePerustieto.getTila()));
        dto.setAloituspaikatKuvaukset(hakukohdePerustieto.getAloituspaikatKuvaukset());
        dto.setKoulutusasteTyyppi(hakukohdePerustieto.getKoulutusastetyyppi());
        dto.setKoulutusmoduuliTyyppi(hakukohdePerustieto.getKoulutusmoduuliTyyppi());
        dto.setRyhmaliitokset(getRyhmaliitokset(hakukohdePerustieto));
        dto.setOpetuskielet(hakukohdePerustieto.getOpetuskielet());
        dto.setToteutustyyppiEnum(ToteutustyyppiEnum.fromString(hakukohdePerustieto.getToteutustyyppi()));
        return dto;
    }

    private List<RyhmaliitosV1RDTO> getRyhmaliitokset(HakukohdePerustieto hakukohdePerustieto) {
        List<RyhmaliitosV1RDTO> ryhmaliitosDTOs = new ArrayList<RyhmaliitosV1RDTO>();
        for (SolrRyhmaliitos solrRyhmaliitos : hakukohdePerustieto.getRyhmaliitokset()) {
            RyhmaliitosV1RDTO ryhmaliitosDTO = new RyhmaliitosV1RDTO();
            ryhmaliitosDTO.setRyhmaOid(solrRyhmaliitos.getRyhmaOid());
            ryhmaliitosDTO.setPrioriteetti(solrRyhmaliitos.getPrioriteetti());
            ryhmaliitosDTOs.add(ryhmaliitosDTO);
        }
        return ryhmaliitosDTOs;
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
            TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> rets = getTarjoaja(ret, tarjoajat, ht);
            rets.getTulokset().add(convert(ht));
        }

        ret.setTuloksia(source.getHitCount());

        return ret;
    }

    public KoulutusHakutulosV1RDTO fromKomoto(KoulutusmoduuliToteutus komoto) {
        KoulutusHakutulosV1RDTO hakutulos = new KoulutusHakutulosV1RDTO();

        hakutulos.setTila(komoto.getTila());
        hakutulos.setOid(komoto.getOid());
        hakutulos.setNimi(convertMonikielinenTekstiToMap(komoto.getNimi(), true));

        ArrayList<String> tarjoajat = new ArrayList<String>();
        tarjoajat.addAll(komoto.getTarjoajaOids());
        hakutulos.setTarjoajat(tarjoajat);

        return hakutulos;
    }

    private KoulutusHakutulosV1RDTO convert(KoulutusPerustieto ht) {
        KoulutusHakutulosV1RDTO ret = new KoulutusHakutulosV1RDTO();

        ret.setOid(ht.getKomotoOid());
        ret.setKomoOid(ht.getKoulutusmoduuli());
        ret.setNimi(ht.getNimi());
        ret.setKausi(ht.getKoulutuksenAlkamiskausi() == null ? null : ht
                .getKoulutuksenAlkamiskausi().getNimi());
        ret.setKausiUri(ht.getKoulutuksenAlkamiskausi() == null ? null : ht.getKoulutuksenAlkamiskausi().getUri());
        ret.setVuosi(ht.getKoulutuksenAlkamisVuosi());
        if (ht.getPohjakoulutusvaatimus() != null) {
            ret.setPohjakoulutusvaatimus(ht.getPohjakoulutusvaatimus().getNimi());
        }
        if (ht.getKoulutuslaji() != null) {
            ret.setKoulutuslaji(ht.getKoulutuslaji().getNimi());
            ret.setKoulutuslajiUri(ht.getKoulutuslaji().getUri());
        }
        ret.setTila(TarjontaTila.valueOf(ht.getTila()));
        ret.setKoulutusasteTyyppi(ht.getKoulutusasteTyyppi());
        ret.setToteutustyyppiEnum(ht.getToteutustyyppi());
        ret.setKoulutuskoodi(ht.getKoulutusKoodi().getUri());

        ret.setKoulutuksenAlkamisPvmMin(ht.getKoulutuksenAlkamisPvmMin());
        ret.setKoulutuksenAlkamisPvmMax(ht.getKoulutuksenAlkamisPvmMax());

        ret.setTarjoajat(ht.getTarjoajat());
        ret.setKoulutusmoduuliTyyppi(ht.getKoulutusmoduuliTyyppi());
        ret.setOpetuskielet(ht.getOpetuskielet());
        return ret;
    }

    private TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> getTarjoaja(
            HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> tulos,
            Map<String, TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO>> tarjoajat,
            KoulutusPerustieto ht) {

        TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> ret = tarjoajat.get(ht.getTarjoaja().getOid());
        if (ret == null) {
            ret = new TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO>();
            tarjoajat.put(ht.getTarjoaja().getOid(), ret);
            ret.setOid(ht.getTarjoaja().getOid());
            ret.setNimi(ht.getTarjoaja().getNimi());
            tulos.getTulokset().add(ret);
        }
        return ret;
    }

    /**
     * Resolves given koodiUri to metadata (translatios);
     * <p/>
     * Becomes: { arvo : koodiArvo, uri: koodiUri, version : koodiVersion, meta:
     * { kieli_fi#1 : "Koodin suomekielinen nimi", kieli_sv#1 : "Kod
     * ruattalainen namn", ... } }
     *
     * @param koodiUri
     * @return
     */
    private KoodiV1RDTO resolveKoodiMeta(String koodiUri) {

        KoodiV1RDTO result = null;

        if (tarjontaKoodistoHelper == null) {
            return result;
        }

        if (koodiUri == null) {
            return result;
        }

        KoodiType koodi = tarjontaKoodistoHelper.getKoodiByUri(koodiUri);
        if (koodi != null) {
            result = new KoodiV1RDTO();

            result.setArvo(koodi.getKoodiArvo());
            result.setUri(koodi.getKoodiUri());
            result.setVersio(koodi.getVersio());

            // Save translations by kieliUri to "meta"
            HashMap<String, KoodiV1RDTO> meta = new HashMap<String, KoodiV1RDTO>();
            result.setMeta(meta);

            for (KoodiMetadataType koodiMetadataType : koodi.getMetadata()) {
                KoodiV1RDTO subMeta = new KoodiV1RDTO();

                String kieliUri = tarjontaKoodistoHelper.convertKielikoodiToKieliUri(koodiMetadataType.getKieli().value());
                subMeta.setKieliUri(kieliUri);
                subMeta.setArvo(koodiMetadataType.getNimi());

                meta.put(kieliUri, subMeta);
            }
        }

        return result;
    }

    private HashMap<String, String> getKuvauksetWithId(Long kuvausId, Set<String> kielet) {

        HashMap<String, String> kuvaukset = new HashMap<String, String>();
        ValintaperusteSoraKuvaus kuvaus = kuvausDAO.read(kuvausId);
        if (kielet != null) {
            for (MonikielinenMetadata meta : kuvaus.getTekstis()) {
                for (String kieli : kielet) {
                    if (kieli.trim().equals(meta.getKieli().trim())) {
                        kuvaukset.put(meta.getKieli(), meta.getArvo());
                    }
                }
            }
        }
        return kuvaukset;
    }
}
