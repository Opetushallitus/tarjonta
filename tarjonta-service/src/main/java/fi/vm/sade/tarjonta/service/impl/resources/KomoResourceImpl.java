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
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.resources.KomoResource;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
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

  @Autowired private KoulutusmoduuliDAO koulutusmoduuliDAO;
  @Autowired private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
  @Autowired private ConversionService conversionService;

  @Autowired private OidService oidService;

  // GET /komo/hello
  @Override
  public String hello() {
    try {
      LOG.debug("/komo/hello -- hello()");
      return "Well hello! Have a nice OID - " + oidService.get(TarjontaOidType.KOMO);
    } catch (OIDCreationException ex) {
      LOG.error("Failed", ex);
      return "ERROR - SEE LOG";
    }
  }

  // GET /komo/{oid}
  @Override
  public KomoDTO getByOID(String oid) {
    try {
      LOG.debug("/komo/}{} -- getByOID()", oid);
      Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(oid);

      // Tutke 2 muutoksen myötä koulutuksia voi luoda suorana tutkinto-tason komoihin,
      // mutta koska koulutusinformaatio ei tue tätä uutta rakennetta, pitää tarjonnassa luoda
      // "virtuaalikomoja" vanhan rakenteen säilyttämiseksi. Näille virtuaalikomoille
      // palautetaan kuitenkin niiden parent komon data (joka on tutkinto-tason komo)
      if (komo.isPseudo()) {

        Koulutusmoduuli tutkintoKomo = koulutusmoduuliDAO.findParentKomo(komo);

        // Suurin osa datasta tulee tutkintoKomosta
        KomoDTO result = conversionService.convert(tutkintoKomo, KomoDTO.class);

        // Ylikirjoita osa kentistä pseudo komon arvoilla
        result.setPseudo(true);
        result.setModuuliTyyppi(komo.getModuuliTyyppi().name());
        result.setOid(komo.getOid());
        result.setAlaModuulit(null);
        List<String> ylaModuulit = new ArrayList<String>();
        ylaModuulit.add(tutkintoKomo.getOid());
        result.setYlaModuulit(ylaModuulit);

        return result;
      }

      KomoDTO result = conversionService.convert(komo, KomoDTO.class);
      LOG.debug("  result={}", result);
      return result;
    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    }
  }

  // GET /komo?searchTerms=xxx&count=x&startIndex=x&lastModifiedBefore=x&lastModifiedSince=x
  @Override
  public List<OidRDTO> search(
      String searchTerms,
      int count,
      int startIndex,
      Date lastModifiedBefore,
      Date lastModifiedSince) {
    LOG.debug(
        "/komo -- search(st={}, c={}, si={}, lmb={}, lms={})",
        new Object[] {searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince});

    // TarjontaTila == null (== all states ok)
    TarjontaTila tarjontaTila = null; // TarjontaTila.JULKAISTU;

    count = manageCountValue(count);

    List<OidRDTO> result =
        HakuResourceImpl.convertOidList(
            koulutusmoduuliDAO.findOIDsBy(
                tarjontaTila, count, startIndex, lastModifiedBefore, lastModifiedSince));
    LOG.debug("  result={}", result.size());
    return result;
  }

  // GET /komo/OID/komoto?count=x&startIndex=x
  @Override
  public List<OidRDTO> getKomotosByKomoOID(String oid, int count, int startIndex) {
    LOG.debug("/komo/{}/komoto -- (si={}, c={})", new Object[] {oid, count, startIndex});

    count = manageCountValue(count);

    List<OidRDTO> result =
        HakuResourceImpl.convertOidList(
            koulutusmoduuliToteutusDAO.findOidsByKomoOid(oid, count, startIndex));
    LOG.debug("  result={}", result);
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
