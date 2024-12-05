package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;

@Tag(name = "Tilastokeskuksen koulutuskoodiin liittyvät relaatiot")
public class KoulutusmoduuliAmmatillinenRelationV1RDTO
    extends KoulutusmoduuliStandardRelationV1RDTO {

  // KOODISTO KOMO DATA OBJECTS:
  @Parameter(name = "OPH tutkintonimike-koodit", required = true)
  private KoodiV1RDTO tutkintonimike;

  @Parameter(name = "OPH tutkintonimike-koodit", required = true)
  private KoodiUrisV1RDTO tutkintonimikes;

  @Parameter(name = "Opintojen laajuuden arvot", required = true)
  private KoodiV1RDTO opintojenLaajuusarvo;

  @Parameter(name = "Pohjakoulutusvaatimus-koodi", required = true)
  private KoodiV1RDTO pohjakoulutusvaatimus;

  @Parameter(name = "Koulutuslaji-koodi", required = true)
  private KoodiV1RDTO koulutuslaji;

  @Parameter(name = "Osaamisala-koodi", required = true)
  private KoodiV1RDTO osaamisala;

  @Parameter(name = "koulutusohjelma-koodi")
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

  public KoodiV1RDTO getOsaamisala() {
    return osaamisala;
  }

  public void setOsaamisala(KoodiV1RDTO osaamisala) {
    this.osaamisala = osaamisala;
  }

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
}
