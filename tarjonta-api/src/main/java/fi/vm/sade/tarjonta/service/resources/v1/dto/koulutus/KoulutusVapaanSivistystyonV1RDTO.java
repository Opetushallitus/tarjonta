package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class KoulutusVapaanSivistystyonV1RDTO extends ValmistavaKoulutusV1RDTO {

  public KoulutusVapaanSivistystyonV1RDTO() {
    super(
        ToteutustyyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS,
        ModuulityyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS);
  }
}
