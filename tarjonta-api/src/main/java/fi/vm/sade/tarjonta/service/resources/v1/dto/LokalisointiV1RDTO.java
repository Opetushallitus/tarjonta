package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;

public class LokalisointiV1RDTO implements Serializable {

  private String _kieli;
  private String _kieliUri;
  private String _arvo;

  public LokalisointiV1RDTO() {}

  public LokalisointiV1RDTO(String kieli, String kieliUri, String arvo) {
    this._kieli = kieli;
    this._kieliUri = kieliUri;
    this._arvo = arvo;
  }

  public String getKieli() {
    return _kieli;
  }

  public void setKieli(String _kieli) {
    this._kieli = _kieli;
  }

  public String getKieliUri() {
    return _kieliUri;
  }

  public void setKieliUri(String _kieliUri) {
    this._kieliUri = _kieliUri;
  }

  public String getArvo() {
    return _arvo;
  }

  public void setArvo(String _arvo) {
    this._arvo = _arvo;
  }
}
