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
package fi.vm.sade.tarjonta.dao;

import static org.junit.Assert.*;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.dao.impl.HakuDAOImpl;
import fi.vm.sade.tarjonta.model.Haku;
import jakarta.persistence.EntityManager;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

/**
 * A separate test for MonikielinenTeksti entity that is used from several other entities. This test
 * uses {@link Haku} as container.
 *
 * @author Jukka Raanamo
 */
@Transactional
public class MonikielinenTekstiTest extends TestUtilityBase {

  private TarjontaFixtures fixtures = new TarjontaFixtures();
  private Haku hakuWithName;
  private EntityManager em;

  @Before
  public void setUp() {

    Haku haku = fixtures.createHaku();
    haku.setNimi(TarjontaFixtures.createText("value fi", "value sv", "value en"));
    hakuWithName = insertAndLoad(haku);

    em = getEntityManager();
  }

  @Test
  public void testTekstiIsPersistendWithParentEntity() {

    Haku haku = hakuDao.read(hakuWithName.getId());
    assertEquals("value fi", haku.getNimi().getTekstiForKieliKoodi("fi"));
    assertEquals("value en", haku.getNimi().getTekstiForKieliKoodi("en"));
  }

  @Test
  public void testRemoveTeksti() {

    hakuWithName.setNimi(null);
    assertNull(updateAndLoad(hakuWithName).getNimi());

    // note that ophan removal does not work with OneToOne mappings
    // where mapped class has generated id

  }

  @Test
  public void testOrphanValuesAreRemoved() {

    long numBefore = numTekstiKaannos();
    assertTrue(numBefore > 0);

    hakuWithName.setNimi(TarjontaFixtures.createText(null, "value sv", null));
    hakuDao.update(hakuWithName);

    assertEquals(numBefore - 2, numTekstiKaannos());
  }

  private long numTekstiKaannos() {
    return (Long) em.createQuery("select count(t.id) from TekstiKaannos t").getSingleResult();
  }

  private Haku updateAndLoad(Haku haku) {

    hakuDao.update(haku);
    return hakuDao.read(haku.getId());
  }

  private Haku insertAndLoad(Haku haku) {

    Haku h = hakuDao.insert(haku);
    return hakuDao.read(h.getId());
  }

  private EntityManager getEntityManager() {
    return ((HakuDAOImpl) hakuDao).getEntityManager();
  }
}
