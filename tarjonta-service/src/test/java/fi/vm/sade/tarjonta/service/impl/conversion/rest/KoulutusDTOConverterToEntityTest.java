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

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.KoodistoItemType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static fi.vm.sade.tarjonta.service.impl.resources.v1.KoulutusImplicitDataPopulator.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;


public class KoulutusDTOConverterToEntityTest extends KoulutusRestBase {

    private static final String KOMO_OID = "komo_oid";

    private KoulutusCommonConverter commonConverterMock;

    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConvertersMock;

    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKuvausConvertersMock;

    private KoulutusmoduuliDAO koulutusmoduuliDAOMock;

    private KoulutusDTOConverterToEntity instance;

    private OidService oidServiceMock;

    private static KoodiType mockCode(final String codeValue, final String codeKoodistoUri) {
        return new KoodiType(){{
            setKoodiUri(codeValue + "_uri");
            setKoodiArvo("x" + codeValue);
            setVersio(1);
            setKoodisto(new KoodistoItemType(){{
                setKoodistoUri(codeKoodistoUri);
            }});
        }};
    }

    public static KoodiService mockKoodiService(KoodiService koodiServiceMock) {
        if (koodiServiceMock == null) {
            koodiServiceMock = Mockito.mock(KoodiService.class);
        }

        List<KoodiType> mockedList = new ArrayList<KoodiType>();
        mockedList.add(mockCode("koulutusala", KOULUTUSALAOPH2002));
        mockedList.add(mockCode("koulutusaste", KOULUTUSASTEOPH2002));
        mockedList.add(mockCode("opintoala", OPINTOALAOPH2002));
        mockedList.add(mockCode("eqf", EQF));
        mockedList.add(mockCode("tutkinto", TUTKINTO));
        mockedList.add(mockCode("tutkintonimike", TUTKINTONIMIKEKK));
        Mockito.when(koodiServiceMock.listKoodiByRelation(
                Matchers.any(KoodiUriAndVersioType.class), Matchers.anyBoolean(), Matchers.any(SuhteenTyyppiType.class)
        )).thenReturn(mockedList);

        return koodiServiceMock;
    }

    @Before
    public void setUp() {
        instance = new KoulutusDTOConverterToEntity();

        commonConverterMock = createMock(KoulutusCommonConverter.class);
        komotoKuvausConvertersMock = createMock(KoulutusKuvausV1RDTO.class);
        komoKuvausConvertersMock = createMock(KoulutusKuvausV1RDTO.class);
        koulutusmoduuliDAOMock = createMock(KoulutusmoduuliDAO.class);
        oidServiceMock = createMock(OidService.class);

        Whitebox.setInternalState(instance, "commonConverter", commonConverterMock);
        Whitebox.setInternalState(instance, "komoKuvausConverters", komoKuvausConvertersMock);
        Whitebox.setInternalState(instance, "komotoKuvausConverters", komotoKuvausConvertersMock);
        Whitebox.setInternalState(instance, "koulutusmoduuliDAO", koulutusmoduuliDAOMock);
        Whitebox.setInternalState(instance, "oidService", oidServiceMock);
        Whitebox.setInternalState(instance, "koodiService", mockKoodiService(null));
    }

