package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValintakoeRDTO extends BaseRDTO {

  private String valintakoeId;
  private String _tyyppiUri;
  private Map<String, String> _lisanaytot;
  private Map<String, String> _kuvaus;
  private List<ValintakoeAjankohtaRDTO> _valintakoeAjankohtas =
      new ArrayList<ValintakoeAjankohtaRDTO>();
  private List<ValintakoePisterajaRDTO> _valintakoePisterajas =
      new ArrayList<ValintakoePisterajaRDTO>();

  public String getTyyppiUri() {
    return _tyyppiUri;
  }

  public void setTyyppiUri(String _tyyppiUri) {
    this._tyyppiUri = _tyyppiUri;
  }

  public Map<String, String> getLisanaytot() {
    return _lisanaytot;
  }

  public void setLisanaytot(Map<String, String> _lisanaytot) {
    this._lisanaytot = _lisanaytot;
  }

  public Map<String, String> getKuvaus() {
    return _kuvaus;
  }

  public void setKuvaus(Map<String, String> _kuvaus) {
    this._kuvaus = _kuvaus;
  }

  public List<ValintakoeAjankohtaRDTO> getValintakoeAjankohtas() {
    return _valintakoeAjankohtas;
  }

  public void setValintakoeAjankohtas(List<ValintakoeAjankohtaRDTO> _valintakoeAjankohtas) {
    this._valintakoeAjankohtas = _valintakoeAjankohtas;
  }

  public List<ValintakoePisterajaRDTO> getValintakoePisterajas() {
    return _valintakoePisterajas;
  }

  public void setValintakoePisterajas(List<ValintakoePisterajaRDTO> _valintakoePisterajas) {
    this._valintakoePisterajas = _valintakoePisterajas;
  }

  public String getValintakoeId() {
    return valintakoeId;
  }

  public void setValintakoeId(String valintakoeId) {
    this.valintakoeId = valintakoeId;
  }
}
