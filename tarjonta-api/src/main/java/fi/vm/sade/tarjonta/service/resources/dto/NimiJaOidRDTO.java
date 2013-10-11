package fi.vm.sade.tarjonta.service.resources.dto;

import java.io.Serializable;
import java.util.Map;

public class NimiJaOidRDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Map<String,String> nimi;
	private String oid;
	
	public NimiJaOidRDTO() {
	}
	
	public NimiJaOidRDTO(Map<String, String> nimi, String oid) {
		super();
		this.nimi = nimi;
		this.oid = oid;
	}

	public Map<String, String> getNimi() {
		return nimi;
	}
	
	public void setNimi(Map<String, String> nimi) {
		this.nimi = nimi;
	}

	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	
}
