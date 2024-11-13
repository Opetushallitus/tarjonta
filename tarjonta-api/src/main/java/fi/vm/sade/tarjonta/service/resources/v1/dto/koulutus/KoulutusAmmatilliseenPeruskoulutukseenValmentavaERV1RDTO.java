package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class KoulutusAmmatilliseenPeruskoulutukseenValmentavaERV1RDTO
    extends ValmistavaKoulutusV1RDTO {

  public KoulutusAmmatilliseenPeruskoulutukseenValmentavaERV1RDTO() {
    super(
        ToteutustyyppiEnum.AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER,
        ModuulityyppiEnum.TUNTEMATON);
  }
}
