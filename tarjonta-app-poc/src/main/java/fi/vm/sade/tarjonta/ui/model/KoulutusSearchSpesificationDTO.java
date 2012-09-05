/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.model;

/**
 *
 * @author mlyly
 */
public class KoulutusSearchSpesificationDTO {
    
    private String searchSpec;
    
    // TODO mitä nämä on, eli mitä formaattia - omia DTOta?
    private String hakukausi;
    private String koulutuksenAlkamiskausi;
    private String hakutapa;
    private String hakutyyppi;
    private String hanKohdejoukko;

    public String getSearchSpec() {
        return searchSpec;
    }

    public void setSearchSpec(String searchSpec) {
        this.searchSpec = searchSpec;
    }

    public String getHakukausi() {
        return hakukausi;
    }

    public void setHakukausi(String hakukausi) {
        this.hakukausi = hakukausi;
    }

    public String getKoulutuksenAlkamiskausi() {
        return koulutuksenAlkamiskausi;
    }

    public void setKoulutuksenAlkamiskausi(String koulutuksenAlkamiskausi) {
        this.koulutuksenAlkamiskausi = koulutuksenAlkamiskausi;
    }

    public String getHakutapa() {
        return hakutapa;
    }

    public void setHakutapa(String hakutapa) {
        this.hakutapa = hakutapa;
    }

    public String getHakutyyppi() {
        return hakutyyppi;
    }

    public void setHakutyyppi(String hakutyyppi) {
        this.hakutyyppi = hakutyyppi;
    }

    public String getHanKohdejoukko() {
        return hanKohdejoukko;
    }

    public void setHanKohdejoukko(String hanKohdejoukko) {
        this.hanKohdejoukko = hanKohdejoukko;
    }
    
}
