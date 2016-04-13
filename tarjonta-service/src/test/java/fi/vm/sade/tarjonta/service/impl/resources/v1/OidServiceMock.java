package fi.vm.sade.tarjonta.service.impl.resources.v1;

import org.springframework.stereotype.Service;

@Service
public class OidServiceMock {

    private int oidCounter = 0;

    public String getOid() {
        oidCounter ++;
        return "1.2.3.4." + Integer.toString(oidCounter);
    }

}