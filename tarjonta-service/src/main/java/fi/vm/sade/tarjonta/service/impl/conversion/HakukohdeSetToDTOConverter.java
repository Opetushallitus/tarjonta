/*
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
package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Jani Wilén
 */
public class HakukohdeSetToDTOConverter {

  public static List<HakukohdeTyyppi> convert(Set<Hakukohde> fromSet) {
    List<HakukohdeTyyppi> toHakuTyyppis = new ArrayList<HakukohdeTyyppi>();
    if (fromSet != null) {

      for (Hakukohde hakukohde : fromSet) {
        HakukohdeTyyppi hakukohdeTyyppi = new HakukohdeTyyppi();
        hakukohdeTyyppi.setHakukohdeKoodistoNimi(hakukohde.getHakukohdeKoodistoNimi());
        hakukohdeTyyppi.setHakukohdeNimi(hakukohde.getHakukohdeNimi());
        hakukohdeTyyppi.setValintaperustekuvausKoodiUri(
            hakukohde.getValintaperustekuvausKoodiUri());
        hakukohdeTyyppi.setAloituspaikat(hakukohde.getAloituspaikatLkm());
        hakukohdeTyyppi.setValinnanAloituspaikat(hakukohde.getValintojenAloituspaikatLkm());
        hakukohdeTyyppi.setOid(hakukohde.getOid());
        hakukohdeTyyppi.setHakukohteenTila(
            fi.vm.sade.tarjonta.service.types.TarjontaTila.fromValue(hakukohde.getTila().name()));
        hakukohdeTyyppi.setLiitteidenToimitusPvm(hakukohde.getLiitteidenToimitusPvm());
        hakukohdeTyyppi.setHakukelpoisuusVaatimukset(getHakukelpoisuusVaatimus(hakukohde));
        toHakuTyyppis.add(hakukohdeTyyppi);
      }
    }

    return toHakuTyyppis;
  }

  private static String getHakukelpoisuusVaatimus(Hakukohde hakukohde) {
    if (hakukohde.getHakukelpoisuusVaatimukset() != null
        && hakukohde.getHakukelpoisuusVaatimukset().size() > 0) {
      List<String> vaatimukset = new ArrayList<String>(hakukohde.getHakukelpoisuusVaatimukset());
      return vaatimukset.get(0);
    } else {
      return null;
    }
  }
}
