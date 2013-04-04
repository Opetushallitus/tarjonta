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

import fi.vm.sade.tarjonta.ui.loader.xls.dto.TutkintonimikeRow;
import java.util.Collection;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class TutkintonimikeMap extends HashMap<String, TutkintonimikeRow> {

    private static final long serialVersionUID = 863191778040860554L;
    private static final Logger log = LoggerFactory.getLogger(TutkintonimikeMap.class);
    private int index = 1;

    public TutkintonimikeMap(Collection<TutkintonimikeRow> dtos) {
        super();

        log.info("Row item count : {}", dtos.size());

        for (TutkintonimikeRow row : dtos) {
            if (row.getTutkintonimikeKoodiarvo() == null || row.getTutkintonimikeKoodiarvo().isEmpty()) {
                throw new RuntimeException("koulutuskoodi cannot be null! Row number : " + index + ", object : " + row);
            }
            final String relation = row.getRelaatioKoodiarvo();

            if (relation == null || relation.isEmpty()) {
                throw new RuntimeException("Koulutusohjelma / lukiolinja cannot be null! Row number : " + index + ", object : " + row);
            }

            if (this.containsKey(relation)) {
                throw new RuntimeException("Key already exists, key '" + relation + "'");
            }

            if (relation.contains(".")) {
                throw new RuntimeException("An invalid character was found in relation key : '" + relation + "'");
            }

            this.put(relation, row);
            index++;
        }
    }
}
