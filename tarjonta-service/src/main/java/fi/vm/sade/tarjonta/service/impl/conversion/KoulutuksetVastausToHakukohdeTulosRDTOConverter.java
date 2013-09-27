package fi.vm.sade.tarjonta.service.impl.conversion;

import java.util.HashMap;
import java.util.Map;

import fi.vm.sade.tarjonta.service.resources.dto.HakutuloksetRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KoulutusHakutulosRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TarjoajaHakutulosRDTO;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus.KoulutusTulos;
import fi.vm.sade.tarjonta.service.types.KoulutusListausTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class KoulutuksetVastausToHakukohdeTulosRDTOConverter extends BaseRDTOConverter<KoulutuksetVastaus, HakutuloksetRDTO<KoulutusHakutulosRDTO>> {

	@Override
	public HakutuloksetRDTO<KoulutusHakutulosRDTO> convert(KoulutuksetVastaus source) {
		HakutuloksetRDTO<KoulutusHakutulosRDTO> ret = new HakutuloksetRDTO<KoulutusHakutulosRDTO>();

		Map<String, TarjoajaHakutulosRDTO<KoulutusHakutulosRDTO>> tarjoajat = new HashMap<String, TarjoajaHakutulosRDTO<KoulutusHakutulosRDTO>>();
		
		for (KoulutusTulos ht : source.getKoulutusTulos()) {
			TarjoajaHakutulosRDTO<KoulutusHakutulosRDTO> rets = getTarjoaja(ret, tarjoajat, ht);
			rets.getTulokset().add(convert(ht.getKoulutus()));
		}
		
		ret.setTuloksia(source.getKoulutusTulos().size());
		
		return ret;
	}
	
	private KoulutusHakutulosRDTO convert(KoulutusListausTyyppi ht) {
		KoulutusHakutulosRDTO ret = new KoulutusHakutulosRDTO();

		ret.setNimi(convertToMap(ht.getNimi()));
		ret.setKausiUri(ht.getKoulutuksenAlkamiskausiUri());
		ret.setVuosi(ht.getKoulutuksenAlkamisVuosi());
		//ret.setKoulutusLaji(convertToMap(ht.getKoulutuslaji()));
		ret.setTila(TarjontaTila.valueOf(ht.getTila()));

		return ret;
	}
	
	private TarjoajaHakutulosRDTO<KoulutusHakutulosRDTO> getTarjoaja(
			HakutuloksetRDTO<KoulutusHakutulosRDTO> tulos,
			Map<String, TarjoajaHakutulosRDTO<KoulutusHakutulosRDTO>> tarjoajat,
			KoulutusTulos ht) {
		TarjoajaHakutulosRDTO<KoulutusHakutulosRDTO> ret = tarjoajat.get(ht.getKoulutus().getTarjoaja().getTarjoajaOid());
		if (ret==null) {
			ret = new TarjoajaHakutulosRDTO<KoulutusHakutulosRDTO>();
			ret.setOid(ht.getKoulutus().getTarjoaja().getTarjoajaOid());
			ret.setNimi(convertToMap(ht.getKoulutus().getTarjoaja().getNimi()));
			tulos.getTulokset().add(ret);
		}
		return ret;
	}
	
	
}
