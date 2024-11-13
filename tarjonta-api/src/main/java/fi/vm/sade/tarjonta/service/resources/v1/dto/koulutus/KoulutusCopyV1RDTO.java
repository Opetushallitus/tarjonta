package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.CopyMode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KoulutusCopyV1RDTO implements Serializable {

  private CopyMode mode;
  private List<String> organisationOids = new ArrayList<String>();

  public KoulutusCopyV1RDTO() {}

  public List<String> getOrganisationOids() {
    return organisationOids;
  }

  public void setOrganisationOids(List<String> organisationOids) {
    this.organisationOids = organisationOids;
  }

  public CopyMode getMode() {
    return mode;
  }

  public void setMode(CopyMode mode) {
    this.mode = mode;
  }
}
