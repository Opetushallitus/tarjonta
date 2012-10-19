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
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.util.CollectionUtils;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.business.HakuBusinessService;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.datatype.DatatypeFactory;

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
    private ConversionService conversionService;

    public TarjontaPublicServiceImpl() {
        super();
    }

    @Override
    public ListHakuVastausTyyppi listHaku(ListaaHakuTyyppi parameters) {
        ListHakuVastausTyyppi hakuVastaus = new ListHakuVastausTyyppi();
        SearchCriteriaDTO allCriteria = new SearchCriteriaDTO();
        allCriteria.setMeneillaan(true);
        allCriteria.setPaattyneet(true);
        allCriteria.setTulevat(true);
        hakuVastaus.getResponse().addAll(convert(businessService.findAll(allCriteria)));
        return hakuVastaus;
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
     * @param businessService the businessService to set
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

            hakukohde.setNimi(hakukohdeModel.getHakukohdeNimi());
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
        //Retrieving all komotos this will be extended search only for komotos matching the criteria
        List<KoulutusmoduuliToteutus> komotos = this.koulutusmoduuliToteutusDAO.findAll();

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
            tulos.setKoulutus(koulutusKooste);
            vastaus.getKoulutusTulos().add(tulos);
        }
        return vastaus;
    }

    public LueKoulutusVastausTyyppi lueKoulutus(
        LueKoulutusKyselyTyyppi kysely) {
        KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findBy("oid", kysely.getOid()).isEmpty() ? null : this.koulutusmoduuliToteutusDAO.
            findBy("oid", kysely.getOid()).get(0);
        return convert(komoto);
    }

    private LueKoulutusVastausTyyppi convert(KoulutusmoduuliToteutus toteutus) {

        LueKoulutusVastausTyyppi koulutus = new LueKoulutusVastausTyyppi();
        KoodistoKoodiTyyppi opetusmuotoKoodi = new KoodistoKoodiTyyppi();
        opetusmuotoKoodi.setUri(toteutus.getOpetusmuotos().isEmpty() ? null : toteutus.getOpetusmuotos().iterator().next().getKoodiUri());
        koulutus.setOpetusmuoto(opetusmuotoKoodi);
        koulutus.setOid(toteutus.getOid());
        GregorianCalendar greg = new GregorianCalendar();
        greg.setTime(toteutus.getKoulutuksenAlkamisPvm());
        try {
            koulutus.setKoulutuksenAlkamisPaiva(DatatypeFactory.newInstance().newXMLGregorianCalendar(greg));
        } catch (Exception ex) {
            koulutus.setKoulutuksenAlkamisPaiva(null);
        }
        KoulutuksenKestoTyyppi kestoT = new KoulutuksenKestoTyyppi();
        kestoT.setArvo(toteutus.getSuunniteltuKestoArvo());
        kestoT.setYksikko(toteutus.getSuunniteltuKestoYksikko());
        koulutus.setKesto(kestoT);

        if (toteutus.getOpetuskielis() != null) {
            for (KoodistoUri opetusKieli : toteutus.getOpetuskielis()) {
                KoodistoKoodiTyyppi koodi = new KoodistoKoodiTyyppi();
                koodi.setUri(opetusKieli.getKoodiUri());
                koulutus.getOpetuskieli().add(koodi);
            }
        }


        for (KoodistoUri uri : toteutus.getKoulutuslajiList()) {
            KoodistoKoodiTyyppi koodi = new KoodistoKoodiTyyppi();
            koodi.setUri(uri.getKoodiUri());
            koulutus.getKoulutuslaji().add(koodi);
        }

        return koulutus;

    }

	@Override
	public LueHakukohdeVastausTyyppi lueHakukohde(LueHakukohdeKyselyTyyppi kysely) {
		Hakukohde hakukohde = hakukohdeDAO.findBy("oid", kysely.getOid()).get(0);
		HakukohdeTyyppi hakukohdeTyyppi = conversionService.convert(hakukohde, HakukohdeTyyppi.class);
		LueHakukohdeVastausTyyppi vastaus = new LueHakukohdeVastausTyyppi();
		vastaus.setHakukohde(hakukohdeTyyppi);
		return vastaus;
	}

}

