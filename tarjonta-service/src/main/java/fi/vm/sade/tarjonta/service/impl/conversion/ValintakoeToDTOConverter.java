package fi.vm.sade.tarjonta.service.impl.conversion; /*
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

import fi.vm.sade.tarjonta.model.Pisteraja;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.model.ValintakoeAjankohta;
import fi.vm.sade.tarjonta.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.service.types.AjankohtaTyyppi;
import fi.vm.sade.tarjonta.service.types.PisterajaTyyppi;
import fi.vm.sade.tarjonta.service.types.ValinnanPisterajaTyyppi;
import fi.vm.sade.tarjonta.service.types.ValintakoeTyyppi;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Created by: Tuomas Katva Date: 22.1.2013 */
public class ValintakoeToDTOConverter
    extends AbstractFromDomainConverter<Valintakoe, ValintakoeTyyppi> {

  @Override
  public ValintakoeTyyppi convert(Valintakoe valintakoe) {
    ValintakoeTyyppi valintakoeTyyppi = new ValintakoeTyyppi();

    valintakoeTyyppi.setValintakokeenTunniste(valintakoe.getId().toString());
    valintakoeTyyppi.setValintakokeenTyyppi(valintakoe.getTyyppiUri());
    valintakoeTyyppi.setKuvaukset(
        CommonToDTOConverter.convertMonikielinenTekstiToTekstiTyyppi(valintakoe.getKuvaus()));
    valintakoeTyyppi.getAjankohdat().addAll(convertAjankohta(valintakoe.getAjankohtas()));
    if (valintakoe.getLisanaytot() != null) {
      valintakoeTyyppi.setLisaNaytot(
          CommonToDTOConverter.convertMonikielinenTekstiToTekstiTyyppi(valintakoe.getLisanaytot()));
    }
    if (valintakoe.getPisterajat() != null) {
      valintakoeTyyppi.getPisterajat().addAll(convertPisterajat(valintakoe.getPisterajat()));
    }
    if (valintakoe.getLastUpdateDate() != null) {
      valintakoeTyyppi.setViimeisinPaivitysPvm(valintakoe.getLastUpdateDate());
    }
    if (valintakoe.getLastUpdatedByOid() != null) {
      valintakoeTyyppi.setViimeisinPaivittajaOid(valintakoe.getLastUpdatedByOid());
    }

    return valintakoeTyyppi;
  }

  private List<PisterajaTyyppi> convertPisterajat(Set<Pisteraja> pisterajat) {
    List<PisterajaTyyppi> pisterajaTyypit = new ArrayList<PisterajaTyyppi>();

    for (Pisteraja pisteraja : pisterajat) {
      PisterajaTyyppi pisterajatyyppi = new PisterajaTyyppi();
      pisterajatyyppi.setAlinHyvaksyttyPistemaara(
          pisteraja.getAlinHyvaksyttyPistemaara() != null
              ? pisteraja.getAlinHyvaksyttyPistemaara().doubleValue()
              : null);
      pisterajatyyppi.setAlinPistemaara(
          pisteraja.getAlinPistemaara() != null
              ? pisteraja.getAlinPistemaara().doubleValue()
              : null);
      pisterajatyyppi.setYlinPistemaara(
          pisteraja.getYlinPistemaara() != null
              ? pisteraja.getYlinPistemaara().doubleValue()
              : null);
      pisterajatyyppi.setPisterajaTunniste(pisteraja.getId().toString());
      pisterajatyyppi.setValinnanPisteraja(
          ValinnanPisterajaTyyppi.fromValue(pisteraja.getValinnanPisterajaTyyppi()));
      pisterajaTyypit.add(pisterajatyyppi);
    }

    return pisterajaTyypit;
  }

  private List<AjankohtaTyyppi> convertAjankohta(Set<ValintakoeAjankohta> ajankohtas) {
    ArrayList<AjankohtaTyyppi> ajankohtaTyyppis = new ArrayList<AjankohtaTyyppi>();

    for (ValintakoeAjankohta valintakoeAjankohta : ajankohtas) {

      AjankohtaTyyppi ajankohtaTyyppi = new AjankohtaTyyppi();

      ajankohtaTyyppi.setAlkamisAika(valintakoeAjankohta.getAlkamisaika());
      ajankohtaTyyppi.setPaattymisAika(valintakoeAjankohta.getPaattymisaika());
      ajankohtaTyyppi.setKuvaus(valintakoeAjankohta.getLisatietoja());
      ajankohtaTyyppi.setValintakoeAjankohtaOsoite(
          CommonToDTOConverter.convertOsoiteToOsoiteTyyppi(
              valintakoeAjankohta.getAjankohdanOsoite()));

      ajankohtaTyyppis.add(ajankohtaTyyppi);
    }

    return ajankohtaTyyppis;
  }
}
