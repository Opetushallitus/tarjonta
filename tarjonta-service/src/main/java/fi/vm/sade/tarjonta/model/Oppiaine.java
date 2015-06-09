package fi.vm.sade.tarjonta.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import fi.vm.sade.generic.model.BaseEntity;

@Entity
@Table(name = Oppiaine.TABLE_NAME)
public class Oppiaine extends BaseEntity {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "oppiaineet";

    @ManyToMany(mappedBy = "oppiaineet", fetch = FetchType.LAZY)
    private Set<KoulutusmoduuliToteutus> komotos = new HashSet<KoulutusmoduuliToteutus>();

    @Column(name = "oppiaine")
    private String oppiaine;

    @Column(name = "kieli_koodi")
    private String kieliKoodi;

    public String getOppiaine() {
        return oppiaine;
    }

    public void setOppiaine(String oppiaine) {
        this.oppiaine = oppiaine;
    }

    public String getKieliKoodi() {
        return kieliKoodi;
    }

    public void setKieliKoodi(String kieliKoodi) {
        this.kieliKoodi = kieliKoodi;
    }

    public Set<KoulutusmoduuliToteutus> getKomotos() {
        return komotos;
    }

    public void setKomotos(Set<KoulutusmoduuliToteutus> komotos) {
        this.komotos = komotos;
    }

}
