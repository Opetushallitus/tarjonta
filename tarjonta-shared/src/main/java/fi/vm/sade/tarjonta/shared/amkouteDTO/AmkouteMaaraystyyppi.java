package fi.vm.sade.tarjonta.shared.amkouteDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AmkouteMaaraystyyppi {
  private AmkouteMaaraystyyppiValue tunniste;

  public AmkouteMaaraystyyppiValue getTunniste() {
    return tunniste;
  }

  public void setTunniste(AmkouteMaaraystyyppiValue tunniste) {
    this.tunniste = tunniste;
  }
}
