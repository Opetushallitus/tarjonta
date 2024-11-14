package fi.vm.sade.tarjonta.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@JsonIgnoreProperties({"id","version"})
@Table(name = "ryhmaliitos")
public class Ryhmaliitos extends BaseEntity {

    @NotNull
    @ManyToOne
    @JsonBackReference
    private Hakukohde hakukohde;

    @NotNull
    @Column(name = "ryhma_oid")
    private String ryhmaOid;

    @Column(name = "prioriteetti")
    private Integer prioriteetti;

    public Hakukohde getHakukohde() {
        return hakukohde;
    }

    public void setHakukohde(Hakukohde hakukohde) {
        this.hakukohde = hakukohde;
    }

    public Integer getPrioriteetti() {
        return prioriteetti;
    }

    public void setPrioriteetti(Integer prioriteetti) {
        this.prioriteetti = prioriteetti;
    }

    public String getRyhmaOid() {
        return ryhmaOid;
    }

    public void setRyhmaOid(String ryhmaOid) {
        this.ryhmaOid = ryhmaOid;
    }
}

