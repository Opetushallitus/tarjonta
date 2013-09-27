package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.ArrayList;
import java.util.List;


public class HakutuloksetRDTO<T extends KoulutusHakutulosRDTO> extends BaseRDTO {

	private static final long serialVersionUID = 1L;
	
	private List<TarjoajaHakutulosRDTO<T>> tulokset;
	private int tuloksia;
	
	public List<TarjoajaHakutulosRDTO<T>> getTulokset() {
		if (tulokset==null) {
			tulokset = new ArrayList<TarjoajaHakutulosRDTO<T>>();
		}
		return tulokset;
	}
	
	public void setTulokset(List<TarjoajaHakutulosRDTO<T>> tulokset) {
		this.tulokset = tulokset;
	}
	
	public int getTuloksia() {
		return tuloksia;
	}
	
	public void setTuloksia(int tuloksia) {
		this.tuloksia = tuloksia;
	}

}
