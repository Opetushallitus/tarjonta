package fi.vm.sade.tarjonta.service.resources.dto;

// case class KoulutusLaajuusarvo(koulutuskoodi: Option[String], opintojenLaajuusarvo: Option[Koodi])
public class KoulutusLaajuusarvoDTO {
    private String oid;
    private String koulutuskoodi;
    private String koulutustyyppi;
    private String opintojenLaajuusarvo;

    public String getOid() {
        return oid;
    }

    public void setOpintojenLaajuusarvo(String opintojenLaajuusarvo) {
        this.opintojenLaajuusarvo = opintojenLaajuusarvo;
    }

    public void setKoulutuskoodi(String koulutuskoodi) {
        this.koulutuskoodi = koulutuskoodi;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getKoulutuskoodi() {
        return koulutuskoodi;
    }

    public String getOpintojenLaajuusarvo() {
        return opintojenLaajuusarvo;
    }

    public String getKoulutustyyppi() {
        return koulutustyyppi;
    }

    public void setKoulutustyyppi(String koulutustyyppi) {
        this.koulutustyyppi = koulutustyyppi;
    }

}
