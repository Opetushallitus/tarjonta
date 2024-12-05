package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.Date;
import java.util.List;

public class HakukohdeLiiteRDTO {

  private String liiteKieliUri;
  private String liiteKieli;
  private Date erapaiva;
  private List<TekstiRDTO> kuvaus;
  private String liitteenTyyppiUri;
  private String liitteenTyyppiKoodistoNimi;
  private String sahkoinenToimitusOsoite;
  private OsoiteRDTO toimitusOsoite;

  public Date getErapaiva() {
    return erapaiva;
  }

  public void setErapaiva(Date erapaiva) {
    this.erapaiva = erapaiva;
  }

  public List<TekstiRDTO> getKuvaus() {
    return kuvaus;
  }

  public void setKuvaus(List<TekstiRDTO> kuvaus) {
    this.kuvaus = kuvaus;
  }

  public String getLiitteenTyyppiUri() {
    return liitteenTyyppiUri;
  }

  public void setLiitteenTyyppiUri(String liitteenTyyppiUri) {
    this.liitteenTyyppiUri = liitteenTyyppiUri;
  }

  public String getLiitteenTyyppiKoodistoNimi() {
    return liitteenTyyppiKoodistoNimi;
  }

  public void setLiitteenTyyppiKoodistoNimi(String liitteenTyyppiKoodistoNimi) {
    this.liitteenTyyppiKoodistoNimi = liitteenTyyppiKoodistoNimi;
  }

  public String getSahkoinenToimitusOsoite() {
    return sahkoinenToimitusOsoite;
  }

  public void setSahkoinenToimitusOsoite(String sahkoinenToimitusOsoite) {
    this.sahkoinenToimitusOsoite = sahkoinenToimitusOsoite;
  }

  public OsoiteRDTO getToimitusOsoite() {
    return toimitusOsoite;
  }

  public void setToimitusOsoite(OsoiteRDTO toimitusOsoite) {
    this.toimitusOsoite = toimitusOsoite;
  }

  public String getLiiteKieliUri() {
    return liiteKieliUri;
  }

  public void setLiiteKieliUri(String liiteKieliUri) {
    this.liiteKieliUri = liiteKieliUri;
  }

  public String getLiiteKieli() {
    return liiteKieli;
  }

  public void setLiiteKieli(String liiteKieli) {
    this.liiteKieli = liiteKieli;
  }
}
