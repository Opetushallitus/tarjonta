package fi.vm.sade.tarjonta.service.resources.v1.dto;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;

@ApiModel(value = "V1 HakukohdeRyhma REST-api model, used by KK-ui")
public class HakukohdeRyhmaV1RDTO implements Serializable {

  public enum ActionCode {
    LISAA,
    POISTA
  }

  private String hakukohdeOid;
  private String ryhmaOid;
  private ActionCode toiminto = ActionCode.LISAA;

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public void setHakukohdeOid(String hakukohdeOid) {
    this.hakukohdeOid = hakukohdeOid;
  }

  public String getRyhmaOid() {
    return ryhmaOid;
  }

  public void setRyhmaOid(String ryhmaOid) {
    this.ryhmaOid = ryhmaOid;
  }

  public ActionCode getToiminto() {
    return toiminto;
  }

  public void setToiminto(ActionCode toiminto) {
    this.toiminto = toiminto;
  }

  @Override
  public String toString() {
    return "toiminto: "
        + getToiminto()
        + ", ryhmaOid: "
        + getRyhmaOid()
        + ", hakukohdeOid: "
        + getHakukohdeOid();
  }
}
