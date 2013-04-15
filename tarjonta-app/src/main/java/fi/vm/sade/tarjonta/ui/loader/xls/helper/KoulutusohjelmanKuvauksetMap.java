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
import fi.vm.sade.tarjonta.ui.loader.xls.dto.KoulutusohjelmanKuvauksetRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.KuvausDTO;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.TutkinnonKuvauksetNuoretRow;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class KoulutusohjelmanKuvauksetMap extends AbstractKeyMap< KuvausDTO> {

    private static final long serialVersionUID = 863191778040860554L;
    private static final Logger log = LoggerFactory.getLogger(KoulutusohjelmanKuvauksetMap.class);

    public KoulutusohjelmanKuvauksetMap(Collection<KoulutusohjelmanKuvauksetRow> dtos) {
        super();
        convertToKuvausDto(dtos);
    }

    protected void convertToKuvausDto(Collection<KoulutusohjelmanKuvauksetRow> dtos) {
        int rowIndex = 1;

        for (KoulutusohjelmanKuvauksetRow row : dtos) {
            final String koodiarvo = row.getKoulutusohjelmaKoodiarvo();
            log.debug(koodiarvo);
            
            checkKey(koodiarvo, row, "Koulutusohjelma", rowIndex);

            KuvausDTO kuvausDTO = new KuvausDTO();

            kuvausDTO.setKoulutuksenRakenneTeksti(createKoulutuksellisetJaAmmatillisetTavoitteet(row));

            this.put(koodiarvo, kuvausDTO);
            rowIndex++;
        }
    }

    private MonikielinenTekstiTyyppi createKoulutuksellisetJaAmmatillisetTavoitteet(KoulutusohjelmanKuvauksetRow row) {
        final String fi = row.getKoulutusohjelmanTavoiteFiTeksti();
        final String sv = row.getKoulutusohjelmanTavoiteSvTeksti();
        final String en = null;
        return TarjontaKomoData.createTeksti(fi, sv, en);
    }
}
