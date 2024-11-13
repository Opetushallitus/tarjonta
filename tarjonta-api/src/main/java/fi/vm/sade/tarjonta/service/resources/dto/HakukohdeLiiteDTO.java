package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.Date;
import java.util.Map;

public class HakukohdeLiiteDTO extends BaseRDTO {

  private Date erapaiva;
  private Map<String, String> kuvaus;
  private String liitteenTyyppiUri;
  private String liitteenTyyppiKoodistonNimi;
  private String sahkoinenToimitusosoite;
  private OsoiteRDTO toimitusosoite;

  public Date getErapaiva() {
    return erapaiva;
  }

  public void setErapaiva(Date erapaiva) {
    this.erapaiva = erapaiva;
  }

  public Map<String, String> getKuvaus() {
    return kuvaus;
  }

  public void setKuvaus(Map<String, String> kuvaus) {
    this.kuvaus = kuvaus;
  }

  public String getLiitteenTyyppiKoodistonNimi() {
    return liitteenTyyppiKoodistonNimi;
  }

  public void setLiitteenTyyppiKoodistonNimi(String liitteenTyyppiKoodistonNimi) {
    this.liitteenTyyppiKoodistonNimi = liitteenTyyppiKoodistonNimi;
  }

  public String getLiitteenTyyppiUri() {
    return liitteenTyyppiUri;
  }

  public void setLiitteenTyyppiUri(String liitteenTyyppiUri) {
    this.liitteenTyyppiUri = liitteenTyyppiUri;
  }

  public String getSahkoinenToimitusosoite() {
    return sahkoinenToimitusosoite;
  }

  public void setSahkoinenToimitusosoite(String sahkoinenToimitusosoite) {
    this.sahkoinenToimitusosoite = sahkoinenToimitusosoite;
  }

  public OsoiteRDTO getToimitusosoite() {
    return toimitusosoite;
  }

  public void setToimitusosoite(OsoiteRDTO toimitusosoite) {
    this.toimitusosoite = toimitusosoite;
  }
}
