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

import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.TarjontaTila;
import fi.vm.sade.tarjonta.service.resources.KomoResource;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class KomoResourceImpl implements KomoResource {

    private static final Logger LOG = LoggerFactory.getLogger(KomoResourceImpl.class);

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired(required = true)
    private ConversionService conversionService;

    // GET /komo/hello
    @Override
    public String hello() {
        LOG.info("hello() -- /komo/hello");
        return "hello";
    }

    // GET /komo/{oid}
    @Override
    public KomoDTO getByOID(String oid) {
        LOG.info("getByOID() -- /komo/{}", oid);
        Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(oid);
        KomoDTO result = conversionService.convert(komo, KomoDTO.class);
        LOG.info("  result={}", result);
        return result;
    }

    // GET /komo?searchTerms=xxx&count=x&startIndex=x&lastModifiedBefore=x&lastModifiedSince=x
    @Override
    public List<String> search(String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.info("/komo -- search(st={}, c={}, si={}, lmb={}, lms={})", new Object[] {searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince});

        // TarjontaTila == null (== all states ok)
        TarjontaTila tarjontaTila = null; // TarjontaTila.JULKAISTU;

        if (count <= 0) {
            count = 100;
            LOG.info("  autolimit search to {} entries!", count);
        }

        List<String> result = new ArrayList<String>();
        result.addAll(koulutusmoduuliDAO.findOIDsBy(tarjontaTila, count, startIndex, lastModifiedBefore, lastModifiedSince));
        LOG.info("  result={}", result);
        return result;
    }

    // GET /komo/OID/komoto?count=x&startIndex=x
    @Override
    public List<String> getKomotosByKomoOID(String oid, int count, int startIndex) {
        LOG.info("/komo/{}/komoto -- (si={}, c={})", new Object[] {oid, count, startIndex});

        if (count <= 0) {
            count = 100;
            LOG.info("  autolimit search to {} entries!", count);
        }

        List<String> result = koulutusmoduuliToteutusDAO.findOidsByKomoOid(oid, count, startIndex);

        LOG.info("  result={}", result);
        return result;
    }

}
