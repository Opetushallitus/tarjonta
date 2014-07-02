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
package fi.vm.sade.tarjonta.service.business.impl;

import com.google.common.base.Preconditions;
import fi.vm.sade.tarjonta.model.BaseKoulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToRDTOTest;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.types.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jani
 */
public class EntityUtilsTest {

    public EntityUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of joinListToString method, of class EntityUtils.
     */
    @Test
    public void testJoinListToString() {
        Collection<String> list = new ArrayList<String>(Arrays.asList("uri1", "uri2", "uri3"));
        final String result = EntityUtils.joinListToString(list);
        assertEquals("|uri1|uri2|uri3|", result);
        assertEquals(3, EntityUtils.splitStringToList(result).size());
    }

    @Test
    public void copyKomoRelationsToKomotoDto() {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();

        LueKoulutusVastausTyyppi koulutusTyyppi = new LueKoulutusVastausTyyppi();
        KoulutusmoduuliKoosteTyyppi t = new KoulutusmoduuliKoosteTyyppi();
        koulutusTyyppi.setKoulutusmoduuli(t);

        //Tests empty komoto
        EntityUtils.copyKomoRelationsToKomotoDto(komoto, koulutusTyyppi);

        assertEquals(null, t.getOpintoalaUri());
        assertEquals(null, t.getKoulutusalaUri());
        assertEquals(null, t.getKoulutusasteUri());
        assertEquals(null, t.getKoulutuskoodiUri());
        assertEquals(null, t.getLaajuusarvoUri());
        assertEquals(null, t.getLaajuusyksikkoUri());
        assertEquals(null, t.getUlkoinenTunniste());
        assertEquals(null, t.getNqfLuokitus());
        assertEquals(null, t.getEqfLuokitus());
        assertEquals(null, t.getKoulutusohjelmakoodiUri());
        assertEquals(null, t.getLukiolinjakoodiUri());

        komoto.setOpintoalaUri(FieldNames.OPINTOALA.name());
        komoto.setKoulutusalaUri(FieldNames.KOULUTUSALA.name());
        komoto.setKoulutusasteUri(FieldNames.KOULUTUSASTE.name());
        komoto.setKoulutusUri(FieldNames.KOULUTUS.name());
        komoto.setOpintojenLaajuusarvoUri(FieldNames.OPINTOJEN_LAAJUUSARVO.name());
        komoto.setOpintojenLaajuusyksikkoUri(FieldNames.OPINTOJEN_LAAJUUSYKSIKKO.name());
        komoto.setUlkoinenTunniste(FieldNames.TUNNISTE.name());
        komoto.setNqfUri(FieldNames.NQF.name());
        komoto.setEqfUri(FieldNames.EQF.name());
        komoto.setKoulutustyyppiUri(FieldNames.KOULUTUSTYYPPI.name());
        komoto.setLukiolinjaUri(FieldNames.LUKIOLINJA.name());
        komoto.setKoulutusohjelmaUri(FieldNames.KOULUTUSOHJELMA.name());

        //Test updated komoto 
        EntityUtils.copyKomoRelationsToKomotoDto(komoto, koulutusTyyppi);

        assertEquals(FieldNames.OPINTOALA.name(), t.getOpintoalaUri());
        assertEquals(FieldNames.KOULUTUSALA.name(), t.getKoulutusalaUri());
        assertEquals(FieldNames.KOULUTUSASTE.name(), t.getKoulutusasteUri());
        assertEquals(FieldNames.KOULUTUS.name(), t.getKoulutuskoodiUri());
        assertEquals(FieldNames.OPINTOJEN_LAAJUUSARVO.name(), t.getLaajuusarvoUri());
        assertEquals(FieldNames.OPINTOJEN_LAAJUUSYKSIKKO.name(), t.getLaajuusyksikkoUri());
        assertEquals(FieldNames.TUNNISTE.name(), t.getUlkoinenTunniste());
        assertEquals(FieldNames.NQF.name(), t.getNqfLuokitus());
        assertEquals(FieldNames.EQF.name(), t.getEqfLuokitus());
        assertEquals(FieldNames.KOULUTUSOHJELMA.name(), t.getKoulutusohjelmakoodiUri());
        assertEquals(FieldNames.LUKIOLINJA.name(), t.getLukiolinjakoodiUri());
    }

