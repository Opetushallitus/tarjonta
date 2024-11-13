package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class ErikoisammattitutkintoV1RDTO extends NayttotutkintoV1RDTO {

  public ErikoisammattitutkintoV1RDTO() {
    super(ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO, ModuulityyppiEnum.ERIKOISAMMATTITUTKINTO);
  }
}
