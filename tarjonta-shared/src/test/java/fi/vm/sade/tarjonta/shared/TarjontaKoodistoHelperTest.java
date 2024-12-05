package fi.vm.sade.tarjonta.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TarjontaKoodistoHelperTest {

  @BeforeEach
  public void setUp() {}

  /** Test of convertKieliUriToKielikoodi method, of class TarjontaKoodistoHelper. */
  @Test
  public void testConvertKieliUriToKielikoodi() {
    assertEquals("en", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("kieli_en"));
    assertEquals("fi", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("kieli_fi"));
    assertEquals("sv", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("kieli_sv"));
    assertEquals("sv", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("KIELI_SV"));
    assertEquals("ee", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("KIELI_EE"));
  }

  @Test
  public void testConvertKieliUriToKielikoodiWithVersion() {
    assertEquals("en", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("kieli_en#1"));
    assertEquals("fi", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("kieli_fi#123"));
    assertEquals("ee", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("KIELI_EE#123"));
  }

  @Test()
  public void testConvertKieliUriToKielikoodiException1() {
    RuntimeException runtimeException =
        assertThrows(
            RuntimeException.class,
            () -> TarjontaKoodistoHelper.convertKieliUriToKielikoodi("kieli_e"));
  }

  @Test()
  public void testConvertKieliUriToKielikoodiException2() {
    RuntimeException runtimeException =
        assertThrows(
            RuntimeException.class,
            () -> TarjontaKoodistoHelper.convertKieliUriToKielikoodi("ILEIK_SV"));
  }

  @Test()
  public void testConvertKieliUriToKielikoodiException3() {
    RuntimeException runtimeException =
        assertThrows(
            RuntimeException.class,
            () -> TarjontaKoodistoHelper.convertKieliUriToKielikoodi("ILEIK_SV#"));
  }

  @Test
  public void testKoodistoUri_splitKoodi_and_hasVersion() {

    String source = null;
    String target = null;

    // Test null split
    assertEquals(
        "", KoodistoURI.splitKoodiToKoodiAndVersion(source)[0], "Null should split to '' and ''.");
    assertEquals(
        "", KoodistoURI.splitKoodiToKoodiAndVersion(source)[1], "Null should split to '' and ''.");

    // Test no version split
    source = "kieli_fi";
    assertFalse(KoodistoURI.koodiHasVersion(source), "Has no version");
    assertEquals(
        "kieli_fi", KoodistoURI.splitKoodiToKoodiAndVersion(source)[0], "No version - koodi");
    assertEquals("", KoodistoURI.splitKoodiToKoodiAndVersion(source)[1], "No version - version");

    // Test version split
    source = "kieli_fi#123";
    assertTrue(KoodistoURI.koodiHasVersion(source), "Has version");
    assertEquals("kieli_fi", KoodistoURI.splitKoodiToKoodiAndVersion(source)[0], "Version - koodi");
    assertEquals("123", KoodistoURI.splitKoodiToKoodiAndVersion(source)[1], "Version - version");
  }

  @Test
  public void testKoodistoUri_compare_versions() {

    Object[][] testData = {
      {null, null, true, true},
      {"", null, false, false},
      {null, "", false, false},
      {null, "kieli_fi", false, false},
      {null, "kieli_fi#1", false, false},
      {null, "kieli_fi#2", false, false},
      {"kieli_fi", null, false, false},
      {"kieli_fi#1", null, false, false},
      {"kieli_fi#2", null, false, false},
      {"kieli_fi", "kieli_fi", true, true},
      {"kieli_fi", "kieli_fi#1", true, true},
      {"kieli_fi", "kieli_fi#1234", true, true},
      {"kieli_fi", "hakutapa_03", false, false},
      {"kieli_fi", "hakutapa_03#1", false, false},
      {"kieli_fi#1", "kieli_fi", false, true},
      {"kieli_fi#1", "kieli_fi#1", true, true},
      {"kieli_fi#1", "kieli_fi#1234", false, true},
      {"kieli_fi", "kieli_sv", false, false},
      {"kieli_fi", "kieli_sv#1", false, false},
      {"kieli_fi", "hakutapa_03", false, false},
      {"kieli_fi", "hakutapa_03#1", false, false},
      {"kieli_fi#1", "kieli_sv", false, false},
      {"kieli_fi#1", "kieli_sv#1", false, false},
      {"kieli_fi#1", "hakutapa_03", false, false},
      {"kieli_fi#1", "hakutapa_03#112", false, false},
    };

    for (Object[] testRow : testData) {
      String source = (String) testRow[0];
      String target = (String) testRow[1];
      boolean expectedResultVersions = (Boolean) testRow[2];
      boolean expectedResultNoVersions = (Boolean) testRow[3];

      if (expectedResultVersions) {
        assertTrue(KoodistoURI.compareKoodi(source, target), source + " == " + target);
      } else {
        assertFalse(KoodistoURI.compareKoodi(source, target), source + " != " + target);
      }

      if (expectedResultNoVersions) {
        assertTrue(
            KoodistoURI.compareKoodi(source, target, true),
            source + " == " + target + " (ignore versions)");
      } else {
        assertFalse(
            KoodistoURI.compareKoodi(source, target, true),
            source + " != " + target + " (ignore versions)");
      }
    }
  }
}
