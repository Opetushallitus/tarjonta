package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HakuaikaV1RDTO {

    private String hakuaikaId;
    private Date alkuPvm;
    private Date loppuPvm;
    private Map<String,String> nimet = new HashMap<String, String>();

    public Date getAlkuPvm() {
        return alkuPvm;
    }

    public void setAlkuPvm(Date alkuPvm) {
        this.alkuPvm = alkuPvm;
    }

    public Date getLoppuPvm() {
        return loppuPvm;
    }

    public void setLoppuPvm(Date loppuPvm) {
        this.loppuPvm = loppuPvm;
    }

    public String getHakuaikaId() {
        return hakuaikaId;
    }

    public void setHakuaikaId(String hakuaikaId) {
        this.hakuaikaId = hakuaikaId;
    }

    public Map<String, String> getNimet() {
        return nimet;
    }

    public void setNimet(Map<String, String> nimet) {
        this.nimet = nimet;
    }
}
