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
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.MonikielinenMetadata;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.service.resources.KomoResource;
import fi.vm.sade.tarjonta.service.resources.dto.Komo;
import fi.vm.sade.tarjonta.service.resources.dto.Komoto;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

/**
 * REST /komo/*
 *
 * @author mlyly
 * @see KomoResource
 */
@Transactional
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
    public Komo getByOID(String oid) {
        LOG.info("getByOID() -- /komo/{}", oid);

        Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(oid);
        if (false && komo == null) {
            LOG.warn("TESTING -- NULL KOMO!");
            // TODO TESTING
            komo = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
            komo.setEqfLuokitus("eqf_a#2");
            komo.setOid(oid);

            MonikielinenTeksti mt = new MonikielinenTeksti();
            mt.addTekstiKaannos("kieli_fi#1", "Suomeksi dataaa");
            mt.addTekstiKaannos("kieli_sv#1", "Svenska talande!");
            komo.setJatkoOpintoMahdollisuudet(mt);
        }

        Komo result = conversionService.convert(komo, Komo.class);
        LOG.info("  result={}", result);
        return result;
    }

    // GET /komo?searchParams=xxx etc.
    @Override
    public List<Komo> search(String searchTerms, int count, int startIndex, String language) {
        LOG.info("search() -- /komo/  (st={}, c={}, si={}, l={})", new Object[]{searchTerms, count, startIndex, language});

        List<Komo> result = new ArrayList<Komo>();

        // Default values for params
        count = (count == 0) ? 100 : count;
        language = (language == null) ? "fi" : language;

        // TODO paging + searching!

        List<Koulutusmoduuli> komos = koulutusmoduuliDAO.findAll();
        for (Koulutusmoduuli komo : komos) {
            Komo k = conversionService.convert(komo, Komo.class);
            result.add(k);
        }

        LOG.info("  result={}", result);

        return result;
    }

    // GET /komo/{oid}/komotos
    @Override
    public List<Komoto> getKomotosByKomotoOID(String oid, int startIndex, int count, String language) {
        LOG.info("getKomotosByKomotoOID() -- /komo/{}/komotos (startIndex={}, count={}, language={}", new Object[] {oid, startIndex, count, language});

        List<Komoto> result = new ArrayList<Komoto>();

        Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(oid);
        Set<KoulutusmoduuliToteutus> komos = komo.getKoulutusmoduuliToteutusList();

        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : komos) {
            result.add(conversionService.convert(koulutusmoduuliToteutus, Komoto.class));
        }

        LOG.info("  result={}", result);

        return result;
    }
}
