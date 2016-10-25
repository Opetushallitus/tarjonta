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

public class AtaruFormUsageV1RDTO implements Serializable {

  private String ataruFormKey;
  private List<String> hakuOids;

  public String getAtaruFormKey() {
    return ataruFormKey;
  }

  public void setAtaruFormKey(String ataruFormKey) {
    this.ataruFormKey = ataruFormKey;
  }

  public List<String> getHakuOids() {
    if (hakuOids == null) {
      hakuOids = new ArrayList<String>();
    }
    return hakuOids;
  }

  public void setHakuOids(List<String> hakuOids) {
    this.hakuOids = hakuOids;
  }
}
