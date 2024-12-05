package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.Map;

public class ValmistavaKoulutusV1RDTO extends KoulutusGenericV1RDTO {

  @Parameter(name = "Opintojen laajuuden arvo (ei koodistosta)", required = false)
  private String opintojenLaajuusarvoKannassa;

  @Parameter(name = "Koulutusohjelman nimi kannassa", required = false)
  private Map<String, String> koulutusohjelmanNimiKannassa;

  public ValmistavaKoulutusV1RDTO(
      ToteutustyyppiEnum toteutustyyppi, ModuulityyppiEnum moduulityyppi) {
    super(toteutustyyppi, moduulityyppi);
  }

  public ValmistavaKoulutusV1RDTO() {
    super();
  }

  public void setOpintojenLaajuusarvoKannassa(String opintojenLaajuusarvo) {
    this.opintojenLaajuusarvoKannassa = opintojenLaajuusarvo;
  }

  public String getOpintojenLaajuusarvoKannassa() {
    return opintojenLaajuusarvoKannassa;
  }

  public Map<String, String> getKoulutusohjelmanNimiKannassa() {
    return koulutusohjelmanNimiKannassa;
  }

  public void setKoulutusohjelmanNimiKannassa(Map<String, String> koulutusohjelmanNimiKannassa) {
    this.koulutusohjelmanNimiKannassa = koulutusohjelmanNimiKannassa;
  }
}
