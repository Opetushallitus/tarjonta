package fi.vm.sade.tarjonta.shared.amkouteDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AmkouteOpetuskieliDTO {

    private String oppilaitoksenopetuskieli;
    private Date alkupvm;
    private Date loppupvm;

    public String getOppilaitoksenopetuskieli() {
        return oppilaitoksenopetuskieli;
    }

    public void setOppilaitoksenopetuskieli(String oppilaitoksenopetuskieli) {
        this.oppilaitoksenopetuskieli = oppilaitoksenopetuskieli;
    }

    public Date getAlkupvm() {
        return alkupvm;
    }

    public void setAlkupvm(Date alkupvm) {
        this.alkupvm = alkupvm;
    }

    public Date getLoppupvm() {
        return loppupvm;
    }

    public void setLoppupvm(Date loppupvm) {
        this.loppupvm = loppupvm;
    }

}
