/*
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
package fi.vm.sade.tarjonta.service.business.impl;

import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.business.KoulutusBusinessService;
import fi.vm.sade.tarjonta.service.business.exception.TarjontaBusinessException;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.TarjontaVirheKoodi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Service
@Transactional
public class KoulutusBusinessServiceImpl implements KoulutusBusinessService {
    
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private KoulutusSisaltyvyysDAO sisaltyvyysDAO;
    
    @Override
    public Koulutusmoduuli create(Koulutusmoduuli moduuli) {
        
        return koulutusmoduuliDAO.insert(moduuli);
        
    }
    
    @Override
    public KoulutusmoduuliToteutus create(KoulutusmoduuliToteutus toteutus, Koulutusmoduuli moduuli) {
        
        final Koulutusmoduuli m = isNew(moduuli) ? create(moduuli) : moduuli;
        toteutus.setKoulutusmoduuli(m);
        
        return (KoulutusmoduuliToteutus) koulutusmoduuliToteutusDAO.insert(toteutus);
        
    }
    
    @Override
    public Koulutusmoduuli findTutkintoOhjelma(String koulutusLuokitusUri, String koulutusOhjelmaUri) {

        // todo: dao kerroksen voisi poistaa, ainoastaan vaikeammat haut voisi sijoittaa helper:n taakse

        return koulutusmoduuliDAO.findTutkintoOhjelma(koulutusLuokitusUri, koulutusOhjelmaUri);
        
    }
    
    @Override
    public KoulutusmoduuliToteutus createKoulutus(LisaaKoulutusTyyppi koulutus) {
        
        
        Koulutusmoduuli moduuli = koulutusmoduuliDAO.findTutkintoOhjelma(
                koulutus.getKoulutusKoodi().getUri(),
                koulutus.getKoulutusohjelmaKoodi().getUri());
        
        if (moduuli == null) {
            throw new TarjontaBusinessException(TarjontaVirheKoodi.KOULUTUSTA_EI_OLEMASSA.value());
        }
        
        KoulutusmoduuliToteutus komotoModel = new KoulutusmoduuliToteutus();
        EntityUtils.copyFields(koulutus, komotoModel);
        komotoModel.setKoulutusmoduuli(moduuli);
        moduuli.addKoulutusmoduuliToteutus(komotoModel);
        
        return koulutusmoduuliToteutusDAO.insert(komotoModel);
        
        
    }
    
    @Override
    public KoulutusmoduuliToteutus updateKoulutus(PaivitaKoulutusTyyppi koulutus) {
        
        final String oid = koulutus.getOid();
        KoulutusmoduuliToteutus model = koulutusmoduuliToteutusDAO.findByOid(oid);
        
        if (model == null) {
            throw new TarjontaBusinessException(TarjontaVirheKoodi.OID_EI_OLEMASSA.value(), oid);
        }
        
        EntityUtils.copyFields(koulutus, model);
        koulutusmoduuliToteutusDAO.update(model);
        
        return model;
        
    }
    
    private boolean isNew(BaseEntity e) {
        // no good
        return (e.getId() == null);
    }
}
