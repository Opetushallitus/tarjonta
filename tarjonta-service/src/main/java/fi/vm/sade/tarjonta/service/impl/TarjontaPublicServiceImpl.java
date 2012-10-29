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

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.util.CollectionUtils;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.business.HakuBusinessService;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.ListHakuVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;
import fi.vm.sade.tarjonta.service.types.tarjonta.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Tuomas Katva
 */
@Transactional
@Service("tarjontaPublicService")
public class TarjontaPublicServiceImpl implements TarjontaPublicService {

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

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public TarjontaPublicServiceImpl() {
        super();
    }

    @Override
    public ListHakuVastausTyyppi listHaku(ListaaHakuTyyppi parameters) {
        ListHakuVastausTyyppi hakuVastaus = new ListHakuVastausTyyppi();
        if (parameters.getHakuOid() != null) {
         List<Haku> haut = new ArrayList<Haku>();
         haut.add(findHakuWithOid(parameters.getHakuOid().trim()));
         hakuVastaus.getResponse().addAll(convert(haut));
        } else {
        SearchCriteriaDTO allCriteria = new SearchCriteriaDTO();
        allCriteria.setMeneillaan(true);
        allCriteria.setPaattyneet(true);
        allCriteria.setTulevat(true);
        hakuVastaus.getResponse().addAll(convert(businessService.findAll(allCriteria)));
        }
        return hakuVastaus;
    }
    
    private Haku findHakuWithOid(String oid) {
        return hakuDao.findByOid(oid);
    }

    @Override
    public TarjontaTyyppi haeTarjonta(String oid) {
        return new TarjontaTyyppi();
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
        List<Hakukohde> hakukohteet = hakukohdeDAO.haeHakukohteetJaKoulutukset(kysely);
        HaeHakukohteetVastausTyyppi vastaus = new HaeHakukohteetVastausTyyppi();

        List<HaeHakukohteetVastausTyyppi.HakukohdeTulos> rivit = vastaus.getHakukohdeTulos();

        for (Hakukohde hakukohdeModel : hakukohteet) {

            HakukohdeTulos tulos = new HakukohdeTulos();

            HakukohdeKoosteTyyppi hakukohde = new HakukohdeKoosteTyyppi();
            HakuKoosteTyyppi haku = new HakuKoosteTyyppi();
            KoulutusKoosteTyyppi koulutus = new KoulutusKoosteTyyppi();

            hakukohde.setNimi(hakukohdeModel.getHakukohdeKoodistoNimi());
            hakukohde.setTila(hakukohdeModel.getTila());
            hakukohde.setOid(hakukohdeModel.getOid());

            Haku hakuModel = hakukohdeModel.getHaku();
            haku.setNimi(hakuModel.getNimiFi());
            haku.setHakutapa(hakuModel.getHakutapaUri());
            haku.setOid(hakuModel.getOid());

            KoulutusmoduuliToteutus toteutus = CollectionUtils.singleItem(hakukohdeModel.getKoulutusmoduuliToteutuses());
            koulutus.setTarjoaja(toteutus.getTarjoaja());


            tulos.setHakukohde(hakukohde);
            tulos.setHaku(haku);
            tulos.setKoulutus(koulutus);
            rivit.add(tulos);

        }

        return vastaus;

    }