    @Test
    public void testKorkeakouluCopyCommonUrisToKomoAndKomoto() throws OIDCreationException {
        KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();
        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO);
        dto.setOrganisaatio(new OrganisaatioV1RDTO("org_oid", "org_name", null));
        dto.setKomoOid(KOMO_OID);
        dto.setOpintoala(toKoodiUri(Type.BOTH, FieldNames.OPINTOALA));
        dto.setKoulutusala(toKoodiUri(Type.BOTH, FieldNames.KOULUTUSALA));
        dto.setKoulutusaste(toKoodiUri(Type.BOTH, FieldNames.KOULUTUSASTE));
        dto.setTutkintonimikes(toKoodiUris(Type.BOTH, FieldNames.TUTKINTONIMIKE));
        dto.setKoulutuskoodi(toKoodiUri(Type.BOTH, FieldNames.KOULUTUS));
        dto.setKoulutusohjelma(toKoodiUriNimi(Type.BOTH, FieldNames.KOULUTUSOHJELMA));
        dto.setOpintojenLaajuusarvo(toKoodiUri(Type.BOTH, FieldNames.OPINTOJEN_LAAJUUSARVO));
        dto.setOpintojenLaajuusyksikko(toKoodiUri(Type.BOTH, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO));
        dto.setKoulutustyyppi(toKoodiUri(Type.BOTH, FieldNames.KOULUTUSTYYPPI));
        dto.setTutkinto(toKoodiUri(Type.BOTH, FieldNames.TUTKINTO));
        dto.setNqf(toKoodiUri(Type.BOTH, FieldNames.NQF));
        dto.setEqf(toKoodiUri(Type.BOTH, FieldNames.EQF));
        dto.setTunniste(testKey(Type.BOTH, FieldNames.TUNNISTE));
        dto.setOpintojenMaksullisuus(false);

        //expect(koulutusmoduuliDAOMock.findByOid(KOMO_OID)).andReturn(m);
        expect(oidServiceMock.get(TarjontaOidType.KOMO)).andReturn("komo_oid");
        expect(oidServiceMock.get(TarjontaOidType.KOMOTO)).andReturn("komoto_oid");

        commonConverterMock.handleDates(isA(KoulutusmoduuliToteutus.class), isA(KoulutusV1RDTO.class));
        komoKuvausConvertersMock.convertTekstiDTOToMonikielinenTeksti(EasyMock.isA(KuvausV1RDTO.class), EasyMock.<Map<KomoTeksti, MonikielinenTeksti>>anyObject());
        komotoKuvausConvertersMock.convertTekstiDTOToMonikielinenTeksti(EasyMock.isA(KuvausV1RDTO.class), EasyMock.<Map<KomotoTeksti, MonikielinenTeksti>>anyObject());

        //EXPECT KOMO CALLS
        kkCommonExpect(Type.KOMO);

        //EXPECT KOMOTO CALLS
        kkCommonExpect(Type.KOMOTO);
        expect(commonConverterMock.convertToTexts(EasyMock.isA(NimiV1RDTO.class), EasyMock.eq(FieldNames.KOULUTUSOHJELMA))).andReturn(new MonikielinenTeksti());

