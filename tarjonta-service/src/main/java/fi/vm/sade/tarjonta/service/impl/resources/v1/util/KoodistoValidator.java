/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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
package fi.vm.sade.tarjonta.service.impl.resources.v1.util;

import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Koodisto koodi uri validation. Checks that koodisto uri
 *
 * @author mlyly
 */
@Component
public class KoodistoValidator {

    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    /**
     * Validates and adds possible error messages to result if koodisto uris are invalid.
     *
     * Validates koodi uris by actually checking that the given koodi uri (may have version #version) exists.
     *
     * Error message key is contructed from : errorKeyPrefix + "." + fieldName + ".missing" / ".invalid"
     *
     * @param koodiUri
     * @param required
     * @param fieldName
     * @param addErrorsTo
     * @param errorKeyPrefix
     * @return false when validation fails
     */
    public boolean validateKoodiUri(String koodiUri, boolean required, String fieldName, ResultV1RDTO addErrorsTo, String errorKeyPrefix) {
        if (isEmpty(koodiUri)) {
            if (required) {
                if (addErrorsTo != null) {
                    addErrorsTo.addError(ErrorV1RDTO.createValidationError(fieldName, errorKeyPrefix + "." + fieldName + ".missing"));
                }
                return false;
            }
            // OK, empty but not required
            return true;
        }

        // URI not empty, check thaty koodi can be found from koodisto
        if (tarjontaKoodistoHelper.getKoodiByUri(koodiUri) == null) {
            if (addErrorsTo != null) {
                addErrorsTo.addError(ErrorV1RDTO.createValidationError(fieldName, errorKeyPrefix + "." + fieldName + ".invalid"));
            }
            // goddammit, koodi not found
            return false;
        }

        // No detected errors
        return true;
    }

    private boolean isEmpty(String koodiUri) {
        return koodiUri == null || koodiUri.trim().isEmpty();
    }
}
