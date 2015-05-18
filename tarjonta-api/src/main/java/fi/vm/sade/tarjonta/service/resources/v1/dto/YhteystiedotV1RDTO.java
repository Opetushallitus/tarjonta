package fi.vm.sade.tarjonta.service.resources.v1.dto;

import fi.vm.sade.tarjonta.service.resources.dto.BaseRDTO;

public class YhteystiedotV1RDTO extends BaseRDTO {

    private static final long serialVersionUID = 2716121730126623555L;
    
    private String lang;
    private String osoiterivi1;
    private String osoiterivi2;
    private String postinumero;
    private String postinumeroArvo;
    private String postitoimipaikka;
    private String puhelinnumero;
    private String sahkopostiosoite;
    private String hakutoimistonNimi;
    private String wwwOsoite;
    private YhteystiedotV1RDTO kayntiosoite;

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

    public String getPostitoimipaikka() {
        return postitoimipaikka;
    }

    public void setPostitoimipaikka(String postitoimipaikka) {
        this.postitoimipaikka = postitoimipaikka;
    }

    public String getPostinumeroArvo() {
        return postinumeroArvo;
    }

    public void setPostinumeroArvo(String postinumeroArvo) {
        this.postinumeroArvo = postinumeroArvo;
    }

    public YhteystiedotV1RDTO getKayntiosoite() {
        return kayntiosoite;
    }

    public void setKayntiosoite(YhteystiedotV1RDTO kayntiosoite) {
        this.kayntiosoite = kayntiosoite;
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

    public String getHakutoimistonNimi() {
        return hakutoimistonNimi;
    }

    public void setHakutoimistonNimi(String hakutoimistonNimi) {
        this.hakutoimistonNimi = hakutoimistonNimi;
    }

    public String getWwwOsoite() {
        return wwwOsoite;
    }

    public void setWwwOsoite(String wwwOsoite) {
        this.wwwOsoite = wwwOsoite;
    }

}
