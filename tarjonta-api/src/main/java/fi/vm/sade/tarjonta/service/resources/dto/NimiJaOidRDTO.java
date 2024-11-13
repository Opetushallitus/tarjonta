package fi.vm.sade.tarjonta.service.resources.dto;

import java.io.Serializable;
import java.util.Map;

public class NimiJaOidRDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Map<String, String> nimi;
  private String oid;
  private String relatedOid;

  public NimiJaOidRDTO() {}

  public NimiJaOidRDTO(Map<String, String> nimi, String oid) {
    super();
    this.nimi = nimi;
    this.oid = oid;
  }

  public NimiJaOidRDTO(Map<String, String> nimi, String oid, String relatedOidParam) {
    super();
    this.nimi = nimi;
    this.oid = oid;
    this.relatedOid = relatedOidParam;
  }

  public Map<String, String> getNimi() {
    return nimi;
  }

  public void setNimi(Map<String, String> nimi) {
    this.nimi = nimi;
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getRelatedOid() {
    return relatedOid;
  }

  public void setRelatedOid(String relatedOid) {
    this.relatedOid = relatedOid;
  }
}
