package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class KoulutusAikuistenPerusopetusV1RDTO extends Koulutus2AsteV1RDTO {

  public KoulutusAikuistenPerusopetusV1RDTO() {
    super(ToteutustyyppiEnum.AIKUISTEN_PERUSOPETUS, ModuulityyppiEnum.TUNTEMATON);
  }
}
