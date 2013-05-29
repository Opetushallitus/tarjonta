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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.jws.WebParam;
import javax.xml.datatype.DatatypeFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO.SearchCriteria;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakuaika;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.Kieliaine;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.business.HakuBusinessService;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.impl.conversion.HakukohdeSetToDTOConverter;
import fi.vm.sade.tarjonta.service.impl.conversion.util.DatatypeHelper;
import fi.vm.sade.tarjonta.service.search.SearchService;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenLiitteetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenLiitteetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenValintakokeetHakukohteenTunnisteellaVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKaikkiKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKaikkiKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeTarjoajanKoulutustenPohjakoulutuksetKysely;
import fi.vm.sade.tarjonta.service.types.HaeTarjoajanKoulutustenPohjakoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeLiiteTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
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
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.SearchCriteriaType;
import fi.vm.sade.tarjonta.service.types.TarjontaTyyppi;
import fi.vm.sade.tarjonta.service.types.ValintakoeTyyppi;

/**
 *
 * @author Tuomas Katva
 */
@Transactional(rollbackFor=Throwable.class, readOnly=true)
@Service("tarjontaPublicService")
public class TarjontaPublicServiceImpl implements TarjontaPublicService {

    protected static final Logger log = LoggerFactory.getLogger(TarjontaPublicServiceImpl.class);
    @Autowired
    private HakuBusinessService businessService;
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
    private SearchService searchService;
    private final static String SYKSY = "syksy";
    private final static String KEVAT = "kevat";

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
                    pohjakoulutusKoodis.add(komoto.getPohjakoulutusvaatimus());
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

    @Override
    public ListHakuVastausTyyppi listHaku(ListaaHakuTyyppi parameters) {
        ListHakuVastausTyyppi hakuVastaus = new ListHakuVastausTyyppi();
        if (parameters.getHakuOid() != null) {
            List<Haku> haut = new ArrayList<Haku>();
            Haku findHakuWithOid = hakuDao.findByOid(parameters.getHakuOid());
            haut.add(findHakuWithOid);
            hakuVastaus.getResponse().addAll(convert(haut, false));
        }  else if (parameters.getKoulutuksenAlkamisKausi() != null && parameters.getKoulutuksenAlkamisVuosi() != null) {
            List<Haku> foundHaut = hakuDao.findByKoulutuksenKausi(parameters.getKoulutuksenAlkamisKausi(), parameters.getKoulutuksenAlkamisVuosi());
            hakuVastaus.getResponse().addAll(convert(foundHaut,false));
        }

        else if (parameters.getHakuSana() != null && !parameters.getHakuSana().isEmpty()) {
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
        }

        else {
            SearchCriteriaType allCriteria = new SearchCriteriaType();
            allCriteria.setMeneillaan(true);
            allCriteria.setPaattyneet(true);
            allCriteria.setTulevat(true);
            hakuVastaus.getResponse().addAll(convert(businessService.findAll(allCriteria), false));
        }
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
        for (TekstiKaannos curKaannos : haku.getNimi().getTekstis()) {
            if (kielikoodi.equals(curKaannos.getKieliKoodi())
                    && (curKaannos.getArvo() != null)
                    && curKaannos.getArvo().contains(hakusana)) {
                return true;
            }
            if ((curKaannos.getArvo() != null)
                    && curKaannos.getArvo().toLowerCase().contains(hakusana)) {
                otherLanguageMatch = true;
            }
        }
        return otherLanguageMatch;
    }

