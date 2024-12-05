package fi.vm.sade.tarjonta.service.resources.v1.dto;

import fi.vm.sade.tarjonta.service.resources.dto.BaseRDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TarjoajaHakutulosV1RDTO<T extends KoulutusHakutulosV1RDTO> extends BaseRDTO {

  private static final long serialVersionUID = 1L;

  private String oid;
  private Map<String, String> nimi;
  private List<T> tulokset;

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

  public List<T> getTulokset() {
    if (tulokset == null) {
      tulokset = new ArrayList<T>();
    }
    return tulokset;
  }

  public void setTulokset(List<T> tulokset) {
    this.tulokset = tulokset;
  }
}
