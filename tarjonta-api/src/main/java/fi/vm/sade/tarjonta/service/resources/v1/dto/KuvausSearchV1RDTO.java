package fi.vm.sade.tarjonta.service.resources.v1.dto;

import com.wordnik.swagger.annotations.ApiModelProperty;

/*
* @author: Tuomas Katva 03/01/14
*/
public class KuvausSearchV1RDTO {
    @ApiModelProperty(value = "Name or part of the description name")
    private String hakusana;

    @ApiModelProperty(value = "Learning institution type")
    private String oppilaitosTyyppi;


    public String getHakusana() {
        return hakusana;
    }

    public void setHakusana(String hakusana) {
        this.hakusana = hakusana;
    }

    public String getOppilaitosTyyppi() {
        return oppilaitosTyyppi;
    }

    public void setOppilaitosTyyppi(String oppilaitosTyyppi) {
        this.oppilaitosTyyppi = oppilaitosTyyppi;
    }
}
