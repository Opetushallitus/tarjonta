package fi.vm.sade.tarjonta.service.resources.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KelaHakukohdeDTO implements Serializable {
    private String oid;
    private String tarjoajaOid;
    private Map<String, String> nimi = new HashMap<String, String>();
    private TarjontaTila tila;

    public KelaHakukohdeDTO(String oid, String tarjoajaOid, Map<String, String> nimi, TarjontaTila tila) {
        this.oid = oid;
        this.tarjoajaOid = tarjoajaOid;
        this.nimi = nimi;
        this.tila = tila;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getTarjoajaOid() {
        return tarjoajaOid;
    }

    public void setTarjoajaOid(String tarjoajaOid) {
        this.tarjoajaOid = tarjoajaOid;
    }

    public TarjontaTila getTila() {
        return tila;
    }

    public void setTila(TarjontaTila tila) {
        this.tila = tila;
    }

    public Map<String, String> getNimi() {
        return nimi;
    }

    public String getNimiLocale(String locale) {
        return nimi.get(locale);
    }


    public void setNimi(Map<String, String> nimi) {
        this.nimi = nimi;
    }


}
