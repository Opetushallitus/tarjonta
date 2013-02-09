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

import fi.vm.sade.events.Event;
import fi.vm.sade.events.EventSender;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.jetty.util.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.YhteyshenkiloDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.service.GenericFault;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.business.HakuBusinessService;
import fi.vm.sade.tarjonta.service.business.KoulutusBusinessService;
import fi.vm.sade.tarjonta.service.business.exception.HakuUsedException;
import fi.vm.sade.tarjonta.service.business.exception.HakukohdeUsedException;
import fi.vm.sade.tarjonta.service.business.exception.KoulutusUsedException;
import fi.vm.sade.tarjonta.service.types.*;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import java.util.Date;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 *
 * @author Tuomas Katva
 */
@Transactional
@Service("tarjontaAdminService")
public class TarjontaAdminServiceImpl implements TarjontaAdminService {

    private static final Logger log = LoggerFactory.getLogger(TarjontaAdminServiceImpl.class);
    @Autowired(required = true)
    private HakuBusinessService hakuBusinessService;
    @Autowired(required = true)
    private KoulutusBusinessService koulutusBusinessService;
    @Autowired(required = true)
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired(required = true)
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired(required = true)
    private HakuDAO hakuDAO;
    @Autowired(required = true)
    private HakukohdeDAO hakukohdeDAO;
    @Autowired(required = true)
    private ConversionService conversionService;
    @Autowired(required = true)
    private YhteyshenkiloDAO yhteyshenkiloDAO;
    @Autowired(required = true)
    private PublicationDataService publication;
    /**
     * Väliaikainne kunnes Koodisto on alustettu.
     */
    @Autowired
    private TarjontaSampleData sampleData;

    @Override
    public HakuTyyppi paivitaHaku(HakuTyyppi hakuDto) {

        Haku foundHaku = hakuBusinessService.findByOid(hakuDto.getOid());
        if (foundHaku != null) {
            mergeHaku(conversionService.convert(hakuDto, Haku.class), foundHaku);
            foundHaku = hakuBusinessService.update(foundHaku);
            publication.sendEvent(foundHaku.getTila(), foundHaku.getOid(), PublicationDataService.DATA_TYPE_HAKU, PublicationDataService.ACTION_UPDATE);
            return conversionService.convert(foundHaku, HakuTyyppi.class);
        } else {
            throw new BusinessException("tarjonta.haku.update.no.oid");
        }
    }

