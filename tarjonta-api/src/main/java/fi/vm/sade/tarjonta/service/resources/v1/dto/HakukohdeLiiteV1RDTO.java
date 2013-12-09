package fi.vm.sade.tarjonta.service.resources.v1.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.tarjonta.service.resources.dto.BaseRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;

import java.util.Date;
import java.util.HashMap;

/*
* @author: Tuomas Katva 10/22/13
*/
@ApiModel(value = "V1 Hakukohde's attachment(liite) REST-api model, used by KK-ui")
public class HakukohdeLiiteV1RDTO extends BaseRDTO {

    @ApiModelProperty(value = "Liite's hakukohde oid", required=true)
    private String hakukohdeOid;
    @ApiModelProperty(value = "Liite's name language uri", required = true)
    private String kieliUri;
    @ApiModelProperty(value = "Liite's name's language's name, used for displaying the value", required = false)
    private String kieliNimi;
    @ApiModelProperty(value = "Liite's name", required = true)
    private String liitteenNimi;
    //TODO: remove this and use the much simpler HashMap implementation
    private TekstiRDTO liitteenKuvaus;

    private HashMap<String,String> liitteenKuvaukset;

    private Date toimitettavaMennessa;

    private OsoiteRDTO liitteenToimitusOsoite;

    private String sahkoinenToimitusOsoite;

    public String getKieliUri() {
        return kieliUri;
    }

    public void setKieliUri(String kieliUri) {
        this.kieliUri = kieliUri;
    }

    public String getLiitteenNimi() {
        return liitteenNimi;
    }

    public void setLiitteenNimi(String liitteenNimi) {
        this.liitteenNimi = liitteenNimi;
    }

    public TekstiRDTO getLiitteenKuvaus() {
        return liitteenKuvaus;
    }

    public void setLiitteenKuvaus(TekstiRDTO liitteenKuvaus) {
        this.liitteenKuvaus = liitteenKuvaus;
    }

    public Date getToimitettavaMennessa() {
        return toimitettavaMennessa;
    }

    public void setToimitettavaMennessa(Date toimitettavaMennessa) {
        this.toimitettavaMennessa = toimitettavaMennessa;
    }

    public OsoiteRDTO getLiitteenToimitusOsoite() {
        return liitteenToimitusOsoite;
    }

    public void setLiitteenToimitusOsoite(OsoiteRDTO liitteenToimitusOsoite) {
        this.liitteenToimitusOsoite = liitteenToimitusOsoite;
    }

    public String getSahkoinenToimitusOsoite() {
        return sahkoinenToimitusOsoite;
    }

    public void setSahkoinenToimitusOsoite(String sahkoinenToimitusOsoite) {
        this.sahkoinenToimitusOsoite = sahkoinenToimitusOsoite;
    }

    public String getKieliNimi() {
        return kieliNimi;
    }

    public void setKieliNimi(String kieliNimi) {
        this.kieliNimi = kieliNimi;
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }

    public HashMap<String, String> getLiitteenKuvaukset() {
        return liitteenKuvaukset;
    }

    public void setLiitteenKuvaukset(HashMap<String, String> liitteenKuvaukset) {
        this.liitteenKuvaukset = liitteenKuvaukset;
    }
}
