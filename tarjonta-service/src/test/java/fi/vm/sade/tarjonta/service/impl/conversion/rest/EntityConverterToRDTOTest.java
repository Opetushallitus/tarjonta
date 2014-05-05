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
import fi.vm.sade.tarjonta.model.BaseKoulutusmoduuli;
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
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
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

/**
 *
 * @author jani
 */
public class EntityConverterToRDTOTest {

    private static final String NOT_TESTED = "this test will ingnore this data field";

    private enum Type {

        KOMO,
        KOMOTO,
        NOT_TESTED

    };

    private static final Locale FI = new Locale("FI");

    private EntityConverterToRDTO<KoulutusKorkeakouluV1RDTO> instanceKk;

    private EntityConverterToRDTO<KoulutusLukioV1RDTO> instanceLukio;

    private KoulutusCommonConverter commonConverterMock;

    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConvertersMock;

    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKuvausConvertersMock;

    private static final Set<KoodistoUri> SET_KOMOTO_TUTKINTONIMIKE = Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMOTO, FieldNames.TUTKINTONIMIKE)));
    private static final Set<KoodistoUri> SET_KOMO_TUTKINTONIMIKE = Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE)));

    protected static KoodiV1RDTO toKoodiUri(final String type) {
        return new KoodiV1RDTO(type, 1, null);
    }

    protected static KoodiV1RDTO toKoodiUri(final Type type, final FieldNames field) {
        return new KoodiV1RDTO(testKey(type, field), 1, null);
    }

    protected static KoodiV1RDTO toKoodiUri(final Type type) {
        return new KoodiV1RDTO(type.name(), 1, null);
    }

    protected static KoodiUrisV1RDTO toKoodiUris(final Type type, final FieldNames field) {
        KoodiUrisV1RDTO uris = new KoodiUrisV1RDTO();
        uris.setUris(Maps.<String, Integer>newHashMap());
        uris.getUris().put(testKey(type, field), 1);
        return uris;
    }

    protected static KoodiUrisV1RDTO toKoodiUris(final Type type) {
        KoodiUrisV1RDTO uris = new KoodiUrisV1RDTO();
        uris.setUris(Maps.<String, Integer>newHashMap());
        uris.getUris().put(type.name(), 1);
        return uris;
    }

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

    }

    /**
     * Test of convert method, of class EntityConverterToRDTO.
     */
    private static String testKey(final Type prefix, final FieldNames field) {
        Preconditions.checkNotNull(prefix, "prefix value cannot be null.");
        Preconditions.checkNotNull(field, "field enum cannot be null.");
        return new StringBuilder(prefix.name()).append("_").append(field).toString();
    }

    private static void populateBaseValues(Type type, BaseKoulutusmoduuli m) {
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

    @Test
    public void testConvertKomoKomotoOverride() {
        Koulutusmoduuli m = new Koulutusmoduuli();
        m.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        populateBaseValues(Type.KOMO, m);
        m.setKoulutustyyppiEnum(KoulutustyyppiEnum.LUKIOKOULUTUS);
        m.setTutkintonimikes(Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE))));

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();
        populateBaseValues(Type.KOMOTO, t);
        t.setTutkintonimikes(Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMOTO, FieldNames.TUTKINTONIMIKE))));
        t.setKoulutusmoduuli(m);

        expect(komoKuvausConvertersMock.convertMonikielinenTekstiToTekstiDTO(EasyMock.<Map<KomoTeksti, MonikielinenTeksti>>anyObject(), anyBoolean())).andReturn(new KuvausV1RDTO());
        expect(komotoKuvausConvertersMock.convertMonikielinenTekstiToTekstiDTO(EasyMock.<Map<KomotoTeksti, MonikielinenTeksti>>anyObject(), anyBoolean())).andReturn(new KuvausV1RDTO());

        final Type returnKomoto = Type.KOMOTO;

        expect(commonConverterMock.convertToKoodiDTO(null, FI, FieldNames.ALKAMISKAUSI, true, false)).andReturn(toKoodiUri(NOT_TESTED));
        expect(commonConverterMock.koulutusohjelmaUiMetaDTO(null, FI, FieldNames.KOULUTUSOHJELMA, false)).andReturn(new NimiV1RDTO());
        expect(commonConverterMock.convertToKoodiUrisDTO(SET_KOMOTO_TUTKINTONIMIKE, FI, FieldNames.TUTKINTONIMIKE, false)).andReturn(toKoodiUris(Type.KOMOTO, FieldNames.TUTKINTONIMIKE));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.AIHEES, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.AMMATTINIMIKKEET, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.POHJALKOULUTUSVAATIMUS, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
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

        expect(commonConverterMock.searchOrganisaationNimi(null, FI)).andReturn(new OrganisaatioV1RDTO(NOT_TESTED, NOT_TESTED, null));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.OPETUSKIELIS, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.OPETUSMUODOS, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.OPETUSPAIKKAS, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiUrisDTO(Sets.<KoodistoUri>newHashSet(), FI, FieldNames.OPETUSAIKAS, false)).andReturn(toKoodiUris(Type.NOT_TESTED));
        expect(commonConverterMock.convertToKoodiDTO(null, FI, FieldNames.SUUNNITELTUKESTON_TYYPPI, false)).andReturn(toKoodiUri(NOT_TESTED));

        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);

        final KoulutusKorkeakouluV1RDTO convert = instanceKk.convert(KoulutusKorkeakouluV1RDTO.class, t, "FI", false);

        EasyMock.verify(commonConverterMock);

        assertEquals(testKey(Type.KOMOTO, FieldNames.OPINTOALA), convert.getOpintoala().getUri());
        assertEquals(testKey(Type.KOMOTO, FieldNames.KOULUTUSALA), convert.getKoulutusala().getUri());
        assertEquals(testKey(Type.KOMOTO, FieldNames.KOULUTUSASTE), convert.getKoulutusaste().getUri());
        assertEquals(false, convert.getTutkintonimikes().getUris().isEmpty());
        assertEquals(testKey(Type.KOMOTO, FieldNames.KOULUTUSKOODI), convert.getKoulutuskoodi().getUri());
        assertEquals(null, convert.getKoulutusohjelma().getUri()); //only name, not uri
        assertEquals(testKey(Type.KOMOTO, FieldNames.OPINTOJEN_LAAJUUSARVO), convert.getOpintojenLaajuusarvo().getUri());
        assertEquals(testKey(Type.KOMOTO, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO), convert.getOpintojenLaajuusyksikko().getUri());
        assertEquals(testKey(Type.KOMOTO, FieldNames.KOULUTUSTYYPPI), convert.getKoulutustyyppi().getUri());
        assertEquals(testKey(Type.KOMOTO, FieldNames.TUTKINTO), convert.getTutkinto().getUri());
        assertEquals(testKey(Type.KOMOTO, FieldNames.TUNNISTE), convert.getTunniste());
        assertEquals(testKey(Type.KOMOTO, FieldNames.NQF), convert.getNqf().getUri());
        assertEquals(testKey(Type.KOMOTO, FieldNames.EQF), convert.getEqf().getUri());
    }

    private final void expectConvertToKoodiDTO(Type returnType, FieldNames field) {
        expect(commonConverterMock.convertToKoodiDTO(testKey(Type.KOMO, field), testKey(Type.KOMOTO, field), FI, field, false)).andReturn(toKoodiUri(returnType, field));
    }

    private final void expectConvertToKoodiDTOAllowNull(Type returnType, FieldNames field) {
        expect(commonConverterMock.convertToKoodiDTO(testKey(Type.KOMO, field), testKey(Type.KOMOTO, field), FI, field, true, false)).andReturn(toKoodiUri(returnType, field));
    }
}
