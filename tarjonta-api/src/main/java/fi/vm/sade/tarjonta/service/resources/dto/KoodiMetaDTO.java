package fi.vm.sade.tarjonta.service.resources.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class KoodiMetaDTO implements Serializable {

  private String koodiUri;
  private Map<String, String> _meta = new HashMap<String, String>();

  public String getKoodiUri() {
    return koodiUri;
  }

  public void setKoodiUri(String koodiUri) {
    this.koodiUri = koodiUri;
  }

  public Map<String, String> getMeta() {
    if (_meta == null) {
      _meta = new HashMap<String, String>();
    }
    return _meta;
  }

  public void setMeta(Map<String, String> _meta) {
    this._meta = _meta;
  }
}
