package fi.vm.sade.tarjonta.service.impl.resources;

import java.util.EnumMap;
import java.util.Map;

import fi.vm.sade.tarjonta.service.resources.TilaResource;
import fi.vm.sade.tarjonta.service.resources.dto.TilaRDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class TilaResourceImpl implements TilaResource {
	
	private final Map<TarjontaTila, TilaRDTO> tilat;

	public TilaResourceImpl() {
		tilat = new EnumMap<TarjontaTila, TilaRDTO>(TarjontaTila.class);
		for (TarjontaTila tila : TarjontaTila.values()) {
			TilaRDTO dto = new TilaRDTO();
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
	public Map<TarjontaTila, TilaRDTO> getTilat() {
		return tilat;
	}

}