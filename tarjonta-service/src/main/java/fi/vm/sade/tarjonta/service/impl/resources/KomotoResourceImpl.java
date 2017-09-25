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

import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.KomotoResource;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Internal documentation: http://liitu.hard.ware.fi/confluence/display/PROG/Tarjonnan+REST+palvelut
 *
 * @author mlyly
 * @see KomotoResource for more documentaton.
 */
@Transactional(readOnly = true)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class KomotoResourceImpl implements KomotoResource {

    private static final Logger LOG = LoggerFactory.getLogger(KomotoResourceImpl.class);

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private ConversionService conversionService;

    // GET /komoto/hello
    @Override
    public String hello() {
        LOG.debug("hello() -- /komoto/hello");
        return "hello";
    }

    // GET /komoto/{oid}
    @Override
    public KomotoDTO getByOID(String oid) {
        //LOG.debug("getByOID() -- /komoto/{}", oid);
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        KomotoDTO result = conversionService.convert(komoto, KomotoDTO.class);
        //LOG.debug("  result={}", result);
        return result;
    }

    // GET /komoto?searchTerms=xxx etc.
    @Override
    public List<OidRDTO> search(String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.debug("search() -- /komoto?st={}, c={}, si={}, lmb={}, lma={})", new Object[]{searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince});

        // TODO hard coded, add param tarjonta tila + get the state!
        TarjontaTila tarjontaTila = TarjontaTila.JULKAISTU;

        count = manageCountValue(count);

        List<OidRDTO> result
                = HakuResourceImpl.convertOidList(koulutusmoduuliToteutusDAO.findOIDsBy(tarjontaTila, count, startIndex, lastModifiedBefore, lastModifiedSince));
        //LOG.debug("  result={}", result);
        return result;
    }

    // GET /komoto/{oid}/komo
    @Override
    public KomoDTO getKomoByKomotoOID(String oid) {
        LOG.debug("getKomoByKomotoOID() -- /komoto/{}/komo", oid);
        KomoDTO result = null;
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        if (komoto != null) {
            result = conversionService.convert(komoto.getKoulutusmoduuli(), KomoDTO.class);
        }
        //LOG.debug("  result={}", result);
        return result;
    }

    // GET /komoto/{oid}/hakukohde
    @Override
    public List<OidRDTO> getHakukohdesByKomotoOID(String oid) {
        LOG.debug("getHakukohdesByKomotoOID() -- /komoto/{}/hakukohde", oid);
        List<OidRDTO> result = new ArrayList<OidRDTO>();

        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        if (komoto != null) {
            // TODO add spesific finder to get just these OIDs... not sure about the usage pattern of this service
            for (Hakukohde hakukohde : komoto.getHakukohdes()) {
                result.add(new OidRDTO(hakukohde.getOid()));
            }
        }

        //LOG.debug("  result={}", result);
        return result;
    }

    private int manageCountValue(int count) {

        if (count < 0) {
            count = Integer.MAX_VALUE;
            LOG.debug("  count < 0, using Integer.MAX_VALUE");
        }

        if (count == 0) {
            count = 100;
            LOG.debug("  autolimit search to {} entries!", count);
        }

        return count;
    }

}
