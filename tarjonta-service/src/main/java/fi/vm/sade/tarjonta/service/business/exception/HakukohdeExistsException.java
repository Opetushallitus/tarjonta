package fi.vm.sade.tarjonta.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/*
 * @author: Tuomas Katva 07/02/14
 */
public class HakukohdeExistsException extends SadeBusinessException {

  private final String errorKey = HakukohdeExistsException.class.getCanonicalName();

  private String additionalDetails;

  public HakukohdeExistsException() {
    super();
  }

  public HakukohdeExistsException(String addlDetails) {
    super();
    this.additionalDetails = addlDetails;
  }

  @Override
  public String getErrorKey() {
    return errorKey;
  }
}
