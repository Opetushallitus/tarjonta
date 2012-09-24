/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.model;

import java.lang.reflect.Field;

/**
 *
 * @author mlyly
 */
public class KoulutusSearchSpesificationViewModel extends BaseUIViewModel {

    private String searchSpec;

    // Koodisto
    private String hakukausi;
    // Koodisto
    private String koulutuksenAlkamiskausi;
    // Koodisto
    private String hakutapa;
    // Koodisto
    private String hakutyyppi;
    // Koodisto
    private String haunKohdejoukko;

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

    public String getHaunKohdejoukko() {
        return haunKohdejoukko;
    }

    public void setHaunKohdejoukko(String hanKohdejoukko) {
        this.haunKohdejoukko = hanKohdejoukko;
    }
}
