package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class KoulutusAmmatillinenPeruskoulutusErityisopetuksenaV1RDTO
    extends KoulutusAmmatillinenPerustutkintoV1RDTO {

  public KoulutusAmmatillinenPeruskoulutusErityisopetuksenaV1RDTO() {
    super(
        ToteutustyyppiEnum.AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA,
        ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
  }
}
