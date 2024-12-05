package fi.vm.sade.tarjonta.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@JsonIgnoreProperties({"id", "version", "kielet"})
@Table(name = Kielivalikoima.TABLE_NAME)
public class Kielivalikoima extends TarjontaBaseEntity {

  private static final long serialVersionUID = 3305481184717052756L;

  public static final String TABLE_NAME = "kielivalikoima";

  private String key;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
      name = TABLE_NAME + "_kieli",
      joinColumns = @JoinColumn(name = TABLE_NAME + "_id"))
  private Set<KoodistoUri> kielet = new HashSet<KoodistoUri>();

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  /**
   * Koodisto uri
   *
   * @return the kieliUri
   */
  public Set<KoodistoUri> getKielet() {
    return Collections.unmodifiableSet(kielet);
  }

  /**
   * @param uris Kieli-urit
   */
  public void setKielet(Collection<String> uris) {
    for (Iterator<KoodistoUri> i = kielet.iterator(); i.hasNext(); ) {
      if (!uris.contains(i.next().getKoodiUri())) {
        i.remove();
      }
    }
    for (String uri : uris) {
      kielet.add(new KoodistoUri(uri));
    }
  }

  /**
   * for Json serializer
   *
   * @return
   */
  @JsonProperty
  public Set<KoodistoUri> getKieliUrit() {
    return Collections.unmodifiableSet(kielet);
  }

  /**
   * for Json serializer
   *
   * @return
   */
  public void setKieliUrit(Set<KoodistoUri> kieliUrit) {
    kielet.clear();
    kielet.addAll(kieliUrit);
  }
}
