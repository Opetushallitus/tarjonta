package fi.vm.sade.tarjonta.service;

import org.springframework.beans.factory.annotation.Autowired;

import fi.vm.sade.oid.generator.OIDGenerationException;
import fi.vm.sade.oid.generator.OIDGenerator;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;

/**
 * Wrapper for oid service api.
 */
public class OidService {

    @Autowired
    private OIDGenerator oidGenerator;

    public String get(TarjontaOidType type) throws OIDCreationException {
        try {
            return oidGenerator.generateOID(type.getValue());
        } catch (OIDGenerationException e) {
            throw new OIDCreationException();
        }
    }

}
