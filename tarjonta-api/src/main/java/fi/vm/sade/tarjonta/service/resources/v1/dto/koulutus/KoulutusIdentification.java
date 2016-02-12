package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

public class KoulutusIdentification {

    private String oid;
    private String tunniste; // oppilaitoksen käyttämä oma tunniste

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getTunniste() {
        return tunniste;
    }

    public void setTunniste(String tunniste) {
        this.tunniste = tunniste;
    }
}
