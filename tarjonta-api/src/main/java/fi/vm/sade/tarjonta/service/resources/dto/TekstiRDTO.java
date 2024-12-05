package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.ArrayList;
import java.util.List;

public class TekstiRDTO {

  private String uri;

  private String nimi;

  private List<KieliNimiRDTO> monikielisetNimet;

  private String arvo;

  private int versio;

  private String teksti;

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getTeksti() {
    return teksti;
  }

  public void setTeksti(String teksti) {
    this.teksti = teksti;
  }

  public String getNimi() {
    return nimi;
  }

  public void setNimi(String nimi) {
    this.nimi = nimi;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TekstiRDTO that = (TekstiRDTO) o;

    if (nimi != null ? !nimi.equals(that.nimi) : that.nimi != null) return false;
    if (!teksti.equals(that.teksti)) return false;
    if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = uri != null ? uri.hashCode() : 0;
    result = 31 * result + (nimi != null ? nimi.hashCode() : 0);
    result = 31 * result + teksti.hashCode();
    return result;
  }

  public String getArvo() {
    return arvo;
  }

  public void setArvo(String arvo) {
    this.arvo = arvo;
  }

  public void addKieliAndNimi(String kieli, String nimi) {
    getMonikielisetNimet().add(new KieliNimiRDTO(kieli, nimi));
  }

  public List<KieliNimiRDTO> getMonikielisetNimet() {
    if (monikielisetNimet == null) {
      monikielisetNimet = new ArrayList<KieliNimiRDTO>();
    }
    return monikielisetNimet;
  }

  public void setMonikielisetNimet(List<KieliNimiRDTO> monikielisetNimet) {
    this.monikielisetNimet = monikielisetNimet;
  }

  public int getVersio() {
    return versio;
  }

  public void setVersio(int versio) {
    this.versio = versio;
  }

  static class KieliNimiRDTO {

    private String kieli;

    private String nimi;

    public KieliNimiRDTO(String kieli, String nimi) {
      this.kieli = kieli;
      this.nimi = nimi;
    }

    public KieliNimiRDTO() {}

    public String getKieli() {
      return kieli;
    }

    public void setKieli(String kieli) {
      this.kieli = kieli;
    }

    public String getNimi() {
      return nimi;
    }

    public void setNimi(String nimi) {
      this.nimi = nimi;
    }
  }
}
