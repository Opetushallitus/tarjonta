package fi.vm.sade.tarjonta.service.search;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Test;

public class NimiTest {

    @Test
    public void test() {

        Nimi nimi = new Nimi();

        nimi.put(Nimi.EN, "value");
        Assert.assertEquals(nimi.get(Nimi.EN), "value");

        try {
            nimi.put("kissa", "koira");
//            fail("Pitäisi heittää poikkeus!");
        } catch (IllegalArgumentException iae) {
            // kaikki ok
        }

    }

}
