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
package fi.vm.sade.tarjonta.service.impl;

import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Api agnostic code, do not use any API (rest/soap) DTOs here! */
@Transactional(readOnly = true)
@Service
public class HakukohdeService {

  @Autowired private HakukohdeDAO hakukohdeDAO;

  @Transactional(readOnly = false)
  public Hakukohde createHakukohde(Hakukohde hakukohde) {

    return hakukohdeDAO.insert(hakukohde);
  }

  @Transactional(readOnly = false)
  public Hakukohde updateHakukohde(Hakukohde hakukohde) {

    hakukohdeDAO.update(hakukohde);
    return hakukohde;
  }

  public List<Hakukohde> findByKoulutus(String koulutusmoduuliToteutusOid) {

    return hakukohdeDAO.findByKoulutusOid(koulutusmoduuliToteutusOid);
  }
}
