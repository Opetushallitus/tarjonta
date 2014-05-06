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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.BaseKoulutusmoduuli;
import fi.vm.sade.tarjonta.model.Kielivalikoima;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.service.enums.KoulutustyyppiEnum;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiValikoimaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.easymock.EasyMock;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.powermock.reflect.Whitebox;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;

/**
 * Test komo and komoto koodi uri override.
 *
 * @author jani
 */
public class EntityConverterToRDTOTest {

    private static final String NOT_TESTED = "this test will ingnore this data field";
    private static final Set<KoodistoUri> SET_KOMOTO_TUTKINTONIMIKE = Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMOTO, FieldNames.TUTKINTONIMIKE)));
    private static final Set<KoodistoUri> SET_KOMO_TUTKINTONIMIKE = Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE)));

    private enum Type {

        KOMO,
        KOMO_CHILD,
        KOMOTO,
        NOT_TESTED

    };

    private static final Locale FI = new Locale("FI");

    private EntityConverterToRDTO<KoulutusKorkeakouluV1RDTO> instanceKk;

    private EntityConverterToRDTO<KoulutusLukioV1RDTO> instanceLukio;

    private KoulutusCommonConverter commonConverterMock;

    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConvertersMock;

    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKuvausConvertersMock;

    private KoulutusmoduuliDAO koulutusmoduuliDAOMock;

    @Before
    public void setUp() {
        commonConverterMock = createMock(KoulutusCommonConverter.class);
        komotoKuvausConvertersMock = createMock(KoulutusKuvausV1RDTO.class);
        komoKuvausConvertersMock = createMock(KoulutusKuvausV1RDTO.class);

        instanceKk = new EntityConverterToRDTO<KoulutusKorkeakouluV1RDTO>();
        instanceLukio = new EntityConverterToRDTO<KoulutusLukioV1RDTO>();

        Whitebox.setInternalState(instanceKk, "commonConverter", commonConverterMock);
        Whitebox.setInternalState(instanceKk, "komoKuvausConverters", komoKuvausConvertersMock);
        Whitebox.setInternalState(instanceKk, "komotoKuvausConverters", komotoKuvausConvertersMock);

        koulutusmoduuliDAOMock = createMock(KoulutusmoduuliDAO.class);
        Whitebox.setInternalState(instanceLukio, "commonConverter", commonConverterMock);
        Whitebox.setInternalState(instanceLukio, "komoKuvausConverters", komoKuvausConvertersMock);
        Whitebox.setInternalState(instanceLukio, "komotoKuvausConverters", komotoKuvausConvertersMock);
        Whitebox.setInternalState(instanceLukio, "koulutusmoduuliDAO", koulutusmoduuliDAOMock);

    }

    @Test
    public void testKorkeakoulutusConvertKomoOverrideByKomotoKoodis() {
        Koulutusmoduuli m = new Koulutusmoduuli();
        m.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        korkeakouluPopulateBaseValues(Type.KOMO, m);
        m.setKoulutustyyppiEnum(KoulutustyyppiEnum.KORKEAKOULUTUS);
        m.setTutkintonimikes(Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE))));

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();
        korkeakouluPopulateBaseValues(Type.KOMOTO, t);
        t.setTutkintonimikes(Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMOTO, FieldNames.TUTKINTONIMIKE))));
        t.setKoulutusmoduuli(m);

        final Type returnKomoto = Type.KOMOTO;

        expectKorkeakoulutusNotTestedCalls();

        expect(commonConverterMock.convertToKoodiUrisDTO(SET_KOMOTO_TUTKINTONIMIKE, FI, FieldNames.TUTKINTONIMIKE, false)).andReturn(toKoodiUris(Type.KOMOTO, FieldNames.TUTKINTONIMIKE));

        expectConvertToKoodiDTO(returnKomoto, FieldNames.KOULUTUSKOODI);
        expectConvertToKoodiDTOAllowNull(returnKomoto, FieldNames.TUTKINTO);
        expectConvertToKoodiDTO(returnKomoto, FieldNames.OPINTOJEN_LAAJUUSARVO);
        expectConvertToKoodiDTO(returnKomoto, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO);
        expectConvertToKoodiDTOAllowNull(returnKomoto, FieldNames.KOULUTUSASTE);
        expectConvertToKoodiDTO(returnKomoto, FieldNames.KOULUTUSALA);
        expectConvertToKoodiDTO(returnKomoto, FieldNames.OPINTOALA);
        expectConvertToKoodiDTOAllowNull(returnKomoto, FieldNames.EQF);
        expectConvertToKoodiDTOAllowNull(returnKomoto, FieldNames.NQF);
        expectConvertToKoodiDTOAllowNull(returnKomoto, FieldNames.KOULUTUSTYYPPI);

        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);

        final KoulutusKorkeakouluV1RDTO convert = instanceKk.convert(KoulutusKorkeakouluV1RDTO.class, t, "FI", false);
        EasyMock.verify(commonConverterMock);
        assertKk(convert, returnKomoto);
    }

    @Test
    public void testKorkeakoulutusConvertEmptyKomoOverrideByKomotoKoodis() {
        Koulutusmoduuli m = new Koulutusmoduuli();
        m.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        m.setKoulutustyyppiEnum(KoulutustyyppiEnum.KORKEAKOULUTUS);

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();
        korkeakouluPopulateBaseValues(Type.KOMOTO, t);
        t.setTutkintonimikes(Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMOTO, FieldNames.TUTKINTONIMIKE))));
        t.setKoulutusmoduuli(m);

        final Type returnKomoto = Type.KOMOTO;

        expectKorkeakoulutusNotTestedCalls();

        expect(commonConverterMock.convertToKoodiUrisDTO(SET_KOMOTO_TUTKINTONIMIKE, FI, FieldNames.TUTKINTONIMIKE, false)).andReturn(toKoodiUris(Type.KOMOTO, FieldNames.TUTKINTONIMIKE));

        expectConvertToKomotoKoodiDTO(returnKomoto, FieldNames.KOULUTUSKOODI);
        expectConvertToKomotoKoodiDTOAllowNull(returnKomoto, FieldNames.TUTKINTO);
        expectConvertToKomotoKoodiDTO(returnKomoto, FieldNames.OPINTOJEN_LAAJUUSARVO);
        expectConvertToKomotoKoodiDTO(returnKomoto, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO);
        expectConvertToKomotoKoodiDTOAllowNull(returnKomoto, FieldNames.KOULUTUSASTE);
        expectConvertToKomotoKoodiDTO(returnKomoto, FieldNames.KOULUTUSALA);
        expectConvertToKomotoKoodiDTO(returnKomoto, FieldNames.OPINTOALA);
        expectConvertToKomotoKoodiDTOAllowNull(returnKomoto, FieldNames.EQF);
        expectConvertToKomotoKoodiDTOAllowNull(returnKomoto, FieldNames.NQF);
        expectConvertToKomotoKoodiDTOAllowNull(returnKomoto, FieldNames.KOULUTUSTYYPPI);

        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);

        final KoulutusKorkeakouluV1RDTO convert = instanceKk.convert(KoulutusKorkeakouluV1RDTO.class, t, "FI", false);
        EasyMock.verify(commonConverterMock);
        assertKk(convert, returnKomoto);
    }

    @Test
    public void testKorkeakoulutusConvertKomoNoKomotoKoodiOverride() {
        Koulutusmoduuli m = new Koulutusmoduuli();
        m.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        korkeakouluPopulateBaseValues(Type.KOMO, m);
        m.setKoulutustyyppiEnum(KoulutustyyppiEnum.LUKIOKOULUTUS);
        m.setTutkintonimikes(Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE))));

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();
        t.setKoulutusmoduuli(m);

        final Type returnKomo = Type.KOMO;

        expectKorkeakoulutusNotTestedCalls();

        expect(commonConverterMock.convertToKoodiUrisDTO(SET_KOMO_TUTKINTONIMIKE, FI, FieldNames.TUTKINTONIMIKE, false)).andReturn(toKoodiUris(Type.KOMOTO, FieldNames.TUTKINTONIMIKE));

        expectConvertToKomoKoodiDTO(returnKomo, FieldNames.KOULUTUSKOODI);
        expectConvertToKomoKoodiDTOAllowNull(returnKomo, FieldNames.TUTKINTO);
        expectConvertToKomoKoodiDTO(returnKomo, FieldNames.OPINTOJEN_LAAJUUSARVO);
        expectConvertToKomoKoodiDTO(returnKomo, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO);
        expectConvertToKomoKoodiDTOAllowNull(returnKomo, FieldNames.KOULUTUSASTE);
        expectConvertToKomoKoodiDTO(returnKomo, FieldNames.KOULUTUSALA);
        expectConvertToKomoKoodiDTO(returnKomo, FieldNames.OPINTOALA);
        expectConvertToKomoKoodiDTOAllowNull(returnKomo, FieldNames.EQF);
        expectConvertToKomoKoodiDTOAllowNull(returnKomo, FieldNames.NQF);
        expectConvertToKomoKoodiDTOAllowNull(returnKomo, FieldNames.KOULUTUSTYYPPI);

        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);

        final KoulutusKorkeakouluV1RDTO convert = instanceKk.convert(KoulutusKorkeakouluV1RDTO.class, t, "FI", false);
        EasyMock.verify(commonConverterMock);
        assertKk(convert, returnKomo);
    }

    @Test
    public void testLukioConvertKomoParentChildHierachyAndOverrideByKomoto() {

        //do not add the child komo to komoto object! 
        Koulutusmoduuli tukintoParentKomo = new Koulutusmoduuli();
        tukintoParentKomo.setOid(Type.KOMO.name());
        tukintoParentKomo.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        lukioPopulateBaseValues(Type.KOMO, tukintoParentKomo);
        tukintoParentKomo.setKoulutustyyppiEnum(KoulutustyyppiEnum.LUKIOKOULUTUS);
        tukintoParentKomo.setTutkintonimikes(Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE))));

        Koulutusmoduuli ohjelmaChildKomo = new Koulutusmoduuli();
        lukioPopulateChildKomoBaseValues(ohjelmaChildKomo);

        tukintoParentKomo.setKoulutustyyppiEnum(KoulutustyyppiEnum.LUKIOKOULUTUS);
        tukintoParentKomo.setTutkintonimikes(Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE))));

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();
        lukioPopulateBaseValues(Type.KOMOTO, t);
        t.setLukiolinjaUri(testKey(Type.KOMOTO, FieldNames.LUKIOLINJA));
        t.setTutkintonimikes(Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMOTO, FieldNames.TUTKINTONIMIKE))));
        t.setKoulutusmoduuli(ohjelmaChildKomo); //set the child 'koulutusohjelma' komo object here 

        final Type returnKomoto = Type.KOMOTO;

        expectLukioNotTestedCalls();

        expect(koulutusmoduuliDAOMock.findParentKomo(ohjelmaChildKomo)).andReturn(tukintoParentKomo);
        expect(commonConverterMock.convertToNimiDTO(testKey(Type.KOMO_CHILD, FieldNames.LUKIOLINJA), testKey(Type.KOMOTO, FieldNames.LUKIOLINJA), FI, FieldNames.LUKIOLINJA, true, false)).andReturn(toKoodiUriNimi(Type.KOMOTO, FieldNames.LUKIOLINJA));

        expectConvertToKoodiDTO(Type.KOMO_CHILD, Type.KOMOTO, returnKomoto, FieldNames.TUTKINTONIMIKE);
        expectConvertToKoodiDTOAllowNull(Type.KOMO_CHILD, Type.KOMOTO, returnKomoto, FieldNames.EQF);
        expectConvertToKoodiDTOAllowNull(Type.KOMO_CHILD, Type.KOMOTO, returnKomoto, FieldNames.NQF);

        expect(commonConverterMock.convertToKoodiDTO(anyObject(String.class), anyObject(String.class), eq(FI), eq(FieldNames.TUTKINTO), eq(true), eq(false))).andReturn(null);

        expectConvertToKoodiDTO(returnKomoto, FieldNames.KOULUTUSKOODI);
        expectConvertToKoodiDTO(returnKomoto, FieldNames.OPINTOJEN_LAAJUUSARVO);
        expectConvertToKoodiDTO(returnKomoto, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO);
        expectConvertToKoodiDTOAllowNull(returnKomoto, FieldNames.KOULUTUSASTE);
        expectConvertToKoodiDTO(returnKomoto, FieldNames.KOULUTUSALA);
        expectConvertToKoodiDTO(returnKomoto, FieldNames.OPINTOALA);
        expectConvertToKoodiDTOAllowNull(returnKomoto, FieldNames.KOULUTUSTYYPPI);

        EasyMock.replay(koulutusmoduuliDAOMock);
        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);

        final KoulutusLukioV1RDTO convert = instanceLukio.convert(KoulutusLukioV1RDTO.class, t, "FI", false);
        EasyMock.verify(commonConverterMock);
        assertLukio(convert, returnKomoto);
    }

    @Test
    public void testLukioConvertKomoParentChildHierachy() {
        //do not add the child komo to komoto object! 
        Koulutusmoduuli tukintoParentKomo = new Koulutusmoduuli();
        tukintoParentKomo.setOid(Type.KOMO.name());
        tukintoParentKomo.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        lukioPopulateBaseValues(Type.KOMO, tukintoParentKomo);
        tukintoParentKomo.setKoulutustyyppiEnum(KoulutustyyppiEnum.LUKIOKOULUTUS);
        tukintoParentKomo.setTutkintonimikes(Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE))));

        Koulutusmoduuli ohjelmaChildKomo = new Koulutusmoduuli();
        lukioPopulateChildKomoBaseValues(ohjelmaChildKomo);

        tukintoParentKomo.setKoulutustyyppiEnum(KoulutustyyppiEnum.LUKIOKOULUTUS);
        tukintoParentKomo.setTutkintonimikes(Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE))));

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();
        t.setKoulutusmoduuli(ohjelmaChildKomo);

        final Type returnKomoto = Type.KOMO;

        expectLukioNotTestedCalls();

        expect(koulutusmoduuliDAOMock.findParentKomo(ohjelmaChildKomo)).andReturn(tukintoParentKomo);
        expect(commonConverterMock.convertToNimiDTO(testKey(Type.KOMO_CHILD, FieldNames.LUKIOLINJA), null, FI, FieldNames.LUKIOLINJA, true, false)).andReturn(toKoodiUriNimi(Type.KOMO, FieldNames.LUKIOLINJA));

        expectConvertToKoodiDTO(Type.KOMO_CHILD, null, returnKomoto, FieldNames.TUTKINTONIMIKE);
        expectConvertToKoodiDTOAllowNull(Type.KOMO_CHILD, null, returnKomoto, FieldNames.EQF);
        expectConvertToKoodiDTOAllowNull(Type.KOMO_CHILD, null, returnKomoto, FieldNames.NQF);

        expect(commonConverterMock.convertToKoodiDTO(anyObject(String.class), anyObject(String.class), eq(FI), eq(FieldNames.TUTKINTO), eq(true), eq(false))).andReturn(null);

        expectConvertToKomoKoodiDTO(returnKomoto, FieldNames.KOULUTUSKOODI);
        expectConvertToKomoKoodiDTO(returnKomoto, FieldNames.OPINTOJEN_LAAJUUSARVO);
        expectConvertToKomoKoodiDTO(returnKomoto, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO);
        expectConvertToKomoKoodiDTOAllowNull(returnKomoto, FieldNames.KOULUTUSASTE);
        expectConvertToKomoKoodiDTO(returnKomoto, FieldNames.KOULUTUSALA);
        expectConvertToKomoKoodiDTO(returnKomoto, FieldNames.OPINTOALA);
        expectConvertToKomoKoodiDTOAllowNull(returnKomoto, FieldNames.KOULUTUSTYYPPI);

        EasyMock.replay(koulutusmoduuliDAOMock);
        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);

        final KoulutusLukioV1RDTO convert = instanceLukio.convert(KoulutusLukioV1RDTO.class, t, "FI", false);
        EasyMock.verify(commonConverterMock);
        assertLukio(convert, returnKomoto);
    }

    private void expectConvertToKoodiDTO(Type returnType, FieldNames field) {
        expect(commonConverterMock.convertToKoodiDTO(testKey(Type.KOMO, field), testKey(Type.KOMOTO, field), FI, field, false)).andReturn(toKoodiUri(returnType, field));
    }

    private void expectConvertToKoodiDTOAllowNull(Type returnType, FieldNames field) {
        expect(commonConverterMock.convertToKoodiDTO(testKey(Type.KOMO, field), testKey(Type.KOMOTO, field), FI, field, true, false)).andReturn(toKoodiUri(returnType, field));
    }

    private void expectConvertToKomotoKoodiDTO(Type returnType, FieldNames field) {
        expect(commonConverterMock.convertToKoodiDTO(null, testKey(Type.KOMOTO, field), FI, field, false)).andReturn(toKoodiUri(returnType, field));
    }

    private void expectConvertToKomotoKoodiDTOAllowNull(Type returnType, FieldNames field) {
        expect(commonConverterMock.convertToKoodiDTO(null, testKey(Type.KOMOTO, field), FI, field, true, false)).andReturn(toKoodiUri(returnType, field));
    }

    private void expectConvertToKomoKoodiDTO(Type returnType, FieldNames field) {
        expect(commonConverterMock.convertToKoodiDTO(testKey(Type.KOMO, field), null, FI, field, false)).andReturn(toKoodiUri(returnType, field));
    }

    private void expectConvertToKomoKoodiDTOAllowNull(Type returnType, FieldNames field) {
        expect(commonConverterMock.convertToKoodiDTO(testKey(Type.KOMO, field), null, FI, field, true, false)).andReturn(toKoodiUri(returnType, field));
    }

    private void expectConvertToKoodiDTO(Type param1, Type param2, Type returnType, FieldNames field) {
        expect(commonConverterMock.convertToKoodiDTO(testKey(param1, field), param2 != null ? testKey(param2, field) : null, FI, field, false)).andReturn(toKoodiUri(returnType, field));
    }

    private void expectConvertToKoodiDTOAllowNull(Type param1, Type param2, Type returnType, FieldNames field) {
        expect(commonConverterMock.convertToKoodiDTO(testKey(param1, field), param2 != null ? testKey(param2, field) : null, FI, field, true, false)).andReturn(toKoodiUri(returnType, field));
    }

    private void assertKk(final KoulutusKorkeakouluV1RDTO convert, final Type type) {
        assertEquals(testKey(type, FieldNames.OPINTOALA), convert.getOpintoala().getUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSALA), convert.getKoulutusala().getUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSASTE), convert.getKoulutusaste().getUri());
        assertEquals(false, convert.getTutkintonimikes().getUris().isEmpty());
        assertEquals(testKey(type, FieldNames.KOULUTUSKOODI), convert.getKoulutuskoodi().getUri());
        assertEquals(null, convert.getKoulutusohjelma().getUri()); //only name, not uri
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSARVO), convert.getOpintojenLaajuusarvo().getUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO), convert.getOpintojenLaajuusyksikko().getUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSTYYPPI), convert.getKoulutustyyppi().getUri());
        assertEquals(testKey(type, FieldNames.TUTKINTO), convert.getTutkinto().getUri());
        assertEquals(testKey(type, FieldNames.TUNNISTE), convert.getTunniste());
        assertEquals(testKey(type, FieldNames.NQF), convert.getNqf().getUri());
        assertEquals(testKey(type, FieldNames.EQF), convert.getEqf().getUri());
    }

    private void assertLukio(final KoulutusLukioV1RDTO convert, final Type type) {
        assertEquals(testKey(type, FieldNames.OPINTOALA), convert.getOpintoala().getUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSALA), convert.getKoulutusala().getUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSASTE), convert.getKoulutusaste().getUri());
        assertEquals(testKey(type, FieldNames.TUTKINTONIMIKE), convert.getTutkintonimike().getUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSKOODI), convert.getKoulutuskoodi().getUri());
        assertEquals(testKey(type, FieldNames.LUKIOLINJA), convert.getKoulutusohjelma().getUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSARVO), convert.getOpintojenLaajuusarvo().getUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO), convert.getOpintojenLaajuusyksikko().getUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSTYYPPI), convert.getKoulutustyyppi().getUri());
        assertEquals(null, convert.getTutkinto());
        assertEquals(testKey(type, FieldNames.TUNNISTE), convert.getTunniste());
        assertEquals(testKey(type, FieldNames.NQF), convert.getNqf().getUri());
        assertEquals(testKey(type, FieldNames.EQF), convert.getEqf().getUri());
    }

    private void expectKorkeakoulutusNotTestedCalls() {
        expect(komoKuvausConvertersMock.convertMonikielinenTekstiToTekstiDTO(EasyMock.<Map<KomoTeksti, MonikielinenTeksti>>anyObject(), anyBoolean())).andReturn(new KuvausV1RDTO());
        expect(komotoKuvausConvertersMock.convertMonikielinenTekstiToTekstiDTO(EasyMock.<Map<KomotoTeksti, MonikielinenTeksti>>anyObject(), anyBoolean())).andReturn(new KuvausV1RDTO());

        expect(commonConverterMock.convertToKoodiDTO(null, FI, FieldNames.ALKAMISKAUSI, true, false)).andReturn(toKoodiUri(NOT_TESTED));
        expect(commonConverterMock.koulutusohjelmaUiMetaDTO(null, FI, FieldNames.KOULUTUSOHJELMA, false)).andReturn(new NimiV1RDTO());
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.AIHEES, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.AMMATTINIMIKKEET, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.POHJALKOULUTUSVAATIMUS, false)).andReturn(toKoodiUris(Type.NOT_TESTED));

        expect(commonConverterMock.searchOrganisaationNimi(null, FI)).andReturn(new OrganisaatioV1RDTO(NOT_TESTED, NOT_TESTED, null));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.OPETUSKIELIS, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.OPETUSMUODOS, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.OPETUSPAIKKAS, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.OPETUSAIKAS, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiDTO(null, FI, FieldNames.SUUNNITELTUKESTON_TYYPPI, false)).andReturn(toKoodiUri(NOT_TESTED));
    }

    private void expectLukioNotTestedCalls() {
        expect(komoKuvausConvertersMock.convertMonikielinenTekstiToTekstiDTO(EasyMock.<Map<KomoTeksti, MonikielinenTeksti>>anyObject(), anyBoolean())).andReturn(new KuvausV1RDTO());
        expect(komotoKuvausConvertersMock.convertMonikielinenTekstiToTekstiDTO(EasyMock.<Map<KomotoTeksti, MonikielinenTeksti>>anyObject(), anyBoolean())).andReturn(new KuvausV1RDTO());
        expect(commonConverterMock.convertToKoodiDTO(null, FI, FieldNames.ALKAMISKAUSI, true, false)).andReturn(toKoodiUri(NOT_TESTED));

        expect(commonConverterMock.convertToKielivalikoimaDTO(new HashMap<String, Kielivalikoima>(), FI, false)).andReturn(new KoodiValikoimaV1RDTO());
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.LUKIODIPLOMI, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiDTO(null, FI, FieldNames.POHJALKOULUTUSVAATIMUS, false)).andReturn(toKoodiUri(NOT_TESTED));
        expect(commonConverterMock.convertToKoodiDTO(null, FI, FieldNames.KOULUTUSLAJI, false)).andReturn(toKoodiUri(NOT_TESTED));

        expect(commonConverterMock.searchOrganisaationNimi(null, FI)).andReturn(new OrganisaatioV1RDTO(NOT_TESTED, NOT_TESTED, null));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.OPETUSKIELIS, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.OPETUSMUODOS, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.OPETUSPAIKKAS, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.OPETUSAIKAS, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiDTO(null, FI, FieldNames.SUUNNITELTUKESTON_TYYPPI, false)).andReturn(toKoodiUri(NOT_TESTED));
    }

    /**
     * Test of convert method, of class EntityConverterToRDTO.
     */
    private static String testKey(final Type prefix, final FieldNames field) {
        Preconditions.checkNotNull(prefix, "prefix value cannot be null.");
        Preconditions.checkNotNull(field, "field enum cannot be null.");
        return new StringBuilder(prefix.name()).append("_").append(field).toString();
    }

    private static void korkeakouluPopulateBaseValues(Type type, BaseKoulutusmoduuli m) {
        Preconditions.checkNotNull(type, "Type value cannot be null.");
        Preconditions.checkNotNull(m, "BaseKoulutusmoduuli object cannot be null.");

        m.setOpintoalaUri(testKey(type, FieldNames.OPINTOALA));
        m.setKoulutusalaUri(testKey(type, FieldNames.KOULUTUSALA));
        m.setKoulutusasteUri(testKey(type, FieldNames.KOULUTUSASTE));
        m.setKoulutusUri(testKey(type, FieldNames.KOULUTUSKOODI));
        m.setKoulutusohjelmaUri(testKey(type, FieldNames.KOULUTUSOHJELMA));
        m.setOpintojenLaajuusarvoUri(testKey(type, FieldNames.OPINTOJEN_LAAJUUSARVO));
        m.setOpintojenLaajuusyksikkoUri(testKey(type, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO));

        m.setTutkintoUri(testKey(type, FieldNames.TUTKINTO));
        m.setUlkoinenTunniste(testKey(type, FieldNames.TUNNISTE));
        m.setNqfUri(testKey(type, FieldNames.NQF));
        m.setEqfUri(testKey(type, FieldNames.EQF));
        m.setKoulutustyyppiUri(testKey(type, FieldNames.KOULUTUSTYYPPI));
    }

    private static void lukioPopulateBaseValues(Type type, BaseKoulutusmoduuli m) {
        Preconditions.checkNotNull(type, "Type value cannot be null.");
        Preconditions.checkNotNull(m, "BaseKoulutusmoduuli object cannot be null.");

        m.setOpintoalaUri(testKey(type, FieldNames.OPINTOALA));
        m.setKoulutusalaUri(testKey(type, FieldNames.KOULUTUSALA));
        m.setKoulutusasteUri(testKey(type, FieldNames.KOULUTUSASTE));
        m.setKoulutusUri(testKey(type, FieldNames.KOULUTUSKOODI));
        m.setOpintojenLaajuusarvoUri(testKey(type, FieldNames.OPINTOJEN_LAAJUUSARVO));
        m.setOpintojenLaajuusyksikkoUri(testKey(type, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO));
        m.setUlkoinenTunniste(testKey(type, FieldNames.TUNNISTE));
        m.setNqfUri(testKey(type, FieldNames.NQF));
        m.setEqfUri(testKey(type, FieldNames.EQF));
        m.setKoulutustyyppiUri(testKey(type, FieldNames.KOULUTUSTYYPPI));
    }

    private static void lukioPopulateChildKomoBaseValues(Koulutusmoduuli m) {
        Preconditions.checkNotNull(m, "Koulutusmoduuli object cannot be null.");
        m.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        m.setOid(Type.KOMO_CHILD.name());
        m.setKoulutusUri(testKey(Type.KOMO_CHILD, FieldNames.KOULUTUSKOODI));
        m.setNqfUri(testKey(Type.KOMO_CHILD, FieldNames.NQF));
        m.setEqfUri(testKey(Type.KOMO_CHILD, FieldNames.EQF));
        m.setLukiolinjaUri(testKey(Type.KOMO_CHILD, FieldNames.LUKIOLINJA));
        m.setTutkintonimikeUri(testKey(Type.KOMO_CHILD, FieldNames.TUTKINTONIMIKE));
        m.setKoulutustyyppiEnum(KoulutustyyppiEnum.LUKIOKOULUTUS);
    }

    private static KoodiV1RDTO toKoodiUri(final String type) {
        return new KoodiV1RDTO(type, 1, null);
    }

    private static KoodiV1RDTO toKoodiUri(final Type type, final FieldNames field) {
        return new KoodiV1RDTO(testKey(type, field), 1, null);
    }

    private static NimiV1RDTO toKoodiUriNimi(final Type type, final FieldNames field) {
        NimiV1RDTO dto = new NimiV1RDTO();
        dto.setUri(testKey(type, field));
        return dto;
    }

    private static KoodiUrisV1RDTO toKoodiUris(final Type type, final FieldNames field) {
        KoodiUrisV1RDTO uris = new KoodiUrisV1RDTO();
        uris.setUris(Maps.<String, Integer>newHashMap());
        uris.getUris().put(testKey(type, field), 1);
        return uris;
    }

    private static KoodiUrisV1RDTO toKoodiUris(final Type type) {
        KoodiUrisV1RDTO uris = new KoodiUrisV1RDTO();
        uris.setUris(Maps.<String, Integer>newHashMap());
        uris.getUris().put(type.name(), 1);
        return uris;
    }
}
