package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;

@ApiModel(value = "Tilastokeskuksen korkeakoulun koulutuskoodiin liittyvät relaatiot")
public class KoulutusmoduuliKorkeakouluRelationV1RDTO
    extends KoulutusmoduuliStandardRelationV1RDTO {

  @ApiModelProperty(value = "OPH tutkintonimike-koodit", required = true)
  private KoodiUrisV1RDTO tutkintonimikes;

  @ApiModelProperty(value = "Opintojen laajuuden arvot", required = true)
  private KoodiUrisV1RDTO opintojenLaajuusarvos;

  public KoodiUrisV1RDTO getTutkintonimikes() {
    if (tutkintonimikes == null) {
      tutkintonimikes = new KoodiUrisV1RDTO();
      tutkintonimikes.setUris(new HashMap<String, Integer>());
    }

    return tutkintonimikes;
  }

  public void setTutkintonimikes(KoodiUrisV1RDTO tutkintonimikes) {
    this.tutkintonimikes = tutkintonimikes;
  }

  public KoodiUrisV1RDTO getOpintojenLaajuusarvos() {
    if (opintojenLaajuusarvos == null) {
      opintojenLaajuusarvos = new KoodiUrisV1RDTO();
      opintojenLaajuusarvos.setUris(new HashMap<String, Integer>());
    }
    return opintojenLaajuusarvos;
  }

  public void setOpintojenLaajuusarvos(KoodiUrisV1RDTO opintojenLaajuusarvos) {
    this.opintojenLaajuusarvos = opintojenLaajuusarvos;
  }
}