    @Override
    public List<ValintakoeTyyppi> tallennaValintakokeitaHakukohteelle(@WebParam(name = "hakukohdeOid", targetNamespace = "") String hakukohdeOid, @WebParam(name = "hakukohteenValintakokeet", targetNamespace = "") List<ValintakoeTyyppi> hakukohteenValintakokeet) {
        List<Hakukohde> hakukohdes = hakukohdeDAO.findHakukohdeWithDepenciesByOid(hakukohdeOid);
        if (hakukohdes != null && hakukohdes.size() > 0) {
            for (Valintakoe valintakoe : convertValintaKokees(hakukohteenValintakokeet)) {
                hakukohdes.get(0).addValintakoe(valintakoe);

            }
            hakukohdeDAO.update(hakukohdes.get(0));

            hakukohdes = hakukohdeDAO.findHakukohdeWithDepenciesByOid(hakukohdeOid);
            if (hakukohdes != null && hakukohdes.get(0).getValintakoes() != null) {
                return convertValintakoeTyyppis(hakukohdes.get(0).getValintakoes());
            } else {
                return new ArrayList<ValintakoeTyyppi>();
            }
        } else {
            throw new BusinessException("tarjonta.haku.no.hakukohde.found");
        }
    }
    /*
    * This method returns true if komoto copy is allowed.
    */
    @Override
    public boolean tarkistaKoulutuksenKopiointi(@WebParam(partName = "parameters", name = "tarkistaKoulutusKopiointi", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") TarkistaKoulutusKopiointiTyyppi parameters) {

        List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findKoulutusModuuliWithPohjakoulutusAndTarjoaja(parameters.getTarjoajaOid(),parameters.getPohjakoulutus(),parameters.getKoulutusLuokitusKoodi(),parameters.getKoulutusohjelmaKoodi(),
                parameters.getOpetuskielis(),parameters.getKoulutuslajis());
        if (komotos == null || komotos.size() < 1) {

            return true;
        } else {
            boolean retVal = true;
            komotoLoop : for (KoulutusmoduuliToteutus komoto:komotos) {
                if (checkKausiAndVuosi(komoto.getKoulutuksenAlkamisPvm(),parameters.getKoulutusAlkamisPvm())) {
                      retVal = false;
                    break  komotoLoop;
                }
            }
            return retVal;
        }
    }
    /*
    * Returns true if compared dates year and kausi matches
    */
    private boolean checkKausiAndVuosi(Date komotoDate,Date checkDate) {
        Calendar komotoCal = Calendar.getInstance();
        Calendar checkCal = Calendar.getInstance();
        komotoCal.setTime(komotoDate);
        checkCal.setTime(checkDate);

        if (komotoCal.get(Calendar.YEAR) == checkCal.get(Calendar.YEAR)) {
           int komotoMonthInt = komotoCal.get(Calendar.MONTH);
           int checkMonthInt = checkCal.get(Calendar.MONTH);
           komotoMonthInt ++;
           checkMonthInt++;
           String komotoKausi = getKausiStringFromMonth(komotoMonthInt);
           String checkKausi = getKausiStringFromMonth(checkMonthInt);

           if (komotoKausi.trim().equalsIgnoreCase(checkKausi.trim())) {
               return true;
           } else {
               return false;
           }

        } else {
          return false;
        }

    }

    private String getKausiStringFromMonth(int month) {
        if (month > 6) {
            return "s";
        } else {
            return "k";
        }
    }

    public static Set<KoodistoUri> convertStringToUris(List<String> koodistoUriStrs) {
        Set<KoodistoUri> koodistoUris = new HashSet<KoodistoUri>();
        for (String koodistoUriStr:koodistoUriStrs) {
            KoodistoUri koodistoUri = new KoodistoUri(koodistoUriStr);
            koodistoUris.add(koodistoUri);
        }
        return koodistoUris;
    }

    @Override
    public void tallennaLiitteitaHakukohteelle(@WebParam(name = "hakukohdeOid", targetNamespace = "") String hakukohdeOid, @WebParam(name = "hakukohteenLiitteen", targetNamespace = "") List<HakukohdeLiiteTyyppi> hakukohteenLiitteen) {
        List<Hakukohde> hakukohdes = hakukohdeDAO.findHakukohdeWithDepenciesByOid(hakukohdeOid);
        if (hakukohdes != null && hakukohdes.size() > 0) {
            for (HakukohdeLiite hakukohdeLiite : convertLiiteTyyppi(hakukohteenLiitteen)) {
                hakukohdes.get(0).addLiite(hakukohdeLiite);

                hakukohdeLiite.setHakukohde(hakukohdes.get(0));

            }
            hakukohdeDAO.update(hakukohdes.get(0));
        } else {
            throw new BusinessException("tarjonta.haku.no.hakukohde.found");
        }
    }

    private List<HakukohdeLiite> convertLiiteTyyppi(List<HakukohdeLiiteTyyppi> tyyppis) {
        ArrayList<HakukohdeLiite> hakukohdeLiites = new ArrayList<HakukohdeLiite>();

        for (HakukohdeLiiteTyyppi hakukohdeLiiteTyyppi : tyyppis) {
            HakukohdeLiite liite = conversionService.convert(hakukohdeLiiteTyyppi, HakukohdeLiite.class);
            hakukohdeLiites.add(liite);
        }

        return hakukohdeLiites;
    }

    private List<ValintakoeTyyppi> convertValintakoeTyyppis(Set<Valintakoe> valintakoes) {
        ArrayList<ValintakoeTyyppi> valintakoeTyyppis = new ArrayList<ValintakoeTyyppi>();

        for (Valintakoe valintakoe : valintakoes) {
            valintakoeTyyppis.add(conversionService.convert(valintakoe, ValintakoeTyyppi.class));
        }

        return valintakoeTyyppis;
    }

    private List<Valintakoe> convertValintaKokees(List<ValintakoeTyyppi> valintakoeTyyppis) {
        ArrayList<Valintakoe> valintakoes = new ArrayList<Valintakoe>();

        for (ValintakoeTyyppi valintakoeTyyppi : valintakoeTyyppis) {
            valintakoes.add(conversionService.convert(valintakoeTyyppi, Valintakoe.class));
        }

        return valintakoes;
    }

    @Override
    public HakukohdeTyyppi lisaaHakukohde(HakukohdeTyyppi hakukohde) {
        Hakukohde hakuk = conversionService.convert(hakukohde, Hakukohde.class);
        Haku haku = hakuDAO.findByOid(hakukohde.getHakukohteenHakuOid());

        hakuk.setHaku(haku);
        hakuk = hakukohdeDAO.insert(hakuk);
        hakuk.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(hakukohde.getHakukohteenKoulutusOidit(), hakuk));
        hakukohdeDAO.update(hakuk);
        publication.sendEvent(hakuk.getTila(), hakuk.getOid(), PublicationDataService.DATA_TYPE_HAKUKOHDE, PublicationDataService.ACTION_INSERT);


        return hakukohde;
    }