        //EXPECT UNTESTED KOMOTO CALLS
        expectFieldNullableNotTested(FieldNames.SUUNNITELTUKESTO);
        expectFieldsNotTested(FieldNames.POHJALKOULUTUSVAATIMUS);
        expectFieldsNotTested(FieldNames.AIHEES);
        expectFieldsNotTested(FieldNames.OPETUSKIELIS);
        expectFieldsNotTested(FieldNames.OPETUSMUODOS);
        expectFieldsNotTested(FieldNames.OPETUSPAIKKAS);
        expectFieldsNotTested(FieldNames.OPETUSAIKAS);
        expectFieldsNotTested(FieldNames.AMMATTINIMIKKEET);

        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);
        EasyMock.replay(koulutusmoduuliDAOMock);
        EasyMock.replay(oidServiceMock);

        final KoulutusmoduuliToteutus result = instance.convert(dto, "user_oid");
        EasyMock.verify(commonConverterMock);

        assertKorkeakouluKomoto(result, Type.KOMOTO);
        assertKorkeakouluKomo(result.getKoulutusmoduuli(), Type.KOMO);
    }

    /**
     * Test of convert method, of class KoulutusDTOConverterToEntity.
     */
    @Test
    public void testLukioCopyCommonUrisToKomoto() throws OIDCreationException {
        Koulutusmoduuli m = new Koulutusmoduuli();
        m.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        m.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);

        KoulutusLukioV1RDTO dto = new KoulutusLukioV1RDTO();
        dto.setOrganisaatio(new OrganisaatioV1RDTO("org_oid", "org_name", null));
        dto.setKomoOid(KOMO_OID);
        dto.setOpintoala(toKoodiUri(Type.KOMOTO, FieldNames.OPINTOALA));
        dto.setKoulutusala(toKoodiUri(Type.KOMOTO, FieldNames.KOULUTUSALA));
        dto.setKoulutusaste(toKoodiUri(Type.KOMOTO, FieldNames.KOULUTUSASTE));
        dto.setTutkintonimike(toKoodiUri(Type.KOMOTO, FieldNames.TUTKINTONIMIKE));
        dto.setKoulutuskoodi(toKoodiUri(Type.KOMOTO, FieldNames.KOULUTUS));
        dto.setKoulutusohjelma(toKoodiUriNimi(Type.KOMOTO, FieldNames.LUKIOLINJA));
        dto.setOpintojenLaajuusarvo(toKoodiUri(Type.KOMOTO, FieldNames.OPINTOJEN_LAAJUUSARVO));
        dto.setOpintojenLaajuusyksikko(toKoodiUri(Type.KOMOTO, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO));
        dto.setKoulutustyyppi(toKoodiUri(Type.KOMOTO, FieldNames.KOULUTUSTYYPPI));
        dto.setTutkinto(toKoodiUri(Type.KOMOTO, FieldNames.TUTKINTO));
        dto.setNqf(toKoodiUri(Type.KOMOTO, FieldNames.NQF));
        dto.setEqf(toKoodiUri(Type.KOMOTO, FieldNames.EQF));
        dto.setTunniste(testKey(Type.KOMOTO, FieldNames.TUNNISTE));

        expect(koulutusmoduuliDAOMock.findByOid(KOMO_OID)).andReturn(m);
        expect(oidServiceMock.get(TarjontaOidType.KOMOTO)).andReturn("komoto_oid");
        commonConverterMock.handleDates(isA(KoulutusmoduuliToteutus.class), isA(KoulutusV1RDTO.class));
        komoKuvausConvertersMock.convertTekstiDTOToMonikielinenTeksti(EasyMock.isA(KuvausV1RDTO.class), EasyMock.<Map<KomoTeksti, MonikielinenTeksti>>anyObject());
        komotoKuvausConvertersMock.convertTekstiDTOToMonikielinenTeksti(EasyMock.isA(KuvausV1RDTO.class), EasyMock.<Map<KomotoTeksti, MonikielinenTeksti>>anyObject());

        expectFieldNullable(FieldNames.TUTKINTO);
        expectFieldNullable(FieldNames.OPINTOJEN_LAAJUUSYKSIKKO);
        expectFieldNullable(FieldNames.OPINTOJEN_LAAJUUSARVO);
        expectField(FieldNames.OPINTOALA);
        expectField(FieldNames.KOULUTUS);
        expectFieldNimi(FieldNames.LUKIOLINJA);
        expectField(FieldNames.KOULUTUSALA);
        expectFieldNullable(FieldNames.KOULUTUSASTE);
        expectField(FieldNames.TUTKINTONIMIKE);
        expectFieldNullable(FieldNames.KOULUTUSTYYPPI);
        expectFieldNullable(FieldNames.NQF);
        expectFieldNullable(FieldNames.EQF);

        expectFieldNullableNotTested(FieldNames.SUUNNITELTUKESTO);
        expectFieldNotTested(FieldNames.POHJALKOULUTUSVAATIMUS);
        expectFieldsNotTested(FieldNames.OPETUSKIELIS);
        expectFieldsNotTested(FieldNames.OPETUSMUODOS);
        expectFieldsNotTested(FieldNames.OPETUSPAIKKAS);
        expectFieldsNotTested(FieldNames.OPETUSAIKAS);
        expectFieldsNotTested(FieldNames.LUKIODIPLOMI);
        expectFieldsNotTested(FieldNames.AMMATTINIMIKKEET);
        expectFieldsNotTested(FieldNames.AIHEES);

        expect(koulutusmoduuliDAOMock.findParentKomo(m)).andReturn(null);

        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);
        EasyMock.replay(koulutusmoduuliDAOMock);
        EasyMock.replay(oidServiceMock);

        final KoulutusmoduuliToteutus result = instance.convert(dto, "user_oid");
        EasyMock.verify(commonConverterMock);

        assertLukioKomoto(result, Type.KOMOTO);
        assertLukioKomoChild(result.getKoulutusmoduuli());
    }
    private void expectFieldNullable(FieldNames field) {
        expect(commonConverterMock.convertToUri(EasyMock.isA(KoodiV1RDTO.class), EasyMock.eq(field), EasyMock.anyBoolean())).andReturn(testKey(Type.KOMOTO, field));
    }

    private void expectFieldNullable(FieldNames field, Type returntype) {
        expect(commonConverterMock.convertToUri(EasyMock.isA(KoodiV1RDTO.class), EasyMock.eq(field), EasyMock.eq(true))).andReturn(testKey(returntype, field));
    }

    private void expectField(FieldNames field) {
        expect(commonConverterMock.convertToUri(EasyMock.isA(KoodiV1RDTO.class), EasyMock.eq(field))).andReturn(testKey(Type.KOMOTO, field));
    }

    private void expectField(FieldNames field, Type returntype) {
        expect(commonConverterMock.convertToUri(EasyMock.isA(KoodiV1RDTO.class), EasyMock.eq(field))).andReturn(testKey(returntype, field));
    }

    private void expectFieldNimi(FieldNames field) {
        expect(commonConverterMock.convertToUri(EasyMock.isA(NimiV1RDTO.class), EasyMock.eq(field))).andReturn(testKey(Type.KOMOTO, field));
    }

    private void expectFieldNotTested(FieldNames field) {
        expect(commonConverterMock.convertToUri(null, field)).andReturn(testKey(Type.NOT_TESTED, field));
    }

    private void expectFieldNullableNotTested(FieldNames field) {
        expect(commonConverterMock.convertToUri(null, field, true)).andReturn(testKey(Type.NOT_TESTED, field));
    }

    private void expectFieldsNotTested(FieldNames field) {
        expect(commonConverterMock.convertToUris(isA(KoodiUrisV1RDTO.class), EasyMock.<Set<KoodistoUri>>anyObject(), eq(field))).andReturn(new HashSet<KoodistoUri>());
    }

    private void expectFields(FieldNames field, Type returntype) {
        Set<KoodistoUri> set = new HashSet<KoodistoUri>();
        set.add(new KoodistoUri(testKey(returntype, field)));
        expect(commonConverterMock.convertToUris(isA(KoodiUrisV1RDTO.class), EasyMock.<Set<KoodistoUri>>anyObject(), eq(field))).andReturn(set);
    }

    private void assertLukioKomoto(final KoulutusmoduuliToteutus komoto, final Type type) {
        assertEquals(testKey(type, FieldNames.OPINTOALA), komoto.getOpintoalaUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSALA), komoto.getKoulutusalaUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSASTE), komoto.getKoulutusasteUri());
        assertEquals(testKey(type, FieldNames.TUTKINTONIMIKE), komoto.getTutkintonimikeUri());
        assertEquals(testKey(type, FieldNames.KOULUTUS), komoto.getKoulutusUri());
        assertEquals(testKey(type, FieldNames.LUKIOLINJA), komoto.getLukiolinjaUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSARVO), komoto.getOpintojenLaajuusarvoUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO), komoto.getOpintojenLaajuusyksikkoUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSTYYPPI), komoto.getKoulutustyyppiUri());
        assertEquals(testKey(type, FieldNames.TUTKINTO), komoto.getTutkintoUri());
        assertEquals(testKey(type, FieldNames.TUNNISTE), komoto.getUlkoinenTunniste());
        assertEquals(testKey(type, FieldNames.NQF), komoto.getNqfUri());
        assertEquals(testKey(type, FieldNames.EQF), komoto.getEqfUri());
        assertEquals(null, komoto.getKoulutusohjelmaUri());
    }

    private void assertLukioKomoChild(final Koulutusmoduuli m) {
        //LUKIO : do not update komo object
        assertEquals(null, m.getOpintoalaUri());
        assertEquals(null, m.getKoulutusalaUri());
        assertEquals(null, m.getKoulutusasteUri());
        assertEquals(null, m.getTutkintonimikeUri());
        assertEquals(null, m.getKoulutusUri());
        assertEquals(null, m.getLukiolinjaUri());
        assertEquals(null, m.getOpintojenLaajuusarvoUri());
        assertEquals(null, m.getOpintojenLaajuusyksikkoUri());
        assertEquals(null, m.getKoulutustyyppiUri());
        assertEquals(null, m.getTutkintoUri());
        assertEquals(null, m.getUlkoinenTunniste());
        assertEquals(null, m.getNqfUri());
        assertEquals(null, m.getEqfUri());
        assertEquals(null, m.getKoulutusohjelmaUri());
    }

    private void assertKorkeakouluKomoto(final KoulutusmoduuliToteutus komoto, final Type type) {
        kkCommonAsserts(komoto, type);
        assertEquals(testKey(type, FieldNames.TUTKINTONIMIKE), komoto.getTutkintonimikeUri());
    }

    private void assertKorkeakouluKomo(final Koulutusmoduuli komo, final Type type) {
        kkCommonAsserts(komo, type);
        assertEquals(testKey(type, FieldNames.TUTKINTONIMIKE), komo.getTutkintonimikeUri());
    }

    private void kkCommonAsserts(BaseKoulutusmoduuli komo, final Type type) {
        assertEquals(testKey(type, FieldNames.OPINTOALA), komo.getOpintoalaUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSALA), komo.getKoulutusalaUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSASTE), komo.getKoulutusasteUri());

        assertEquals(testKey(type, FieldNames.KOULUTUS), komo.getKoulutusUri());
        assertEquals(null, komo.getKoulutusohjelmaUri());
        assertEquals(null, komo.getLukiolinjaUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSARVO), komo.getOpintojenLaajuusarvoUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO), komo.getOpintojenLaajuusyksikkoUri());
        if (Type.KOMO == type) {
            assertEquals("|koulutustyyppi_3|", komo.getKoulutustyyppiUri());
        } else {
            assertEquals(testKey(type, FieldNames.KOULUTUSTYYPPI), komo.getKoulutustyyppiUri());
        }
        assertEquals(testKey(type, FieldNames.TUTKINTO), komo.getTutkintoUri());
        assertEquals(testKey(Type.BOTH, FieldNames.TUNNISTE), komo.getUlkoinenTunniste());
        assertEquals(testKey(type, FieldNames.NQF), komo.getNqfUri());
        assertEquals(testKey(type, FieldNames.EQF), komo.getEqfUri());
    }

    private void kkCommonExpect(Type type) {
        expectFieldNullable(FieldNames.TUTKINTO, type);
        expectFieldNullable(FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, type);
        expectFieldNullable(FieldNames.OPINTOJEN_LAAJUUSARVO, type);
        expectFieldNullable(FieldNames.KOULUTUSASTE, type);

        expectField(FieldNames.OPINTOALA, type);
        expectField(FieldNames.KOULUTUS, type);
        expectField(FieldNames.KOULUTUSALA, type);
        expectFieldNullable(FieldNames.NQF, type);
        expectFieldNullable(FieldNames.EQF, type);
        expectFieldNullable(FieldNames.KOULUTUSTYYPPI, type);
        expectFields(FieldNames.TUTKINTONIMIKE, type);
    }
}
