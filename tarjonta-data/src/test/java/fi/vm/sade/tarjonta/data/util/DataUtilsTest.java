package fi.vm.sade.tarjonta.data.util;

import junit.framework.Assert;
import org.junit.Test;

public class DataUtilsTest {
    @Test
    public void testGetKoodistoUri() {
        Assert.assertEquals("kuntaryhma", DataUtils.createKoodistoUriFromName("KUNTARYHMÄ"));
    }
}
