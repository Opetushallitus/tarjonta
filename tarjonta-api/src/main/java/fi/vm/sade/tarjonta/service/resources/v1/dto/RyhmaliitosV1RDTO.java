package fi.vm.sade.tarjonta.service.resources.v1.dto;

import com.wordnik.swagger.annotations.ApiModel;

import java.io.Serializable;

@ApiModel(value = "Rajapintaolio organisaatioryhmien hallintaan")
public class RyhmaliitosV1RDTO implements Serializable {

    private String ryhmaOid;
    private String hakukohdeOid;
    private Integer prioriteetti;

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }

    public Integer getPrioriteetti() {
        return prioriteetti;
    }

    public void setPrioriteetti(Integer prioriteetti) {
        this.prioriteetti = prioriteetti;
    }

    public String getRyhmaOid() {
        return ryhmaOid;
    }

    public void setRyhmaOid(String ryhmaOid) {
        this.ryhmaOid = ryhmaOid;
    }
}
