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

import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakuaika;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakuaikaRDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Convert domain Haku to REST API DTO.
 *
 * @author mlyly
 */
public class HakuToHakuDTOConverter extends BaseRDTOConverter<Haku, HakuDTO> {

  @Override
  public HakuDTO convert(Haku s) {
    HakuDTO t = new HakuDTO();

    t.setOid(s.getOid());
    t.setVersion(s.getVersion() != null ? s.getVersion().intValue() : -1);

    t.setHakuaikas(convertHakuaikas(s.getHakuaikas()));

    t.setHakukausiUri(s.getHakukausiUri());
    t.setHakukausiVuosi(s.getHakukausiVuosi());
    // t.set(s.getHakukohdes());
    t.setHakulomakeUrl(s.getHakulomakeUrl());
    t.setHakutapaUri(s.getHakutapaUri());
    t.setHakutyyppiUri(s.getHakutyyppiUri());
    t.setHaunTunniste(s.getHaunTunniste());
    t.setKohdejoukkoUri(s.getKohdejoukkoUri());
    if (s.getKoulutuksenAlkamisVuosi() != null) {
      t.setKoulutuksenAlkamisVuosi(s.getKoulutuksenAlkamisVuosi());
    }
    t.setKoulutuksenAlkamiskausiUri(s.getKoulutuksenAlkamiskausiUri());
    t.setModified(s.getLastUpdateDate());
    t.setModifiedBy(s.getLastUpdatedByOid());
    t.setNimi(convertMonikielinenTekstiToMap(s.getNimi()));
    t.setTila(s.getTila() != null ? s.getTila().name() : null);
    t.setMaxHakukohdes(s.getMaxHakukohdes());

    t.setSijoittelu(s.isSijoittelu());

    return t;
  }

  private List<HakuaikaRDTO> convertHakuaikas(Set<Hakuaika> hakuaikas) {
    List<HakuaikaRDTO> result = new ArrayList<HakuaikaRDTO>();

    for (Hakuaika hakuaika : hakuaikas) {
      result.add(getConversionService().convert(hakuaika, HakuaikaRDTO.class));
    }

    return result;
  }
}