    @Override
    public HaeKoulutuksetVastausTyyppi haeKoulutukset(HaeKoulutuksetKyselyTyyppi kysely) {
    	
    	//Retrieving komotos according to criteria provided in kysely, currently list of tarjoajaOids and a name
        List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findByCriteria(kysely.getTarjoajaOids(), kysely.getNimi());        

        //Creating the answer type
        HaeKoulutuksetVastausTyyppi vastaus = new HaeKoulutuksetVastausTyyppi();

        //Retrieving all komotos this will be extended search only for komotos matching the criteria

        //Populating the answer with required data
        for (KoulutusmoduuliToteutus komoto : komotos) {
            KoulutusTulos tulos = new KoulutusTulos();

            KoulutusKoosteTyyppi koulutusKooste = new KoulutusKoosteTyyppi();
            koulutusKooste.setTarjoaja(komoto.getTarjoaja());
            koulutusKooste.setNimi(komoto.getNimi());
            koulutusKooste.setTila(komoto.getTila());
            koulutusKooste.setKoulutusmoduuli((komoto.getKoulutusmoduuli() != null) ? komoto.getKoulutusmoduuli().getOid() : null);
            koulutusKooste.setKoulutusmoduuliToteutus(komoto.getOid());
            koulutusKooste.setKoulutuskoodi((komoto.getKoulutusmoduuli() != null) ? komoto.getKoulutusmoduuli().getKoulutusKoodi() : null);
            koulutusKooste.setKoulutusohjelmakoodi((komoto.getKoulutusmoduuli() != null) ? komoto.getKoulutusmoduuli().getKoulutusohjelmaKoodi() : null);
            tulos.setKoulutus(koulutusKooste);
            vastaus.getKoulutusTulos().add(tulos);
        }
        return vastaus;
    }

    public LueKoulutusVastausTyyppi lueKoulutus(LueKoulutusKyselyTyyppi kysely) {
        KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(kysely.getOid());

        LueKoulutusVastausTyyppi convert = convert(komoto);

        //
        KoodistoKoodiTyyppi koulutusKoodi = new KoodistoKoodiTyyppi();
        koulutusKoodi.setArvo(komoto.getKoulutusmoduuli().getKoulutusNimi());
        koulutusKoodi.setUri(komoto.getKoulutusmoduuli().getKoulutusKoodi());
        convert.setKoulutusKoodi(koulutusKoodi);

        KoodistoKoodiTyyppi koulutusOhjelmaKoodi = new KoodistoKoodiTyyppi();
        koulutusOhjelmaKoodi.setArvo(komoto.getKoulutusmoduuli().getKoulutusNimi());
        koulutusOhjelmaKoodi.setUri(komoto.getKoulutusmoduuli().getKoulutusohjelmaKoodi());
        convert.setKoulutusohjelmaKoodi(koulutusOhjelmaKoodi);

        return convert;
    }

    private LueKoulutusVastausTyyppi convert(KoulutusmoduuliToteutus fromKoulutus) {

        LueKoulutusVastausTyyppi toKoulutus = new LueKoulutusVastausTyyppi();

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

        EntityUtils.copyKoodistoUris(fromKoulutus.getOpetusmuotos(), toKoulutus.getOpetusmuoto());
        EntityUtils.copyKoodistoUris(fromKoulutus.getOpetuskielis(), toKoulutus.getOpetuskieli());
        EntityUtils.copyKoodistoUris(fromKoulutus.getKoulutuslajiList(), toKoulutus.getKoulutuslaji());
        EntityUtils.copyWebLinkkis(fromKoulutus.getLinkkis(), toKoulutus.getLinkki());
        EntityUtils.copyYhteyshenkilos(fromKoulutus.getYhteyshenkilos(), toKoulutus.getYhteyshenkilo());

        return toKoulutus;

    }

    @Override
    public LueHakukohdeVastausTyyppi lueHakukohde(LueHakukohdeKyselyTyyppi kysely) {
//		Hakukohde hakukohde = hakukohdeDAO.findBy("oid", kysely.getOid()).get(0);
        List<Hakukohde> hakukohdes = hakukohdeDAO.findHakukohdeWithDepenciesByOid(kysely.getOid());
        Hakukohde hakukohde = hakukohdes.get(0);
        HakukohdeTyyppi hakukohdeTyyppi = conversionService.convert(hakukohde, HakukohdeTyyppi.class);
        LueHakukohdeVastausTyyppi vastaus = new LueHakukohdeVastausTyyppi();
        vastaus.setHakukohde(hakukohdeTyyppi);
        return vastaus;
    }

}

