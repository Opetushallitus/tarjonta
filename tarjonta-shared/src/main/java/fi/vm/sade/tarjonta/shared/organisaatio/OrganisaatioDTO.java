package fi.vm.sade.tarjonta.shared.organisaatio;

import java.util.List;

public class OrganisaatioDTO {
    private String oid;
    private String oppilaitosKoodi;
    private List<OrganisaatioDTO> children;

    public List<OrganisaatioDTO> getChildren() {
        return children;
    }

    public String getOppilaitosKoodi() {
        return oppilaitosKoodi;
    }

    public String getOid() {
        return oid;
    }
}
