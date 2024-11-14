package fi.vm.sade.tarjonta.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.security.xssfilter.FilterXss;
import fi.vm.sade.security.xssfilter.XssFilterListener;
import jakarta.persistence.*;
import java.sql.Types;
import java.util.Date;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * This entity is used to store "looong" multilanguage texts by key (avain) and category
 * (kategoria).
 *
 * @author mlyly
 */
@Entity
@Table(
    name = "monikielinen_metadata",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"avain", "kategoria", "kieli"})})
@EntityListeners(XssFilterListener.class)
@JsonIgnoreProperties({"id", "version"})
public class MonikielinenMetadata extends TarjontaBaseEntity {

  /** */
  private static final long serialVersionUID = 1L;

  // Make translated texts behave more like "metadata".
  // For example: "uri: Sosiaali- ja Terveysala"
  private String avain = null;
  // For example: "SORA", "Valintaperustekuvaus"
  @FilterXss private String kategoria = null;
  // For example: "uri: Finnish 358#1"
  private String kieli = null;

  @Lob
  @JdbcTypeCode(Types.LONGVARCHAR)
  @Basic(fetch = FetchType.EAGER)
  @FilterXss
  private String arvo = null;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(updatable = false, nullable = false)
  private Date created = new Date();

  @Temporal(TemporalType.TIMESTAMP)
  @Column(updatable = true, nullable = false)
  private Date modified = new Date();

  public String getAvain() {
    return avain;
  }

  public void setAvain(String avain) {
    this.avain = avain;
  }

  public String getKategoria() {
    return kategoria;
  }

  public void setKategoria(String kategoria) {
    this.kategoria = kategoria;
  }

  public String getKieli() {
    return kieli;
  }

  public void setKieli(String kieli) {
    this.kieli = kieli;
  }

  public String getArvo() {
    return arvo;
  }

  public void setArvo(String arvo) {
    this.arvo = arvo;
  }

  public Date getCreated() {
    return created;
  }

  public Date getModified() {
    return modified;
  }
}
