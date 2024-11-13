package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class KoulutusAmmatilliseenPeruskoulutukseenValmentavaV1RDTO
    extends ValmistavaKoulutusV1RDTO {

  public KoulutusAmmatilliseenPeruskoulutukseenValmentavaV1RDTO() {
    super(
        ToteutustyyppiEnum.AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA,
        ModuulityyppiEnum.TUNTEMATON);
  }
}
