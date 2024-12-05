package fi.vm.sade.tarjonta.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

public class KoulutusUsedException extends SadeBusinessException {

  private String errorKey = KoulutusUsedException.class.getCanonicalName();

  public KoulutusUsedException() {
    super();
  }

  @Override
  public String getErrorKey() {
    return errorKey;
  }
}
