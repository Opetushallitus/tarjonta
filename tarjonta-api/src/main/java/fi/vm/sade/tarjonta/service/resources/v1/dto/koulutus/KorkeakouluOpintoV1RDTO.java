package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class KorkeakouluOpintoV1RDTO extends TutkintoonJohtamatonKoulutusV1RDTO {

  private static final long serialVersionUID = 1L;

  public KorkeakouluOpintoV1RDTO() {
    super(ToteutustyyppiEnum.KORKEAKOULUOPINTO, ModuulityyppiEnum.KORKEAKOULUTUS);
  }
}
