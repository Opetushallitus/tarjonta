package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultV1RDTO<T> implements Serializable {

  private static final long serialVersionUID = 1L;

  public enum ResultStatus {
    OK,
    INFO,
    WARNING,
    VALIDATION,
    ERROR,
    NOT_FOUND
  };

  private ResultStatus _status = ResultStatus.OK;

  private T _result;

  private List<ErrorV1RDTO> _errors;

  private GenericSearchParamsV1RDTO _params = null;

  private Map<String, Boolean> _accessRights = new HashMap<String, Boolean>();

  public ResultStatus getStatus() {
    return _status;
  }

  public void setStatus(ResultStatus _status) {
    this._status = _status;
  }

  public ResultV1RDTO() {}

  public ResultV1RDTO(T result) {
    setResult(result);
  }

  public ResultV1RDTO(T result, ResultStatus status) {
    setResult(result);
    setStatus(status);
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

    if (hasErrors()) {
      setStatus(ResultStatus.ERROR);
    }
  }

  public void addError(ErrorV1RDTO error) {
    if (error != null) {
      if (getErrors() == null) {
        setErrors(new ArrayList<ErrorV1RDTO>());
      }
      getErrors().add(error);

      // Well, now we have an error...
      setStatus(ResultStatus.ERROR);
    }
  }

  public void addTechnicalError(Throwable t) {
    setStatus(ResultV1RDTO.ResultStatus.ERROR);
    ErrorV1RDTO errorRDTO = new ErrorV1RDTO();
    // t.printStackTrace();
    errorRDTO.setErrorTechnicalInformation(t.toString());
    errorRDTO.setErrorCode(ErrorV1RDTO.ErrorCode.ERROR);
    addError(errorRDTO);
  }

  public boolean hasErrors() {
    return _errors != null && !_errors.isEmpty();
  }

  public boolean hasErrors(ErrorV1RDTO.ErrorCode errorType) {
    boolean result = false;
    for (ErrorV1RDTO errorV1RDTO : getErrors()) {
      result = result || errorV1RDTO.getErrorCode().equals(errorType);
    }
    return result;
  }

  public GenericSearchParamsV1RDTO getParams() {
    return _params;
  }

  public void setParams(GenericSearchParamsV1RDTO _params) {
    this._params = _params;
  }

  public Map<String, Boolean> getAccessRights() {
    return _accessRights;
  }

  public void setAccessRights(Map<String, Boolean> accessRights) {
    if (accessRights == null) {
      accessRights = new HashMap<String, Boolean>();
    }
    this._accessRights = accessRights;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getSimpleName());
    sb.append("[status=");
    sb.append(getStatus() != null ? getStatus().name() : "null");
    sb.append(", errors=[");
    if (hasErrors()) {
      for (ErrorV1RDTO errorV1RDTO : _errors) {
        sb.append(errorV1RDTO.toString());
        sb.append(", ");
      }
    }
    sb.append("], result=");
    sb.append(getResult() != null ? getResult().toString() : "null");
    sb.append("]");
    return sb.toString();
  }

  public static <T> ResultV1RDTO<T> create(ResultStatus status, T t, ErrorV1RDTO error) {
    ResultV1RDTO<T> r = new ResultV1RDTO<T>(t);
    r.addError(error);
    return r;
  }

  public static <T> ResultV1RDTO<T> notFound() {
    ResultV1RDTO<T> r = new ResultV1RDTO<>();
    r.setStatus(ResultStatus.NOT_FOUND);
    return r;
  }
}
