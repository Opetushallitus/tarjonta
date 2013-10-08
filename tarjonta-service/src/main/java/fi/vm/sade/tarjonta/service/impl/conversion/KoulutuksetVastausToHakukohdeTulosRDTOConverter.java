package fi.vm.sade.tarjonta.service.impl.conversion;

import java.util.HashMap;
import java.util.Map;

import fi.vm.sade.tarjonta.service.resources.dto.HakutuloksetRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KoulutusHakutulosRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TarjoajaHakutulosRDTO;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class KoulutuksetVastausToHakukohdeTulosRDTOConverter extends BaseRDTOConverter<KoulutuksetVastaus, HakutuloksetRDTO<KoulutusHakutulosRDTO>> {

	@Override
	public HakutuloksetRDTO<KoulutusHakutulosRDTO> convert(KoulutuksetVastaus source) {
		HakutuloksetRDTO<KoulutusHakutulosRDTO> ret = new HakutuloksetRDTO<KoulutusHakutulosRDTO>();

		Map<String, TarjoajaHakutulosRDTO<KoulutusHakutulosRDTO>> tarjoajat = new HashMap<String, TarjoajaHakutulosRDTO<KoulutusHakutulosRDTO>>();
		
		for (KoulutusPerustieto ht : source.getKoulutukset()) {
			TarjoajaHakutulosRDTO<KoulutusHakutulosRDTO> rets = getTarjoaja(ret, tarjoajat, ht);
			rets.getTulokset().add(convert(ht));
		}
		
		//XXX use getHitCount when available
		ret.setTuloksia(source.getKoulutukset().size());
		
		return ret;
	}
	
	private KoulutusHakutulosRDTO convert(KoulutusPerustieto ht) {
		KoulutusHakutulosRDTO ret = new KoulutusHakutulosRDTO();

		ret.setOid(ht.getKomotoOid());
		ret.setNimi(ht.getNimi());
		ret.setKausi(ht.getKoulutuksenAlkamiskausi().getNimi());
		ret.setVuosi(ht.getKoulutuksenAlkamisVuosi());
		if(ht.getKoulutuslaji()!=null) {
		    ret.setKoulutuslaji(ht.getKoulutuslaji().getNimi());
		}
		ret.setTila(TarjontaTila.valueOf(ht.getTila()));

		return ret;
	}
	
	private TarjoajaHakutulosRDTO<KoulutusHakutulosRDTO> getTarjoaja(
			HakutuloksetRDTO<KoulutusHakutulosRDTO> tulos,
			Map<String, TarjoajaHakutulosRDTO<KoulutusHakutulosRDTO>> tarjoajat,
			KoulutusPerustieto ht) {
		TarjoajaHakutulosRDTO<KoulutusHakutulosRDTO> ret = tarjoajat.get(ht.getTarjoaja().getTarjoajaOid());
		if (ret==null) {
			ret = new TarjoajaHakutulosRDTO<KoulutusHakutulosRDTO>();
			tarjoajat.put(ht.getTarjoaja().getTarjoajaOid(), ret);
			ret.setOid(ht.getTarjoaja().getTarjoajaOid());
			ret.setNimi(convertToMap(ht.getTarjoaja().getNimi()));
			tulos.getTulokset().add(ret);
		}
		return ret;
	}
	
	
}
