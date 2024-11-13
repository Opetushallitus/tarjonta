package fi.vm.sade.tarjonta.service.resources.v1.dto;

import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import io.swagger.annotations.ApiModel;
import java.util.*;

@ApiModel(
    value = "Näyttää hakukohteeseen valittujen koulutustusmoduulien toteutuksien yhteensopivuuden.")
public class ValitutKoulutuksetV1RDTO extends BaseV1RDTO {

  private static final long serialVersionUID = 1L;

  // <komotoOid, <invalid with komoto oids>>
  private Map<String, Set<String>> oidConflictingWithOids;
  private Boolean valid = false;
  private List<NimiJaOidRDTO> names;
  private Set<String> toteutustyyppis;

  public Map<String, Set<String>> getOidConflictingWithOids() {
    if (oidConflictingWithOids == null) {
      oidConflictingWithOids = new HashMap<String, Set<String>>();
    }

    return oidConflictingWithOids;
  }

  public void setOidConflictingWithOids(Map<String, Set<String>> selectedOids) {
    this.oidConflictingWithOids = selectedOids;
  }

  public Boolean getValid() {
    return valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }

  public List<NimiJaOidRDTO> getNames() {
    return names;
  }

  public void setNames(List<NimiJaOidRDTO> names) {
    this.names = names;
  }

  public Set<String> getToteutustyyppis() {
    return toteutustyyppis;
  }

  public void setToteutustyyppis(Set<String> toteutustyyppis) {
    this.toteutustyyppis = toteutustyyppis;
  }
}
