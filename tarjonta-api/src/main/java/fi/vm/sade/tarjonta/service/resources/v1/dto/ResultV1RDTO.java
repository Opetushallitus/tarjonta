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
package fi.vm.sade.tarjonta.service.resources.v1.dto;

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
public class ResultV1RDTO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

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
        ERROR,

        /**
         * For those occasions requested resource is not found.
         */
        NOT_FOUND
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
    private List<ErrorV1RDTO> _errors;

    public ResultStatus getStatus() {
        return _status;
    }

    public void setStatus(ResultStatus _status) {
        this._status = _status;
    }

    public ResultV1RDTO() {
    }

    public ResultV1RDTO(T result) {
        setResult(result);
    }

    public T getResult() {
        return _result;
    }

    public void setResult(T result) {
        _result = result;
    }

    public List<ErrorV1RDTO> getErrors() {
        return _errors;
    }

    public void setErrors(List<ErrorV1RDTO> _errors) {
        this._errors = _errors;
    }

    public void addError(ErrorV1RDTO error) {
        if (error != null) {
            if (getErrors() == null) {
                setErrors(new ArrayList<ErrorV1RDTO>());
            }
            getErrors().add(error);
        }
    }

    /**
     * Returns true if any "errors" added. ie. getErrors() returns nonempty collection.
     *
     * @return
     */
    public boolean hasErrors() {
        return _errors != null && !_errors.isEmpty();
    }

    /**
     * Return true if any of the errors is given type.
     *
     * @param errorType
     * @return
     */
    public boolean hasErrors(ErrorV1RDTO.ErrorCode errorType) {
        boolean result = false;
        for (ErrorV1RDTO errorV1RDTO : getErrors()) {
            result = result || errorV1RDTO.getErrorCode().equals(errorType);
        }
        return result;
    }


}
