package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import io.swagger.v3.oas.annotations.Parameter;

public class KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO extends NayttotutkintoV1RDTO {

  @Parameter(
      name =
          "OPH tutkintonimike-koodit (korkeakoulutuksella eri koodistot kuin ammatillisella- ja lukio-koulutuksella)")
  private KoodiUrisV1RDTO tutkintonimikes;

  public KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO() {
    super(
        ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA,
        ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
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
}
