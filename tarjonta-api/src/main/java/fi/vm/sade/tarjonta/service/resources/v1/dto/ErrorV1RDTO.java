package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ErrorV1RDTO implements Serializable {

  public enum ErrorCode {
    NONE,
    INFO,
    WARNING,
    VALIDATION,
    ERROR
  };

  private ErrorCode _errorCode = ErrorCode.NONE;

  private String _errorTarget = null;

  private String _errorField = null;

  private String _errorMessageKey = null;

  private List<String> _errorMessageParameters = null;

  private String _errorTechnicalInformation = null;

  public static ErrorV1RDTO createInfo(String key, String... messageParameters) {
    ErrorV1RDTO result = new ErrorV1RDTO();

    result.setErrorCode(ErrorCode.INFO);
    result.setErrorMessageKey(key);
    fillInMessageParameters(result, messageParameters);

    return result;
  }

  public static ErrorV1RDTO createSystemError(
      Throwable ex, String key, String... messageParameters) {
    ErrorV1RDTO result = createInfo(key, messageParameters);
    result.setErrorCode(ErrorCode.ERROR);

    if (ex != null) {
      StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw));
      result.setErrorTechnicalInformation(sw.toString());
    }

    return result;
  }

  public static ErrorV1RDTO createValidationError(
      String field, String key, String... messageParameters) {
    ErrorV1RDTO result = createInfo(key, messageParameters);
    result.setErrorCode(ErrorCode.VALIDATION);
    result.setErrorField(field);
    return result;
  }

  private static void fillInMessageParameters(ErrorV1RDTO result, String... messageParameters) {
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

  @Override
  public String toString() {
    return "ErrorV1RDTO [_errorCode="
        + _errorCode
        + ", _errorTarget="
        + _errorTarget
        + ", _errorField="
        + _errorField
        + ", _errorMessageKey="
        + _errorMessageKey
        + ", _errorMessageParameters="
        + _errorMessageParameters
        + ", _errorTechnicalInformation="
        + _errorTechnicalInformation
        + "]";
  }
}
