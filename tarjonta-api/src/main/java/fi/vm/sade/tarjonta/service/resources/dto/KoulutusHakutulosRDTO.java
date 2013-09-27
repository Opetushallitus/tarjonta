package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.Map;

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class KoulutusHakutulosRDTO extends BaseRDTO {

	private static final long serialVersionUID = 1L;

	private String oid;
	
	private Map<String, String> nimi;
	
	private String kausiUri;
	private Integer vuosi;
	
	private Map<String, String> koulutusLaji;
	
	private TarjontaTila tila;

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public Map<String, String> getNimi() {
		return nimi;
	}

	public void setNimi(Map<String, String> nimi) {
		this.nimi = nimi;
	}

	public String getKausiUri() {
		return kausiUri;
	}

	public void setKausiUri(String kausiUri) {
		this.kausiUri = kausiUri;
	}

	public Integer getVuosi() {
		return vuosi;
	}

	public void setVuosi(Integer vuosi) {
		this.vuosi = vuosi;
	}
	
	public Map<String, String> getKoulutusLaji() {
		return koulutusLaji;
	}
	
	public void setKoulutusLaji(Map<String, String> koulutusLaji) {
		this.koulutusLaji = koulutusLaji;
	}

	public TarjontaTila getTila() {
		return tila;
	}

	public void setTila(TarjontaTila tila) {
		this.tila = tila;
	}

}