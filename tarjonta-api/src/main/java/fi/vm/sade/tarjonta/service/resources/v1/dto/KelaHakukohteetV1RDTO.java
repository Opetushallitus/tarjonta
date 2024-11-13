package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KelaHakukohteetV1RDTO implements Serializable {

  private List<KelaHakukohdeV1RDTO> hakukohteet = new ArrayList<>();

  public KelaHakukohteetV1RDTO() {}

  public KelaHakukohteetV1RDTO(List<KelaHakukohdeV1RDTO> hakukohteet) {
    this.hakukohteet = hakukohteet;
  }

  public List<KelaHakukohdeV1RDTO> getHakukohteet() {
    return hakukohteet;
  }

  public void setHakukohteet(List<KelaHakukohdeV1RDTO> hakukohteet) {
    this.hakukohteet = hakukohteet;
  }
}
