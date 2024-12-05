package fi.vm.sade.tarjonta.shared.organisaatio;

import java.util.List;

public class OrganisaatioKelaDTO {
  private String oid;
  private String oppilaitosKoodi;
  private List<OrganisaatioKelaDTO> children;

  public List<OrganisaatioKelaDTO> getChildren() {
    return children;
  }

  public String getOppilaitosKoodi() {
    return oppilaitosKoodi;
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public void setOppilaitosKoodi(String oppilaitosKoodi) {
    this.oppilaitosKoodi = oppilaitosKoodi;
  }

  public void setChildren(List<OrganisaatioKelaDTO> children) {
    this.children = children;
  }
}
