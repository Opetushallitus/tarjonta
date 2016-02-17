package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

public class KoulutusIdentification {

    private String oid;
    private String ulkoinenTunniste; // oppilaitoksen käyttämä oma ulkoinenTunniste

    public KoulutusIdentification() {
    }

    public KoulutusIdentification(String oid, String ulkoinenTunniste) {
        this.oid = oid;
        this.ulkoinenTunniste = ulkoinenTunniste;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getUlkoinenTunniste() {
        return ulkoinenTunniste;
    }

    public void setUlkoinenTunniste(String ulkoinenTunniste) {
        this.ulkoinenTunniste = ulkoinenTunniste;
    }
}
