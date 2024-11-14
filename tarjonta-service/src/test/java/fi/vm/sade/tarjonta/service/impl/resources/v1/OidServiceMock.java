package fi.vm.sade.tarjonta.service.impl.resources.v1;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OidServiceMock {

  public String getOid() {
    return UUID.randomUUID().toString();
  }
}
