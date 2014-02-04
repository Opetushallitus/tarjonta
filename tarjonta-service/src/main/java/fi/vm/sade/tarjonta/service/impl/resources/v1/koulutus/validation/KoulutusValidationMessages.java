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
package fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation;

public enum KoulutusValidationMessages {
    //date
    KOULUTUS_INVALID_ALKAMISPVM_MISSING,
    KOULUTUS_INVALID_ALKAMISPVM_WRONG_KAUSI_VUOSI,
    //sets
    KOULUTUS_INVALID_KOODI_OPETUSAIKA,
    KOULUTUS_INVALID_KOODI_TEEMAT_AIHEET,
    KOULUTUS_INVALID_KOODI_OPETUSPAIKKA,
    KOULUTUS_INVALID_KOODI_OPETUSKIELI,
    //relations
    KOULUTUS_INVALID_KOODI_KOULUTUSKOODI,
    KOULUTUS_INVALID_KOODI_KOULUTUSALA,
    KOULUTUS_INVALID_KOODI_OPINTOALA,
    KOULUTUS_INVALID_KOODI_TUTKINTO,
    KOULUTUS_INVALID_KOODI_TUTKINTONIMIKE,
    KOULUTUS_INVALID_KOODI_EQF,
    KOULUTUS_INVALID_KOODI_OPINTOJENLAAJUUS,
    KOULUTUS_INVALID_KOODI_KOULUTUSASTE,
    //name
    KOULUTUS_INVALID_KOULUTUSOHJELMA_KOODI,
    KOULUTUS_INVALID_KOULUTUSOHJELMA_NAME,
    KOULUTUS_MISSING_KOULUTUSOHJELMA,
    //other
    KOULUTUS_MISSING,
    KOULUTUS_NIMI_MISSING,
    KOULUTUS_TARJOAJA_MISSING,
    KOULUTUS_TILA_MISSING;
}
