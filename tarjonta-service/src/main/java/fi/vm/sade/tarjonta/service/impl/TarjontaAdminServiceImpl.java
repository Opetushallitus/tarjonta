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
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutusmoduuliKoosteTyyppi;

import java.util.HashSet;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi paivitaHaku(fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi hakuDto) {

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
    public HakukohdeTyyppi lisaaHakukohde(HakukohdeTyyppi hakukohde) {
        Hakukohde hakuk = conversionService.convert(hakukohde, Hakukohde.class);
        Haku haku = hakuDAO.findByOid(hakukohde.getHakukohteenHakuOid());

        hakuk.setHaku(haku);
        hakuk = hakukohdeDAO.insert(hakuk);
        hakuk.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(hakukohde.getHakukohteenKoulutusOidit(),hakuk));
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
    public HakukohdeTyyppi poistaHakukohde(HakukohdeTyyppi hakukohdePoisto) {
    	Hakukohde hakukohde = hakukohdeDAO.findBy("oid", hakukohdePoisto.getOid()).get(0);
    	for (KoulutusmoduuliToteutus curKoul:  hakukohde.getKoulutusmoduuliToteutuses()) {
    		curKoul.removeHakukohde(hakukohde);
    	}
    	hakukohdeDAO.remove(hakukohde);
    	return new HakukohdeTyyppi();
    }

    @Override
    public HakukohdeTyyppi paivitaHakukohde(HakukohdeTyyppi hakukohdePaivitys) {
        Hakukohde hakukohde = conversionService.convert(hakukohdePaivitys, Hakukohde.class);
        hakukohdeDAO.update(hakukohde);
        return hakukohdePaivitys;
    }



    @Override
    public fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi lisaaHaku(fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi hakuDto) {
        Haku haku = conversionService.convert(hakuDto, Haku.class);
        haku = hakuBusinessService.save(haku);
        return conversionService.convert(haku, HakuTyyppi.class);
    }

    @Override
    public void poistaHaku(fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi hakuDto) {

        Haku haku = hakuBusinessService.findByOid(hakuDto.getOid());

        hakuDAO.remove(haku);
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
	public void poistaKoulutus(String koulutusOid) {
		KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findByOid(koulutusOid);
		this.koulutusmoduuliToteutusDAO.remove(komoto);
		removeOrphanHakukohteet();
	}

	private void removeOrphanHakukohteet() {
		for (Hakukohde curHakukohde : this.hakukohdeDAO.findOrphanHakukohteet()) {
			this.hakukohdeDAO.remove(curHakukohde);
		}
	}


    /**
     * Remove once koodisto has proper data.
     */
    @Override
    public void initSample(String parameters) {
        try {
            log.warn("SAMPLE DATA CREATED");
            sampleData.init();
        } catch (Exception e) {
            log.warn("initializing tarjonta data threw exception", e);
        }
    }

	@Override
	public KoulutusmoduuliKoosteTyyppi lisaaKoulutusmoduuli(KoulutusmoduuliKoosteTyyppi koulutusmoduuli)
			throws GenericFault {
		Koulutusmoduuli komo = new Koulutusmoduuli(KoulutusmoduuliTyyppi.valueOf(koulutusmoduuli.getKoulutusmoduuliTyyppi().value()));
		komo.setOid(koulutusmoduuli.getOid());
		komo.setKoulutusKoodi(koulutusmoduuli.getKoulutuskoodiUri());
		komo.setKoulutusohjelmaKoodi(koulutusmoduuli.getKoulutusohjelmakoodiUri());
        komo.setLaajuus(koulutusmoduuli.getLaajuusyksikkoUri(), koulutusmoduuli.getLaajuusarvo());
		komo.setTutkintoOhjelmanNimi(koulutusmoduuli.getTutkintoOhjelmaUri());
		komo.setTutkintonimike(koulutusmoduuli.getTutkintonimikeUri());
		komo.setUlkoinenTunniste(koulutusmoduuli.getUlkoinenTunniste());
		koulutusmoduuliDAO.insert(komo);
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

