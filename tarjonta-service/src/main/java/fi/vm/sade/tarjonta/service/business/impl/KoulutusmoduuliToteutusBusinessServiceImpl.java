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

import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusSearchDTO;
import fi.vm.sade.tarjonta.service.business.KoulutusmoduuliToteutusBusinessService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jukka Raanamo
 */
@Transactional
@Service("koulutusmoduuliToteutusService")
public class KoulutusmoduuliToteutusBusinessServiceImpl implements KoulutusmoduuliToteutusBusinessService {

    @Autowired
    private KoulutusmoduuliToteutusDAO toteutusDAO;

    @Autowired
    private KoulutusmoduuliDAO moduuliDAO;

    @Autowired
    private OIDService oidService;

    @Override
    public KoulutusmoduuliToteutus save(KoulutusmoduuliToteutus toteutus) {

        if (toteutus.getOid() == null) {
            toteutus.setOid(newKoulutusmoduuliToteutusOID());
            return insert(toteutus);
        } else {
            return update(toteutus);
        }

    }

    public KoulutusmoduuliToteutus insert(KoulutusmoduuliToteutus toteutus) {
        return toteutusDAO.insert(toteutus);
    }

    public KoulutusmoduuliToteutus update(KoulutusmoduuliToteutus toteutus) {

        KoulutusmoduuliToteutus saved = toteutusDAO.read(toteutus.getId());
        EntityUtils.copyFields(toteutus, saved);

        toteutusDAO.update(saved);
        return saved;

    }

    @Override
    public KoulutusmoduuliToteutus findByOid(String koulutusmoduuliOID) {
        return toteutusDAO.findByOid(koulutusmoduuliOID);
    }

    private String newKoulutusmoduuliToteutusOID() {
        try {
            return oidService.newOid(NodeClassCode.PALVELUT);
        } catch (ExceptionMessage e) {
            throw new RuntimeException("creating oid failed", e);
        
        }
    }

    @Override
    public List<KoulutusmoduuliToteutus> findWithTila(KoulutusmoduuliToteutusSearchDTO criteria) {
        return toteutusDAO.findWithTila(criteria);
    }
    
    

}

