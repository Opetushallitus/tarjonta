package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KoulutusCopyResultV1RDTO implements Serializable {

    private String fromOid;
    private List<KoulutusCopyStatusV1RDTO> to = new ArrayList<KoulutusCopyStatusV1RDTO>();

    public KoulutusCopyResultV1RDTO() {
        // TODO Auto-generated constructor stub
    }

    public KoulutusCopyResultV1RDTO(String fromOid) {
        this.fromOid = fromOid;
    }

    /**
     * @return the fromOid
     */
    public String getFromOid() {
        return fromOid;
    }

    /**
     * @param fromOid the fromOid to set
     */
    public void setFromOid(String fromOid) {
        this.fromOid = fromOid;
    }

    /**
     * @return the to
     */
    public List<KoulutusCopyStatusV1RDTO> getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(List<KoulutusCopyStatusV1RDTO> to) {
        this.to = to;
    }

}
