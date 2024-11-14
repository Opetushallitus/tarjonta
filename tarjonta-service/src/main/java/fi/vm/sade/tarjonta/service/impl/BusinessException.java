/*
 *
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

package fi.vm.sade.tarjonta.service.impl;

/**
 * @author Tuomas Katva
 */
public class BusinessException extends RuntimeException {

  private String key;

  public BusinessException(String key) {
    super(key);
    this.key = key;
  }

  public BusinessException(String message, Throwable cause) {
    super(message, cause);
  }

  public String getKey() {
    return key;
  }

  public BusinessException(Throwable cause) {
    super(cause);
  }
}
