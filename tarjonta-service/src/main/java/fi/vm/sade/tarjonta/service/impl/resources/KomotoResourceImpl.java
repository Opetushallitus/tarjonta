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
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.KomotoResource;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

/**
 * REST /komoto/*
 *
 * @author mlyly
 */
@Transactional
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
        return "hello";
    }

    // GET /komoto/{oid}
    @Override
    public KoulutusmoduuliKoosteTyyppi getByOID(String oid) {
        LOG.warn("getByOID({})", oid);
        return new KoulutusmoduuliKoosteTyyppi();
    }

    // GET /komoto?searchParams=xxx etc.
    @Override
    public List<KoulutusmoduuliKoosteTyyppi> search(String searchTerms, int count, int startIndex, int startPage, String language) {
        LOG.warn("search({}, {}, {}, {}, {})", new Object[]{searchTerms, count, startIndex, startPage, language});

        List<KoulutusmoduuliKoosteTyyppi> result = new ArrayList<KoulutusmoduuliKoosteTyyppi>();

        // Default values for params
        count = (count == 0) ? 100 : count;
        language = (language == null) ? "fi" : language;

        // TODO paging + searching!

        List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findAll();
        for (KoulutusmoduuliToteutus komoto : komotos) {
            KoulutusmoduuliKoosteTyyppi komokt = conversionService.convert(komoto, KoulutusmoduuliKoosteTyyppi.class);
            result.add(komokt);
        }

        LOG.info("  result={}", result);

        return result;
    }
}
