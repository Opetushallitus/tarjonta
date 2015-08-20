package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HakukohteetVastaus implements Serializable {

    private final static long serialVersionUID = 100L;

    private Integer hitCount;

    public List<HakukohdePerustieto> getHakukohteet() {
        return hakukohteet;
    }

    public void setHakukohteet(List<HakukohdePerustieto> hakukohteet) {
        this.hakukohteet = hakukohteet;
    }

    public Integer getHitCount() {
        return hitCount;
    }

    public void setHitCount(Integer hitCount) {
        this.hitCount = hitCount;
    }

    private List<HakukohdePerustieto> hakukohteet = new ArrayList<HakukohdePerustieto>();

}
