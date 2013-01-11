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
import java.util.List;

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
import fi.vm.sade.tarjonta.service.GenericFault;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.business.HakuBusinessService;
import fi.vm.sade.tarjonta.service.business.KoulutusBusinessService;
import fi.vm.sade.tarjonta.service.business.exception.HakuUsedException;
import fi.vm.sade.tarjonta.service.business.exception.HakukohdeUsedException;
import fi.vm.sade.tarjonta.service.business.exception.KoulutusUsedException;
import fi.vm.sade.tarjonta.service.types.*;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;

/**
 *
 * @author Tuomas Katva
 */
@Transactional
@Service("tarjontaAdminService")
public class TarjontaAdminServiceImpl implements TarjontaAdminService {

    private static final Logger log = LoggerFactory.getLogger(TarjontaAdminServiceImpl.class);
    @Autowired
    private HakuBusinessService hakuBusinessService;
    @Autowired
    private KoulutusBusinessService koulutusBusinessService;
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private HakuDAO hakuDAO;
    @Autowired
    private HakukohdeDAO hakukohdeDAO;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private YhteyshenkiloDAO yhteyshenkiloDAO;
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
            return conversionService.convert(foundHaku, HakuTyyppi.class);
        } else {
            throw new BusinessException("tarjonta.haku.update.no.oid");
        }
    }

    @Override
    public void tallennaLiitteitaHakukohteelle(@WebParam(name = "hakukohdeOid", targetNamespace = "") String hakukohdeOid, @WebParam(name = "hakukohteenLiitteen", targetNamespace = "") List<HakukohdeLiiteTyyppi> hakukohteenLiitteen) {
            List<Hakukohde> hakukohdes = hakukohdeDAO.findHakukohdeWithDepenciesByOid(hakukohdeOid);
            if (hakukohdes != null && hakukohdes.size() > 0) {
                for (HakukohdeLiite hakukohdeLiite: convertLiiteTyyppi(hakukohteenLiitteen)) {
                    hakukohdes.get(0).addLiite(hakukohdeLiite);

                    hakukohdeLiite.setHakukohde(hakukohdes.get(0));

                }
                hakukohdeDAO.update(hakukohdes.get(0));
            }   else {
                throw new BusinessException("tarjonta.haku.no.hakukohde.found");
            }
    }

    private List<HakukohdeLiite> convertLiiteTyyppi(List<HakukohdeLiiteTyyppi> tyyppis) {
        ArrayList<HakukohdeLiite> hakukohdeLiites = new ArrayList<HakukohdeLiite>();

        for (HakukohdeLiiteTyyppi hakukohdeLiiteTyyppi: tyyppis) {
            HakukohdeLiite liite = conversionService.convert(hakukohdeLiiteTyyppi,HakukohdeLiite.class);
            hakukohdeLiites.add(liite);
        }

        return hakukohdeLiites;
    }

    @Override
    public HakukohdeTyyppi lisaaHakukohde(HakukohdeTyyppi hakukohde) {
        Hakukohde hakuk = conversionService.convert(hakukohde, Hakukohde.class);
        Haku haku = hakuDAO.findByOid(hakukohde.getHakukohteenHakuOid());

        hakuk.setHaku(haku);
        hakuk = hakukohdeDAO.insert(hakuk);
        hakuk.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(hakukohde.getHakukohteenKoulutusOidit(), hakuk));
        hakukohdeDAO.update(hakuk);
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
        hakukohde.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(parameters.getKoulutusOids(),hakukohde));
        log.info("Adding {} koulutukses to hakukohde: {}",hakukohde.getKoulutusmoduuliToteutuses().size(),hakukohde.getOid());
        hakukohdeDAO.update(hakukohde);
        } else {
            List<KoulutusmoduuliToteutus> poistettavatModuuliLinkit = koulutusmoduuliToteutusDAO.findKoulutusModuulisWithHakukohdesByOids(parameters.getKoulutusOids());
            for(KoulutusmoduuliToteutus komoto:poistettavatModuuliLinkit) {
                log.info("REMOVING KOULUTUS : {} FROM HAKUKOHDE {}",komoto.getOid(),hakukohde.getOid());

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


        return hakukohdePaivitys;
    }

    @Override
    public HakuTyyppi lisaaHaku(HakuTyyppi hakuDto) {
        Haku haku = conversionService.convert(hakuDto, Haku.class);
        haku = hakuBusinessService.save(haku);
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

        LisaaKoulutusVastausTyyppi vastaus = new LisaaKoulutusVastausTyyppi();
        return vastaus;

    }

    @Override
    public PaivitaKoulutusVastausTyyppi paivitaKoulutus(PaivitaKoulutusTyyppi koulutus) {

        KoulutusmoduuliToteutus toteutus = koulutusBusinessService.updateKoulutus(koulutus);

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
}
