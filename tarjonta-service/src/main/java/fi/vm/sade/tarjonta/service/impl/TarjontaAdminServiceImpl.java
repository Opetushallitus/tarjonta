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
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.business.HakuBusinessService;
import fi.vm.sade.tarjonta.service.business.KoulutusBusinessService;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoodistoKoodiTyyppi;

/**
 *
 * @author Tuomas Katva
 */
@Transactional
@Service("tarjontaAdminService")
public class TarjontaAdminServiceImpl implements TarjontaAdminService {

    @Autowired
    private HakuBusinessService hakuBusinessService;

    @Autowired
    private KoulutusBusinessService koulutusBusinessService;

    @Autowired
    private HakuDAO hakuDAO;

    @Autowired
    private ConversionService conversionService;

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

        Koulutusmoduuli moduuli = koulutusBusinessService.findTutkintoOhjelma(
            koulutus.getKoulutusKoodi().getUri(),
            koulutus.getKoulutusohjelmaKoodi().getUri());

        if (moduuli == null) {
            // todo: error reporting
            throw new IllegalArgumentException("no such Koulutusmoduuli: " + koulutus.getKoulutusKoodi()
                + ", " + koulutus.getKoulutusohjelmaKoodi());
        }

        koulutusBusinessService.create(convert(koulutus), moduuli);

        LisaaKoulutusVastausTyyppi vastaus = new LisaaKoulutusVastausTyyppi();
        return vastaus;

    }

    private List<HakuTyyppi> convert(List<Haku> haut) {
        List<HakuTyyppi> tyypit = new ArrayList<HakuTyyppi>();
        for (Haku haku : haut) {
            tyypit.add(conversionService.convert(haku, HakuTyyppi.class));
        }
        return tyypit;
    }

    private KoulutusmoduuliToteutus convert(LisaaKoulutusTyyppi koulutus) {

        KoulutusmoduuliToteutus toteutus = new KoulutusmoduuliToteutus();

        toteutus.addOpetusmuoto(new KoodistoUri(koulutus.getOpetusmuoto().getUri()));
        toteutus.setOid(koulutus.getOid());
        toteutus.setKoulutuksenAlkamisPvm(koulutus.getKoulutuksenAlkamisPaiva().toGregorianCalendar().getTime());

        toteutus.setSuunniteltuKestoArvo(koulutus.getKesto().getArvo());
        toteutus.setSuunniteltuKestoYksikko(koulutus.getKesto().getYksikko());

        for (KoodistoKoodiTyyppi opetusKieli : koulutus.getOpetuskieli()) {
            toteutus.addOpetuskieli(new KoodistoUri(opetusKieli.getUri()));
        }

        for (KoodistoKoodiTyyppi koulutuslaji : koulutus.getKoulutuslaji()) {
            // fix: toteutus should have multiple koulutuslahji
        }

        return toteutus;

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

}

