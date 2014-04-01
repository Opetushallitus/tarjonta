package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import java.io.Serializable;

public class KoulutusCopyStatusV1RDTO implements Serializable {

    private String oid;
    private Boolean success = true;
    private String organisationOid;

    public KoulutusCopyStatusV1RDTO() {
        // TODO Auto-generated constructor stub
    }

    public KoulutusCopyStatusV1RDTO(String oid, String organisationOid) {
        this.oid = oid;
        this.organisationOid = organisationOid;
    }

    /**
     * @return the oid
     */
    public String getOid() {
        return oid;
    }

    /**
     * @param oid the oid to set
     */
    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     * @return the success
     */
    public Boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * @return the organisationOid
     */
    public String getOrganisationOid() {
        return organisationOid;
    }

    /**
     * @param organisationOid the organisationOid to set
     */
    public void setOrganisationOid(String organisationOid) {
        this.organisationOid = organisationOid;
    }

}
