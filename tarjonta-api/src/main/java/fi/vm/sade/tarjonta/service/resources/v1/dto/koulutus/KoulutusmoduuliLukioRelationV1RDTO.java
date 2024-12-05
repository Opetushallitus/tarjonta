package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Tilastokeskuksen koulutuskoodiin liittyvät relaatiot")
public class KoulutusmoduuliLukioRelationV1RDTO extends KoulutusmoduuliStandardRelationV1RDTO {

  @Parameter(name = "OPH tutkintonimike-koodit", required = true)
  private KoodiV1RDTO tutkintonimike;

  @Parameter(name = "Opintojen laajuuden arvot", required = true)
  private KoodiV1RDTO opintojenLaajuusarvo;

  @Parameter(name = "Pohjakoulutusvaatimus-koodi", required = true)
  private KoodiV1RDTO pohjakoulutusvaatimus;

  @Parameter(name = "Koulutuslaji-koodi", required = true)
  private KoodiV1RDTO koulutuslaji;

  @Parameter(name = "Lukiolinja-koodi", required = true)
  private KoodiV1RDTO lukiolinja;

  @Parameter(
          name =
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
