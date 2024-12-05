package fi.vm.sade.tarjonta.shared.types;

import java.util.ArrayList;
import java.util.List;

public enum TarjontaTila {
  POISTETTU,
  LUONNOS,
  VALMIS,
  JULKAISTU,
  PERUTTU,
  KOPIOITU,
  PUUTTEELLINEN;

  private static final TarjontaTila[] CANCELLABLE_DATA;

  private static final TarjontaTila[] PUBLIC_DATA;

  static {
    List<TarjontaTila> cd = new ArrayList<TarjontaTila>();
    List<TarjontaTila> pd = new ArrayList<TarjontaTila>();

    for (TarjontaTila tt : values()) {
      if (tt.isCancellable()) {
        cd.add(tt);
      }
      if (tt.isPublic()) {
        pd.add(tt);
      }
    }

    CANCELLABLE_DATA = cd.toArray(new TarjontaTila[cd.size()]);
    PUBLIC_DATA = pd.toArray(new TarjontaTila[pd.size()]);
  }

  public boolean acceptsTransitionTo(TarjontaTila tt) {
    if (tt == this) {
      return true;
    }
    switch (tt) {
      case VALMIS:
        return this == LUONNOS || this == KOPIOITU || this == PERUTTU || this == PUUTTEELLINEN;
      case PERUTTU:
        return this == JULKAISTU;
      case JULKAISTU:
        return this == VALMIS || this == PERUTTU;
      case POISTETTU:
        return isRemovable();
      case LUONNOS:
        return this == KOPIOITU || this == PUUTTEELLINEN;
      default:
        return false;
    }
  }

  @Deprecated // muokattavuuslogiikka ei ole tilasidonnaista
  public boolean isMutable() {
    return true; // this==LUONNOS || this==KOPIOITU;
  }

  // muokattu OVT-8135 mukaisesti
  public boolean isRemovable() {
    return this == LUONNOS || this == KOPIOITU || this == VALMIS || this == PUUTTEELLINEN;
  }

  public boolean isCancellable() {
    return this == JULKAISTU || this == VALMIS;
  }

  public boolean isPublic() {
    return this == JULKAISTU || this == PERUTTU;
  }

  public static TarjontaTila[] cancellableValues() {
    return CANCELLABLE_DATA;
  }

  public static TarjontaTila[] publicValues() {
    return PUBLIC_DATA;
  }

  public static TarjontaTila valueOf(fi.vm.sade.tarjonta.service.types.TarjontaTila tt) {
    return valueOf(tt.toString());
  }

  public fi.vm.sade.tarjonta.service.types.TarjontaTila asDto() {
    return fi.vm.sade.tarjonta.service.types.TarjontaTila.valueOf(toString());
  }
}
