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
package fi.vm.sade.tarjonta.shared.auth;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Oganisation context. Immutable. */
public class OrganisaatioContext {

  private static final Logger LOG = LoggerFactory.getLogger(OrganisaatioContext.class);

  final String ooid;

  private OrganisaatioContext(final String ooid) {
    this.ooid = ooid;
  }

  /**
   * Creates new context.
   *
   * @param ooid Organisaatio OID
   */
  public static final OrganisaatioContext getContext(final String ooid) {
    if (ooid == null) {
      Exception e = new Exception();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(baos);
      e.printStackTrace(pw);
      pw.close();
      LOG.warn(
          "Someone is  getting org context with null oid, stack trace follows:\n {}",
          new String(baos.toByteArray()));
    }
    return new OrganisaatioContext(ooid);
  }

  @Override
  public String toString() {
    return this.getClass().getName() + ": " + ooid;
  }

  @Override
  public int hashCode() {
    return ooid.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
}
