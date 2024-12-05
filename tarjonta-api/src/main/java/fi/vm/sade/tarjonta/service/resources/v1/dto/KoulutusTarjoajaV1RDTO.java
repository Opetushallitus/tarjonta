package fi.vm.sade.tarjonta.service.resources.v1.dto;

public class KoulutusTarjoajaV1RDTO {

  private String oid;
  private String tarjoajaOid;

  public String getTarjoajaOid() {
    return tarjoajaOid;
  }

  public void setTarjoajaOid(String tarjoajaOid) {
    this.tarjoajaOid = tarjoajaOid;
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }
}
