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

import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import org.springframework.core.convert.converter.Converter;

/**
 * @author mlyly
 */
public class OsoiteToOsoiteRDTOConverter implements Converter<Osoite, OsoiteRDTO> {

  @Override
  public OsoiteRDTO convert(Osoite s) {
    OsoiteRDTO t = new OsoiteRDTO();

    t.setOsoiterivi1(s.getOsoiterivi1());
    t.setOsoiterivi2(s.getOsoiterivi2());
    t.setPostinumero(s.getPostinumero());
    t.setPostitoimipaikka(s.getPostitoimipaikka());

    return t;
  }
}
