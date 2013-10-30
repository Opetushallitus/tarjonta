package fi.vm.sade.tarjonta.service.resources.v1.dto;

import fi.vm.sade.tarjonta.service.resources.dto.BaseRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;

import java.util.Date;

/*
* @author: Tuomas Katva 10/22/13
*/
public class HakukohdeLiiteV1RDTO extends BaseRDTO {

    private String kieliUri;

    private String kieliNimi;

    private String liitteenNimi;

    private TekstiRDTO liitteenKuvaus;

    private Date toimitettavaMennessa;

    private OsoiteRDTO liitteenToimitusOsoite;

    private String sahkoinenToimitusOsoite;

    public String getKieliUri() {
        return kieliUri;
    }

    public void setKieliUri(String kieliUri) {
        this.kieliUri = kieliUri;
    }

    public String getLiitteenNimi() {
        return liitteenNimi;
    }

    public void setLiitteenNimi(String liitteenNimi) {
        this.liitteenNimi = liitteenNimi;
    }

    public TekstiRDTO getLiitteenKuvaus() {
        return liitteenKuvaus;
    }

    public void setLiitteenKuvaus(TekstiRDTO liitteenKuvaus) {
        this.liitteenKuvaus = liitteenKuvaus;
    }

    public Date getToimitettavaMennessa() {
        return toimitettavaMennessa;
    }

    public void setToimitettavaMennessa(Date toimitettavaMennessa) {
        this.toimitettavaMennessa = toimitettavaMennessa;
    }

    public OsoiteRDTO getLiitteenToimitusOsoite() {
        return liitteenToimitusOsoite;
    }

    public void setLiitteenToimitusOsoite(OsoiteRDTO liitteenToimitusOsoite) {
        this.liitteenToimitusOsoite = liitteenToimitusOsoite;
    }

    public String getSahkoinenToimitusOsoite() {
        return sahkoinenToimitusOsoite;
    }

    public void setSahkoinenToimitusOsoite(String sahkoinenToimitusOsoite) {
        this.sahkoinenToimitusOsoite = sahkoinenToimitusOsoite;
    }

    public String getKieliNimi() {
        return kieliNimi;
    }

    public void setKieliNimi(String kieliNimi) {
        this.kieliNimi = kieliNimi;
    }
}
