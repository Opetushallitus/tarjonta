/*
 *
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
package fi.vm.sade.tarjonta.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jws.WebParam;

import fi.vm.sade.tarjonta.service.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO.SearchCriteria;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.koodisto.OppilaitosKoodiRelations;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.Kieliaine;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.model.searchParams.ListHakuSearchParam;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.business.impl.HakuService;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.service.impl.conversion.HakukohdeSetToDTOConverter;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutustyyppiKoosteV1RDTO;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenLiitteetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenLiitteetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenValintakokeetHakukohteenTunnisteellaVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKaikkiKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKaikkiKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutustyyppiUrisKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutustyyppiUrisVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeTarjoajanKoulutustenPohjakoulutuksetKysely;
import fi.vm.sade.tarjonta.service.types.HaeTarjoajanKoulutustenPohjakoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeLiiteTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.service.types.ListHakuVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKoulutuksineenKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKoulutuksineenVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohteenLiiteTunnisteellaKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohteenLiiteTunnisteellaVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohteenValintakoeTunnisteellaKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohteenValintakoeTunnisteellaVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusmoduuliKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusmoduuliVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTyyppi;
import fi.vm.sade.tarjonta.service.types.ValintakoeTyyppi;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;

/**
 *
 * @author Tuomas Katva
 */
@Transactional(rollbackFor = Throwable.class, readOnly = true)
@Service("tarjontaPublicService")
public class TarjontaPublicServiceImpl implements TarjontaPublicService {

    protected static final Logger log = LoggerFactory.getLogger(TarjontaPublicServiceImpl.class);
    @Autowired
    private HakuService businessService;

    @Autowired
    private HakuDAO hakuDao;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private ConversionService conversionService;

    @Value("${tarjonta-alkamiskausi-syksy}")
    private String kausiUri;

    @Autowired
    private KoulutusSearchService koulutusSearchService;

    @Autowired
    private HakukohdeSearchService hakukohdeSearchService;

    @Autowired(required = true)
    private OppilaitosKoodiRelations oppilaitosKoodiRelations;

    public TarjontaPublicServiceImpl() {
        super();
    }

    @Override
    public HaeTarjoajanKoulutustenPohjakoulutuksetVastaus haeTarjoajanKoulutustenPohjakoulutukset(@WebParam(partName = "parameters", name = "haeTarjoajanKoulutustenPohjakoulutuksetKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") HaeTarjoajanKoulutustenPohjakoulutuksetKysely parameters) {

        HaeTarjoajanKoulutustenPohjakoulutuksetVastaus vastaus = new HaeTarjoajanKoulutustenPohjakoulutuksetVastaus();

        List<KoulutusmoduuliToteutus> toteutuses = koulutusmoduuliToteutusDAO.findKoulutusModuuliWithPohjakoulutusAndTarjoaja(parameters.getTarjoaja(), parameters.getPohjakoulutus(),
                parameters.getKoulutusluokitusKoodi(), parameters.getKoulutusOhjelmaKoodi(), parameters.getOpetuskielis(), parameters.getKoulutuslajis());

        List<String> pohjakoulutusKoodis = new ArrayList<String>();

        for (KoulutusmoduuliToteutus komoto : toteutuses) {

            Calendar cal = Calendar.getInstance();
            cal.setTime(komoto.getKoulutuksenAlkamisPvm());
            if ((cal.get(Calendar.YEAR) == parameters.getVuosi())) {
                List<Integer> kausiMonths = getAlkuKuukaudet(parameters.getKausi());
                int month = cal.get(Calendar.MONTH);
                month++;
                if (kausiMonths.contains(new Integer(month))) {
                    pohjakoulutusKoodis.add(komoto.getPohjakoulutusvaatimusUri());
                }

            }

        }

        vastaus.getPohjakoulutusKoodi().addAll(pohjakoulutusKoodis);

        return vastaus;
    }

