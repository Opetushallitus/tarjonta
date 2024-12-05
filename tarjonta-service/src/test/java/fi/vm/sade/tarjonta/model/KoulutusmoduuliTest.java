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
package fi.vm.sade.tarjonta.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.Date;
import org.junit.Test;

/**
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliTest {

  @Test
  public void testGetTilaAfterCreate() {
    Koulutusmoduuli moduuli = newModuuli();
    assertEquals(TarjontaTila.LUONNOS, moduuli.getTila());
  }

  @Test
  public void testUpdateTimestampIsSetAtInsert() {

    Koulutusmoduuli m = newModuuli();
    m.beforePersist();
    assertNotNull(m.getUpdated());
  }

  @Test
  public void testUpdateTimestampIsUpdatesAtUpdate() throws Exception {

    Koulutusmoduuli m = newModuuli();
    m.beforePersist();
    Date before = m.getUpdated();

    Thread.sleep(50L);

    m.beforeUpdate();
    Date after = m.getUpdated();

    assertEquals(1, after.compareTo(before));
  }

  private Koulutusmoduuli newModuuli() {
    return new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
  }
}