    @Override
    public HaeHakukohteenLiitteetVastausTyyppi lueHakukohteenLiitteet(@WebParam(partName = "parameters", name = "haeHakukohteenLiitteetKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") HaeHakukohteenLiitteetKyselyTyyppi parameters) {
    	//long t = System.currentTimeMillis();
    	HaeHakukohteenLiitteetVastausTyyppi vastaus = new HaeHakukohteenLiitteetVastausTyyppi();
    	Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithDepenciesByOid(parameters.getHakukohdeOid());

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
    public HakuBusinessService getBusinessService() {
        return businessService;
    }

    /**
     * @param businessService the businessService to setv
     */
    public void setBusinessService(HakuBusinessService businessService) {
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

    @Override
    public HaeHakukohteetVastausTyyppi haeHakukohteet(HaeHakukohteetKyselyTyyppi kysely) {
        HaeHakukohteetVastausTyyppi vastaus = this.searchService.haeHakukohteet(kysely);
//        if (vastaus != null && vastaus.getHakukohdeTulos() != null) {
//            for (HakukohdeTulos tulos : vastaus.getHakukohdeTulos()) {
//                HaeKoulutuksetKyselyTyyppi koulutusKysely = new HaeKoulutuksetKyselyTyyppi();
//                koulutusKysely.getHakukohdeOids().add(tulos.getHakukohde().getOid());
//                HaeKoulutuksetVastausTyyppi koulutusVastaus = searchService.haeKoulutukset(koulutusKysely);
//                log.info("GOT KOULUTUS : {0}", koulutusVastaus.getKoulutusTulos());
//                if (koulutusVastaus != null && koulutusVastaus.getKoulutusTulos() != null && koulutusVastaus.getKoulutusTulos().size() > 0) {
//                    log.info("GETTING KOULUTUSLAJI : {0}", koulutusVastaus.getKoulutusTulos().get(0).getKoulutus().getKoulutuslaji());
//                    //Get the first koulutuslaji because hakukohde should not have koulutukses with other koulutuslajis attached to it.
//                    tulos.getHakukohde().setHakukohteenKoulutuslaji(koulutusVastaus.getKoulutusTulos().get(0).getKoulutus().getKoulutuslaji());
//                }
//            }
//        }
        return vastaus;

        //return new HaeHakukohteetVastausTyyppi();
        /*
         List<Hakukohde> hakukohteet = hakukohdeDAO.haeHakukohteetJaKoulutukset(kysely);
         HaeHakukohteetVastausTyyppi vastaus = new HaeHakukohteetVastausTyyppi();

         List<HaeHakukohteetVastausTyyppi.HakukohdeTulos> rivit = vastaus.getHakukohdeTulos();

         for (Hakukohde hakukohdeModel : hakukohteet) {

         HakukohdeTulos tulos = new HakukohdeTulos();

         HakukohdeKoosteTyyppi hakukohde = new HakukohdeKoosteTyyppi();
         HakuKoosteTyyppi haku = new HakuKoosteTyyppi();
         KoulutusKoosteTyyppi koulutus = new KoulutusKoosteTyyppi();

         hakukohde.setNimi(hakukohdeModel.getHakukohdeNimi());
         hakukohde.setTila(EntityUtils.convertTila(hakukohdeModel.getTila()));
         hakukohde.setAloituspaikat((hakukohdeModel.getAloituspaikatLkm() != null) ? hakukohdeModel.getAloituspaikatLkm().toString() : "" + 0);
         hakukohde.setOid(hakukohdeModel.getOid());

         Haku hakuModel = hakukohdeModel.getHaku();
         haku.setNimi(hakuModel.getNimiFi());
         haku.setHakutapa(hakuModel.getHakutapaUri());
         haku.setOid(hakuModel.getOid());
         haku.setHakukausiUri(hakuModel.getHakukausiUri());
         haku.setHakuvuosi(hakuModel.getHakukausiVuosi().toString());
         haku.setKoulutuksenAlkamiskausiUri(hakuModel.getKoulutuksenAlkamiskausiUri());
         haku.setKoulutuksenAlkamisvuosi(hakuModel.getKoulutuksenAlkamisVuosi().toString());
         haku.setHakuAlkamisPvm(getStartDate(hakukohdeModel.getHaku().getHakuaikas()));
         haku.setHakuPaattymisPvm(getEndDate(hakukohdeModel.getHaku().getHakuaikas()));

         KoulutusmoduuliToteutus toteutus = CollectionUtils.singleItem(hakukohdeModel.getKoulutusmoduuliToteutuses());
         koulutus.setTarjoaja(toteutus.getTarjoaja());


         tulos.setHakukohde(hakukohde);
         tulos.setHaku(haku);
         tulos.setKoulutus(koulutus);
         rivit.add(tulos);
            

         }

         return vastaus;*/

    }

    private Date getStartDate(Set<Hakuaika> hakuaikas) {
        Date startDate = null;
        for (Hakuaika aika : hakuaikas) {
            if (startDate == null) {
                startDate = aika.getAlkamisPvm();
            } else if (aika.getAlkamisPvm().before(startDate)) {
                startDate = aika.getAlkamisPvm();
            }
        }
        return startDate;
    }

    private Date getEndDate(Set<Hakuaika> hakuaikas) {
        Date endDate = null;
        for (Hakuaika aika : hakuaikas) {
            if (endDate == null) {
                endDate = aika.getPaattymisPvm();
            } else if (aika.getPaattymisPvm().after(endDate)) {
                endDate = aika.getPaattymisPvm();
            }
        }
        return endDate;
    }

    @Override
    public LueHakukohdeKoulutuksineenVastausTyyppi lueHakukohdeKoulutuksineen(@WebParam(partName = "hakukohdeKysely", name = "LueHakukohdeKoulutuksineenKysely", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") LueHakukohdeKoulutuksineenKyselyTyyppi hakukohdeKysely) {
       /* Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(hakukohdeKysely.getHakukohdeOid());
        List<KoulutusmoduuliToteutus> komotos = new ArrayList<KoulutusmoduuliToteutus>();
        if (hakukohde.getKoulutusmoduuliToteutuses() != null) {
            komotos.addAll(hakukohde.getKoulutusmoduuliToteutuses());
        }*/


        HakukohdeTyyppi hakukohdeTyyppi = new HakukohdeTyyppi();
        hakukohdeTyyppi.setOid(hakukohdeKysely.getHakukohdeOid());

        HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
        kysely.getHakukohdeOids().add(hakukohdeKysely.getHakukohdeOid());

        HaeKoulutuksetVastausTyyppi koulutusVastaus = searchService.haeKoulutukset(kysely);

        for (KoulutusTulos tulos : koulutusVastaus.getKoulutusTulos()) {

           KoulutusKoosteTyyppi koulutus = new KoulutusKoosteTyyppi();
           koulutus.setTila(tulos.getKoulutus().getTila());
           koulutus.setKomotoOid(tulos.getKoulutus().getKomotoOid());
           koulutus.setKoulutustyyppi(tulos.getKoulutus().getKoulutustyyppi());
           koulutus.setAjankohta(tulos.getKoulutus().getAjankohta());
           if (tulos.getKoulutus().getKoulutusohjelmakoodi() != null) {
               koulutus.setKoulutusohjelmakoodi(tulos.getKoulutus().getKoulutusohjelmakoodi().getUri());
           } else if (tulos.getKoulutus().getLukiolinjakoodi() != null) {
               koulutus.setLukiolinjakoodi(tulos.getKoulutus().getLukiolinjakoodi().getUri());
           }
           
           koulutus.setKoulutuskoodi(tulos.getKoulutus().getKoulutuskoodi().getUri());

          hakukohdeTyyppi.getHakukohdeKoulutukses().add(koulutus);
        }


        LueHakukohdeKoulutuksineenVastausTyyppi vastaus = new LueHakukohdeKoulutuksineenVastausTyyppi();

        vastaus.setHakukohde(hakukohdeTyyppi);

        return vastaus;

    }

    private List<KoulutusKoosteTyyppi> mapKomotoListToKoulutusKoosteTyyppiList(List<KoulutusmoduuliToteutus> komotos) {
        List<KoulutusKoosteTyyppi> koulutusKoostees = new ArrayList<KoulutusKoosteTyyppi>();
        for (KoulutusmoduuliToteutus komoto : komotos) {
            koulutusKoostees.add(getKoulutusTulosFromKoulutusmoduuliToteutus(komoto));
        }
        return koulutusKoostees;
    }

    @Override
    public HaeKoulutuksetVastausTyyppi haeKoulutukset(HaeKoulutuksetKyselyTyyppi kysely) {

        return this.searchService.haeKoulutukset(kysely);
        /*
         //If a list of oids is provided the komotos matching those oids are returned
         if (kysely.getKoulutusOids() != null && kysely.getKoulutusOids().size() > 0) {
         HaeKoulutuksetVastausTyyppi vastaus = new HaeKoulutuksetVastausTyyppi();

         List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findKoulutusModuuliToteutusesByOids(kysely.getKoulutusOids());
         vastaus.getKoulutusTulos().addAll(mapKomotoListToKoulutusTulosList(komotos));
         return vastaus;
         //If the search specifies a koulutuskoodi and tarjoaja, the komoto matching those is returned.
         } else if (kysely.getKoulutusKoodi() != null && kysely.getTarjoajaOids() != null && !kysely.getTarjoajaOids().isEmpty()) {
         Koulutusmoduuli tutkintoKomo = this.koulutusmoduuliDAO.findTutkintoOhjelma(kysely.getKoulutusKoodi(), null);
         log.debug("TutkintoKomo: {}", (tutkintoKomo != null) ? tutkintoKomo.getOid() : null);
         List<KoulutusmoduuliToteutus> komotoRes = this.koulutusmoduuliToteutusDAO.findKomotosByKomoAndtarjoaja(tutkintoKomo, kysely.getTarjoajaOids().get(0));
         log.debug("komotoRes size: {}", (komotoRes != null) ? komotoRes.size() : 0);
         KoulutusmoduuliToteutus komoto = (komotoRes != null && !komotoRes.isEmpty()) ? komotoRes.get(0) : null;
         log.debug("TutkintoKomoto: {}", (komoto != null) ? komoto.getOid() : null);
         HaeKoulutuksetVastausTyyppi vastaus = new HaeKoulutuksetVastausTyyppi();
         if (komoto != null) {
         KoulutusTulos tulos = getKoulutusTulosFromKoulutusmoduuliToteutus(komoto);
         vastaus.getKoulutusTulos().add(tulos);
         }
         return vastaus;
         } else {
         //Retrieving komotos according to criteria provided in kysely, currently list of tarjoajaOids and a name
         int koulutusAlkuvuosi = kysely.getKoulutuksenAlkamisvuosi() != null ? kysely.getKoulutuksenAlkamisvuosi().intValue() : -1;

         List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findByCriteria(kysely.getTarjoajaOids(), kysely.getNimi(), koulutusAlkuvuosi, getAlkuKuukaudet(kysely.getKoulutuksenAlkamiskausi()));

         //Creating the answer type
         HaeKoulutuksetVastausTyyppi vastaus = new HaeKoulutuksetVastausTyyppi();

         //Populating the answer with required data
         for (KoulutusmoduuliToteutus komoto : komotos) {

         KoulutusTulos tulos = getKoulutusTulosFromKoulutusmoduuliToteutus(komoto);
         vastaus.getKoulutusTulos().add(tulos);
         }
         return vastaus;
         }*/
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

    private KoulutusKoosteTyyppi getKoulutusTulosFromKoulutusmoduuliToteutus(KoulutusmoduuliToteutus komoto) {
        //KoulutusTulos tulos = new KoulutusTulos();
        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();

        KoulutusKoosteTyyppi koulutusKooste = new KoulutusKoosteTyyppi();
        koulutusKooste.setTarjoaja(komoto.getTarjoaja());
        koulutusKooste.setNimi(EntityUtils.copyFields(komo.getNimi()));
        koulutusKooste.setTila(EntityUtils.convertTila(komoto.getTila()));
        koulutusKooste.setKoulutusmoduuli((komo != null) ? komo.getOid() : null);
        koulutusKooste.setKoulutusmoduuliToteutus(komoto.getOid());
        koulutusKooste.setKoulutuskoodi((komo != null) ? komo.getKoulutusKoodi() : null);
        koulutusKooste.setKoulutusohjelmakoodi((komo != null) ? komo.getKoulutusohjelmaKoodi() : null);
        koulutusKooste.setPohjakoulutusVaatimus(komoto.getPohjakoulutusvaatimus());
        koulutusKooste.setAjankohta(parseAjankohtaString(komoto.getKoulutuksenAlkamisPvm()));
        koulutusKooste.setKomotoOid(komoto.getOid());
        koulutusKooste.setTutkintonimike((komo != null) ? komo.getTutkintonimike() : null);
        koulutusKooste.setKoulutustyyppi(KoulutusasteTyyppi.fromValue(komo.getKoulutustyyppi()));
        koulutusKooste.setLukiolinjakoodi(komo.getLukiolinja());
        //tulos.setKoulutus(koulutusKooste);
        return koulutusKooste;
    }

    private String parseAjankohtaString(Date koulutuksenAlkamisPvm) {
        if (koulutuksenAlkamisPvm == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(koulutuksenAlkamisPvm);
        String pvmStr = cal.get(Calendar.MONTH) < 7 ? KEVAT : SYKSY;
        return pvmStr + " " + cal.get(Calendar.YEAR);
    }

    public LueKoulutusVastausTyyppi lueKoulutus(LueKoulutusKyselyTyyppi kysely) {
    	//long t = System.currentTimeMillis();
        log.debug("in LueKoulutusVastausTyyppi");
        KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(kysely.getOid());

        LueKoulutusVastausTyyppi result = convert(komoto);

        KoodistoKoodiTyyppi koulutusKoodi = new KoodistoKoodiTyyppi();
        koulutusKoodi.setUri(komoto.getKoulutusmoduuli().getKoulutusKoodi());
        result.setKoulutusKoodi(koulutusKoodi);

        KoodistoKoodiTyyppi koulutusOhjelmaKoodi = new KoodistoKoodiTyyppi();
        koulutusOhjelmaKoodi.setUri(komoto.getKoulutusmoduuli().getKoulutusohjelmaKoodi());
        result.setKoulutusohjelmaKoodi(koulutusOhjelmaKoodi);

        KoodistoKoodiTyyppi lukiolinja = new KoodistoKoodiTyyppi();
        lukiolinja.setUri(komoto.getKoulutusmoduuli().getLukiolinja());
        result.setLukiolinjaKoodi(lukiolinja);

        //Asetetaan koulutusmoduuli
        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();

        Koulutusmoduuli parentKomo = this.koulutusmoduuliDAO.findParentKomo(komo);

        //if parent komo does not exist, we are reading a parent komoto, thus the koulutusohjelmanvalinta field is in the komoto itself
        if (parentKomo == null) {
            result.setKoulutusmoduuli(EntityUtils.copyFieldsToKoulutusmoduuliKoosteTyyppi(komo));
            result.setKoulutusohjelmanValinta(EntityUtils.copyFields(komoto.getKoulutusohjelmanValinta()));

            //if parent komo exists we read the koulutusohjelmanValinta field from the parent (tutkinto) komoto,
            //and merging the parent and actual komo to get the komo fields.
        } else {
            result.setKoulutusmoduuli(EntityUtils.copyFieldsToKoulutusmoduuliKoosteTyyppi(komo, parentKomo));
            handleParentKomoto(parentKomo, komoto, result);
        }

        //System.out.println("lueKoulutus(...) -> "+(System.currentTimeMillis()-t)+" ms");

        return result;
    }

    /*
     * reading the parent komoto fields to the result dto. 
     */
    private void handleParentKomoto(Koulutusmoduuli parentKomo, KoulutusmoduuliToteutus komoto, LueKoulutusVastausTyyppi result) {
        List<KoulutusmoduuliToteutus> parentList = this.koulutusmoduuliToteutusDAO.findKomotosByKomoTarjoajaPohjakoulutus(parentKomo, komoto.getTarjoaja(), komoto.getPohjakoulutusvaatimus());
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
            result.setKoulutusohjelmanValinta(EntityUtils.copyFields(parentKomoto.getKoulutusohjelmanValinta()));
        }
    }

    private String getHakukohdeTulosKoodistoNimi(HakukohdeTulos hakukohdeTulos) {
        String koodistoNimi = null;

        for (MonikielinenTekstiTyyppi.Teksti teksti: hakukohdeTulos.getHakukohde().getNimi().getTeksti())   {
            if (teksti.getKieliKoodi().trim().equalsIgnoreCase("fi")) {
                koodistoNimi = teksti.getValue();
            }
        }
        if (koodistoNimi != null) {
            koodistoNimi = koodistoNimi + ", " + hakukohdeTulos.getHakukohde().getTila().value();
        }

        return koodistoNimi;
    }

    private LueKoulutusVastausTyyppi convert(KoulutusmoduuliToteutus fromKoulutus) {
        log.debug("in convert ");
        LueKoulutusVastausTyyppi toKoulutus = new LueKoulutusVastausTyyppi();
        toKoulutus.setVersion(fromKoulutus.getVersion());
        toKoulutus.setTila(EntityUtils.convertTila(fromKoulutus.getTila()));

        HaeHakukohteetKyselyTyyppi kysely = new HaeHakukohteetKyselyTyyppi();
        kysely.getKoulutusOids().add(fromKoulutus.getOid());
        kysely.setKoulutuksenAlkamisvuosi(0);
        HaeHakukohteetVastausTyyppi vastaus =  searchService.haeHakukohteet(kysely);
        if (fromKoulutus.getHakukohdes() != null) {
            for (HakukohdeTulos hakukohde : vastaus.getHakukohdeTulos()) {
                HakukohdeKoosteTyyppi hakukohdeKoosteTyyppi = new HakukohdeKoosteTyyppi();
                hakukohdeKoosteTyyppi.setOid(hakukohde.getHakukohde().getOid());
                hakukohdeKoosteTyyppi.setKoodistoNimi(getHakukohdeTulosKoodistoNimi(hakukohde));
                hakukohdeKoosteTyyppi.setNimi(hakukohde.getHakukohde().getKoodistoNimi());
                hakukohdeKoosteTyyppi.setTila(fi.vm.sade.tarjonta.service.types.TarjontaTila.fromValue(hakukohde.getHakukohde().getTila().name()));
                toKoulutus.getHakukohteet().add(hakukohdeKoosteTyyppi);
            }
        }


        toKoulutus.setViimeisinPaivittajaOid(fromKoulutus.getLastUpdatedByOid());
        toKoulutus.setViimeisinPaivitysPvm(DatatypeHelper.convertDateToXmlGregorianCal(fromKoulutus.getUpdated()));

        toKoulutus.setOid(fromKoulutus.getOid());
        GregorianCalendar greg = new GregorianCalendar();
        greg.setTime(fromKoulutus.getKoulutuksenAlkamisPvm());
        try {
            toKoulutus.setKoulutuksenAlkamisPaiva(DatatypeFactory.newInstance().newXMLGregorianCalendar(greg));
        } catch (Exception ex) {
            toKoulutus.setKoulutuksenAlkamisPaiva(null);
        }
        KoulutuksenKestoTyyppi kestoT = new KoulutuksenKestoTyyppi();
        kestoT.setArvo(fromKoulutus.getSuunniteltuKestoArvo());
        kestoT.setYksikko(fromKoulutus.getSuunniteltuKestoYksikko());
        toKoulutus.setKesto(kestoT);

        if (fromKoulutus.getKoulutusaste() != null) {
            KoodistoKoodiTyyppi koulutusaste = new KoodistoKoodiTyyppi();
            koulutusaste.setUri(fromKoulutus.getKoulutusaste());
            toKoulutus.setKoulutusaste(koulutusaste);
        }

        if (fromKoulutus.getPohjakoulutusvaatimus() != null) {
            KoodistoKoodiTyyppi pohjakoulutusvaatimus = new KoodistoKoodiTyyppi();
            pohjakoulutusvaatimus.setUri(fromKoulutus.getPohjakoulutusvaatimus());
            toKoulutus.setPohjakoulutusvaatimus(pohjakoulutusvaatimus);
        }

        toKoulutus.setTarjoaja(fromKoulutus.getTarjoaja());

        EntityUtils.copyKoodistoUris(fromKoulutus.getOpetusmuotos(), toKoulutus.getOpetusmuoto());
        EntityUtils.copyKoodistoUris(fromKoulutus.getOpetuskielis(), toKoulutus.getOpetuskieli());
        EntityUtils.copyKoodistoUris(fromKoulutus.getKoulutuslajis(), toKoulutus.getKoulutuslaji());
        EntityUtils.copyWebLinkkis(fromKoulutus.getLinkkis(), toKoulutus.getLinkki());
        EntityUtils.copyYhteyshenkilos(fromKoulutus.getYhteyshenkilos(), toKoulutus.getYhteyshenkilo());
        toKoulutus.setPainotus(EntityUtils.copyFields(fromKoulutus.getPainotus()));
        //
        // Koulutus lisätiedot / additional information for Koulutus
        //
        EntityUtils.copyKoodistoUris(fromKoulutus.getAmmattinimikes(), toKoulutus.getAmmattinimikkeet());
        toKoulutus.setKuvailevatTiedot(EntityUtils.copyFields(fromKoulutus.getKuvailevatTiedot()));
        toKoulutus.setSisalto(EntityUtils.copyFields(fromKoulutus.getSisalto()));
        toKoulutus.setSijoittuminenTyoelamaan(EntityUtils.copyFields(fromKoulutus.getSijoittuminenTyoelamaan()));
        toKoulutus.setKansainvalistyminen(EntityUtils.copyFields(fromKoulutus.getKansainvalistyminen()));
        toKoulutus.setYhteistyoMuidenToimijoidenKanssa(EntityUtils.copyFields(fromKoulutus.getYhteistyoMuidenToimijoidenKanssa()));

        toKoulutus.getA1A2Kieli().addAll(EntityUtils.copyFields(fromKoulutus.getTarjotutKielet().values(), Kieliaine.A1A2KIELI));
        toKoulutus.getB1Kieli().addAll(EntityUtils.copyFields(fromKoulutus.getTarjotutKielet().values(), Kieliaine.B1KIELI));
        toKoulutus.getB2Kieli().addAll(EntityUtils.copyFields(fromKoulutus.getTarjotutKielet().values(), Kieliaine.B2KIELI));
        toKoulutus.getB3Kieli().addAll(EntityUtils.copyFields(fromKoulutus.getTarjotutKielet().values(), Kieliaine.B3KIELI));
        toKoulutus.getMuutKielet().addAll(EntityUtils.copyFields(fromKoulutus.getTarjotutKielet().values(), Kieliaine.MUUT_KIELET));

        EntityUtils.copyKoodistoUris(fromKoulutus.getLukiodiplomit(), toKoulutus.getLukiodiplomit());

        return toKoulutus;
    }

    @Override
    public LueHakukohdeVastausTyyppi lueHakukohde(LueHakukohdeKyselyTyyppi kysely) {
    	//long t = System.currentTimeMillis();
//		Hakukohde hakukohde = hakukohdeDAO.findBy("oid", kysely.getOid()).get(0);
    	Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithDepenciesByOid(kysely.getOid());
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
        for (TekstiKaannos tekstiKaannos : monikielinenTeksti.getTekstis()) {

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
        criteria.setKoulutusKoodi(kysely.getKoulutuskoodiUri());
        criteria.setKoulutusohjelmaKoodi(kysely.getKoulutusohjelmakoodiUri());
        criteria.setLukiolinjaKoodiUri(kysely.getLukiolinjakoodiUri());
        criteria.setKoulutustyyppi(kysely.getKoulutustyyppi());

        HaeKoulutusmoduulitVastausTyyppi vastaus = new HaeKoulutusmoduulitVastausTyyppi();
        for (Koulutusmoduuli curKomo : this.koulutusmoduuliDAO.search(criteria)) {
            KoulutusmoduuliTulos tulos = new KoulutusmoduuliTulos();
            KoulutusmoduuliKoosteTyyppi kooste = new KoulutusmoduuliKoosteTyyppi();
            Koulutusmoduuli findParentKomo = this.koulutusmoduuliDAO.findParentKomo(curKomo);
            if (findParentKomo != null) {
                kooste.setParentOid(findParentKomo.getOid());
            }
            kooste.setOid(curKomo.getOid());
            kooste.setKoulutuskoodiUri(curKomo.getKoulutusKoodi());
            kooste.setKoulutusohjelmakoodiUri(curKomo.getKoulutusohjelmaKoodi());
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
        criteria.setKoulutustyyppi(kysely.getKoulutustyyppi());
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
}
