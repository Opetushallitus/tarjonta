package fi.vm.sade.tarjonta.service.types;

import java.util.Collections;
import java.util.List;

public class YhteyshenkiloTyyppi {

  protected String henkiloOid;
  protected String nimi;
  protected String titteli;
  protected String sahkoposti;
  protected String puhelin;
  protected List<String> kielet;
  protected HenkiloTyyppi henkiloTyyppi;

  public String getHenkiloOid() {
    return henkiloOid;
  }

  public void setHenkiloOid(String value) {
    this.henkiloOid = value;
  }

  public String getNimi() {
    return nimi;
  }

  public void setNimi(String value) {
    this.nimi = value;
  }

  public String getTitteli() {
    return titteli;
  }

  public void setTitteli(String value) {
    this.titteli = value;
  }

  public String getSahkoposti() {
    return sahkoposti;
  }

  public void setSahkoposti(String value) {
    this.sahkoposti = value;
  }

  public String getPuhelin() {
    return puhelin;
  }

  public void setPuhelin(String value) {
    this.puhelin = value;
  }

  public List<String> getKielet() {
    if (kielet == null) {
      kielet = Collections.emptyList();
    }
    return this.kielet;
  }

  public HenkiloTyyppi getHenkiloTyyppi() {
    return henkiloTyyppi;
  }

  public void setHenkiloTyyppi(HenkiloTyyppi value) {
    this.henkiloTyyppi = value;
  }
}
