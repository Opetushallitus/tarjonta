package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HakukohteetVastaus implements Serializable {

    private final static long serialVersionUID = 100L;

    public List<HakukohdePerustieto> getHakukohteet() {
        return hakukohteet;
    }

    public void setHakukohteet(List<HakukohdePerustieto> hakukohteet) {
        this.hakukohteet = hakukohteet;
    }

    public int getHitCount() {
        return hakukohteet.size();
    }


    private List<HakukohdePerustieto> hakukohteet = new ArrayList<HakukohdePerustieto>();

}
