package fi.vm.sade.tarjonta.service.impl.conversion;

import java.util.HashMap;
import java.util.Map;

import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeHakutulosRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakutuloksetRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TarjoajaHakutulosRDTO;
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi.Nimi;
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

		ret.setNimi(convertToMap(ht.getNimi()));
		ret.setKausiUri(ht.getKoulutuksenAlkamiskausiUri());
		ret.setVuosi(Integer.parseInt(ht.getKoulutuksenAlkamisvuosi()));
		
		Map<String,String> htps = new HashMap<String, String>();
		ret.setHakutapa(htps);
		for (Nimi n : ht.getHakutapaKoodi().getNimi()) {
			htps.put(n.getKieli(), n.getValue());
		}
		
		ret.setAloituspaikat(Integer.valueOf(ht.getAloituspaikat()));
		ret.setKoulutusLaji(convertToMap(ht.getHakukohteenKoulutuslaji()));
		ret.setTila(TarjontaTila.valueOf(ht.getTila()));

		return ret;
	}
	
	private TarjoajaHakutulosRDTO<HakukohdeHakutulosRDTO> getTarjoaja(
			HakutuloksetRDTO<HakukohdeHakutulosRDTO> tulos,
			Map<String, TarjoajaHakutulosRDTO<HakukohdeHakutulosRDTO>> tarjoajat,
			HakukohdePerustieto ht) {
		TarjoajaHakutulosRDTO<HakukohdeHakutulosRDTO> ret = tarjoajat.get(ht.getTarjoaja().getTarjoajaOid());
		if (ret==null) {
			ret = new TarjoajaHakutulosRDTO<HakukohdeHakutulosRDTO>();
			tarjoajat.put(ht.getTarjoaja().getTarjoajaOid(), ret);
			ret.setOid(ht.getTarjoaja().getTarjoajaOid());
			ret.setNimi(convertToMap(ht.getTarjoaja().getNimi()));
			tulos.getTulokset().add(ret);
		}
		return ret;
	}
	
	
}
