package fi.vm.sade.tarjonta.shared.types;

public enum TarjontaOidType {
  KOMO(13),
  KOMOTO(17),
  HAKUKOHDE(20),
  HAKU(29);

  private final int value;

  public int getValue() {
    return value;
  }

  private TarjontaOidType(int value) {
    this.value = value;
  }
}
