package fi.vm.sade.tarjonta.service.resources.v1.dto;

import com.wordnik.swagger.annotations.ApiModel;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import java.util.*;

/*
 * @author: Tuomas Katva 10/11/13
 */
@ApiModel(value = "Näyttää hakukohteeseen valittujen koulutustusmoduulien toteutuksien yhteensopivuuden.")
public class ValitutKoulutuksetV1RDTO extends BaseV1RDTO {

    private static final long serialVersionUID = 1L;

    //<komotoOid, <invalid with komoto oids>>
    private Map<String, Set<String>> oidConflictingWithOids;
    private Boolean valid = false;
    private List<NimiJaOidRDTO> names;
    private Set<String> toteutustyyppis;

    /**
     * @return the selectedOids
     */
    public Map<String, Set<String>> getOidConflictingWithOids() {
        if (oidConflictingWithOids == null) {
            oidConflictingWithOids = new HashMap<String, Set<String>>();
        }

        return oidConflictingWithOids;
    }

    /**
     * @param selectedOids the selectedOids to set
     */
    public void setOidConflictingWithOids(Map<String, Set<String>> selectedOids) {
        this.oidConflictingWithOids = selectedOids;
    }

    /**
     * @return the valid
     */
    public Boolean getValid() {
        return valid;
    }

    /**
     * @param valid the valid to set
     */
    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    /**
     * @return the names
     */
    public List<NimiJaOidRDTO> getNames() {
        return names;
    }

    /**
     * @param names
     */
    public void setNames(List<NimiJaOidRDTO> names) {
        this.names = names;
    }

    /**
     * @return the toteutustyyppis
     */
    public Set<String> getToteutustyyppis() {
        return toteutustyyppis;
    }

    /**
     * @param toteutustyyppis the toteutustyyppis to set
     */
    public void setToteutustyyppis(Set<String> toteutustyyppis) {
        this.toteutustyyppis = toteutustyyppis;
    }

}
