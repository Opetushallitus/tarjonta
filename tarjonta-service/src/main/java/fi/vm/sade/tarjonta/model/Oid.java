/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import org.apache.commons.lang.StringUtils;

/** Container for OID. Use as @Embedded or inside @ElementCollection. */
@Embeddable
public class Oid implements Serializable {

  private static final long serialVersionUID = 6886982838559131251L;

  @Column(name = "oid", nullable = false)
  private String oid;

  protected Oid() {}

  public Oid(String oid) {
    assert StringUtils.isNotEmpty(oid) : "oid cannot be null or empty string";
    this.oid = oid;
  }

  public String getOid() {
    return oid;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Oid other = (Oid) obj;
    if ((this.oid == null) ? (other.oid != null) : !this.oid.equals(other.oid)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 47 * hash + (this.oid != null ? this.oid.hashCode() : 0);
    return hash;
  }
}
