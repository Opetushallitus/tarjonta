package fi.vm.sade.tarjonta.shared.organisaatio;

import java.util.List;

public class OrganisaatioResultDTO {
    private List<OrganisaatioDTO> organisaatiot;

    public List<OrganisaatioDTO> getOrganisaatiot() {
        return organisaatiot;
    }

    public void setOrganisaatiot(List<OrganisaatioDTO> organisaatiot) {
        this.organisaatiot = organisaatiot;
    }
}
