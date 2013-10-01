
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KoulutuksetVastaus implements Serializable
{

    private final static long serialVersionUID = 100L;
    private List<KoulutusPerustieto> koulutukset = new ArrayList<KoulutusPerustieto>();
    private int hitCount;

    public List<KoulutusPerustieto> getKoulutukset() {
        return koulutukset;
    }
    public void setKoulutukset(List<KoulutusPerustieto> koulutukset) {
        this.koulutukset = koulutukset;
    }
    public int getHitCount() {
        return hitCount;
    }
    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

}
