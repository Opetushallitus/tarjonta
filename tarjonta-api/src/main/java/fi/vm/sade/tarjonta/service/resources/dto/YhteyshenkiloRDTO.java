package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Yhteyshenkilo information for the REST api.
 *
 * @author mlyly
 */
public class YhteyshenkiloRDTO extends BaseRDTO {

  private String _puhelin;
  private String _titteli;
  private String _tyyppi;
  private String _henkiloOid;
  private String _email;
  private String _nimi;
  private List<String> _kielet;

  public String getPuhelin() {
    return _puhelin;
  }

  public void setPuhelin(String puhelin) {
    this._puhelin = puhelin;
  }

  public String getTitteli() {
    return _titteli;
  }

  public void setTitteli(String titteli) {
    this._titteli = titteli;
  }

  public String getTyyppi() {
    return _tyyppi;
  }

  public void setTyyppi(String tyyppi) {
    this._tyyppi = tyyppi;
  }

  public String getHenkiloOid() {
    return _henkiloOid;
  }

  public void setHenkiloOid(String henkiloOid) {
    this._henkiloOid = henkiloOid;
  }

  public String getEmail() {
    return _email;
  }

  public void setEmail(String email) {
    this._email = email;
  }

  public String getNimi() {
    return _nimi;
  }

  public void setNimi(String nimi) {
    this._nimi = nimi;
  }

  public List<String> getKielet() {
    if (_kielet == null) {
      _kielet = new ArrayList<String>();
    }
    return _kielet;
  }

  public void setKielet(List<String> kielet) {
    this._kielet = kielet;
  }
}
