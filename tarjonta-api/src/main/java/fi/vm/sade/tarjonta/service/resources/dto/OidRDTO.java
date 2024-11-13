package fi.vm.sade.tarjonta.service.resources.dto;

import java.io.Serializable;

public class OidRDTO implements Serializable {

  private String oid;

  public OidRDTO() {}

  public OidRDTO(String oid) {
    this.oid = oid;
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }
}
