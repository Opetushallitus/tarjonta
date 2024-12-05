package fi.vm.sade.tarjonta.service.resources.v1.dto;

import io.swagger.v3.oas.annotations.Parameter;

import java.util.HashMap;

public class KuvausV1RDTO extends BaseV1RDTO {

  @Parameter(name = "Unique id of the description")
  private String kuvauksenTunniste;

  @Parameter(
          name = "HashMap containing description names and description name language",
      required = true)
  private HashMap<String, String> kuvauksenNimet;

  @Parameter(name = "Organization type on which description is bound to", required = true)
  private String organisaatioTyyppi;

  @Parameter(
          name = "Type of the description",
      required = true,
      example = "valintaperustekuvaus,SORA")
  private String kuvauksenTyyppi;

  @Parameter(name = "Period-Uri of the description", required = true)
  private String kausi;

  @Parameter(name = "Year of the description", required = true)
  private Integer vuosi;

  @Parameter(name = "HashMap containing descriptions and description languages")
  private HashMap<String, String> kuvaukset;

  @Parameter(name = "Koodisto-key used by 2.-degree")
  private String avain;

  public HashMap<String, String> getKuvauksenNimet() {
    return kuvauksenNimet;
  }

  public void setKuvauksenNimet(HashMap<String, String> kuvauksenNimet) {
    this.kuvauksenNimet = kuvauksenNimet;
  }

  public String getOrganisaatioTyyppi() {
    return organisaatioTyyppi;
  }

  public void setOrganisaatioTyyppi(String organisaatioTyyppi) {
    this.organisaatioTyyppi = organisaatioTyyppi;
  }

  public String getKuvauksenTyyppi() {
    return kuvauksenTyyppi;
  }

  public void setKuvauksenTyyppi(String kuvauksenTyyppi) {
    this.kuvauksenTyyppi = kuvauksenTyyppi;
  }

  public HashMap<String, String> getKuvaukset() {
    return kuvaukset;
  }

  public void setKuvaukset(HashMap<String, String> kuvaukset) {
    this.kuvaukset = kuvaukset;
  }

  public String getKuvauksenTunniste() {
    return kuvauksenTunniste;
  }

  public void setKuvauksenTunniste(String kuvauksenTunniste) {
    this.kuvauksenTunniste = kuvauksenTunniste;
  }

  public String getKausi() {
    return kausi;
  }

  public void setKausi(String kausi) {
    this.kausi = kausi;
  }

  public Integer getVuosi() {
    return vuosi;
  }

  public void setVuosi(Integer vuosi) {
    this.vuosi = vuosi;
  }

  public void setAvain(String avain) {
    this.avain = avain;
  }

  public String getAvain() {
    return avain;
  }
}
