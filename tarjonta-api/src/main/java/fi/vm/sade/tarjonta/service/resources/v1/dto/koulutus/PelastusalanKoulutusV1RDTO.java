package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class PelastusalanKoulutusV1RDTO extends KoulutusAmmatillinenPerustutkintoV1RDTO {

  public PelastusalanKoulutusV1RDTO() {
    super(ToteutustyyppiEnum.PELASTUSALAN_KOULUTUS);
  }
}
