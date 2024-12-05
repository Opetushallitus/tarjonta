package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.valmistava.ValmistavaV1RDTO;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import io.swagger.v3.oas.annotations.Parameter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public abstract class NayttotutkintoV1RDTO extends KoulutusV1RDTO {
  private static final Date beginningOfJanuary2018 =
      Date.from(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("EET")).toInstant());

  @Parameter(
      name =
          "OPH tutkintonimike-koodit (korkeakoulutuksella eri koodistot kuin ammatillisella- ja lukio-koulutuksella)")
  private KoodiUrisV1RDTO tutkintonimikes;

  @Parameter(name = "HTTP-linkki opetussuunnitelmaan", required = false)
  private String linkkiOpetussuunnitelmaan;

  @Parameter(
          name = "Tarjoaja tai organisaation johon koulutus on liitetty",
      required = true)
  private OrganisaatioV1RDTO jarjestavaOrganisaatio;

  @Parameter(name = "Valmistavan koulutukseen tarvittavat tiedot", required = false)
  private ValmistavaV1RDTO valmistavaKoulutus;

  @Parameter(name = "Osaamisalan tarkenne", required = false)
  private String tarkenne;

  protected NayttotutkintoV1RDTO(
      ToteutustyyppiEnum toteutustyyppi, ModuulityyppiEnum moduulityyppi) {
    super(toteutustyyppi, moduulityyppi);
  }

  public ValmistavaV1RDTO getValmistavaKoulutus() {
    return valmistavaKoulutus;
  }

  public void setValmistavaKoulutus(ValmistavaV1RDTO valmistavaKoulutus) {
    this.valmistavaKoulutus = valmistavaKoulutus;
  }

  public OrganisaatioV1RDTO getJarjestavaOrganisaatio() {
    return jarjestavaOrganisaatio;
  }

  public void setJarjestavaOrganisaatio(OrganisaatioV1RDTO jarjestavaOrganisaatio) {
    this.jarjestavaOrganisaatio = jarjestavaOrganisaatio;
  }

  public KoodiUrisV1RDTO getTutkintonimikes() {
    if (this.tutkintonimikes == null) {
      this.tutkintonimikes = new KoodiUrisV1RDTO();
    }

    return tutkintonimikes;
  }

  public void setTutkintonimikes(KoodiUrisV1RDTO tutkintonimikes) {
    this.tutkintonimikes = tutkintonimikes;
  }

  public String getLinkkiOpetussuunnitelmaan() {
    return linkkiOpetussuunnitelmaan;
  }

  public void setLinkkiOpetussuunnitelmaan(String linkkiOpetussuunnitelmaan) {
    this.linkkiOpetussuunnitelmaan = linkkiOpetussuunnitelmaan;
  }

  public String getTarkenne() {
    return tarkenne;
  }

  public void setTarkenne(String tarkenne) {
    this.tarkenne = tarkenne;
  }

  public boolean alkaaEnnenReformia() {
    if ((getKoulutuksenAlkamisPvms() == null || getKoulutuksenAlkamisPvms().isEmpty())
        && (getKoulutuksenAlkamisvuosi() == null || getKoulutuksenAlkamisvuosi() >= 2018)) {
      return false;
    }
    return getKoulutuksenAlkamisPvms().stream().allMatch(a -> a.before(beginningOfJanuary2018));
  }
}
