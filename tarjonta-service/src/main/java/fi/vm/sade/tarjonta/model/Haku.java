package fi.vm.sade.tarjonta.model;

import static fi.vm.sade.generic.common.validation.ValidationConstants.WWW_PATTERN;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import jakarta.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.apache.commons.lang.StringUtils;

@Entity
@JsonIgnoreProperties({
  "id",
  "version",
  "hakukohdes",
  "hibernateLazyInitializer",
  "handler",
  "parentHaku",
  "sisaltyvatHaut"
})
@Table(
    name = Haku.TABLE_NAME,
    uniqueConstraints = {
      @UniqueConstraint(
          name = "UK_haku_01",
          columnNames = {"oid"})
    })
public class Haku extends TarjontaBaseEntity {

  private static final long serialVersionUID = 1L;

  public static final String TABLE_NAME = "haku";

  @NotNull
  @Column(unique = true)
  private String oid;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "nimi_teksti_id")
  private MonikielinenTeksti nimi;

  @NotNull
  @Column(name = "hakutyyppi")
  private String hakutyyppiUri;

  /** kevat tai syksy */
  @NotNull
  @Column(name = "hakukausi")
  private String hakukausiUri;

  @NotNull
  @Column(name = "hakukausi_vuosi")
  private Integer hakukausiVuosi;

  @Column(name = "koulutuksen_alkamiskausi")
  private String koulutuksenAlkamiskausiUri;

  @Column(name = "koulutuksen_alkamisvuosi")
  private Integer koulutuksenAlkamisVuosi;

  /** Haulla on jo OID. Mihin tunnistetta käytetään ja mikä on sen formaatti? */
  @Column(name = "haun_tunniste")
  private String haunTunniste;

  /** yliopistojen / ammattikorkeitten / peruskoulujen jne.. esm. ammatillinen koulutus */
  @NotNull
  @Column(name = "kohdejoukko")
  private String kohdejoukkoUri;

  @Column(name = "kohdejoukon_tarkenne")
  private String kohdejoukonTarkenne;

  /** yhteishaku yms. */
  @NotNull
  @Column(name = "hakutapa")
  private String hakutapaUri;

  @Column(name = "ylioppilastutkinto_antaa_hakukelpoisuuden")
  private Boolean ylioppilastutkintoAntaaHakukelpoisuuden = false;

  @Column(name = "sijoittelu")
  private boolean sijoittelu;

  @Column(name = "tunnistuskaytossa")
  private boolean tunnistusKaytossa;

  @Column(name = "jarjestelmanHakulomake")
  private boolean jarjestelmanHakulomake;

  @Pattern(regexp = WWW_PATTERN)
  @Column(name = "hakulomake_url")
  private String hakulomakeUrl;

  @Column(name = "ataru_lomake_avain")
  private String ataruLomakeAvain;

  @NotNull
  @Enumerated(EnumType.STRING)
  private TarjontaTila tila;

  @OneToMany(mappedBy = "haku", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  private Set<Hakukohde> hakukohdes = new HashSet<Hakukohde>();

  @OneToMany(
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "haku",
      fetch = FetchType.EAGER)
  @OrderBy("alkamisPvm")
  private Set<Hakuaika> hakuaikas = new HashSet<Hakuaika>();

  @Column(name = "viimPaivitysPvm")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdateDate = new Date();

  @Column(name = "opintopolun_nayttaminen_loppuu")
  @Temporal(TemporalType.DATE)
  private Date opintopolunNayttaminenLoppuu;

  @Column(name = "viimPaivittajaOid")
  private String lastUpdatedByOid;

  @Enumerated(EnumType.STRING)
  @Column(name = "koulutusmoduuli_tyyppi")
  private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;

  /**
   * How many hakukohdes can be added to "muistilista" (application list?) and to how many hakukohde
   * can hakija apply to with a single application.
   */
  @Column(name = "max_hakukohdes")
  private int maxHakukohdes = 0;

  /** Can applicant submit multiple applications to this haku? */
  @Column(name = "can_submit_multiple_applications")
  private boolean canSubmitMultipleApplications;

  /**
   * Array of organisation OIDs, comma separated. This lists the organisations that can bind
   * hakukohdes to this haku.
   *
   * <p>This is needed for KK-spesific functionality. See task KJOH-744 --
   * (https://jira.oph.ware.fi/jira/browse/KJOH-744)
   */
  @Column(name = "organisationOids")
  private String organisationOidString;

  /**
   * Array of organisation OIDs, comma separated. This lists the "tarjoaja" oids for the haku
   * (allowed to edit/delete it)
   *
   * <p>Tarjoaja organisation oids;
   */
  @Column(name = "tarjoajaOid")
  private String tarjoajaOidString;

  @ManyToOne()
  @JoinColumn(name = "parent_haku_id")
  private Haku parentHaku;

  @OneToMany(mappedBy = "parentHaku", cascade = CascadeType.ALL)
  private Set<Haku> sisaltyvatHaut = new HashSet<>();

  @Column(name = "autosync_tarjonta")
  private boolean autosyncTarjonta = false;

  @Column(name = "autosync_tarjonta_from")
  private Date autosyncTarjontaFrom;

  @Column(name = "autosync_tarjonta_to")
  private Date autosyncTarjontaTo;

  /**
   * If this is true, then the hakukohde choises users make should be arranged in priority order.
   */
  private boolean usePriority = false;

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getNimiFi() {
    return getNimi("fi");
  }

  public void setNimiFi(String nimiFi) {
    setNimi("fi", nimiFi);
  }

  public String getNimiSv() {
    return getNimi("sv");
  }

  public void setNimiSv(String nimiSv) {
    setNimi("sv", nimiSv);
  }

  public String getNimiEn() {
    return getNimi("en");
  }

  public void setNimiEn(String nimiEn) {
    setNimi("en", nimiEn);
  }

  public boolean isKorkeakouluHaku() {
    return StringUtils.defaultString(getKohdejoukkoUri()).startsWith("haunkohdejoukko_12#");
  }

  /**
   * Koulutukseen hakeutumisen tyypin tieto. Esim. varsinainen haku, täydennys- tai lisähaku. Arvo
   * on viittaus koodistoon.
   *
   * @return
   */
  public String getHakutyyppiUri() {
    return hakutyyppiUri;
  }

  /**
   * @param hakutyyppi uri to koodisto
   */
  public void setHakutyyppiUri(String hakutyyppi) {
    this.hakutyyppiUri = hakutyyppi;
  }

  /**
   * Returns uri to Koodisto.
   *
   * @return
   */
  public String getHakukausiUri() {
    return hakukausiUri;
  }

  public void setHakukausiUri(String hakukausi) {
    this.hakukausiUri = hakukausi;
  }

  public String getKoulutuksenAlkamiskausiUri() {
    return koulutuksenAlkamiskausiUri;
  }

  public void setKoulutuksenAlkamiskausiUri(String koodistoUri) {
    this.koulutuksenAlkamiskausiUri = koodistoUri;
  }

  public String getKohdejoukkoUri() {
    return kohdejoukkoUri;
  }

  /**
   * Uri to koodisto. Sample value behind uri could be: "Ammatillinen koulutus". This attribute is
   * mandatory.
   *
   * @param koodistoUri
   */
  public void setKohdejoukkoUri(String koodistoUri) {
    this.kohdejoukkoUri = koodistoUri;
  }

  public String getHakutapaUri() {
    return hakutapaUri;
  }

  /**
   * Uri to koodisto. Sample value behind uri could be: "Yhteishaku". This attribute is mandatory.
   *
   * @param hakutapa
   */
  public void setHakutapaUri(String hakutapa) {
    this.hakutapaUri = hakutapa;
  }

  /**
   * Returns true if "sijoittelu" is to be used.
   *
   * @return
   */
  public boolean isSijoittelu() {
    return sijoittelu;
  }

  public void setSijoittelu(boolean sijoittelu) {
    this.sijoittelu = sijoittelu;
  }

  public boolean isTunnistusKaytossa() {
    return tunnistusKaytossa;
  }

  public void setTunnistusKaytossa(boolean tunnistusKaytossa) {
    this.tunnistusKaytossa = tunnistusKaytossa;
  }

  public String getHakulomakeUrl() {
    return hakulomakeUrl;
  }

  public void setHakulomakeUrl(String hakulomakeUrl) {
    this.hakulomakeUrl = hakulomakeUrl;
  }

  public String getAtaruLomakeAvain() {
    return ataruLomakeAvain;
  }

  public void setAtaruLomakeAvain(String ataruLomakeAvain) {
    this.ataruLomakeAvain = ataruLomakeAvain;
  }

  public MonikielinenTeksti getNimi() {
    return nimi;
  }

  public void setNimi(MonikielinenTeksti newNimi) {
    nimi = MonikielinenTeksti.merge(nimi, newNimi);
  }

  private String getNimi(String kieliKoodi) {
    return (nimi != null ? nimi.getTekstiForKieliKoodi(kieliKoodi) : null);
  }

  private void setNimi(String kieliKoodi, String teksti) {
    if (nimi == null) {
      nimi = new MonikielinenTeksti();
    }
    nimi.setTekstiKaannos(kieliKoodi, teksti);
  }

  public Set<Hakukohde> getHakukohdes() {
    return Collections.unmodifiableSet(hakukohdes);
  }

  public void addHakukohde(Hakukohde hakukohde) {
    hakukohdes.add(hakukohde);
  }

  public void removeHakukohde(Hakukohde hakukohde) {
    hakukohdes.remove(hakukohde);
  }

  public Set<Hakuaika> getHakuaikas() {
    if (hakuaikas == null) {
      hakuaikas = new HashSet<Hakuaika>();
    }
    return hakuaikas;
    // return Collections.unmodifiableSet(hakuaikas);
  }

  public void addHakuaika(Hakuaika hakuaika) {
    hakuaika.setHaku(this);
    hakuaikas.add(hakuaika);
  }

  public void removeHakuaika(Hakuaika hakuaika) {
    if (hakuaikas.remove(hakuaika)) {
      hakuaika.setHaku(null);
    }
  }

  public Hakuaika getHakuaikaById(String hakuaikaId) {
    try {
      return getHakuaikaById(Long.parseLong(hakuaikaId));
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  public Hakuaika getHakuaikaById(Long hakuaikaId) {
    for (Hakuaika hakuaika : getHakuaikas()) {
      if (hakuaika.getId().equals(hakuaikaId)) {
        return hakuaika;
      }
    }
    return null;
  }

  /**
   * Returns current state. Value is a Koodisto uri.
   *
   * @return the tila
   */
  public TarjontaTila getTila() {
    return tila;
  }

  /**
   * Set state of this Haku. Value is a Koodisto uri.
   *
   * @param tila the tila to set
   */
  public void setTila(TarjontaTila tila) {
    this.tila = tila;
  }

  /**
   * @return the hakukausiVuosi
   */
  public Integer getHakukausiVuosi() {
    return hakukausiVuosi;
  }

  /**
   * @param hakukausiVuosi the hakukausiVuosi to set
   */
  public void setHakukausiVuosi(Integer hakukausiVuosi) {
    this.hakukausiVuosi = hakukausiVuosi;
  }

  /**
   * @return the koulutuksenAlkamisVuosi
   */
  public Integer getKoulutuksenAlkamisVuosi() {
    return koulutuksenAlkamisVuosi;
  }

  /**
   * @param koulutuksenAlkamisVuosi the koulutuksenAlkamisVuosi to set
   */
  public void setKoulutuksenAlkamisVuosi(Integer koulutuksenAlkamisVuosi) {
    this.koulutuksenAlkamisVuosi = koulutuksenAlkamisVuosi;
  }

  /**
   * @return the haunTunniste
   */
  public String getHaunTunniste() {
    return haunTunniste;
  }

  /**
   * @param haunTunniste the haunTunniste to set
   */
  public void setHaunTunniste(String haunTunniste) {
    this.haunTunniste = haunTunniste;
  }

  public Date getLastUpdateDate() {
    return lastUpdateDate;
  }

  public void setLastUpdateDate(Date lastUpdateDate) {
    this.lastUpdateDate = lastUpdateDate;
  }

  public String getLastUpdatedByOid() {
    return lastUpdatedByOid;
  }

  public void setLastUpdatedByOid(String lastUpdatedByOid) {
    this.lastUpdatedByOid = lastUpdatedByOid;
  }

  public int getMaxHakukohdes() {
    return maxHakukohdes;
  }

  public void setMaxHakukohdes(int maxHakukohdes) {
    this.maxHakukohdes = maxHakukohdes;
  }

  public boolean getCanSubmitMultipleApplications() {
    return canSubmitMultipleApplications;
  }

  public void setCanSubmitMultipleApplications(boolean canSubmitMultipleApplications) {
    this.canSubmitMultipleApplications = canSubmitMultipleApplications;
  }

  public String[] getOrganisationOids() {
    if (organisationOidString == null || organisationOidString.isEmpty()) {
      return new String[0];
    }
    return organisationOidString.split(",");
  }

  public void setOrganisationOids(String[] organisationOids) {
    if (organisationOids == null || organisationOids.length == 0) {
      this.organisationOidString = null;
    } else {
      this.organisationOidString = StringUtils.join(organisationOids, ",");
    }
  }

  public String[] getTarjoajaOids() {
    if (tarjoajaOidString == null || tarjoajaOidString.isEmpty()) {
      return new String[0];
    }
    return tarjoajaOidString.split(",");
  }

  public void setTarjoajaOids(String[] organisationOids) {
    if (organisationOids == null || organisationOids.length == 0) {
      this.tarjoajaOidString = null;
    } else {
      this.tarjoajaOidString = StringUtils.join(organisationOids, ",");
    }
  }

  public boolean isUsePriority() {
    return usePriority;
  }

  public void setUsePriority(boolean usePriority) {
    this.usePriority = usePriority;
  }

  public boolean isJarjestelmanHakulomake() {
    return jarjestelmanHakulomake;
  }

  public void setJarjestelmanHakulomake(boolean jarjestelmanHakulomake) {
    this.jarjestelmanHakulomake = jarjestelmanHakulomake;
  }

  public Set<Haku> getSisaltyvatHaut() {
    return sisaltyvatHaut;
  }

  public Haku getParentHaku() {
    return parentHaku;
  }

  public void setParentHaku(Haku parentHaku) {
    this.parentHaku = parentHaku;
  }

  public KoulutusmoduuliTyyppi getKoulutusmoduuliTyyppi() {
    return koulutusmoduuliTyyppi;
  }

  public void setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi koulutusmoduuliTyyppi) {
    this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
  }

  public boolean isJatkuva() {
    return StringUtils.contains(getHakutapaUri(), "hakutapa_03");
  }

  public boolean isYhteishaku() {
    return StringUtils.contains(getHakutapaUri(), "hakutapa_01");
  }

  public Boolean getYlioppilastutkintoAntaaHakukelpoisuuden() {
    return ylioppilastutkintoAntaaHakukelpoisuuden;
  }

  public void setYlioppilastutkintoAntaaHakukelpoisuuden(
      Boolean ylioppilastutkintoAntaaHakukelpoisuuden) {
    this.ylioppilastutkintoAntaaHakukelpoisuuden = ylioppilastutkintoAntaaHakukelpoisuuden;
  }

  public Date getOpintopolunNayttaminenLoppuu() {
    return opintopolunNayttaminenLoppuu;
  }

  public void setOpintopolunNayttaminenLoppuu(Date opintopolunNayttaminenLoppuu) {
    this.opintopolunNayttaminenLoppuu = opintopolunNayttaminenLoppuu;
  }

  public String getKohdejoukonTarkenne() {
    return kohdejoukonTarkenne;
  }

  public void setKohdejoukonTarkenne(String kohdejoukonTarkenne) {
    this.kohdejoukonTarkenne = kohdejoukonTarkenne;
  }

  public boolean isAutosyncTarjonta() {
    return autosyncTarjonta;
  }

  public void setAutosyncTarjonta(boolean autosyncTarjonta) {
    this.autosyncTarjonta = autosyncTarjonta;
  }

  public Date getAutosyncTarjontaFrom() {
    return autosyncTarjontaFrom;
  }

  public void setAutosyncTarjontaFrom(Date autosyncTarjontaFrom) {
    this.autosyncTarjontaFrom = autosyncTarjontaFrom;
  }

  public Date getAutosyncTarjontaTo() {
    return autosyncTarjontaTo;
  }

  public void setAutosyncTarjontaTo(Date autosyncTarjontaTo) {
    this.autosyncTarjontaTo = autosyncTarjontaTo;
  }
}
