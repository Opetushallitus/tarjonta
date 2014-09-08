package fi.vm.sade.tarjonta.service.resources.v1.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.tarjonta.service.resources.dto.BaseRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;

import java.util.ArrayList;
import java.util.List;

/*
* @author: Tuomas Katva 10/21/13
*/
@ApiModel(value = "V1 Hakukohde's valintakoe REST-api model, used by KK-ui")
public class ValintakoeV1RDTO extends BaseRDTO {

    @ApiModelProperty(value = "Valintakoe's hakukohde oid", required=true)
    private String hakukohdeOid;
    @ApiModelProperty(value = "Valintakoe's name language uri", required = true)
    private String kieliUri;
    @ApiModelProperty(value = "Valintakoe's name's language's name, used for displaying the value", required = false)
    private String kieliNimi;
    @ApiModelProperty(value = "Valintakoe's name ", required = true)
    private String valintakoeNimi;
    @ApiModelProperty(value = "Valintakoe's tyyppi", required=true)
    private String valintakoetyyppi;

    private TekstiRDTO valintakokeenKuvaus;
    @ApiModelProperty(value = "Valintakoe's dates")
    private List<ValintakoeAjankohtaRDTO> valintakoeAjankohtas;


    public String getKieliUri() {
        return kieliUri;
    }

    public void setKieliUri(String kieliUri) {
        this.kieliUri = kieliUri;
    }

    public String getValintakoeNimi() {
        return valintakoeNimi;
    }

    public void setValintakoeNimi(String valintakoeNimi) {
        this.valintakoeNimi = valintakoeNimi;
    }

    public TekstiRDTO getValintakokeenKuvaus() {
        return valintakokeenKuvaus;
    }

    public void setValintakokeenKuvaus(TekstiRDTO valintakokeenKuvaus) {
        this.valintakokeenKuvaus = valintakokeenKuvaus;
    }

    public List<ValintakoeAjankohtaRDTO> getValintakoeAjankohtas() {
        if (valintakoeAjankohtas == null) {
            valintakoeAjankohtas = new ArrayList<ValintakoeAjankohtaRDTO>();
        }

        return valintakoeAjankohtas;
    }

    public void setValintakoeAjankohtas(List<ValintakoeAjankohtaRDTO> valintakoeAjankohtas) {
        this.valintakoeAjankohtas = valintakoeAjankohtas;
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }

    public String getKieliNimi() {
        return kieliNimi;
    }

    public void setKieliNimi(String kieliNimi) {
        this.kieliNimi = kieliNimi;
    }

    public String getValintakoetyyppi() {
        return valintakoetyyppi;
    }

    public void setValintakoetyyppi(String valintakoetyyppi) {
        this.valintakoetyyppi = valintakoetyyppi;
    }
}
