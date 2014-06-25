package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import java.util.ArrayList;
import java.util.List;

public class KoulutusMultiCopyV1RDTO extends KoulutusCopyV1RDTO {

    private List<String> komotoOids = new ArrayList<String>();

    public KoulutusMultiCopyV1RDTO() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the komotoOids
     */
    public List<String> getKomotoOids() {
        return komotoOids;
    }

    /**
     * @param komotoOids the komotoOids to set
     */
    public void setKomotoOids(List<String> komotoOids) {
        this.komotoOids = komotoOids;
    }

}
