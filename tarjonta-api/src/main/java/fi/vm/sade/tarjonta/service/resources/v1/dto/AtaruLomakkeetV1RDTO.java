package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AtaruLomakkeetV1RDTO implements Serializable {

  private String avain;
  private List<AtaruLomakeHakuV1RDTO> haut;

  public String getAvain() {
    return avain;
  }

  public void setAvain(String avain) {
    this.avain = avain;
  }

  public List<AtaruLomakeHakuV1RDTO> getHaut() {
    if (haut == null) {
      haut = new ArrayList<>();
    }
    return haut;
  }

  public void setHaut(List<AtaruLomakeHakuV1RDTO> haut) {
    this.haut = haut;
  }
}
