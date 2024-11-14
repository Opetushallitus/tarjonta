package fi.vm.sade.tarjonta.service.types;

import java.util.Collections;
import java.util.List;

public class MonikielinenTekstiTyyppi {

  protected List<Teksti> teksti;

  public MonikielinenTekstiTyyppi(List<Teksti> teksti) {
    this.teksti = teksti;
  }

  public MonikielinenTekstiTyyppi() {}

  public List<Teksti> getTeksti() {
    if (teksti == null) {
      teksti = Collections.emptyList();
    }
    return this.teksti;
  }

  public static class Teksti {

    protected String value;
    protected String kieliKoodi;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public String getKieliKoodi() {
      return kieliKoodi;
    }

    public void setKieliKoodi(String value) {
      this.kieliKoodi = value;
    }
  }
}
