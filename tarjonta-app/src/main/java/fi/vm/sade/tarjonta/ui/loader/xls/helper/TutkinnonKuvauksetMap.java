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

import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.ui.loader.xls.TarjontaKomoData;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.KuvausDTO;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.TutkinnonKuvauksetNuoretRow;
import java.util.Collection;

/**
 *
 * @author Jani Wilén
 */
public class TutkinnonKuvauksetMap extends KoulutuskoodiMap<KuvausDTO> {

    private static final long serialVersionUID = 863191778040860554L;

    public TutkinnonKuvauksetMap(Collection<TutkinnonKuvauksetNuoretRow> dtos) {
        super();
        convertToKuvausDto(dtos);
    }

    protected void convertToKuvausDto(Collection<TutkinnonKuvauksetNuoretRow> dtos) {
        int rowIndex = 1;

        for (TutkinnonKuvauksetNuoretRow row : dtos) {
            final String koodiarvo = row.getKoulutuskoodiKoodiarvo();
            checkKey(koodiarvo, row, "Koulutuskoodi", rowIndex);

            KuvausDTO kuvausDTO = new KuvausDTO();
            kuvausDTO.setJatkoOpintomahdollisuudetTeksti(createJatkoOpintomahdollisuudet(row));
            kuvausDTO.setKoulutuksenRakenneTeksti(createKoulutuksellisetJaAmmatillisetTavoitteet(row));
            kuvausDTO.setTavoiteTeksti(createKoulutuksenRakenne(row));

            this.put(koodiarvo, kuvausDTO);
            rowIndex++;
        }
    }

    private MonikielinenTekstiTyyppi createJatkoOpintomahdollisuudet(TutkinnonKuvauksetNuoretRow row) {
        final String fi = row.getJatkoOpintomahdollisuudetFiTeksti();
        final String sv = row.getJatkoOpintomahdollisuudetSvTeksti();
        final String en = row.getJatkoOpintomahdollisuudetEnTeksti();
        return TarjontaKomoData.createTeksti(fi, sv, en);
    }

    private MonikielinenTekstiTyyppi createKoulutuksellisetJaAmmatillisetTavoitteet(TutkinnonKuvauksetNuoretRow row) {
        final String fi = row.getKoulutuksellisetJaAmmatillisetTavoitteetFiTeksti();
        final String sv = row.getKoulutuksellisetJaAmmatillisetTavoitteetSvTeksti();
        final String en = row.getKoulutuksellisetJaAmmatillisetTavoitteetEnTeksti();
        return TarjontaKomoData.createTeksti(fi, sv, en);
    }

    private MonikielinenTekstiTyyppi createKoulutuksenRakenne(TutkinnonKuvauksetNuoretRow row) {
        final String fi = row.getKoulutuksenRakenneFiTeksti();
        final String sv = row.getKoulutuksenRakenneSvTeksti();
        final String en = row.getKoulutuksenRakenneEnTeksti();
        return TarjontaKomoData.createTeksti(fi, sv, en);
    }
}
