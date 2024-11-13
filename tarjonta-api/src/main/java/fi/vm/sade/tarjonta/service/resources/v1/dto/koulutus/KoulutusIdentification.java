package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

public class KoulutusIdentification {

  private String oid;
  private String uniqueExternalId; // oppilaitoksen käyttämä oma globaalisti uniikki tunniste

  public KoulutusIdentification() {}

  public KoulutusIdentification(String oid, String uniqueExternalId) {
    this.oid = oid;
    this.uniqueExternalId = uniqueExternalId;
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getUniqueExternalId() {
    return uniqueExternalId;
  }

  public void setUniqueExternalId(String uniqueExternalId) {
    this.uniqueExternalId = uniqueExternalId;
  }
}
