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
public class KoulutusSearchSpesificationViewModel {

    private String searchSpec;

    // TODO mitä nämä on, eli mitä formaattia - omia DTOta?
    private String hakukausi;
    private String koulutuksenAlkamiskausi;
    private String hakutapa;
    private String hakutyyppi;
    private String hanKohdejoukko;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("[");

        Field[] fields = this.getClass().getDeclaredFields();

        boolean isFirstField = true;

        for (Field field : fields) {
            if (!isFirstField) {
                sb.append(", ");
            }

            sb.append(field.getName());
            sb.append("=");

            try {
                Object v = field.get(this);
                if (v == null) {
                    sb.append("NULL");
                } else {
                    sb.append(v.toString());
                }
            } catch (Throwable ex) {
                sb.append("FAILED TO GET VALUE");
            }

            isFirstField = false;
        }

        sb.append("]");
        return sb.toString();
    }

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
