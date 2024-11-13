package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class KoulutusPerusopetuksenLisaopetusV1RDTO extends ValmistavaKoulutusV1RDTO {

  public KoulutusPerusopetuksenLisaopetusV1RDTO() {
    super(
        ToteutustyyppiEnum.PERUSOPETUKSEN_LISAOPETUS, ModuulityyppiEnum.PERUSOPETUKSEN_LISAOPETUS);
  }
}
