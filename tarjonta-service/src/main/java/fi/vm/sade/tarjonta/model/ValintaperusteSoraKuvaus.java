package fi.vm.sade.tarjonta.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@JsonIgnoreProperties({"id", "version"})
@Table(name = ValintaperusteSoraKuvaus.VALINTAPERUSTEKUVAUSORA_TABLE_NAME)
public class ValintaperusteSoraKuvaus extends TarjontaBaseEntity {

  private static final long serialVersionUID = 1L;

  public static final String VALINTAPERUSTEKUVAUSORA_TABLE_NAME = "valintaperuste_sora_kuvaus";

  public String getAvain() {
    return avain;
  }

  public void setAvain(String avain) {
    this.avain = avain;
  }

  public static enum Tyyppi {
    VALINTAPERUSTEKUVAUS,
    SORA
  }

  public static enum Tila {
    VALMIS,
    POISTETTU
  }

  @NotNull
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "monikielinen_nimi_id")
  private MonikielinenTeksti monikielinenNimi;

  @Column(name = "organisaatio_tyyppi")
  private String organisaatioTyyppi;

  @Column(name = "tyyppi")
  @JdbcTypeCode(Types.INTEGER)
  private Tyyppi tyyppi;

  @Column(name = "kausi")
  private String kausi;

  @Column(name = "vuosi")
  private Integer vuosi;

  @Column(name = "tekstis")
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinTable(
      name = ValintaperusteSoraKuvaus.VALINTAPERUSTEKUVAUSORA_TABLE_NAME + "_monikielinen_metadata",
      joinColumns =
          @JoinColumn(
              name = ValintaperusteSoraKuvaus.VALINTAPERUSTEKUVAUSORA_TABLE_NAME + "_id",
              referencedColumnName = BaseEntity.ID_COLUMN_NAME),
      inverseJoinColumns =
          @JoinColumn(name = "tekstis_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME))
  private List<MonikielinenMetadata> tekstis;

  @Column(name = "viimPaivitysPvm")
  @Temporal(TemporalType.TIMESTAMP)
  private Date viimPaivitysPvm = new Date();

  @Column(name = "viimPaivittajaOid")
  private String viimPaivittajaOid;

  @Column(name = "avain")
  private String avain;

  @Enumerated(EnumType.STRING)
  @Column(name = "tila")
  private Tila tila;

  public MonikielinenTeksti getMonikielinenNimi() {
    return monikielinenNimi;
  }

  public void setMonikielinenNimi(MonikielinenTeksti monikielinenNimi) {
    this.monikielinenNimi = monikielinenNimi;
  }

  public String getOrganisaatioTyyppi() {
    return organisaatioTyyppi;
  }

  public void setOrganisaatioTyyppi(String organisaatioTyyppi) {
    this.organisaatioTyyppi = organisaatioTyyppi;
  }

  public List<MonikielinenMetadata> getTekstis() {
    return tekstis;
  }

  public void setTekstis(List<MonikielinenMetadata> tekstis) {
    this.tekstis = tekstis;
  }

  public Tyyppi getTyyppi() {
    return tyyppi;
  }

  public void setTyyppi(Tyyppi tyyppi) {
    this.tyyppi = tyyppi;
  }

  public String getKausi() {
    return kausi;
  }

  public void setKausi(String kausi) {
    this.kausi = kausi;
  }

  public Integer getVuosi() {
    return vuosi;
  }

  public void setVuosi(Integer vuosi) {
    this.vuosi = vuosi;
  }

  public Date getViimPaivitysPvm() {
    return viimPaivitysPvm;
  }

  public void setViimPaivitysPvm(Date viimPaivitysPvm) {
    this.viimPaivitysPvm = viimPaivitysPvm;
  }

  public String getViimPaivittajaOid() {
    return viimPaivittajaOid;
  }

  public void setViimPaivittajaOid(String viimPaivittajaOid) {
    this.viimPaivittajaOid = viimPaivittajaOid;
  }

  public Tila getTila() {
    return tila;
  }

  public void setTila(Tila tila) {
    this.tila = tila;
  }
}
