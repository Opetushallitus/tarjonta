package fi.vm.sade.tarjonta.service.search;

import java.util.List;

/** Hakutulos ryhmiteltynä organisaatioittain. */
public class OrganisaatioHakukohdeGroup {
  public OrganisaatioHakukohdeGroup(String organisaatioOid, Long hitCount) {
    super();
    this.hitCount = hitCount;
    this.organisaatioOid = organisaatioOid;
  }

  public String getOrganisaatioNimi() {
    return organisaatioNimi;
  }

  public Long getHitCount() {
    return hitCount;
  }

  private String organisaatioNimi;

  public void setOrganisaatioNimi(String organisaatioNimi) {
    this.organisaatioNimi = organisaatioNimi;
  }

  public static int countHits(List<OrganisaatioHakukohdeGroup> groups) {

    int i = 0;
    for (OrganisaatioHakukohdeGroup group : groups) {
      i += group.getHitCount();
    }
    return i;
  }

  private final String organisaatioOid;

  public String getOrganisaatioOid() {
    return organisaatioOid;
  }

  private final Long hitCount;

  @Override
  public String toString() {
    return "OrganisaatioHakukohdeGroup [organisaatioNimi="
        + organisaatioNimi
        + ", organisaatioOid="
        + organisaatioOid
        + ", hitCount="
        + hitCount
        + "]";
  }
}
