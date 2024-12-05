package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import io.swagger.v3.oas.annotations.Parameter;

public abstract class Koulutus2AsteV1RDTO extends KoulutusGenericV1RDTO {

  @Parameter(name = "Kielivalikoimat", required = true)
  private KoodiValikoimaV1RDTO kielivalikoima;

  @Parameter(name = "Tutkintonimike", required = true)
  private KoodiV1RDTO tutkintonimike;

  protected Koulutus2AsteV1RDTO(
      ToteutustyyppiEnum toteutustyyppiEnum, ModuulityyppiEnum moduulityyppiEnum) {
    super(toteutustyyppiEnum, moduulityyppiEnum);
  }

  public KoodiValikoimaV1RDTO getKielivalikoima() {
    return kielivalikoima;
  }

  public void setKielivalikoima(KoodiValikoimaV1RDTO kielivalikoima) {
    this.kielivalikoima = kielivalikoima;
  }

  public KoodiV1RDTO getTutkintonimike() {
    return tutkintonimike;
  }

  public void setTutkintonimike(KoodiV1RDTO tutkintonimike) {
    this.tutkintonimike = tutkintonimike;
  }
}
