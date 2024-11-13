package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import io.swagger.annotations.ApiModel;
import java.util.HashSet;
import java.util.Set;

@ApiModel(value = "KOulutustyyppien näyttämiseen liittyvä rajapintaolio")
public class KoulutustyyppiKoosteV1RDTO extends KoodiV1RDTO {

  private static final long serialVersionUID = 1L;
  private Set<String> koulutustyyppiUris;
  private boolean modules;

  public Set<String> getKoulutustyyppiUris() {
    if (koulutustyyppiUris == null) {
      koulutustyyppiUris = new HashSet<String>();
    }

    return koulutustyyppiUris;
  }

  public void setKoulutustyyppiUris(Set<String> koulutustyyppiUris) {
    this.koulutustyyppiUris = koulutustyyppiUris;
  }

  public boolean isModules() {
    return modules;
  }

  public void setModules(boolean modules) {
    this.modules = modules;
  }
}
