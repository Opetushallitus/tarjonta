package fi.vm.sade.tarjonta.service.impl.conversion;

import java.util.HashMap;
import java.util.Map;

import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeHakutulosRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakutuloksetRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TarjoajaHakutulosRDTO;
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class HakukohteetVastausToHakukohdeTulosRDTOConverter extends BaseRDTOConverter<HakukohteetVastaus, HakutuloksetRDTO<HakukohdeHakutulosRDTO>> {

	@Override
	public HakutuloksetRDTO<HakukohdeHakutulosRDTO> convert(HakukohteetVastaus source) {
		HakutuloksetRDTO<HakukohdeHakutulosRDTO> ret = new HakutuloksetRDTO<HakukohdeHakutulosRDTO>();

		Map<String, TarjoajaHakutulosRDTO<HakukohdeHakutulosRDTO>> tarjoajat = new HashMap<String, TarjoajaHakutulosRDTO<HakukohdeHakutulosRDTO>>();
		
		for (HakukohdePerustieto ht : source.getHakukohteet()) {
			TarjoajaHakutulosRDTO<HakukohdeHakutulosRDTO> rets = getTarjoaja(ret, tarjoajat, ht);
			rets.getTulokset().add(convert(ht));
		}
		
		//XX use hitCount when implemented
		ret.setTuloksia(source.getHakukohteet().size());
		
		return ret;
	}
	
	private HakukohdeHakutulosRDTO convert(HakukohdePerustieto ht) {
		HakukohdeHakutulosRDTO ret = new HakukohdeHakutulosRDTO();

		ret.setOid(ht.getOid());
		ret.setNimi(ht.getNimi());
		ret.setKausiUri(ht.getKoulutuksenAlkamiskausiUri());
		ret.setVuosi(ht.getKoulutuksenAlkamisvuosi());
		ret.setHakutapa(ht.getHakutapaNimi());
		ret.setAloituspaikat(Integer.valueOf(ht.getAloituspaikat()));
		ret.setKoulutusLaji(ht.getKoulutuslajiNimi());
		ret.setTila(TarjontaTila.valueOf(ht.getTila()));

		return ret;
	}
	
	private TarjoajaHakutulosRDTO<HakukohdeHakutulosRDTO> getTarjoaja(
			HakutuloksetRDTO<HakukohdeHakutulosRDTO> tulos,
			Map<String, TarjoajaHakutulosRDTO<HakukohdeHakutulosRDTO>> tarjoajat,
			HakukohdePerustieto ht) {
		TarjoajaHakutulosRDTO<HakukohdeHakutulosRDTO> ret = tarjoajat.get(ht.getTarjoajaOid());
		if (ret==null) {
			ret = new TarjoajaHakutulosRDTO<HakukohdeHakutulosRDTO>();
			tarjoajat.put(ht.getTarjoajaOid(), ret);
			ret.setOid(ht.getTarjoajaOid());
			ret.setNimi(ht.getTarjoajaNimi());
			tulos.getTulokset().add(ret);
		}
		return ret;
	}
	
	
}