    @Override
    public LueHakukohteenValintakoeTunnisteellaVastausTyyppi lueHakukohteenValintakoeTunnisteella(@WebParam(partName = "parameters", name = "lueHakukohteenValintakoeTunnisteellaKyselyTyyppi", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") LueHakukohteenValintakoeTunnisteellaKyselyTyyppi parameters) {
        LueHakukohteenValintakoeTunnisteellaVastausTyyppi vastaus = new LueHakukohteenValintakoeTunnisteellaVastausTyyppi();

        Valintakoe valintakoe = hakukohdeDAO.findValintaKoeById(parameters.getHakukohteenValintakoeTunniste());

        vastaus.setHakukohdeValintakoe(conversionService.convert(valintakoe, ValintakoeTyyppi.class));

        return vastaus;
    }

    @Override
    public HaeHakukohteenValintakokeetHakukohteenTunnisteellaVastausTyyppi haeHakukohteenValintakokeetHakukohteenTunnisteella(@WebParam(partName = "parameters", name = "haeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi parameters) {
        HaeHakukohteenValintakokeetHakukohteenTunnisteellaVastausTyyppi vastaus = new HaeHakukohteenValintakokeetHakukohteenTunnisteellaVastausTyyppi();

        List<Valintakoe> valintakoes = hakukohdeDAO.findValintakoeByHakukohdeOid(parameters.getHakukohteenTunniste());
        for (Valintakoe valintakoe : valintakoes) {
            vastaus.getHakukohteenValintaKokeet().add(conversionService.convert(valintakoe, ValintakoeTyyppi.class));
        }

        return vastaus;
    }

    private ListHakuSearchParam convertWsParamToDaoParam(ListaaHakuTyyppi parameters) {
        ListHakuSearchParam daoParam = new ListHakuSearchParam();
        // Convert Enums from API enum to DB enum
        fi.vm.sade.tarjonta.shared.types.TarjontaTila dbTarjontaTila = null;
        if (parameters.getTila() != null) {
            dbTarjontaTila = fi.vm.sade.tarjonta.shared.types.TarjontaTila.valueOf(parameters.getTila().value());
        }
        daoParam.setTila(dbTarjontaTila);
        daoParam.setKoulutuksenAlkamisKausi(parameters.getKoulutuksenAlkamisKausi());
        daoParam.setKoulutuksenAlkamisVuosi(parameters.getKoulutuksenAlkamisVuosi());

        return daoParam;

    }

    @Override
    public ListHakuVastausTyyppi listHaku(ListaaHakuTyyppi parameters) {
        ListHakuVastausTyyppi hakuVastaus = new ListHakuVastausTyyppi();
        if (parameters.getHakuOid() != null) {
            List<Haku> haut = new ArrayList<Haku>();
            Haku findHakuWithOid = hakuDao.findByOid(parameters.getHakuOid());
            haut.add(findHakuWithOid);
            hakuVastaus.getResponse().addAll(convert(haut, false));
        } else if (parameters.getHakuSana() != null || parameters.getKoulutuksenAlkamisVuosi() != null || parameters.getKoulutuksenAlkamisKausi() != null || parameters.getTila() != null) {
            List<Haku> hakus = null;
            try {
                hakus = hakuDao.findBySearchCriteria(convertWsParamToDaoParam(parameters));
            } catch (Exception exp) {
                exp.printStackTrace();
                log.error("Error querying listHaku : " + exp.toString());
            }
            if (parameters.getHakuSana() != null && parameters.getHakuSana().trim().length() > 0) {
                List<Haku> filteredHakus = filterByHakusana(parameters.getHakuSana(), parameters.getHakuSanaKielikoodi(), hakus);
                hakuVastaus.getResponse().addAll(convert(filteredHakus, false));
            } else {
                hakuVastaus.getResponse().addAll(convert(hakus, false));
            }

        } else {
            hakuVastaus.getResponse().addAll(convert(hakuDao.findAll(), false));
        }


        /*
         else if (parameters.getKoulutuksenAlkamisKausi() != null && parameters.getKoulutuksenAlkamisVuosi() != null) {
         List<Haku> foundHaut = hakuDao.findByKoulutuksenKausi(parameters.getKoulutuksenAlkamisKausi(), parameters.getKoulutuksenAlkamisVuosi());
         hakuVastaus.getResponse().addAll(convert(foundHaut, false));
         } else if (parameters.getHakuSana() != null && !parameters.getHakuSana().isEmpty()) {
         //REMOVING FIND BY SEARCH STRING QUERY FOR NOW, NOT WORKING PROPERLY
         //List<Haku> haut = new ArrayList<Haku>();
         //haut.addAll(hakuDao.findBySearchString(parameters.getHakuSana(), parameters.getHakuSanaKielikoodi()));
         String hakusana = parameters.getHakuSana().toLowerCase();
         List<Haku> haut = new ArrayList<Haku>();
         SearchCriteriaType allCriteria = new SearchCriteriaType();
         allCriteria.setMeneillaan(true);
         allCriteria.setPaattyneet(true);
         allCriteria.setTulevat(true);
         haut.addAll(businessService.findAll(allCriteria));
         hakuVastaus.getResponse().addAll(convert(filterByHakusana(hakusana, parameters.getHakuSanaKielikoodi(), haut), true));
         } else {
         SearchCriteriaType allCriteria = new SearchCriteriaType();
         allCriteria.setMeneillaan(true);
         allCriteria.setPaattyneet(true);
         allCriteria.setTulevat(true);
         hakuVastaus.getResponse().addAll(convert(businessService.findAll(allCriteria), false));
         } */
        return hakuVastaus;
    }

    private List<Haku> filterByHakusana(String hakusana, String kielikoodi, List<Haku> fullList) {
        List<Haku> filteredList = new ArrayList<Haku>();
        for (Haku curHaku : fullList) {
            if (hakusanaMatches(curHaku, hakusana, kielikoodi)) {
                filteredList.add(curHaku);
            }
        }
        return filteredList;
    }

    private boolean hakusanaMatches(Haku haku, String hakusana, String kielikoodi) {
        boolean otherLanguageMatch = false;
        for (TekstiKaannos curKaannos : haku.getNimi().getTekstiKaannos()) {
            if (kielikoodi.equals(curKaannos.getKieliKoodi())
                    && (curKaannos.getArvo() != null)
                    && curKaannos.getArvo().toLowerCase().contains(hakusana.toLowerCase())) {
                return true;
            }
            if ((curKaannos.getArvo() != null)
                    && curKaannos.getArvo().toLowerCase().contains(hakusana.toLowerCase())) {
                otherLanguageMatch = true;
            }
        }
        return otherLanguageMatch;
    }

    @Override
    public HaeHakukohteenLiitteetVastausTyyppi lueHakukohteenLiitteet(@WebParam(partName = "parameters", name = "haeHakukohteenLiitteetKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") HaeHakukohteenLiitteetKyselyTyyppi parameters) {
        //long t = System.currentTimeMillis();
        HaeHakukohteenLiitteetVastausTyyppi vastaus = new HaeHakukohteenLiitteetVastausTyyppi();
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(parameters.getHakukohdeOid());

        ArrayList<HakukohdeLiiteTyyppi> liiteTyyppis = new ArrayList<HakukohdeLiiteTyyppi>();

        for (HakukohdeLiite hakukohdeLiite : hakukohde.getLiites()) {
            liiteTyyppis.add(conversionService.convert(hakukohdeLiite, HakukohdeLiiteTyyppi.class));
        }

        vastaus.getHakukohteenLiitteet().addAll(liiteTyyppis);
        return vastaus;
    }

    @Override
    public LueHakukohteenLiiteTunnisteellaVastausTyyppi lueHakukohteenLiiteTunnisteella(@WebParam(partName = "parameters", name = "lueHakukohteenLiiteTunnisteellaKyselyTyyppi", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") LueHakukohteenLiiteTunnisteellaKyselyTyyppi parameters) {
        HakukohdeLiite hakukohdeLiite = hakukohdeDAO.findHakuKohdeLiiteById(parameters.getHakukohteenLiiteTunniste());
        LueHakukohteenLiiteTunnisteellaVastausTyyppi vastaus = new LueHakukohteenLiiteTunnisteellaVastausTyyppi();
        HakukohdeLiiteTyyppi hakukohdeLiiteTyyppi = conversionService.convert(hakukohdeLiite, HakukohdeLiiteTyyppi.class);
        vastaus.setHakukohteenLiite(hakukohdeLiiteTyyppi);
        return vastaus;
    }

    @Override
    public TarjontaTyyppi haeTarjonta(String oid) {
        TarjontaTyyppi vastaus = new TarjontaTyyppi();
        Haku haku = hakuDao.findByOid(oid);
        vastaus.setHaku(conversionService.convert(haku, HakuTyyppi.class));
        for (Hakukohde hk : haku.getHakukohdes()) {
            vastaus.getHakukohde().add(conversionService.convert(hk, HakukohdeTyyppi.class));

        }
        return vastaus;
    }

    private List<HakuTyyppi> convert(List<Haku> haut, boolean eagerFetchHakukohtees) {
        List<HakuTyyppi> tyypit = new ArrayList<HakuTyyppi>();
        for (Haku haku : haut) {
            HakuTyyppi convert = conversionService.convert(haku, HakuTyyppi.class);

            if (eagerFetchHakukohtees) {
                /*
                 * haku.getHakukohdes:
                 * This will be time-consuming eager fetch operation. Not recommended. 
                 */
                convert.getHakukohteet().addAll(HakukohdeSetToDTOConverter.convert(haku.getHakukohdes()));
            }

            tyypit.add(convert);
        }
        return tyypit;
    }

    /**
     * @return the businessService
     */
    public HakuService getBusinessService() {
        return businessService;
    }

    /**
     * @param businessService the businessService to setv
     */
    public void setBusinessService(HakuService businessService) {
        this.businessService = businessService;
    }

    /**
     * @return the conversionService
     */
    public ConversionService getConversionService() {
        return conversionService;
    }

    /**
     * @param conversionService the conversionService to set
     */
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * @return the hakuDao
     */
    public HakuDAO getHakuDao() {
        return hakuDao;
    }

    /**
     * @param hakuDao the hakuDao to set
     */
    public void setHakuDao(HakuDAO hakuDao) {
        this.hakuDao = hakuDao;
    }

//    @Override
//    public HaeHakukohteetVastausTyyppi haeHakukohteet(HaeHakukohteetKyselyTyyppi kysely) {
//        HaeHakukohteetVastausTyyppi vastaus = this.koulutusSearchService.haeHakukohteet(kysely);
//        return vastaus;
//    }
    @Override
    public LueHakukohdeKoulutuksineenVastausTyyppi lueHakukohdeKoulutuksineen(@WebParam(partName = "hakukohdeKysely", name = "LueHakukohdeKoulutuksineenKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") LueHakukohdeKoulutuksineenKyselyTyyppi hakukohdeKysely) {
        HakukohdeTyyppi hakukohdeTyyppi = new HakukohdeTyyppi();
        hakukohdeTyyppi.setOid(hakukohdeKysely.getHakukohdeOid());

        KoulutuksetKysely kysely = new KoulutuksetKysely();
        kysely.getHakukohdeOids().add(hakukohdeKysely.getHakukohdeOid());

        KoulutuksetVastaus koulutusVastaus = koulutusSearchService.haeKoulutukset(kysely);

        for (KoulutusPerustieto tulos : koulutusVastaus.getKoulutukset()) {

            KoulutusKoosteTyyppi koulutus = new KoulutusKoosteTyyppi();
            koulutus.setTila(tulos.getTila());
            koulutus.setKomotoOid(tulos.getKomotoOid());
            koulutus.setKoulutustyyppi(tulos.getKoulutusasteTyyppi());
            koulutus.setAjankohta(tulos.getKoulutuksenAlkamiskausi().getNimi().get("fi") + " " + tulos.getKoulutuksenAlkamisVuosi());
            if (tulos.getKoulutusohjelma() != null) {
                koulutus.setKoulutusohjelmakoodi(tulos.getKoulutusohjelma().getUri());
            }

            koulutus.setKoulutuskoodi(tulos.getKoulutusKoodi().getUri());

            hakukohdeTyyppi.getHakukohdeKoulutukses().add(koulutus);
        }

        LueHakukohdeKoulutuksineenVastausTyyppi vastaus = new LueHakukohdeKoulutuksineenVastausTyyppi();

        vastaus.setHakukohde(hakukohdeTyyppi);

        return vastaus;

    }

    private List<Integer> getAlkuKuukaudet(String kausi) {
        List<Integer> kuukaudet = new ArrayList<Integer>();
        if (kausi != null && kausi.contains(kausiUri)) {
            kuukaudet.add(7);
            kuukaudet.add(8);
            kuukaudet.add(9);
            kuukaudet.add(10);
            kuukaudet.add(11);
            kuukaudet.add(12);
        } else if (kausi != null) {
            kuukaudet.add(1);
            kuukaudet.add(2);
            kuukaudet.add(3);
            kuukaudet.add(4);
            kuukaudet.add(5);
            kuukaudet.add(6);
        }

        return kuukaudet;
    }

    @Override
    public LueKoulutusVastausTyyppi lueKoulutus(LueKoulutusKyselyTyyppi kysely) {
        //long t = System.currentTimeMillis();
        log.debug("in LueKoulutusVastausTyyppi");
        KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(kysely.getOid());

        LueKoulutusVastausTyyppi result = convert(komoto);

        KoodistoKoodiTyyppi koulutusKoodi = new KoodistoKoodiTyyppi();
        koulutusKoodi.setUri(komoto.getKoulutusmoduuli().getKoulutusUri());
        result.setKoulutusKoodi(koulutusKoodi);

        KoodistoKoodiTyyppi koulutusOhjelmaKoodi = new KoodistoKoodiTyyppi();
        koulutusOhjelmaKoodi.setUri(komoto.getKoulutusmoduuli().getKoulutusohjelmaUri());
        result.setKoulutusohjelmaKoodi(koulutusOhjelmaKoodi);

        KoodistoKoodiTyyppi lukiolinja = new KoodistoKoodiTyyppi();
        lukiolinja.setUri(komoto.getKoulutusmoduuli().getLukiolinjaUri());
        result.setLukiolinjaKoodi(lukiolinja);

        //Asetetaan koulutusmoduuli
        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
        result.setKoulutustyyppi(komo.getKoulutustyyppiEnum().getKoulutusasteTyyppi());
        //result.setKoulutusasteTyyppi(komo.getKoulutusasteTyyppi());

        Koulutusmoduuli parentKomo = this.koulutusmoduuliDAO.findParentKomo(komo);

        //if parent komo does not exist, we are reading a parent komoto, thus the koulutusohjelmanvalinta field is in the komoto itself
        log.debug("parent : " + parentKomo);
        if (parentKomo == null) {
            result.setKoulutusmoduuli(EntityUtils.copyFieldsToKoulutusmoduuliKoosteTyyppi(komo));
            EntityUtils.copyFields(result.getTekstit(), komoto.getTekstit(), KomotoTeksti.KOULUTUSOHJELMAN_VALINTA);
            //result.setKoulutusohjelmanValinta(EntityUtils.copyFields(komoto.getKoulutusohjelmanValinta()));

            if (result.getKoulutusmoduuli().getNimi() != null && !result.getKoulutusmoduuli().getNimi().getTeksti().isEmpty()) {
                log.debug("child name : " + result.getKoulutusmoduuli().getNimi().getTeksti().size());
            } else {
                log.debug("no child name ");
            }
            //if parent komo exists we read the koulutusohjelmanValinta field from the parent (tutkinto) komoto,
            //and merging the parent and actual komo to get the komo fields.
        } else {
            result.setKoulutusmoduuli(EntityUtils.copyFieldsToKoulutusmoduuliKoosteTyyppi(komo, parentKomo));
            handleParentKomoto(parentKomo, komoto, result);
        }

        //override komo relation data with the latest koodi uris.
        EntityUtils.copyKomoRelationsToKomotoDto(komoto, result);

        return result;
    }

    /*
     * reading the parent komoto fields to the result dto. 
     */
    private void handleParentKomoto(Koulutusmoduuli parentKomo, KoulutusmoduuliToteutus komoto, LueKoulutusVastausTyyppi result) {
        List<KoulutusmoduuliToteutus> parentList = this.koulutusmoduuliToteutusDAO.findKomotosByKomoTarjoajaPohjakoulutus(parentKomo, komoto.getTarjoaja(), komoto.getPohjakoulutusvaatimusUri());
        KoulutusmoduuliToteutus parentKomoto = (parentList != null && !parentList.isEmpty()) ? parentList.get(0) : null;
        if (parentKomoto != null) {
            //alkamispaiv no longer in parent
            /*GregorianCalendar greg = new GregorianCalendar();
             greg.setTime(parentKomoto.getKoulutuksenAlkamisPvm());
             try {
             result.setKoulutuksenAlkamisPaiva(DatatypeFactory.newInstance().newXMLGregorianCalendar(greg));
             } catch (Exception ex) {
             result.setKoulutuksenAlkamisPaiva(null);
             }*/
            EntityUtils.copyFields(result.getTekstit(), parentKomoto.getTekstit(), KomotoTeksti.KOULUTUSOHJELMAN_VALINTA);
            //result.setKoulutusohjelmanValinta(EntityUtils.copyFields(parentKomoto.getKoulutusohjelmanValinta()));
        }
    }

    private String getHakukohdeTulosKoodistoNimi(HakukohdePerustieto hakukohde) {
        String koodistoNimi = null;

        //TODO, miksi haetaan suomi???
        koodistoNimi = hakukohde.getNimi("fi");
        if (koodistoNimi != null) {
            koodistoNimi = koodistoNimi + ", " + hakukohde.getTila().value();
        }

        return koodistoNimi;
    }

    private LueKoulutusVastausTyyppi convert(KoulutusmoduuliToteutus fromKoulutus) {
        log.debug("in convert ");
        LueKoulutusVastausTyyppi toKoulutus = new LueKoulutusVastausTyyppi();
        toKoulutus.setVersion(fromKoulutus.getVersion());
        toKoulutus.setTila(EntityUtils.convertTila(fromKoulutus.getTila()));

        HakukohteetKysely kysely = new HakukohteetKysely();
        kysely.getKoulutusOids().add(fromKoulutus.getOid());
        kysely.setKoulutuksenAlkamisvuosi(0);
        HakukohteetVastaus vastaus = hakukohdeSearchService.haeHakukohteet(kysely);
        if (fromKoulutus.getHakukohdes() != null) {
            for (HakukohdePerustieto hakukohde : vastaus.getHakukohteet()) {
                HakukohdeKoosteTyyppi hakukohdeKoosteTyyppi = new HakukohdeKoosteTyyppi();
                hakukohdeKoosteTyyppi.setOid(hakukohde.getOid());
                hakukohdeKoosteTyyppi.setKoodistoNimi(getHakukohdeTulosKoodistoNimi(hakukohde));
                hakukohdeKoosteTyyppi.setNimi(hakukohde.getKoodistoNimi());
                hakukohdeKoosteTyyppi.setTila(fi.vm.sade.tarjonta.service.types.TarjontaTila.fromValue(hakukohde.getTila().name()));
                toKoulutus.getHakukohteet().add(hakukohdeKoosteTyyppi);
            }
        }

        toKoulutus.setViimeisinPaivittajaOid(fromKoulutus.getLastUpdatedByOid());
        toKoulutus.setViimeisinPaivitysPvm(fromKoulutus.getUpdated());

        toKoulutus.setOid(fromKoulutus.getOid());
        GregorianCalendar greg = new GregorianCalendar();
        greg.setTime(fromKoulutus.getKoulutuksenAlkamisPvm());
        try {
            toKoulutus.setKoulutuksenAlkamisPaiva(greg.getTime());
        } catch (Exception ex) {
            toKoulutus.setKoulutuksenAlkamisPaiva(null);
        }
        KoulutuksenKestoTyyppi kestoT = new KoulutuksenKestoTyyppi();
        kestoT.setArvo(fromKoulutus.getSuunniteltukestoArvo());
        kestoT.setYksikko(fromKoulutus.getSuunniteltukestoYksikkoUri());
        toKoulutus.setKesto(kestoT);

        if (fromKoulutus.getOpintojenLaajuusArvo() != null) {
            KoulutuksenKestoTyyppi laajuusT = new KoulutuksenKestoTyyppi();
            laajuusT.setArvo(fromKoulutus.getOpintojenLaajuusArvo());
            laajuusT.setYksikko(fromKoulutus.getOpintojenLaajuusyksikkoUri());
            toKoulutus.setLaajuus(laajuusT);
        }

        if (fromKoulutus.getKoulutusasteUri() != null) {
            KoodistoKoodiTyyppi koulutusaste = new KoodistoKoodiTyyppi();
            koulutusaste.setUri(fromKoulutus.getKoulutusasteUri());
            toKoulutus.setKoulutusaste(koulutusaste);
        }

        if (fromKoulutus.getPohjakoulutusvaatimusUri() != null) {
            KoodistoKoodiTyyppi pohjakoulutusvaatimus = new KoodistoKoodiTyyppi();
            pohjakoulutusvaatimus.setUri(fromKoulutus.getPohjakoulutusvaatimusUri());
            toKoulutus.setPohjakoulutusvaatimus(pohjakoulutusvaatimus);
        }

        toKoulutus.setTarjoaja(fromKoulutus.getTarjoaja());

        EntityUtils.copyKoodistoUris(fromKoulutus.getOpetusmuotos(), toKoulutus.getOpetusmuoto());
        EntityUtils.copyKoodistoUris(fromKoulutus.getOpetuskielis(), toKoulutus.getOpetuskieli());
        EntityUtils.copyKoodistoUris(fromKoulutus.getKoulutuslajis(), toKoulutus.getKoulutuslaji());
        EntityUtils.copyWebLinkkis(fromKoulutus.getLinkkis(), toKoulutus.getLinkki());
        EntityUtils.copyYhteyshenkilos(fromKoulutus.getYhteyshenkilos(), toKoulutus.getYhteyshenkiloTyyppi());
        //
        // Koulutus lisätiedot / additional information for Koulutus
        //
        EntityUtils.copyKoodistoUris(fromKoulutus.getAmmattinimikes(), toKoulutus.getAmmattinimikkeet());

        EntityUtils.copyFields(toKoulutus.getTekstit(), fromKoulutus.getTekstit()); // TODO rajaus?

        /*toKoulutus.setPainotus(EntityUtils.copyFields(fromKoulutus.getPainotus()));
         toKoulutus.setKuvailevatTiedot(EntityUtils.copyFields(fromKoulutus.getKuvailevatTiedot()));
         toKoulutus.setSisalto(EntityUtils.copyFields(fromKoulutus.getSisalto()));
         toKoulutus.setSijoittuminenTyoelamaan(EntityUtils.copyFields(fromKoulutus.getSijoittuminenTyoelamaan()));
         toKoulutus.setKansainvalistyminen(EntityUtils.copyFields(fromKoulutus.getKansainvalistyminen()));
         toKoulutus.setYhteistyoMuidenToimijoidenKanssa(EntityUtils.copyFields(fromKoulutus.getYhteistyoMuidenToimijoidenKanssa()));
         */
        toKoulutus.getA1A2Kieli().addAll(EntityUtils.copyFields(fromKoulutus.getTarjotutKielet().values(), Kieliaine.A1A2KIELI));
        toKoulutus.getB1Kieli().addAll(EntityUtils.copyFields(fromKoulutus.getTarjotutKielet().values(), Kieliaine.B1KIELI));
        toKoulutus.getB2Kieli().addAll(EntityUtils.copyFields(fromKoulutus.getTarjotutKielet().values(), Kieliaine.B2KIELI));
        toKoulutus.getB3Kieli().addAll(EntityUtils.copyFields(fromKoulutus.getTarjotutKielet().values(), Kieliaine.B3KIELI));
        toKoulutus.getMuutKielet().addAll(EntityUtils.copyFields(fromKoulutus.getTarjotutKielet().values(), Kieliaine.MUUT_KIELET));

        EntityUtils.copyKoodistoUris(fromKoulutus.getLukiodiplomit(), toKoulutus.getLukiodiplomit());

        //Korkeakoulu
        EntityUtils.copyKoodistoUris(fromKoulutus.getTeemas(), toKoulutus.getTeemat());
        EntityUtils.copyKoodistoUris(fromKoulutus.getKkPohjakoulutusvaatimus(), toKoulutus.getPohjakoulutusvaatimusKorkeakoulu());
        if (fromKoulutus.getHinta() != null) {
            toKoulutus.setHinta(fromKoulutus.getHinta().toString());
        }
        toKoulutus.setMaksullisuus(false); //todo

        MonikielinenTeksti nimi = fromKoulutus.getNimi();
        if (nimi != null && nimi.getTekstiKaannos().size() > 0) {
            TekstiKaannos tk = nimi.getKaannoksetAsList().get(0);
            toKoulutus.setNimi(new MonikielinenTekstiTyyppi(Lists.newArrayList(new MonikielinenTekstiTyyppi.Teksti(tk.getArvo(), tk.getKieliKoodi()))));
        }

        EntityUtils.copyKomoRelationsToKomotoDto(fromKoulutus, toKoulutus);

        return toKoulutus;
    }

    @Override
    public LueHakukohdeVastausTyyppi lueHakukohde(LueHakukohdeKyselyTyyppi kysely) {
        Preconditions.checkNotNull(kysely, "LueHakukohdeKyselyTyyppi object cannot be null.");
        Preconditions.checkNotNull(kysely.getOid(), "Hakukohde OID cannot be null.");

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(kysely.getOid());
        HakukohdeTyyppi hakukohdeTyyppi = conversionService.convert(hakukohde, HakukohdeTyyppi.class);
        if (hakukohde.getHaku() != null) {
            hakukohdeTyyppi.setHakukohteenHaunNimi(mapMonikielinenTekstiToTyyppi(hakukohde.getHaku().getNimi()));
        }

        LueHakukohdeVastausTyyppi vastaus = new LueHakukohdeVastausTyyppi();
        vastaus.setHakukohde(hakukohdeTyyppi);
        return vastaus;
    }
    //TODO: these helper methods implemented in CommonFrom/To Converters

    private MonikielinenTekstiTyyppi mapMonikielinenTekstiToTyyppi(MonikielinenTeksti monikielinenTeksti) {
        MonikielinenTekstiTyyppi monikielinenTekstiTyyppi = new MonikielinenTekstiTyyppi();
        for (TekstiKaannos tekstiKaannos : monikielinenTeksti.getTekstiKaannos()) {

            MonikielinenTekstiTyyppi.Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
            teksti.setKieliKoodi(tekstiKaannos.getKieliKoodi());
            teksti.setValue(tekstiKaannos.getArvo());
            monikielinenTekstiTyyppi.getTeksti().add(teksti);
        }

        return monikielinenTekstiTyyppi;
    }

    @Override
    public HaeKoulutusmoduulitVastausTyyppi haeKoulutusmoduulit(HaeKoulutusmoduulitKyselyTyyppi kysely) {
        SearchCriteria criteria = new SearchCriteria();

        if (kysely.getKoulutuskoodiUri() != null) {
            //version information will be removed in search method from koodi uris.
            criteria.setLikeKoulutusKoodiUriWithoutVersion(TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(kysely.getKoulutuskoodiUri()));
        }

        if (kysely.getKoulutusohjelmakoodiUri() != null) {
            //version information will be removed in search method from koodi uris.
            criteria.setLikeKoulutusohjelmaKoodiUriWithoutVersion(TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(kysely.getKoulutusohjelmakoodiUri()));
        }

        if (kysely.getLukiolinjakoodiUri() != null) {
            //version information will be removed in search method from koodi uris.
            criteria.setLikeLukiolinjaKoodiUriUriWithoutVersion(TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(kysely.getLukiolinjakoodiUri()));
        }

        if (kysely.getKoulutustyyppi() != null) {
            criteria.setModuulityyppi(ModuulityyppiEnum.fromEnum(kysely.getKoulutustyyppi()));
        }

        if (kysely.getHakusana() != null) {
            //search by search word
            criteria.setNimiQuery(kysely.getHakusana().getHakusana());
            criteria.setKieliUri(kysely.getHakusana().getKieliUri());
        }

        criteria.setTarjoajaOids(kysely.getTarjoajaOids());

        HaeKoulutusmoduulitVastausTyyppi vastaus = new HaeKoulutusmoduulitVastausTyyppi();
        for (Koulutusmoduuli curKomo : this.koulutusmoduuliDAO.search(criteria)) {
            KoulutusmoduuliTulos tulos = new KoulutusmoduuliTulos();

            final String childLaajuusarvoUri = curKomo.getOpintojenLaajuusarvoUri();

            Koulutusmoduuli findParentKomo = this.koulutusmoduuliDAO.findParentKomo(curKomo);
            KoulutusmoduuliKoosteTyyppi kooste = EntityUtils.copyFieldsToKoulutusmoduuliKoosteTyyppiSimple(curKomo); //no description data.
            if (findParentKomo != null) {
                kooste.setParentOid(findParentKomo.getOid());
            }

            if (childLaajuusarvoUri != null && !childLaajuusarvoUri.isEmpty()) {
                //if a child komo has a laajuus uri, then override parent uri with it. 
                kooste.setLaajuusarvoUri(childLaajuusarvoUri);
            }
            kooste.setKoulutusmoduulinNimi(EntityUtils.copyFields(curKomo.getNimi()));
            tulos.setKoulutusmoduuli(kooste);
            vastaus.getKoulutusmoduuliTulos().add(tulos);
        }
        return vastaus;
    }

    @Override
    public HaeKaikkiKoulutusmoduulitVastausTyyppi haeKaikkiKoulutusmoduulit(HaeKaikkiKoulutusmoduulitKyselyTyyppi kysely) {

        HaeKaikkiKoulutusmoduulitVastausTyyppi vastaus = new HaeKaikkiKoulutusmoduulitVastausTyyppi();

        SearchCriteria criteria = new SearchCriteria();
        criteria.setKoulutusKoodi(kysely.getKoulutuskoodiUri());
        criteria.setKoulutusohjelmaKoodi(kysely.getKoulutusohjelmakoodiUri());
        criteria.setLukiolinjaKoodiUri(kysely.getLukiolinjakoodiUri());
        if (kysely.getKoulutustyyppi() != null) {
            criteria.setModuulityyppi(ModuulityyppiEnum.fromEnum(kysely.getKoulutustyyppi()));
        }
        criteria.setOppilaitostyyppis(kysely.getOppilaitostyyppiUris());

        for (Koulutusmoduuli curKomo : this.koulutusmoduuliDAO.search(criteria)) {

            if (!curKomo.getAlamoduuliList().isEmpty()) {
                addChildModulesToVastaus(curKomo, vastaus.getKoulutusmoduuliTulos());
            }
        }

        return vastaus;
    }

    private void addChildModulesToVastaus(Koulutusmoduuli parentKomo, List<KoulutusmoduuliTulos> resultList) {
        for (Koulutusmoduuli curKomo : parentKomo.getAlamoduuliList()) {
            KoulutusmoduuliKoosteTyyppi komo = EntityUtils.copyFieldsToKoulutusmoduuliKoosteTyyppi(curKomo, parentKomo);
            if (!containsKomo(resultList, komo.getOid())) {
                KoulutusmoduuliTulos koulutusmoduuliTulos = new KoulutusmoduuliTulos();
                koulutusmoduuliTulos.setKoulutusmoduuli(komo);
                resultList.add(koulutusmoduuliTulos);
            }
        }
    }

    private boolean containsKomo(List<KoulutusmoduuliTulos> resultList, String komoOid) {
        for (KoulutusmoduuliTulos curTulos : resultList) {
            if (curTulos.getKoulutusmoduuli().getOid().equals(komoOid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public LueKoulutusmoduuliVastausTyyppi lueKoulutusmoduuli(LueKoulutusmoduuliKyselyTyyppi kysely) {
        Preconditions.checkNotNull(kysely, "LueKoulutusmoduuliKyselyTyyppi object cannot be null");
        Preconditions.checkNotNull(kysely.getOid(), "KOMO OID cannot be null");
        LueKoulutusmoduuliVastausTyyppi komo = new LueKoulutusmoduuliVastausTyyppi();
        komo.setKoulutusmoduuli(EntityUtils.copyFieldsToKoulutusmoduuliKoosteTyyppi(koulutusmoduuliDAO.findByOid(kysely.getOid())));
        return komo;
    }

    @Override
    public HaeKoulutustyyppiUrisVastausTyyppi haeKoulutustyyppiUris(HaeKoulutustyyppiUrisKyselyTyyppi kysely) {
        Preconditions.checkNotNull(kysely.getOrganisaatioOid(), "KOMO OID cannot be null");

        KoulutusmoduuliDAO.SearchCriteria searchCriteria = new KoulutusmoduuliDAO.SearchCriteria();
        searchCriteria.setKoulutustyyppiUris(oppilaitosKoodiRelations.getKoulutustyyppiUris(kysely.getOrganisaatioOid()));
        List<Koulutusmoduuli> search = koulutusmoduuliDAO.search(new KoulutusmoduuliDAO.SearchCriteria());
        KoulutustyyppiKoosteV1RDTO k = new KoulutustyyppiKoosteV1RDTO();
        k.setKoulutustyyppiUris(searchCriteria.getKoulutustyyppiUris());
        k.setModules(!search.isEmpty());

        HaeKoulutustyyppiUrisVastausTyyppi vastaus = new HaeKoulutustyyppiUrisVastausTyyppi();
        vastaus.setOrganisaatioOid(kysely.getOrganisaatioOid());
        vastaus.getKoulutustyyppiUris().addAll(searchCriteria.getKoulutustyyppiUris());
        vastaus.setHasKoulutusmodules(!search.isEmpty());
        return vastaus;
    }
}