    @Test
    public void copyKomoRelationsToKomotoEntity() {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();

        LueKoulutusVastausTyyppi koulutusTyyppi = new LueKoulutusVastausTyyppi();
        KoulutusmoduuliKoosteTyyppi t = new KoulutusmoduuliKoosteTyyppi();
        koulutusTyyppi.setKoulutusmoduuli(t);

        PaivitaKoulutusTyyppi p = new PaivitaKoulutusTyyppi();
        p.setTila(TarjontaTila.VALMIS);
        p.setKoulutuksenAlkamisPaiva(new Date());
        p.setKesto(new KoulutuksenKestoTyyppi("1", "2"));
        p.setKoulutusmoduuli(t);

        //Tests empty komoto
        EntityUtils.copyFields(p, komoto);

        assertEquals(null, komoto.getOpintoalaUri());
        assertEquals(null, komoto.getKoulutusalaUri());
        assertEquals(null, komoto.getKoulutusasteUri());
        assertEquals(null, komoto.getKoulutusUri());
        assertEquals(null, komoto.getOpintojenLaajuusarvoUri());
        assertEquals(null, komoto.getOpintojenLaajuusyksikkoUri());
        assertEquals(null, komoto.getUlkoinenTunniste());
        assertEquals(null, komoto.getNqfUri());
        assertEquals(null, komoto.getEqfUri());
        assertEquals(null, komoto.getKoulutusohjelmaUri());
        assertEquals(null, komoto.getLukiolinjaUri());

        t.setOpintoalaUri(FieldNames.OPINTOALA.name());
        t.setKoulutusalaUri(FieldNames.KOULUTUSALA.name());
        t.setKoulutusasteUri(FieldNames.KOULUTUSASTE.name());
        t.setKoulutuskoodiUri(FieldNames.KOULUTUS.name());
        t.setLaajuusarvoUri(FieldNames.OPINTOJEN_LAAJUUSARVO.name());
        t.setLaajuusyksikkoUri(FieldNames.OPINTOJEN_LAAJUUSYKSIKKO.name());
        t.setUlkoinenTunniste(FieldNames.TUNNISTE.name());
        t.setNqfLuokitus(FieldNames.NQF.name());
        t.setEqfLuokitus(FieldNames.EQF.name());
        t.setLukiolinjakoodiUri(FieldNames.LUKIOLINJA.name());
        t.setKoulutusohjelmakoodiUri(FieldNames.KOULUTUSOHJELMA.name());

        //Test updated komoto 
        EntityUtils.copyFields(p, komoto);

        assertEquals(FieldNames.OPINTOALA.name(), komoto.getOpintoalaUri());
        assertEquals(FieldNames.KOULUTUSALA.name(), komoto.getKoulutusalaUri());
        assertEquals(FieldNames.KOULUTUSASTE.name(), komoto.getKoulutusasteUri());
        assertEquals(FieldNames.KOULUTUS.name(), komoto.getKoulutusUri());
        assertEquals(FieldNames.OPINTOJEN_LAAJUUSARVO.name(), komoto.getOpintojenLaajuusarvoUri());
        assertEquals(FieldNames.OPINTOJEN_LAAJUUSYKSIKKO.name(), komoto.getOpintojenLaajuusyksikkoUri());
        assertEquals(FieldNames.TUNNISTE.name(), komoto.getUlkoinenTunniste());
        assertEquals(FieldNames.NQF.name(), komoto.getNqfUri());
        assertEquals(FieldNames.EQF.name(), komoto.getEqfUri());
        assertEquals(FieldNames.LUKIOLINJA.name(), komoto.getLukiolinjaUri());
        assertEquals(FieldNames.KOULUTUSOHJELMA.name(), komoto.getKoulutusohjelmaUri());
    }

}
