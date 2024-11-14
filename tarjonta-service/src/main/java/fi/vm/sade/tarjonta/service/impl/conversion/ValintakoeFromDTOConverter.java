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
import fi.vm.sade.tarjonta.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.service.types.AjankohtaTyyppi;
import fi.vm.sade.tarjonta.service.types.PisterajaTyyppi;
import fi.vm.sade.tarjonta.service.types.ValintakoeTyyppi;
import java.math.BigDecimal;
import java.util.*;

/** Created by: Tuomas Katva Date: 22.1.2013 */
public class ValintakoeFromDTOConverter
    extends AbstractToDomainConverter<ValintakoeTyyppi, Valintakoe> {

  @Override
  public Valintakoe convert(ValintakoeTyyppi valintakoeTyyppi) {
    Valintakoe valintakoe = new Valintakoe();

    if (valintakoeTyyppi.getValintakokeenTunniste() != null) {
      valintakoe.setId(new Long(valintakoeTyyppi.getValintakokeenTunniste()));
    }

    valintakoe.setKuvaus(
        CommonFromDTOConverter.convertMonikielinenTekstiTyyppiToDomainValue(
            valintakoeTyyppi.getKuvaukset()));
    valintakoe.setTyyppiUri(valintakoeTyyppi.getValintakokeenTyyppi());
    for (ValintakoeAjankohta ajankohta :
        convertAjankohtaTyyppiToValintakoeAjankohta(valintakoe, valintakoeTyyppi.getAjankohdat())) {
      valintakoe.addAjankohta(ajankohta);
    }
    if (valintakoeTyyppi.getLisaNaytot() != null) {
      valintakoe.setLisanaytot(
          CommonFromDTOConverter.convertMonikielinenTekstiTyyppiToDomainValue(
              valintakoeTyyppi.getLisaNaytot()));
    }
    if (valintakoeTyyppi.getPisterajat() != null) {
      valintakoe.setPisterajat(convertPisterajat(valintakoe, valintakoeTyyppi.getPisterajat()));
    }

    if (valintakoeTyyppi.getViimeisinPaivittajaOid() != null) {
      valintakoe.setLastUpdatedByOid(valintakoeTyyppi.getViimeisinPaivittajaOid());
    }

    valintakoe.setLastUpdateDate(Calendar.getInstance().getTime());

    return valintakoe;
  }

  private Set<Pisteraja> convertPisterajat(Valintakoe vk, List<PisterajaTyyppi> pisterajaTyypit) {
    Set<Pisteraja> pisterajat = new HashSet<Pisteraja>();
    for (PisterajaTyyppi pisterajaTyyppi : pisterajaTyypit) {
      Pisteraja pisteRaja = new Pisteraja();
      pisteRaja.setValintakoe(vk);
      pisteRaja.setAlinHyvaksyttyPistemaara(
          new BigDecimal(
              Double.toString(
                  pisterajaTyyppi
                      .getAlinHyvaksyttyPistemaara()))); // pisterajaTyyppi.getAlinHyvaksyttyPistemaara());
      pisteRaja.setAlinPistemaara(
          new BigDecimal(
              Double.toString(
                  pisterajaTyyppi.getAlinPistemaara()))); // pisterajaTyyppi.getAlinPistemaara());
      pisteRaja.setValinnanPisterajaTyyppi(pisterajaTyyppi.getValinnanPisteraja().value());
      pisteRaja.setYlinPistemaara(
          new BigDecimal(Double.toString(pisterajaTyyppi.getYlinPistemaara())));
      if (pisterajaTyyppi.getPisterajaTunniste() != null) {
        pisteRaja.setId(new Long(pisterajaTyyppi.getPisterajaTunniste()));
      }
      pisterajat.add(pisteRaja);
    }
    return pisterajat;
  }

  private List<ValintakoeAjankohta> convertAjankohtaTyyppiToValintakoeAjankohta(
      Valintakoe vk, List<AjankohtaTyyppi> ajankohtaTyyppis) {
    ArrayList<ValintakoeAjankohta> valintakoeAjankohtas = new ArrayList<ValintakoeAjankohta>();

    for (AjankohtaTyyppi ajankohtaTyyppi : ajankohtaTyyppis) {
      ValintakoeAjankohta valintakoeAjankohta = new ValintakoeAjankohta();
      valintakoeAjankohta.setValintakoe(vk);
      valintakoeAjankohta.setAjankohdanOsoite(
          CommonFromDTOConverter.convertOsoiteToOsoiteTyyppi(
              ajankohtaTyyppi.getValintakoeAjankohtaOsoite()));
      valintakoeAjankohta.setAlkamisaika(ajankohtaTyyppi.getAlkamisAika());
      valintakoeAjankohta.setPaattymisaika(ajankohtaTyyppi.getPaattymisAika());
      valintakoeAjankohta.setLisatietoja(ajankohtaTyyppi.getKuvaus());
      valintakoeAjankohtas.add(valintakoeAjankohta);
    }

    return valintakoeAjankohtas;
  }
}
