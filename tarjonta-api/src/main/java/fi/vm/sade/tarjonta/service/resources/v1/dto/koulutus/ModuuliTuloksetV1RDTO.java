package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import io.swagger.annotations.ApiModelProperty;

public class ModuuliTuloksetV1RDTO extends BaseV1RDTO {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "Koulutusmoduulin tyyppi", required = true)
  private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;

  @ApiModelProperty(
      value = "Koulutusmoduulin käyttämä ohjelma uri (koulutusohjelma, lukiolinja tai osaamisala)",
      required = true)
  private String ohjelmaUri;

  @ApiModelProperty(value = "Koulutusmoduulin koulutusohjelma uri, jos olemassa", required = false)
  private String koulutusohjelmaUri;

  @ApiModelProperty(value = "Koulutusmoduulin lukiolinja uri, jos olemassa", required = false)
  private String lukiolinjaUri;

  @ApiModelProperty(value = "Koulutusmoduulin osaamisala uri, jos olemassa", required = false)
  private String osaamisalaUri;

  @ApiModelProperty(value = "Kuusinumeroinen tilastokeskuksen koulutuskoodin uri", required = true)
  private String koulutuskoodiUri;

  public ModuuliTuloksetV1RDTO(
      String oid,
      KoulutusmoduuliTyyppi koulutusmoduuliTyyppi,
      String koulutuskoodiUri,
      String ohjelmaUri,
      String koulutusohjelmaUri,
      String lukiolinjaUri,
      String osaamisalaUri) {
    setOid(oid);
    this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
    this.ohjelmaUri = ohjelmaUri;
    this.koulutuskoodiUri = koulutuskoodiUri;
    this.koulutusohjelmaUri = koulutusohjelmaUri;
    this.lukiolinjaUri = lukiolinjaUri;
    this.osaamisalaUri = osaamisalaUri;
  }

  public KoulutusmoduuliTyyppi getKoulutusmoduuliTyyppi() {
    return koulutusmoduuliTyyppi;
  }

  public void setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi koulutusmoduuliTyyppi) {
    this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
  }

  public String getKoulutusohjelmaUri() {
    return koulutusohjelmaUri;
  }

  public void setKoulutusohjelmaUri(String koulutusohjelmaUri) {
    this.koulutusohjelmaUri = koulutusohjelmaUri;
  }

  public String getKoulutuskoodiUri() {
    return koulutuskoodiUri;
  }

  public void setKoulutuskoodiUri(String koulutuskoodiUri) {
    this.koulutuskoodiUri = koulutuskoodiUri;
  }

  public String getOhjelmaUri() {
    return ohjelmaUri;
  }

  public void setOhjelmaUri(String ohjelmaUri) {
    this.ohjelmaUri = ohjelmaUri;
  }

  public String getLukiolinjaUri() {
    return lukiolinjaUri;
  }

  public void setLukiolinjaUri(String lukiolinjaUri) {
    this.lukiolinjaUri = lukiolinjaUri;
  }

  public String getOsaamisalaUri() {
    return osaamisalaUri;
  }

  public void setOsaamisalaUri(String osaamisalaUri) {
    this.osaamisalaUri = osaamisalaUri;
  }
}
