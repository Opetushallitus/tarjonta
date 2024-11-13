package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Tilastokeskuksen koulutuskoodiin liittyvät relaatiot")
public class KoulutusmoduuliLukioRelationV1RDTO extends KoulutusmoduuliStandardRelationV1RDTO {

  @ApiModelProperty(value = "OPH tutkintonimike-koodit", required = true)
  private KoodiV1RDTO tutkintonimike;

  @ApiModelProperty(value = "Opintojen laajuuden arvot", required = true)
  private KoodiV1RDTO opintojenLaajuusarvo;

  @ApiModelProperty(value = "Pohjakoulutusvaatimus-koodi", required = true)
  private KoodiV1RDTO pohjakoulutusvaatimus;

  @ApiModelProperty(value = "Koulutuslaji-koodi", required = true)
  private KoodiV1RDTO koulutuslaji;

  @ApiModelProperty(value = "Lukiolinja-koodi", required = true)
  private KoodiV1RDTO lukiolinja;

  @ApiModelProperty(
      value =
          "Lukiolinja-koodi, REST-rajapinnan selkeyttämisen vuoksi lukiolinja-koodiston tietoa kuljetetaan myös koulutusohjelma-kentässä")
  private KoodiV1RDTO koulutusohjelma;

  public KoodiV1RDTO getTutkintonimike() {
    return tutkintonimike;
  }

  public void setTutkintonimike(KoodiV1RDTO tutkintonimike) {
    this.tutkintonimike = tutkintonimike;
  }

  public KoodiV1RDTO getOpintojenLaajuusarvo() {
    return opintojenLaajuusarvo;
  }

  public void setOpintojenLaajuusarvo(KoodiV1RDTO opintojenLaajuusarvo) {
    this.opintojenLaajuusarvo = opintojenLaajuusarvo;
  }

  public KoodiV1RDTO getPohjakoulutusvaatimus() {
    return pohjakoulutusvaatimus;
  }

  public void setPohjakoulutusvaatimus(KoodiV1RDTO pohjakoulutusvaatimus) {
    this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
  }

  public KoodiV1RDTO getKoulutuslaji() {
    return koulutuslaji;
  }

  public void setKoulutuslaji(KoodiV1RDTO koulutuslaji) {
    this.koulutuslaji = koulutuslaji;
  }

  public KoodiV1RDTO getKoulutusohjelma() {
    return koulutusohjelma;
  }

  public void setKoulutusohjelma(KoodiV1RDTO koulutusohjelma) {
    this.koulutusohjelma = koulutusohjelma;
  }

  public KoodiV1RDTO getLukiolinja() {
    return lukiolinja;
  }

  public void setLukiolinja(KoodiV1RDTO lukiolinja) {
    this.lukiolinja = lukiolinja;
  }
}
