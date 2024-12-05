package fi.vm.sade.tarjonta.service.types;

public enum HenkiloTyyppi {
  ECTS_KOORDINAATTORI("EctsKoordinaattori"),
  YHTEYSHENKILO("Yhteyshenkilo");
  private final String value;

  HenkiloTyyppi(String v) {
    value = v;
  }

  public String value() {
    return value;
  }

  public static HenkiloTyyppi fromValue(String v) {
    for (HenkiloTyyppi c : HenkiloTyyppi.values()) {
      if (c.value.equals(v)) {
        return c;
      }
    }
    throw new IllegalArgumentException(v);
  }
}
