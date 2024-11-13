package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.service.resources.v1.dto.BaseV1RDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;

@ApiModel(value = "Tilastokeskuksen koulutuskoodiin liittyvät relaatiot")
public class KoulutusmoduuliStandardRelationV1RDTO extends BaseV1RDTO {

  @ApiModelProperty(value = "Kuusinumeroinen tilastokeskuksen koulutuskoodi", required = true)
  private KoodiV1RDTO koulutuskoodi;

  @ApiModelProperty(value = "OPH2002 koulutusaste-koodi", required = true)
  private KoodiV1RDTO koulutusaste;

  @ApiModelProperty(value = "OPH2002 koulutusala-koodi", required = true)
  private KoodiV1RDTO koulutusala;

  @ApiModelProperty(value = "OPH2002 opintoala-koodi", required = true)
  private KoodiV1RDTO opintoala;

  @ApiModelProperty(value = "OPH tutkinto-koodi", required = true)
  private KoodiV1RDTO tutkinto;

  @ApiModelProperty(value = "EQF-koodi", required = true)
  private KoodiV1RDTO eqf;

  @ApiModelProperty(value = "NQF-koodi", required = true)
  private KoodiV1RDTO nqf;

  @ApiModelProperty(value = "Opintojen laajusyksikko-koodi", required = true)
  private KoodiV1RDTO opintojenLaajuusyksikko;

  @ApiModelProperty(value = "Koulutustyyppi-koodi", required = true)
  private KoodiV1RDTO koulutustyyppi;

  @ApiModelProperty(
      value =
          "Kaikki haettuun koodiin sisaltyvat koulutusohjelma-, osaamisala- tai lukiolinja-tyyppiset koodit.")
  private KoodiUrisV1RDTO ohjelmas;

  public KoodiV1RDTO getKoulutuskoodi() {
    return koulutuskoodi;
  }

  public void setKoulutuskoodi(KoodiV1RDTO koulutuskoodi) {
    this.koulutuskoodi = koulutuskoodi;
  }

  public KoodiV1RDTO getKoulutusaste() {
    return koulutusaste;
  }

  public void setKoulutusaste(KoodiV1RDTO koulutusaste) {
    this.koulutusaste = koulutusaste;
  }

  public KoodiV1RDTO getKoulutusala() {
    return koulutusala;
  }

  public void setKoulutusala(KoodiV1RDTO koulutusala) {
    this.koulutusala = koulutusala;
  }

  public KoodiV1RDTO getOpintoala() {
    return opintoala;
  }

  public void setOpintoala(KoodiV1RDTO opintoala) {
    this.opintoala = opintoala;
  }

  public KoodiV1RDTO getTutkinto() {
    return tutkinto;
  }

  public void setTutkinto(KoodiV1RDTO tutkinto) {
    this.tutkinto = tutkinto;
  }

  public KoodiV1RDTO getEqf() {
    return eqf;
  }

  public void setEqf(KoodiV1RDTO eqf) {
    this.eqf = eqf;
  }

  public KoodiV1RDTO getOpintojenLaajuusyksikko() {
    return opintojenLaajuusyksikko;
  }

  public void setOpintojenLaajuusyksikko(KoodiV1RDTO opintojenLaajuusyksikko) {
    this.opintojenLaajuusyksikko = opintojenLaajuusyksikko;
  }

  public KoodiV1RDTO getNqf() {
    return nqf;
  }

  public void setNqf(KoodiV1RDTO nqf) {
    this.nqf = nqf;
  }

  public KoodiV1RDTO getKoulutustyyppi() {
    return koulutustyyppi;
  }

  public void setKoulutustyyppi(KoodiV1RDTO koulutustyyppi) {
    this.koulutustyyppi = koulutustyyppi;
  }

  public KoodiUrisV1RDTO getOhjelmas() {
    if (ohjelmas == null) {
      ohjelmas = new KoodiUrisV1RDTO();
      ohjelmas.setUris(new HashMap<String, Integer>());
    }

    return ohjelmas;
  }

  public void setOhjelmas(KoodiUrisV1RDTO ohjelmas) {
    this.ohjelmas = ohjelmas;
  }
}
