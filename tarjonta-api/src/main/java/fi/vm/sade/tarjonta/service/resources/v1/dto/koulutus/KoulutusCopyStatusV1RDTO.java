package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import java.io.Serializable;

public class KoulutusCopyStatusV1RDTO implements Serializable {

  private String oid;
  private Boolean success = true;
  private String organisationOid;

  public KoulutusCopyStatusV1RDTO() {}

  public KoulutusCopyStatusV1RDTO(String oid, String organisationOid) {
    this.oid = oid;
    this.organisationOid = organisationOid;
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public Boolean isSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public String getOrganisationOid() {
    return organisationOid;
  }

  public void setOrganisationOid(String organisationOid) {
    this.organisationOid = organisationOid;
  }
}
