package fi.vm.sade.tarjonta.service.impl.aspects;

import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;

public class KoulutusPermissionException extends RuntimeException {

    private String organisaationNimi;
    private String organisaationOid;
    private String koodisto;
    private String puuttuvaKoodi;
    private String kohdeKoodi;
    private KoulutusmoduuliToteutus komoto;

    public KoulutusPermissionException(String organisaationNimi, String organisaationOid, String koodisto, String puuttuuvaKoodi, String kohdeKoodi) {
        this.organisaationNimi = organisaationNimi;
        this.organisaationOid = organisaationOid;
        this.koodisto = koodisto;
        this.puuttuvaKoodi = puuttuuvaKoodi;
        this.kohdeKoodi = kohdeKoodi;
    }

    public String getOrganisaationNimi() {
        return organisaationNimi;
    }

    public String getOrganisaationOid() {
        return organisaationOid;
    }

    public String getPuuttuvaKoodi() {
        return puuttuvaKoodi;
    }

    public String getKohdeKoodi() {
        return kohdeKoodi;
    }

    public KoulutusmoduuliToteutus getKomoto() {
        return komoto;
    }

    public void setKomoto(KoulutusmoduuliToteutus komoto) {
        this.komoto = komoto;
    }

    public String getKoodisto() {
        return koodisto;
    }

    @Override
    public String toString() {
        return "KoulutusPermissionException{" +
                "organisaationNimi='" + organisaationNimi + '\'' +
                ", organisaationOid='" + organisaationOid + '\'' +
                ", koodisto='" + koodisto + '\'' +
                ", puuttuvaKoodi='" + puuttuvaKoodi + '\'' +
                ", kohdeKoodi='" + kohdeKoodi + '\'' +
                ", komoto=" + komoto +
                '}';
    }
}
