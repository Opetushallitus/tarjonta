package fi.vm.sade.tarjonta.publication;

import com.google.common.base.Function;
import fi.vm.sade.tarjonta.publication.Tila.Tyyppi;
import fi.vm.sade.tarjonta.service.types.GeneerinenTilaTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import javax.annotation.Nullable;

public class GeneerinenTilaTyyppiToTilaFunction implements Function<GeneerinenTilaTyyppi, Tila> {

  @Override
  @Nullable
  public Tila apply(@Nullable GeneerinenTilaTyyppi input) {
    if (input == null) {
      return null;
    }
    Tila output =
        new Tila(
            Tyyppi.valueOf(input.getSisalto().name()),
            TarjontaTila.valueOf(input.getTila()),
            input.getOid());
    return output;
  }
}
