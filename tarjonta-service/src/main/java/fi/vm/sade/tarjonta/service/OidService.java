package fi.vm.sade.tarjonta.service;

import org.springframework.beans.factory.annotation.Autowired;

import fi.vm.sade.oid.generator.OIDGenerationException;
import fi.vm.sade.oid.generator.OIDGenerator;

/**
 * Wrapper for oid service api.
 */
public class OidService {

    public static enum Type {
        KOMO(13), KOMOTO(17), HAKUKOHDE(20), HAKU(29);

        private String value;

        Type(int value) {
            this.value = Integer.toString(value);
        }
    }

    @Autowired
    private OIDGenerator oidGenerator;

    public String get(Type type) throws OIDCreationException {
        try {
            return oidGenerator.generateOID(type.value);
        } catch (OIDGenerationException e) {
            throw new OIDCreationException();
        }
    }

}
