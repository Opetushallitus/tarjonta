package fi.vm.sade.tarjonta.shared.amkouteDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AmkouteMaarays {

    private String koodisto;
    private String koodiarvo;
    private Integer koodistoversio;
    private AmkouteMaaraystyyppi maaraystyyppi;
    private List<AmkouteMaarays> aliMaaraykset = Lists.newArrayList();


    public String getKoodisto() {
        return koodisto;
    }

    public void setKoodisto(String koodisto) {
        this.koodisto = koodisto;
    }

    public String getKoodiarvo() {
        return koodiarvo;
    }

    public void setKoodiarvo(String koodiarvo) {
        this.koodiarvo = koodiarvo;
    }

    public Integer getKoodistoversio() {
        return koodistoversio;
    }

    public void setKoodistoversio(Integer koodistoversio) {
        this.koodistoversio = koodistoversio;
    }

    public AmkouteMaaraystyyppi getMaaraystyyppi() {
        return maaraystyyppi;
    }

    public void setMaaraystyyppi(AmkouteMaaraystyyppi maaraystyyppi) {
        this.maaraystyyppi = maaraystyyppi;
    }

    public List<AmkouteMaarays> getAliMaaraykset() {
        return aliMaaraykset;
    }

    public void setAliMaaraykset(List<AmkouteMaarays> aliMaaraykset) {
        this.aliMaaraykset = aliMaaraykset;
    }
}
