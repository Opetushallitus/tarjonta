/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.tarjonta.service.resources.v1.KoulutusResource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusAmmatillinenPeruskoulutusRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusLukioRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultRDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mlyly
 */
public class KoulutusResourceImplV1 implements KoulutusResource {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusResourceImplV1.class);

    @Override
    public ResultRDTO<KoulutusRDTO> findByOid(String oid) {
        LOG.error("findByOid({})", oid);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<KoulutusLukioRDTO> postLukiokoulutus(ResultRDTO<KoulutusLukioRDTO> koulutus) {
        LOG.error("");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<KoulutusLukioRDTO> postAmmatillinenPeruskoulutus(ResultRDTO<KoulutusAmmatillinenPeruskoulutusRDTO> koulutus) {
        LOG.error("postLukiokoulutus({})", koulutus);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<KoulutusRDTO> postAmmattikorkeakoulutus(ResultRDTO<KoulutusRDTO> koulutus) {
        LOG.error("postAmmattikorkeakoulutus({})", koulutus);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<KoulutusRDTO> postYliopistokoulutus(ResultRDTO<KoulutusRDTO> koulutus) {
        LOG.error("postYliopistokoulutus({})", koulutus);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<KoulutusRDTO> postPerusopetuksenLisaopetusKoulutus(ResultRDTO<KoulutusRDTO> koulutus) {
        LOG.error("postPerusopetuksenLisaopetusKoulutus({})", koulutus);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<KoulutusRDTO> postValmentavaJaKuntouttavaKoulutus(ResultRDTO<KoulutusRDTO> koulutus) {
        LOG.error("postValmentavaJaKuntouttavaKoulutus({})", koulutus);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
