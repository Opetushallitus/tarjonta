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

import fi.vm.sade.tarjonta.model.Pisteraja;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoePisterajaRDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mlyly
 */
public class PisterajaToValintakoePisterajaRDTOConverter
    extends BaseRDTOConverter<Pisteraja, ValintakoePisterajaRDTO> {

  private static final Logger LOG =
      LoggerFactory.getLogger(PisterajaToValintakoePisterajaRDTOConverter.class);

  @Override
  public ValintakoePisterajaRDTO convert(Pisteraja s) {

    if (s == null) {
      return null;
    }

    ValintakoePisterajaRDTO t = new ValintakoePisterajaRDTO();

    t.setAlinHyvaksyttyPistemaara(
        s.getAlinHyvaksyttyPistemaara() != null
            ? s.getAlinHyvaksyttyPistemaara().doubleValue()
            : null);
    t.setAlinPistemaara(s.getAlinPistemaara() != null ? s.getAlinPistemaara().doubleValue() : null);
    t.setTyyppi(s.getValinnanPisterajaTyyppi());
    t.setYlinPistemaara(s.getYlinPistemaara() != null ? s.getYlinPistemaara().doubleValue() : null);

    t.setOid("" + s.getId());
    t.setVersion(s.getVersion() != null ? s.getVersion().intValue() : 0);

    return t;
  }
}
