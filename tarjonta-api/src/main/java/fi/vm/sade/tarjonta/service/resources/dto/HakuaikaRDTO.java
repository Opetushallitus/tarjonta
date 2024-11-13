package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.Date;

public class HakuaikaRDTO extends BaseRDTO {

  String _nimi;
  Date _alkuPvm;
  Date _loppuPvm;

  public String getNimi() {
    return _nimi;
  }

  public void setNimi(String name) {
    this._nimi = name;
  }

  public Date getAlkuPvm() {
    return _alkuPvm;
  }

  public void setAlkuPvm(Date alkuPvm) {
    this._alkuPvm = alkuPvm;
  }

  public Date getLoppuPvm() {
    return _loppuPvm;
  }

  public void setLoppuPvm(Date loppuPvm) {
    this._loppuPvm = loppuPvm;
  }
}
