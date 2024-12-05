/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.tarjonta.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@JsonIgnoreProperties({"id", "version"})
@Table(name = KoulutusOwner.TABLE_NAME)
public class KoulutusOwner extends TarjontaBaseEntity {

  public static final String TABLE_NAME = "koulutusmoduuli_toteutus_owner";

  public static final String TARJOAJA = "TARJOAJA";
  public static final String JARJESTAJA = "JARJESTAJA";

  @Column(name = "owneroid")
  private String ownerOid;

  @Column(name = "ownertype")
  private String ownerType = TARJOAJA;

  public String getOwnerOid() {
    return ownerOid;
  }

  public void setOwnerOid(String ownerOid) {
    this.ownerOid = ownerOid;
  }

  public String getOwnerType() {
    return ownerType;
  }

  public void setOwnerType(String ownerType) {
    this.ownerType = ownerType;
  }

  public boolean isTarjoaja() {
    return TARJOAJA.equals(getOwnerType());
  }
}
