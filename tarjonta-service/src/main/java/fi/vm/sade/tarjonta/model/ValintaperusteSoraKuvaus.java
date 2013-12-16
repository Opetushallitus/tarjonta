package fi.vm.sade.tarjonta.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/*
* @author: Tuomas Katva 16/12/13
*
* This entity holds either SORA-kuvaukses or Valintaperuste-kuvaukses for
* university and polytechnic education
*
*/
@Entity
@Table( name = ValintaperusteSoraKuvaus.VALINTAPERUSTEKUVAUSORA_TABLE_NAME)
public class ValintaperusteSoraKuvaus  extends  TarjontaBaseEntity {

    public  static final  String VALINTAPERUSTEKUVAUSORA_TABLE_NAME = "valintaperuste_sora_kuvaus";

    public Tyyppi getTyyppi() {
        return tyyppi;
    }

    public void setTyyppi(Tyyppi tyyppi) {
        this.tyyppi = tyyppi;
    }

    public static enum Tyyppi { VALINTAPERUSTEKUVAUS, SORA };

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn( name = "monikielinen_nimi_id")
    private MonikielinenTeksti monikielinenNimi;

    @Column( name = "organisaatio_tyyppi")
    private String organisaatioTyyppi;

    @Column( name =  "tyyppi")
    private Tyyppi tyyppi;

    @Column( name = "tekstis")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MonikielinenMetadata> tekstis;


    public MonikielinenTeksti getMonikielinenNimi() {
        return monikielinenNimi;
    }

    public void setMonikielinenNimi(MonikielinenTeksti monikielinenNimi) {
        this.monikielinenNimi = monikielinenNimi;
    }

    public String getOrganisaatioTyyppi() {
        return organisaatioTyyppi;
    }

    public void setOrganisaatioTyyppi(String organisaatioTyyppi) {
        this.organisaatioTyyppi = organisaatioTyyppi;
    }

    public List<MonikielinenMetadata> getTekstis() {
        return tekstis;
    }

    public void setTekstis(List<MonikielinenMetadata> tekstis) {
        this.tekstis = tekstis;
    }
}
