package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class KoulutusMaahanmuuttajienAmmatilliseenPeruskoulutukseenValmistavaV1RDTO
    extends ValmistavaKoulutusV1RDTO {

  public KoulutusMaahanmuuttajienAmmatilliseenPeruskoulutukseenValmistavaV1RDTO() {
    super(
        ToteutustyyppiEnum.MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS,
        ModuulityyppiEnum.MAAHANM_AMM_VALMISTAVA_KOULUTUS);
  }
}
