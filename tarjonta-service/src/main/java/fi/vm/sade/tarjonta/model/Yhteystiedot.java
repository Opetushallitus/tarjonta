package fi.vm.sade.tarjonta.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"id","version"})
@Table(name = Yhteystiedot.TABLE_NAME)
public class Yhteystiedot extends TarjontaBaseEntity {

    public static final String TABLE_NAME = "yhteystiedot";
    private static final long serialVersionUID = 2820464295959137992L;

    @ManyToOne
    @JoinColumn(name = "hakukohde_id", nullable = false)
    @JsonIgnore
    @JsonBackReference
    private Hakukohde hakukohde;

    @Column(name = "lang")
    private String lang;

    @Column(name = "osoiterivi1")
    private String osoiterivi1;

    @Column(name = "osoiterivi2")
    private String osoiterivi2;

    @Column(name = "postinumero")
    private String postinumero;

    @Column(name = "postitoimipaikka")
    private String postitoimipaikka;

    @Column(name = "hakutoimiston_nimi")
    private String hakutoimistonNimi;

    @Column(name = "kayntiosoite_osoiterivi1")
    private String kayntiosoiteOsoiterivi1;

    @Column(name = "kayntiosoite_postinumero")
    private String kayntiosoitePostinumero;

    @Column(name = "kayntiosoite_postitoimipaikka")
    private String kayntiosoitePostitoimipaikka;

    @Column(name = "puhelinnumero")
    private String puhelinnumero;

    @Column(name = "sahkopostiosoite")
    private String sahkopostiosoite;

    @Column(name = "www_osoite")
    private String wwwOsoite;

    public Hakukohde getHakukohde() {
        return hakukohde;
    }

    public void setHakukohde(Hakukohde hakukohde) {
        this.hakukohde = hakukohde;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getOsoiterivi1() {
        return osoiterivi1;
    }

    public void setOsoiterivi1(String osoiterivi1) {
        this.osoiterivi1 = osoiterivi1;
    }

    public String getOsoiterivi2() {
        return osoiterivi2;
    }

    public void setOsoiterivi2(String osoiterivi2) {
        this.osoiterivi2 = osoiterivi2;
    }

    public String getPostinumero() {
        return postinumero;
    }

    public void setPostinumero(String postinumero) {
        this.postinumero = postinumero;
    }

    public void setPostitoimipaikka(String postitoimipaikka) {
        this.postitoimipaikka = postitoimipaikka;
    }

    public String getPostitoimipaikka() {
        return postitoimipaikka;
    }

    public String getHakutoimistonNimi() {
        return hakutoimistonNimi;
    }

    public void setHakutoimistonNimi(String hakutoimistonNimi) {
        this.hakutoimistonNimi = hakutoimistonNimi;
    }

    public String getKayntiosoiteOsoiterivi1() {
        return kayntiosoiteOsoiterivi1;
    }

    public void setKayntiosoiteOsoiterivi1(String kayntiosoiteOsoiterivi1) {
        this.kayntiosoiteOsoiterivi1 = kayntiosoiteOsoiterivi1;
    }

    public String getKayntiosoitePostinumero() {
        return kayntiosoitePostinumero;
    }

    public void setKayntiosoitePostinumero(String kayntiosoitePostinumero) {
        this.kayntiosoitePostinumero = kayntiosoitePostinumero;
    }

    public String getKayntiosoitePostitoimipaikka() {
        return kayntiosoitePostitoimipaikka;
    }

    public void setKayntiosoitePostitoimipaikka(String kayntiosoitePostitoimipaikka) {
        this.kayntiosoitePostitoimipaikka = kayntiosoitePostitoimipaikka;
    }

    public String getPuhelinnumero() {
        return puhelinnumero;
    }

    public void setPuhelinnumero(String puhelinnumero) {
        this.puhelinnumero = puhelinnumero;
    }

    public String getSahkopostiosoite() {
        return sahkopostiosoite;
    }

    public void setSahkopostiosoite(String sahkopostiosoite) {
        this.sahkopostiosoite = sahkopostiosoite;
    }

    public String getWwwOsoite() {
        return wwwOsoite;
    }

    public void setWwwOsoite(String wwwOsoite) {
        this.wwwOsoite = wwwOsoite;
    }

}