    private Set<KoulutusmoduuliToteutus> findKoulutusModuuliToteutus(List<String> komotoOids, Hakukohde hakukohde) {
        Set<KoulutusmoduuliToteutus> komotos = new HashSet<KoulutusmoduuliToteutus>();

        for (String komotoOid : komotoOids) {
            KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(komotoOid);
            komoto.addHakukohde(hakukohde);
            komotos.add(komoto);
        }

        return komotos;
    }

    @Override
    public void lisaaTaiPoistaKoulutuksiaHakukohteelle(@WebParam(partName = "parameters", name = "lisaaKoulutusHakukohteelle", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") LisaaKoulutusHakukohteelleTyyppi parameters) {
        List<Hakukohde> hakukohdes = hakukohdeDAO.findHakukohdeWithDepenciesByOid(parameters.getHakukohdeOid());
        Hakukohde hakukohde = hakukohdes.get(0);

        if (parameters.isLisaa()) {
            hakukohde.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(parameters.getKoulutusOids(), hakukohde));
            log.info("Adding {} koulutukses to hakukohde: {}", hakukohde.getKoulutusmoduuliToteutuses().size(), hakukohde.getOid());
            hakukohdeDAO.update(hakukohde);
        } else {
            List<KoulutusmoduuliToteutus> poistettavatModuuliLinkit = koulutusmoduuliToteutusDAO.findKoulutusModuulisWithHakukohdesByOids(parameters.getKoulutusOids());
            for (KoulutusmoduuliToteutus komoto : poistettavatModuuliLinkit) {
                log.info("REMOVING KOULUTUS : {} FROM HAKUKOHDE {}", komoto.getOid(), hakukohde.getOid());

                komoto.removeHakukohde(hakukohde);

                hakukohde.removeKoulutusmoduuliToteutus(komoto);
                koulutusmoduuliToteutusDAO.update(komoto);
            }
            hakukohdeDAO.update(hakukohde);
        }
    }

    @Override
    public HakukohdeTyyppi poistaHakukohde(HakukohdeTyyppi hakukohdePoisto) throws GenericFault {
        Hakukohde hakukohde = hakukohdeDAO.findBy("oid", hakukohdePoisto.getOid()).get(0);
        if (hakuAlkanut(hakukohde)) {
            throw new HakukohdeUsedException();
        } else {
            for (KoulutusmoduuliToteutus curKoul : hakukohde.getKoulutusmoduuliToteutuses()) {
                curKoul.removeHakukohde(hakukohde);
            }
            hakukohdeDAO.remove(hakukohde);
        }
        return new HakukohdeTyyppi();
    }

