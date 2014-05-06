/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.model.BaseKoulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.enums.KoulutustyyppiEnum;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author jani
 */
abstract class KoulutusRestBase {

    protected static final Locale FI = new Locale("FI");
    protected static final String NOT_TESTED = "this test will ingnore this data field";
    protected static final Set<KoodistoUri> SET_KOMOTO_TUTKINTONIMIKE = Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMOTO, FieldNames.TUTKINTONIMIKE)));
    protected static final Set<KoodistoUri> SET_KOMO_TUTKINTONIMIKE = Sets.<KoodistoUri>newHashSet(new KoodistoUri(testKey(Type.KOMO, FieldNames.TUTKINTONIMIKE)));

    protected enum Type {
        BOTH,
        KOMO,
        KOMO_CHILD,
        KOMOTO,
        NOT_TESTED

    };

    /**
     * Test of convert method, of class EntityConverterToRDTO.
     */
    protected static String testKey(final EntityConverterToRDTOTest.Type prefix, final FieldNames field) {
        Preconditions.checkNotNull(prefix, "prefix value cannot be null.");
        Preconditions.checkNotNull(field, "field enum cannot be null.");
        return new StringBuilder(prefix.name()).append("_").append(field).toString();
    }

    protected static void korkeakouluPopulateBaseValues(EntityConverterToRDTOTest.Type type, BaseKoulutusmoduuli m) {
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

    protected static void lukioPopulateBaseValues(EntityConverterToRDTOTest.Type type, BaseKoulutusmoduuli m) {
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

    protected static void lukioPopulateChildKomoBaseValues(Koulutusmoduuli m) {
        Preconditions.checkNotNull(m, "Koulutusmoduuli object cannot be null.");
        m.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        m.setOid(EntityConverterToRDTOTest.Type.KOMO_CHILD.name());
        m.setKoulutusUri(testKey(EntityConverterToRDTOTest.Type.KOMO_CHILD, FieldNames.KOULUTUSKOODI));
        m.setNqfUri(testKey(EntityConverterToRDTOTest.Type.KOMO_CHILD, FieldNames.NQF));
        m.setEqfUri(testKey(EntityConverterToRDTOTest.Type.KOMO_CHILD, FieldNames.EQF));
        m.setLukiolinjaUri(testKey(EntityConverterToRDTOTest.Type.KOMO_CHILD, FieldNames.LUKIOLINJA));
        m.setTutkintonimikeUri(testKey(EntityConverterToRDTOTest.Type.KOMO_CHILD, FieldNames.TUTKINTONIMIKE));
        m.setKoulutustyyppiEnum(KoulutustyyppiEnum.LUKIOKOULUTUS);
    }

    protected static KoodiV1RDTO toKoodiUri(final String type) {
        return new KoodiV1RDTO(type, 1, null);
    }

    protected static KoodiV1RDTO toKoodiUri(final EntityConverterToRDTOTest.Type type, final FieldNames field) {
        return new KoodiV1RDTO(testKey(type, field), 1, null);
    }

    protected static NimiV1RDTO toKoodiUriNimi(final EntityConverterToRDTOTest.Type type, final FieldNames field) {
        NimiV1RDTO dto = new NimiV1RDTO();
        dto.setUri(testKey(type, field));
        return dto;
    }

    protected static KoodiUrisV1RDTO toKoodiUris(final EntityConverterToRDTOTest.Type type, final FieldNames field) {
        KoodiUrisV1RDTO uris = new KoodiUrisV1RDTO();
        uris.setUris(Maps.<String, Integer>newHashMap());
        uris.getUris().put(testKey(type, field), 1);
        return uris;
    }

    protected static KoodiUrisV1RDTO toKoodiUris(final EntityConverterToRDTOTest.Type type) {
        KoodiUrisV1RDTO uris = new KoodiUrisV1RDTO();
        uris.setUris(Maps.<String, Integer>newHashMap());
        uris.getUris().put(type.name(), 1);
        return uris;
    }

}
