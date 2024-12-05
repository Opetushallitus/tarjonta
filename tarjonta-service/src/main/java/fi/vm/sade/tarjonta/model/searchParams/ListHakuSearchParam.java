package fi.vm.sade.tarjonta.model.searchParams; /*
                                                 *
                                                 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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
                                                 * European Union Public Licence for more details.
                                                 */

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 * @author: Tuomas Katva Date: 2.8.2013
 */
public class ListHakuSearchParam {

  private TarjontaTila tila;

  private String koulutuksenAlkamisKausi;
  private Integer koulutuksenAlkamisVuosi;

  public TarjontaTila getTila() {
    return tila;
  }

  public void setTila(TarjontaTila tila) {
    this.tila = tila;
  }

  public String getKoulutuksenAlkamisKausi() {
    return koulutuksenAlkamisKausi;
  }

  public void setKoulutuksenAlkamisKausi(String koulutuksenAlkamisKausi) {
    this.koulutuksenAlkamisKausi = koulutuksenAlkamisKausi;
  }

  public Integer getKoulutuksenAlkamisVuosi() {
    return koulutuksenAlkamisVuosi;
  }

  public void setKoulutuksenAlkamisVuosi(Integer koulutuksenAlkamisVuosi) {
    this.koulutuksenAlkamisVuosi = koulutuksenAlkamisVuosi;
  }
}
