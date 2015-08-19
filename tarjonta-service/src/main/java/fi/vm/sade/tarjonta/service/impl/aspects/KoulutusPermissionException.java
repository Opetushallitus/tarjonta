package fi.vm.sade.tarjonta.service.impl.aspects;

import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;

public class KoulutusPermissionException extends RuntimeException {

    private String organisaationNimi;
    private String organisaationOid;
    private String puuttuvaKoodi;
    private KoulutusmoduuliToteutus komoto;

    public KoulutusPermissionException(String organisaationNimi, String organisaationOid, String puuttuuvaKoodi) {
        this.organisaationNimi = organisaationNimi;
        this.organisaationOid = organisaationOid;
        this.puuttuvaKoodi = puuttuuvaKoodi;
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

    public KoulutusmoduuliToteutus getKomoto() {
        return komoto;
    }

    public void setKomoto(KoulutusmoduuliToteutus komoto) {
        this.komoto = komoto;
    }

}
