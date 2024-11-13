package fi.vm.sade.tarjonta.service.resources.dto;

import java.io.Serializable;
import java.util.List;

public class HakukohdeTulosRDTO implements Serializable {

  private int kokonaismaara;
  private List<HakukohdeNimiRDTO> tulokset;

  public HakukohdeTulosRDTO() {}

  public HakukohdeTulosRDTO(int kokonaismaara, List<HakukohdeNimiRDTO> tulokset) {
    this.kokonaismaara = kokonaismaara;
    this.tulokset = tulokset;
  }

  public int getKokonaismaara() {
    return kokonaismaara;
  }

  public void setKokonaismaara(int kokonaismaara) {
    this.kokonaismaara = kokonaismaara;
  }

  public List<HakukohdeNimiRDTO> getTulokset() {
    return tulokset;
  }

  public void setTulokset(List<HakukohdeNimiRDTO> tulokset) {
    this.tulokset = tulokset;
  }
}
