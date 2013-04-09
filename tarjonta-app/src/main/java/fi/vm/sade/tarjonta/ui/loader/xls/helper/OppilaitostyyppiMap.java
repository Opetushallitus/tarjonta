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

import fi.vm.sade.tarjonta.ui.loader.xls.dto.GenericRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.OppilaitostyyppiRow;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class OppilaitostyyppiMap extends HashMap<String, List<String>> {

    private static final long serialVersionUID = 863191778040860554L;
    private static final Logger log = LoggerFactory.getLogger(OppilaitostyyppiMap.class);
    private int index = 1;

    public OppilaitostyyppiMap(Collection<OppilaitostyyppiRow> dtos) {
        super();

        log.info("Row item count : {}", dtos.size());


        for (OppilaitostyyppiRow row : dtos) {
            if (row.getOppilaitostyyppiKoodiarvo() == null || row.getOppilaitostyyppiKoodiarvo().isEmpty()) {
                throw new RuntimeException("Oppilaitostyyppi cannot be null! Row number : " + index + ", object : " + row);
            }
            final String relation = row.getOppilaitostyyppiKoodiarvo();

            for (String koulutusaste : row.getKoulutusastes()) {
                if (koulutusaste == null || koulutusaste.isEmpty()) {
                    throw new RuntimeException("Koulutusaste cannot be null! Row number : " + index + ", object : " + row);
                }

                //kouutusaste value as key, all oppilaitostyyppi string values as the result
                if (this.containsKey(koulutusaste)) {
                    this.get(koulutusaste).add(relation);
                } else {
                    this.put(koulutusaste, new ArrayList(Arrays.asList(relation)));
                }
            }


            index++;
        }
    }
}
