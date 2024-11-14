package fi.vm.sade.tarjonta.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import org.apache.commons.lang.StringUtils;

/**
 * Strongly typed class to enforce structure of an uri and to make the code more readable that uses
 * Koodisto uri's (instead of just using strings).
 *
 * <p>Wrapper for Koodisto uri. Use as @Embedded or inside @ElementCollection.
 */
@Embeddable
public class KoodistoUri implements Serializable {

  private static final long serialVersionUID = 6772772416321895399L;

  @Column(name = "koodi_uri", nullable = false)
  private String koodiUri;

  /** JPA constructor. */
  protected KoodistoUri() {}

  /**
   * @param koodiUri a non-empty Uri
   */
  public KoodistoUri(String koodiUri) {
    this();
    assert StringUtils.isNotEmpty(koodiUri) : "koodiUri cannot be empty string";
    this.koodiUri = koodiUri;
  }

  /**
   * Returns a non-null koodi uri.
   *
   * @return
   */
  public String getKoodiUri() {
    return koodiUri;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final KoodistoUri other = (KoodistoUri) obj;
    if ((this.koodiUri == null)
        ? (other.koodiUri != null)
        : !this.koodiUri.equals(other.koodiUri)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 79 * hash + (this.koodiUri != null ? this.koodiUri.hashCode() : 0);
    return hash;
  }
}
