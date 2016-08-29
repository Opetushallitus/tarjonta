package fi.vm.sade.tarjonta.service.impl.resources.v1;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OidServiceMock {

    public String getOid() {
        return UUID.randomUUID().toString();
    }

}