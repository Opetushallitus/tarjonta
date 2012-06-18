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
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.NoSuchOIDException;
import fi.vm.sade.tarjonta.service.business.KoulutusmoduuliBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jukka Raanamo
 */
@Transactional
@Service("koulutusmoduuliService")
public class KoulutusmoduuliBusinessServiceImpl implements KoulutusmoduuliBusinessService {

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private OIDService oidService;

    @Override
    public Koulutusmoduuli save(Koulutusmoduuli moduuli) {

        // TODO: we need to update any existing entity by copying those fields that have been overwritten

        if (moduuli.getOid() == null) {
            moduuli.setOid(newKoulutusmoduuliOID());
            return koulutusmoduuliDAO.insert(moduuli);
        } else {
            koulutusmoduuliDAO.update(moduuli);
            return moduuli;
        }
    }

    @Override
    public Koulutusmoduuli findByOid(String koulutusmoduuliOID) {
        final Koulutusmoduuli koulutusmoduuli = koulutusmoduuliDAO.findByOid(koulutusmoduuliOID);
        if (koulutusmoduuli == null) {
            throw new NoSuchOIDException("No such Koulutusmoduuli: " + koulutusmoduuliOID);
        }
        return koulutusmoduuli;
    }

    private String newKoulutusmoduuliOID() {
        try {
            return oidService.newOid(NodeClassCode.PALVELUT);
        } catch (ExceptionMessage e) {
            throw new RuntimeException("creating OID failed", e);
        }
    }

}

