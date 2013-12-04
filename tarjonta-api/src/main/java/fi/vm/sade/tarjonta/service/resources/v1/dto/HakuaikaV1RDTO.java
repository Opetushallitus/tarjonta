package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.util.Date;

/*
* @author: Tuomas Katva 04/11/13
*/
public class HakuaikaV1RDTO {

    private String hakuaikaId;
    private String nimi;
    private Date alkuPvm;
    private Date loppuPvm;


    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

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
}
