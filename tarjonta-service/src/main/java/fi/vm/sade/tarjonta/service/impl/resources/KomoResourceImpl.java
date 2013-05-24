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
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.service.impl.resources;

import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.TarjontaTila;
import fi.vm.sade.tarjonta.service.resources.KomoResource;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

/**
 * REST: /komo , /komo/hello, /komo/OID, /komo/OID/komoto
 *
 * @author mlyly
 * @see KomoResource for fuller docs.
 */
@Transactional(readOnly = true)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class KomoResourceImpl implements KomoResource {

    private static final Logger LOG = LoggerFactory.getLogger(KomoResourceImpl.class);

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private ConversionService conversionService;

    @Autowired
    private OIDService oidService;

    // GET /komo/hello
    @Override
    public String hello() {
        try {
            LOG.debug("/komo/hello -- hello()");
            return "Well hello! Have a nice OID - " + oidService.newOid(NodeClassCode.TEKN_5);
        } catch (ExceptionMessage ex) {
            LOG.error("Failed", ex);
            return "ERROR - SEE LOG";
        }
    }

    // GET /komo/{oid}
    @Override
    public KomoDTO getByOID(String oid) {
        LOG.info("/komo/}{} -- getByOID()", oid);
        Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(oid);
        KomoDTO result = conversionService.convert(komo, KomoDTO.class);
        LOG.debug("  result={}", result);
        return result;
    }

    // GET /komo?searchTerms=xxx&count=x&startIndex=x&lastModifiedBefore=x&lastModifiedSince=x
    @Override
    public List<OidRDTO> search(String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.info("/komo -- search(st={}, c={}, si={}, lmb={}, lms={})", new Object[]{searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince});

        // TarjontaTila == null (== all states ok)
        TarjontaTila tarjontaTila = null; // TarjontaTila.JULKAISTU;

        if (count <= 0) {
            count = 100;
            LOG.debug("  autolimit search to {} entries!", count);
        }

        List<OidRDTO> result =
                HakuResourceImpl.convertOidList(koulutusmoduuliDAO.findOIDsBy(tarjontaTila, count, startIndex, lastModifiedBefore, lastModifiedSince));
        LOG.debug("  result={}", result);
        return result;
    }

    // GET /komo/OID/komoto?count=x&startIndex=x
    @Override
    public List<OidRDTO> getKomotosByKomoOID(String oid, int count, int startIndex) {
        LOG.info("/komo/{}/komoto -- (si={}, c={})", new Object[]{oid, count, startIndex});

        if (count <= 0) {
            count = 100;
            LOG.debug("  autolimit search to {} entries!", count);
        }

        List<OidRDTO> result = HakuResourceImpl.convertOidList(koulutusmoduuliToteutusDAO.findOidsByKomoOid(oid, count, startIndex));
        LOG.debug("  result={}", result);
        return result;
    }
}
