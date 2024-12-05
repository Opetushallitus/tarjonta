package fi.vm.sade.tarjonta.service.types;

import java.io.Serializable;

public class WebLinkkiTyyppi implements Serializable {

  private static final long serialVersionUID = 100L;
  protected String tyyppi;
  protected String kieli;
  protected String uri;

  /** Default no-arg constructor */
  public WebLinkkiTyyppi() {
    super();
  }

  /** Fully-initialising value constructor */
  public WebLinkkiTyyppi(final String tyyppi, final String kieli, final String uri) {
    this.tyyppi = tyyppi;
    this.kieli = kieli;
    this.uri = uri;
  }

  /**
   * Gets the value of the tyyppi property.
   *
   * @return possible object is {@link String }
   */
  public String getTyyppi() {
    return tyyppi;
  }

  /**
   * Sets the value of the tyyppi property.
   *
   * @param value allowed object is {@link String }
   */
  public void setTyyppi(String value) {
    this.tyyppi = value;
  }

  /**
   * Gets the value of the kieli property.
   *
   * @return possible object is {@link String }
   */
  public String getKieli() {
    return kieli;
  }

  /**
   * Sets the value of the kieli property.
   *
   * @param value allowed object is {@link String }
   */
  public void setKieli(String value) {
    this.kieli = value;
  }

  /**
   * Gets the value of the uri property.
   *
   * @return possible object is {@link String }
   */
  public String getUri() {
    return uri;
  }

  /**
   * Sets the value of the uri property.
   *
   * @param value allowed object is {@link String }
   */
  public void setUri(String value) {
    this.uri = value;
  }
}
