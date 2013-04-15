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

import fi.vm.sade.tarjonta.ui.loader.xls.dto.AbstractKoulutuskoodiField;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.KoulutusohjelmanKuvauksetRow;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class KoulutuskoodiMap<ROWOBJECT extends AbstractKoulutuskoodiField> extends AbstractKeyMap<ROWOBJECT> {

    private static final long serialVersionUID = 863191778040860554L;
    private static final Logger log = LoggerFactory.getLogger(KoulutuskoodiMap.class);

    public KoulutuskoodiMap() {
    }

    public KoulutuskoodiMap(Collection<ROWOBJECT> dtos) {
        super();

        log.info("Row item count : {}", dtos.size());
        convert(dtos);
    }

    protected void convert(Collection<ROWOBJECT> dtos) {
        int rowIndex = 1;
        for (ROWOBJECT rowKr : dtos) {
            final String koulutuskoodiKoodiarvo = rowKr.getKoulutuskoodiKoodiarvo();

            checkKey(koulutuskoodiKoodiarvo, rowKr, "Koulutuskoodi", rowIndex);
            this.put(koulutuskoodiKoodiarvo, rowKr);
            rowIndex++;
        }
    }
}
