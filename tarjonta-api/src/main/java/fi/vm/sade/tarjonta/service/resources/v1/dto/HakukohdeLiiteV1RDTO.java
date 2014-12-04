package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.util.Date;
import java.util.Map;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import fi.vm.sade.tarjonta.service.resources.dto.BaseRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;

/*
* @author: Tuomas Katva 10/22/13
*/
@ApiModel(value = "V1 Hakukohde's attachment(liite) REST-api model, used by KK-ui")
public class HakukohdeLiiteV1RDTO extends BaseRDTO {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "Liite's hakukohde oid", required=true)
    private String hakukohdeOid;
    @ApiModelProperty(value = "Liite's name language uri", required = true)
    private String kieliUri;
    @ApiModelProperty(value = "Liite's name's language's name, used for displaying the value", required = false)
    private String kieliNimi;
    @ApiModelProperty(value = "Liite's name", required = true)
    private String liitteenNimi;
    @ApiModelProperty(value = "Liite's tyyppi", required = true)
    private String liitteenTyyppi;
    @ApiModelProperty(value = "Liite's order", required = false)
    private Integer jarjestys;
    @ApiModelProperty(value = "Should this liite be used in hakulomake")
    private boolean kaytetaanHakulomakkeella = true;

    private Map<String,String> liitteenKuvaukset;

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

    public Map<String, String> getLiitteenKuvaukset() {
        return liitteenKuvaukset;
    }

    public void setLiitteenKuvaukset(Map<String, String> liitteenKuvaukset) {
        this.liitteenKuvaukset = liitteenKuvaukset;
    }

    public String getLiitteenTyyppi() {
        return liitteenTyyppi;
    }

    public void setLiitteenTyyppi(String liitteenTyyppi) {
        this.liitteenTyyppi = liitteenTyyppi;
    }

    public void setJarjestys(Integer jarjestys) {
        this.jarjestys = jarjestys;
    }

    public Integer getJarjestys() {
        return jarjestys;
    }

    public boolean isKaytetaanHakulomakkeella() {
        return kaytetaanHakulomakkeella;
    }

    public void setKaytetaanHakulomakkeella(boolean kaytetaanHakulomakkeella) {
        this.kaytetaanHakulomakkeella = kaytetaanHakulomakkeella;
    }
}
