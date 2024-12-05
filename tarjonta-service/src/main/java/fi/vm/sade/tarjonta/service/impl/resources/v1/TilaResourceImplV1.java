package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.tarjonta.service.resources.v1.TilaV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TilaV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.EnumMap;
import java.util.Map;

public class TilaResourceImplV1 implements TilaV1Resource {

  private final Map<TarjontaTila, TilaV1RDTO> tilat;

  public TilaResourceImplV1() {
    tilat = new EnumMap<TarjontaTila, TilaV1RDTO>(TarjontaTila.class);
    for (TarjontaTila tila : TarjontaTila.values()) {
      TilaV1RDTO dto = new TilaV1RDTO();
      dto.setCancellable(tila.isCancellable());
      dto.setMutable(tila.isMutable());
      dto.setPublic(tila.isPublic());
      dto.setRemovable(tila.isRemovable());

      for (TarjontaTila next : TarjontaTila.values()) {
        if (tila.acceptsTransitionTo(next)) {
          dto.getTransitions().add(next);
        }
      }

      tilat.put(tila, dto);
    }
  }

  @Override
  public ResultV1RDTO<Map<TarjontaTila, TilaV1RDTO>> getTilat() {
    return new ResultV1RDTO<Map<TarjontaTila, TilaV1RDTO>>(tilat);
  }
}
