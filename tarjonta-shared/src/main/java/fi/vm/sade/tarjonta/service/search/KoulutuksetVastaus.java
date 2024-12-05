package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KoulutuksetVastaus implements Serializable {

  private static final long serialVersionUID = 100L;
  private List<KoulutusPerustieto> koulutukset = new ArrayList<KoulutusPerustieto>();

  public List<KoulutusPerustieto> getKoulutukset() {
    return koulutukset;
  }

  public void setKoulutukset(List<KoulutusPerustieto> koulutukset) {
    this.koulutukset = koulutukset;
  }

  public int getHitCount() {
    return koulutukset.size();
  }
}
