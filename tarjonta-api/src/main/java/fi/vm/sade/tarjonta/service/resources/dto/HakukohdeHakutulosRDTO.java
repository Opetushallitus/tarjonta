package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.Map;


public class HakukohdeHakutulosRDTO extends KoulutusHakutulosRDTO {

	private static final long serialVersionUID = 1L;

	private Map<String, String> hakutapa;
	private Integer aloituspaikat;
	
	public Map<String, String> getHakutapa() {
		return hakutapa;
	}

	public void setHakutapa(Map<String, String> hakutapa) {
		this.hakutapa = hakutapa;
	}
	
	public Integer getAloituspaikat() {
		return aloituspaikat;
	}
	
	public void setAloituspaikat(Integer aloituspaikat) {
		this.aloituspaikat = aloituspaikat;
	}

}