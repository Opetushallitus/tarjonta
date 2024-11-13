package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class KoulutusmoduuliTarjoajatiedotV1RDTO implements Serializable {

  private Set<String> tarjoajaOids = new HashSet<String>();

  public Set<String> getTarjoajaOids() {
    return tarjoajaOids;
  }

  public void setTarjoajaOids(Set<String> tarjoajaOids) {
    this.tarjoajaOids = tarjoajaOids;
  }

  public void addTarjoajaOid(String tarjoajaOid) {
    getTarjoajaOids().add(tarjoajaOid);
  }
}
