package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.Map;

public class KoulutusAmmatillinenPerustutkintoAlk2018V1RDTO extends Koulutus2AsteV1RDTO {

  @Parameter(name = "Koulutuksen-tavoitteet", required = false)
  private Map<String, String> koulutuksenTavoitteet;

  @Parameter(
          name =
          "OPH tutkintonimike-koodit (korkeakoulutuksella eri koodistot kuin ammatillisella- ja lukio-koulutuksella)")
  private KoodiUrisV1RDTO tutkintonimikes;

  public KoulutusAmmatillinenPerustutkintoAlk2018V1RDTO() {
    super(
        ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_ALK_2018,
        ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
  }

  public KoulutusAmmatillinenPerustutkintoAlk2018V1RDTO(
      ToteutustyyppiEnum toteutustyyppiEnum, ModuulityyppiEnum moduulityyppiEnum) {
    super(toteutustyyppiEnum, moduulityyppiEnum);
  }

  protected KoulutusAmmatillinenPerustutkintoAlk2018V1RDTO(ToteutustyyppiEnum koulutustyyppiUri) {
    super(koulutustyyppiUri, ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
  }

  public Map<String, String> getKoulutuksenTavoitteet() {
    return koulutuksenTavoitteet;
  }

  public void setKoulutuksenTavoitteet(Map<String, String> koulutuksenTavoitteet) {
    this.koulutuksenTavoitteet = koulutuksenTavoitteet;
  }

  public KoodiUrisV1RDTO getTutkintonimikes() {
    if (this.tutkintonimikes == null) {
      this.tutkintonimikes = new KoodiUrisV1RDTO();
    }

    return tutkintonimikes;
  }

  public void setTutkintonimikes(KoodiUrisV1RDTO tutkintonimikes) {
    this.tutkintonimikes = tutkintonimikes;
  }
}
