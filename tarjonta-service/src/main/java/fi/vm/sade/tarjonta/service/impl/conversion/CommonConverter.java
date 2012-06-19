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
package fi.vm.sade.tarjonta.service.impl.conversion;

import java.util.Set;

import fi.vm.sade.tarjonta.model.KoodistoKoodi;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliPerustiedot;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliPerustiedotDTO;
import java.util.HashSet;

/**
 *
 * @author Jukka Raanamo
 */
public final class CommonConverter {

    private CommonConverter() {
    }

    public static KoulutusmoduuliPerustiedotDTO convert(KoulutusmoduuliPerustiedot source) {

        if (source == null) {
            return null;
        }

        KoulutusmoduuliPerustiedotDTO dto = new KoulutusmoduuliPerustiedotDTO();
        dto.setKoulutusKoodiUri(source.getKoulutusKoodiUri());
        dto.setOpetuskielis(convertKoodistoKoodisToString(source.getOpetuskieletkielis()));
        dto.setOpetusmuotos(convertKoodistoKoodisToString(source.getOpetusmuotos()));
        dto.setAsiasanoituses(convertKoodistoKoodisToString(source.getAsiasanoituses()));
        return dto;

    }

    public static KoulutusmoduuliPerustiedot convert(KoulutusmoduuliPerustiedotDTO source) {

        if (source == null) {
            return null;
        }

        KoulutusmoduuliPerustiedot model = new KoulutusmoduuliPerustiedot();
        model.setAsiasanoituses(source.getAsiasanoituses());
        model.setOpetuskielis(source.getOpetuskielis());
        model.setOpetusmuotos(source.getOpetusmuotos());
        model.setKoulutusKoodiUri(source.getKoulutusKoodiUri());

        return model;

    }

    private static Set<String> convertKoodistoKoodisToString(Set<KoodistoKoodi> koodis) {

        if (koodis == null) {
            return null;
        }
        
        final Set<String> opetuskielis = new HashSet<String>();
        for (KoodistoKoodi curKieli : koodis) {
            opetuskielis.add(curKieli.getKoodiUri());
        }
        return opetuskielis;
        
    }

}

