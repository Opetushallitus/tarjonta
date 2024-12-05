package fi.vm.sade.tarjonta.service.types;

import java.io.Serializable;
import java.util.List;

public class NimettyMonikielinenTekstiTyyppi extends MonikielinenTekstiTyyppi
    implements Serializable {

  private static final long serialVersionUID = 100L;
  protected String tunniste;

  /** Default no-arg constructor */
  public NimettyMonikielinenTekstiTyyppi() {
    super();
  }

  /** Fully-initialising value constructor */
  public NimettyMonikielinenTekstiTyyppi(
      final List<MonikielinenTekstiTyyppi.Teksti> teksti, final String tunniste) {
    super(teksti);
    this.tunniste = tunniste;
  }

  /**
   * Gets the value of the tunniste property.
   *
   * @return possible object is {@link String }
   */
  public String getTunniste() {
    return tunniste;
  }

  /**
   * Sets the value of the tunniste property.
   *
   * @param value allowed object is {@link String }
   */
  public void setTunniste(String value) {
    this.tunniste = value;
  }
}
