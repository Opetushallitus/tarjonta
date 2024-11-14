package fi.vm.sade.tarjonta.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/** Exception class for situations where referenced Koulutusmoduuli does not exist. */
public class TarjontaBusinessException extends SadeBusinessException {

  private static final long serialVersionUID = 1939093851983854854L;

  private String errorKey = TarjontaBusinessException.class.getCanonicalName();

  public TarjontaBusinessException() {
    super();
  }

  public TarjontaBusinessException(String key, String message, Throwable cause) {
    super(message, cause);
    errorKey = key;
  }

  public TarjontaBusinessException(String key) {
    this(key, null, null);
  }

  public TarjontaBusinessException(String key, String message) {
    this(key, message, null);
  }

  public TarjontaBusinessException(String key, Throwable cause) {
    this(key, null, cause);
  }

  @Override
  public String getErrorKey() {
    return errorKey;
  }
}
