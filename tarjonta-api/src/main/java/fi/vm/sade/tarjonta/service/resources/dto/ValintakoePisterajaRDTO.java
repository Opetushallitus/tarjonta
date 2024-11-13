package fi.vm.sade.tarjonta.service.resources.dto;

public class ValintakoePisterajaRDTO extends BaseRDTO {

  private double _alinHyvaksyttyPistemaara;
  private double _alinPistemaara;
  private double _ylinPistemaara;
  private String _tyyppi;

  public double getAlinHyvaksyttyPistemaara() {
    return _alinHyvaksyttyPistemaara;
  }

  public void setAlinHyvaksyttyPistemaara(double _alinHyvaksyttyPistemaara) {
    this._alinHyvaksyttyPistemaara = _alinHyvaksyttyPistemaara;
  }

  public double getAlinPistemaara() {
    return _alinPistemaara;
  }

  public void setAlinPistemaara(double _alinPistemaara) {
    this._alinPistemaara = _alinPistemaara;
  }

  public double getYlinPistemaara() {
    return _ylinPistemaara;
  }

  public void setYlinPistemaara(double _ylinPistemaara) {
    this._ylinPistemaara = _ylinPistemaara;
  }

  public String getTyyppi() {
    return _tyyppi;
  }

  public void setTyyppi(String tyyppi) {
    this._tyyppi = tyyppi;
  }
}
