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
package fi.vm.sade.tarjonta.koodisto.util;

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.koodisto.model.Koodi;

/**
 *
 * @author Jukka Raanamo
 */
public class ConversionUtils {

    public static void copy(Koodi from, KoodiType to) {

        to.setKoodiArvo(from.getKoodiArvo());
        to.setKoodiUri(from.getKoodiUri());
        to.setVersio(from.getKoodiVersio());

        if (from.getKoodiNimiFi() != null) {
            to.getMetadata().add(createMetadata(from.getKoodiNimiFi()));
        }
        if (from.getKoodiNimiEn() != null) {
            to.getMetadata().add(createMetadata(from.getKoodiNimiEn()));
        }
        if (from.getKoodiNimiSv() != null) {
            to.getMetadata().add(createMetadata(from.getKoodiNimiSv()));
        }

    }

    private static KoodiMetadataType createMetadata(String nimiFi) {

        KoodiMetadataType md = new KoodiMetadataType();
        md.setNimi(nimiFi);
        md.setKieli(KieliType.FI);

        return md;

    }

}

