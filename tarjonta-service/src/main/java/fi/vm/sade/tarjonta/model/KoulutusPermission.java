package fi.vm.sade.tarjonta.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "koulutus_permissions")
public class KoulutusPermission {

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
  @SequenceGenerator(name = "hibernate_sequence", allocationSize = 1)
  private Long id;

  @Column(name = "org_oid")
  private String orgOid;

  @Column(name = "kohde_koodi")
  private String kohdeKoodi;

  @Column(name = "koodisto")
  private String koodisto;

  @Column(name = "koodi_uri")
  private String koodiUri;

  @Column(name = "alku_pvm")
  private Date alkuPvm;

  @Column(name = "loppu_pvm")
  private Date loppuPvm;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private KoulutusPermissionType type;

  public KoulutusPermission(
      String orgOid,
      String kohdeKoodi,
      String koodisto,
      String koodiUri,
      Date alkuPvm,
      Date loppuPvm,
      KoulutusPermissionType type) {
    this.orgOid = orgOid;
    this.kohdeKoodi = kohdeKoodi;
    this.koodisto = koodisto;
    this.koodiUri = koodiUri;
    this.alkuPvm = alkuPvm;
    this.loppuPvm = loppuPvm;
    this.type = type;
  }

  public KoulutusPermission() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOrgOid() {
    return orgOid;
  }

  public void setOrgOid(String orgOid) {
    this.orgOid = orgOid;
  }

  public String getKohdeKoodi() {
    return kohdeKoodi;
  }

  public void setKohdeKoodi(String kohdeKoodi) {
    this.kohdeKoodi = kohdeKoodi;
  }

  public String getKoodisto() {
    return koodisto;
  }

  public void setKoodisto(String koodisto) {
    this.koodisto = koodisto;
  }

  public String getKoodiUri() {
    return koodiUri;
  }

  public void setKoodiUri(String koodiUri) {
    this.koodiUri = koodiUri;
  }

  public Date getAlkuPvm() {
    return alkuPvm;
  }

  public void setAlkuPvm(Date alkuPvm) {
    this.alkuPvm = alkuPvm;
  }

  public Date getLoppuPvm() {
    return loppuPvm;
  }

  public void setLoppuPvm(Date loppuPvm) {
    this.loppuPvm = loppuPvm;
  }

  public KoulutusPermissionType getType() {
    return type;
  }

  public void setType(KoulutusPermissionType type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "KoulutusPermission{"
        + "orgOid='"
        + orgOid
        + '\''
        + ", kohdeKoodi='"
        + kohdeKoodi
        + '\''
        + ", koodisto='"
        + koodisto
        + '\''
        + ", koodiUri='"
        + koodiUri
        + '\''
        + ", alkuPvm="
        + alkuPvm
        + ", loppuPvm="
        + loppuPvm
        + ", type="
        + type
        + '}';
  }
}
