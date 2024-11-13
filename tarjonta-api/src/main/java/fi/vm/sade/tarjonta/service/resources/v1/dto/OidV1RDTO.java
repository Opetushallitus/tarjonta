package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;

public class OidV1RDTO implements Serializable {

  private String _oid = null;
  private int _version = 0;

  public OidV1RDTO() {
    super();
  }

  public OidV1RDTO(String oid) {
    this();
    setOid(oid);
  }

  public OidV1RDTO(String oid, int version) {
    this();
    setOid(oid);
    setVersion(version);
  }

  public String getOid() {
    return _oid;
  }

  public void setOid(String oid) {
    this._oid = oid;
  }

  public int getVersion() {
    return _version;
  }

  public void setVersion(int _version) {
    this._version = _version;
  }
}
