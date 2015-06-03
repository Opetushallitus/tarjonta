package fi.vm.sade.tarjonta.model;

import fi.vm.sade.generic.model.BaseEntity;
import javax.persistence.*;

@Entity
@Table(name = Oppiaine.TABLE_NAME)
public class Oppiaine extends BaseEntity {

    public static final String TABLE_NAME = "oppiaineet";

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

}