    @Override
    public HakukohdeTyyppi paivitaHakukohde(HakukohdeTyyppi hakukohdePaivitys) {
        Hakukohde hakukohde = conversionService.convert(hakukohdePaivitys, Hakukohde.class);
        List<Hakukohde> hakukohdeTemp = hakukohdeDAO.findBy("oid", hakukohdePaivitys.getOid());
        hakukohde.setId(hakukohdeTemp.get(0).getId());
        hakukohde.setVersion(hakukohdeTemp.get(0).getVersion());
        Haku haku = hakuDAO.findByOid(hakukohdePaivitys.getHakukohteenHakuOid());

        hakukohde.setHaku(haku);
        hakukohde.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(hakukohdePaivitys.getHakukohteenKoulutusOidit(), hakukohde));

        hakukohdeDAO.update(hakukohde);
        publication.sendEvent(hakukohde.getTila(), hakukohde.getOid(), PublicationDataService.DATA_TYPE_HAKUKOHDE, PublicationDataService.ACTION_UPDATE);


        return hakukohdePaivitys;
    }

    @Override
    public HakuTyyppi lisaaHaku(HakuTyyppi hakuDto) {
        Haku haku = conversionService.convert(hakuDto, Haku.class);
        haku = hakuBusinessService.save(haku);
        publication.sendEvent(haku.getTila(), haku.getOid(), PublicationDataService.DATA_TYPE_HAKU, PublicationDataService.ACTION_INSERT);

        return conversionService.convert(haku, HakuTyyppi.class);
    }

    @Override
    public void poistaHaku(HakuTyyppi hakuDto) throws GenericFault {

        Haku haku = hakuBusinessService.findByOid(hakuDto.getOid());
        if (checkHakuDepencies(haku)) {
            throw new HakuUsedException();
        } else {
            hakuDAO.remove(haku);
        }
    }

    private boolean checkHakuDepencies(Haku haku) {
        List<Haku> haut = hakuDAO.findHakukohdeHakus(haku);
        if (haut != null && haut.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkHakukohdeDepencies(Hakukohde hakukohde) {
        List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliDAO.findKomotoByHakukohde(hakukohde);
        if (komotos != null && komotos.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public LisaaKoulutusVastausTyyppi lisaaKoulutus(LisaaKoulutusTyyppi koulutus) {
        KoulutusmoduuliToteutus toteutus = koulutusBusinessService.createKoulutus(koulutus);
        publication.sendEvent(toteutus.getTila(), toteutus.getOid(), PublicationDataService.DATA_TYPE_KOMOTO, PublicationDataService.ACTION_INSERT);
        LisaaKoulutusVastausTyyppi vastaus = new LisaaKoulutusVastausTyyppi();
        return vastaus;
    }

    @Override
    public PaivitaKoulutusVastausTyyppi paivitaKoulutus(PaivitaKoulutusTyyppi koulutus) {
        KoulutusmoduuliToteutus toteutus = koulutusBusinessService.updateKoulutus(koulutus);
        publication.sendEvent(toteutus.getTila(), toteutus.getOid(), PublicationDataService.DATA_TYPE_KOMOTO, PublicationDataService.ACTION_UPDATE);

        PaivitaKoulutusVastausTyyppi vastaus = new PaivitaKoulutusVastausTyyppi();
        return vastaus;
    }

    @Override
    public void poistaKoulutus(String koulutusOid) throws GenericFault {
        KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findByOid(koulutusOid);
        if (komoto.getHakukohdes().isEmpty()) {
            this.koulutusmoduuliToteutusDAO.remove(komoto);
        } else {
            throw new KoulutusUsedException();
        }
    }

    private boolean hakuAlkanut(Hakukohde hakukohde) {
        for (Hakuaika curHakuaika : hakukohde.getHaku().getHakuaikas()) {
            if (!curHakuaika.getAlkamisPvm().after(Calendar.getInstance().getTime())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove once koodisto has proper data.
     */
    @Override
    public void initSample(String parameters) {
        try {
            sampleData.init();
            log.info("SAMPLE DATA CREATED");
        } catch (Exception e) {
            log.warn("initializing tarjonta data threw exception", e);
        }
    }

    /**
     * Remove once koodisto has proper data.
     */
    @Override
    public void initKomo(String parameters) {

        log.warn("Implementation is still missing!");

    }

    @Override
    public KoulutusmoduuliKoosteTyyppi lisaaKoulutusmoduuli(KoulutusmoduuliKoosteTyyppi koulutusmoduuli)
            throws GenericFault {
        koulutusmoduuliDAO.insert(EntityUtils.copyFieldsToKoulutusmoduuli(koulutusmoduuli));
        return koulutusmoduuli;
    }

    private List<HakuTyyppi> convert(List<Haku> haut) {
        List<HakuTyyppi> tyypit = new ArrayList<HakuTyyppi>();
        for (Haku haku : haut) {
            tyypit.add(conversionService.convert(haku, HakuTyyppi.class));
        }
        return tyypit;
    }

    /**
     * @return the businessService
     */
    public HakuBusinessService getBusinessService() {
        return hakuBusinessService;
    }

    /**
     * @param businessService the businessService to set
     */
    public void setBusinessService(HakuBusinessService businessService) {
        this.hakuBusinessService = businessService;
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
        return hakuDAO;
    }

    /**
     * @param hakuDao the hakuDao to set
     */
    public void setHakuDao(HakuDAO hakuDao) {
        this.hakuDAO = hakuDao;
    }

    private void mergeHaku(Haku source, Haku target) {
        target.setNimi(source.getNimi());
        target.setOid(source.getOid());
        target.setHakukausiUri(source.getHakukausiUri());
        target.setHakukausiVuosi(source.getHakukausiVuosi());
        target.setHakulomakeUrl(source.getHakulomakeUrl());
        target.setHakutapaUri(source.getHakutapaUri());
        target.setHakutyyppiUri(source.getHakutyyppiUri());
        target.setKohdejoukkoUri(source.getKohdejoukkoUri());
        target.setKoulutuksenAlkamiskausiUri(source.getKoulutuksenAlkamiskausiUri());
        target.setKoulutuksenAlkamisVuosi(source.getKoulutuksenAlkamisVuosi());
        target.setSijoittelu(source.isSijoittelu());
        target.setTila(source.getTila());
        target.setHaunTunniste(source.getHaunTunniste());
        mergeSisaisetHaunAlkamisAjat(source, target);
    }

    private void mergeSisaisetHaunAlkamisAjat(Haku source, Haku target) {
        List<Hakuaika> hakuajat = new ArrayList<Hakuaika>();
        for (Hakuaika curAika : target.getHakuaikas()) {
            hakuajat.add(curAika);
        }

        for (Hakuaika curHak : hakuajat) {
            target.removeHakuaika(curHak);
        }

        for (Hakuaika curHakuaika : source.getHakuaikas()) {
            target.addHakuaika(curHakuaika);
        }
    }

    /**
     * @return the hakukohdeDAO
     */
    public HakukohdeDAO getHakukohdeDAO() {
        return hakukohdeDAO;
    }

    /**
     * @param hakukohdeDAO the hakukohdeDAO to set
     */
    public void setHakukohdeDAO(HakukohdeDAO hakukohdeDAO) {
        this.hakukohdeDAO = hakukohdeDAO;
    }

    /**
     * @return the koulutusmoduuliToteutusDAO
     */
    public KoulutusmoduuliToteutusDAO getKoulutusmoduuliToteutusDAO() {
        return koulutusmoduuliToteutusDAO;
    }

    /**
     * @param koulutusmoduuliToteutusDAO the koulutusmoduuliToteutusDAO to set
     */
    public void setKoulutusmoduuliToteutusDAO(KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO) {
        this.koulutusmoduuliToteutusDAO = koulutusmoduuliToteutusDAO;
    }

    @Override
    public PaivitaTilaVastausTyyppi paivitaTilat(PaivitaTilaTyyppi tarjontatiedonTila) {
        publication.updatePublicationStatus(tarjontatiedonTila.getTilaOids());
        return new PaivitaTilaVastausTyyppi();
    }

    @Override
    public boolean testaaTilasiirtyma(GeneerinenTilaTyyppi parameters) {
        return publication.isValidStatusChange(parameters);
    }
}
