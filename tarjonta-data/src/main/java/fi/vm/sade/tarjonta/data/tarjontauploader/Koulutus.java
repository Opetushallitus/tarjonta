package fi.vm.sade.tarjonta.data.tarjontauploader;

import fi.vm.sade.tarjonta.data.dto.AbstractReadableRow;

public class Koulutus extends AbstractReadableRow {
    private String nimi;

    public Koulutus() {

    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(final String nimi) {
        this.nimi = nimi;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
