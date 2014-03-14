package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.CopyMode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KoulutusCopyV1RDTO implements Serializable {

    private CopyMode mode;
    private List<String> organisationOids = new ArrayList<String>();

    public KoulutusCopyV1RDTO() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the organisationOids
     */
    public List<String> getOrganisationOids() {
        return organisationOids;
    }

    /**
     * @param organisationOids the organisationOids to set
     */
    public void setOrganisationOids(List<String> organisationOids) {
        this.organisationOids = organisationOids;
    }

    /**
     * @return the mode
     */
    public CopyMode getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(CopyMode mode) {
        this.mode = mode;
    }

}
