package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.Date;

public class ValintakoeAjankohtaRDTO extends BaseRDTO {

  private OsoiteRDTO _osoite;
  private Date _alkaa;
  private Date _loppuu;
  private String _lisatiedot;
  private boolean kellonaikaKaytossa = true;

  public OsoiteRDTO getOsoite() {
    if (_osoite == null) {
      _osoite = new OsoiteRDTO();
    }
    return _osoite;
  }

  public void setOsoite(OsoiteRDTO _osoite) {
    this._osoite = _osoite;
  }

  public Date getAlkaa() {
    return _alkaa;
  }

  public void setAlkaa(Date _alkaa) {
    this._alkaa = _alkaa;
  }

  public Date getLoppuu() {
    return _loppuu;
  }

  public void setLoppuu(Date _loppuu) {
    this._loppuu = _loppuu;
  }

  public String getLisatiedot() {
    return _lisatiedot;
  }

  public void setLisatiedot(String _lisatiedot) {
    this._lisatiedot = _lisatiedot;
  }

  public boolean isKellonaikaKaytossa() {
    return kellonaikaKaytossa;
  }

  public void setKellonaikaKaytossa(boolean kellonaikaKaytossa) {
    this.kellonaikaKaytossa = kellonaikaKaytossa;
  }
}
