package fi.vm.sade.tarjonta.service.resources.v1.dto;

import com.wordnik.swagger.annotations.ApiModel;

import java.util.HashSet;
import java.util.Set;

public class KoulutusmoduuliTarjoajatiedotV1RDTO {

    private Set<String> tarjoajaOids = new HashSet<String>();

    public Set<String> getTarjoajaOids() {
        return tarjoajaOids;
    }

    public void setTarjoajaOids(Set<String> tarjoajaOids) {
        this.tarjoajaOids = tarjoajaOids;
    }
}
