package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import io.swagger.annotations.ApiModelProperty;

public class KoulutusLukioV1RDTO extends Koulutus2AsteV1RDTO {

  @ApiModelProperty(value = "Lukiodiplomit", required = true)
  private KoodiUrisV1RDTO lukiodiplomit;

  public KoulutusLukioV1RDTO() {
    super(ToteutustyyppiEnum.LUKIOKOULUTUS, ModuulityyppiEnum.LUKIOKOULUTUS);
  }

  protected KoulutusLukioV1RDTO(ToteutustyyppiEnum koulutustyyppiUri) {
    super(koulutustyyppiUri, ModuulityyppiEnum.LUKIOKOULUTUS);
  }

  public KoodiUrisV1RDTO getLukiodiplomit() {
    if (lukiodiplomit == null) {
      lukiodiplomit = new KoodiUrisV1RDTO();
    }
    return lukiodiplomit;
  }

  public void setLukiodiplomit(KoodiUrisV1RDTO lukiodiplomit) {
    this.lukiodiplomit = lukiodiplomit;
  }
}
