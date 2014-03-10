package fi.vm.sade.tarjonta.ui.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;

@Service
public class OidHelper {
    
    @Autowired
    TarjontaAdminService adminService;
    
    public static class OidContainer {
        private String oid;

        public String getOid() {
            return oid;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }
    }

    public String getOid(TarjontaOidType type) throws OidCreationException {
        return adminService.haeOid(type.toString());
    }
}
