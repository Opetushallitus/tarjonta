package fi.vm.sade.tarjonta.service.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KoodistoKoodiTyyppi implements Serializable {

  private static final long serialVersionUID = 100L;
  protected String uri;
  protected Integer versio;
  protected String arvo;
  protected List<KoodistoKoodiTyyppi.Nimi> nimi;

  /** Default no-arg constructor */
  public KoodistoKoodiTyyppi() {
    super();
  }

  /** Fully-initialising value constructor */
  public KoodistoKoodiTyyppi(
      final String uri,
      final Integer versio,
      final String arvo,
      final List<KoodistoKoodiTyyppi.Nimi> nimi) {
    this.uri = uri;
    this.versio = versio;
    this.arvo = arvo;
    this.nimi = nimi;
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

  /**
   * Gets the value of the versio property.
   *
   * @return possible object is {@link Integer }
   */
  public Integer getVersio() {
    return versio;
  }

  /**
   * Sets the value of the versio property.
   *
   * @param value allowed object is {@link Integer }
   */
  public void setVersio(Integer value) {
    this.versio = value;
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
   * Gets the value of the nimi property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the nimi property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getNimi().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link KoodistoKoodiTyyppi.Nimi }
   */
  public List<KoodistoKoodiTyyppi.Nimi> getNimi() {
    if (nimi == null) {
      nimi = new ArrayList<KoodistoKoodiTyyppi.Nimi>();
    }
    return this.nimi;
  }

  public static class Nimi implements Serializable {

    private static final long serialVersionUID = 100L;
    protected String value;
    protected String kieli;

    /** Default no-arg constructor */
    public Nimi() {
      super();
    }

    /** Fully-initialising value constructor */
    public Nimi(final String value, final String kieli) {
      this.value = value;
      this.kieli = kieli;
    }

    /**
     * Gets the value of the value property.
     *
     * @return possible object is {@link String }
     */
    public String getValue() {
      return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is {@link String }
     */
    public void setValue(String value) {
      this.value = value;
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
  }
}
