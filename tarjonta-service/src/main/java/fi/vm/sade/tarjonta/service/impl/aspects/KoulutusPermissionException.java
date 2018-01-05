package fi.vm.sade.tarjonta.service.impl.aspects;

import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;

public class KoulutusPermissionException extends RuntimeException {

    private String organisaationNimi;
    private String organisaationOid;
    private String koodisto;
    private String kohdeKoodi;
    private KoulutusmoduuliToteutus komoto;

    public KoulutusPermissionException(String organisaationNimi, String organisaationOid, String koodisto, String puuttuuvaKoodi) {
        this.organisaationNimi = organisaationNimi;
        this.organisaationOid = organisaationOid;
        this.koodisto = koodisto;
        this.kohdeKoodi = puuttuuvaKoodi;
    }

    public String getOrganisaationNimi() {
        return organisaationNimi;
    }

    public String getOrganisaationOid() {
        return organisaationOid;
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

}
