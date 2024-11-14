package fi.vm.sade.tarjonta.service.types;

import java.io.Serializable;

public class KoulutuksenKestoTyyppi implements Serializable {

  private static final long serialVersionUID = 100L;
  protected String arvo;
  protected String yksikko;

  /** Default no-arg constructor */
  public KoulutuksenKestoTyyppi() {
    super();
  }

  /** Fully-initialising value constructor */
  public KoulutuksenKestoTyyppi(final String arvo, final String yksikko) {
    this.arvo = arvo;
    this.yksikko = yksikko;
  }

  /**
   * Gets the value of the arvo property.
   *
   * @return possible object is {@link String }
   */
  public String getArvo() {
    return arvo;
  }

  /**
   * Sets the value of the arvo property.
   *
   * @param value allowed object is {@link String }
   */
  public void setArvo(String value) {
    this.arvo = value;
  }

  /**
   * Gets the value of the yksikko property.
   *
   * @return possible object is {@link String }
   */
  public String getYksikko() {
    return yksikko;
  }

  /**
   * Sets the value of the yksikko property.
   *
   * @param value allowed object is {@link String }
   */
  public void setYksikko(String value) {
    this.yksikko = value;
  }
}
