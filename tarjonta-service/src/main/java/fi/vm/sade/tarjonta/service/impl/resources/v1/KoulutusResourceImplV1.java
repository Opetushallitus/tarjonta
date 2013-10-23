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

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusResource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusAmmatillinenPeruskoulutusRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusAmmattikorkeakouluRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusLukioRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusPerusopetuksenLisaopetusRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusValmentavaJaKuntouttavaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusYliopistoRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultRDTO;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mlyly
 */
@Transactional(readOnly = false)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class KoulutusResourceImplV1 implements KoulutusResource {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusResourceImplV1.class);

    @Autowired
    private KoulutusmoduuliDAO _komoDao;
    @Autowired
    private KoulutusmoduuliToteutusDAO _komotoDao;

    private V1Converter _converter;

    @PostConstruct
    private void init() {
        LOG.info("init()");
        _converter = new V1Converter();
        _converter.setKomoDao(_komoDao);
        _converter.setKomotoDao(_komotoDao);
    }

    @Override
    public ResultRDTO<KoulutusRDTO> findByOid(String oid) {
        LOG.info("findByOid({})", oid);

        ResultRDTO<KoulutusRDTO> result = new ResultRDTO<KoulutusRDTO>();

        try {
            KoulutusRDTO k = _converter.fromKomotoToKoulutusRDTO(oid);
            result.setResult(k);
            if (k == null) {
                result.setStatus(ResultRDTO.ResultStatus.NOT_FOUND);
            }
        } catch (Throwable ex) {
            result.setStatus(ResultRDTO.ResultStatus.ERROR);
            result.addError(ErrorRDTO.createSystemError(ex, "system.error", oid));
        }

        return result;
    }

    @Override
    public ResultRDTO<KoulutusLukioRDTO> postLukiokoulutus(ResultRDTO<KoulutusLukioRDTO> koulutus) {
        LOG.info("postLukiokoulutus({})", koulutus);

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<KoulutusAmmatillinenPeruskoulutusRDTO> postAmmatillinenPeruskoulutus(ResultRDTO<KoulutusAmmatillinenPeruskoulutusRDTO> koulutus) {
        LOG.info("postAmmatillinenPeruskoulutus({})", koulutus);

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<KoulutusAmmattikorkeakouluRDTO> postAmmattikorkeakoulutus(ResultRDTO<KoulutusAmmattikorkeakouluRDTO> koulutus) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<KoulutusYliopistoRDTO> postYliopistokoulutus(ResultRDTO<KoulutusYliopistoRDTO> koulutus) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<KoulutusPerusopetuksenLisaopetusRDTO> postPerusopetuksenLisaopetusKoulutus(ResultRDTO<KoulutusPerusopetuksenLisaopetusRDTO> koulutus) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<KoulutusValmentavaJaKuntouttavaRDTO> postValmentavaJaKuntouttavaKoulutus(ResultRDTO<KoulutusValmentavaJaKuntouttavaRDTO> koulutus) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response deleteByOid(String oid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response saveKuva(String oid, InputStream in, String fileType, long fileSize, String kieliUri) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response deleteKuva(String oid, String kieliUri) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
