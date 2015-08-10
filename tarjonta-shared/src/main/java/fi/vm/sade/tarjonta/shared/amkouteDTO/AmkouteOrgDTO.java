package fi.vm.sade.tarjonta.shared.amkouteDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AmkouteOrgDTO {

    private String oid;
    private Date alkupvm;
    private Date loppupvm;
    private List<AmkouteKoulutusDTO> koulutukset = new ArrayList<AmkouteKoulutusDTO>();
    private List<AmkouteJarjestamiskuntaDTO> jarjestamiskunnat = new ArrayList<AmkouteJarjestamiskuntaDTO>();
    private List<AmkouteOpetuskieliDTO> opetuskielet = new ArrayList<AmkouteOpetuskieliDTO>();

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
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

    public List<AmkouteKoulutusDTO> getKoulutukset() {
        return koulutukset;
    }

    public void setKoulutukset(List<AmkouteKoulutusDTO> koulutukset) {
        this.koulutukset = koulutukset;
    }

    public List<AmkouteJarjestamiskuntaDTO> getJarjestamiskunnat() {
        return jarjestamiskunnat;
    }

    public void setJarjestamiskunnat(List<AmkouteJarjestamiskuntaDTO> jarjestamiskunnat) {
        this.jarjestamiskunnat = jarjestamiskunnat;
    }

    public List<AmkouteOpetuskieliDTO> getOpetuskielet() {
        return opetuskielet;
    }

    public void setOpetuskielet(List<AmkouteOpetuskieliDTO> opetuskielet) {
        this.opetuskielet = opetuskielet;
    }

}
