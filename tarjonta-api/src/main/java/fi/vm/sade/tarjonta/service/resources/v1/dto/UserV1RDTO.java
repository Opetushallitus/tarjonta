package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;

public class UserV1RDTO implements Serializable {

  private String oid;
  private String lang;

  public UserV1RDTO() {}

  public UserV1RDTO(String oid, String lang) {
    this.oid = oid;
    this.lang = lang;
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  @Override
  public String toString() {
    return "[oid : " + oid + ", lang : " + lang + "]";
  }
}
