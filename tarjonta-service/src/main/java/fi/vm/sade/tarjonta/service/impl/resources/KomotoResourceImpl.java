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
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.TarjontaTila;
import fi.vm.sade.tarjonta.service.resources.KomotoResource;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

/**
 * /komoto, /komoto/hello, /komoto/OID, /komoto/OID/komo
 *
 * Internal documentation: http://liitu.hard.ware.fi/confluence/display/PROG/Tarjonnan+REST+palvelut
 *
 * @author mlyly
 * @see KomotoResource
 */
@Transactional(readOnly = true)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class KomotoResourceImpl implements KomotoResource {

    private static final Logger LOG = LoggerFactory.getLogger(KomotoResourceImpl.class);
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired(required = true)
    private ConversionService conversionService;

    // GET /komoto/hello
    @Override
    public String hello() {
        LOG.info("hello() -- /komoto/hello");
        return "hello";
    }

    // GET /komoto/{oid}
    @Override
    public KomotoDTO getByOID(String oid) {
        LOG.info("getByOID() -- /komoto/{}", oid);
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        KomotoDTO result = conversionService.convert(komoto, KomotoDTO.class);
        LOG.info("  result={}", result);
        return result;
    }

    // GET /komoto?searchTerms=xxx etc.
    @Override
    public List<String> search(String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.info("search() -- /komoto?st={}, c={}, si={}, lmb={}, lma={})", new Object[]{searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince});

        // TODO hard coded, add param tarjonta tila + get the state!
        TarjontaTila tarjontaTila = TarjontaTila.JULKAISTU;

        if (count <= 0) {
            count = 100;
            LOG.info("  autolimit search to {} entries!", count);
        }

        List<String> result = new ArrayList<String>();
        result.addAll(koulutusmoduuliToteutusDAO.findOIDsBy(tarjontaTila, count, startIndex, lastModifiedBefore, lastModifiedSince));
        LOG.info("  result={}", result);
        return result;
    }

    // GET /komoto/{oid}/komo
    @Override
    public KomoDTO getKomoByKomotoOID(String oid) {
        LOG.info("getKomoByKomotoOID() -- /komoto/{}/komo", oid);
        KomoDTO result = null;
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        if (komoto != null) {
            result = conversionService.convert(komoto.getKoulutusmoduuli(), KomoDTO.class);
        }
        LOG.info("  result={}", result);
        return result;
    }


    // GET /komoto/{oid}/hakukohde
    @Override
    public List<String> getHakukohdesByKomotoOID(String oid) {
        LOG.info("getHakukohdesByKomotoOID() -- /komoto/{}/hakukohde", oid);
        List<String> result = new ArrayList<String>();

        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        if (komoto != null) {
            // TODO add spesific finder to get just these OIDs... not sure about the usage pattern of this service
            for (Hakukohde hakukohde : komoto.getHakukohdes()) {
                result.add(hakukohde.getOid());
            }
        }

        LOG.info("  result={}", result);
        return result;
    }
}
