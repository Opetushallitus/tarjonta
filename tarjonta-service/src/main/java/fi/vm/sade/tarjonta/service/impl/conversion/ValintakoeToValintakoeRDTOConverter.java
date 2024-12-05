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
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.model.ValintakoeAjankohta;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoePisterajaRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeRDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Valintakoe conversion to REST API dto's.
 *
 * @author mlyly
 */
public class ValintakoeToValintakoeRDTOConverter
    extends BaseRDTOConverter<Valintakoe, ValintakoeRDTO> {

  private static final Logger LOG =
      LoggerFactory.getLogger(ValintakoeToValintakoeRDTOConverter.class);

  @Override
  public ValintakoeRDTO convert(Valintakoe s) {
    LOG.debug("convert({})", s);

    if (s == null) {
      return null;
    }

    ValintakoeRDTO t = new ValintakoeRDTO();

    t.setCreated(null);
    t.setCreatedBy(null);
    t.setModified(s.getLastUpdateDate());
    t.setModifiedBy(s.getLastUpdatedByOid());
    t.setOid("" + s.getId());
    t.setVersion(s.getVersion() != null ? s.getVersion().intValue() : 0);
    t.setKuvaus(convertMonikielinenTekstiToMap(s.getKuvaus()));
    t.setLisanaytot(convertMonikielinenTekstiToMap(s.getLisanaytot()));
    t.setTyyppiUri(s.getTyyppiUri());

    t.setValintakoeAjankohtas(convertAjankohtas(s.getAjankohtas()));
    t.setValintakoePisterajas(convertPisterajat(s.getPisterajat()));

    return t;
  }

  private List<ValintakoePisterajaRDTO> convertPisterajat(Set<Pisteraja> pisterajat) {
    List<ValintakoePisterajaRDTO> result = new ArrayList<ValintakoePisterajaRDTO>();

    for (Pisteraja pisteraja : pisterajat) {
      result.add(getConversionService().convert(pisteraja, ValintakoePisterajaRDTO.class));
    }

    return result;
  }

  private List<ValintakoeAjankohtaRDTO> convertAjankohtas(Set<ValintakoeAjankohta> ajankohtas) {
    List<ValintakoeAjankohtaRDTO> result = new ArrayList<ValintakoeAjankohtaRDTO>();

    for (ValintakoeAjankohta valintakoeAjankohta : ajankohtas) {
      result.add(
          getConversionService().convert(valintakoeAjankohta, ValintakoeAjankohtaRDTO.class));
    }

    return result;
  }
}
