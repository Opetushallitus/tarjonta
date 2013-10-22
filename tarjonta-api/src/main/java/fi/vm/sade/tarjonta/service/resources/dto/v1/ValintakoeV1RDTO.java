package fi.vm.sade.tarjonta.service.resources.dto.v1;

import fi.vm.sade.tarjonta.service.resources.dto.BaseRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;

import java.util.ArrayList;
import java.util.List;

/*
* @author: Tuomas Katva 10/21/13
*/
public class ValintakoeV1RDTO extends BaseRDTO {

    private String kieliUri;

    private String valintakoeNimi;

    private TekstiRDTO valintakokeenKuvaus;

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
}