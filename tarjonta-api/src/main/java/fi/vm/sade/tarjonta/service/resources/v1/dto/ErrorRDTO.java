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

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mlyly
 */
public class ErrorRDTO implements Serializable {

    /**
     * Generic error codes.
     */
    public enum ErrorCode {

        /**
         * Indicates NO error.
         */
        NONE,
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
     * The type of the error
     */
    private ErrorCode _errorCode = ErrorCode.NONE;
    /**
     * Target object / whatever caused the error.
     */
    private String _errorTarget = null;
    /**
     * The field which the error is part of.
     */
    private String _errorField = null;
    /**
     * Localised error message key.
     */
    private String _errorMessageKey = null;
    /**
     * Localised messages parameteres, ui should expand these.
     */
    private List<String> _errorMessageParameters = null;
    /**
     * This can contain the stacktrace or other error information.
     */
    private String _errorTechnicalInformation = null;

    /**
     * Create info message.
     *
     * @param key
     * @param messageParameters
     * @return
     */
    public static ErrorRDTO createInfo(String key, String... messageParameters) {
        ErrorRDTO result = new ErrorRDTO();

        result.setErrorCode(ErrorCode.INFO);
        result.setErrorMessageKey(key);
        fillInMessageParameters(result, messageParameters);

        return result;
    }

    /**
     * Create "system" error entry, includes stacktrace.
     *
     * @param ex
     * @param key
     * @param messageParameters
     * @return
     */
    public static ErrorRDTO createSystemError(Throwable ex, String key, String... messageParameters) {
        ErrorRDTO result = createInfo(key, messageParameters);
        result.setErrorCode(ErrorCode.ERROR);

        if (ex != null) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            result.setErrorTechnicalInformation(sw.toString());
        }

        return result;
    }

    /**
     * Create simple validation error.
     *
     * @param field
     * @param key
     * @param messageParameters
     * @return
     */
    public static ErrorRDTO createValidationError(String field, String key, String... messageParameters) {
        ErrorRDTO result = createInfo(key, messageParameters);
        result.setErrorCode(ErrorCode.VALIDATION);
        result.setErrorField(field);
        return result;
    }

    private static void fillInMessageParameters(ErrorRDTO result, String... messageParameters) {
        if (messageParameters != null) {
            result.setErrorMessageParameters(new ArrayList<String>());
            for (String messageParam : messageParameters) {
                result.getErrorMessageParameters().add(messageParam);
            }
        }
    }

    public ErrorCode getErrorCode() {
        return _errorCode;
    }

    public void setErrorCode(ErrorCode _errorCode) {
        this._errorCode = _errorCode;
    }

    public String getErrorMessageKey() {
        return _errorMessageKey;
    }

    public void setErrorMessageKey(String _errorMessageKey) {
        this._errorMessageKey = _errorMessageKey;
    }

    public List<String> getErrorMessageParameters() {
        return _errorMessageParameters;
    }

    public void setErrorMessageParameters(List<String> _errorMessageParameters) {
        this._errorMessageParameters = _errorMessageParameters;
    }

    public String getErrorTarget() {
        return _errorTarget;
    }

    public void setErrorTarget(String _errorTarget) {
        this._errorTarget = _errorTarget;
    }

    public String getErrorField() {
        return _errorField;
    }

    public void setErrorField(String _errorField) {
        this._errorField = _errorField;
    }

    public String getErrorTechnicalInformation() {
        return _errorTechnicalInformation;
    }

    public void setErrorTechnicalInformation(String _errorTechnicalInformation) {
        this._errorTechnicalInformation = _errorTechnicalInformation;
    }
}
