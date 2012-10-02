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
import fi.vm.sade.tarjonta.service.HakuService;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.business.HakuBusinessService;
import fi.vm.sade.tarjonta.service.types.ListHakuVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;
import fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.TarjontaTyyppi;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Tuomas Katva
 */
@Transactional
@Service("tarjontaAdminService")
public class HakuServiceImpl implements HakuService {

    @Autowired
    private HakuBusinessService businessService;
    
    @Autowired
    private HakuDAO hakuDao;
    
    @Autowired
    private ConversionService conversionService;
    
    @Override
    public fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi paivitaHaku(fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi hakuDto) {
        
        Haku foundHaku = businessService.findByOid(hakuDto.getOid());
        if (foundHaku != null) {
            mergeHaku(conversionService.convert(hakuDto,Haku.class), foundHaku); 
            foundHaku = businessService.update(foundHaku);
            return conversionService.convert(foundHaku, HakuTyyppi.class);
        } else {
            throw new BusinessException("tarjonta.haku.update.no.oid");
        }
    }

    @Override
    public fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi lisaaHaku(fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi hakuDto) {
        Haku haku = conversionService.convert(hakuDto,Haku.class);
        haku = businessService.save(haku);
        return conversionService.convert(haku, HakuTyyppi.class);
    }

    @Override
    public void poistaHaku(fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi hakuDto) {
        
        Haku haku = businessService.findByOid(hakuDto.getOid());
        
        hakuDao.remove(haku);
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
        for (Haku haku:haut) {
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
    
    
    private void mergeHaku(Haku source, Haku target) {
        target.setNimi(source.getNimi());
        target.setOid(source.getOid());
        target.setHakukausiUri(source.getHakukausiUri());
        target.setHakukausiVuosi(source.getHakukausiVuosi());
        target.setHakulomakeUrl(source.getHakulomakeUrl());
        target.setHakutapaUri(source.getHakutapaUri());
        target.setHakutyyppiUri(source.getHakutyyppiUri());
        target.setHaunAlkamisPvm(source.getHaunAlkamisPvm());
        target.setHaunLoppumisPvm(source.getHaunLoppumisPvm());
        target.setKohdejoukkoUri(source.getKohdejoukkoUri());
        target.setKoulutuksenAlkamiskausiUri(source.getKoulutuksenAlkamiskausiUri());
        target.setKoulutuksenAlkamisVuosi(source.getKoulutuksenAlkamisVuosi());
        target.setSijoittelu(source.isSijoittelu());
        target.setTila(source.getTila());
        target.setHaunTunniste(source.getHaunTunniste());
    }

}
