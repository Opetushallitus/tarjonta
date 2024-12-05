package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class AmmattitutkintoV1RDTO extends NayttotutkintoV1RDTO {

  public AmmattitutkintoV1RDTO() {
    super(ToteutustyyppiEnum.AMMATTITUTKINTO, ModuulityyppiEnum.ERIKOISAMMATTITUTKINTO);
  }
}
