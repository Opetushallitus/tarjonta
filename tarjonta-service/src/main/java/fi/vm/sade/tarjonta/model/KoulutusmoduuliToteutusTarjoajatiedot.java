package fi.vm.sade.tarjonta.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@JsonIgnoreProperties({"id", "version"})
@Table(name = KoulutusmoduuliToteutusTarjoajatiedot.TABLE_NAME)
public class KoulutusmoduuliToteutusTarjoajatiedot extends BaseEntity {

  public static final String TABLE_NAME = "koulutusmoduuli_toteutus_tarjoajatiedot";

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
      name = TABLE_NAME + "_tarjoaja_oid",
      joinColumns = @JoinColumn(name = TABLE_NAME + "_id"))
  @Column(name = "tarjoaja_oid")
  private Set<String> tarjoajaOids = new HashSet<String>();

  public Set<String> getTarjoajaOids() {
    return tarjoajaOids;
  }

  public void setTarjoajaOids(Set<String> tarjoajaOids) {
    this.tarjoajaOids = tarjoajaOids;
  }

  public boolean containsOnlyTarjoaja(String tarjoajaOid) {
    return getTarjoajaOids().size() == 1 && getTarjoajaOids().contains(tarjoajaOid);
  }

  public void removeTarjoaja(String tarjoajaOid) {
    getTarjoajaOids().remove(tarjoajaOid);
  }
}
