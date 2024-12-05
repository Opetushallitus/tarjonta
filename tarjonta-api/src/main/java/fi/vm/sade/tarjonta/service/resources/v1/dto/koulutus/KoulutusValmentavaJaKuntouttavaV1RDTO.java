package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class KoulutusValmentavaJaKuntouttavaV1RDTO extends ValmistavaKoulutusV1RDTO {

  public KoulutusValmentavaJaKuntouttavaV1RDTO() {
    super(
        ToteutustyyppiEnum.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS,
        ModuulityyppiEnum.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS);
  }
}
