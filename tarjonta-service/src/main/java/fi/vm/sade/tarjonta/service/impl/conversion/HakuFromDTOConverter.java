/*
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
package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HaunNimi;
import fi.vm.sade.tarjonta.service.types.SisaisetHakuAjat;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Tuomas Katva
 */
public class HakuFromDTOConverter extends AbstractToDomainConverter<HakuTyyppi, Haku> {

  @Autowired private TarjontaKoodistoHelper koodistoHelpper;

  @Override
  public Haku convert(HakuTyyppi s) {
    Haku m = new Haku();
    m.setVersion(s.getVersion());
    m.setNimi(convertNimis(s.getHaunKielistetytNimet()));
    m.setOid(s.getOid());
    m.setHakukausiUri(s.getHakukausiUri());
    m.setHakukausiVuosi(s.getHakuVuosi());
    m.setHakulomakeUrl(s.getHakulomakeUrl());
    m.setHakutapaUri(s.getHakutapaUri());
    m.setHakutyyppiUri(s.getHakutyyppiUri());
    m.setKohdejoukkoUri(s.getKohdejoukkoUri());
    m.setKoulutuksenAlkamiskausiUri(s.getKoulutuksenAlkamisKausiUri());
    m.setKoulutuksenAlkamisVuosi(s.getKoulutuksenAlkamisVuosi());
    m.setSijoittelu(s.isSijoittelu());
    m.setTila(EntityUtils.convertTila(s.getHaunTila()));
    m.setHaunTunniste(s.getHaunTunniste());
    if (s.getViimeisinPaivitysPvm() != null) {
      m.setLastUpdateDate(s.getViimeisinPaivitysPvm());
    } else {
      m.setLastUpdateDate(new Date());
    }
    m.setLastUpdatedByOid(s.getViimeisinPaivittajaOid());

    convertSisaisetHaunAlkamisAjat(m, s.getSisaisetHakuajat());
    return m;
  }

  private MonikielinenTeksti convertNimis(List<HaunNimi> haunNimet) {
    MonikielinenTeksti mt = new MonikielinenTeksti();
    if (haunNimet != null) {
      for (HaunNimi nimi : haunNimet) {
        mt.addTekstiKaannos(
            koodistoHelpper.convertKielikoodiToKieliUri(nimi.getKielikoodi()), nimi.getNimi());
      }
    }
    return mt;
  }

  private void convertSisaisetHaunAlkamisAjat(Haku mm, List<SisaisetHakuAjat> sisAjat) {
    if (sisAjat != null) {
      for (SisaisetHakuAjat curHA : sisAjat) {
        mm.addHakuaika(CommonFromDTOConverter.convertSisaisetHakuAjatToHakuaika(curHA));
      }
    }
  }
}
