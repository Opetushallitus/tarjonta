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
package fi.vm.sade.tarjonta.ui.loader.xls.helper;

import fi.vm.sade.tarjonta.ui.loader.xls.TarjontaKomoData;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.KuvausDTO;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.LukionKoulutusModuulitRow;
import java.util.Collection;

/**
 *
 * @author Jani Wilén
 */
public class LukionModuulitMap extends KoulutuskoodiMap< KuvausDTO> {

    private static final long serialVersionUID = 863191778040860554L;

    public LukionModuulitMap(Collection<LukionKoulutusModuulitRow> dtos) {
        super();
        convertToKuvausDto(dtos);
    }

    protected void convertToKuvausDto(Collection<LukionKoulutusModuulitRow> dtos) {
        int rowIndex = 1;

        for (LukionKoulutusModuulitRow row : dtos) {
            final String koodiarvo = row.getKoulutuskoodiKoodiarvo();
            checkKey(koodiarvo, row, "Koulutuskoodi", rowIndex);

            KuvausDTO kuvausDTO = new KuvausDTO();
            final String jatkoOpintomahdollisuudetTeksti = row.getJatkoOpintomahdollisuudetTeksti();
            final String koulutuksellisetTeksti = row.getKoulutuksellisetTeksti();
            final String koulutuksenRakenneTeksti = row.getKoulutuksenRakenneTeksti();

            kuvausDTO.setJatkoOpintomahdollisuudetTeksti(TarjontaKomoData.createTeksti(jatkoOpintomahdollisuudetTeksti, null, null));
            kuvausDTO.setKoulutuksenRakenneTeksti(TarjontaKomoData.createTeksti(koulutuksenRakenneTeksti, null, null));
            kuvausDTO.setTavoiteTeksti(TarjontaKomoData.createTeksti(koulutuksellisetTeksti, null, null));

            this.put(koodiarvo, kuvausDTO);
            rowIndex++;
        }
    }
}
