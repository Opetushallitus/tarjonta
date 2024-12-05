package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;

@Tag(name = "Tilastokeskuksen korkeakoulun koulutuskoodiin liittyvät relaatiot")
public class KoulutusmoduuliKorkeakouluRelationV1RDTO
    extends KoulutusmoduuliStandardRelationV1RDTO {

  @Parameter(name = "OPH tutkintonimike-koodit", required = true)
  private KoodiUrisV1RDTO tutkintonimikes;

  @Parameter(name = "Opintojen laajuuden arvot", required = true)
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
