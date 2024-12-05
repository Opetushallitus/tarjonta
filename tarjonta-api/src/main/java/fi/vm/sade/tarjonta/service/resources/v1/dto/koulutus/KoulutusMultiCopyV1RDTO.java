package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import java.util.ArrayList;
import java.util.List;

public class KoulutusMultiCopyV1RDTO extends KoulutusCopyV1RDTO {

  private List<String> komotoOids = new ArrayList<String>();

  public KoulutusMultiCopyV1RDTO() {}

  public List<String> getKomotoOids() {
    return komotoOids;
  }

  public void setKomotoOids(List<String> komotoOids) {
    this.komotoOids = komotoOids;
  }
}
