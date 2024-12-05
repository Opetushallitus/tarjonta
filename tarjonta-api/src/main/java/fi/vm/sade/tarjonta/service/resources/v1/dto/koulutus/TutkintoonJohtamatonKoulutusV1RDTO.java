package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public abstract class TutkintoonJohtamatonKoulutusV1RDTO extends KoulutusV1RDTO {

  private static final long serialVersionUID = 1L;

  @Parameter(name = "Koulutuksen loppumispvm")
  private Date koulutuksenLoppumisPvm;

  @Parameter(name = "Opintojen laajuus opintopisteissä (vapaa teksti)")
  private String opintojenLaajuusPistetta;

  @Parameter(name = "Opettaja")
  private String opettaja;

  @Parameter(name = "Oppiaine")
  private String oppiaine;

  @Parameter(name = "Koulutusryhmät OID listana", required = false)
  private Set<String> koulutusRyhmaOids = new HashSet<String>();

  @Parameter(name = "Opinnon tyyppi")
  private String opinnonTyyppiUri;

  @Parameter(name = "Alkuperäinen koulutus, joka järjestetään")
  private String tarjoajanKoulutus;

  @Parameter(name = "Opintokokonaisuus, johon koulutus kuuluu")
  private String opintokokonaisuusOid;

  @Parameter(name = "Opintokokonaisuuteen kuuluvat opintojaksot")
  private Set<String> opintojaksoOids = new HashSet<String>();

  public TutkintoonJohtamatonKoulutusV1RDTO(
      ToteutustyyppiEnum toteutustyyppi, ModuulityyppiEnum moduulityyppi) {
    super(toteutustyyppi, moduulityyppi);
  }

  public Date getKoulutuksenLoppumisPvm() {
    return koulutuksenLoppumisPvm;
  }

  public void setKoulutuksenLoppumisPvm(Date loppumisPvm) {
    this.koulutuksenLoppumisPvm = loppumisPvm;
  }

  public String getOpintojenLaajuusPistetta() {
    return this.opintojenLaajuusPistetta;
  }

  public void setOpintojenLaajuusPistetta(String opintojenLaajuusPistetta) {
    this.opintojenLaajuusPistetta = opintojenLaajuusPistetta;
  }

  public String getOpettaja() {
    return opettaja;
  }

  public void setOpettaja(String opettaja) {
    this.opettaja = opettaja;
  }

  public String getOppiaine() {
    return oppiaine;
  }

  public void setOppiaine(String oppiaine) {
    this.oppiaine = oppiaine;
  }

  public Set<String> getKoulutusRyhmaOids() {
    return koulutusRyhmaOids;
  }

  public void setKoulutusRyhmaOids(Set<String> koulutusRyhmaOids) {
    this.koulutusRyhmaOids = koulutusRyhmaOids;
  }

  public String getOpinnonTyyppiUri() {
    return opinnonTyyppiUri;
  }

  public void setOpinnonTyyppiUri(String opinnonTyyppiUri) {
    this.opinnonTyyppiUri = opinnonTyyppiUri;
  }

  public String getTarjoajanKoulutus() {
    return tarjoajanKoulutus;
  }

  public void setTarjoajanKoulutus(String tarjoajanKoulutus) {
    this.tarjoajanKoulutus = tarjoajanKoulutus;
  }

  public String getOpintokokonaisuusOid() {
    return opintokokonaisuusOid;
  }

  public void setOpintokokonaisuusOid(String opintokokonaisuusOid) {
    this.opintokokonaisuusOid = opintokokonaisuusOid;
  }

  public Set<String> getOpintojaksoOids() {
    return opintojaksoOids;
  }

  public void setOpintojaksoOids(Set<String> opintojaksoOids) {
    this.opintojaksoOids = opintojaksoOids;
  }
}
