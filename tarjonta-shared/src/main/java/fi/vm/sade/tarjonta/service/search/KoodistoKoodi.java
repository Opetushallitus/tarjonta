package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;

/** Yhden Koodisto koodin tietojen kuvaus. */
public class KoodistoKoodi implements Serializable {

  private static final long serialVersionUID = 1L;
  private final String uri;

  public String getUri() {
    return uri;
  }

  private Nimi nimi = new Nimi();

  public KoodistoKoodi(String uri) {
    this.uri = uri;
  }

  /**
   * Palauta nimi localelle
   *
   * @param locale (fi,sv,en);
   * @return
   */
  public Nimi getNimi() {
    return nimi;
  }

  @Override
  public String toString() {
    return "KoodistoKoodi [uri=" + uri + ", nimi=" + nimi + "]";
  }
}
