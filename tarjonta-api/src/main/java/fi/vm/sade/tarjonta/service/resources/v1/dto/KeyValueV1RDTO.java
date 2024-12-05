package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;
import java.util.HashMap;

public class KeyValueV1RDTO extends HashMap<String, Serializable> {

  public KeyValueV1RDTO() {}

  public KeyValueV1RDTO(String key, Serializable value) {
    this();
    put(key, value);
  }
}
