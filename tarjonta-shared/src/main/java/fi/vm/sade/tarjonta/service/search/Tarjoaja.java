package fi.vm.sade.tarjonta.service.search;

public class Tarjoaja {

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public Nimi getNimi() {
    return nimi;
  }

  public void setNimi(Nimi nimi) {
    this.nimi = nimi;
  }

  private String oid;
  private Nimi nimi;
}
