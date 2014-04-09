package fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.base.Objects;

import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;

public class KoulutusValidatorTest {

    @Test
    public void testTunniste() {
        
        KoulutusKorkeakouluV1RDTO kk = new KoulutusKorkeakouluV1RDTO();
        kk.setTunniste("123456789012345678901234567890123456");
        List<ErrorV1RDTO> errors = KoulutusValidator.validateKoulutus(kk);
        assertErrorExist(errors, "koulutus_tunniste_length");
        kk.setTunniste("12345678901234567890123456789012345");
        errors = KoulutusValidator.validateKoulutus(kk);

        assertErrorDoesNotExist(errors, "koulutus_tunniste_length");
    }

    private void assertErrorDoesNotExist(List<ErrorV1RDTO> errors, String em) {
        for(ErrorV1RDTO error: errors) {
            if(Objects.equal(em,  error.getErrorMessageKey())){
                Assert.fail("Error was present when it should not have been:'" + em + "'");
            } 
        }
    }

    private void assertErrorExist(List<ErrorV1RDTO> errors, String em) {
        for(ErrorV1RDTO error: errors) {
            if(Objects.equal(em,  error.getErrorMessageKey())){
                return;
            } 
        }
        Assert.fail("Could not find error code '" + em + "' in errors:" + errors);
    }

}
