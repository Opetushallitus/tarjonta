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

import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.model.RestParam;
import fi.vm.sade.tarjonta.service.impl.resources.v1.KoulutusImplicitDataPopulator;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.YhdenPaikanSaantoBuilder;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;


public class EntityConverterToRDTOTest extends KoulutusRestBase {

    private static final Set<KoodistoUri> SET_KOMOTO_TUTKINTONIMIKE = Sets.newHashSet(new KoodistoUri(testKey(Type.KOMOTO, FieldNames.TUTKINTONIMIKE)));
    private static final Set<KoodistoUri> SET_KOMO_TUTKINTONIMIKE = Sets.newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE)));

    private static final RestParam PARAM = RestParam.byUserRequest(Boolean.FALSE, Boolean.FALSE, "FI");
    private static final Type NO_URI = null;
    private static final String NO_OVERRIDE_URI = null;
    private static final String NULL_KOMOTO = null;
    private static final KoulutusCommonConverter.Nullable YES = KoulutusCommonConverter.Nullable.YES;
    private static final KoulutusCommonConverter.Nullable NO = KoulutusCommonConverter.Nullable.NO;

    private EntityConverterToRDTO<KoulutusKorkeakouluV1RDTO> instanceKk;

    private EntityConverterToRDTO<KoulutusLukioV1RDTO> instanceLukio;

    private KoulutusCommonConverter commonConverterMock;

    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConvertersMock;

    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKuvausConvertersMock;

    private KoulutusmoduuliDAO koulutusmoduuliDAOMock;

    private KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAOMock;

    private YhdenPaikanSaantoBuilder yhdenPaikanSaantoBuilderMock;

    @Before
    public void setUp() {
        commonConverterMock = createMock(KoulutusCommonConverter.class);
        komotoKuvausConvertersMock = createMock(KoulutusKuvausV1RDTO.class);
        komoKuvausConvertersMock = createMock(KoulutusKuvausV1RDTO.class);
        koulutusSisaltyvyysDAOMock = createMock(KoulutusSisaltyvyysDAO.class);
        yhdenPaikanSaantoBuilderMock = createMock(YhdenPaikanSaantoBuilder.class);

        instanceKk = new EntityConverterToRDTO<KoulutusKorkeakouluV1RDTO>();
        instanceLukio = new EntityConverterToRDTO<KoulutusLukioV1RDTO>();

        Whitebox.setInternalState(instanceKk, "commonConverter", commonConverterMock);
        Whitebox.setInternalState(instanceKk, "komoKuvausConverters", komoKuvausConvertersMock);
        Whitebox.setInternalState(instanceKk, "komotoKuvausConverters", komotoKuvausConvertersMock);
        Whitebox.setInternalState(instanceKk, "dataPopulator", new KoulutusImplicitDataPopulator());
        Whitebox.setInternalState(instanceKk, "yhdenPaikanSaantoBuilder", yhdenPaikanSaantoBuilderMock);

        koulutusmoduuliDAOMock = createMock(KoulutusmoduuliDAO.class);
        Whitebox.setInternalState(instanceLukio, "commonConverter", commonConverterMock);
        Whitebox.setInternalState(instanceLukio, "komoKuvausConverters", komoKuvausConvertersMock);
        Whitebox.setInternalState(instanceLukio, "komotoKuvausConverters", komotoKuvausConvertersMock);
        Whitebox.setInternalState(instanceLukio, "koulutusmoduuliDAO", koulutusmoduuliDAOMock);
        Whitebox.setInternalState(instanceLukio, "koulutusSisaltyvyysDAO", koulutusSisaltyvyysDAOMock);
        Whitebox.setInternalState(instanceLukio, "dataPopulator", new KoulutusImplicitDataPopulator());

        KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAOMock = Mockito.mock(KoulutusSisaltyvyysDAO.class);
        Mockito.when(koulutusSisaltyvyysDAOMock.getParents(Matchers.anyString())).thenReturn(new ArrayList<String>());
        Whitebox.setInternalState(instanceLukio, "koulutusSisaltyvyysDAO", koulutusSisaltyvyysDAOMock);
        Whitebox.setInternalState(instanceKk, "koulutusSisaltyvyysDAO", koulutusSisaltyvyysDAOMock);
    }

    @Test
    public void testKorkeakoulutusConvertKomoOverrideByKomotoKoodis() {
        Koulutusmoduuli m = new Koulutusmoduuli();
        m.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        korkeakouluPopulateBaseValues(Type.KOMO, m);
        m.setKoulutustyyppiEnum(ModuulityyppiEnum.KORKEAKOULUTUS);
        m.setTutkintonimikes(Sets.newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE))));

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();
        korkeakouluPopulateBaseValues(Type.KOMOTO, t);
        t.setTutkintonimikes(Sets.newHashSet(new KoodistoUri(testKey(Type.KOMOTO, FieldNames.TUTKINTONIMIKE))));
        t.setKoulutusmoduuli(m);
        t.setToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS);

        final Type returnKomoto = Type.KOMOTO;

        expectKorkeakoulutusNotTestedCalls();
        expect(commonConverterMock.convertToKoodiUrisDTO(SET_KOMOTO_TUTKINTONIMIKE, FieldNames.TUTKINTONIMIKE, PARAM)).andReturn(toKoodiUris(Type.KOMO, FieldNames.TUTKINTONIMIKE));

        expect_komo_x(returnKomoto, returnKomoto, FieldNames.KOULUTUS, NO);
        expect_komo_x(returnKomoto, returnKomoto, FieldNames.TUTKINTO, YES);
        expect_komo_x(returnKomoto, returnKomoto, FieldNames.OPINTOJEN_LAAJUUSARVO, YES);
        expect_komo_x(returnKomoto, returnKomoto, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, YES);
        expect_komo_x(returnKomoto, returnKomoto, FieldNames.KOULUTUSASTE, YES);
        expect_komo_x(returnKomoto, returnKomoto, FieldNames.KOULUTUSALA, NO);
        expect_komo_x(returnKomoto, returnKomoto, FieldNames.OPINTOALA, NO);
        expect_komo_x(returnKomoto, returnKomoto, FieldNames.EQF, YES);
        expect_komo_x(returnKomoto, returnKomoto, FieldNames.NQF, YES);
        expect_koulutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS, returnKomoto, FieldNames.KOULUTUSTYYPPI, NO);
        //  expectNull(returnKomoto, FieldNames.SUUNNITELTUKESTON_TYYPPI, YES);  //correct as the tyyppi is only in komoto

        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);

        final KoulutusKorkeakouluV1RDTO convert = instanceKk.convert(KoulutusKorkeakouluV1RDTO.class, t, PARAM);
        EasyMock.verify(commonConverterMock);
        assertKk(convert, returnKomoto);
    }

    @Test
    public void testKorkeakoulutusConvertEmptyKomoOverrideByKomotoKoodis() {
        Koulutusmoduuli m = new Koulutusmoduuli();
        m.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        m.setKoulutustyyppiEnum(ModuulityyppiEnum.KORKEAKOULUTUS);

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();
        korkeakouluPopulateBaseValues(Type.KOMOTO, t);
        t.setTutkintonimikes(Sets.newHashSet(new KoodistoUri(testKey(Type.KOMOTO, FieldNames.TUTKINTONIMIKE))));
        t.setKoulutusmoduuli(m);
        t.setToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS);
        t.setKoulutuksenlaajuusUri("koulutustyyppifasetti_et010205#1");
        final Type returnKomoto = Type.KOMOTO;

        expectKorkeakoulutusNotTestedCalls();
        expect(commonConverterMock.convertToKoodiUrisDTO(SET_KOMOTO_TUTKINTONIMIKE, FieldNames.TUTKINTONIMIKE, PARAM)).andReturn(toKoodiUris(Type.KOMO, FieldNames.TUTKINTONIMIKE));

        expect_null_komo_x(returnKomoto, returnKomoto, FieldNames.KOULUTUS, NO);
        expect_null_komo_x(returnKomoto, returnKomoto, FieldNames.TUTKINTO, YES);
        expect_null_komo_x(returnKomoto, returnKomoto, FieldNames.OPINTOJEN_LAAJUUSARVO, YES);
        expect_null_komo_x(returnKomoto, returnKomoto, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, YES);
        expect_null_komo_x(returnKomoto, returnKomoto, FieldNames.KOULUTUSASTE, YES);
        expect_null_komo_x(returnKomoto, returnKomoto, FieldNames.KOULUTUSALA, NO);
        expect_null_komo_x(returnKomoto, returnKomoto, FieldNames.OPINTOALA, NO);
        expect_null_komo_x(returnKomoto, returnKomoto, FieldNames.EQF, YES);
        expect_null_komo_x(returnKomoto, returnKomoto, FieldNames.NQF, YES);
        expect_koulutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS, returnKomoto, FieldNames.KOULUTUSTYYPPI, NO);
        //expectNull(returnKomoto, FieldNames.SUUNNITELTUKESTON_TYYPPI, YES);  //correct as the tyyppi is only in komoto
        expect(commonConverterMock.convertToKoodiDTO("koulutustyyppifasetti_et010205#1", "koulutustyyppifasetti_et010205#1", FieldNames.KOULUTUKSENLAAJUUS, NO, PARAM)).andReturn(toKoodiUri("koulutustyyppifasetti_et010205"));

        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);

        final KoulutusKorkeakouluV1RDTO convert = instanceKk.convert(KoulutusKorkeakouluV1RDTO.class, t, PARAM);
        EasyMock.verify(commonConverterMock);
        assertKk(convert, returnKomoto);
    }

    @Test
    public void testKorkeakoulutusConvertKomoNoKomotoKoodiOverride() {
        Koulutusmoduuli m = new Koulutusmoduuli();
        m.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        korkeakouluPopulateBaseValues(Type.KOMO, m);
        m.setKoulutustyyppiEnum(ModuulityyppiEnum.KORKEAKOULUTUS);
        m.setTutkintonimikes(Sets.newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE))));

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();
        t.setKoulutusmoduuli(m);
        t.setToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS);

        final Type returnKomo = Type.KOMO;

        expectKorkeakoulutusNotTestedCalls();
        expect(commonConverterMock.convertToKoodiUrisDTO(SET_KOMO_TUTKINTONIMIKE, FieldNames.TUTKINTONIMIKE, PARAM)).andReturn(toKoodiUris(Type.KOMO, FieldNames.TUTKINTONIMIKE));

        expect_komo_x(returnKomo, NO_URI, FieldNames.KOULUTUS, NO);
        expect_komo_x(returnKomo, NO_URI, FieldNames.TUTKINTO, YES);
        expect_komo_x(returnKomo, NO_URI, FieldNames.OPINTOJEN_LAAJUUSARVO, YES);
        expect_komo_x(returnKomo, NO_URI, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, YES);
        expect_komo_x(returnKomo, NO_URI, FieldNames.KOULUTUSASTE, YES);
        expect_komo_x(returnKomo, NO_URI, FieldNames.KOULUTUSALA, NO);
        expect_komo_x(returnKomo, NO_URI, FieldNames.OPINTOALA, NO);
        expect_komo_x(returnKomo, NO_URI, FieldNames.EQF, YES);
        expect_komo_x(returnKomo, NO_URI, FieldNames.NQF, YES);
        expect_koulutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS, returnKomo, FieldNames.KOULUTUSTYYPPI, NO);

        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);

        final KoulutusKorkeakouluV1RDTO convert = instanceKk.convert(KoulutusKorkeakouluV1RDTO.class, t, PARAM);
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
        tukintoParentKomo.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);
        tukintoParentKomo.setTutkintonimikes(Sets.newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE))));

        Koulutusmoduuli ohjelmaChildKomo = new Koulutusmoduuli();
        lukioPopulateChildKomoBaseValues(ohjelmaChildKomo);

        tukintoParentKomo.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);
        tukintoParentKomo.setTutkintonimikes(Sets.newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE))));

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();
        lukioPopulateBaseValues(Type.KOMOTO, t);
        t.setLukiolinjaUri(testKey(Type.KOMOTO, FieldNames.LUKIOLINJA));
        t.setTutkintonimikes(Sets.newHashSet(new KoodistoUri(testKey(Type.KOMOTO, FieldNames.TUTKINTONIMIKE))));
        t.setKoulutusmoduuli(ohjelmaChildKomo); //set the child 'koulutusohjelma' komo object here 
        t.setToteutustyyppi(ToteutustyyppiEnum.LUKIOKOULUTUS);

        final Type returnKomoto = Type.KOMOTO;

        expectLukioNotTestedCalls();

        expect(commonConverterMock.convertToNimiDTO(testKey(Type.KOMO_CHILD, FieldNames.LUKIOLINJA), testKey(Type.KOMOTO, FieldNames.LUKIOLINJA), FieldNames.LUKIOLINJA, NO, PARAM)).andReturn(toKoodiUriNimi(Type.KOMOTO, FieldNames.LUKIOLINJA));
        expect_x_x_return(Type.KOMO_CHILD, returnKomoto, returnKomoto, FieldNames.TUTKINTONIMIKE, NO);
        expect(koulutusmoduuliDAOMock.findParentKomo(ohjelmaChildKomo)).andReturn(tukintoParentKomo);
        expect(koulutusSisaltyvyysDAOMock.getParents(ohjelmaChildKomo.getOid())).andReturn(new ArrayList<String>());
        expect(koulutusSisaltyvyysDAOMock.getChildren(ohjelmaChildKomo.getOid())).andReturn(new ArrayList<String>());

        //correct only one uri in komoto, data is a list of uris in komo
        expect_koulutustyyppi(ToteutustyyppiEnum.LUKIOKOULUTUS, returnKomoto, FieldNames.KOULUTUSTYYPPI, NO);
        expect_komo_x(returnKomoto, returnKomoto, FieldNames.KOULUTUS, NO);
        expectNull(Type.RETURN_NULL, FieldNames.TUTKINTO, YES);

        expect_komo_x(returnKomoto, returnKomoto, FieldNames.OPINTOJEN_LAAJUUSARVO, YES);
        expect_komo_x(returnKomoto, returnKomoto, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, YES);
        expect_komo_x(returnKomoto, returnKomoto, FieldNames.KOULUTUSASTE, YES);
        expect_komo_x(returnKomoto, returnKomoto, FieldNames.KOULUTUSALA, NO);
        expect_komo_x(returnKomoto, returnKomoto, FieldNames.OPINTOALA, NO);
        expect_komoChild_x(returnKomoto, returnKomoto, FieldNames.EQF, YES);
        expect_komoChild_x(returnKomoto, returnKomoto, FieldNames.NQF, YES);
        // expectNull(Type.RETURN_NULL, FieldNames.SUUNNITELTUKESTON_TYYPPI, YES);
        EasyMock.replay(koulutusmoduuliDAOMock);
        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);
        EasyMock.replay(koulutusSisaltyvyysDAOMock);

        final KoulutusLukioV1RDTO convert = instanceLukio.convert(KoulutusLukioV1RDTO.class, t, PARAM);
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
        tukintoParentKomo.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);
        tukintoParentKomo.setTutkintonimikes(Sets.newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE))));

        Koulutusmoduuli ohjelmaChildKomo = new Koulutusmoduuli();
        lukioPopulateChildKomoBaseValues(ohjelmaChildKomo);

        tukintoParentKomo.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);
        tukintoParentKomo.setTutkintonimikes(Sets.newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE))));

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();
        t.setKoulutusmoduuli(ohjelmaChildKomo);
        t.setToteutustyyppi(ToteutustyyppiEnum.LUKIOKOULUTUS);

        final Type returnKomoto = Type.KOMO;

        expectLukioNotTestedCalls();

        expect(commonConverterMock.convertToNimiDTO(testKey(Type.KOMO_CHILD, FieldNames.LUKIOLINJA), NULL_KOMOTO, FieldNames.LUKIOLINJA, NO, PARAM)).andReturn(toKoodiUriNimi(Type.KOMO, FieldNames.LUKIOLINJA));
        expect_x_x_return(Type.KOMO_CHILD, NO_URI, returnKomoto, FieldNames.TUTKINTONIMIKE, NO);
        expect(koulutusmoduuliDAOMock.findParentKomo(ohjelmaChildKomo)).andReturn(tukintoParentKomo);

        expect(koulutusSisaltyvyysDAOMock.getParents(ohjelmaChildKomo.getOid())).andReturn(new ArrayList<String>());
        expect(koulutusSisaltyvyysDAOMock.getChildren(ohjelmaChildKomo.getOid())).andReturn(new ArrayList<String>());

        //correct only one uri in komoto, data is a list of uris in komo
        expect_koulutustyyppi(ToteutustyyppiEnum.LUKIOKOULUTUS, returnKomoto, FieldNames.KOULUTUSTYYPPI, NO);
        expect_komo_x(returnKomoto, NO_URI, FieldNames.KOULUTUS, NO);
        expectNull(Type.RETURN_NULL, FieldNames.TUTKINTO, YES);

        expect_komo_x(returnKomoto, NO_URI, FieldNames.OPINTOJEN_LAAJUUSARVO, YES);
        expect_komo_x(returnKomoto, NO_URI, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, YES);
        expect_komo_x(returnKomoto, NO_URI, FieldNames.KOULUTUSASTE, YES);
        expect_komo_x(returnKomoto, NO_URI, FieldNames.KOULUTUSALA, NO);
        expect_komo_x(returnKomoto, NO_URI, FieldNames.OPINTOALA, NO);
        expect_komoChild_x(returnKomoto, NO_URI, FieldNames.EQF, YES);
        expect_komoChild_x(returnKomoto, NO_URI, FieldNames.NQF, YES);
        // expectNull(Type.RETURN_NULL, FieldNames.SUUNNITELTUKESTON_TYYPPI, YES);
        EasyMock.replay(koulutusmoduuliDAOMock);
        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);
        EasyMock.replay(koulutusSisaltyvyysDAOMock);

        final KoulutusLukioV1RDTO convert = instanceLukio.convert(KoulutusLukioV1RDTO.class, t, PARAM);
        EasyMock.verify(commonConverterMock);
        assertLukio(convert, returnKomoto);
    }

    private void expect_x_x_return(Type param1, Type param2, Type returnType, FieldNames field, KoulutusCommonConverter.Nullable n) {
        expect(commonConverterMock.convertToKoodiDTO(param1 != null ? testKey(param1, field) : null, param2 != null ? testKey(param2, field) : null, field, n, PARAM)).andReturn(toKoodiUri(returnType, field));
    }

    private void expect_komoChild_x(Type returnType, Type x, FieldNames field, KoulutusCommonConverter.Nullable n) {
        expect(commonConverterMock.convertToKoodiDTO(testKey(Type.KOMO_CHILD, field),
                x != null ? testKey(x, field) : null,
                field, n,
                PARAM)).andReturn(toKoodiUri(returnType, field));
    }

    private void expect_komo_x(Type returnType, Type x, FieldNames field, KoulutusCommonConverter.Nullable n) {
        expect(commonConverterMock.convertToKoodiDTO(testKey(Type.KOMO, field),
                x != null ? testKey(x, field) : null,
                field, n,
                PARAM)).andReturn(toKoodiUri(returnType, field));
    }

    private void expect_null_komo_x(Type returnType, Type x, FieldNames field, KoulutusCommonConverter.Nullable n) {
        expect(commonConverterMock.convertToKoodiDTO(null,
                x != null ? testKey(x, field) : null,
                field, n,
                PARAM)).andReturn(toKoodiUri(returnType, field));
    }

    private void expectNull(Type returnType, FieldNames field, KoulutusCommonConverter.Nullable n) {
        expect(commonConverterMock.convertToKoodiDTO(null, null, field, n, PARAM)).andReturn(toKoodiUri(returnType, field));
    }

    private void expect_koulutustyyppi(ToteutustyyppiEnum koulutustyyppiUri, Type returnType, FieldNames field, KoulutusCommonConverter.Nullable n) {
        switch (returnType) {
            case KOMOTO:
                expect(commonConverterMock.convertToKoodiDTO(koulutustyyppiUri.uri(), testKey(Type.KOMOTO, field), field, n, PARAM)).andReturn(toKoodiUri(returnType, field));
                break;
            case KOMO:
                expect(commonConverterMock.convertToKoodiDTO(koulutustyyppiUri.uri(), null, field, n, PARAM)).andReturn(toKoodiUri(returnType, field));
                break;
        }
    }

    private void assertKk(final KoulutusKorkeakouluV1RDTO convert, final Type type) {
        assertEquals(testKey(type, FieldNames.OPINTOALA), convert.getOpintoala().getUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSALA), convert.getKoulutusala().getUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSASTE), convert.getKoulutusaste().getUri());
        assertEquals(false, convert.getTutkintonimikes().getUris().isEmpty());
        assertEquals(testKey(type, FieldNames.KOULUTUS), convert.getKoulutuskoodi().getUri());
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
        assertEquals(testKey(type, FieldNames.KOULUTUS), convert.getKoulutuskoodi().getUri());
        assertEquals(testKey(type, FieldNames.LUKIOLINJA), convert.getKoulutusohjelma().getUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSARVO), convert.getOpintojenLaajuusarvo().getUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO), convert.getOpintojenLaajuusyksikko().getUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSTYYPPI), convert.getKoulutustyyppi().getUri());
        assertEquals(testKey(Type.RETURN_NULL, FieldNames.TUTKINTO), convert.getTutkinto().getUri());
        assertEquals(testKey(type, FieldNames.TUNNISTE), convert.getTunniste());
        assertEquals(testKey(type, FieldNames.NQF), convert.getNqf().getUri());
        assertEquals(testKey(type, FieldNames.EQF), convert.getEqf().getUri());
    }

    private void expectKorkeakoulutusNotTestedCalls() {
        expect(komoKuvausConvertersMock.convertMonikielinenTekstiToTekstiDTO(EasyMock.<Map<KomoTeksti, MonikielinenTeksti>>anyObject(), anyBoolean())).andReturn(new KuvausV1RDTO());
        expect(komotoKuvausConvertersMock.convertMonikielinenTekstiToTekstiDTO(EasyMock.<Map<KomotoTeksti, MonikielinenTeksti>>anyObject(), anyBoolean())).andReturn(new KuvausV1RDTO());

        expect(commonConverterMock.convertToKoodiDTO(null, null, FieldNames.ALKAMISKAUSI, YES, PARAM)).andReturn(toKoodiUri(NOT_TESTED));
        expect(commonConverterMock.koulutusohjelmaUiMetaDTO(null, FieldNames.KOULUTUSOHJELMA, PARAM)).andReturn(new NimiV1RDTO());
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FieldNames.AIHEES, PARAM)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FieldNames.AMMATTINIMIKKEET, PARAM)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FieldNames.POHJALKOULUTUSVAATIMUS, PARAM)).andReturn(toKoodiUris(Type.NOT_TESTED));

        //expect(commonConverterMock.searchOrganisaationNimi(null, FI)).andReturn(new OrganisaatioV1RDTO(NOT_TESTED, NOT_TESTED, null));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FieldNames.OPETUSKIELIS, PARAM)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FieldNames.OPETUSMUODOS, PARAM)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FieldNames.OPETUSPAIKKAS, PARAM)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FieldNames.OPETUSAIKAS, PARAM)).andReturn(toKoodiUris(Type.NOT_TESTED));

    }

    private void expectLukioNotTestedCalls() {
        expect(komoKuvausConvertersMock.convertMonikielinenTekstiToTekstiDTO(EasyMock.<Map<KomoTeksti, MonikielinenTeksti>>anyObject(), anyBoolean())).andReturn(new KuvausV1RDTO());
        expect(commonConverterMock.convertToKoodiDTO(null, null, FieldNames.ALKAMISKAUSI, YES, PARAM)).andReturn(toKoodiUri(NOT_TESTED));
        expect(komotoKuvausConvertersMock.convertMonikielinenTekstiToTekstiDTO(EasyMock.<Map<KomotoTeksti, MonikielinenTeksti>>anyObject(), anyBoolean())).andReturn(new KuvausV1RDTO());
        expect(commonConverterMock.convertToKielivalikoimaDTO(new HashMap<String, Kielivalikoima>(), PARAM)).andReturn(new KoodiValikoimaV1RDTO());
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FieldNames.LUKIODIPLOMI, PARAM)).andReturn(toKoodiUris(Type.NOT_TESTED));

        expect(commonConverterMock.convertToKoodiDTO(null, NO_OVERRIDE_URI, FieldNames.POHJALKOULUTUSVAATIMUS, NO, PARAM)).andReturn(toKoodiUri(NOT_TESTED));
        expect(commonConverterMock.convertToKoodiDTO(null, NO_OVERRIDE_URI, FieldNames.KOULUTUSLAJI, NO, PARAM)).andReturn(toKoodiUri(NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FieldNames.AIHEES, PARAM)).andReturn(toKoodiUris(Type.NOT_TESTED));

        // expect(commonConverterMock.searchOrganisaationNimi(null, FI)).andReturn(new OrganisaatioV1RDTO(NOT_TESTED, NOT_TESTED, null));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FieldNames.OPETUSKIELIS, PARAM)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FieldNames.OPETUSMUODOS, PARAM)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FieldNames.OPETUSPAIKKAS, PARAM)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FieldNames.OPETUSAIKAS, PARAM)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FieldNames.AMMATTINIMIKKEET, PARAM)).andReturn(toKoodiUris(Type.NOT_TESTED));
    }

}
