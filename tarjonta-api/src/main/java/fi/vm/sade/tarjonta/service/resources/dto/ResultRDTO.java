/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
package fi.vm.sade.tarjonta.service.resources.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic result wrapping for REST apis.
 *
 * Examples:
 * <pre>
 * {
 *   status: "OK",
 *   result: {
 *     …
 *   }
 * }
 * </pre>
 *
 * <pre>
 * {
 *    status: "VALIDATION",
 *    result: {
 *      …
 *    } ,
 *
 *    errors: [
 *      {
 *        errorCode: "VALIDATION",
 *	      errorTarget: "USER/1.2.3.4.5",
 *	      errorField: "password",
 *        errorMessageKey: "password.tooShort",
 *        errorMessageParameters: [ "foobar", 10],
 *	},
 *      {
 *        errorCode: "VALIDATION",
 *        errorTarget: "1.2.3.4.5",
 *	      errorField: "email",
 *        errorMessageKey: "email.reserved",
 *	},
 *    ]
 *}
 * </pre>
 *
 * <pre>
 * {
 *     status: "ERROR",
 *     errors: [
 *       {
 *           errorCode: "ERROR",
 *           errorTarget: "Hakukohde/1.2.3.4.5",
 *           errorMessageKey: "system.error",
 *           errorTechnicalInformation: "java.lang.NullPointerExceltion in Hakukohde.java:752 …"
 *       }
 *     ]
 * }
 * </pre>
 *
 *
 * @author mlyly
 */
public class ResultRDTO<T> implements Serializable {

    /**
     * Generic error codes.
     */
    public enum ResultStatus {

        /**
         * Indicates NO error.
         */
        OK,
        /**
         * Information for the user.
         */
        INFO,
        /**
         * Warning for the user.
         */
        WARNING,
        /**
         * Validation errors.
         */
        VALIDATION,
        /**
         * A real "error".
         */
        ERROR
    };
    /**
     * Default status for the result is OK.
     */
    private ResultStatus _status = ResultStatus.OK;
    /**
     * Actual result contained here.
     */
    private T _result;
    /**
     * "Sub" errors for this one (if this one is a "master" error).
     */
    private List<ErrorRDTO> _errors;

    public ResultStatus getStatus() {
        return _status;
    }

    public void setStatus(ResultStatus _status) {
        this._status = _status;
    }

    public T getResult() {
        return _result;
    }

    public void setResult(T result) {
        _result = result;
    }

    public List<ErrorRDTO> getErrors() {
        return _errors;
    }

    public void setErrors(List<ErrorRDTO> _errors) {
        this._errors = _errors;
    }

    public void addError(ErrorRDTO error) {
        if (error != null) {
            if (getErrors() == null) {
                setErrors(new ArrayList<ErrorRDTO>());
            }
            getErrors().add(error);
        }
    }
}
