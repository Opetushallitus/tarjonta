package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.util.HashMap;
import java.util.Map;



public class HakukohdeHakutulosV1RDTO extends KoulutusHakutulosV1RDTO {

	private static final long serialVersionUID = 1L;

	private Map<String, String> hakutapa;
    private Map<String, String> aloituspaikatKuvaukset = new HashMap<String, String>();
	private Integer aloituspaikat;
	private String hakuOid;

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

    public String getHakuOid() {
        return hakuOid;
    }

    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }

    public Map<String, String> getAloituspaikatKuvaukset() {
        return aloituspaikatKuvaukset;
    }

    public void setAloituspaikatKuvaukset(Map<String, String> aloituspaikatKuvaukset) {
        this.aloituspaikatKuvaukset = aloituspaikatKuvaukset;
    }
}