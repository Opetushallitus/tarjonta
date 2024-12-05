package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class KoulutusAmmatilliseenPeruskoulutukseenOhjaavaJaValmistavaV1RDTO
    extends ValmistavaKoulutusV1RDTO {

  public KoulutusAmmatilliseenPeruskoulutukseenOhjaavaJaValmistavaV1RDTO() {
    super(
        ToteutustyyppiEnum.AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS,
        ModuulityyppiEnum.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS);
  }
}
