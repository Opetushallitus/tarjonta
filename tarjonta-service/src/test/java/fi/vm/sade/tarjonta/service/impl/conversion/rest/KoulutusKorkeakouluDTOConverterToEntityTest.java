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
package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import java.util.Date;
import java.util.HashSet;
import org.joda.time.DateTime;
import org.junit.Test;

/**
 * @author jani
 */
public class KoulutusKorkeakouluDTOConverterToEntityTest {

  private final KoulutusCommonConverter instance = new KoulutusCommonConverter();

  @Test(expected = NullPointerException.class)
  public void testHandleDatesNoKausi() {
    KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
    KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();
    instance.handleDates(komoto, dto);
  }

  @Test(expected = NullPointerException.class)
  public void testHandleDatesNoYear() {
    KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
    KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();
    dto.setKoulutuksenAlkamiskausi(new KoodiUrisV1RDTO());
    instance.handleDates(komoto, dto);
  }

  @Test(expected = NullPointerException.class)
  public void testHandleEmptyStrKausiVuosi() {
    KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
    KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();
    KoodiUrisV1RDTO koodiUrisV1RDTO = new KoodiUrisV1RDTO();
    koodiUrisV1RDTO.setUri("");
    koodiUrisV1RDTO.setVersio(1);
    instance.handleDates(komoto, dto);
  }

  @Test()
  public void testHandleDatesOneOrManyDates() {
    KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
    KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();
    HashSet<Date> dates = Sets.newHashSet();
    dates.add(new DateTime(2014, 1, 1, 1, 1).toDate());
    dates.add(new DateTime(2014, 1, 2, 1, 1).toDate());
    dates.add(new DateTime(2014, 2, 1, 1, 1).toDate());
    dto.setKoulutuksenAlkamisPvms(dates);

    instance.handleDates(komoto, dto);

    assertEquals(3, dto.getKoulutuksenAlkamisPvms().size());
    assertEquals(null, dto.getKoulutuksenAlkamiskausi());
    assertEquals(null, dto.getKoulutuksenAlkamisvuosi());
  }

  @Test()
  public void testHandleDatesOneOrManyDatesRemoveOld() {
    KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
    komoto.addKoulutuksenAlkamisPvms(new DateTime(2015, 1, 5, 1, 1).toDate());
    komoto.addKoulutuksenAlkamisPvms(new DateTime(2014, 1, 1, 0, 0).toDate());

    KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();
    HashSet<Date> dates = Sets.newHashSet();
    dates.add(new DateTime(2014, 1, 1, 1, 1).toDate());
    dates.add(new DateTime(2014, 1, 2, 1, 1).toDate());
    dates.add(new DateTime(2014, 2, 1, 1, 1).toDate());
    dto.setKoulutuksenAlkamisPvms(dates);

    instance.handleDates(komoto, dto);

    assertEquals(3, dto.getKoulutuksenAlkamisPvms().size());
    assertEquals(null, dto.getKoulutuksenAlkamiskausi());
    assertEquals(null, dto.getKoulutuksenAlkamisvuosi());
  }
}
