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
package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakuaikaRDTO;
import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HaunNimi;
import fi.vm.sade.tarjonta.service.types.SisaisetHakuAjat;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;

/**
 * Convert Haku REST DTO to WSDL DTO.
 *
 * @author Timo Santasalo / Teknokala Ky
 */
public class HakuRDTOToHakuTyyppiConverter implements Converter<HakuDTO, HakuTyyppi> {

  @Override
  public HakuTyyppi convert(HakuDTO s) {
    HakuTyyppi t = new HakuTyyppi();

    t.setOid(s.getOid());
    t.setVersion((long) s.getVersion());

    t.getSisaisetHakuajat().addAll(convertHakuaikas(s.getHakuaikas()));

    t.setHakukausiUri(s.getHakukausiUri());
    t.setHakuVuosi(s.getHakukausiVuosi());
    // t.set(s.getHakukohdes());
    t.setHakulomakeUrl(s.getHakulomakeUrl());
    t.setHakutapaUri(s.getHakutapaUri());
    t.setHakutyyppiUri(s.getHakutyyppiUri());
    t.setHaunTunniste(s.getHaunTunniste());
    t.setKohdejoukkoUri(s.getKohdejoukkoUri());
    t.setKoulutuksenAlkamisVuosi(s.getKoulutuksenAlkamisVuosi());
    t.setKoulutuksenAlkamisKausiUri(s.getKoulutuksenAlkamiskausiUri());
    t.getHaunKielistetytNimet().addAll(convertHakuNimis(s.getNimi()));

    t.setHaunTila(TarjontaTila.valueOf(s.getTila()));

    t.setSijoittelu(s.isSijoittelu());

    return t;
  }

  private List<HaunNimi> convertHakuNimis(Map<String, String> values) {
    List<HaunNimi> ret = new ArrayList<HaunNimi>();
    values.forEach((key, value) -> ret.add(new HaunNimi(key, value)));
    return ret;
  }

  private List<SisaisetHakuAjat> convertHakuaikas(List<HakuaikaRDTO> hakuaikas) {
    List<SisaisetHakuAjat> result = new ArrayList<SisaisetHakuAjat>();

    for (HakuaikaRDTO h : hakuaikas) {
      result.add(new SisaisetHakuAjat(h.getOid(), h.getNimi(), h.getAlkuPvm(), h.getLoppuPvm()));
    }

    return result;
  }
}
