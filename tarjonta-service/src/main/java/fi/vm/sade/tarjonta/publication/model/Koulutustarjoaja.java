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
package fi.vm.sade.tarjonta.publication.model;

/**
 * Koulutustarjoaja (LearningOpportunityProvider) has now been placed into Organisaatio -service.
 * This means that Tarjonta -service has no other data to present than OID -reference. This class
 * acts as a placeholder for that information.
 *
 * @author Jukka Raanamo
 */
public class Koulutustarjoaja {

  private String organisaatioOid;

  public Koulutustarjoaja(String oid) {
    this.organisaatioOid = oid;
  }

  public String getOrganisaatioOid() {
    return organisaatioOid;
  }
}
