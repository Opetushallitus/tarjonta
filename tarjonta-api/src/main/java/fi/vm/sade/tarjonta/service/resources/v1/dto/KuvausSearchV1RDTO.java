package fi.vm.sade.tarjonta.service.resources.v1.dto;

import io.swagger.v3.oas.annotations.Parameter;

public class KuvausSearchV1RDTO {
  @Parameter(name = "Name or part of the description name")
  private String hakusana;

  @Parameter(name = "Learning institution type")
  private String oppilaitosTyyppi;

  @Parameter(name = "Uri of the season")
  private String kausiUri;

  @Parameter(name = "Year")
  private Integer vuosi;

  @Parameter(name = "Avain")
  private String avain;

  public String getHakusana() {
    return hakusana;
  }

  public void setHakusana(String hakusana) {
    this.hakusana = hakusana;
  }

  public String getOppilaitosTyyppi() {
    return oppilaitosTyyppi;
  }

  public void setOppilaitosTyyppi(String oppilaitosTyyppi) {
    this.oppilaitosTyyppi = oppilaitosTyyppi;
  }

  public String getKausiUri() {
    return kausiUri;
  }

  public void setKausiUri(String kausiUri) {
    this.kausiUri = kausiUri;
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
