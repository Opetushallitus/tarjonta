package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtaruLomakeHakuV1RDTO implements Serializable {
  private String oid;
  private Map<String, String> nimi = new HashMap<>();
  private List<HakuaikaV1RDTO> hakuaikas;

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public Map<String, String> getNimi() {
    return nimi;
  }

  public void setNimi(Map<String, String> nimi) {
    this.nimi = nimi;
  }

  public List<HakuaikaV1RDTO> getHakuaikas() {
    if (hakuaikas == null) {
      hakuaikas = new ArrayList<>();
    }
    return hakuaikas;
  }

  public void setHakuaikas(List<HakuaikaV1RDTO> hakuaikas) {
    this.hakuaikas = hakuaikas;
  }
}
