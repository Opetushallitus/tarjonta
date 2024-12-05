package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.util.Objects;

public class OppiaineV1RDTO {

  private String oppiaine;
  private String kieliKoodi;

  public OppiaineV1RDTO() {}

  public OppiaineV1RDTO(String kieliKoodi, String oppiaine) {
    this.kieliKoodi = kieliKoodi;
    this.oppiaine = oppiaine;
  }

  public String getOppiaine() {
    return oppiaine;
  }

  public void setOppiaine(String oppiaine) {
    this.oppiaine = oppiaine;
  }

  public String getKieliKoodi() {
    return kieliKoodi;
  }

  public void setKieliKoodi(String kieliKoodi) {
    this.kieliKoodi = kieliKoodi;
  }

  @Override
  public int hashCode() {
    return getOppiaine().hashCode() + getKieliKoodi().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof OppiaineV1RDTO) {
      OppiaineV1RDTO otherOppiaine = (OppiaineV1RDTO) obj;
      return Objects.equals(getOppiaine(), otherOppiaine.getOppiaine())
          && Objects.equals(getKieliKoodi(), otherOppiaine.getKieliKoodi());
    }
    return false;
  }
}
