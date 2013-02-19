package fi.vm.sade.tarjonta.data.util;

/**
 * Created by: Tuomas Katva
 * Date: 19.2.2013
 * Time: 18:08
 */
public class CommonConstants {

    private String organisaatioOid;

    private String organisaatioNimi;

    private String baseGroupUri;

    public CommonConstants(String orgOid,String orgName,String baseUri) {
        this.organisaatioNimi = orgName;
        this.organisaatioOid = orgOid;
        this.baseGroupUri = baseUri;
    }

    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    public String getOrganisaatioNimi() {
        return organisaatioNimi;
    }

    public String getBaseGroupUri() {
        return baseGroupUri;
    }
}
