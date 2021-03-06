/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */
package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import java.util.Map;

public class KoulutusAmmatillinenPerustutkintoAlk2018V1RDTO extends Koulutus2AsteV1RDTO {

  @ApiModelProperty(value = "Koulutuksen-tavoitteet", required = false)
  private Map<String, String> koulutuksenTavoitteet;

  @ApiModelProperty(value = "OPH tutkintonimike-koodit (korkeakoulutuksella eri koodistot kuin ammatillisella- ja lukio-koulutuksella)")
  private KoodiUrisV1RDTO tutkintonimikes;

  public KoulutusAmmatillinenPerustutkintoAlk2018V1RDTO() {
    super(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_ALK_2018, ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
  }

  public KoulutusAmmatillinenPerustutkintoAlk2018V1RDTO(ToteutustyyppiEnum toteutustyyppiEnum, ModuulityyppiEnum moduulityyppiEnum) {
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

  /**
   * @return the tutkintonimike
   */
  public KoodiUrisV1RDTO getTutkintonimikes() {
    if (this.tutkintonimikes == null) {
      this.tutkintonimikes = new KoodiUrisV1RDTO();
    }

    return tutkintonimikes;
  }

  /**
   * @param tutkintonimikes the tutkintonimikes to set
   */
  public void setTutkintonimikes(KoodiUrisV1RDTO tutkintonimikes) {
    this.tutkintonimikes = tutkintonimikes;
  }

}
