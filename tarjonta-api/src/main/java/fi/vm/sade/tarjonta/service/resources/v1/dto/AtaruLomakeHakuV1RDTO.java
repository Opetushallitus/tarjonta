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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtaruLomakeHakuV1RDTO implements Serializable {
  private String oid;
  private Map<String, String> nimi = new HashMap<String, String>();
  private List<HakuaikaV1RDTO> hakuaikas;

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public Map<String, String> getNimi() {
    return nimi;
  }

  public void setNimi(Map<String, String> nimi) {
    this.nimi = nimi;
  }

  public List<HakuaikaV1RDTO> getHakuaikas() {
    if (hakuaikas == null) {
      hakuaikas = new ArrayList<HakuaikaV1RDTO>();
    }
    return hakuaikas;
  }

  public void setHakuaikas(List<HakuaikaV1RDTO> hakuaikas) {
    this.hakuaikas = hakuaikas;
  }
}
