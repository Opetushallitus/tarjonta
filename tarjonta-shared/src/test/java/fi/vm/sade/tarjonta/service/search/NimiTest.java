package fi.vm.sade.tarjonta.service.search;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NimiTest {

  @Test
  public void test() {

    Nimi nimi = new Nimi();

    nimi.put(Nimi.EN, "value");
    assertEquals(nimi.get(Nimi.EN), "value");

    try {
      nimi.put("kissa", "koira");
      //            fail("Pitäisi heittää poikkeus!");
    } catch (IllegalArgumentException iae) {
      // kaikki ok
    }
  }
}
