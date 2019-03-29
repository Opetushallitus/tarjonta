package fi.vm.sade.tarjonta.service.resources.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KelaHakukohteetDTO implements Serializable {

    public List<KelaHakukohdeDTO> hakukohteet = new ArrayList<>();

    public List<KelaHakukohdeDTO> getHakukohteet() {
        return hakukohteet;
    }

    public void setHakukohteet(List<KelaHakukohdeDTO> hakukohteet) {
        this.hakukohteet = hakukohteet;
    }

}
