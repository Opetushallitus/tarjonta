package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import io.swagger.v3.oas.annotations.Parameter;

public class KoulutusGenericV1RDTO extends KoulutusV1RDTO {

  @Parameter(name = "Pohjakoulutusvaatimus-koodi", required = true)
  private KoodiV1RDTO pohjakoulutusvaatimus;

  @Parameter(name = "HTTP-linkki opetussuunnitelmaan")
  private String linkkiOpetussuunnitelmaan;

  public KoulutusGenericV1RDTO(
      ToteutustyyppiEnum toteutustyyppiEnum, ModuulityyppiEnum moduulityyppiEnum) {
    super(toteutustyyppiEnum, moduulityyppiEnum);
  }

  public KoulutusGenericV1RDTO() {
    super();
  }

  public KoodiV1RDTO getPohjakoulutusvaatimus() {
    return pohjakoulutusvaatimus;
  }

  public void setPohjakoulutusvaatimus(KoodiV1RDTO pohjakoulutusvaatimus) {
    this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
  }

  public String getLinkkiOpetussuunnitelmaan() {
    return linkkiOpetussuunnitelmaan;
  }

  public void setLinkkiOpetussuunnitelmaan(String linkkiOpetussuunnitelmaan) {
    this.linkkiOpetussuunnitelmaan = linkkiOpetussuunnitelmaan;
  }
}
