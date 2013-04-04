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

import fi.vm.sade.tarjonta.ui.loader.xls.dto.KoulutusRelaatioRow;
import java.util.Collection;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class KoulutuskoodiMap extends HashMap<String, KoulutusRelaatioRow> {

    private static final long serialVersionUID = 863191778040860554L;
    private static final Logger log = LoggerFactory.getLogger(KoulutuskoodiMap.class);
    private int index = 1;

    public KoulutuskoodiMap(Collection<KoulutusRelaatioRow> dtos) {
        super();

        log.info("Row item count : {}", dtos.size());

        for (KoulutusRelaatioRow rowKr : dtos) {
            if (rowKr.getKoulutuskoodiKoodiarvo() == null || rowKr.getKoulutuskoodiKoodiarvo().isEmpty()) {
                throw new RuntimeException("koulutuskoodi cannot be null! Row number : " + index);
            }

            if (rowKr.getKoulutuskoodiKoodiarvo().contains(".")) {
                throw new RuntimeException("An invalid character was found in relation key : '" + rowKr.getKoulutuskoodiKoodiarvo() + "'");
            }

            this.put(rowKr.getKoulutuskoodiKoodiarvo(), rowKr);
            index++;
        }
    }
}
