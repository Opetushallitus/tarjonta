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
package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AtaruLomakkeetV1RDTO implements Serializable {

  private String avain;
  private List<AtaruLomakeHakuV1RDTO> haut;

  public String getAvain() {
    return avain;
  }

  public void setAvain(String avain) {
    this.avain = avain;
  }

  public List<AtaruLomakeHakuV1RDTO> getHaut() {
    if (haut == null) {
      haut = new ArrayList<>();
    }
    return haut;
  }

  public void setHaut(List<AtaruLomakeHakuV1RDTO> haut) {
    this.haut = haut;
  }
}
